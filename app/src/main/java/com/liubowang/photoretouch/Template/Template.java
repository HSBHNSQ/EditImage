package com.liubowang.photoretouch.Template;

/**
 * Created by heshaobo on 2017/11/30.
 */

public enum Template {
    FOCUS,
    GRAY,
    PAINTING,
    CORSSHATCH,
    NONE;

    public int getInt(){
        if (this == FOCUS)
            return 0;
        else if (this == GRAY)
            return 1;
        else if (this == PAINTING)
            return 2;
        else if (this == CORSSHATCH)
            return 3;
        else if (this == NONE)
            return 4;
        return 0;
    }

    public static Template toTemplate(int i){
        if (i == FOCUS.getInt())
            return FOCUS;
        else if (i == GRAY.getInt())
            return GRAY;
        else if (i == PAINTING.getInt())
            return PAINTING;
        else if (i == CORSSHATCH.getInt())
            return CORSSHATCH;
        else if (i == NONE.getInt())
            return NONE;
        return FOCUS;
    }

}
