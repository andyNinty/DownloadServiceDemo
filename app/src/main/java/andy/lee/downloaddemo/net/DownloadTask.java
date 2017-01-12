package andy.lee.downloaddemo.net;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import andy.lee.downloaddemo.DownloadListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * andy.lee.downloaddemo
 * Created by andy on 17-1-11.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = "DownloadTask";

    private static final int DOWNLOAD_SUCCESS = 0;
    private static final int DOWNLOAD_FAILED = 1;
    private static final int DOWNLOAD_PAUSE = 2;
    private static final int DOWNLOAD_CANCEL = 3;

    private DownloadListener mListener;

    private boolean isPause = false;
    private boolean isCancel = false;
    private int lastProgress;


    public DownloadTask(DownloadListener listener) {
        mListener = listener;
    }


    @Override
    protected Integer doInBackground(String... strings) {

        InputStream is = null;
        File file = null;
        RandomAccessFile accessFile = null;
        //已下载文件长度
        long downloadLength = 0;
        //文件总长度
        long contentLength;
        String url = strings[0];
        try {
            String fileName = url.substring(url.lastIndexOf("/"));
            String fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(fileDir + fileName);
            if (file.exists()) {
                downloadLength = file.length();
            }
            contentLength = getContentLength(url);
            if (contentLength == 0) {
                return DOWNLOAD_FAILED;
            } else if (contentLength == downloadLength) {
                return DOWNLOAD_SUCCESS;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    //okhttp 断点下载
                    .addHeader("RANGE", "bytes=" + downloadLength + "-")
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                accessFile = new RandomAccessFile(file, "rw");
                accessFile.seek(downloadLength);
                byte[] bytes = new byte[1024];
                int total = 0;
                int length;
                while ((length = is.read(bytes)) != -1) {
                    if (isCancel) {
                        return DOWNLOAD_CANCEL;
                    } else if (isPause) {
                        return DOWNLOAD_PAUSE;
                    } else {
                        total += length;
                        accessFile.write(bytes, 0, length);
                        int progress = (int) ((total + downloadLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return DOWNLOAD_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (accessFile != null) {
                    accessFile.close();
                }
                if (isCancel && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return DOWNLOAD_FAILED;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            mListener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case DOWNLOAD_SUCCESS:
                mListener.onSuccess();
                break;
            case DOWNLOAD_FAILED:
                mListener.onFailed();
                break;
            case DOWNLOAD_PAUSE:
                mListener.onPause();
                break;
            case DOWNLOAD_CANCEL:
                mListener.onCancel();
                break;
        }
    }

    public void pauseDownload() {
        isPause = true;
    }

    public void cancelDownload() {
        isCancel = true;
    }

    //计算文件长度
    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

}
