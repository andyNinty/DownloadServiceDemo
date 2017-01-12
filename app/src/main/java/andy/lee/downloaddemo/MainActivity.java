package andy.lee.downloaddemo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import andy.lee.downloaddemo.service.DownloadService;
import andy.lee.downloaddemo.service.MyIntentService;
import andy.lee.downloaddemo.service.MyService;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button mBindServiceBtn, mStartServiceBtn;
    private Button mUnBindServiceBtn, mStopServiceBtn;
    private Button mIntentServiceBtn;
    private DownloadService.DownloadBinder mBinder;


    private Button mStartBtn, mPauseBtn, mCancelBtn, mDelBtn;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mBinder = (DownloadService.DownloadBinder) binder;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBindServiceBtn = (Button) findViewById(R.id.bind_service);
        mUnBindServiceBtn = (Button) findViewById(R.id.unBind_service);
        mStartServiceBtn = (Button) findViewById(R.id.start_service);
        mStopServiceBtn = (Button) findViewById(R.id.stop_service);
        mIntentServiceBtn = (Button) findViewById(R.id.start_intentService);
        mDelBtn = (Button) findViewById(R.id.rm_file);

        mStartBtn = (Button) findViewById(R.id.start_download);
        mPauseBtn = (Button) findViewById(R.id.pause_download);
        mCancelBtn = (Button) findViewById(R.id.cancel_download);

        mBindServiceBtn.setOnClickListener(this);
        mUnBindServiceBtn.setOnClickListener(this);
        mStartServiceBtn.setOnClickListener(this);
        mStopServiceBtn.setOnClickListener(this);
        mIntentServiceBtn.setOnClickListener(this);

        mStartBtn.setOnClickListener(this);
        mPauseBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
        mDelBtn.setOnClickListener(this);

        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        checkAppPermission();

    }

    private void checkAppPermission() {
        requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> permissionList) {

            }
        });
    }


    public static final String URL = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";

    @Override
    public void onClick(View view) {
        if (mBinder == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.start_service:
                startService(new Intent(this, MyService.class));
                break;
            case R.id.stop_service:
                stopService(new Intent(this, MyService.class));
                break;
            case R.id.bind_service:
                Intent intent = new Intent(this, MyService.class);
                bindService(intent, mConnection, BIND_AUTO_CREATE);
                //auto create 表示绑定成功之后就启动服务,此时onCreate方法会得到执行,onStartCommand方法不会得到执行
                break;
            case R.id.unBind_service:
                unbindService(mConnection);
                break;
            case R.id.start_intentService:
                startService(new Intent(this, MyIntentService.class));
                break;
            case R.id.start_download:
                mBinder.startDownload(URL);
                break;
            case R.id.pause_download:
                mBinder.pauseDownload();
                break;
            case R.id.cancel_download:
                mBinder.cancelDownload();
                break;
            case R.id.rm_file:
                String fileName = URL.substring(URL.lastIndexOf("/"));
                String fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(fileDir + fileName);
                if (file.exists()) {
                    boolean isDel = file.delete();
                    if (isDel) {
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}