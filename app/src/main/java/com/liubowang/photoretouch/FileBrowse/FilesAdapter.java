package com.liubowang.photoretouch.FileBrowse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Text.TextTypeModel;
import com.liubowang.photoretouch.Utils.BitmpUtil;
import com.liubowang.photoretouch.Utils.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/11/3.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesHolder> {

    private static final String TAG = FilesAdapter.class.getSimpleName();
    private static int INDEX = 0;
    private boolean isEdit = false;
    private Context context;
    public OnFileItemListener onFileItemListener;
    public List<FileInfo> fileList = new ArrayList<>();
    public List<FileInfo> selectedList = new ArrayList<>();
    private FilesAdapter(){ super();}

    public FilesAdapter(OnFileItemListener listener){
        super();
        this.onFileItemListener = listener;
        initData();
    }
    /*
    * 文件过滤
    * */
    private FileFilter fileFilter= new FileFilter() {
        @Override
        public boolean accept(File file) {
            String s = file.getName().toLowerCase();
            if (s.endsWith(".png")|| s.endsWith(".jpg")||s.endsWith(".bmp")){
                return true;
            }
            return false;
        }
    };

    private void initData(){
        String resultPath = FileUtil.getPicturesResultPath();
        File bmpFile = new File(resultPath);
        File[] files = bmpFile.listFiles(fileFilter);
        fileList = new ArrayList<>();
        if (files != null ){
            for (int i = 0 ;i < files.length; i ++){
                File file = files[i];
                FileInfo info = new FileInfo(file);
                fileList.add(info);
            }
        }
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        for (FileInfo info:
             selectedList) {
            info.isSelected = false;
        }
        selectedList.clear();
        notifyDataSetChanged();
    }

    public void toDelete(){
        for (FileInfo info: selectedList) {
            if (info.file.exists()) {
                if (info.file.delete()) {
                    fileList.remove(info);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean removeItem(FileInfo info){
        int index = fileList.indexOf(info);
//        FileInfo info = fileList.get(index);
        if (info.file.exists()) {
            if (info.file.delete()) {
                fileList.remove(info);
                notifyItemRemoved(index);
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }



    @Override
    public FilesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        if (this.context == null){
            this.context = ctx;
        }
        View view = LayoutInflater.from(ctx).inflate(R.layout.view_file_item,parent,false);
        FilesHolder holder = new FilesHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FilesHolder holder, int position) {
        FileInfo info = fileList.get(position);
        holder.bind(info);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }


    class FilesHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageButton deleteButton;
        private ImageButton selectedButton;
        private TextView textView;
        private FileInfo fileInfo;
        public FilesHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_file_image_files);
            deleteButton = itemView.findViewById(R.id.ib_delete_button_files);
            selectedButton = itemView.findViewById(R.id.ib_selected_button_files);
            textView = itemView.findViewById(R.id.tv_file_title_files);
            itemView.setOnClickListener(clickListener);
            deleteButton.setOnClickListener(clickListener);
            selectedButton.setOnClickListener(clickListener);
        }

        public void bind(FileInfo fileInfo){
            this.fileInfo = fileInfo;
            if (isEdit){
                deleteButton.setVisibility(View.INVISIBLE);
                selectedButton.setVisibility(View.VISIBLE);
                if (fileInfo.isSelected){
                    selectedButton.setImageResource(R.drawable.selected);
                }else {
                    selectedButton.setImageResource(R.drawable.not_selected);
                }
            }else {
                deleteButton.setVisibility(View.VISIBLE);
                selectedButton.setVisibility(View.INVISIBLE);
            }
            textView.setText(fileInfo.fileName);
            if (fileInfo.thumUrl != null){
                Bitmap bitmap = BitmapFactory.decodeFile(fileInfo.thumUrl);
                imageView.setImageBitmap(bitmap);
            }else {
                new FileThumbTask().execute();
            }

        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.ib_delete_button_files){
                    if (onFileItemListener != null){
                        onFileItemListener.onItemDeleteButtonClick(fileInfo);
                    }
                }else if (view.getId() == R.id.ib_selected_button_files){
                    toAddSelectedListFile();
                }else {
                    if (isEdit){
                        toAddSelectedListFile();
                    }else {
                        if (onFileItemListener != null){
                            onFileItemListener.onItemDidClick(fileInfo);
                        }
                    }
                }
            }
        };

        private void toAddSelectedListFile(){
            if (selectedList.contains(fileInfo)){
                fileInfo.isSelected = false;
                selectedButton.setImageResource(R.drawable.not_selected);
                selectedList.remove(fileInfo);
            }else {
                fileInfo.isSelected = true;
                selectedButton.setImageResource(R.drawable.selected);
                selectedList.add(fileInfo);
            }
            if (onFileItemListener != null){
                onFileItemListener.onItemHasSelectedClick(fileInfo);
            }
        }
        private class FileThumbTask extends AsyncTask<Void,Void,Bitmap>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                imageView.setImageBitmap(null);
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                if (!fileInfo.file.exists()){
                    return  null;
                }

                if (fileInfo.thumUrl == null){
                    Bitmap bitmap = BitmapFactory.decodeFile(fileInfo.fileUrl);
                    bitmap = BitmpUtil.scaleBitmpToMaxSize(bitmap,200);
                    if (bitmap == null) return null;
                    String filePath = FileUtil.getTmpPath() + INDEX + ".png";
                    INDEX++;
                    File outPutFile = new File(filePath);
                    FileUtil.writeBitmap(outPutFile,bitmap);
                    fileInfo.thumUrl = filePath;
                    return bitmap;
                }else {
                    Bitmap bitmap = BitmapFactory.decodeFile(fileInfo.thumUrl);
                    return bitmap;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    interface OnFileItemListener{
        void onItemDidClick(FileInfo fileInfo);
        void onItemDeleteButtonClick(FileInfo fileInfo);
        void onItemHasSelectedClick(FileInfo fileInfo);
    }


}
