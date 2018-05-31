package com.boer.delos.view;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ScreenUtils;
import com.nineoldandroids.view.ViewHelper;

/**
 * @author wangkai
 * @Description: 自定义侧滑，继承FrameLayout，效果为底层的View永远不动，上层View滑动并且缩放
 * create at 2016/3/24 15:44
 */
public class MySlidingMenu extends FrameLayout {
    private int openPosition, minOpenWidth, width, height;
    private View pllMain;
    private GestureDetectorCompat gestureDetector;
    public boolean isOpen = false;
    private float scrollX;
    private boolean isScroll = false;
    public boolean isChildScroll = false;

    public MySlidingMenu(Context context) {
        super(context);
    }

    public MySlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取屏幕宽度
        width = ScreenUtils.getScreenWidth(context);
        height = ScreenUtils.getScreenHeight(context);
        if (width < height) {
            //定义侧滑菜单打开的位置
            openPosition = (int) (width * 0.3);
            //定义当手势滑到一个临界值的时候，控制侧滑的开关
            minOpenWidth = (int) (width * 0.15);
        } else {
            //适配 因为菜单缩放了80% 所以减去因为缩放带来的误差
            //定义侧滑菜单打开的位置
            openPosition = (int) (DensityUitl.dip2px(context, 150) - width * 0.1f);
            //定义当手势滑到一个临界值的时候，控制侧滑的开关
            minOpenWidth = (int) (width * 0.03);
        }

        //注册手势监听
        gestureDetector = new GestureDetectorCompat(context, new MyOnGestureListener());
    }

    public MySlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChildScroll(boolean childScroll) {
        isChildScroll = childScroll;
    }

    /**
     * 手势监听
     */
    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Loger.d("MyGesture onSingleTapUp");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //判断是否已经打开，如果打开，平移距离为与初始值的差
            //判断是否为X轴方向运动
            //X Y轴距离存在负数，代表方向，所以取绝对值判断
            //getRawX()获取滑动的点在屏幕中所在的水平坐标
            isScroll = true;
            float rawX1 = e1.getRawX();
            float rawX2 = e2.getRawX();
            if (rawX1 <= width / 5 || isOpen) {
                if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                    //坐标相减，解决手势在任意位置开始滑动，View不会直接定位到down的坐标，取差值是为了让侧滑每次都是移动相对距离
                    if (isOpen) {
                        if (rawX1 >= rawX2) {
                            //向左滑动
                            float max = openPosition - (rawX1 - rawX2);
                            animateView(max >= 0 ? max : 0);

                        } else {
                            //向右滑动
                            animateView(openPosition + (rawX2 - rawX1));
                        }
                    } else {
                        if (rawX1 <= rawX2) {
                            animateView(rawX2 - rawX1);
                        }
                    }
                }
            }
            Loger.d("MyGesture onScroll");
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /*
   * 在onTouch()方法中，我们调用GestureDetector的onTouchEvent()方法，将捕捉到的MotionEvent交给GestureDetector
   * 来分析是否有合适的callback函数来处理用户的手势
   */
        public boolean onTouch(View v, MotionEvent event) {
            Loger.d("MyGesture onTouch");
            return gestureDetector.onTouchEvent(event);
        }

        // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
        public boolean onDown(MotionEvent arg0) {
            Loger.d("MyGesture onDown");
            return false;
        }

        /*
         * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
         * 注意和onDown()的区别，强调的是没有松开或者拖动的状态
         */
        public void onShowPress(MotionEvent e) {
            Loger.d("MyGesture onShowPress");
        }

        // onSingleTapUp用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
        // 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Loger.d("MyGesture onFling");
            return true;
        }
        // onScroll用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发

        // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
        public void onLongPress(MotionEvent e) {
            Loger.d("MyGesture onLongPress");
        }

    }

    /**
     * @param ev
     * @return
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                //如果是子View在滑动并且侧滑不是打开状态，则不消耗此次的触碰事件
                if (isChildScroll && !isOpen) {
                    setChildScroll(false);
                    return super.dispatchTouchEvent(ev);
                }
                //如果水平方向滑动的距离大于最小的打开距离，则到达这个值就打开侧滑，反之则关闭侧滑
                //如果整体布局处于滑动状态，则消耗触碰的事件，不向子View传递，如果不是滑动状态，则不消耗点击事件，让子View触发点击事件
                if (isScroll) {
                    if (scrollX >= minOpenWidth) openMenu();
                    else closeMenu();
                    isScroll = false;
                    return true;
                } else {
                    openOrCloseMenu();
                }
                break;
            case MotionEvent.ACTION_DOWN:

                //初始化滑动的状态
                isScroll = false;
                break;
        }

        return gestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    public void openOrCloseMenu() {
        if (isOpen) {
            openMenu();
        } else {
            closeMenu();
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        ViewHelper.setTranslationX(pllMain, 0);
        ViewHelper.setScaleX(pllMain, 1f);
        ViewHelper.setScaleY(pllMain, 1f);
        scrollX = 0;
        isOpen = false;
    }

    /**
     * 打开菜单
     */
    public void openMenu() {
        ViewHelper.setTranslationX(pllMain, openPosition);
        ViewHelper.setScaleX(pllMain, 0.8f);
        ViewHelper.setScaleY(pllMain, 0.8f);
        isOpen = true;
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 布局加载完成后，获取第二个子View,并且设置可以点击，不然无法触发手势
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        pllMain = getChildAt(1);
        pllMain.setClickable(true);
    }

    /**
     * View的缩放动画
     *
     * @param scrollX 水平滑动距离
     */
    private void animateView(float scrollX) {
        this.scrollX = scrollX;
        float percent = scrollX / width;
        //固定View的缩放比为0.8f
        float scale = (1 - percent) <= 0.8f ? 0.8f : 1 - percent;
        //不放大View
        scale = scale >= 1 ? 1 : scale;
        ViewHelper.setScaleX(pllMain, scale);
        ViewHelper.setScaleY(pllMain, scale);
        ViewHelper.setTranslationX(pllMain, scrollX * 8 / 10);
    }


    interface OnMyslideMenuScroll {
        void myslideMenuScroll(float scrollX, float scrollY);
    }

    private OnMyslideMenuScroll listener;

    public void setListener(OnMyslideMenuScroll listener) {
        this.listener = listener;
    }
}
