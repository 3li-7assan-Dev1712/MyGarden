package com.example.mygarden.ui;


import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygarden.PlantWaterService;
import com.example.mygarden.R;
import com.example.mygarden.provider.PlantContract;

public class AddPlantActivity extends AppCompatActivity {
    private RecyclerView mTypesRecyclerView;
    private PlantTypesAdapter mTypesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        // Plant types are displayed as a recycler view using PlantTypesAdapter
        mTypesAdapter = new PlantTypesAdapter(this);
        mTypesRecyclerView = findViewById(R.id.plant_types_recycler_view);
        mTypesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        // ou know I',really  extraOrdinaryperson bhy just reading, seat number a conna
        mTypesRecyclerView.setAdapter(mTypesAdapter);

    }

    /**
     * Event handler to handle clicking on a plant type
     *
     * @param view
     */
    public void onPlantTypeClick(View view) {
        // When the chosen plant type is clicked, create a new plant and set the creation time and
        // water time to now
        // Extract the plant type from the tag
        // that is really depends on others you
        // we get the clicked image first

        Log.d("TAG", "on Plnat Click ");
        ImageView imgView = view.findViewById(R.id.plant_type_image);
        int plantType = (int) imgView.getTag();
        Log.d("AddPlantActivity.class", plantType + "");
//
        long timeNow = System.currentTimeMillis();
//        // Insert the new plant into DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlantContract.PlantEntry.COLUMN_PLANT_TYPE, plantType);
        contentValues.put(PlantContract.PlantEntry.COLUMN_CREATION_TIME, timeNow);
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().insert(PlantContract.PlantEntry.CONTENT_URI, contentValues);
        Toast.makeText(getApplicationContext(), "Plnat added to the database", Toast.LENGTH_SHORT).show();
        Log.d("ADD", "plant added to the database");
        // Close this activity
        PlantWaterService.startActionUpdatePlant(this);
        finish();
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}
