package me.codenick.dishider.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import me.codenick.dishider.database.AppDatabase;
import me.codenick.dishider.database.model.FoodEntry;
import me.codenick.dishider.database.dao.FoodEntryDAO;
import me.codenick.dishider.R;
import me.codenick.dishider.database.model.RankedFoodEntry;
import me.codenick.dishider.utils.SnackOptions;

public class FoodResult extends AppCompatActivity {

    File foodHistoryFile;

    ArrayList<Integer> consumedFoodHistory = new ArrayList<Integer>();
    ArrayList<RankedFoodEntry> rankedFoodEntries;

    FoodEntry shownEntry = null;
    int shownEntryIndex = -1;
    TextView shownTitle;
    TextView shownDescription;
    boolean isSortedByNutrients = false;

    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        foodHistoryFile = new File(getFilesDir(), "/history.txt");
        loadFoodHistory();

        database = AppDatabase.getInstance(getApplicationContext());
        prepopulateDatabase();


        shownTitle = findViewById(R.id.foodTitle);
        shownDescription = findViewById(R.id.foodDescription);
        shownTitle.setText("No food found.");
        shownDescription.setText("Sorry :(");

        Bundle extras = getIntent().getExtras();
        rankFoodEntries(extras);

        Random r = new Random();
        shownEntry = rankedFoodEntries.get(r.nextInt(rankedFoodEntries.size()));
        shownTitle.setText(shownEntry.getName());
        shownDescription.setText(shownEntry.getDescription());

        setUpListeners();

    }

    private void loadFoodHistory()
    {
        try
        {
            if (!foodHistoryFile.exists())
            {
                foodHistoryFile.createNewFile();
                return;
            }

            Scanner reader = new Scanner(foodHistoryFile);
            String[] ids = reader.nextLine().split(";");
            for (String id : ids)
            {
                consumedFoodHistory.add(Integer.parseInt(id));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Oops! on retrieving history of food entries." + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFoodHistory()
    {
        try
        {
            FileWriter writer = new FileWriter(foodHistoryFile);
            String res = consumedFoodHistory.stream().map(String::valueOf)
                    .collect(Collectors.joining(";"));
            writer.write( res+"\n");
            writer.close();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Oops! on saving history." + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void rankFoodEntries(Bundle extras)
    {
        if (extras == null) return;

        RankedFoodEntry[] rankedEntries = database.getFoodEntryDAO().getRankedEntries(
                extras.getFloat("fruitScore"), extras.getFloat("vegetableScore"),
                extras.getFloat("proteinScore"), extras.getFloat("sugarScore"),
                extras.getFloat("carbScore"), extras.getFloat("fatScore"),
                extras.getBoolean("isVegan"));
        int snackOption = extras.getInt("snackOption");

        //If nutrient scores are not used, don't sort them.
        if (!extras.getBoolean("useNutrients"))
        {
            rankedFoodEntries = new ArrayList<RankedFoodEntry>(Arrays.asList(rankedEntries));
            return;
        }
        isSortedByNutrients = true;
        Button b = findViewById(R.id.retry_food_button);
        b.setText(getString(R.string.option_retry_food_without_nutrients));

        //Add to the score of recently eaten entries.
        for (RankedFoodEntry entry : rankedEntries)
        {
            int count = Collections.frequency(consumedFoodHistory, entry.getId());
            entry.setScore(entry.getScore() + (float)Math.exp(count/5.0f));
        }
        Arrays.sort(rankedEntries);

        //Filter entries that do not match SnackOption choice.
        rankedFoodEntries = new ArrayList<RankedFoodEntry>();
        for (RankedFoodEntry entry : rankedEntries)
        {
            switch (snackOption)
            {
                case SnackOptions.ALL:
                    break;
                case SnackOptions.SNACK_ONLY:
                    if (!entry.isSnack()) continue;
                    break;
                case SnackOptions.MEAL_ONLY:
                    if (entry.isSnack()) continue;
                    break;
            }
            rankedFoodEntries.add(entry);
        }

    }

    private void prepopulateDatabase()
    {
        FoodEntryDAO dao = database.getFoodEntryDAO();
        if (!dao.getAllFoodEntries().isEmpty()) return;

        List<FoodEntry> entries = loadFoodEntries();
        dao.addFoodEntries(entries);

    }

    //Loads hardcoded food entries.
    private ArrayList<FoodEntry> loadFoodEntries()
    {
        ArrayList<FoodEntry> foodEntries = new ArrayList<FoodEntry>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getResources().openRawResource(R.raw.recipes)));

        try
        {
            boolean skippedFirstLine = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (!skippedFirstLine) {
                    skippedFirstLine = true;
                    continue;
                }

                String[] splitLine = line.split("\t");
                FoodEntry entry = FoodEntry.builder().withName(splitLine[1])
                        .withDescription(splitLine[2])
                        .withFruitScore(Integer.parseInt(splitLine[3]))
                        .withVegetableScore(Integer.parseInt(splitLine[4]))
                        .withProteinScore(Integer.parseInt(splitLine[5]))
                        .withSugarScore(Integer.parseInt(splitLine[6]))
                        .withCarbScore(Integer.parseInt(splitLine[7]))
                        .withFatScore(Integer.parseInt(splitLine[8]))
                        .isSnack(splitLine[9].equals("TRUE"))
                        .isVegan(splitLine[10].equals("TRUE")).build();
                foodEntries.add(entry);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Oops! on initializing food entries." + e, Toast.LENGTH_SHORT).show();
        }
        return foodEntries;

    }

    private void setUpListeners()
    {
        findViewById(R.id.retry_food_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rankedFoodEntries.size() <= 1) return;

                //If sorted by nutrient score, just pick the next best.
                if (isSortedByNutrients)
                {
                    shownEntryIndex++;
                    if (shownEntryIndex >= rankedFoodEntries.size()) shownEntryIndex = 0;
                }
                else //Otherwise, random select.
                {
                    Random r = new Random();
                    int randomIndex = -1;
                    while (true)
                    {
                        randomIndex = r.nextInt(rankedFoodEntries.size());
                        if (randomIndex != shownEntryIndex)
                        {
                            shownEntryIndex = randomIndex;
                            break;
                        }
                    }
                }

                shownEntry = rankedFoodEntries.get(shownEntryIndex);
                shownTitle.setText(shownEntry.getName());
                shownDescription.setText(shownEntry.getDescription());
            }
        });

        findViewById(R.id.confirm_food_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Add consumed entry to our history
                consumedFoodHistory.add(shownEntry.getId());
                if (consumedFoodHistory.size() > 30) consumedFoodHistory.remove(0);
                saveFoodHistory();

                //Transfer nutrients for user nutrient recalculation
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("acceptedFood", true);

                intent.putExtra("fruitScoreEaten", shownEntry.getFruitScore());
                intent.putExtra("vegetableScoreEaten", shownEntry.getVegetableScore());
                intent.putExtra("proteinScoreEaten", shownEntry.getProteinScore());
                intent.putExtra("sugarScoreEaten", shownEntry.getSugarScore());
                intent.putExtra("carbScoreEaten", shownEntry.getCarbScore());
                intent.putExtra("fatScoreEaten", shownEntry.getFatScore());

                startActivity(intent);


            }
        });

        findViewById(R.id.returnButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}