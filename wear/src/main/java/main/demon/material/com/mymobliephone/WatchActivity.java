package main.demon.material.com.mymobliephone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;

public class WatchActivity extends Activity{

    private Button button;
    private DelayedConfirmationView delayedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                button = (Button) stub.findViewById(R.id.button);
                delayedView = (DelayedConfirmationView) stub.findViewById(R.id.delayedView);
                delayedView.setTotalTimeMs(3000);
                showOnlyButton();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void beginCountdown(View view) {
        button.setVisibility(View.GONE);
        delayedView.setVisibility(View.VISIBLE);
        delayedView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                grideIntent();
            }

            @Override
            public void onTimerSelected(View view) {
                showOnlyButton();
            }
        });
        delayedView.start();
    }

    public void showOnlyButton() {
        delayedView.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
    }

    public void grideIntent(){
        Intent intent = new Intent(this,GridActivity.class);
        startActivity(intent);
    }

}
