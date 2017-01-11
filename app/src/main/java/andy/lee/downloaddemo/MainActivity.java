package andy.lee.downloaddemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import andy.lee.downloaddemo.service.DownloadService;
import andy.lee.downloaddemo.service.MyIntentService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBindServiceBtn, mStartServiceBtn;
    private Button mUnBindServiceBtn, mStopServiceBtn;
    private Button mIntentServiceBtn;
    private DownloadService.DownloadBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBindServiceBtn = (Button) findViewById(R.id.bind_service);
        mUnBindServiceBtn = (Button) findViewById(R.id.unBind_service);
        mStartServiceBtn = (Button) findViewById(R.id.start_service);
        mStopServiceBtn = (Button) findViewById(R.id.stop_service);
        mIntentServiceBtn = (Button) findViewById(R.id.start_intentService);

        mBindServiceBtn.setOnClickListener(this);
        mUnBindServiceBtn.setOnClickListener(this);
        mStartServiceBtn.setOnClickListener(this);
        mStopServiceBtn.setOnClickListener(this);
        mIntentServiceBtn.setOnClickListener(this);

    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mBinder = (DownloadService.DownloadBinder) binder;
            mBinder.startDownload();
            mBinder.getProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_service:
                startService(new Intent(this, DownloadService.class));
                break;
            case R.id.stop_service:
                stopService(new Intent(this, DownloadService.class));
                break;
            case R.id.bind_service:
                Intent intent = new Intent(this, DownloadService.class);
                bindService(intent, mConnection, BIND_AUTO_CREATE);
                //auto create 表示绑定成功之后就启动服务,此时onCreate方法会得到执行,onStartCommand方法不会得到执行
                break;
            case R.id.unBind_service:
                unbindService(mConnection);
                break;
            case R.id.start_intentService:
                startService(new Intent(this, MyIntentService.class));
                break;

        }
    }
}