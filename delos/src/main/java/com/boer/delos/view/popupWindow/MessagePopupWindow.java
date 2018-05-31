package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.MessageAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Host;
import com.boer.delos.utils.DateHelper;
import com.boer.delos.utils.DensityUitl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @Created by Administrator on 2016/7/28 0028.
 * @Description:消息中心里，系统消息和告警信息筛选的popupWindow
 */
public class MessagePopupWindow {
    //主机信息
    private List<Host> hostList = new ArrayList<>();
    //为RecyclerView设置的adapter
    MessageAdapter mMessageAdapter0;
    MessageAdapter mMessageAdapter1;
    MessageAdapter mMessageAdapter2;
    MessageAdapter mMessageAdapter3;
    MessageAdapter mMessageAdapter4;
    private Context mContext;
    private View topView;//popupwindow显示在topView下方
    private MessagePopupWindowInterface mMessagePopupWindowInterface;
    private PopupWindow pop;
    private int fragmentID = 0;//告警信息和历史消息界面，根据不同的界面，会对popupwindow进行不同的操作和设置界面内容

    //筛选条件
    private String paramsHost;
    private String paramsMessage;
    private String paramsDate;
    private String hostName;

    private String year;
    private String month;
    private String day;

    //年月日
    private ArrayList<String> list2;
    private ArrayList<String> list3;
    private ArrayList<String> list4;


    public MessagePopupWindow(Context context) {
        mContext = context;
    }

    public void initPopup(View topView, int mFragmentID, List<Host> mHostList) {
        this.topView = topView;
        this.fragmentID = mFragmentID;
        this.hostList = mHostList;
    }

    private View view;
    public void showPopupWindow() {
        //控件初始化
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(view==null)
        view = inflater.inflate(R.layout.message_popupwindow, null);

        TextView mTxtCancel = (TextView) view.findViewById(R.id.txt_cancel);
        RecyclerView mRvHost = (RecyclerView) view.findViewById(R.id.rvHost);
        TextView mTxtMessageType = (TextView) view.findViewById(R.id.txt_messageType);
        RecyclerView mRvMessageType = (RecyclerView) view.findViewById(R.id.rvMessageType);
        RecyclerView mRvYear = (RecyclerView) view.findViewById(R.id.rvYear);
        RecyclerView mRvMonth = (RecyclerView) view.findViewById(R.id.rvMonth);
        RecyclerView mRvDay = (RecyclerView) view.findViewById(R.id.rvDay);
        TextView mTvConfirm = (TextView) view.findViewById(R.id.tvConfirm);
        ImageView mImgCancle = (ImageView) view.findViewById(R.id.ivBack);
        ArrayList<RecyclerView> recyclerViews = new ArrayList<>();
        recyclerViews.add(mRvHost);
        recyclerViews.add(mRvMessageType);
        recyclerViews.add(mRvYear);
        recyclerViews.add(mRvMonth);
        recyclerViews.add(mRvDay);

        for (int i = 0; i < recyclerViews.size(); i++) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerViews.get(i).setLayoutManager(layoutManager);
            recyclerViews.get(i).setHasFixedSize(true);

//            int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.space);
//            recyclerViews.get(i).addItemDecoration(new SpaceItemDecoration(DensityUitl.dip2px(mContext, 10)));
        }

        //设置主机
        final List<String> hostNameList = new ArrayList<>();//主机名称，用于显示
        final List<String> hostIDList = new ArrayList<>();//主机ID，用于传参

        if (hostList == null) {
            hostList = new ArrayList<>();
        }
        //设置默认值           //没外网直连
        if (!Constant.IS_INTERNET_CONN && Constant.IS_LOCAL_CONNECTION) {
        } else {
            hostNameList.add("全部主机");
            hostIDList.add("");
        }
        for (Host host : hostList) {
            hostNameList.add(host.getName());
            hostIDList.add(host.getHostId());
        }

        mMessageAdapter0 = new MessageAdapter(mContext, hostNameList);
        recyclerViews.get(0).setAdapter(mMessageAdapter0);
        //根据不同的消息界面，设置不同的消息类型
        final ArrayList<String> list1 = new ArrayList();//用于显示
        final ArrayList<String> list1Param = new ArrayList();//用于接口传参数
        if (fragmentID == 0) {//历史告警,“”(全部),“非法入侵”,“火灾报警”,“水浸报警”,“环境污染”,“跌倒报警”
            list1.add("全部");
            list1.add("入侵");
            list1.add("安防");
            list1.add("水浸");
            list1.add("空气");
            list1.add("跌倒");
            list1.add("煤气");
            list1Param.add("");
            list1Param.add("非法入侵");
            list1Param.add("火灾报警");
            list1Param.add("水浸报警");
            list1Param.add("环境污染");
            list1Param.add("跌倒报警");
            list1Param.add("煤气超标");

            mTxtMessageType.setText("历史告警");
        }
        if (fragmentID == 1) {//系统消息,0：全部，1：家庭管理，2：权限变更
            list1.add("全部");
            list1.add("家庭管理");
            list1.add("权限变更");
            list1Param.add("0");
            list1Param.add("1");
            list1Param.add("2");
            mTxtMessageType.setText("系统消息");
        }
        mMessageAdapter1 = new MessageAdapter(mContext, list1);
        recyclerViews.get(1).setAdapter(mMessageAdapter1);

        //设置当前年份与前一年
        Calendar c = Calendar.getInstance();
        list2 = new ArrayList();
        list2.add(c.get(Calendar.YEAR) + "");
        list2.add((c.get(Calendar.YEAR) - 1) + "");
        mMessageAdapter2 = new MessageAdapter(mContext, list2);
        recyclerViews.get(2).setAdapter(mMessageAdapter2);
        //设置月份
        list3 = getMonth(c.get(Calendar.YEAR) + "");
        mMessageAdapter3 = new MessageAdapter(mContext, list3);
        recyclerViews.get(3).setAdapter(mMessageAdapter3);

        //设置日期
        list4 = getDay(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
        mMessageAdapter4 = new MessageAdapter(mContext, list4);
        recyclerViews.get(4).setAdapter(mMessageAdapter4);


        paramsHost = hostIDList.get(0);

        paramsMessage = list1Param.get(0);
        hostName = hostNameList.get(0);
        year = list2.get(0);
        month = list3.get(0);
        day = list4.get(0);
        paramsDate = year + "-" + month + "-" + day;
        //各个recyclerView的点击事件
        //主机
        mMessageAdapter0.setOnItemClickLitener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                paramsHost = hostIDList.get(position);
                hostName = hostNameList.get(position);
            }
        });
        //报警类型
        mMessageAdapter1.setOnItemClickLitener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                paramsMessage = list1Param.get(position);
            }
        });

        //年
        mMessageAdapter2.setOnItemClickLitener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                year = list2.get(position);
                list3 = getMonth(year);
                mMessageAdapter3.setDatas(list3);
                mMessageAdapter3.notifyDataSetChanged();
                //选择年份，月份默认选中第一个
                month = list3.get(0);
                //日期默认第一个
                list4 = getDay(Integer.parseInt(year), Integer.parseInt(month));
                mMessageAdapter4.setDatas(list4);
                mMessageAdapter4.notifyDataSetChanged();
                //选中月份，日期默认选择第一个
                day = list4.get(0);
            }
        });

        //月
        mMessageAdapter3.setOnItemClickLitener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                month = list3.get(position);
                list4 = getDay(Integer.parseInt(year), Integer.parseInt(month));
                mMessageAdapter4.setDatas(list4);
                mMessageAdapter4.notifyDataSetChanged();
                //选中月份，日期默认选择第一个
                day = list4.get(0);
            }
        });

        //日期
        mMessageAdapter4.setOnItemClickLitener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                day = list4.get(position);
            }
        });

//        //把之前的pop关闭，重新打开一个，保证每次弹出一致
//        if (pop != null) {
//            if (pop.isShowing()) {
//                pop.dismiss();
//            }
//        }
        if (pop == null)
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
        mImgCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        mTvConfirm.setOnClickListener(new View.OnClickListener() {
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
        });

    }

    public void setMessagePopupWindowInterface(MessagePopupWindowInterface s) {
        mMessagePopupWindowInterface = s;
    }

    //设置ImageView隐藏
    private void setImageViewHide(ArrayList<ImageView> arrayListImageView) {
        for (ImageView i :
                arrayListImageView) {
            i.setVisibility(View.INVISIBLE);
        }
    }

    //关闭popupwindow
    public void dismissPop() {
        if (pop != null) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }
    }

    public boolean isPopWindowShowing() {
        return pop.isShowing();
    }

    public interface MessagePopupWindowInterface {
        //调用接口需要传入的参数
        public void submitButtonClick(String paramsHost, String paramsMessage, String paramsDate, String hostName);
    }

    //根据年得到月，当前年得到年初到本月，前一年，得到12个月
    private ArrayList<String> getMonth(String year) {
        Calendar c = Calendar.getInstance();
        ArrayList<String> list = new ArrayList();
        if (year.equals(c.get(Calendar.YEAR) + "")) {//当前年
            int currentMonth = c.get(Calendar.MONTH) + 1;
            for (int i = currentMonth; i > 0; i--) {
                list.add(i + "");
            }
        } else {//前一年
            for (int i = 12; i > 0; i--) {
                list.add(i + "");
            }
        }
        return list;
    }

    //根据月得到日期，当前月得到月初到当天，其他月设置天数
    private ArrayList<String> getDay(int year, int month) {
        Calendar c = Calendar.getInstance();
        ArrayList<String> list = new ArrayList();
        int monthDay = DateHelper.getInstance().getMonthLastDay(year, month);//这个月的天数
        if (month == c.get(Calendar.MONTH) + 1) {//当前月
            int currentDay = c.get(Calendar.DAY_OF_MONTH);
            for (int i = currentDay; i > 0; i--) {
                list.add(i + "");
            }
        } else {
            for (int i = monthDay; i > 0; i--) {
                list.add(i + "");
            }
        }
        return list;
    }


    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
//            outRect.bottom = space;
//
//            // Add top margin only for the first item to avoid double space between items
//            if (parent.getChildPosition(view) == 0)
//                outRect.top = space;
        }
    }
}
