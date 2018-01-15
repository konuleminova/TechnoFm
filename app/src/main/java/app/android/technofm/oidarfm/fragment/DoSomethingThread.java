package app.android.technofm.oidarfm.fragment;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by Asus on 1/9/2018.
 */

public class DoSomethingThread extends Thread
{
    private static final String TAG = "DoSomethingThread";
        private static final int DELAY = 5000; // 5 seconds


        @Override
        public void run() {
            Log.v(TAG, "doing work in Random Number Thread");
            while (true) {

                // need to publish the random number back on the UI at this point in the code through the publishProgress(randNum) call
                // publishProgress(randNum);
                try {
                    Log.v(TAG, "doing work in Random Number Thread");

                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    Log.v(TAG, "Interrupting and stopping the Random Number Thread");
                    return;
                }
            }
        }
}