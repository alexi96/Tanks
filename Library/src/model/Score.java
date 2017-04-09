package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Score implements Serializable, Comparable<Score> {

    static private final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private int score;
    private String name;
    private Date date;

    public Score() {
    }

    public Score(int score, String nume, Date date) {
        this.score = score;
        this.name = nume;
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void addPoints(int pts) {
        this.score += pts;
    }

    @Override
    public int compareTo(Score t) {
        return this.score - t.score;
    }

    @Override
    public String toString() {
        return this.name + ": " + this.score + " " + Score.FORMAT.format(this.date);
    }
}
