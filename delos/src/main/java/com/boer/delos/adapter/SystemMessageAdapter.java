package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.SystemMessage;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author PengJiYang
 * @Description: "系统消息"列表适配器
 * create at 2016/4/14 13:43
 */
public class SystemMessageAdapter extends BaseAdapter {

    private final ClickListener listener;
    private List<SystemMessage.MsgListBean> datas = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;
    private boolean mIsDelete = false;//是否是删除功能，删除显示多选框，不能点击列表项进行操作
    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected;
    private HashMap<Integer, String> msgIdSelected;//选中的 msgId

    public SystemMessageAdapter(Context context, List<SystemMessage.MsgListBean> datas, boolean isDelete, ClickListener listener) {
        this.context = context;
        this.datas = datas;
        this.listener = listener;
        mIsDelete = isDelete;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();

        msgIdSelected = new HashMap<Integer, String>();
        for (int i = 0; i < datas.size(); i++) {
            getIsSelected().put(i, false);
            msgIdSelected.put(i, "");
        }
    }

    /*public void setDatas(List<SystemMessage.MsgListBean> datas) {
        this.datas = datas;
        Loger.d("HistoricalAlarmsAdapter's datas=======" + new Gson().Object2Json(datas));
        for (int i = 0; i < datas.size(); i++) {
            getIsSelected().put(i, false);
        }
    }*/

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public SystemMessage.MsgListBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_system_alarms, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SystemMessage.MsgListBean message = getItem(position);

        if (mIsDelete) {//是删除功能
            viewHolder.mCbSelect.setVisibility(View.VISIBLE);
            viewHolder.mCbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelected.get(position)) {
                        isSelected.put(position, false);
                        msgIdSelected.put(position, "");

                    } else {
                        isSelected.put(position, true);
                        msgIdSelected.put(position, datas.get(position).getMsgId());


                    }
                    onSelectStateChange();
                }
            });
            // 根据isSelected来设置checkbox的选中状况
            viewHolder.mCbSelect.setChecked(getIsSelected().get(position));
        } else {
            viewHolder.mCbSelect.setVisibility(View.GONE);
        }

        viewHolder.mIdAccept.setVisibility(View.GONE);
        viewHolder.mIdReject.setVisibility(View.GONE);
        viewHolder.mIdStatus.setVisibility(View.GONE);

        //处理消息
        String detail = viewHolder.dealWithMessage(getItem(position));
        viewHolder.viewHolderClickListener(getItem(position), listener, mUserCancleApplayListener);

        viewHolder.mIdTextViewMessage1.setText(detail);
        viewHolder.mIdTextViewMessage2.setText(TimeUtil.formatStamp2Time(message.getTimestamp(), "HH:mm:ss"));

        String url="";
        if (message.getFromUser() != null && !StringUtil.isEmpty(message.getFromUser().getId())
                && Constant.USERID.equals(message.getFromUser().getId())) {
            url=datas.get(position).getToUser().getAvatarUrl().toString().trim();
        }
        else if (message.getFromUser() != null) {
            url=datas.get(position).getFromUser().getAvatarUrl().toString().trim();
        }


//        url = detail.contains("管理员")
//                ? datas.get(position).getFromUser().getAvatarUrl().toString().trim()
//                : datas.get(position).getToUser().getAvatarUrl().toString().trim();

        if (!StringUtil.isEmpty(url) && (viewHolder.mProfileImage.getTag() == null
                || StringUtil.isEmpty(viewHolder.mProfileImage.getTag().toString())
                || !viewHolder.mProfileImage.getTag().toString().equals(url))) {

//            String picUrl = (datas.get(position).getToUser() != null) ?
//                    datas.get(position).getToUser().getAvatarUrl().toString().trim() : "";
            String picUrl = URLConfig.HTTP + url;
            ImageLoader.getInstance().displayImage(picUrl, viewHolder.mProfileImage, BaseApplication.getInstance().displayImageOptions);

            viewHolder.mProfileImage.setTag(url);
        }

        return convertView;
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public HashMap<Integer, String> getMsgIdSelected() {
        return msgIdSelected;
    }


    //全选，改变选项，改变alarmID
    public void selectAll() {
        for (int i = 0; i < datas.size(); i++) {
            isSelected.put(i, true);
            msgIdSelected.put(i, datas.get(i).getMsgId());
        }
        notifyDataSetChanged();
        onSelectStateChange();
    }

    //反选
    public void selectNo() {
        for (int i = 0; i < datas.size(); i++) {
            isSelected.put(i, false);
            msgIdSelected.put(i, "");
        }
        notifyDataSetChanged();
        onSelectStateChange();
    }


    static class ViewHolder {
        @Bind(R.id.cbSelect)
        CheckBox mCbSelect;
        @Bind(R.id.profile_image)
        ImageView mProfileImage;
        @Bind(R.id.id_textViewMessage1)
        TextView mIdTextViewMessage1;
        @Bind(R.id.id_textViewMessage2)
        TextView mIdTextViewMessage2;
        @Bind(R.id.idReject)
        TextView mIdReject;
        @Bind(R.id.idAccept)
        TextView mIdAccept;
        @Bind(R.id.idStatus)
        TextView mIdStatus;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void viewHolderClickListener(final SystemMessage.MsgListBean message,
                                            final ClickListener listener,
                                            final UserCancleApplayListener mUserCancleApplayListener) {
            //管理员拒绝
            mIdReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.reject(message);
                    }
                }
            });
            //管理员同意
            mIdAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.accept(message);
                    }
                }
            });

            //用户发送取消等待申请
            mIdStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUserCancleApplayListener == null) {
                        return;
                    }
                    if (!"取消等待".equals(mIdStatus.getText())) {
                        return;
                    }
                    if (message.getHostRealId() == null) {
                        return;
                    }
                    mUserCancleApplayListener.userCancleApplay(message.getHostRealId(), message.getFromUser().getId());


                }
            });

        }

        public String dealWithMessage(SystemMessage.MsgListBean message) {
            String detail = "";
            //当前用户是管理员
            if (Constant.USERID == null || message == null) {
                return detail;
            }
//            if (!StringUtil.isEmpty(message.getDetail())) {
//                detail = message.getDetail();
//                return detail;
//            }

            if (message.getFromUser() != null && !StringUtil.isEmpty(message.getFromUser().getId())
                    && Constant.USERID.equals(message.getFromUser().getId())) { //主动
                //消息类型(1001:“创建家人申请”,1002:“转让管理员”, 2001:“分享数据”,2002:“取消分享数据”,
                // 2003:“家人授权”,2004:“取消家人授权”,20005:“用户解绑主机”, 20006:“管理员解绑主机”)
                switch (message.getMsgType()) {
                    case 1001:
                        if (message.getExtra() != null) {  //applyStatus: 1、主动 2、被动
                            if (message.getExtra().getApplyStatus() == 1) {
                                detail = message.getToUser().getName() + "申请加入您的主机";
                            } else {
                                detail = "邀请" + message.getToUser().getName() + "加入" + message.getHostName();
                            }
                            /** applyStatus: 1、用户申请 2、管理员分享
                             // status : 0、待确认     1、用户同意   2、用户拒绝
                             //          3、用户取消  　4、管理员同意 5、管理员拒绝
                             //          6、管理员取消
                             */
                            // modify  by sunzhibin
                            if (message.getExtra().getStatus() == 0) {
                                if (message.getExtra().getApplyStatus() == 1) { //用户申请
                                    mIdAccept.setVisibility(View.VISIBLE);
                                    mIdReject.setVisibility(View.VISIBLE);
                                    mIdStatus.setVisibility(View.GONE);
                                } else { //管理员分享
                                    mIdAccept.setVisibility(View.GONE);
                                    mIdReject.setVisibility(View.GONE);
                                    mIdStatus.setVisibility(View.VISIBLE);
                                    mIdStatus.setText(Constant.getApplyStatus(message.getExtra().getStatus()));

                                }

                            } else {
                                mIdAccept.setVisibility(View.GONE);
                                mIdReject.setVisibility(View.GONE);
                                mIdStatus.setVisibility(View.VISIBLE);
                                mIdStatus.setText(Constant.getApplyStatus(message.getExtra().getStatus()));
                            }
                        }
                        break;
                    case 1002:
                        detail = "我将管理员权限转让给" + message.getToUser().getName();
                        break;
                    case 2001:
                        detail = "对" + message.getToUser().getName()+"分享了健康数据";
                        break;
                    case 2002:
                        detail = "我取消了对" + message.getToUser().getName() + "的数据分享";
                        break;
                    case 2003:
                        detail = "我修改了" + message.getToUser().getName() + "的权限";
                        break;
                    case 2004:
                        detail = "我取消了" + message.getToUser().getName() + "的权限";
                        break;
                    case 20005: //,20005:“用户解绑主机”, 20006:“管理员解绑主机”)
                        detail = message.getToUser().getName() + "退出了主机" + message.getHostName();
                        break;
                    case 20006:
                        detail = "您退出了主机" + message.getHostName();
                        break;
                }
            } else if (message.getFromUser() != null) { //被动
                //消息类型(1001:“创建家人申请”,1002:“转让管理员”, 2001:“分享数据”,2002:“取消分享数据”,
                // 2003:“家人授权”,2004:“取消家人授权”,20005:“用户解绑主机”, 20006:“管理员解绑主机”)
                switch (message.getMsgType()) {
                    case 1001:
                        if (message.getExtra() != null) {
                            if (message.getExtra().getApplyStatus() == 1) {
                                detail = "我申请加入" + message.getHostName();
                            } else {
                                detail = "管理员邀请我加入" + message.getHostName();
                            }

                            if (message.getExtra().getStatus() == 0) {
                                if (message.getExtra().getApplyStatus() == 1) {
                                    mIdAccept.setVisibility(View.GONE);
                                    mIdReject.setVisibility(View.GONE);
                                    mIdStatus.setVisibility(View.VISIBLE);
//                            viewHolder.mIdStatus.setText("已申请");
                                    mIdStatus.setText("取消等待");
                                    mIdStatus.setTextColor(Color.parseColor("#FE5248"));//红色

                                } else {
                                    mIdAccept.setVisibility(View.VISIBLE);
                                    mIdReject.setVisibility(View.VISIBLE);
                                    mIdStatus.setVisibility(View.GONE);
                                }
                            } else {
                                mIdAccept.setVisibility(View.GONE);
                                mIdReject.setVisibility(View.GONE);
                                mIdStatus.setVisibility(View.VISIBLE);
                                mIdStatus.setTextColor(Color.parseColor("#919191"));//灰色
                                mIdStatus.setText(Constant.getApplyStatus(message.getExtra().getStatus()));
                            }
                        }
                        break;
                    case 1002:
                        detail = message.getFromUser().getName() + "将管理员权限转让给我";
                        break;
                    case 2001:
                            detail = message.getFromUser().getName() + "对您分享了健康数据";
                        break;
                    case 2002:
                        detail = message.getFromUser().getName() + "取消了对我的数据分享";
                        break;
                    case 2003:
                        detail = message.getFromUser().getName() + "修改了我的权限";

                        break;
                    case 2004:
                        detail = message.getFromUser().getName() + "取消了我的权限";

                        break;
                    case 20005:  //,20005:“用户解绑主机”, 20006:“管理员解绑主机”)
                        detail = "您退出了主机" + message.getHostName();
                        break;
                    case 20006:
                        detail = "管理员" + message.getFromUser().getName() + "解绑了主机" + message.getHostName();
                        break;
                }
            } else {
                detail = message.getDetail();
            }
            return detail;
        }
    }

    public interface ClickListener {
        void accept(SystemMessage.MsgListBean message);

        void reject(SystemMessage.MsgListBean message);
    }


    public interface UserCancleApplayListener {
        void userCancleApplay(String hostId, String adminId);
    }

    public void setUserCancleApplayListener(UserCancleApplayListener userCancleApplayListener) {
        mUserCancleApplayListener = userCancleApplayListener;
    }

    private UserCancleApplayListener mUserCancleApplayListener;


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
    private HistoricalAlarmsAdapter.SelectStateLinstener mSelectStateLinstener;
    public void setSelectStateLinstener(HistoricalAlarmsAdapter.SelectStateLinstener l){
        mSelectStateLinstener=l;
    }

}
