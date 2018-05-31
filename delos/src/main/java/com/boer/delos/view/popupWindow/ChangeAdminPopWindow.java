package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.changeAdminPopAdapter;
import com.boer.delos.model.Family;
import com.boer.delos.model.Host;
import com.boer.delos.model.User;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/26 0026.
 */

public class ChangeAdminPopWindow extends PopupWindow {

    ListView lvChangeAdimnPopup;
    TextView tvChangeAdminPopupCancle;
    TextView tvChangeAdminPopupOk;
    TextView tvPopupTitle;

    private Context mContext;
    private Host mHost;
    private View view;
    private LayoutInflater inflater;
    private List<Family> familyList;
    private changeAdminPopAdapter adapter;
    private ResultCallBack resultCallBack;
    private User userChoise;

    public ChangeAdminPopWindow(Context context, Host host, ResultCallBack resultCallBack) {
        this.mContext = context;
        this.mHost = host;
        this.resultCallBack = resultCallBack;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.pop_change_adimn, null);
        setContentView(view);

        initView();
        setProperty();
        initListener();
        initData();
    }


    private void setProperty() {
        // 设置弹窗体宽度，高度
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        lvChangeAdimnPopup = (ListView) view.findViewById(R.id.lvChangeAdimnPopup);
        tvChangeAdminPopupCancle = (TextView) view.findViewById(R.id.tvChangeAdminPopupCancle);
        tvChangeAdminPopupOk = (TextView) view.findViewById(R.id.tvChangeAdminPopupOk);
        tvPopupTitle = (TextView) view.findViewById(R.id.tvPopupTitle);

//        lvChangeAdimnPopup.setChoiceMode(CHOICE_MODE_SINGLE);
    }

    private void initData() {
        if (mHost != null) {
            familyList=new ArrayList<>();
            familyList.addAll(mHost.getFamilies());
//            familyList = mHost.getFamilies();
        }
//        if (familyList != null && familyList.size() != 0) {
//            for (Family f : familyList) {
//                if (f.getAdmin() == 1) {
//                    familyList.remove(f);
//                    break;
//                }
//            }
//        }
        familyList.remove(0);
        familyList.remove(familyList.size()-1);
        adapter = new changeAdminPopAdapter(mContext, familyList);
        lvChangeAdimnPopup.setAdapter(adapter);
    }

    private void initListener() {
        tvChangeAdminPopupCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvChangeAdminPopupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<Integer> integerList = adapter.getIntegerList();
//                User user = null;
//                for (int i = 0; i < integerList.size(); i++) {
//                    if (integerList.get(i) != 1) {
//                        continue;
//                    }
//                    user = familyList.get(i).getUser();
//                    break;
//                }
                if (null == userChoise) {
                    new ToastUtils(mContext).showErrorWithStatus(mContext.getResources().getString(R.string.pop_change_admin));
                    return;
                }
                L.d("Myhome ChangeAdminPopWindow " + userChoise.getName());

                if (resultCallBack == null) return;
                resultCallBack.resultCallBack(userChoise.getId());

                dismiss();
//                adminId = Constant.USERID;
//                changeAdmin(adminId, user.getId()); //只有管理员才能更改用户权限，此处取当前用户ID

            }
        });

        lvChangeAdimnPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.check(position);
                userChoise = familyList.get(position).getUser();
            }
        });
    }


    public interface ResultCallBack {
        void resultCallBack(String newAdminId);
    }


}
