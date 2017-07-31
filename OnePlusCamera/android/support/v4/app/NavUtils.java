package android.support.v4.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.util.Log;

public class NavUtils
{
  private static final NavUtilsImpl IMPL = new NavUtilsImplJB();
  public static final String PARENT_ACTIVITY = "android.support.PARENT_ACTIVITY";
  private static final String TAG = "NavUtils";
  
  static
  {
    if (Build.VERSION.SDK_INT < 16)
    {
      IMPL = new NavUtilsImplBase();
      return;
    }
  }
  
  public static Intent getParentActivityIntent(Activity paramActivity)
  {
    return IMPL.getParentActivityIntent(paramActivity);
  }
  
  public static Intent getParentActivityIntent(Context paramContext, ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    String str = getParentActivityName(paramContext, paramComponentName);
    if (str != null)
    {
      paramComponentName = new ComponentName(paramComponentName.getPackageName(), str);
      if (getParentActivityName(paramContext, paramComponentName) != null) {
        return new Intent().setComponent(paramComponentName);
      }
    }
    else
    {
      return null;
    }
    return IntentCompat.makeMainActivity(paramComponentName);
  }
  
  public static Intent getParentActivityIntent(Context paramContext, Class<?> paramClass)
    throws PackageManager.NameNotFoundException
  {
    paramClass = getParentActivityName(paramContext, new ComponentName(paramContext, paramClass));
    if (paramClass != null)
    {
      paramClass = new ComponentName(paramContext, paramClass);
      if (getParentActivityName(paramContext, paramClass) != null) {
        return new Intent().setComponent(paramClass);
      }
    }
    else
    {
      return null;
    }
    return IntentCompat.makeMainActivity(paramClass);
  }
  
  @Nullable
  public static String getParentActivityName(Activity paramActivity)
  {
    try
    {
      paramActivity = getParentActivityName(paramActivity, paramActivity.getComponentName());
      return paramActivity;
    }
    catch (PackageManager.NameNotFoundException paramActivity)
    {
      throw new IllegalArgumentException(paramActivity);
    }
  }
  
  @Nullable
  public static String getParentActivityName(Context paramContext, ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    paramComponentName = paramContext.getPackageManager().getActivityInfo(paramComponentName, 128);
    return IMPL.getParentActivityName(paramContext, paramComponentName);
  }
  
  public static void navigateUpFromSameTask(Activity paramActivity)
  {
    Intent localIntent = getParentActivityIntent(paramActivity);
    if (localIntent != null)
    {
      navigateUpTo(paramActivity, localIntent);
      return;
    }
    throw new IllegalArgumentException("Activity " + paramActivity.getClass().getSimpleName() + " does not have a parent activity name specified." + " (Did you forget to add the android.support.PARENT_ACTIVITY <meta-data> " + " element in your manifest?)");
  }
  
  public static void navigateUpTo(Activity paramActivity, Intent paramIntent)
  {
    IMPL.navigateUpTo(paramActivity, paramIntent);
  }
  
  public static boolean shouldUpRecreateTask(Activity paramActivity, Intent paramIntent)
  {
    return IMPL.shouldUpRecreateTask(paramActivity, paramIntent);
  }
  
  static abstract interface NavUtilsImpl
  {
    public abstract Intent getParentActivityIntent(Activity paramActivity);
    
    public abstract String getParentActivityName(Context paramContext, ActivityInfo paramActivityInfo);
    
    public abstract void navigateUpTo(Activity paramActivity, Intent paramIntent);
    
    public abstract boolean shouldUpRecreateTask(Activity paramActivity, Intent paramIntent);
  }
  
  static class NavUtilsImplBase
    implements NavUtils.NavUtilsImpl
  {
    public Intent getParentActivityIntent(Activity paramActivity)
    {
      String str = NavUtils.getParentActivityName(paramActivity);
      if (str != null)
      {
        ComponentName localComponentName = new ComponentName(paramActivity, str);
        try
        {
          if (NavUtils.getParentActivityName(paramActivity, localComponentName) != null) {
            return new Intent().setComponent(localComponentName);
          }
          paramActivity = IntentCompat.makeMainActivity(localComponentName);
          return paramActivity;
        }
        catch (PackageManager.NameNotFoundException paramActivity)
        {
          Log.e("NavUtils", "getParentActivityIntent: bad parentActivityName '" + str + "' in manifest");
          return null;
        }
      }
      return null;
    }
    
    public String getParentActivityName(Context paramContext, ActivityInfo paramActivityInfo)
    {
      if (paramActivityInfo.metaData != null)
      {
        paramActivityInfo = paramActivityInfo.metaData.getString("android.support.PARENT_ACTIVITY");
        if (paramActivityInfo != null)
        {
          if (paramActivityInfo.charAt(0) == '.') {
            break label37;
          }
          return paramActivityInfo;
        }
      }
      else
      {
        return null;
      }
      return null;
      label37:
      return paramContext.getPackageName() + paramActivityInfo;
    }
    
    public void navigateUpTo(Activity paramActivity, Intent paramIntent)
    {
      paramIntent.addFlags(67108864);
      paramActivity.startActivity(paramIntent);
      paramActivity.finish();
    }
    
    public boolean shouldUpRecreateTask(Activity paramActivity, Intent paramIntent)
    {
      paramActivity = paramActivity.getIntent().getAction();
      if (paramActivity == null) {}
      while (paramActivity.equals("android.intent.action.MAIN")) {
        return false;
      }
      return true;
    }
  }
  
  static class NavUtilsImplJB
    extends NavUtils.NavUtilsImplBase
  {
    public Intent getParentActivityIntent(Activity paramActivity)
    {
      Intent localIntent = NavUtilsJB.getParentActivityIntent(paramActivity);
      if (localIntent != null) {
        return localIntent;
      }
      return superGetParentActivityIntent(paramActivity);
    }
    
    public String getParentActivityName(Context paramContext, ActivityInfo paramActivityInfo)
    {
      String str = NavUtilsJB.getParentActivityName(paramActivityInfo);
      if (str != null) {
        return str;
      }
      return super.getParentActivityName(paramContext, paramActivityInfo);
    }
    
    public void navigateUpTo(Activity paramActivity, Intent paramIntent)
    {
      NavUtilsJB.navigateUpTo(paramActivity, paramIntent);
    }
    
    public boolean shouldUpRecreateTask(Activity paramActivity, Intent paramIntent)
    {
      return NavUtilsJB.shouldUpRecreateTask(paramActivity, paramIntent);
    }
    
    Intent superGetParentActivityIntent(Activity paramActivity)
    {
      return super.getParentActivityIntent(paramActivity);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/NavUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */