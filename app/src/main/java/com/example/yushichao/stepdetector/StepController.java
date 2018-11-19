package com.example.yushichao.stepdetector;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by yushi on 2018/11/19.
 */

public class StepController {

    //状态：静止
    public static int STAY=0;

    //状态：运动
    public static int MOVE=1;

    //运动状态判断数组
    private float[] stateValue=new float[20];

    //加速度数组
    private float[] Accs=new float[100];

    //每次传入加速度的时间，省去定时器
    private long[] time=new long[100];

    //数组下标
    private int count=0;

    //行走步数
    private int Step=0;

    //步长
    private float Length=0;

    //行进距离
    private float Distance=0;

    //状态
    private int State=STAY;

    //回调
    public interface StepCallback{
        void refreshStep(int step,float stepLength,float distance);
        void setText(String text);
        void drawData(float[] data);
    }
    private StepCallback callback;

    public StepController(StepCallback callback){
        this.callback=callback;
    }

    public void refreshAcc(float[] values,long timestamp){
        Accs[count]=(float) Math.sqrt(values[0]*values[0]+
                values[1]*values[1]+
                values[2]*values[2]);
        time[count]=timestamp;

        //检查运动状态
        stateValue[count%20]=Accs[count];
        checkState();

        //设置检测点，检测点的作用是来减少计算量
        final int ckpoint = Accs.length / 5;

        //运动状态判断加上判断检测点是否有波谷存在
        if (State==MOVE&&Accs[(count-ckpoint+Accs.length)%Accs.length]<Accs[(count-ckpoint+Accs.length+1)%Accs.length]
                &&Accs[(count-ckpoint+Accs.length)%Accs.length]<Accs[(count-ckpoint+Accs.length-1)%Accs.length]) {
            //求均值
            float ave = Utils.ave(Accs);
            for (int i = 0; i < Accs.length; ++i) {
                ave += Accs[i];
            }
            ave /= Accs.length;

            //调整数组顺序，把新数据放在前面，同时把数据减去均值
            float[] data = new float[Accs.length];
            for (int i = 0, j = count; i < data.length; ++i, --j) {
                if (j < 0) j += data.length;
                data[i] = Accs[j] - ave;
            }

            //寻找波峰波谷
            float[] sign = new float[Accs.length];
            for (int i = 1; i < Accs.length - 1; i++) {
                if (Math.abs(data[i]) > 0.8 && Math.abs(2 * data[i]) > Math.abs((data[i - 1] + data[i + 1]))) {
                    if (data[i] > 0) {
                        sign[i] = 1;
                    } else {
                        sign[i] = -1;
                    }
                }
            }

            //取相邻波峰中的最大值和相邻波谷中的最小值
            for (int i = 1; i < sign.length - 1; ) {
                int index = i;
                while (++i < sign.length - 1 && (sign[i] == 0 || sign[i] == sign[index])) {
                    if (sign[i] != 0 && Math.abs(data[i]) > Math.abs(data[index])) {
                        sign[index] = 0;
                        index = i;
                    } else {
                        sign[i] = 0;
                    }
                }
            }

            //再次判断检测点是否是波谷
            if (sign[ckpoint] < 0) {
                int index = ckpoint;

                //寻找下个波峰
                while (++index < sign.length && sign[index] == 0) ;

                if (index < sign.length && sign[index] > 0) {
                    int peak = index;

                    //寻找下个波谷
                    while (++index < sign.length && sign[index] == 0) ;

                    if (index < sign.length && sign[index] < 0) {
                        int valley = index;

                        //计算H和T
                        float H = data[peak] - 0.5f * data[ckpoint] - 0.5f * data[valley];
                        long T = time[(count - ckpoint + time.length) % time.length] - time[(count - valley + time.length) % time.length];

                        //门限判决
                        if (H > 3 && T > 300 && T < 1400) {
                            //步长计算
                            DetectStepLength((int) T, H);
                            ++Step;
                            callback.refreshStep(Step,Length,Distance);
                        }
                    }
                }
            }

            callback.drawData(sign);
        }

        if (++count==Accs.length) count=0;
    }

    //运动状态判断
    private void checkState(){
        float var=Utils.var(stateValue,Utils.ave(stateValue));

        //状态判决
        State=var>0.5?MOVE:STAY;
    }

    //步长计算,该公式利用最小二乘法推导出,有一定可信性
    private void DetectStepLength(int time,float f){
        float steplength=0.35f-0.000155f*time+0.1638f*(float) Math.sqrt(f);
        this.Length=(this.Length+steplength)/2;
        Distance+=steplength;
    }
}
