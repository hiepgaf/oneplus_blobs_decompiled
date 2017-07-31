package android.renderscript;

import java.io.File;

public class RenderScriptCacheDir
{
  static File mCacheDir;
  
  public static void setupDiskCache(File paramFile)
  {
    mCacheDir = paramFile;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/RenderScriptCacheDir.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */