package com.example.roy12.lockcounter;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Roy Sun on 2016/3/16.
 */
public abstract class CountTimer implements Runnable {

    private Handler mHandler;
    private long    timeTask; // 时间戳
    private boolean isKilled;

    private static int INPUT_LENGTH = 5; // 限制为最多5个长度的输入
    private static int COLON_INDEX  = 2; // 需要插入 ":"的位置


    public CountTimer(Handler handler) {
        mHandler = handler;
        this.timeTask = 0;
    }

    public CountTimer(Handler handler, long timeTask) {
        mHandler = handler;
        this.timeTask = timeTask;
    }

    public void setTimeTask(long timeTask) {
        this.timeTask = timeTask;
    }


    public static boolean isValidInput(String time) {
        if (!TextUtils.isEmpty(time)) {

            String trimmedInput = time.trim();
            if (trimmedInput.length() == INPUT_LENGTH && trimmedInput.indexOf(':') == COLON_INDEX) {

                try {
                    int totalDuration = extractTotalDuration(time);
                    return totalDuration > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }


        return false;
    }

    public void start() {
        isKilled = false;
        mHandler.postDelayed(this, 1000);
    }

    public void stop() {
        isKilled = true;
        onTimerStopped();
    }

    @Override
    public void run() {
        if (!isKilled) {
            updateUI(timeTask);

            timeTask -= 1000;
            if (timeTask >= 0) {
                mHandler.postDelayed(this, 1000);
            } else {
                onTimeFinished();
            }
        }
    }

    public static String convertToString(long time) {
        int totalSeconds = (int) (time / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String minuteString = (minutes < 10) ?
                "0" + minutes :
                minutes + "";

        String secondString = (seconds < 10) ?
                "0" + seconds :
                seconds + "";

        return minuteString + ":" + secondString;
    }

    public static long convertToMilliseconds(String time) {

        try {
            return extractTotalDuration(time) * 1000;

        } catch (NumberFormatException e) {
            return 0;
        }

    }

    private static int extractSeconds(String time) throws NumberFormatException {
        return Integer.parseInt(time.substring(COLON_INDEX + 1, time.length()));
    }

    private static int extractMinutes(String time) throws NumberFormatException {
        return Integer.parseInt(time.substring(0, COLON_INDEX));
    }

    private static int extractTotalDuration(String time) throws NumberFormatException {
        return extractMinutes(time) * 60 + extractSeconds(time);
    }


    protected abstract void onTimeFinished();

    public abstract void updateUI(long timeTask);

    public abstract void onTimerStopped();
}
