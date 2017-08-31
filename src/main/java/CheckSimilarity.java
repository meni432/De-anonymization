import java.util.*;

/**
 */


public class CheckSimilarity {
    public static final int MIN_MOVIES_THRESHOLDS = 30;
    private static final double[] RANKINGS_OFFSET_FACTOR = {0.1, 0.5};

    private Dataset imdbDataset;
    private Dataset netflixDataset;

    public CheckSimilarity(Dataset imdbDataset, Dataset netflixDataset) {
        this.imdbDataset = imdbDataset;
        this.netflixDataset = netflixDataset;
        createUsersIndexToMoiveListMaps();
    }

    private void createUsersIndexToMoiveListMaps() {
        MyLogger.info("start create user index to movie list maps");
        // create imdb map


        double maxMatch = 0;
        long maxNetflixId = -1;
        long maxImdbId = -1;

        long iterCounting = 0;
        for (int currentFactor = 0; currentFactor < RANKINGS_OFFSET_FACTOR.length; currentFactor++) {
            double currentFactorValue = RANKINGS_OFFSET_FACTOR[currentFactor];
            MyLogger.info("current factor: " + currentFactorValue);
            for (Long imdbUserIndex : this.imdbDataset.getUsersIdsList()) {
                for (Long netflixUserIndex : this.netflixDataset.getUsersIdsList()) {
                    double currentMatch = getCurrentMatch(currentFactorValue, imdbUserIndex, netflixUserIndex);
                    if (currentMatch > maxMatch) {
                        maxMatch = currentMatch;
                        maxNetflixId = netflixUserIndex;
                        maxImdbId = imdbUserIndex;
                    }
                }
                if (iterCounting++ % 100 == 0) {
                    MyLogger.info("current iter : " + (iterCounting) + " maxMatch: " + maxMatch + " netflix Id : " + maxNetflixId + " imdb Id: " + maxImdbId);
                }
            }

            MyLogger.info("summery for Factor: " + currentFactorValue + " maxMatch: " + maxMatch + " netflix Id : " + maxNetflixId + " imdb Id: " + maxImdbId);
        }
    }

    private double getCurrentMatch(double currentFactorValue, Long imdbUserIndex, Long netflixUserIndex) {
        double currentMatch = 0;
        List<UserRank> imdbUserMovieList = this.imdbDataset.getUserMovieList(imdbUserIndex);
        if (imdbUserMovieList.size() > MIN_MOVIES_THRESHOLDS) {
            for (UserRank imdbUserMovieId : imdbUserMovieList) {
                List<UserRank> netflixUserMovieList = this.netflixDataset.getUserMovieList(netflixUserIndex);
                if (netflixUserMovieList.size() > MIN_MOVIES_THRESHOLDS) {
                    for (UserRank netflixUserMovieId : netflixUserMovieList) {
                        if (imdbUserMovieId.getCommonIndex() == netflixUserMovieId.getCommonIndex()) {
                            currentMatch = currentMatch + 1 - (currentFactorValue * Math.abs(imdbUserMovieId.getRank() - netflixUserMovieId.getRank()));
                        }
                    }
                }
            }
        }
        return currentMatch;
    }

    private static List<Long> getOrCreateMovieList(Map<Long, List<Long>> listMap, long userId) {
        if (!listMap.containsKey(userId)) {
            listMap.put(userId, new ArrayList<>());
        }
        return listMap.get(userId);
    }

    private static long max(long a, long b) {
        if (a > b) {
            return a;
        }
        return b;
    }
}
