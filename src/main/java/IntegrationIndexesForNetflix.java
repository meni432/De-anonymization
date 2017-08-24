import java.io.File;

/**
 * Created by meni on 24/08/17.
 */
public class IntegrationIndexesForNetflix implements IntegrationIndexesInterface {

    IntegrationIndexes integrationIndexes;

    public IntegrationIndexesForNetflix(IntegrationIndexes integrationIndexes) {
        this.integrationIndexes = integrationIndexes;
    }

    @Override
    public long getCommonIndex(long index) {
        return this.integrationIndexes.netflixIndexToCommonIndex(index);
    }
}
