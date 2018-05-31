package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.SelectPhoneAdapter;

import java.util.List;

/**
 * @author PengJiYang
 * @Description: 家人申请登录界面，选择管理员手机号popupWindow
 * create at 2016/6/3 11:21
 *
 */
public class SelectPhonePopUpWindow extends PopupWindow implements View.OnClickListener{

    private ListView lvSelectPhone;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private List<String> phoneList;
    private SelectPhoneAdapter adapter;
    private ClickResultListener listener;
    private String phoneNumber = "";

    public SelectPhonePopUpWindow(Context context, List<String> phoneList, ClickResultListener listener) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_select_phone, null);
        this.phoneList = phoneList;
        this.listener = listener;

        setContentView(view);
        setProperty();
        initView();
    }

    private void setProperty() {
        // 设置弹窗体宽度，高度
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        TextView tvHomeApplyDisagree = (TextView) view.findViewById(R.id.tvHomeApplyDisagree);
        TextView tvHomeApplyAgree = (TextView) view.findViewById(R.id.tvHomeApplyAgree);
        lvSelectPhone = (ListView) view.findViewById(R.id.lvSelectPhone);
        adapter = new SelectPhoneAdapter(context, phoneList);
        lvSelectPhone.setAdapter(adapter);
        lvSelectPhone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                phoneNumber = phoneList.get(position);
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();
            }
        });

        tvHomeApplyDisagree.setOnClickListener(this);
        tvHomeApplyAgree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvHomeApplyDisagree:
                dismiss();
                break;
            case R.id.tvHomeApplyAgree:
                if("".equals(phoneNumber)) {
                    listener.result(phoneList.get(0));
                } else {
                    listener.result(phoneNumber);
                }
                dismiss();
                break;
        }
    }

    public interface ClickResultListener {
        void result(String phone);
    }

}
