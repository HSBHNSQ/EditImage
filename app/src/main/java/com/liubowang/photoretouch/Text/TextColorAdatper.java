package com.liubowang.photoretouch.Text;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.ColorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2018/1/17.
 */

public class TextColorAdatper extends RecyclerView.Adapter<TextColorAdatper.TextColorHolder> {


    private static final String TAG = TextColorAdatper.class.getSimpleName();

    private List<TextColorModel> modelList = new ArrayList<>();
    public TextColorModel currentColorModel;

    public TextColorAdatper(){
        super();
        init();
    }

    private void init(){
        modelList.clear();
        int[] colors = ColorUtil.getRandomColors(10);
        for (int i = 0; i < colors.length ; i ++){
            TextColorModel model = new TextColorModel();
            if (i == 0) {
                model.isSelected = true;
                currentColorModel = model;
            }
            model.mainColor = colors[i];
            model.secondColor = ColorUtil.getDifferentRandomColor(model.mainColor,50);
            modelList.add(model);
        }
    }

    public void changeColor(){
        init();
        notifyDataSetChanged();
    }

    @Override
    public TextColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_text_color_item,
                parent,false);
        TextColorHolder holder = new TextColorHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TextColorHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class TextColorHolder extends RecyclerView.ViewHolder{

        private CornerBgView bgView;
        private View selectedView;
        private TextColorModel colorModel;
        public TextColorHolder(View itemView) {
            super(itemView);
            bgView = itemView.findViewById(R.id.v_fore_color_view_tci);
            selectedView = itemView.findViewById(R.id.v_select_view_tci);
            itemView.setOnClickListener(clickListener);
        }

        public void bind(int position){
            colorModel = modelList.get(position);
            bgView.setBgColor(colorModel.mainColor);
            if (colorModel.isSelected){
                selectedView.setVisibility(View.VISIBLE);
            }else {
                selectedView.setVisibility(View.INVISIBLE);
            }
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldIndex = 0;
                if (currentColorModel != null){
                    currentColorModel.isSelected = false;
                    oldIndex = modelList.indexOf(currentColorModel);
                }
                if (currentColorModel == colorModel){
                    colorModel.secondColor = ColorUtil.getDifferentRandomColor(colorModel.mainColor,50);
                }
                currentColorModel = colorModel;
                colorModel.isSelected = true;
                selectedView.setVisibility(View.VISIBLE);
                notifyItemChanged(oldIndex);
                if (textColorListener != null){
                    textColorListener.onColorClcik(colorModel);
                }
            }
        };
    }

    private OnTextColorListener textColorListener;

    public void setTextColorListener(OnTextColorListener textColorListener) {
        this.textColorListener = textColorListener;
    }

    public interface OnTextColorListener{
        void onColorClcik(TextColorModel model);
    }
}
