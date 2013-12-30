package com.batec.game.isomap;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

class IsoThread extends Thread{
    private SurfaceHolder surfaceHolder;
    private IsoPanel isoPanel;
    private boolean run = false;

    public IsoThread(SurfaceHolder surfaceHolder, IsoPanel isoPanel) {
        this.surfaceHolder = surfaceHolder;
        this.isoPanel = isoPanel;
    }

    public void setRunning(boolean run) {
        this.run = run;
    }

    @Override
    public  void run() {
        Canvas c;
        while (this.run) {
            c = null;
            try {
                c = this.surfaceHolder.lockCanvas(null);
                synchronized (this.surfaceHolder) {
                    this.isoPanel.onDraw(c);
                }
            } finally {
                if (c != null) {
                    this.surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

}
    