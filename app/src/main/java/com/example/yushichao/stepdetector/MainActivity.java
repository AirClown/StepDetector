package com.example.yushichao.stepdetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    };

    private StepController stepController;
    private StepController.StepCallback stepCallback=new StepController.StepCallback() {
        @Override
        public void refreshStep(int step, float stepLength, float distance) {

            dv.setText(step+"/"+stepLength);
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
    private Button bt;

    private boolean stop=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorController=new SensorController((SensorManager)getSystemService(Context.SENSOR_SERVICE),sensorCallback);
        sensorController.registerSensor(Sensor.TYPE_ACCELEROMETER,SensorManager.SENSOR_DELAY_UI);

        stepController=new StepController(stepCallback);

        dv=findViewById(R.id.dataview);
        dv.setLength(100);

        bt=findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop=!stop;
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
