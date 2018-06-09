package com.bignerdranch.android.download;

import android.util.Log;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 多线程下载任务类
 */

public class DownloadTask extends Thread {

    private static final String TAG = "DownloadTask";
    private DownloadEntity dEntity;
    private DownloadListener downloadListener;
//    private String configFPath;

//    private String downloadUrl;

    public DownloadTask(DownloadEntity downloadInfo/*,String downloadUrl*/,DownloadListener downloadListener){
        this.dEntity = downloadInfo;
        this.downloadListener = downloadListener;
//        this.downloadUrl = downloadUrl;
//        configFPath = dEntity.context.getFilesDir().getPath() + "/temp/" + dEntity.tempFile.getName() + ".properties";
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "线程_" + dEntity.threadId + "_正在下载【" + "开始位置：" + dEntity.startLocation + "，结束位置：" + dEntity.endLocation + "】");
            URL url = new URL(dEntity.downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //在头里面请求下载开始位置和结束位置
//            conn.setRequestProperty("Range", "bytes=" + dEntity.startLocation + "-" + dEntity.endLocation);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Accept","image/gif,image/jpeg,image/pjpeg,image/pjpeg,application/x-shockwave-flash,application/xaml+xml,application/vnd.ms-xpsdocument,application/x-ms-xbap,application/x-ms-application,application/vnd.ms-excel,application/vnd.ms-powerpoint,application/msword,*/*");
            conn.setRequestProperty("Accept-Language","zhi-CN");
            conn.setRequestProperty("Charset","UTF-8");
            conn.setRequestProperty("Range", "bytes=" + dEntity.startLocation + "-" + dEntity.endLocation);
            conn.setRequestProperty("User-Agent","Mozilla/4.0(compatible;MSIE 8.0;Window NT 5.2;Trident/4.0;.NET CLR 1.1.4322;.NET CLR 2.0.50727;.NET CLR 3.0.04506.30;.NET CLR 3.0.4506.2152;.NET CLR 3.5.30729)");
            conn.setRequestProperty("Connection","Keep-Alive");
//            conn.setRequestProperty("Charset","UTF-8");
//            conn.setReadTimeout(TIME_OUT);
//            conn.setRequestProperty("User-Agent","Mozilla/4.0(compatible;MSIE 8.0;Window NT 5.2;Trident/4.0;.NET CLR 1.1.4322;.NET CLR 2.0.50727;.NET CLR 3.0.04506.30;.NET CLR 3.0.4506.2152;.NET CLR 3.5.30729)");
//            conn.setRequestProperty("Accept","image/gif,image/jpeg,image/pjpeg,image/pjpeg,application/x-shockwave-flash,application/xaml+xml,application/vnd.ms-xpsdocument,application/x-ms-xbap,application/x-ms-application,application/vnd.ms-excel,application/vnd.ms-powerpoint,application/msword,*/*");
//            //设置读取流的等待时间
//            conn.setReadTimeout(2000);

//            InputStream is = null;
//            RandomAccessFile savedFile = null;
//            File file
//            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
//            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                InputStream is = conn.getInputStream();
                //创建可设置位置的文件
                RandomAccessFile file = new RandomAccessFile(dEntity.tempFile, "rwd");
                //设置每条线程写入文件的位置
                file.seek(dEntity.startLocation);
                byte[] buffer = new byte[1024];
                int len = 0;
                //当前子线程的下载位置
                long currentLocation = dEntity.startLocation;
                while ((len = is.read(buffer)) != -1 && currentLocation - dEntity.startLocation < dEntity.endLocation - dEntity.startLocation) {
                    if (/*isCancel*/StaticNum.isCancel) {
                        Log.d(TAG, "thread_" + dEntity.threadId + "_cancel");
                        break;
                    }
                    if (/*isStop*/StaticNum.isStop) {
                        break;
                    }

                    //把下载数据写入文件
                    file.write(buffer, 0, len);
                    synchronized (/*DownLoadUtil.*/this) {
                    /*mCurrentLocation += len;*/
                    /*currentLocation*/
                        StaticNum.mCurrentLocation += len;
                        downloadListener.onProgress(StaticNum.mCurrentLocation);
//                        mListener.onProgress(/*mCurrentLocation*/StaticNum.mCurrentLocation);
                    }
                    currentLocation += len;
                }
                file.close();
                is.close();
                StaticNum.mCompleteThreadNum ++;
//                if(StaticNum.mCompleteThreadNum == StaticNum.THREAD_NUM){
//                    mListener.onComplete();
//                }

//            if(/*isCancel*/StaticNum.isCancel){
//                synchronized (/*DownLoadUtil.*/this){
//                    /*mCancelNum ++;*/
//                    StaticNum.mCancelNum++;
//                    if(StaticNum.mCancelNum == StaticNum.THREAD_NUM){
//                        File configFile = new File(configFPath);
//                        if(configFile.exists()){
//                            configFile.delete();
//                        }
//
//                        if(dEntity.tempFile.exists()){
//                            dEntity.tempFile.delete();
//                        }
//                        Log.d(TAG,"onCancel");
//                        /*isDownloading*/StaticNum.isDownloading = false;
//                        mListener.onCancel();
//                        System.gc();
//                    }
//                }
//                return;
//            }
//
//            //停止状态不需要删除记录文件
//            if(/*isStop*/StaticNum.isStop){
//                synchronized (/*DownLoadUtil.*/this){
//                    /*mStopNum*/StaticNum.mStopNum ++;
//                    String location = String.valueOf(currentLocation);
//                    Log.i(TAG,"thread_" + dEntity.threadId + "_stop,stop location ==>" + currentLocation);
//                    /*writeConfig(dEntity.tempFile.getName() + "_record_" + dEntity.threadId,location);*/
//                    if(/*mStopNum*/StaticNum.mStopNum == /*THREAD_NUM*/StaticNum.THREAD_NUM){
//                        Log.d(TAG,"onStop");
//                        /*isDownloading = false;*/
//                        StaticNum.isDownloading = false;
//                        mListener.onStop(/*mCurrentLocation*/StaticNum.mCurrentLocation);
//                        System.gc();
//                    }
//                }
//                return;
//            }

                Log.i(TAG, "线程【" + dEntity.threadId + "】下载完毕");
        }catch (Exception e){
            Log.e(TAG,"线程" + (dEntity.threadId + 1) + "下载出错" + e);
        }
            /*writeConfig(dEntity.tempFile.getName() + "_state_" + dEntity.threadId,1 + "");*/
//            mListener.onChildComplete(dEntity.endLocation);
            /*mCompleteThreadNum*/
//            if(/*mCompleteThreadNum*/StaticNum.mCompleteThreadNum == /*THREAD_NUM*/StaticNum.THREAD_NUM){
//                File configFile = new File(configFPath);
//                if(configFile.exists()){
//                    configFile.delete();
//                }
//                mListener.onComplete();
//                /*isDownloading*/StaticNum.isDownloading = false;
//                System.gc();
//            }
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//            /*isDownloading = false;*/
//            StaticNum.isDownloading = false;
//            mListener.onFail();
//        }catch (IOException e){
//            Log.e(TAG, "下载失败【" + dEntity.downloadUrl + "】");
//            /*isDownloading = false;*/
//            StaticNum.isDownloading = false;
//            mListener.onFail();
//        }catch (Exception e){
//            Log.e(TAG, "获取流失败" );
//            /*isDownloading = false;*/
//            StaticNum.isDownloading = false;
//            mListener.onFail();
//        }
    }

    //下载入口
//    public void download(final Context context, @NonNull final String downloadUrl,@NonNull final String filePath,@NonNull final DownloadListener downloadListener){
//        /*isDownloading*/StaticNum.isDownloading = true;
//        /*mCurrentLocation*/StaticNum.mCurrentLocation = 0L;
//        /*isStop*/StaticNum.isStop = false;
//        /*isCancel*/StaticNum.isCancel = false;
//        /*mCancelNum*/StaticNum.mCancelNum = 0;
//        /*mStopNum*/StaticNum.mStopNum = 0;
//        final File dFile = new File(filePath);
//        //读取已完成的线程数
//        final File configFile = new File(context.getFilesDir().getPath() + "/temp/" + dEntity.tempFile.getName() + ".properties");
//        try {
//            if(!configFile.exists()){
//                //记录文件被删除，则重新下载
//                /*newTask*/StaticNum.newTask = true;
//                /*FileUtil.createFile(configFile.getPath());*/
//                return;
//            }else {
//                /*newTask*/StaticNum.newTask = false;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            mListener.onFail();
//            return;
//        }
//
//        /*newTask*/StaticNum.newTask = !dFile.exists();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mListener = downloadListener;
//                    URL url = null;
//                    url = new URL(downloadUrl);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setRequestProperty("Charset", "UTF-8");
//                    conn.setReadTimeout(TIME_OUT);
//                    conn.setRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE 8.0;Window NT 5.2;Trident/4.0;.NET CLR 1.1.4322;.NET CLR 2.0.50727;.NET CLR 3.0.04506.30;.NET CLR 3.0.4506.2152;.NET CLR 3.5.30729)");
//                    conn.setRequestProperty("Accept", "image/gif,image/jpeg,image/pjpeg,image/pjpeg,application/x-shockwave-flash,application/xaml+xml,application/vnd.ms-xpsdocument,application/x-ms-xbap,application/x-ms-application,application/vnd.ms-excel,application/vnd.ms-powerpoint,application/msword*/*");
//                    conn.connect();
//                    int len = conn.getContentLength();
//                    if (len < 0) {
//                        //网络被劫持时会出现这个问题
//                        mListener.onFail();
//                        return;
//                    }
//                    int code = conn.getResponseCode();
//                    if (code == 200) {
//                        int fileLength = conn.getContentLength();
//                        //必须建一个文件
//                        /*FileUtil.createFile(filePath);*/
//                        RandomAccessFile file = new RandomAccessFile(filePath, "rwd");
//                        //设置文件长度
//                        file.setLength(fileLength);
//                        mListener.onPreDownload(conn);
//                        //分配每条线程的下载区间
//                        Properties pro = null;
//                        /*pro = Util.loadConfig(configFile);*/
//                        int blockSize = fileLength / /*THREAD_NUM*/StaticNum.THREAD_NUM;
//                        SparseArray<Thread> tasks = new SparseArray<>();
//                        for (int i = 0; i < /*THREAD_NUM*/StaticNum.THREAD_NUM; i++) {
//                            long startL = i * blockSize, endL = (i + 1) * blockSize;
//                            Object state = pro.getProperty(dFile.getName() + "_state_" + i);
//                            if (state != null && Integer.parseInt(state + "") == 1) {
//                                //该线程已经完成
//                                /*mCurrentLocation*/StaticNum.mCurrentLocation += endL - startL;
//                                Log.d(TAG, "线程_" + i + "_已经下载完成");
//                                /*mCompleteThreadNum*/StaticNum.mCompleteThreadNum++;
//                                if (/*mCompleteThreadNum*/StaticNum.mCompleteThreadNum == /*THREAD_NUM*/StaticNum.THREAD_NUM) {
//                                    if (configFile.exists()) {
//                                        configFile.delete();
//                                    }
//                                    mListener.onComplete();
//                                    /*isDownloading*/StaticNum.isDownloading = false;
//                                    System.gc();
//                                    return;
//                                }
//                                continue;
//                            }
//                            //分配下载位置
//                            Object record = pro.getProperty(dFile.getName() + "_record_" + i);
//                            if (!/*newTask*/StaticNum.newTask && record != null && Long.parseLong(record + "") > 0) {
//                                //如果有记录，则恢复下载
//                                Long r = Long.parseLong(record + "");
//                                /*mCurrentLocation*/StaticNum.mCurrentLocation += r - startL;
//                                Log.d(TAG, "线程_" + i + "_恢复下载");
//                                mListener.onChildResume(r);
//                                startL = r;
//                            }
//                            if (i == (/*THREAD_NUM*/StaticNum.THREAD_NUM - 1)) {
//                                //如果整个文件的大小不为线程个数的整数倍，则最后一个线程的结束位置极为文件的总长度
//                                endL = fileLength;
//                            }
//                            DownloadEntity entity = new DownloadEntity(context, fileLength, downloadUrl, dFile, i, startL, endL);
//                            DownloadTask task = new DownloadTask(entity);
//                            tasks.put(i, new Thread());
//                        }
//                        if (/*mCurrentLocation*/StaticNum.mCurrentLocation > 0) {
//                            mListener.onResume(/*mCurrentLocation*/StaticNum.mCurrentLocation);
//                        } else {
//                            mListener.onStart(/*mCurrentLocation*/StaticNum.mCurrentLocation);
//                        }
//                        for (int i = 0, count = tasks.size(); i < count; i++) {
//                            Thread task = tasks.get(i);
//                            if (task != null) {
//                                task.start();
//                            }
//                        }
//                    } else {
//                        Log.e(TAG, "下载失败，返回码：" + code);
//                        /*isDownloading*/StaticNum.isDownloading = false;
//                        System.gc();
//                        mListener.onFail();
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "下载失败");
//                    /*isDownloading*/StaticNum.isDownloading = false;
//                    mListener.onFail();
//                }
//            }
//        }).start();
//    }
}
