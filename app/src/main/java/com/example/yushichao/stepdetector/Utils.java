package com.example.yushichao.stepdetector;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yushi on 2018/9/19.
 */

public class Utils {
    public static float var(float[] value,float ave){
        float re=0;

        for (int i=0;i<value.length;i++){
            re+=(value[i]-ave)*(value[i]-ave);
        }

        return re;
    }

    public static float ave(float[] value){
        float re=0;
        for (int i=0;i<value.length;++i){
            re+=value[i];
        }
        return re/value.length;
    }

    public static float d(float[] value){
        float re=0;
        for (float x:value){
            re+=x*x;
        }
        return (float)Math.sqrt(re);
    }
}
