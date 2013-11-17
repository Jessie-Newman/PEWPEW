package com.example.pewpew;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity implements PictureCallback, SurfaceHolder.Callback{
	private MediaRecorder recorder;
	private Timer timer;
	
	private static final int AMPLITUDE = 6000;
	
	private Camera camera;
	static Bitmap bmp = null;
	static boolean firing = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        View mainView = findViewById(R.id.main);
        mainView.setBackgroundColor(Color.BLACK);

        recorder = setupRecorder();
        timer = new Timer();
        
        // We'll just pretend the next 16 lines don't exist
        findViewById(R.id.imageView2).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView3).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView4).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView5).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView6).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView7).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView8).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView9).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView10).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView11).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView12).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView13).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView14).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView15).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView16).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView17).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageView18).setVisibility(View.INVISIBLE);

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
        private MainActivity mainact;

        public RecorderTask(MediaRecorder recorder, MainActivity mainact) {
            this.recorder = recorder;
            this.mainact = mainact;
        }

        public void run() {
//            Log.v("MicInfoService", "amplitude: " + recorder.getMaxAmplitude());
        	if (recorder.getMaxAmplitude() > AMPLITUDE) {
        		Log.v("MicInfoService", "PEW");
        		if(!firing) mainact.fire();
        	}
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	camera.stopPreview();
		camera.release();
    	
    	recorder.stop();
    	timer.cancel();
    	
    	finish();
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
    	timer.scheduleAtFixedRate(new RecorderTask(recorder, this), 0, 100);
    	
    	camera = Camera.open();
    	SurfaceView sv = (SurfaceView)findViewById(R.id.surfaceView1);
    	sv.getHolder().addCallback(this);
    }
    
    public void fire()
    {
    	firing = true;
    	camera.takePicture(null, null, null, this);
    }
    
    private void hit()
    {
    	Log.d("PEWPEWpixel", "HIT!!!!!!!!!");
    	
		playAnimation();

		MediaPlayer mp = MediaPlayer.create(this, R.raw.explode);
        mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }

        });   
        mp.start();
    }
    
    @Override
	public void onPictureTaken(byte[] picture, Camera camera) {
    	Log.d("PEWPEWpixel", "Picture taken");
    	
    	final int xcenter = 1059;
		final int ycenter = 564;
		final int RADIUS = 15;
		
    	try {
			BitmapRegionDecoder brd = BitmapRegionDecoder.newInstance(picture, 0, picture.length, true);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inBitmap = bmp;
			opt.inSampleSize = 1;
			bmp = brd.decodeRegion(new Rect(xcenter-RADIUS, ycenter-RADIUS, xcenter+RADIUS, ycenter+RADIUS), opt);
			brd.recycle();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		float hsv[] = {0,0,0};
		boolean found = false;
		
		for(int x = 0; x < bmp.getWidth(); x++)
		{
			for(int y = 0; y < bmp.getHeight(); y++)
			{
				int c = bmp.getPixel(x, y);
				Color.colorToHSV(c, hsv);
				
				if( (hsv[2] > 0.5) && (hsv[1] > 0.25) && ( (hsv[0] < 25) || (hsv[0] > 300) ) )
				{
					
					hit();
					Log.d("PEWPEWpixel", "X,Y = "+ x +","+ y + "\n" + hsv[0] + ", " + hsv[1] + ", " + hsv[2]);
					found = true;
					break;
				}
			}
			if(found) break;
		}
		
		if(!found)
		{
			try {
                Uri notification = Uri.fromFile(new File(
                                "/system/media/audio/ui/sound_disallowed_action.ogg"));
                Ringtone r = RingtoneManager.getRingtone(
                                getApplicationContext(), notification);
                r.play();
			} catch (Exception e) {}
		}
		Log.d("PEWPEWpixel", "done");
		camera.startPreview();
		firing = false;
	}
    
    @Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
    	super.onKeyUp(keyCode, event);
		
		if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			if(!firing) fire();
			return true;
		}
		
		return false;
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
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {}
	
	/**
	 * Warning. Reading this terrible code may ruin your life.
	 */
	private void playAnimation() {
		final Handler handler = new Handler();

		Runnable r = new Runnable() {
	        @Override
	        public void run() {
	        	Log.d("explosion", "run");
	        	if (findViewById(R.id.imageView2).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView2).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView3).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView3).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView4).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView4).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView4).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView5).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView5).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView5).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView6).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView6).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView6).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView7).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView7).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView7).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView8).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView8).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView8).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView9).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView9).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView9).setVisibility(View.INVISIBLE);
		   	    	return;
	        	} else if (findViewById(R.id.imageView10).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView10).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView11).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView11).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView11).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView12).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView12).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView12).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView13).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView13).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView13).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView14).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView14).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView14).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView15).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView15).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView15).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView16).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView16).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView16).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView17).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView17).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView17).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView18).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else if (findViewById(R.id.imageView18).getVisibility() == View.VISIBLE) {
	        		findViewById(R.id.imageView18).setVisibility(View.INVISIBLE);
	        		findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
		   	    	return;
	        	} else {
	        		findViewById(R.id.imageView10).setVisibility(View.VISIBLE);
		   	    	handler.postDelayed(this, 100);
	        	}
	        }
		};
	    
	    handler.postDelayed(r, 100);
	}
	
	
}
