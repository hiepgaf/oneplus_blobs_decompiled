package android.content.res;

import android.animation.Animator;
import android.animation.StateListAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.icu.text.PluralRules;
import android.os.LocaleList;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.DisplayAdjustments;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;

public class ResourcesImpl
{
  private static final boolean DEBUG_CONFIG = false;
  private static final boolean DEBUG_LOAD = false;
  private static final int ID_OTHER = 16777220;
  private static final int LAYOUT_DIR_CONFIG = ActivityInfo.activityInfoConfigJavaToNative(8192);
  static final String TAG = "Resources";
  private static final boolean TRACE_FOR_MISS_PRELOAD = false;
  private static final boolean TRACE_FOR_PRELOAD = false;
  private static final int XML_BLOCK_CACHE_SIZE = 4;
  private static boolean sPreloaded;
  private static final LongSparseArray<Drawable.ConstantState> sPreloadedColorDrawables;
  private static final LongSparseArray<ConstantState<ComplexColor>> sPreloadedComplexColors;
  private static final LongSparseArray<Drawable.ConstantState>[] sPreloadedDrawables;
  private static final Object sSync = new Object();
  private final Object mAccessLock = new Object();
  private final ConfigurationBoundResourceCache<Animator> mAnimatorCache = new ConfigurationBoundResourceCache();
  final AssetManager mAssets;
  private final int[] mCachedXmlBlockCookies = new int[4];
  private final String[] mCachedXmlBlockFiles = new String[4];
  private final XmlBlock[] mCachedXmlBlocks = new XmlBlock[4];
  private final DrawableCache mColorDrawableCache = new DrawableCache();
  private final ConfigurationBoundResourceCache<ComplexColor> mComplexColorCache = new ConfigurationBoundResourceCache();
  private final Configuration mConfiguration = new Configuration();
  private final DisplayAdjustments mDisplayAdjustments;
  private final DrawableCache mDrawableCache = new DrawableCache();
  private int mLastCachedXmlBlockIndex = -1;
  private final DisplayMetrics mMetrics = new DisplayMetrics();
  private PluralRules mPluralRule;
  private boolean mPreloading;
  private final ConfigurationBoundResourceCache<StateListAnimator> mStateListAnimatorCache = new ConfigurationBoundResourceCache();
  private final Configuration mTmpConfig = new Configuration();
  
  static
  {
    sPreloadedColorDrawables = new LongSparseArray();
    sPreloadedComplexColors = new LongSparseArray();
    sPreloadedDrawables = new LongSparseArray[2];
    sPreloadedDrawables[0] = new LongSparseArray();
    sPreloadedDrawables[1] = new LongSparseArray();
  }
  
  public ResourcesImpl(AssetManager paramAssetManager, DisplayMetrics paramDisplayMetrics, Configuration paramConfiguration, DisplayAdjustments paramDisplayAdjustments)
  {
    this.mAssets = paramAssetManager;
    this.mMetrics.setToDefaults();
    this.mDisplayAdjustments = paramDisplayAdjustments;
    updateConfiguration(paramConfiguration, paramDisplayMetrics, paramDisplayAdjustments.getCompatibilityInfo());
    this.mAssets.ensureStringBlocks();
  }
  
  private static String adjustLanguageTag(String paramString)
  {
    int i = paramString.indexOf('-');
    if (i == -1) {}
    String str;
    for (Object localObject = "";; localObject = str)
    {
      return Locale.adjustLanguageCode(paramString) + (String)localObject;
      localObject = paramString.substring(0, i);
      str = paramString.substring(i);
      paramString = (String)localObject;
    }
  }
  
  private static int attrForQuantityCode(String paramString)
  {
    if (paramString.equals("zero")) {
      return 16777221;
    }
    if (paramString.equals("one")) {
      return 16777222;
    }
    if (paramString.equals("two")) {
      return 16777223;
    }
    if (paramString.equals("few")) {
      return 16777224;
    }
    if (paramString.equals("many")) {
      return 16777225;
    }
    return 16777220;
  }
  
  private void cacheDrawable(TypedValue arg1, boolean paramBoolean1, DrawableCache paramDrawableCache, Resources.Theme paramTheme, boolean paramBoolean2, long paramLong, Drawable paramDrawable)
  {
    paramDrawable = paramDrawable.getConstantState();
    if (paramDrawable == null) {
      return;
    }
    if (this.mPreloading)
    {
      int i = paramDrawable.getChangingConfigurations();
      if (paramBoolean1) {
        if (verifyPreloadConfig(i, 0, ???.resourceId, "drawable")) {
          sPreloadedColorDrawables.put(paramLong, paramDrawable);
        }
      }
      while (!verifyPreloadConfig(i, LAYOUT_DIR_CONFIG, ???.resourceId, "drawable")) {
        return;
      }
      if ((LAYOUT_DIR_CONFIG & i) == 0)
      {
        sPreloadedDrawables[0].put(paramLong, paramDrawable);
        sPreloadedDrawables[1].put(paramLong, paramDrawable);
        return;
      }
      sPreloadedDrawables[this.mConfiguration.getLayoutDirection()].put(paramLong, paramDrawable);
      return;
    }
    synchronized (this.mAccessLock)
    {
      paramDrawableCache.put(paramLong, paramTheme, paramDrawable, paramBoolean2);
      return;
    }
  }
  
  private ColorStateList getColorStateListFromInt(TypedValue paramTypedValue, long paramLong)
  {
    Object localObject = (ConstantState)sPreloadedComplexColors.get(paramLong);
    if (localObject != null) {
      return (ColorStateList)((ConstantState)localObject).newInstance();
    }
    localObject = ColorStateList.valueOf(paramTypedValue.data);
    if ((this.mPreloading) && (verifyPreloadConfig(paramTypedValue.changingConfigurations, 0, paramTypedValue.resourceId, "color"))) {
      sPreloadedComplexColors.put(paramLong, ((ColorStateList)localObject).getConstantState());
    }
    return (ColorStateList)localObject;
  }
  
  private PluralRules getPluralRule()
  {
    synchronized (sSync)
    {
      if (this.mPluralRule == null) {
        this.mPluralRule = PluralRules.forLocale(this.mConfiguration.getLocales().get(0));
      }
      PluralRules localPluralRules = this.mPluralRule;
      return localPluralRules;
    }
  }
  
  private ComplexColor loadComplexColorForCookie(Resources paramResources, TypedValue paramTypedValue, int paramInt, Resources.Theme paramTheme)
  {
    if (paramTypedValue.string == null) {
      throw new UnsupportedOperationException("Can't convert to ComplexColor: type=0x" + paramTypedValue.type);
    }
    String str1 = paramTypedValue.string.toString();
    Object localObject = null;
    Trace.traceBegin(8192L, str1);
    if (str1.endsWith(".xml"))
    {
      XmlResourceParser localXmlResourceParser;
      AttributeSet localAttributeSet;
      try
      {
        localXmlResourceParser = loadXmlResourceParser(str1, paramInt, paramTypedValue.assetCookie, "ComplexColor");
        localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
        int i;
        do
        {
          i = localXmlResourceParser.next();
        } while ((i != 2) && (i != 1));
        if (i != 2) {
          throw new XmlPullParserException("No start tag found");
        }
      }
      catch (Exception paramResources)
      {
        Trace.traceEnd(8192L);
        paramTypedValue = new Resources.NotFoundException("File " + str1 + " from ComplexColor resource ID #0x" + Integer.toHexString(paramInt));
        paramTypedValue.initCause(paramResources);
        throw paramTypedValue;
      }
      String str2 = localXmlResourceParser.getName();
      if (str2.equals("gradient")) {
        paramTypedValue = GradientColor.createFromXmlInner(paramResources, localXmlResourceParser, localAttributeSet, paramTheme);
      }
      for (;;)
      {
        localXmlResourceParser.close();
        Trace.traceEnd(8192L);
        return paramTypedValue;
        paramTypedValue = (TypedValue)localObject;
        if (str2.equals("selector")) {
          paramTypedValue = ColorStateList.createFromXmlInner(paramResources, localXmlResourceParser, localAttributeSet, paramTheme);
        }
      }
    }
    Trace.traceEnd(8192L);
    throw new Resources.NotFoundException("File " + str1 + " from drawable resource ID #0x" + Integer.toHexString(paramInt) + ": .xml extension required");
  }
  
  private ComplexColor loadComplexColorFromName(Resources paramResources, Resources.Theme paramTheme, TypedValue paramTypedValue, int paramInt)
  {
    long l = paramTypedValue.assetCookie << 32 | paramTypedValue.data;
    ConfigurationBoundResourceCache localConfigurationBoundResourceCache = this.mComplexColorCache;
    ComplexColor localComplexColor = (ComplexColor)localConfigurationBoundResourceCache.getInstance(l, paramResources, paramTheme);
    if (localComplexColor != null) {
      return localComplexColor;
    }
    Object localObject = (ConstantState)sPreloadedComplexColors.get(l);
    if (localObject != null) {
      localComplexColor = (ComplexColor)((ConstantState)localObject).newInstance(paramResources, paramTheme);
    }
    localObject = localComplexColor;
    if (localComplexColor == null) {
      localObject = loadComplexColorForCookie(paramResources, paramTypedValue, paramInt, paramTheme);
    }
    if (localObject != null)
    {
      ((ComplexColor)localObject).setBaseChangingConfigurations(paramTypedValue.changingConfigurations);
      if (!this.mPreloading) {
        break label151;
      }
      if (verifyPreloadConfig(((ComplexColor)localObject).getChangingConfigurations(), 0, paramTypedValue.resourceId, "color")) {
        sPreloadedComplexColors.put(l, ((ComplexColor)localObject).getConstantState());
      }
    }
    return (ComplexColor)localObject;
    label151:
    localConfigurationBoundResourceCache.put(l, paramTheme, ((ComplexColor)localObject).getConstantState());
    return (ComplexColor)localObject;
  }
  
  /* Error */
  private Drawable loadDrawableForCookie(Resources paramResources, TypedValue paramTypedValue, int paramInt, Resources.Theme paramTheme)
  {
    // Byte code:
    //   0: aload_2
    //   1: getfield 299	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   4: ifnonnull +58 -> 62
    //   7: new 360	android/content/res/Resources$NotFoundException
    //   10: dup
    //   11: new 164	java/lang/StringBuilder
    //   14: dup
    //   15: invokespecial 165	java/lang/StringBuilder:<init>	()V
    //   18: ldc_w 423
    //   21: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_0
    //   25: iload_3
    //   26: invokevirtual 426	android/content/res/ResourcesImpl:getResourceName	(I)Ljava/lang/String;
    //   29: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   32: ldc_w 428
    //   35: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: iload_3
    //   39: invokestatic 369	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   42: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: ldc_w 430
    //   48: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: aload_2
    //   52: invokevirtual 433	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   55: invokevirtual 178	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   58: invokespecial 370	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   61: athrow
    //   62: aload_2
    //   63: getfield 299	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   66: invokeinterface 315 1 0
    //   71: astore 5
    //   73: ldc2_w 316
    //   76: aload 5
    //   78: invokestatic 323	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   81: aload 5
    //   83: ldc_w 325
    //   86: invokevirtual 329	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   89: ifeq +39 -> 128
    //   92: aload_0
    //   93: aload 5
    //   95: iload_3
    //   96: aload_2
    //   97: getfield 332	android/util/TypedValue:assetCookie	I
    //   100: ldc -27
    //   102: invokevirtual 338	android/content/res/ResourcesImpl:loadXmlResourceParser	(Ljava/lang/String;IILjava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   105: astore_2
    //   106: aload_1
    //   107: aload_2
    //   108: aload 4
    //   110: invokestatic 437	android/graphics/drawable/Drawable:createFromXml	(Landroid/content/res/Resources;Lorg/xmlpull/v1/XmlPullParser;Landroid/content/res/Resources$Theme;)Landroid/graphics/drawable/Drawable;
    //   113: astore_1
    //   114: aload_2
    //   115: invokeinterface 388 1 0
    //   120: ldc2_w 316
    //   123: invokestatic 358	android/os/Trace:traceEnd	(J)V
    //   126: aload_1
    //   127: areturn
    //   128: aload_0
    //   129: getfield 134	android/content/res/ResourcesImpl:mAssets	Landroid/content/res/AssetManager;
    //   132: aload_2
    //   133: getfield 332	android/util/TypedValue:assetCookie	I
    //   136: aload 5
    //   138: iconst_2
    //   139: invokevirtual 441	android/content/res/AssetManager:openNonAsset	(ILjava/lang/String;I)Ljava/io/InputStream;
    //   142: astore 4
    //   144: aload_1
    //   145: aload_2
    //   146: aload 4
    //   148: aload 5
    //   150: aconst_null
    //   151: invokestatic 445	android/graphics/drawable/Drawable:createFromResourceStream	(Landroid/content/res/Resources;Landroid/util/TypedValue;Ljava/io/InputStream;Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/drawable/Drawable;
    //   154: astore_1
    //   155: aload 4
    //   157: invokevirtual 448	java/io/InputStream:close	()V
    //   160: goto -40 -> 120
    //   163: astore_1
    //   164: ldc2_w 316
    //   167: invokestatic 358	android/os/Trace:traceEnd	(J)V
    //   170: new 360	android/content/res/Resources$NotFoundException
    //   173: dup
    //   174: new 164	java/lang/StringBuilder
    //   177: dup
    //   178: invokespecial 165	java/lang/StringBuilder:<init>	()V
    //   181: ldc_w 362
    //   184: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   187: aload 5
    //   189: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   192: ldc_w 395
    //   195: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: iload_3
    //   199: invokestatic 369	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   202: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: invokevirtual 178	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   208: invokespecial 370	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   211: astore_2
    //   212: aload_2
    //   213: aload_1
    //   214: invokevirtual 374	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   217: pop
    //   218: aload_2
    //   219: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	220	0	this	ResourcesImpl
    //   0	220	1	paramResources	Resources
    //   0	220	2	paramTypedValue	TypedValue
    //   0	220	3	paramInt	int
    //   0	220	4	paramTheme	Resources.Theme
    //   71	117	5	str	String
    // Exception table:
    //   from	to	target	type
    //   81	120	163	java/lang/Exception
    //   128	160	163	java/lang/Exception
  }
  
  private boolean verifyPreloadConfig(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    if ((0xBFFFEFFF & paramInt1 & paramInt2) != 0) {
      try
      {
        String str1 = getResourceName(paramInt3);
        Log.w("Resources", "Preloaded " + paramString + " resource #0x" + Integer.toHexString(paramInt3) + " (" + str1 + ") that varies with configuration!!");
        return false;
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        for (;;)
        {
          String str2 = "?";
        }
      }
    }
    return true;
  }
  
  public int calcConfigChanges(Configuration paramConfiguration)
  {
    if (paramConfiguration == null) {
      return -1;
    }
    this.mTmpConfig.setTo(paramConfiguration);
    int j = paramConfiguration.densityDpi;
    int i = j;
    if (j == 0) {
      i = this.mMetrics.noncompatDensityDpi;
    }
    this.mDisplayAdjustments.getCompatibilityInfo().applyToConfiguration(i, this.mTmpConfig);
    if (this.mTmpConfig.getLocales().isEmpty()) {
      this.mTmpConfig.setLocales(LocaleList.getDefault());
    }
    return this.mConfiguration.updateFrom(this.mTmpConfig);
  }
  
  void finishPreloading()
  {
    if (this.mPreloading)
    {
      this.mPreloading = false;
      flushLayoutCache();
    }
  }
  
  public void flushLayoutCache()
  {
    for (;;)
    {
      int i;
      synchronized (this.mCachedXmlBlocks)
      {
        Arrays.fill(this.mCachedXmlBlockCookies, 0);
        Arrays.fill(this.mCachedXmlBlockFiles, null);
        XmlBlock[] arrayOfXmlBlock2 = this.mCachedXmlBlocks;
        i = 0;
        if (i < 4)
        {
          XmlBlock localXmlBlock = arrayOfXmlBlock2[i];
          if (localXmlBlock != null) {
            localXmlBlock.close();
          }
        }
        else
        {
          Arrays.fill(arrayOfXmlBlock2, null);
          return;
        }
      }
      i += 1;
    }
  }
  
  ConfigurationBoundResourceCache<Animator> getAnimatorCache()
  {
    return this.mAnimatorCache;
  }
  
  public AssetManager getAssets()
  {
    return this.mAssets;
  }
  
  CompatibilityInfo getCompatibilityInfo()
  {
    return this.mDisplayAdjustments.getCompatibilityInfo();
  }
  
  Configuration getConfiguration()
  {
    return this.mConfiguration;
  }
  
  public DisplayAdjustments getDisplayAdjustments()
  {
    return this.mDisplayAdjustments;
  }
  
  DisplayMetrics getDisplayMetrics()
  {
    return this.mMetrics;
  }
  
  int getIdentifier(String paramString1, String paramString2, String paramString3)
  {
    if (paramString1 == null) {
      throw new NullPointerException("name is null");
    }
    try
    {
      int i = Integer.parseInt(paramString1);
      return i;
    }
    catch (Exception localException) {}
    return this.mAssets.getResourceIdentifier(paramString1, paramString2, paramString3);
  }
  
  LongSparseArray<Drawable.ConstantState> getPreloadedDrawables()
  {
    return sPreloadedDrawables[0];
  }
  
  CharSequence getQuantityText(int paramInt1, int paramInt2)
    throws Resources.NotFoundException
  {
    PluralRules localPluralRules = getPluralRule();
    CharSequence localCharSequence = this.mAssets.getResourceBagText(paramInt1, attrForQuantityCode(localPluralRules.select(paramInt2)));
    if (localCharSequence != null) {
      return localCharSequence;
    }
    localCharSequence = this.mAssets.getResourceBagText(paramInt1, 16777220);
    if (localCharSequence != null) {
      return localCharSequence;
    }
    throw new Resources.NotFoundException("Plural resource ID #0x" + Integer.toHexString(paramInt1) + " quantity=" + paramInt2 + " item=" + localPluralRules.select(paramInt2));
  }
  
  String getResourceEntryName(int paramInt)
    throws Resources.NotFoundException
  {
    String str = this.mAssets.getResourceEntryName(paramInt);
    if (str != null) {
      return str;
    }
    throw new Resources.NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  String getResourceName(int paramInt)
    throws Resources.NotFoundException
  {
    String str = this.mAssets.getResourceName(paramInt);
    if (str != null) {
      return str;
    }
    throw new Resources.NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  String getResourcePackageName(int paramInt)
    throws Resources.NotFoundException
  {
    String str = this.mAssets.getResourcePackageName(paramInt);
    if (str != null) {
      return str;
    }
    throw new Resources.NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  String getResourceTypeName(int paramInt)
    throws Resources.NotFoundException
  {
    String str = this.mAssets.getResourceTypeName(paramInt);
    if (str != null) {
      return str;
    }
    throw new Resources.NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  Configuration[] getSizeConfigurations()
  {
    return this.mAssets.getSizeConfigurations();
  }
  
  ConfigurationBoundResourceCache<StateListAnimator> getStateListAnimatorCache()
  {
    return this.mStateListAnimatorCache;
  }
  
  void getValue(int paramInt, TypedValue paramTypedValue, boolean paramBoolean)
    throws Resources.NotFoundException
  {
    if (this.mAssets.getResourceValue(paramInt, 0, paramTypedValue, paramBoolean)) {
      return;
    }
    throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  void getValue(String paramString, TypedValue paramTypedValue, boolean paramBoolean)
    throws Resources.NotFoundException
  {
    int i = getIdentifier(paramString, "string", null);
    if (i != 0)
    {
      getValue(i, paramTypedValue, paramBoolean);
      return;
    }
    throw new Resources.NotFoundException("String resource name " + paramString);
  }
  
  void getValueForDensity(int paramInt1, int paramInt2, TypedValue paramTypedValue, boolean paramBoolean)
    throws Resources.NotFoundException
  {
    if (this.mAssets.getResourceValue(paramInt1, paramInt2, paramTypedValue, paramBoolean)) {
      return;
    }
    throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt1));
  }
  
  ColorStateList loadColorStateList(Resources paramResources, TypedValue paramTypedValue, int paramInt, Resources.Theme paramTheme)
    throws Resources.NotFoundException
  {
    long l1 = paramTypedValue.assetCookie;
    long l2 = paramTypedValue.data;
    if ((paramTypedValue.type >= 28) && (paramTypedValue.type <= 31)) {
      return getColorStateListFromInt(paramTypedValue, l1 << 32 | l2);
    }
    paramResources = loadComplexColorFromName(paramResources, paramTheme, paramTypedValue, paramInt);
    if ((paramResources != null) && ((paramResources instanceof ColorStateList))) {
      return (ColorStateList)paramResources;
    }
    throw new Resources.NotFoundException("Can't find ColorStateList from drawable resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  ComplexColor loadComplexColor(Resources paramResources, TypedValue paramTypedValue, int paramInt, Resources.Theme paramTheme)
  {
    long l1 = paramTypedValue.assetCookie;
    long l2 = paramTypedValue.data;
    if ((paramTypedValue.type >= 28) && (paramTypedValue.type <= 31)) {
      return getColorStateListFromInt(paramTypedValue, l1 << 32 | l2);
    }
    String str = paramTypedValue.string.toString();
    if (str.endsWith(".xml")) {
      try
      {
        paramResources = loadComplexColorFromName(paramResources, paramTheme, paramTypedValue, paramInt);
        return paramResources;
      }
      catch (Exception paramResources)
      {
        paramTypedValue = new Resources.NotFoundException("File " + str + " from complex color resource ID #0x" + Integer.toHexString(paramInt));
        paramTypedValue.initCause(paramResources);
        throw paramTypedValue;
      }
    }
    throw new Resources.NotFoundException("File " + str + " from drawable resource ID #0x" + Integer.toHexString(paramInt) + ": .xml extension required");
  }
  
  Drawable loadDrawable(Resources paramResources, TypedValue paramTypedValue, int paramInt, Resources.Theme paramTheme, boolean paramBoolean)
    throws Resources.NotFoundException
  {
    try
    {
      boolean bool1;
      DrawableCache localDrawableCache;
      if ((paramTypedValue.type >= 28) && (paramTypedValue.type <= 31))
      {
        bool1 = true;
        localDrawableCache = this.mColorDrawableCache;
      }
      Object localObject;
      for (long l = paramTypedValue.data; (!this.mPreloading) && (paramBoolean); l = paramTypedValue.assetCookie << 32 | paramTypedValue.data)
      {
        localObject = localDrawableCache.getInstance(l, paramResources, paramTheme);
        if (localObject == null) {
          break;
        }
        return (Drawable)localObject;
        bool1 = false;
        localDrawableCache = this.mDrawableCache;
      }
      if (bool1)
      {
        localObject = (Drawable.ConstantState)sPreloadedColorDrawables.get(l);
        if (localObject == null) {
          break label231;
        }
        paramResources = ((Drawable.ConstantState)localObject).newDrawable(paramResources);
        label124:
        if (paramResources == null) {
          break label263;
        }
      }
      label231:
      label263:
      for (boolean bool2 = paramResources.canApplyTheme();; bool2 = false)
      {
        localObject = paramResources;
        if (bool2)
        {
          localObject = paramResources;
          if (paramTheme != null)
          {
            localObject = paramResources.mutate();
            ((Drawable)localObject).applyTheme(paramTheme);
            ((Drawable)localObject).clearMutated();
          }
        }
        if ((localObject == null) || (!paramBoolean)) {
          break label336;
        }
        ((Drawable)localObject).setChangingConfigurations(paramTypedValue.changingConfigurations);
        cacheDrawable(paramTypedValue, bool1, localDrawableCache, paramTheme, bool2, l, (Drawable)localObject);
        return (Drawable)localObject;
        localObject = (Drawable.ConstantState)sPreloadedDrawables[this.mConfiguration.getLayoutDirection()].get(l);
        break;
        if (bool1)
        {
          paramResources = new ColorDrawable(paramTypedValue.data);
          break label124;
        }
        paramResources = loadDrawableForCookie(paramResources, paramTypedValue, paramInt, null);
        break label124;
      }
      return (Drawable)localObject;
    }
    catch (Exception paramTypedValue)
    {
      try
      {
        paramResources = getResourceName(paramInt);
        paramResources = new Resources.NotFoundException("Drawable " + paramResources + " with resource ID #0x" + Integer.toHexString(paramInt), paramTypedValue);
        paramResources.setStackTrace(new StackTraceElement[0]);
        throw paramResources;
      }
      catch (Resources.NotFoundException paramResources)
      {
        for (;;)
        {
          paramResources = "(missing name)";
        }
      }
    }
  }
  
  /* Error */
  XmlResourceParser loadXmlResourceParser(String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws Resources.NotFoundException
  {
    // Byte code:
    //   0: iload_2
    //   1: ifeq +184 -> 185
    //   4: aload_0
    //   5: getfield 125	android/content/res/ResourcesImpl:mCachedXmlBlocks	[Landroid/content/res/XmlBlock;
    //   8: astore 7
    //   10: aload 7
    //   12: monitorenter
    //   13: aload_0
    //   14: getfield 117	android/content/res/ResourcesImpl:mCachedXmlBlockCookies	[I
    //   17: astore 8
    //   19: aload_0
    //   20: getfield 121	android/content/res/ResourcesImpl:mCachedXmlBlockFiles	[Ljava/lang/String;
    //   23: astore 9
    //   25: aload_0
    //   26: getfield 125	android/content/res/ResourcesImpl:mCachedXmlBlocks	[Landroid/content/res/XmlBlock;
    //   29: astore 10
    //   31: aload 9
    //   33: arraylength
    //   34: istore 6
    //   36: iconst_0
    //   37: istore 5
    //   39: iload 5
    //   41: iload 6
    //   43: if_icmpge +57 -> 100
    //   46: aload 8
    //   48: iload 5
    //   50: iaload
    //   51: iload_3
    //   52: if_icmpne +39 -> 91
    //   55: aload 9
    //   57: iload 5
    //   59: aaload
    //   60: ifnull +31 -> 91
    //   63: aload 9
    //   65: iload 5
    //   67: aaload
    //   68: aload_1
    //   69: invokevirtual 193	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   72: ifeq +19 -> 91
    //   75: aload 10
    //   77: iload 5
    //   79: aaload
    //   80: invokevirtual 659	android/content/res/XmlBlock:newParser	()Landroid/content/res/XmlResourceParser;
    //   83: astore 8
    //   85: aload 7
    //   87: monitorexit
    //   88: aload 8
    //   90: areturn
    //   91: iload 5
    //   93: iconst_1
    //   94: iadd
    //   95: istore 5
    //   97: goto -58 -> 39
    //   100: aload_0
    //   101: getfield 134	android/content/res/ResourcesImpl:mAssets	Landroid/content/res/AssetManager;
    //   104: iload_3
    //   105: aload_1
    //   106: invokevirtual 663	android/content/res/AssetManager:openXmlBlockAsset	(ILjava/lang/String;)Landroid/content/res/XmlBlock;
    //   109: astore 11
    //   111: aload 11
    //   113: ifnull +69 -> 182
    //   116: aload_0
    //   117: getfield 115	android/content/res/ResourcesImpl:mLastCachedXmlBlockIndex	I
    //   120: iconst_1
    //   121: iadd
    //   122: iload 6
    //   124: irem
    //   125: istore 5
    //   127: aload_0
    //   128: iload 5
    //   130: putfield 115	android/content/res/ResourcesImpl:mLastCachedXmlBlockIndex	I
    //   133: aload 10
    //   135: iload 5
    //   137: aaload
    //   138: astore 12
    //   140: aload 12
    //   142: ifnull +8 -> 150
    //   145: aload 12
    //   147: invokevirtual 511	android/content/res/XmlBlock:close	()V
    //   150: aload 8
    //   152: iload 5
    //   154: iload_3
    //   155: iastore
    //   156: aload 9
    //   158: iload 5
    //   160: aload_1
    //   161: aastore
    //   162: aload 10
    //   164: iload 5
    //   166: aload 11
    //   168: aastore
    //   169: aload 11
    //   171: invokevirtual 659	android/content/res/XmlBlock:newParser	()Landroid/content/res/XmlResourceParser;
    //   174: astore 8
    //   176: aload 7
    //   178: monitorexit
    //   179: aload 8
    //   181: areturn
    //   182: aload 7
    //   184: monitorexit
    //   185: new 360	android/content/res/Resources$NotFoundException
    //   188: dup
    //   189: new 164	java/lang/StringBuilder
    //   192: dup
    //   193: invokespecial 165	java/lang/StringBuilder:<init>	()V
    //   196: ldc_w 362
    //   199: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   202: aload_1
    //   203: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: ldc_w 665
    //   209: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   212: aload 4
    //   214: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: ldc_w 667
    //   220: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   223: iload_2
    //   224: invokestatic 369	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   227: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   230: invokevirtual 178	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   233: invokespecial 370	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   236: athrow
    //   237: astore 8
    //   239: aload 7
    //   241: monitorexit
    //   242: aload 8
    //   244: athrow
    //   245: astore 7
    //   247: new 360	android/content/res/Resources$NotFoundException
    //   250: dup
    //   251: new 164	java/lang/StringBuilder
    //   254: dup
    //   255: invokespecial 165	java/lang/StringBuilder:<init>	()V
    //   258: ldc_w 362
    //   261: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   264: aload_1
    //   265: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   268: ldc_w 665
    //   271: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   274: aload 4
    //   276: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   279: ldc_w 667
    //   282: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   285: iload_2
    //   286: invokestatic 369	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   289: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: invokevirtual 178	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   295: invokespecial 370	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   298: astore_1
    //   299: aload_1
    //   300: aload 7
    //   302: invokevirtual 374	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   305: pop
    //   306: aload_1
    //   307: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	308	0	this	ResourcesImpl
    //   0	308	1	paramString1	String
    //   0	308	2	paramInt1	int
    //   0	308	3	paramInt2	int
    //   0	308	4	paramString2	String
    //   37	128	5	i	int
    //   34	91	6	j	int
    //   245	56	7	localException	Exception
    //   17	163	8	localObject1	Object
    //   237	6	8	localObject2	Object
    //   23	134	9	arrayOfString	String[]
    //   29	134	10	arrayOfXmlBlock2	XmlBlock[]
    //   109	61	11	localXmlBlock1	XmlBlock
    //   138	8	12	localXmlBlock2	XmlBlock
    // Exception table:
    //   from	to	target	type
    //   13	36	237	finally
    //   63	85	237	finally
    //   100	111	237	finally
    //   116	133	237	finally
    //   145	150	237	finally
    //   169	176	237	finally
    //   4	13	245	java/lang/Exception
    //   85	88	245	java/lang/Exception
    //   176	179	245	java/lang/Exception
    //   182	185	245	java/lang/Exception
    //   239	245	245	java/lang/Exception
  }
  
  ThemeImpl newThemeImpl()
  {
    return new ThemeImpl();
  }
  
  ThemeImpl newThemeImpl(Resources.ThemeKey paramThemeKey)
  {
    ThemeImpl localThemeImpl = new ThemeImpl();
    ThemeImpl.-get0(localThemeImpl).setTo(paramThemeKey);
    localThemeImpl.rebase();
    return localThemeImpl;
  }
  
  InputStream openRawResource(int paramInt, TypedValue paramTypedValue)
    throws Resources.NotFoundException
  {
    getValue(paramInt, paramTypedValue, true);
    StringBuilder localStringBuilder;
    try
    {
      InputStream localInputStream = this.mAssets.openNonAsset(paramTypedValue.assetCookie, paramTypedValue.string.toString(), 2);
      return localInputStream;
    }
    catch (Exception localException)
    {
      localStringBuilder = new StringBuilder().append("File ");
      if (paramTypedValue.string != null) {}
    }
    for (paramTypedValue = "(null)";; paramTypedValue = paramTypedValue.string.toString())
    {
      paramTypedValue = new Resources.NotFoundException(paramTypedValue + " from drawable resource ID #0x" + Integer.toHexString(paramInt));
      paramTypedValue.initCause(localException);
      throw paramTypedValue;
    }
  }
  
  AssetFileDescriptor openRawResourceFd(int paramInt, TypedValue paramTypedValue)
    throws Resources.NotFoundException
  {
    getValue(paramInt, paramTypedValue, true);
    try
    {
      AssetFileDescriptor localAssetFileDescriptor = this.mAssets.openNonAssetFd(paramTypedValue.assetCookie, paramTypedValue.string.toString());
      return localAssetFileDescriptor;
    }
    catch (Exception localException)
    {
      throw new Resources.NotFoundException("File " + paramTypedValue.string.toString() + " from drawable " + "resource ID #0x" + Integer.toHexString(paramInt), localException);
    }
  }
  
  public final void startPreloading()
  {
    synchronized (sSync)
    {
      if (sPreloaded) {
        throw new IllegalStateException("Resources already preloaded");
      }
    }
    sPreloaded = true;
    this.mPreloading = true;
    this.mConfiguration.densityDpi = DisplayMetrics.DENSITY_DEVICE;
    updateConfiguration(null, null, null);
  }
  
  /* Error */
  public void updateConfiguration(Configuration arg1, DisplayMetrics paramDisplayMetrics, CompatibilityInfo paramCompatibilityInfo)
  {
    // Byte code:
    //   0: ldc2_w 316
    //   3: ldc_w 712
    //   6: invokestatic 323	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   9: aload_0
    //   10: getfield 92	android/content/res/ResourcesImpl:mAccessLock	Ljava/lang/Object;
    //   13: astore 8
    //   15: aload 8
    //   17: monitorenter
    //   18: aload_3
    //   19: ifnull +11 -> 30
    //   22: aload_0
    //   23: getfield 139	android/content/res/ResourcesImpl:mDisplayAdjustments	Landroid/view/DisplayAdjustments;
    //   26: aload_3
    //   27: invokevirtual 716	android/view/DisplayAdjustments:setCompatibilityInfo	(Landroid/content/res/CompatibilityInfo;)V
    //   30: aload_2
    //   31: ifnull +11 -> 42
    //   34: aload_0
    //   35: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   38: aload_2
    //   39: invokevirtual 719	android/util/DisplayMetrics:setTo	(Landroid/util/DisplayMetrics;)V
    //   42: aload_0
    //   43: getfield 139	android/content/res/ResourcesImpl:mDisplayAdjustments	Landroid/view/DisplayAdjustments;
    //   46: invokevirtual 145	android/view/DisplayAdjustments:getCompatibilityInfo	()Landroid/content/res/CompatibilityInfo;
    //   49: aload_0
    //   50: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   53: invokevirtual 722	android/content/res/CompatibilityInfo:applyToDisplayMetrics	(Landroid/util/DisplayMetrics;)V
    //   56: aload_0
    //   57: aload_1
    //   58: invokevirtual 724	android/content/res/ResourcesImpl:calcConfigChanges	(Landroid/content/res/Configuration;)I
    //   61: istore 7
    //   63: aload_0
    //   64: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   67: invokevirtual 280	android/content/res/Configuration:getLocales	()Landroid/os/LocaleList;
    //   70: astore_1
    //   71: aload_1
    //   72: astore_2
    //   73: aload_1
    //   74: invokevirtual 487	android/os/LocaleList:isEmpty	()Z
    //   77: ifeq +15 -> 92
    //   80: invokestatic 490	android/os/LocaleList:getDefault	()Landroid/os/LocaleList;
    //   83: astore_2
    //   84: aload_0
    //   85: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   88: aload_2
    //   89: invokevirtual 494	android/content/res/Configuration:setLocales	(Landroid/os/LocaleList;)V
    //   92: iload 7
    //   94: iconst_4
    //   95: iand
    //   96: ifeq +86 -> 182
    //   99: aload_2
    //   100: invokevirtual 727	android/os/LocaleList:size	()I
    //   103: iconst_1
    //   104: if_icmple +78 -> 182
    //   107: aload_0
    //   108: getfield 134	android/content/res/ResourcesImpl:mAssets	Landroid/content/res/AssetManager;
    //   111: invokevirtual 731	android/content/res/AssetManager:getNonSystemLocales	()[Ljava/lang/String;
    //   114: astore_3
    //   115: aload_3
    //   116: astore_1
    //   117: aload_3
    //   118: invokestatic 735	android/os/LocaleList:isPseudoLocalesOnly	([Ljava/lang/String;)Z
    //   121: ifeq +22 -> 143
    //   124: aload_0
    //   125: getfield 134	android/content/res/ResourcesImpl:mAssets	Landroid/content/res/AssetManager;
    //   128: invokevirtual 737	android/content/res/AssetManager:getLocales	()[Ljava/lang/String;
    //   131: astore_3
    //   132: aload_3
    //   133: astore_1
    //   134: aload_3
    //   135: invokestatic 735	android/os/LocaleList:isPseudoLocalesOnly	([Ljava/lang/String;)Z
    //   138: ifeq +5 -> 143
    //   141: aconst_null
    //   142: astore_1
    //   143: aload_1
    //   144: ifnull +38 -> 182
    //   147: aload_2
    //   148: aload_1
    //   149: invokevirtual 741	android/os/LocaleList:getFirstMatchWithEnglishSupported	([Ljava/lang/String;)Ljava/util/Locale;
    //   152: astore_1
    //   153: aload_1
    //   154: ifnull +28 -> 182
    //   157: aload_1
    //   158: aload_2
    //   159: iconst_0
    //   160: invokevirtual 285	android/os/LocaleList:get	(I)Ljava/util/Locale;
    //   163: if_acmpeq +19 -> 182
    //   166: aload_0
    //   167: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   170: new 282	android/os/LocaleList
    //   173: dup
    //   174: aload_1
    //   175: aload_2
    //   176: invokespecial 744	android/os/LocaleList:<init>	(Ljava/util/Locale;Landroid/os/LocaleList;)V
    //   179: invokevirtual 494	android/content/res/Configuration:setLocales	(Landroid/os/LocaleList;)V
    //   182: aload_0
    //   183: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   186: getfield 474	android/content/res/Configuration:densityDpi	I
    //   189: ifeq +36 -> 225
    //   192: aload_0
    //   193: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   196: aload_0
    //   197: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   200: getfield 474	android/content/res/Configuration:densityDpi	I
    //   203: putfield 745	android/util/DisplayMetrics:densityDpi	I
    //   206: aload_0
    //   207: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   210: aload_0
    //   211: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   214: getfield 474	android/content/res/Configuration:densityDpi	I
    //   217: i2f
    //   218: ldc_w 746
    //   221: fmul
    //   222: putfield 750	android/util/DisplayMetrics:density	F
    //   225: aload_0
    //   226: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   229: aload_0
    //   230: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   233: getfield 750	android/util/DisplayMetrics:density	F
    //   236: aload_0
    //   237: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   240: getfield 753	android/content/res/Configuration:fontScale	F
    //   243: fmul
    //   244: putfield 756	android/util/DisplayMetrics:scaledDensity	F
    //   247: aload_0
    //   248: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   251: getfield 759	android/util/DisplayMetrics:widthPixels	I
    //   254: aload_0
    //   255: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   258: getfield 762	android/util/DisplayMetrics:heightPixels	I
    //   261: if_icmplt +255 -> 516
    //   264: aload_0
    //   265: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   268: getfield 759	android/util/DisplayMetrics:widthPixels	I
    //   271: istore 4
    //   273: aload_0
    //   274: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   277: getfield 762	android/util/DisplayMetrics:heightPixels	I
    //   280: istore 5
    //   282: aload_0
    //   283: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   286: getfield 765	android/content/res/Configuration:keyboardHidden	I
    //   289: iconst_1
    //   290: if_icmpne +247 -> 537
    //   293: aload_0
    //   294: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   297: getfield 768	android/content/res/Configuration:hardKeyboardHidden	I
    //   300: iconst_2
    //   301: if_icmpne +236 -> 537
    //   304: iconst_3
    //   305: istore 6
    //   307: aload_0
    //   308: getfield 134	android/content/res/ResourcesImpl:mAssets	Landroid/content/res/AssetManager;
    //   311: aload_0
    //   312: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   315: getfield 771	android/content/res/Configuration:mcc	I
    //   318: aload_0
    //   319: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   322: getfield 774	android/content/res/Configuration:mnc	I
    //   325: aload_0
    //   326: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   329: invokevirtual 280	android/content/res/Configuration:getLocales	()Landroid/os/LocaleList;
    //   332: iconst_0
    //   333: invokevirtual 285	android/os/LocaleList:get	(I)Ljava/util/Locale;
    //   336: invokevirtual 777	java/util/Locale:toLanguageTag	()Ljava/lang/String;
    //   339: invokestatic 779	android/content/res/ResourcesImpl:adjustLanguageTag	(Ljava/lang/String;)Ljava/lang/String;
    //   342: aload_0
    //   343: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   346: getfield 782	android/content/res/Configuration:orientation	I
    //   349: aload_0
    //   350: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   353: getfield 785	android/content/res/Configuration:touchscreen	I
    //   356: aload_0
    //   357: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   360: getfield 474	android/content/res/Configuration:densityDpi	I
    //   363: aload_0
    //   364: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   367: getfield 788	android/content/res/Configuration:keyboard	I
    //   370: iload 6
    //   372: aload_0
    //   373: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   376: getfield 791	android/content/res/Configuration:navigation	I
    //   379: iload 4
    //   381: iload 5
    //   383: aload_0
    //   384: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   387: getfield 794	android/content/res/Configuration:smallestScreenWidthDp	I
    //   390: aload_0
    //   391: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   394: getfield 797	android/content/res/Configuration:screenWidthDp	I
    //   397: aload_0
    //   398: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   401: getfield 800	android/content/res/Configuration:screenHeightDp	I
    //   404: aload_0
    //   405: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   408: getfield 803	android/content/res/Configuration:screenLayout	I
    //   411: aload_0
    //   412: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   415: getfield 806	android/content/res/Configuration:uiMode	I
    //   418: getstatic 811	android/os/Build$VERSION:RESOURCES_SDK_INT	I
    //   421: invokevirtual 815	android/content/res/AssetManager:setConfiguration	(IILjava/lang/String;IIIIIIIIIIIIII)V
    //   424: aload_0
    //   425: getfield 102	android/content/res/ResourcesImpl:mDrawableCache	Landroid/content/res/DrawableCache;
    //   428: iload 7
    //   430: invokevirtual 818	android/content/res/DrawableCache:onConfigurationChange	(I)V
    //   433: aload_0
    //   434: getfield 104	android/content/res/ResourcesImpl:mColorDrawableCache	Landroid/content/res/DrawableCache;
    //   437: iload 7
    //   439: invokevirtual 818	android/content/res/DrawableCache:onConfigurationChange	(I)V
    //   442: aload_0
    //   443: getfield 109	android/content/res/ResourcesImpl:mComplexColorCache	Landroid/content/res/ConfigurationBoundResourceCache;
    //   446: iload 7
    //   448: invokevirtual 819	android/content/res/ConfigurationBoundResourceCache:onConfigurationChange	(I)V
    //   451: aload_0
    //   452: getfield 111	android/content/res/ResourcesImpl:mAnimatorCache	Landroid/content/res/ConfigurationBoundResourceCache;
    //   455: iload 7
    //   457: invokevirtual 819	android/content/res/ConfigurationBoundResourceCache:onConfigurationChange	(I)V
    //   460: aload_0
    //   461: getfield 113	android/content/res/ResourcesImpl:mStateListAnimatorCache	Landroid/content/res/ConfigurationBoundResourceCache;
    //   464: iload 7
    //   466: invokevirtual 819	android/content/res/ConfigurationBoundResourceCache:onConfigurationChange	(I)V
    //   469: aload_0
    //   470: invokevirtual 501	android/content/res/ResourcesImpl:flushLayoutCache	()V
    //   473: aload 8
    //   475: monitorexit
    //   476: getstatic 79	android/content/res/ResourcesImpl:sSync	Ljava/lang/Object;
    //   479: astore_1
    //   480: aload_1
    //   481: monitorenter
    //   482: aload_0
    //   483: getfield 276	android/content/res/ResourcesImpl:mPluralRule	Landroid/icu/text/PluralRules;
    //   486: ifnull +21 -> 507
    //   489: aload_0
    //   490: aload_0
    //   491: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   494: invokevirtual 280	android/content/res/Configuration:getLocales	()Landroid/os/LocaleList;
    //   497: iconst_0
    //   498: invokevirtual 285	android/os/LocaleList:get	(I)Ljava/util/Locale;
    //   501: invokestatic 291	android/icu/text/PluralRules:forLocale	(Ljava/util/Locale;)Landroid/icu/text/PluralRules;
    //   504: putfield 276	android/content/res/ResourcesImpl:mPluralRule	Landroid/icu/text/PluralRules;
    //   507: aload_1
    //   508: monitorexit
    //   509: ldc2_w 316
    //   512: invokestatic 358	android/os/Trace:traceEnd	(J)V
    //   515: return
    //   516: aload_0
    //   517: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   520: getfield 762	android/util/DisplayMetrics:heightPixels	I
    //   523: istore 4
    //   525: aload_0
    //   526: getfield 130	android/content/res/ResourcesImpl:mMetrics	Landroid/util/DisplayMetrics;
    //   529: getfield 759	android/util/DisplayMetrics:widthPixels	I
    //   532: istore 5
    //   534: goto -252 -> 282
    //   537: aload_0
    //   538: getfield 132	android/content/res/ResourcesImpl:mConfiguration	Landroid/content/res/Configuration;
    //   541: getfield 765	android/content/res/Configuration:keyboardHidden	I
    //   544: istore 6
    //   546: goto -239 -> 307
    //   549: astore_1
    //   550: aload 8
    //   552: monitorexit
    //   553: aload_1
    //   554: athrow
    //   555: astore_1
    //   556: ldc2_w 316
    //   559: invokestatic 358	android/os/Trace:traceEnd	(J)V
    //   562: aload_1
    //   563: athrow
    //   564: astore_2
    //   565: aload_1
    //   566: monitorexit
    //   567: aload_2
    //   568: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	569	0	this	ResourcesImpl
    //   0	569	2	paramDisplayMetrics	DisplayMetrics
    //   0	569	3	paramCompatibilityInfo	CompatibilityInfo
    //   271	253	4	i	int
    //   280	253	5	j	int
    //   305	240	6	k	int
    //   61	404	7	m	int
    //   13	538	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   22	30	549	finally
    //   34	42	549	finally
    //   42	71	549	finally
    //   73	92	549	finally
    //   99	115	549	finally
    //   117	132	549	finally
    //   134	141	549	finally
    //   147	153	549	finally
    //   157	182	549	finally
    //   182	225	549	finally
    //   225	282	549	finally
    //   282	304	549	finally
    //   307	473	549	finally
    //   516	534	549	finally
    //   537	546	549	finally
    //   9	18	555	finally
    //   473	482	555	finally
    //   507	509	555	finally
    //   550	555	555	finally
    //   565	569	555	finally
    //   482	507	564	finally
  }
  
  public class ThemeImpl
  {
    private final AssetManager mAssets = ResourcesImpl.this.mAssets;
    private final Resources.ThemeKey mKey = new Resources.ThemeKey();
    private final long mTheme = this.mAssets.createTheme();
    private int mThemeResId = 0;
    
    ThemeImpl() {}
    
    void applyStyle(int paramInt, boolean paramBoolean)
    {
      synchronized (this.mKey)
      {
        AssetManager.applyThemeStyle(this.mTheme, paramInt, paramBoolean);
        this.mThemeResId = paramInt;
        this.mKey.append(paramInt, paramBoolean);
        return;
      }
    }
    
    public void dump(int paramInt, String paramString1, String paramString2)
    {
      synchronized (this.mKey)
      {
        AssetManager.dumpTheme(this.mTheme, paramInt, paramString1, paramString2);
        return;
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      super.finalize();
      this.mAssets.releaseTheme(this.mTheme);
    }
    
    int[] getAllAttributes()
    {
      return this.mAssets.getStyleAttributes(getAppliedStyleResId());
    }
    
    int getAppliedStyleResId()
    {
      return this.mThemeResId;
    }
    
    int getChangingConfigurations()
    {
      synchronized (this.mKey)
      {
        int i = ActivityInfo.activityInfoConfigNativeToJava(AssetManager.getThemeChangingConfigurations(this.mTheme));
        return i;
      }
    }
    
    Resources.ThemeKey getKey()
    {
      return this.mKey;
    }
    
    long getNativeTheme()
    {
      return this.mTheme;
    }
    
    String[] getTheme()
    {
      for (;;)
      {
        int i;
        String[] arrayOfString;
        int j;
        synchronized (this.mKey)
        {
          i = this.mKey.mCount;
          arrayOfString = new String[i * 2];
          j = 0;
          i -= 1;
          if (j >= arrayOfString.length) {
            break label111;
          }
          int k = this.mKey.mResId[i];
          int m = this.mKey.mForce[i];
          try
          {
            arrayOfString[j] = ResourcesImpl.this.getResourceName(k);
            if (m != 0) {
              String str1 = "forced";
            }
          }
          catch (Resources.NotFoundException localNotFoundException)
          {
            arrayOfString[j] = Integer.toHexString(j);
            continue;
          }
        }
        String str2 = "not forced";
        break label117;
        label111:
        return arrayOfString;
        label117:
        arrayOfString[(j + 1)] = str2;
        j += 2;
        i -= 1;
      }
    }
    
    TypedArray obtainStyledAttributes(Resources.Theme paramTheme, AttributeSet paramAttributeSet, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    {
      synchronized (this.mKey)
      {
        int i = paramArrayOfInt.length;
        TypedArray localTypedArray = TypedArray.obtain(paramTheme.getResources(), i);
        paramAttributeSet = (XmlBlock.Parser)paramAttributeSet;
        long l2 = this.mTheme;
        if (paramAttributeSet != null)
        {
          l1 = paramAttributeSet.mParseState;
          AssetManager.applyStyle(l2, paramInt1, paramInt2, l1, paramArrayOfInt, localTypedArray.mData, localTypedArray.mIndices);
          localTypedArray.mTheme = paramTheme;
          localTypedArray.mXml = paramAttributeSet;
          return localTypedArray;
        }
        long l1 = 0L;
      }
    }
    
    void rebase()
    {
      synchronized (this.mKey)
      {
        AssetManager.clearTheme(this.mTheme);
        int i = 0;
        while (i < this.mKey.mCount)
        {
          int j = this.mKey.mResId[i];
          int k = this.mKey.mForce[i];
          AssetManager.applyThemeStyle(this.mTheme, j, k);
          i += 1;
        }
        return;
      }
    }
    
    boolean resolveAttribute(int paramInt, TypedValue paramTypedValue, boolean paramBoolean)
    {
      synchronized (this.mKey)
      {
        paramBoolean = this.mAssets.getThemeValue(this.mTheme, paramInt, paramTypedValue, paramBoolean);
        return paramBoolean;
      }
    }
    
    TypedArray resolveAttributes(Resources.Theme paramTheme, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      int i;
      synchronized (this.mKey)
      {
        i = paramArrayOfInt2.length;
        if ((paramArrayOfInt1 == null) || (i != paramArrayOfInt1.length)) {
          throw new IllegalArgumentException("Base attribute values must the same length as attrs");
        }
      }
      TypedArray localTypedArray = TypedArray.obtain(paramTheme.getResources(), i);
      AssetManager.resolveAttrs(this.mTheme, 0, 0, paramArrayOfInt1, paramArrayOfInt2, localTypedArray.mData, localTypedArray.mIndices);
      localTypedArray.mTheme = paramTheme;
      localTypedArray.mXml = null;
      return localTypedArray;
    }
    
    void setTo(ThemeImpl paramThemeImpl)
    {
      synchronized (this.mKey)
      {
        synchronized (paramThemeImpl.mKey)
        {
          AssetManager.copyTheme(this.mTheme, paramThemeImpl.mTheme);
          this.mThemeResId = paramThemeImpl.mThemeResId;
          this.mKey.setTo(paramThemeImpl.getKey());
          return;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ResourcesImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */