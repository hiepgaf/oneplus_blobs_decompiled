package android.support.v4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;

class DrawerLayoutCompatApi21
{
  private static final int[] THEME_ATTRS = { 16843828 };
  
  public static void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt)
  {
    paramObject = (WindowInsets)paramObject;
    if (paramInt != 3) {
      if (paramInt == 5) {
        break label69;
      }
    }
    for (;;)
    {
      paramMarginLayoutParams.leftMargin = ((WindowInsets)paramObject).getSystemWindowInsetLeft();
      paramMarginLayoutParams.topMargin = ((WindowInsets)paramObject).getSystemWindowInsetTop();
      paramMarginLayoutParams.rightMargin = ((WindowInsets)paramObject).getSystemWindowInsetRight();
      paramMarginLayoutParams.bottomMargin = ((WindowInsets)paramObject).getSystemWindowInsetBottom();
      return;
      paramObject = ((WindowInsets)paramObject).replaceSystemWindowInsets(((WindowInsets)paramObject).getSystemWindowInsetLeft(), ((WindowInsets)paramObject).getSystemWindowInsetTop(), 0, ((WindowInsets)paramObject).getSystemWindowInsetBottom());
      continue;
      label69:
      paramObject = ((WindowInsets)paramObject).replaceSystemWindowInsets(0, ((WindowInsets)paramObject).getSystemWindowInsetTop(), ((WindowInsets)paramObject).getSystemWindowInsetRight(), ((WindowInsets)paramObject).getSystemWindowInsetBottom());
    }
  }
  
  public static void configureApplyInsets(View paramView)
  {
    if (!(paramView instanceof DrawerLayoutImpl)) {
      return;
    }
    paramView.setOnApplyWindowInsetsListener(new InsetsListener());
    paramView.setSystemUiVisibility(1280);
  }
  
  public static void dispatchChildInsets(View paramView, Object paramObject, int paramInt)
  {
    paramObject = (WindowInsets)paramObject;
    if (paramInt != 3) {
      if (paramInt == 5) {
        break label43;
      }
    }
    for (;;)
    {
      paramView.dispatchApplyWindowInsets((WindowInsets)paramObject);
      return;
      paramObject = ((WindowInsets)paramObject).replaceSystemWindowInsets(((WindowInsets)paramObject).getSystemWindowInsetLeft(), ((WindowInsets)paramObject).getSystemWindowInsetTop(), 0, ((WindowInsets)paramObject).getSystemWindowInsetBottom());
      continue;
      label43:
      paramObject = ((WindowInsets)paramObject).replaceSystemWindowInsets(0, ((WindowInsets)paramObject).getSystemWindowInsetTop(), ((WindowInsets)paramObject).getSystemWindowInsetRight(), ((WindowInsets)paramObject).getSystemWindowInsetBottom());
    }
  }
  
  public static Drawable getDefaultStatusBarBackground(Context paramContext)
  {
    paramContext = paramContext.obtainStyledAttributes(THEME_ATTRS);
    try
    {
      Drawable localDrawable = paramContext.getDrawable(0);
      return localDrawable;
    }
    finally
    {
      paramContext.recycle();
    }
  }
  
  public static int getTopInset(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    return ((WindowInsets)paramObject).getSystemWindowInsetTop();
  }
  
  static class InsetsListener
    implements View.OnApplyWindowInsetsListener
  {
    public WindowInsets onApplyWindowInsets(View paramView, WindowInsets paramWindowInsets)
    {
      boolean bool = false;
      paramView = (DrawerLayoutImpl)paramView;
      if (paramWindowInsets.getSystemWindowInsetTop() <= 0) {}
      for (;;)
      {
        paramView.setChildInsets(paramWindowInsets, bool);
        return paramWindowInsets.consumeSystemWindowInsets();
        bool = true;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/widget/DrawerLayoutCompatApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */