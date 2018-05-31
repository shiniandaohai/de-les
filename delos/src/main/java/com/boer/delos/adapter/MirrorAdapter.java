package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.SmartMirror;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author gaolong
 */
public class MirrorAdapter extends BaseAdapter {


    private List<SmartMirror> datas;
    private Context context;
    private IClickResult iClickResult;

    public MirrorAdapter(Context context, List<SmartMirror> list) {
        datas = list;
        this.context = context;
    }

    @Override
    public int getCount() {

        return datas.size();

    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.item_smart_mirror_info, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SmartMirror smartMirror = datas.get(position);

        viewHolder.tvMirroId.setText(smartMirror.getId());
        viewHolder.tvMirroRename.setText(smartMirror.getRemark());
        viewHolder.tvMirroStandard.setText(smartMirror.getSpecification());
        viewHolder.tvMirroType.setText(smartMirror.getModel());
        viewHolder.btnOffLine.setVisibility(View.VISIBLE);

        viewHolder.btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickResult.reName(viewHolder.tvMirroRename.getText().toString(), viewHolder.tvMirroId.getText().toString(),position);
            }
        });

        viewHolder.btnOffLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickResult.offLine( viewHolder.tvMirroId.getText().toString(),position);
            }
        });

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.tv_mirro_standard)
        TextView tvMirroStandard;
        @Bind(R.id.tv_mirro_type)
        TextView tvMirroType;
        @Bind(R.id.tv_mirro_id)
        TextView tvMirroId;
        @Bind(R.id.tv_mirro_rename)
        TextView tvMirroRename;
        @Bind(R.id.btn_rename)
        Button btnRename;
        @Bind(R.id.btn_off_line)
        Button btnOffLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public interface IClickResult {

         void reName(String reName, String id,int pos);

         void offLine(String id, int pos);


    }

    public void setClickListener(IClickResult iClickResult) {

        this.iClickResult = iClickResult;

    }


}
