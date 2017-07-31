package android.animation;

import android.graphics.PointF;

public class PointFEvaluator
  implements TypeEvaluator<PointF>
{
  private PointF mPoint;
  
  public PointFEvaluator() {}
  
  public PointFEvaluator(PointF paramPointF)
  {
    this.mPoint = paramPointF;
  }
  
  public PointF evaluate(float paramFloat, PointF paramPointF1, PointF paramPointF2)
  {
    float f = paramPointF1.x + (paramPointF2.x - paramPointF1.x) * paramFloat;
    paramFloat = paramPointF1.y + (paramPointF2.y - paramPointF1.y) * paramFloat;
    if (this.mPoint != null)
    {
      this.mPoint.set(f, paramFloat);
      return this.mPoint;
    }
    return new PointF(f, paramFloat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/PointFEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */