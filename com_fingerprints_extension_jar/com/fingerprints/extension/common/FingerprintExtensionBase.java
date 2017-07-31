package com.fingerprints.extension.common;

import android.os.IBinder;
import com.fingerprints.extension.util.Logger;
import java.lang.reflect.Method;

public class FingerprintExtensionBase
{
  private Logger mLogger = new Logger(getClass().getSimpleName());
  
  public IBinder getFingerprintExtension(String paramString)
  {
    this.mLogger.enter("getFingerprintExtension");
    try
    {
      paramString = (IBinder)Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", new Class[] { String.class }).invoke(null, new Object[] { paramString });
      return paramString;
    }
    catch (Exception paramString)
    {
      this.mLogger.w("Exception: " + paramString);
      this.mLogger.exit("getFingerprintExtension");
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/common/FingerprintExtensionBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */