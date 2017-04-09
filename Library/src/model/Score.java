package model;

import java.io.Serializable;
import java.util.Date;

public class Score implements Serializable, Comparable<Score> {
    private int score;
    private Date date;

    @Override
    public int compareTo(Score t) {
        return this.date.compareTo(t.date);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
