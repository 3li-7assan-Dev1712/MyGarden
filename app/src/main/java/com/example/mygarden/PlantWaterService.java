package com.example.mygarden;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.util.Log;

import com.example.mygarden.provider.PlantContract;
import com.example.mygarden.ui.PlantDetailActivity;
import com.example.mygarden.utils.PlantUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlantWaterService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    public  static final String ACTION_WATER_PLANT = "com.example.mygarden.action.water_plant";
    public static final String ACTION_UPDATE_PLANT = "com.example.mygarden.action.update_plant";

    public PlantWaterService() {
        super("PlantWaterService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method


    public static void startActionUpdatePlant (Context context){
        Intent updatePlantIntent = new Intent(context, PlantWaterService.class);
        updatePlantIntent.setAction(ACTION_UPDATE_PLANT);
        context.startService(updatePlantIntent);
    }
    public static void startActionWaterPlant(Context context){
        Intent waterIntent= new Intent(context, PlantWaterService.class);
        waterIntent.setAction(ACTION_WATER_PLANT);
        context.startService(waterIntent);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WATER_PLANT.equals(action)) {
                handleActionWaterPlant(intent.getLongExtra(PlantDetailActivity.EXTRA_PLANT_ID, PlantContract.INVALID_PLANT_ID));
            } else if (ACTION_UPDATE_PLANT.equals(action)) {
                handleActionUpdateWidgetPlants();
            }
        }
    }

    private void handleActionUpdateWidgetPlants (){
        Log.d(PlantWaterService.class.getSimpleName(), "handle the action to update the plant");
        Uri SINGLE_PLANT_URI = PlantContract.PlantEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(SINGLE_PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int imageRec;
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            // any who do you think speaking more than one language would bear much fruit
            int createdAtIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
            int waterAtIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int plantTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);
            long wateredAt = cursor.getLong(waterAtIndex);
            long createdAt = cursor.getLong(createdAtIndex);
            int plantType = cursor.getInt(plantTypeIndex);
            long timeNow  = System.currentTimeMillis();
            long plantId = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry._ID));
            cursor.close();
            imageRec = PlantUtils.getPlantImageRes(this,
                    timeNow - createdAt,
                    timeNow - wateredAt,
                    plantType);
            Log.d(PlantWaterService.class.getSimpleName(), "plant id is from the database" + plantId);
            boolean canWater=
                    timeNow - wateredAt > PlantUtils.MIN_AGE_BETWEEN_WATER && timeNow - wateredAt < PlantUtils.MAX_AGE_WITHOUT_WATER;


            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            int [] widgetIds = manager.getAppWidgetIds(new ComponentName(this, PlantWidgetProvider.class));
            manager.notifyAppWidgetViewDataChanged(widgetIds, R.id.widget_grid_view);
            PlantWidgetProvider.updatePlantWidget(this,
                    manager,
                    imageRec,
                    widgetIds,
                    plantId,
                    canWater);
        }
    }
    private void handleActionWaterPlant(long id){
        long timeNow = System.currentTimeMillis();
        Uri SINGLE_URI_PLANT = ContentUris.withAppendedId(PlantContract.PlantEntry.CONTENT_URI, id);
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().update(SINGLE_URI_PLANT,
                contentValues,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME + ">?",
                new String[]{String.valueOf(timeNow - PlantUtils.MAX_AGE_WITHOUT_WATER)});
        startActionUpdatePlant(this);
    }

}
