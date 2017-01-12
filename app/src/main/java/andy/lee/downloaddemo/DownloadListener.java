package andy.lee.downloaddemo;

/**
 * andy.lee.downloaddemo
 * Created by andy on 17-1-12.
 */

public interface DownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPause();

    void onCancel();
}
