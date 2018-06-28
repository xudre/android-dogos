package com.xudre.dogosfromouterspace;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Enemy extends Sprite {

    public boolean Dead;

    public Enemy(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public void Draw(Canvas canvas) {
        if (Dead) return;

        super.Draw(canvas);
    }

}
