package com.example.pewpew;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private MediaRecorder recorder;
	private Timer timer;
	
	private static final int AMPLITUDE = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        findViewById(R.id.main).setBackgroundColor(Color.BLACK);

        recorder = setupRecorder();
        timer = new Timer();
    }
    
    private MediaRecorder setupRecorder() {
    	recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile("/dev/null");
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        return recorder;
    }
    
    private class RecorderTask extends TimerTask {
        private MediaRecorder recorder;

        public RecorderTask(MediaRecorder recorder) {
            this.recorder = recorder;
        }

        public void run() {
//            Log.v("MicInfoService", "amplitude: " + recorder.getMaxAmplitude());
        	if (recorder.getMaxAmplitude() > AMPLITUDE) {
        		Log.v("MicInfoService", "PEW");
        	}
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	recorder.stop();
    	timer.cancel();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if (recorder == null) {
    		recorder = setupRecorder();
    	}
    	
    	try {
    		recorder.prepare();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	recorder.start();
    	timer.scheduleAtFixedRate(new RecorderTask(recorder), 0, 100);
    }
}