package engine.data;

public class Dataset {

    public final String name;
    public final int[] scores;
    public final String[] studentNames;

    public Dataset(String name, int[] scores) {
        this.name = name;
        this.scores = scores;
        this.studentNames = null;
    }

    public Dataset(String name, int[] scores, String[] studentNames) {
        this.name = name;
        this.scores = scores;
        this.studentNames = studentNames;
    }

    public String getName() {
        return name;
    }

    public int[] getScores() {
        return scores;
    }

    public String[] getStudentNames() {
        return studentNames;
    }

    public int size() {
        return scores.length;
    }
}
