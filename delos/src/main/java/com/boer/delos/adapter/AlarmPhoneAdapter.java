package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.AlarmPhone;
import com.boer.delos.utils.L;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.google.gson.Gson;

import java.util.List;

/**
 * @author XieQingTing
 * @Description: 报警电话列表适配器
 * create at 2016/5/17 10:56
 */
public class AlarmPhoneAdapter extends BaseSwipeAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<AlarmPhone> datas;
    private SwipeLayout currentExpandedSwipeLayout;
    private ItemClckListener mItemClckListener;

    private boolean isAdmin;

    public AlarmPhoneAdapter(Context context, ItemClckListener itemClckListener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mItemClckListener = itemClckListener;
    }


    public void setDatas(List<AlarmPhone> datas) {
        this.datas = datas;
        L.e("AlarmPhoneAdapter's datas=======" + new Gson().toJson(datas));
    }

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public AlarmPhone getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View convertView = inflater.inflate(R.layout.item_alarm_phone, null);
        return convertView;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        ImageView ivAlarm = (ImageView) convertView.findViewById(R.id.ivAlarm);
        TextView tvAlarmPhone = (TextView) convertView.findViewById(R.id.tvAlarmPhone);
        //滑动
        final SwipeLayout sl = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        LinearLayout mLinearLayoutDelete = (LinearLayout) convertView.findViewById(R.id.llDelete);
        mLinearLayoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClckListener.deleteItem(position);
                sl.close();
            }
        });
        ivAlarm.setImageResource(R.drawable.ic_personal_center_alarm);
        tvAlarmPhone.setText(getItem(position).getPhone());

    }

//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.item_alarm_phone, null);
//            viewHolder = new ViewHolder(convertView);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        viewHolder.viewHolderUpdate(getItem(position), position);
//        return convertView;
//    }

    class ViewHolder {
        private ImageView ivAlarm;
        private TextView tvAlarmPhone;
        private SwipeLayout mSwipeLayout;
        private LinearLayout mLinearLayoutDelete;

        public ViewHolder(View convertView) {

            ivAlarm = (ImageView) convertView.findViewById(R.id.ivAlarm);
            tvAlarmPhone = (TextView) convertView.findViewById(R.id.tvAlarmPhone);
            //滑动
            mSwipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
            mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            mSwipeLayout.setSwipeEnabled(isAdmin);
            mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, mSwipeLayout.findViewWithTag("Bottom2"));
            mLinearLayoutDelete = (LinearLayout) convertView.findViewById(R.id.llDelete);
            mSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    if (currentExpandedSwipeLayout != null && currentExpandedSwipeLayout != layout)
                        currentExpandedSwipeLayout.close(true);

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    currentExpandedSwipeLayout = layout;


                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onClose(SwipeLayout layout) {
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }
            });
        }

        public void viewHolderUpdate(AlarmPhone item, final int position) {
            mLinearLayoutDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClckListener.deleteItem(position);
                }
            });
            ivAlarm.setImageResource(R.drawable.ic_personal_center_alarm);
            tvAlarmPhone.setText(item.getPhone());


        }
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public interface ItemClckListener {
        public void deleteItem(int position);
    }
}
