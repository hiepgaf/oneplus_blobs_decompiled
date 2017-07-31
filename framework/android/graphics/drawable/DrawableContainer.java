package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.SparseArray;
import java.util.Collection;

public class DrawableContainer
  extends Drawable
  implements Drawable.Callback
{
  private static final boolean DEBUG = false;
  private static final boolean DEFAULT_DITHER = true;
  private static final String TAG = "DrawableContainer";
  private int mAlpha = 255;
  private Runnable mAnimationRunnable;
  private BlockInvalidateCallback mBlockInvalidateCallback;
  private int mCurIndex = -1;
  private Drawable mCurrDrawable;
  private DrawableContainerState mDrawableContainerState;
  private long mEnterAnimationEnd;
  private long mExitAnimationEnd;
  private boolean mHasAlpha;
  private Rect mHotspotBounds;
  private Drawable mLastDrawable;
  private int mLastIndex = -1;
  private boolean mMutated;
  
  /* Error */
  private void initializeDrawableForDisplay(Drawable paramDrawable)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 58	android/graphics/drawable/DrawableContainer:mBlockInvalidateCallback	Landroid/graphics/drawable/DrawableContainer$BlockInvalidateCallback;
    //   4: ifnonnull +15 -> 19
    //   7: aload_0
    //   8: new 10	android/graphics/drawable/DrawableContainer$BlockInvalidateCallback
    //   11: dup
    //   12: aconst_null
    //   13: invokespecial 61	android/graphics/drawable/DrawableContainer$BlockInvalidateCallback:<init>	(Landroid/graphics/drawable/DrawableContainer$BlockInvalidateCallback;)V
    //   16: putfield 58	android/graphics/drawable/DrawableContainer:mBlockInvalidateCallback	Landroid/graphics/drawable/DrawableContainer$BlockInvalidateCallback;
    //   19: aload_1
    //   20: aload_0
    //   21: getfield 58	android/graphics/drawable/DrawableContainer:mBlockInvalidateCallback	Landroid/graphics/drawable/DrawableContainer$BlockInvalidateCallback;
    //   24: aload_1
    //   25: invokevirtual 65	android/graphics/drawable/Drawable:getCallback	()Landroid/graphics/drawable/Drawable$Callback;
    //   28: invokevirtual 69	android/graphics/drawable/DrawableContainer$BlockInvalidateCallback:wrap	(Landroid/graphics/drawable/Drawable$Callback;)Landroid/graphics/drawable/DrawableContainer$BlockInvalidateCallback;
    //   31: invokevirtual 73	android/graphics/drawable/Drawable:setCallback	(Landroid/graphics/drawable/Drawable$Callback;)V
    //   34: aload_0
    //   35: getfield 75	android/graphics/drawable/DrawableContainer:mDrawableContainerState	Landroid/graphics/drawable/DrawableContainer$DrawableContainerState;
    //   38: getfield 78	android/graphics/drawable/DrawableContainer$DrawableContainerState:mEnterFadeDuration	I
    //   41: ifgt +18 -> 59
    //   44: aload_0
    //   45: getfield 80	android/graphics/drawable/DrawableContainer:mHasAlpha	Z
    //   48: ifeq +11 -> 59
    //   51: aload_1
    //   52: aload_0
    //   53: getfield 49	android/graphics/drawable/DrawableContainer:mAlpha	I
    //   56: invokevirtual 84	android/graphics/drawable/Drawable:setAlpha	(I)V
    //   59: aload_0
    //   60: getfield 75	android/graphics/drawable/DrawableContainer:mDrawableContainerState	Landroid/graphics/drawable/DrawableContainer$DrawableContainerState;
    //   63: getfield 87	android/graphics/drawable/DrawableContainer$DrawableContainerState:mHasColorFilter	Z
    //   66: ifeq +122 -> 188
    //   69: aload_1
    //   70: aload_0
    //   71: getfield 75	android/graphics/drawable/DrawableContainer:mDrawableContainerState	Landroid/graphics/drawable/DrawableContainer$DrawableContainerState;
    //   74: getfield 91	android/graphics/drawable/DrawableContainer$DrawableContainerState:mColorFilter	Landroid/graphics/ColorFilter;
    //   77: invokevirtual 95	android/graphics/drawable/Drawable:setColorFilter	(Landroid/graphics/ColorFilter;)V
    //   80: aload_1
    //   81: aload_0
    //   82: invokevirtual 99	android/graphics/drawable/DrawableContainer:isVisible	()Z
    //   85: iconst_1
    //   86: invokevirtual 103	android/graphics/drawable/Drawable:setVisible	(ZZ)Z
    //   89: pop
    //   90: aload_1
    //   91: aload_0
    //   92: getfield 75	android/graphics/drawable/DrawableContainer:mDrawableContainerState	Landroid/graphics/drawable/DrawableContainer$DrawableContainerState;
    //   95: getfield 106	android/graphics/drawable/DrawableContainer$DrawableContainerState:mDither	Z
    //   98: invokevirtual 110	android/graphics/drawable/Drawable:setDither	(Z)V
    //   101: aload_1
    //   102: aload_0
    //   103: invokevirtual 114	android/graphics/drawable/DrawableContainer:getState	()[I
    //   106: invokevirtual 118	android/graphics/drawable/Drawable:setState	([I)Z
    //   109: pop
    //   110: aload_1
    //   111: aload_0
    //   112: invokevirtual 122	android/graphics/drawable/DrawableContainer:getLevel	()I
    //   115: invokevirtual 126	android/graphics/drawable/Drawable:setLevel	(I)Z
    //   118: pop
    //   119: aload_1
    //   120: aload_0
    //   121: invokevirtual 130	android/graphics/drawable/DrawableContainer:getBounds	()Landroid/graphics/Rect;
    //   124: invokevirtual 134	android/graphics/drawable/Drawable:setBounds	(Landroid/graphics/Rect;)V
    //   127: aload_1
    //   128: aload_0
    //   129: invokevirtual 137	android/graphics/drawable/DrawableContainer:getLayoutDirection	()I
    //   132: invokevirtual 140	android/graphics/drawable/Drawable:setLayoutDirection	(I)Z
    //   135: pop
    //   136: aload_1
    //   137: aload_0
    //   138: getfield 75	android/graphics/drawable/DrawableContainer:mDrawableContainerState	Landroid/graphics/drawable/DrawableContainer$DrawableContainerState;
    //   141: getfield 143	android/graphics/drawable/DrawableContainer$DrawableContainerState:mAutoMirrored	Z
    //   144: invokevirtual 146	android/graphics/drawable/Drawable:setAutoMirrored	(Z)V
    //   147: aload_0
    //   148: getfield 148	android/graphics/drawable/DrawableContainer:mHotspotBounds	Landroid/graphics/Rect;
    //   151: astore_2
    //   152: aload_2
    //   153: ifnull +23 -> 176
    //   156: aload_1
    //   157: aload_2
    //   158: getfield 153	android/graphics/Rect:left	I
    //   161: aload_2
    //   162: getfield 156	android/graphics/Rect:top	I
    //   165: aload_2
    //   166: getfield 159	android/graphics/Rect:right	I
    //   169: aload_2
    //   170: getfield 162	android/graphics/Rect:bottom	I
    //   173: invokevirtual 166	android/graphics/drawable/Drawable:setHotspotBounds	(IIII)V
    //   176: aload_1
    //   177: aload_0
    //   178: getfield 58	android/graphics/drawable/DrawableContainer:mBlockInvalidateCallback	Landroid/graphics/drawable/DrawableContainer$BlockInvalidateCallback;
    //   181: invokevirtual 169	android/graphics/drawable/DrawableContainer$BlockInvalidateCallback:unwrap	()Landroid/graphics/drawable/Drawable$Callback;
    //   184: invokevirtual 73	android/graphics/drawable/Drawable:setCallback	(Landroid/graphics/drawable/Drawable$Callback;)V
    //   187: return
    //   188: aload_0
    //   189: getfield 75	android/graphics/drawable/DrawableContainer:mDrawableContainerState	Landroid/graphics/drawable/DrawableContainer$DrawableContainerState;
    //   192: getfield 172	android/graphics/drawable/DrawableContainer$DrawableContainerState:mHasTintList	Z
    //   195: ifeq +14 -> 209
    //   198: aload_1
    //   199: aload_0
    //   200: getfield 75	android/graphics/drawable/DrawableContainer:mDrawableContainerState	Landroid/graphics/drawable/DrawableContainer$DrawableContainerState;
    //   203: getfield 176	android/graphics/drawable/DrawableContainer$DrawableContainerState:mTintList	Landroid/content/res/ColorStateList;
    //   206: invokevirtual 180	android/graphics/drawable/Drawable:setTintList	(Landroid/content/res/ColorStateList;)V
    //   209: aload_0
    //   210: getfield 75	android/graphics/drawable/DrawableContainer:mDrawableContainerState	Landroid/graphics/drawable/DrawableContainer$DrawableContainerState;
    //   213: getfield 183	android/graphics/drawable/DrawableContainer$DrawableContainerState:mHasTintMode	Z
    //   216: ifeq -136 -> 80
    //   219: aload_1
    //   220: aload_0
    //   221: getfield 75	android/graphics/drawable/DrawableContainer:mDrawableContainerState	Landroid/graphics/drawable/DrawableContainer$DrawableContainerState;
    //   224: getfield 187	android/graphics/drawable/DrawableContainer$DrawableContainerState:mTintMode	Landroid/graphics/PorterDuff$Mode;
    //   227: invokevirtual 191	android/graphics/drawable/Drawable:setTintMode	(Landroid/graphics/PorterDuff$Mode;)V
    //   230: goto -150 -> 80
    //   233: astore_2
    //   234: aload_1
    //   235: aload_0
    //   236: getfield 58	android/graphics/drawable/DrawableContainer:mBlockInvalidateCallback	Landroid/graphics/drawable/DrawableContainer$BlockInvalidateCallback;
    //   239: invokevirtual 169	android/graphics/drawable/DrawableContainer$BlockInvalidateCallback:unwrap	()Landroid/graphics/drawable/Drawable$Callback;
    //   242: invokevirtual 73	android/graphics/drawable/Drawable:setCallback	(Landroid/graphics/drawable/Drawable$Callback;)V
    //   245: aload_2
    //   246: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	247	0	this	DrawableContainer
    //   0	247	1	paramDrawable	Drawable
    //   151	19	2	localRect	Rect
    //   233	13	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   34	59	233	finally
    //   59	80	233	finally
    //   80	152	233	finally
    //   156	176	233	finally
    //   188	209	233	finally
    //   209	230	233	finally
  }
  
  private boolean needsMirroring()
  {
    return (isAutoMirrored()) && (getLayoutDirection() == 1);
  }
  
  void animate(boolean paramBoolean)
  {
    this.mHasAlpha = true;
    long l = SystemClock.uptimeMillis();
    int j = 0;
    int i;
    if (this.mCurrDrawable != null)
    {
      i = j;
      if (this.mEnterAnimationEnd != 0L)
      {
        if (this.mEnterAnimationEnd <= l)
        {
          this.mCurrDrawable.setAlpha(this.mAlpha);
          this.mEnterAnimationEnd = 0L;
          i = j;
        }
      }
      else
      {
        if (this.mLastDrawable == null) {
          break label236;
        }
        j = i;
        if (this.mExitAnimationEnd != 0L)
        {
          if (this.mExitAnimationEnd > l) {
            break label193;
          }
          this.mLastDrawable.setVisible(false, false);
          this.mLastDrawable = null;
          this.mLastIndex = -1;
          this.mExitAnimationEnd = 0L;
          j = i;
        }
      }
    }
    for (;;)
    {
      if ((paramBoolean) && (j != 0)) {
        scheduleSelf(this.mAnimationRunnable, 16L + l);
      }
      return;
      i = (int)((this.mEnterAnimationEnd - l) * 255L) / this.mDrawableContainerState.mEnterFadeDuration;
      this.mCurrDrawable.setAlpha((255 - i) * this.mAlpha / 255);
      i = 1;
      break;
      this.mEnterAnimationEnd = 0L;
      i = j;
      break;
      label193:
      i = (int)((this.mExitAnimationEnd - l) * 255L) / this.mDrawableContainerState.mExitFadeDuration;
      this.mLastDrawable.setAlpha(this.mAlpha * i / 255);
      j = 1;
      continue;
      label236:
      this.mExitAnimationEnd = 0L;
      j = i;
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    this.mDrawableContainerState.applyTheme(paramTheme);
  }
  
  public boolean canApplyTheme()
  {
    return this.mDrawableContainerState.canApplyTheme();
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    this.mDrawableContainerState.clearMutated();
    this.mMutated = false;
  }
  
  DrawableContainerState cloneConstantState()
  {
    return this.mDrawableContainerState;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mCurrDrawable != null) {
      this.mCurrDrawable.draw(paramCanvas);
    }
    if (this.mLastDrawable != null) {
      this.mLastDrawable.draw(paramCanvas);
    }
  }
  
  public int getAlpha()
  {
    return this.mAlpha;
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mDrawableContainerState.getChangingConfigurations();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    if (this.mDrawableContainerState.canConstantState())
    {
      this.mDrawableContainerState.mChangingConfigurations = getChangingConfigurations();
      return this.mDrawableContainerState;
    }
    return null;
  }
  
  public Drawable getCurrent()
  {
    return this.mCurrDrawable;
  }
  
  public int getCurrentIndex()
  {
    return this.mCurIndex;
  }
  
  public void getHotspotBounds(Rect paramRect)
  {
    if (this.mHotspotBounds != null)
    {
      paramRect.set(this.mHotspotBounds);
      return;
    }
    super.getHotspotBounds(paramRect);
  }
  
  public int getIntrinsicHeight()
  {
    if (this.mDrawableContainerState.isConstantSize()) {
      return this.mDrawableContainerState.getConstantHeight();
    }
    if (this.mCurrDrawable != null) {
      return this.mCurrDrawable.getIntrinsicHeight();
    }
    return -1;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.mDrawableContainerState.isConstantSize()) {
      return this.mDrawableContainerState.getConstantWidth();
    }
    if (this.mCurrDrawable != null) {
      return this.mCurrDrawable.getIntrinsicWidth();
    }
    return -1;
  }
  
  public int getMinimumHeight()
  {
    if (this.mDrawableContainerState.isConstantSize()) {
      return this.mDrawableContainerState.getConstantMinimumHeight();
    }
    if (this.mCurrDrawable != null) {
      return this.mCurrDrawable.getMinimumHeight();
    }
    return 0;
  }
  
  public int getMinimumWidth()
  {
    if (this.mDrawableContainerState.isConstantSize()) {
      return this.mDrawableContainerState.getConstantMinimumWidth();
    }
    if (this.mCurrDrawable != null) {
      return this.mCurrDrawable.getMinimumWidth();
    }
    return 0;
  }
  
  public int getOpacity()
  {
    if ((this.mCurrDrawable != null) && (this.mCurrDrawable.isVisible())) {
      return this.mDrawableContainerState.getOpacity();
    }
    return -2;
  }
  
  public Insets getOpticalInsets()
  {
    if (this.mCurrDrawable != null) {
      return this.mCurrDrawable.getOpticalInsets();
    }
    return Insets.NONE;
  }
  
  public void getOutline(Outline paramOutline)
  {
    if (this.mCurrDrawable != null) {
      this.mCurrDrawable.getOutline(paramOutline);
    }
  }
  
  public boolean getPadding(Rect paramRect)
  {
    Rect localRect = this.mDrawableContainerState.getConstantPadding();
    boolean bool;
    if (localRect != null)
    {
      paramRect.set(localRect);
      if ((localRect.left | localRect.top | localRect.bottom | localRect.right) != 0) {
        bool = true;
      }
    }
    for (;;)
    {
      if (needsMirroring())
      {
        int i = paramRect.left;
        paramRect.left = paramRect.right;
        paramRect.right = i;
      }
      return bool;
      bool = false;
      continue;
      if (this.mCurrDrawable != null) {
        bool = this.mCurrDrawable.getPadding(paramRect);
      } else {
        bool = super.getPadding(paramRect);
      }
    }
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if ((paramDrawable == this.mCurrDrawable) && (getCallback() != null)) {
      getCallback().invalidateDrawable(this);
    }
  }
  
  public boolean isAutoMirrored()
  {
    return this.mDrawableContainerState.mAutoMirrored;
  }
  
  public boolean isStateful()
  {
    return this.mDrawableContainerState.isStateful();
  }
  
  public void jumpToCurrentState()
  {
    int i = 0;
    if (this.mLastDrawable != null)
    {
      this.mLastDrawable.jumpToCurrentState();
      this.mLastDrawable = null;
      this.mLastIndex = -1;
      i = 1;
    }
    if (this.mCurrDrawable != null)
    {
      this.mCurrDrawable.jumpToCurrentState();
      if (this.mHasAlpha) {
        this.mCurrDrawable.setAlpha(this.mAlpha);
      }
    }
    if (this.mExitAnimationEnd != 0L)
    {
      this.mExitAnimationEnd = 0L;
      i = 1;
    }
    if (this.mEnterAnimationEnd != 0L)
    {
      this.mEnterAnimationEnd = 0L;
      i = 1;
    }
    if (i != 0) {
      invalidateSelf();
    }
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      DrawableContainerState localDrawableContainerState = cloneConstantState();
      DrawableContainerState.-wrap0(localDrawableContainerState);
      setConstantState(localDrawableContainerState);
      this.mMutated = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    if (this.mLastDrawable != null) {
      this.mLastDrawable.setBounds(paramRect);
    }
    if (this.mCurrDrawable != null) {
      this.mCurrDrawable.setBounds(paramRect);
    }
  }
  
  public boolean onLayoutDirectionChanged(int paramInt)
  {
    return this.mDrawableContainerState.setLayoutDirection(paramInt, getCurrentIndex());
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    if (this.mLastDrawable != null) {
      return this.mLastDrawable.setLevel(paramInt);
    }
    if (this.mCurrDrawable != null) {
      return this.mCurrDrawable.setLevel(paramInt);
    }
    return false;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    if (this.mLastDrawable != null) {
      return this.mLastDrawable.setState(paramArrayOfInt);
    }
    if (this.mCurrDrawable != null) {
      return this.mCurrDrawable.setState(paramArrayOfInt);
    }
    return false;
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    if ((paramDrawable == this.mCurrDrawable) && (getCallback() != null)) {
      getCallback().scheduleDrawable(this, paramRunnable, paramLong);
    }
  }
  
  public boolean selectDrawable(int paramInt)
  {
    if (paramInt == this.mCurIndex) {
      return false;
    }
    long l = SystemClock.uptimeMillis();
    if (this.mDrawableContainerState.mExitFadeDuration > 0)
    {
      if (this.mLastDrawable != null) {
        this.mLastDrawable.setVisible(false, false);
      }
      if (this.mCurrDrawable != null)
      {
        this.mLastDrawable = this.mCurrDrawable;
        this.mLastIndex = this.mCurIndex;
        this.mExitAnimationEnd = (this.mDrawableContainerState.mExitFadeDuration + l);
        if ((paramInt < 0) || (paramInt >= this.mDrawableContainerState.mNumChildren)) {
          break label235;
        }
        Drawable localDrawable = this.mDrawableContainerState.getChild(paramInt);
        this.mCurrDrawable = localDrawable;
        this.mCurIndex = paramInt;
        if (localDrawable != null)
        {
          if (this.mDrawableContainerState.mEnterFadeDuration > 0) {
            this.mEnterAnimationEnd = (this.mDrawableContainerState.mEnterFadeDuration + l);
          }
          initializeDrawableForDisplay(localDrawable);
        }
        label149:
        if ((this.mEnterAnimationEnd != 0L) || (this.mExitAnimationEnd != 0L))
        {
          if (this.mAnimationRunnable != null) {
            break label248;
          }
          this.mAnimationRunnable = new Runnable()
          {
            public void run()
            {
              DrawableContainer.this.animate(true);
              DrawableContainer.this.invalidateSelf();
            }
          };
        }
      }
    }
    for (;;)
    {
      animate(true);
      invalidateSelf();
      return true;
      this.mLastDrawable = null;
      this.mLastIndex = -1;
      this.mExitAnimationEnd = 0L;
      break;
      if (this.mCurrDrawable == null) {
        break;
      }
      this.mCurrDrawable.setVisible(false, false);
      break;
      label235:
      this.mCurrDrawable = null;
      this.mCurIndex = -1;
      break label149;
      label248:
      unscheduleSelf(this.mAnimationRunnable);
    }
  }
  
  public void setAlpha(int paramInt)
  {
    if ((!this.mHasAlpha) || (this.mAlpha != paramInt))
    {
      this.mHasAlpha = true;
      this.mAlpha = paramInt;
      if (this.mCurrDrawable != null)
      {
        if (this.mEnterAnimationEnd != 0L) {
          break label50;
        }
        this.mCurrDrawable.setAlpha(paramInt);
      }
    }
    return;
    label50:
    animate(false);
  }
  
  public void setAutoMirrored(boolean paramBoolean)
  {
    if (this.mDrawableContainerState.mAutoMirrored != paramBoolean)
    {
      this.mDrawableContainerState.mAutoMirrored = paramBoolean;
      if (this.mCurrDrawable != null) {
        this.mCurrDrawable.setAutoMirrored(this.mDrawableContainerState.mAutoMirrored);
      }
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mDrawableContainerState.mHasColorFilter = true;
    if (this.mDrawableContainerState.mColorFilter != paramColorFilter)
    {
      this.mDrawableContainerState.mColorFilter = paramColorFilter;
      if (this.mCurrDrawable != null) {
        this.mCurrDrawable.setColorFilter(paramColorFilter);
      }
    }
  }
  
  protected void setConstantState(DrawableContainerState paramDrawableContainerState)
  {
    this.mDrawableContainerState = paramDrawableContainerState;
    if (this.mCurIndex >= 0)
    {
      this.mCurrDrawable = paramDrawableContainerState.getChild(this.mCurIndex);
      if (this.mCurrDrawable != null) {
        initializeDrawableForDisplay(this.mCurrDrawable);
      }
    }
    this.mLastIndex = -1;
    this.mLastDrawable = null;
  }
  
  public void setCurrentIndex(int paramInt)
  {
    selectDrawable(paramInt);
  }
  
  public void setDither(boolean paramBoolean)
  {
    if (this.mDrawableContainerState.mDither != paramBoolean)
    {
      this.mDrawableContainerState.mDither = paramBoolean;
      if (this.mCurrDrawable != null) {
        this.mCurrDrawable.setDither(this.mDrawableContainerState.mDither);
      }
    }
  }
  
  public void setEnterFadeDuration(int paramInt)
  {
    this.mDrawableContainerState.mEnterFadeDuration = paramInt;
  }
  
  public void setExitFadeDuration(int paramInt)
  {
    this.mDrawableContainerState.mExitFadeDuration = paramInt;
  }
  
  public void setHotspot(float paramFloat1, float paramFloat2)
  {
    if (this.mCurrDrawable != null) {
      this.mCurrDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mHotspotBounds == null) {
      this.mHotspotBounds = new Rect(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    for (;;)
    {
      if (this.mCurrDrawable != null) {
        this.mCurrDrawable.setHotspotBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      }
      return;
      this.mHotspotBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    this.mDrawableContainerState.mHasTintList = true;
    if (this.mDrawableContainerState.mTintList != paramColorStateList)
    {
      this.mDrawableContainerState.mTintList = paramColorStateList;
      if (this.mCurrDrawable != null) {
        this.mCurrDrawable.setTintList(paramColorStateList);
      }
    }
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    this.mDrawableContainerState.mHasTintMode = true;
    if (this.mDrawableContainerState.mTintMode != paramMode)
    {
      this.mDrawableContainerState.mTintMode = paramMode;
      if (this.mCurrDrawable != null) {
        this.mCurrDrawable.setTintMode(paramMode);
      }
    }
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    if (this.mLastDrawable != null) {
      this.mLastDrawable.setVisible(paramBoolean1, paramBoolean2);
    }
    if (this.mCurrDrawable != null) {
      this.mCurrDrawable.setVisible(paramBoolean1, paramBoolean2);
    }
    return bool;
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    if ((paramDrawable == this.mCurrDrawable) && (getCallback() != null)) {
      getCallback().unscheduleDrawable(this, paramRunnable);
    }
  }
  
  protected final void updateDensity(Resources paramResources)
  {
    this.mDrawableContainerState.updateDensity(paramResources);
  }
  
  private static class BlockInvalidateCallback
    implements Drawable.Callback
  {
    private Drawable.Callback mCallback;
    
    public void invalidateDrawable(Drawable paramDrawable) {}
    
    public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
    {
      if (this.mCallback != null) {
        this.mCallback.scheduleDrawable(paramDrawable, paramRunnable, paramLong);
      }
    }
    
    public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
    {
      if (this.mCallback != null) {
        this.mCallback.unscheduleDrawable(paramDrawable, paramRunnable);
      }
    }
    
    public Drawable.Callback unwrap()
    {
      Drawable.Callback localCallback = this.mCallback;
      this.mCallback = null;
      return localCallback;
    }
    
    public BlockInvalidateCallback wrap(Drawable.Callback paramCallback)
    {
      this.mCallback = paramCallback;
      return this;
    }
  }
  
  public static abstract class DrawableContainerState
    extends Drawable.ConstantState
  {
    boolean mAutoMirrored;
    boolean mCanConstantState;
    int mChangingConfigurations;
    boolean mCheckedConstantSize;
    boolean mCheckedConstantState;
    boolean mCheckedOpacity;
    boolean mCheckedPadding;
    boolean mCheckedStateful;
    int mChildrenChangingConfigurations;
    ColorFilter mColorFilter;
    int mConstantHeight;
    int mConstantMinimumHeight;
    int mConstantMinimumWidth;
    Rect mConstantPadding;
    boolean mConstantSize = false;
    int mConstantWidth;
    int mDensity = 160;
    boolean mDither = true;
    SparseArray<Drawable.ConstantState> mDrawableFutures;
    Drawable[] mDrawables;
    int mEnterFadeDuration = 0;
    int mExitFadeDuration = 0;
    boolean mHasColorFilter;
    boolean mHasTintList;
    boolean mHasTintMode;
    int mLayoutDirection;
    boolean mMutated;
    int mNumChildren;
    int mOpacity;
    final DrawableContainer mOwner;
    Resources mSourceRes;
    boolean mStateful;
    ColorStateList mTintList;
    PorterDuff.Mode mTintMode;
    boolean mVariablePadding = false;
    
    protected DrawableContainerState(DrawableContainerState paramDrawableContainerState, DrawableContainer paramDrawableContainer, Resources paramResources)
    {
      this.mOwner = paramDrawableContainer;
      int i;
      if (paramResources != null)
      {
        paramDrawableContainer = paramResources;
        this.mSourceRes = paramDrawableContainer;
        if (paramDrawableContainerState == null) {
          break label447;
        }
        i = paramDrawableContainerState.mDensity;
        label65:
        this.mDensity = Drawable.resolveDensity(paramResources, i);
        if (paramDrawableContainerState == null) {
          break label485;
        }
        this.mChangingConfigurations = paramDrawableContainerState.mChangingConfigurations;
        this.mChildrenChangingConfigurations = paramDrawableContainerState.mChildrenChangingConfigurations;
        this.mCheckedConstantState = true;
        this.mCanConstantState = true;
        this.mVariablePadding = paramDrawableContainerState.mVariablePadding;
        this.mConstantSize = paramDrawableContainerState.mConstantSize;
        this.mDither = paramDrawableContainerState.mDither;
        this.mMutated = paramDrawableContainerState.mMutated;
        this.mLayoutDirection = paramDrawableContainerState.mLayoutDirection;
        this.mEnterFadeDuration = paramDrawableContainerState.mEnterFadeDuration;
        this.mExitFadeDuration = paramDrawableContainerState.mExitFadeDuration;
        this.mAutoMirrored = paramDrawableContainerState.mAutoMirrored;
        this.mColorFilter = paramDrawableContainerState.mColorFilter;
        this.mHasColorFilter = paramDrawableContainerState.mHasColorFilter;
        this.mTintList = paramDrawableContainerState.mTintList;
        this.mTintMode = paramDrawableContainerState.mTintMode;
        this.mHasTintList = paramDrawableContainerState.mHasTintList;
        this.mHasTintMode = paramDrawableContainerState.mHasTintMode;
        if (paramDrawableContainerState.mDensity == this.mDensity)
        {
          if (paramDrawableContainerState.mCheckedPadding)
          {
            this.mConstantPadding = new Rect(paramDrawableContainerState.mConstantPadding);
            this.mCheckedPadding = true;
          }
          if (paramDrawableContainerState.mCheckedConstantSize)
          {
            this.mConstantWidth = paramDrawableContainerState.mConstantWidth;
            this.mConstantHeight = paramDrawableContainerState.mConstantHeight;
            this.mConstantMinimumWidth = paramDrawableContainerState.mConstantMinimumWidth;
            this.mConstantMinimumHeight = paramDrawableContainerState.mConstantMinimumHeight;
            this.mCheckedConstantSize = true;
          }
        }
        if (paramDrawableContainerState.mCheckedOpacity)
        {
          this.mOpacity = paramDrawableContainerState.mOpacity;
          this.mCheckedOpacity = true;
        }
        if (paramDrawableContainerState.mCheckedStateful)
        {
          this.mStateful = paramDrawableContainerState.mStateful;
          this.mCheckedStateful = true;
        }
        paramDrawableContainer = paramDrawableContainerState.mDrawables;
        this.mDrawables = new Drawable[paramDrawableContainer.length];
        this.mNumChildren = paramDrawableContainerState.mNumChildren;
        paramDrawableContainerState = paramDrawableContainerState.mDrawableFutures;
        if (paramDrawableContainerState == null) {
          break label453;
        }
        this.mDrawableFutures = paramDrawableContainerState.clone();
        label378:
        int j = this.mNumChildren;
        i = 0;
        label387:
        if (i >= j) {
          return;
        }
        if (paramDrawableContainer[i] != null)
        {
          paramDrawableContainerState = paramDrawableContainer[i].getConstantState();
          if (paramDrawableContainerState == null) {
            break label471;
          }
          this.mDrawableFutures.put(i, paramDrawableContainerState);
        }
      }
      for (;;)
      {
        i += 1;
        break label387;
        paramDrawableContainer = (DrawableContainer)localObject;
        if (paramDrawableContainerState == null) {
          break;
        }
        paramDrawableContainer = paramDrawableContainerState.mSourceRes;
        break;
        label447:
        i = 0;
        break label65;
        label453:
        this.mDrawableFutures = new SparseArray(this.mNumChildren);
        break label378;
        label471:
        this.mDrawables[i] = paramDrawableContainer[i];
      }
      label485:
      this.mDrawables = new Drawable[10];
      this.mNumChildren = 0;
    }
    
    private void createAllFutures()
    {
      if (this.mDrawableFutures != null)
      {
        int j = this.mDrawableFutures.size();
        int i = 0;
        while (i < j)
        {
          int k = this.mDrawableFutures.keyAt(i);
          Drawable.ConstantState localConstantState = (Drawable.ConstantState)this.mDrawableFutures.valueAt(i);
          this.mDrawables[k] = prepareDrawable(localConstantState.newDrawable(this.mSourceRes));
          i += 1;
        }
        this.mDrawableFutures = null;
      }
    }
    
    private void mutate()
    {
      int j = this.mNumChildren;
      Drawable[] arrayOfDrawable = this.mDrawables;
      int i = 0;
      while (i < j)
      {
        if (arrayOfDrawable[i] != null) {
          arrayOfDrawable[i].mutate();
        }
        i += 1;
      }
      this.mMutated = true;
    }
    
    private Drawable prepareDrawable(Drawable paramDrawable)
    {
      paramDrawable.setLayoutDirection(this.mLayoutDirection);
      paramDrawable = paramDrawable.mutate();
      paramDrawable.setCallback(this.mOwner);
      return paramDrawable;
    }
    
    public int addAtlasableBitmaps(Collection<Bitmap> paramCollection)
    {
      int m = this.mNumChildren;
      int j = 0;
      int i = 0;
      while (i < m)
      {
        Drawable.ConstantState localConstantState = getChild(i).getConstantState();
        int k = j;
        if (localConstantState != null) {
          k = j + localConstantState.addAtlasableBitmaps(paramCollection);
        }
        i += 1;
        j = k;
      }
      return j;
    }
    
    public final int addChild(Drawable paramDrawable)
    {
      int i = this.mNumChildren;
      if (i >= this.mDrawables.length) {
        growArray(i, i + 10);
      }
      paramDrawable.mutate();
      paramDrawable.setVisible(false, true);
      paramDrawable.setCallback(this.mOwner);
      this.mDrawables[i] = paramDrawable;
      this.mNumChildren += 1;
      this.mChildrenChangingConfigurations |= paramDrawable.getChangingConfigurations();
      this.mCheckedStateful = false;
      this.mCheckedOpacity = false;
      this.mConstantPadding = null;
      this.mCheckedPadding = false;
      this.mCheckedConstantSize = false;
      this.mCheckedConstantState = false;
      return i;
    }
    
    final void applyTheme(Resources.Theme paramTheme)
    {
      if (paramTheme != null)
      {
        createAllFutures();
        int j = this.mNumChildren;
        Drawable[] arrayOfDrawable = this.mDrawables;
        int i = 0;
        while (i < j)
        {
          if ((arrayOfDrawable[i] != null) && (arrayOfDrawable[i].canApplyTheme()))
          {
            arrayOfDrawable[i].applyTheme(paramTheme);
            this.mChildrenChangingConfigurations |= arrayOfDrawable[i].getChangingConfigurations();
          }
          i += 1;
        }
        updateDensity(paramTheme.getResources());
      }
    }
    
    public boolean canApplyTheme()
    {
      int j = this.mNumChildren;
      Drawable[] arrayOfDrawable = this.mDrawables;
      int i = 0;
      while (i < j)
      {
        Object localObject = arrayOfDrawable[i];
        if (localObject != null)
        {
          if (((Drawable)localObject).canApplyTheme()) {
            return true;
          }
        }
        else
        {
          localObject = (Drawable.ConstantState)this.mDrawableFutures.get(i);
          if ((localObject != null) && (((Drawable.ConstantState)localObject).canApplyTheme())) {
            return true;
          }
        }
        i += 1;
      }
      return false;
    }
    
    public boolean canConstantState()
    {
      try
      {
        if (this.mCheckedConstantState)
        {
          boolean bool = this.mCanConstantState;
          return bool;
        }
        createAllFutures();
        this.mCheckedConstantState = true;
        int j = this.mNumChildren;
        Drawable[] arrayOfDrawable = this.mDrawables;
        int i = 0;
        while (i < j)
        {
          if (arrayOfDrawable[i].getConstantState() == null)
          {
            this.mCanConstantState = false;
            return false;
          }
          i += 1;
        }
        this.mCanConstantState = true;
        return true;
      }
      finally {}
    }
    
    final void clearMutated()
    {
      int j = this.mNumChildren;
      Drawable[] arrayOfDrawable = this.mDrawables;
      int i = 0;
      while (i < j)
      {
        if (arrayOfDrawable[i] != null) {
          arrayOfDrawable[i].clearMutated();
        }
        i += 1;
      }
      this.mMutated = false;
    }
    
    protected void computeConstantSize()
    {
      this.mCheckedConstantSize = true;
      createAllFutures();
      int j = this.mNumChildren;
      Drawable[] arrayOfDrawable = this.mDrawables;
      this.mConstantHeight = -1;
      this.mConstantWidth = -1;
      this.mConstantMinimumHeight = 0;
      this.mConstantMinimumWidth = 0;
      int i = 0;
      while (i < j)
      {
        Drawable localDrawable = arrayOfDrawable[i];
        int k = localDrawable.getIntrinsicWidth();
        if (k > this.mConstantWidth) {
          this.mConstantWidth = k;
        }
        k = localDrawable.getIntrinsicHeight();
        if (k > this.mConstantHeight) {
          this.mConstantHeight = k;
        }
        k = localDrawable.getMinimumWidth();
        if (k > this.mConstantMinimumWidth) {
          this.mConstantMinimumWidth = k;
        }
        k = localDrawable.getMinimumHeight();
        if (k > this.mConstantMinimumHeight) {
          this.mConstantMinimumHeight = k;
        }
        i += 1;
      }
    }
    
    final int getCapacity()
    {
      return this.mDrawables.length;
    }
    
    public int getChangingConfigurations()
    {
      return this.mChangingConfigurations | this.mChildrenChangingConfigurations;
    }
    
    public final Drawable getChild(int paramInt)
    {
      Drawable localDrawable = this.mDrawables[paramInt];
      if (localDrawable != null) {
        return localDrawable;
      }
      if (this.mDrawableFutures != null)
      {
        int i = this.mDrawableFutures.indexOfKey(paramInt);
        if (i >= 0)
        {
          localDrawable = prepareDrawable(((Drawable.ConstantState)this.mDrawableFutures.valueAt(i)).newDrawable(this.mSourceRes));
          this.mDrawables[paramInt] = localDrawable;
          this.mDrawableFutures.removeAt(i);
          if (this.mDrawableFutures.size() == 0) {
            this.mDrawableFutures = null;
          }
          return localDrawable;
        }
      }
      return null;
    }
    
    public final int getChildCount()
    {
      return this.mNumChildren;
    }
    
    public final Drawable[] getChildren()
    {
      createAllFutures();
      return this.mDrawables;
    }
    
    public final int getConstantHeight()
    {
      if (!this.mCheckedConstantSize) {
        computeConstantSize();
      }
      return this.mConstantHeight;
    }
    
    public final int getConstantMinimumHeight()
    {
      if (!this.mCheckedConstantSize) {
        computeConstantSize();
      }
      return this.mConstantMinimumHeight;
    }
    
    public final int getConstantMinimumWidth()
    {
      if (!this.mCheckedConstantSize) {
        computeConstantSize();
      }
      return this.mConstantMinimumWidth;
    }
    
    public final Rect getConstantPadding()
    {
      if (this.mVariablePadding) {
        return null;
      }
      if ((this.mConstantPadding != null) || (this.mCheckedPadding)) {
        return this.mConstantPadding;
      }
      createAllFutures();
      Object localObject1 = null;
      Rect localRect = new Rect();
      int j = this.mNumChildren;
      Drawable[] arrayOfDrawable = this.mDrawables;
      int i = 0;
      while (i < j)
      {
        Object localObject3 = localObject1;
        if (arrayOfDrawable[i].getPadding(localRect))
        {
          Object localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new Rect(0, 0, 0, 0);
          }
          if (localRect.left > ((Rect)localObject2).left) {
            ((Rect)localObject2).left = localRect.left;
          }
          if (localRect.top > ((Rect)localObject2).top) {
            ((Rect)localObject2).top = localRect.top;
          }
          if (localRect.right > ((Rect)localObject2).right) {
            ((Rect)localObject2).right = localRect.right;
          }
          localObject3 = localObject2;
          if (localRect.bottom > ((Rect)localObject2).bottom)
          {
            ((Rect)localObject2).bottom = localRect.bottom;
            localObject3 = localObject2;
          }
        }
        i += 1;
        localObject1 = localObject3;
      }
      this.mCheckedPadding = true;
      this.mConstantPadding = ((Rect)localObject1);
      return (Rect)localObject1;
    }
    
    public final int getConstantWidth()
    {
      if (!this.mCheckedConstantSize) {
        computeConstantSize();
      }
      return this.mConstantWidth;
    }
    
    public final int getEnterFadeDuration()
    {
      return this.mEnterFadeDuration;
    }
    
    public final int getExitFadeDuration()
    {
      return this.mExitFadeDuration;
    }
    
    public final int getOpacity()
    {
      if (this.mCheckedOpacity) {
        return this.mOpacity;
      }
      createAllFutures();
      this.mCheckedOpacity = true;
      int m = this.mNumChildren;
      Drawable[] arrayOfDrawable = this.mDrawables;
      if (m > 0) {}
      int j;
      for (int i = arrayOfDrawable[0].getOpacity();; i = -2)
      {
        int k = 1;
        j = i;
        i = k;
        while (i < m)
        {
          j = Drawable.resolveOpacity(j, arrayOfDrawable[i].getOpacity());
          i += 1;
        }
      }
      this.mOpacity = j;
      return j;
    }
    
    public void growArray(int paramInt1, int paramInt2)
    {
      Drawable[] arrayOfDrawable = new Drawable[paramInt2];
      System.arraycopy(this.mDrawables, 0, arrayOfDrawable, 0, paramInt1);
      this.mDrawables = arrayOfDrawable;
    }
    
    public final boolean isConstantSize()
    {
      return this.mConstantSize;
    }
    
    public final boolean isStateful()
    {
      if (this.mCheckedStateful) {
        return this.mStateful;
      }
      createAllFutures();
      this.mCheckedStateful = true;
      int j = this.mNumChildren;
      Drawable[] arrayOfDrawable = this.mDrawables;
      int i = 0;
      while (i < j)
      {
        if (arrayOfDrawable[i].isStateful())
        {
          this.mStateful = true;
          return true;
        }
        i += 1;
      }
      this.mStateful = false;
      return false;
    }
    
    public final void setConstantSize(boolean paramBoolean)
    {
      this.mConstantSize = paramBoolean;
    }
    
    public final void setEnterFadeDuration(int paramInt)
    {
      this.mEnterFadeDuration = paramInt;
    }
    
    public final void setExitFadeDuration(int paramInt)
    {
      this.mExitFadeDuration = paramInt;
    }
    
    final boolean setLayoutDirection(int paramInt1, int paramInt2)
    {
      boolean bool1 = false;
      int j = this.mNumChildren;
      Drawable[] arrayOfDrawable = this.mDrawables;
      int i = 0;
      while (i < j)
      {
        boolean bool2 = bool1;
        if (arrayOfDrawable[i] != null)
        {
          boolean bool3 = arrayOfDrawable[i].setLayoutDirection(paramInt1);
          bool2 = bool1;
          if (i == paramInt2) {
            bool2 = bool3;
          }
        }
        i += 1;
        bool1 = bool2;
      }
      this.mLayoutDirection = paramInt1;
      return bool1;
    }
    
    public final void setVariablePadding(boolean paramBoolean)
    {
      this.mVariablePadding = paramBoolean;
    }
    
    final void updateDensity(Resources paramResources)
    {
      if (paramResources != null)
      {
        this.mSourceRes = paramResources;
        int i = Drawable.resolveDensity(paramResources, this.mDensity);
        int j = this.mDensity;
        this.mDensity = i;
        if (j != i)
        {
          this.mCheckedConstantSize = false;
          this.mCheckedPadding = false;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/DrawableContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */