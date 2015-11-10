package main.demon.material.com.mymobliephone;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class MoblieActivity extends FragmentActivity implements DataApi.DataListener, MessageApi.MessageListener
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, NodeApi.NodeListener {

    private EditText editText;
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private final static String GROUP_KEY_EMAILS = "group_key_emails";
    private String pageContent = "当你想要在不需要用户在他们的手机上打开你的app的情况下，还可以让你表达更多的信息，那么你可以在wear上的Notification中添加一个或更多的页面.";
    private int notificationId = 001;
    private int notificationId1 = 002;
    private String TAG = "googleAPI";
    private GoogleApiClient mGoogleAppiClient;
    private boolean mResolvingError = false;
    private static final String COUNT_KEY = "count_key";
    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private ScheduledExecutorService mGeneratorExecutor;
    private ScheduledFuture<?> mDataItemGeneratorFuture;
    private DataThread dataThread;
    private int count = 3;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moblie);
        editText = (EditText) findViewById(R.id.editText);
        mGeneratorExecutor = new ScheduledThreadPoolExecutor(1);
        dataThread = new DataThread();
        mGoogleAppiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Wearable.API)
                .build();
    }

    public void onStartWearableActivityClick(View view) {
        Log.d(TAG, "Generating RPC");
        new StartWearableActivityTask().execute();
    }

    public void sendStartActivityMessage(String node) {
        String message = "Test/Description";
         MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleAppiClient, node, START_ACTIVITY_PATH, message.getBytes()).await();
        if (result.getStatus().isSuccess()){
            Log.e(TAG, "Success to send message with status code: "
                            + result.getStatus().getStatusCode());
        }
//                .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
//            @Override
//            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
//                if (!sendMessageResult.getStatus().isSuccess()) {
//                    Log.e(TAG, "Failed to send message with status code: "
//                            + sendMessageResult.getStatus().getStatusCode());
//                }
//            }
//        });
    }

    public void sendData(View view) {
        dataThread.run();
//        DataApiRequest();
    }

    public void sendNotification(View view) {
        String toSend = editText.getText().toString();
        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(replyLabel).build();
        if (TextUtils.isEmpty(toSend)) {
            toSend = "You sent an empty notification.";
        }
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("send", toSend);
        Intent replyIntent = new Intent(this, ReplyActivity.class);
        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Page 2")
                .bigText(pageContent);
        Notification secondPageNotification = new NotificationCompat.Builder(this).setStyle(secondPageStyle).build();
        PendingIntent replyPendingIntent = PendingIntent.getActivity(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_yoyo, getString(R.string.reply_label),
                replyPendingIntent).addRemoteInput(remoteInput).build();
        Notification notification = new NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_maoyan))
                .setContentTitle("Android Wear小试身手。。。啦啦啦")
                .setContentText(toSend)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .extend(new NotificationCompat.WearableExtender()
                        .setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.bg_welcome_default))
                        .addAction(action)
                        .addPage(secondPageNotification))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(toSend))
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());
        notificationManager.notify(notificationId, notification);
    }

    public void sendStackNotification(View view) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        for (int i = 0; i < 4; i++) {
            Notification notif = new NotificationCompat.Builder(this)
                    .setContentTitle("News from 猫眼电影")
                    .setContentText(pageContent)
                    .setSmallIcon(R.drawable.ic_maoyan)
                    .setGroup(GROUP_KEY_EMAILS)
                    .build();
            notificationManager.notify(notificationId1, notif);
            notificationId1++;
        }

    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleAppiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleAppiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mDataItemGeneratorFuture.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mDataItemGeneratorFuture = mGeneratorExecutor.scheduleWithFixedDelay(new DataItemGenerator(),1,5, TimeUnit.SECONDS);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived() A message from watch was received:" + messageEvent
                .getRequestId() + " " + messageEvent.getPath());
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected:" + node);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConnected:" + node);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mResolvingError = false;
        Wearable.DataApi.addListener(mGoogleAppiClient, this);
        Wearable.MessageApi.addListener(mGoogleAppiClient, this);
        Wearable.NodeApi.addListener(mGoogleAppiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleAppiClient.connect();
            }
        } else {
            Log.e(TAG, "Connection to Google API client has failed");
            mResolvingError = false;
            Wearable.DataApi.removeListener(mGoogleAppiClient, this);
            Wearable.MessageApi.removeListener(mGoogleAppiClient, this);
            Wearable.NodeApi.removeListener(mGoogleAppiClient, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "success");
        }
    }

    @Override
    protected void onStop() {
        if (!mResolvingError) {
            Wearable.DataApi.removeListener(mGoogleAppiClient, this);
            Wearable.MessageApi.removeListener(mGoogleAppiClient, this);
            Wearable.NodeApi.removeListener(mGoogleAppiClient, this);
            mGoogleAppiClient.disconnect();
        }
        if (dataThread.isAlive()) {
            dataThread.stop();
        }
        super.onStop();
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

    private class DataThread extends Thread {

//    }
//
//    private class DataItemGenerator implements Runnable{

        private int count = 3;
        private String data;

        @Override
        public void run() {
            data = String.valueOf(count);
//            PutDataRequest request = PutDataRequest.create("/count");
//            if (!TextUtils.isEmpty(data)){
//                request.setData(data.getBytes());
//            }
            count++;
            String[] datalist = new String[count];
            for (int i = 0; i < count; i++) {
                datalist[i] = "List Item Smaple" + i;
            }
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/count");
//            putDataMapRequest.getDataMap().putInt(COUNT_KEY, count);
            putDataMapRequest.getDataMap().putStringArray("data", datalist);
            PutDataRequest request = putDataMapRequest.asPutDataRequest();
            if (!mGoogleAppiClient.isConnected()) {
                return;
            }
            Wearable.DataApi.putDataItem(mGoogleAppiClient, request).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    if (!dataItemResult.getStatus().isSuccess()) {
                        Log.e(TAG, "ERROR: failed to putDataItem, status code: "
                                + dataItemResult.getStatus().getStatusCode());
                    } else {
                        count++;
                    }
                }
            });
        }
    }

//    private void DataApiRequest() {
//
//        data = String.valueOf(count);
////            PutDataRequest request = PutDataRequest.create("/count");
////            if (!TextUtils.isEmpty(data)){
////                request.setData(data.getBytes());
////            }
//        count++;
//        String[] datalist = new String[count];
//        for (int i = 0; i < count; i++) {
//            datalist[i] = "List Item Smaple" + i;
//        }
//        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/count");
////            putDataMapRequest.getDataMap().putInt(COUNT_KEY, count);
//        putDataMapRequest.getDataMap().putStringArray("data", datalist);
//        PutDataRequest request = putDataMapRequest.asPutDataRequest();
//        if (!mGoogleAppiClient.isConnected()) {
//            return;
//        }
//        Wearable.DataApi.putDataItem(mGoogleAppiClient, request).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
//            @Override
//            public void onResult(DataApi.DataItemResult dataItemResult) {
//                if (!dataItemResult.getStatus().isSuccess()) {
//                    Log.e(TAG, "ERROR: failed to putDataItem, status code: "
//                            + dataItemResult.getStatus().getStatusCode());
//                } else {
//                    count++;
//                }
//            }
//        });
//    }
}
