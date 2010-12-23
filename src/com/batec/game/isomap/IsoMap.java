package com.batec.game.isomap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class IsoMap extends Activity {
    /** Called when the activity is first created. */
    private IsoThread _thread;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new IsoPanel(this));
    }
    
    class IsoPanel extends SurfaceView implements SurfaceHolder.Callback {
    	public Bitmap[] tiles = new Bitmap[51] ;
    	public Map worldMap;
    	float offX = 0;
    	float offY = 0;
    	long lastTickMs = 0;;
    	
    	public IsoPanel (Context context) {
    		super(context);
    		getHolder().setKeepScreenOn(true);
    		getHolder().setFixedSize(320, 240);
    		getHolder().addCallback(this);
    		_thread = new IsoThread(getHolder(),this);
    		
    		worldMap = new Map(BitmapFactory.decodeResource(getResources(), R.drawable.heightmap2),BitmapFactory.decodeResource(getResources(), R.drawable.objmap));
    		    		//BitmapFactory.Options o = new BitmapFactory.Options();
    		    		
    		tiles[0] = BitmapFactory.decodeResource(getResources(), R.drawable.t010);
    		tiles[1] = BitmapFactory.decodeResource(getResources(), R.drawable.t012);
    		tiles[2] = BitmapFactory.decodeResource(getResources(), R.drawable.t011);
    		tiles[3] = BitmapFactory.decodeResource(getResources(), R.drawable.t013);
    		tiles[4] = BitmapFactory.decodeResource(getResources(), R.drawable.t001);
    		tiles[10] = BitmapFactory.decodeResource(getResources(), R.drawable.obj1);
    		tiles[50] = BitmapFactory.decodeResource(getResources(), R.drawable.obj2);
    		tiles[33] = BitmapFactory.decodeResource(getResources(), R.drawable.obj3);
    	}
    	
    	@Override
    	public boolean onTouchEvent(MotionEvent event) 
    	{
    	        //int touchX = (int) (event.getX()/320)*240;
    	        //int touchY = (int) (event.getY()/240)*320;
    	        //Log.d("SIZE",Integer.toString((int)event.getSize()));
    	        int touchX = (int) (event.getX()/320*240);
    	        int touchY = (int) (event.getY()/440*320);
    	        
    	        float c = 0.5f;
    	        
    	        if(touchX < 50){
    	        	offX = offX -c;
    	        	offY = offY +c;
    	        }
    	        if(touchX > 270){
    	        	offY = offY -c;
    	        	offX = offX +c;
    	        }
    	        if(touchY < 50){
    	        	offX = offX -c;
    	        	offY = offY -c;
    	        }
    	        if(touchY > 190){
    	        	offY = offY +c;
    	        	offX = offX +c;
    	        }
    	        
    	        //offX = touchX;
    	        //offY = touchY;
    	        
    	        /*
    	        if (touchX < 50)
    	        	offX = posX - 0.01f;
    	        if (touchX > 240-50)
    	        	posX = posX + 0.01f;
    	        if (touchY < 50)
    	        	posY = posY - 0.01f;
    	        if (touchY > 320-50)
    	        	posY = posY + 0.01f;
    		 */
            return true;
    	}
    	
    	@Override
    	public void onDraw(Canvas canvas) {
    		canvas.drawColor(Color.BLACK);
    		float renX = 0; //1/2*offX + 1/2*offY;
    		float renY = 0;
    		
    		float offScreenY = 0;
    		float offScreenX = 0;
    		
    		int tOffX = (int)offX;
    		int tOffY = (int)offY;
    		
    		for (int y = 0; y < 18; y++){
        		renX = (320 / 2) - 10;
        		renY = 30;
        		renX = renX - (y * 10);
        		renY = renY + (y * 30 /3 /2);
    			
    			for (int x = 0; x < 18; x++){
    				renX = renX + 20 / 2;
    				renY = renY + 30 / 3 / 2;
    				
    				int i = 0;
    				if(worldMap.getHeight(x + tOffX,y + tOffY) > worldMap.getHeight(x + tOffX, y  + tOffY -1) ){
    					i = 2;}
    				if(worldMap.getHeight(x + tOffX,y + tOffY) > worldMap.getHeight(x + tOffX -1, y + tOffY) ){
    					i = 1;}
    				if(worldMap.getHeight(x + tOffX,y + tOffY) > worldMap.getHeight(x + tOffX, y + (int)offY -1) &&worldMap.getHeight(x + tOffX,y + tOffY) > worldMap.getHeight(x + tOffX -1, y + tOffY)){
    					i = 3;}
    				if(worldMap.getHeight(x + tOffX, y + tOffY) == 0){
    					i = 4;}
    				canvas.drawBitmap(tiles[i], renX + offScreenX, renY + offScreenY - (worldMap.getHeight(x + tOffX, y + tOffY) * 2), null);
    				
    				if(worldMap.getObject(x + tOffX, y + tOffY) > 0 && worldMap.getHeight(x + tOffX, y + tOffY) > 0){
    					//Log.d("SIZE", Integer.toString(worldMap.getObject(x + offX, y + offY)));
    					canvas.drawBitmap(tiles[worldMap.getObject(x + tOffX, y + tOffY)], renX, renY - (worldMap.getHeight(x + tOffX, y + tOffY) * 2) -20, null);
    					
    				}
    				
    				if(x ==8 && y == 8)
    				{
    					canvas.drawBitmap(tiles[33], renX, renY - (worldMap.getHeight(x + tOffX, y + tOffY) * 2) -20, null);
    				}
    			}
    		}
    		Paint p = new Paint();
    		p.setColor(Color.RED);
    		/*
    		canvas.drawPoint(offX + 1, offY +1, p);
    		canvas.drawPoint(offX-1, offY-1, p);
    		canvas.drawPoint(offX, offY, p);
    		canvas.drawPoint(offX+1, offY-1, p);
    		canvas.drawPoint(offX-1, offY+1, p);
    		*/
    		
    		
    		int fps =  (int)(1000/(System.currentTimeMillis() - lastTickMs)); 
    		canvas.drawText(Integer.toString(fps) + " fps", 280, 200, p);
    		//canvas.drawText((offX + " " + offY), 150, 100, p);
    		lastTickMs = System.currentTimeMillis(); 
    	}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			_thread.setRunning(true);
			_thread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			boolean retry = true;
			_thread.setRunning(false);
			while (retry){
				try{
					_thread.join();
					retry = false;
				} catch (InterruptedException e) {
					
				}
			}
		}
    }
    
    class IsoThread extends Thread{
    	private SurfaceHolder _surfaceHolder;
    	private IsoPanel _isoPanel;
    	private boolean _run = false;
    
    	public IsoThread(SurfaceHolder surfaceHolder, IsoPanel isoPanel){
    		_surfaceHolder = surfaceHolder;
    		_isoPanel = isoPanel;
    	}
    
    	public void setRunning(boolean run){
    		_run = run;
    	}
    	
    	@Override
    	public  void run() {
    		Canvas c;
    		while (_run){
    			c = null;
    			try {
    				c = _surfaceHolder.lockCanvas(null);
    				synchronized (_surfaceHolder) {
    					_isoPanel.onDraw(c);
    				}
    			} finally {
					if (c != null) {
						_surfaceHolder.unlockCanvasAndPost(c);
					}
    			}
    		}
    	}
   
    }
    
}