package com.example.yushichao.stepdetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SensorController sensorController;
    private SensorController.SensorCallback sensorCallback=new SensorController.SensorCallback() {
        @Override
        public void refreshAcc(float[] accs) {
            if (stepController!=null){
                stepController.refreshAcc(accs,System.currentTimeMillis());
            }

            if (dv!=null){
                dv.setData(Utils.d(accs));
                if (!stop) {
                    dv.invalidate();
                }
            }
        }

        @Override
        public void refreshStep(int step) {
            Rsysstep=step;
            if (Osysstep==0){
                Osysstep=step;
            }else{
                tv.setText("系统计步器:"+(Rsysstep-Osysstep)+ "\n算法计步器:"+stepController.Step+
                "\nT:"+stepController.value[0]+"\nH:"+stepController.value[1]);
            }
        }
    };

    private StepController stepController;
    private StepController.StepCallback stepCallback=new StepController.StepCallback() {
        @Override
        public void refreshStep(int step, float stepLength, float distance) {
            Rmystep=step;
            tv.setText("系统计步器:"+(Rsysstep-Osysstep)+ "\n算法计步器:"+stepController.Step+
                    "\nT:"+stepController.value[0]+"\nH:"+stepController.value[1]);
        }

        @Override
        public void setText(String text) {
        }

        @Override
        public void drawData(float[] data) {
            dv.setDatax(data);
        }
    };

    private DataView dv;
    private Button bt,bt2;
    private TextView tv;

    private boolean stop=false;

    private int Osysstep=0;
    private int Omystep=0;
    private int Rsysstep=0;
    private int Rmystep=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sensorController=new SensorController((SensorManager)getSystemService(Context.SENSOR_SERVICE),sensorCallback);
        sensorController.registerSensor(Sensor.TYPE_ACCELEROMETER,SensorManager.SENSOR_DELAY_UI);
        sensorController.registerSensor(Sensor.TYPE_STEP_COUNTER,SensorManager.SENSOR_DELAY_UI);

        stepController=new StepController(stepCallback);

        dv=findViewById(R.id.dataview);
        dv.setLength(100);

        tv=findViewById(R.id.textView);

        bt=findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop=!stop;
            }
        });

        bt2=findViewById(R.id.button2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepController.Step=0;
                stepController.value[0]=0;
                stepController.value[1]=0;
                Osysstep=Rsysstep;

                tv.setText("系统计步器:"+(Rsysstep-Osysstep)+ "\n算法计步器:"+stepController.Step+
                        "\nT:"+stepController.value[0]+"\nH:"+stepController.value[1]);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //关闭标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //关闭状态栏
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(flag);
        }
    }
}
