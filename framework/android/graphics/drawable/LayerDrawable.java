package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.Collection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class LayerDrawable
  extends Drawable
  implements Drawable.Callback
{
  public static final int INSET_UNDEFINED = Integer.MIN_VALUE;
  public static final int PADDING_MODE_NEST = 0;
  public static final int PADDING_MODE_STACK = 1;
  private boolean mChildRequestedInvalidation;
  private Rect mHotspotBounds;
  LayerState mLayerState = createConstantState(paramLayerState, paramResources);
  private boolean mMutated;
  private int[] mPaddingB;
  private int[] mPaddingL;
  private int[] mPaddingR;
  private int[] mPaddingT;
  private boolean mSuspendChildInvalidation;
  private final Rect mTmpContainer = new Rect();
  private final Rect mTmpOutRect = new Rect();
  private final Rect mTmpRect = new Rect();
  
  LayerDrawable()
  {
    this((LayerState)null, null);
  }
  
  LayerDrawable(LayerState paramLayerState, Resources paramResources)
  {
    if (this.mLayerState.mNum > 0)
    {
      ensurePadding();
      refreshPadding();
    }
  }
  
  public LayerDrawable(Drawable[] paramArrayOfDrawable)
  {
    this(paramArrayOfDrawable, null);
  }
  
  LayerDrawable(Drawable[] paramArrayOfDrawable, LayerState paramLayerState)
  {
    this(paramLayerState, null);
    if (paramArrayOfDrawable == null) {
      throw new IllegalArgumentException("layers must be non-null");
    }
    int j = paramArrayOfDrawable.length;
    paramLayerState = new ChildDrawable[j];
    int i = 0;
    while (i < j)
    {
      paramLayerState[i] = new ChildDrawable(this.mLayerState.mDensity);
      paramLayerState[i].mDrawable = paramArrayOfDrawable[i];
      paramArrayOfDrawable[i].setCallback(this);
      LayerState localLayerState = this.mLayerState;
      localLayerState.mChildrenChangingConfigurations |= paramArrayOfDrawable[i].getChangingConfigurations();
      i += 1;
    }
    this.mLayerState.mNum = j;
    this.mLayerState.mChildren = paramLayerState;
    ensurePadding();
    refreshPadding();
  }
  
  private void computeNestedPadding(Rect paramRect)
  {
    paramRect.left = 0;
    paramRect.top = 0;
    paramRect.right = 0;
    paramRect.bottom = 0;
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      refreshChildPadding(i, arrayOfChildDrawable[i]);
      paramRect.left += this.mPaddingL[i];
      paramRect.top += this.mPaddingT[i];
      paramRect.right += this.mPaddingR[i];
      paramRect.bottom += this.mPaddingB[i];
      i += 1;
    }
  }
  
  private void computeStackedPadding(Rect paramRect)
  {
    paramRect.left = 0;
    paramRect.top = 0;
    paramRect.right = 0;
    paramRect.bottom = 0;
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      refreshChildPadding(i, arrayOfChildDrawable[i]);
      paramRect.left = Math.max(paramRect.left, this.mPaddingL[i]);
      paramRect.top = Math.max(paramRect.top, this.mPaddingT[i]);
      paramRect.right = Math.max(paramRect.right, this.mPaddingR[i]);
      paramRect.bottom = Math.max(paramRect.bottom, this.mPaddingB[i]);
      i += 1;
    }
  }
  
  private ChildDrawable createLayer(Drawable paramDrawable)
  {
    ChildDrawable localChildDrawable = new ChildDrawable(this.mLayerState.mDensity);
    localChildDrawable.mDrawable = paramDrawable;
    return localChildDrawable;
  }
  
  private Drawable getFirstNonNullDrawable()
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        return localDrawable;
      }
      i += 1;
    }
    return null;
  }
  
  private void inflateLayers(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    LayerState localLayerState = this.mLayerState;
    int i = paramXmlPullParser.getDepth() + 1;
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (j == 1) {
        break;
      }
      int k = paramXmlPullParser.getDepth();
      if ((k < i) && (j == 3)) {
        break;
      }
      if ((j == 2) && (k <= i) && (paramXmlPullParser.getName().equals("item")))
      {
        ChildDrawable localChildDrawable = new ChildDrawable(localLayerState.mDensity);
        TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.LayerDrawableItem);
        updateLayerFromTypedArray(localChildDrawable, localTypedArray);
        localTypedArray.recycle();
        if ((localChildDrawable.mDrawable == null) && ((localChildDrawable.mThemeAttrs == null) || (localChildDrawable.mThemeAttrs[4] == 0)))
        {
          do
          {
            j = paramXmlPullParser.next();
          } while (j == 4);
          if (j != 2) {
            throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
          }
          localChildDrawable.mDrawable = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
          localChildDrawable.mDrawable.setCallback(this);
          localLayerState.mChildrenChangingConfigurations |= localChildDrawable.mDrawable.getChangingConfigurations();
        }
        addLayer(localChildDrawable);
      }
    }
  }
  
  private boolean refreshChildPadding(int paramInt, ChildDrawable paramChildDrawable)
  {
    if (paramChildDrawable.mDrawable != null)
    {
      Rect localRect = this.mTmpRect;
      paramChildDrawable.mDrawable.getPadding(localRect);
      if ((localRect.left != this.mPaddingL[paramInt]) || (localRect.top != this.mPaddingT[paramInt])) {}
      while ((localRect.right != this.mPaddingR[paramInt]) || (localRect.bottom != this.mPaddingB[paramInt]))
      {
        this.mPaddingL[paramInt] = localRect.left;
        this.mPaddingT[paramInt] = localRect.top;
        this.mPaddingR[paramInt] = localRect.right;
        this.mPaddingB[paramInt] = localRect.bottom;
        return true;
      }
    }
    return false;
  }
  
  private static int resolveGravity(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = paramInt1;
    if (!Gravity.isHorizontal(paramInt1))
    {
      if (paramInt2 < 0) {
        i = paramInt1 | 0x7;
      }
    }
    else
    {
      paramInt1 = i;
      if (!Gravity.isVertical(i)) {
        if (paramInt3 >= 0) {
          break label93;
        }
      }
    }
    label93:
    for (paramInt1 = i | 0x70;; paramInt1 = i | 0x30)
    {
      i = paramInt1;
      if (paramInt2 < 0)
      {
        i = paramInt1;
        if (paramInt4 < 0) {
          i = paramInt1 | 0x7;
        }
      }
      paramInt1 = i;
      if (paramInt3 < 0)
      {
        paramInt1 = i;
        if (paramInt5 < 0) {
          paramInt1 = i | 0x70;
        }
      }
      return paramInt1;
      i = paramInt1 | 0x800003;
      break;
    }
  }
  
  private void resumeChildInvalidation()
  {
    this.mSuspendChildInvalidation = false;
    if (this.mChildRequestedInvalidation)
    {
      this.mChildRequestedInvalidation = false;
      invalidateSelf();
    }
  }
  
  private void setLayerInsetInternal(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    ChildDrawable localChildDrawable = this.mLayerState.mChildren[paramInt1];
    localChildDrawable.mInsetL = paramInt2;
    localChildDrawable.mInsetT = paramInt3;
    localChildDrawable.mInsetR = paramInt4;
    localChildDrawable.mInsetB = paramInt5;
    localChildDrawable.mInsetS = paramInt6;
    localChildDrawable.mInsetE = paramInt7;
  }
  
  private void suspendChildInvalidation()
  {
    this.mSuspendChildInvalidation = true;
  }
  
  private void updateLayerBounds(Rect paramRect)
  {
    try
    {
      suspendChildInvalidation();
      updateLayerBoundsInternal(paramRect);
      return;
    }
    finally
    {
      resumeChildInvalidation();
    }
  }
  
  private void updateLayerBoundsInternal(Rect paramRect)
  {
    int i4 = 0;
    int n = 0;
    int i3 = 0;
    int i2 = 0;
    Rect localRect1 = this.mTmpOutRect;
    int i7 = getLayoutDirection();
    int k;
    if (i7 == 1)
    {
      k = 1;
      if (LayerState.-get1(this.mLayerState) != 0) {
        break label136;
      }
    }
    int i1;
    ChildDrawable localChildDrawable;
    Drawable localDrawable;
    int j;
    int i;
    label136:
    for (int m = 1;; m = 0)
    {
      ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
      i1 = 0;
      int i8 = this.mLayerState.mNum;
      for (;;)
      {
        if (i1 >= i8) {
          return;
        }
        localChildDrawable = arrayOfChildDrawable[i1];
        localDrawable = localChildDrawable.mDrawable;
        if (localDrawable != null) {
          break;
        }
        i6 = n;
        i5 = i3;
        j = i4;
        i = i2;
        i1 += 1;
        i2 = i;
        i4 = j;
        i3 = i5;
        n = i6;
      }
      k = 0;
      break;
    }
    int i5 = localChildDrawable.mInsetT;
    int i6 = localChildDrawable.mInsetB;
    label167:
    label178:
    label190:
    label202:
    Rect localRect2;
    int i9;
    if (k != 0)
    {
      j = localChildDrawable.mInsetE;
      if (k == 0) {
        break label400;
      }
      i = localChildDrawable.mInsetS;
      if (j != Integer.MIN_VALUE) {
        break label409;
      }
      j = localChildDrawable.mInsetL;
      if (i != Integer.MIN_VALUE) {
        break label412;
      }
      i = localChildDrawable.mInsetR;
      localRect2 = this.mTmpContainer;
      localRect2.set(paramRect.left + j + i4, paramRect.top + i5 + n, paramRect.right - i - i3, paramRect.bottom - i6 - i2);
      i = localDrawable.getIntrinsicWidth();
      j = localDrawable.getIntrinsicHeight();
      i6 = localChildDrawable.mWidth;
      i5 = localChildDrawable.mHeight;
      i9 = resolveGravity(localChildDrawable.mGravity, i6, i5, i, j);
      if (i6 >= 0) {
        break label415;
      }
      label298:
      if (i5 >= 0) {
        break label421;
      }
    }
    for (;;)
    {
      Gravity.apply(i9, i, j, localRect2, localRect1, i7);
      localDrawable.setBounds(localRect1);
      i = i2;
      j = i4;
      i5 = i3;
      i6 = n;
      if (m == 0) {
        break;
      }
      j = i4 + this.mPaddingL[i1];
      i5 = i3 + this.mPaddingR[i1];
      i6 = n + this.mPaddingT[i1];
      i = i2 + this.mPaddingB[i1];
      break;
      j = localChildDrawable.mInsetS;
      break label167;
      label400:
      i = localChildDrawable.mInsetE;
      break label178;
      label409:
      break label190;
      label412:
      break label202;
      label415:
      i = i6;
      break label298;
      label421:
      j = i5;
    }
  }
  
  private void updateLayerFromTypedArray(ChildDrawable paramChildDrawable, TypedArray paramTypedArray)
  {
    LayerState localLayerState = this.mLayerState;
    localLayerState.mChildrenChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    paramChildDrawable.mThemeAttrs = paramTypedArray.extractThemeAttrs();
    int j = paramTypedArray.getIndexCount();
    int i = 0;
    if (i < j)
    {
      int k = paramTypedArray.getIndex(i);
      switch (k)
      {
      }
      for (;;)
      {
        i += 1;
        break;
        paramChildDrawable.mInsetL = paramTypedArray.getDimensionPixelOffset(k, paramChildDrawable.mInsetL);
        continue;
        paramChildDrawable.mInsetT = paramTypedArray.getDimensionPixelOffset(k, paramChildDrawable.mInsetT);
        continue;
        paramChildDrawable.mInsetR = paramTypedArray.getDimensionPixelOffset(k, paramChildDrawable.mInsetR);
        continue;
        paramChildDrawable.mInsetB = paramTypedArray.getDimensionPixelOffset(k, paramChildDrawable.mInsetB);
        continue;
        paramChildDrawable.mInsetS = paramTypedArray.getDimensionPixelOffset(k, paramChildDrawable.mInsetS);
        continue;
        paramChildDrawable.mInsetE = paramTypedArray.getDimensionPixelOffset(k, paramChildDrawable.mInsetE);
        continue;
        paramChildDrawable.mWidth = paramTypedArray.getDimensionPixelSize(k, paramChildDrawable.mWidth);
        continue;
        paramChildDrawable.mHeight = paramTypedArray.getDimensionPixelSize(k, paramChildDrawable.mHeight);
        continue;
        paramChildDrawable.mGravity = paramTypedArray.getInteger(k, paramChildDrawable.mGravity);
        continue;
        paramChildDrawable.mId = paramTypedArray.getResourceId(k, paramChildDrawable.mId);
      }
    }
    paramTypedArray = paramTypedArray.getDrawable(4);
    if (paramTypedArray != null)
    {
      if (paramChildDrawable.mDrawable != null) {
        paramChildDrawable.mDrawable.setCallback(null);
      }
      paramChildDrawable.mDrawable = paramTypedArray;
      paramChildDrawable.mDrawable.setCallback(this);
      localLayerState.mChildrenChangingConfigurations |= paramChildDrawable.mDrawable.getChangingConfigurations();
    }
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    LayerState localLayerState = this.mLayerState;
    localLayerState.mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    LayerState.-set2(localLayerState, paramTypedArray.extractThemeAttrs());
    int j = paramTypedArray.getIndexCount();
    int i = 0;
    if (i < j)
    {
      int k = paramTypedArray.getIndex(i);
      switch (k)
      {
      }
      for (;;)
      {
        i += 1;
        break;
        localLayerState.mOpacityOverride = paramTypedArray.getInt(k, localLayerState.mOpacityOverride);
        continue;
        localLayerState.mPaddingTop = paramTypedArray.getDimensionPixelOffset(k, localLayerState.mPaddingTop);
        continue;
        localLayerState.mPaddingBottom = paramTypedArray.getDimensionPixelOffset(k, localLayerState.mPaddingBottom);
        continue;
        localLayerState.mPaddingLeft = paramTypedArray.getDimensionPixelOffset(k, localLayerState.mPaddingLeft);
        continue;
        localLayerState.mPaddingRight = paramTypedArray.getDimensionPixelOffset(k, localLayerState.mPaddingRight);
        continue;
        localLayerState.mPaddingStart = paramTypedArray.getDimensionPixelOffset(k, localLayerState.mPaddingStart);
        continue;
        localLayerState.mPaddingEnd = paramTypedArray.getDimensionPixelOffset(k, localLayerState.mPaddingEnd);
        continue;
        LayerState.-set0(localLayerState, paramTypedArray.getBoolean(k, LayerState.-get0(localLayerState)));
        continue;
        LayerState.-set1(localLayerState, paramTypedArray.getInteger(k, LayerState.-get1(localLayerState)));
      }
    }
  }
  
  public int addLayer(Drawable paramDrawable)
  {
    paramDrawable = createLayer(paramDrawable);
    int i = addLayer(paramDrawable);
    ensurePadding();
    refreshChildPadding(i, paramDrawable);
    return i;
  }
  
  int addLayer(ChildDrawable paramChildDrawable)
  {
    LayerState localLayerState = this.mLayerState;
    if (localLayerState.mChildren != null) {}
    for (int i = localLayerState.mChildren.length;; i = 0)
    {
      int j = localLayerState.mNum;
      if (j >= i)
      {
        ChildDrawable[] arrayOfChildDrawable = new ChildDrawable[i + 10];
        if (j > 0) {
          System.arraycopy(localLayerState.mChildren, 0, arrayOfChildDrawable, 0, j);
        }
        localLayerState.mChildren = arrayOfChildDrawable;
      }
      localLayerState.mChildren[j] = paramChildDrawable;
      localLayerState.mNum += 1;
      localLayerState.invalidateCache();
      return j;
    }
  }
  
  ChildDrawable addLayer(Drawable paramDrawable, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    ChildDrawable localChildDrawable = createLayer(paramDrawable);
    localChildDrawable.mId = paramInt1;
    localChildDrawable.mThemeAttrs = paramArrayOfInt;
    localChildDrawable.mDrawable.setAutoMirrored(isAutoMirrored());
    localChildDrawable.mInsetL = paramInt2;
    localChildDrawable.mInsetT = paramInt3;
    localChildDrawable.mInsetR = paramInt4;
    localChildDrawable.mInsetB = paramInt5;
    addLayer(localChildDrawable);
    paramArrayOfInt = this.mLayerState;
    paramArrayOfInt.mChildrenChangingConfigurations |= paramDrawable.getChangingConfigurations();
    paramDrawable.setCallback(this);
    return localChildDrawable;
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    super.applyTheme(paramTheme);
    LayerState localLayerState = this.mLayerState;
    if (localLayerState == null) {
      return;
    }
    int j = Drawable.resolveDensity(paramTheme.getResources(), 0);
    localLayerState.setDensity(j);
    if (LayerState.-get2(localLayerState) != null)
    {
      localObject1 = paramTheme.resolveAttributes(LayerState.-get2(localLayerState), R.styleable.LayerDrawable);
      updateStateFromTypedArray((TypedArray)localObject1);
      ((TypedArray)localObject1).recycle();
    }
    Object localObject1 = localLayerState.mChildren;
    int k = localLayerState.mNum;
    int i = 0;
    while (i < k)
    {
      Object localObject2 = localObject1[i];
      ((ChildDrawable)localObject2).setDensity(j);
      if (((ChildDrawable)localObject2).mThemeAttrs != null)
      {
        TypedArray localTypedArray = paramTheme.resolveAttributes(((ChildDrawable)localObject2).mThemeAttrs, R.styleable.LayerDrawableItem);
        updateLayerFromTypedArray((ChildDrawable)localObject2, localTypedArray);
        localTypedArray.recycle();
      }
      localObject2 = ((ChildDrawable)localObject2).mDrawable;
      if ((localObject2 != null) && (((Drawable)localObject2).canApplyTheme()))
      {
        ((Drawable)localObject2).applyTheme(paramTheme);
        localLayerState.mChildrenChangingConfigurations |= ((Drawable)localObject2).getChangingConfigurations();
      }
      i += 1;
    }
  }
  
  public boolean canApplyTheme()
  {
    if ((this.mLayerState == null) || (!this.mLayerState.canApplyTheme())) {
      return super.canApplyTheme();
    }
    return true;
  }
  
  public void clearMutated()
  {
    super.clearMutated();
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.clearMutated();
      }
      i += 1;
    }
    this.mMutated = false;
  }
  
  LayerState createConstantState(LayerState paramLayerState, Resources paramResources)
  {
    return new LayerState(paramLayerState, this, paramResources);
  }
  
  public void draw(Canvas paramCanvas)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.draw(paramCanvas);
      }
      i += 1;
    }
  }
  
  void ensurePadding()
  {
    int i = this.mLayerState.mNum;
    if ((this.mPaddingL != null) && (this.mPaddingL.length >= i)) {
      return;
    }
    this.mPaddingL = new int[i];
    this.mPaddingT = new int[i];
    this.mPaddingR = new int[i];
    this.mPaddingB = new int[i];
  }
  
  public Drawable findDrawableByLayerId(int paramInt)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int i = this.mLayerState.mNum - 1;
    while (i >= 0)
    {
      if (arrayOfChildDrawable[i].mId == paramInt) {
        return arrayOfChildDrawable[i].mDrawable;
      }
      i -= 1;
    }
    return null;
  }
  
  public int findIndexByLayerId(int paramInt)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      if (arrayOfChildDrawable[i].mId == paramInt) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public int getAlpha()
  {
    Drawable localDrawable = getFirstNonNullDrawable();
    if (localDrawable != null) {
      return localDrawable.getAlpha();
    }
    return super.getAlpha();
  }
  
  public int getBottomPadding()
  {
    return this.mLayerState.mPaddingBottom;
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mLayerState.getChangingConfigurations();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    if (this.mLayerState.canConstantState())
    {
      this.mLayerState.mChangingConfigurations = getChangingConfigurations();
      return this.mLayerState;
    }
    return null;
  }
  
  public Drawable getDrawable(int paramInt)
  {
    if (paramInt >= this.mLayerState.mNum) {
      throw new IndexOutOfBoundsException();
    }
    return this.mLayerState.mChildren[paramInt].mDrawable;
  }
  
  public int getEndPadding()
  {
    return this.mLayerState.mPaddingEnd;
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
  
  public int getId(int paramInt)
  {
    if (paramInt >= this.mLayerState.mNum) {
      throw new IndexOutOfBoundsException();
    }
    return this.mLayerState.mChildren[paramInt].mId;
  }
  
  public int getIntrinsicHeight()
  {
    int i = -1;
    int i2 = 0;
    int i1 = 0;
    if (LayerState.-get1(this.mLayerState) == 0) {}
    int n;
    ChildDrawable localChildDrawable;
    int i3;
    for (int m = 1;; m = 0)
    {
      ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
      int i4 = this.mLayerState.mNum;
      n = 0;
      for (;;)
      {
        if (n >= i4) {
          return i;
        }
        localChildDrawable = arrayOfChildDrawable[n];
        if (localChildDrawable.mDrawable != null) {
          break;
        }
        i3 = i2;
        k = i1;
        n += 1;
        i1 = k;
        i2 = i3;
      }
    }
    int j;
    if (localChildDrawable.mHeight < 0)
    {
      j = localChildDrawable.mDrawable.getIntrinsicHeight();
      label110:
      if (j >= 0) {
        break label176;
      }
    }
    label176:
    for (int k = -1;; k = localChildDrawable.mInsetT + j + localChildDrawable.mInsetB + i2 + i1)
    {
      j = i;
      if (k > i) {
        j = k;
      }
      i = j;
      k = i1;
      i3 = i2;
      if (m == 0) {
        break;
      }
      i3 = i2 + this.mPaddingT[n];
      k = i1 + this.mPaddingB[n];
      i = j;
      break;
      j = localChildDrawable.mHeight;
      break label110;
    }
    return i;
  }
  
  public int getIntrinsicWidth()
  {
    int i = -1;
    int i3 = 0;
    int i1 = 0;
    int m;
    if (LayerState.-get1(this.mLayerState) == 0)
    {
      m = 1;
      if (getLayoutDirection() != 1) {
        break label108;
      }
    }
    int i2;
    ChildDrawable localChildDrawable;
    int i4;
    label108:
    for (int n = 1;; n = 0)
    {
      ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
      int i5 = this.mLayerState.mNum;
      i2 = 0;
      for (;;)
      {
        if (i2 >= i5) {
          return i;
        }
        localChildDrawable = arrayOfChildDrawable[i2];
        if (localChildDrawable.mDrawable != null) {
          break;
        }
        i4 = i;
        k = i1;
        i = i3;
        i2 += 1;
        i3 = i;
        i1 = k;
        i = i4;
      }
      m = 0;
      break;
    }
    label125:
    int j;
    if (n != 0)
    {
      k = localChildDrawable.mInsetE;
      if (n == 0) {
        break label245;
      }
      j = localChildDrawable.mInsetS;
      label136:
      if (k != Integer.MIN_VALUE) {
        break label254;
      }
      k = localChildDrawable.mInsetL;
      label148:
      if (j != Integer.MIN_VALUE) {
        break label257;
      }
      j = localChildDrawable.mInsetR;
      label160:
      if (localChildDrawable.mWidth >= 0) {
        break label260;
      }
      i4 = localChildDrawable.mDrawable.getIntrinsicWidth();
      label178:
      if (i4 >= 0) {
        break label270;
      }
    }
    label245:
    label254:
    label257:
    label260:
    label270:
    for (int k = -1;; k = i4 + k + j + i3 + i1)
    {
      j = i;
      if (k > i) {
        j = k;
      }
      i = i3;
      k = i1;
      i4 = j;
      if (m == 0) {
        break;
      }
      i = i3 + this.mPaddingL[i2];
      k = i1 + this.mPaddingR[i2];
      i4 = j;
      break;
      k = localChildDrawable.mInsetS;
      break label125;
      j = localChildDrawable.mInsetE;
      break label136;
      break label148;
      break label160;
      i4 = localChildDrawable.mWidth;
      break label178;
    }
    return i;
  }
  
  public int getLayerGravity(int paramInt)
  {
    return this.mLayerState.mChildren[paramInt].mGravity;
  }
  
  public int getLayerHeight(int paramInt)
  {
    return this.mLayerState.mChildren[paramInt].mHeight;
  }
  
  public int getLayerInsetBottom(int paramInt)
  {
    return this.mLayerState.mChildren[paramInt].mInsetB;
  }
  
  public int getLayerInsetEnd(int paramInt)
  {
    return this.mLayerState.mChildren[paramInt].mInsetE;
  }
  
  public int getLayerInsetLeft(int paramInt)
  {
    return this.mLayerState.mChildren[paramInt].mInsetL;
  }
  
  public int getLayerInsetRight(int paramInt)
  {
    return this.mLayerState.mChildren[paramInt].mInsetR;
  }
  
  public int getLayerInsetStart(int paramInt)
  {
    return this.mLayerState.mChildren[paramInt].mInsetS;
  }
  
  public int getLayerInsetTop(int paramInt)
  {
    return this.mLayerState.mChildren[paramInt].mInsetT;
  }
  
  public int getLayerWidth(int paramInt)
  {
    return this.mLayerState.mChildren[paramInt].mWidth;
  }
  
  public int getLeftPadding()
  {
    return this.mLayerState.mPaddingLeft;
  }
  
  public int getNumberOfLayers()
  {
    return this.mLayerState.mNum;
  }
  
  public int getOpacity()
  {
    if (this.mLayerState.mOpacityOverride != 0) {
      return this.mLayerState.mOpacityOverride;
    }
    return this.mLayerState.getOpacity();
  }
  
  public void getOutline(Outline paramOutline)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null)
      {
        localDrawable.getOutline(paramOutline);
        if (!paramOutline.isEmpty()) {
          return;
        }
      }
      i += 1;
    }
  }
  
  public boolean getPadding(Rect paramRect)
  {
    LayerState localLayerState = this.mLayerState;
    int j;
    label43:
    int i;
    if (LayerState.-get1(localLayerState) == 0)
    {
      computeNestedPadding(paramRect);
      int k = localLayerState.mPaddingTop;
      int m = localLayerState.mPaddingBottom;
      if (getLayoutDirection() != 1) {
        break label135;
      }
      j = 1;
      if (j == 0) {
        break label140;
      }
      i = localLayerState.mPaddingEnd;
      label53:
      if (j == 0) {
        break label149;
      }
      j = localLayerState.mPaddingStart;
      label63:
      if (i < 0) {
        break label158;
      }
      label67:
      if (j < 0) {
        break label167;
      }
      label71:
      if (i >= 0) {
        paramRect.left = i;
      }
      if (k >= 0) {
        paramRect.top = k;
      }
      if (j >= 0) {
        paramRect.right = j;
      }
      if (m >= 0) {
        paramRect.bottom = m;
      }
      if ((paramRect.left == 0) && (paramRect.top == 0)) {
        break label176;
      }
    }
    label135:
    label140:
    label149:
    label158:
    label167:
    label176:
    while ((paramRect.right != 0) || (paramRect.bottom != 0))
    {
      return true;
      computeStackedPadding(paramRect);
      break;
      j = 0;
      break label43;
      i = localLayerState.mPaddingStart;
      break label53;
      j = localLayerState.mPaddingEnd;
      break label63;
      i = localLayerState.mPaddingLeft;
      break label67;
      j = localLayerState.mPaddingRight;
      break label71;
    }
    return false;
  }
  
  public int getPaddingMode()
  {
    return LayerState.-get1(this.mLayerState);
  }
  
  public int getRightPadding()
  {
    return this.mLayerState.mPaddingRight;
  }
  
  public int getStartPadding()
  {
    return this.mLayerState.mPaddingStart;
  }
  
  public int getTopPadding()
  {
    return this.mLayerState.mPaddingTop;
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    super.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    LayerState localLayerState = this.mLayerState;
    if (localLayerState == null) {
      return;
    }
    int j = Drawable.resolveDensity(paramResources, 0);
    localLayerState.setDensity(j);
    Object localObject = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.LayerDrawable);
    updateStateFromTypedArray((TypedArray)localObject);
    ((TypedArray)localObject).recycle();
    localObject = localLayerState.mChildren;
    int k = localLayerState.mNum;
    int i = 0;
    while (i < k)
    {
      localObject[i].setDensity(j);
      i += 1;
    }
    inflateLayers(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    ensurePadding();
    refreshPadding();
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (this.mSuspendChildInvalidation)
    {
      this.mChildRequestedInvalidation = true;
      return;
    }
    invalidateSelf();
  }
  
  public boolean isAutoMirrored()
  {
    return LayerState.-get0(this.mLayerState);
  }
  
  public boolean isProjected()
  {
    if (super.isProjected()) {
      return true;
    }
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      if (arrayOfChildDrawable[i].mDrawable.isProjected()) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public boolean isStateful()
  {
    return this.mLayerState.isStateful();
  }
  
  public void jumpToCurrentState()
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.jumpToCurrentState();
      }
      i += 1;
    }
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mLayerState = createConstantState(this.mLayerState, null);
      ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
      int j = this.mLayerState.mNum;
      int i = 0;
      while (i < j)
      {
        Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
        if (localDrawable != null) {
          localDrawable.mutate();
        }
        i += 1;
      }
      this.mMutated = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    updateLayerBounds(paramRect);
  }
  
  public boolean onLayoutDirectionChanged(int paramInt)
  {
    boolean bool1 = false;
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      boolean bool2 = bool1;
      if (localDrawable != null) {
        bool2 = bool1 | localDrawable.setLayoutDirection(paramInt);
      }
      i += 1;
      bool1 = bool2;
    }
    updateLayerBounds(getBounds());
    return bool1;
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    boolean bool1 = false;
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      boolean bool2 = bool1;
      if (localDrawable != null)
      {
        bool2 = bool1;
        if (localDrawable.setLevel(paramInt))
        {
          refreshChildPadding(i, arrayOfChildDrawable[i]);
          bool2 = true;
        }
      }
      i += 1;
      bool1 = bool2;
    }
    if (bool1) {
      updateLayerBounds(getBounds());
    }
    return bool1;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    boolean bool1 = false;
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      boolean bool2 = bool1;
      if (localDrawable != null)
      {
        bool2 = bool1;
        if (localDrawable.isStateful())
        {
          bool2 = bool1;
          if (localDrawable.setState(paramArrayOfInt))
          {
            refreshChildPadding(i, arrayOfChildDrawable[i]);
            bool2 = true;
          }
        }
      }
      i += 1;
      bool1 = bool2;
    }
    if (bool1) {
      updateLayerBounds(getBounds());
    }
    return bool1;
  }
  
  void refreshPadding()
  {
    int j = this.mLayerState.mNum;
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int i = 0;
    while (i < j)
    {
      refreshChildPadding(i, arrayOfChildDrawable[i]);
      i += 1;
    }
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.setAlpha(paramInt);
      }
      i += 1;
    }
  }
  
  public void setAutoMirrored(boolean paramBoolean)
  {
    LayerState.-set0(this.mLayerState, paramBoolean);
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.setAutoMirrored(paramBoolean);
      }
      i += 1;
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.setColorFilter(paramColorFilter);
      }
      i += 1;
    }
  }
  
  public void setDither(boolean paramBoolean)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.setDither(paramBoolean);
      }
      i += 1;
    }
  }
  
  public void setDrawable(int paramInt, Drawable paramDrawable)
  {
    if (paramInt >= this.mLayerState.mNum) {
      throw new IndexOutOfBoundsException();
    }
    ChildDrawable localChildDrawable = this.mLayerState.mChildren[paramInt];
    if (localChildDrawable.mDrawable != null)
    {
      if (paramDrawable != null) {
        paramDrawable.setBounds(localChildDrawable.mDrawable.getBounds());
      }
      localChildDrawable.mDrawable.setCallback(null);
    }
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
    }
    localChildDrawable.mDrawable = paramDrawable;
    this.mLayerState.invalidateCache();
    refreshChildPadding(paramInt, localChildDrawable);
  }
  
  public boolean setDrawableByLayerId(int paramInt, Drawable paramDrawable)
  {
    paramInt = findIndexByLayerId(paramInt);
    if (paramInt < 0) {
      return false;
    }
    setDrawable(paramInt, paramDrawable);
    return true;
  }
  
  public void setHotspot(float paramFloat1, float paramFloat2)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.setHotspot(paramFloat1, paramFloat2);
      }
      i += 1;
    }
  }
  
  public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.setHotspotBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      }
      i += 1;
    }
    if (this.mHotspotBounds == null)
    {
      this.mHotspotBounds = new Rect(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    this.mHotspotBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setId(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mId = paramInt2;
  }
  
  public void setLayerGravity(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mGravity = paramInt2;
  }
  
  public void setLayerHeight(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mHeight = paramInt2;
  }
  
  public void setLayerInset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    setLayerInsetInternal(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
  
  public void setLayerInsetBottom(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mInsetB = paramInt2;
  }
  
  public void setLayerInsetEnd(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mInsetE = paramInt2;
  }
  
  public void setLayerInsetLeft(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mInsetL = paramInt2;
  }
  
  public void setLayerInsetRelative(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    setLayerInsetInternal(paramInt1, 0, paramInt3, 0, paramInt5, paramInt2, paramInt4);
  }
  
  public void setLayerInsetRight(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mInsetR = paramInt2;
  }
  
  public void setLayerInsetStart(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mInsetS = paramInt2;
  }
  
  public void setLayerInsetTop(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mInsetT = paramInt2;
  }
  
  public void setLayerSize(int paramInt1, int paramInt2, int paramInt3)
  {
    ChildDrawable localChildDrawable = this.mLayerState.mChildren[paramInt1];
    localChildDrawable.mWidth = paramInt2;
    localChildDrawable.mHeight = paramInt3;
  }
  
  public void setLayerWidth(int paramInt1, int paramInt2)
  {
    this.mLayerState.mChildren[paramInt1].mWidth = paramInt2;
  }
  
  public void setOpacity(int paramInt)
  {
    this.mLayerState.mOpacityOverride = paramInt;
  }
  
  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    LayerState localLayerState = this.mLayerState;
    localLayerState.mPaddingLeft = paramInt1;
    localLayerState.mPaddingTop = paramInt2;
    localLayerState.mPaddingRight = paramInt3;
    localLayerState.mPaddingBottom = paramInt4;
    localLayerState.mPaddingStart = -1;
    localLayerState.mPaddingEnd = -1;
  }
  
  public void setPaddingMode(int paramInt)
  {
    if (LayerState.-get1(this.mLayerState) != paramInt) {
      LayerState.-set1(this.mLayerState, paramInt);
    }
  }
  
  public void setPaddingRelative(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    LayerState localLayerState = this.mLayerState;
    localLayerState.mPaddingStart = paramInt1;
    localLayerState.mPaddingTop = paramInt2;
    localLayerState.mPaddingEnd = paramInt3;
    localLayerState.mPaddingBottom = paramInt4;
    localLayerState.mPaddingLeft = -1;
    localLayerState.mPaddingRight = -1;
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.setTintList(paramColorStateList);
      }
      i += 1;
    }
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.setTintMode(paramMode);
      }
      i += 1;
    }
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    ChildDrawable[] arrayOfChildDrawable = this.mLayerState.mChildren;
    int j = this.mLayerState.mNum;
    int i = 0;
    while (i < j)
    {
      Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
      if (localDrawable != null) {
        localDrawable.setVisible(paramBoolean1, paramBoolean2);
      }
      i += 1;
    }
    return bool;
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    unscheduleSelf(paramRunnable);
  }
  
  static class ChildDrawable
  {
    public int mDensity = 160;
    public Drawable mDrawable;
    public int mGravity = 0;
    public int mHeight = -1;
    public int mId = -1;
    public int mInsetB;
    public int mInsetE = Integer.MIN_VALUE;
    public int mInsetL;
    public int mInsetR;
    public int mInsetS = Integer.MIN_VALUE;
    public int mInsetT;
    public int[] mThemeAttrs;
    public int mWidth = -1;
    
    ChildDrawable(int paramInt)
    {
      this.mDensity = paramInt;
    }
    
    ChildDrawable(ChildDrawable paramChildDrawable, LayerDrawable paramLayerDrawable, Resources paramResources)
    {
      Drawable localDrawable = paramChildDrawable.mDrawable;
      Object localObject;
      if (localDrawable != null)
      {
        localObject = localDrawable.getConstantState();
        if (localObject == null)
        {
          localObject = localDrawable;
          ((Drawable)localObject).setCallback(paramLayerDrawable);
          ((Drawable)localObject).setLayoutDirection(localDrawable.getLayoutDirection());
          ((Drawable)localObject).setBounds(localDrawable.getBounds());
          ((Drawable)localObject).setLevel(localDrawable.getLevel());
        }
      }
      for (;;)
      {
        this.mDrawable = ((Drawable)localObject);
        this.mThemeAttrs = paramChildDrawable.mThemeAttrs;
        this.mInsetL = paramChildDrawable.mInsetL;
        this.mInsetT = paramChildDrawable.mInsetT;
        this.mInsetR = paramChildDrawable.mInsetR;
        this.mInsetB = paramChildDrawable.mInsetB;
        this.mInsetS = paramChildDrawable.mInsetS;
        this.mInsetE = paramChildDrawable.mInsetE;
        this.mWidth = paramChildDrawable.mWidth;
        this.mHeight = paramChildDrawable.mHeight;
        this.mGravity = paramChildDrawable.mGravity;
        this.mId = paramChildDrawable.mId;
        this.mDensity = Drawable.resolveDensity(paramResources, paramChildDrawable.mDensity);
        if (paramChildDrawable.mDensity != this.mDensity) {
          applyDensityScaling(paramChildDrawable.mDensity, this.mDensity);
        }
        return;
        if (paramResources != null)
        {
          localObject = ((Drawable.ConstantState)localObject).newDrawable(paramResources);
          break;
        }
        localObject = ((Drawable.ConstantState)localObject).newDrawable();
        break;
        localObject = null;
      }
    }
    
    private void applyDensityScaling(int paramInt1, int paramInt2)
    {
      this.mInsetL = Drawable.scaleFromDensity(this.mInsetL, paramInt1, paramInt2, false);
      this.mInsetT = Drawable.scaleFromDensity(this.mInsetT, paramInt1, paramInt2, false);
      this.mInsetR = Drawable.scaleFromDensity(this.mInsetR, paramInt1, paramInt2, false);
      this.mInsetB = Drawable.scaleFromDensity(this.mInsetB, paramInt1, paramInt2, false);
      if (this.mInsetS != Integer.MIN_VALUE) {
        this.mInsetS = Drawable.scaleFromDensity(this.mInsetS, paramInt1, paramInt2, false);
      }
      if (this.mInsetE != Integer.MIN_VALUE) {
        this.mInsetE = Drawable.scaleFromDensity(this.mInsetE, paramInt1, paramInt2, false);
      }
      if (this.mWidth > 0) {
        this.mWidth = Drawable.scaleFromDensity(this.mWidth, paramInt1, paramInt2, true);
      }
      if (this.mHeight > 0) {
        this.mHeight = Drawable.scaleFromDensity(this.mHeight, paramInt1, paramInt2, true);
      }
    }
    
    public boolean canApplyTheme()
    {
      if (this.mThemeAttrs == null)
      {
        if (this.mDrawable != null) {
          return this.mDrawable.canApplyTheme();
        }
      }
      else {
        return true;
      }
      return false;
    }
    
    public final void setDensity(int paramInt)
    {
      if (this.mDensity != paramInt)
      {
        int i = this.mDensity;
        this.mDensity = paramInt;
        applyDensityScaling(i, paramInt);
      }
    }
  }
  
  static class LayerState
    extends Drawable.ConstantState
  {
    private boolean mAutoMirrored = false;
    int mChangingConfigurations;
    LayerDrawable.ChildDrawable[] mChildren;
    int mChildrenChangingConfigurations;
    int mDensity;
    private boolean mHaveIsStateful;
    private boolean mHaveOpacity;
    private boolean mIsStateful;
    int mNum;
    private int mOpacity;
    int mOpacityOverride = 0;
    int mPaddingBottom = -1;
    int mPaddingEnd = -1;
    int mPaddingLeft = -1;
    private int mPaddingMode = 0;
    int mPaddingRight = -1;
    int mPaddingStart = -1;
    int mPaddingTop = -1;
    private int[] mThemeAttrs;
    
    LayerState(LayerState paramLayerState, LayerDrawable paramLayerDrawable, Resources paramResources)
    {
      if (paramLayerState != null) {}
      for (int i = paramLayerState.mDensity;; i = 0)
      {
        this.mDensity = Drawable.resolveDensity(paramResources, i);
        if (paramLayerState == null) {
          break label302;
        }
        LayerDrawable.ChildDrawable[] arrayOfChildDrawable = paramLayerState.mChildren;
        int j = paramLayerState.mNum;
        this.mNum = j;
        this.mChildren = new LayerDrawable.ChildDrawable[j];
        this.mChangingConfigurations = paramLayerState.mChangingConfigurations;
        this.mChildrenChangingConfigurations = paramLayerState.mChildrenChangingConfigurations;
        i = 0;
        while (i < j)
        {
          LayerDrawable.ChildDrawable localChildDrawable = arrayOfChildDrawable[i];
          this.mChildren[i] = new LayerDrawable.ChildDrawable(localChildDrawable, paramLayerDrawable, paramResources);
          i += 1;
        }
      }
      this.mHaveOpacity = paramLayerState.mHaveOpacity;
      this.mOpacity = paramLayerState.mOpacity;
      this.mHaveIsStateful = paramLayerState.mHaveIsStateful;
      this.mIsStateful = paramLayerState.mIsStateful;
      this.mAutoMirrored = paramLayerState.mAutoMirrored;
      this.mPaddingMode = paramLayerState.mPaddingMode;
      this.mThemeAttrs = paramLayerState.mThemeAttrs;
      this.mPaddingTop = paramLayerState.mPaddingTop;
      this.mPaddingBottom = paramLayerState.mPaddingBottom;
      this.mPaddingLeft = paramLayerState.mPaddingLeft;
      this.mPaddingRight = paramLayerState.mPaddingRight;
      this.mPaddingStart = paramLayerState.mPaddingStart;
      this.mPaddingEnd = paramLayerState.mPaddingEnd;
      this.mOpacityOverride = paramLayerState.mOpacityOverride;
      if (paramLayerState.mDensity != this.mDensity) {
        applyDensityScaling(paramLayerState.mDensity, this.mDensity);
      }
      return;
      label302:
      this.mNum = 0;
      this.mChildren = null;
    }
    
    private void applyDensityScaling(int paramInt1, int paramInt2)
    {
      if (this.mPaddingLeft > 0) {
        this.mPaddingLeft = Drawable.scaleFromDensity(this.mPaddingLeft, paramInt1, paramInt2, false);
      }
      if (this.mPaddingTop > 0) {
        this.mPaddingTop = Drawable.scaleFromDensity(this.mPaddingTop, paramInt1, paramInt2, false);
      }
      if (this.mPaddingRight > 0) {
        this.mPaddingRight = Drawable.scaleFromDensity(this.mPaddingRight, paramInt1, paramInt2, false);
      }
      if (this.mPaddingBottom > 0) {
        this.mPaddingBottom = Drawable.scaleFromDensity(this.mPaddingBottom, paramInt1, paramInt2, false);
      }
      if (this.mPaddingStart > 0) {
        this.mPaddingStart = Drawable.scaleFromDensity(this.mPaddingStart, paramInt1, paramInt2, false);
      }
      if (this.mPaddingEnd > 0) {
        this.mPaddingEnd = Drawable.scaleFromDensity(this.mPaddingEnd, paramInt1, paramInt2, false);
      }
    }
    
    public int addAtlasableBitmaps(Collection<Bitmap> paramCollection)
    {
      LayerDrawable.ChildDrawable[] arrayOfChildDrawable = this.mChildren;
      int m = this.mNum;
      int j = 0;
      int i = 0;
      while (i < m)
      {
        Object localObject = arrayOfChildDrawable[i].mDrawable;
        int k = j;
        if (localObject != null)
        {
          localObject = ((Drawable)localObject).getConstantState();
          k = j;
          if (localObject != null) {
            k = j + ((Drawable.ConstantState)localObject).addAtlasableBitmaps(paramCollection);
          }
        }
        i += 1;
        j = k;
      }
      return j;
    }
    
    public boolean canApplyTheme()
    {
      if ((this.mThemeAttrs != null) || (super.canApplyTheme())) {
        return true;
      }
      LayerDrawable.ChildDrawable[] arrayOfChildDrawable = this.mChildren;
      int j = this.mNum;
      int i = 0;
      while (i < j)
      {
        if (arrayOfChildDrawable[i].canApplyTheme()) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public final boolean canConstantState()
    {
      LayerDrawable.ChildDrawable[] arrayOfChildDrawable = this.mChildren;
      int j = this.mNum;
      int i = 0;
      while (i < j)
      {
        Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
        if ((localDrawable != null) && (localDrawable.getConstantState() == null)) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    
    public int getChangingConfigurations()
    {
      return this.mChangingConfigurations | this.mChildrenChangingConfigurations;
    }
    
    public final int getOpacity()
    {
      if (this.mHaveOpacity) {
        return this.mOpacity;
      }
      LayerDrawable.ChildDrawable[] arrayOfChildDrawable = this.mChildren;
      int m = this.mNum;
      int k = -1;
      int i = 0;
      int j = k;
      if (i < m)
      {
        if (arrayOfChildDrawable[i].mDrawable != null) {
          j = i;
        }
      }
      else {
        if (j < 0) {
          break label119;
        }
      }
      label119:
      for (i = arrayOfChildDrawable[j].mDrawable.getOpacity();; i = -2)
      {
        k = j + 1;
        j = i;
        i = k;
        while (i < m)
        {
          Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
          k = j;
          if (localDrawable != null) {
            k = Drawable.resolveOpacity(j, localDrawable.getOpacity());
          }
          i += 1;
          j = k;
        }
        i += 1;
        break;
      }
      this.mOpacity = j;
      this.mHaveOpacity = true;
      return j;
    }
    
    public void invalidateCache()
    {
      this.mHaveOpacity = false;
      this.mHaveIsStateful = false;
    }
    
    public final boolean isStateful()
    {
      if (this.mHaveIsStateful) {
        return this.mIsStateful;
      }
      LayerDrawable.ChildDrawable[] arrayOfChildDrawable = this.mChildren;
      int j = this.mNum;
      boolean bool2 = false;
      int i = 0;
      for (;;)
      {
        boolean bool1 = bool2;
        if (i < j)
        {
          Drawable localDrawable = arrayOfChildDrawable[i].mDrawable;
          if ((localDrawable != null) && (localDrawable.isStateful())) {
            bool1 = true;
          }
        }
        else
        {
          this.mIsStateful = bool1;
          this.mHaveIsStateful = true;
          return bool1;
        }
        i += 1;
      }
    }
    
    public Drawable newDrawable()
    {
      return new LayerDrawable(this, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new LayerDrawable(this, paramResources);
    }
    
    protected void onDensityChanged(int paramInt1, int paramInt2)
    {
      applyDensityScaling(paramInt1, paramInt2);
    }
    
    public final void setDensity(int paramInt)
    {
      if (this.mDensity != paramInt)
      {
        int i = this.mDensity;
        this.mDensity = paramInt;
        onDensityChanged(i, paramInt);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/LayerDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */