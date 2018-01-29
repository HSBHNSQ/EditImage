package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

/**
 * Created by heshaobo on 2018/1/22.
 */

public interface TextStyleInterface  {

    void setMainText(String text);
    void setSecondText(String text);
    void setMainColor(int mainColor);
    void setSecondColor(int secondColor);

}
