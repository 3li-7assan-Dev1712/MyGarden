package com.example.mygarden.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygarden.R;

import static com.example.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.mygarden.provider.PlantContract.PATH_PLANTS;
import static com.example.mygarden.provider.PlantContract.PlantEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int GARDEN_LOADER_ID = 100;
    private PlantListAdapter mAdapter;

    private RecyclerView mGardenRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The main activity displays the garden as a grid layout recycler view
        mGardenRecyclerView = findViewById(R.id.plants_list_recycler_view);
        mGardenRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 4)
        );
        mAdapter = new PlantListAdapter(this, null);
        mGardenRecyclerView.setAdapter(mAdapter);

//        getSupportLoaderManager().initLoader(GARDEN_LOADER_ID, null, this);
        LoaderManager.getInstance(this).initLoader(GARDEN_LOADER_ID, null, this);
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // WE DO OUT BACKGROUND LOGIC HERE
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        return new CursorLoader(this, PLANT_URI, null,
                null, null, PlantEntry.COLUMN_CREATION_TIME);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // moving curosr to its first row that is because changes might heppen in the top-positioned row D: :D

        if (cursor == null || cursor.getCount() == 0){
            Log.d("MainActivity", "Cursor is null");
            return;
        }

        cursor.moveToFirst();
        Log.d("MainActivity", "going to swap the cursor");
        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public void onPlantClick(View view) {
        ImageView imgView =  view.findViewById(R.id.plant_list_item_image);
        long plantId = (long) imgView.getTag();
        Intent intent = new Intent(getBaseContext(), PlantDetailActivity.class);
        intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        startActivity(intent);
    }


    public void onAddFabClick(View view) {
        Intent intent = new Intent(this, AddPlantActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Activity paused", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        getSupportLoaderManager().restartLoader(GARDEN_LOADER_ID, null, this);
        Toast.makeText(this, "Activity restart again", Toast.LENGTH_SHORT).show();
    }


}