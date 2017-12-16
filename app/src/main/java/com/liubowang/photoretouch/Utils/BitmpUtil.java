package com.liubowang.photoretouch.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by heshaobo on 2017/7/27.
 */

public class BitmpUtil {

    private static final String TAG = BitmpUtil.class.getSimpleName();

    public static int calculateInSampleSize(BitmapFactory.Options options, int maxSize) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > maxSize || width > maxSize) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) maxSize);
            final int widthRatio = Math.round((float) width / (float) maxSize);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高           // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;

    }
    public static Bitmap scaleBitmp(Bitmap bitmap,float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
    public static Bitmap decodeScaleToMaxSize(String path,int maxSize){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize=calculateInSampleSize(options, maxSize);
        options.inJustDecodeBounds=false;
        options.inDither=false;
        options.inPreferredConfig= Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path, options);
    }
    public static Bitmap scaleBitmpToMaxSize(Bitmap bitmap,int maxSize) {
        if (bitmap == null) return null;
        Matrix matrix = new Matrix();
        float scale = 1;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        if (height > maxSize || width > maxSize) {
            float heightRatio = (float) height / (float) maxSize;
            float widthRatio = (float) width / (float) maxSize;
            scale = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        int newHeight = (int) (height / scale);
        int newWidth = (int) (width / scale);
        return Bitmap.createScaledBitmap(bitmap,newWidth,newHeight,true);
    }

    public static Bitmap creatMaskBitmp(Bitmap original,Bitmap mask){
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(original, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }

    public static Bitmap getProperResizedImage(Bitmap originBitmap,int maxLength){
        float oldWidth = originBitmap.getWidth();
        float oldHeight = originBitmap.getHeight();
        float scale = oldWidth / oldHeight;
        float dstWidth = oldWidth;
        float dstHeight = oldHeight;
        if (scale > 1){ //W > H
            if (oldWidth > maxLength){
                dstWidth = maxLength;
                dstHeight = dstWidth / scale;
            }
        }else {//H >= w
            if (oldHeight > maxLength){
                dstHeight = maxLength;
                dstWidth = dstHeight * scale;
            }
        }
        Bitmap scaleBmp = Bitmap.createScaledBitmap(originBitmap,(int) dstWidth,(int) dstHeight,true);
        return scaleBmp;
    }

    public static Bitmap getViewBitmap(View v,int centerX ,int centerY,int size) {

        Bitmap bitmap = BitmpUtil.getViewBitmap(v);
        if (bitmap == null){
            return  null;
        }
        int X = centerX - size/2;
        int Y = centerY - size/2;
        if (X < 0) X = 0;
        if (Y < 0) Y = 0;
        if (X + size > bitmap.getWidth()){
            X = bitmap.getWidth() - size;
        }
        if (Y + size > bitmap.getHeight()){
            Y = bitmap.getHeight() - size;
        }
        bitmap = Bitmap.createBitmap(bitmap,X,Y,size,size);
        return bitmap;
    }
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("Folder", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
//-----------------------------------------------------
//        View view = activity.getWindow().getDecorView();
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//        bitmap = view.getDrawingCache();
//-----------------------------------------------------

        return bitmap;
    }
    //从相册获取合适的图片
    public static Bitmap getSuitableBitmapFromAlbum(Context context, String imgPath) {
        File file = new File(imgPath);
        if (file.exists() && file.canRead()) {
            // -------1.图片缩放--------
            int dw = ScreenUtil.getScreenSize(context).widthPixels; // 屏幕宽
            int dh = ScreenUtil.getScreenSize(context).heightPixels; // 屏幕高
            // 加载图像，只是为了获取尺寸
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 设置之后可以获取尺寸信息
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
            // 计算水平和垂直缩放系数
            int heightRatio = (int) Math.ceil(options.outHeight / (float) dh);
            int widthRatio = (int) Math.ceil(options.outWidth / (float) dw);
            // 判断哪个大
            if (heightRatio > 1 && widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    options.inSampleSize = heightRatio;
                } else {
                    options.inSampleSize = widthRatio;
                }
            }
            // 图片缩放
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imgPath, options);

            // -------2.判断图片朝向--------
            try {
                ExifInterface exif = new ExifInterface(imgPath);
                int degree = 0; // 图片旋转角度
                if (exif != null) {
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, -1);
                    if (orientation != -1) {
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                degree = 90;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                degree = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                degree = 270;
                                break;
                            default:
                                break;
                        }
                    }
                }
                if (degree != 0) { // 图片需要旋转
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.preRotate(degree);
                    Bitmap mRotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            width, height, matrix, true);
                    return mRotateBitmap;
                } else {
                    return bitmap;
                }
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
    //保存文件到指定路径
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * A copy of the Android internals  insertImage method, this method populates the
     * meta data with DATE_ADDED and DATE_TAKEN. This fixes a common problem where media
     * that is inserted manually gets saved at the end of the gallery (because date is not populated).
     * @see MediaStore.Images.Media#insertImage(ContentResolver, Bitmap, String, String)
     */
    public static final Uri insertImage(ContentResolver cr,
                                           Bitmap source,
                                           String title,
                                           String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return url;
    }

    /**
     * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
     * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
     * meta data. The StoreThumbnail method is private so it must be duplicated here.
     * @see MediaStore.Images.Media (StoreThumbnail private method)
     */
    private static final Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND,kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
}
