package com.liubowang.editimage.Effect;

/**
 * Created by jiazhiguo on 2017/6/8.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.liubowang.editimage.Base.EIBaseActiviry;
import com.liubowang.editimage.Draw.DrawView;
import com.liubowang.editimage.R;
import com.liubowang.editimage.Utils.FileUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.nio.ByteBuffer;


public class TmpActivity extends EIBaseActiviry {

    private static final String TAG  = TmpActivity.class.getSimpleName();
    static final int REQUEST_OPEN_IMAGE = 1;

    String mCurrentPhotoPath;
    String mMaskPath;
    Bitmap mBitmap;
    ImageView mImageView;
    DrawView mDrawView;
    int touchCount = 0;
    Point tl;
    Point br;
    boolean targetChose = false;
    ProgressDialog dlg;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    //Log.i(TAG, "OpenCV loaded successfully");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp);

        mImageView = (ImageView) findViewById(R.id.imgDisplay);
        mDrawView = (DrawView) findViewById(R.id.draw_view_tmp);
        findViewById(R.id.white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setDrawColor(Color.parseColor("#FFFFFF"));
            }
        });
        findViewById(R.id.black).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setDrawColor(Color.parseColor("#000000"));
            }
        });
        dlg = new ProgressDialog(this);
        tl = new Point();
        br = new Point();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    int scaleFactor = 1;
    private void setPic() {
        int targetW = 720;//mImageView.getWidth();
        int targetH = 1128;//mImageView.getHeight();
        Log.i(">>>>>", "targetW="+targetW);
        Log.i(">>>>>", "targetH=" + targetH);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Log.i(">>>>>", "photoW="+photoW);
        Log.i(">>>>>", "photoH=" + photoH);

        scaleFactor = Math.max(photoW / targetW, photoH / targetH)+1;
        Log.i(">>>>>", "photoW / targetW="+(photoW / targetW));
        Log.i(">>>>>", "photoH / targetH="+(photoH / targetH));

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        mBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(mBitmap);
        Log.i(">>>>>", "mBitmap.getWidth()="+mBitmap.getWidth());
        Log.i(">>>>>", "mBitmap.getHeight()=" + mBitmap.getHeight());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OPEN_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imgUri = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(imgUri, filePathColumn,
                            null, null, null);
                    cursor.moveToFirst();

                    int colIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(colIndex);
                    cursor.close();

                    mCurrentPhotoPath = FileUtil.getCurrentTimeMillisPath("jpg");
                    File imageFile = new File(imagePath);
                    Bitmap bitmap = FileUtil.readBitmap(imageFile);
                    FileUtil.writeBitmap(new File(mCurrentPhotoPath),bitmap);
                    setPic();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_open_img:
                requestSDCardPermission();
                return true;
            case R.id.action_choose_target:

                if (mCurrentPhotoPath != null)
                    targetChose = false;
                mImageView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mDrawView.onTouch(event);

                        return true;
                    }
                });

                return true;
            case R.id.action_cut_image:
                Bitmap bitmap = mDrawView.getBitmp();
                mDrawView.restartDrawing();
                mMaskPath = FileUtil.getCurrentTimeMillisPath("jpg");
                FileUtil.writeBitmap(new File(mMaskPath),bitmap);
                new TmpActivity.ProcessImageTask().execute();
                targetChose = false;
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void permissionSDCardAllow() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_OPEN_IMAGE);
    }

    public static int convertByteToInt(byte data) {

        int heightBit = (int) ((data >> 4) & 0x0F);
        int lowBit = (int) (0x0F & data);
        return heightBit * 16 + lowBit;
    }

    private Mat chuliMask(Bitmap drawImg) {
        int w = drawImg.getWidth();
        int h = drawImg.getHeight();
        Mat  mask = new Mat(h, w, CvType.CV_8UC1);
        int bytes = drawImg.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        drawImg.copyPixelsToBuffer(buf);
        byte[] byteArray = buf.array();
        Log.d(TAG,"w*h:"+(w*h));
        byte maskBuff[] = new byte[(int) (mask.total() * mask.channels())];
        mask.get(0,0,maskBuff);
        Log.d(TAG,"maskBuff.length:"+maskBuff.length);
        Log.d(TAG,"mask.channels:"+mask.channels());
        int countFGD = 0;
        int countBGD = 0;
        int countRem = 0;
        for (int y = 0; y < h ; y ++){
            for (int x = 0 ; x < w ; x ++){
                int index = y * w + x;
                int R = convertByteToInt(byteArray[index * 4]);
                int G = convertByteToInt(byteArray[index * 4 + 1]);
                int B = convertByteToInt(byteArray[index * 4 + 2]);
                int A = convertByteToInt(byteArray[index * 4 + 3]);
                if (R == 255 && G == 255 && B == 255 && A == 255){//白色前景色
                    maskBuff[index] = Imgproc.GC_FGD;
                    countFGD ++;
                }
                else if (R == 0 && G == 0 && B == 0 && A == 255){//黑色背景色
                    maskBuff[index] = Imgproc.GC_BGD;
                    countBGD ++;
                }else {
                    maskBuff[index] = Imgproc.GC_PR_BGD;
                    countRem ++;
                }
            }
        }
        mask.put(0,0,maskBuff);
        Log.d(TAG,"countBGD:"+countBGD);
        Log.d(TAG,"countFGD:"+countFGD);
        Log.d(TAG,"countRem:"+countRem);
        String tmpPath = FileUtil.getCurrentTimeMillisPath("png");
        Imgcodecs.imwrite(tmpPath, mask);
        return mask;
    }

    private Mat resultForMask(Mat mask){
        int cols = mask.cols();
        int rows = mask.rows();
        byte maskBuff[] = new byte[(int) (mask.total() * mask.channels())];
        mask.get(0,0,maskBuff);
        Mat result = new Mat(rows,cols,CvType.CV_8UC4);
        int resultChannels = result.channels();
        byte resultBuff[] = new byte[(int) (result.total() * resultChannels)];
        result.get(0,0,resultBuff);
        for(int y = 0; y < rows; y++){
            for( int x = 0; x < cols; x++){
                int index = cols*y+x;
                if(maskBuff[index] == Imgproc.GC_FGD){
                    resultBuff[index*4] = (byte)255;
                    resultBuff[index*4 + 1] = (byte)255;
                    resultBuff[index*4 + 2] = (byte)255;
                    resultBuff[index*4 + 3] = (byte)255;
                }else if(maskBuff[index] == Imgproc.GC_BGD){
                    resultBuff[index*4] = (byte)255;
                    resultBuff[index*4 + 1] = (byte)255;
                    resultBuff[index*4 + 2] = (byte)255;
                    resultBuff[index*4 + 3] = (byte)0;
                }else if(maskBuff[index] == Imgproc.GC_PR_FGD){
                    resultBuff[index*4] = (byte)255;//B
                    resultBuff[index*4 + 1] = (byte)255;//G
                    resultBuff[index*4 + 2] = (byte)255;//R
                    resultBuff[index*4 + 3] = (byte)255;
                }else if(maskBuff[index] == Imgproc.GC_PR_BGD){
                    resultBuff[index*4] = (byte)255;
                    resultBuff[index*4 + 1] = (byte)255;
                    resultBuff[index*4 + 2] = (byte)255;
                    resultBuff[index*4 + 3] = (byte)0;
                }
            }
        }
        result.put(0,0,resultBuff);
        return result;
    }

    private class ProcessImageTask extends AsyncTask<Integer, Integer, Integer> {
        Mat img;
        Mat foreground;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlg.setMessage("Processing Image...");
            dlg.setCancelable(false);
            dlg.setIndeterminate(true);
            dlg.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            //1
            Log.d(TAG,">>>>>1:" + "load img resize img");
            img = Imgcodecs.imread(mCurrentPhotoPath);
            Imgproc.resize(img, img, new Size(img.cols()/scaleFactor, img.rows()/scaleFactor));
            //2
            Log.d(TAG,">>>>>2:" + "load draw img resize draw img");
//            Mat firstMask = Imgcodecs.imread(mMaskPath);
//            Imgproc.resize(firstMask,firstMask,new Size(img.cols()/scaleFactor, img.rows()/scaleFactor));
            Bitmap drawImg = FileUtil.readBitmap(new File(mMaskPath));
            drawImg = Bitmap.createScaledBitmap(drawImg,img.width(),img.height(),false);
            //3
            Log.d(TAG,">>>>>3:" + "load mask ");
            Mat firstMask = chuliMask(drawImg);
            Mat bgModel = new Mat();
            Mat fgModel = new Mat();
            Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
            Rect rect = new Rect(0,0,0,0);
            Log.d(TAG,">>>>>4:" + " grabCut ");
            Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel,
                    1, Imgproc.GC_INIT_WITH_MASK);

//            Core.compare(firstMask, source, firstMask, Core.CMP_EQ);
//            foreground = new Mat(img.size(), CvType.CV_8UC3,
//                    new Scalar(255, 255, 255));
//            img.copyTo(foreground);
////            Imgproc.blur(foreground, foreground, new Size(20, 20));
//            img.copyTo(foreground, firstMask);
//            Log.d(TAG,">>>>>5:" + " imwrite firstMask ");

            Mat resultMaskMat = resultForMask(firstMask);
            Imgcodecs.imwrite(mMaskPath, resultMaskMat);
            Bitmap maskResultBmp = Bitmap.createBitmap(resultMaskMat.cols(),resultMaskMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(resultForMask(firstMask),maskResultBmp);

            //获取图片的资源文件
            Bitmap original = mBitmap;
            //获取遮罩层图片
            Bitmap maskBmp = Bitmap.createScaledBitmap(maskResultBmp,original.getWidth(),original.getHeight(),true);
            Bitmap result = Bitmap.createBitmap(maskBmp.getWidth(), maskBmp.getHeight(), Bitmap.Config.ARGB_8888);
            //将遮罩层的图片放到画布中
            Canvas mCanvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            //设置两张图片相交时的模式
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mCanvas.drawBitmap(original, 0, 0, null);
            mCanvas.drawBitmap(maskBmp, 0, 0, paint);
            paint.setXfermode(null);
            FileUtil.writeBitmap(new File(mMaskPath),result);
            firstMask.release();
            source.release();
            bgModel.release();
            fgModel.release();



            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

//            Bitmap jpg = BitmapFactory
//                    .decodeFile(mCurrentPhotoPath + ".png");
            Bitmap jpg = BitmapFactory
                    .decodeFile(mMaskPath);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mImageView.setAdjustViewBounds(true);
            mImageView.setPadding(2, 2, 2, 2);
            mImageView.setImageBitmap(jpg);
            mImageView.invalidate();


            dlg.dismiss();
        }
    }
//    private class ProcessImageTask extends AsyncTask<Integer, Integer, Integer> {
//        Mat img;
//        Mat foreground;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dlg.setMessage("Processing Image...");
//            dlg.setCancelable(false);
//            dlg.setIndeterminate(true);
//            dlg.show();
//        }
//
//        @Override
//        protected Integer doInBackground(Integer... params) {
//            long ll = System.currentTimeMillis();
//            Log.i(">>>>>", "start="+ll);
//            img = Imgcodecs.imread(mCurrentPhotoPath);
//            Imgproc.resize(img, img, new Size(img.cols()/scaleFactor, img.rows()/scaleFactor));
//            Log.i(">>>>>", "11111=" + System.currentTimeMillis()+"@@@@@"+(System.currentTimeMillis()-ll));
//            Mat background = new Mat(img.size(), CvType.CV_8UC3,
//                    new Scalar(255, 255, 255));
//
//            Mat firstMask = new Mat();
//            Mat bgModel = new Mat();
//            Mat fgModel = new Mat();
//            Mat mask;
//
//            Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
//            Mat dst = new Mat();
//            Rect rect = new Rect(tl, br);
//            Log.i(">>>>>", "22222="+ System.currentTimeMillis()+"@@@@@"+(System.currentTimeMillis()-ll));
//            Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel,
//                    1, Imgproc.GC_INIT_WITH_RECT);
//            Log.i(">>>>>", "33333=" + System.currentTimeMillis() + "@@@@@" + (System.currentTimeMillis() - ll));
//            Core.compare(firstMask, source, firstMask, Core.CMP_EQ);
//            Log.i(">>>>>", "44444=" + System.currentTimeMillis() + "@@@@@" + (System.currentTimeMillis() - ll));
//            foreground = new Mat(img.size(), CvType.CV_8UC3,
//                    new Scalar(255, 255, 255));
//            /////
//            img.copyTo(foreground);
////            Imgproc.blur(foreground, foreground, new Size(20, 20));
//            Log.i(">>>>>", "55555=" + System.currentTimeMillis()+"@@@@@"+(System.currentTimeMillis()-ll));
//            /////
//            img.copyTo(foreground, firstMask);
//            Log.i(">>>>>", "66666=" + System.currentTimeMillis()+"@@@@@"+(System.currentTimeMillis()-ll));
//            Imgcodecs.imwrite(mCurrentPhotoPath + ".png", firstMask);
//
//            firstMask.release();
//            source.release();
//            bgModel.release();
//            fgModel.release();
//
//
//
//            return 0;
//        }
//
//        @Override
//        protected void onPostExecute(Integer result) {
//            super.onPostExecute(result);
//
//            Bitmap jpg = BitmapFactory
//                    .decodeFile(mCurrentPhotoPath + ".png");
//
//            mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            mImageView.setAdjustViewBounds(true);
//            mImageView.setPadding(2, 2, 2, 2);
//            mImageView.setImageBitmap(jpg);
//            mImageView.invalidate();
//
//
//            dlg.dismiss();
//        }
//    }
}

//修改像素值
//Mat m = ...  // assuming it's of CV_8U type
//        byte buff[] = new byte[m.total() * m.channels()];
//        m.get(0, 0, buff);
//// working with buff
//// ...
//        m.put(0, 0, buff);
