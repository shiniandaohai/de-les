package com.boer.delos.activity.personal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.ImageUtils;
import com.boer.delos.view.ClipView;

import java.io.File;


public class ImageCutOutListeningActivity extends BaseListeningActivity implements OnTouchListener {

    public static final String IMAGE_BECUTOUT = "image_becutout";

    ImageView iv;
    ClipView clipView;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    int cutHeight = 0;
    int statusbarHeight = 0;
    int titleHeight = 0;
    String path;
    File tempFile;// 若要裁剪的图片过大，压缩处理后 临时存储的文件，上传完毕或退出该界面 删除

    Bitmap rawBitmap, screenBitmap, finalBitmap;// 原始初始显示，全屏截图，最终截取的

    View cashview;// Activity的截屏view


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagecutout);
        initTopBar(R.string.image_cut, R.string.save, true, false);
        findViews();
        setListener();
        initData();
    }

    public void findViews() {
        iv = (ImageView) findViewById(R.id.imagecutout_iv);
        clipView = (ClipView) findViewById(R.id.imagecutout_clipview);

    }


    public void setListener() {
        iv.setOnTouchListener(this);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightMarkClick();
            }
        });
    }


    public void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int[] arr = DensityUitl.getDisplayWidthHeight();
        int screenHeight = arr[1];
        statusbarHeight = DensityUitl.getWindowsStatusBarHeight(this);
        titleHeight = (int) (50 * dm.density);
        cutHeight = (screenHeight - statusbarHeight - titleHeight) / 2;
        getIntentData();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void getIntentData() {
        path = getIntent().getStringExtra(Constant.KEY_PATH);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "文件不可用", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            final File file = new File(path);
            if (file != null && file.exists() && file.length() > 0) {
                if (file.length() / 1024 > 300) {
                    String fileName = ImageUtils.createCacheImageFile();
                    File files = ImageUtils.checkBitmapDegree(
                            getApplicationContext(), new File(path));
                    if (files != null) {
                        path = files.getAbsolutePath();
                    }
                    boolean isFlag = ImageUtils.createLargeThumbImage(path, "",
                            metrics.density, fileName);
                    if (isFlag) {
                        path = fileName;
                    }
                    rawBitmap = ImageUtils.getSpecifiedBitmap(path, cutHeight,
                            cutHeight);
                    if (rawBitmap != null) {
                        iv.setImageBitmap(rawBitmap);
                    }
                } else {
                    rawBitmap = ImageUtils.getSpecifiedBitmap(path, cutHeight,
                            cutHeight);
                    if (rawBitmap != null) {
                        iv.setImageBitmap(rawBitmap);
                    } else {
                        Toast.makeText(this, "加载失败，请重新选择图片", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "文件不可用", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rawBitmap != null) {
            rawBitmap.recycle();
        }
        if (screenBitmap != null) {
            screenBitmap.recycle();
        }
        if (finalBitmap != null) {
            finalBitmap.recycle();
        }
        System.gc();
    }

    private void rightMarkClick() {
        finalBitmap = getBitmap();

        String newPath = Constant.IMAGE_PATH + System.currentTimeMillis() + ".cache";
        if (finalBitmap != null) {
            ImageUtils.saveBitmap2File(getApplicationContext(), newPath,
                    finalBitmap);
            setResult(RESULT_OK,
                    new Intent().putExtra(Constant.KEY_PATH, newPath));
            finish();
        } else {
            Toast.makeText(this, "裁剪失败", Toast.LENGTH_SHORT).show();
        }
    }

    /* 获取矩形区域内的截图 */
    private Bitmap getBitmap() {
        screenBitmap = takeScreenShot();

        int width = clipView.getWidth();
        int height = clipView.getHeight();
        screenBitmap = Bitmap.createBitmap(screenBitmap,
                (width - height / 2) / 2, height / 4 + statusbarHeight
                        + titleHeight, height / 2, height / 2);
        return screenBitmap;
    }

    // 获取Activity的截屏
    private Bitmap takeScreenShot() {
        cashview = this.getWindow().getDecorView();
        cashview.setDrawingCacheEnabled(true);
        cashview.buildDrawingCache();
        return cashview.getDrawingCache();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 設置初始點位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true; // indicate event was handled
    }

    /**
     * Determine the space between the first two fingers
     */
    @SuppressLint("FloatMath")
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


}
