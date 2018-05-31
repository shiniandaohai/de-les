package com.boer.delos.activity.healthylife.urine;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/2 0002.
 * 显示尿检信息的dialog
 */
public class UrineDialog {

    ListView mIdUrineListView;
    private Map mMap = new HashMap();
    private Context mContext;
    private Dialog dialog = null;
    private String key = "";//获取Map对应数据的key
    private String judgeIndex = "0";

    public UrineDialog(Context context, Map map, String mkey, String mJudgeIndex) {
        mContext = context;
        mMap = map;
        key = mkey;
        judgeIndex = mJudgeIndex;
        showDialog();
    }

    public void showDialog() {

        View view = View.inflate(mContext, R.layout.urinedialog_list, null);
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.alert_dialog);
        }
        dialog.setContentView(view);
        mIdUrineListView = (ListView) view.findViewById(R.id.id_urineListView);

        ArrayList<Map> resultList = UrineResultListeningActivity.JudgeDataArrayList.get(key);


        mIdUrineListView.setAdapter(new UrineDialogAdapter(resultList));
        dialog.setCancelable(true);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6

        // lp.height = (int) (d.widthPixels * 0.5); // 高度
        dialogWindow.setAttributes(lp);
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class UrineDialogAdapter extends BaseAdapter {
        ArrayList<Map> mDataList;

        public UrineDialogAdapter(ArrayList<Map> dataList) {
            mDataList = dataList;
        }

        @Override
        public int getCount() {
            return mDataList.size();
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.urinedialog_list_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //如果是两列数据，中间的状态隐藏，第一列数据是蓝色，第二列数据是黑色
            if (mDataList.get(position).get("state").toString().equals("无")) {
                viewHolder.mIdTextViewState.setVisibility(View.GONE);
                viewHolder.mIdTextViewResult.setTextColor(Color.parseColor("#00a0e9"));
                viewHolder.mIdTextViewJudge.setTextColor(Color.parseColor("#3a3a3a"));
            }
            if (mDataList.get(position).get("result").toString().equals("结果")) {//第一行是黑色
                viewHolder.mIdTextViewState.setTextColor(Color.parseColor("#a2a2a2"));
                viewHolder.mIdTextViewResult.setTextColor(Color.parseColor("#a2a2a2"));
                viewHolder.mIdTextViewJudge.setTextColor(Color.parseColor("#a2a2a2"));
            } else {//其他三列，第一列显示蓝色，第二列显示绿色，第三列显示黑色
                viewHolder.mIdTextViewResult.setTextColor(Color.parseColor("#00a0e9"));
                viewHolder.mIdTextViewState.setTextColor(Color.parseColor("#71bd40"));
                viewHolder.mIdTextViewJudge.setTextColor(Color.parseColor("#3a3a3a"));
            }

            //第一行数据颜色设置为#f2f2f2
            if (mDataList.get(position).get("result").toString().equals("结果")) {
                viewHolder.mIdTextViewState.setBackgroundColor(Color.parseColor("#f2f2f2"));
                viewHolder.mIdTextViewResult.setBackgroundColor(Color.parseColor("#f2f2f2"));
                viewHolder.mIdTextViewJudge.setBackgroundColor(Color.parseColor("#f2f2f2"));
            }

            if ((position + "").equals(judgeIndex)) {//如果对话框中的状态和已选中状态一致，用红色显示
                viewHolder.mIdTextViewState.setTextColor(Color.parseColor("#FF0000"));
                viewHolder.mIdTextViewResult.setTextColor(Color.parseColor("#FF0000"));
                viewHolder.mIdTextViewJudge.setTextColor(Color.parseColor("#FF0000"));
            }


            viewHolder.mIdTextViewState.setText(mDataList.get(position).get("state").toString());
            viewHolder.mIdTextViewResult.setText(mDataList.get(position).get("result").toString());
            viewHolder.mIdTextViewJudge.setText(mDataList.get(position).get("judge").toString());

            return convertView;
        }


    }

    static class ViewHolder {
        @Bind(R.id.id_textViewResult)
        TextView mIdTextViewResult;
        @Bind(R.id.id_textViewState)
        TextView mIdTextViewState;
        @Bind(R.id.id_textViewJudge)
        TextView mIdTextViewJudge;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    //设置列表控件颜色
    private void setupItemViewsColor(TextView textView1, TextView textView2, TextView textView3, int color) {
        textView1.setTextColor(color);
        textView2.setTextColor(color);
        textView3.setTextColor(color);
    }
}
