package com.boer.delos.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ClipView extends View {
	public ClipView(Context context) {
		super(context);
	}

	public ClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/* 这里就是绘制矩形区域 */
		int width = this.getWidth();
		int height = this.getHeight();

		Paint paint = new Paint();
		// paint.setColor(0xaa000000);
		paint.setColor(0x66000000);

		// top
		canvas.drawRect(0, 0, width, height / 4, paint);
		// left
		canvas.drawRect(0, height / 4, (width - height / 2) / 2,
				height * 3 / 4, paint);
		// right
		canvas.drawRect((width + height / 2) / 2, height / 4, width,
				height * 3 / 4, paint);
		// bottom
		canvas.drawRect(0, height * 3 / 4, width, height, paint);

		paint.setColor(0xaaffffff);
		// top
		canvas.drawLine((width - height / 2) / 2, height / 4,
				(width + height / 2) / 2, height / 4, paint);
		// bottom
		canvas.drawLine((width - height / 2) / 2, height * 3 / 4,
				(width + height / 2) / 2, height * 3 / 4, paint);
		// left
		canvas.drawLine((width - height / 2) / 2, height / 4,
				(width - height / 2) / 2, height * 3 / 4, paint);
		// right
		canvas.drawLine((width + height / 2) / 2, height / 4,
				(width + height / 2) / 2, height * 3 / 4, paint);
	}

}
