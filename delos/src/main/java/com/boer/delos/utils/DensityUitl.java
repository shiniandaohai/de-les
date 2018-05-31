package com.boer.delos.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.boer.delos.commen.BaseApplication;

import java.lang.reflect.Field;


public class DensityUitl {
	/**
	 * 获取手机像素分辨率
	 * 
	 * @return arr[0] x, arr[1] y
	 */
	public static int[] getDisplayWidthHeight() {
		int[] arr = new int[2];
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) BaseApplication
				.getInstance().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);

		arr[0] = metrics.widthPixels;
		arr[1] = metrics.heightPixels;
		return arr;
	}

	/** dip转换成像素 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/** 像素转换成dip */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取屏幕状态栏高度
	 *
	 * @param activity
	 * @return
	 */
	public static int getWindowsStatusBarHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;// 状态栏的高度
		if (statusBarHeight != 0) {

			return statusBarHeight;
		}

		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			int y = activity.getResources().getDimensionPixelSize(x);
			return y;// 最后的一道防线
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
