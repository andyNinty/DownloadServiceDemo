package andy.lee.downloaddemo.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * andy.lee.downloaddemo.service
 * Created by andy on 17-1-11.
 */

public class MyIntentService extends IntentService {
    private static final String TAG = "MyIntentService";
    
    public MyIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
