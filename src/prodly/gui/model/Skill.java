package prodly.gui.model;

public class Skill {

    private String name;
    private String difficulty;

    public Skill(String name, String difficulty) {
        this.name = name;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
