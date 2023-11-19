package bot_v2;

import org.jetbrains.annotations.NotNull;

public class RatedMove implements Comparable<RatedMove>{
    public Move move;
    public float rating;

    public RatedMove(Move move, float rating){
        this.move = move;
        this.rating = rating;
    }


    @Override
    public int compareTo(@NotNull RatedMove o) {
        return Float.compare(this.rating, o.rating);
    }
}
