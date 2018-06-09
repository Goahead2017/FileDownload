package com.bignerdranch.android.download;

/**
 * 对文件的下载状态进行监听
 */

public interface DownloadListener {

    //取消下载
    void onCancel();

    //下载失败
    void onFail();

//    //下载预处理，可通过HttpURLConnection获取文件长度
//    void onPreDownload(HttpURLConnection connection);

    //下载监听
    void onProgress(long currentLocation);

//    //单一线程的结束位置
//    void onChildComplete(long finishLocation);

    //开始
    void onStart(/*long startLocation*/);

    //子线程恢复下载的位置
//    void onChildResume(long resumeLocation);

    //恢复位置
//    void onResume(long resumeLocation);

    //停止
    void onStop(/*long stopLocation*/);

    //下载完成
    void onComplete();

}
