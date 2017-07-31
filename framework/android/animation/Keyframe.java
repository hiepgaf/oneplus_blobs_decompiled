package android.animation;

public abstract class Keyframe
  implements Cloneable
{
  float mFraction;
  boolean mHasValue;
  private TimeInterpolator mInterpolator = null;
  Class mValueType;
  boolean mValueWasSetOnStart;
  
  public static Keyframe ofFloat(float paramFloat)
  {
    return new FloatKeyframe(paramFloat);
  }
  
  public static Keyframe ofFloat(float paramFloat1, float paramFloat2)
  {
    return new FloatKeyframe(paramFloat1, paramFloat2);
  }
  
  public static Keyframe ofInt(float paramFloat)
  {
    return new IntKeyframe(paramFloat);
  }
  
  public static Keyframe ofInt(float paramFloat, int paramInt)
  {
    return new IntKeyframe(paramFloat, paramInt);
  }
  
  public static Keyframe ofObject(float paramFloat)
  {
    return new ObjectKeyframe(paramFloat, null);
  }
  
  public static Keyframe ofObject(float paramFloat, Object paramObject)
  {
    return new ObjectKeyframe(paramFloat, paramObject);
  }
  
  public abstract Keyframe clone();
  
  public float getFraction()
  {
    return this.mFraction;
  }
  
  public TimeInterpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  public Class getType()
  {
    return this.mValueType;
  }
  
  public abstract Object getValue();
  
  public boolean hasValue()
  {
    return this.mHasValue;
  }
  
  public void setFraction(float paramFloat)
  {
    this.mFraction = paramFloat;
  }
  
  public void setInterpolator(TimeInterpolator paramTimeInterpolator)
  {
    this.mInterpolator = paramTimeInterpolator;
  }
  
  public abstract void setValue(Object paramObject);
  
  void setValueWasSetOnStart(boolean paramBoolean)
  {
    this.mValueWasSetOnStart = paramBoolean;
  }
  
  boolean valueWasSetOnStart()
  {
    return this.mValueWasSetOnStart;
  }
  
  static class FloatKeyframe
    extends Keyframe
  {
    float mValue;
    
    FloatKeyframe(float paramFloat)
    {
      this.mFraction = paramFloat;
      this.mValueType = Float.TYPE;
    }
    
    FloatKeyframe(float paramFloat1, float paramFloat2)
    {
      this.mFraction = paramFloat1;
      this.mValue = paramFloat2;
      this.mValueType = Float.TYPE;
      this.mHasValue = true;
    }
    
    public FloatKeyframe clone()
    {
      if (this.mHasValue) {}
      for (FloatKeyframe localFloatKeyframe = new FloatKeyframe(getFraction(), this.mValue);; localFloatKeyframe = new FloatKeyframe(getFraction()))
      {
        localFloatKeyframe.setInterpolator(getInterpolator());
        localFloatKeyframe.mValueWasSetOnStart = this.mValueWasSetOnStart;
        return localFloatKeyframe;
      }
    }
    
    public float getFloatValue()
    {
      return this.mValue;
    }
    
    public Object getValue()
    {
      return Float.valueOf(this.mValue);
    }
    
    public void setValue(Object paramObject)
    {
      if ((paramObject != null) && (paramObject.getClass() == Float.class))
      {
        this.mValue = ((Float)paramObject).floatValue();
        this.mHasValue = true;
      }
    }
  }
  
  static class IntKeyframe
    extends Keyframe
  {
    int mValue;
    
    IntKeyframe(float paramFloat)
    {
      this.mFraction = paramFloat;
      this.mValueType = Integer.TYPE;
    }
    
    IntKeyframe(float paramFloat, int paramInt)
    {
      this.mFraction = paramFloat;
      this.mValue = paramInt;
      this.mValueType = Integer.TYPE;
      this.mHasValue = true;
    }
    
    public IntKeyframe clone()
    {
      if (this.mHasValue) {}
      for (IntKeyframe localIntKeyframe = new IntKeyframe(getFraction(), this.mValue);; localIntKeyframe = new IntKeyframe(getFraction()))
      {
        localIntKeyframe.setInterpolator(getInterpolator());
        localIntKeyframe.mValueWasSetOnStart = this.mValueWasSetOnStart;
        return localIntKeyframe;
      }
    }
    
    public int getIntValue()
    {
      return this.mValue;
    }
    
    public Object getValue()
    {
      return Integer.valueOf(this.mValue);
    }
    
    public void setValue(Object paramObject)
    {
      if ((paramObject != null) && (paramObject.getClass() == Integer.class))
      {
        this.mValue = ((Integer)paramObject).intValue();
        this.mHasValue = true;
      }
    }
  }
  
  static class ObjectKeyframe
    extends Keyframe
  {
    Object mValue;
    
    ObjectKeyframe(float paramFloat, Object paramObject)
    {
      this.mFraction = paramFloat;
      this.mValue = paramObject;
      boolean bool;
      if (paramObject != null)
      {
        bool = true;
        this.mHasValue = bool;
        if (!this.mHasValue) {
          break label48;
        }
      }
      label48:
      for (paramObject = paramObject.getClass();; paramObject = Object.class)
      {
        this.mValueType = ((Class)paramObject);
        return;
        bool = false;
        break;
      }
    }
    
    public ObjectKeyframe clone()
    {
      float f = getFraction();
      if (hasValue()) {}
      for (Object localObject = this.mValue;; localObject = null)
      {
        localObject = new ObjectKeyframe(f, localObject);
        ((ObjectKeyframe)localObject).mValueWasSetOnStart = this.mValueWasSetOnStart;
        ((ObjectKeyframe)localObject).setInterpolator(getInterpolator());
        return (ObjectKeyframe)localObject;
      }
    }
    
    public Object getValue()
    {
      return this.mValue;
    }
    
    public void setValue(Object paramObject)
    {
      this.mValue = paramObject;
      if (paramObject != null) {}
      for (boolean bool = true;; bool = false)
      {
        this.mHasValue = bool;
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/Keyframe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */