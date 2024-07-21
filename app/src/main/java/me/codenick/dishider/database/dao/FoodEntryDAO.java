package me.codenick.dishider.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import me.codenick.dishider.database.model.FoodEntry;
import me.codenick.dishider.database.model.RankedFoodEntry;

@Dao
public interface FoodEntryDAO {

    @Insert
    public void addFoodEntry(FoodEntry entry);
    @Update
    public void updateFoodEntry(FoodEntry entry);
    @Delete
    public void deleteFoodEntry(FoodEntry entry);

    @Insert
    public void addFoodEntries(List<FoodEntry> entries);

    @Query("select * from FoodEntries")
    public List<FoodEntry> getAllFoodEntries();

    @Query("select * from FoodEntries where id ==:id")
    public FoodEntry getFoodEntry(int id);

    @Query("SELECT *, abs(5-(:fruitScore+fruit_score)/2)+abs(5-(:vegetableScore+vegetable_score)/2)" +
                    "+abs(5-(:proteinScore+protein_score)/2)+abs(5-(:sugarScore+sugar_score)/2)" +
                    "+abs(5-(:carbScore+carb_score)/2)+abs(5-(:fatScore+fat_score)/2) " +
                    "as score FROM FoodEntries WHERE is_vegan = :wantsVegan ORDER BY score LIMIT 50")
    public RankedFoodEntry[] getRankedEntries(float fruitScore, float vegetableScore, float proteinScore,
                                              float sugarScore, float carbScore, float fatScore, boolean wantsVegan);

    @Query("SELECT * FROM FoodEntries WHERE id > 10")
    public FoodEntry[] getUserAddedEntries();
}
