package com.xudre.dogosfromouterspace;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

public class Sprite {

    public PointF Position;
    public Point Size;
    public PointF Pivot;
    public float Rotation;
    public PointF Scale;
    public Paint Paint;

    private Matrix m_Matrix;
    private Bitmap m_Bitmap;

    public Sprite(Bitmap bitmap) {
        Position = new PointF(0, 0);
        Size = new Point(bitmap.getWidth(), bitmap.getHeight());
        Pivot = new PointF(Size.x / 2, Size.y / 2);
        Rotation = 0;
        Scale = new PointF(1, 1);
        Paint = new Paint();

        m_Matrix = new Matrix();
        m_Bitmap = bitmap;
    }

    public void Draw(Canvas canvas) {
        m_Matrix.setRotate(Rotation, Pivot.x, Pivot.y);
        m_Matrix.postTranslate(Position.x, Position.y);
        m_Matrix.postScale(Scale.x, Scale.y);

        canvas.drawBitmap(m_Bitmap, m_Matrix, Paint);
    }

    public boolean Hit(float x, float y) {
        RectF area = new RectF(Position.x, Position.y, Position.x + (Size.x * Scale.x), Position.y + (Size.y * Scale.y));

        return area.contains(x, y);
    }

}
