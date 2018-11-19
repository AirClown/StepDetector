package com.example.yushichao.stepdetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by yushi on 2018/10/25.
 */

public class SensorController implements SensorEventListener {

    private SensorManager manager;

    public interface SensorCallback{
        //加速度
        void refreshAcc(float[] accs);
    }

    private SensorCallback callback;

    public SensorController(SensorManager manager, SensorCallback callback){
        this.manager=manager;
        this.callback=callback;
    }

    //注册传感器
    public boolean registerSensor(int type,int speed){
        if (manager==null) return false;

        Sensor sensor=manager.getDefaultSensor(type);
        manager.registerListener(this,sensor,speed);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (callback==null) return;
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                callback.refreshAcc(event.values.clone());
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
