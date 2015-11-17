package main.demon.material.com.mymobliephone;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.text.TextUtils;

import main.demon.material.com.mymobliephone.Adapter.Adapter;
import main.demon.material.com.mymobliephone.Service.DataLayerListenerService;

/**
 * Created by lixinxin on 15/11/9.
 */
public class DataActivity extends Activity implements WearableListView.ClickListener {

    private  static String[] elements ={"List Item 1", "List Item 2", "List Item 3", "List Item 4", "List Item 5"};
    private String TAG = "DataActivity";
    private WearableListView listView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        listView = (WearableListView) findViewById(R.id.wearable_list);
        if (getIntent()!=null){
            String[] data = getIntent().getStringArrayExtra("data");
            adapter=new Adapter(this, data);
        }else {
            adapter=new Adapter(this, elements);
        }
        listView.setAdapter(adapter);
        listView.setClickListener(this);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Integer tag = (Integer) viewHolder.itemView.getTag();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(DataLayerListenerService.dataBroadcast);
        registerReceiver(dataReceiver, dynamic_filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, DataLayerListenerService.dataBroadcast)){
                String[] data = intent.getStringArrayExtra("datachanged");
                adapter.setDataset(data);
                adapter.notifyDataSetChanged();
            }
        }
    };
}
