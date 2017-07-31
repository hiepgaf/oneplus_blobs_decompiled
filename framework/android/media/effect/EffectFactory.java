package android.media.effect;

public class EffectFactory
{
  public static final String EFFECT_AUTOFIX = "android.media.effect.effects.AutoFixEffect";
  public static final String EFFECT_BACKDROPPER = "android.media.effect.effects.BackDropperEffect";
  public static final String EFFECT_BITMAPOVERLAY = "android.media.effect.effects.BitmapOverlayEffect";
  public static final String EFFECT_BLACKWHITE = "android.media.effect.effects.BlackWhiteEffect";
  public static final String EFFECT_BRIGHTNESS = "android.media.effect.effects.BrightnessEffect";
  public static final String EFFECT_CONTRAST = "android.media.effect.effects.ContrastEffect";
  public static final String EFFECT_CROP = "android.media.effect.effects.CropEffect";
  public static final String EFFECT_CROSSPROCESS = "android.media.effect.effects.CrossProcessEffect";
  public static final String EFFECT_DOCUMENTARY = "android.media.effect.effects.DocumentaryEffect";
  public static final String EFFECT_DUOTONE = "android.media.effect.effects.DuotoneEffect";
  public static final String EFFECT_FILLLIGHT = "android.media.effect.effects.FillLightEffect";
  public static final String EFFECT_FISHEYE = "android.media.effect.effects.FisheyeEffect";
  public static final String EFFECT_FLIP = "android.media.effect.effects.FlipEffect";
  public static final String EFFECT_GRAIN = "android.media.effect.effects.GrainEffect";
  public static final String EFFECT_GRAYSCALE = "android.media.effect.effects.GrayscaleEffect";
  public static final String EFFECT_IDENTITY = "IdentityEffect";
  public static final String EFFECT_LOMOISH = "android.media.effect.effects.LomoishEffect";
  public static final String EFFECT_NEGATIVE = "android.media.effect.effects.NegativeEffect";
  private static final String[] EFFECT_PACKAGES = { "android.media.effect.effects.", "" };
  public static final String EFFECT_POSTERIZE = "android.media.effect.effects.PosterizeEffect";
  public static final String EFFECT_REDEYE = "android.media.effect.effects.RedEyeEffect";
  public static final String EFFECT_ROTATE = "android.media.effect.effects.RotateEffect";
  public static final String EFFECT_SATURATE = "android.media.effect.effects.SaturateEffect";
  public static final String EFFECT_SEPIA = "android.media.effect.effects.SepiaEffect";
  public static final String EFFECT_SHARPEN = "android.media.effect.effects.SharpenEffect";
  public static final String EFFECT_STRAIGHTEN = "android.media.effect.effects.StraightenEffect";
  public static final String EFFECT_TEMPERATURE = "android.media.effect.effects.ColorTemperatureEffect";
  public static final String EFFECT_TINT = "android.media.effect.effects.TintEffect";
  public static final String EFFECT_VIGNETTE = "android.media.effect.effects.VignetteEffect";
  private EffectContext mEffectContext;
  
  EffectFactory(EffectContext paramEffectContext)
  {
    this.mEffectContext = paramEffectContext;
  }
  
  private static Class getEffectClassByName(String paramString)
  {
    Object localObject1 = null;
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    String[] arrayOfString = EFFECT_PACKAGES;
    int i = 0;
    int j = arrayOfString.length;
    for (;;)
    {
      Object localObject2 = localObject1;
      if (i < j) {
        localObject2 = arrayOfString[i];
      }
      try
      {
        localObject2 = localClassLoader.loadClass((String)localObject2 + paramString);
        localObject1 = localObject2;
        localObject2 = localObject1;
        if (localObject1 != null)
        {
          localObject2 = localObject1;
          return (Class)localObject2;
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Object localObject3 = localObject1;
        i += 1;
        localObject1 = localObject3;
      }
    }
  }
  
  /* Error */
  private Effect instantiateEffect(Class paramClass, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc -106
    //   3: invokevirtual 156	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   6: pop
    //   7: aload_1
    //   8: iconst_2
    //   9: anewarray 152	java/lang/Class
    //   12: dup
    //   13: iconst_0
    //   14: ldc -98
    //   16: aastore
    //   17: dup
    //   18: iconst_1
    //   19: ldc 97
    //   21: aastore
    //   22: invokevirtual 162	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   25: astore_3
    //   26: aload_3
    //   27: iconst_2
    //   28: anewarray 4	java/lang/Object
    //   31: dup
    //   32: iconst_0
    //   33: aload_0
    //   34: getfield 110	android/media/effect/EffectFactory:mEffectContext	Landroid/media/effect/EffectContext;
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: aload_2
    //   41: aastore
    //   42: invokevirtual 168	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   45: checkcast 150	android/media/effect/Effect
    //   48: astore_2
    //   49: aload_2
    //   50: areturn
    //   51: astore_2
    //   52: new 170	java/lang/IllegalArgumentException
    //   55: dup
    //   56: new 126	java/lang/StringBuilder
    //   59: dup
    //   60: invokespecial 127	java/lang/StringBuilder:<init>	()V
    //   63: ldc -84
    //   65: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   68: aload_1
    //   69: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   72: ldc -79
    //   74: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   80: aload_2
    //   81: invokespecial 180	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   84: athrow
    //   85: astore_2
    //   86: new 182	java/lang/RuntimeException
    //   89: dup
    //   90: new 126	java/lang/StringBuilder
    //   93: dup
    //   94: invokespecial 127	java/lang/StringBuilder:<init>	()V
    //   97: ldc -72
    //   99: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: aload_1
    //   103: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   106: ldc -70
    //   108: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: ldc -68
    //   113: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   116: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   119: aload_2
    //   120: invokespecial 189	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   123: athrow
    //   124: astore_2
    //   125: new 182	java/lang/RuntimeException
    //   128: dup
    //   129: new 126	java/lang/StringBuilder
    //   132: dup
    //   133: invokespecial 127	java/lang/StringBuilder:<init>	()V
    //   136: ldc -65
    //   138: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: aload_1
    //   142: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   145: ldc -63
    //   147: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   153: aload_2
    //   154: invokespecial 189	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   157: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	158	0	this	EffectFactory
    //   0	158	1	paramClass	Class
    //   0	158	2	paramString	String
    //   25	2	3	localConstructor	java.lang.reflect.Constructor
    // Exception table:
    //   from	to	target	type
    //   0	7	51	java/lang/ClassCastException
    //   7	26	85	java/lang/NoSuchMethodException
    //   26	49	124	java/lang/Throwable
  }
  
  public static boolean isEffectSupported(String paramString)
  {
    return getEffectClassByName(paramString) != null;
  }
  
  public Effect createEffect(String paramString)
  {
    Class localClass = getEffectClassByName(paramString);
    if (localClass == null) {
      throw new IllegalArgumentException("Cannot instantiate unknown effect '" + paramString + "'!");
    }
    return instantiateEffect(localClass, paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/EffectFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */