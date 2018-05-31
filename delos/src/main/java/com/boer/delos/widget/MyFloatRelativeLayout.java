package com.boer.delos.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.boer.delos.commen.BaseApplication;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class MyFloatRelativeLayout extends RelativeLayout {

    private Context mContext;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;

    public MyFloatRelativeLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public MyFloatRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public MyFloatRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private WindowManager wm=(WindowManager)getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

    //此wmParams为获取的全局变量，用以保存悬浮窗口的属性
    private WindowManager.LayoutParams wmParams = ((BaseApplication)getContext().getApplicationContext()).getMywmParams();

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //getRawX()获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY()-25;   //25是系统状态栏的高度
        Log.i("currP", "currX"+x+"====currY"+y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //getX()获取相对View的坐标，即以此View左上角为原点
                mTouchStartX =  event.getX();
                mTouchStartY =  event.getY();

                Log.i("startP", "startX"+mTouchStartX+"====startY"+mTouchStartY);

                break;
            case MotionEvent.ACTION_MOVE:
                updateViewPosition();
                break;

            case MotionEvent.ACTION_UP:
//                updateViewPosition();
                float lastX = event.getX();
                float lastY = event.getY();
                motionActionUpEvent(mTouchStartX, mTouchStartY, lastX, lastY);
                mTouchStartX=mTouchStartY=0;

                break;
        }
        return true;
    }

    private void updateViewPosition(){
        //更新浮动窗口位置参数,x是鼠标在屏幕的位置，mTouchStartX是鼠标在图片的位置
        wmParams.x=(int)( x-mTouchStartX);
        System.out.println(mTouchStartX);
        wmParams.y=(int) (y-mTouchStartY);
        wm.updateViewLayout(this, wmParams);
    }

    private void motionActionUpEvent(float x, float y, float lastX, float lastY) {

        int dx = (int) (x - lastX);
        int dy = (int) (y - lastY);
        if (dx == 0 && dy == 0) {
            if (onFloatActionClickLisener == null) {
                return;
            }
            //点击事件
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    String flag = "MyFloatButton";
                    onFloatActionClickLisener.floatAction(flag);
                }
            });
        }
    }

    public interface OnFloatActionClickLisener {
        /**
         * @param flag view 标示
         */
        void floatAction(String flag);
    }

    public void setOnFloatActionClickLisener(OnFloatActionClickLisener onFloatActionClickLisener) {
        this.onFloatActionClickLisener = onFloatActionClickLisener;
    }

    private OnFloatActionClickLisener onFloatActionClickLisener;



}
