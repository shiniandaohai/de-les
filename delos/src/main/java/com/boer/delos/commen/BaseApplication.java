package com.boer.delos.commen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.boer.delos.R;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.ExternalStorageUtils;
import com.boer.delos.utils.Loger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.OkHttpClient;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author wangkai
 * @Description: BaseApplication
 * create at 2016/1/17 23:32
 */
public class BaseApplication extends MultiDexApplication implements Thread.UncaughtExceptionHandler {

    public static BaseApplication instance = null;
    private static LayoutInflater mInflater;
    private static SharedPreferences mSharePreference;

    private static Context mContext;
    public static String SHARENAME = "com.boer.jiaweishi";
    public String SD_SAVEDIR = ExternalStorageUtils.CACHE_DIR;
    public static String ImagePath = "";
    public static OkHttpClient mOKHttpClient;
    public static Handler mDelivery;
    public static Dialog dialog;

    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(); //浮动按钮用
    private static int activityCount;
    public static boolean isForeground;
    public static boolean LOGER = true;

    private RefWatcher mRefWatcher;

    public static BaseApplication getInstance() {
        if (instance == null) {
            synchronized (BaseApplication.class) {
                if (instance == null) {
                    instance = new BaseApplication();
                }
            }
        }
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Config.DEBUG = false;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);

        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());

        Fresco.initialize(this);
        initImageLoader(this);
        mOKHttpClient = new OkHttpClient();
        setTimeOut(20000);

        mDelivery = new Handler(Looper.getMainLooper());

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mSharePreference = getSharedPreferences(SHARENAME, 0);

        mContext = this;

        dialog = customDialog(this, getString(R.string.progress_loading));


        File file = new File(SD_SAVEDIR);
        if (!file.exists())
            file.mkdir();
        Loger.v("file==" + file.getAbsolutePath());

        //捕获异常
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(this);
        LOGER = true;

//        squirrelCallImpl.getInstance().initSquirrel();
        mRefWatcher = LeakCanary.install(this);
        judgeIsForeground();
    }

    public static RefWatcher getRefWatcher(Context context) {
        BaseApplication application = (BaseApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }

    /**
     * 设定超时时间
     *
     * @param milliSeconds
     */
    public static void setTimeOut(int milliSeconds) {
        getOKHttpClient().setConnectTimeout(milliSeconds, TimeUnit.MILLISECONDS);
        getOKHttpClient().setWriteTimeout(milliSeconds, TimeUnit.MILLISECONDS);
        getOKHttpClient().setReadTimeout(milliSeconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取网络请求队列
     *
     * @return mRequestQueue
     */

    public static OkHttpClient getOKHttpClient() {
        return mOKHttpClient;
    }

    public static Handler getDelivery() {
        return mDelivery;
    }

    /**
     * 返回layoutinflater
     *
     * @return
     */
    public static LayoutInflater getLayoutInflater() {
        if (mInflater == null) {
            synchronized (mInflater) {
                if (mInflater == null) {
                    mInflater = (LayoutInflater) BaseApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                }
            }
        }
        return mInflater;
    }

    public Context getContext() {
        return mContext;
    }

    public static SharedPreferences getSharedPreferences() {
        return mSharePreference;
    }


    public static void showToast(String content) {
        Toast.makeText(getInstance(), content, Toast.LENGTH_LONG).show();
    }

    public static void showToastShort(String content) {
        Toast.makeText(getInstance(), content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 自定义Dialog
     */
    public static Dialog customDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_progress_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.progress_anim);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(true);// 可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    /**
     * 返回Dialog
     *
     * @return
     */
    public static Dialog getDialog() {
        if (dialog == null) {
            return null;
        }
        return dialog;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//        Intent i = new Intent(this, CameraListActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pi = PendingIntent.getActivity(
//                this.getApplicationContext(), 0, i,
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pi);
//        System.exit(0);
    }

    private void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this,
                SD_SAVEDIR + "/Cache/imageloader");
        initImageOption();
        int screen_width = DensityUitl.getDisplayWidthHeight()[0];
        int screen_height = DensityUitl.getDisplayWidthHeight()[1];
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(screen_width, screen_height)

                .threadPoolSize(5)
                // default
                .threadPriority(Thread.NORM_PRIORITY - 2)
                // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(50 * 1024 * 1024))
                // .memoryCacheSizePercentage(13) // default
                .discCache(new UnlimitedDiscCache(cacheDir))// default
                .discCacheSize(50 * 1024 * 1024) // 缓冲大小
                // .discCacheFileCount(300) // 缓冲文件数目
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                // .imageDownloader(new BaseImageDownloader(context)) // default
                // .imageDecoder(new BaseImageDecoder()) // default
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .imageDownloader(
                        new BaseImageDownloader(context, 30 * 1000, 30 * 1000)) // connectTimeout
                // (5
                // s),
                // readTimeout
                // (30
                // s)超时时间
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

    }

    public DisplayImageOptions displayImageOptions;

    private void initImageOption() {
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_avatar) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_avatar)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_avatar)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
//                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成
    }

    public WindowManager.LayoutParams getMywmParams() {

        return wmParams;
    }

    public WindowManager getWindowManager() {

        WindowManager mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        return mWindowManager;
    }

    public static Context getAppContext() {
        return mContext;
    }

    private void judgeIsForeground() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                isForeground = true;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (activityCount == 0) {
                    isForeground = false;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    //各个平台的配置
    {
        PlatformConfig.setWeixin("wxa0f8e6cddfc7ee76", "452b6f25450e5595ea9dff64eb20b6fd");
        PlatformConfig.setSinaWeibo("3426980749", "e16481bb0f42f125dc95272dbe938f66","https://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1106183638", "bun5ul2wNCsmDEnp");
    }

}