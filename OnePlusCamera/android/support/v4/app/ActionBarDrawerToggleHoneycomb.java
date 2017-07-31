package android.support.v4.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.lang.reflect.Method;

class ActionBarDrawerToggleHoneycomb
{
  private static final String TAG = "ActionBarDrawerToggleHoneycomb";
  private static final int[] THEME_ATTRS = { 16843531 };
  
  public static Drawable getThemeUpIndicator(Activity paramActivity)
  {
    paramActivity = paramActivity.obtainStyledAttributes(THEME_ATTRS);
    Drawable localDrawable = paramActivity.getDrawable(0);
    paramActivity.recycle();
    return localDrawable;
  }
  
  public static Object setActionBarDescription(Object paramObject, Activity paramActivity, int paramInt)
  {
    SetIndicatorInfo localSetIndicatorInfo;
    if (paramObject != null)
    {
      localSetIndicatorInfo = (SetIndicatorInfo)paramObject;
      if (localSetIndicatorInfo.setHomeAsUpIndicator != null) {
        break label30;
      }
    }
    for (;;)
    {
      return paramObject;
      paramObject = new SetIndicatorInfo(paramActivity);
      break;
      try
      {
        label30:
        paramActivity = paramActivity.getActionBar();
        localSetIndicatorInfo.setHomeActionContentDescription.invoke(paramActivity, new Object[] { Integer.valueOf(paramInt) });
        if (Build.VERSION.SDK_INT <= 19)
        {
          paramActivity.setSubtitle(paramActivity.getSubtitle());
          return paramObject;
        }
      }
      catch (Exception paramActivity)
      {
        Log.w("ActionBarDrawerToggleHoneycomb", "Couldn't set content description via JB-MR2 API", paramActivity);
      }
    }
    return paramObject;
  }
  
  public static Object setActionBarUpIndicator(Object paramObject, Activity paramActivity, Drawable paramDrawable, int paramInt)
  {
    if (paramObject != null) {}
    SetIndicatorInfo localSetIndicatorInfo;
    for (;;)
    {
      localSetIndicatorInfo = (SetIndicatorInfo)paramObject;
      if (localSetIndicatorInfo.setHomeAsUpIndicator != null) {
        break;
      }
      if (localSetIndicatorInfo.upIndicatorView != null) {
        break label106;
      }
      Log.w("ActionBarDrawerToggleHoneycomb", "Couldn't set home-as-up indicator");
      return paramObject;
      paramObject = new SetIndicatorInfo(paramActivity);
    }
    try
    {
      paramActivity = paramActivity.getActionBar();
      localSetIndicatorInfo.setHomeAsUpIndicator.invoke(paramActivity, new Object[] { paramDrawable });
      localSetIndicatorInfo.setHomeActionContentDescription.invoke(paramActivity, new Object[] { Integer.valueOf(paramInt) });
      return paramObject;
    }
    catch (Exception paramActivity)
    {
      Log.w("ActionBarDrawerToggleHoneycomb", "Couldn't set home-as-up indicator via JB-MR2 API", paramActivity);
      return paramObject;
    }
    label106:
    localSetIndicatorInfo.upIndicatorView.setImageDrawable(paramDrawable);
    return paramObject;
  }
  
  private static class SetIndicatorInfo
  {
    public Method setHomeActionContentDescription;
    public Method setHomeAsUpIndicator;
    public ImageView upIndicatorView;
    
    SetIndicatorInfo(Activity paramActivity)
    {
      try
      {
        this.setHomeAsUpIndicator = ActionBar.class.getDeclaredMethod("setHomeAsUpIndicator", new Class[] { Drawable.class });
        this.setHomeActionContentDescription = ActionBar.class.getDeclaredMethod("setHomeActionContentDescription", new Class[] { Integer.TYPE });
        return;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        paramActivity = paramActivity.findViewById(16908332);
        if (paramActivity != null)
        {
          paramActivity = (ViewGroup)paramActivity.getParent();
          if (paramActivity.getChildCount() == 2)
          {
            View localView = paramActivity.getChildAt(0);
            paramActivity = paramActivity.getChildAt(1);
            if (localView.getId() != 16908332) {
              paramActivity = localView;
            }
            if ((paramActivity instanceof ImageView)) {
              break label107;
            }
          }
        }
        else
        {
          return;
        }
      }
      return;
      label107:
      this.upIndicatorView = ((ImageView)paramActivity);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/ActionBarDrawerToggleHoneycomb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */