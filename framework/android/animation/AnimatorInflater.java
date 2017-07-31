package android.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.PathParser;
import android.util.PathParser.PathData;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.InflateException;
import android.view.animation.AnimationUtils;
import android.view.animation.BaseInterpolator;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimatorInflater
{
  private static final boolean DBG_ANIMATOR_INFLATER = false;
  private static final int SEQUENTIALLY = 1;
  private static final String TAG = "AnimatorInflater";
  private static final int TOGETHER = 0;
  private static final int VALUE_TYPE_COLOR = 3;
  private static final int VALUE_TYPE_FLOAT = 0;
  private static final int VALUE_TYPE_INT = 1;
  private static final int VALUE_TYPE_PATH = 2;
  private static final int VALUE_TYPE_UNDEFINED = 4;
  private static final TypedValue sTmpTypedValue = new TypedValue();
  
  private static Animator createAnimatorFromXml(Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, float paramFloat)
    throws XmlPullParserException, IOException
  {
    return createAnimatorFromXml(paramResources, paramTheme, paramXmlPullParser, Xml.asAttributeSet(paramXmlPullParser), null, 0, paramFloat);
  }
  
  private static Animator createAnimatorFromXml(Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, AnimatorSet paramAnimatorSet, int paramInt, float paramFloat)
    throws XmlPullParserException, IOException
  {
    Object localObject3 = null;
    Object localObject2 = null;
    int j = paramXmlPullParser.getDepth();
    do
    {
      i = paramXmlPullParser.next();
      if (((i == 3) && (paramXmlPullParser.getDepth() <= j)) || (i == 1)) {
        break;
      }
    } while (i != 2);
    Object localObject1 = paramXmlPullParser.getName();
    int i = 0;
    if (((String)localObject1).equals("objectAnimator")) {
      localObject1 = loadObjectAnimator(paramResources, paramTheme, paramAttributeSet, paramFloat);
    }
    for (;;)
    {
      localObject3 = localObject1;
      if (paramAnimatorSet == null) {
        break;
      }
      localObject3 = localObject1;
      if (i != 0) {
        break;
      }
      Object localObject4 = localObject2;
      if (localObject2 == null) {
        localObject4 = new ArrayList();
      }
      ((ArrayList)localObject4).add(localObject1);
      localObject3 = localObject1;
      localObject2 = localObject4;
      break;
      if (((String)localObject1).equals("animator"))
      {
        localObject1 = loadAnimator(paramResources, paramTheme, paramAttributeSet, null, paramFloat);
      }
      else
      {
        if (((String)localObject1).equals("set"))
        {
          localObject3 = new AnimatorSet();
          if (paramTheme != null) {}
          for (localObject1 = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.AnimatorSet, 0, 0);; localObject1 = paramResources.obtainAttributes(paramAttributeSet, R.styleable.AnimatorSet))
          {
            ((Animator)localObject3).appendChangingConfigurations(((TypedArray)localObject1).getChangingConfigurations());
            int k = ((TypedArray)localObject1).getInt(0, 0);
            createAnimatorFromXml(paramResources, paramTheme, paramXmlPullParser, paramAttributeSet, (AnimatorSet)localObject3, k, paramFloat);
            ((TypedArray)localObject1).recycle();
            localObject1 = localObject3;
            break;
          }
        }
        if (!((String)localObject1).equals("propertyValuesHolder")) {
          break label317;
        }
        localObject1 = loadValues(paramResources, paramTheme, paramXmlPullParser, Xml.asAttributeSet(paramXmlPullParser));
        if ((localObject1 != null) && (localObject3 != null) && ((localObject3 instanceof ValueAnimator))) {
          ((ValueAnimator)localObject3).setValues((PropertyValuesHolder[])localObject1);
        }
        i = 1;
        localObject1 = localObject3;
      }
    }
    label317:
    throw new RuntimeException("Unknown animator name: " + paramXmlPullParser.getName());
    if ((paramAnimatorSet != null) && (localObject2 != null))
    {
      paramResources = new Animator[((ArrayList)localObject2).size()];
      i = 0;
      paramTheme = ((Iterable)localObject2).iterator();
      while (paramTheme.hasNext())
      {
        paramResources[i] = ((Animator)paramTheme.next());
        i += 1;
      }
      if (paramInt == 0) {
        paramAnimatorSet.playTogether(paramResources);
      }
    }
    else
    {
      return (Animator)localObject3;
    }
    paramAnimatorSet.playSequentially(paramResources);
    return (Animator)localObject3;
  }
  
  private static Keyframe createNewKeyframe(Keyframe paramKeyframe, float paramFloat)
  {
    if (paramKeyframe.getType() == Float.TYPE) {
      return Keyframe.ofFloat(paramFloat);
    }
    if (paramKeyframe.getType() == Integer.TYPE) {
      return Keyframe.ofInt(paramFloat);
    }
    return Keyframe.ofObject(paramFloat);
  }
  
  private static StateListAnimator createStateListAnimatorFromXml(Context paramContext, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws IOException, XmlPullParserException
  {
    StateListAnimator localStateListAnimator = new StateListAnimator();
    for (;;)
    {
      switch (paramXmlPullParser.next())
      {
      default: 
        break;
      case 1: 
      case 3: 
        return localStateListAnimator;
      case 2: 
        Animator localAnimator1 = null;
        if ("item".equals(paramXmlPullParser.getName()))
        {
          int n = paramXmlPullParser.getAttributeCount();
          int[] arrayOfInt = new int[n];
          int j = 0;
          int i = 0;
          while (j < n)
          {
            int k = paramAttributeSet.getAttributeNameResource(j);
            if (k == 16843213)
            {
              localAnimator1 = loadAnimator(paramContext, paramAttributeSet.getAttributeResourceValue(j, 0));
              j += 1;
            }
            else
            {
              int m = i + 1;
              if (paramAttributeSet.getAttributeBooleanValue(j, false)) {}
              for (;;)
              {
                arrayOfInt[i] = k;
                i = m;
                break;
                k = -k;
              }
            }
          }
          Animator localAnimator2 = localAnimator1;
          if (localAnimator1 == null) {
            localAnimator2 = createAnimatorFromXml(paramContext.getResources(), paramContext.getTheme(), paramXmlPullParser, 1.0F);
          }
          if (localAnimator2 == null) {
            throw new Resources.NotFoundException("animation state item must have a valid animation");
          }
          localStateListAnimator.addState(StateSet.trimStateSet(arrayOfInt, i), localAnimator2);
        }
        break;
      }
    }
  }
  
  private static void distributeKeyframes(Keyframe[] paramArrayOfKeyframe, float paramFloat, int paramInt1, int paramInt2)
  {
    paramFloat /= (paramInt2 - paramInt1 + 2);
    while (paramInt1 <= paramInt2)
    {
      paramArrayOfKeyframe[paramInt1].setFraction(paramArrayOfKeyframe[(paramInt1 - 1)].getFraction() + paramFloat);
      paramInt1 += 1;
    }
  }
  
  private static void dumpKeyframes(Object[] paramArrayOfObject, String paramString)
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0)) {
      return;
    }
    Log.d("AnimatorInflater", paramString);
    int j = paramArrayOfObject.length;
    int i = 0;
    if (i < j)
    {
      Keyframe localKeyframe = (Keyframe)paramArrayOfObject[i];
      StringBuilder localStringBuilder = new StringBuilder().append("Keyframe ").append(i).append(": fraction ");
      if (localKeyframe.getFraction() < 0.0F)
      {
        paramString = "null";
        label74:
        localStringBuilder = localStringBuilder.append(paramString).append(", ").append(", value : ");
        if (!localKeyframe.hasValue()) {
          break label142;
        }
      }
      label142:
      for (paramString = localKeyframe.getValue();; paramString = "null")
      {
        Log.d("AnimatorInflater", paramString);
        i += 1;
        break;
        paramString = Float.valueOf(localKeyframe.getFraction());
        break label74;
      }
    }
  }
  
  private static int getChangingConfigs(Resources paramResources, int paramInt)
  {
    synchronized (sTmpTypedValue)
    {
      paramResources.getValue(paramInt, sTmpTypedValue, true);
      paramInt = sTmpTypedValue.changingConfigurations;
      return paramInt;
    }
  }
  
  private static PropertyValuesHolder getPVH(TypedArray paramTypedArray, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    Object localObject1 = paramTypedArray.peekValue(paramInt2);
    int j;
    int m;
    label27:
    int k;
    label42:
    int n;
    label54:
    int i;
    label91:
    label98:
    Object localObject3;
    String str1;
    String str2;
    if (localObject1 != null)
    {
      j = 1;
      if (j == 0) {
        break label228;
      }
      m = ((TypedValue)localObject1).type;
      localObject1 = paramTypedArray.peekValue(paramInt3);
      if (localObject1 == null) {
        break label234;
      }
      k = 1;
      if (k == 0) {
        break label240;
      }
      n = ((TypedValue)localObject1).type;
      i = paramInt1;
      if (paramInt1 == 4)
      {
        if (((j == 0) || (!isColorType(m))) && ((k == 0) || (!isColorType(n)))) {
          break label246;
        }
        i = 3;
      }
      if (i != 0) {
        break label252;
      }
      paramInt1 = 1;
      localObject1 = null;
      localObject3 = null;
      if (i != 2) {
        break label355;
      }
      str1 = paramTypedArray.getString(paramInt2);
      str2 = paramTypedArray.getString(paramInt3);
      if (str1 != null) {
        break label257;
      }
      localObject1 = null;
      label132:
      if (str2 != null) {
        break label271;
      }
    }
    label228:
    label234:
    label240:
    label246:
    label252:
    label257:
    label271:
    for (Object localObject2 = null;; localObject2 = new PathParser.PathData(str2))
    {
      if (localObject1 == null)
      {
        paramTypedArray = (TypedArray)localObject3;
        if (localObject2 == null) {
          break label306;
        }
      }
      if (localObject1 == null) {
        break label324;
      }
      paramTypedArray = new PathDataEvaluator(null);
      if (localObject2 == null) {
        break label308;
      }
      if (PathParser.canMorph((PathParser.PathData)localObject1, (PathParser.PathData)localObject2)) {
        break label285;
      }
      throw new InflateException(" Can't morph from " + str1 + " to " + str2);
      j = 0;
      break;
      m = 0;
      break label27;
      k = 0;
      break label42;
      n = 0;
      break label54;
      i = 0;
      break label91;
      paramInt1 = 0;
      break label98;
      localObject1 = new PathParser.PathData(str1);
      break label132;
    }
    label285:
    paramTypedArray = PropertyValuesHolder.ofObject(paramString, paramTypedArray, new Object[] { localObject1, localObject2 });
    label306:
    label308:
    label324:
    do
    {
      return paramTypedArray;
      return PropertyValuesHolder.ofObject(paramString, paramTypedArray, new Object[] { localObject1 });
      paramTypedArray = (TypedArray)localObject3;
    } while (localObject2 == null);
    return PropertyValuesHolder.ofObject(paramString, new PathDataEvaluator(null), new Object[] { localObject2 });
    label355:
    localObject2 = null;
    if (i == 3) {
      localObject2 = ArgbEvaluator.getInstance();
    }
    float f1;
    label392:
    float f2;
    if (paramInt1 != 0) {
      if (j != 0) {
        if (m == 5)
        {
          f1 = paramTypedArray.getDimension(paramInt2, 0.0F);
          if (k == 0) {
            break label479;
          }
          if (n != 5) {
            break label468;
          }
          f2 = paramTypedArray.getDimension(paramInt3, 0.0F);
          label411:
          localObject1 = PropertyValuesHolder.ofFloat(paramString, new float[] { f1, f2 });
        }
      }
    }
    label468:
    label479:
    label559:
    label627:
    label655:
    label672:
    do
    {
      for (;;)
      {
        paramTypedArray = (TypedArray)localObject1;
        if (localObject1 == null) {
          break;
        }
        paramTypedArray = (TypedArray)localObject1;
        if (localObject2 == null) {
          break;
        }
        ((PropertyValuesHolder)localObject1).setEvaluator((TypeEvaluator)localObject2);
        return (PropertyValuesHolder)localObject1;
        f1 = paramTypedArray.getFloat(paramInt2, 0.0F);
        break label392;
        f2 = paramTypedArray.getFloat(paramInt3, 0.0F);
        break label411;
        localObject1 = PropertyValuesHolder.ofFloat(paramString, new float[] { f1 });
        continue;
        if (n == 5) {}
        for (f1 = paramTypedArray.getDimension(paramInt3, 0.0F);; f1 = paramTypedArray.getFloat(paramInt3, 0.0F))
        {
          localObject1 = PropertyValuesHolder.ofFloat(paramString, new float[] { f1 });
          break;
        }
        if (j == 0) {
          break label672;
        }
        if (m == 5)
        {
          paramInt1 = (int)paramTypedArray.getDimension(paramInt2, 0.0F);
          if (k == 0) {
            break label655;
          }
          if (n != 5) {
            break label627;
          }
          paramInt2 = (int)paramTypedArray.getDimension(paramInt3, 0.0F);
        }
        for (;;)
        {
          localObject1 = PropertyValuesHolder.ofInt(paramString, new int[] { paramInt1, paramInt2 });
          break;
          if (isColorType(m))
          {
            paramInt1 = paramTypedArray.getColor(paramInt2, 0);
            break label559;
          }
          paramInt1 = paramTypedArray.getInt(paramInt2, 0);
          break label559;
          if (isColorType(n)) {
            paramInt2 = paramTypedArray.getColor(paramInt3, 0);
          } else {
            paramInt2 = paramTypedArray.getInt(paramInt3, 0);
          }
        }
        localObject1 = PropertyValuesHolder.ofInt(paramString, new int[] { paramInt1 });
      }
    } while (k == 0);
    if (n == 5) {
      paramInt1 = (int)paramTypedArray.getDimension(paramInt3, 0.0F);
    }
    for (;;)
    {
      localObject1 = PropertyValuesHolder.ofInt(paramString, new int[] { paramInt1 });
      break;
      if (isColorType(n)) {
        paramInt1 = paramTypedArray.getColor(paramInt3, 0);
      } else {
        paramInt1 = paramTypedArray.getInt(paramInt3, 0);
      }
    }
  }
  
  private static int inferValueTypeFromValues(TypedArray paramTypedArray, int paramInt1, int paramInt2)
  {
    int j = 1;
    TypedValue localTypedValue = paramTypedArray.peekValue(paramInt1);
    int i;
    if (localTypedValue != null)
    {
      paramInt1 = 1;
      if (paramInt1 == 0) {
        break label80;
      }
      i = localTypedValue.type;
      label27:
      paramTypedArray = paramTypedArray.peekValue(paramInt2);
      if (paramTypedArray == null) {
        break label85;
      }
      paramInt2 = j;
      label40:
      if (paramInt2 == 0) {
        break label90;
      }
    }
    label80:
    label85:
    label90:
    for (j = paramTypedArray.type;; j = 0)
    {
      if (((paramInt1 == 0) || (!isColorType(i))) && ((paramInt2 == 0) || (!isColorType(j)))) {
        break label96;
      }
      return 3;
      paramInt1 = 0;
      break;
      i = 0;
      break label27;
      paramInt2 = 0;
      break label40;
    }
    label96:
    return 0;
  }
  
  private static int inferValueTypeOfKeyframe(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet)
  {
    int i = 0;
    if (paramTheme != null)
    {
      paramResources = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.Keyframe, 0, 0);
      paramTheme = paramResources.peekValue(0);
      if (paramTheme != null) {
        i = 1;
      }
      if ((i == 0) || (!isColorType(paramTheme.type))) {
        break label63;
      }
    }
    label63:
    for (i = 3;; i = 0)
    {
      paramResources.recycle();
      return i;
      paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.Keyframe);
      break;
    }
  }
  
  private static boolean isColorType(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 28)
    {
      bool1 = bool2;
      if (paramInt <= 31) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static Animator loadAnimator(Context paramContext, int paramInt)
    throws Resources.NotFoundException
  {
    return loadAnimator(paramContext.getResources(), paramContext.getTheme(), paramInt);
  }
  
  public static Animator loadAnimator(Resources paramResources, Resources.Theme paramTheme, int paramInt)
    throws Resources.NotFoundException
  {
    return loadAnimator(paramResources, paramTheme, paramInt, 1.0F);
  }
  
  /* Error */
  public static Animator loadAnimator(Resources paramResources, Resources.Theme paramTheme, int paramInt, float paramFloat)
    throws Resources.NotFoundException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 404	android/content/res/Resources:getAnimatorCache	()Landroid/content/res/ConfigurationBoundResourceCache;
    //   4: astore 9
    //   6: aload 9
    //   8: iload_2
    //   9: i2l
    //   10: aload_0
    //   11: aload_1
    //   12: invokevirtual 409	android/content/res/ConfigurationBoundResourceCache:getInstance	(JLandroid/content/res/Resources;Landroid/content/res/Resources$Theme;)Ljava/lang/Object;
    //   15: checkcast 116	android/animation/Animator
    //   18: astore 4
    //   20: aload 4
    //   22: ifnull +6 -> 28
    //   25: aload 4
    //   27: areturn
    //   28: aconst_null
    //   29: astore 4
    //   31: aconst_null
    //   32: astore 6
    //   34: aconst_null
    //   35: astore 5
    //   37: aload_0
    //   38: iload_2
    //   39: invokevirtual 413	android/content/res/Resources:getAnimation	(I)Landroid/content/res/XmlResourceParser;
    //   42: astore 7
    //   44: aload 7
    //   46: astore 5
    //   48: aload 7
    //   50: astore 4
    //   52: aload 7
    //   54: astore 6
    //   56: aload_0
    //   57: aload_1
    //   58: aload 7
    //   60: fload_3
    //   61: invokestatic 253	android/animation/AnimatorInflater:createAnimatorFromXml	(Landroid/content/res/Resources;Landroid/content/res/Resources$Theme;Lorg/xmlpull/v1/XmlPullParser;F)Landroid/animation/Animator;
    //   64: astore 8
    //   66: aload 8
    //   68: astore 4
    //   70: aload 8
    //   72: ifnull +101 -> 173
    //   75: aload 7
    //   77: astore 5
    //   79: aload 7
    //   81: astore 4
    //   83: aload 7
    //   85: astore 6
    //   87: aload 8
    //   89: aload_0
    //   90: iload_2
    //   91: invokestatic 415	android/animation/AnimatorInflater:getChangingConfigs	(Landroid/content/res/Resources;I)I
    //   94: invokevirtual 120	android/animation/Animator:appendChangingConfigurations	(I)V
    //   97: aload 7
    //   99: astore 5
    //   101: aload 7
    //   103: astore 4
    //   105: aload 7
    //   107: astore 6
    //   109: aload 8
    //   111: invokevirtual 419	android/animation/Animator:createConstantState	()Landroid/content/res/ConstantState;
    //   114: astore 10
    //   116: aload 8
    //   118: astore 4
    //   120: aload 10
    //   122: ifnull +51 -> 173
    //   125: aload 7
    //   127: astore 5
    //   129: aload 7
    //   131: astore 4
    //   133: aload 7
    //   135: astore 6
    //   137: aload 9
    //   139: iload_2
    //   140: i2l
    //   141: aload_1
    //   142: aload 10
    //   144: invokevirtual 423	android/content/res/ConfigurationBoundResourceCache:put	(JLandroid/content/res/Resources$Theme;Ljava/lang/Object;)V
    //   147: aload 7
    //   149: astore 5
    //   151: aload 7
    //   153: astore 4
    //   155: aload 7
    //   157: astore 6
    //   159: aload 10
    //   161: aload_0
    //   162: aload_1
    //   163: invokevirtual 429	android/content/res/ConstantState:newInstance	(Landroid/content/res/Resources;Landroid/content/res/Resources$Theme;)Ljava/lang/Object;
    //   166: checkcast 116	android/animation/Animator
    //   169: astore_0
    //   170: aload_0
    //   171: astore 4
    //   173: aload 7
    //   175: ifnull +10 -> 185
    //   178: aload 7
    //   180: invokeinterface 434 1 0
    //   185: aload 4
    //   187: areturn
    //   188: astore_0
    //   189: aload 5
    //   191: astore 4
    //   193: new 255	android/content/res/Resources$NotFoundException
    //   196: dup
    //   197: new 149	java/lang/StringBuilder
    //   200: dup
    //   201: invokespecial 150	java/lang/StringBuilder:<init>	()V
    //   204: ldc_w 436
    //   207: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: iload_2
    //   211: invokestatic 439	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   214: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   220: invokespecial 258	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   223: astore_1
    //   224: aload 5
    //   226: astore 4
    //   228: aload_1
    //   229: aload_0
    //   230: invokevirtual 443	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   233: pop
    //   234: aload 5
    //   236: astore 4
    //   238: aload_1
    //   239: athrow
    //   240: astore_0
    //   241: aload 4
    //   243: ifnull +10 -> 253
    //   246: aload 4
    //   248: invokeinterface 434 1 0
    //   253: aload_0
    //   254: athrow
    //   255: astore_0
    //   256: aload 6
    //   258: astore 4
    //   260: new 255	android/content/res/Resources$NotFoundException
    //   263: dup
    //   264: new 149	java/lang/StringBuilder
    //   267: dup
    //   268: invokespecial 150	java/lang/StringBuilder:<init>	()V
    //   271: ldc_w 436
    //   274: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   277: iload_2
    //   278: invokestatic 439	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   281: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   284: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   287: invokespecial 258	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   290: astore_1
    //   291: aload 6
    //   293: astore 4
    //   295: aload_1
    //   296: aload_0
    //   297: invokevirtual 443	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   300: pop
    //   301: aload 6
    //   303: astore 4
    //   305: aload_1
    //   306: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	307	0	paramResources	Resources
    //   0	307	1	paramTheme	Resources.Theme
    //   0	307	2	paramInt	int
    //   0	307	3	paramFloat	float
    //   18	286	4	localObject1	Object
    //   35	200	5	localObject2	Object
    //   32	270	6	localObject3	Object
    //   42	137	7	localXmlResourceParser	android.content.res.XmlResourceParser
    //   64	53	8	localAnimator	Animator
    //   4	134	9	localConfigurationBoundResourceCache	android.content.res.ConfigurationBoundResourceCache
    //   114	46	10	localConstantState	android.content.res.ConstantState
    // Exception table:
    //   from	to	target	type
    //   37	44	188	java/io/IOException
    //   56	66	188	java/io/IOException
    //   87	97	188	java/io/IOException
    //   109	116	188	java/io/IOException
    //   137	147	188	java/io/IOException
    //   159	170	188	java/io/IOException
    //   37	44	240	finally
    //   56	66	240	finally
    //   87	97	240	finally
    //   109	116	240	finally
    //   137	147	240	finally
    //   159	170	240	finally
    //   193	224	240	finally
    //   228	234	240	finally
    //   238	240	240	finally
    //   260	291	240	finally
    //   295	301	240	finally
    //   305	307	240	finally
    //   37	44	255	org/xmlpull/v1/XmlPullParserException
    //   56	66	255	org/xmlpull/v1/XmlPullParserException
    //   87	97	255	org/xmlpull/v1/XmlPullParserException
    //   109	116	255	org/xmlpull/v1/XmlPullParserException
    //   137	147	255	org/xmlpull/v1/XmlPullParserException
    //   159	170	255	org/xmlpull/v1/XmlPullParserException
  }
  
  private static ValueAnimator loadAnimator(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, ValueAnimator paramValueAnimator, float paramFloat)
    throws Resources.NotFoundException
  {
    AttributeSet localAttributeSet = null;
    TypedArray localTypedArray;
    if (paramTheme != null)
    {
      localTypedArray = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.Animator, 0, 0);
      if (paramValueAnimator != null) {
        if (paramTheme == null) {
          break label157;
        }
      }
    }
    label157:
    for (paramAttributeSet = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.PropertyAnimator, 0, 0);; paramAttributeSet = paramResources.obtainAttributes(paramAttributeSet, R.styleable.PropertyAnimator))
    {
      paramValueAnimator.appendChangingConfigurations(paramAttributeSet.getChangingConfigurations());
      localAttributeSet = paramAttributeSet;
      paramAttributeSet = paramValueAnimator;
      if (paramValueAnimator == null) {
        paramAttributeSet = new ValueAnimator();
      }
      paramAttributeSet.appendChangingConfigurations(localTypedArray.getChangingConfigurations());
      parseAnimatorFromTypeArray(paramAttributeSet, localTypedArray, localAttributeSet, paramFloat);
      int i = localTypedArray.getResourceId(0, 0);
      if (i > 0)
      {
        paramResources = AnimationUtils.loadInterpolator(paramResources, paramTheme, i);
        if ((paramResources instanceof BaseInterpolator)) {
          paramAttributeSet.appendChangingConfigurations(((BaseInterpolator)paramResources).getChangingConfiguration());
        }
        paramAttributeSet.setInterpolator(paramResources);
      }
      localTypedArray.recycle();
      if (localAttributeSet != null) {
        localAttributeSet.recycle();
      }
      return paramAttributeSet;
      localTypedArray = paramResources.obtainAttributes(paramAttributeSet, R.styleable.Animator);
      break;
    }
  }
  
  private static Keyframe loadKeyframe(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, int paramInt)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray;
    float f;
    int j;
    label44:
    int i;
    if (paramTheme != null)
    {
      localTypedArray = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.Keyframe, 0, 0);
      Object localObject = null;
      f = localTypedArray.getFloat(3, -1.0F);
      paramAttributeSet = localTypedArray.peekValue(0);
      if (paramAttributeSet == null) {
        break label157;
      }
      j = 1;
      i = paramInt;
      if (paramInt == 4)
      {
        if ((j == 0) || (!isColorType(paramAttributeSet.type))) {
          break label163;
        }
        i = 3;
      }
      label70:
      if (j == 0) {
        break label201;
      }
      paramAttributeSet = (AttributeSet)localObject;
      switch (i)
      {
      default: 
        paramAttributeSet = (AttributeSet)localObject;
      }
    }
    for (;;)
    {
      paramInt = localTypedArray.getResourceId(1, 0);
      if (paramInt > 0) {
        paramAttributeSet.setInterpolator(AnimationUtils.loadInterpolator(paramResources, paramTheme, paramInt));
      }
      localTypedArray.recycle();
      return paramAttributeSet;
      localTypedArray = paramResources.obtainAttributes(paramAttributeSet, R.styleable.Keyframe);
      break;
      label157:
      j = 0;
      break label44;
      label163:
      i = 0;
      break label70;
      paramAttributeSet = Keyframe.ofFloat(f, localTypedArray.getFloat(0, 0.0F));
      continue;
      paramAttributeSet = Keyframe.ofInt(f, localTypedArray.getInt(0, 0));
      continue;
      label201:
      if (i == 0) {
        paramAttributeSet = Keyframe.ofFloat(f);
      } else {
        paramAttributeSet = Keyframe.ofInt(f);
      }
    }
  }
  
  private static ObjectAnimator loadObjectAnimator(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, float paramFloat)
    throws Resources.NotFoundException
  {
    ObjectAnimator localObjectAnimator = new ObjectAnimator();
    loadAnimator(paramResources, paramTheme, paramAttributeSet, localObjectAnimator, paramFloat);
    return localObjectAnimator;
  }
  
  private static PropertyValuesHolder loadPvh(Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, String paramString, int paramInt)
    throws XmlPullParserException, IOException
  {
    Object localObject3 = null;
    Object localObject1 = null;
    int j = paramInt;
    for (;;)
    {
      paramInt = paramXmlPullParser.next();
      if ((paramInt == 3) || (paramInt == 1)) {
        break;
      }
      if (paramXmlPullParser.getName().equals("keyframe"))
      {
        paramInt = j;
        if (j == 4) {
          paramInt = inferValueTypeOfKeyframe(paramResources, paramTheme, Xml.asAttributeSet(paramXmlPullParser));
        }
        Keyframe localKeyframe = loadKeyframe(paramResources, paramTheme, Xml.asAttributeSet(paramXmlPullParser), paramInt);
        Object localObject2 = localObject1;
        if (localKeyframe != null)
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((ArrayList)localObject2).add(localKeyframe);
        }
        paramXmlPullParser.next();
        localObject1 = localObject2;
        j = paramInt;
      }
    }
    paramResources = (Resources)localObject3;
    if (localObject1 != null)
    {
      int i = ((ArrayList)localObject1).size();
      paramResources = (Resources)localObject3;
      if (i > 0)
      {
        paramResources = (Keyframe)((ArrayList)localObject1).get(0);
        paramTheme = (Keyframe)((ArrayList)localObject1).get(i - 1);
        float f = paramTheme.getFraction();
        paramInt = i;
        int k;
        if (f < 1.0F)
        {
          if (f < 0.0F)
          {
            paramTheme.setFraction(1.0F);
            paramInt = i;
          }
        }
        else
        {
          f = paramResources.getFraction();
          k = paramInt;
          if (f != 0.0F)
          {
            if (f >= 0.0F) {
              break label324;
            }
            paramResources.setFraction(0.0F);
            k = paramInt;
          }
          label244:
          paramResources = new Keyframe[k];
          ((ArrayList)localObject1).toArray(paramResources);
          paramInt = 0;
          label260:
          if (paramInt >= k) {
            break label435;
          }
          paramTheme = paramResources[paramInt];
          if (paramTheme.getFraction() < 0.0F)
          {
            if (paramInt != 0) {
              break label344;
            }
            paramTheme.setFraction(0.0F);
          }
        }
        for (;;)
        {
          paramInt += 1;
          break label260;
          ((ArrayList)localObject1).add(((ArrayList)localObject1).size(), createNewKeyframe(paramTheme, 1.0F));
          paramInt = i + 1;
          break;
          label324:
          ((ArrayList)localObject1).add(0, createNewKeyframe(paramResources, 0.0F));
          k = paramInt + 1;
          break label244;
          label344:
          if (paramInt != k - 1) {
            break label361;
          }
          paramTheme.setFraction(1.0F);
        }
        label361:
        int m = paramInt;
        i = paramInt + 1;
        for (;;)
        {
          if ((i >= k - 1) || (paramResources[i].getFraction() >= 0.0F))
          {
            distributeKeyframes(paramResources, paramResources[(m + 1)].getFraction() - paramResources[(paramInt - 1)].getFraction(), paramInt, m);
            break;
          }
          m = i;
          i += 1;
        }
        label435:
        paramTheme = PropertyValuesHolder.ofKeyframe(paramString, paramResources);
        paramResources = paramTheme;
        if (j == 3)
        {
          paramTheme.setEvaluator(ArgbEvaluator.getInstance());
          paramResources = paramTheme;
        }
      }
    }
    return paramResources;
  }
  
  /* Error */
  public static StateListAnimator loadStateListAnimator(Context paramContext, int paramInt)
    throws Resources.NotFoundException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 247	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   4: astore 7
    //   6: aload 7
    //   8: invokevirtual 518	android/content/res/Resources:getStateListAnimatorCache	()Landroid/content/res/ConfigurationBoundResourceCache;
    //   11: astore 8
    //   13: aload_0
    //   14: invokevirtual 251	android/content/Context:getTheme	()Landroid/content/res/Resources$Theme;
    //   17: astore 9
    //   19: aload 8
    //   21: iload_1
    //   22: i2l
    //   23: aload 7
    //   25: aload 9
    //   27: invokevirtual 409	android/content/res/ConfigurationBoundResourceCache:getInstance	(JLandroid/content/res/Resources;Landroid/content/res/Resources$Theme;)Ljava/lang/Object;
    //   30: checkcast 218	android/animation/StateListAnimator
    //   33: astore_2
    //   34: aload_2
    //   35: ifnull +5 -> 40
    //   38: aload_2
    //   39: areturn
    //   40: aconst_null
    //   41: astore_2
    //   42: aconst_null
    //   43: astore 4
    //   45: aconst_null
    //   46: astore_3
    //   47: aload 7
    //   49: iload_1
    //   50: invokevirtual 413	android/content/res/Resources:getAnimation	(I)Landroid/content/res/XmlResourceParser;
    //   53: astore 5
    //   55: aload 5
    //   57: astore_3
    //   58: aload 5
    //   60: astore_2
    //   61: aload 5
    //   63: astore 4
    //   65: aload_0
    //   66: aload 5
    //   68: aload 5
    //   70: invokestatic 51	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   73: invokestatic 520	android/animation/AnimatorInflater:createStateListAnimatorFromXml	(Landroid/content/Context;Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;)Landroid/animation/StateListAnimator;
    //   76: astore 6
    //   78: aload 6
    //   80: astore_0
    //   81: aload 6
    //   83: ifnull +93 -> 176
    //   86: aload 5
    //   88: astore_3
    //   89: aload 5
    //   91: astore_2
    //   92: aload 5
    //   94: astore 4
    //   96: aload 6
    //   98: aload 7
    //   100: iload_1
    //   101: invokestatic 415	android/animation/AnimatorInflater:getChangingConfigs	(Landroid/content/res/Resources;I)I
    //   104: invokevirtual 521	android/animation/StateListAnimator:appendChangingConfigurations	(I)V
    //   107: aload 5
    //   109: astore_3
    //   110: aload 5
    //   112: astore_2
    //   113: aload 5
    //   115: astore 4
    //   117: aload 6
    //   119: invokevirtual 522	android/animation/StateListAnimator:createConstantState	()Landroid/content/res/ConstantState;
    //   122: astore 10
    //   124: aload 6
    //   126: astore_0
    //   127: aload 10
    //   129: ifnull +47 -> 176
    //   132: aload 5
    //   134: astore_3
    //   135: aload 5
    //   137: astore_2
    //   138: aload 5
    //   140: astore 4
    //   142: aload 8
    //   144: iload_1
    //   145: i2l
    //   146: aload 9
    //   148: aload 10
    //   150: invokevirtual 423	android/content/res/ConfigurationBoundResourceCache:put	(JLandroid/content/res/Resources$Theme;Ljava/lang/Object;)V
    //   153: aload 5
    //   155: astore_3
    //   156: aload 5
    //   158: astore_2
    //   159: aload 5
    //   161: astore 4
    //   163: aload 10
    //   165: aload 7
    //   167: aload 9
    //   169: invokevirtual 429	android/content/res/ConstantState:newInstance	(Landroid/content/res/Resources;Landroid/content/res/Resources$Theme;)Ljava/lang/Object;
    //   172: checkcast 218	android/animation/StateListAnimator
    //   175: astore_0
    //   176: aload 5
    //   178: ifnull +10 -> 188
    //   181: aload 5
    //   183: invokeinterface 434 1 0
    //   188: aload_0
    //   189: areturn
    //   190: astore_0
    //   191: aload_3
    //   192: astore_2
    //   193: new 255	android/content/res/Resources$NotFoundException
    //   196: dup
    //   197: new 149	java/lang/StringBuilder
    //   200: dup
    //   201: invokespecial 150	java/lang/StringBuilder:<init>	()V
    //   204: ldc_w 524
    //   207: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: iload_1
    //   211: invokestatic 439	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   214: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   220: invokespecial 258	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   223: astore 4
    //   225: aload_3
    //   226: astore_2
    //   227: aload 4
    //   229: aload_0
    //   230: invokevirtual 443	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   233: pop
    //   234: aload_3
    //   235: astore_2
    //   236: aload 4
    //   238: athrow
    //   239: astore_0
    //   240: aload_2
    //   241: ifnull +9 -> 250
    //   244: aload_2
    //   245: invokeinterface 434 1 0
    //   250: aload_0
    //   251: athrow
    //   252: astore_0
    //   253: aload 4
    //   255: astore_2
    //   256: new 255	android/content/res/Resources$NotFoundException
    //   259: dup
    //   260: new 149	java/lang/StringBuilder
    //   263: dup
    //   264: invokespecial 150	java/lang/StringBuilder:<init>	()V
    //   267: ldc_w 524
    //   270: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   273: iload_1
    //   274: invokestatic 439	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   277: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   280: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   283: invokespecial 258	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   286: astore_3
    //   287: aload 4
    //   289: astore_2
    //   290: aload_3
    //   291: aload_0
    //   292: invokevirtual 443	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   295: pop
    //   296: aload 4
    //   298: astore_2
    //   299: aload_3
    //   300: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	301	0	paramContext	Context
    //   0	301	1	paramInt	int
    //   33	266	2	localObject1	Object
    //   46	254	3	localObject2	Object
    //   43	254	4	localObject3	Object
    //   53	129	5	localXmlResourceParser	android.content.res.XmlResourceParser
    //   76	49	6	localStateListAnimator	StateListAnimator
    //   4	162	7	localResources	Resources
    //   11	132	8	localConfigurationBoundResourceCache	android.content.res.ConfigurationBoundResourceCache
    //   17	151	9	localTheme	Resources.Theme
    //   122	42	10	localConstantState	android.content.res.ConstantState
    // Exception table:
    //   from	to	target	type
    //   47	55	190	java/io/IOException
    //   65	78	190	java/io/IOException
    //   96	107	190	java/io/IOException
    //   117	124	190	java/io/IOException
    //   142	153	190	java/io/IOException
    //   163	176	190	java/io/IOException
    //   47	55	239	finally
    //   65	78	239	finally
    //   96	107	239	finally
    //   117	124	239	finally
    //   142	153	239	finally
    //   163	176	239	finally
    //   193	225	239	finally
    //   227	234	239	finally
    //   236	239	239	finally
    //   256	287	239	finally
    //   290	296	239	finally
    //   299	301	239	finally
    //   47	55	252	org/xmlpull/v1/XmlPullParserException
    //   65	78	252	org/xmlpull/v1/XmlPullParserException
    //   96	107	252	org/xmlpull/v1/XmlPullParserException
    //   117	124	252	org/xmlpull/v1/XmlPullParserException
    //   142	153	252	org/xmlpull/v1/XmlPullParserException
    //   163	176	252	org/xmlpull/v1/XmlPullParserException
  }
  
  private static PropertyValuesHolder[] loadValues(Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    Object localObject1 = null;
    int i;
    for (;;)
    {
      i = paramXmlPullParser.getEventType();
      if ((i == 3) || (i == 1)) {
        break label191;
      }
      if (i == 2) {
        break;
      }
      paramXmlPullParser.next();
    }
    Object localObject2 = localObject1;
    if (paramXmlPullParser.getName().equals("propertyValuesHolder")) {
      if (paramTheme == null) {
        break label178;
      }
    }
    label178:
    for (TypedArray localTypedArray = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.PropertyValuesHolder, 0, 0);; localTypedArray = paramResources.obtainAttributes(paramAttributeSet, R.styleable.PropertyValuesHolder))
    {
      String str = localTypedArray.getString(3);
      i = localTypedArray.getInt(2, 4);
      localObject2 = loadPvh(paramResources, paramTheme, paramXmlPullParser, str, i);
      Object localObject3 = localObject2;
      if (localObject2 == null) {
        localObject3 = getPVH(localTypedArray, i, 0, 1, str);
      }
      localObject2 = localObject1;
      if (localObject3 != null)
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((ArrayList)localObject2).add(localObject3);
      }
      localTypedArray.recycle();
      paramXmlPullParser.next();
      localObject1 = localObject2;
      break;
    }
    label191:
    paramResources = null;
    if (localObject1 != null)
    {
      int j = ((ArrayList)localObject1).size();
      paramTheme = new PropertyValuesHolder[j];
      i = 0;
      for (;;)
      {
        paramResources = paramTheme;
        if (i >= j) {
          break;
        }
        paramTheme[i] = ((PropertyValuesHolder)((ArrayList)localObject1).get(i));
        i += 1;
      }
    }
    return paramResources;
  }
  
  private static void parseAnimatorFromTypeArray(ValueAnimator paramValueAnimator, TypedArray paramTypedArray1, TypedArray paramTypedArray2, float paramFloat)
  {
    long l1 = paramTypedArray1.getInt(1, 300);
    long l2 = paramTypedArray1.getInt(2, 0);
    int j = paramTypedArray1.getInt(7, 4);
    int i = j;
    if (j == 4) {
      i = inferValueTypeFromValues(paramTypedArray1, 5, 6);
    }
    PropertyValuesHolder localPropertyValuesHolder = getPVH(paramTypedArray1, i, 5, 6, "");
    if (localPropertyValuesHolder != null) {
      paramValueAnimator.setValues(new PropertyValuesHolder[] { localPropertyValuesHolder });
    }
    paramValueAnimator.setDuration(l1);
    paramValueAnimator.setStartDelay(l2);
    if (paramTypedArray1.hasValue(3)) {
      paramValueAnimator.setRepeatCount(paramTypedArray1.getInt(3, 0));
    }
    if (paramTypedArray1.hasValue(4)) {
      paramValueAnimator.setRepeatMode(paramTypedArray1.getInt(4, 1));
    }
    if (paramTypedArray2 != null) {
      if (i != 0) {
        break label150;
      }
    }
    label150:
    for (boolean bool = true;; bool = false)
    {
      setupObjectAnimator(paramValueAnimator, paramTypedArray2, bool, paramFloat);
      return;
    }
  }
  
  private static TypeEvaluator setupAnimatorForPath(ValueAnimator paramValueAnimator, TypedArray paramTypedArray)
  {
    Object localObject = null;
    String str1 = paramTypedArray.getString(5);
    String str2 = paramTypedArray.getString(6);
    PathParser.PathData localPathData1;
    PathParser.PathData localPathData2;
    if (str1 == null)
    {
      localPathData1 = null;
      if (str2 != null) {
        break label124;
      }
      localPathData2 = null;
    }
    for (;;)
    {
      if (localPathData1 != null)
      {
        if (localPathData2 != null)
        {
          paramValueAnimator.setObjectValues(new Object[] { localPathData1, localPathData2 });
          if (PathParser.canMorph(localPathData1, localPathData2)) {
            break label149;
          }
          throw new InflateException(paramTypedArray.getPositionDescription() + " Can't morph from " + str1 + " to " + str2);
          localPathData1 = new PathParser.PathData(str1);
          break;
          label124:
          localPathData2 = new PathParser.PathData(str2);
          continue;
        }
        paramValueAnimator.setObjectValues(new Object[] { localPathData1 });
        label149:
        paramTypedArray = new PathDataEvaluator(null);
      }
    }
    do
    {
      return paramTypedArray;
      paramTypedArray = (TypedArray)localObject;
    } while (localPathData2 == null);
    paramValueAnimator.setObjectValues(new Object[] { localPathData2 });
    return new PathDataEvaluator(null);
  }
  
  private static void setupObjectAnimator(ValueAnimator paramValueAnimator, TypedArray paramTypedArray, boolean paramBoolean, float paramFloat)
  {
    ObjectAnimator localObjectAnimator = (ObjectAnimator)paramValueAnimator;
    paramValueAnimator = paramTypedArray.getString(1);
    if (paramValueAnimator != null)
    {
      String str2 = paramTypedArray.getString(2);
      String str1 = paramTypedArray.getString(3);
      if ((str2 == null) && (str1 == null)) {
        throw new InflateException(paramTypedArray.getPositionDescription() + " propertyXName or propertyYName is needed for PathData");
      }
      paramValueAnimator = KeyframeSet.ofPath(PathParser.createPathFromPathData(paramValueAnimator), 0.5F * paramFloat);
      if (paramBoolean) {
        paramTypedArray = paramValueAnimator.createXFloatKeyframes();
      }
      PropertyValuesHolder localPropertyValuesHolder;
      for (paramValueAnimator = paramValueAnimator.createYFloatKeyframes();; paramValueAnimator = paramValueAnimator.createYIntKeyframes())
      {
        localPropertyValuesHolder = null;
        Object localObject = null;
        if (str2 != null) {
          localPropertyValuesHolder = PropertyValuesHolder.ofKeyframes(str2, paramTypedArray);
        }
        paramTypedArray = (TypedArray)localObject;
        if (str1 != null) {
          paramTypedArray = PropertyValuesHolder.ofKeyframes(str1, paramValueAnimator);
        }
        if (localPropertyValuesHolder != null) {
          break;
        }
        localObjectAnimator.setValues(new PropertyValuesHolder[] { paramTypedArray });
        return;
        paramTypedArray = paramValueAnimator.createXIntKeyframes();
      }
      if (paramTypedArray == null)
      {
        localObjectAnimator.setValues(new PropertyValuesHolder[] { localPropertyValuesHolder });
        return;
      }
      localObjectAnimator.setValues(new PropertyValuesHolder[] { localPropertyValuesHolder, paramTypedArray });
      return;
    }
    localObjectAnimator.setPropertyName(paramTypedArray.getString(0));
  }
  
  private static void setupValues(ValueAnimator paramValueAnimator, TypedArray paramTypedArray, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, boolean paramBoolean3, int paramInt2)
  {
    float f1;
    float f2;
    if (paramBoolean1) {
      if (paramBoolean2) {
        if (paramInt1 == 5)
        {
          f1 = paramTypedArray.getDimension(5, 0.0F);
          if (!paramBoolean3) {
            break label83;
          }
          if (paramInt2 != 5) {
            break label71;
          }
          f2 = paramTypedArray.getDimension(6, 0.0F);
          label42:
          paramValueAnimator.setFloatValues(new float[] { f1, f2 });
        }
      }
    }
    label71:
    label83:
    label224:
    label256:
    do
    {
      return;
      f1 = paramTypedArray.getFloat(5, 0.0F);
      break;
      f2 = paramTypedArray.getFloat(6, 0.0F);
      break label42;
      paramValueAnimator.setFloatValues(new float[] { f1 });
      return;
      if (paramInt2 == 5) {}
      for (f1 = paramTypedArray.getDimension(6, 0.0F);; f1 = paramTypedArray.getFloat(6, 0.0F))
      {
        paramValueAnimator.setFloatValues(new float[] { f1 });
        return;
      }
      if (paramBoolean2)
      {
        if (paramInt1 == 5)
        {
          paramInt1 = (int)paramTypedArray.getDimension(5, 0.0F);
          if (!paramBoolean3) {
            break label256;
          }
          if (paramInt2 != 5) {
            break label224;
          }
          paramInt2 = (int)paramTypedArray.getDimension(6, 0.0F);
        }
        for (;;)
        {
          paramValueAnimator.setIntValues(new int[] { paramInt1, paramInt2 });
          return;
          if (isColorType(paramInt1))
          {
            paramInt1 = paramTypedArray.getColor(5, 0);
            break;
          }
          paramInt1 = paramTypedArray.getInt(5, 0);
          break;
          if (isColorType(paramInt2)) {
            paramInt2 = paramTypedArray.getColor(6, 0);
          } else {
            paramInt2 = paramTypedArray.getInt(6, 0);
          }
        }
        paramValueAnimator.setIntValues(new int[] { paramInt1 });
        return;
      }
    } while (!paramBoolean3);
    if (paramInt2 == 5) {
      paramInt1 = (int)paramTypedArray.getDimension(6, 0.0F);
    }
    for (;;)
    {
      paramValueAnimator.setIntValues(new int[] { paramInt1 });
      return;
      if (isColorType(paramInt2)) {
        paramInt1 = paramTypedArray.getColor(6, 0);
      } else {
        paramInt1 = paramTypedArray.getInt(6, 0);
      }
    }
  }
  
  private static class PathDataEvaluator
    implements TypeEvaluator<PathParser.PathData>
  {
    private final PathParser.PathData mPathData = new PathParser.PathData();
    
    public PathParser.PathData evaluate(float paramFloat, PathParser.PathData paramPathData1, PathParser.PathData paramPathData2)
    {
      if (!PathParser.interpolatePathData(this.mPathData, paramPathData1, paramPathData2, paramFloat)) {
        throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
      }
      return this.mPathData;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/AnimatorInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */