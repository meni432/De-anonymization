import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 */
public class MainTest {

    public static final String NETFLIX_DATASET_MASTER_DIR = "/home/meni/Desktop/ML/prod1/nf_prize_dataset/download";
    public static final String IMDB_DATASET_MASTER_DIR = "/home/meni/Desktop/workspace/EXS/IMDB/Netflix";
    public static final String USERS_INDEX_FILE = "users.index.txt";
    public static final String MOVIE_TITLE_FILE = "movie_titles.txt";
    public static final String DATASET_DIR = "training_set";


    private Dataset loadDataSet(String aDir, IntegrationIndexesInterface integrationIndexesInterface) throws IOException {
        File[] aFileList = new File(aDir + File.separator + DATASET_DIR).listFiles();
        File aUserIndexFile = new File(aDir + File.separator + USERS_INDEX_FILE);
        if (aFileList == null)
            System.out.println("Directory " + (new File(aDir + File.separator + DATASET_DIR)).getAbsolutePath() + " does not exist!");
        if (!aUserIndexFile.exists())
            System.out.println("File " + aUserIndexFile.getAbsolutePath() + " does not exist!");
        Dataset ads = new Dataset(aFileList, integrationIndexesInterface);
        return ads;
    }

    private void runTest() {
        Dataset imdbDataset;
        Dataset netflixDataset;
        File movieTitleImdbFile = new File(IMDB_DATASET_MASTER_DIR + "/"+ MOVIE_TITLE_FILE);
        File movieTitleNetflixFile = new File(NETFLIX_DATASET_MASTER_DIR + "/"+ MOVIE_TITLE_FILE);
        IntegrationIndexes integrationIndexes = new IntegrationIndexes(movieTitleImdbFile, movieTitleNetflixFile);
        try {
            MyLogger.info("max common index: " + integrationIndexes.getMaxCommonIndex());
            imdbDataset = loadDataSet(IMDB_DATASET_MASTER_DIR, new IntegrationIndexesForImdb(integrationIndexes));
            MyLogger.info("get total imdb: " +imdbDataset.getTotalUsers());
            netflixDataset = loadDataSet(NETFLIX_DATASET_MASTER_DIR, new IntegrationIndexesForNetflix(integrationIndexes));
            MyLogger.info("get total netflixDataset: " +netflixDataset.getTotalUsers());
            CheckSimilarity checkSimilarity = new CheckSimilarity(imdbDataset, netflixDataset);
            MyLogger.info("after check similarity");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void definitionCheck() {
        assert NETFLIX_DATASET_MASTER_DIR != null : "invalid netflix master dir";
        assert IMDB_DATASET_MASTER_DIR != null : "invalid imdb master dir";
        assert USERS_INDEX_FILE != null : "invalid user index file name";
        assert DATASET_DIR != null : "invalid dataset dir trining set";
    }

    public static void main(String[] args) {
//        definitionCheck();
        MainTest mainTest = new MainTest();
        mainTest.runTest();
    }

}
