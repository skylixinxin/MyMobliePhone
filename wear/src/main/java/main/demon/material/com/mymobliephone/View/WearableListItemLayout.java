package main.demon.material.com.mymobliephone.View;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import main.demon.material.com.mymobliephone.R;

/**
 * Created by lixinxin on 15/11/9.
 */
public class WearableListItemLayout extends LinearLayout implements WearableListView.OnCenterProximityListener {

    private final float mFadedTextAlpha;
    private final int mFadedCircleColor;
    private final int mChosenCircleColor;
    private ImageView mCircle;
    private float mScale;
    private TextView mName;

    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFadedTextAlpha = getResources().getInteger(R.integer.action_text_fade_alpha)/100f;
        mFadedCircleColor = getResources().getColor(R.color.grey);
        mChosenCircleColor = getResources().getColor(R.color.blue);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCircle = (ImageView) findViewById(R.id.circle);
        mName = (TextView) findViewById(R.id.name);
    }

    @Override
    public void onCenterPosition(boolean b) {
        mName.animate().alpha(1f).scaleX(1.1f).scaleY(1.1f);
        mCircle.animate().alpha(1f).scaleX(1.3f).scaleY(1.3f);
        ((GradientDrawable) mCircle.getDrawable()).setColor(mChosenCircleColor);
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        mName.animate().alpha(0.5f).scaleX(1f).scaleY(1f);
        mCircle.animate().alpha(0.5f).scaleX(1f).scaleY(1f);
        ((GradientDrawable) mCircle.getDrawable()).setColor(mFadedCircleColor);
    }
}
