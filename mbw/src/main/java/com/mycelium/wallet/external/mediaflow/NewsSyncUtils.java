package com.mycelium.wallet.external.mediaflow;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;
import com.mycelium.wallet.external.mediaflow.database.NewsDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class NewsSyncUtils {
    private static final int REQUEST_CODE = 1001;
    private static final String MEDIA_OPERATION = "operation";
    private static final String OPERATION_DELETE = "delete";
    public static final String ID = "id";

    public static void startNewsUpdateRepeating(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NewsSyncReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP
                , System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10)
                , AlarmManager.INTERVAL_HOUR, alarmIntent);
    }

    public static void delete(final Context context, final String id) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                NewsDatabase.INSTANCE.delete(id);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NewsConstants.MEDIA_FLOW_UPDATE_ACTION));
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void handle(Context context, RemoteMessage remoteMessage) {
        String data = remoteMessage.getData().get("data");
        try {
            JSONObject dataObject = new JSONObject(data);
            String operation = dataObject.getString(MEDIA_OPERATION);
            if (OPERATION_DELETE.equalsIgnoreCase(operation) && dataObject.has(ID)) {
                delete(context, dataObject.getString(ID));
            } else {
                context.startService(new Intent(context, NewsSyncService.class));
            }
        } catch (JSONException e) {
            Log.e("NewsSync", "json data wrong", e);
        }
    }
}
