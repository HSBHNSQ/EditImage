package com.liubowang.photoretouch.Text;

import android.content.Context;
import android.graphics.RectF;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.liubowang.photoretouch.Style.TextStyle;
import com.liubowang.photoretouch.Style.TextStyle0;
import com.liubowang.photoretouch.Style.TextStyle1;
import com.liubowang.photoretouch.Style.TextStyle2;
import com.liubowang.photoretouch.Style.TextStyle3;
import com.liubowang.photoretouch.Style.TextStyle4;
import com.liubowang.photoretouch.Style.TextStyle5;
import com.liubowang.photoretouch.Style.TextStyle6;
import com.liubowang.photoretouch.Style.TextStyle7;
import com.liubowang.photoretouch.Style.TextStyle8;
import com.liubowang.photoretouch.Style.TextStyle9;
import com.liubowang.photoretouch.Utils.Size;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by heshaobo on 2018/1/24.
 */

public class TextStyleFactory {


    public static TextStyle getStyleWithModel(TextTypeModel model, Context context){
        try {
//            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//            Class styleClass = classLoader.loadClass(model.className);
//            Constructor construct = styleClass.getConstructor(Context.class);
//            TextStyle style = (TextStyle)construct.newInstance(context);
            Class<?> cls = Class.forName(model.className);
            //参数类型
            Class<?>[] params = {Context.class};
            //参数值
            Object[] values = {context};
            //构造有两个参数的构造函数
            Constructor<?> constructor = cls.getDeclaredConstructor(params);
            //根据构造函数，传入值生成实例
            Object object = constructor.newInstance(values);
//            Method getAge = cls.getDeclaredMethod("getAge");
//            Method getName = cls.getDeclaredMethod("getName");
//            System.out.println("getAge = " + (Integer)getAge.invoke(object));
//            System.out.println("getName = " + (String)getName.invoke(object));
            return (TextStyle) object;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addStyleToView(ViewGroup parentView, TextStyle textStyle,RectF rect){
        if (textStyle == null) return;
        int width = (int) rect.right, height = (int) rect.bottom;
        int left = (int) rect.left, top = (int) rect.top;
        int min = Math.min(width,height);
        if (textStyle instanceof TextStyle0){
            width = 0; height = 0;
            left = 0; top = 0;
        }
        else if (textStyle instanceof TextStyle1){
            if (width > height){
                left = left + (width - min) / 2;
            }else {
                top = top + (height - min) / 2;
            }
            width = min; height = min;
        }
        else if (textStyle instanceof TextStyle2){
            if (width > height){
                left = left + (width - min) / 2;
            }else {
                top = top + (height - min) / 2;
            }
            width = min; height = min;
        }
        else if (textStyle instanceof TextStyle3){
            left = left + width / 6 * 5 - 30;
            width = width / 6;
            height = height / 5 * 2;
        }
        else if (textStyle instanceof TextStyle4){
            width = width / 5 * 2;
        }
        else if (textStyle instanceof TextStyle5){
            height = height / 5 * 2;
        }
        else if (textStyle instanceof TextStyle6){
            top = top + height / 10 ;
            left = left + width / 7;
            height = height / 10 * 3;
            width = width / 7 * 5;
        }
        else if (textStyle instanceof TextStyle7){
            if (width < height){
                top = top + (height - min) / 2;
            }
            width = min; height = min;
        }
        else if (textStyle instanceof TextStyle8){
            if (width > height){
                left = left + (width - min) / 2;
            }else {
                top = top + (height - min) / 2;
            }
            width = min; height = min;
        }
        else if (textStyle instanceof TextStyle9){
            top = top + height / 5 * 3;
            height = height / 5 * 2;
        }
        FrameLayout.LayoutParams layoutParams  = new FrameLayout.LayoutParams(width,height);
        layoutParams.topMargin = top;
        layoutParams.leftMargin = left;
        parentView.addView(textStyle,layoutParams);

    }

}
