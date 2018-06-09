package com.bignerdranch.android.download;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 具体执行下载操作的类
 */

public class Downloader {

    String TAG = "Downloader";
    private DownloadEntity entity;
    private Context context;
    private String downloadUrl;

    //下载入口
    public void download(final Context context, @NonNull final String downloadUrl, /*@NonNull final String filePath,*/ @NonNull final DownloadListener downloadListener) throws Exception {
//        StaticNum.isDownloading = true;
//        StaticNum.mCurrentLocation = 0L;
//        StaticNum.isStop = false;
//        StaticNum.isCancel = false;
//        StaticNum.mCancelNum = 0;
//        StaticNum.mStopNum = 0;

//        String downloadUrl = "http://p5mi59sy0.bkt.clouddn.com/app-release.apk";
        try {
            this.context = context;
            this.downloadUrl = downloadUrl;
            if (StaticNum.isDownloading) {
                File file = null;
                RandomAccessFile savedFile = null;
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                file = new File(directory + fileName);
                if (file.exists()) {
                    StaticNum.mCurrentLocation = file.length();
                }
//        StaticNum.contentLength = getContentLength(downloadUrl);

                //获得需要下载的文件的长度
                URL url = new URL(this.downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, " +
                        "image/pjpeg, application/x-shockwave-flash, application/xaml+xml, " +
                        "application/vnd.ms-xpsdocument, application/x-ms-xbap," +
                        " application/x-ms-application, application/vnd.ms-excel," +
                        " application/vnd.ms-powerpoint, application/msword, */*");
                conn.setRequestProperty("Accept-Language", "zh-CN");
                conn.setRequestProperty("Referer", downloadUrl);
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; " +
                        "Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727;" +
                        " .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.connect();
                if(conn.getResponseCode() == 200) {
                    StaticNum.contentLength = conn.getContentLength();

                    if (StaticNum.contentLength == 0) {
                        downloadListener.onFail();
//                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (StaticNum.contentLength == StaticNum.mCurrentLocation) {
                        downloadListener.onComplete();
//                Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //生成一个大小相同的本地文件
                    savedFile = new RandomAccessFile(file, "rwd");
                    if(StaticNum.contentLength > 0)
                    savedFile.setLength(StaticNum.contentLength);
                    savedFile.close();
                    conn.disconnect();
                }

                //计算每个线程下载的量
                long threadlength = (StaticNum.contentLength - StaticNum.mCurrentLocation) % StaticNum.THREAD_NUM == 0 ? (StaticNum.contentLength - StaticNum.mCurrentLocation) / StaticNum.THREAD_NUM : (StaticNum.contentLength - StaticNum.mCurrentLocation) / StaticNum.THREAD_NUM + 1;
                for (int i = 0; i < StaticNum.THREAD_NUM; i++) {
                    //设置每条线程从哪个位置开始下载
                    long startposition = StaticNum.mCurrentLocation + i * threadlength;
                    //从文件的什么位置开始写入数据
//            RandomAccessFile threadfile = new RandomAccessFile(file, "rw");
//            threadfile.seek(startposition);
                    //启动多个线程分别从startposition位置开始下载文件
                    entity = new DownloadEntity(context,/*StaticNum.contentLength,*/downloadUrl, file, i, startposition, startposition + threadlength);
                    new DownloadTask(entity,downloadListener);
                }
                if(StaticNum.mCompleteThreadNum == StaticNum.THREAD_NUM)
                    downloadListener.onComplete();
//                int quit = System.in.read();
//                while ('q' != quit) {
//                    Thread.sleep(2000);
//                }
            } else if (StaticNum.isStop) {
                downloadListener.onStop();
            } else if (StaticNum.isCancel) {
                downloadListener.onCancel();
            }
        }catch (Exception e){
            throw new Exception("文件下载异常");
        }

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
//                                //如果整个文件的大小不为线程个数的整数倍，则最后一个线程的结束位置即为文件的总长度
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

}
