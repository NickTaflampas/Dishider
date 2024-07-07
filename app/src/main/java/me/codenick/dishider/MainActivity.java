package me.codenick.dishider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    File userStatistics;
    float fruitScore;
    float vegetableScore;
    float proteinScore;
    float sugarScore;
    float carbScore;
    float fatScore;

    ProgressBar fruitBar;
    ProgressBar vegetableBar;
    ProgressBar proteinBar;
    ProgressBar sugarBar;
    ProgressBar carbBar;
    ProgressBar fatBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userStatistics = new File(getFilesDir(), "/statistics.txt");
        fruitBar = findViewById(R.id.fruitScore);
        vegetableBar = findViewById(R.id.vegetableScore);
        proteinBar = findViewById(R.id.proteinScore);
        sugarBar = findViewById(R.id.sugarScore);
        carbBar = findViewById(R.id.carbScore);
        fatBar = findViewById(R.id.fatScore);

        Bundle extras = getIntent().getExtras();
        createOrLoadStatistics(extras);
        updateProgressBars();

        findViewById(R.id.foodButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FoodOptions.class);
                intent.putExtra("fruitScore", fruitScore);
                intent.putExtra("vegetableScore", vegetableScore);
                intent.putExtra("proteinScore", proteinScore);
                intent.putExtra("sugarScore", sugarScore);
                intent.putExtra("carbScore", carbScore);
                intent.putExtra("fatScore", fatScore);


                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        saveStatistics();
    }

    private void updateProgressBars()
    {
        fruitBar.setProgress(Math.round(fruitScore*10));
        vegetableBar.setProgress(Math.round(vegetableScore*10));
        proteinBar.setProgress(Math.round(proteinScore*10));
        sugarBar.setProgress(Math.round(sugarScore*10));
        carbBar.setProgress(Math.round(carbScore*10));
        fatBar.setProgress(Math.round(fatScore*10));

    }

    private void createOrLoadStatistics(Bundle extras)
    {
        try {
            if (userStatistics.exists()) {
                Scanner reader = new Scanner(userStatistics);
                String[] scores = reader.nextLine().split(";");
                fruitScore = Float.parseFloat(scores[0]);
                vegetableScore = Float.parseFloat(scores[1]);
                proteinScore = Float.parseFloat(scores[2]);
                sugarScore = Float.parseFloat(scores[3]);
                carbScore = Float.parseFloat(scores[4]);
                fatScore = Float.parseFloat(scores[5]);

                if (extras != null && extras.containsKey("acceptedFood"))
                {
                    fruitScore += (extras.getInt("fruitScoreEaten")-fruitScore)*0.5f;
                    vegetableScore += (extras.getInt("vegetableScoreEaten")-vegetableScore)*0.5f;
                    proteinScore += (extras.getInt("proteinScoreEaten")-proteinScore)*0.5f;
                    sugarScore += (extras.getInt("sugarScoreEaten")-sugarScore)*0.5f;
                    carbScore += (extras.getInt("carbScoreEaten")-carbScore)*0.5f;
                    fatScore += (extras.getInt("fatScoreEaten")-fatScore)*0.5f;

                }

            } else {
                userStatistics.createNewFile();

                fruitScore = 5;
                vegetableScore = 5;
                proteinScore = 5;
                sugarScore = 5;
                carbScore = 5;
                fatScore = 5;

                saveStatistics();

            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Oops! on initializing statistics." + e, Toast.LENGTH_SHORT).show();
        }

    }

    private void saveStatistics()
    {
        try
        {
            FileWriter writer = new FileWriter(userStatistics);
            writer.write(fruitScore + ";" + vegetableScore + ";" + proteinScore + ";" +
                    sugarScore + ";" + carbScore + ";" + fatScore + "\n");
            writer.close();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Oops! on saving statistics." + e, Toast.LENGTH_SHORT).show();
        }
    }

}