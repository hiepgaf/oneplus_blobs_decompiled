package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;

public class OvalShape
  extends RectShape
{
  public void draw(Canvas paramCanvas, Paint paramPaint)
  {
    paramCanvas.drawOval(rect(), paramPaint);
  }
  
  public void getOutline(Outline paramOutline)
  {
    RectF localRectF = rect();
    paramOutline.setOval((int)Math.ceil(localRectF.left), (int)Math.ceil(localRectF.top), (int)Math.floor(localRectF.right), (int)Math.floor(localRectF.bottom));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/shapes/OvalShape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */