package android.animation;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public final class ObjectAnimator
  extends ValueAnimator
{
  private static final boolean DBG = false;
  private static final String LOG_TAG = "ObjectAnimator";
  private boolean mAutoCancel = false;
  private Property mProperty;
  private String mPropertyName;
  private WeakReference<Object> mTarget;
  
  public ObjectAnimator() {}
  
  private <T> ObjectAnimator(T paramT, Property<T, ?> paramProperty)
  {
    setTarget(paramT);
    setProperty(paramProperty);
  }
  
  private ObjectAnimator(Object paramObject, String paramString)
  {
    setTarget(paramObject);
    setPropertyName(paramString);
  }
  
  private boolean hasSameTargetAndProperties(Animator paramAnimator)
  {
    if ((paramAnimator instanceof ObjectAnimator))
    {
      PropertyValuesHolder[] arrayOfPropertyValuesHolder = ((ObjectAnimator)paramAnimator).getValues();
      if ((((ObjectAnimator)paramAnimator).getTarget() == getTarget()) && (this.mValues.length == arrayOfPropertyValuesHolder.length))
      {
        int i = 0;
        while (i < this.mValues.length)
        {
          paramAnimator = this.mValues[i];
          PropertyValuesHolder localPropertyValuesHolder = arrayOfPropertyValuesHolder[i];
          if ((paramAnimator.getPropertyName() != null) && (paramAnimator.getPropertyName().equals(localPropertyValuesHolder.getPropertyName()))) {
            i += 1;
          } else {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public static <T> ObjectAnimator ofArgb(T paramT, Property<T, Integer> paramProperty, int... paramVarArgs)
  {
    paramT = ofInt(paramT, paramProperty, paramVarArgs);
    paramT.setEvaluator(ArgbEvaluator.getInstance());
    return paramT;
  }
  
  public static ObjectAnimator ofArgb(Object paramObject, String paramString, int... paramVarArgs)
  {
    paramObject = ofInt(paramObject, paramString, paramVarArgs);
    ((ObjectAnimator)paramObject).setEvaluator(ArgbEvaluator.getInstance());
    return (ObjectAnimator)paramObject;
  }
  
  public static <T> ObjectAnimator ofFloat(T paramT, Property<T, Float> paramProperty1, Property<T, Float> paramProperty2, Path paramPath)
  {
    paramPath = KeyframeSet.ofPath(paramPath);
    return ofPropertyValuesHolder(paramT, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframes(paramProperty1, paramPath.createXFloatKeyframes()), PropertyValuesHolder.ofKeyframes(paramProperty2, paramPath.createYFloatKeyframes()) });
  }
  
  public static <T> ObjectAnimator ofFloat(T paramT, Property<T, Float> paramProperty, float... paramVarArgs)
  {
    paramT = new ObjectAnimator(paramT, paramProperty);
    paramT.setFloatValues(paramVarArgs);
    return paramT;
  }
  
  public static ObjectAnimator ofFloat(Object paramObject, String paramString1, String paramString2, Path paramPath)
  {
    paramPath = KeyframeSet.ofPath(paramPath);
    return ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframes(paramString1, paramPath.createXFloatKeyframes()), PropertyValuesHolder.ofKeyframes(paramString2, paramPath.createYFloatKeyframes()) });
  }
  
  public static ObjectAnimator ofFloat(Object paramObject, String paramString, float... paramVarArgs)
  {
    paramObject = new ObjectAnimator(paramObject, paramString);
    ((ObjectAnimator)paramObject).setFloatValues(paramVarArgs);
    return (ObjectAnimator)paramObject;
  }
  
  public static <T> ObjectAnimator ofInt(T paramT, Property<T, Integer> paramProperty1, Property<T, Integer> paramProperty2, Path paramPath)
  {
    paramPath = KeyframeSet.ofPath(paramPath);
    return ofPropertyValuesHolder(paramT, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframes(paramProperty1, paramPath.createXIntKeyframes()), PropertyValuesHolder.ofKeyframes(paramProperty2, paramPath.createYIntKeyframes()) });
  }
  
  public static <T> ObjectAnimator ofInt(T paramT, Property<T, Integer> paramProperty, int... paramVarArgs)
  {
    paramT = new ObjectAnimator(paramT, paramProperty);
    paramT.setIntValues(paramVarArgs);
    return paramT;
  }
  
  public static ObjectAnimator ofInt(Object paramObject, String paramString1, String paramString2, Path paramPath)
  {
    paramPath = KeyframeSet.ofPath(paramPath);
    return ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframes(paramString1, paramPath.createXIntKeyframes()), PropertyValuesHolder.ofKeyframes(paramString2, paramPath.createYIntKeyframes()) });
  }
  
  public static ObjectAnimator ofInt(Object paramObject, String paramString, int... paramVarArgs)
  {
    paramObject = new ObjectAnimator(paramObject, paramString);
    ((ObjectAnimator)paramObject).setIntValues(paramVarArgs);
    return (ObjectAnimator)paramObject;
  }
  
  @SafeVarargs
  public static <T> ObjectAnimator ofMultiFloat(Object paramObject, String paramString, TypeConverter<T, float[]> paramTypeConverter, TypeEvaluator<T> paramTypeEvaluator, T... paramVarArgs)
  {
    return ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofMultiFloat(paramString, paramTypeConverter, paramTypeEvaluator, paramVarArgs) });
  }
  
  public static ObjectAnimator ofMultiFloat(Object paramObject, String paramString, Path paramPath)
  {
    return ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofMultiFloat(paramString, paramPath) });
  }
  
  public static ObjectAnimator ofMultiFloat(Object paramObject, String paramString, float[][] paramArrayOfFloat)
  {
    return ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofMultiFloat(paramString, paramArrayOfFloat) });
  }
  
  @SafeVarargs
  public static <T> ObjectAnimator ofMultiInt(Object paramObject, String paramString, TypeConverter<T, int[]> paramTypeConverter, TypeEvaluator<T> paramTypeEvaluator, T... paramVarArgs)
  {
    return ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofMultiInt(paramString, paramTypeConverter, paramTypeEvaluator, paramVarArgs) });
  }
  
  public static ObjectAnimator ofMultiInt(Object paramObject, String paramString, Path paramPath)
  {
    return ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofMultiInt(paramString, paramPath) });
  }
  
  public static ObjectAnimator ofMultiInt(Object paramObject, String paramString, int[][] paramArrayOfInt)
  {
    return ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofMultiInt(paramString, paramArrayOfInt) });
  }
  
  @SafeVarargs
  public static <T, V, P> ObjectAnimator ofObject(T paramT, Property<T, P> paramProperty, TypeConverter<V, P> paramTypeConverter, TypeEvaluator<V> paramTypeEvaluator, V... paramVarArgs)
  {
    return ofPropertyValuesHolder(paramT, new PropertyValuesHolder[] { PropertyValuesHolder.ofObject(paramProperty, paramTypeConverter, paramTypeEvaluator, paramVarArgs) });
  }
  
  public static <T, V> ObjectAnimator ofObject(T paramT, Property<T, V> paramProperty, TypeConverter<PointF, V> paramTypeConverter, Path paramPath)
  {
    return ofPropertyValuesHolder(paramT, new PropertyValuesHolder[] { PropertyValuesHolder.ofObject(paramProperty, paramTypeConverter, paramPath) });
  }
  
  @SafeVarargs
  public static <T, V> ObjectAnimator ofObject(T paramT, Property<T, V> paramProperty, TypeEvaluator<V> paramTypeEvaluator, V... paramVarArgs)
  {
    paramT = new ObjectAnimator(paramT, paramProperty);
    paramT.setObjectValues(paramVarArgs);
    paramT.setEvaluator(paramTypeEvaluator);
    return paramT;
  }
  
  public static ObjectAnimator ofObject(Object paramObject, String paramString, TypeConverter<PointF, ?> paramTypeConverter, Path paramPath)
  {
    return ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofObject(paramString, paramTypeConverter, paramPath) });
  }
  
  public static ObjectAnimator ofObject(Object paramObject, String paramString, TypeEvaluator paramTypeEvaluator, Object... paramVarArgs)
  {
    paramObject = new ObjectAnimator(paramObject, paramString);
    ((ObjectAnimator)paramObject).setObjectValues(paramVarArgs);
    ((ObjectAnimator)paramObject).setEvaluator(paramTypeEvaluator);
    return (ObjectAnimator)paramObject;
  }
  
  public static ObjectAnimator ofPropertyValuesHolder(Object paramObject, PropertyValuesHolder... paramVarArgs)
  {
    ObjectAnimator localObjectAnimator = new ObjectAnimator();
    localObjectAnimator.setTarget(paramObject);
    localObjectAnimator.setValues(paramVarArgs);
    return localObjectAnimator;
  }
  
  void animateValue(float paramFloat)
  {
    Object localObject = getTarget();
    if ((this.mTarget != null) && (localObject == null))
    {
      cancel();
      return;
    }
    super.animateValue(paramFloat);
    int j = this.mValues.length;
    int i = 0;
    while (i < j)
    {
      this.mValues[i].setAnimatedValue(localObject);
      i += 1;
    }
  }
  
  public ObjectAnimator clone()
  {
    return (ObjectAnimator)super.clone();
  }
  
  String getNameForTrace()
  {
    return "animator:" + getPropertyName();
  }
  
  public String getPropertyName()
  {
    Object localObject3 = null;
    Object localObject2 = null;
    if (this.mPropertyName != null) {
      localObject1 = this.mPropertyName;
    }
    int i;
    do
    {
      do
      {
        do
        {
          return (String)localObject1;
          if (this.mProperty != null) {
            return this.mProperty.getName();
          }
          localObject1 = localObject2;
        } while (this.mValues == null);
        localObject1 = localObject2;
      } while (this.mValues.length <= 0);
      i = 0;
      localObject2 = localObject3;
      localObject1 = localObject2;
    } while (i >= this.mValues.length);
    if (i == 0) {}
    for (Object localObject1 = "";; localObject1 = (String)localObject2 + ",")
    {
      localObject2 = (String)localObject1 + this.mValues[i].getPropertyName();
      i += 1;
      break;
    }
  }
  
  public Object getTarget()
  {
    if (this.mTarget == null) {
      return null;
    }
    return this.mTarget.get();
  }
  
  void initAnimation()
  {
    if (!this.mInitialized)
    {
      Object localObject = getTarget();
      if (localObject != null)
      {
        int j = this.mValues.length;
        int i = 0;
        while (i < j)
        {
          this.mValues[i].setupSetterAndGetter(localObject);
          i += 1;
        }
      }
      super.initAnimation();
    }
  }
  
  public void setAutoCancel(boolean paramBoolean)
  {
    this.mAutoCancel = paramBoolean;
  }
  
  public ObjectAnimator setDuration(long paramLong)
  {
    super.setDuration(paramLong);
    return this;
  }
  
  public void setFloatValues(float... paramVarArgs)
  {
    if ((this.mValues == null) || (this.mValues.length == 0))
    {
      if (this.mProperty != null)
      {
        setValues(new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat(this.mProperty, paramVarArgs) });
        return;
      }
      setValues(new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat(this.mPropertyName, paramVarArgs) });
      return;
    }
    super.setFloatValues(paramVarArgs);
  }
  
  public void setIntValues(int... paramVarArgs)
  {
    if ((this.mValues == null) || (this.mValues.length == 0))
    {
      if (this.mProperty != null)
      {
        setValues(new PropertyValuesHolder[] { PropertyValuesHolder.ofInt(this.mProperty, paramVarArgs) });
        return;
      }
      setValues(new PropertyValuesHolder[] { PropertyValuesHolder.ofInt(this.mPropertyName, paramVarArgs) });
      return;
    }
    super.setIntValues(paramVarArgs);
  }
  
  public void setObjectValues(Object... paramVarArgs)
  {
    if ((this.mValues == null) || (this.mValues.length == 0))
    {
      if (this.mProperty != null)
      {
        setValues(new PropertyValuesHolder[] { PropertyValuesHolder.ofObject(this.mProperty, (TypeEvaluator)null, paramVarArgs) });
        return;
      }
      setValues(new PropertyValuesHolder[] { PropertyValuesHolder.ofObject(this.mPropertyName, (TypeEvaluator)null, paramVarArgs) });
      return;
    }
    super.setObjectValues(paramVarArgs);
  }
  
  public void setProperty(Property paramProperty)
  {
    if (this.mValues != null)
    {
      PropertyValuesHolder localPropertyValuesHolder = this.mValues[0];
      String str = localPropertyValuesHolder.getPropertyName();
      localPropertyValuesHolder.setProperty(paramProperty);
      this.mValuesMap.remove(str);
      this.mValuesMap.put(this.mPropertyName, localPropertyValuesHolder);
    }
    if (this.mProperty != null) {
      this.mPropertyName = paramProperty.getName();
    }
    this.mProperty = paramProperty;
    this.mInitialized = false;
  }
  
  public void setPropertyName(String paramString)
  {
    if (this.mValues != null)
    {
      PropertyValuesHolder localPropertyValuesHolder = this.mValues[0];
      String str = localPropertyValuesHolder.getPropertyName();
      localPropertyValuesHolder.setPropertyName(paramString);
      this.mValuesMap.remove(str);
      this.mValuesMap.put(paramString, localPropertyValuesHolder);
    }
    this.mPropertyName = paramString;
    this.mInitialized = false;
  }
  
  public void setTarget(Object paramObject)
  {
    Object localObject = null;
    if (getTarget() != paramObject)
    {
      if (isStarted()) {
        cancel();
      }
      if (paramObject != null) {
        break label38;
      }
    }
    label38:
    for (paramObject = localObject;; paramObject = new WeakReference(paramObject))
    {
      this.mTarget = ((WeakReference)paramObject);
      this.mInitialized = false;
      return;
    }
  }
  
  public void setupEndValues()
  {
    initAnimation();
    Object localObject = getTarget();
    if (localObject != null)
    {
      int j = this.mValues.length;
      int i = 0;
      while (i < j)
      {
        this.mValues[i].setupEndValue(localObject);
        i += 1;
      }
    }
  }
  
  public void setupStartValues()
  {
    initAnimation();
    Object localObject = getTarget();
    if (localObject != null)
    {
      int j = this.mValues.length;
      int i = 0;
      while (i < j)
      {
        this.mValues[i].setupStartValue(localObject);
        i += 1;
      }
    }
  }
  
  boolean shouldAutoCancel(AnimationHandler.AnimationFrameCallback paramAnimationFrameCallback)
  {
    if (paramAnimationFrameCallback == null) {
      return false;
    }
    if ((paramAnimationFrameCallback instanceof ObjectAnimator))
    {
      paramAnimationFrameCallback = (ObjectAnimator)paramAnimationFrameCallback;
      if ((paramAnimationFrameCallback.mAutoCancel) && (hasSameTargetAndProperties(paramAnimationFrameCallback))) {
        return true;
      }
    }
    return false;
  }
  
  public void start()
  {
    AnimationHandler.getInstance().autoCancelBasedOn(this);
    super.start();
  }
  
  public String toString()
  {
    String str1 = "ObjectAnimator@" + Integer.toHexString(hashCode()) + ", target " + getTarget();
    String str2 = str1;
    if (this.mValues != null)
    {
      int i = 0;
      for (;;)
      {
        str2 = str1;
        if (i >= this.mValues.length) {
          break;
        }
        str1 = str1 + "\n    " + this.mValues[i].toString();
        i += 1;
      }
    }
    return str2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/ObjectAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */