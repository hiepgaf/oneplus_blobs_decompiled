package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;

public class RoundRectShape
  extends RectShape
{
  private float[] mInnerRadii;
  private RectF mInnerRect;
  private RectF mInset;
  private float[] mOuterRadii;
  private Path mPath;
  
  public RoundRectShape(float[] paramArrayOfFloat1, RectF paramRectF, float[] paramArrayOfFloat2)
  {
    if ((paramArrayOfFloat1 != null) && (paramArrayOfFloat1.length < 8)) {
      throw new ArrayIndexOutOfBoundsException("outer radii must have >= 8 values");
    }
    if ((paramArrayOfFloat2 != null) && (paramArrayOfFloat2.length < 8)) {
      throw new ArrayIndexOutOfBoundsException("inner radii must have >= 8 values");
    }
    this.mOuterRadii = paramArrayOfFloat1;
    this.mInset = paramRectF;
    this.mInnerRadii = paramArrayOfFloat2;
    if (paramRectF != null) {
      this.mInnerRect = new RectF();
    }
    this.mPath = new Path();
  }
  
  public RoundRectShape clone()
    throws CloneNotSupportedException
  {
    RoundRectShape localRoundRectShape = (RoundRectShape)super.clone();
    if (this.mOuterRadii != null)
    {
      arrayOfFloat = (float[])this.mOuterRadii.clone();
      localRoundRectShape.mOuterRadii = arrayOfFloat;
      if (this.mInnerRadii == null) {
        break label106;
      }
    }
    label106:
    for (float[] arrayOfFloat = (float[])this.mInnerRadii.clone();; arrayOfFloat = null)
    {
      localRoundRectShape.mInnerRadii = arrayOfFloat;
      localRoundRectShape.mInset = new RectF(this.mInset);
      localRoundRectShape.mInnerRect = new RectF(this.mInnerRect);
      localRoundRectShape.mPath = new Path(this.mPath);
      return localRoundRectShape;
      arrayOfFloat = null;
      break;
    }
  }
  
  public void draw(Canvas paramCanvas, Paint paramPaint)
  {
    paramCanvas.drawPath(this.mPath, paramPaint);
  }
  
  public void getOutline(Outline paramOutline)
  {
    if (this.mInnerRect != null) {
      return;
    }
    float f1 = 0.0F;
    if (this.mOuterRadii != null)
    {
      float f2 = this.mOuterRadii[0];
      int i = 1;
      for (;;)
      {
        f1 = f2;
        if (i >= 8) {
          break;
        }
        if (this.mOuterRadii[i] != f2)
        {
          paramOutline.setConvexPath(this.mPath);
          return;
        }
        i += 1;
      }
    }
    RectF localRectF = rect();
    paramOutline.setRoundRect((int)Math.ceil(localRectF.left), (int)Math.ceil(localRectF.top), (int)Math.floor(localRectF.right), (int)Math.floor(localRectF.bottom), f1);
  }
  
  protected void onResize(float paramFloat1, float paramFloat2)
  {
    super.onResize(paramFloat1, paramFloat2);
    RectF localRectF = rect();
    this.mPath.reset();
    if (this.mOuterRadii != null) {
      this.mPath.addRoundRect(localRectF, this.mOuterRadii, Path.Direction.CW);
    }
    for (;;)
    {
      if (this.mInnerRect != null)
      {
        this.mInnerRect.set(localRectF.left + this.mInset.left, localRectF.top + this.mInset.top, localRectF.right - this.mInset.right, localRectF.bottom - this.mInset.bottom);
        if ((this.mInnerRect.width() < paramFloat1) && (this.mInnerRect.height() < paramFloat2))
        {
          if (this.mInnerRadii == null) {
            break;
          }
          this.mPath.addRoundRect(this.mInnerRect, this.mInnerRadii, Path.Direction.CCW);
        }
      }
      return;
      this.mPath.addRect(localRectF, Path.Direction.CW);
    }
    this.mPath.addRect(this.mInnerRect, Path.Direction.CCW);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/shapes/RoundRectShape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */