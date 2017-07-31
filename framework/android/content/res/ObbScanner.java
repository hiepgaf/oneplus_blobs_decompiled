package android.content.res;

import java.io.File;
import java.io.IOException;

public class ObbScanner
{
  public static ObbInfo getObbInfo(String paramString)
    throws IOException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("file path cannot be null");
    }
    Object localObject = new File(paramString);
    if (!((File)localObject).exists()) {
      throw new IllegalArgumentException("OBB file does not exist: " + paramString);
    }
    paramString = ((File)localObject).getCanonicalPath();
    localObject = new ObbInfo();
    ((ObbInfo)localObject).filename = paramString;
    getObbInfo_native(paramString, (ObbInfo)localObject);
    return (ObbInfo)localObject;
  }
  
  private static native void getObbInfo_native(String paramString, ObbInfo paramObbInfo)
    throws IOException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ObbScanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */