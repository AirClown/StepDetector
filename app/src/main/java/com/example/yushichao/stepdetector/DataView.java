package com.example.yushichao.stepdetector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

public class DataView extends View {

    private int Length=0;
    private String text;
    private float[] data;
    private float[] data2;
    private int dataIndex;
    private Queue<float[]> datas=new LinkedList<>();

    public DataView(Context context) {
        super(context);
    }

    public DataView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLength(int length){
        this.Length=length;
        data=new float[Length];
        dataIndex=0;
    }

    public void setText(String text){
        this.text=text;
    }

    public void setDatas(float[] values){
        if (Length>0){
            if(datas.size()==Length) datas.remove();
            datas.offer(values.clone());
        }
    }

    public void setData(float values){
        if (Length>0){
            data[dataIndex]=values;
            if (++dataIndex==Length) dataIndex=0;
        }
    }

    public void setDatax(float[] value){
        this.data2=value;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (Length>0) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(10);
            paint.setTextSize(100);
            paint.setStyle(Paint.Style.STROKE);

            canvas.drawLine(width/2,0,width/2,height,paint);

            float dx=(width-100)/40;
            float dy=height/Length;

            if (datas.size() > 0) {
                int[] color={Color.RED,Color.GREEN,Color.BLUE};
                for (int i=0;i<color.length;i++){
                    Path path=new Path();
                    int index=datas.size();
                    path.moveTo(width/2,index*dy);
                    for (float[] d:datas){
                        --index;
                        path.lineTo((d[i]*dx+width/2),index*dy);
                    }
                    paint.setColor(color[i]);
                    canvas.drawPath(path,paint);
                }
            }

            if (data!=null){
                Path path=new Path();
                path.moveTo(width/2,0);
                for(int i=0,j=dataIndex-1;i<data.length;++i,--j){
                    if (j<0) j=data.length-1;
                    path.lineTo(data[j]*dx+width/2,i*dy);
                }
                paint.setColor(Color.GRAY);
                canvas.drawPath(path,paint);
            }

            if (data2!=null){
                for(int i=0,j=dataIndex-1;i<data.length;++i,--j){
                    if (j<0) j=data.length-1;
                    if (data2[i]!=0){
                        if (data2[i]>0) {
                            paint.setColor(Color.GREEN);
                        }else{
                            paint.setColor(Color.BLUE);
                        }
                        canvas.drawPoint(data[j]*dx+width/2,i*dy,paint);
                    }
                }
            }

            if (text!=null){
                paint.setStrokeWidth(5);
                paint.setAlpha(10);
                paint.setColor(Color.RED);
                canvas.drawText(text,0,height/2,paint);
            }

            data2=null;
        }
    }
}
