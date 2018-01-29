package com.liubowang.photoretouch.Utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.opencv.core.*;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

/**
 * Created by heshaobo on 2017/11/23.
 */

public class GrabCutUtil {

    private static final String TAG = GrabCutUtil.class.getSimpleName();
    private static OnGrabCutListener mGrabCutListener ;
    private static String mSrcImgPath;
    private static String mOutPath;

    /**
     * GrabCut 处理
     * @param srcImgPath 原图片；路径
     * @param maskImg mask 图片可根据自己需要设置 我这里使用红色代表可能的前景，白色代表确定的前景 黑色代表确定的背景，其他代表可能的背景
     * @param outPath 输出路径
     * @param listener  处理监听
     */
    public static void doGrabCut(String srcImgPath,Bitmap maskImg,String outPath,OnGrabCutListener listener){
        mGrabCutListener = listener;
        mSrcImgPath = srcImgPath;
        mOutPath = outPath;
        new ProcessImageTask().execute(maskImg);
    }

    private static class ProcessImageTask extends AsyncTask<Bitmap, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mGrabCutListener != null){
                mGrabCutListener.onStartGrabCut();
            }
        }

        @Override
        protected Integer doInBackground(Bitmap... params) {
            Log.d(TAG,">>>>>" + "加载SrcImg start" + "<<<<<");
            Mat img = Imgcodecs.imread(mSrcImgPath);
            Log.d(TAG,">>>>>" + "加载SrcImg" + "<<<<<");
            Bitmap drawImg = params[0];
            Log.d(TAG,">>>>>" + "处理 Mask start" + "<<<<<");
            Mat firstMask = GrabCutUtil.getMaskMap(drawImg);
            Log.d(TAG,">>>>>" + "处理 Mask end" + "<<<<<");
            Mat bgModel = new Mat();
            Mat fgModel = new Mat();
            Rect rect = new Rect(0,0,0,0);
            Log.d(TAG,">>>>>" + "grabCut start" + "<<<<<");

            try{
                Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel,
                        1, Imgproc.GC_INIT_WITH_MASK);
            }catch (Exception e){
                e.printStackTrace();
                Log.d(TAG,"grabCut 错误");
                return null;
            }


            Log.d(TAG,">>>>>" + "grabCut end" + "<<<<<");
            Log.d(TAG,">>>>>" + "处理完成后的遮罩 start" + "<<<<<");
            Mat resultMaskMat = resultForMask(firstMask);
            Log.d(TAG,">>>>>" + "处理完成后的遮罩 end" + "<<<<<");
            Imgcodecs.imwrite(mOutPath, resultMaskMat);
            firstMask.release();
            bgModel.release();
            fgModel.release();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (mGrabCutListener != null){
                mGrabCutListener.onFinishGrabCut();
            }
        }
    }

    /**
     * 请结果 Mat mask 筛选 这里我只取了 前景和可能的前景 并将其用红色表示
     * @param  mask 函数处理后未经帅选处理的
     * @return 筛选后的 mask
     */
    private static Mat resultForMask(Mat mask){
        int cols = mask.cols();
        int rows = mask.rows();
        byte maskBuff[] = new byte[(int) (mask.total() * mask.channels())];
        mask.get(0,0,maskBuff);
        Mat result = new Mat(rows,cols, CvType.CV_8UC4);
        int resultChannels = result.channels();
        byte resultBuff[] = new byte[(int) (result.total() * resultChannels)];
        result.get(0,0,resultBuff);
        for(int y = 0; y < rows; y++){
            for( int x = 0; x < cols; x++){
                int index = cols*y+x;
                //BGRA
                if(maskBuff[index] == Imgproc.GC_FGD){
                    resultBuff[index*4] = (byte)0;
                    resultBuff[index*4 + 1] = (byte)0;
                    resultBuff[index*4 + 2] = (byte)255;
                    resultBuff[index*4 + 3] = (byte)255;
                }else if(maskBuff[index] == Imgproc.GC_BGD){
                    resultBuff[index*4] = (byte)255;
                    resultBuff[index*4 + 1] = (byte)255;
                    resultBuff[index*4 + 2] = (byte)255;
                    resultBuff[index*4 + 3] = (byte)0;
                }else if(maskBuff[index] == Imgproc.GC_PR_FGD){
                    resultBuff[index*4] = (byte)0;//B
                    resultBuff[index*4 + 1] = (byte)0;//G
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

    /**
     * 将Bitmap maskImg 转为 Mat Mask
     * @param maskImg
     * @return Mat
     */
    private static Mat getMaskMap(Bitmap maskImg) {
        int w = maskImg.getWidth();
        int h = maskImg.getHeight();
        Mat  mask = new Mat(h, w, CvType.CV_8UC1);
        int bytes = maskImg.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        maskImg.copyPixelsToBuffer(buf);
        byte[] byteArray = buf.array();
        byte maskBuff[] = new byte[(int) (mask.total() * mask.channels())];
        mask.get(0,0,maskBuff);
        for (int y = 0; y < h ; y ++){
            for (int x = 0 ; x < w ; x ++){
                int index = y * w + x;
                int R = convertByteToInt(byteArray[index * 4]);
                int G = convertByteToInt(byteArray[index * 4 + 1]);
                int B = convertByteToInt(byteArray[index * 4 + 2]);
                int A = convertByteToInt(byteArray[index * 4 + 3]);
                //这里的白色 黑色 红色 等都可以根据自己的需要调整
                // 在 Mat mask 中 只有 Imgproc.GC_FGD， Imgproc.GC_BGD，
                // Imgproc.GC_PR_FGD，Imgproc.GC_PR_BGD 四种
                if (R == 255 && G == 255 && B == 255 && A == 255){//白色前景色
                    maskBuff[index] = Imgproc.GC_FGD;
                }
                else if (R == 0 && G == 0 && B == 0 && A == 255){//黑色背景色
                    maskBuff[index] = Imgproc.GC_BGD;
                }else if (R == 255 && G == 0 && B == 0 && A == 255){//红色可能的情景色
                    maskBuff[index] = Imgproc.GC_PR_FGD;
                }else {
                    maskBuff[index] = Imgproc.GC_PR_BGD;
                }
            }
        }
        mask.put(0,0,maskBuff);
        return mask;
    }

    public static int convertByteToInt(byte data) {
        int heightBit = (int) ((data >> 4) & 0x0F);
        int lowBit = (int) (0x0F & data);
        return heightBit * 16 + lowBit;
    }


//   public static void bianYuanQuJuChi(String bitmapPath,String outPath){
//       Mat vesselImage = Imgcodecs.imread(bitmapPath);
//       Imgproc.threshold(vesselImage,vesselImage,125,125,Imgproc.THRESH_BINARY);
//       Imgproc.pyrUp(vesselImage,vesselImage);
//       Imgproc.pyrDown(vesselImage,vesselImage);
//       Imgproc.threshold(vesselImage,vesselImage,200,255,Imgproc.THRESH_BINARY);
//       Imgcodecs.imwrite(outPath,vesselImage);
//   }
//
//   public static void bianYuanXuHua(String bitmapPath,String outPath){
//       Mat oriMat = Imgcodecs.imread(bitmapPath);
//       Imgproc.boxFilter(oriMat,oriMat,-1, new Size(5,5));
//       Imgcodecs.imwrite(outPath,oriMat);
//   }

    public interface OnGrabCutListener {
        void onStartGrabCut();
        void onFinishGrabCut();
    }

}
