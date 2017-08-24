/**
 * Created by meni on 24/08/17.
 */
public class Raiting {
    private long movieId;
    private int raiting;

    public Raiting(long movieId, int raiting) {
        this.movieId = movieId;
        this.raiting = raiting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Raiting)) return false;

        Raiting raiting = (Raiting) o;

        return movieId == raiting.movieId;
    }

    @Override
    public int hashCode() {
        return (int) (movieId ^ (movieId >>> 32));
    }
}
