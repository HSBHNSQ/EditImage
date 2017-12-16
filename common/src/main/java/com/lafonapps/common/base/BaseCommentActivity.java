package com.lafonapps.common.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;
import com.lafonapps.common.R;
import com.lafonapps.common.Common;
import android.support.v7.app.AlertDialog;
import java.util.List;

/**
 * Created by zhangzhanhe on 2017/9/22.
 */

public class BaseCommentActivity extends BaseActivity {
    private boolean mIsExit;
    private boolean isPingfen;
    //以下评分的方法

    /**
     * 弹出评分窗口,请求评分    类方法
     */
    public static void scoreView(final Context context){
        new AlertDialog.Builder(context).setTitle("☺☺☺☺☺")//设置对话框标题

                .setMessage(context.getString(R.string.score_app))//设置显示的内容

                .setPositiveButton(context.getString(R.string.hard_top_finish),new DialogInterface.OnClickListener() {//添加确定按钮



                    @Override

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        // TODO Auto-generated method stub
                        if (hasAnyMarketInstalled(context)){
                            Uri uri = Uri.parse("market://details?id="+context.getPackageName());
                            Intent intentpf = new Intent(Intent.ACTION_VIEW,uri);
                            intentpf.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentpf);
                        }else {
                            Toast.makeText(context, context.getString(R.string.toast), Toast.LENGTH_SHORT).show();
                        }

                    }

                }).setNegativeButton(context.getString(R.string.hard_top_cancel),new DialogInterface.OnClickListener() {//添加返回按钮



            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub



            }

        }).show();//在按键响应事件中显示此对话框
    }

    /**
     * 弹出评分窗口,请求评分    对象方法
     */
    public void scoreView(){

        new AlertDialog.Builder(BaseCommentActivity.this).setTitle("☺☺☺☺☺")//设置对话框标题

                .setMessage(this.getString(R.string.score_app))//设置显示的内容

                .setPositiveButton(this.getString(R.string.hard_top_finish),new DialogInterface.OnClickListener() {//添加确定按钮



                    @Override

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        // TODO Auto-generated method stub
                        if (hasAnyMarketInstalled(BaseCommentActivity.this)){
                            Uri uri = Uri.parse("market://details?id="+getPackageName());
                            Intent intentpf = new Intent(Intent.ACTION_VIEW,uri);
                            intentpf.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentpf);
                        }else {
                            Toast.makeText(BaseCommentActivity.this, getString(R.string.toast), Toast.LENGTH_SHORT).show();
                        }

                    }

                }).setNegativeButton(this.getString(R.string.hard_top_cancel),new DialogInterface.OnClickListener() {//添加返回按钮



            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub



            }

        }).show();//在按键响应事件中显示此对话框
    }

    public static boolean hasAnyMarketInstalled(Context context){

        Intent intent = new Intent();
        intent.setData(Uri.parse("market://details?id=android.browser"));
        List list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return 0 != list.size();
    }


    /**
     * 双击返回键退出
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (Common.isApkDebugable()){
            mIsExit = true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();

            } else {
                if (!isPingfen){
                    scoreView();
                    isPingfen = true;
                }else {
                    Toast.makeText(this, this.getString(R.string.exit_app), Toast.LENGTH_SHORT).show();
                    mIsExit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsExit = false;
                        }
                    }, 2000);
                }


            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
