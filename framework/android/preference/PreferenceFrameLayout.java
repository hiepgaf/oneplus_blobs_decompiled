package android.preference;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.internal.R.styleable;

public class PreferenceFrameLayout
  extends FrameLayout
{
  private static final int DEFAULT_BORDER_BOTTOM = 0;
  private static final int DEFAULT_BORDER_LEFT = 0;
  private static final int DEFAULT_BORDER_RIGHT = 0;
  private static final int DEFAULT_BORDER_TOP = 0;
  private final int mBorderBottom;
  private final int mBorderLeft;
  private final int mBorderRight;
  private final int mBorderTop;
  private boolean mPaddingApplied;
  
  public PreferenceFrameLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PreferenceFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 18219054);
  }
  
  public PreferenceFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PreferenceFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PreferenceFrameLayout, paramInt1, paramInt2);
    float f = paramContext.getResources().getDisplayMetrics().density;
    paramInt1 = (int)(f * 0.0F + 0.5F);
    paramInt2 = (int)(f * 0.0F + 0.5F);
    int i = (int)(f * 0.0F + 0.5F);
    int j = (int)(f * 0.0F + 0.5F);
    this.mBorderTop = paramAttributeSet.getDimensionPixelSize(0, paramInt1);
    this.mBorderBottom = paramAttributeSet.getDimensionPixelSize(1, paramInt2);
    this.mBorderLeft = paramAttributeSet.getDimensionPixelSize(2, i);
    this.mBorderRight = paramAttributeSet.getDimensionPixelSize(3, j);
    paramAttributeSet.recycle();
  }
  
  public void addView(View paramView)
  {
    LayoutParams localLayoutParams = null;
    int i3 = getPaddingTop();
    int i2 = getPaddingBottom();
    int i1 = getPaddingLeft();
    int n = getPaddingRight();
    if ((paramView.getLayoutParams() instanceof LayoutParams)) {
      localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    }
    int i;
    int j;
    int k;
    int m;
    if ((localLayoutParams != null) && (localLayoutParams.removeBorders))
    {
      i = i2;
      j = i1;
      k = n;
      m = i3;
      if (this.mPaddingApplied)
      {
        m = i3 - this.mBorderTop;
        i = i2 - this.mBorderBottom;
        j = i1 - this.mBorderLeft;
        k = n - this.mBorderRight;
        this.mPaddingApplied = false;
      }
      n = getPaddingTop();
      i1 = getPaddingBottom();
      i2 = getPaddingLeft();
      i3 = getPaddingRight();
      if ((n == m) && (i1 == i)) {
        break label235;
      }
    }
    for (;;)
    {
      label156:
      setPadding(j, m, k, i);
      label235:
      do
      {
        super.addView(paramView);
        return;
        i = i2;
        j = i1;
        k = n;
        m = i3;
        if (this.mPaddingApplied) {
          break;
        }
        m = i3 + this.mBorderTop;
        i = i2 + this.mBorderBottom;
        j = i1 + this.mBorderLeft;
        k = n + this.mBorderRight;
        this.mPaddingApplied = true;
        break;
        if (i2 != j) {
          break label156;
        }
      } while (i3 == k);
    }
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  public static class LayoutParams
    extends FrameLayout.LayoutParams
  {
    public boolean removeBorders = false;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PreferenceFrameLayout_Layout);
      this.removeBorders = paramContext.getBoolean(0, false);
      paramContext.recycle();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/PreferenceFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */