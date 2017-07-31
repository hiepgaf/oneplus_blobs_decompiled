package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class PathShape
  extends Shape
{
  private Path mPath;
  private float mScaleX;
  private float mScaleY;
  private float mStdHeight;
  private float mStdWidth;
  
  public PathShape(Path paramPath, float paramFloat1, float paramFloat2)
  {
    this.mPath = paramPath;
    this.mStdWidth = paramFloat1;
    this.mStdHeight = paramFloat2;
  }
  
  public PathShape clone()
    throws CloneNotSupportedException
  {
    PathShape localPathShape = (PathShape)super.clone();
    localPathShape.mPath = new Path(this.mPath);
    return localPathShape;
  }
  
  public void draw(Canvas paramCanvas, Paint paramPaint)
  {
    paramCanvas.save();
    paramCanvas.scale(this.mScaleX, this.mScaleY);
    paramCanvas.drawPath(this.mPath, paramPaint);
    paramCanvas.restore();
  }
  
  protected void onResize(float paramFloat1, float paramFloat2)
  {
    this.mScaleX = (paramFloat1 / this.mStdWidth);
    this.mScaleY = (paramFloat2 / this.mStdHeight);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/shapes/PathShape.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */