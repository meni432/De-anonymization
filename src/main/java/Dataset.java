import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by meni on 24/08/17.
 */

public class Dataset {
    public static final boolean MULTITHREADED_ENABLE = true;
    public static final int MULTITHREADED_NUM_OF_WORKER_THREADS = 8;
    private File[] datasetFiles;
    private Map<Long, List<UserRank>> userToMovies;
    private Map<Long, Lock> userToLock;
    private List<Long> usersIdsList;
    private List<Long> usersMovieOrigIdsList;
    private IntegrationIndexesInterface integrationIndexes;
    private long maxUserIndex = 0;

    /**
     * Data structure for Netflix Format Data Set
     *
     * @param datasetFiles       array of training files
     * @param integrationIndexes integration index between Datasets indexes to common index
     */
    public Dataset(File[] datasetFiles, IntegrationIndexesInterface integrationIndexes) {
        userToMovies = new HashMap<>();
        userToLock = new HashMap<>();
        usersIdsList = new LinkedList<>();
        usersMovieOrigIdsList = new LinkedList<>();
        this.datasetFiles = datasetFiles;
        this.integrationIndexes = integrationIndexes;
        readFiles();
    }

    private void readFiles() {
        MyLogger.info("start read files");
        ExecutorService executorService = Executors.newFixedThreadPool(MULTITHREADED_NUM_OF_WORKER_THREADS);
        List<Future<Boolean>> futures = new LinkedList<>();
        for (File file : datasetFiles) {
            if (!MULTITHREADED_ENABLE) {
            long userIndex;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                if ((line = br.readLine()) != null) {
                    String[] column = line.split(":");
                    int movieRank = Integer.valueOf(column[0]);
                    long movieCommonId = integrationIndexes.getCommonIndex(movieRank);
                    while ((line = br.readLine()) != null) {
                        column = line.split(",");
                        userIndex = Long.parseLong(column[0]);
                        if (userIndex > maxUserIndex) {
                            maxUserIndex = userIndex;
                        }
                        usersIdsList.add(userIndex);
                        List<UserRank> userMovieList = getOrCreateMovieList(userToMovies, userIndex);
                        if (movieCommonId != IntegrationIndexes.COMMON_INDEX_NOT_FOUND) {
                            userMovieList.add(new UserRank(movieCommonId, movieRank));
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            } else {
                Future<Boolean> future = executorService.submit(new ReadFileCallable(file));
                if (future == null) {
                    throw new RuntimeException("null future");
                }
                futures.add(future);
            }

            MyLogger.warning("files: " + datasetFiles.length + " future: " + futures.size());
            int _sec_num = 0;
            for (Future<Boolean> future : futures) {
                try {
                    if (future != null) {
                        future.get();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if (_sec_num++ % 100 == 0) {
                    MyLogger.info("sec num : " + _sec_num);
                }
            }
            executorService.shutdown();
            MyLogger.info("end reading files");
        }
    }

    /**
     * Get common index of movie list of given user id (user index)
     *
     * @param userId
     * @return
     */
    public List<UserRank> getUserMovieList(long userId) {
        if (userToMovies.containsKey(userId)) {
            return userToMovies.get(userId);
        }
        return null;
    }

    /**
     * @return total user read from files
     */
    public long getTotalUsers() {
        return usersIdsList.size();
    }

    /**
     * @return all users in a list
     */
    public List<Long> getUsersIdsList() {
        return usersIdsList;
    }

    /**
     * @return max user idndex
     */
    public long getMaxUserIndex() {
        return maxUserIndex;
    }

    private static List<UserRank> getOrCreateMovieList(Map<Long, List<UserRank>> listMap, long userId) {
        if (!listMap.containsKey(userId)) {
            listMap.put(userId, new ArrayList<>());
        }
        return listMap.get(userId);
    }

    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private Lock getOrCreatedUserLock(long userId) {
        Lock ans = null;
        readWriteLock.readLock().lock();
        if (!userToLock.containsKey(userId)) {
            readWriteLock.readLock().unlock();
            readWriteLock.writeLock().lock();
            if (!userToLock.containsKey(userId)) {
                ans = new ReentrantLock();
                userToLock.put(userId, ans);
            } else {
                ans = userToLock.get(userId);
            }
            readWriteLock.writeLock().unlock();
            return ans;
        }
        ans = userToLock.get(userId);
        readWriteLock.readLock().unlock();
        return ans;
    }


    class ReadFileCallable implements Callable<Boolean> {
        File file;

        public ReadFileCallable(File file) {
            this.file = file;
        }

        @Override
        public Boolean call() throws Exception {
            long userIndex;
            List<String> allLines = Files.readAllLines(file.toPath());
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = allLines.get(0);
                if ((line = br.readLine()) != null) {
                    String[] column = line.split(":");
                    int movieRank = Integer.valueOf(column[0]);
                    long movieCommonId = integrationIndexes.getCommonIndex(movieRank);
//                    while ((line = br.readLine()) != null) {
                    for (int i = 1; i < allLines.size(); i++) {
                        line = allLines.get(i);
                        column = line.split(",");
                        userIndex = Long.parseLong(column[0]);
                        if (userIndex > maxUserIndex) {
                            maxUserIndex = userIndex;
                        }
                        usersIdsList.add(userIndex);
                        if (movieCommonId != IntegrationIndexes.COMMON_INDEX_NOT_FOUND) {
                            List<UserRank> userMovieList = getOrCreateMovieList(userToMovies, userIndex);
                            Lock lock = getOrCreatedUserLock(userIndex);
                            lock.lock();
                            userMovieList.add(new UserRank(movieCommonId, movieRank));
                            lock.unlock();
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
