package engine.data;
import java.util.*;

public class Dataset {

    public final String name;
    public final int[] scores;

    public Dataset(String name, int[] scores) {
        this.name = name;
        this.scores = scores;
    }

    public String getName() {
        return name;

    }

    public int[] getScores() {
        return scores;
    }

    public int size() {
        return scores.length;
    }




}
