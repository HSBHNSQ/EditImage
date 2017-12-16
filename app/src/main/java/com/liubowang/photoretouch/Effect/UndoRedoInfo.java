package com.liubowang.photoretouch.Effect;

/**
 * Created by heshaobo on 2017/12/1.
 */

public class UndoRedoInfo {
    public String unDoMaskPath;
    public boolean canEditDraw = false;
    public String reDoMaskPath;

    public UndoRedoInfo (String maskPath,boolean canEditDraw){
        super();
        this.unDoMaskPath = maskPath;
        this.canEditDraw = canEditDraw;
    }

    @Override
    public String toString() {
        String str = "UndoRedoInfo:{\n" +
                "    unDoMaskPath:" + unDoMaskPath + "\n" +
                "    canEditDraw:" + canEditDraw + "\n" +
                "    reDoMaskPath:" + reDoMaskPath + "\n" +
                "}\n";
        return str;
    }
}
