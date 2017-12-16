package com.liubowang.photoretouch.Main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.liubowang.photoretouch.R;

import java.util.List;

/**
 * Created by heshaobo on 2017/10/10.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {

    public List<String> itemList;
    private SettingsInterface mInterface;

    private SettingsAdapter(){ }

    public SettingsAdapter(SettingsInterface settingsInterface){
        mInterface = settingsInterface;
        init();
    }

    @Override
    public SettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.view_setting_item,parent,false);
        SettingViewHolder holder = new SettingViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SettingViewHolder holder, int position) {
        String title = itemList.get(position);
        holder.bind(title);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void init(){
        if (mInterface != null){
            itemList = mInterface.configItemList();
        }
    }


    class SettingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        public SettingViewHolder(View itemView) {
            super(itemView);
            textView =  itemView.findViewById(R.id.tv_setting);
            itemView.setOnClickListener(this);
        }

        public void bind(String title){
            textView.setText(title);
        }

        @Override
        public void onClick(View view) {
            if (mInterface != null)
            {
                mInterface.onItemClick(textView.getText().toString());
            }
        }
    }

    interface SettingsInterface {
        void onItemClick(String title);
        List<String> configItemList();
    }
}
