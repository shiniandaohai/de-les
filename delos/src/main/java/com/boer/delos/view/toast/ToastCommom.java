package com.boer.delos.view.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.utils.StringUtil;

/**
 * @author XieQingTing
 * @Description: 自定义Toast
 * create at 2016/4/13 17:21
 */
public class ToastCommom {
    private static ToastCommom toastCommom;
    private Toast toast;

    public static final int TOAST_INFO = 0;
    public static final int TOAST_ERROR = 1;
    public static final int TOAST_SUCCESS = 2;

    private ToastCommom() {
    }

    public static ToastCommom getInstance() {
        if (toastCommom == null) {
            toastCommom = new ToastCommom();
        }
        return toastCommom;
    }


    /**
     * 显示Toast short
     *
     * @param context
     * @param msg
     */

    public void toastShort(Context context, String msg, int type) {
//        if (toast != null) {
////            toastDismiss();
//        }
        if (toast == null) {
            toast = new Toast(context);
            settingToastShow(context, toast, msg, type);
        } else {
            settingToastShow(toast, msg, type);
        }
        if (toast.getDuration() != Toast.LENGTH_SHORT) {
            toast.setDuration(Toast.LENGTH_SHORT);
        }
    }

    public void toastDismiss() {
        if (toast != null) {
            toast.cancel();
        }
    }

    /**
     * 默认居中显示
     *
     * @param toast
     * @param context
     */
    private void settingToastShow(Context context, Toast toast, String tvString, int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_xml, null);
        TextView tvToast = (TextView) view.findViewById(R.id.tvToast);
        ImageView imageView = (ImageView) view.findViewById(R.id.ivToast);
        settingImagviewShow(tvToast, imageView, type);
        if (!StringUtil.isEmpty(tvString)) {
            tvToast.setText(tvString);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.show();
    }

    /**
     * 默认居中显示
     *
     * @param toast
     * @param tvString
     * @param type
     */
    private void settingToastShow(Toast toast, String tvString, int type) {
        View view = toast.getView();
        TextView tvToast = (TextView) view.findViewById(R.id.tvToast);
        ImageView imageView = (ImageView) view.findViewById(R.id.ivToast);
        settingImagviewShow(tvToast, imageView, type);
        if (!StringUtil.isEmpty(tvString)) {
            tvToast.setText(tvString);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void settingImagviewShow(TextView tvToast, ImageView imageView, int type) {
        switch (type) {
            case TOAST_INFO:
                break;
            case TOAST_SUCCESS:
                imageView.setImageResource(R.mipmap.ic_launcher);
                tvToast.setText("操作成功");
                break;
            case TOAST_ERROR:
                imageView.setImageResource(R.mipmap.ic_launcher);
                tvToast.setText("操作失败");
                break;

        }
    }


    //方案二
    private Toast mToast;
    private TextView mTvToast;

    public void showToast(Context ctx, String content) {
        if (mToast == null) {
            mToast = new Toast(ctx);
            mToast.setGravity(Gravity.CENTER, 0, 0);//设置toast显示的位置，这是居中
            mToast.setDuration(Toast.LENGTH_SHORT);//设置toast显示的时长
            View _root = LayoutInflater.from(ctx).inflate(R.layout.toast_xml, null);//自定义样式，自定义布局文件
            mTvToast = (TextView) _root.findViewById(R.id.tvToast);
            mToast.setView(_root);//设置自定义的view
        }
        mTvToast.setText(content);//设置文本
        mToast.show();//展示toast
    }

    public void showToast(Context ctx, int stringId) {
        showToast(ctx, ctx.getString(stringId));
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
            mTvToast = null;
        }
    }
}
