package com.liubowang.photoretouch.Template;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liubowang.photoretouch.Base.EIApplication;
import com.liubowang.photoretouch.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/11/30.
 */

public class TemplateAdatper extends RecyclerView.Adapter<TemplateAdatper.TemplateHolder> {


    private static final String TAG = TemplateAdatper.class.getSimpleName();
    private List<TemplateModel> temlateList = new ArrayList<>();
    private String[] colors = getColors();
    private AssetManager assetManager;
    private TemplateAdatper(){
        super();

    }

    public TemplateAdatper(AssetManager assetManager){
        super();
        this.assetManager = assetManager;
        initData();
    }

    public void initData(){
        temlateList = getDataSource();
    }

    @Override
    public TemplateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_template_item,parent,false);
        TemplateHolder holder = new TemplateHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TemplateHolder holder, int position) {
        holder.bind(temlateList.get(position));
    }

    @Override
    public int getItemCount() {
        return temlateList.size();
    }

    class TemplateHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameTextView;
        private TemplateModel model;

        public TemplateHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image_tiv);
            nameTextView = itemView.findViewById(R.id.tv_name_tiv);
            itemView.setOnClickListener(onClickListener);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (templateChangeListener != null){
                    templateChangeListener.onTemplateChanged(model);
                }
            }
        };

        public void bind(TemplateModel model){
            this.model = model;
            imageView.setImageBitmap(BitmapFactory.decodeFile(model.thumbUrl));
            nameTextView.setText(model.name);
            int index = temlateList.indexOf(model) % colors.length;
            nameTextView.setBackgroundColor(Color.parseColor(colors[index]));
            new LoadThumbTask().execute(model.thumbUrl);
        }
        private Bitmap getBmp(String path){
            InputStream is = null;
            try {
                is = assetManager.open(path);
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
                imageView.setImageBitmap(bitmap);
            }
        }
    }


    interface OnTemplateChangeListener {
        void onTemplateChanged(TemplateModel model);
    }

    private OnTemplateChangeListener templateChangeListener;

    public void setTemplateChangeListener(OnTemplateChangeListener templateChangeListener) {
        this.templateChangeListener = templateChangeListener;
    }

    private List<TemplateModel> getDataSource(){
        List<TemplateModel> list = new ArrayList<>();
        TemplateModel focus = new TemplateModel(EIApplication.getContext().getString(R.string.ei_FOCUS),
                                                Template.FOCUS.getInt(),null);
        focus.thumbUrl = "thumbs/normalTP.png";
        list.add(focus);
        TemplateModel black_white = new TemplateModel(EIApplication.getContext().getString(R.string.ei_GRAY),
                Template.GRAY.getInt(),null);
        black_white.thumbUrl = "thumbs/grayTP.png";
        list.add(black_white);
        TemplateModel painting = new TemplateModel(EIApplication.getContext().getString(R.string.ei_PAINTING),
                Template.PAINTING.getInt(),null);
        painting.thumbUrl = "thumbs/paintingTP.png";
        list.add(painting);
        TemplateModel corss = new TemplateModel(EIApplication.getContext().getString(R.string.ei_CORSSHATCH),
                Template.CORSSHATCH.getInt(),null);
        corss.thumbUrl = "thumbs/crosshatTP.png";
        list.add(corss);
        TemplateModel other = new TemplateModel(EIApplication.getContext().getString(R.string.ei_NONE),
                Template.NONE.getInt(),null);
        other.thumbUrl = "thumbs/normalTP.png";
        list.add(other);
        return list;
    }

    private static String[] getColors(){
        String[] colors = new String[5];
        colors[0] = "#DDBD0A45";
        colors[1] = "#DD0F4FB2";
        colors[2] = "#DD5E0C76";
        colors[3] = "#DD0F6A95";
        colors[4] = "#DDBF3101";
        return colors;
    }

}
