package com.xudre.dogosfromouterspace;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Shoot extends Sprite {

    public boolean Hidden;

    public Shoot(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public void Draw(Canvas canvas) {
        if (Hidden) return;

        super.Draw(canvas);
    }

}
