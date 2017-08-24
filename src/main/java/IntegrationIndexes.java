import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */

public class IntegrationIndexes {
    public static final int COMMON_INDEX_NOT_FOUND = -1;
    public static final int NETFLIX_INDEX_ARR_POSITION = 0;
    public static final int IMDB_INDEX_ARR_POSITION = 1;
    private static long _id = 0;
    private Map<String, Long> imdbNamesToIndexMap;
    private Map<String, Long> netflixNamesToIndexMap;
    private Map<String, Long> commonNamesToIndexMap;
    private Map<Long, Long> imdbIndexToCommonIndex;
    private Map<Long, Long> netflixIndexToCommonIndex;
    private Map<Long, Long[]> commonIndexToBaseIndex;

    public IntegrationIndexes(File imdbMovieTitle, File netflixMovieTitle) {
        imdbIndexToCommonIndex = new HashMap<>();
        netflixIndexToCommonIndex = new HashMap<>();
        commonIndexToBaseIndex = new HashMap<>();
        imdbNamesToIndexMap = indexFileToMap(imdbMovieTitle);
        netflixNamesToIndexMap = indexFileToMap(netflixMovieTitle);
        analyzesMaps();
    }
    
    private void analyzesMaps() {
        commonNamesToIndexMap = new HashMap<>();
        for(Map.Entry<String, Long> entry : imdbNamesToIndexMap.entrySet()) {
            String name = entry.getKey();
            Long imdbIndex = entry.getValue();

            if (netflixNamesToIndexMap.containsKey(name)) {
                if (!commonNamesToIndexMap.containsKey(name)) {
                    long index = _id++;
                    long netflixIndex = netflixNamesToIndexMap.get(name);
                    // insert to netflix to common index
                    if (!netflixIndexToCommonIndex.containsKey(name)) {
                        netflixIndexToCommonIndex.put(netflixIndex, index);
                    }
                    // insert to imdb to common index
                    if (!imdbIndexToCommonIndex.containsKey(name)) {
                        imdbIndexToCommonIndex.put(imdbIndex, index);
                    }
                    commonNamesToIndexMap.put(name, index);
                    setCommonIndexToBaseIndexArray(index, imdbIndex, netflixIndex);
                }
            }
        }
    }

    private void setCommonIndexToBaseIndexArray(long commonIndex, long imdbIndex, long netflixIndex) {
        if (!commonIndexToBaseIndex.containsKey(commonIndex)) {
            commonIndexToBaseIndex.put(commonIndex, new Long[2]);
        }
        commonIndexToBaseIndex.get(commonIndex)[IMDB_INDEX_ARR_POSITION] = imdbIndex;
        commonIndexToBaseIndex.get(commonIndex)[NETFLIX_INDEX_ARR_POSITION] = netflixIndex;
    }

    public long getMaxCommonIndex() {
        return commonNamesToIndexMap.size();
    }

    public long imdbIndexToCommonIndex(long imdbIndex) {
        if (imdbIndexToCommonIndex.containsKey(imdbIndex)) {
            return imdbIndexToCommonIndex.get(imdbIndex);
        }
        return COMMON_INDEX_NOT_FOUND;
    }

    public long netflixIndexToCommonIndex(long netflixIndex) {
        if (netflixIndexToCommonIndex.containsKey(netflixIndex)) {
            return netflixIndexToCommonIndex.get(netflixIndex);
        }
        return COMMON_INDEX_NOT_FOUND;
    }

    private static Map<String, Long> indexFileToMap(File file) {
        Map<String, Long> targetMap = new HashMap<>();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] column  = line.split(",");
                    long id = Long.parseLong(column[0]);
                    String name = column[2];
                    targetMap.put(name, id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetMap;
    }
}
