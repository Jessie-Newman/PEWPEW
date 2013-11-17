package com.example.pewpew;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity implements PictureCallback, SurfaceHolder.Callback{
//	private AudioRecord audioRecorder;
//	private boolean isRecording = false;
//	private int SAMPLE_PER_SEC = 8000;
//	private int minSize;
	private MediaRecorder recorder;
	private Timer timer;
	
	private Camera camera;
	static int bestC = 0;
	static int bestX = 0;
	static int bestY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main).setBackgroundColor(Color.BLACK);
        
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
//        audioRecorder = constructAudioRecorder();
//        MicThread micThread = new MicThread();
//        micThread.run();
        
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.start();
//
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new RecorderTask(recorder), 0, 100);

    }
    
//    private class RecorderTask extends TimerTask {
//        private MediaRecorder recorder;
//
//        public RecorderTask(MediaRecorder recorder) {
//            this.recorder = recorder;
//        }
//
//        public void run() {
//            Log.v("MicInfoService", "amplitude: " + recorder.getMaxAmplitude());
////        	if (recorder.getMaxAmplitude() > recorder.)
//        }
//    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	camera.stopPreview();
		camera.release();
    	
//    	recorder.stop();
//    	timer.cancel();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	camera = Camera.open();
    	SurfaceView sv = (SurfaceView)findViewById(R.id.surfaceView1);
    	sv.getHolder().addCallback(this);
		
//    	recorder.start();
//    	timer.scheduleAtFixedRate(new RecorderTask(recorder), 0, 100);
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
    
    private void fire()
    {
    	camera.takePicture(null, null, null, this);
    }
    
    @Override
	public void onPictureTaken(byte[] picture, Camera camera) {
//    	Camera.Parameters params = camera.getParameters();
//		Camera.Size size = params.getPictureSize();
//		int bytesperpixel = ImageFormat.getBitsPerPixel(params.getPictureFormat())/8;
//		Log.d("PEWPEWpixel", "Format = " + params.getPictureFormat());
//		
//		int x = size.width/2;
//		int y = size.height/2;
//		
//		int index = (y * (bytesperpixel * size.width)) + (x * bytesperpixel);
    	
		Bitmap bmp = BitmapFactory.decodeByteArray(picture, 0, picture.length);
		Log.d("PEWPEWpixel", "pic!");
//		for(int x=0; x < bmp.getWidth(); x++)
//		{
//			for(int y=0; y < bmp.getHeight(); y++)
//			{
//				int c = bmp.getPixel(x, y);
//				if(Color.red(c) > 240 && Color.green(c) < 127 && Color.blue(c) < 127)
//				{
//					Log.d("PEWPEWpixel", "X,Y = "+ x +","+ y + "\n" + Color.red(c) + ", " + Color.green(c) + ", " + Color.blue(c));
//				}
//			}
//		}
		
		int xcenter = 1059;
		int ycenter = 564;
		float hsv[] = {0,0,0};
		final int RADIUS = 50;
		boolean found = false;
		
		for(int x = xcenter-RADIUS; x < xcenter + RADIUS; x++)
		{
			for(int y = ycenter-RADIUS; y < ycenter + RADIUS; y++)
			{
				int c = bmp.getPixel(x, y);
				Color.colorToHSV(c, hsv);
				
				if( (hsv[2] > 0.5) && ( (hsv[0] < 30) || (hsv[0] > 300) ) )
				{
					Log.d("PEWPEWpixel", "HIT!!!!!!!!!");
					Log.d("PEWPEWpixel", "X,Y = "+ x +","+ y + "\n" + hsv[0] + ", " + hsv[1] + ", " + hsv[2]);
					found = true;
					break;
				}
			}
			if(found) break;
		}
		
		Log.d("PEWPEWpixel", "done");
		camera.startPreview();
	}
    
    @Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
    	super.onKeyUp(keyCode, event);
		
		if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			fire();
			return true;
		}
		
		return false;
    }

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			camera.setPreviewDisplay(arg0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}
}
