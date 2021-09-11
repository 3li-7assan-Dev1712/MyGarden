package com.example.mygarden;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.mygarden.provider.PlantContract;
import com.example.mygarden.ui.PlantDetailActivity;
import com.example.mygarden.utils.PlantUtils;

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewFactory(this.getApplicationContext());
    }
}
class GridRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory{
    Context mContext;
    Cursor mCursor;
    public GridRemoteViewFactory(Context context){
        this.mContext= context;
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Uri PLANT_URI = PlantContract.PlantEntry.CONTENT_URI;
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME);

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(GridWidgetService.class.getSimpleName(), "getViewAt: " + position);
        if ( (mCursor == null) || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int creationTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        long plantId = mCursor.getLong(idIndex);
        long creationTime = mCursor.getLong(creationTimeIndex);
        long waterdAt = mCursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();
        int plantType = mCursor.getInt(plantTypeIndex);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.plant_widget);

        int imgRec=
                PlantUtils.getPlantImageRes(mContext, timeNow - creationTime, timeNow - waterdAt, plantType);
        views.setImageViewResource(R.id.widget_plant_image, imgRec);
        views.setTextViewText(R.id.plant_type_widget, String.valueOf(plantId));
        views.setViewVisibility(R.id.water_drop_widget, View.GONE);
        Bundle extras = new Bundle();
        extras.putLong(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        Intent fillInIntent= new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_plant_image, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
