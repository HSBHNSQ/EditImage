package com.liubowang.photoretouch.Effect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.FileUtil;

import java.io.File;

/**
 * Created by Ing. Oscar G. Medina Cruz on 09/11/2016.
 */

public class SaveBitmapDialog extends DialogFragment {

    private OnSaveBitmapListener onSaveBitmapListener;

    // VARS
    private Bitmap mPreviewBitmap;
    private String mAppName;
    public SaveBitmapDialog(){}

    public static SaveBitmapDialog newInstance(){
        return new SaveBitmapDialog();
    }

    // METHODS
    public void setPreviewBitmap(Bitmap bitmap){
        this.mPreviewBitmap = bitmap;
    }
    public void setAppName(String appName){
        this.mAppName = appName;
    }
    // LISTENER
    public void setOnSaveBitmapListener(OnSaveBitmapListener onSaveBitmapListener){
        this.onSaveBitmapListener = onSaveBitmapListener;
    }

    public interface OnSaveBitmapListener{
        void onStartSave();
        void onSaveBitmapCompleted(boolean success,String bmpPath);
        void onSaveBitmapCanceled();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_save_bitmap, null);
        ImageView imageView = view.findViewById(R.id.iv_capture_preview);

        final TextInputEditText textInputEditText = view.findViewById(R.id.et_file_name);

        final String[] fileName = {System.currentTimeMillis() + ""};
        if (mPreviewBitmap != null)
            imageView.setImageBitmap(mPreviewBitmap);
        else
            imageView.setImageResource(R.color.colorAccent);
            textInputEditText.setText(fileName[0]);

             textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fileName[0] = charSequence.toString();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onSaveBitmapListener != null){
                            onSaveBitmapListener.onStartSave();
                        }
                        textInputEditText.setText(fileName[0]);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                FileUtil.removeTmpFiles();
                                final String path = FileUtil.getPictureResultPathWithName(fileName[0],"jpg");
                                FileUtil.removeTmpFiles();
                                final boolean success = FileUtil.writeBitmap(new File(path),mPreviewBitmap,80);
                                if (onSaveBitmapListener != null){
                                    onSaveBitmapListener.onSaveBitmapCompleted(success,path);
                                }
                            }
                        }).start();

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onSaveBitmapListener != null)
                            onSaveBitmapListener.onSaveBitmapCanceled();
                        dismiss();
                    }
                });
        return builder.create();
    }

}
