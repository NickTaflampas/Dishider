package me.codenick.dishider.database.model;

import androidx.room.ColumnInfo;

public class RankedFoodEntry extends FoodEntry implements Comparable<RankedFoodEntry>{

    @ColumnInfo(name = "score")
    public float score;


    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public int compareTo(RankedFoodEntry rankedFoodEntry) {
        return Float.compare(score, rankedFoodEntry.score);
    }
}
