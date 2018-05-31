package com.boer.delos.view;

/**
 * Created by ACER~ on 2016/5/31.
 */
import android.net.Uri;

import com.boer.delos.constant.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

public class PortraitLoad {
    public static void frescoImage(String picPath, SimpleDraweeView iv) {
        String url= picPath.contains("http://") || picPath.contains("https://") ? picPath : URLConfig.PIC_URL+picPath;
//        if (picPath.contains("res:") || picPath.contains("file:")) {
//            url = picPath;
//        }
        Uri uri = Uri.parse(url);
        iv.setImageURI(uri);
    }
}
