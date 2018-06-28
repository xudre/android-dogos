package com.xudre.dogosfromouterspace;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class Button extends Sprite {

    public boolean pressing;

    public Button(Bitmap bitmap) {
        super(bitmap);
    }

    public void ProcessEvents(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressing = Hit(x, y);

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                pressing = false;

                break;
            case MotionEvent.ACTION_MOVE:
                pressing = Hit(x, y);

                break;
        }
    }

    @Override
    public void Draw(Canvas canvas) {
        if (pressing) {
            Paint.setAlpha(0x99);
        } else {
            Paint.setAlpha(0xFF);
        }

        super.Draw(canvas);
    }
}
