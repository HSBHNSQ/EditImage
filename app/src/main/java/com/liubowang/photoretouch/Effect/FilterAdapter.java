package com.liubowang.photoretouch.Effect;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liubowang.photoretouch.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/11/28.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {


    private Context context;
    private List<FilterModel> filterModels = new ArrayList<>();
    private String[] colors = getColors();
    public FilterAdapter(Context context){
        super();
        this.context = context;
        init(context);
    }

    private void init(Context context){
        creatFilterModels();
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_filter_item,parent,false);
        FilterHolder holder = new FilterHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, int position) {
        FilterModel filterModel = filterModels.get(position);
        holder.bind(filterModel);
    }

    @Override
    public int getItemCount() {
        return filterModels.size();
    }


    class FilterHolder extends RecyclerView.ViewHolder {

        private ImageView thumbImageView;
        private TextView titleTextView;
        private FilterModel filterModel;

        public FilterHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.iv_thumb_image_fiv);
            titleTextView = itemView.findViewById(R.id.tv_name_fiv);
            itemView.setOnClickListener(clickListener);
        }
        public void bind(FilterModel filterModel){
            this.filterModel = filterModel;
            titleTextView.setText(filterModel.title);
            int position = getPosition();
            if (position == 0){
                titleTextView.setBackgroundColor(Color.parseColor("#191919"));
            }else {
                int index = (position - 1) % (colors.length);
                titleTextView.setBackgroundColor(Color.parseColor(colors[index]));
            }
            new LoadThumbTask().execute(filterModel.thumbPath);
        }

        private Bitmap getBmp(String path){
            InputStream is = null;
            try {
                is = context.getAssets().open(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return BitmapFactory.decodeStream(is);
        }

        class LoadThumbTask extends AsyncTask<String,Void,Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String path = strings[0];
                return getBmp(path);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                thumbImageView.setImageBitmap(bitmap);
            }
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterChangeListener != null){
                    filterChangeListener.onFilterChanged(filterModel);
                }
            }
        };
    }



    private void creatFilterModels(){
        filterModels.clear();
        List<FilterModel.FilterType> types = getFilterTypes();
        List<String> names = getFilterTypeNames();
        for (int i = 0; i < types.size(); i ++){
            FilterModel.FilterType type = types.get(i);
            String name = names.get(i);
            FilterModel model = new FilterModel(name,type,null);
            model.thumbPath = "thumbs/" + name.toLowerCase() + ".png";
            model.rendererType = FilterFactory.getRendererType(model);
            filterModels.add(model);
            Log.d("thumbsPath:",model.thumbPath);
        }
    }

    public interface OnFilterChangeListener {
        void onFilterChanged(FilterModel filterModel);
    }

    private OnFilterChangeListener filterChangeListener;

    public void setFilterChangeListener(OnFilterChangeListener filterChangeListener) {
        this.filterChangeListener = filterChangeListener;
    }

    private List<FilterModel.FilterType> getFilterTypes(){
        List<FilterModel.FilterType> list = new ArrayList<>();
        list.add(FilterModel.FilterType.NONE);
        list.add(FilterModel.FilterType.MONEY);
        list.add(FilterModel.FilterType.BLUR);
        list.add(FilterModel.FilterType.PAINTING);
        list.add(FilterModel.FilterType.LICHTENSTEIN);
        list.add(FilterModel.FilterType.CROSSHATCH);
        list.add(FilterModel.FilterType.CONTRAST);
        list.add(FilterModel.FilterType.CT);
        list.add(FilterModel.FilterType.MOSAIC);
        list.add(FilterModel.FilterType.EDGE);
        list.add(FilterModel.FilterType.INVERT);
        list.add(FilterModel.FilterType.GRAY);
        list.add(FilterModel.FilterType.LOMO);
        list.add(FilterModel.FilterType.OLDPHOTO);
        list.add(FilterModel.FilterType.BLACKWHITE);
        list.add(FilterModel.FilterType.SKETCH);
        list.add(FilterModel.FilterType.CRAYON);
        list.add(FilterModel.FilterType.COOL);
        list.add(FilterModel.FilterType.INKWELL);
//        list.add(FilterModel.FilterType.MORE);
        return list;
    }
    private List<String> getFilterTypeNames(){
        List<String> list = new ArrayList<>();
        list.add("NONE");
        list.add("MONEY");
        list.add("BLUR");
        list.add("PAINTING");
        list.add("LICHTENSTEIN");
        list.add("CROSSHATCH");
        list.add("CONTRAST");
        list.add("CT");
        list.add("MOSAIC");
        list.add("EDGE");
        list.add("INVERT");
        list.add("GRAY");
        list.add("LOMO");
        list.add("OLDPHOTO");
        list.add("BLACKWHITE");
        list.add("SKETCH");
        list.add("CRAYON");
        list.add("COOL");
        list.add("INKWELL");
//        list.add(context.getString(R.string.ei_more));
        return list;
    }
    private static String[] getColors(){
        String[] colors = new String[12];
        colors[0] = "#DD0F4FB2";
        colors[1] = "#DD5E0C76";
        colors[2] = "#DDBF3101";
        colors[3] = "#DDBD0A45";
        colors[4] = "#DD0F6A95";
        colors[5] = "#DD8D0D4F";
        colors[6] = "#DD2C068F";
        colors[7] = "#DD05958D";
        colors[8] = "#DD8E560C";
        colors[9] = "#DD7F1905";
        colors[10] = "#DD170FB2";
        colors[11] = "#DD059523";
        return colors;
    }
}
