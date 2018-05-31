package com.boer.delos.activity.main.adv;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Advertisement;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 推送消息
 */
public class SlidePagerListeningActivity extends BaseListeningActivity {
    private List<String> pics = new ArrayList<>();
    ViewPager pager;
    SlidePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_pager);
        initTopBar("推送消息", null, true, false);
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());
        test();

    }

    private void test() {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", Constant.TOKEN + "");
        map.put("recent", 10 + "");
        L.e("SlidePagerActivity_Constant.CURRENTUID===" + Constant.USERID);
        OKHttpRequest.postWithNoKey(this, URLConfig.HTTP + "/notification/query_recent_notification?uid=" + Constant.USERID,
                "hahahah", new Gson().toJson(map), new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        if (JsonUtil.parseStateCode(Json)) {
                            JSONObject jb = null;
                            try {
                                jb = new JSONObject(Json);
                                JSONObject jb2 = jb.getJSONObject("response");
                                List<Advertisement> list = new ArrayList<>();
                                list.addAll(JsonUtil.parseDataList(jb2.toString(), Advertisement.class, "data"));
                                for (Advertisement ad : list
                                        ) {
                                    pics.add(ad.getDetail());
                                }
                                L.e("" + pics.size());
                                pagerAdapter.addAll(pics);
                                pager.setAdapter(pagerAdapter);
                                pager.setCurrentItem(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailed(String json) {

                    }
                });
    }


}
