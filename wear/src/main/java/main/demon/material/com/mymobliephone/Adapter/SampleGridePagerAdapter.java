package main.demon.material.com.mymobliephone.Adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.CardFrame;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.Gravity;

import java.util.List;

import main.demon.material.com.mymobliephone.R;

/**
 * Created by lixinxin on 15/11/10.
 */
public class SampleGridePagerAdapter extends FragmentGridPagerAdapter{

    private final Context mContext;
    private List mRows;
    static final int[] BG_IMAGES = new int[]{
            R.drawable.grid_background_one,R.drawable.grid_background_two,
            R.drawable.grid_background_three,R.drawable.grid_background_four,
            R.drawable.grid_background_five
    };
    private static class Page{
        int titleRes = R.string.titleRes;
        int textRes = R.string.textRes;
        int iconRes = R.drawable.ic_maps;
        int cardGravity = Gravity.CENTER;
        boolean expansionEnabled = true;
        int expansionDirection = CardFrame.EXPAND_DOWN;
    }

    private Page[] pagerow = {new Page(), new Page(), new Page()};
    private final Page[][] PAGES = {pagerow, pagerow, pagerow,pagerow,pagerow};

    public SampleGridePagerAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.mContext =context;
    }

    @Override
    public Fragment getFragment(int i, int i1) {
        Page page = PAGES[i][i1];
        String title = page.titleRes!=0?mContext.getString(page.titleRes):null;
        String text = page.textRes!=0?mContext.getString(page.textRes):null;
        CardFragment fragment = CardFragment.create(title,text,page.iconRes);
        fragment.setCardGravity(page.cardGravity);
        fragment.setExpansionEnabled(page.expansionEnabled);
        fragment.setExpansionDirection(page.expansionDirection);
        return fragment;
    }

    @Override
    public Drawable getBackgroundForRow(int row) {
        return mContext.getResources().getDrawable((BG_IMAGES[row % BG_IMAGES.length]));
    }

    @Override
    public int getColumnCount(int i) {
        return PAGES[i].length;
    }

    @Override
    public int getRowCount() {
        return PAGES.length;
    }
}
