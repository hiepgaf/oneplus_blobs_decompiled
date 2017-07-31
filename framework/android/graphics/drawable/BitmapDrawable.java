package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class BitmapDrawable
  extends Drawable
{
  private static final int DEFAULT_PAINT_FLAGS = 6;
  private static final int TILE_MODE_CLAMP = 0;
  private static final int TILE_MODE_DISABLED = -1;
  private static final int TILE_MODE_MIRROR = 2;
  private static final int TILE_MODE_REPEAT = 1;
  private static final int TILE_MODE_UNDEFINED = -2;
  private int mBitmapHeight;
  private BitmapState mBitmapState;
  private int mBitmapWidth;
  private final Rect mDstRect = new Rect();
  private boolean mDstRectAndInsetsDirty = true;
  private Matrix mMirrorMatrix;
  private boolean mMutated;
  private Insets mOpticalInsets = Insets.NONE;
  private int mTargetDensity = 160;
  private PorterDuffColorFilter mTintFilter;
  
  @Deprecated
  public BitmapDrawable()
  {
    this.mBitmapState = new BitmapState((Bitmap)null);
  }
  
  @Deprecated
  public BitmapDrawable(Resources paramResources)
  {
    this.mBitmapState = new BitmapState((Bitmap)null);
    this.mBitmapState.mTargetDensity = this.mTargetDensity;
  }
  
  public BitmapDrawable(Resources paramResources, Bitmap paramBitmap)
  {
    this(new BitmapState(paramBitmap), paramResources);
    this.mBitmapState.mTargetDensity = this.mTargetDensity;
  }
  
  public BitmapDrawable(Resources paramResources, InputStream paramInputStream)
  {
    this(new BitmapState(BitmapFactory.decodeStream(paramInputStream)), null);
    this.mBitmapState.mTargetDensity = this.mTargetDensity;
    if (this.mBitmapState.mBitmap == null) {
      Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + paramInputStream);
    }
  }
  
  public BitmapDrawable(Resources paramResources, String paramString)
  {
    this(new BitmapState(BitmapFactory.decodeFile(paramString)), null);
    this.mBitmapState.mTargetDensity = this.mTargetDensity;
    if (this.mBitmapState.mBitmap == null) {
      Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + paramString);
    }
  }
  
  @Deprecated
  public BitmapDrawable(Bitmap paramBitmap)
  {
    this(new BitmapState(paramBitmap), null);
  }
  
  private BitmapDrawable(BitmapState paramBitmapState, Resources paramResources)
  {
    this.mBitmapState = paramBitmapState;
    updateLocalState(paramResources);
  }
  
  @Deprecated
  public BitmapDrawable(InputStream paramInputStream)
  {
    this(new BitmapState(BitmapFactory.decodeStream(paramInputStream)), null);
    if (this.mBitmapState.mBitmap == null) {
      Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + paramInputStream);
    }
  }
  
  @Deprecated
  public BitmapDrawable(String paramString)
  {
    this(new BitmapState(BitmapFactory.decodeFile(paramString)), null);
    if (this.mBitmapState.mBitmap == null) {
      Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + paramString);
    }
  }
  
  private void computeBitmapSize()
  {
    Bitmap localBitmap = this.mBitmapState.mBitmap;
    if (localBitmap != null)
    {
      this.mBitmapWidth = localBitmap.getScaledWidth(this.mTargetDensity);
      this.mBitmapHeight = localBitmap.getScaledHeight(this.mTargetDensity);
      return;
    }
    this.mBitmapHeight = -1;
    this.mBitmapWidth = -1;
  }
  
  private Matrix getOrCreateMirrorMatrix()
  {
    if (this.mMirrorMatrix == null) {
      this.mMirrorMatrix = new Matrix();
    }
    return this.mMirrorMatrix;
  }
  
  private boolean needMirroring()
  {
    return (isAutoMirrored()) && (getLayoutDirection() == 1);
  }
  
  private static Shader.TileMode parseTileMode(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return Shader.TileMode.CLAMP;
    case 1: 
      return Shader.TileMode.REPEAT;
    }
    return Shader.TileMode.MIRROR;
  }
  
  private void updateDstRectAndInsetsIfDirty()
  {
    Rect localRect;
    if (this.mDstRectAndInsetsDirty)
    {
      if ((this.mBitmapState.mTileModeX != null) || (this.mBitmapState.mTileModeY != null)) {
        break label122;
      }
      localRect = getBounds();
      int i = getLayoutDirection();
      Gravity.apply(this.mBitmapState.mGravity, this.mBitmapWidth, this.mBitmapHeight, localRect, this.mDstRect, i);
    }
    for (this.mOpticalInsets = Insets.of(this.mDstRect.left - localRect.left, this.mDstRect.top - localRect.top, localRect.right - this.mDstRect.right, localRect.bottom - this.mDstRect.bottom);; this.mOpticalInsets = Insets.NONE)
    {
      this.mDstRectAndInsetsDirty = false;
      return;
      label122:
      copyBounds(this.mDstRect);
    }
  }
  
  private void updateLocalState(Resources paramResources)
  {
    this.mTargetDensity = resolveDensity(paramResources, this.mBitmapState.mTargetDensity);
    this.mTintFilter = updateTintFilter(this.mTintFilter, this.mBitmapState.mTint, this.mBitmapState.mTintMode);
    computeBitmapSize();
  }
  
  private void updateShaderMatrix(Bitmap paramBitmap, Paint paramPaint, Shader paramShader, boolean paramBoolean)
  {
    int j = 0;
    int k = paramBitmap.getDensity();
    int m = this.mTargetDensity;
    int i = j;
    if (k != 0)
    {
      i = j;
      if (k != m) {
        i = 1;
      }
    }
    if ((i != 0) || (paramBoolean))
    {
      paramBitmap = getOrCreateMirrorMatrix();
      paramBitmap.reset();
      if (paramBoolean)
      {
        paramBitmap.setTranslate(this.mDstRect.right - this.mDstRect.left, 0.0F);
        paramBitmap.setScale(-1.0F, 1.0F);
      }
      if (i != 0)
      {
        float f = m / k;
        paramBitmap.postScale(f, f);
      }
      paramShader.setLocalMatrix(paramBitmap);
    }
    for (;;)
    {
      paramPaint.setShader(paramShader);
      return;
      this.mMirrorMatrix = null;
      paramShader.setLocalMatrix(Matrix.IDENTITY_MATRIX);
    }
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    Resources localResources = paramTypedArray.getResources();
    BitmapState localBitmapState = this.mBitmapState;
    localBitmapState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    localBitmapState.mThemeAttrs = paramTypedArray.extractThemeAttrs();
    int i = paramTypedArray.getResourceId(1, 0);
    Object localObject;
    if (i != 0)
    {
      localObject = BitmapFactory.decodeResource(localResources, i);
      if (localObject == null) {
        throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <bitmap> requires a valid 'src' attribute");
      }
      localBitmapState.mBitmap = ((Bitmap)localObject);
    }
    localBitmapState.mTargetDensity = localResources.getDisplayMetrics().densityDpi;
    if (localBitmapState.mBitmap != null) {}
    for (boolean bool = localBitmapState.mBitmap.hasMipMap();; bool = false)
    {
      setMipMap(paramTypedArray.getBoolean(8, bool));
      localBitmapState.mAutoMirrored = paramTypedArray.getBoolean(9, localBitmapState.mAutoMirrored);
      localBitmapState.mBaseAlpha = paramTypedArray.getFloat(7, localBitmapState.mBaseAlpha);
      i = paramTypedArray.getInt(10, -1);
      if (i != -1) {
        localBitmapState.mTintMode = Drawable.parseTintMode(i, PorterDuff.Mode.SRC_IN);
      }
      localObject = paramTypedArray.getColorStateList(5);
      if (localObject != null) {
        localBitmapState.mTint = ((ColorStateList)localObject);
      }
      localObject = this.mBitmapState.mPaint;
      ((Paint)localObject).setAntiAlias(paramTypedArray.getBoolean(2, ((Paint)localObject).isAntiAlias()));
      ((Paint)localObject).setFilterBitmap(paramTypedArray.getBoolean(3, ((Paint)localObject).isFilterBitmap()));
      ((Paint)localObject).setDither(paramTypedArray.getBoolean(4, ((Paint)localObject).isDither()));
      setGravity(paramTypedArray.getInt(0, localBitmapState.mGravity));
      i = paramTypedArray.getInt(6, -2);
      if (i != -2)
      {
        localObject = parseTileMode(i);
        setTileModeXY((Shader.TileMode)localObject, (Shader.TileMode)localObject);
      }
      i = paramTypedArray.getInt(11, -2);
      if (i != -2) {
        setTileModeX(parseTileMode(i));
      }
      i = paramTypedArray.getInt(12, -2);
      if (i != -2) {
        setTileModeY(parseTileMode(i));
      }
      localBitmapState.mTargetDensity = Drawable.resolveDensity(localResources, 0);
      return;
    }
  }
  
  private void verifyRequiredAttributes(TypedArray paramTypedArray)
    throws XmlPullParserException
  {
    BitmapState localBitmapState = this.mBitmapState;
    if ((localBitmapState.mBitmap == null) && ((localBitmapState.mThemeAttrs == null) || (localBitmapState.mThemeAttrs[1] == 0))) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + ": <bitmap> requires a valid 'src' attribute");
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    BitmapState localBitmapState = this.mBitmapState;
    if (localBitmapState == null) {
      return;
    }
    if (localBitmapState.mThemeAttrs != null) {
      localTypedArray = paramTheme.resolveAttributes(localBitmapState.mThemeAttrs, R.styleable.BitmapDrawable);
    }
    try
    {
      updateStateFromTypedArray(localTypedArray);
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
    if ((localBitmapState.mTint != null) && (localBitmapState.mTint.canApplyTheme())) {
      localBitmapState.mTint = localBitmapState.mTint.obtainForTheme(paramTheme);
    }
    updateLocalState(paramTheme.getResources());
  }
  
  public boolean canApplyTheme()
  {
    if (this.mBitmapState != null) {
      return this.mBitmapState.canApplyTheme();
    }
    return false;
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mMutated = false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    Bitmap localBitmap = this.mBitmapState.mBitmap;
    if (localBitmap == null) {
      return;
    }
    BitmapState localBitmapState = this.mBitmapState;
    Paint localPaint = localBitmapState.mPaint;
    Object localObject2;
    Shader.TileMode localTileMode;
    Object localObject1;
    int i;
    label113:
    int j;
    label140:
    boolean bool;
    if (localBitmapState.mRebuildShader)
    {
      localObject2 = localBitmapState.mTileModeX;
      localTileMode = localBitmapState.mTileModeY;
      if ((localObject2 == null) && (localTileMode == null))
      {
        localPaint.setShader(null);
        localBitmapState.mRebuildShader = false;
      }
    }
    else
    {
      if (localBitmapState.mBaseAlpha == 1.0F) {
        break label294;
      }
      localObject1 = getPaint();
      i = ((Paint)localObject1).getAlpha();
      ((Paint)localObject1).setAlpha((int)(i * localBitmapState.mBaseAlpha + 0.5F));
      if ((this.mTintFilter == null) || (localPaint.getColorFilter() != null)) {
        break label299;
      }
      localPaint.setColorFilter(this.mTintFilter);
      j = 1;
      updateDstRectAndInsetsIfDirty();
      localObject1 = localPaint.getShader();
      bool = needMirroring();
      if (localObject1 != null) {
        break label304;
      }
      if (bool)
      {
        paramCanvas.save();
        paramCanvas.translate(this.mDstRect.right - this.mDstRect.left, 0.0F);
        paramCanvas.scale(-1.0F, 1.0F);
      }
      paramCanvas.drawBitmap(localBitmap, null, this.mDstRect, localPaint);
      if (bool) {
        paramCanvas.restore();
      }
    }
    for (;;)
    {
      if (j != 0) {
        localPaint.setColorFilter(null);
      }
      if (i >= 0) {
        localPaint.setAlpha(i);
      }
      return;
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = Shader.TileMode.CLAMP;
      }
      localObject2 = localTileMode;
      if (localTileMode == null) {
        localObject2 = Shader.TileMode.CLAMP;
      }
      localPaint.setShader(new BitmapShader(localBitmap, (Shader.TileMode)localObject1, (Shader.TileMode)localObject2));
      break;
      label294:
      i = -1;
      break label113;
      label299:
      j = 0;
      break label140;
      label304:
      updateShaderMatrix(localBitmap, localPaint, (Shader)localObject1, bool);
      paramCanvas.drawRect(this.mDstRect, localPaint);
    }
  }
  
  public int getAlpha()
  {
    return this.mBitmapState.mPaint.getAlpha();
  }
  
  public final Bitmap getBitmap()
  {
    return this.mBitmapState.mBitmap;
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mBitmapState.getChangingConfigurations();
  }
  
  public ColorFilter getColorFilter()
  {
    return this.mBitmapState.mPaint.getColorFilter();
  }
  
  public final Drawable.ConstantState getConstantState()
  {
    BitmapState localBitmapState = this.mBitmapState;
    localBitmapState.mChangingConfigurations |= getChangingConfigurations();
    return this.mBitmapState;
  }
  
  public int getGravity()
  {
    return this.mBitmapState.mGravity;
  }
  
  public int getIntrinsicHeight()
  {
    return this.mBitmapHeight;
  }
  
  public int getIntrinsicWidth()
  {
    return this.mBitmapWidth;
  }
  
  public int getOpacity()
  {
    if (this.mBitmapState.mGravity != 119) {
      return -3;
    }
    Bitmap localBitmap = this.mBitmapState.mBitmap;
    if ((localBitmap == null) || (localBitmap.hasAlpha()) || (this.mBitmapState.mPaint.getAlpha() < 255)) {
      return -3;
    }
    return -1;
  }
  
  public Insets getOpticalInsets()
  {
    updateDstRectAndInsetsIfDirty();
    return this.mOpticalInsets;
  }
  
  public void getOutline(Outline paramOutline)
  {
    int j = 0;
    updateDstRectAndInsetsIfDirty();
    paramOutline.setRect(this.mDstRect);
    int i = j;
    if (this.mBitmapState.mBitmap != null)
    {
      if (this.mBitmapState.mBitmap.hasAlpha()) {
        i = j;
      }
    }
    else {
      if (i == 0) {
        break label69;
      }
    }
    label69:
    for (float f = getAlpha() / 255.0F;; f = 0.0F)
    {
      paramOutline.setAlpha(f);
      return;
      i = 1;
      break;
    }
  }
  
  public final Paint getPaint()
  {
    return this.mBitmapState.mPaint;
  }
  
  public Shader.TileMode getTileModeX()
  {
    return this.mBitmapState.mTileModeX;
  }
  
  public Shader.TileMode getTileModeY()
  {
    return this.mBitmapState.mTileModeY;
  }
  
  public ColorStateList getTint()
  {
    return this.mBitmapState.mTint;
  }
  
  public PorterDuff.Mode getTintMode()
  {
    return this.mBitmapState.mTintMode;
  }
  
  public boolean hasAntiAlias()
  {
    return this.mBitmapState.mPaint.isAntiAlias();
  }
  
  public boolean hasMipMap()
  {
    if (this.mBitmapState.mBitmap != null) {
      return this.mBitmapState.mBitmap.hasMipMap();
    }
    return false;
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    paramXmlPullParser = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.BitmapDrawable);
    updateStateFromTypedArray(paramXmlPullParser);
    verifyRequiredAttributes(paramXmlPullParser);
    paramXmlPullParser.recycle();
    updateLocalState(paramResources);
  }
  
  public final boolean isAutoMirrored()
  {
    return this.mBitmapState.mAutoMirrored;
  }
  
  public boolean isFilterBitmap()
  {
    return this.mBitmapState.mPaint.isFilterBitmap();
  }
  
  public boolean isStateful()
  {
    if ((this.mBitmapState.mTint == null) || (!this.mBitmapState.mTint.isStateful())) {
      return super.isStateful();
    }
    return true;
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mBitmapState = new BitmapState(this.mBitmapState);
      this.mMutated = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    this.mDstRectAndInsetsDirty = true;
    paramRect = this.mBitmapState.mBitmap;
    Shader localShader = this.mBitmapState.mPaint.getShader();
    if ((paramRect != null) && (localShader != null)) {
      updateShaderMatrix(paramRect, this.mBitmapState.mPaint, localShader, needMirroring());
    }
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    paramArrayOfInt = this.mBitmapState;
    if ((paramArrayOfInt.mTint != null) && (paramArrayOfInt.mTintMode != null))
    {
      this.mTintFilter = updateTintFilter(this.mTintFilter, paramArrayOfInt.mTint, paramArrayOfInt.mTintMode);
      return true;
    }
    return false;
  }
  
  public void setAlpha(int paramInt)
  {
    if (paramInt != this.mBitmapState.mPaint.getAlpha())
    {
      this.mBitmapState.mPaint.setAlpha(paramInt);
      invalidateSelf();
    }
  }
  
  public void setAntiAlias(boolean paramBoolean)
  {
    this.mBitmapState.mPaint.setAntiAlias(paramBoolean);
    invalidateSelf();
  }
  
  public void setAutoMirrored(boolean paramBoolean)
  {
    if (this.mBitmapState.mAutoMirrored != paramBoolean)
    {
      this.mBitmapState.mAutoMirrored = paramBoolean;
      invalidateSelf();
    }
  }
  
  public void setBitmap(Bitmap paramBitmap)
  {
    if (this.mBitmapState.mBitmap != paramBitmap)
    {
      this.mBitmapState.mBitmap = paramBitmap;
      computeBitmapSize();
      invalidateSelf();
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mBitmapState.mPaint.setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  public void setDither(boolean paramBoolean)
  {
    this.mBitmapState.mPaint.setDither(paramBoolean);
    invalidateSelf();
  }
  
  public void setFilterBitmap(boolean paramBoolean)
  {
    this.mBitmapState.mPaint.setFilterBitmap(paramBoolean);
    invalidateSelf();
  }
  
  public void setGravity(int paramInt)
  {
    if (this.mBitmapState.mGravity != paramInt)
    {
      this.mBitmapState.mGravity = paramInt;
      this.mDstRectAndInsetsDirty = true;
      invalidateSelf();
    }
  }
  
  public void setMipMap(boolean paramBoolean)
  {
    if (this.mBitmapState.mBitmap != null)
    {
      this.mBitmapState.mBitmap.setHasMipMap(paramBoolean);
      invalidateSelf();
    }
  }
  
  public void setTargetDensity(int paramInt)
  {
    if (this.mTargetDensity != paramInt)
    {
      int i = paramInt;
      if (paramInt == 0) {
        i = 160;
      }
      this.mTargetDensity = i;
      if (this.mBitmapState.mBitmap != null) {
        computeBitmapSize();
      }
      invalidateSelf();
    }
  }
  
  public void setTargetDensity(Canvas paramCanvas)
  {
    setTargetDensity(paramCanvas.getDensity());
  }
  
  public void setTargetDensity(DisplayMetrics paramDisplayMetrics)
  {
    setTargetDensity(paramDisplayMetrics.densityDpi);
  }
  
  public void setTileModeX(Shader.TileMode paramTileMode)
  {
    setTileModeXY(paramTileMode, this.mBitmapState.mTileModeY);
  }
  
  public void setTileModeXY(Shader.TileMode paramTileMode1, Shader.TileMode paramTileMode2)
  {
    BitmapState localBitmapState = this.mBitmapState;
    if ((localBitmapState.mTileModeX != paramTileMode1) || (localBitmapState.mTileModeY != paramTileMode2))
    {
      localBitmapState.mTileModeX = paramTileMode1;
      localBitmapState.mTileModeY = paramTileMode2;
      localBitmapState.mRebuildShader = true;
      this.mDstRectAndInsetsDirty = true;
      invalidateSelf();
    }
  }
  
  public final void setTileModeY(Shader.TileMode paramTileMode)
  {
    setTileModeXY(this.mBitmapState.mTileModeX, paramTileMode);
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    BitmapState localBitmapState = this.mBitmapState;
    if (localBitmapState.mTint != paramColorStateList)
    {
      localBitmapState.mTint = paramColorStateList;
      this.mTintFilter = updateTintFilter(this.mTintFilter, paramColorStateList, this.mBitmapState.mTintMode);
      invalidateSelf();
    }
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    BitmapState localBitmapState = this.mBitmapState;
    if (localBitmapState.mTintMode != paramMode)
    {
      localBitmapState.mTintMode = paramMode;
      this.mTintFilter = updateTintFilter(this.mTintFilter, this.mBitmapState.mTint, paramMode);
      invalidateSelf();
    }
  }
  
  public void setXfermode(Xfermode paramXfermode)
  {
    this.mBitmapState.mPaint.setXfermode(paramXfermode);
    invalidateSelf();
  }
  
  static final class BitmapState
    extends Drawable.ConstantState
  {
    boolean mAutoMirrored = false;
    float mBaseAlpha = 1.0F;
    Bitmap mBitmap = null;
    int mChangingConfigurations;
    int mGravity = 119;
    final Paint mPaint;
    boolean mRebuildShader;
    int mTargetDensity = 160;
    int[] mThemeAttrs = null;
    Shader.TileMode mTileModeX = null;
    Shader.TileMode mTileModeY = null;
    ColorStateList mTint = null;
    PorterDuff.Mode mTintMode = BitmapDrawable.DEFAULT_TINT_MODE;
    
    BitmapState(Bitmap paramBitmap)
    {
      this.mBitmap = paramBitmap;
      this.mPaint = new Paint(6);
    }
    
    BitmapState(BitmapState paramBitmapState)
    {
      this.mBitmap = paramBitmapState.mBitmap;
      this.mTint = paramBitmapState.mTint;
      this.mTintMode = paramBitmapState.mTintMode;
      this.mThemeAttrs = paramBitmapState.mThemeAttrs;
      this.mChangingConfigurations = paramBitmapState.mChangingConfigurations;
      this.mGravity = paramBitmapState.mGravity;
      this.mTileModeX = paramBitmapState.mTileModeX;
      this.mTileModeY = paramBitmapState.mTileModeY;
      this.mTargetDensity = paramBitmapState.mTargetDensity;
      this.mBaseAlpha = paramBitmapState.mBaseAlpha;
      this.mPaint = new Paint(paramBitmapState.mPaint);
      this.mRebuildShader = paramBitmapState.mRebuildShader;
      this.mAutoMirrored = paramBitmapState.mAutoMirrored;
    }
    
    public int addAtlasableBitmaps(Collection<Bitmap> paramCollection)
    {
      if ((isAtlasable(this.mBitmap)) && (paramCollection.add(this.mBitmap))) {
        return this.mBitmap.getWidth() * this.mBitmap.getHeight();
      }
      return 0;
    }
    
    public boolean canApplyTheme()
    {
      if (this.mThemeAttrs == null)
      {
        if (this.mTint != null) {
          return this.mTint.canApplyTheme();
        }
      }
      else {
        return true;
      }
      return false;
    }
    
    public int getChangingConfigurations()
    {
      int j = this.mChangingConfigurations;
      if (this.mTint != null) {}
      for (int i = this.mTint.getChangingConfigurations();; i = 0) {
        return i | j;
      }
    }
    
    public Drawable newDrawable()
    {
      return new BitmapDrawable(this, null, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new BitmapDrawable(this, paramResources, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/BitmapDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */