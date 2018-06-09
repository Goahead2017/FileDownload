package com.bignerdranch.android.download;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    //    public static final String TAG = "MainActivity";
//    private DownloadEntity dEntity;
//    private DownloadListener mListener;
//    private Context context = getApplicationContext();

    private ProgressBar progressbar;
    private TextView textresult;

    Button button1;
    Button button2;
    Button button3;
    Downloader downloader;
    String downloadUrl = "http://p5mi59sy0.bkt.clouddn.com/app-release.apk";
    Context context;
//    DownloadListener listener = new DownloadListener() {
//        @Override
//        public void onCancel() {
//
//        }
//
//        @Override
//        public void onFail() {
//            Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onProgress(final long currentLocation) {
//            progressbar.setMax(StaticNum.contentLength);//设置进度条的最大刻度
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    progressbar.setProgress((int) currentLocation);         //设置进度条的进度
//                    //计算已经下载的百分比,此处需要转换为浮点数计算
//                    float num = (float)progressbar.getProgress() / (float)progressbar.getMax();
//                    int result = (int)(num * 100);     //把获取的浮点数计算结果转换为整数
//                    textresult.setText(result+ "%");   //把下载的百分比显示到界面控件上
//                }
//            });
//        }
//
//        @Override
//        public void onStart() {
//
//        }
//
//        @Override
//        public void onStop() {
//
//        }
//
//        @Override
//        public void onComplete() {
//            Toast.makeText(context,"下载完成",Toast.LENGTH_SHORT).show();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressbar = findViewById(R.id.progressBar);
        textresult = findViewById(R.id.textresult);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        downloader = new Downloader();
        context = getApplicationContext();

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:
                StaticNum.isDownloading = true;
                Toast.makeText(context,"开始下载",Toast.LENGTH_SHORT).show();
                try {
                    downloader.download(context, downloadUrl, new DownloadListener() {
                        @Override
                        public void onCancel() {
                            Toast.makeText(MainActivity.this,"下载取消",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail() {
                            Toast.makeText(MainActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgress(final long currentLocation) {
                            progressbar.setMax(StaticNum.contentLength);//设置进度条的最大刻度
                            runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                 progressbar.setProgress((int) currentLocation);         //设置进度条的进度
                                 //计算已经下载的百分比,此处需要转换为浮点数计算
                                 float num = (float)progressbar.getProgress() / (float)progressbar.getMax();
                                 int result = (int)(num * 100);     //把获取的浮点数计算结果转换为整数
                                  textresult.setText(result+ "%");   //把下载的百分比显示到界面控件上
                             }
                         });
                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onStop() {
                            Toast.makeText(MainActivity.this,"下载暂停",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                           Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button2:
                StaticNum.isStop = true;
                Toast.makeText(context,"下载暂停",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button3:
                StaticNum.isStop = true;
                Toast.makeText(context,"下载取消",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
                default:
        }
    }

    //    //初始化下载信息
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void initDownload(){
//        String downloadUrl = "http://p5mi59sy0.bkt.clouddn.com/app-release.apk";
//        File file = null;
//        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
//        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
//        file = new File(directory + fileName);
//        if(file.exists()){
//            StaticNum.mCurrentLocation = file.length();
//        }
//        StaticNum.contentLength = getContentLength(downloadUrl);
//        if(StaticNum.contentLength == 0){
//            Toast.makeText(getApplicationContext(),"下载失败",Toast.LENGTH_SHORT).show();
//        } else if(Objects.equals(StaticNum.contentLength, StaticNum.mCurrentLocation)){
//            Toast.makeText(getApplicationContext(),"下载成功",Toast.LENGTH_SHORT).show();
//        }
//    }

//    private Long getContentLength(String downloadUrl) {
//    }
//
//    //下载入口
//    public void startDownload(final Context context, @NonNull final String downloadUrl, @NonNull final String filePath, @NonNull final DownloadListener downloadListener){
//        StaticNum.isDownloading = true;
//        StaticNum.mCurrentLocation = 0L;
//        StaticNum.isStop = false;
//        StaticNum.isCancel = false;
//        StaticNum.mCancelNum = 0;
//        StaticNum.mStopNum = 0;
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
