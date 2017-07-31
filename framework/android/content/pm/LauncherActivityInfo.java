package android.content.pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.Log;

public class LauncherActivityInfo
{
  private static final String TAG = "LauncherActivityInfo";
  private ActivityInfo mActivityInfo;
  private ComponentName mComponentName;
  private final PackageManager mPm;
  private UserHandle mUser;
  
  LauncherActivityInfo(Context paramContext)
  {
    this.mPm = paramContext.getPackageManager();
  }
  
  LauncherActivityInfo(Context paramContext, ActivityInfo paramActivityInfo, UserHandle paramUserHandle)
  {
    this(paramContext);
    this.mActivityInfo = paramActivityInfo;
    this.mComponentName = new ComponentName(paramActivityInfo.packageName, paramActivityInfo.name);
    this.mUser = paramUserHandle;
  }
  
  public int getApplicationFlags()
  {
    return this.mActivityInfo.applicationInfo.flags;
  }
  
  public ApplicationInfo getApplicationInfo()
  {
    return this.mActivityInfo.applicationInfo;
  }
  
  public Drawable getBadgedIcon(int paramInt)
  {
    Drawable localDrawable = getIcon(paramInt);
    if ((localDrawable instanceof BitmapDrawable)) {
      return this.mPm.getUserBadgedIcon(localDrawable, this.mUser);
    }
    Log.e("LauncherActivityInfo", "Unable to create badged icon for " + this.mActivityInfo);
    return localDrawable;
  }
  
  public ComponentName getComponentName()
  {
    return this.mComponentName;
  }
  
  public long getFirstInstallTime()
  {
    try
    {
      long l = this.mPm.getPackageInfo(this.mActivityInfo.packageName, 8192).firstInstallTime;
      return l;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return 0L;
  }
  
  public Drawable getIcon(int paramInt)
  {
    int i = this.mActivityInfo.getIconResource();
    Object localObject3 = null;
    Object localObject1 = localObject3;
    if (paramInt != 0)
    {
      localObject1 = localObject3;
      if (i == 0) {}
    }
    try
    {
      localObject1 = this.mPm.getResourcesForApplication(this.mActivityInfo.applicationInfo).getDrawableForDensity(i, paramInt);
      localObject3 = localObject1;
      if (localObject1 == null) {
        localObject3 = this.mActivityInfo.loadIcon(this.mPm);
      }
      return (Drawable)localObject3;
    }
    catch (PackageManager.NameNotFoundException|Resources.NotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Object localObject2 = localObject3;
      }
    }
  }
  
  public CharSequence getLabel()
  {
    return this.mActivityInfo.loadLabel(this.mPm);
  }
  
  public String getName()
  {
    return this.mActivityInfo.name;
  }
  
  public UserHandle getUser()
  {
    return this.mUser;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/LauncherActivityInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */