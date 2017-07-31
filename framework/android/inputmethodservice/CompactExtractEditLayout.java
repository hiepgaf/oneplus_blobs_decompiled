package android.inputmethodservice;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;

public class CompactExtractEditLayout
  extends LinearLayout
{
  private View mInputExtractAccessories;
  private View mInputExtractAction;
  private View mInputExtractEditText;
  private boolean mPerformLayoutChanges;
  
  public CompactExtractEditLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public CompactExtractEditLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public CompactExtractEditLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private int applyFractionInt(int paramInt1, int paramInt2)
  {
    return Math.round(getResources().getFraction(paramInt1, paramInt2, paramInt2));
  }
  
  private void applyProportionalLayout(int paramInt1, int paramInt2)
  {
    if (getResources().getConfiguration().isScreenRound()) {
      setGravity(80);
    }
    setLayoutHeight(this, applyFractionInt(18022406, paramInt2));
    setPadding(applyFractionInt(18022407, paramInt1), 0, applyFractionInt(18022409, paramInt1), 0);
    setLayoutMarginBottom(this.mInputExtractEditText, applyFractionInt(18022410, paramInt2));
    setLayoutMarginBottom(this.mInputExtractAccessories, applyFractionInt(18022411, paramInt2));
  }
  
  private static void setLayoutHeight(View paramView, int paramInt)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    localLayoutParams.height = paramInt;
    paramView.setLayoutParams(localLayoutParams);
  }
  
  private static void setLayoutMarginBottom(View paramView, int paramInt)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    localMarginLayoutParams.bottomMargin = paramInt;
    paramView.setLayoutParams(localMarginLayoutParams);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mPerformLayoutChanges)
    {
      DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
      int i = localDisplayMetrics.heightPixels;
      applyProportionalLayout(localDisplayMetrics.widthPixels, i);
    }
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mInputExtractEditText = findViewById(16908325);
    this.mInputExtractAccessories = findViewById(16909193);
    this.mInputExtractAction = findViewById(16909194);
    if ((this.mInputExtractEditText != null) && (this.mInputExtractAccessories != null) && (this.mInputExtractAction != null)) {
      this.mPerformLayoutChanges = true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/CompactExtractEditLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */