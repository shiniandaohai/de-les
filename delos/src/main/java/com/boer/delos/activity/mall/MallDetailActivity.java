package com.boer.delos.activity.mall;

import android.os.Bundle;
import android.widget.ImageView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class MallDetailActivity extends CommonBaseActivity {

    @Bind(R.id.img_detail)
    ImageView imgDetail;

    @Override
    protected int initLayout() {
        return R.layout.activity_mall_detail;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        int tag = bundle.getInt("tag",0);
        int titleId =0;
        switch (tag){
            case 0:
                titleId = R.string.mall_homeairclean;
                imgDetail.setImageResource(R.mipmap.mall_allhomeairclean);
                break;
            case 1:
                titleId = R.string.mall_airclean;
                imgDetail.setImageResource(R.mipmap.mall_allairclean);
                break;
            case 2:
                titleId = R.string.mall_radium;
                imgDetail.setImageResource(R.mipmap.mall_allradium);
                break;
            case 3:
                titleId = R.string.mall_aromatherapy;
                imgDetail.setImageResource(R.mipmap.mall_allaromatherapy);
                break;
            case 4:
                titleId = R.string.mall_homewaterclean;
                imgDetail.setImageResource(R.mipmap.mall_allaromatherapy);
                break;
            case 5:
                titleId = R.string.mall_tableclean;
                imgDetail.setImageResource(R.mipmap.mall_alltableclean);
                break;
            case 6:
                titleId = R.string.mall_floorclean;
                imgDetail.setImageResource(R.mipmap.mall_allfloorclean);
                break;
            case 7:
                titleId = R.string.mall_phythmlight;
                imgDetail.setImageResource(R.mipmap.mall_all_phythmlight);
                break;
            case 8:
                titleId = R.string.mall_energylight;
                imgDetail.setImageResource(R.mipmap.mall_all_energylight);
                break;
            case 9:
                titleId = R.string.mall_smartmirror;
                imgDetail.setImageResource(R.mipmap.mall_allmirror);
                break;
            case 10:
                titleId = R.string.mall_mattress;
                imgDetail.setImageResource(R.mipmap.mall_all_mattress);
                break;
            case 11:
                titleId = R.string.mall_indoorcurtain;
                imgDetail.setImageResource(R.mipmap.mall_all_incurtain);
                break;
            case 12:
                titleId = R.string.mall_nightlight;
                imgDetail.setImageResource(R.mipmap.mall_nightlight);
                break;
            case 13:
                titleId = R.string.mall_outdoorcurtain;
                imgDetail.setImageResource(R.mipmap.mall_all_incurtain);
                break;
        }
        tlTitleLayout.setTitle(titleId);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }
}
