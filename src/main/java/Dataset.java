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

    public Dataset(File[] datasetFiles, IntegrationIndexesInterface integrationIndexes) {
        userToMovies = new HashMap<>();
        usersIdsList = new LinkedList<>();
        usersMovieOrigIdsList = new LinkedList<>();
        this.datasetFiles = datasetFiles;
        this.integrationIndexes = integrationIndexes;
        readFiles();
    }

    private void readFiles() {
        for (File file : datasetFiles) {
            long userIndex;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                if ((line = br.readLine()) != null) {
                    String[] column = line.split(":");
                    userIndex = Long.parseLong(column[0]);
                    if (userIndex > maxUserIndex) {
                        maxUserIndex = userIndex;
                    }
                    usersIdsList.add(userIndex);
                    List<UserRank> userMovieList = getOrCreateMovieList(userToMovies, userIndex);
                    while ((line = br.readLine()) != null) {
                        column = line.split(",");
                        long movieCommonId = integrationIndexes.getCommonIndex(Long.valueOf(column[0]));
                        if (movieCommonId != IntegrationIndexes.COMMON_INDEX_NOT_FOUND) {
                            int movieRank = Integer.valueOf(column[1]);
                            userMovieList.add(new UserRank(movieCommonId, movieRank));
                            usersMovieOrigIdsList.add(Long.valueOf(column[0]));
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<UserRank> getUserMovieList(long userId) {
        if (userToMovies.containsKey(userId)) {
            return userToMovies.get(userId);
        }
        return null;
    }

    public long getTotalUsers() {
        return usersIdsList.size();
    }

    public List<Long> getUsersIdsList() {
        return usersIdsList;
    }

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
