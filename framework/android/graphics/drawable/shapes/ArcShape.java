package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;

public class ArcShape
  extends RectShape
{
  private float mStart;
  private float mSweep;
  
  public ArcShape(float paramFloat1, float paramFloat2)
  {
    this.mStart = paramFloat1;
    this.mSweep = paramFloat2;
  }
  
  public void draw(Canvas paramCanvas, Paint paramPaint)
  {
    paramCanvas.drawArc(rect(), this.mStart, this.mSweep, true, paramPaint);
  }
  
  public void getOutline(Outline paramOutline) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/shapes/ArcShape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */