package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import java.io.File;

public class ContextCompat
{
  private static final String DIR_ANDROID = "Android";
  private static final String DIR_CACHE = "cache";
  private static final String DIR_DATA = "data";
  private static final String DIR_FILES = "files";
  private static final String DIR_OBB = "obb";
  private static final String TAG = "ContextCompat";
  
  private static File buildPath(File paramFile, String... paramVarArgs)
  {
    int j = paramVarArgs.length;
    int i = 0;
    if (i >= j) {
      return paramFile;
    }
    String str = paramVarArgs[i];
    if (paramFile != null) {
      if (str != null) {
        break label46;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      paramFile = new File(str);
      continue;
      label46:
      paramFile = new File(paramFile, str);
    }
  }
  
  private static File createFilesDir(File paramFile)
  {
    try
    {
      boolean bool = paramFile.exists();
      if (bool) {}
      while (paramFile.mkdirs()) {
        return paramFile;
      }
      if (!paramFile.exists())
      {
        Log.w("ContextCompat", "Unable to create files subdir " + paramFile.getPath());
        return null;
      }
      return paramFile;
    }
    finally {}
  }
  
  public static final Drawable getDrawable(Context paramContext, int paramInt)
  {
    if (Build.VERSION.SDK_INT < 21) {
      return paramContext.getResources().getDrawable(paramInt);
    }
    return ContextCompatApi21.getDrawable(paramContext, paramInt);
  }
  
  public static File[] getExternalCacheDirs(Context paramContext)
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 19) {
      if (i >= 8) {
        break label63;
      }
    }
    label63:
    for (paramContext = buildPath(Environment.getExternalStorageDirectory(), new String[] { "Android", "data", paramContext.getPackageName(), "cache" });; paramContext = ContextCompatFroyo.getExternalCacheDir(paramContext))
    {
      return new File[] { paramContext };
      return ContextCompatKitKat.getExternalCacheDirs(paramContext);
    }
  }
  
  public static File[] getExternalFilesDirs(Context paramContext, String paramString)
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 19) {
      if (i >= 8) {
        break label68;
      }
    }
    label68:
    for (paramContext = buildPath(Environment.getExternalStorageDirectory(), new String[] { "Android", "data", paramContext.getPackageName(), "files", paramString });; paramContext = ContextCompatFroyo.getExternalFilesDir(paramContext, paramString))
    {
      return new File[] { paramContext };
      return ContextCompatKitKat.getExternalFilesDirs(paramContext, paramString);
    }
  }
  
  public static File[] getObbDirs(Context paramContext)
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 19) {
      if (i >= 11) {
        break label58;
      }
    }
    label58:
    for (paramContext = buildPath(Environment.getExternalStorageDirectory(), new String[] { "Android", "obb", paramContext.getPackageName() });; paramContext = ContextCompatHoneycomb.getObbDir(paramContext))
    {
      return new File[] { paramContext };
      return ContextCompatKitKat.getObbDirs(paramContext);
    }
  }
  
  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent)
  {
    return startActivities(paramContext, paramArrayOfIntent, null);
  }
  
  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent, Bundle paramBundle)
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 16)
    {
      if (i < 11) {
        return false;
      }
    }
    else
    {
      ContextCompatJellybean.startActivities(paramContext, paramArrayOfIntent, paramBundle);
      return true;
    }
    ContextCompatHoneycomb.startActivities(paramContext, paramArrayOfIntent);
    return true;
  }
  
  public final File getCodeCacheDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT < 21) {
      return createFilesDir(new File(paramContext.getApplicationInfo().dataDir, "code_cache"));
    }
    return ContextCompatApi21.getCodeCacheDir(paramContext);
  }
  
  public final File getNoBackupFilesDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT < 21) {
      return createFilesDir(new File(paramContext.getApplicationInfo().dataDir, "no_backup"));
    }
    return ContextCompatApi21.getNoBackupFilesDir(paramContext);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/content/ContextCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */