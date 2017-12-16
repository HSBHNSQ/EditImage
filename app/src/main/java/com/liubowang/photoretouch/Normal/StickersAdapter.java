package com.liubowang.photoretouch.Normal;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.liubowang.photoretouch.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/11/21.
 */

public class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.StickersViewHolder> {

    private static final String TAG = StickersAdapter.class.getSimpleName();
    private List<String> dataSource = new ArrayList<>();
    private AssetManager asssetManager;
    private StickersClickListener stickersClickListener;

    private StickersAdapter (){super();};

    public StickersAdapter(AssetManager am){
        super();
        asssetManager = am;
        initDataSource();
    }


    private void initDataSource (){
        dataSource = new ArrayList<>();
        try {
            String[] list = asssetManager.list("sticker");
            for (String name : list){
                String newName = "sticker/"+name;
                dataSource.add(newName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStickersClickListener(StickersClickListener stickersClickListener) {
        this.stickersClickListener = stickersClickListener;
    }

    @Override
    public StickersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.view_sticker_item,parent,false);
        StickersViewHolder holder = new StickersViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(StickersViewHolder holder, int position) {
        String stickerPath = dataSource.get(position);
        holder.bindItem(stickerPath);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    class StickersViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private String stickerPath;
        public StickersViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_sticker_siv);
            itemView.setOnClickListener(itemClickListner);
        }

        public void bindItem(String stickerPath){
            this.stickerPath = stickerPath;
            LoadStickerTask task = new LoadStickerTask();
            task.execute(stickerPath);
//            imageView.setImageBitmap(getBmp(stickerPath));
        }

        private View.OnClickListener itemClickListner = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stickersClickListener != null){
                    stickersClickListener.stickerClick(getBmp(stickerPath));
                }
            }
        };

        private Bitmap getBmp(String path){
            InputStream is = null;
            try {
                is = asssetManager.open(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return BitmapFactory.decodeStream(is);
        }

        class LoadStickerTask extends AsyncTask<String,Void,Bitmap>{

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



    interface StickersClickListener {
        void stickerClick(Bitmap stickerBmp);
    }

}
