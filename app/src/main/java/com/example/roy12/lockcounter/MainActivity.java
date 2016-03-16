package com.example.roy12.lockcounter;

import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CompoundButton
        .OnCheckedChangeListener, DialogInterface.OnClickListener {

    private TextView mTime;

    private ToggleButton mToggleButton;
    private EditText     mInput;
    private CountTimer   mCountTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initAction();

    }

    private void initView() {
        mTime = (TextView) findViewById(R.id.text_time);
        mToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        mToggleButton.setOnCheckedChangeListener(this);

    }

    private void initAction() {
        final Handler handler = new Handler();
        mCountTimer = new CountTimer(handler) {

            @Override
            protected void onTimeFinished() {
                mToggleButton.setChecked(false);
                playSound();
            }

            @Override
            public void updateUI(long timeTask) {
                mTime.setText(CountTimer.convertToString(timeTask));
            }

            @Override
            public void onTimerStopped() {
                mTime.setText("00:00");
            }
        };
    }

    private void playSound() {

        try {
            AssetFileDescriptor file = getAssets().openFd("sounds/1.mp3");
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(file.getFileDescriptor());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.item_user_input, null);
            mInput = (EditText) view.findViewById(R.id.et_input);

            new AlertDialog.Builder(this)
                    .setTitle("请输入时间：")
                    .setMessage("格式为: 00:00")
                    .setView(view)
                    .setCancelable(false)
                    .setPositiveButton("OK", this)
                    .setNegativeButton("Cancel", this)
                    .show();


        } else {

            mCountTimer.stop();

        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                /**
                 * 用户点击了OK后:
                 * 1, 获取输入的时间
                 * 2, 如果时间有效, 开始计时
                 * 3, 如果时间无效, 复位时间为00:00, 复位toggle为off, 重置时间戳.
                 * 4, 输入的时间不能为空
                 *
                 */
                String input = mInput.getText()
                                     .toString();

                if (CountTimer.isValidInput(input)) {
                    mCountTimer.setTimeTask(CountTimer.convertToMilliseconds(input));
                    mCountTimer.start();
                } else {
                    mToggleButton.setChecked(false);
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mToggleButton.setChecked(false);

                break;
        }

    }


}
