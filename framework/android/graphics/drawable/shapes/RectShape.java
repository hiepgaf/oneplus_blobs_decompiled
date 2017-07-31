package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;

public class RectShape
  extends Shape
{
  private RectF mRect = new RectF();
  
  public RectShape clone()
    throws CloneNotSupportedException
  {
    RectShape localRectShape = (RectShape)super.clone();
    localRectShape.mRect = new RectF(this.mRect);
    return localRectShape;
  }
  
  public void draw(Canvas paramCanvas, Paint paramPaint)
  {
    paramCanvas.drawRect(this.mRect, paramPaint);
  }
  
  public void getOutline(Outline paramOutline)
  {
    RectF localRectF = rect();
    paramOutline.setRect((int)Math.ceil(localRectF.left), (int)Math.ceil(localRectF.top), (int)Math.floor(localRectF.right), (int)Math.floor(localRectF.bottom));
  }
  
  protected void onResize(float paramFloat1, float paramFloat2)
  {
    this.mRect.set(0.0F, 0.0F, paramFloat1, paramFloat2);
  }
  
  protected final RectF rect()
  {
    return this.mRect;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/shapes/RectShape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */