package android.animation;

import android.graphics.Path;
import android.graphics.PointF;
import java.util.ArrayList;

public class PathKeyframes
  implements Keyframes
{
  private static final ArrayList<Keyframe> EMPTY_KEYFRAMES = new ArrayList();
  private static final int FRACTION_OFFSET = 0;
  private static final int NUM_COMPONENTS = 3;
  private static final int X_OFFSET = 1;
  private static final int Y_OFFSET = 2;
  private float[] mKeyframeData;
  private PointF mTempPointF = new PointF();
  
  public PathKeyframes(Path paramPath)
  {
    this(paramPath, 0.5F);
  }
  
  public PathKeyframes(Path paramPath, float paramFloat)
  {
    if ((paramPath == null) || (paramPath.isEmpty())) {
      throw new IllegalArgumentException("The path must not be null or empty");
    }
    this.mKeyframeData = paramPath.approximate(paramFloat);
  }
  
  private static float interpolate(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat3 - paramFloat2) * paramFloat1 + paramFloat2;
  }
  
  private PointF interpolateInRange(float paramFloat, int paramInt1, int paramInt2)
  {
    paramInt1 *= 3;
    paramInt2 *= 3;
    float f1 = this.mKeyframeData[(paramInt1 + 0)];
    paramFloat = (paramFloat - f1) / (this.mKeyframeData[(paramInt2 + 0)] - f1);
    float f3 = this.mKeyframeData[(paramInt1 + 1)];
    float f4 = this.mKeyframeData[(paramInt2 + 1)];
    f1 = this.mKeyframeData[(paramInt1 + 2)];
    float f2 = this.mKeyframeData[(paramInt2 + 2)];
    f3 = interpolate(paramFloat, f3, f4);
    paramFloat = interpolate(paramFloat, f1, f2);
    this.mTempPointF.set(f3, paramFloat);
    return this.mTempPointF;
  }
  
  private PointF pointForIndex(int paramInt)
  {
    paramInt *= 3;
    this.mTempPointF.set(this.mKeyframeData[(paramInt + 1)], this.mKeyframeData[(paramInt + 2)]);
    return this.mTempPointF;
  }
  
  public Keyframes clone()
  {
    try
    {
      Keyframes localKeyframes = (Keyframes)super.clone();
      return localKeyframes;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public Keyframes.FloatKeyframes createXFloatKeyframes()
  {
    new FloatKeyframesBase()
    {
      public float getFloatValue(float paramAnonymousFloat)
      {
        return ((PointF)PathKeyframes.this.getValue(paramAnonymousFloat)).x;
      }
    };
  }
  
  public Keyframes.IntKeyframes createXIntKeyframes()
  {
    new IntKeyframesBase()
    {
      public int getIntValue(float paramAnonymousFloat)
      {
        return Math.round(((PointF)PathKeyframes.this.getValue(paramAnonymousFloat)).x);
      }
    };
  }
  
  public Keyframes.FloatKeyframes createYFloatKeyframes()
  {
    new FloatKeyframesBase()
    {
      public float getFloatValue(float paramAnonymousFloat)
      {
        return ((PointF)PathKeyframes.this.getValue(paramAnonymousFloat)).y;
      }
    };
  }
  
  public Keyframes.IntKeyframes createYIntKeyframes()
  {
    new IntKeyframesBase()
    {
      public int getIntValue(float paramAnonymousFloat)
      {
        return Math.round(((PointF)PathKeyframes.this.getValue(paramAnonymousFloat)).y);
      }
    };
  }
  
  public ArrayList<Keyframe> getKeyframes()
  {
    return EMPTY_KEYFRAMES;
  }
  
  public Class getType()
  {
    return PointF.class;
  }
  
  public Object getValue(float paramFloat)
  {
    int j = this.mKeyframeData.length / 3;
    if (paramFloat < 0.0F) {
      return interpolateInRange(paramFloat, 0, 1);
    }
    if (paramFloat > 1.0F) {
      return interpolateInRange(paramFloat, j - 2, j - 1);
    }
    if (paramFloat == 0.0F) {
      return pointForIndex(0);
    }
    if (paramFloat == 1.0F) {
      return pointForIndex(j - 1);
    }
    int i = 0;
    j -= 1;
    while (i <= j)
    {
      int k = (i + j) / 2;
      float f = this.mKeyframeData[(k * 3 + 0)];
      if (paramFloat < f) {
        j = k - 1;
      } else if (paramFloat > f) {
        i = k + 1;
      } else {
        return pointForIndex(k);
      }
    }
    return interpolateInRange(paramFloat, j, i);
  }
  
  public void invalidateCache() {}
  
  public void setEvaluator(TypeEvaluator paramTypeEvaluator) {}
  
  static abstract class FloatKeyframesBase
    extends PathKeyframes.SimpleKeyframes
    implements Keyframes.FloatKeyframes
  {
    FloatKeyframesBase()
    {
      super();
    }
    
    public Class getType()
    {
      return Float.class;
    }
    
    public Object getValue(float paramFloat)
    {
      return Float.valueOf(getFloatValue(paramFloat));
    }
  }
  
  static abstract class IntKeyframesBase
    extends PathKeyframes.SimpleKeyframes
    implements Keyframes.IntKeyframes
  {
    IntKeyframesBase()
    {
      super();
    }
    
    public Class getType()
    {
      return Integer.class;
    }
    
    public Object getValue(float paramFloat)
    {
      return Integer.valueOf(getIntValue(paramFloat));
    }
  }
  
  private static abstract class SimpleKeyframes
    implements Keyframes
  {
    public Keyframes clone()
    {
      try
      {
        Keyframes localKeyframes = (Keyframes)super.clone();
        return localKeyframes;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}
      return null;
    }
    
    public ArrayList<Keyframe> getKeyframes()
    {
      return PathKeyframes.-get0();
    }
    
    public void invalidateCache() {}
    
    public void setEvaluator(TypeEvaluator paramTypeEvaluator) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/PathKeyframes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */