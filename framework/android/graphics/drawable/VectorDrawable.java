package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.ComplexColor;
import android.content.res.GradientColor;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Log;
import android.util.PathParser.PathData;
import android.util.Property;
import android.util.Xml;
import com.android.internal.R.styleable;
import com.android.internal.util.VirtualRefBasePtr;
import dalvik.system.VMRuntime;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class VectorDrawable
  extends Drawable
{
  private static final String LOGTAG = VectorDrawable.class.getSimpleName();
  private static final String SHAPE_CLIP_PATH = "clip-path";
  private static final String SHAPE_GROUP = "group";
  private static final String SHAPE_PATH = "path";
  private static final String SHAPE_VECTOR = "vector";
  private ColorFilter mColorFilter;
  private boolean mDpiScaledDirty = true;
  private int mDpiScaledHeight = 0;
  private Insets mDpiScaledInsets = Insets.NONE;
  private int mDpiScaledWidth = 0;
  private boolean mMutated;
  private int mTargetDensity;
  private PorterDuffColorFilter mTintFilter;
  private final Rect mTmpBounds = new Rect();
  private VectorDrawableState mVectorState;
  
  public VectorDrawable()
  {
    this(new VectorDrawableState(null), null);
  }
  
  private VectorDrawable(VectorDrawableState paramVectorDrawableState, Resources paramResources)
  {
    this.mVectorState = paramVectorDrawableState;
    updateLocalState(paramResources);
  }
  
  public static VectorDrawable create(Resources paramResources, int paramInt)
  {
    try
    {
      localXmlResourceParser = paramResources.getXml(paramInt);
      localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      do
      {
        paramInt = localXmlResourceParser.next();
      } while ((paramInt != 2) && (paramInt != 1));
      if (paramInt != 2) {
        throw new XmlPullParserException("No start tag found");
      }
    }
    catch (XmlPullParserException paramResources)
    {
      XmlResourceParser localXmlResourceParser;
      AttributeSet localAttributeSet;
      Log.e(LOGTAG, "parser error", paramResources);
      return null;
      VectorDrawable localVectorDrawable = new VectorDrawable();
      localVectorDrawable.inflate(paramResources, localXmlResourceParser, localAttributeSet);
      return localVectorDrawable;
    }
    catch (IOException paramResources)
    {
      for (;;)
      {
        Log.e(LOGTAG, "parser error", paramResources);
      }
    }
  }
  
  private void inflateChildElements(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    VectorDrawableState localVectorDrawableState = this.mVectorState;
    int i = 1;
    Stack localStack = new Stack();
    localStack.push(localVectorDrawableState.mRootGroup);
    int k = paramXmlPullParser.getEventType();
    int m = paramXmlPullParser.getDepth();
    if ((k != 1) && ((paramXmlPullParser.getDepth() >= m + 1) || (k != 3)))
    {
      Object localObject;
      VGroup localVGroup;
      int j;
      if (k == 2)
      {
        localObject = paramXmlPullParser.getName();
        localVGroup = (VGroup)localStack.peek();
        if ("path".equals(localObject))
        {
          localObject = new VFullPath();
          ((VFullPath)localObject).inflate(paramResources, paramAttributeSet, paramTheme);
          localVGroup.addChild((VObject)localObject);
          if (((VFullPath)localObject).getPathName() != null) {
            localVectorDrawableState.mVGTargetsMap.put(((VFullPath)localObject).getPathName(), localObject);
          }
          j = 0;
          localVectorDrawableState.mChangingConfigurations |= ((VFullPath)localObject).mChangingConfigurations;
        }
      }
      for (;;)
      {
        k = paramXmlPullParser.next();
        i = j;
        break;
        if ("clip-path".equals(localObject))
        {
          localObject = new VClipPath();
          ((VClipPath)localObject).inflate(paramResources, paramAttributeSet, paramTheme);
          localVGroup.addChild((VObject)localObject);
          if (((VClipPath)localObject).getPathName() != null) {
            localVectorDrawableState.mVGTargetsMap.put(((VClipPath)localObject).getPathName(), localObject);
          }
          localVectorDrawableState.mChangingConfigurations |= ((VClipPath)localObject).mChangingConfigurations;
          j = i;
        }
        else
        {
          j = i;
          if ("group".equals(localObject))
          {
            localObject = new VGroup();
            ((VGroup)localObject).inflate(paramResources, paramAttributeSet, paramTheme);
            localVGroup.addChild((VObject)localObject);
            localStack.push(localObject);
            if (((VGroup)localObject).getGroupName() != null) {
              localVectorDrawableState.mVGTargetsMap.put(((VGroup)localObject).getGroupName(), localObject);
            }
            localVectorDrawableState.mChangingConfigurations |= VGroup.-get7((VGroup)localObject);
            j = i;
            continue;
            j = i;
            if (k == 3)
            {
              j = i;
              if ("group".equals(paramXmlPullParser.getName()))
              {
                localStack.pop();
                j = i;
              }
            }
          }
        }
      }
    }
    if (i != 0)
    {
      paramResources = new StringBuffer();
      if (paramResources.length() > 0) {
        paramResources.append(" or ");
      }
      paramResources.append("path");
      throw new XmlPullParserException("no " + paramResources + " defined");
    }
  }
  
  private static native void nAddChild(long paramLong1, long paramLong2);
  
  private static native long nCreateClipPath();
  
  private static native long nCreateClipPath(long paramLong);
  
  private static native long nCreateFullPath();
  
  private static native long nCreateFullPath(long paramLong);
  
  private static native long nCreateGroup();
  
  private static native long nCreateGroup(long paramLong);
  
  private static native long nCreateTree(long paramLong);
  
  private static native long nCreateTreeFromCopy(long paramLong1, long paramLong2);
  
  private static native int nDraw(long paramLong1, long paramLong2, long paramLong3, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2);
  
  private static native float nGetFillAlpha(long paramLong);
  
  private static native int nGetFillColor(long paramLong);
  
  private static native boolean nGetFullPathProperties(long paramLong, byte[] paramArrayOfByte, int paramInt);
  
  private static native boolean nGetGroupProperties(long paramLong, float[] paramArrayOfFloat, int paramInt);
  
  private static native float nGetPivotX(long paramLong);
  
  private static native float nGetPivotY(long paramLong);
  
  private static native float nGetRootAlpha(long paramLong);
  
  private static native float nGetRotation(long paramLong);
  
  private static native float nGetScaleX(long paramLong);
  
  private static native float nGetScaleY(long paramLong);
  
  private static native float nGetStrokeAlpha(long paramLong);
  
  private static native int nGetStrokeColor(long paramLong);
  
  private static native float nGetStrokeWidth(long paramLong);
  
  private static native float nGetTranslateX(long paramLong);
  
  private static native float nGetTranslateY(long paramLong);
  
  private static native float nGetTrimPathEnd(long paramLong);
  
  private static native float nGetTrimPathOffset(long paramLong);
  
  private static native float nGetTrimPathStart(long paramLong);
  
  private static native void nSetAllowCaching(long paramLong, boolean paramBoolean);
  
  private static native void nSetFillAlpha(long paramLong, float paramFloat);
  
  private static native void nSetFillColor(long paramLong, int paramInt);
  
  private static native void nSetName(long paramLong, String paramString);
  
  private static native void nSetPathData(long paramLong1, long paramLong2);
  
  private static native void nSetPathString(long paramLong, String paramString, int paramInt);
  
  private static native void nSetPivotX(long paramLong, float paramFloat);
  
  private static native void nSetPivotY(long paramLong, float paramFloat);
  
  private static native void nSetRendererViewportSize(long paramLong, float paramFloat1, float paramFloat2);
  
  private static native boolean nSetRootAlpha(long paramLong, float paramFloat);
  
  private static native void nSetRotation(long paramLong, float paramFloat);
  
  private static native void nSetScaleX(long paramLong, float paramFloat);
  
  private static native void nSetScaleY(long paramLong, float paramFloat);
  
  private static native void nSetStrokeAlpha(long paramLong, float paramFloat);
  
  private static native void nSetStrokeColor(long paramLong, int paramInt);
  
  private static native void nSetStrokeWidth(long paramLong, float paramFloat);
  
  private static native void nSetTranslateX(long paramLong, float paramFloat);
  
  private static native void nSetTranslateY(long paramLong, float paramFloat);
  
  private static native void nSetTrimPathEnd(long paramLong, float paramFloat);
  
  private static native void nSetTrimPathOffset(long paramLong, float paramFloat);
  
  private static native void nSetTrimPathStart(long paramLong, float paramFloat);
  
  private static native void nUpdateFullPathFillGradient(long paramLong1, long paramLong2);
  
  private static native void nUpdateFullPathProperties(long paramLong, float paramFloat1, int paramInt1, float paramFloat2, int paramInt2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, int paramInt3, int paramInt4, int paramInt5);
  
  private static native void nUpdateFullPathStrokeGradient(long paramLong1, long paramLong2);
  
  private static native void nUpdateGroupProperties(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7);
  
  private boolean needMirroring()
  {
    return (isAutoMirrored()) && (getLayoutDirection() == 1);
  }
  
  private void updateLocalState(Resources paramResources)
  {
    int i = Drawable.resolveDensity(paramResources, this.mVectorState.mDensity);
    if (this.mTargetDensity != i)
    {
      this.mTargetDensity = i;
      this.mDpiScaledDirty = true;
    }
    this.mTintFilter = updateTintFilter(this.mTintFilter, this.mVectorState.mTint, this.mVectorState.mTintMode);
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    VectorDrawableState localVectorDrawableState = this.mVectorState;
    localVectorDrawableState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    localVectorDrawableState.mThemeAttrs = paramTypedArray.extractThemeAttrs();
    int i = paramTypedArray.getInt(6, -1);
    if (i != -1) {
      localVectorDrawableState.mTintMode = Drawable.parseTintMode(i, PorterDuff.Mode.SRC_IN);
    }
    ColorStateList localColorStateList = paramTypedArray.getColorStateList(1);
    if (localColorStateList != null) {
      localVectorDrawableState.mTint = localColorStateList;
    }
    localVectorDrawableState.mAutoMirrored = paramTypedArray.getBoolean(5, localVectorDrawableState.mAutoMirrored);
    localVectorDrawableState.setViewportSize(paramTypedArray.getFloat(7, localVectorDrawableState.mViewportWidth), paramTypedArray.getFloat(8, localVectorDrawableState.mViewportHeight));
    if (localVectorDrawableState.mViewportWidth <= 0.0F) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + "<vector> tag requires viewportWidth > 0");
    }
    if (localVectorDrawableState.mViewportHeight <= 0.0F) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + "<vector> tag requires viewportHeight > 0");
    }
    localVectorDrawableState.mBaseWidth = paramTypedArray.getDimension(3, localVectorDrawableState.mBaseWidth);
    localVectorDrawableState.mBaseHeight = paramTypedArray.getDimension(2, localVectorDrawableState.mBaseHeight);
    if (localVectorDrawableState.mBaseWidth <= 0.0F) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + "<vector> tag requires width > 0");
    }
    if (localVectorDrawableState.mBaseHeight <= 0.0F) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + "<vector> tag requires height > 0");
    }
    localVectorDrawableState.mOpticalInsets = Insets.of(paramTypedArray.getDimensionPixelOffset(9, localVectorDrawableState.mOpticalInsets.left), paramTypedArray.getDimensionPixelOffset(10, localVectorDrawableState.mOpticalInsets.top), paramTypedArray.getDimensionPixelOffset(11, localVectorDrawableState.mOpticalInsets.right), paramTypedArray.getDimensionPixelOffset(12, localVectorDrawableState.mOpticalInsets.bottom));
    localVectorDrawableState.setAlpha(paramTypedArray.getFloat(4, localVectorDrawableState.getAlpha()));
    paramTypedArray = paramTypedArray.getString(0);
    if (paramTypedArray != null)
    {
      localVectorDrawableState.mRootName = paramTypedArray;
      localVectorDrawableState.mVGTargetsMap.put(paramTypedArray, localVectorDrawableState);
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    VectorDrawableState localVectorDrawableState = this.mVectorState;
    if (localVectorDrawableState == null) {
      return;
    }
    boolean bool = this.mVectorState.setDensity(Drawable.resolveDensity(paramTheme.getResources(), 0));
    this.mDpiScaledDirty |= bool;
    TypedArray localTypedArray;
    if (localVectorDrawableState.mThemeAttrs != null) {
      localTypedArray = paramTheme.resolveAttributes(localVectorDrawableState.mThemeAttrs, R.styleable.VectorDrawable);
    }
    try
    {
      localVectorDrawableState.mCacheDirty = true;
      updateStateFromTypedArray(localTypedArray);
      localTypedArray.recycle();
      this.mDpiScaledDirty = true;
      if ((localVectorDrawableState.mTint != null) && (localVectorDrawableState.mTint.canApplyTheme())) {
        localVectorDrawableState.mTint = localVectorDrawableState.mTint.obtainForTheme(paramTheme);
      }
      if ((this.mVectorState != null) && (this.mVectorState.canApplyTheme())) {
        this.mVectorState.applyTheme(paramTheme);
      }
      updateLocalState(paramTheme.getResources());
      return;
    }
    catch (XmlPullParserException paramTheme)
    {
      throw new RuntimeException(paramTheme);
    }
    finally
    {
      localTypedArray.recycle();
    }
  }
  
  public boolean canApplyTheme()
  {
    if ((this.mVectorState == null) || (!this.mVectorState.canApplyTheme())) {
      return super.canApplyTheme();
    }
    return true;
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mMutated = false;
  }
  
  void computeVectorSize()
  {
    Insets localInsets = this.mVectorState.mOpticalInsets;
    int i = this.mVectorState.mDensity;
    int j = this.mTargetDensity;
    int k;
    int m;
    if (j != i)
    {
      this.mDpiScaledWidth = Drawable.scaleFromDensity((int)this.mVectorState.mBaseWidth, i, j, true);
      this.mDpiScaledHeight = Drawable.scaleFromDensity((int)this.mVectorState.mBaseHeight, i, j, true);
      k = Drawable.scaleFromDensity(localInsets.left, i, j, false);
      m = Drawable.scaleFromDensity(localInsets.right, i, j, false);
    }
    for (this.mDpiScaledInsets = Insets.of(k, Drawable.scaleFromDensity(localInsets.top, i, j, false), m, Drawable.scaleFromDensity(localInsets.bottom, i, j, false));; this.mDpiScaledInsets = localInsets)
    {
      this.mDpiScaledDirty = false;
      return;
      this.mDpiScaledWidth = ((int)this.mVectorState.mBaseWidth);
      this.mDpiScaledHeight = ((int)this.mVectorState.mBaseHeight);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    copyBounds(this.mTmpBounds);
    if ((this.mTmpBounds.width() <= 0) || (this.mTmpBounds.height() <= 0)) {
      return;
    }
    Object localObject;
    if (this.mColorFilter == null)
    {
      localObject = this.mTintFilter;
      if (localObject != null) {
        break label100;
      }
    }
    int j;
    label100:
    for (long l = 0L;; l = ((ColorFilter)localObject).native_instance)
    {
      boolean bool = this.mVectorState.canReuseCache();
      j = nDraw(this.mVectorState.getNativeRenderer(), paramCanvas.getNativeCanvasWrapper(), l, this.mTmpBounds, needMirroring(), bool);
      if (j != 0) {
        break label110;
      }
      return;
      localObject = this.mColorFilter;
      break;
    }
    label110:
    int i;
    if (paramCanvas.isHardwareAccelerated())
    {
      i = (j - this.mVectorState.mLastHWCachePixelCount) * 4;
      this.mVectorState.mLastHWCachePixelCount = j;
      if (i <= 0) {
        break label172;
      }
      VMRuntime.getRuntime().registerNativeAllocation(i);
    }
    label172:
    while (i >= 0)
    {
      return;
      i = (j - this.mVectorState.mLastSWCachePixelCount) * 4;
      this.mVectorState.mLastSWCachePixelCount = j;
      break;
    }
    VMRuntime.getRuntime().registerNativeFree(-i);
  }
  
  public int getAlpha()
  {
    return (int)(this.mVectorState.getAlpha() * 255.0F);
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mVectorState.getChangingConfigurations();
  }
  
  public ColorFilter getColorFilter()
  {
    return this.mColorFilter;
  }
  
  public Drawable.ConstantState getConstantState()
  {
    this.mVectorState.mChangingConfigurations = getChangingConfigurations();
    return this.mVectorState;
  }
  
  public int getIntrinsicHeight()
  {
    if (this.mDpiScaledDirty) {
      computeVectorSize();
    }
    return this.mDpiScaledHeight;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.mDpiScaledDirty) {
      computeVectorSize();
    }
    return this.mDpiScaledWidth;
  }
  
  public long getNativeTree()
  {
    return this.mVectorState.getNativeRenderer();
  }
  
  public int getOpacity()
  {
    if (getAlpha() == 0) {
      return -2;
    }
    return -3;
  }
  
  public Insets getOpticalInsets()
  {
    if (this.mDpiScaledDirty) {
      computeVectorSize();
    }
    return this.mDpiScaledInsets;
  }
  
  public float getPixelSize()
  {
    if ((this.mVectorState == null) || (this.mVectorState.mBaseWidth == 0.0F)) {}
    while ((this.mVectorState.mBaseHeight == 0.0F) || (this.mVectorState.mViewportHeight == 0.0F) || (this.mVectorState.mViewportWidth == 0.0F)) {
      return 1.0F;
    }
    float f1 = this.mVectorState.mBaseWidth;
    float f2 = this.mVectorState.mBaseHeight;
    float f3 = this.mVectorState.mViewportWidth;
    float f4 = this.mVectorState.mViewportHeight;
    return Math.min(f3 / f1, f4 / f2);
  }
  
  Object getTargetByName(String paramString)
  {
    return this.mVectorState.mVGTargetsMap.get(paramString);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    if ((this.mVectorState.mRootGroup != null) || (this.mVectorState.mNativeTree != null))
    {
      if (this.mVectorState.mRootGroup != null)
      {
        VMRuntime.getRuntime().registerNativeFree(this.mVectorState.mRootGroup.getNativeSize());
        this.mVectorState.mRootGroup.setTree(null);
      }
      this.mVectorState.mRootGroup = new VGroup();
      if (this.mVectorState.mNativeTree != null)
      {
        VMRuntime.getRuntime().registerNativeFree(316);
        this.mVectorState.mNativeTree.release();
      }
      VectorDrawableState.-wrap0(this.mVectorState, this.mVectorState.mRootGroup);
    }
    VectorDrawableState localVectorDrawableState = this.mVectorState;
    localVectorDrawableState.setDensity(Drawable.resolveDensity(paramResources, 0));
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.VectorDrawable);
    updateStateFromTypedArray(localTypedArray);
    localTypedArray.recycle();
    this.mDpiScaledDirty = true;
    localVectorDrawableState.mCacheDirty = true;
    inflateChildElements(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    localVectorDrawableState.onTreeConstructionFinished();
    updateLocalState(paramResources);
  }
  
  public boolean isAutoMirrored()
  {
    return this.mVectorState.mAutoMirrored;
  }
  
  public boolean isStateful()
  {
    if (!super.isStateful())
    {
      if (this.mVectorState != null) {
        return this.mVectorState.isStateful();
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mVectorState = new VectorDrawableState(this.mVectorState);
      this.mMutated = true;
    }
    return this;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    boolean bool1 = false;
    if (isStateful()) {
      mutate();
    }
    VectorDrawableState localVectorDrawableState = this.mVectorState;
    if (localVectorDrawableState.onStateChange(paramArrayOfInt))
    {
      bool1 = true;
      localVectorDrawableState.mCacheDirty = true;
    }
    boolean bool2 = bool1;
    if (localVectorDrawableState.mTint != null)
    {
      bool2 = bool1;
      if (localVectorDrawableState.mTintMode != null)
      {
        this.mTintFilter = updateTintFilter(this.mTintFilter, localVectorDrawableState.mTint, localVectorDrawableState.mTintMode);
        bool2 = true;
      }
    }
    return bool2;
  }
  
  void setAllowCaching(boolean paramBoolean)
  {
    nSetAllowCaching(this.mVectorState.getNativeRenderer(), paramBoolean);
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.mVectorState.setAlpha(paramInt / 255.0F)) {
      invalidateSelf();
    }
  }
  
  public void setAutoMirrored(boolean paramBoolean)
  {
    if (this.mVectorState.mAutoMirrored != paramBoolean)
    {
      this.mVectorState.mAutoMirrored = paramBoolean;
      invalidateSelf();
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mColorFilter = paramColorFilter;
    invalidateSelf();
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    VectorDrawableState localVectorDrawableState = this.mVectorState;
    if (localVectorDrawableState.mTint != paramColorStateList)
    {
      localVectorDrawableState.mTint = paramColorStateList;
      this.mTintFilter = updateTintFilter(this.mTintFilter, paramColorStateList, localVectorDrawableState.mTintMode);
      invalidateSelf();
    }
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    VectorDrawableState localVectorDrawableState = this.mVectorState;
    if (localVectorDrawableState.mTintMode != paramMode)
    {
      localVectorDrawableState.mTintMode = paramMode;
      this.mTintFilter = updateTintFilter(this.mTintFilter, localVectorDrawableState.mTint, paramMode);
      invalidateSelf();
    }
  }
  
  private static class VClipPath
    extends VectorDrawable.VPath
  {
    private static final int NATIVE_ALLOCATION_SIZE = 120;
    private final long mNativePtr;
    
    public VClipPath()
    {
      this.mNativePtr = VectorDrawable.-wrap19();
    }
    
    public VClipPath(VClipPath paramVClipPath)
    {
      super();
      this.mNativePtr = VectorDrawable.-wrap20(paramVClipPath.mNativePtr);
    }
    
    private void updateStateFromTypedArray(TypedArray paramTypedArray)
    {
      this.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
      String str = paramTypedArray.getString(0);
      if (str != null)
      {
        this.mPathName = str;
        VectorDrawable.-wrap30(this.mNativePtr, this.mPathName);
      }
      paramTypedArray = paramTypedArray.getString(1);
      if (paramTypedArray != null)
      {
        this.mPathData = new PathParser.PathData(paramTypedArray);
        VectorDrawable.-wrap32(this.mNativePtr, paramTypedArray, paramTypedArray.length());
      }
    }
    
    public void applyTheme(Resources.Theme paramTheme) {}
    
    public boolean canApplyTheme()
    {
      return false;
    }
    
    public long getNativePtr()
    {
      return this.mNativePtr;
    }
    
    int getNativeSize()
    {
      return 120;
    }
    
    public void inflate(Resources paramResources, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    {
      paramResources = VectorDrawable.obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.VectorDrawableClipPath);
      updateStateFromTypedArray(paramResources);
      paramResources.recycle();
    }
    
    public boolean isStateful()
    {
      return false;
    }
    
    public boolean onStateChange(int[] paramArrayOfInt)
    {
      return false;
    }
  }
  
  static class VFullPath
    extends VectorDrawable.VPath
  {
    private static final Property<VFullPath, Float> FILL_ALPHA;
    private static final int FILL_ALPHA_INDEX = 4;
    private static final Property<VFullPath, Integer> FILL_COLOR;
    private static final int FILL_COLOR_INDEX = 3;
    private static final int FILL_TYPE_INDEX = 11;
    private static final int NATIVE_ALLOCATION_SIZE = 264;
    private static final Property<VFullPath, Float> STROKE_ALPHA;
    private static final int STROKE_ALPHA_INDEX = 2;
    private static final Property<VFullPath, Integer> STROKE_COLOR;
    private static final int STROKE_COLOR_INDEX = 1;
    private static final int STROKE_LINE_CAP_INDEX = 8;
    private static final int STROKE_LINE_JOIN_INDEX = 9;
    private static final int STROKE_MITER_LIMIT_INDEX = 10;
    private static final Property<VFullPath, Float> STROKE_WIDTH;
    private static final int STROKE_WIDTH_INDEX = 0;
    private static final int TOTAL_PROPERTY_COUNT = 12;
    private static final Property<VFullPath, Float> TRIM_PATH_END = new FloatProperty("trimPathEnd")
    {
      public Float get(VectorDrawable.VFullPath paramAnonymousVFullPath)
      {
        return Float.valueOf(paramAnonymousVFullPath.getTrimPathEnd());
      }
      
      public void setValue(VectorDrawable.VFullPath paramAnonymousVFullPath, float paramAnonymousFloat)
      {
        paramAnonymousVFullPath.setTrimPathEnd(paramAnonymousFloat);
      }
    };
    private static final int TRIM_PATH_END_INDEX = 6;
    private static final Property<VFullPath, Float> TRIM_PATH_OFFSET = new FloatProperty("trimPathOffset")
    {
      public Float get(VectorDrawable.VFullPath paramAnonymousVFullPath)
      {
        return Float.valueOf(paramAnonymousVFullPath.getTrimPathOffset());
      }
      
      public void setValue(VectorDrawable.VFullPath paramAnonymousVFullPath, float paramAnonymousFloat)
      {
        paramAnonymousVFullPath.setTrimPathOffset(paramAnonymousFloat);
      }
    };
    private static final int TRIM_PATH_OFFSET_INDEX = 7;
    private static final Property<VFullPath, Float> TRIM_PATH_START;
    private static final int TRIM_PATH_START_INDEX = 5;
    private static final HashMap<String, Integer> sPropertyIndexMap = new HashMap() {};
    private static final HashMap<String, Property> sPropertyMap = new HashMap() {};
    ComplexColor mFillColors = null;
    private final long mNativePtr;
    private byte[] mPropertyData;
    ComplexColor mStrokeColors = null;
    private int[] mThemeAttrs;
    
    static
    {
      STROKE_WIDTH = new FloatProperty("strokeWidth")
      {
        public Float get(VectorDrawable.VFullPath paramAnonymousVFullPath)
        {
          return Float.valueOf(paramAnonymousVFullPath.getStrokeWidth());
        }
        
        public void setValue(VectorDrawable.VFullPath paramAnonymousVFullPath, float paramAnonymousFloat)
        {
          paramAnonymousVFullPath.setStrokeWidth(paramAnonymousFloat);
        }
      };
      STROKE_COLOR = new IntProperty("strokeColor")
      {
        public Integer get(VectorDrawable.VFullPath paramAnonymousVFullPath)
        {
          return Integer.valueOf(paramAnonymousVFullPath.getStrokeColor());
        }
        
        public void setValue(VectorDrawable.VFullPath paramAnonymousVFullPath, int paramAnonymousInt)
        {
          paramAnonymousVFullPath.setStrokeColor(paramAnonymousInt);
        }
      };
      STROKE_ALPHA = new FloatProperty("strokeAlpha")
      {
        public Float get(VectorDrawable.VFullPath paramAnonymousVFullPath)
        {
          return Float.valueOf(paramAnonymousVFullPath.getStrokeAlpha());
        }
        
        public void setValue(VectorDrawable.VFullPath paramAnonymousVFullPath, float paramAnonymousFloat)
        {
          paramAnonymousVFullPath.setStrokeAlpha(paramAnonymousFloat);
        }
      };
      FILL_COLOR = new IntProperty("fillColor")
      {
        public Integer get(VectorDrawable.VFullPath paramAnonymousVFullPath)
        {
          return Integer.valueOf(paramAnonymousVFullPath.getFillColor());
        }
        
        public void setValue(VectorDrawable.VFullPath paramAnonymousVFullPath, int paramAnonymousInt)
        {
          paramAnonymousVFullPath.setFillColor(paramAnonymousInt);
        }
      };
      FILL_ALPHA = new FloatProperty("fillAlpha")
      {
        public Float get(VectorDrawable.VFullPath paramAnonymousVFullPath)
        {
          return Float.valueOf(paramAnonymousVFullPath.getFillAlpha());
        }
        
        public void setValue(VectorDrawable.VFullPath paramAnonymousVFullPath, float paramAnonymousFloat)
        {
          paramAnonymousVFullPath.setFillAlpha(paramAnonymousFloat);
        }
      };
      TRIM_PATH_START = new FloatProperty("trimPathStart")
      {
        public Float get(VectorDrawable.VFullPath paramAnonymousVFullPath)
        {
          return Float.valueOf(paramAnonymousVFullPath.getTrimPathStart());
        }
        
        public void setValue(VectorDrawable.VFullPath paramAnonymousVFullPath, float paramAnonymousFloat)
        {
          paramAnonymousVFullPath.setTrimPathStart(paramAnonymousFloat);
        }
      };
    }
    
    public VFullPath()
    {
      this.mNativePtr = VectorDrawable.-wrap21();
    }
    
    public VFullPath(VFullPath paramVFullPath)
    {
      super();
      this.mNativePtr = VectorDrawable.-wrap22(paramVFullPath.mNativePtr);
      this.mThemeAttrs = paramVFullPath.mThemeAttrs;
      this.mStrokeColors = paramVFullPath.mStrokeColors;
      this.mFillColors = paramVFullPath.mFillColors;
    }
    
    private boolean canComplexColorApplyTheme(ComplexColor paramComplexColor)
    {
      if (paramComplexColor != null) {
        return paramComplexColor.canApplyTheme();
      }
      return false;
    }
    
    private void updateStateFromTypedArray(TypedArray paramTypedArray)
    {
      if (this.mPropertyData == null) {
        this.mPropertyData = new byte[48];
      }
      if (!VectorDrawable.-wrap0(this.mNativePtr, this.mPropertyData, 48)) {
        throw new RuntimeException("Error: inconsistent property count");
      }
      Object localObject1 = ByteBuffer.wrap(this.mPropertyData);
      ((ByteBuffer)localObject1).order(ByteOrder.nativeOrder());
      float f7 = ((ByteBuffer)localObject1).getFloat(0);
      int j = ((ByteBuffer)localObject1).getInt(4);
      float f6 = ((ByteBuffer)localObject1).getFloat(8);
      int i = ((ByteBuffer)localObject1).getInt(12);
      float f2 = ((ByteBuffer)localObject1).getFloat(16);
      float f1 = ((ByteBuffer)localObject1).getFloat(20);
      float f5 = ((ByteBuffer)localObject1).getFloat(24);
      float f3 = ((ByteBuffer)localObject1).getFloat(28);
      int n = ((ByteBuffer)localObject1).getInt(32);
      int m = ((ByteBuffer)localObject1).getInt(36);
      float f4 = ((ByteBuffer)localObject1).getFloat(40);
      int k = ((ByteBuffer)localObject1).getInt(44);
      Object localObject2 = null;
      localObject1 = null;
      Object localObject4 = null;
      Object localObject3 = null;
      this.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
      this.mThemeAttrs = paramTypedArray.extractThemeAttrs();
      Object localObject5 = paramTypedArray.getString(0);
      if (localObject5 != null)
      {
        this.mPathName = ((String)localObject5);
        VectorDrawable.-wrap30(this.mNativePtr, this.mPathName);
      }
      localObject5 = paramTypedArray.getString(2);
      if (localObject5 != null)
      {
        this.mPathData = new PathParser.PathData((String)localObject5);
        VectorDrawable.-wrap32(this.mNativePtr, (String)localObject5, ((String)localObject5).length());
      }
      localObject5 = paramTypedArray.getComplexColor(1);
      label351:
      long l2;
      if (localObject5 != null)
      {
        if ((localObject5 instanceof GradientColor))
        {
          this.mFillColors = ((ComplexColor)localObject5);
          localObject1 = ((GradientColor)localObject5).getShader();
          i = ((ComplexColor)localObject5).getDefaultColor();
          localObject2 = localObject1;
        }
      }
      else
      {
        localObject5 = paramTypedArray.getComplexColor(3);
        localObject1 = localObject4;
        if (localObject5 != null)
        {
          if (!(localObject5 instanceof GradientColor)) {
            break label557;
          }
          this.mStrokeColors = ((ComplexColor)localObject5);
          localObject1 = ((GradientColor)localObject5).getShader();
          j = ((ComplexColor)localObject5).getDefaultColor();
        }
        l2 = this.mNativePtr;
        if (localObject2 == null) {
          break label590;
        }
        l1 = ((Shader)localObject2).getNativeInstance();
        label376:
        VectorDrawable.-wrap47(l2, l1);
        l2 = this.mNativePtr;
        if (localObject1 == null) {
          break label596;
        }
      }
      label557:
      label590:
      label596:
      for (long l1 = ((Shader)localObject1).getNativeInstance();; l1 = 0L)
      {
        VectorDrawable.-wrap49(l2, l1);
        f2 = paramTypedArray.getFloat(12, f2);
        n = paramTypedArray.getInt(8, n);
        m = paramTypedArray.getInt(9, m);
        f4 = paramTypedArray.getFloat(10, f4);
        f6 = paramTypedArray.getFloat(11, f6);
        f7 = paramTypedArray.getFloat(4, f7);
        f5 = paramTypedArray.getFloat(6, f5);
        f3 = paramTypedArray.getFloat(7, f3);
        f1 = paramTypedArray.getFloat(5, f1);
        k = paramTypedArray.getInt(13, k);
        VectorDrawable.-wrap48(this.mNativePtr, f7, j, f6, i, f2, f1, f5, f3, f4, n, m, k);
        return;
        if (((ComplexColor)localObject5).isStateful())
        {
          this.mFillColors = ((ComplexColor)localObject5);
          break;
        }
        this.mFillColors = null;
        break;
        if (((ComplexColor)localObject5).isStateful())
        {
          this.mStrokeColors = ((ComplexColor)localObject5);
          localObject1 = localObject3;
          break label351;
        }
        this.mStrokeColors = null;
        localObject1 = localObject3;
        break label351;
        l1 = 0L;
        break label376;
      }
    }
    
    public void applyTheme(Resources.Theme paramTheme)
    {
      if (this.mThemeAttrs != null)
      {
        TypedArray localTypedArray = paramTheme.resolveAttributes(this.mThemeAttrs, R.styleable.VectorDrawablePath);
        updateStateFromTypedArray(localTypedArray);
        localTypedArray.recycle();
      }
      boolean bool1 = canComplexColorApplyTheme(this.mFillColors);
      boolean bool2 = canComplexColorApplyTheme(this.mStrokeColors);
      if (bool1)
      {
        this.mFillColors = this.mFillColors.obtainForTheme(paramTheme);
        if (!(this.mFillColors instanceof GradientColor)) {
          break label142;
        }
        VectorDrawable.-wrap47(this.mNativePtr, ((GradientColor)this.mFillColors).getShader().getNativeInstance());
      }
      label142:
      do
      {
        for (;;)
        {
          if (bool2)
          {
            this.mStrokeColors = this.mStrokeColors.obtainForTheme(paramTheme);
            if (!(this.mStrokeColors instanceof GradientColor)) {
              break;
            }
            VectorDrawable.-wrap49(this.mNativePtr, ((GradientColor)this.mStrokeColors).getShader().getNativeInstance());
          }
          return;
          if ((this.mFillColors instanceof ColorStateList)) {
            VectorDrawable.-wrap29(this.mNativePtr, this.mFillColors.getDefaultColor());
          }
        }
      } while (!(this.mStrokeColors instanceof ColorStateList));
      VectorDrawable.-wrap40(this.mNativePtr, this.mStrokeColors.getDefaultColor());
    }
    
    public boolean canApplyTheme()
    {
      if (this.mThemeAttrs != null) {
        return true;
      }
      boolean bool1 = canComplexColorApplyTheme(this.mFillColors);
      boolean bool2 = canComplexColorApplyTheme(this.mStrokeColors);
      return (bool1) || (bool2);
    }
    
    float getFillAlpha()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap3(this.mNativePtr);
      }
      return 0.0F;
    }
    
    int getFillColor()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap17(this.mNativePtr);
      }
      return 0;
    }
    
    public long getNativePtr()
    {
      return this.mNativePtr;
    }
    
    int getNativeSize()
    {
      return 264;
    }
    
    Property getProperty(String paramString)
    {
      Property localProperty = super.getProperty(paramString);
      if (localProperty != null) {
        return localProperty;
      }
      if (sPropertyMap.containsKey(paramString)) {
        return (Property)sPropertyMap.get(paramString);
      }
      return null;
    }
    
    int getPropertyIndex(String paramString)
    {
      if (!sPropertyIndexMap.containsKey(paramString)) {
        return -1;
      }
      return ((Integer)sPropertyIndexMap.get(paramString)).intValue();
    }
    
    float getStrokeAlpha()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap10(this.mNativePtr);
      }
      return 0.0F;
    }
    
    int getStrokeColor()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap18(this.mNativePtr);
      }
      return 0;
    }
    
    float getStrokeWidth()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap11(this.mNativePtr);
      }
      return 0.0F;
    }
    
    float getTrimPathEnd()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap14(this.mNativePtr);
      }
      return 0.0F;
    }
    
    float getTrimPathOffset()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap15(this.mNativePtr);
      }
      return 0.0F;
    }
    
    float getTrimPathStart()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap16(this.mNativePtr);
      }
      return 0.0F;
    }
    
    public void inflate(Resources paramResources, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    {
      paramResources = VectorDrawable.obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.VectorDrawablePath);
      updateStateFromTypedArray(paramResources);
      paramResources.recycle();
    }
    
    public boolean isStateful()
    {
      return (this.mStrokeColors != null) || (this.mFillColors != null);
    }
    
    public boolean onStateChange(int[] paramArrayOfInt)
    {
      int i2 = 0;
      int i1 = i2;
      if (this.mStrokeColors != null)
      {
        i1 = i2;
        if ((this.mStrokeColors instanceof ColorStateList))
        {
          i = getStrokeColor();
          int k = ((ColorStateList)this.mStrokeColors).getColorForState(paramArrayOfInt, i);
          if (i == k) {
            break label159;
          }
          i2 = 1;
          i1 = i2;
          if (i != k)
          {
            VectorDrawable.-wrap40(this.mNativePtr, k);
            i1 = i2;
          }
        }
      }
      i2 = i1;
      int m;
      int n;
      if (this.mFillColors != null)
      {
        i2 = i1;
        if ((this.mFillColors instanceof ColorStateList))
        {
          m = getFillColor();
          n = ((ColorStateList)this.mFillColors).getColorForState(paramArrayOfInt, m);
          if (m == n) {
            break label165;
          }
        }
      }
      label159:
      label165:
      int j;
      for (int i = 1;; j = 0)
      {
        i1 |= i;
        i2 = i1;
        if (m != n)
        {
          VectorDrawable.-wrap29(this.mNativePtr, n);
          i2 = i1;
        }
        return i2;
        i2 = 0;
        break;
      }
    }
    
    void setFillAlpha(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap28(this.mNativePtr, paramFloat);
      }
    }
    
    void setFillColor(int paramInt)
    {
      this.mFillColors = null;
      if (isTreeValid()) {
        VectorDrawable.-wrap29(this.mNativePtr, paramInt);
      }
    }
    
    void setStrokeAlpha(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap39(this.mNativePtr, paramFloat);
      }
    }
    
    void setStrokeColor(int paramInt)
    {
      this.mStrokeColors = null;
      if (isTreeValid()) {
        VectorDrawable.-wrap40(this.mNativePtr, paramInt);
      }
    }
    
    void setStrokeWidth(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap41(this.mNativePtr, paramFloat);
      }
    }
    
    void setTrimPathEnd(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap44(this.mNativePtr, paramFloat);
      }
    }
    
    void setTrimPathOffset(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap45(this.mNativePtr, paramFloat);
      }
    }
    
    void setTrimPathStart(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap46(this.mNativePtr, paramFloat);
      }
    }
  }
  
  static class VGroup
    extends VectorDrawable.VObject
  {
    private static final int NATIVE_ALLOCATION_SIZE = 100;
    private static final Property<VGroup, Float> PIVOT_X = new FloatProperty("pivotX")
    {
      public Float get(VectorDrawable.VGroup paramAnonymousVGroup)
      {
        return Float.valueOf(paramAnonymousVGroup.getPivotX());
      }
      
      public void setValue(VectorDrawable.VGroup paramAnonymousVGroup, float paramAnonymousFloat)
      {
        paramAnonymousVGroup.setPivotX(paramAnonymousFloat);
      }
    };
    private static final int PIVOT_X_INDEX = 1;
    private static final Property<VGroup, Float> PIVOT_Y = new FloatProperty("pivotY")
    {
      public Float get(VectorDrawable.VGroup paramAnonymousVGroup)
      {
        return Float.valueOf(paramAnonymousVGroup.getPivotY());
      }
      
      public void setValue(VectorDrawable.VGroup paramAnonymousVGroup, float paramAnonymousFloat)
      {
        paramAnonymousVGroup.setPivotY(paramAnonymousFloat);
      }
    };
    private static final int PIVOT_Y_INDEX = 2;
    private static final Property<VGroup, Float> ROTATION = new FloatProperty("rotation")
    {
      public Float get(VectorDrawable.VGroup paramAnonymousVGroup)
      {
        return Float.valueOf(paramAnonymousVGroup.getRotation());
      }
      
      public void setValue(VectorDrawable.VGroup paramAnonymousVGroup, float paramAnonymousFloat)
      {
        paramAnonymousVGroup.setRotation(paramAnonymousFloat);
      }
    };
    private static final int ROTATION_INDEX = 0;
    private static final Property<VGroup, Float> SCALE_X;
    private static final int SCALE_X_INDEX = 3;
    private static final Property<VGroup, Float> SCALE_Y;
    private static final int SCALE_Y_INDEX = 4;
    private static final int TRANSFORM_PROPERTY_COUNT = 7;
    private static final Property<VGroup, Float> TRANSLATE_X;
    private static final int TRANSLATE_X_INDEX = 5;
    private static final Property<VGroup, Float> TRANSLATE_Y;
    private static final int TRANSLATE_Y_INDEX = 6;
    private static final HashMap<String, Integer> sPropertyIndexMap = new HashMap() {};
    private static final HashMap<String, Property> sPropertyMap = new HashMap() {};
    private int mChangingConfigurations;
    private final ArrayList<VectorDrawable.VObject> mChildren = new ArrayList();
    private String mGroupName = null;
    private boolean mIsStateful;
    private final long mNativePtr;
    private int[] mThemeAttrs;
    private float[] mTransform;
    
    static
    {
      TRANSLATE_X = new FloatProperty("translateX")
      {
        public Float get(VectorDrawable.VGroup paramAnonymousVGroup)
        {
          return Float.valueOf(paramAnonymousVGroup.getTranslateX());
        }
        
        public void setValue(VectorDrawable.VGroup paramAnonymousVGroup, float paramAnonymousFloat)
        {
          paramAnonymousVGroup.setTranslateX(paramAnonymousFloat);
        }
      };
      TRANSLATE_Y = new FloatProperty("translateY")
      {
        public Float get(VectorDrawable.VGroup paramAnonymousVGroup)
        {
          return Float.valueOf(paramAnonymousVGroup.getTranslateY());
        }
        
        public void setValue(VectorDrawable.VGroup paramAnonymousVGroup, float paramAnonymousFloat)
        {
          paramAnonymousVGroup.setTranslateY(paramAnonymousFloat);
        }
      };
      SCALE_X = new FloatProperty("scaleX")
      {
        public Float get(VectorDrawable.VGroup paramAnonymousVGroup)
        {
          return Float.valueOf(paramAnonymousVGroup.getScaleX());
        }
        
        public void setValue(VectorDrawable.VGroup paramAnonymousVGroup, float paramAnonymousFloat)
        {
          paramAnonymousVGroup.setScaleX(paramAnonymousFloat);
        }
      };
      SCALE_Y = new FloatProperty("scaleY")
      {
        public Float get(VectorDrawable.VGroup paramAnonymousVGroup)
        {
          return Float.valueOf(paramAnonymousVGroup.getScaleY());
        }
        
        public void setValue(VectorDrawable.VGroup paramAnonymousVGroup, float paramAnonymousFloat)
        {
          paramAnonymousVGroup.setScaleY(paramAnonymousFloat);
        }
      };
    }
    
    public VGroup()
    {
      this.mNativePtr = VectorDrawable.-wrap23();
    }
    
    public VGroup(VGroup paramVGroup, ArrayMap<String, Object> paramArrayMap)
    {
      this.mIsStateful = paramVGroup.mIsStateful;
      this.mThemeAttrs = paramVGroup.mThemeAttrs;
      this.mGroupName = paramVGroup.mGroupName;
      this.mChangingConfigurations = paramVGroup.mChangingConfigurations;
      if (this.mGroupName != null) {
        paramArrayMap.put(this.mGroupName, this);
      }
      this.mNativePtr = VectorDrawable.-wrap24(paramVGroup.mNativePtr);
      ArrayList localArrayList = paramVGroup.mChildren;
      int i = 0;
      while (i < localArrayList.size())
      {
        paramVGroup = (VectorDrawable.VObject)localArrayList.get(i);
        if ((paramVGroup instanceof VGroup))
        {
          addChild(new VGroup((VGroup)paramVGroup, paramArrayMap));
          i += 1;
        }
        else
        {
          if ((paramVGroup instanceof VectorDrawable.VFullPath)) {}
          for (paramVGroup = new VectorDrawable.VFullPath((VectorDrawable.VFullPath)paramVGroup);; paramVGroup = new VectorDrawable.VClipPath((VectorDrawable.VClipPath)paramVGroup))
          {
            addChild(paramVGroup);
            if (paramVGroup.mPathName == null) {
              break;
            }
            paramArrayMap.put(paramVGroup.mPathName, paramVGroup);
            break;
            if (!(paramVGroup instanceof VectorDrawable.VClipPath)) {
              break label203;
            }
          }
          label203:
          throw new IllegalStateException("Unknown object in the tree!");
        }
      }
    }
    
    static int getPropertyIndex(String paramString)
    {
      if (sPropertyIndexMap.containsKey(paramString)) {
        return ((Integer)sPropertyIndexMap.get(paramString)).intValue();
      }
      return -1;
    }
    
    public void addChild(VectorDrawable.VObject paramVObject)
    {
      VectorDrawable.-wrap27(this.mNativePtr, paramVObject.getNativePtr());
      this.mChildren.add(paramVObject);
      this.mIsStateful |= paramVObject.isStateful();
    }
    
    public void applyTheme(Resources.Theme paramTheme)
    {
      if (this.mThemeAttrs != null)
      {
        localObject = paramTheme.resolveAttributes(this.mThemeAttrs, R.styleable.VectorDrawableGroup);
        updateStateFromTypedArray((TypedArray)localObject);
        ((TypedArray)localObject).recycle();
      }
      Object localObject = this.mChildren;
      int i = 0;
      int j = ((ArrayList)localObject).size();
      while (i < j)
      {
        VectorDrawable.VObject localVObject = (VectorDrawable.VObject)((ArrayList)localObject).get(i);
        if (localVObject.canApplyTheme())
        {
          localVObject.applyTheme(paramTheme);
          this.mIsStateful |= localVObject.isStateful();
        }
        i += 1;
      }
    }
    
    public boolean canApplyTheme()
    {
      if (this.mThemeAttrs != null) {
        return true;
      }
      ArrayList localArrayList = this.mChildren;
      int i = 0;
      int j = localArrayList.size();
      while (i < j)
      {
        if (((VectorDrawable.VObject)localArrayList.get(i)).canApplyTheme()) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public String getGroupName()
    {
      return this.mGroupName;
    }
    
    public long getNativePtr()
    {
      return this.mNativePtr;
    }
    
    int getNativeSize()
    {
      int j = 100;
      int i = 0;
      while (i < this.mChildren.size())
      {
        j += ((VectorDrawable.VObject)this.mChildren.get(i)).getNativeSize();
        i += 1;
      }
      return j;
    }
    
    public float getPivotX()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap4(this.mNativePtr);
      }
      return 0.0F;
    }
    
    public float getPivotY()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap5(this.mNativePtr);
      }
      return 0.0F;
    }
    
    Property getProperty(String paramString)
    {
      if (sPropertyMap.containsKey(paramString)) {
        return (Property)sPropertyMap.get(paramString);
      }
      return null;
    }
    
    public float getRotation()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap7(this.mNativePtr);
      }
      return 0.0F;
    }
    
    public float getScaleX()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap8(this.mNativePtr);
      }
      return 0.0F;
    }
    
    public float getScaleY()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap9(this.mNativePtr);
      }
      return 0.0F;
    }
    
    public float getTranslateX()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap12(this.mNativePtr);
      }
      return 0.0F;
    }
    
    public float getTranslateY()
    {
      if (isTreeValid()) {
        return VectorDrawable.-wrap13(this.mNativePtr);
      }
      return 0.0F;
    }
    
    public void inflate(Resources paramResources, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    {
      paramResources = VectorDrawable.obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.VectorDrawableGroup);
      updateStateFromTypedArray(paramResources);
      paramResources.recycle();
    }
    
    public boolean isStateful()
    {
      return this.mIsStateful;
    }
    
    public boolean onStateChange(int[] paramArrayOfInt)
    {
      boolean bool1 = false;
      ArrayList localArrayList = this.mChildren;
      int i = 0;
      int j = localArrayList.size();
      while (i < j)
      {
        VectorDrawable.VObject localVObject = (VectorDrawable.VObject)localArrayList.get(i);
        boolean bool2 = bool1;
        if (localVObject.isStateful()) {
          bool2 = bool1 | localVObject.onStateChange(paramArrayOfInt);
        }
        i += 1;
        bool1 = bool2;
      }
      return bool1;
    }
    
    public void setPivotX(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap33(this.mNativePtr, paramFloat);
      }
    }
    
    public void setPivotY(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap34(this.mNativePtr, paramFloat);
      }
    }
    
    public void setRotation(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap36(this.mNativePtr, paramFloat);
      }
    }
    
    public void setScaleX(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap37(this.mNativePtr, paramFloat);
      }
    }
    
    public void setScaleY(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap38(this.mNativePtr, paramFloat);
      }
    }
    
    public void setTranslateX(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap42(this.mNativePtr, paramFloat);
      }
    }
    
    public void setTranslateY(float paramFloat)
    {
      if (isTreeValid()) {
        VectorDrawable.-wrap43(this.mNativePtr, paramFloat);
      }
    }
    
    public void setTree(VirtualRefBasePtr paramVirtualRefBasePtr)
    {
      super.setTree(paramVirtualRefBasePtr);
      int i = 0;
      while (i < this.mChildren.size())
      {
        ((VectorDrawable.VObject)this.mChildren.get(i)).setTree(paramVirtualRefBasePtr);
        i += 1;
      }
    }
    
    void updateStateFromTypedArray(TypedArray paramTypedArray)
    {
      this.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
      this.mThemeAttrs = paramTypedArray.extractThemeAttrs();
      if (this.mTransform == null) {
        this.mTransform = new float[7];
      }
      if (!VectorDrawable.-wrap1(this.mNativePtr, this.mTransform, 7)) {
        throw new RuntimeException("Error: inconsistent property count");
      }
      float f1 = paramTypedArray.getFloat(5, this.mTransform[0]);
      float f2 = paramTypedArray.getFloat(1, this.mTransform[1]);
      float f3 = paramTypedArray.getFloat(2, this.mTransform[2]);
      float f4 = paramTypedArray.getFloat(3, this.mTransform[3]);
      float f5 = paramTypedArray.getFloat(4, this.mTransform[4]);
      float f6 = paramTypedArray.getFloat(6, this.mTransform[5]);
      float f7 = paramTypedArray.getFloat(7, this.mTransform[6]);
      paramTypedArray = paramTypedArray.getString(0);
      if (paramTypedArray != null)
      {
        this.mGroupName = paramTypedArray;
        VectorDrawable.-wrap30(this.mNativePtr, this.mGroupName);
      }
      VectorDrawable.-wrap50(this.mNativePtr, f1, f2, f3, f4, f5, f6, f7);
    }
  }
  
  static abstract class VObject
  {
    VirtualRefBasePtr mTreePtr = null;
    
    abstract void applyTheme(Resources.Theme paramTheme);
    
    abstract boolean canApplyTheme();
    
    abstract long getNativePtr();
    
    abstract int getNativeSize();
    
    abstract Property getProperty(String paramString);
    
    abstract void inflate(Resources paramResources, AttributeSet paramAttributeSet, Resources.Theme paramTheme);
    
    abstract boolean isStateful();
    
    boolean isTreeValid()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.mTreePtr != null)
      {
        bool1 = bool2;
        if (this.mTreePtr.get() != 0L) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    abstract boolean onStateChange(int[] paramArrayOfInt);
    
    void setTree(VirtualRefBasePtr paramVirtualRefBasePtr)
    {
      this.mTreePtr = paramVirtualRefBasePtr;
    }
  }
  
  static abstract class VPath
    extends VectorDrawable.VObject
  {
    private static final Property<VPath, PathParser.PathData> PATH_DATA = new Property(PathParser.PathData.class, "pathData")
    {
      public PathParser.PathData get(VectorDrawable.VPath paramAnonymousVPath)
      {
        return paramAnonymousVPath.getPathData();
      }
      
      public void set(VectorDrawable.VPath paramAnonymousVPath, PathParser.PathData paramAnonymousPathData)
      {
        paramAnonymousVPath.setPathData(paramAnonymousPathData);
      }
    };
    int mChangingConfigurations;
    protected PathParser.PathData mPathData = null;
    String mPathName;
    
    public VPath() {}
    
    public VPath(VPath paramVPath)
    {
      this.mPathName = paramVPath.mPathName;
      this.mChangingConfigurations = paramVPath.mChangingConfigurations;
      if (paramVPath.mPathData == null) {}
      for (paramVPath = (VPath)localObject;; paramVPath = new PathParser.PathData(paramVPath.mPathData))
      {
        this.mPathData = paramVPath;
        return;
      }
    }
    
    public PathParser.PathData getPathData()
    {
      return this.mPathData;
    }
    
    public String getPathName()
    {
      return this.mPathName;
    }
    
    Property getProperty(String paramString)
    {
      if (PATH_DATA.getName().equals(paramString)) {
        return PATH_DATA;
      }
      return null;
    }
    
    public void setPathData(PathParser.PathData paramPathData)
    {
      this.mPathData.setPathData(paramPathData);
      if (isTreeValid()) {
        VectorDrawable.-wrap31(getNativePtr(), this.mPathData.getNativePtr());
      }
    }
  }
  
  static class VectorDrawableState
    extends Drawable.ConstantState
  {
    static final Property<VectorDrawableState, Float> ALPHA = new FloatProperty("alpha")
    {
      public Float get(VectorDrawable.VectorDrawableState paramAnonymousVectorDrawableState)
      {
        return Float.valueOf(paramAnonymousVectorDrawableState.getAlpha());
      }
      
      public void setValue(VectorDrawable.VectorDrawableState paramAnonymousVectorDrawableState, float paramAnonymousFloat)
      {
        paramAnonymousVectorDrawableState.setAlpha(paramAnonymousFloat);
      }
    };
    private static final int NATIVE_ALLOCATION_SIZE = 316;
    private int mAllocationOfAllNodes = 0;
    boolean mAutoMirrored;
    float mBaseHeight = 0.0F;
    float mBaseWidth = 0.0F;
    boolean mCacheDirty;
    boolean mCachedAutoMirrored;
    int[] mCachedThemeAttrs;
    ColorStateList mCachedTint;
    PorterDuff.Mode mCachedTintMode;
    int mChangingConfigurations;
    int mDensity = 160;
    int mLastHWCachePixelCount = 0;
    int mLastSWCachePixelCount = 0;
    VirtualRefBasePtr mNativeTree = null;
    Insets mOpticalInsets = Insets.NONE;
    VectorDrawable.VGroup mRootGroup;
    String mRootName = null;
    int[] mThemeAttrs;
    ColorStateList mTint = null;
    PorterDuff.Mode mTintMode = VectorDrawable.DEFAULT_TINT_MODE;
    final ArrayMap<String, Object> mVGTargetsMap = new ArrayMap();
    float mViewportHeight = 0.0F;
    float mViewportWidth = 0.0F;
    
    public VectorDrawableState(VectorDrawableState paramVectorDrawableState)
    {
      if (paramVectorDrawableState != null)
      {
        this.mThemeAttrs = paramVectorDrawableState.mThemeAttrs;
        this.mChangingConfigurations = paramVectorDrawableState.mChangingConfigurations;
        this.mTint = paramVectorDrawableState.mTint;
        this.mTintMode = paramVectorDrawableState.mTintMode;
        this.mAutoMirrored = paramVectorDrawableState.mAutoMirrored;
        this.mRootGroup = new VectorDrawable.VGroup(paramVectorDrawableState.mRootGroup, this.mVGTargetsMap);
        createNativeTreeFromCopy(paramVectorDrawableState, this.mRootGroup);
        this.mBaseWidth = paramVectorDrawableState.mBaseWidth;
        this.mBaseHeight = paramVectorDrawableState.mBaseHeight;
        setViewportSize(paramVectorDrawableState.mViewportWidth, paramVectorDrawableState.mViewportHeight);
        this.mOpticalInsets = paramVectorDrawableState.mOpticalInsets;
        this.mRootName = paramVectorDrawableState.mRootName;
        this.mDensity = paramVectorDrawableState.mDensity;
        if (paramVectorDrawableState.mRootName != null) {
          this.mVGTargetsMap.put(paramVectorDrawableState.mRootName, this);
        }
      }
      for (;;)
      {
        onTreeConstructionFinished();
        return;
        this.mRootGroup = new VectorDrawable.VGroup();
        createNativeTree(this.mRootGroup);
      }
    }
    
    private void applyDensityScaling(int paramInt1, int paramInt2)
    {
      this.mBaseWidth = Drawable.scaleFromDensity(this.mBaseWidth, paramInt1, paramInt2);
      this.mBaseHeight = Drawable.scaleFromDensity(this.mBaseHeight, paramInt1, paramInt2);
      this.mOpticalInsets = Insets.of(Drawable.scaleFromDensity(this.mOpticalInsets.left, paramInt1, paramInt2, false), Drawable.scaleFromDensity(this.mOpticalInsets.top, paramInt1, paramInt2, false), Drawable.scaleFromDensity(this.mOpticalInsets.right, paramInt1, paramInt2, false), Drawable.scaleFromDensity(this.mOpticalInsets.bottom, paramInt1, paramInt2, false));
    }
    
    private void createNativeTree(VectorDrawable.VGroup paramVGroup)
    {
      this.mNativeTree = new VirtualRefBasePtr(VectorDrawable.-wrap26(VectorDrawable.VGroup.-get8(paramVGroup)));
      VMRuntime.getRuntime().registerNativeAllocation(316);
    }
    
    private void createNativeTreeFromCopy(VectorDrawableState paramVectorDrawableState, VectorDrawable.VGroup paramVGroup)
    {
      this.mNativeTree = new VirtualRefBasePtr(VectorDrawable.-wrap25(paramVectorDrawableState.mNativeTree.get(), VectorDrawable.VGroup.-get8(paramVGroup)));
      VMRuntime.getRuntime().registerNativeAllocation(316);
    }
    
    public void applyTheme(Resources.Theme paramTheme)
    {
      this.mRootGroup.applyTheme(paramTheme);
    }
    
    public boolean canApplyTheme()
    {
      if ((this.mThemeAttrs == null) && ((this.mRootGroup == null) || (!this.mRootGroup.canApplyTheme())) && ((this.mTint == null) || (!this.mTint.canApplyTheme()))) {
        return super.canApplyTheme();
      }
      return true;
    }
    
    public boolean canReuseCache()
    {
      if ((!this.mCacheDirty) && (this.mCachedThemeAttrs == this.mThemeAttrs) && (this.mCachedTint == this.mTint) && (this.mCachedTintMode == this.mTintMode) && (this.mCachedAutoMirrored == this.mAutoMirrored)) {
        return true;
      }
      updateCacheStates();
      return false;
    }
    
    public void finalize()
      throws Throwable
    {
      super.finalize();
      int i = this.mLastHWCachePixelCount;
      int j = this.mLastSWCachePixelCount;
      VMRuntime.getRuntime().registerNativeFree(this.mAllocationOfAllNodes + 316 + (i * 4 + j * 4));
    }
    
    public float getAlpha()
    {
      return VectorDrawable.-wrap6(this.mNativeTree.get());
    }
    
    public int getChangingConfigurations()
    {
      int j = this.mChangingConfigurations;
      if (this.mTint != null) {}
      for (int i = this.mTint.getChangingConfigurations();; i = 0) {
        return i | j;
      }
    }
    
    long getNativeRenderer()
    {
      if (this.mNativeTree == null) {
        return 0L;
      }
      return this.mNativeTree.get();
    }
    
    Property getProperty(String paramString)
    {
      if (ALPHA.getName().equals(paramString)) {
        return ALPHA;
      }
      return null;
    }
    
    public boolean isStateful()
    {
      if ((this.mTint == null) || (!this.mTint.isStateful()))
      {
        if (this.mRootGroup != null) {
          return this.mRootGroup.isStateful();
        }
      }
      else {
        return true;
      }
      return false;
    }
    
    public Drawable newDrawable()
    {
      return new VectorDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new VectorDrawable(this, paramResources, null);
    }
    
    public boolean onStateChange(int[] paramArrayOfInt)
    {
      return this.mRootGroup.onStateChange(paramArrayOfInt);
    }
    
    void onTreeConstructionFinished()
    {
      this.mRootGroup.setTree(this.mNativeTree);
      this.mAllocationOfAllNodes = this.mRootGroup.getNativeSize();
      VMRuntime.getRuntime().registerNativeAllocation(this.mAllocationOfAllNodes);
    }
    
    public boolean setAlpha(float paramFloat)
    {
      return VectorDrawable.-wrap2(this.mNativeTree.get(), paramFloat);
    }
    
    public final boolean setDensity(int paramInt)
    {
      if (this.mDensity != paramInt)
      {
        int i = this.mDensity;
        this.mDensity = paramInt;
        applyDensityScaling(i, paramInt);
        return true;
      }
      return false;
    }
    
    void setViewportSize(float paramFloat1, float paramFloat2)
    {
      this.mViewportWidth = paramFloat1;
      this.mViewportHeight = paramFloat2;
      VectorDrawable.-wrap35(getNativeRenderer(), paramFloat1, paramFloat2);
    }
    
    public void updateCacheStates()
    {
      this.mCachedThemeAttrs = this.mThemeAttrs;
      this.mCachedTint = this.mTint;
      this.mCachedTintMode = this.mTintMode;
      this.mCachedAutoMirrored = this.mAutoMirrored;
      this.mCacheDirty = false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/VectorDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */