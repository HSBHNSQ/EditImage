package com.lafonapps.common.utils;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Random;

/**
 * Created by chenjie on 2017/10/19.
 */

public class AnimationPerformer {

    private final static String TAG = AnimationPerformer.class.getCanonicalName();

    private int duration = 1200;
    private int repeat = 0;
    private int delay = 0;

    public void performAnimation(@NonNull View view, AnimationType type) {
        Techniques technique = type.getTechniques();
        YoYo.with(technique)
                .duration(duration)
                .repeat(repeat)
                .delay(delay)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(view);
    }

    public void performRandomAnimation(@NonNull View view) {
        int randomInt = new Random().nextInt(AnimationType.values().length);
        AnimationType type = AnimationType.values()[randomInt];

        performAnimation(view, type);
    }

    public enum AnimationType {

        DropOut(Techniques.DropOut),
        Landing(Techniques.Landing),
//        TakingOff(Techniques.TakingOff),

//        Flash(Techniques.Flash),
//        Pulse(Techniques.Pulse),
        RubberBand(Techniques.RubberBand),
        Shake(Techniques.Shake),
        Swing(Techniques.Swing),
        Wobble(Techniques.Wobble),
        Bounce(Techniques.Bounce),
        Tada(Techniques.Tada),
        StandUp(Techniques.StandUp),
//        Wave(Techniques.Wave),

//        Hinge(Techniques.Hinge),
        RollIn(Techniques.RollIn),
//        RollOut(Techniques.RollOut),

        BounceIn(Techniques.BounceIn),
//        BounceInDown(Techniques.BounceInDown),
        BounceInLeft(Techniques.BounceInLeft),
        BounceInRight(Techniques.BounceInRight),
        BounceInUp(Techniques.BounceInUp),

//        FadeIn(Techniques.FadeIn),
        FadeInUp(Techniques.FadeInUp),
        FadeInDown(Techniques.FadeInDown),
        FadeInLeft(Techniques.FadeInLeft),
        FadeInRight(Techniques.FadeInRight),

//        FadeOut(Techniques.FadeOut),
//        FadeOutDown(Techniques.FadeOutDown),
//        FadeOutLeft(Techniques.FadeOutLeft),
//        FadeOutRight(Techniques.FadeOutRight),
//        FadeOutUp(Techniques.FadeOutUp),

        FlipInX(Techniques.FlipInX),
//        FlipOutX(Techniques.FlipOutX),
        FlipInY(Techniques.FlipInY),
//        FlipOutY(Techniques.FlipOutY),
//        RotateIn(Techniques.RotateIn),
        RotateInDownLeft(Techniques.RotateInDownLeft),
        RotateInDownRight(Techniques.RotateInDownRight),
        RotateInUpLeft(Techniques.RotateInUpLeft),
        RotateInUpRight(Techniques.RotateInUpRight),

//        RotateOut(Techniques.RotateOut),
//        RotateOutDownLeft(Techniques.RotateOutDownLeft),
//        RotateOutDownRight(Techniques.RotateOutDownRight),
//        RotateOutUpLeft(Techniques.RotateOutUpLeft),
//        RotateOutUpRight(Techniques.RotateOutUpRight),

//        SlideInLeft(Techniques.SlideInLeft),
//        SlideInRight(Techniques.SlideInRight),
//        SlideInUp(Techniques.SlideInUp),
//        SlideInDown(Techniques.SlideInDown),

//        SlideOutLeft(Techniques.SlideOutLeft),
//        SlideOutRight(Techniques.SlideOutRight),
//        SlideOutUp(Techniques.SlideOutUp),
//        SlideOutDown(Techniques.SlideOutDown),

        ZoomIn(Techniques.ZoomIn),
        ZoomInDown(Techniques.ZoomInDown),
        ZoomInLeft(Techniques.ZoomInLeft),
        ZoomInRight(Techniques.ZoomInRight),
        ZoomInUp(Techniques.ZoomInUp);

//        ZoomOut(Techniques.ZoomOut),
//        ZoomOutDown(Techniques.ZoomOutDown),
//        ZoomOutLeft(Techniques.ZoomOutLeft),
//        ZoomOutRight(Techniques.ZoomOutRight),
//        ZoomOutUp(Techniques.ZoomOutUp);


        private Techniques techniques;

        private AnimationType(Techniques techniques) {
            this.techniques = techniques;
        }

        public Techniques getTechniques() {
            return techniques;
        }
    }

}
