package android.animation;

import android.graphics.Path;
import android.util.Log;
import java.util.Arrays;
import java.util.List;

public class KeyframeSet
  implements Keyframes
{
  TypeEvaluator mEvaluator;
  Keyframe mFirstKeyframe;
  TimeInterpolator mInterpolator;
  List<Keyframe> mKeyframes;
  Keyframe mLastKeyframe;
  int mNumKeyframes;
  
  public KeyframeSet(Keyframe... paramVarArgs)
  {
    this.mNumKeyframes = paramVarArgs.length;
    this.mKeyframes = Arrays.asList(paramVarArgs);
    this.mFirstKeyframe = paramVarArgs[0];
    this.mLastKeyframe = paramVarArgs[(this.mNumKeyframes - 1)];
    this.mInterpolator = this.mLastKeyframe.getInterpolator();
  }
  
  public static KeyframeSet ofFloat(float... paramVarArgs)
  {
    int i = 0;
    int j = 0;
    int m = paramVarArgs.length;
    Keyframe.FloatKeyframe[] arrayOfFloatKeyframe = new Keyframe.FloatKeyframe[Math.max(m, 2)];
    if (m == 1)
    {
      arrayOfFloatKeyframe[0] = ((Keyframe.FloatKeyframe)Keyframe.ofFloat(0.0F));
      arrayOfFloatKeyframe[1] = ((Keyframe.FloatKeyframe)Keyframe.ofFloat(1.0F, paramVarArgs[0]));
      if (Float.isNaN(paramVarArgs[0])) {
        j = 1;
      }
      if (j != 0) {
        Log.w("Animator", "Bad value (NaN) in float animator");
      }
      return new FloatKeyframeSet(arrayOfFloatKeyframe);
    }
    arrayOfFloatKeyframe[0] = ((Keyframe.FloatKeyframe)Keyframe.ofFloat(0.0F, paramVarArgs[0]));
    int k = 1;
    for (;;)
    {
      j = i;
      if (k >= m) {
        break;
      }
      arrayOfFloatKeyframe[k] = ((Keyframe.FloatKeyframe)Keyframe.ofFloat(k / (m - 1), paramVarArgs[k]));
      if (Float.isNaN(paramVarArgs[k])) {
        i = 1;
      }
      k += 1;
    }
  }
  
  public static KeyframeSet ofInt(int... paramVarArgs)
  {
    int j = paramVarArgs.length;
    Keyframe.IntKeyframe[] arrayOfIntKeyframe = new Keyframe.IntKeyframe[Math.max(j, 2)];
    if (j == 1)
    {
      arrayOfIntKeyframe[0] = ((Keyframe.IntKeyframe)Keyframe.ofInt(0.0F));
      arrayOfIntKeyframe[1] = ((Keyframe.IntKeyframe)Keyframe.ofInt(1.0F, paramVarArgs[0]));
    }
    for (;;)
    {
      return new IntKeyframeSet(arrayOfIntKeyframe);
      arrayOfIntKeyframe[0] = ((Keyframe.IntKeyframe)Keyframe.ofInt(0.0F, paramVarArgs[0]));
      int i = 1;
      while (i < j)
      {
        arrayOfIntKeyframe[i] = ((Keyframe.IntKeyframe)Keyframe.ofInt(i / (j - 1), paramVarArgs[i]));
        i += 1;
      }
    }
  }
  
  public static KeyframeSet ofKeyframe(Keyframe... paramVarArgs)
  {
    int n = paramVarArgs.length;
    int m = 0;
    int k = 0;
    int j = 0;
    int i = 0;
    if (i < n)
    {
      if ((paramVarArgs[i] instanceof Keyframe.FloatKeyframe)) {
        m = 1;
      }
      for (;;)
      {
        i += 1;
        break;
        if ((paramVarArgs[i] instanceof Keyframe.IntKeyframe)) {
          k = 1;
        } else {
          j = 1;
        }
      }
    }
    if ((m == 0) || (k != 0)) {
      if ((k != 0) && (m == 0)) {
        break label130;
      }
    }
    label130:
    while (j != 0)
    {
      return new KeyframeSet(paramVarArgs);
      if (j != 0) {
        break;
      }
      localObject = new Keyframe.FloatKeyframe[n];
      i = 0;
      while (i < n)
      {
        localObject[i] = ((Keyframe.FloatKeyframe)paramVarArgs[i]);
        i += 1;
      }
      return new FloatKeyframeSet((Keyframe.FloatKeyframe[])localObject);
    }
    Object localObject = new Keyframe.IntKeyframe[n];
    i = 0;
    while (i < n)
    {
      localObject[i] = ((Keyframe.IntKeyframe)paramVarArgs[i]);
      i += 1;
    }
    return new IntKeyframeSet((Keyframe.IntKeyframe[])localObject);
  }
  
  public static KeyframeSet ofObject(Object... paramVarArgs)
  {
    int j = paramVarArgs.length;
    Keyframe.ObjectKeyframe[] arrayOfObjectKeyframe = new Keyframe.ObjectKeyframe[Math.max(j, 2)];
    if (j == 1)
    {
      arrayOfObjectKeyframe[0] = ((Keyframe.ObjectKeyframe)Keyframe.ofObject(0.0F));
      arrayOfObjectKeyframe[1] = ((Keyframe.ObjectKeyframe)Keyframe.ofObject(1.0F, paramVarArgs[0]));
    }
    for (;;)
    {
      return new KeyframeSet(arrayOfObjectKeyframe);
      arrayOfObjectKeyframe[0] = ((Keyframe.ObjectKeyframe)Keyframe.ofObject(0.0F, paramVarArgs[0]));
      int i = 1;
      while (i < j)
      {
        arrayOfObjectKeyframe[i] = ((Keyframe.ObjectKeyframe)Keyframe.ofObject(i / (j - 1), paramVarArgs[i]));
        i += 1;
      }
    }
  }
  
  public static PathKeyframes ofPath(Path paramPath)
  {
    return new PathKeyframes(paramPath);
  }
  
  public static PathKeyframes ofPath(Path paramPath, float paramFloat)
  {
    return new PathKeyframes(paramPath, paramFloat);
  }
  
  public KeyframeSet clone()
  {
    List localList = this.mKeyframes;
    int j = this.mKeyframes.size();
    Keyframe[] arrayOfKeyframe = new Keyframe[j];
    int i = 0;
    while (i < j)
    {
      arrayOfKeyframe[i] = ((Keyframe)localList.get(i)).clone();
      i += 1;
    }
    return new KeyframeSet(arrayOfKeyframe);
  }
  
  public List<Keyframe> getKeyframes()
  {
    return this.mKeyframes;
  }
  
  public Class getType()
  {
    return this.mFirstKeyframe.getType();
  }
  
  public Object getValue(float paramFloat)
  {
    float f;
    if (this.mNumKeyframes == 2)
    {
      f = paramFloat;
      if (this.mInterpolator != null) {
        f = this.mInterpolator.getInterpolation(paramFloat);
      }
      return this.mEvaluator.evaluate(f, this.mFirstKeyframe.getValue(), this.mLastKeyframe.getValue());
    }
    Object localObject2;
    if (paramFloat <= 0.0F)
    {
      localObject1 = (Keyframe)this.mKeyframes.get(1);
      localObject2 = ((Keyframe)localObject1).getInterpolator();
      f = paramFloat;
      if (localObject2 != null) {
        f = ((TimeInterpolator)localObject2).getInterpolation(paramFloat);
      }
      paramFloat = this.mFirstKeyframe.getFraction();
      paramFloat = (f - paramFloat) / (((Keyframe)localObject1).getFraction() - paramFloat);
      return this.mEvaluator.evaluate(paramFloat, this.mFirstKeyframe.getValue(), ((Keyframe)localObject1).getValue());
    }
    if (paramFloat >= 1.0F)
    {
      localObject1 = (Keyframe)this.mKeyframes.get(this.mNumKeyframes - 2);
      localObject2 = this.mLastKeyframe.getInterpolator();
      f = paramFloat;
      if (localObject2 != null) {
        f = ((TimeInterpolator)localObject2).getInterpolation(paramFloat);
      }
      paramFloat = ((Keyframe)localObject1).getFraction();
      paramFloat = (f - paramFloat) / (this.mLastKeyframe.getFraction() - paramFloat);
      return this.mEvaluator.evaluate(paramFloat, ((Keyframe)localObject1).getValue(), this.mLastKeyframe.getValue());
    }
    Object localObject1 = this.mFirstKeyframe;
    int i = 1;
    while (i < this.mNumKeyframes)
    {
      localObject2 = (Keyframe)this.mKeyframes.get(i);
      if (paramFloat < ((Keyframe)localObject2).getFraction())
      {
        TimeInterpolator localTimeInterpolator = ((Keyframe)localObject2).getInterpolator();
        f = ((Keyframe)localObject1).getFraction();
        f = (paramFloat - f) / (((Keyframe)localObject2).getFraction() - f);
        paramFloat = f;
        if (localTimeInterpolator != null) {
          paramFloat = localTimeInterpolator.getInterpolation(f);
        }
        return this.mEvaluator.evaluate(paramFloat, ((Keyframe)localObject1).getValue(), ((Keyframe)localObject2).getValue());
      }
      localObject1 = localObject2;
      i += 1;
    }
    return this.mLastKeyframe.getValue();
  }
  
  public void invalidateCache() {}
  
  public void setEvaluator(TypeEvaluator paramTypeEvaluator)
  {
    this.mEvaluator = paramTypeEvaluator;
  }
  
  public String toString()
  {
    String str = " ";
    int i = 0;
    while (i < this.mNumKeyframes)
    {
      str = str + ((Keyframe)this.mKeyframes.get(i)).getValue() + "  ";
      i += 1;
    }
    return str;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/KeyframeSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */