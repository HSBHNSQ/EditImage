package com.liubowang.editimage.Mosaic;

import android.graphics.Rect;

/**
 * Created by zhaoya on 2017/10/24.
 */

public class GeometryHelper {
    //判断点在直线的左侧或上或右侧
    //返回值 ：-1：点在线段左侧 0：点在线段上 1：点在线段右侧
    public static int PointAtLineLeftRight(Point start, Point end, Point test) {
        start.x -= test.x;
        start.y -= test.y;
        end.x -= test.x;
        end.y -= test.y;

        float ret = start.x * end.y - start.y * end.x;
        if (ret == 0) {
            return 0;
        } else if (ret > 0) {
            return 1;
        } else if (ret < 0) {
            return -1;
        }
        return 0;
    }

    //判断两条线段是否相交
    public static Boolean IsTwoLineIntersect(Point start1, Point end1, Point start2, Point end2) {
        int nLine1Start = PointAtLineLeftRight(start2.clone(), end2.clone(), start1.clone());
        int nLine1End = PointAtLineLeftRight(start2.clone(), end2.clone(), end1.clone());
        if (nLine1Start * nLine1End > 0)
            return false;
        int nLine2Start = PointAtLineLeftRight(start1.clone(), end1.clone(), start2.clone());
        int nLine2End = PointAtLineLeftRight(start1.clone(), end1.clone(), end2.clone());
        if (nLine2Start * nLine2End > 0)
            return false;
        return true;
    }

    //判断线段与矩形是否相交
    public static Boolean IsLineIntersectRect(Point start, Point end, Rect rect) {
        // 直线的一端在矩形内 直线的两端都在矩形内
        if (IsPointInRect(rect, start) || IsPointInRect(rect, end)) {
            return true;
        }

        // 直线的两端都不在矩形内，判断直线是否与矩形的四条边相交
        if (IsTwoLineIntersect(start, end, new Point(rect.left, rect.top), new Point(rect.left, rect.bottom))) {
            return true;
        }
        if (IsTwoLineIntersect(start, end, new Point(rect.left, rect.bottom), new Point(rect.right, rect.bottom))) {
            return true;
        }

        if (IsTwoLineIntersect(start, end, new Point(rect.right, rect.bottom), new Point(rect.right, rect.top))) {
            return true;
        }
        if (IsTwoLineIntersect(start, end, new Point(rect.left, rect.top), new Point(rect.right, rect.top))) {
            return true;
        }
        return false;
    }

    private static Boolean IsPointInRect(Rect rect, Point test) {
        if (test.x >= (float) rect.left && test.x <= (float) rect.right && test.y >= (float) rect.top && test.y <= (float) rect.bottom) {
            return true;
        }
        return false;
    }
}
