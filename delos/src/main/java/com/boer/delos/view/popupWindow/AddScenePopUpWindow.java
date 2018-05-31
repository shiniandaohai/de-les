package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.SceneEditPopAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.SceneManage;
import com.boer.delos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 场景添加 新建房间 popup
 * create at 2016/4/12 9:55
 */
public class AddScenePopUpWindow extends PopupWindow implements View.OnClickListener {
    private static final int TAG = 2;
    private ClickResultListener listener;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private TextView evCustomName;
    private TextView tvCancle;
    private TextView tvOk;
    private GridView gvScencePopup;
    private List<SceneManage> list;
    private SceneEditPopAdapter adapter;
    public String name;
    public int checkPosition = -1;
    private ToastUtils toastUtils;

    public SceneManage getSceneManage() {
        return sceneManage;
    }

    private SceneManage sceneManage;

    public AddScenePopUpWindow(Context context, ClickResultListener clickResultListener) {
        this.context = context;
        this.listener = clickResultListener;

        toastUtils = new ToastUtils(context);
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_add_scence, null);

        setContentView(view);
        setProperty();
        initView();
        initData();
        initListener();
    }

    private void setProperty() {
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        this.evCustomName = (TextView) view.findViewById(R.id.evCustomName);
        this.tvCancle = (TextView) view.findViewById(R.id.tvCancle);
        this.tvOk = (TextView) view.findViewById(R.id.tvOk);
        this.gvScencePopup = (GridView) view.findViewById(R.id.gvScenePopup);

       /* this.evCustomName.setCursorVisible(false);
        this.evCustomName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                if (!hasFocus) {
                    editText.setHint(editText.getTag().toString());
                } else {
                    editText.setText("");
//                    String hint = editText.getHint().toString();
//                    if (hint != null) {
//                        editText.setHint(null);
//                        editText.setTag(hint);
//                    }
                }
            }
        });*/
    }

    private void initData() {
        this.list = new ArrayList<>();
        this.list = Constant.sceneTypeList();
        adapter = new SceneEditPopAdapter(0, -1, context);
        adapter.setDatas(list);
        gvScencePopup.setAdapter(adapter);
    }

    private void initListener() {
        this.evCustomName.setOnClickListener(this);
        this.tvCancle.setOnClickListener(this);
        this.tvOk.setOnClickListener(this);
        gvScencePopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkPosition = position;
                sceneManage = list.get(position);
                evCustomName.setText(context.getResources().getString(sceneManage.getItemName()));
                adapter.setImageView(checkPosition);
                adapter.notifyDataSetChanged();
            }
        });


    }

    public int getCheckPosition() {
        return checkPosition;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.evCustomName:

                evCustomName.setCursorVisible(true);
                break;
            case R.id.tvCancle:
                dismiss();
                break;
            case R.id.tvOk:
                name = evCustomName.getText().toString();
                listener.ClickResult(TAG);
                break;
        }

    }

    public String getName() {
        return name;
    }

    public interface ClickResultListener {
        void ClickResult(int tag);
    }
}
