package android.content.res;

import android.animation.Animator;
import android.animation.StateListAnimator;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.DrawableInflater;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pools.SynchronizedPool;
import android.util.TypedValue;
import android.view.DisplayAdjustments;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewHierarchyEncoder;
import com.android.internal.R.styleable;
import com.android.internal.util.GrowingArrayUtils;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;

public class Resources
{
  static final String TAG = "Resources";
  static Resources mSystem = null;
  private static final Object sSync = new Object();
  final ClassLoader mClassLoader;
  private DrawableInflater mDrawableInflater;
  private ResourcesImpl mResourcesImpl;
  private final ArrayList<WeakReference<Theme>> mThemeRefs = new ArrayList();
  private TypedValue mTmpValue = new TypedValue();
  private final Object mTmpValueLock = new Object();
  final Pools.SynchronizedPool<TypedArray> mTypedArrayPool = new Pools.SynchronizedPool(5);
  
  private Resources()
  {
    this(null);
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    localDisplayMetrics.setToDefaults();
    Configuration localConfiguration = new Configuration();
    localConfiguration.setToDefaults();
    this.mResourcesImpl = new ResourcesImpl(AssetManager.getSystem(), localDisplayMetrics, localConfiguration, new DisplayAdjustments());
  }
  
  @Deprecated
  public Resources(AssetManager paramAssetManager, DisplayMetrics paramDisplayMetrics, Configuration paramConfiguration)
  {
    this(null);
    this.mResourcesImpl = new ResourcesImpl(paramAssetManager, paramDisplayMetrics, paramConfiguration, new DisplayAdjustments());
  }
  
  public Resources(ClassLoader paramClassLoader)
  {
    ClassLoader localClassLoader = paramClassLoader;
    if (paramClassLoader == null) {
      localClassLoader = ClassLoader.getSystemClassLoader();
    }
    this.mClassLoader = localClassLoader;
  }
  
  public static Resources getSystem()
  {
    synchronized (sSync)
    {
      Resources localResources2 = mSystem;
      Resources localResources1 = localResources2;
      if (localResources2 == null)
      {
        localResources1 = new Resources();
        mSystem = localResources1;
      }
      return localResources1;
    }
  }
  
  public static TypedArray obtainAttributes(Resources paramResources, Theme paramTheme, AttributeSet paramAttributeSet, int[] paramArrayOfInt)
  {
    if (paramTheme == null) {
      return paramResources.obtainAttributes(paramAttributeSet, paramArrayOfInt);
    }
    return paramTheme.obtainStyledAttributes(paramAttributeSet, paramArrayOfInt, 0, 0);
  }
  
  private TypedValue obtainTempTypedValue()
  {
    TypedValue localTypedValue1 = null;
    synchronized (this.mTmpValueLock)
    {
      if (this.mTmpValue != null)
      {
        localTypedValue1 = this.mTmpValue;
        this.mTmpValue = null;
      }
      if (localTypedValue1 == null) {
        return new TypedValue();
      }
    }
    return localTypedValue2;
  }
  
  private void releaseTempTypedValue(TypedValue paramTypedValue)
  {
    synchronized (this.mTmpValueLock)
    {
      if (this.mTmpValue == null) {
        this.mTmpValue = paramTypedValue;
      }
      return;
    }
  }
  
  public static boolean resourceHasPackage(int paramInt)
  {
    boolean bool = false;
    if (paramInt >>> 24 != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static int selectDefaultTheme(int paramInt1, int paramInt2)
  {
    return selectSystemTheme(paramInt1, paramInt2, 16973829, 16973931, 16974120, 16974143);
  }
  
  public static int selectSystemTheme(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if (paramInt1 != 0) {
      return paramInt1;
    }
    if (paramInt2 < 11) {
      return paramInt3;
    }
    if (paramInt2 < 14) {
      return paramInt4;
    }
    if (paramInt2 < 24) {
      return paramInt5;
    }
    return paramInt6;
  }
  
  public static void updateSystemConfiguration(Configuration paramConfiguration, DisplayMetrics paramDisplayMetrics, CompatibilityInfo paramCompatibilityInfo)
  {
    if (mSystem != null) {
      mSystem.updateConfiguration(paramConfiguration, paramDisplayMetrics, paramCompatibilityInfo);
    }
  }
  
  public int calcConfigChanges(Configuration paramConfiguration)
  {
    return this.mResourcesImpl.calcConfigChanges(paramConfiguration);
  }
  
  public final void finishPreloading()
  {
    this.mResourcesImpl.finishPreloading();
  }
  
  public final void flushLayoutCache()
  {
    this.mResourcesImpl.flushLayoutCache();
  }
  
  public XmlResourceParser getAnimation(int paramInt)
    throws Resources.NotFoundException
  {
    return loadXmlResourceParser(paramInt, "anim");
  }
  
  public ConfigurationBoundResourceCache<Animator> getAnimatorCache()
  {
    return this.mResourcesImpl.getAnimatorCache();
  }
  
  public final AssetManager getAssets()
  {
    return this.mResourcesImpl.getAssets();
  }
  
  public boolean getBoolean(int paramInt)
    throws Resources.NotFoundException
  {
    boolean bool = true;
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      this.mResourcesImpl.getValue(paramInt, localTypedValue, true);
      if ((localTypedValue.type >= 16) && (localTypedValue.type <= 31))
      {
        paramInt = localTypedValue.data;
        if (paramInt != 0) {}
        for (;;)
        {
          return bool;
          bool = false;
        }
      }
      throw new NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt) + " type #0x" + Integer.toHexString(localTypedValue.type) + " is not valid");
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public ClassLoader getClassLoader()
  {
    return this.mClassLoader;
  }
  
  @Deprecated
  public int getColor(int paramInt)
    throws Resources.NotFoundException
  {
    return getColor(paramInt, null);
  }
  
  public int getColor(int paramInt, Theme paramTheme)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    ResourcesImpl localResourcesImpl;
    try
    {
      localResourcesImpl = this.mResourcesImpl;
      localResourcesImpl.getValue(paramInt, localTypedValue, true);
      if ((localTypedValue.type >= 16) && (localTypedValue.type <= 31))
      {
        paramInt = localTypedValue.data;
        return paramInt;
      }
      if (localTypedValue.type != 3) {
        throw new NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt) + " type #0x" + Integer.toHexString(localTypedValue.type) + " is not valid");
      }
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
    paramInt = localResourcesImpl.loadColorStateList(this, localTypedValue, paramInt, paramTheme).getDefaultColor();
    releaseTempTypedValue(localTypedValue);
    return paramInt;
  }
  
  @Deprecated
  public ColorStateList getColorStateList(int paramInt)
    throws Resources.NotFoundException
  {
    ColorStateList localColorStateList = getColorStateList(paramInt, null);
    if ((localColorStateList != null) && (localColorStateList.canApplyTheme())) {
      Log.w("Resources", "ColorStateList " + getResourceName(paramInt) + " has " + "unresolved theme attributes! Consider using " + "Resources.getColorStateList(int, Theme) or " + "Context.getColorStateList(int).", new RuntimeException());
    }
    return localColorStateList;
  }
  
  public ColorStateList getColorStateList(int paramInt, Theme paramTheme)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      ResourcesImpl localResourcesImpl = this.mResourcesImpl;
      localResourcesImpl.getValue(paramInt, localTypedValue, true);
      paramTheme = localResourcesImpl.loadColorStateList(this, localTypedValue, paramInt, paramTheme);
      return paramTheme;
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public CompatibilityInfo getCompatibilityInfo()
  {
    return this.mResourcesImpl.getCompatibilityInfo();
  }
  
  public Configuration getConfiguration()
  {
    return this.mResourcesImpl.getConfiguration();
  }
  
  public float getDimension(int paramInt)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      ResourcesImpl localResourcesImpl = this.mResourcesImpl;
      localResourcesImpl.getValue(paramInt, localTypedValue, true);
      if (localTypedValue.type == 5)
      {
        float f = TypedValue.complexToDimension(localTypedValue.data, localResourcesImpl.getDisplayMetrics());
        return f;
      }
      throw new NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt) + " type #0x" + Integer.toHexString(localTypedValue.type) + " is not valid");
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public int getDimensionPixelOffset(int paramInt)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      ResourcesImpl localResourcesImpl = this.mResourcesImpl;
      localResourcesImpl.getValue(paramInt, localTypedValue, true);
      if (localTypedValue.type == 5)
      {
        paramInt = TypedValue.complexToDimensionPixelOffset(localTypedValue.data, localResourcesImpl.getDisplayMetrics());
        return paramInt;
      }
      throw new NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt) + " type #0x" + Integer.toHexString(localTypedValue.type) + " is not valid");
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public int getDimensionPixelSize(int paramInt)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      ResourcesImpl localResourcesImpl = this.mResourcesImpl;
      localResourcesImpl.getValue(paramInt, localTypedValue, true);
      if (localTypedValue.type == 5)
      {
        paramInt = TypedValue.complexToDimensionPixelSize(localTypedValue.data, localResourcesImpl.getDisplayMetrics());
        return paramInt;
      }
      throw new NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt) + " type #0x" + Integer.toHexString(localTypedValue.type) + " is not valid");
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public DisplayAdjustments getDisplayAdjustments()
  {
    return this.mResourcesImpl.getDisplayAdjustments();
  }
  
  public DisplayMetrics getDisplayMetrics()
  {
    return this.mResourcesImpl.getDisplayMetrics();
  }
  
  @Deprecated
  public Drawable getDrawable(int paramInt)
    throws Resources.NotFoundException
  {
    Drawable localDrawable = getDrawable(paramInt, null);
    if ((localDrawable != null) && (localDrawable.canApplyTheme())) {
      Log.w("Resources", "Drawable " + getResourceName(paramInt) + " has unresolved theme " + "attributes! Consider using Resources.getDrawable(int, Theme) or " + "Context.getDrawable(int).", new RuntimeException());
    }
    return localDrawable;
  }
  
  public Drawable getDrawable(int paramInt, Theme paramTheme)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      ResourcesImpl localResourcesImpl = this.mResourcesImpl;
      localResourcesImpl.getValue(paramInt, localTypedValue, true);
      paramTheme = localResourcesImpl.loadDrawable(this, localTypedValue, paramInt, paramTheme, true);
      return paramTheme;
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  @Deprecated
  public Drawable getDrawableForDensity(int paramInt1, int paramInt2)
    throws Resources.NotFoundException
  {
    return getDrawableForDensity(paramInt1, paramInt2, null);
  }
  
  /* Error */
  public Drawable getDrawableForDensity(int paramInt1, int paramInt2, Theme paramTheme)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 168	android/content/res/Resources:obtainTempTypedValue	()Landroid/util/TypedValue;
    //   4: astore 5
    //   6: aload_0
    //   7: getfield 75	android/content/res/Resources:mResourcesImpl	Landroid/content/res/ResourcesImpl;
    //   10: astore 6
    //   12: aload 6
    //   14: iload_1
    //   15: iload_2
    //   16: aload 5
    //   18: iconst_1
    //   19: invokevirtual 314	android/content/res/ResourcesImpl:getValueForDensity	(IILandroid/util/TypedValue;Z)V
    //   22: aload 6
    //   24: invokevirtual 268	android/content/res/ResourcesImpl:getDisplayMetrics	()Landroid/util/DisplayMetrics;
    //   27: astore 7
    //   29: aload 5
    //   31: getfield 317	android/util/TypedValue:density	I
    //   34: aload 7
    //   36: getfield 320	android/util/DisplayMetrics:densityDpi	I
    //   39: if_icmpne +65 -> 104
    //   42: iconst_1
    //   43: istore 4
    //   45: aload 5
    //   47: getfield 317	android/util/TypedValue:density	I
    //   50: ifle +33 -> 83
    //   53: aload 5
    //   55: getfield 317	android/util/TypedValue:density	I
    //   58: ldc_w 321
    //   61: if_icmpeq +22 -> 83
    //   64: aload 5
    //   66: getfield 317	android/util/TypedValue:density	I
    //   69: iload_2
    //   70: if_icmpne +40 -> 110
    //   73: aload 5
    //   75: aload 7
    //   77: getfield 320	android/util/DisplayMetrics:densityDpi	I
    //   80: putfield 317	android/util/TypedValue:density	I
    //   83: aload 6
    //   85: aload_0
    //   86: aload 5
    //   88: iload_1
    //   89: aload_3
    //   90: iload 4
    //   92: invokevirtual 305	android/content/res/ResourcesImpl:loadDrawable	(Landroid/content/res/Resources;Landroid/util/TypedValue;ILandroid/content/res/Resources$Theme;Z)Landroid/graphics/drawable/Drawable;
    //   95: astore_3
    //   96: aload_0
    //   97: aload 5
    //   99: invokespecial 181	android/content/res/Resources:releaseTempTypedValue	(Landroid/util/TypedValue;)V
    //   102: aload_3
    //   103: areturn
    //   104: iconst_0
    //   105: istore 4
    //   107: goto -62 -> 45
    //   110: aload 5
    //   112: aload 5
    //   114: getfield 317	android/util/TypedValue:density	I
    //   117: aload 7
    //   119: getfield 320	android/util/DisplayMetrics:densityDpi	I
    //   122: imul
    //   123: iload_2
    //   124: idiv
    //   125: putfield 317	android/util/TypedValue:density	I
    //   128: goto -45 -> 83
    //   131: astore_3
    //   132: aload_0
    //   133: aload 5
    //   135: invokespecial 181	android/content/res/Resources:releaseTempTypedValue	(Landroid/util/TypedValue;)V
    //   138: aload_3
    //   139: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	140	0	this	Resources
    //   0	140	1	paramInt1	int
    //   0	140	2	paramInt2	int
    //   0	140	3	paramTheme	Theme
    //   43	63	4	bool	boolean
    //   4	130	5	localTypedValue	TypedValue
    //   10	74	6	localResourcesImpl	ResourcesImpl
    //   27	91	7	localDisplayMetrics	DisplayMetrics
    // Exception table:
    //   from	to	target	type
    //   6	42	131	finally
    //   45	83	131	finally
    //   83	96	131	finally
    //   110	128	131	finally
  }
  
  public final DrawableInflater getDrawableInflater()
  {
    if (this.mDrawableInflater == null) {
      this.mDrawableInflater = new DrawableInflater(this, this.mClassLoader);
    }
    return this.mDrawableInflater;
  }
  
  public float getFloat(int paramInt)
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      this.mResourcesImpl.getValue(paramInt, localTypedValue, true);
      if (localTypedValue.type == 4)
      {
        float f = localTypedValue.getFloat();
        return f;
      }
      throw new NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt) + " type #0x" + Integer.toHexString(localTypedValue.type) + " is not valid");
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public float getFraction(int paramInt1, int paramInt2, int paramInt3)
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      this.mResourcesImpl.getValue(paramInt1, localTypedValue, true);
      if (localTypedValue.type == 6)
      {
        float f = TypedValue.complexToFraction(localTypedValue.data, paramInt2, paramInt3);
        return f;
      }
      throw new NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt1) + " type #0x" + Integer.toHexString(localTypedValue.type) + " is not valid");
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public int getIdentifier(String paramString1, String paramString2, String paramString3)
  {
    return this.mResourcesImpl.getIdentifier(paramString1, paramString2, paramString3);
  }
  
  public ResourcesImpl getImpl()
  {
    return this.mResourcesImpl;
  }
  
  public int[] getIntArray(int paramInt)
    throws Resources.NotFoundException
  {
    int[] arrayOfInt = this.mResourcesImpl.getAssets().getArrayIntResource(paramInt);
    if (arrayOfInt != null) {
      return arrayOfInt;
    }
    throw new NotFoundException("Int array resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  public int getInteger(int paramInt)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      this.mResourcesImpl.getValue(paramInt, localTypedValue, true);
      if ((localTypedValue.type >= 16) && (localTypedValue.type <= 31))
      {
        paramInt = localTypedValue.data;
        return paramInt;
      }
      throw new NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt) + " type #0x" + Integer.toHexString(localTypedValue.type) + " is not valid");
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public XmlResourceParser getLayout(int paramInt)
    throws Resources.NotFoundException
  {
    return loadXmlResourceParser(paramInt, "layout");
  }
  
  public Movie getMovie(int paramInt)
    throws Resources.NotFoundException
  {
    InputStream localInputStream = openRawResource(paramInt);
    Movie localMovie = Movie.decodeStream(localInputStream);
    try
    {
      localInputStream.close();
      return localMovie;
    }
    catch (IOException localIOException) {}
    return localMovie;
  }
  
  public LongSparseArray<Drawable.ConstantState> getPreloadedDrawables()
  {
    return this.mResourcesImpl.getPreloadedDrawables();
  }
  
  public String getQuantityString(int paramInt1, int paramInt2)
    throws Resources.NotFoundException
  {
    return getQuantityText(paramInt1, paramInt2).toString();
  }
  
  public String getQuantityString(int paramInt1, int paramInt2, Object... paramVarArgs)
    throws Resources.NotFoundException
  {
    String str = getQuantityText(paramInt1, paramInt2).toString();
    return String.format(this.mResourcesImpl.getConfiguration().getLocales().get(0), str, paramVarArgs);
  }
  
  public CharSequence getQuantityText(int paramInt1, int paramInt2)
    throws Resources.NotFoundException
  {
    return this.mResourcesImpl.getQuantityText(paramInt1, paramInt2);
  }
  
  public String getResourceEntryName(int paramInt)
    throws Resources.NotFoundException
  {
    return this.mResourcesImpl.getResourceEntryName(paramInt);
  }
  
  public String getResourceName(int paramInt)
    throws Resources.NotFoundException
  {
    return this.mResourcesImpl.getResourceName(paramInt);
  }
  
  public String getResourcePackageName(int paramInt)
    throws Resources.NotFoundException
  {
    return this.mResourcesImpl.getResourcePackageName(paramInt);
  }
  
  public String getResourceTypeName(int paramInt)
    throws Resources.NotFoundException
  {
    return this.mResourcesImpl.getResourceTypeName(paramInt);
  }
  
  public Configuration[] getSizeConfigurations()
  {
    return this.mResourcesImpl.getSizeConfigurations();
  }
  
  public ConfigurationBoundResourceCache<StateListAnimator> getStateListAnimatorCache()
  {
    return this.mResourcesImpl.getStateListAnimatorCache();
  }
  
  public String getString(int paramInt)
    throws Resources.NotFoundException
  {
    return getText(paramInt).toString();
  }
  
  public String getString(int paramInt, Object... paramVarArgs)
    throws Resources.NotFoundException
  {
    String str = getString(paramInt);
    return String.format(this.mResourcesImpl.getConfiguration().getLocales().get(0), str, paramVarArgs);
  }
  
  public String[] getStringArray(int paramInt)
    throws Resources.NotFoundException
  {
    String[] arrayOfString = this.mResourcesImpl.getAssets().getResourceStringArray(paramInt);
    if (arrayOfString != null) {
      return arrayOfString;
    }
    throw new NotFoundException("String array resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  public CharSequence getText(int paramInt)
    throws Resources.NotFoundException
  {
    CharSequence localCharSequence = this.mResourcesImpl.getAssets().getResourceText(paramInt);
    if (localCharSequence != null) {
      return localCharSequence;
    }
    throw new NotFoundException("String resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  public CharSequence getText(int paramInt, CharSequence paramCharSequence)
  {
    CharSequence localCharSequence = null;
    if (paramInt != 0) {
      localCharSequence = this.mResourcesImpl.getAssets().getResourceText(paramInt);
    }
    if (localCharSequence != null) {
      return localCharSequence;
    }
    return paramCharSequence;
  }
  
  public CharSequence[] getTextArray(int paramInt)
    throws Resources.NotFoundException
  {
    CharSequence[] arrayOfCharSequence = this.mResourcesImpl.getAssets().getResourceTextArray(paramInt);
    if (arrayOfCharSequence != null) {
      return arrayOfCharSequence;
    }
    throw new NotFoundException("Text array resource ID #0x" + Integer.toHexString(paramInt));
  }
  
  public void getValue(int paramInt, TypedValue paramTypedValue, boolean paramBoolean)
    throws Resources.NotFoundException
  {
    this.mResourcesImpl.getValue(paramInt, paramTypedValue, paramBoolean);
  }
  
  public void getValue(String paramString, TypedValue paramTypedValue, boolean paramBoolean)
    throws Resources.NotFoundException
  {
    this.mResourcesImpl.getValue(paramString, paramTypedValue, paramBoolean);
  }
  
  public void getValueForDensity(int paramInt1, int paramInt2, TypedValue paramTypedValue, boolean paramBoolean)
    throws Resources.NotFoundException
  {
    this.mResourcesImpl.getValueForDensity(paramInt1, paramInt2, paramTypedValue, paramBoolean);
  }
  
  public XmlResourceParser getXml(int paramInt)
    throws Resources.NotFoundException
  {
    return loadXmlResourceParser(paramInt, "xml");
  }
  
  ColorStateList loadColorStateList(TypedValue paramTypedValue, int paramInt, Theme paramTheme)
    throws Resources.NotFoundException
  {
    return this.mResourcesImpl.loadColorStateList(this, paramTypedValue, paramInt, paramTheme);
  }
  
  public ComplexColor loadComplexColor(TypedValue paramTypedValue, int paramInt, Theme paramTheme)
  {
    return this.mResourcesImpl.loadComplexColor(this, paramTypedValue, paramInt, paramTheme);
  }
  
  Drawable loadDrawable(TypedValue paramTypedValue, int paramInt, Theme paramTheme)
    throws Resources.NotFoundException
  {
    return this.mResourcesImpl.loadDrawable(this, paramTypedValue, paramInt, paramTheme, true);
  }
  
  XmlResourceParser loadXmlResourceParser(int paramInt, String paramString)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      ResourcesImpl localResourcesImpl = this.mResourcesImpl;
      localResourcesImpl.getValue(paramInt, localTypedValue, true);
      if (localTypedValue.type == 3)
      {
        paramString = localResourcesImpl.loadXmlResourceParser(localTypedValue.string.toString(), paramInt, localTypedValue.assetCookie, paramString);
        return paramString;
      }
      throw new NotFoundException("Resource ID #0x" + Integer.toHexString(paramInt) + " type #0x" + Integer.toHexString(localTypedValue.type) + " is not valid");
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  XmlResourceParser loadXmlResourceParser(String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws Resources.NotFoundException
  {
    return this.mResourcesImpl.loadXmlResourceParser(paramString1, paramInt1, paramInt2, paramString2);
  }
  
  public final Theme newTheme()
  {
    Theme localTheme = new Theme(null);
    localTheme.setImpl(this.mResourcesImpl.newThemeImpl());
    this.mThemeRefs.add(new WeakReference(localTheme));
    return localTheme;
  }
  
  public TypedArray obtainAttributes(AttributeSet paramAttributeSet, int[] paramArrayOfInt)
  {
    TypedArray localTypedArray = TypedArray.obtain(this, paramArrayOfInt.length);
    paramAttributeSet = (XmlBlock.Parser)paramAttributeSet;
    this.mResourcesImpl.getAssets().retrieveAttributes(paramAttributeSet.mParseState, paramArrayOfInt, localTypedArray.mData, localTypedArray.mIndices);
    localTypedArray.mXml = paramAttributeSet;
    return localTypedArray;
  }
  
  public TypedArray obtainTypedArray(int paramInt)
    throws Resources.NotFoundException
  {
    ResourcesImpl localResourcesImpl = this.mResourcesImpl;
    int i = localResourcesImpl.getAssets().getArraySize(paramInt);
    if (i < 0) {
      throw new NotFoundException("Array resource ID #0x" + Integer.toHexString(paramInt));
    }
    TypedArray localTypedArray = TypedArray.obtain(this, i);
    localTypedArray.mLength = localResourcesImpl.getAssets().retrieveArray(paramInt, localTypedArray.mData);
    localTypedArray.mIndices[0] = 0;
    return localTypedArray;
  }
  
  public InputStream openRawResource(int paramInt)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      InputStream localInputStream = openRawResource(paramInt, localTypedValue);
      return localInputStream;
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public InputStream openRawResource(int paramInt, TypedValue paramTypedValue)
    throws Resources.NotFoundException
  {
    return this.mResourcesImpl.openRawResource(paramInt, paramTypedValue);
  }
  
  public AssetFileDescriptor openRawResourceFd(int paramInt)
    throws Resources.NotFoundException
  {
    TypedValue localTypedValue = obtainTempTypedValue();
    try
    {
      AssetFileDescriptor localAssetFileDescriptor = this.mResourcesImpl.openRawResourceFd(paramInt, localTypedValue);
      return localAssetFileDescriptor;
    }
    finally
    {
      releaseTempTypedValue(localTypedValue);
    }
  }
  
  public void parseBundleExtra(String paramString, AttributeSet paramAttributeSet, Bundle paramBundle)
    throws XmlPullParserException
  {
    boolean bool = true;
    TypedArray localTypedArray = obtainAttributes(paramAttributeSet, R.styleable.Extra);
    String str = localTypedArray.getString(0);
    if (str == null)
    {
      localTypedArray.recycle();
      throw new XmlPullParserException("<" + paramString + "> requires an android:name attribute at " + paramAttributeSet.getPositionDescription());
    }
    TypedValue localTypedValue = localTypedArray.peekValue(1);
    if (localTypedValue != null)
    {
      if (localTypedValue.type == 3) {
        paramBundle.putCharSequence(str, localTypedValue.coerceToString());
      }
      for (;;)
      {
        localTypedArray.recycle();
        return;
        if (localTypedValue.type == 18)
        {
          if (localTypedValue.data != 0) {}
          for (;;)
          {
            paramBundle.putBoolean(str, bool);
            break;
            bool = false;
          }
        }
        if ((localTypedValue.type >= 16) && (localTypedValue.type <= 31))
        {
          paramBundle.putInt(str, localTypedValue.data);
        }
        else
        {
          if (localTypedValue.type != 4) {
            break;
          }
          paramBundle.putFloat(str, localTypedValue.getFloat());
        }
      }
      localTypedArray.recycle();
      throw new XmlPullParserException("<" + paramString + "> only supports string, integer, float, color, and boolean at " + paramAttributeSet.getPositionDescription());
    }
    localTypedArray.recycle();
    throw new XmlPullParserException("<" + paramString + "> requires an android:value or android:resource attribute at " + paramAttributeSet.getPositionDescription());
  }
  
  public void parseBundleExtras(XmlResourceParser paramXmlResourceParser, Bundle paramBundle)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlResourceParser.getDepth();
    for (;;)
    {
      int j = paramXmlResourceParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlResourceParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if (paramXmlResourceParser.getName().equals("extra"))
        {
          parseBundleExtra("extra", paramXmlResourceParser, paramBundle);
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
        else
        {
          XmlUtils.skipCurrentTag(paramXmlResourceParser);
        }
      }
    }
  }
  
  public void setCompatibilityInfo(CompatibilityInfo paramCompatibilityInfo)
  {
    if (paramCompatibilityInfo != null) {
      this.mResourcesImpl.updateConfiguration(null, null, paramCompatibilityInfo);
    }
  }
  
  public void setImpl(ResourcesImpl paramResourcesImpl)
  {
    if (paramResourcesImpl == this.mResourcesImpl) {
      return;
    }
    this.mResourcesImpl = paramResourcesImpl;
    synchronized (this.mThemeRefs)
    {
      int j = this.mThemeRefs.size();
      int i = 0;
      if (i < j)
      {
        paramResourcesImpl = (WeakReference)this.mThemeRefs.get(i);
        if (paramResourcesImpl != null) {}
        for (paramResourcesImpl = (Theme)paramResourcesImpl.get();; paramResourcesImpl = null)
        {
          if (paramResourcesImpl != null) {
            paramResourcesImpl.setImpl(this.mResourcesImpl.newThemeImpl(paramResourcesImpl.getKey()));
          }
          i += 1;
          break;
        }
      }
      return;
    }
  }
  
  public final void startPreloading()
  {
    this.mResourcesImpl.startPreloading();
  }
  
  public void switchResources(Resources paramResources)
  {
    this.mResourcesImpl = paramResources.mResourcesImpl;
  }
  
  @Deprecated
  public void updateConfiguration(Configuration paramConfiguration, DisplayMetrics paramDisplayMetrics)
  {
    updateConfiguration(paramConfiguration, paramDisplayMetrics, null);
  }
  
  public void updateConfiguration(Configuration paramConfiguration, DisplayMetrics paramDisplayMetrics, CompatibilityInfo paramCompatibilityInfo)
  {
    this.mResourcesImpl.updateConfiguration(paramConfiguration, paramDisplayMetrics, paramCompatibilityInfo);
  }
  
  public static class NotFoundException
    extends RuntimeException
  {
    public NotFoundException() {}
    
    public NotFoundException(String paramString)
    {
      super();
    }
    
    public NotFoundException(String paramString, Exception paramException)
    {
      super(paramException);
    }
  }
  
  public final class Theme
  {
    private ResourcesImpl.ThemeImpl mThemeImpl;
    
    private Theme() {}
    
    private String getResourceNameFromHexString(String paramString)
    {
      return Resources.this.getResourceName(Integer.parseInt(paramString, 16));
    }
    
    public void applyStyle(int paramInt, boolean paramBoolean)
    {
      this.mThemeImpl.applyStyle(paramInt, paramBoolean);
    }
    
    public void dump(int paramInt, String paramString1, String paramString2)
    {
      this.mThemeImpl.dump(paramInt, paramString1, paramString2);
    }
    
    public void encode(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      paramViewHierarchyEncoder.beginObject(this);
      String[] arrayOfString = getTheme();
      int i = 0;
      while (i < arrayOfString.length)
      {
        paramViewHierarchyEncoder.addProperty(arrayOfString[i], arrayOfString[(i + 1)]);
        i += 2;
      }
      paramViewHierarchyEncoder.endObject();
    }
    
    public int[] getAllAttributes()
    {
      return this.mThemeImpl.getAllAttributes();
    }
    
    int getAppliedStyleResId()
    {
      return this.mThemeImpl.getAppliedStyleResId();
    }
    
    public int getChangingConfigurations()
    {
      return this.mThemeImpl.getChangingConfigurations();
    }
    
    public Drawable getDrawable(int paramInt)
      throws Resources.NotFoundException
    {
      return Resources.this.getDrawable(paramInt, this);
    }
    
    public Resources.ThemeKey getKey()
    {
      return this.mThemeImpl.getKey();
    }
    
    long getNativeTheme()
    {
      return this.mThemeImpl.getNativeTheme();
    }
    
    public Resources getResources()
    {
      return Resources.this;
    }
    
    @ViewDebug.ExportedProperty(category="theme", hasAdjacentMapping=true)
    public String[] getTheme()
    {
      return this.mThemeImpl.getTheme();
    }
    
    public TypedArray obtainStyledAttributes(int paramInt, int[] paramArrayOfInt)
      throws Resources.NotFoundException
    {
      return this.mThemeImpl.obtainStyledAttributes(this, null, paramArrayOfInt, 0, paramInt);
    }
    
    public TypedArray obtainStyledAttributes(AttributeSet paramAttributeSet, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    {
      return this.mThemeImpl.obtainStyledAttributes(this, paramAttributeSet, paramArrayOfInt, paramInt1, paramInt2);
    }
    
    public TypedArray obtainStyledAttributes(int[] paramArrayOfInt)
    {
      return this.mThemeImpl.obtainStyledAttributes(this, null, paramArrayOfInt, 0, 0);
    }
    
    public void rebase()
    {
      this.mThemeImpl.rebase();
    }
    
    public boolean resolveAttribute(int paramInt, TypedValue paramTypedValue, boolean paramBoolean)
    {
      return this.mThemeImpl.resolveAttribute(paramInt, paramTypedValue, paramBoolean);
    }
    
    public TypedArray resolveAttributes(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      return this.mThemeImpl.resolveAttributes(this, paramArrayOfInt1, paramArrayOfInt2);
    }
    
    void setImpl(ResourcesImpl.ThemeImpl paramThemeImpl)
    {
      this.mThemeImpl = paramThemeImpl;
    }
    
    public void setTo(Theme paramTheme)
    {
      this.mThemeImpl.setTo(paramTheme.mThemeImpl);
    }
  }
  
  static class ThemeKey
    implements Cloneable
  {
    int mCount;
    boolean[] mForce;
    private int mHashCode = 0;
    int[] mResId;
    
    public void append(int paramInt, boolean paramBoolean)
    {
      if (this.mResId == null) {
        this.mResId = new int[4];
      }
      if (this.mForce == null) {
        this.mForce = new boolean[4];
      }
      this.mResId = GrowingArrayUtils.append(this.mResId, this.mCount, paramInt);
      this.mForce = GrowingArrayUtils.append(this.mForce, this.mCount, paramBoolean);
      this.mCount += 1;
      int j = this.mHashCode;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        this.mHashCode = (i + (j * 31 + paramInt) * 31);
        return;
      }
    }
    
    public ThemeKey clone()
    {
      ThemeKey localThemeKey = new ThemeKey();
      localThemeKey.mResId = this.mResId;
      localThemeKey.mForce = this.mForce;
      localThemeKey.mCount = this.mCount;
      localThemeKey.mHashCode = this.mHashCode;
      return localThemeKey;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {}
      while (hashCode() != paramObject.hashCode()) {
        return false;
      }
      paramObject = (ThemeKey)paramObject;
      if (this.mCount != ((ThemeKey)paramObject).mCount) {
        return false;
      }
      int j = this.mCount;
      int i = 0;
      while (i < j)
      {
        if ((this.mResId[i] != paramObject.mResId[i]) || (this.mForce[i] != paramObject.mForce[i])) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    
    public int hashCode()
    {
      return this.mHashCode;
    }
    
    public void setTo(ThemeKey paramThemeKey)
    {
      Object localObject2 = null;
      if (paramThemeKey.mResId == null)
      {
        localObject1 = null;
        this.mResId = ((int[])localObject1);
        if (paramThemeKey.mForce != null) {
          break label53;
        }
      }
      label53:
      for (Object localObject1 = localObject2;; localObject1 = (boolean[])paramThemeKey.mForce.clone())
      {
        this.mForce = ((boolean[])localObject1);
        this.mCount = paramThemeKey.mCount;
        return;
        localObject1 = (int[])paramThemeKey.mResId.clone();
        break;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/Resources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */