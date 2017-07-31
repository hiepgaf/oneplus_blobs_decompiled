package android.animation;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Log;
import android.util.PathParser.PathData;
import android.util.Property;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class PropertyValuesHolder
  implements Cloneable
{
  private static Class[] DOUBLE_VARIANTS;
  private static Class[] FLOAT_VARIANTS;
  private static Class[] INTEGER_VARIANTS;
  private static final TypeEvaluator sFloatEvaluator;
  private static final HashMap<Class, HashMap<String, Method>> sGetterPropertyMap = new HashMap();
  private static final TypeEvaluator sIntEvaluator = new IntEvaluator();
  private static final HashMap<Class, HashMap<String, Method>> sSetterPropertyMap;
  private Object mAnimatedValue;
  private TypeConverter mConverter;
  private TypeEvaluator mEvaluator;
  private Method mGetter = null;
  Keyframes mKeyframes = null;
  protected Property mProperty;
  String mPropertyName;
  Method mSetter = null;
  final Object[] mTmpValueArray = new Object[1];
  Class mValueType;
  
  static
  {
    sFloatEvaluator = new FloatEvaluator();
    FLOAT_VARIANTS = new Class[] { Float.TYPE, Float.class, Double.TYPE, Integer.TYPE, Double.class, Integer.class };
    INTEGER_VARIANTS = new Class[] { Integer.TYPE, Integer.class, Float.TYPE, Double.TYPE, Float.class, Double.class };
    DOUBLE_VARIANTS = new Class[] { Double.TYPE, Double.class, Float.TYPE, Integer.TYPE, Float.class, Integer.class };
    sSetterPropertyMap = new HashMap();
  }
  
  private PropertyValuesHolder(Property paramProperty)
  {
    this.mProperty = paramProperty;
    if (paramProperty != null) {
      this.mPropertyName = paramProperty.getName();
    }
  }
  
  private PropertyValuesHolder(String paramString)
  {
    this.mPropertyName = paramString;
  }
  
  private Object convertBack(Object paramObject)
  {
    Object localObject = paramObject;
    if (this.mConverter != null)
    {
      if (!(this.mConverter instanceof BidirectionalTypeConverter)) {
        throw new IllegalArgumentException("Converter " + this.mConverter.getClass().getName() + " must be a BidirectionalTypeConverter");
      }
      localObject = ((BidirectionalTypeConverter)this.mConverter).convertBack(paramObject);
    }
    return localObject;
  }
  
  static String getMethodName(String paramString1, String paramString2)
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      return paramString1;
    }
    char c = Character.toUpperCase(paramString2.charAt(0));
    paramString2 = paramString2.substring(1);
    return paramString1 + c + paramString2;
  }
  
  private Method getPropertyFunction(Class paramClass1, String paramString, Class paramClass2)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    String str = getMethodName(paramString, this.mPropertyName);
    if (paramClass2 == null)
    {
      try
      {
        Method localMethod = paramClass1.getMethod(str, null);
        localObject2 = localMethod;
      }
      catch (NoSuchMethodException localNoSuchMethodException1)
      {
        for (;;) {}
      }
      if (localObject2 == null) {
        Log.w("PropertyValuesHolder", "Method " + getMethodName(paramString, this.mPropertyName) + "() with type " + paramClass2 + " not found on target class " + paramClass1);
      }
      return (Method)localObject2;
    }
    Class[] arrayOfClass2 = new Class[1];
    Class[] arrayOfClass1;
    label120:
    int j;
    int i;
    if (paramClass2.equals(Float.class))
    {
      arrayOfClass1 = FLOAT_VARIANTS;
      j = arrayOfClass1.length;
      i = 0;
    }
    for (;;)
    {
      localObject2 = localObject1;
      if (i >= j) {
        break;
      }
      Class localClass = arrayOfClass1[i];
      arrayOfClass2[0] = localClass;
      try
      {
        localObject2 = paramClass1.getMethod(str, arrayOfClass2);
        localObject1 = localObject2;
        if (this.mConverter == null)
        {
          localObject1 = localObject2;
          this.mValueType = localClass;
        }
        return (Method)localObject2;
      }
      catch (NoSuchMethodException localNoSuchMethodException2)
      {
        i += 1;
      }
      if (paramClass2.equals(Integer.class))
      {
        arrayOfClass1 = INTEGER_VARIANTS;
        break label120;
      }
      if (paramClass2.equals(Double.class))
      {
        arrayOfClass1 = DOUBLE_VARIANTS;
        break label120;
      }
      arrayOfClass1 = new Class[1];
      arrayOfClass1[0] = paramClass2;
      break label120;
    }
  }
  
  private static native void nCallFloatMethod(Object paramObject, long paramLong, float paramFloat);
  
  private static native void nCallFourFloatMethod(Object paramObject, long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  private static native void nCallFourIntMethod(Object paramObject, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private static native void nCallIntMethod(Object paramObject, long paramLong, int paramInt);
  
  private static native void nCallMultipleFloatMethod(Object paramObject, long paramLong, float[] paramArrayOfFloat);
  
  private static native void nCallMultipleIntMethod(Object paramObject, long paramLong, int[] paramArrayOfInt);
  
  private static native void nCallTwoFloatMethod(Object paramObject, long paramLong, float paramFloat1, float paramFloat2);
  
  private static native void nCallTwoIntMethod(Object paramObject, long paramLong, int paramInt1, int paramInt2);
  
  private static native long nGetFloatMethod(Class paramClass, String paramString);
  
  private static native long nGetIntMethod(Class paramClass, String paramString);
  
  private static native long nGetMultipleFloatMethod(Class paramClass, String paramString, int paramInt);
  
  private static native long nGetMultipleIntMethod(Class paramClass, String paramString, int paramInt);
  
  public static PropertyValuesHolder ofFloat(Property<?, Float> paramProperty, float... paramVarArgs)
  {
    return new FloatPropertyValuesHolder(paramProperty, paramVarArgs);
  }
  
  public static PropertyValuesHolder ofFloat(String paramString, float... paramVarArgs)
  {
    return new FloatPropertyValuesHolder(paramString, paramVarArgs);
  }
  
  public static PropertyValuesHolder ofInt(Property<?, Integer> paramProperty, int... paramVarArgs)
  {
    return new IntPropertyValuesHolder(paramProperty, paramVarArgs);
  }
  
  public static PropertyValuesHolder ofInt(String paramString, int... paramVarArgs)
  {
    return new IntPropertyValuesHolder(paramString, paramVarArgs);
  }
  
  public static PropertyValuesHolder ofKeyframe(Property paramProperty, Keyframe... paramVarArgs)
  {
    return ofKeyframes(paramProperty, KeyframeSet.ofKeyframe(paramVarArgs));
  }
  
  public static PropertyValuesHolder ofKeyframe(String paramString, Keyframe... paramVarArgs)
  {
    return ofKeyframes(paramString, KeyframeSet.ofKeyframe(paramVarArgs));
  }
  
  static PropertyValuesHolder ofKeyframes(Property paramProperty, Keyframes paramKeyframes)
  {
    if ((paramKeyframes instanceof Keyframes.IntKeyframes)) {
      return new IntPropertyValuesHolder(paramProperty, (Keyframes.IntKeyframes)paramKeyframes);
    }
    if ((paramKeyframes instanceof Keyframes.FloatKeyframes)) {
      return new FloatPropertyValuesHolder(paramProperty, (Keyframes.FloatKeyframes)paramKeyframes);
    }
    paramProperty = new PropertyValuesHolder(paramProperty);
    paramProperty.mKeyframes = paramKeyframes;
    paramProperty.mValueType = paramKeyframes.getType();
    return paramProperty;
  }
  
  static PropertyValuesHolder ofKeyframes(String paramString, Keyframes paramKeyframes)
  {
    if ((paramKeyframes instanceof Keyframes.IntKeyframes)) {
      return new IntPropertyValuesHolder(paramString, (Keyframes.IntKeyframes)paramKeyframes);
    }
    if ((paramKeyframes instanceof Keyframes.FloatKeyframes)) {
      return new FloatPropertyValuesHolder(paramString, (Keyframes.FloatKeyframes)paramKeyframes);
    }
    paramString = new PropertyValuesHolder(paramString);
    paramString.mKeyframes = paramKeyframes;
    paramString.mValueType = paramKeyframes.getType();
    return paramString;
  }
  
  public static <T> PropertyValuesHolder ofMultiFloat(String paramString, TypeConverter<T, float[]> paramTypeConverter, TypeEvaluator<T> paramTypeEvaluator, Keyframe... paramVarArgs)
  {
    return new MultiFloatValuesHolder(paramString, paramTypeConverter, paramTypeEvaluator, KeyframeSet.ofKeyframe(paramVarArgs));
  }
  
  @SafeVarargs
  public static <V> PropertyValuesHolder ofMultiFloat(String paramString, TypeConverter<V, float[]> paramTypeConverter, TypeEvaluator<V> paramTypeEvaluator, V... paramVarArgs)
  {
    return new MultiFloatValuesHolder(paramString, paramTypeConverter, paramTypeEvaluator, paramVarArgs);
  }
  
  public static PropertyValuesHolder ofMultiFloat(String paramString, Path paramPath)
  {
    paramPath = KeyframeSet.ofPath(paramPath);
    return new MultiFloatValuesHolder(paramString, new PointFToFloatArray(), null, paramPath);
  }
  
  public static PropertyValuesHolder ofMultiFloat(String paramString, float[][] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length < 2) {
      throw new IllegalArgumentException("At least 2 values must be supplied");
    }
    int j = 0;
    int i = 0;
    if (i < paramArrayOfFloat.length)
    {
      if (paramArrayOfFloat[i] == null) {
        throw new IllegalArgumentException("values must not be null");
      }
      int m = paramArrayOfFloat[i].length;
      int k;
      if (i == 0) {
        k = m;
      }
      do
      {
        i += 1;
        j = k;
        break;
        k = j;
      } while (m == j);
      throw new IllegalArgumentException("Values must all have the same length");
    }
    return new MultiFloatValuesHolder(paramString, null, new FloatArrayEvaluator(new float[j]), paramArrayOfFloat);
  }
  
  public static <T> PropertyValuesHolder ofMultiInt(String paramString, TypeConverter<T, int[]> paramTypeConverter, TypeEvaluator<T> paramTypeEvaluator, Keyframe... paramVarArgs)
  {
    return new MultiIntValuesHolder(paramString, paramTypeConverter, paramTypeEvaluator, KeyframeSet.ofKeyframe(paramVarArgs));
  }
  
  @SafeVarargs
  public static <V> PropertyValuesHolder ofMultiInt(String paramString, TypeConverter<V, int[]> paramTypeConverter, TypeEvaluator<V> paramTypeEvaluator, V... paramVarArgs)
  {
    return new MultiIntValuesHolder(paramString, paramTypeConverter, paramTypeEvaluator, paramVarArgs);
  }
  
  public static PropertyValuesHolder ofMultiInt(String paramString, Path paramPath)
  {
    paramPath = KeyframeSet.ofPath(paramPath);
    return new MultiIntValuesHolder(paramString, new PointFToIntArray(), null, paramPath);
  }
  
  public static PropertyValuesHolder ofMultiInt(String paramString, int[][] paramArrayOfInt)
  {
    if (paramArrayOfInt.length < 2) {
      throw new IllegalArgumentException("At least 2 values must be supplied");
    }
    int j = 0;
    int i = 0;
    if (i < paramArrayOfInt.length)
    {
      if (paramArrayOfInt[i] == null) {
        throw new IllegalArgumentException("values must not be null");
      }
      int m = paramArrayOfInt[i].length;
      int k;
      if (i == 0) {
        k = m;
      }
      do
      {
        i += 1;
        j = k;
        break;
        k = j;
      } while (m == j);
      throw new IllegalArgumentException("Values must all have the same length");
    }
    return new MultiIntValuesHolder(paramString, null, new IntArrayEvaluator(new int[j]), paramArrayOfInt);
  }
  
  @SafeVarargs
  public static <T, V> PropertyValuesHolder ofObject(Property<?, V> paramProperty, TypeConverter<T, V> paramTypeConverter, TypeEvaluator<T> paramTypeEvaluator, T... paramVarArgs)
  {
    paramProperty = new PropertyValuesHolder(paramProperty);
    paramProperty.setConverter(paramTypeConverter);
    paramProperty.setObjectValues(paramVarArgs);
    paramProperty.setEvaluator(paramTypeEvaluator);
    return paramProperty;
  }
  
  public static <V> PropertyValuesHolder ofObject(Property<?, V> paramProperty, TypeConverter<PointF, V> paramTypeConverter, Path paramPath)
  {
    paramProperty = new PropertyValuesHolder(paramProperty);
    paramProperty.mKeyframes = KeyframeSet.ofPath(paramPath);
    paramProperty.mValueType = PointF.class;
    paramProperty.setConverter(paramTypeConverter);
    return paramProperty;
  }
  
  @SafeVarargs
  public static <V> PropertyValuesHolder ofObject(Property paramProperty, TypeEvaluator<V> paramTypeEvaluator, V... paramVarArgs)
  {
    paramProperty = new PropertyValuesHolder(paramProperty);
    paramProperty.setObjectValues(paramVarArgs);
    paramProperty.setEvaluator(paramTypeEvaluator);
    return paramProperty;
  }
  
  public static PropertyValuesHolder ofObject(String paramString, TypeConverter<PointF, ?> paramTypeConverter, Path paramPath)
  {
    paramString = new PropertyValuesHolder(paramString);
    paramString.mKeyframes = KeyframeSet.ofPath(paramPath);
    paramString.mValueType = PointF.class;
    paramString.setConverter(paramTypeConverter);
    return paramString;
  }
  
  public static PropertyValuesHolder ofObject(String paramString, TypeEvaluator paramTypeEvaluator, Object... paramVarArgs)
  {
    paramString = new PropertyValuesHolder(paramString);
    paramString.setObjectValues(paramVarArgs);
    paramString.setEvaluator(paramTypeEvaluator);
    return paramString;
  }
  
  private void setupGetter(Class paramClass)
  {
    this.mGetter = setupSetterOrGetter(paramClass, sGetterPropertyMap, "get", null);
  }
  
  private Method setupSetterOrGetter(Class paramClass1, HashMap<Class, HashMap<String, Method>> paramHashMap, String paramString, Class paramClass2)
  {
    Object localObject2 = null;
    try
    {
      HashMap localHashMap = (HashMap)paramHashMap.get(paramClass1);
      int i = 0;
      Object localObject1 = localObject2;
      if (localHashMap != null)
      {
        boolean bool = localHashMap.containsKey(this.mPropertyName);
        localObject1 = localObject2;
        i = bool;
        if (bool)
        {
          localObject1 = (Method)localHashMap.get(this.mPropertyName);
          i = bool;
        }
      }
      if (i == 0)
      {
        localObject1 = getPropertyFunction(paramClass1, paramString, paramClass2);
        paramString = localHashMap;
        if (localHashMap == null)
        {
          paramString = new HashMap();
          paramHashMap.put(paramClass1, paramString);
        }
        paramString.put(this.mPropertyName, localObject1);
      }
      return (Method)localObject1;
    }
    finally {}
  }
  
  private void setupValue(Object paramObject, Keyframe paramKeyframe)
  {
    if (this.mProperty != null)
    {
      paramKeyframe.setValue(convertBack(this.mProperty.get(paramObject)));
      return;
    }
    try
    {
      if (this.mGetter == null)
      {
        setupGetter(paramObject.getClass());
        if (this.mGetter == null) {
          return;
        }
      }
      paramKeyframe.setValue(convertBack(this.mGetter.invoke(paramObject, new Object[0])));
      return;
    }
    catch (InvocationTargetException paramObject)
    {
      Log.e("PropertyValuesHolder", ((InvocationTargetException)paramObject).toString());
      return;
    }
    catch (IllegalAccessException paramObject)
    {
      Log.e("PropertyValuesHolder", ((IllegalAccessException)paramObject).toString());
    }
  }
  
  void calculateValue(float paramFloat)
  {
    Object localObject = this.mKeyframes.getValue(paramFloat);
    if (this.mConverter == null) {}
    for (;;)
    {
      this.mAnimatedValue = localObject;
      return;
      localObject = this.mConverter.convert(localObject);
    }
  }
  
  public PropertyValuesHolder clone()
  {
    try
    {
      PropertyValuesHolder localPropertyValuesHolder = (PropertyValuesHolder)super.clone();
      localPropertyValuesHolder.mPropertyName = this.mPropertyName;
      localPropertyValuesHolder.mProperty = this.mProperty;
      localPropertyValuesHolder.mKeyframes = this.mKeyframes.clone();
      localPropertyValuesHolder.mEvaluator = this.mEvaluator;
      return localPropertyValuesHolder;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  Object getAnimatedValue()
  {
    return this.mAnimatedValue;
  }
  
  public String getPropertyName()
  {
    return this.mPropertyName;
  }
  
  public void getPropertyValues(PropertyValues paramPropertyValues)
  {
    init();
    paramPropertyValues.propertyName = this.mPropertyName;
    paramPropertyValues.type = this.mValueType;
    paramPropertyValues.startValue = this.mKeyframes.getValue(0.0F);
    if ((paramPropertyValues.startValue instanceof PathParser.PathData)) {
      paramPropertyValues.startValue = new PathParser.PathData((PathParser.PathData)paramPropertyValues.startValue);
    }
    paramPropertyValues.endValue = this.mKeyframes.getValue(1.0F);
    if ((paramPropertyValues.endValue instanceof PathParser.PathData)) {
      paramPropertyValues.endValue = new PathParser.PathData((PathParser.PathData)paramPropertyValues.endValue);
    }
    if (((this.mKeyframes instanceof PathKeyframes.FloatKeyframesBase)) || ((this.mKeyframes instanceof PathKeyframes.IntKeyframesBase)) || ((this.mKeyframes.getKeyframes() != null) && (this.mKeyframes.getKeyframes().size() > 2)))
    {
      paramPropertyValues.dataSource = new PropertyValuesHolder.PropertyValues.DataSource()
      {
        public Object getValueAtFraction(float paramAnonymousFloat)
        {
          return PropertyValuesHolder.this.mKeyframes.getValue(paramAnonymousFloat);
        }
      };
      return;
    }
    paramPropertyValues.dataSource = null;
  }
  
  public Class getValueType()
  {
    return this.mValueType;
  }
  
  void init()
  {
    TypeEvaluator localTypeEvaluator = null;
    if (this.mEvaluator == null)
    {
      if (this.mValueType != Integer.class) {
        break label48;
      }
      localTypeEvaluator = sIntEvaluator;
    }
    for (;;)
    {
      this.mEvaluator = localTypeEvaluator;
      if (this.mEvaluator != null) {
        this.mKeyframes.setEvaluator(this.mEvaluator);
      }
      return;
      label48:
      if (this.mValueType == Float.class) {
        localTypeEvaluator = sFloatEvaluator;
      }
    }
  }
  
  void setAnimatedValue(Object paramObject)
  {
    if (this.mProperty != null) {
      this.mProperty.set(paramObject, getAnimatedValue());
    }
    if (this.mSetter != null) {}
    try
    {
      this.mTmpValueArray[0] = getAnimatedValue();
      this.mSetter.invoke(paramObject, this.mTmpValueArray);
      return;
    }
    catch (IllegalAccessException paramObject)
    {
      Log.e("PropertyValuesHolder", ((IllegalAccessException)paramObject).toString());
      return;
    }
    catch (InvocationTargetException paramObject)
    {
      Log.e("PropertyValuesHolder", ((InvocationTargetException)paramObject).toString());
    }
  }
  
  public void setConverter(TypeConverter paramTypeConverter)
  {
    this.mConverter = paramTypeConverter;
  }
  
  public void setEvaluator(TypeEvaluator paramTypeEvaluator)
  {
    this.mEvaluator = paramTypeEvaluator;
    this.mKeyframes.setEvaluator(paramTypeEvaluator);
  }
  
  public void setFloatValues(float... paramVarArgs)
  {
    this.mValueType = Float.TYPE;
    this.mKeyframes = KeyframeSet.ofFloat(paramVarArgs);
  }
  
  public void setIntValues(int... paramVarArgs)
  {
    this.mValueType = Integer.TYPE;
    this.mKeyframes = KeyframeSet.ofInt(paramVarArgs);
  }
  
  public void setKeyframes(Keyframe... paramVarArgs)
  {
    int j = paramVarArgs.length;
    Keyframe[] arrayOfKeyframe = new Keyframe[Math.max(j, 2)];
    this.mValueType = paramVarArgs[0].getType();
    int i = 0;
    while (i < j)
    {
      arrayOfKeyframe[i] = paramVarArgs[i];
      i += 1;
    }
    this.mKeyframes = new KeyframeSet(arrayOfKeyframe);
  }
  
  public void setObjectValues(Object... paramVarArgs)
  {
    this.mValueType = paramVarArgs[0].getClass();
    this.mKeyframes = KeyframeSet.ofObject(paramVarArgs);
    if (this.mEvaluator != null) {
      this.mKeyframes.setEvaluator(this.mEvaluator);
    }
  }
  
  public void setProperty(Property paramProperty)
  {
    this.mProperty = paramProperty;
  }
  
  public void setPropertyName(String paramString)
  {
    this.mPropertyName = paramString;
  }
  
  void setupEndValue(Object paramObject)
  {
    List localList = this.mKeyframes.getKeyframes();
    if (!localList.isEmpty()) {
      setupValue(paramObject, (Keyframe)localList.get(localList.size() - 1));
    }
  }
  
  void setupSetter(Class paramClass)
  {
    if (this.mConverter == null) {}
    for (Class localClass = this.mValueType;; localClass = this.mConverter.getTargetType())
    {
      this.mSetter = setupSetterOrGetter(paramClass, sSetterPropertyMap, "set", localClass);
      return;
    }
  }
  
  void setupSetterAndGetter(Object paramObject)
  {
    this.mKeyframes.invalidateCache();
    Object localObject1;
    if (this.mProperty != null) {
      localObject1 = null;
    }
    for (;;)
    {
      Object localObject4;
      int i;
      Object localObject3;
      try
      {
        localObject4 = this.mKeyframes.getKeyframes();
        if (localObject4 == null)
        {
          i = 0;
          break label366;
          if (j < i)
          {
            Keyframe localKeyframe = (Keyframe)((List)localObject4).get(j);
            if (localKeyframe.hasValue())
            {
              localObject3 = localObject1;
              if (!localKeyframe.valueWasSetOnStart()) {
                break label371;
              }
            }
            localObject3 = localObject1;
            if (localObject1 == null) {
              localObject3 = convertBack(this.mProperty.get(paramObject));
            }
            localKeyframe.setValue(localObject3);
            localKeyframe.setValueWasSetOnStart(true);
            break label371;
          }
        }
        else
        {
          i = ((List)localObject4).size();
          break label366;
        }
        return;
      }
      catch (ClassCastException localClassCastException)
      {
        Log.w("PropertyValuesHolder", "No such property (" + this.mProperty.getName() + ") on target object " + paramObject + ". Trying reflection instead");
        this.mProperty = null;
      }
      if (this.mProperty == null)
      {
        localObject2 = paramObject.getClass();
        if (this.mSetter == null) {
          setupSetter((Class)localObject2);
        }
        localObject3 = this.mKeyframes.getKeyframes();
        if (localObject3 == null)
        {
          i = 0;
          j = 0;
        }
        for (;;)
        {
          if (j < i)
          {
            localObject4 = (Keyframe)((List)localObject3).get(j);
            if ((!((Keyframe)localObject4).hasValue()) || (((Keyframe)localObject4).valueWasSetOnStart())) {
              if (this.mGetter == null)
              {
                setupGetter((Class)localObject2);
                if (this.mGetter == null)
                {
                  return;
                  i = ((List)localObject3).size();
                  break;
                }
              }
            }
            try
            {
              ((Keyframe)localObject4).setValue(convertBack(this.mGetter.invoke(paramObject, new Object[0])));
              ((Keyframe)localObject4).setValueWasSetOnStart(true);
              j += 1;
            }
            catch (IllegalAccessException localIllegalAccessException)
            {
              for (;;)
              {
                Log.e("PropertyValuesHolder", localIllegalAccessException.toString());
              }
            }
            catch (InvocationTargetException localInvocationTargetException)
            {
              for (;;)
              {
                Log.e("PropertyValuesHolder", localInvocationTargetException.toString());
              }
            }
          }
        }
      }
      return;
      label366:
      int j = 0;
      continue;
      label371:
      j += 1;
      Object localObject2 = localObject3;
    }
  }
  
  void setupStartValue(Object paramObject)
  {
    List localList = this.mKeyframes.getKeyframes();
    if (!localList.isEmpty()) {
      setupValue(paramObject, (Keyframe)localList.get(0));
    }
  }
  
  public String toString()
  {
    return this.mPropertyName + ": " + this.mKeyframes.toString();
  }
  
  static class FloatPropertyValuesHolder
    extends PropertyValuesHolder
  {
    private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap();
    float mFloatAnimatedValue;
    Keyframes.FloatKeyframes mFloatKeyframes;
    private FloatProperty mFloatProperty;
    long mJniSetter;
    
    public FloatPropertyValuesHolder(Property paramProperty, Keyframes.FloatKeyframes paramFloatKeyframes)
    {
      super(null);
      this.mValueType = Float.TYPE;
      this.mKeyframes = paramFloatKeyframes;
      this.mFloatKeyframes = paramFloatKeyframes;
      if ((paramProperty instanceof FloatProperty)) {
        this.mFloatProperty = ((FloatProperty)this.mProperty);
      }
    }
    
    public FloatPropertyValuesHolder(Property paramProperty, float... paramVarArgs)
    {
      super(null);
      setFloatValues(paramVarArgs);
      if ((paramProperty instanceof FloatProperty)) {
        this.mFloatProperty = ((FloatProperty)this.mProperty);
      }
    }
    
    public FloatPropertyValuesHolder(String paramString, Keyframes.FloatKeyframes paramFloatKeyframes)
    {
      super(null);
      this.mValueType = Float.TYPE;
      this.mKeyframes = paramFloatKeyframes;
      this.mFloatKeyframes = paramFloatKeyframes;
    }
    
    public FloatPropertyValuesHolder(String paramString, float... paramVarArgs)
    {
      super(null);
      setFloatValues(paramVarArgs);
    }
    
    void calculateValue(float paramFloat)
    {
      this.mFloatAnimatedValue = this.mFloatKeyframes.getFloatValue(paramFloat);
    }
    
    public FloatPropertyValuesHolder clone()
    {
      FloatPropertyValuesHolder localFloatPropertyValuesHolder = (FloatPropertyValuesHolder)super.clone();
      localFloatPropertyValuesHolder.mFloatKeyframes = ((Keyframes.FloatKeyframes)localFloatPropertyValuesHolder.mKeyframes);
      return localFloatPropertyValuesHolder;
    }
    
    Object getAnimatedValue()
    {
      return Float.valueOf(this.mFloatAnimatedValue);
    }
    
    void setAnimatedValue(Object paramObject)
    {
      if (this.mFloatProperty != null)
      {
        this.mFloatProperty.setValue(paramObject, this.mFloatAnimatedValue);
        return;
      }
      if (this.mProperty != null)
      {
        this.mProperty.set(paramObject, Float.valueOf(this.mFloatAnimatedValue));
        return;
      }
      if (this.mJniSetter != 0L)
      {
        PropertyValuesHolder.-wrap4(paramObject, this.mJniSetter, this.mFloatAnimatedValue);
        return;
      }
      if (this.mSetter != null) {}
      try
      {
        this.mTmpValueArray[0] = Float.valueOf(this.mFloatAnimatedValue);
        this.mSetter.invoke(paramObject, this.mTmpValueArray);
        return;
      }
      catch (IllegalAccessException paramObject)
      {
        Log.e("PropertyValuesHolder", ((IllegalAccessException)paramObject).toString());
        return;
      }
      catch (InvocationTargetException paramObject)
      {
        Log.e("PropertyValuesHolder", ((InvocationTargetException)paramObject).toString());
      }
    }
    
    public void setFloatValues(float... paramVarArgs)
    {
      super.setFloatValues(paramVarArgs);
      this.mFloatKeyframes = ((Keyframes.FloatKeyframes)this.mKeyframes);
    }
    
    public void setProperty(Property paramProperty)
    {
      if ((paramProperty instanceof FloatProperty))
      {
        this.mFloatProperty = ((FloatProperty)paramProperty);
        return;
      }
      super.setProperty(paramProperty);
    }
    
    void setupSetter(Class paramClass)
    {
      if (this.mProperty != null) {
        return;
      }
      HashMap localHashMap1;
      Object localObject;
      synchronized (sJNISetterPropertyMap)
      {
        localHashMap1 = (HashMap)sJNISetterPropertyMap.get(paramClass);
        int i = 0;
        if (localHashMap1 != null)
        {
          boolean bool = localHashMap1.containsKey(this.mPropertyName);
          i = bool;
          if (bool)
          {
            localObject = (Long)localHashMap1.get(this.mPropertyName);
            i = bool;
            if (localObject != null)
            {
              this.mJniSetter = ((Long)localObject).longValue();
              i = bool;
            }
          }
        }
        if (i == 0) {
          localObject = getMethodName("set", this.mPropertyName);
        }
      }
      try
      {
        this.mJniSetter = PropertyValuesHolder.-wrap0(paramClass, (String)localObject);
        localObject = localHashMap1;
        if (localHashMap1 == null)
        {
          localObject = new HashMap();
          sJNISetterPropertyMap.put(paramClass, localObject);
        }
        ((HashMap)localObject).put(this.mPropertyName, Long.valueOf(this.mJniSetter));
        if (this.mJniSetter == 0L) {
          super.setupSetter(paramClass);
        }
        return;
        paramClass = finally;
        throw paramClass;
      }
      catch (NoSuchMethodError localNoSuchMethodError)
      {
        for (;;) {}
      }
    }
  }
  
  static class IntPropertyValuesHolder
    extends PropertyValuesHolder
  {
    private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap();
    int mIntAnimatedValue;
    Keyframes.IntKeyframes mIntKeyframes;
    private IntProperty mIntProperty;
    long mJniSetter;
    
    public IntPropertyValuesHolder(Property paramProperty, Keyframes.IntKeyframes paramIntKeyframes)
    {
      super(null);
      this.mValueType = Integer.TYPE;
      this.mKeyframes = paramIntKeyframes;
      this.mIntKeyframes = paramIntKeyframes;
      if ((paramProperty instanceof IntProperty)) {
        this.mIntProperty = ((IntProperty)this.mProperty);
      }
    }
    
    public IntPropertyValuesHolder(Property paramProperty, int... paramVarArgs)
    {
      super(null);
      setIntValues(paramVarArgs);
      if ((paramProperty instanceof IntProperty)) {
        this.mIntProperty = ((IntProperty)this.mProperty);
      }
    }
    
    public IntPropertyValuesHolder(String paramString, Keyframes.IntKeyframes paramIntKeyframes)
    {
      super(null);
      this.mValueType = Integer.TYPE;
      this.mKeyframes = paramIntKeyframes;
      this.mIntKeyframes = paramIntKeyframes;
    }
    
    public IntPropertyValuesHolder(String paramString, int... paramVarArgs)
    {
      super(null);
      setIntValues(paramVarArgs);
    }
    
    void calculateValue(float paramFloat)
    {
      this.mIntAnimatedValue = this.mIntKeyframes.getIntValue(paramFloat);
    }
    
    public IntPropertyValuesHolder clone()
    {
      IntPropertyValuesHolder localIntPropertyValuesHolder = (IntPropertyValuesHolder)super.clone();
      localIntPropertyValuesHolder.mIntKeyframes = ((Keyframes.IntKeyframes)localIntPropertyValuesHolder.mKeyframes);
      return localIntPropertyValuesHolder;
    }
    
    Object getAnimatedValue()
    {
      return Integer.valueOf(this.mIntAnimatedValue);
    }
    
    void setAnimatedValue(Object paramObject)
    {
      if (this.mIntProperty != null)
      {
        this.mIntProperty.setValue(paramObject, this.mIntAnimatedValue);
        return;
      }
      if (this.mProperty != null)
      {
        this.mProperty.set(paramObject, Integer.valueOf(this.mIntAnimatedValue));
        return;
      }
      if (this.mJniSetter != 0L)
      {
        PropertyValuesHolder.-wrap7(paramObject, this.mJniSetter, this.mIntAnimatedValue);
        return;
      }
      if (this.mSetter != null) {}
      try
      {
        this.mTmpValueArray[0] = Integer.valueOf(this.mIntAnimatedValue);
        this.mSetter.invoke(paramObject, this.mTmpValueArray);
        return;
      }
      catch (IllegalAccessException paramObject)
      {
        Log.e("PropertyValuesHolder", ((IllegalAccessException)paramObject).toString());
        return;
      }
      catch (InvocationTargetException paramObject)
      {
        Log.e("PropertyValuesHolder", ((InvocationTargetException)paramObject).toString());
      }
    }
    
    public void setIntValues(int... paramVarArgs)
    {
      super.setIntValues(paramVarArgs);
      this.mIntKeyframes = ((Keyframes.IntKeyframes)this.mKeyframes);
    }
    
    public void setProperty(Property paramProperty)
    {
      if ((paramProperty instanceof IntProperty))
      {
        this.mIntProperty = ((IntProperty)paramProperty);
        return;
      }
      super.setProperty(paramProperty);
    }
    
    void setupSetter(Class paramClass)
    {
      if (this.mProperty != null) {
        return;
      }
      HashMap localHashMap1;
      Object localObject;
      synchronized (sJNISetterPropertyMap)
      {
        localHashMap1 = (HashMap)sJNISetterPropertyMap.get(paramClass);
        int i = 0;
        if (localHashMap1 != null)
        {
          boolean bool = localHashMap1.containsKey(this.mPropertyName);
          i = bool;
          if (bool)
          {
            localObject = (Long)localHashMap1.get(this.mPropertyName);
            i = bool;
            if (localObject != null)
            {
              this.mJniSetter = ((Long)localObject).longValue();
              i = bool;
            }
          }
        }
        if (i == 0) {
          localObject = getMethodName("set", this.mPropertyName);
        }
      }
      try
      {
        this.mJniSetter = PropertyValuesHolder.-wrap1(paramClass, (String)localObject);
        localObject = localHashMap1;
        if (localHashMap1 == null)
        {
          localObject = new HashMap();
          sJNISetterPropertyMap.put(paramClass, localObject);
        }
        ((HashMap)localObject).put(this.mPropertyName, Long.valueOf(this.mJniSetter));
        if (this.mJniSetter == 0L) {
          super.setupSetter(paramClass);
        }
        return;
        paramClass = finally;
        throw paramClass;
      }
      catch (NoSuchMethodError localNoSuchMethodError)
      {
        for (;;) {}
      }
    }
  }
  
  static class MultiFloatValuesHolder
    extends PropertyValuesHolder
  {
    private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap();
    private long mJniSetter;
    
    public MultiFloatValuesHolder(String paramString, TypeConverter paramTypeConverter, TypeEvaluator paramTypeEvaluator, Keyframes paramKeyframes)
    {
      super(null);
      setConverter(paramTypeConverter);
      this.mKeyframes = paramKeyframes;
      setEvaluator(paramTypeEvaluator);
    }
    
    public MultiFloatValuesHolder(String paramString, TypeConverter paramTypeConverter, TypeEvaluator paramTypeEvaluator, Object... paramVarArgs)
    {
      super(null);
      setConverter(paramTypeConverter);
      setObjectValues(paramVarArgs);
      setEvaluator(paramTypeEvaluator);
    }
    
    void setAnimatedValue(Object paramObject)
    {
      float[] arrayOfFloat = (float[])getAnimatedValue();
      int i = arrayOfFloat.length;
      if (this.mJniSetter != 0L) {}
      switch (i)
      {
      case 3: 
      default: 
        PropertyValuesHolder.-wrap8(paramObject, this.mJniSetter, arrayOfFloat);
        return;
      case 1: 
        PropertyValuesHolder.-wrap4(paramObject, this.mJniSetter, arrayOfFloat[0]);
        return;
      case 2: 
        PropertyValuesHolder.-wrap10(paramObject, this.mJniSetter, arrayOfFloat[0], arrayOfFloat[1]);
        return;
      }
      PropertyValuesHolder.-wrap5(paramObject, this.mJniSetter, arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
    }
    
    void setupSetter(Class paramClass)
    {
      if (this.mJniSetter != 0L) {
        return;
      }
      synchronized (sJNISetterPropertyMap)
      {
        HashMap localHashMap1 = (HashMap)sJNISetterPropertyMap.get(paramClass);
        int j = 0;
        Object localObject;
        if (localHashMap1 != null)
        {
          boolean bool = localHashMap1.containsKey(this.mPropertyName);
          j = bool;
          if (bool)
          {
            localObject = (Long)localHashMap1.get(this.mPropertyName);
            j = bool;
            if (localObject != null)
            {
              this.mJniSetter = ((Long)localObject).longValue();
              j = bool;
            }
          }
        }
        int i;
        if (j == 0)
        {
          localObject = getMethodName("set", this.mPropertyName);
          calculateValue(0.0F);
          i = ((float[])getAnimatedValue()).length;
        }
        try
        {
          this.mJniSetter = PropertyValuesHolder.-wrap2(paramClass, (String)localObject, i);
          localObject = localHashMap1;
          if (localHashMap1 == null)
          {
            localObject = new HashMap();
            sJNISetterPropertyMap.put(paramClass, localObject);
          }
          ((HashMap)localObject).put(this.mPropertyName, Long.valueOf(this.mJniSetter));
          return;
        }
        catch (NoSuchMethodError localNoSuchMethodError1)
        {
          for (;;)
          {
            try
            {
              this.mJniSetter = PropertyValuesHolder.-wrap2(paramClass, this.mPropertyName, i);
            }
            catch (NoSuchMethodError localNoSuchMethodError2) {}
          }
        }
      }
    }
    
    void setupSetterAndGetter(Object paramObject)
    {
      setupSetter(paramObject.getClass());
    }
  }
  
  static class MultiIntValuesHolder
    extends PropertyValuesHolder
  {
    private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap();
    private long mJniSetter;
    
    public MultiIntValuesHolder(String paramString, TypeConverter paramTypeConverter, TypeEvaluator paramTypeEvaluator, Keyframes paramKeyframes)
    {
      super(null);
      setConverter(paramTypeConverter);
      this.mKeyframes = paramKeyframes;
      setEvaluator(paramTypeEvaluator);
    }
    
    public MultiIntValuesHolder(String paramString, TypeConverter paramTypeConverter, TypeEvaluator paramTypeEvaluator, Object... paramVarArgs)
    {
      super(null);
      setConverter(paramTypeConverter);
      setObjectValues(paramVarArgs);
      setEvaluator(paramTypeEvaluator);
    }
    
    void setAnimatedValue(Object paramObject)
    {
      int[] arrayOfInt = (int[])getAnimatedValue();
      int i = arrayOfInt.length;
      if (this.mJniSetter != 0L) {}
      switch (i)
      {
      case 3: 
      default: 
        PropertyValuesHolder.-wrap9(paramObject, this.mJniSetter, arrayOfInt);
        return;
      case 1: 
        PropertyValuesHolder.-wrap7(paramObject, this.mJniSetter, arrayOfInt[0]);
        return;
      case 2: 
        PropertyValuesHolder.-wrap11(paramObject, this.mJniSetter, arrayOfInt[0], arrayOfInt[1]);
        return;
      }
      PropertyValuesHolder.-wrap6(paramObject, this.mJniSetter, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
    }
    
    void setupSetter(Class paramClass)
    {
      if (this.mJniSetter != 0L) {
        return;
      }
      synchronized (sJNISetterPropertyMap)
      {
        HashMap localHashMap1 = (HashMap)sJNISetterPropertyMap.get(paramClass);
        int j = 0;
        Object localObject;
        if (localHashMap1 != null)
        {
          boolean bool = localHashMap1.containsKey(this.mPropertyName);
          j = bool;
          if (bool)
          {
            localObject = (Long)localHashMap1.get(this.mPropertyName);
            j = bool;
            if (localObject != null)
            {
              this.mJniSetter = ((Long)localObject).longValue();
              j = bool;
            }
          }
        }
        int i;
        if (j == 0)
        {
          localObject = getMethodName("set", this.mPropertyName);
          calculateValue(0.0F);
          i = ((int[])getAnimatedValue()).length;
        }
        try
        {
          this.mJniSetter = PropertyValuesHolder.-wrap3(paramClass, (String)localObject, i);
          localObject = localHashMap1;
          if (localHashMap1 == null)
          {
            localObject = new HashMap();
            sJNISetterPropertyMap.put(paramClass, localObject);
          }
          ((HashMap)localObject).put(this.mPropertyName, Long.valueOf(this.mJniSetter));
          return;
        }
        catch (NoSuchMethodError localNoSuchMethodError1)
        {
          for (;;)
          {
            try
            {
              this.mJniSetter = PropertyValuesHolder.-wrap3(paramClass, this.mPropertyName, i);
            }
            catch (NoSuchMethodError localNoSuchMethodError2) {}
          }
        }
      }
    }
    
    void setupSetterAndGetter(Object paramObject)
    {
      setupSetter(paramObject.getClass());
    }
  }
  
  private static class PointFToFloatArray
    extends TypeConverter<PointF, float[]>
  {
    private float[] mCoordinates = new float[2];
    
    public PointFToFloatArray()
    {
      super(float[].class);
    }
    
    public float[] convert(PointF paramPointF)
    {
      this.mCoordinates[0] = paramPointF.x;
      this.mCoordinates[1] = paramPointF.y;
      return this.mCoordinates;
    }
  }
  
  private static class PointFToIntArray
    extends TypeConverter<PointF, int[]>
  {
    private int[] mCoordinates = new int[2];
    
    public PointFToIntArray()
    {
      super(int[].class);
    }
    
    public int[] convert(PointF paramPointF)
    {
      this.mCoordinates[0] = Math.round(paramPointF.x);
      this.mCoordinates[1] = Math.round(paramPointF.y);
      return this.mCoordinates;
    }
  }
  
  public static class PropertyValues
  {
    public DataSource dataSource = null;
    public Object endValue;
    public String propertyName;
    public Object startValue;
    public Class type;
    
    public String toString()
    {
      return "property name: " + this.propertyName + ", type: " + this.type + ", startValue: " + this.startValue.toString() + ", endValue: " + this.endValue.toString();
    }
    
    public static abstract interface DataSource
    {
      public abstract Object getValueAtFraction(float paramFloat);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/PropertyValuesHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */