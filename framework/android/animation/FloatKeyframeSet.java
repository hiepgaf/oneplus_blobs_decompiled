package android.animation;

import java.util.List;

class FloatKeyframeSet
  extends KeyframeSet
  implements Keyframes.FloatKeyframes
{
  private float deltaValue;
  private boolean firstTime = true;
  private float firstValue;
  private float lastValue;
  
  public FloatKeyframeSet(Keyframe.FloatKeyframe... paramVarArgs)
  {
    super(paramVarArgs);
  }
  
  public FloatKeyframeSet clone()
  {
    List localList = this.mKeyframes;
    int j = this.mKeyframes.size();
    Keyframe.FloatKeyframe[] arrayOfFloatKeyframe = new Keyframe.FloatKeyframe[j];
    int i = 0;
    while (i < j)
    {
      arrayOfFloatKeyframe[i] = ((Keyframe.FloatKeyframe)((Keyframe)localList.get(i)).clone());
      i += 1;
    }
    return new FloatKeyframeSet(arrayOfFloatKeyframe);
  }
  
  public float getFloatValue(float paramFloat)
  {
    float f1;
    if (this.mNumKeyframes == 2)
    {
      if (this.firstTime)
      {
        this.firstTime = false;
        this.firstValue = ((Keyframe.FloatKeyframe)this.mKeyframes.get(0)).getFloatValue();
        this.lastValue = ((Keyframe.FloatKeyframe)this.mKeyframes.get(1)).getFloatValue();
        this.deltaValue = (this.lastValue - this.firstValue);
      }
      f1 = paramFloat;
      if (this.mInterpolator != null) {
        f1 = this.mInterpolator.getInterpolation(paramFloat);
      }
      if (this.mEvaluator == null) {
        return this.firstValue + this.deltaValue * f1;
      }
      return ((Number)this.mEvaluator.evaluate(f1, Float.valueOf(this.firstValue), Float.valueOf(this.lastValue))).floatValue();
    }
    Keyframe.FloatKeyframe localFloatKeyframe;
    float f2;
    float f3;
    float f4;
    float f5;
    if (paramFloat <= 0.0F)
    {
      localObject = (Keyframe.FloatKeyframe)this.mKeyframes.get(0);
      localFloatKeyframe = (Keyframe.FloatKeyframe)this.mKeyframes.get(1);
      f2 = ((Keyframe.FloatKeyframe)localObject).getFloatValue();
      f3 = localFloatKeyframe.getFloatValue();
      f4 = ((Keyframe.FloatKeyframe)localObject).getFraction();
      f5 = localFloatKeyframe.getFraction();
      localObject = localFloatKeyframe.getInterpolator();
      f1 = paramFloat;
      if (localObject != null) {
        f1 = ((TimeInterpolator)localObject).getInterpolation(paramFloat);
      }
      paramFloat = (f1 - f4) / (f5 - f4);
      if (this.mEvaluator == null) {
        return (f3 - f2) * paramFloat + f2;
      }
      return ((Number)this.mEvaluator.evaluate(paramFloat, Float.valueOf(f2), Float.valueOf(f3))).floatValue();
    }
    if (paramFloat >= 1.0F)
    {
      localObject = (Keyframe.FloatKeyframe)this.mKeyframes.get(this.mNumKeyframes - 2);
      localFloatKeyframe = (Keyframe.FloatKeyframe)this.mKeyframes.get(this.mNumKeyframes - 1);
      f2 = ((Keyframe.FloatKeyframe)localObject).getFloatValue();
      f3 = localFloatKeyframe.getFloatValue();
      f4 = ((Keyframe.FloatKeyframe)localObject).getFraction();
      f5 = localFloatKeyframe.getFraction();
      localObject = localFloatKeyframe.getInterpolator();
      f1 = paramFloat;
      if (localObject != null) {
        f1 = ((TimeInterpolator)localObject).getInterpolation(paramFloat);
      }
      paramFloat = (f1 - f4) / (f5 - f4);
      if (this.mEvaluator == null) {
        return (f3 - f2) * paramFloat + f2;
      }
      return ((Number)this.mEvaluator.evaluate(paramFloat, Float.valueOf(f2), Float.valueOf(f3))).floatValue();
    }
    Object localObject = (Keyframe.FloatKeyframe)this.mKeyframes.get(0);
    int i = 1;
    while (i < this.mNumKeyframes)
    {
      localFloatKeyframe = (Keyframe.FloatKeyframe)this.mKeyframes.get(i);
      if (paramFloat < localFloatKeyframe.getFraction())
      {
        TimeInterpolator localTimeInterpolator = localFloatKeyframe.getInterpolator();
        f1 = (paramFloat - ((Keyframe.FloatKeyframe)localObject).getFraction()) / (localFloatKeyframe.getFraction() - ((Keyframe.FloatKeyframe)localObject).getFraction());
        f2 = ((Keyframe.FloatKeyframe)localObject).getFloatValue();
        f3 = localFloatKeyframe.getFloatValue();
        paramFloat = f1;
        if (localTimeInterpolator != null) {
          paramFloat = localTimeInterpolator.getInterpolation(f1);
        }
        if (this.mEvaluator == null) {
          return (f3 - f2) * paramFloat + f2;
        }
        return ((Number)this.mEvaluator.evaluate(paramFloat, Float.valueOf(f2), Float.valueOf(f3))).floatValue();
      }
      localObject = localFloatKeyframe;
      i += 1;
    }
    return ((Number)((Keyframe)this.mKeyframes.get(this.mNumKeyframes - 1)).getValue()).floatValue();
  }
  
  public Class getType()
  {
    return Float.class;
  }
  
  public Object getValue(float paramFloat)
  {
    return Float.valueOf(getFloatValue(paramFloat));
  }
  
  public void invalidateCache()
  {
    this.firstTime = true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/FloatKeyframeSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */