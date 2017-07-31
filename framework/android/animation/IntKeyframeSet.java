package android.animation;

import java.util.List;

class IntKeyframeSet
  extends KeyframeSet
  implements Keyframes.IntKeyframes
{
  private int deltaValue;
  private boolean firstTime = true;
  private int firstValue;
  private int lastValue;
  
  public IntKeyframeSet(Keyframe.IntKeyframe... paramVarArgs)
  {
    super(paramVarArgs);
  }
  
  public IntKeyframeSet clone()
  {
    List localList = this.mKeyframes;
    int j = this.mKeyframes.size();
    Keyframe.IntKeyframe[] arrayOfIntKeyframe = new Keyframe.IntKeyframe[j];
    int i = 0;
    while (i < j)
    {
      arrayOfIntKeyframe[i] = ((Keyframe.IntKeyframe)((Keyframe)localList.get(i)).clone());
      i += 1;
    }
    return new IntKeyframeSet(arrayOfIntKeyframe);
  }
  
  public int getIntValue(float paramFloat)
  {
    float f1;
    if (this.mNumKeyframes == 2)
    {
      if (this.firstTime)
      {
        this.firstTime = false;
        this.firstValue = ((Keyframe.IntKeyframe)this.mKeyframes.get(0)).getIntValue();
        this.lastValue = ((Keyframe.IntKeyframe)this.mKeyframes.get(1)).getIntValue();
        this.deltaValue = (this.lastValue - this.firstValue);
      }
      f1 = paramFloat;
      if (this.mInterpolator != null) {
        f1 = this.mInterpolator.getInterpolation(paramFloat);
      }
      if (this.mEvaluator == null) {
        return this.firstValue + (int)(this.deltaValue * f1);
      }
      return ((Number)this.mEvaluator.evaluate(f1, Integer.valueOf(this.firstValue), Integer.valueOf(this.lastValue))).intValue();
    }
    Keyframe.IntKeyframe localIntKeyframe;
    int j;
    float f2;
    float f3;
    if (paramFloat <= 0.0F)
    {
      localObject = (Keyframe.IntKeyframe)this.mKeyframes.get(0);
      localIntKeyframe = (Keyframe.IntKeyframe)this.mKeyframes.get(1);
      i = ((Keyframe.IntKeyframe)localObject).getIntValue();
      j = localIntKeyframe.getIntValue();
      f2 = ((Keyframe.IntKeyframe)localObject).getFraction();
      f3 = localIntKeyframe.getFraction();
      localObject = localIntKeyframe.getInterpolator();
      f1 = paramFloat;
      if (localObject != null) {
        f1 = ((TimeInterpolator)localObject).getInterpolation(paramFloat);
      }
      paramFloat = (f1 - f2) / (f3 - f2);
      if (this.mEvaluator == null) {
        return (int)((j - i) * paramFloat) + i;
      }
      return ((Number)this.mEvaluator.evaluate(paramFloat, Integer.valueOf(i), Integer.valueOf(j))).intValue();
    }
    if (paramFloat >= 1.0F)
    {
      localObject = (Keyframe.IntKeyframe)this.mKeyframes.get(this.mNumKeyframes - 2);
      localIntKeyframe = (Keyframe.IntKeyframe)this.mKeyframes.get(this.mNumKeyframes - 1);
      i = ((Keyframe.IntKeyframe)localObject).getIntValue();
      j = localIntKeyframe.getIntValue();
      f2 = ((Keyframe.IntKeyframe)localObject).getFraction();
      f3 = localIntKeyframe.getFraction();
      localObject = localIntKeyframe.getInterpolator();
      f1 = paramFloat;
      if (localObject != null) {
        f1 = ((TimeInterpolator)localObject).getInterpolation(paramFloat);
      }
      paramFloat = (f1 - f2) / (f3 - f2);
      if (this.mEvaluator == null) {
        return (int)((j - i) * paramFloat) + i;
      }
      return ((Number)this.mEvaluator.evaluate(paramFloat, Integer.valueOf(i), Integer.valueOf(j))).intValue();
    }
    Object localObject = (Keyframe.IntKeyframe)this.mKeyframes.get(0);
    int i = 1;
    while (i < this.mNumKeyframes)
    {
      localIntKeyframe = (Keyframe.IntKeyframe)this.mKeyframes.get(i);
      if (paramFloat < localIntKeyframe.getFraction())
      {
        TimeInterpolator localTimeInterpolator = localIntKeyframe.getInterpolator();
        f1 = (paramFloat - ((Keyframe.IntKeyframe)localObject).getFraction()) / (localIntKeyframe.getFraction() - ((Keyframe.IntKeyframe)localObject).getFraction());
        i = ((Keyframe.IntKeyframe)localObject).getIntValue();
        j = localIntKeyframe.getIntValue();
        paramFloat = f1;
        if (localTimeInterpolator != null) {
          paramFloat = localTimeInterpolator.getInterpolation(f1);
        }
        if (this.mEvaluator == null) {
          return (int)((j - i) * paramFloat) + i;
        }
        return ((Number)this.mEvaluator.evaluate(paramFloat, Integer.valueOf(i), Integer.valueOf(j))).intValue();
      }
      localObject = localIntKeyframe;
      i += 1;
    }
    return ((Number)((Keyframe)this.mKeyframes.get(this.mNumKeyframes - 1)).getValue()).intValue();
  }
  
  public Class getType()
  {
    return Integer.class;
  }
  
  public Object getValue(float paramFloat)
  {
    return Integer.valueOf(getIntValue(paramFloat));
  }
  
  public void invalidateCache()
  {
    this.firstTime = true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/IntKeyframeSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */