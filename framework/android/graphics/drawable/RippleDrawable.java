package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class RippleDrawable
  extends LayerDrawable
{
  private static final int MASK_CONTENT = 1;
  private static final int MASK_EXPLICIT = 2;
  private static final int MASK_NONE = 0;
  private static final int MASK_UNKNOWN = -1;
  private static final int MAX_RIPPLES = 10;
  public static final int RADIUS_AUTO = -1;
  private RippleBackground mBackground;
  private boolean mBackgroundActive;
  private int mDensity;
  private final Rect mDirtyBounds = new Rect();
  private final Rect mDrawingBounds = new Rect();
  private RippleForeground[] mExitingRipples;
  private int mExitingRipplesCount = 0;
  private boolean mForceSoftware;
  private boolean mHasPending;
  private boolean mHasValidMask;
  private final Rect mHotspotBounds = new Rect();
  private Drawable mMask;
  private Bitmap mMaskBuffer;
  private Canvas mMaskCanvas;
  private PorterDuffColorFilter mMaskColorFilter;
  private Matrix mMaskMatrix;
  private BitmapShader mMaskShader;
  private boolean mOverrideBounds;
  private float mPendingX;
  private float mPendingY;
  private RippleForeground mRipple;
  private boolean mRippleActive;
  private Paint mRipplePaint;
  private RippleState mState;
  private final Rect mTempRect = new Rect();
  
  RippleDrawable()
  {
    this(new RippleState(null, null, null), null);
  }
  
  public RippleDrawable(ColorStateList paramColorStateList, Drawable paramDrawable1, Drawable paramDrawable2)
  {
    this(new RippleState(null, null, null), null);
    if (paramColorStateList == null) {
      throw new IllegalArgumentException("RippleDrawable requires a non-null color");
    }
    if (paramDrawable1 != null) {
      addLayer(paramDrawable1, null, 0, 0, 0, 0, 0);
    }
    if (paramDrawable2 != null) {
      addLayer(paramDrawable2, null, 16908334, 0, 0, 0, 0);
    }
    setColor(paramColorStateList);
    ensurePadding();
    refreshPadding();
    updateLocalState();
  }
  
  private RippleDrawable(RippleState paramRippleState, Resources paramResources)
  {
    this.mState = new RippleState(paramRippleState, this, paramResources);
    this.mLayerState = this.mState;
    this.mDensity = Drawable.resolveDensity(paramResources, this.mState.mDensity);
    if (this.mState.mNum > 0)
    {
      ensurePadding();
      refreshPadding();
    }
    updateLocalState();
  }
  
  private void cancelExitingRipples()
  {
    int j = this.mExitingRipplesCount;
    RippleForeground[] arrayOfRippleForeground = this.mExitingRipples;
    int i = 0;
    while (i < j)
    {
      arrayOfRippleForeground[i].end();
      i += 1;
    }
    if (arrayOfRippleForeground != null) {
      Arrays.fill(arrayOfRippleForeground, 0, j, null);
    }
    this.mExitingRipplesCount = 0;
    invalidateSelf(false);
  }
  
  private void clearHotspots()
  {
    if (this.mRipple != null)
    {
      this.mRipple.end();
      this.mRipple = null;
      this.mRippleActive = false;
    }
    if (this.mBackground != null)
    {
      this.mBackground.end();
      this.mBackground = null;
      this.mBackgroundActive = false;
    }
    cancelExitingRipples();
  }
  
  private void drawBackgroundAndRipples(Canvas paramCanvas)
  {
    RippleForeground localRippleForeground = this.mRipple;
    Object localObject1 = this.mBackground;
    int j = this.mExitingRipplesCount;
    float f1;
    float f2;
    Object localObject2;
    int i;
    int k;
    if ((localRippleForeground != null) || (j > 0) || ((localObject1 != null) && (((RippleBackground)localObject1).isVisible())))
    {
      f1 = this.mHotspotBounds.exactCenterX();
      f2 = this.mHotspotBounds.exactCenterY();
      paramCanvas.translate(f1, f2);
      updateMaskShaderIfNeeded();
      if (this.mMaskShader != null)
      {
        localObject2 = getBounds();
        this.mMaskMatrix.setTranslate(((Rect)localObject2).left - f1, ((Rect)localObject2).top - f2);
        this.mMaskShader.setLocalMatrix(this.mMaskMatrix);
      }
      i = this.mState.mColor.getColorForState(getState(), -16777216);
      k = Color.alpha(i) / 2 << 24;
      localObject2 = getRipplePaint();
      if (this.mMaskColorFilter == null) {
        break label261;
      }
      this.mMaskColorFilter.setColor(i | 0xFF000000);
      ((Paint)localObject2).setColor(k);
      ((Paint)localObject2).setColorFilter(this.mMaskColorFilter);
      ((Paint)localObject2).setShader(this.mMaskShader);
    }
    for (;;)
    {
      if ((localObject1 != null) && (((RippleBackground)localObject1).isVisible())) {
        ((RippleBackground)localObject1).draw(paramCanvas, (Paint)localObject2);
      }
      if (j <= 0) {
        break;
      }
      localObject1 = this.mExitingRipples;
      i = 0;
      while (i < j)
      {
        localObject1[i].draw(paramCanvas, (Paint)localObject2);
        i += 1;
      }
      return;
      label261:
      ((Paint)localObject2).setColor(0xFFFFFF & i | k);
      ((Paint)localObject2).setColorFilter(null);
      ((Paint)localObject2).setShader(null);
    }
    if (localRippleForeground != null) {
      localRippleForeground.draw(paramCanvas, (Paint)localObject2);
    }
    paramCanvas.translate(-f1, -f2);
  }
  
  private void drawContent(Canvas paramCanvas)
  {
    LayerDrawable.ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      if (arrayOfChildDrawable[i].mId != 16908334) {
        arrayOfChildDrawable[i].mDrawable.draw(paramCanvas);
      }
      i += 1;
    }
  }
  
  private void drawMask(Canvas paramCanvas)
  {
    this.mMask.draw(paramCanvas);
  }
  
  private int getMaskType()
  {
    if ((this.mRipple != null) || (this.mExitingRipplesCount > 0) || ((this.mBackground != null) && (this.mBackground.isVisible())))
    {
      if (this.mMask == null) {
        break label55;
      }
      if (this.mMask.getOpacity() == -1) {
        return 0;
      }
    }
    else
    {
      return -1;
    }
    return 2;
    label55:
    LayerDrawable.ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      if (arrayOfChildDrawable[i].mDrawable.getOpacity() != -1) {
        return 1;
      }
      i += 1;
    }
    return 0;
  }
  
  private Paint getRipplePaint()
  {
    if (this.mRipplePaint == null)
    {
      this.mRipplePaint = new Paint();
      this.mRipplePaint.setAntiAlias(true);
      this.mRipplePaint.setStyle(Paint.Style.FILL);
    }
    return this.mRipplePaint;
  }
  
  private boolean isBounded()
  {
    boolean bool = false;
    if (getNumberOfLayers() > 0) {
      bool = true;
    }
    return bool;
  }
  
  private void onHotspotBoundsChanged()
  {
    int j = this.mExitingRipplesCount;
    RippleForeground[] arrayOfRippleForeground = this.mExitingRipples;
    int i = 0;
    while (i < j)
    {
      arrayOfRippleForeground[i].onHotspotBoundsChanged();
      i += 1;
    }
    if (this.mRipple != null) {
      this.mRipple.onHotspotBoundsChanged();
    }
    if (this.mBackground != null) {
      this.mBackground.onHotspotBoundsChanged();
    }
  }
  
  private void pruneRipples()
  {
    RippleForeground[] arrayOfRippleForeground = this.mExitingRipples;
    int m = this.mExitingRipplesCount;
    int j = 0;
    int i = 0;
    if (j < m)
    {
      if (arrayOfRippleForeground[j].hasFinishedExit()) {
        break label79;
      }
      int k = i + 1;
      arrayOfRippleForeground[i] = arrayOfRippleForeground[j];
      i = k;
    }
    label79:
    for (;;)
    {
      j += 1;
      break;
      j = i;
      while (j < m)
      {
        arrayOfRippleForeground[j] = null;
        j += 1;
      }
      this.mExitingRipplesCount = i;
      return;
    }
  }
  
  private void setBackgroundActive(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mBackgroundActive != paramBoolean1)
    {
      this.mBackgroundActive = paramBoolean1;
      if (paramBoolean1) {
        tryBackgroundEnter(paramBoolean2);
      }
    }
    else
    {
      return;
    }
    tryBackgroundExit();
  }
  
  private void setRippleActive(boolean paramBoolean)
  {
    if (this.mRippleActive != paramBoolean)
    {
      this.mRippleActive = paramBoolean;
      if (paramBoolean) {
        tryRippleEnter();
      }
    }
    else
    {
      return;
    }
    tryRippleExit();
  }
  
  private void tryBackgroundEnter(boolean paramBoolean)
  {
    if (this.mBackground == null)
    {
      boolean bool = isBounded();
      this.mBackground = new RippleBackground(this, this.mHotspotBounds, bool, this.mForceSoftware);
    }
    this.mBackground.setup(this.mState.mMaxRadius, this.mDensity);
    this.mBackground.enter(paramBoolean);
  }
  
  private void tryBackgroundExit()
  {
    if (this.mBackground != null) {
      this.mBackground.exit();
    }
  }
  
  private void tryRippleEnter()
  {
    if (this.mExitingRipplesCount >= 10) {
      return;
    }
    float f1;
    if (this.mRipple == null)
    {
      if (!this.mHasPending) {
        break label95;
      }
      this.mHasPending = false;
      f1 = this.mPendingX;
    }
    for (float f2 = this.mPendingY;; f2 = this.mHotspotBounds.exactCenterY())
    {
      boolean bool = isBounded();
      this.mRipple = new RippleForeground(this, this.mHotspotBounds, f1, f2, bool, this.mForceSoftware);
      this.mRipple.setup(this.mState.mMaxRadius, this.mDensity);
      this.mRipple.enter(false);
      return;
      label95:
      f1 = this.mHotspotBounds.exactCenterX();
    }
  }
  
  private void tryRippleExit()
  {
    if (this.mRipple != null)
    {
      if (this.mExitingRipples == null) {
        this.mExitingRipples = new RippleForeground[10];
      }
      RippleForeground[] arrayOfRippleForeground = this.mExitingRipples;
      int i = this.mExitingRipplesCount;
      this.mExitingRipplesCount = (i + 1);
      arrayOfRippleForeground[i] = this.mRipple;
      this.mRipple.exit();
      this.mRipple = null;
    }
  }
  
  private void updateLocalState()
  {
    this.mMask = findDrawableByLayerId(16908334);
  }
  
  private void updateMaskShaderIfNeeded()
  {
    if (this.mHasValidMask) {
      return;
    }
    int i = getMaskType();
    if (i == -1) {
      return;
    }
    this.mHasValidMask = true;
    Rect localRect = getBounds();
    if ((i == 0) || (localRect.isEmpty()))
    {
      if (this.mMaskBuffer != null)
      {
        this.mMaskBuffer.recycle();
        this.mMaskBuffer = null;
        this.mMaskShader = null;
        this.mMaskCanvas = null;
      }
      this.mMaskMatrix = null;
      this.mMaskColorFilter = null;
      return;
    }
    label174:
    label192:
    int j;
    int k;
    if ((this.mMaskBuffer == null) || (this.mMaskBuffer.getWidth() != localRect.width()))
    {
      if (this.mMaskBuffer != null) {
        this.mMaskBuffer.recycle();
      }
      this.mMaskBuffer = Bitmap.createBitmap(localRect.width(), localRect.height(), Bitmap.Config.ALPHA_8);
      this.mMaskShader = new BitmapShader(this.mMaskBuffer, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      this.mMaskCanvas = new Canvas(this.mMaskBuffer);
      if (this.mMaskMatrix != null) {
        break label290;
      }
      this.mMaskMatrix = new Matrix();
      if (this.mMaskColorFilter == null) {
        this.mMaskColorFilter = new PorterDuffColorFilter(0, PorterDuff.Mode.SRC_IN);
      }
      j = localRect.left;
      k = localRect.top;
      this.mMaskCanvas.translate(-j, -k);
      if (i != 2) {
        break label300;
      }
      drawMask(this.mMaskCanvas);
    }
    for (;;)
    {
      this.mMaskCanvas.translate(j, k);
      return;
      if (this.mMaskBuffer.getHeight() != localRect.height()) {
        break;
      }
      this.mMaskBuffer.eraseColor(0);
      break label174;
      label290:
      this.mMaskMatrix.reset();
      break label192;
      label300:
      if (i == 1) {
        drawContent(this.mMaskCanvas);
      }
    }
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    Object localObject = this.mState;
    ((RippleState)localObject).mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    ((RippleState)localObject).mTouchThemeAttrs = paramTypedArray.extractThemeAttrs();
    localObject = paramTypedArray.getColorStateList(0);
    if (localObject != null) {
      this.mState.mColor = ((ColorStateList)localObject);
    }
    this.mState.mMaxRadius = paramTypedArray.getDimensionPixelSize(1, this.mState.mMaxRadius);
  }
  
  private void verifyRequiredAttributes(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    if ((this.mState.mColor == null) && ((this.mState.mTouchThemeAttrs == null) || (this.mState.mTouchThemeAttrs[0] == 0))) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <ripple> requires a valid color attribute");
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    RippleState localRippleState = this.mState;
    if (localRippleState == null) {
      return;
    }
    if (localRippleState.mTouchThemeAttrs != null) {
      localTypedArray = paramTheme.resolveAttributes(localRippleState.mTouchThemeAttrs, R.styleable.RippleDrawable);
    }
    try
    {
      updateStateFromTypedArray(localTypedArray);
      verifyRequiredAttributes(localTypedArray);
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      for (;;)
      {
        rethrowAsRuntimeException(localXmlPullParserException);
        localTypedArray.recycle();
      }
    }
    finally
    {
      localTypedArray.recycle();
    }
    if ((localRippleState.mColor != null) && (localRippleState.mColor.canApplyTheme())) {
      localRippleState.mColor = localRippleState.mColor.obtainForTheme(paramTheme);
    }
    updateLocalState();
  }
  
  public boolean canApplyTheme()
  {
    if ((this.mState == null) || (!this.mState.canApplyTheme())) {
      return super.canApplyTheme();
    }
    return true;
  }
  
  RippleState createConstantState(LayerDrawable.LayerState paramLayerState, Resources paramResources)
  {
    return new RippleState(paramLayerState, this, paramResources);
  }
  
  public void draw(Canvas paramCanvas)
  {
    pruneRipples();
    Rect localRect = getDirtyBounds();
    int i = paramCanvas.save(2);
    paramCanvas.clipRect(localRect);
    drawContent(paramCanvas);
    drawBackgroundAndRipples(paramCanvas);
    paramCanvas.restoreToCount(i);
  }
  
  public Drawable.ConstantState getConstantState()
  {
    return this.mState;
  }
  
  public Rect getDirtyBounds()
  {
    if (!isBounded())
    {
      Rect localRect1 = this.mDrawingBounds;
      Rect localRect2 = this.mDirtyBounds;
      localRect2.set(localRect1);
      localRect1.setEmpty();
      int j = (int)this.mHotspotBounds.exactCenterX();
      int k = (int)this.mHotspotBounds.exactCenterY();
      Rect localRect3 = this.mTempRect;
      Object localObject = this.mExitingRipples;
      int m = this.mExitingRipplesCount;
      int i = 0;
      while (i < m)
      {
        localObject[i].getBounds(localRect3);
        localRect3.offset(j, k);
        localRect1.union(localRect3);
        i += 1;
      }
      localObject = this.mBackground;
      if (localObject != null)
      {
        ((RippleBackground)localObject).getBounds(localRect3);
        localRect3.offset(j, k);
        localRect1.union(localRect3);
      }
      localRect2.union(localRect1);
      localRect2.union(super.getDirtyBounds());
      return localRect2;
    }
    return getBounds();
  }
  
  public void getHotspotBounds(Rect paramRect)
  {
    paramRect.set(this.mHotspotBounds);
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public void getOutline(Outline paramOutline)
  {
    LayerDrawable.LayerState localLayerState = this.mLayerState;
    LayerDrawable.ChildDrawable[] arrayOfChildDrawable = localLayerState.mChildren;
    int j = localLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      if (arrayOfChildDrawable[i].mId != 16908334)
      {
        arrayOfChildDrawable[i].mDrawable.getOutline(paramOutline);
        if (!paramOutline.isEmpty()) {
          return;
        }
      }
      i += 1;
    }
  }
  
  public int getRadius()
  {
    return this.mState.mMaxRadius;
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.RippleDrawable);
    setPaddingMode(1);
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    updateStateFromTypedArray(localTypedArray);
    verifyRequiredAttributes(localTypedArray);
    localTypedArray.recycle();
    updateLocalState();
  }
  
  public void invalidateSelf()
  {
    invalidateSelf(true);
  }
  
  void invalidateSelf(boolean paramBoolean)
  {
    super.invalidateSelf();
    if (paramBoolean) {
      this.mHasValidMask = false;
    }
  }
  
  public boolean isProjected()
  {
    if (isBounded()) {
      return false;
    }
    int i = this.mState.mMaxRadius;
    Rect localRect1 = getBounds();
    Rect localRect2 = this.mHotspotBounds;
    return (i == -1) || (i > localRect2.width() / 2) || (i > localRect2.height() / 2) || ((!localRect1.equals(localRect2)) && (!localRect1.contains(localRect2)));
  }
  
  public boolean isStateful()
  {
    return true;
  }
  
  public void jumpToCurrentState()
  {
    super.jumpToCurrentState();
    if (this.mRipple != null) {
      this.mRipple.end();
    }
    if (this.mBackground != null) {
      this.mBackground.end();
    }
    cancelExitingRipples();
  }
  
  public Drawable mutate()
  {
    super.mutate();
    this.mState = ((RippleState)this.mLayerState);
    this.mMask = findDrawableByLayerId(16908334);
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    if (!this.mOverrideBounds)
    {
      this.mHotspotBounds.set(paramRect);
      onHotspotBoundsChanged();
    }
    if (this.mBackground != null) {
      this.mBackground.onBoundsChange();
    }
    if (this.mRipple != null) {
      this.mRipple.onBoundsChange();
    }
    invalidateSelf();
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    boolean bool4 = super.onStateChange(paramArrayOfInt);
    int k = 0;
    boolean bool1 = false;
    int j = 0;
    boolean bool2 = false;
    int i1 = paramArrayOfInt.length;
    int i = 0;
    boolean bool3;
    if (i < i1)
    {
      int i2 = paramArrayOfInt[i];
      int m;
      int n;
      if (i2 == 16842910)
      {
        m = 1;
        bool3 = bool1;
        n = j;
      }
      for (;;)
      {
        i += 1;
        k = m;
        j = n;
        bool1 = bool3;
        break;
        if (i2 == 16842908)
        {
          n = 1;
          m = k;
          bool3 = bool1;
        }
        else if (i2 == 16842919)
        {
          bool3 = true;
          m = k;
          n = j;
        }
        else
        {
          m = k;
          n = j;
          bool3 = bool1;
          if (i2 == 16843623)
          {
            bool2 = true;
            m = k;
            n = j;
            bool3 = bool1;
          }
        }
      }
    }
    if (k != 0)
    {
      bool3 = bool1;
      setRippleActive(bool3);
      if ((bool2) || (j != 0)) {
        break label200;
      }
      if (k == 0) {
        break label206;
      }
      label179:
      if (j != 0) {
        break label212;
      }
    }
    for (;;)
    {
      setBackgroundActive(bool1, bool2);
      return bool4;
      bool3 = false;
      break;
      label200:
      bool1 = true;
      break label179;
      label206:
      bool1 = false;
      break label179;
      label212:
      bool2 = true;
    }
  }
  
  public void setColor(ColorStateList paramColorStateList)
  {
    this.mState.mColor = paramColorStateList;
    invalidateSelf(false);
  }
  
  public boolean setDrawableByLayerId(int paramInt, Drawable paramDrawable)
  {
    if (super.setDrawableByLayerId(paramInt, paramDrawable))
    {
      if (paramInt == 16908334)
      {
        this.mMask = paramDrawable;
        this.mHasValidMask = false;
      }
      return true;
    }
    return false;
  }
  
  public void setForceSoftware(boolean paramBoolean)
  {
    this.mForceSoftware = paramBoolean;
  }
  
  public void setHotspot(float paramFloat1, float paramFloat2)
  {
    if ((this.mRipple == null) || (this.mBackground == null))
    {
      this.mPendingX = paramFloat1;
      this.mPendingY = paramFloat2;
      this.mHasPending = true;
    }
    if (this.mRipple != null) {
      this.mRipple.move(paramFloat1, paramFloat2);
    }
  }
  
  public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mOverrideBounds = true;
    this.mHotspotBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
    onHotspotBoundsChanged();
  }
  
  public void setPaddingMode(int paramInt)
  {
    super.setPaddingMode(paramInt);
  }
  
  public void setRadius(int paramInt)
  {
    this.mState.mMaxRadius = paramInt;
    invalidateSelf(false);
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    paramBoolean2 = super.setVisible(paramBoolean1, paramBoolean2);
    if (!paramBoolean1) {
      clearHotspots();
    }
    while (!paramBoolean2) {
      return paramBoolean2;
    }
    if (this.mRippleActive) {
      tryRippleEnter();
    }
    if (this.mBackgroundActive) {
      tryBackgroundEnter(false);
    }
    jumpToCurrentState();
    return paramBoolean2;
  }
  
  static class RippleState
    extends LayerDrawable.LayerState
  {
    ColorStateList mColor = ColorStateList.valueOf(-65281);
    int mMaxRadius = -1;
    int[] mTouchThemeAttrs;
    
    public RippleState(LayerDrawable.LayerState paramLayerState, RippleDrawable paramRippleDrawable, Resources paramResources)
    {
      super(paramRippleDrawable, paramResources);
      if ((paramLayerState != null) && ((paramLayerState instanceof RippleState)))
      {
        paramRippleDrawable = (RippleState)paramLayerState;
        this.mTouchThemeAttrs = paramRippleDrawable.mTouchThemeAttrs;
        this.mColor = paramRippleDrawable.mColor;
        this.mMaxRadius = paramRippleDrawable.mMaxRadius;
        if (paramRippleDrawable.mDensity != this.mDensity) {
          applyDensityScaling(paramLayerState.mDensity, this.mDensity);
        }
      }
    }
    
    private void applyDensityScaling(int paramInt1, int paramInt2)
    {
      if (this.mMaxRadius != -1) {
        this.mMaxRadius = Drawable.scaleFromDensity(this.mMaxRadius, paramInt1, paramInt2, true);
      }
    }
    
    public boolean canApplyTheme()
    {
      if ((this.mTouchThemeAttrs == null) && ((this.mColor == null) || (!this.mColor.canApplyTheme()))) {
        return super.canApplyTheme();
      }
      return true;
    }
    
    public int getChangingConfigurations()
    {
      int j = super.getChangingConfigurations();
      if (this.mColor != null) {}
      for (int i = this.mColor.getChangingConfigurations();; i = 0) {
        return i | j;
      }
    }
    
    public Drawable newDrawable()
    {
      return new RippleDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new RippleDrawable(this, paramResources, null);
    }
    
    protected void onDensityChanged(int paramInt1, int paramInt2)
    {
      super.onDensityChanged(paramInt1, paramInt2);
      applyDensityScaling(paramInt1, paramInt2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/RippleDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */