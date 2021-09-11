package com.example.mygarden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.mygarden.provider.PlantContract;
import com.example.mygarden.ui.MainActivity;
import com.example.mygarden.ui.PlantDetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class PlantWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object//        views.setTextViewText(R.id.appwidget_text, widgetText);
        //
        //        // Instruct the widget manager to update the widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);
        Intent intent = new Intent (context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
        PlantWaterService.startActionUpdatePlant(context);
        Log.d(PlantWidgetProvider.class.getSimpleName(), "start update the plant from on update method");
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    public static void updatePlantWidget (Context context, AppWidgetManager manager, int rec, int [] widgetIds, long plantId, boolean canWater){
        for (int id : widgetIds){
            updateAppWidgets(context,
                    manager,
                    rec,
                    id,
                    plantId,
                    canWater);
        }
    }
    public static void updateAppWidgets(Context context, AppWidgetManager manager, int rec, int widgetId, long plantId, boolean canWater){
        // we mentioned in order to update a widget we need two primary arguments 1- RemoteViews 2- manager 3- WidgetId

        RemoteViews rv;
        Bundle bundle = manager.getAppWidgetOptions(widgetId);
        int width = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        if (width < 300 ){
            rv = getSinglePlantRemoteView(context, manager, rec, widgetId, plantId, canWater);
        }
        else {
            rv = getGardenRemoteViews(context);
        }
        manager.updateAppWidget(widgetId, rv);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        PlantWaterService.startActionUpdatePlant(context);
    }
    public static RemoteViews getSinglePlantRemoteView(Context context, AppWidgetManager manager, int rec, int widgetId, long plantId, boolean canWater){
        Intent intent;
        if (plantId == PlantContract.INVALID_PLANT_ID){
            intent = new Intent(context, MainActivity.class);
        } else {
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
            intent.putExtra("ali", canWater);
        }
//        intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
//        Log.d(PlantWidgetProvider.class.getSimpleName(), "plant id in widget provider " + plantId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.plant_widget);
        remoteViews.setImageViewResource(R.id.widget_plant_image, rec);
        Log.d(PlantWidgetProvider.class.getSimpleName(), "can water is: " + canWater);
        if (canWater) remoteViews.setViewVisibility(R.id.water_drop_widget, View.VISIBLE);
        else remoteViews.setViewVisibility(R.id.water_drop_widget, View.INVISIBLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_plant_image,
                pendingIntent);
        Intent wateringIntent = new Intent(context, PlantWaterService.class);
        wateringIntent.setAction(PlantWaterService.ACTION_WATER_PLANT);
        wateringIntent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        PendingIntent waterPendingIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.water_drop_widget, waterPendingIntent);
        return remoteViews;
    }
    public static RemoteViews getGardenRemoteViews(Context context){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);
        Intent intent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);

        Intent appIntent = new Intent(context, PlantDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }
}

