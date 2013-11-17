package com.example.pewpew;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
//	private AudioRecord audioRecorder;
//	private boolean isRecording = false;
//	private int SAMPLE_PER_SEC = 8000;
//	private int minSize;
	private MediaRecorder recorder;
	private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main).setBackgroundColor(Color.BLACK);
        
//        audioRecorder = constructAudioRecorder();
//        MicThread micThread = new MicThread();
//        micThread.run();
        
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.start();

        timer = new Timer();
        timer.scheduleAtFixedRate(new RecorderTask(recorder), 0, 100);

    }
    
    private class RecorderTask extends TimerTask {
        private MediaRecorder recorder;

        public RecorderTask(MediaRecorder recorder) {
            this.recorder = recorder;
        }

        public void run() {
            Log.v("MicInfoService", "amplitude: " + recorder.getMaxAmplitude());
//        	if (recorder.getMaxAmplitude() > recorder.)
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
    	recorder.start();
    	timer.scheduleAtFixedRate(new RecorderTask(recorder), 0, 100);
    }
    
    

//    private AudioRecord constructAudioRecorder() {
//        minSize = AudioRecord.getMinBufferSize(SAMPLE_PER_SEC, 
//        		AudioFormat.CHANNEL_IN_MONO,             
//        		AudioFormat.ENCODING_PCM_16BIT);     
//        AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_PER_SEC,
//        		AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize * 10);
//        audioRecorder.startRecording();
//        isRecording = true;
//        return audioRecorder;
//    }
//    
//    private class MicThread implements Runnable {
//
//		@Override
//		public void run() {
//			while (isRecording && audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
//	        {
//	            short recordedData[] = new short[minSize];
//	            audioRecorder.read(recordedData, 0, recordedData.length); 
//	            //fun
//	        }			
//		}
//    }
    
}
