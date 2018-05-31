package com.boer.delos.activity.smartdoorbell;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.widget.ImageView;

import com.boer.delos.R;
import com.boer.delos.activity.smartdoorbell.imageloader.ImageLoaderThreadPool;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.request.smartdoorbell.ICVSSUserModule;
import com.boer.delos.utils.ToastHelper;
import com.eques.icvss.api.ICVSSUserInstance;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;

import butterknife.Bind;

import static com.boer.delos.activity.smartdoorbell.imageloader.FileHelper.getRootFilePath;

/**
 * Created by Administrator on 2018/1/15.
 */

public class SmartDoorbellShowVisitorActivity extends CommonBaseActivity{
    @Bind(R.id.ivPic)
    ImageView ivPic;
    private ICVSSUserInstance icvss;
    private ImageLoaderThreadPool imageLoaderThreadPool;
    private String filePath;
    @Override
    protected int initLayout() {
        return R.layout.activity_door_bell_show_visitor;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("访客记录");
        tlTitleLayout.setLinearRightImage(R.drawable.ic_download);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void rightViewClick() {
        if(filePath!=null)
        MediaScannerConnection.scanFile(this, new String[]{filePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastHelper.showShortMsg("保存到相册");
                    }
                });
            }
        });
    }

    @Override
    protected void initAction() {
        icvss = ICVSSUserModule.getInstance(null).getIcvss();
        Intent intent=getIntent();
        URL url=icvss.equesGetRingPictureUrl(intent.getStringExtra("fid"),intent.getStringExtra("bid"));
        String imagePathTemp = getAlarmPath() + intent.getStringExtra("fid")+".jpg";
        filePath=imagePathTemp;
        imageLoaderThreadPool = ImageLoaderThreadPool.getInstance(1, ImageLoaderThreadPool.Type.LIFO);
        ivPic.setTag(imagePathTemp);
        if(StringUtils.isNotBlank(url.toString()) && StringUtils.isNotBlank(imagePathTemp)){
            imageLoaderThreadPool.loadImage(url.toString(), ivPic, imagePathTemp, imagePathTemp);
        }else{
            ivPic.setImageResource(R.drawable.bg_door_bell_camera);
        }
    }

    public String getAlarmPath() {
        String camPicPath = getRootFilePath() + getPackageName() + File.separator + "visitor_image" + File.separator;
        return camPicPath;
    }
}
