package com.liubowang.photoretouch.Text;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.liubowang.photoretouch.R;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2018/1/17.
 */

public class TextTypeAdapter extends RecyclerView.Adapter<TextTypeAdapter.TextViewHolder> {


    private static final String TAG = TextTypeAdapter.class.getSimpleName();
    private List<TextTypeModel> modelList = new ArrayList<>();
    private TextTypeModel currentTypeModel;
    private AssetManager assetManager;

    private TextTypeAdapter(){
        super();
    }

    public TextTypeAdapter(Context context){
        super();
        initData(context);
    }


    private void initData(Context context){
        assetManager = context.getAssets();
        modelList.clear();
        try {
            String[] list = assetManager.list("thumbs");

            for (int i = 0; i < list.length; i ++){
                String name = list[i];
                TextTypeModel model = new TextTypeModel();
                model.thumbPath = "thumbs/"+ name;
                model.className = "com.liubowang.photoretouch.Style.TextStyle" + i;
                if (i == 0){
                    model.isSelected = true;
                    currentTypeModel = model;
                }
                modelList.add(model);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_text_type_item,
                parent,false);
        TextViewHolder holder = new TextViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TextViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class TextViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private View selectedView;
        private TextTypeModel typeModel;
        public TextViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image_view_tti);
            selectedView = itemView.findViewById(R.id.v_select_view_tti);
            itemView.setOnClickListener(clickListener);
        }

        public void bind(int position){
            typeModel = modelList.get(position);
            if (typeModel.isSelected){
                selectedView.setVisibility(View.VISIBLE);
            }else {
                selectedView.setVisibility(View.INVISIBLE);
            }
            new LoadThumbTask().execute(typeModel.thumbPath);
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldIndex = 0;
                if (currentTypeModel != null){
                    currentTypeModel.isSelected = false;
                    oldIndex = modelList.indexOf(currentTypeModel);
                }
                currentTypeModel = typeModel;
                typeModel.isSelected = true;
                selectedView.setVisibility(View.VISIBLE);
                notifyItemChanged(oldIndex);
                if (textTypeListener != null){
                    textTypeListener.onTextTypeClick(typeModel);
                }
            }
        };

        private Bitmap getBmp(String path){
            if (assetManager == null || path == null || path.length() < 1){
                return null;
            }
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

    private OnTextTypeListener textTypeListener;

    public void setTextTypeListener(OnTextTypeListener textTypeListener) {
        this.textTypeListener = textTypeListener;
    }

    public interface OnTextTypeListener {
        void onTextTypeClick(TextTypeModel model);
    }

}
