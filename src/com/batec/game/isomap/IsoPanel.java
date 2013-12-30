package com.batec.game.isomap;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class IsoPanel extends SurfaceView implements SurfaceHolder.Callback {
    public Bitmap[] tiles = new Bitmap[51] ;
    public Map worldMap;
    float offX = 0;
    float offY = 0;
    long lastTickMs = 0;;

    private IsoThread thread;

    public IsoPanel (Context context) {
        super(context);
        this.getHolder().setKeepScreenOn(true);
        this.getHolder().setFixedSize(1280, 720);
        this.getHolder().addCallback(this);
        this.thread = new IsoThread(getHolder(),this);

        worldMap = new Map(
                BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.heightmap2),
                BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.objmap
                ));

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
        int touchX = (int) (event.getX()/320 * 720);
        int touchY = (int) (event.getY()/530*1200);

        Log.println(Log.DEBUG, "event", (int)(event.getX()) + " " + (int)(event.getY()));

        float c = 1.0f;

        if(touchX < 200){
            offX = offX -c;
            offY = offY +c;
        }
        if(touchX > 1080){
            offY = offY -c;
            offX = offX +c;
        }
        if(touchY < 200){
            offX = offX -c;
            offY = offY -c;
        }
        if(touchY > 520){
            offY = offY +c;
            offX = offX +c;
        }

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        //canvas.drawBitmap(bg,0,0,null);

        float renX = 0; //1/2*offX + 1/2*offY;
        float renY = 0;

        // X = ($scale / 2 * ($offx + $offy))+ $off,
        // Y = ($scale /4 * ($offx - $offy))+ $off -25,
        float offScreenY =  ((offX - ((int)offX))*1 + (offY - ((int)offY)))*1 ;
        float offScreenX = ((offX - ((int)offX))*1 - (offY - ((int)offY)))*1 ;
        offScreenX *=-1;
        offScreenY *=-1;
        offScreenX /= 2;
        offScreenY /= 4;
        int amount = 0;

        int tOffX = (int)offX;
        int tOffY = (int)offY;

        for (int y = 0; y < 80; y++){
            renX = (1280 / 2) - 10;
            renY = -70;
            renX = renX - (y * 10);
            renY = renY + (y * 30 /3 /2);

            for (int x = 0; x < 80; x++){
                renX = renX + 20 / 2;
                renY = renY + 30 / 3 / 2;

                if (renX > -10 && renY > -5 && renX < 1028 && renY < 720)
                {
                    amount += 1;
                    int i = 0;
                    if (worldMap.getHeight(x + tOffX,y + tOffY) > worldMap.getHeight(x + tOffX, y  + tOffY -1) ) {
                        i = 2;
                    }
                    if (worldMap.getHeight(x + tOffX,y + tOffY) > worldMap.getHeight(x + tOffX -1, y + tOffY) ) {
                        i = 1;
                    }
                    if (
                            worldMap.getHeight(x + tOffX,y + tOffY) > worldMap.getHeight(x + tOffX, y + (int)offY -1)
                            && worldMap.getHeight(x + tOffX,y + tOffY) > worldMap.getHeight(x + tOffX -1, y + tOffY)
                    ) {
                        i = 3;
                    }
                    if (worldMap.getHeight(x + tOffX, y + tOffY) == 0) {
                        i = 4;
                    }

                    canvas.drawBitmap(
                            tiles[i],
                            renX,
                            renY - (worldMap.getHeight(x + tOffX, y + tOffY) * 2),
                            null
                    );

                    if (worldMap.getObject(x + tOffX, y + tOffY) > 0 && worldMap.getHeight(x + tOffX, y + tOffY) > 0) {
                        canvas.drawBitmap(
                                tiles[worldMap.getObject(x + tOffX, y + tOffY)],
                                renX,
                                renY - (worldMap.getHeight(x + tOffX, y + tOffY) * 2) -20,
                                null
                        );
                    }

                    if (x ==8 && y == 8) {
                        canvas.drawBitmap(
                                tiles[33],
                                renX + offScreenX,
                                renY + offScreenY - (worldMap.getHeight(x + tOffX, y + tOffY) * 2) -20,
                                null)
                        ;
                    }
                }
            }
        }

        Paint p = new Paint();
        p.setColor(Color.RED);

        int fps =  (int)(1000/(System.currentTimeMillis() - lastTickMs));
        canvas.drawText(Integer.toString(fps) + " fps", 680, 200, p);
        canvas.drawText("x " + Float.toString(offX), 680, 180, p);
        canvas.drawText("y " + Float.toString(offY), 680, 160, p);
        canvas.drawText("tiles: " + Integer.toString(amount), 280, 140, p);
        //canvas.drawText((offX + " " + offY), 150, 100, p);
        lastTickMs = System.currentTimeMillis();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry){
            try{
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }
}