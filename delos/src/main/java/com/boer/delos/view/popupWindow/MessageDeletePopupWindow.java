package com.boer.delos.view.popupWindow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.adapter.HistoricalAlarmsAdapter;
import com.boer.delos.adapter.HistoricalAlarmsAdapter.SelectStateLinstener;
import com.boer.delos.adapter.SystemMessageAdapter;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.alarm.AlarmController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ToastHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/29 0028.
 * 消息中心里，删除消息的popupWindow
 */
public class MessageDeletePopupWindow {

    //private ToastUtils toastUtils;
    private Context mContext;
    private View topView;//popupwindow显示在topView下方
    private MessageDeletePopupWindowInterface mMessageDeletePopupWindowInterface;
    private PopupWindow pop;
    private int fragmentID = 0;//告警信息和历史消息界面，根据不同的界面，会对popupwindow进行不同的操作和设置界面内容
    private List datas;//列表的adapter，根据不同界面，加载告警信息或者历史消息的adapter
    private String[] mTitleStrings;//头部显示的信息，主机，时间，类型
    private HistoricalAlarmsAdapter mHistoricalAlarmsAdapter = null;
    private SystemMessageAdapter mSystemMessageAdapter = null;

    public MessageDeletePopupWindow(Context context, View topView, int mFragmentID, List mDatas, String[] titleStrings) {
        mContext = context;
        this.topView = topView;
        fragmentID = mFragmentID;
        datas = mDatas;
        mTitleStrings = titleStrings;
        // toastUtils = new ToastUtils(mContext);
    }

    public void setMessageDeletePopupWindowInterface(MessageDeletePopupWindowInterface messageDeletePopupWindowInterface) {
        mMessageDeletePopupWindowInterface = messageDeletePopupWindowInterface;
    }

    public void showPopupWindow() {
        //控件初始化
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.message_delete_popupwindow, null);

        TextView mTxtDelete = (TextView) view.findViewById(R.id.txt_delete);
        TextView mTvTitle = (TextView) view.findViewById(R.id.tvTitle);
        TextView mTxtCancel = (TextView) view.findViewById(R.id.txt_cancel);
        TextView mIdTextViewDate = (TextView) view.findViewById(R.id.id_textViewDate);
        TextView mIdTextViewHost = (TextView) view.findViewById(R.id.id_textViewHost);
        TextView mIdTextViewType = (TextView) view.findViewById(R.id.id_textViewType);
        LinearLayout mLinearTop = (LinearLayout) view.findViewById(R.id.linearTop);
        ListView mIdListView = (ListView) view.findViewById(R.id.id_listView);
        final TextView mIdTxtSelectAll = (TextView) view.findViewById(R.id.id_txtSelectAll);
        final TextView mIdTxtDelete = (TextView) view.findViewById(R.id.id_txtDelete);

        if (mTitleStrings != null) {//设置头部信息
            if (mTitleStrings.length == 3) {
                mIdTextViewDate.setText(mTitleStrings[0]);
                mIdTextViewHost.setText(mTitleStrings[1]);
                mIdTextViewType.setText(mTitleStrings[2]);
            }
        }

        if (fragmentID == 0) {
            mTvTitle.setText("报警信息");
            mHistoricalAlarmsAdapter = new HistoricalAlarmsAdapter(mContext, datas, true);
            mIdListView.setAdapter(mHistoricalAlarmsAdapter);
            mHistoricalAlarmsAdapter.setSelectStateLinstener(new SelectStateLinstener(){

                @Override
                public void onSelected(boolean isSelected) {
                    mIdTxtDelete.setEnabled(isSelected);
                    mIdTxtDelete.setTextColor(isSelected?Color.BLACK:Color.parseColor("#a2a2a2"));
                }
            });
        } else {
            mTvTitle.setText("系统消息");
            mSystemMessageAdapter = new SystemMessageAdapter(mContext, datas, true, null);
            mIdListView.setAdapter(mSystemMessageAdapter);
            mSystemMessageAdapter.setSelectStateLinstener(new SelectStateLinstener(){

                @Override
                public void onSelected(boolean isSelected) {
                    mIdTxtDelete.setEnabled(isSelected);
                    mIdTxtDelete.setTextColor(isSelected?Color.BLACK:Color.parseColor("#a2a2a2"));
                }
            });
        }


        mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("确定要删除所有数据吗？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //全选
                        if (fragmentID == 0) {
                            mHistoricalAlarmsAdapter.selectAll();
                        } else {
                            mSystemMessageAdapter.selectAll();
                        }
                        //全部删除
                        delete(fragmentID);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.create().show();
            }
        });


        mIdTxtSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentID == 0) {
                    boolean isSelectAll = true;//是否全选，如果全选，字体变成“反选”
                    HashMap<Integer, String> alarmIDs = mHistoricalAlarmsAdapter.getAlarmIDSelected();
                    for (int i = 0; i < alarmIDs.size(); i++) {
                        if (alarmIDs.get(i).equals("")) {
                            isSelectAll = false;
                            break;
                        }
                    }
                    if (isSelectAll) {
                        mHistoricalAlarmsAdapter.selectNo();
                        mIdTxtSelectAll.setText("全选");

                    } else {
                        mHistoricalAlarmsAdapter.selectAll();
                        mIdTxtSelectAll.setText("反选");
                    }

                } else {
                    boolean isSelectAll = true;//是否全选，如果全选，字体变成“反选”
                    HashMap<Integer, String> msgId = mSystemMessageAdapter.getMsgIdSelected();
                    for (int i = 0; i < msgId.size(); i++) {
                        if (msgId.get(i).equals("")) {
                            isSelectAll = false;
                            break;
                        }
                    }
                    if (!isSelectAll) {
                        mIdTxtSelectAll.setText("反选");
                        mSystemMessageAdapter.selectAll();

                    } else {
                        mSystemMessageAdapter.selectNo();
                        mIdTxtSelectAll.setText("全选");
                    }
                }
            }
        });

        mIdTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Integer, String> delateIds = null;
                switch (fragmentID) {
                    case 0:
                        delateIds = mHistoricalAlarmsAdapter.getAlarmIDSelected();
                        break;
                    default:
                        delateIds = mSystemMessageAdapter.getMsgIdSelected();
                        break;
                }
                if (delateIds == null || delateIds.size() == 0) {
                    ToastHelper.showShortMsg("没选择删除数据");
                    return;
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("确定要删除数据吗？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        //删除
                        delete(fragmentID);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.create().show();

            }
        });

        //把之前的pop关闭，重新打开一个
        if (pop != null) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }

        pop = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);


        //防止被底部虚拟键挡住
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        pop.setBackgroundDrawable(new BitmapDrawable());//需要设置背景，用物理键返回的时候
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setAnimationStyle(R.style.MenuAnimationFadeDown);
        if (pop.isShowing()) {
            pop.dismiss();
        }
        pop.showAtLocation(topView,
                Gravity.BOTTOM, 0, 0);
        mTxtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

      /*  mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                if (mMessagePopupWindowInterface != null) {
                    if (Integer.parseInt(month) < 10) {//对小于10的月份，加0，组合成参数
                        month = "0" + month;
                    }
                    if (Integer.parseInt(day) < 10) {
                        day = "0" + day;
                    }
                    paramsDate = year + "-" + month + "-" + day;
                    mMessagePopupWindowInterface.submitButtonClick(paramsHost, paramsMessage, paramsDate, hostName);
                }
            }
        });*/

    }


    public boolean isPopWindowShowing() {
        return pop.isShowing();
    }

    public interface MessageDeletePopupWindowInterface {
        //调用接口需要传入的参数
        //如果删除成功，重新刷新数据
        public void submitButtonClick(boolean isNeedUpdate);
    }

    //删除历史消息
    private void deleteAlarmInformation(String[] alarmId) {

        AlarmController.getInstance().deleteAlarm(mContext, alarmId, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                //   toastUtils.dismiss();
                L.e("getSystemInformation===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    mMessageDeletePopupWindowInterface.submitButtonClick(true);
                } else {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                    mMessageDeletePopupWindowInterface.submitButtonClick(false);
                }
                pop.dismiss();
            }

            @Override
            public void onFailed(String json) {
                // toastUtils.dismiss();
                L.e("getSystemInformation:" + json);
                Toast.makeText(mContext, json, Toast.LENGTH_SHORT).show();
                mMessageDeletePopupWindowInterface.submitButtonClick(false);
                pop.dismiss();
            }
        });
    }

    //删除系统消息
    private void deleteSystemMessageInformation(String[] msgId) {

        AlarmController.getInstance().deleteSystemMessage(mContext, msgId, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                //  toastUtils.dismiss();

                L.e("getSystemInformation===" + Json);

                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    mMessageDeletePopupWindowInterface.submitButtonClick(true);
                } else {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                    mMessageDeletePopupWindowInterface.submitButtonClick(false);
                }
                pop.dismiss();
            }

            @Override
            public void onFailed(String json) {
                L.e("getSystemInformation:" + json);
                // toastUtils.dismiss();
                Toast.makeText(mContext, json, Toast.LENGTH_SHORT).show();
                mMessageDeletePopupWindowInterface.submitButtonClick(false);
                pop.dismiss();
            }
        });
    }


    //删除事件
    private void delete(int fragmentID) {

        if (fragmentID == 0) {
            HashMap<Integer, String> alarmIDs = mHistoricalAlarmsAdapter.getAlarmIDSelected();
            ArrayList<String> arrayList = new ArrayList<String>();
            for (int i = 0; i < alarmIDs.size(); i++) {
                if (!alarmIDs.get(i).equals("")) {
                    arrayList.add(alarmIDs.get(i));
                }
            }
            if (arrayList.size() == 0) {
                Toast.makeText(mContext, "没选择删除数据", Toast.LENGTH_SHORT).show();
            } else {
                String[] strings = new String[arrayList.size()];
                for (int i = 0; i < arrayList.size(); i++) {
                    strings[i] = arrayList.get(i);
                }
                //  toastUtils.showProgress("删除中...");
                deleteAlarmInformation(strings);
            }


        } else {
            HashMap<Integer, String> msgIds = mSystemMessageAdapter.getMsgIdSelected();
            ArrayList<String> arrayList = new ArrayList<String>();
            for (int i = 0; i < msgIds.size(); i++) {
                if (!msgIds.get(i).equals("")) {
                    arrayList.add(msgIds.get(i));
                }
            }
            if (arrayList.size() == 0) {
                Toast.makeText(mContext, "没选择删除数据", Toast.LENGTH_SHORT).show();
            } else {
                String[] strings = new String[arrayList.size()];
                for (int i = 0; i < arrayList.size(); i++) {
                    strings[i] = arrayList.get(i);
                }
                // toastUtils.showProgress("删除中...");
                deleteSystemMessageInformation(strings);
            }

        }
    }


}
