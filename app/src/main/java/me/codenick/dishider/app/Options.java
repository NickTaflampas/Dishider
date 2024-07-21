package me.codenick.dishider.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import me.codenick.dishider.R;
import me.codenick.dishider.database.AppDatabase;
import me.codenick.dishider.database.model.FoodEntry;

public class Options extends AppCompatActivity {

    int purgeLongPresses = 0;

    Spinner spinner;
    FoodEntry[] userFoodEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_options);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerListeners();
        loadDropdownUserEntries();
    }

    private void loadDropdownUserEntries()
    {
        spinner = findViewById(R.id.spinner);

        userFoodEntries = AppDatabase.getInstance(getApplicationContext()).getFoodEntryDAO().getUserAddedEntries();
        ArrayList<String> foodEntryNames = new ArrayList<String>();
        for (FoodEntry entry : userFoodEntries)
        {
            foodEntryNames.add(entry.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, foodEntryNames);
        spinner.setAdapter(adapter);
    }

    private void registerListeners()
    {
        findViewById(R.id.returnButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.purgeDatabaseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Hold down button to begin purging.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.purgeDatabaseButton).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                if (purgeLongPresses >= 3)
                {
                    deleteDatabase("DishiderDB");
                    purgeLongPresses = 0;
                    Toast.makeText(getApplicationContext(),
                            "All entries purged successfully! Restart the application.",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    purgeLongPresses++;
                    Toast.makeText(getApplicationContext(),
                            "Purging entries in " + (4-purgeLongPresses) + " long presses.",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        findViewById(R.id.deleteEntry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Hold down button to delete entry.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.deleteEntry).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                FoodEntry deleteEntry = userFoodEntries[spinner.getSelectedItemPosition()];
                AppDatabase.getInstance(getApplicationContext()).getFoodEntryDAO().deleteFoodEntry(deleteEntry);
                Toast.makeText(getApplicationContext(),
                        "Entry deleted successfully. Restart the application.",
                        Toast.LENGTH_SHORT).show();

                loadDropdownUserEntries();

                return true;
            }
        });
    }
}