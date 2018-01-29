package com.liubowang.photoretouch.Text;

/**
 * Created by heshaobo on 2018/1/17.
 */

public class TextTypeModel {

    public String className;
    public boolean isSelected = false;
    public String thumbPath;


    @Override
    public String toString() {
        return "TextTypeModel:{" +
                "\nclassName = " + className +
                "\nthumbPath = " + thumbPath +
                "\nisSelected = " + isSelected +
                "\n}";
    }
}
