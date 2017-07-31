package android.support.v4.os;

import android.os.Build.VERSION;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;

public class EnvironmentCompat
{
  public static final String MEDIA_UNKNOWN = "unknown";
  private static final String TAG = "EnvironmentCompat";
  
  public static String getStorageState(File paramFile)
  {
    if (Build.VERSION.SDK_INT < 19) {}
    try
    {
      boolean bool = paramFile.getCanonicalPath().startsWith(Environment.getExternalStorageDirectory().getCanonicalPath());
      if (bool) {
        break label34;
      }
    }
    catch (IOException paramFile)
    {
      for (;;)
      {
        Log.w("EnvironmentCompat", "Failed to resolve canonical path: " + paramFile);
      }
    }
    return "unknown";
    return EnvironmentCompatKitKat.getStorageState(paramFile);
    label34:
    paramFile = Environment.getExternalStorageState();
    return paramFile;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/os/EnvironmentCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */