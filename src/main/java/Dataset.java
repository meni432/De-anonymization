import java.io.*;
import java.util.*;

/**
 * Created by meni on 24/08/17.
 */

public class Dataset {
    private File[] datasetFiles;
    private Map<Long, List<UserRank>> userToMovies;
    private List<Long> usersIdsList;
    private List<Long> usersMovieOrigIdsList;
    private IntegrationIndexesInterface integrationIndexes;
    private long maxUserIndex = 0;

    /**
     * Data structure for Netflix Format Data Set
     * @param datasetFiles array of training files
     * @param integrationIndexes integration index between Datasets indexes to common index
     */
    public Dataset(File[] datasetFiles, IntegrationIndexesInterface integrationIndexes) {
        userToMovies = new HashMap<>();
        usersIdsList = new LinkedList<>();
        usersMovieOrigIdsList = new LinkedList<>();
        this.datasetFiles = datasetFiles;
        this.integrationIndexes = integrationIndexes;
        readFiles();
    }

    private void readFiles() {
        MyLogger.info("start read files");
        int _sec_num = 0;
        for (File file : datasetFiles) {
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

            if (_sec_num++ % 100 == 0) {
                MyLogger.info("end read file " + _sec_num);
            }
        }
        MyLogger.info("end reading files");
    }

    /**
     * Get common index of movie list of given user id (user index)
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
}
