package com.boer.delos.view.customDialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.boer.delos.R;
import com.boer.delos.utils.DensityUitl;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: FurnitureActivity区域编辑是的弹窗
 * @CreateDate: 2017/2/21 0021 09:46
 * @Modify:
 * @ModifyDate:
 */


public class EditAreaDialog extends DialogFragment {
    @Bind(R.id.ll_edit)
    LinearLayout llEdit;
    @Bind(R.id.ll_delete)
    LinearLayout llDelete;
    @Bind(R.id.ll_add_new_device)
    LinearLayout llAddNewDevice;
    @Bind(R.id.add_have_device)
    LinearLayout addHaveDevice;

    private String areaId;
    private View rootView;

    public static EditAreaDialog newInstance(String areaId) {
        EditAreaDialog dialog = new EditAreaDialog();
        Bundle bundle = new Bundle();
        bundle.putString("areaId", areaId);
        dialog.setArguments(bundle);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.popup_area_edit, null);
        ButterKnife.bind(this, rootView);
        areaId = getArguments().getString("areaId");
//        setStyle(STYLE_NO_FRAME,);
        positionDialog();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void positionDialog() {
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        lp.y = DensityUitl.dip2px(getContext(), 94);
        lp.x = 0;
        lp.alpha = 0.7f;
        dialogWindow.setAttributes(lp);
    }

    public int dpToPx(float valueInDp) {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @OnClick({R.id.ll_edit, R.id.ll_delete, R.id.ll_add_new_device, R.id.add_have_device})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_edit:
                if (listener != null) {
                    listener.editAreaName();
                }
                break;
            case R.id.ll_delete:
                if (listener != null) {
                    listener.deleteAreaName();

                }
                break;
            case R.id.ll_add_new_device:
                if (listener != null) {
                    listener.areaAddNewDevice();
                }
                break;
            case R.id.add_have_device:
                if (listener != null) {
                    listener.areaAddHaveDevice();
                }
                break;
        }
        dismiss();
    }


    public interface AreaEditClickListener {
        void editAreaName();

        void deleteAreaName();

        void areaAddNewDevice();

        void areaAddHaveDevice();
    }

    private AreaEditClickListener listener;

    public void setListener(AreaEditClickListener listener) {
        this.listener = listener;
    }
}
