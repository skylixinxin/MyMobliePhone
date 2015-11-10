package main.demon.material.com.mymobliephone;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.GridViewPager;

import main.demon.material.com.mymobliephone.Adapter.SampleGridePagerAdapter;

/**
 * Created by lixinxin on 15/11/10.
 */
public class GridActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grideview);
        final GridViewPager pager = (GridViewPager)findViewById(R.id.wearable_gridview);
        pager.setAdapter(new SampleGridePagerAdapter(this, getFragmentManager()));
    }
}
