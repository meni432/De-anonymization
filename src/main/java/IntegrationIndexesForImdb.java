import java.io.File;

/**
 * Created by meni on 24/08/17.
 */
public class IntegrationIndexesForImdb implements IntegrationIndexesInterface {

    IntegrationIndexes integrationIndexes;

    public IntegrationIndexesForImdb(IntegrationIndexes integrationIndexes) {
        this.integrationIndexes = integrationIndexes;
    }

    @Override
    public long getCommonIndex(long index) {
        return this.integrationIndexes.imdbIndexToCommonIndex(index);
    }
}
