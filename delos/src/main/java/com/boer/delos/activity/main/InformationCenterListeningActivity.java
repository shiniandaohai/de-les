package com.boer.delos.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.fragment.AlarmFragmentsAdapter;
import com.boer.delos.fragment.HistoricalAlarmsFragment;
import com.boer.delos.fragment.SystemMessageFragment;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.Host;
import com.boer.delos.model.HostResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.popupWindow.MessageDeletePopupWindow;
import com.boer.delos.view.popupWindow.MessagePopupWindow;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author PengJiYang
 * @Description: "报警信息"界面  消息中心
 * create at 2016/4/14 13:40
 */
public class InformationCenterListeningActivity extends BaseListeningActivity {

    @Bind(R.id.id_textViewDate)
    TextView mIdTextViewDate;
    @Bind(R.id.id_textViewHost)
    TextView mIdTextViewHost;
    @Bind(R.id.id_textViewType)
    TextView mIdTextViewType;
    @Bind(R.id.linearTop)
    LinearLayout mLinearTop;
    @Bind(R.id.ivRight)
    ImageView mIvRight;
    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;
    /**
     * Tab显示内容TextView
     */
    private TextView mHistoricalAlarmsTv, mSystemMessageTv;
    /**
     * Tab的那个引导线
     */
    private ImageView mTabLineIv;

    private HistoricalAlarmsFragment mHistoricalAlarmsFragment;
    private SystemMessageFragment mSystemMessageFragment;
    private List<Host> hostList = new ArrayList<>();


    //头部控件显示的时间，主机，报警类型信息,切换ViewPager改变头部控件的值
    //历史告警
    private String topDate1 = "";
    private String topHostHistiry = "全部主机";
    private String topType1 = "全部";
    //系统消息
    private String topDate2 = "";
    private String topHost2 = "全部主机";
    private String topType2 = "全部";
    //刷新数据需要的参数
    private String mParamsHost = "";
    private String mParamsMessage = "";
    private String mParamsDate = "";
    private MessagePopupWindow messagePopupWindow;//筛选信息
    private Host hostLocalConn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_information);
        initTopBar("消息中心", null, true, true);

        Date date = new Date();
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
        mParamsDate = myFmt.format(date);

        ButterKnife.bind(this);
        mHistoricalAlarmsTv = (TextView) this.findViewById(R.id.id_detail_tv);
        mSystemMessageTv = (TextView) this.findViewById(R.id.id_photo_tv);
        mTabLineIv = (ImageView) this.findViewById(R.id.id_tab_line_iv);
        ivRight.setImageResource(R.mipmap.ic_nav_delete);
        initTabLineWidth();

        settingHostLoacalConn(); //本地直连处理

        //构造适配器
        List<Fragment> fragments = new ArrayList<Fragment>();
        mHistoricalAlarmsFragment = new HistoricalAlarmsFragment();
        mSystemMessageFragment = new SystemMessageFragment();
        fragments.add(mHistoricalAlarmsFragment);
        fragments.add(mSystemMessageFragment);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mIdTextViewDate.setText(simpleDateFormat.format(new Date()));
        //设置默认值
        topDate1 = simpleDateFormat.format(new Date());
        topDate2 = simpleDateFormat.format(new Date());

        AlarmFragmentsAdapter adapter = new AlarmFragmentsAdapter(getSupportFragmentManager(), fragments);

        //设定适配器
        final ViewPager vp = (ViewPager) findViewById(R.id.id_viewpager);
        vp.setAdapter(adapter);


        vp.setCurrentItem(0);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }

            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
                lineViewClick(position, offset);
                //系统消息界面    //没外网
                if ((position == 1) && !Constant.IS_INTERNET_CONN) {
                    toastUtils.showInfoWithStatus("请先连接因特网查看系统消息");
                }
                mLinearTop.setEnabled(!((position == 1) && !Constant.IS_INTERNET_CONN));
            }

            @Override
            public void onPageSelected(int position) {
                textViewClick(position);

            }

        });

        findViewById(R.id.id_tab_Detail_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(0);
            }
        });
        findViewById(R.id.id_tab_photo_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(1);
            }
        });

        mLinearTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtil.checkNet(getApplicationContext()) && Constant.IS_INTERNET_CONN) {
                    getFamilyData(vp);

                } else if (NetUtil.checkNet(getApplicationContext())) {
                    getCurrentGatewayHistoryInfo(vp);
                }
            }
        });

        mIvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List datas = null;
                String[] titleData = null;
                if (vp.getCurrentItem() == 0) {//告警信息
                    datas = mHistoricalAlarmsFragment.getDatas();
                    titleData = new String[]{topDate1, topHostHistiry, topType1};
                } else {//系统消息
                    datas = mSystemMessageFragment.getDatas();
                    titleData = new String[]{topDate2, topHost2, topType2};
                }

                if (datas.size() == 0) {
                    ToastHelper.showShortMsg("没有数据可以删除");
                    return;
                }
                MessageDeletePopupWindow messageDeletePopupWindow =
                        new MessageDeletePopupWindow(InformationCenterListeningActivity.this,
                                mLinearTop, vp.getCurrentItem(), datas, titleData);
                messageDeletePopupWindow.showPopupWindow();
                messageDeletePopupWindow.setMessageDeletePopupWindowInterface(new MessageDeletePopupWindow.MessageDeletePopupWindowInterface() {
                    @Override
                    public void submitButtonClick(boolean isNeedUpdate) {
                        if (isNeedUpdate) {//需要刷新数据
                            if (vp.getCurrentItem() == 0) {//告警信息
                                mHistoricalAlarmsFragment.refreshData(mParamsDate, mParamsHost, mParamsMessage);
                            } else {//系统消息
                                mSystemMessageFragment.refreshData(mParamsDate, mParamsHost, mParamsMessage);
                            }
                        }
                    }
                });

            }
        });

    }


    //根绝viewpager变化，下划线改变位置
    private void lineViewClick(int pos, float offset) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();

        /**
         * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
         * 设置mTabLineIv的左边距 滑动场景：
         * 记2个页面,
         * 从左到右分别为0,1
         * 0->1;  1->0
         */

        if (currentIndex == 0 && pos == 0)// 0->1
        {
            lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex
                    * (screenWidth / 2));

        } else if (currentIndex == 1 && pos == 0) // 1->0
        {
            lp.leftMargin = (int) (-(1 - offset)
                    * (screenWidth * 1.0 / 2) + currentIndex
                    * (screenWidth / 2));

        }
        mTabLineIv.setLayoutParams(lp);
    }


    //根据viewpager变化，标签改变位置
    private void textViewClick(int pos) {
        resetTextView();
        switch (pos) {
            case 0:
                mHistoricalAlarmsTv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                mIdTextViewDate.setText(topDate1);
                mIdTextViewHost.setText(topHostHistiry);
                mIdTextViewType.setText(topType1);
                break;
            case 1:
                mSystemMessageTv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                mIdTextViewDate.setText(topDate2);
                mIdTextViewHost.setText(topHost2);
                mIdTextViewType.setText(topType2);
                break;

        }
        currentIndex = pos;
    }


    /**
     * 设置滑动条的宽度为屏幕的1/2(根据Tab的个数而定)
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 2;
        mTabLineIv.setLayoutParams(lp);
    }

    /**
     * 重置颜色
     */
    private void resetTextView() {
        mHistoricalAlarmsTv.setTextColor(getResources().getColor(R.color.txt_gray));

        mSystemMessageTv.setTextColor(getResources().getColor(R.color.txt_gray));

    }

    /**
     * 本地直连状态下，仅显示登录的主机信息
     */
    private void settingHostLoacalConn() {
        hostLocalConn = new Host();
        if (NetUtil.checkNet(this) && !Constant.IS_INTERNET_CONN) {
            for (GatewayInfo gatewayInfo : Constant.gatewayInfos) {
                if (gatewayInfo.getHostId().equals(Constant.CURRENTHOSTID)) {
                    hostLocalConn.setName(gatewayInfo.getHostName());
                    hostLocalConn.setHostId(Constant.CURRENTHOSTID);
                }
            }
            topHostHistiry = hostLocalConn.getName();
            mIdTextViewHost.setText(topHostHistiry);
        }


    }

    /**
     * 获取家人数据,得到主机信息
     */
    private void getFamilyData(final ViewPager vp) {
        toastUtils.showProgress("查询主机中...");
        FamilyManageController.getInstance().showFamilies(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    toastUtils.dismiss();
                    HostResult result = new Gson().fromJson(Json, HostResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        hostList.clear();
                        hostList.addAll(sortHostList(result.getHosts()));
                        if (messagePopupWindow == null) {
                            messagePopupWindow = new MessagePopupWindow(InformationCenterListeningActivity.this);
                        }
                        messagePopupWindow.initPopup(mLinearTop, vp.getCurrentItem(), hostList);
                        messagePopupWindow.showPopupWindow();
                        messagePopupWindow.setMessagePopupWindowInterface(new MessagePopupWindow.MessagePopupWindowInterface() {
                            @Override
                            public void submitButtonClick(String paramsHost, String paramsMessage, String paramsDate, String hostName) {

                                if (hostName.equals("")) {
                                    mIdTextViewHost.setText("全部主机");
                                } else {
                                    mIdTextViewHost.setText(hostName);
                                }

                                mIdTextViewDate.setText(paramsDate);

                                if (vp.getCurrentItem() == 0) {//历史告警
                                    if (paramsMessage.equals("")) {
                                        mIdTextViewType.setText("全部");
                                    } else {
                                        mIdTextViewType.setText(paramsMessage);
                                    }
                                    topDate1 = mIdTextViewDate.getText().toString();
                                    topHostHistiry = mIdTextViewHost.getText().toString();
                                    topType1 = mIdTextViewType.getText().toString();
                                    mParamsDate = paramsDate;
                                    mParamsHost = paramsHost;
                                    mParamsMessage = paramsMessage;
                                    mHistoricalAlarmsFragment.refreshData(paramsDate, paramsHost, paramsMessage);
                                }

                                if (vp.getCurrentItem() == 1) {//系统消息
                                    if (paramsMessage.equals("")) {
                                        mIdTextViewType.setText("全部");
                                    } else if ("0".equals(paramsMessage)) {
                                        mIdTextViewType.setText("全部");
                                    } else if ("1".equals(paramsMessage)) {
                                        mIdTextViewType.setText("家庭管理");
                                    } else if ("2".equals(paramsMessage)) {
                                        mIdTextViewType.setText("权限变更");
                                    }

                                    topDate2 = mIdTextViewDate.getText().toString();
                                    topHost2 = mIdTextViewHost.getText().toString();
                                    topType2 = mIdTextViewType.getText().toString();
                                    mParamsDate = paramsDate;
                                    mParamsHost = paramsHost;
                                    mParamsMessage = paramsMessage;
                                    mSystemMessageFragment.refreshData(paramsDate, paramsHost, paramsMessage);
                                }

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                toastUtils.showErrorWithStatus(json);
            }
        });
    }

    private void getCurrentGatewayHistoryInfo(final ViewPager vp) {
        if (messagePopupWindow == null) {
            messagePopupWindow = new MessagePopupWindow(InformationCenterListeningActivity.this);
        }
        if (hostList == null) hostList = new ArrayList<>();
        hostList.clear();
        hostList.add(hostLocalConn);
        messagePopupWindow.initPopup(mLinearTop, vp.getCurrentItem(), hostList);
        messagePopupWindow.showPopupWindow();
        messagePopupWindow.setMessagePopupWindowInterface(new MessagePopupWindow.MessagePopupWindowInterface() {
            @Override
            public void submitButtonClick(String paramsHost, String paramsMessage, String paramsDate, String hostName) {

                if (hostName.equals("")) {
                    mIdTextViewHost.setText("全部主机");
                } else {
                    mIdTextViewHost.setText(hostName);
                }

                mIdTextViewDate.setText(paramsDate);

                if (vp.getCurrentItem() == 0) {//历史告警
                    if (paramsMessage.equals("")) {
                        mIdTextViewType.setText("全部");
                    } else {
                        mIdTextViewType.setText(paramsMessage);
                    }
                    topDate1 = mIdTextViewDate.getText().toString();
                    topHostHistiry = mIdTextViewHost.getText().toString();
                    topType1 = mIdTextViewType.getText().toString();
                    mParamsDate = paramsDate;
                    mParamsHost = paramsHost;
                    mParamsMessage = paramsMessage;
                    mHistoricalAlarmsFragment.refreshData(paramsDate, paramsHost, paramsMessage);
                }

                if (vp.getCurrentItem() == 1) {//系统消息
                    if (paramsMessage.equals("")) {
                        mIdTextViewType.setText("全部");
                    } else if ("0".equals(paramsMessage)) {
                        mIdTextViewType.setText("全部");
                    } else if ("1".equals(paramsMessage)) {
                        mIdTextViewType.setText("家庭管理");
                    } else if ("2".equals(paramsMessage)) {
                        mIdTextViewType.setText("权限变更");
                    }

                    topDate2 = mIdTextViewDate.getText().toString();
                    topHost2 = mIdTextViewHost.getText().toString();
                    topType2 = mIdTextViewType.getText().toString();
                    mParamsDate = paramsDate;
                    mParamsHost = paramsHost;
                    mParamsMessage = paramsMessage;
                    mSystemMessageFragment.refreshData(paramsDate, paramsHost, paramsMessage);
                }

            }
        });
    }

    /**
     * 对返回的host进行排序,当前使用的放在最上面
     *
     * @param hosts
     * @return
     */
    private List<Host> sortHostList(List<Host> hosts) {
        Host currentHost = null;
        //找到当前主机
        for (Host host : hosts) {
            if (Constant.CURRENTHOSTID.equals(host.getHostId())) {
                currentHost = host;
                break;
            }
        }
        if (currentHost != null) {
            hosts.remove(currentHost);
            hosts.add(0, currentHost);
        }
        return hosts;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1000 && resultCode == RESULT_OK) {
//            mSystemMessageFragment.onActivityResult(requestCode, resultCode, data);
//        }
        mSystemMessageFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //返回键取消进度条
            if (toastUtils.isShowing()) {
                toastUtils.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void refreshDelBtn(boolean flag){
        if(flag){
            mIvRight.setEnabled(true);
            mIvRight.getBackground().setAlpha(255);
        }
        else{
            mIvRight.setEnabled(false);
            mIvRight.getBackground().setAlpha(100);
        }
    }
}
