package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.SceneEditPopAdapter;
import com.boer.delos.adapter.addbatchdevice.AddBatchAreaAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.BasePopupWindow;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Area;
import com.boer.delos.model.Room;
import com.boer.delos.model.RoomResult;
import com.boer.delos.model.SceneManage;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.room.RoomController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:批量添加 pop
 * @CreateDate: 2017/3/24 0024 15:32
 * @Modify:
 * @ModifyDate:
 */


public class AddBatchAddRoomPopupWindow extends BasePopupWindow implements View.OnClickListener {
    private Context mContext;

    private TextView mAddArea;
    private TextView tvCancle;
    private TextView tvOk;
    private TextView edit_room_name;

    private GridView mGvRoom;
    private ListView listView_area;

    private List<Area> mAreaLists;
    private List<SceneManage> mList;

    private SceneEditPopAdapter adapterRoom;
    private AddBatchAreaAdapter adapterArea;

    private int checkPosition;
    private SceneManage sceneManage;
    private String name;
    private IObjectInterface<Room> listener;

    private Room mRoom;
    private int areaId = 1;
    private int checkAreaPos = -1;

    public AddBatchAddRoomPopupWindow(Context context, @LayoutRes int resId) {
        super(context, resId);
//        R.layout.popup_addbatch_room
        initView();
        initData();
        initListener();
    }

    @Override
    protected void setProperty() {
        super.setProperty();
    }

    @Override
    public int setPopWindowResId() {
        return 0;
    }

    private void initView() {
        mAddArea = getView(R.id.tv_add_area);
        mGvRoom = getView(R.id.gv_room);
        listView_area = getView(R.id.listView_area);
        edit_room_name = getView(R.id.edit_room_name);
        tvCancle = getView(R.id.tvCancle);
        tvOk = getView(R.id.tvOk);

        listView_area.setChoiceMode(CHOICE_MODE_SINGLE);
    }

    private void initData() {

        mList = new ArrayList<>();
        mAreaLists = new ArrayList<>();

        mList.addAll(Constant.sceneTypeList());

        adapterRoom = new SceneEditPopAdapter(0, -1, mContext);
        adapterRoom.setDatas(mList);
        mGvRoom.setAdapter(adapterRoom);

        adapterArea = new AddBatchAreaAdapter(mContext, mAreaLists, R.layout.item_addbatch_popup_area);
        listView_area.setAdapter(adapterArea);

    }

    private void initListener() {
        edit_room_name.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        mAddArea.setOnClickListener(this);
        mGvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkPosition = position;
                sceneManage = mList.get(position);

                areaId = 0;
                mAreaLists.clear();
                Area area = new Area();
                area.setName("新区域");
                area.setAreaId("" + areaId++);
                mAreaLists.add(area);

                edit_room_name.setText(mContext.getResources().getString(sceneManage.getItemName()));
                adapterRoom.setImageView(checkPosition);
                adapterRoom.notifyDataSetChanged();
            }
        });

        checkAreaPos = listView_area.getCheckedItemPosition();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_room_name:
                edit_room_name.setCursorVisible(true);
                break;
            case R.id.tvCancle:
                dismiss();
                break;
            case R.id.tvOk:
                //创建房间
                name = edit_room_name.getText().toString();
                if (StringUtil.isEmpty(name)) {
                    ToastHelper.showShortMsg("请输入房间名");
                    return;
                }
                if (StringUtil.isEmpty(name)) {
                    ToastHelper.showShortMsg("请输入房间名称");
                    return;
                }
                if (name.length() > 3) {
                    BaseApplication.showToast("长度不能大于4");
                    return;
                }
                if (checkPosition == -1) {
                    BaseApplication.showToast("请选择房间类型");
                    return;
                }
                if (mRoom == null) {
                    mRoom = new Room(name);
                    mRoom.setType(mContext.getString(sceneManage.getType()));
                    if (mAreaLists.size() == 0) {
                        Area area = new Area();
                        area.setName("新区域");
                        area.setAreaId("" + 1);
                    }
                    mRoom.setAreas(mAreaLists);
                }

                if (checkAreaPos == -1) {
                    checkAreaPos = 0;
                }
                listener.onClickListenerOK(mRoom, checkAreaPos, null);
                break;
            case R.id.tv_add_area:
                if (mRoom == null) {
                    if (StringUtil.isEmpty(name)) {
                        ToastHelper.showShortMsg("请输入房间名");
                        return;
                    }
                    if (StringUtil.isEmpty(name)) {
                        ToastHelper.showShortMsg("请输入房间名称");
                        return;
                    }
                    if (name.length() > 3) {
                        BaseApplication.showToast("长度不能大于4");
                        return;
                    }
                    if (checkPosition == -1) {
                        BaseApplication.showToast("请选择房间类型");
                        return;
                    }
                    mRoom = new Room(edit_room_name.getText().toString());

                }
                //添加区域
                Area area = new Area();
                area.setName("新区域");
                area.setAreaId("" + areaId++);
                area.setRoomId(mRoom.getRoomId());
                mAreaLists.add(area);
                mRoom.setAreas(mAreaLists);

                addRoom(mRoom);
                break;
        }
    }

    public void setListener(IObjectInterface<Room> listener) {
        this.listener = listener;
    }

    private void addRoom(Room room) {
        RoomController.getInstance().updateRoom(mContext, room, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                RoomResult result = GsonUtil.getObject(json, RoomResult.class);
                if (result.getRet() == 0) {
                    mRoom.setRoomId(result.getResponse().getRoomId());
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }
}
