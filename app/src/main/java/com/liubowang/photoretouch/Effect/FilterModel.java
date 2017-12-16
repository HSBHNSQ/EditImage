package com.liubowang.photoretouch.Effect;

import com.liubowang.photoretouch.Template.Template;
import com.liubowang.photoretouch.Template.TemplateModel;


/**
 * Created by heshaobo on 2017/11/28.
 */

public class FilterModel {

    public enum FilterType{
        NONE,
        MONEY,
        BLUR,
        PAINTING,
        ASCII,
        LICHTENSTEIN,
        CROSSHATCH,
        CONTRAST,
        CT,
        MOSAIC,
        EDGE,
        INVERT,
        GRAY,
        LOMO,
        OLDPHOTO,
        BLACKWHITE,
        SKETCH,
        CRAYON,
        COOL,
        INKWELL,
        MORE
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
        else if (Template.toTemplate(templateModel.template) == Template.GRAY){
            return new FilterModel(null,FilterType.GRAY,null);
        }
        else if (Template.toTemplate(templateModel.template) == Template.FOCUS){
            return new FilterModel(null,FilterType.BLUR,null);
        }
        else if (Template.toTemplate(templateModel.template) == Template.PAINTING){
            return new FilterModel(null,FilterType.PAINTING,null);
        }
        return null;
    }

    public String title ;
    public FilterType filterType;
    public String thumbPath;
    public RendererType rendererType = RendererType.GPUIMAGEFILTER;

}
