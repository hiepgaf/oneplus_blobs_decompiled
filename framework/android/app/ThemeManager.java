package android.app;

import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.ResourcesImpl;
import android.content.res.ResourcesKey;
import android.os.BaseBundle;
import android.util.Log;
import java.io.File;

public class ThemeManager
{
  private static final String METADATA_HAS_COLOR_MODE = "use_common_accent_color";
  private static final String THEME_TAG = "Theme";
  private static ThemeManager sThemeManager = null;
  private String mColorFrameworkResName = "color.com.hydrogen.apk";
  private String mFrameworkResName = "com.hydrogen.apk";
  private String mOverlayColorFilePath = "/data/theme/color";
  private String mOverlayFilePath = "/data/theme";
  
  private boolean checkColorMode(String paramString)
  {
    Object localObject2 = ActivityThread.currentActivityThread().getSystemContext().getPackageManager();
    Object localObject1 = null;
    boolean bool = false;
    try
    {
      localObject2 = ((PackageManager)localObject2).getApplicationInfo(paramString, 128);
      localObject1 = localObject2;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Log.e("Theme", localNameNotFoundException.getMessage());
        continue;
        Log.e("Theme", "info is null");
      }
    }
    if (localObject1 != null)
    {
      localObject1 = ((PackageItemInfo)localObject1).metaData;
      if (localObject1 != null) {
        bool = ((BaseBundle)localObject1).getBoolean("use_common_accent_color");
      }
      Log.v("Theme", "ColorMode:" + paramString + " is " + bool);
      return bool;
    }
  }
  
  public static ThemeManager getInstance()
  {
    if (sThemeManager == null) {
      sThemeManager = new ThemeManager();
    }
    return sThemeManager;
  }
  
  public Resources changeTheme(String paramString, Resources paramResources, ResourcesKey paramResourcesKey)
  {
    String str1 = ActivityThread.currentPackageName();
    String str2 = paramString + ".apk";
    String str3 = "color." + paramString + ".apk";
    str2 = this.mOverlayFilePath + "/" + str2;
    str3 = this.mOverlayColorFilePath + "/" + str3;
    new StringBuilder().append(this.mOverlayFilePath).append("/").append(this.mFrameworkResName).toString();
    if ((str1 == null) || (str1.equals(paramString)))
    {
      str1 = ActivityThread.currentResDir();
      if ((str1 == null) || (str1.equals(paramResourcesKey.mResDir)))
      {
        boolean bool = new File(str2).exists();
        if (bool)
        {
          paramResources.getAssets().addOverlayPath(str2);
          Log.v("Theme", paramString + ", Add app's resource");
        }
        if ((bool) && (checkColorMode(paramString)) && (new File(str3).exists()))
        {
          Log.v("Theme", paramString + ", Add app's color resource");
          paramResources.getAssets().addOverlayPath(str3);
        }
        return paramResources;
      }
    }
    else
    {
      return paramResources;
    }
    return paramResources;
  }
  
  public ResourcesImpl changeTheme(ResourcesImpl paramResourcesImpl, ResourcesKey paramResourcesKey)
  {
    String str1 = ActivityThread.currentPackageName();
    String str2 = str1 + ".apk";
    String str3 = "color." + str1 + ".apk";
    str2 = this.mOverlayFilePath + "/" + str2;
    str3 = this.mOverlayColorFilePath + "/" + str3;
    new StringBuilder().append(this.mOverlayFilePath).append("/").append(this.mFrameworkResName).toString();
    boolean bool = new File(str2).exists();
    String str4 = ActivityThread.currentResDir();
    if ((str4 == null) || (str4.equals(paramResourcesKey.mResDir)))
    {
      if (bool)
      {
        paramResourcesImpl.getAssets().addOverlayPath(str2);
        Log.v("Theme", str1 + ", Add app's resource");
      }
      if ((bool) && (checkColorMode(str1)) && (new File(str3).exists()))
      {
        Log.v("Theme", str1 + ", Add app's color resource");
        paramResourcesImpl.getAssets().addOverlayPath(str3);
      }
      return paramResourcesImpl;
    }
    return paramResourcesImpl;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ThemeManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */