package me.codenick.dishider.app;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import me.codenick.dishider.R;
import me.codenick.dishider.database.AppDatabase;
import me.codenick.dishider.database.dao.FoodEntryDAO;
import me.codenick.dishider.database.model.FoodEntry;

public class AddFoodEntryActivity extends AppCompatActivity {

    EditText nameInput;
    EditText descriptionInput;
    EditText fruitScoreInput;
    EditText vegetableScoreInput;
    EditText proteinScoreInput;
    EditText sugarScoreInput;
    EditText carbScoreInput;
    EditText fatScoreInput;

    CheckBox isVeganCheckbox;
    CheckBox isSnackCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_food_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameInput = findViewById(R.id.nameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        fruitScoreInput = findViewById(R.id.fruitScoreInput);
        vegetableScoreInput = findViewById(R.id.vegetableScoreInput);
        proteinScoreInput = findViewById(R.id.proteinScoreInput);
        sugarScoreInput = findViewById(R.id.sugarScoreInput);
        carbScoreInput = findViewById(R.id.carbScoreInput);
        fatScoreInput = findViewById(R.id.fatScoreInput);

        isVeganCheckbox = findViewById(R.id.isVeganCheckbox);
        isSnackCheckbox = findViewById(R.id.isSnackCheckbox);

        registerListeners();

    }


    private void registerListeners()
    {
        findViewById(R.id.returnButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.confirmFoodAdditionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateInputs())
                {
                    if (addNewFoodEntry()) finish();
                }

            }
        });
    }

    private boolean validateInputs()
    {
        EditText[] scoreInputs = new EditText[]{fruitScoreInput, vegetableScoreInput,
                proteinScoreInput, sugarScoreInput, carbScoreInput, fatScoreInput };

        if (nameInput.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Name is required.", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (EditText input : scoreInputs)
        {
            if (input.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Missing one of the nutrient score values.", Toast.LENGTH_SHORT).show();
                return false;
            }
            int inputValue = Integer.parseInt(input.getText().toString());
            if (inputValue < 1 || inputValue > 10)
            {
                Toast.makeText(this, "Invalid nutrient score value. Keep it between 1 and 10.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private boolean addNewFoodEntry()
    {
        try
        {
            AppDatabase database = AppDatabase.getInstance(getApplicationContext());
            FoodEntryDAO dao = database.getFoodEntryDAO();

            FoodEntry newFoodEntry = FoodEntry.builder().withName(nameInput.getText().toString())
                    .withDescription(descriptionInput.getText().toString())
                    .withFruitScore(Integer.parseInt(fruitScoreInput.getText().toString()))
                    .withVegetableScore(Integer.parseInt(vegetableScoreInput.getText().toString()))
                    .withProteinScore(Integer.parseInt(proteinScoreInput.getText().toString()))
                    .withSugarScore(Integer.parseInt(sugarScoreInput.getText().toString()))
                    .withCarbScore(Integer.parseInt(carbScoreInput.getText().toString()))
                    .withFatScore(Integer.parseInt(fatScoreInput.getText().toString()))
                    .isSnack(isSnackCheckbox.isChecked())
                    .isVegan(isVeganCheckbox.isChecked())
                    .build();

            dao.addFoodEntry(newFoodEntry);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Something went wrong. Entry not added.", Toast.LENGTH_SHORT).show();
            System.out.println(e);
            return false;
        }
        Toast.makeText(this, "Added new food item successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }
}