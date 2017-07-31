package com.oneplus.gallery2;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Log;

public final class GalleryLib
{
  private static final boolean IS_CLIENT;
  
  static
  {
    Object localObject1 = BaseApplication.current();
    PackageManager localPackageManager = ((BaseApplication)localObject1).getPackageManager();
    String str = GalleryLib.class.getSimpleName();
    try
    {
      localObject1 = localPackageManager.getApplicationInfo(((BaseApplication)localObject1).getPackageName(), 128).metaData;
      if (localObject1 == null)
      {
        bool = true;
        IS_CLIENT = bool;
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        boolean bool;
        Log.e(str, "Fail to get application info", localThrowable);
        Object localObject2 = null;
        continue;
        if (((Bundle)localObject2).getBoolean("is_gallery_library_server", false)) {
          bool = false;
        }
      }
    }
  }
  
  public static boolean isClient()
  {
    return IS_CLIENT;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/GalleryLib.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */