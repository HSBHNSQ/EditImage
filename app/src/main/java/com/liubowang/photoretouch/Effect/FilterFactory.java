package com.liubowang.photoretouch.Effect;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsCoolFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsCrayonFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsLomoFilter;
import com.martin.ads.omoshiroilib.filter.effect.instb.InsSketchFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.BlackWhiteFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.PastTimeFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.AscIIArtFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.BlueorangeFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.ContrastFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.CrosshatchFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.EdgeDetectionFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.FastBlurFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.LichtensteinEsqueFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.MoneyFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.PixelizeFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.PolygonizationFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.InkwellFilter;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.filter.imgproc.GrayScaleShaderFilter;
import com.martin.ads.omoshiroilib.filter.imgproc.InvertColorFilter;


/**
 * Created by heshaobo on 2017/11/28.
 */

public class FilterFactory {

    public static Object creatFilter(FilterModel filterModel, Context context){
        if (filterModel == null){
            return new PassThroughFilter(context);
        }
        switch (filterModel.filterType){
            case NONE:
                return new PassThroughFilter(context);
            case MONEY:
                return new MoneyFilter(context);
            case BLUR:
                return new FastBlurFilter(context);
            case PAINTING:
                return new PolygonizationFilter(context);
            case ASCII:
                return new AscIIArtFilter(context);
            case LICHTENSTEIN:
                return new LichtensteinEsqueFilter(context);
            case CROSSHATCH:
                return new CrosshatchFilter(context);
            case CONTRAST:
                return new ContrastFilter(context);
            case CT:
                return new BlueorangeFilter(context);
            case MOSAIC:
                return new PixelizeFilter(context);
            case EDGE:
                return new EdgeDetectionFilter(context);
            case INVERT:
                return new InvertColorFilter(context);
            case GRAY:
                return new GrayScaleShaderFilter(context);
            case LOMO:
                return new InsLomoFilter(context);
            case OLDPHOTO:
                return new PastTimeFilter(context);
            case BLACKWHITE:
                return new BlackWhiteFilter(context);
            case SKETCH:
                return new InsSketchFilter(context);
            case CRAYON:
                return new InsCrayonFilter(context);
            case COOL:
                return new InsCoolFilter(context);
            case INKWELL:
                return new InkwellFilter(context);
        }
        return new PassThroughFilter(context);
    }

    public static FilterType getAbsFilterType(FilterModel model){
        if (model == null){
            return FilterType.NONE;
        }
        switch (model.filterType){
            case NONE:
                return FilterType.NONE;
            case MONEY:
                return FilterType.MONEY_FILTER ;
            case BLUR:
                return FilterType.FAST_BLUR_FILTER;
            case PAINTING:
                return FilterType.POLYGONIZATION_FILTER;
            case ASCII:
                return FilterType.ASCII_ART_FILTER;
            case LICHTENSTEIN:
                return FilterType.LICHTENSTEINESQUE_FILTER ;
            case CROSSHATCH:
                return FilterType.CROSSHATCH_FILTER ;
            case CONTRAST:
                return FilterType.CONTRAST_FILTER ;
            case CT:
                return FilterType.BLUEORANGE_FILTER;
            case MOSAIC:
                return FilterType.PIXELIZE_FILTER ;
            case EDGE:
                return FilterType.EDGE_DETECTION_FILTER;
            case INVERT:
                return FilterType.INVERT_COLOR;
            case GRAY:
                return FilterType.GRAY_SCALE;
            case LOMO:
                return FilterType.LOMO;
            case OLDPHOTO:
                return FilterType.PAST_TIME_FILTER;
            case BLACKWHITE:
                return FilterType.BLACK_WHITE_FILTER;
            case SKETCH:
                return FilterType.SKETCH;
            case CRAYON:
                return FilterType.CRAYON;
            case COOL:
                return FilterType.COOL ;
            case INKWELL:
                return FilterType.INKWELL;
        }
        return FilterType.NONE;
    }


    public static FilterModel.RendererType getRendererType(FilterModel filterModel){
//        switch (filterModel.filterType){
//            case CROSSHATCH:
//                return FilterModel.RendererType.ABSFILTER;
//        }
        return FilterModel.RendererType.ABSFILTER;
    }

}
