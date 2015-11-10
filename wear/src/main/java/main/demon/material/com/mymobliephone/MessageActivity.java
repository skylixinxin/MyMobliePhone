package main.demon.material.com.mymobliephone;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.CardFragment;
import android.text.TextUtils;

/**
 * Created by lixinxin on 15/11/9.
 */
public class MessageActivity extends FragmentActivity{

    private String TAG = "googleAPI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Intent intent = getIntent();
        String titile = intent.getStringExtra("titile");
        if (!TextUtils.isEmpty(titile)){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            CardFragment cardFragment = CardFragment.create(titile,"Description",R.drawable.ic_maps);
            fragmentTransaction.add(R.id.frame_layout, cardFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
