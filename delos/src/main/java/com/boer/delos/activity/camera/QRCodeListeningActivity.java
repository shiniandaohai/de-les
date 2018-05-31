package com.boer.delos.activity.camera;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.utils.L;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

//生成二维码界面
public class QRCodeListeningActivity extends BaseListeningActivity implements OnDateSetListener {

    @Bind(R.id.id_editTextStartTime)
    EditText mIdEditTextStartTime;
    @Bind(R.id.id_editTextEndTime)
    EditText mIdEditTextEndTime;
    @Bind(R.id.id_imageViewQRCode)
    ImageView mIdImageViewQRCode;
    @Bind(R.id.id_linearLayoutTime)
    LinearLayout mIdLinearLayoutTime;
    @Bind(R.id.id_buttonQR)
    Button mIdButtonQR;
    TimePickerDialog mDialogAll;

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    @Bind(R.id.id_textViewNotice)
    TextView mIdTextViewNotice;
    private EditText mEditTextDate;
    private String SIP = "";
    private int screenWidth = 0;//屏幕宽度

    //开始时间的时间戳
    private long startTime = 0;
    //结束时间的时间戳
    private long endTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        initTopBar("生成二维码", null, true, false);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;//获取屏幕宽度

        Bundle bundle = this.getIntent().getExtras();
        SIP = bundle.getString("SIP");


        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("时间选择")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(15)
                .build();
        mIdEditTextStartTime.setFocusable(false);
        mIdEditTextEndTime.setFocusable(false);
        mEditTextDate = mIdEditTextStartTime;
        //点击开始时间
        mIdEditTextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextDate = mIdEditTextStartTime;
                mDialogAll.show(getSupportFragmentManager(), "all");
            }
        });

        mIdEditTextEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextDate = mIdEditTextEndTime;
                mDialogAll.show(getSupportFragmentManager(), "all");
            }
        });

        mIdButtonQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdEditTextStartTime.getText().toString().equals("") || mIdEditTextEndTime.getText().toString().equals("")) {
                    Toast.makeText(QRCodeListeningActivity.this,"请选择时间",Toast.LENGTH_LONG).show();
                } else if(startTime>endTime) {//开始时间比结束时间靠后
                    Toast.makeText(QRCodeListeningActivity.this,"开始时间比结束时间大，请重新选择",Toast.LENGTH_LONG).show();
                }else{
                        toastUtils.showProgress("正在生成二维码...");
                        final String filePath = getFileRoot(QRCodeListeningActivity.this) + File.separator
                                + "qr_" + mIdEditTextStartTime.getText().toString().replace(" ", "").replace("-","").replace(":","") + ".jpg";

                        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
                        final String data = SIP + mIdEditTextStartTime.getText().toString().replace(" ", "").replace("-","").replace(":","") + mIdEditTextEndTime.getText().toString().replace(" ", "").replace("-","").replace(":","");
                        L.e("data" + data);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean success = QRCodeUtil.createQRImage(data, screenWidth - 150, screenWidth - 150,
                                        null,
                                        filePath);

                                if (success) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mIdLinearLayoutTime.setVisibility(View.GONE);
                                            mIdImageViewQRCode.setVisibility(View.VISIBLE);
                                            mIdImageViewQRCode.setImageBitmap(BitmapFactory.decodeFile(filePath));
                                            mIdButtonQR.setVisibility(View.GONE);
                                            mIdTextViewNotice.setText("二维码已生成，保存在:" + filePath);
                                            toastUtils.dismiss();
                                        }
                                    });
                                }
                            }
                        }).start();

                    }
                }

        });

    }

    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        if(mEditTextDate == mIdEditTextStartTime){
            startTime = millseconds;
        }else if(mEditTextDate == mIdEditTextEndTime){
            endTime = millseconds;
        }
        mEditTextDate.setText(getDateToString(millseconds));
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }
}
