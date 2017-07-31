package android.support.v4.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.ref.WeakReference;

public class PagerTitleStrip
  extends ViewGroup
  implements ViewPager.Decor
{
  private static final int[] ATTRS = { 16842804, 16842901, 16842904, 16842927 };
  private static final PagerTitleStripImpl IMPL = new PagerTitleStripImplIcs();
  private static final float SIDE_ALPHA = 0.6F;
  private static final String TAG = "PagerTitleStrip";
  private static final int[] TEXT_ATTRS = { 16843660 };
  private static final int TEXT_SPACING = 16;
  TextView mCurrText;
  private int mGravity;
  private int mLastKnownCurrentPage = -1;
  private float mLastKnownPositionOffset = -1.0F;
  TextView mNextText;
  private int mNonPrimaryAlpha;
  private final PageListener mPageListener = new PageListener(null);
  ViewPager mPager;
  TextView mPrevText;
  private int mScaledTextSpacing;
  int mTextColor;
  private boolean mUpdatingPositions;
  private boolean mUpdatingText;
  private WeakReference<PagerAdapter> mWatchingAdapter;
  
  static
  {
    if (Build.VERSION.SDK_INT < 14)
    {
      IMPL = new PagerTitleStripImplBase();
      return;
    }
  }
  
  public PagerTitleStrip(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PagerTitleStrip(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TextView localTextView = new TextView(paramContext);
    this.mPrevText = localTextView;
    addView(localTextView);
    localTextView = new TextView(paramContext);
    this.mCurrText = localTextView;
    addView(localTextView);
    localTextView = new TextView(paramContext);
    this.mNextText = localTextView;
    addView(localTextView);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, ATTRS);
    int i = paramAttributeSet.getResourceId(0, 0);
    int j;
    if (i == 0)
    {
      j = paramAttributeSet.getDimensionPixelSize(1, 0);
      if (j != 0) {
        break label284;
      }
      label132:
      if (paramAttributeSet.hasValue(2)) {
        break label295;
      }
      label140:
      this.mGravity = paramAttributeSet.getInteger(3, 80);
      paramAttributeSet.recycle();
      this.mTextColor = this.mCurrText.getTextColors().getDefaultColor();
      setNonPrimaryAlpha(0.6F);
      this.mPrevText.setEllipsize(TextUtils.TruncateAt.END);
      this.mCurrText.setEllipsize(TextUtils.TruncateAt.END);
      this.mNextText.setEllipsize(TextUtils.TruncateAt.END);
      if (i != 0) {
        break label333;
      }
      label209:
      if (bool) {
        break label357;
      }
      this.mPrevText.setSingleLine();
      this.mCurrText.setSingleLine();
      this.mNextText.setSingleLine();
    }
    for (;;)
    {
      this.mScaledTextSpacing = ((int)(paramContext.getResources().getDisplayMetrics().density * 16.0F));
      return;
      this.mPrevText.setTextAppearance(paramContext, i);
      this.mCurrText.setTextAppearance(paramContext, i);
      this.mNextText.setTextAppearance(paramContext, i);
      break;
      label284:
      setTextSize(0, j);
      break label132;
      label295:
      j = paramAttributeSet.getColor(2, 0);
      this.mPrevText.setTextColor(j);
      this.mCurrText.setTextColor(j);
      this.mNextText.setTextColor(j);
      break label140;
      label333:
      paramAttributeSet = paramContext.obtainStyledAttributes(i, TEXT_ATTRS);
      bool = paramAttributeSet.getBoolean(0, false);
      paramAttributeSet.recycle();
      break label209;
      label357:
      setSingleLineAllCaps(this.mPrevText);
      setSingleLineAllCaps(this.mCurrText);
      setSingleLineAllCaps(this.mNextText);
    }
  }
  
  private static void setSingleLineAllCaps(TextView paramTextView)
  {
    IMPL.setSingleLineAllCaps(paramTextView);
  }
  
  int getMinHeight()
  {
    Drawable localDrawable = getBackground();
    if (localDrawable == null) {
      return 0;
    }
    return localDrawable.getIntrinsicHeight();
  }
  
  public int getTextSpacing()
  {
    return this.mScaledTextSpacing;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    Object localObject = getParent();
    PagerAdapter localPagerAdapter;
    if ((localObject instanceof ViewPager))
    {
      localObject = (ViewPager)localObject;
      localPagerAdapter = ((ViewPager)localObject).getAdapter();
      ((ViewPager)localObject).setInternalPageChangeListener(this.mPageListener);
      ((ViewPager)localObject).setOnAdapterChangeListener(this.mPageListener);
      this.mPager = ((ViewPager)localObject);
      if (this.mWatchingAdapter != null) {
        break label74;
      }
    }
    label74:
    for (localObject = null;; localObject = (PagerAdapter)this.mWatchingAdapter.get())
    {
      updateAdapter((PagerAdapter)localObject, localPagerAdapter);
      return;
      throw new IllegalStateException("PagerTitleStrip must be a direct child of a ViewPager.");
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mPager == null) {
      return;
    }
    updateAdapter(this.mPager.getAdapter(), null);
    this.mPager.setInternalPageChangeListener(null);
    this.mPager.setOnAdapterChangeListener(null);
    this.mPager = null;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    float f = 0.0F;
    if (this.mPager == null) {
      return;
    }
    if (this.mLastKnownPositionOffset >= 0.0F) {
      f = this.mLastKnownPositionOffset;
    }
    updateTextPositions(this.mLastKnownCurrentPage, f, true);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.getMode(paramInt1);
    int i = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    if (j == 1073741824)
    {
      j = getMinHeight();
      int k = getPaddingTop() + getPaddingBottom();
      int m = View.MeasureSpec.makeMeasureSpec((int)(paramInt1 * 0.8F), Integer.MIN_VALUE);
      int n = View.MeasureSpec.makeMeasureSpec(paramInt2 - k, Integer.MIN_VALUE);
      this.mPrevText.measure(m, n);
      this.mCurrText.measure(m, n);
      this.mNextText.measure(m, n);
      if (i != 1073741824) {
        setMeasuredDimension(paramInt1, Math.max(j, this.mCurrText.getMeasuredHeight() + k));
      }
    }
    else
    {
      throw new IllegalStateException("Must measure with an exact width");
    }
    setMeasuredDimension(paramInt1, paramInt2);
  }
  
  public void requestLayout()
  {
    if (this.mUpdatingText) {
      return;
    }
    super.requestLayout();
  }
  
  public void setGravity(int paramInt)
  {
    this.mGravity = paramInt;
    requestLayout();
  }
  
  public void setNonPrimaryAlpha(float paramFloat)
  {
    this.mNonPrimaryAlpha = ((int)(255.0F * paramFloat) & 0xFF);
    int i = this.mNonPrimaryAlpha << 24 | this.mTextColor & 0xFFFFFF;
    this.mPrevText.setTextColor(i);
    this.mNextText.setTextColor(i);
  }
  
  public void setTextColor(int paramInt)
  {
    this.mTextColor = paramInt;
    this.mCurrText.setTextColor(paramInt);
    paramInt = this.mNonPrimaryAlpha << 24 | this.mTextColor & 0xFFFFFF;
    this.mPrevText.setTextColor(paramInt);
    this.mNextText.setTextColor(paramInt);
  }
  
  public void setTextSize(int paramInt, float paramFloat)
  {
    this.mPrevText.setTextSize(paramInt, paramFloat);
    this.mCurrText.setTextSize(paramInt, paramFloat);
    this.mNextText.setTextSize(paramInt, paramFloat);
  }
  
  public void setTextSpacing(int paramInt)
  {
    this.mScaledTextSpacing = paramInt;
    requestLayout();
  }
  
  void updateAdapter(PagerAdapter paramPagerAdapter1, PagerAdapter paramPagerAdapter2)
  {
    if (paramPagerAdapter1 == null) {
      if (paramPagerAdapter2 != null) {
        break label32;
      }
    }
    for (;;)
    {
      if (this.mPager != null) {
        break label55;
      }
      return;
      paramPagerAdapter1.unregisterDataSetObserver(this.mPageListener);
      this.mWatchingAdapter = null;
      break;
      label32:
      paramPagerAdapter2.registerDataSetObserver(this.mPageListener);
      this.mWatchingAdapter = new WeakReference(paramPagerAdapter2);
    }
    label55:
    this.mLastKnownCurrentPage = -1;
    this.mLastKnownPositionOffset = -1.0F;
    updateText(this.mPager.getCurrentItem(), paramPagerAdapter2);
    requestLayout();
  }
  
  void updateText(int paramInt, PagerAdapter paramPagerAdapter)
  {
    Object localObject2 = null;
    int i;
    label19:
    Object localObject1;
    if (paramPagerAdapter == null)
    {
      i = 0;
      this.mUpdatingText = true;
      if (paramInt >= 1) {
        break label198;
      }
      localObject1 = null;
      label22:
      this.mPrevText.setText((CharSequence)localObject1);
      TextView localTextView = this.mCurrText;
      if (paramPagerAdapter != null) {
        break label214;
      }
      label41:
      localObject1 = null;
      label44:
      localTextView.setText((CharSequence)localObject1);
      if (paramInt + 1 < i) {
        break label229;
      }
      localObject1 = localObject2;
      label62:
      this.mNextText.setText((CharSequence)localObject1);
      int m = getWidth();
      int n = getPaddingLeft();
      int i1 = getPaddingRight();
      i = getHeight();
      int j = getPaddingTop();
      int k = getPaddingBottom();
      m = View.MeasureSpec.makeMeasureSpec((int)((m - n - i1) * 0.8F), Integer.MIN_VALUE);
      i = View.MeasureSpec.makeMeasureSpec(i - j - k, Integer.MIN_VALUE);
      this.mPrevText.measure(m, i);
      this.mCurrText.measure(m, i);
      this.mNextText.measure(m, i);
      this.mLastKnownCurrentPage = paramInt;
      if (!this.mUpdatingPositions) {
        break label249;
      }
    }
    for (;;)
    {
      this.mUpdatingText = false;
      return;
      i = paramPagerAdapter.getCount();
      break;
      label198:
      if (paramPagerAdapter == null) {
        break label19;
      }
      localObject1 = paramPagerAdapter.getPageTitle(paramInt - 1);
      break label22;
      label214:
      if (paramInt >= i) {
        break label41;
      }
      localObject1 = paramPagerAdapter.getPageTitle(paramInt);
      break label44;
      label229:
      localObject1 = localObject2;
      if (paramPagerAdapter == null) {
        break label62;
      }
      localObject1 = paramPagerAdapter.getPageTitle(paramInt + 1);
      break label62;
      label249:
      updateTextPositions(paramInt, this.mLastKnownPositionOffset, false);
    }
  }
  
  void updateTextPositions(int paramInt, float paramFloat, boolean paramBoolean)
  {
    int m;
    int i4;
    int k;
    int i3;
    int n;
    int i;
    int i2;
    int i1;
    int j;
    int i5;
    int i7;
    int i6;
    int i8;
    if (paramInt == this.mLastKnownCurrentPage)
    {
      if (!paramBoolean) {
        break label457;
      }
      this.mUpdatingPositions = true;
      m = this.mPrevText.getMeasuredWidth();
      i4 = this.mCurrText.getMeasuredWidth();
      k = this.mNextText.getMeasuredWidth();
      i3 = i4 / 2;
      n = getWidth();
      i = getHeight();
      i2 = getPaddingLeft();
      i1 = getPaddingRight();
      paramInt = getPaddingTop();
      j = getPaddingBottom();
      i5 = i1 + i3;
      float f2 = 0.5F + paramFloat;
      float f1 = f2;
      if (f2 > 1.0F) {
        f1 = f2 - 1.0F;
      }
      i3 = n - i5 - (int)(f1 * (n - (i2 + i3) - i5)) - i4 / 2;
      i4 = i3 + i4;
      i7 = this.mPrevText.getBaseline();
      i6 = this.mCurrText.getBaseline();
      i5 = this.mNextText.getBaseline();
      i8 = Math.max(Math.max(i7, i6), i5);
      i7 = i8 - i7;
      i6 = i8 - i6;
      i5 = i8 - i5;
      i8 = this.mPrevText.getMeasuredHeight();
      int i9 = this.mCurrText.getMeasuredHeight();
      int i10 = this.mNextText.getMeasuredHeight();
      i8 = Math.max(Math.max(i8 + i7, i9 + i6), i10 + i5);
      switch (this.mGravity & 0x70)
      {
      case 48: 
      default: 
        j = paramInt + i7;
        i = i6 + paramInt;
        paramInt += i5;
      }
    }
    for (;;)
    {
      this.mCurrText.layout(i3, i, i4, this.mCurrText.getMeasuredHeight() + i);
      i = Math.min(i2, i3 - this.mScaledTextSpacing - m);
      this.mPrevText.layout(i, j, m + i, this.mPrevText.getMeasuredHeight() + j);
      i = Math.max(n - i1 - k, this.mScaledTextSpacing + i4);
      this.mNextText.layout(i, paramInt, i + k, this.mNextText.getMeasuredHeight() + paramInt);
      this.mLastKnownPositionOffset = paramFloat;
      this.mUpdatingPositions = false;
      return;
      updateText(paramInt, this.mPager.getAdapter());
      break;
      label457:
      if (paramFloat != this.mLastKnownPositionOffset) {
        break;
      }
      return;
      paramInt = (i - paramInt - j - i8) / 2;
      j = paramInt + i7;
      i = i6 + paramInt;
      paramInt += i5;
      continue;
      paramInt = i - j - i8;
      j = paramInt + i7;
      i = i6 + paramInt;
      paramInt += i5;
    }
  }
  
  private class PageListener
    extends DataSetObserver
    implements ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener
  {
    private int mScrollState;
    
    private PageListener() {}
    
    public void onAdapterChanged(PagerAdapter paramPagerAdapter1, PagerAdapter paramPagerAdapter2)
    {
      PagerTitleStrip.this.updateAdapter(paramPagerAdapter1, paramPagerAdapter2);
    }
    
    public void onChanged()
    {
      float f = 0.0F;
      PagerTitleStrip.this.updateText(PagerTitleStrip.this.mPager.getCurrentItem(), PagerTitleStrip.this.mPager.getAdapter());
      if (PagerTitleStrip.this.mLastKnownPositionOffset >= 0.0F) {
        f = PagerTitleStrip.this.mLastKnownPositionOffset;
      }
      PagerTitleStrip.this.updateTextPositions(PagerTitleStrip.this.mPager.getCurrentItem(), f, true);
    }
    
    public void onPageScrollStateChanged(int paramInt)
    {
      this.mScrollState = paramInt;
    }
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
    {
      paramInt2 = paramInt1;
      if (paramFloat > 0.5F) {
        paramInt2 = paramInt1 + 1;
      }
      PagerTitleStrip.this.updateTextPositions(paramInt2, paramFloat, false);
    }
    
    public void onPageSelected(int paramInt)
    {
      float f = 0.0F;
      if (this.mScrollState != 0) {
        return;
      }
      PagerTitleStrip.this.updateText(PagerTitleStrip.this.mPager.getCurrentItem(), PagerTitleStrip.this.mPager.getAdapter());
      if (PagerTitleStrip.this.mLastKnownPositionOffset >= 0.0F) {
        f = PagerTitleStrip.this.mLastKnownPositionOffset;
      }
      PagerTitleStrip.this.updateTextPositions(PagerTitleStrip.this.mPager.getCurrentItem(), f, true);
    }
  }
  
  static abstract interface PagerTitleStripImpl
  {
    public abstract void setSingleLineAllCaps(TextView paramTextView);
  }
  
  static class PagerTitleStripImplBase
    implements PagerTitleStrip.PagerTitleStripImpl
  {
    public void setSingleLineAllCaps(TextView paramTextView)
    {
      paramTextView.setSingleLine();
    }
  }
  
  static class PagerTitleStripImplIcs
    implements PagerTitleStrip.PagerTitleStripImpl
  {
    public void setSingleLineAllCaps(TextView paramTextView)
    {
      PagerTitleStripIcs.setSingleLineAllCaps(paramTextView);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/view/PagerTitleStrip.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */