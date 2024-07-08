package me.codenick.dishider.database.model;

import androidx.room.ColumnInfo;

public class RankedFoodEntry extends FoodEntry{

    @ColumnInfo(name = "score")
    public float score;



}
