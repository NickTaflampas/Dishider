package me.codenick.dishider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class FoodResult extends AppCompatActivity {

    ArrayList<FoodEntry> foodEntries = new ArrayList<FoodEntry>();

    ArrayList<FoodEntry> rankedFoodEntries;

    FoodEntry shownEntry = null;
    TextView shownTitle;
    TextView shownDescription;

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

        shownTitle = findViewById(R.id.foodTitle);
        shownDescription = findViewById(R.id.foodDescription);
        shownTitle.setText("No food found.");
        shownDescription.setText("Sorry :(");

        Bundle extras = getIntent().getExtras();
        loadFoodEntries();
        rankFoodEntries(extras);

        Random r = new Random();
        shownEntry = rankedFoodEntries.get(r.nextInt(rankedFoodEntries.size()));
        shownTitle.setText(shownEntry.getName());
        shownDescription.setText(shownEntry.getDescription());

        findViewById(R.id.retry_food_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r = new Random();
                shownEntry = rankedFoodEntries.get(r.nextInt(rankedFoodEntries.size()));
                shownTitle.setText(shownEntry.getName());
                shownDescription.setText(shownEntry.getDescription());
            }
        });

        if (rankedFoodEntries.size() <= 1) { findViewById(R.id.retry_food_button).setAlpha(0.5f); }

        findViewById(R.id.confirm_food_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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


    }

    private void rankFoodEntries(Bundle extras)
    {

        HashMap<FoodEntry,Float> validEntries = new HashMap<FoodEntry,Float>();
        int snackOption = extras.getInt("snackOption");
        boolean shouldBeVegan = extras.getBoolean("isVegan");
        boolean shouldUseNutrients = extras.getBoolean("useNutrients");
        float[] userScores = new float[] { extras.getFloat("fruitScore"),
                                            extras.getFloat("vegetableScore"),
                                            extras.getFloat("proteinScore"),
                                            extras.getFloat("sugarScore"),
                                            extras.getFloat("carbScore"),
                                            extras.getFloat("fatScore") };

        for (FoodEntry entry : foodEntries)
        {
            float score = 0;
            if (shouldBeVegan && !entry.isVegan()) continue;
            if ((snackOption == SnackOptions.SNACK_ONLY && !entry.isSnack()) ||
                    (snackOption == SnackOptions.MEAL_ONLY && entry.isSnack()))
            {
                continue;
            }
            if (!shouldUseNutrients) { validEntries.put(entry,score); continue; }

            float[] entryScores = new float[] {entry.getFruitScore(), entry.getVegetableScore(),
                    entry.getProteinScore(), entry.getSugarScore(), entry.getCarbScore(),
                    entry.getFatScore()};

            for (int i = 0; i < entryScores.length; i++)
            {
                score += Math.abs(5.0f-((userScores[i]+entryScores[i])/2.0f));
            }

            validEntries.put(entry,score);
        }

        float bestScore = Collections.min(new ArrayList<Float>(validEntries.values()));

        rankedFoodEntries = new ArrayList<FoodEntry>();
        for (Map.Entry<FoodEntry, Float> entry : validEntries.entrySet())
        {
            if (entry.getValue() == bestScore)
            {
                rankedFoodEntries.add(entry.getKey());
            }
        }

    }


    private void loadFoodEntries()
    {
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
                FoodEntry entry = FoodEntry.builder(Integer.parseInt(splitLine[0])).withName(splitLine[1])
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


    }
}