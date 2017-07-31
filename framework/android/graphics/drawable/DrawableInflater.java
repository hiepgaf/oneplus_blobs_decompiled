package android.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.AttributeSet;
import android.view.InflateException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class DrawableInflater
{
  private static final HashMap<String, Constructor<? extends Drawable>> CONSTRUCTOR_MAP = new HashMap();
  private final ClassLoader mClassLoader;
  private final Resources mRes;
  
  public DrawableInflater(Resources paramResources, ClassLoader paramClassLoader)
  {
    this.mRes = paramResources;
    this.mClassLoader = paramClassLoader;
  }
  
  /* Error */
  private Drawable inflateFromClass(String paramString)
  {
    // Byte code:
    //   0: getstatic 20	android/graphics/drawable/DrawableInflater:CONSTRUCTOR_MAP	Ljava/util/HashMap;
    //   3: astore 4
    //   5: aload 4
    //   7: monitorenter
    //   8: getstatic 20	android/graphics/drawable/DrawableInflater:CONSTRUCTOR_MAP	Ljava/util/HashMap;
    //   11: aload_1
    //   12: invokevirtual 41	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   15: checkcast 43	java/lang/reflect/Constructor
    //   18: astore_3
    //   19: aload_3
    //   20: astore_2
    //   21: aload_3
    //   22: ifnonnull +33 -> 55
    //   25: aload_0
    //   26: getfield 27	android/graphics/drawable/DrawableInflater:mClassLoader	Ljava/lang/ClassLoader;
    //   29: aload_1
    //   30: invokevirtual 49	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   33: ldc 51
    //   35: invokevirtual 57	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   38: iconst_0
    //   39: anewarray 53	java/lang/Class
    //   42: invokevirtual 61	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   45: astore_2
    //   46: getstatic 20	android/graphics/drawable/DrawableInflater:CONSTRUCTOR_MAP	Ljava/util/HashMap;
    //   49: aload_1
    //   50: aload_2
    //   51: invokevirtual 65	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   54: pop
    //   55: aload 4
    //   57: monitorexit
    //   58: aload_2
    //   59: iconst_0
    //   60: anewarray 4	java/lang/Object
    //   63: invokevirtual 69	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   66: checkcast 51	android/graphics/drawable/Drawable
    //   69: areturn
    //   70: astore_2
    //   71: aload 4
    //   73: monitorexit
    //   74: aload_2
    //   75: athrow
    //   76: astore_2
    //   77: new 71	android/view/InflateException
    //   80: dup
    //   81: new 73	java/lang/StringBuilder
    //   84: dup
    //   85: invokespecial 74	java/lang/StringBuilder:<init>	()V
    //   88: ldc 76
    //   90: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: aload_1
    //   94: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   97: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   100: invokespecial 87	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   103: astore_1
    //   104: aload_1
    //   105: aload_2
    //   106: invokevirtual 91	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   109: pop
    //   110: aload_1
    //   111: athrow
    //   112: astore_2
    //   113: new 71	android/view/InflateException
    //   116: dup
    //   117: new 73	java/lang/StringBuilder
    //   120: dup
    //   121: invokespecial 74	java/lang/StringBuilder:<init>	()V
    //   124: ldc 76
    //   126: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: aload_1
    //   130: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   133: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   136: invokespecial 87	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   139: astore_1
    //   140: aload_1
    //   141: aload_2
    //   142: invokevirtual 91	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   145: pop
    //   146: aload_1
    //   147: athrow
    //   148: astore_2
    //   149: new 71	android/view/InflateException
    //   152: dup
    //   153: new 73	java/lang/StringBuilder
    //   156: dup
    //   157: invokespecial 74	java/lang/StringBuilder:<init>	()V
    //   160: ldc 93
    //   162: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: aload_1
    //   166: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   172: invokespecial 87	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   175: astore_1
    //   176: aload_1
    //   177: aload_2
    //   178: invokevirtual 91	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   181: pop
    //   182: aload_1
    //   183: athrow
    //   184: astore_2
    //   185: new 71	android/view/InflateException
    //   188: dup
    //   189: new 73	java/lang/StringBuilder
    //   192: dup
    //   193: invokespecial 74	java/lang/StringBuilder:<init>	()V
    //   196: ldc 95
    //   198: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   201: aload_1
    //   202: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   208: invokespecial 87	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   211: astore_1
    //   212: aload_1
    //   213: aload_2
    //   214: invokevirtual 91	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   217: pop
    //   218: aload_1
    //   219: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	220	0	this	DrawableInflater
    //   0	220	1	paramString	String
    //   20	39	2	localConstructor1	Constructor
    //   70	5	2	localObject	Object
    //   76	30	2	localNoSuchMethodException	NoSuchMethodException
    //   112	30	2	localException	Exception
    //   148	30	2	localClassNotFoundException	ClassNotFoundException
    //   184	30	2	localClassCastException	ClassCastException
    //   18	4	3	localConstructor2	Constructor
    // Exception table:
    //   from	to	target	type
    //   8	19	70	finally
    //   25	55	70	finally
    //   0	8	76	java/lang/NoSuchMethodException
    //   55	70	76	java/lang/NoSuchMethodException
    //   71	76	76	java/lang/NoSuchMethodException
    //   0	8	112	java/lang/Exception
    //   55	70	112	java/lang/Exception
    //   71	76	112	java/lang/Exception
    //   0	8	148	java/lang/ClassNotFoundException
    //   55	70	148	java/lang/ClassNotFoundException
    //   71	76	148	java/lang/ClassNotFoundException
    //   0	8	184	java/lang/ClassCastException
    //   55	70	184	java/lang/ClassCastException
    //   71	76	184	java/lang/ClassCastException
  }
  
  private Drawable inflateFromTag(String paramString)
  {
    if (paramString.equals("selector")) {
      return new StateListDrawable();
    }
    if (paramString.equals("animated-selector")) {
      return new AnimatedStateListDrawable();
    }
    if (paramString.equals("level-list")) {
      return new LevelListDrawable();
    }
    if (paramString.equals("layer-list")) {
      return new LayerDrawable();
    }
    if (paramString.equals("transition")) {
      return new TransitionDrawable();
    }
    if (paramString.equals("ripple")) {
      return new RippleDrawable();
    }
    if (paramString.equals("color")) {
      return new ColorDrawable();
    }
    if (paramString.equals("shape")) {
      return new GradientDrawable();
    }
    if (paramString.equals("vector")) {
      return new VectorDrawable();
    }
    if (paramString.equals("animated-vector")) {
      return new AnimatedVectorDrawable();
    }
    if (paramString.equals("scale")) {
      return new ScaleDrawable();
    }
    if (paramString.equals("clip")) {
      return new ClipDrawable();
    }
    if (paramString.equals("rotate")) {
      return new RotateDrawable();
    }
    if (paramString.equals("animated-rotate")) {
      return new AnimatedRotateDrawable();
    }
    if (paramString.equals("animation-list")) {
      return new AnimationDrawable();
    }
    if (paramString.equals("inset")) {
      return new InsetDrawable();
    }
    if (paramString.equals("bitmap")) {
      return new BitmapDrawable();
    }
    if (paramString.equals("nine-patch")) {
      return new NinePatchDrawable();
    }
    return null;
  }
  
  public static Drawable loadDrawable(Context paramContext, int paramInt)
  {
    return loadDrawable(paramContext.getResources(), paramContext.getTheme(), paramInt);
  }
  
  public static Drawable loadDrawable(Resources paramResources, Resources.Theme paramTheme, int paramInt)
  {
    return paramResources.getDrawable(paramInt, paramTheme);
  }
  
  public Drawable inflateFromXml(String paramString, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    String str = paramString;
    if (paramString.equals("drawable"))
    {
      paramString = paramAttributeSet.getAttributeValue(null, "class");
      str = paramString;
      if (paramString == null) {
        throw new InflateException("<drawable> tag must specify class attribute");
      }
    }
    Drawable localDrawable = inflateFromTag(str);
    paramString = localDrawable;
    if (localDrawable == null) {
      paramString = inflateFromClass(str);
    }
    paramString.inflate(this.mRes, paramXmlPullParser, paramAttributeSet, paramTheme);
    return paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/DrawableInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */