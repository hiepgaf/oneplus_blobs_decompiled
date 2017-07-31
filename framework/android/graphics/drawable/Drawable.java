package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.NinePatch;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class Drawable
{
  static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;
  private static final Rect ZERO_BOUNDS_RECT = new Rect();
  private Rect mBounds = ZERO_BOUNDS_RECT;
  private WeakReference<Callback> mCallback = null;
  private int mChangingConfigurations = 0;
  private int mLayoutDirection;
  private int mLevel = 0;
  private int[] mStateSet = StateSet.WILD_CARD;
  private boolean mVisible = true;
  
  public static Drawable createFromPath(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    Trace.traceBegin(8192L, paramString);
    try
    {
      Bitmap localBitmap = BitmapFactory.decodeFile(paramString);
      if (localBitmap != null)
      {
        paramString = drawableFromBitmap(null, localBitmap, null, null, null, paramString);
        return paramString;
      }
      return null;
    }
    finally
    {
      Trace.traceEnd(8192L);
    }
  }
  
  public static Drawable createFromResourceStream(Resources paramResources, TypedValue paramTypedValue, InputStream paramInputStream, String paramString)
  {
    if (paramString != null) {}
    for (String str = paramString;; str = "Unknown drawable")
    {
      Trace.traceBegin(8192L, str);
      try
      {
        paramResources = createFromResourceStream(paramResources, paramTypedValue, paramInputStream, paramString, null);
        return paramResources;
      }
      finally
      {
        Trace.traceEnd(8192L);
      }
    }
  }
  
  public static Drawable createFromResourceStream(Resources paramResources, TypedValue paramTypedValue, InputStream paramInputStream, String paramString, BitmapFactory.Options paramOptions)
  {
    if (paramInputStream == null) {
      return null;
    }
    Rect localRect = new Rect();
    Object localObject = paramOptions;
    if (paramOptions == null) {
      localObject = new BitmapFactory.Options();
    }
    ((BitmapFactory.Options)localObject).inScreenDensity = resolveDensity(paramResources, 0);
    paramOptions = BitmapFactory.decodeResourceStream(paramResources, paramTypedValue, paramInputStream, localRect, (BitmapFactory.Options)localObject);
    if (paramOptions != null)
    {
      paramTypedValue = paramOptions.getNinePatchChunk();
      if ((paramTypedValue != null) && (NinePatch.isNinePatchChunk(paramTypedValue))) {}
      for (paramInputStream = localRect;; paramInputStream = null)
      {
        localObject = new Rect();
        paramOptions.getOpticalInsets((Rect)localObject);
        return drawableFromBitmap(paramResources, paramOptions, paramTypedValue, paramInputStream, (Rect)localObject, paramString);
        paramTypedValue = null;
      }
    }
    return null;
  }
  
  public static Drawable createFromStream(InputStream paramInputStream, String paramString)
  {
    if (paramString != null) {}
    for (String str = paramString;; str = "Unknown drawable")
    {
      Trace.traceBegin(8192L, str);
      try
      {
        paramInputStream = createFromResourceStream(null, null, paramInputStream, paramString);
        return paramInputStream;
      }
      finally
      {
        Trace.traceEnd(8192L);
      }
    }
  }
  
  public static Drawable createFromXml(Resources paramResources, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    return createFromXml(paramResources, paramXmlPullParser, null);
  }
  
  public static Drawable createFromXml(Resources paramResources, XmlPullParser paramXmlPullParser, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    AttributeSet localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
    int i;
    do
    {
      i = paramXmlPullParser.next();
    } while ((i != 2) && (i != 1));
    if (i != 2) {
      throw new XmlPullParserException("No start tag found");
    }
    paramResources = createFromXmlInner(paramResources, paramXmlPullParser, localAttributeSet, paramTheme);
    if (paramResources == null) {
      throw new RuntimeException("Unknown initial tag: " + paramXmlPullParser.getName());
    }
    return paramResources;
  }
  
  public static Drawable createFromXmlInner(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    return createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, null);
  }
  
  public static Drawable createFromXmlInner(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    return paramResources.getDrawableInflater().inflateFromXml(paramXmlPullParser.getName(), paramXmlPullParser, paramAttributeSet, paramTheme);
  }
  
  private static Drawable drawableFromBitmap(Resources paramResources, Bitmap paramBitmap, byte[] paramArrayOfByte, Rect paramRect1, Rect paramRect2, String paramString)
  {
    if (paramArrayOfByte != null) {
      return new NinePatchDrawable(paramResources, paramBitmap, paramArrayOfByte, paramRect1, paramRect2, paramString);
    }
    return new BitmapDrawable(paramResources, paramBitmap);
  }
  
  protected static TypedArray obtainAttributes(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, int[] paramArrayOfInt)
  {
    if (paramTheme == null) {
      return paramResources.obtainAttributes(paramAttributeSet, paramArrayOfInt);
    }
    return paramTheme.obtainStyledAttributes(paramAttributeSet, paramArrayOfInt, 0, 0);
  }
  
  public static PorterDuff.Mode parseTintMode(int paramInt, PorterDuff.Mode paramMode)
  {
    switch (paramInt)
    {
    case 4: 
    case 6: 
    case 7: 
    case 8: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    default: 
      return paramMode;
    case 3: 
      return PorterDuff.Mode.SRC_OVER;
    case 5: 
      return PorterDuff.Mode.SRC_IN;
    case 9: 
      return PorterDuff.Mode.SRC_ATOP;
    case 14: 
      return PorterDuff.Mode.MULTIPLY;
    case 15: 
      return PorterDuff.Mode.SCREEN;
    }
    return PorterDuff.Mode.ADD;
  }
  
  static int resolveDensity(Resources paramResources, int paramInt)
  {
    if (paramResources == null) {}
    for (;;)
    {
      int i = paramInt;
      if (paramInt == 0) {
        i = 160;
      }
      return i;
      paramInt = paramResources.getDisplayMetrics().densityDpi;
    }
  }
  
  public static int resolveOpacity(int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return paramInt1;
    }
    if ((paramInt1 == 0) || (paramInt2 == 0)) {
      return 0;
    }
    if ((paramInt1 == -3) || (paramInt2 == -3)) {
      return -3;
    }
    if ((paramInt1 == -2) || (paramInt2 == -2)) {
      return -2;
    }
    return -1;
  }
  
  static void rethrowAsRuntimeException(Exception paramException)
    throws RuntimeException
  {
    paramException = new RuntimeException(paramException);
    paramException.setStackTrace(new StackTraceElement[0]);
    throw paramException;
  }
  
  static float scaleFromDensity(float paramFloat, int paramInt1, int paramInt2)
  {
    return paramInt2 * paramFloat / paramInt1;
  }
  
  static int scaleFromDensity(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if ((paramInt1 == 0) || (paramInt2 == paramInt3)) {
      return paramInt1;
    }
    float f = paramInt1 * paramInt3 / paramInt2;
    if (!paramBoolean) {
      return (int)f;
    }
    paramInt2 = Math.round(f);
    if (paramInt2 != 0) {
      return paramInt2;
    }
    if (paramInt1 > 0) {
      return 1;
    }
    return -1;
  }
  
  public void applyTheme(Resources.Theme paramTheme) {}
  
  public boolean canApplyTheme()
  {
    return false;
  }
  
  public void clearColorFilter()
  {
    setColorFilter(null);
  }
  
  public void clearMutated() {}
  
  public final Rect copyBounds()
  {
    return new Rect(this.mBounds);
  }
  
  public final void copyBounds(Rect paramRect)
  {
    paramRect.set(this.mBounds);
  }
  
  public abstract void draw(Canvas paramCanvas);
  
  public int getAlpha()
  {
    return 255;
  }
  
  public final Rect getBounds()
  {
    if (this.mBounds == ZERO_BOUNDS_RECT) {
      this.mBounds = new Rect();
    }
    return this.mBounds;
  }
  
  public Callback getCallback()
  {
    Callback localCallback = null;
    if (this.mCallback != null) {
      localCallback = (Callback)this.mCallback.get();
    }
    return localCallback;
  }
  
  public int getChangingConfigurations()
  {
    return this.mChangingConfigurations;
  }
  
  public ColorFilter getColorFilter()
  {
    return null;
  }
  
  public ConstantState getConstantState()
  {
    return null;
  }
  
  public Drawable getCurrent()
  {
    return this;
  }
  
  public Rect getDirtyBounds()
  {
    return getBounds();
  }
  
  public void getHotspotBounds(Rect paramRect)
  {
    paramRect.set(getBounds());
  }
  
  public int getIntrinsicHeight()
  {
    return -1;
  }
  
  public int getIntrinsicWidth()
  {
    return -1;
  }
  
  public int getLayoutDirection()
  {
    return this.mLayoutDirection;
  }
  
  public final int getLevel()
  {
    return this.mLevel;
  }
  
  public int getMinimumHeight()
  {
    int i = getIntrinsicHeight();
    if (i > 0) {
      return i;
    }
    return 0;
  }
  
  public int getMinimumWidth()
  {
    int i = getIntrinsicWidth();
    if (i > 0) {
      return i;
    }
    return 0;
  }
  
  public abstract int getOpacity();
  
  public Insets getOpticalInsets()
  {
    return Insets.NONE;
  }
  
  public void getOutline(Outline paramOutline)
  {
    paramOutline.setRect(getBounds());
    paramOutline.setAlpha(0.0F);
  }
  
  public boolean getPadding(Rect paramRect)
  {
    paramRect.set(0, 0, 0, 0);
    return false;
  }
  
  public int[] getState()
  {
    return this.mStateSet;
  }
  
  public Region getTransparentRegion()
  {
    return null;
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    inflate(paramResources, paramXmlPullParser, paramAttributeSet, null);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    paramResources = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.Drawable);
    this.mVisible = paramResources.getBoolean(0, this.mVisible);
    paramResources.recycle();
  }
  
  void inflateWithAttributes(Resources paramResources, XmlPullParser paramXmlPullParser, TypedArray paramTypedArray, int paramInt)
    throws XmlPullParserException, IOException
  {
    this.mVisible = paramTypedArray.getBoolean(paramInt, this.mVisible);
  }
  
  public void invalidateSelf()
  {
    Callback localCallback = getCallback();
    if (localCallback != null) {
      localCallback.invalidateDrawable(this);
    }
  }
  
  public boolean isAutoMirrored()
  {
    return false;
  }
  
  public boolean isFilterBitmap()
  {
    return false;
  }
  
  public boolean isProjected()
  {
    return false;
  }
  
  public boolean isStateful()
  {
    return false;
  }
  
  public final boolean isVisible()
  {
    return this.mVisible;
  }
  
  public void jumpToCurrentState() {}
  
  public Drawable mutate()
  {
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect) {}
  
  public boolean onLayoutDirectionChanged(int paramInt)
  {
    return false;
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    return false;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    return false;
  }
  
  public void scheduleSelf(Runnable paramRunnable, long paramLong)
  {
    Callback localCallback = getCallback();
    if (localCallback != null) {
      localCallback.scheduleDrawable(this, paramRunnable, paramLong);
    }
  }
  
  public abstract void setAlpha(int paramInt);
  
  public void setAutoMirrored(boolean paramBoolean) {}
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rect localRect2 = this.mBounds;
    Rect localRect1 = localRect2;
    if (localRect2 == ZERO_BOUNDS_RECT)
    {
      localRect1 = new Rect();
      this.mBounds = localRect1;
    }
    if ((localRect1.left != paramInt1) || (localRect1.top != paramInt2)) {
      break label83;
    }
    for (;;)
    {
      if (!localRect1.isEmpty()) {
        invalidateSelf();
      }
      this.mBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
      onBoundsChange(this.mBounds);
      label83:
      return;
      if (localRect1.right == paramInt3) {
        if (localRect1.bottom == paramInt4) {
          break;
        }
      }
    }
  }
  
  public void setBounds(Rect paramRect)
  {
    setBounds(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public final void setCallback(Callback paramCallback)
  {
    WeakReference localWeakReference = null;
    if (paramCallback != null) {
      localWeakReference = new WeakReference(paramCallback);
    }
    this.mCallback = localWeakReference;
  }
  
  public void setChangingConfigurations(int paramInt)
  {
    this.mChangingConfigurations = paramInt;
  }
  
  public void setColorFilter(int paramInt, PorterDuff.Mode paramMode)
  {
    setColorFilter(new PorterDuffColorFilter(paramInt, paramMode));
  }
  
  public abstract void setColorFilter(ColorFilter paramColorFilter);
  
  @Deprecated
  public void setDither(boolean paramBoolean) {}
  
  public void setFilterBitmap(boolean paramBoolean) {}
  
  public void setHotspot(float paramFloat1, float paramFloat2) {}
  
  public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public final boolean setLayoutDirection(int paramInt)
  {
    if (this.mLayoutDirection != paramInt)
    {
      this.mLayoutDirection = paramInt;
      return onLayoutDirectionChanged(paramInt);
    }
    return false;
  }
  
  public final boolean setLevel(int paramInt)
  {
    if (this.mLevel != paramInt)
    {
      this.mLevel = paramInt;
      return onLevelChange(paramInt);
    }
    return false;
  }
  
  public boolean setState(int[] paramArrayOfInt)
  {
    if (!Arrays.equals(this.mStateSet, paramArrayOfInt))
    {
      this.mStateSet = paramArrayOfInt;
      return onStateChange(paramArrayOfInt);
    }
    return false;
  }
  
  public void setTint(int paramInt)
  {
    setTintList(ColorStateList.valueOf(paramInt));
  }
  
  public void setTintList(ColorStateList paramColorStateList) {}
  
  public void setTintMode(PorterDuff.Mode paramMode) {}
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mVisible != paramBoolean1) {}
    for (paramBoolean2 = true;; paramBoolean2 = false)
    {
      if (paramBoolean2)
      {
        this.mVisible = paramBoolean1;
        invalidateSelf();
      }
      return paramBoolean2;
    }
  }
  
  public void setXfermode(Xfermode paramXfermode) {}
  
  public void unscheduleSelf(Runnable paramRunnable)
  {
    Callback localCallback = getCallback();
    if (localCallback != null) {
      localCallback.unscheduleDrawable(this, paramRunnable);
    }
  }
  
  PorterDuffColorFilter updateTintFilter(PorterDuffColorFilter paramPorterDuffColorFilter, ColorStateList paramColorStateList, PorterDuff.Mode paramMode)
  {
    if ((paramColorStateList == null) || (paramMode == null)) {
      return null;
    }
    int i = paramColorStateList.getColorForState(getState(), 0);
    if (paramPorterDuffColorFilter == null) {
      return new PorterDuffColorFilter(i, paramMode);
    }
    paramPorterDuffColorFilter.setColor(i);
    paramPorterDuffColorFilter.setMode(paramMode);
    return paramPorterDuffColorFilter;
  }
  
  public static abstract interface Callback
  {
    public abstract void invalidateDrawable(Drawable paramDrawable);
    
    public abstract void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong);
    
    public abstract void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable);
  }
  
  public static abstract class ConstantState
  {
    public int addAtlasableBitmaps(Collection<Bitmap> paramCollection)
    {
      return 0;
    }
    
    public boolean canApplyTheme()
    {
      return false;
    }
    
    public abstract int getChangingConfigurations();
    
    protected final boolean isAtlasable(Bitmap paramBitmap)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (paramBitmap != null)
      {
        bool1 = bool2;
        if (paramBitmap.getConfig() == Bitmap.Config.ARGB_8888) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public abstract Drawable newDrawable();
    
    public Drawable newDrawable(Resources paramResources)
    {
      return newDrawable();
    }
    
    public Drawable newDrawable(Resources paramResources, Resources.Theme paramTheme)
    {
      return newDrawable(paramResources);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/Drawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */