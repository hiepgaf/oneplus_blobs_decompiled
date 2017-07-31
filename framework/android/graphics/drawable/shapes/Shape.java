package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;

public abstract class Shape
  implements Cloneable
{
  private float mHeight;
  private float mWidth;
  
  public Shape clone()
    throws CloneNotSupportedException
  {
    return (Shape)super.clone();
  }
  
  public abstract void draw(Canvas paramCanvas, Paint paramPaint);
  
  public final float getHeight()
  {
    return this.mHeight;
  }
  
  public void getOutline(Outline paramOutline) {}
  
  public final float getWidth()
  {
    return this.mWidth;
  }
  
  public boolean hasAlpha()
  {
    return true;
  }
  
  protected void onResize(float paramFloat1, float paramFloat2) {}
  
  public final void resize(float paramFloat1, float paramFloat2)
  {
    float f = paramFloat1;
    if (paramFloat1 < 0.0F) {
      f = 0.0F;
    }
    paramFloat1 = paramFloat2;
    if (paramFloat2 < 0.0F) {
      paramFloat1 = 0.0F;
    }
    if ((this.mWidth != f) || (this.mHeight != paramFloat1))
    {
      this.mWidth = f;
      this.mHeight = paramFloat1;
      onResize(f, paramFloat1);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/shapes/Shape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */