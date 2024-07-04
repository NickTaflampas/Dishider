package me.codenick.dishider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class FoodOptions extends AppCompatActivity {

    float fruitScore;
    float vegetableScore;
    float proteinScore;
    float sugarScore;
    float carbScore;
    float fatScore;

    CheckBox useNutrientScores;
    CheckBox isVegan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_options);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        fruitScore = extras.getFloat("fruitScore");
        vegetableScore = extras.getFloat("vegetableScore");
        proteinScore = extras.getFloat("proteinScore");
        sugarScore = extras.getFloat("sugarScore");
        carbScore = extras.getFloat("carbScore");
        fatScore = extras.getFloat("fatScore");

        useNutrientScores = findViewById(R.id.nutrient_checkbox);
        isVegan = findViewById(R.id.vegan_checkbox);

        findViewById(R.id.getFoodButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FoodResult.class);
                intent.putExtra("useNutrients", useNutrientScores.isChecked());
                intent.putExtra("isVegan", isVegan.isChecked());

                RadioGroup snackGroup = findViewById(R.id.snack_group);
                intent.putExtra("snackOption",
                        snackGroup.indexOfChild(findViewById(snackGroup.getCheckedRadioButtonId())));


                if (useNutrientScores.isChecked())
                {
                    intent.putExtra("fruitScore", fruitScore);
                    intent.putExtra("vegetableScore", vegetableScore);
                    intent.putExtra("proteinScore", proteinScore);
                    intent.putExtra("sugarScore", sugarScore);
                    intent.putExtra("carbScore", carbScore);
                    intent.putExtra("fatScore", fatScore);
                }
                startActivity(intent);
            }
        });


    }
}