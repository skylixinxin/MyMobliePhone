package main.demon.material.com.mymobliephone;

import android.app.Activity;
import android.app.RemoteInput;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by lixinxin on 15/10/28.
 */
public class ReplyActivity extends Activity {

    private TextView textView;
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_layout);
        textView = (TextView) findViewById(R.id.text_example);
        Intent intent = getIntent();
        textView.setText(getMessageText(intent)!=null?getMessageText(intent):"空");
    }

    private CharSequence getMessageText(Intent intent) {
        if (Build.VERSION.SDK_INT >= 20) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                return remoteInput.getCharSequence(EXTRA_VOICE_REPLY);
            }
        } else {
            ClipData remoteData = intent.getClipData();
            Bundle remoteInput = remoteData.getItemAt(0).getIntent().getExtras().getParcelable(RemoteInput.EXTRA_RESULTS_DATA);
            CharSequence replyMessage = remoteInput.getCharSequence(EXTRA_VOICE_REPLY);
            return replyMessage;
        }
        return null;
    }
}
