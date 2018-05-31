package com.boer.delos.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.boer.delos.activity.main.adv.SlidePagerListeningActivity;
import com.boer.delos.view.PortraitLoad;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by ACER~ on 2016/5/31.
 */
public class NetworkImageHolderView implements Holder<String> {
    private SimpleDraweeView imageView;

    @Override
    public View createView(final Context context) {

        imageView = new SimpleDraweeView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, SlidePagerListeningActivity.class));
            }
        });
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        PortraitLoad.frescoImage(data, imageView);

    }
}
