package main.demon.material.com.mymobliephone.Service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import main.demon.material.com.mymobliephone.DataActivity;
import main.demon.material.com.mymobliephone.MessageActivity;

/**
 * Created by lixinxin on 15/11/5.
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerService";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String START_DATAACTIVITY_PATH = "/start-dataactivity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
    public static final String COUNT_PATH = "/count";
    private static final String COUNT_KEY = "count_key";
    private static final int MAX_LOG_TAG_LENGTH = 23;
    public static GoogleApiClient mGoogleApiClient;
    private ActivityManager mActivityManager;
    private String mPackageName ="main.demon.material.com.mymobliephone.DataActivity";
    public static String dataBroadcast= "DataLayerListenerService.data.Broadcast";

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityManager = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.d(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        if (!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "DataLayerListenerService failed to connect to GoogleApiClient.");
                return;
            }
        }

        // Loop through the events and send a message back to the node that created the data item.
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            DataMap listMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
            String[] data = listMap.getStringArray("data");
//            int count = dataMap.getInt(COUNT_KEY);
//            String count = new String(event.getDataItem().getData());
            if (COUNT_PATH.equals(path)) {
                // Get the node id of the node that created the data item from the host portion of
                // the uri.
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();
                // Send the rpc
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                        payload);
                if (data.length > 0) {
                    if (isAppOnForeground()){
                        SendServiceBroadCast(data);
                    }else {
                        Intent intent = new Intent(this, DataActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("data", data);
                        startActivity(intent);
                    }
                }
//                if (count == 10)
////                if (count.equals("10"))
//                {
//                    Intent intent = new Intent(this, WatchActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d(TAG, "onMessageReceived: " + messageEvent);

        // Check to see if the message is to start an activity
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            String message = new String(messageEvent.getData());
            Intent startIntent = new Intent(this, MessageActivity.class);
            startIntent.putExtra("titile", message);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.d(TAG, "onPeerDisconnected:" + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.d(TAG, "onPeerDisconnected:" + peer);
    }

    public void SendServiceBroadCast(String[] data){
        Intent intent = new Intent();
        intent.setAction(dataBroadcast);
        intent.putExtra("datachanged", data);
        sendBroadcast(intent);
    }


    public boolean isAppOnForeground() {
        List<ActivityManager.RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (mPackageName.equals(tasksInfo.get(0).topActivity.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
