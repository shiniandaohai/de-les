package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.AlarmDetail;
import com.boer.delos.model.AlarmInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author PengJiYang
 * @Description: "报警信息"列表适配器
 * create at 2016/4/14 13:43
 */
public class HistoricalAlarmsAdapter extends BaseAdapter {

    private List<AlarmInfo> datas = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;
    private boolean mIsDelete = false;//是否是删除功能，删除显示多选框，
    // 用来控制CheckBox的选中状况
    private  HashMap<Integer, Boolean> isSelected;
    private  HashMap<Integer, String> alarmIDSelected;//选中的alarmID

    public HistoricalAlarmsAdapter(Context context, List<AlarmInfo> datas, boolean isDelete) {
        this.context = context;
        this.datas = datas;
        mIsDelete = isDelete;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        alarmIDSelected = new HashMap<Integer, String>();
        for(int i = 0;i<datas.size();i++){
            getIsSelected().put(i,false);
            alarmIDSelected.put(i,"");
        }
    }


    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_historical_alarms, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AlarmInfo info = datas.get(position);

        AlarmDetail alarmDetail = new Gson().fromJson(info.getDetail(), AlarmDetail.class);
        viewHolder.mIdTextViewType.setText(alarmDetail.getType());
        String time = "";
        try {
            time = alarmDetail.getProducetime().substring(10);
        } catch (Exception e) {
            e.printStackTrace();
            time = alarmDetail.getProducetime();
        }
        viewHolder.mIdTextViewTime.setText(time);

        if(mIsDelete){//是删除功能
            viewHolder.mCbSelect.setVisibility(View.VISIBLE);
            viewHolder.mCbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelected.get(position)) {
                        isSelected.put(position, false);
                        alarmIDSelected.put(position,"");
                    } else {
                        isSelected.put(position, true);
                        alarmIDSelected.put(position,info.getAlarmId());
                    }
                    onSelectStateChange();
                }
            });
            // 根据isSelected来设置checkbox的选中状况
            viewHolder.mCbSelect.setChecked(getIsSelected().get(position));
        }else{
            viewHolder.mCbSelect.setVisibility(View.GONE);
        }

        String[] messages = alarmDetail.getMessage().split(",");
        if (messages.length == 5) {
            viewHolder.mIdTextViewMessage1.setText(messages[1] + "(" + messages[0] + ")");
            viewHolder.mIdTextViewMessage2.setText(messages[2] + messages[3]);
        } else if (messages.length == 4) {
            viewHolder.mIdTextViewMessage1.setText(messages[0]);
            viewHolder.mIdTextViewMessage2.setText(messages[1] + messages[2]);
        } else if (messages.length == 3) {
            viewHolder.mIdTextViewMessage1.setText(messages[0]);
            viewHolder.mIdTextViewMessage2.setText(messages[1]);
        } else {
            viewHolder.mIdTextViewMessage1.setText("未知");
            viewHolder.mIdTextViewMessage2.setText(messages[0]);
        }
        if ("0".equals(alarmDetail.getAlarming())) {
            viewHolder.mIdTextViewAlarming.setText("已恢复");
            viewHolder.mIdTextViewAlarming.setTextColor(Color.parseColor("#a2a2a2"));
        } else {
            viewHolder.mIdTextViewAlarming.setText("正在告警");
            viewHolder.mIdTextViewAlarming.setTextColor(Color.parseColor("#fe473c"));
        }


        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.id_textViewType)
        TextView mIdTextViewType;
        @Bind(R.id.id_textViewTime)
        TextView mIdTextViewTime;
        @Bind(R.id.id_textViewMessage1)
        TextView mIdTextViewMessage1;
        @Bind(R.id.id_textViewMessage2)
        TextView mIdTextViewMessage2;
        @Bind(R.id.id_textViewAlarming)
        TextView mIdTextViewAlarming;
        @Bind(R.id.cbSelect)
        CheckBox mCbSelect;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public  HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }


    public HashMap<Integer, String> getAlarmIDSelected() {
        return alarmIDSelected;
    }


    //全选，改变选项，改变alarmID
    public void selectAll(){
        for(int i = 0;i<datas.size();i++){
            isSelected.put(i,true);
            alarmIDSelected.put(i,datas.get(i).getAlarmId());
        }
        notifyDataSetChanged();

        onSelectStateChange();
    }

    //反选
    public void selectNo(){
        for(int i = 0;i<datas.size();i++){
            isSelected.put(i,false);
            alarmIDSelected.put(i,"");
        }
        notifyDataSetChanged();

        onSelectStateChange();
    }

    private void onSelectStateChange(){
        Iterator iter=isSelected.entrySet().iterator();
        boolean isSelected=false;
        while (iter.hasNext()){
            Map.Entry entry=(Map.Entry)iter.next();
            Boolean value= (Boolean)entry.getValue();
            if(value){
                isSelected=true;
                break;
            }
        }
        if(mSelectStateLinstener!=null){
            mSelectStateLinstener.onSelected(isSelected);
        }
    }
    public interface SelectStateLinstener{
        public void onSelected(boolean isSelected);
    }
    private SelectStateLinstener mSelectStateLinstener;
    public void setSelectStateLinstener(SelectStateLinstener l){
        mSelectStateLinstener=l;
    }
}
