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

import com.liubowang.photoretouch.Effect.FilterModel.FilterType;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.BitmpUtil;
import com.liubowang.photoretouch.Utils.FileUtil;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;

/**
 * Created by heshaobo on 2017/11/28.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {


    private Context context;
    private List<FilterModel> filterModels = new ArrayList<>();
    private String[] colors = getColors();
    private Bitmap smaleImage;
    public FilterAdapter(Context context){
        super();
        this.context = context;
        init(context);
    }

    public void setSmaleImage(Bitmap smaleImage) {
        this.smaleImage = smaleImage;
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
            if (smaleImage != null){
                File thumbFile = new File(filterModel.thumbPath);
                if (thumbFile.exists()){
                    thumbImageView.setImageBitmap(BitmapFactory.decodeFile(filterModel.thumbPath));
                }else {
                    new LoadThumbTask().execute(filterModel);
                }

            }
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

        class LoadThumbTask extends AsyncTask<FilterModel,Void,Bitmap> {

            @Override
            protected Bitmap doInBackground(FilterModel... models) {
                FilterModel model = models[0];

                GPUImage gpuImage = new GPUImage(context);
                gpuImage.setImage(smaleImage);
                gpuImage.setFilter(FilterFactory.creatFilter(model,context));
                Bitmap result = gpuImage.getBitmapWithFilterApplied();
                FileUtil.writeBitmap(new File(model.thumbPath),result);

                return result;
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
        FilterModel modelNone = new FilterModel("none", FilterType.NONE,null);
        modelNone.thumbPath = FileUtil.getTmpPath() + modelNone.title + ".jpg";
        modelNone.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(modelNone);
        FilterModel modelContrast = new FilterModel("Contrast", FilterType.CONTRAST,null);
        modelContrast.thumbPath = FileUtil.getTmpPath() + modelContrast.title + ".jpg";
        modelContrast.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(modelContrast);
        FilterModel modelInvert = new FilterModel("Invert", FilterType.INVERT,null);
        modelInvert.thumbPath = FileUtil.getTmpPath() + modelInvert.title + ".jpg";
        modelInvert.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(modelInvert);
        FilterModel modelPixelation = new FilterModel("Pixelation", FilterType.PIXELATION,null);
        modelPixelation.thumbPath = FileUtil.getTmpPath() + modelPixelation.title + ".jpg";
        modelPixelation.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(modelPixelation);
        FilterModel modelHue = new FilterModel("Hue", FilterType.HUE,null);
        modelHue.thumbPath = FileUtil.getTmpPath() + modelHue.title + ".jpg";
        modelHue.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(modelHue);
        FilterModel modelGamma = new FilterModel("Gamma", FilterType.GAMMA,null);
        modelGamma.thumbPath = FileUtil.getTmpPath() + modelGamma.title + ".jpg";
        modelGamma.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(modelGamma);
        FilterModel modelBrightness = new FilterModel("Brightness", FilterType.BRIGHTNESS,null);
        modelBrightness.thumbPath = FileUtil.getTmpPath() + modelBrightness.title + ".jpg";
        modelBrightness.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(modelBrightness);
        FilterModel modelSepia = new FilterModel("Sepia", FilterType.SEPIA,null);
        modelSepia.thumbPath = FileUtil.getTmpPath() + modelSepia.title + ".jpg";
        modelSepia.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(modelSepia);
        FilterModel Grayscale = new FilterModel("Grayscale", FilterType.GRAYSCALE,null);
        Grayscale.thumbPath = FileUtil.getTmpPath() + Grayscale.title + ".jpg";
        Grayscale.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Grayscale);
        FilterModel Sharpness = new FilterModel("Sharpness", FilterType.SHARPEN,null);
        Sharpness.thumbPath = FileUtil.getTmpPath() + Sharpness.title + ".jpg";
        Sharpness.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Sharpness);
        FilterModel Sobel = new FilterModel("Sobel Edge Detection", FilterType.SOBEL_EDGE_DETECTION,null);
        Sobel.thumbPath = FileUtil.getTmpPath() + Sobel.title + ".jpg";
        Sobel.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Sobel);
        FilterModel Emboss = new FilterModel("Emboss", FilterType.EMBOSS,null);
        Emboss.thumbPath = FileUtil.getTmpPath() + Emboss.title + ".jpg";
        Emboss.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Emboss);
        FilterModel Posterize = new FilterModel("Posterize", FilterType.POSTERIZE,null);
        Posterize.thumbPath = FileUtil.getTmpPath() + Posterize.title + ".jpg";
        Posterize.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Posterize);
        FilterModel Saturation = new FilterModel("Saturation", FilterType.SATURATION,null);
        Saturation.thumbPath = FileUtil.getTmpPath() + Saturation.title + ".jpg";
        Saturation.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Saturation);
        FilterModel Exposure = new FilterModel("Exposure", FilterType.EXPOSURE,null);
        Exposure.thumbPath = FileUtil.getTmpPath() + Exposure.title + ".jpg";
        Exposure.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Exposure);
        FilterModel Highlight = new FilterModel("Highlight Shadow", FilterType.HIGHLIGHT_SHADOW,null);
        Highlight.thumbPath = FileUtil.getTmpPath() + Highlight.title + ".jpg";
        Highlight.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Highlight);
        FilterModel Monochrome = new FilterModel("Monochrome", FilterType.MONOCHROME,null);
        Monochrome.thumbPath = FileUtil.getTmpPath() + Monochrome.title + ".jpg";
        Monochrome.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Monochrome);
        FilterModel Opacity = new FilterModel("Opacity", FilterType.OPACITY,null);
        Opacity.thumbPath = FileUtil.getTmpPath() + Opacity.title + ".jpg";
        Opacity.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Opacity);
        FilterModel RGB = new FilterModel("RGB", FilterType.RGB,null);
        RGB.thumbPath = FileUtil.getTmpPath() + RGB.title + ".jpg";
        RGB.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(RGB);
        FilterModel Balance = new FilterModel("White Balance", FilterType.WHITE_BALANCE,null);
        Balance.thumbPath = FileUtil.getTmpPath() + Balance.title + ".jpg";
        Balance.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Balance);
        FilterModel Vignette = new FilterModel("Vignette", FilterType.VIGNETTE,null);
        Vignette.thumbPath = FileUtil.getTmpPath() + Vignette.title + ".jpg";
        Vignette.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Vignette);
        FilterModel ToneCurve = new FilterModel("ToneCurve", FilterType.TONE_CURVE,null);
        ToneCurve.thumbPath = FileUtil.getTmpPath() + ToneCurve.title + ".jpg";
        ToneCurve.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(ToneCurve);
        FilterModel Lookup = new FilterModel("Lookup (Amatorka)", FilterType.LOOKUP_AMATORKA,null);
        Lookup.thumbPath = FileUtil.getTmpPath() + Lookup.title + ".jpg";
        Lookup.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Lookup);
        FilterModel Gaussian = new FilterModel("Gaussian Blur", FilterType.GAUSSIAN_BLUR,null);
        Gaussian.thumbPath = FileUtil.getTmpPath() + Gaussian.title + ".jpg";
        Gaussian.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Gaussian);
        FilterModel Crosshatch = new FilterModel("Crosshatch", FilterType.CROSSHATCH,null);
        Crosshatch.thumbPath = FileUtil.getTmpPath() + Crosshatch.title + ".jpg";
        Crosshatch.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Crosshatch);
        FilterModel Blur = new FilterModel("Box Blur", FilterType.BOX_BLUR,null);
        Blur.thumbPath = FileUtil.getTmpPath() + Blur.title + ".jpg";
        Blur.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Blur);
        FilterModel CGA = new FilterModel("CGA Color Space", FilterType.CGA_COLORSPACE,null);
        CGA.thumbPath = FileUtil.getTmpPath() + CGA.title + ".jpg";
        CGA.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(CGA);
        FilterModel Dilation = new FilterModel("Dilation", FilterType.DILATION,null);
        Dilation.thumbPath = FileUtil.getTmpPath() + Dilation.title + ".jpg";
        Dilation.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Dilation);
        FilterModel Kuwahara = new FilterModel("Kuwahara", FilterType.KUWAHARA,null);
        Kuwahara.thumbPath = FileUtil.getTmpPath() + Kuwahara.title + ".jpg";
        Kuwahara.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Kuwahara);
        FilterModel DilationRGB = new FilterModel("RGB Dilation", FilterType.RGB_DILATION,null);
        DilationRGB.thumbPath = FileUtil.getTmpPath() + DilationRGB.title + ".jpg";
        DilationRGB.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(DilationRGB);
        FilterModel Sketch = new FilterModel("Sketch", FilterType.SKETCH,null);
        Sketch.thumbPath = FileUtil.getTmpPath() + Sketch.title + ".jpg";
        Sketch.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Sketch);
        FilterModel Toon = new FilterModel("Toon", FilterType.TOON,null);
        Toon.thumbPath = FileUtil.getTmpPath() + Toon.title + ".jpg";
        Toon.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Toon);
        FilterModel Smooth = new FilterModel("Smooth Toon", FilterType.SMOOTH_TOON,null);
        Smooth.thumbPath = FileUtil.getTmpPath() + Smooth.title + ".jpg";
        Smooth.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Smooth);
        FilterModel Haze = new FilterModel("Haze", FilterType.HAZE,null);
        Haze.thumbPath = FileUtil.getTmpPath() + Haze.title + ".jpg";
        Haze.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Haze);
        FilterModel Laplacian = new FilterModel("Laplacian", FilterType.LAPLACIAN,null);
        Laplacian.thumbPath = FileUtil.getTmpPath() + Laplacian.title + ".jpg";
        Laplacian.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Laplacian);
        FilterModel Suppression = new FilterModel("Non Maximum Suppression", FilterType.NON_MAXIMUM_SUPPRESSION,null);
        Suppression.thumbPath = FileUtil.getTmpPath() + Suppression.title + ".jpg";
        Suppression.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Suppression);
        FilterModel Pixel = new FilterModel("Weak Pixel Inclusion", FilterType.WEAK_PIXEL_INCLUSION,null);
        Pixel.thumbPath = FileUtil.getTmpPath() + Pixel.title + ".jpg";
        Pixel.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Pixel);
        FilterModel False = new FilterModel("False Color", FilterType.FALSE_COLOR,null);
        False.thumbPath = FileUtil.getTmpPath() + False.title + ".jpg";
        False.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(False);
        FilterModel BalanceColor = new FilterModel("Color Balance", FilterType.COLOR_BALANCE,null);
        BalanceColor.thumbPath = FileUtil.getTmpPath() + BalanceColor.title + ".jpg";
        BalanceColor.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(BalanceColor);
        FilterModel Levels = new FilterModel("Levels Min (Mid Adjust)", FilterType.LEVELS_FILTER_MIN,null);
        Levels.thumbPath = FileUtil.getTmpPath() + Levels.title + ".jpg";
        Levels.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Levels);
        FilterModel Bilateral = new FilterModel("Bilateral Blur", FilterType.BILATERAL_BLUR,null);
        Bilateral.thumbPath = FileUtil.getTmpPath() + Bilateral.title + ".jpg";
        Bilateral.rendererType = FilterFactory.getRendererType(null);
        filterModels.add(Bilateral);
    }

    public interface OnFilterChangeListener {
        void onFilterChanged(FilterModel filterModel);
        void onSeekAdjustChanged(int progress);
    }

    private OnFilterChangeListener filterChangeListener;

    public void setFilterChangeListener(OnFilterChangeListener filterChangeListener) {
        this.filterChangeListener = filterChangeListener;
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
