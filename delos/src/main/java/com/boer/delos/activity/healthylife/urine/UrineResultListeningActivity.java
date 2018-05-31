package com.boer.delos.activity.healthylife.urine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.SerializableMap;
import com.boer.delos.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Description：尿检 尿检结果列表 尿检
 */
public class UrineResultListeningActivity extends BaseUrineActivity {

    @Bind(R.id.id_textUrineTime)
    TextView mIdTextUrineTime;
    @Bind(R.id.id_textUrineTitle)
    TextView mIdTextUrineTitle;
    @Bind(R.id.id_listViewUrine)
    ListView mIdListViewUrine;
    //尿检仪获取数据对应的名称
    private ArrayList<String> dataList = new ArrayList<String>();
    //尿检仪获取数据对应的数值
    private Map<String, String> resultMap = new HashMap<String, String>();
    //尿检结果详细说明
    public static Map<String, ArrayList> JudgeDataArrayList = new HashMap<String, ArrayList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urine_result);
        ButterKnife.bind(this);

        initTopBar("尿检", null, true, true);
        ivRight.setImageResource(R.drawable.ic_health_live_more);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UrineResultListeningActivity.this, UrinalysisHistoryActivity.class));
            }
        });
        Intent bundle = getIntent();
        String result = bundle.getStringExtra("result");
        Map<String, String> dataMap = (Map<String, String>) bundle.getSerializableExtra("data");
        if (dataMap != null) {
            if (dataMap.size() != 0) {
                //历史记录
                resultMap.putAll(dataMap);
                mIdTextUrineTime.setText(TimeUtil.formatStamp2Time(Long.valueOf(dataMap.get("time")), null));
            }
        } else {
            //测试结果
            initData(result);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            mIdTextUrineTime.setText(dateFormat.format(new Date()));
        }
        initItemShow();

        //计算结果
        calculateData(resultMap);
        JudgeDataArrayList = handleUrineDataWithJudgeData();
        mIdTextUrineTitle.setText(urineNumber + "分");
        mIdListViewUrine.setAdapter(new UrineResultAdapter());
        mIdListViewUrine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UrineResultListeningActivity.this, UrineDetailListeningActivity.class);
                Bundle bundle = new Bundle();

                intent.putExtra("dataList", dataList);
                SerializableMap tmpmap = new SerializableMap();
                tmpmap.setStringMap(statesMap);
                bundle.putSerializable("statesMap", tmpmap);
                SerializableMap tmpmap2 = new SerializableMap();
                tmpmap2.setStringMap(judgeIndexMap);
                bundle.putSerializable("judgeIndexMap", tmpmap2);
                SerializableMap tmpmap3 = new SerializableMap();
                tmpmap3.setIntsMap(imagesMap);
                bundle.putSerializable("imagesMap", tmpmap3);
                bundle.putInt("position", position);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    //拿到尿检仪的原始数据
    private void initData(String result) {
        //根据文档，将尿检仪获得的数据放到对应的map中，格式年 月 日  LEU BLD PH PRO UBG NIT VC GLU BIL KET SG
        try {
            String[] strings = result.replace("  ", " ").replace(" ", ",").split(",");
            resultMap.put(LEU, strings[4]);
            resultMap.put(BLD, strings[5]);
            resultMap.put(PH, strings[6]);
            resultMap.put(PRO, strings[7]);
            resultMap.put(UBG, strings[8]);
            resultMap.put(NIT, strings[9]);
            resultMap.put(VC, strings[10]);
            resultMap.put(GLU, strings[11]);
            resultMap.put(BIL, strings[12]);
            resultMap.put(KET, strings[13]);
            resultMap.put(SG, strings[14]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * item
     */
    private void initItemShow() {

        //添加尿检结果选项
        dataList.add(LEU);
        dataList.add(NIT);
        dataList.add(UBG);
        dataList.add(PRO);
        dataList.add(PH);
        dataList.add(BLD);
        dataList.add(SG);
        dataList.add(KET);
        dataList.add(BIL);
        dataList.add(GLU);
        dataList.add(VC);
    }

    class UrineResultAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(UrineResultListeningActivity.this).inflate(R.layout.urineresult_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mIdTxtUrineName.setText(dataList.get(position));

            setupViewsWithData(dataList.get(position), resultMap.get(dataList.get(position)), viewHolder.mIdTxtUrineState, viewHolder.mIdLinearLayoutAddView);

            return convertView;
        }


    }

    class ViewHolder {
        @Bind(R.id.id_txtUrineName)
        TextView mIdTxtUrineName;
        @Bind(R.id.id_txtUrineState)
        TextView mIdTxtUrineState;
        @Bind(R.id.id_linearLayoutAddView)
        LinearLayout mIdLinearLayoutAddView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    //列表中根据值加入ImageView，创建基础的ImageView
    private ImageView getBaseImageView(int sourceID) {
        ImageView imageView = new ImageView(UrineResultListeningActivity.this);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setMargins(15, 0, 15, 0);
        imageView.setLayoutParams(layout);
        imageView.setBackgroundResource(sourceID);
        return imageView;
    }

    //根据数值，对界面进行赋值
    private void setupViewsWithData(String key, String date, TextView textViewState, LinearLayout linearLayoutAdd) {
        linearLayoutAdd.removeAllViews();
        textViewState.setText(Html.fromHtml(statesMap.get(key)));
        for (int i : imagesMap.get(key)) {
            linearLayoutAdd.addView(getBaseImageView(i));
        }

    }


}
