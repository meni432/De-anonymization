/**
 * Created by meni on 24/08/17.
 */
public class UserRank {
    long commonIndex;
    int rank;

    public UserRank(long commonIndex, int rank) {
        this.commonIndex = commonIndex;
        this.rank = rank;
    }

    public long getCommonIndex() {
        return commonIndex;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRank)) return false;

        UserRank userRank = (UserRank) o;

        if (getCommonIndex() != userRank.getCommonIndex()) return false;
        return Math.abs(getRank() - userRank.getRank()) <= 1;
    }

    @Override
    public int hashCode() {
        int result = (int) (getCommonIndex() ^ (getCommonIndex() >>> 32));
        result = 31 * result + getRank();
        return result;
    }


}
