package android.app;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.R.styleable;

@Deprecated
public class FragmentBreadCrumbs
  extends ViewGroup
  implements FragmentManager.OnBackStackChangedListener
{
  private static final int DEFAULT_GRAVITY = 8388627;
  Activity mActivity;
  LinearLayout mContainer;
  private int mGravity;
  LayoutInflater mInflater;
  private int mLayoutResId;
  int mMaxVisible = -1;
  private OnBreadCrumbClickListener mOnBreadCrumbClickListener;
  private View.OnClickListener mOnClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Object localObject = null;
      FragmentManager.BackStackEntry localBackStackEntry;
      if ((paramAnonymousView.getTag() instanceof FragmentManager.BackStackEntry))
      {
        localBackStackEntry = (FragmentManager.BackStackEntry)paramAnonymousView.getTag();
        if (localBackStackEntry != FragmentBreadCrumbs.this.mParentEntry) {
          break label55;
        }
        if (FragmentBreadCrumbs.-get1(FragmentBreadCrumbs.this) != null) {
          FragmentBreadCrumbs.-get1(FragmentBreadCrumbs.this).onClick(paramAnonymousView);
        }
      }
      return;
      label55:
      if (FragmentBreadCrumbs.-get0(FragmentBreadCrumbs.this) != null)
      {
        FragmentBreadCrumbs.OnBreadCrumbClickListener localOnBreadCrumbClickListener = FragmentBreadCrumbs.-get0(FragmentBreadCrumbs.this);
        if (localBackStackEntry == FragmentBreadCrumbs.this.mTopEntry) {}
        for (paramAnonymousView = (View)localObject; localOnBreadCrumbClickListener.onBreadCrumbClick(paramAnonymousView, 0); paramAnonymousView = localBackStackEntry) {
          return;
        }
      }
      if (localBackStackEntry == FragmentBreadCrumbs.this.mTopEntry)
      {
        FragmentBreadCrumbs.this.mActivity.getFragmentManager().popBackStack();
        return;
      }
      FragmentBreadCrumbs.this.mActivity.getFragmentManager().popBackStack(localBackStackEntry.getId(), 0);
    }
  };
  private View.OnClickListener mParentClickListener;
  BackStackRecord mParentEntry;
  private int mTextColor;
  BackStackRecord mTopEntry;
  
  public FragmentBreadCrumbs(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public FragmentBreadCrumbs(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 18219036);
  }
  
  public FragmentBreadCrumbs(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public FragmentBreadCrumbs(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.FragmentBreadCrumbs, paramInt1, paramInt2);
    this.mGravity = paramContext.getInt(0, 8388627);
    this.mLayoutResId = paramContext.getResourceId(1, 17367139);
    this.mTextColor = paramContext.getColor(2, 0);
    paramContext.recycle();
  }
  
  private BackStackRecord createBackStackEntry(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (paramCharSequence1 == null) {
      return null;
    }
    BackStackRecord localBackStackRecord = new BackStackRecord((FragmentManagerImpl)this.mActivity.getFragmentManager());
    localBackStackRecord.setBreadCrumbTitle(paramCharSequence1);
    localBackStackRecord.setBreadCrumbShortTitle(paramCharSequence2);
    return localBackStackRecord;
  }
  
  private FragmentManager.BackStackEntry getPreEntry(int paramInt)
  {
    if (this.mParentEntry != null)
    {
      if (paramInt == 0) {
        return this.mParentEntry;
      }
      return this.mTopEntry;
    }
    return this.mTopEntry;
  }
  
  private int getPreEntryCount()
  {
    int j = 1;
    int i;
    if (this.mTopEntry != null)
    {
      i = 1;
      if (this.mParentEntry == null) {
        break label27;
      }
    }
    for (;;)
    {
      return i + j;
      i = 0;
      break;
      label27:
      j = 0;
    }
  }
  
  public void onBackStackChanged()
  {
    updateCrumbs();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (getChildCount() == 0) {
      return;
    }
    View localView = getChildAt(0);
    paramInt4 = this.mPaddingTop;
    int i = this.mPaddingTop;
    int j = localView.getMeasuredHeight();
    int k = this.mPaddingBottom;
    paramInt1 = getLayoutDirection();
    switch (Gravity.getAbsoluteGravity(this.mGravity & 0x800007, paramInt1))
    {
    default: 
      paramInt2 = this.mPaddingLeft;
      paramInt1 = paramInt2 + localView.getMeasuredWidth();
    }
    for (;;)
    {
      paramInt3 = paramInt2;
      if (paramInt2 < this.mPaddingLeft) {
        paramInt3 = this.mPaddingLeft;
      }
      paramInt2 = paramInt1;
      if (paramInt1 > this.mRight - this.mLeft - this.mPaddingRight) {
        paramInt2 = this.mRight - this.mLeft - this.mPaddingRight;
      }
      localView.layout(paramInt3, paramInt4, paramInt2, i + j - k);
      return;
      paramInt1 = this.mRight - this.mLeft - this.mPaddingRight;
      paramInt2 = paramInt1 - localView.getMeasuredWidth();
      continue;
      paramInt2 = this.mPaddingLeft + (this.mRight - this.mLeft - localView.getMeasuredWidth()) / 2;
      paramInt1 = paramInt2 + localView.getMeasuredWidth();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i3 = getChildCount();
    int k = 0;
    int j = 0;
    int i = 0;
    int m = 0;
    while (m < i3)
    {
      View localView = getChildAt(m);
      int i2 = k;
      int i1 = j;
      n = i;
      if (localView.getVisibility() != 8)
      {
        measureChild(localView, paramInt1, paramInt2);
        i1 = Math.max(j, localView.getMeasuredWidth());
        i2 = Math.max(k, localView.getMeasuredHeight());
        n = combineMeasuredStates(i, localView.getMeasuredState());
      }
      m += 1;
      k = i2;
      j = i1;
      i = n;
    }
    m = this.mPaddingLeft;
    int n = this.mPaddingRight;
    k = Math.max(k + (this.mPaddingTop + this.mPaddingBottom), getSuggestedMinimumHeight());
    setMeasuredDimension(resolveSizeAndState(Math.max(j + (m + n), getSuggestedMinimumWidth()), paramInt1, i), resolveSizeAndState(k, paramInt2, i << 16));
  }
  
  public void setActivity(Activity paramActivity)
  {
    this.mActivity = paramActivity;
    this.mInflater = ((LayoutInflater)paramActivity.getSystemService("layout_inflater"));
    this.mContainer = ((LinearLayout)this.mInflater.inflate(17367141, this, false));
    addView(this.mContainer);
    paramActivity.getFragmentManager().addOnBackStackChangedListener(this);
    updateCrumbs();
    setLayoutTransition(new LayoutTransition());
  }
  
  public void setMaxVisible(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("visibleCrumbs must be greater than zero");
    }
    this.mMaxVisible = paramInt;
  }
  
  public void setOnBreadCrumbClickListener(OnBreadCrumbClickListener paramOnBreadCrumbClickListener)
  {
    this.mOnBreadCrumbClickListener = paramOnBreadCrumbClickListener;
  }
  
  public void setParentTitle(CharSequence paramCharSequence1, CharSequence paramCharSequence2, View.OnClickListener paramOnClickListener)
  {
    this.mParentEntry = createBackStackEntry(paramCharSequence1, paramCharSequence2);
    this.mParentClickListener = paramOnClickListener;
    updateCrumbs();
  }
  
  public void setTitle(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    this.mTopEntry = createBackStackEntry(paramCharSequence1, paramCharSequence2);
    updateCrumbs();
  }
  
  void updateCrumbs()
  {
    Object localObject2 = this.mActivity.getFragmentManager();
    int m = ((FragmentManager)localObject2).getBackStackEntryCount();
    int n = getPreEntryCount();
    int j = this.mContainer.getChildCount();
    int i = 0;
    Object localObject1;
    int k;
    while (i < m + n)
    {
      if (i < n) {}
      for (localObject1 = getPreEntry(i);; localObject1 = ((FragmentManager)localObject2).getBackStackEntryAt(i - n))
      {
        k = j;
        if (i >= j) {
          break label117;
        }
        k = j;
        if (this.mContainer.getChildAt(i).getTag() == localObject1) {
          break label117;
        }
        k = i;
        while (k < j)
        {
          this.mContainer.removeViewAt(i);
          k += 1;
        }
      }
      k = i;
      label117:
      if (i >= k)
      {
        View localView = this.mInflater.inflate(this.mLayoutResId, this, false);
        TextView localTextView = (TextView)localView.findViewById(16908310);
        localTextView.setText(((FragmentManager.BackStackEntry)localObject1).getBreadCrumbTitle());
        localTextView.setTag(localObject1);
        localTextView.setTextColor(this.mTextColor);
        if (i == 0) {
          localView.findViewById(16908354).setVisibility(8);
        }
        this.mContainer.addView(localView);
        localTextView.setOnClickListener(this.mOnClickListener);
      }
      i += 1;
      j = k;
    }
    i = this.mContainer.getChildCount();
    while (i > m + n)
    {
      this.mContainer.removeViewAt(i - 1);
      i -= 1;
    }
    j = 0;
    if (j < i)
    {
      localObject1 = this.mContainer.getChildAt(j);
      localObject2 = ((View)localObject1).findViewById(16908310);
      boolean bool;
      if (j < i - 1)
      {
        bool = true;
        label293:
        ((View)localObject2).setEnabled(bool);
        if (this.mMaxVisible > 0)
        {
          if (j >= i - this.mMaxVisible) {
            break label371;
          }
          k = 8;
          label320:
          ((View)localObject1).setVisibility(k);
          localObject1 = ((View)localObject1).findViewById(16908354);
          if ((j <= i - this.mMaxVisible) || (j == 0)) {
            break label376;
          }
        }
      }
      label371:
      label376:
      for (k = 0;; k = 8)
      {
        ((View)localObject1).setVisibility(k);
        j += 1;
        break;
        bool = false;
        break label293;
        k = 0;
        break label320;
      }
    }
  }
  
  public static abstract interface OnBreadCrumbClickListener
  {
    public abstract boolean onBreadCrumbClick(FragmentManager.BackStackEntry paramBackStackEntry, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/FragmentBreadCrumbs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */