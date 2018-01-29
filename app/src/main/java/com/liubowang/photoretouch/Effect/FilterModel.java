package com.liubowang.photoretouch.Effect;

import com.liubowang.photoretouch.Template.Template;
import com.liubowang.photoretouch.Template.TemplateModel;


/**
 * Created by heshaobo on 2017/11/28.
 */

public class FilterModel {

    public enum FilterType{
        NONE,CONTRAST, GRAYSCALE, SHARPEN,
        SEPIA, SOBEL_EDGE_DETECTION,
        EMBOSS, POSTERIZE,
        GAMMA, BRIGHTNESS, INVERT, HUE,
        PIXELATION,SATURATION, EXPOSURE,
        HIGHLIGHT_SHADOW, MONOCHROME, OPACITY,
        RGB, WHITE_BALANCE, VIGNETTE,
        TONE_CURVE, LOOKUP_AMATORKA,
        GAUSSIAN_BLUR, CROSSHATCH, BOX_BLUR, CGA_COLORSPACE,
        DILATION, KUWAHARA, RGB_DILATION, SKETCH,
        TOON, SMOOTH_TOON, BULGE_DISTORTION,
        GLASS_SPHERE, HAZE, LAPLACIAN,
        NON_MAXIMUM_SUPPRESSION,
        SPHERE_REFRACTION, SWIRL, WEAK_PIXEL_INCLUSION,
        FALSE_COLOR, COLOR_BALANCE, LEVELS_FILTER_MIN,
        BILATERAL_BLUR
    }

    public enum RendererType{
        GPUIMAGEFILTER,ABSFILTER
    }

    private FilterModel(){
        super();
    }

    public FilterModel(String title,FilterType filterType,String thumbPath){
        super();
        this.title = title;
        this.filterType = filterType;
        this.thumbPath = thumbPath;
    }

    public static FilterModel create(TemplateModel templateModel){
        if (Template.toTemplate(templateModel.template) == Template.CORSSHATCH){
            return new FilterModel(null,FilterType.CROSSHATCH,null);
        }
//        else if (Template.toTemplate(templateModel.template) == Template.GRAY){
//            return new FilterModel(null,FilterType.GRAY,null);
//        }
//        else if (Template.toTemplate(templateModel.template) == Template.FOCUS){
//            return new FilterModel(null,FilterType.BLUR,null);
//        }
//        else if (Template.toTemplate(templateModel.template) == Template.PAINTING){
//            return new FilterModel(null,FilterType.PAINTING,null);
//        }
        return null;
    }

    public String title ;
    public FilterType filterType;
    public String thumbPath;
    public RendererType rendererType = RendererType.GPUIMAGEFILTER;

}
