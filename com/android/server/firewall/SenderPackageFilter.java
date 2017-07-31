package com.android.server.firewall;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.os.RemoteException;
import android.os.UserHandle;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SenderPackageFilter
  implements Filter
{
  private static final String ATTR_NAME = "name";
  public static final FilterFactory FACTORY = new FilterFactory("sender-package")
  {
    public Filter newFilter(XmlPullParser paramAnonymousXmlPullParser)
      throws IOException, XmlPullParserException
    {
      String str = paramAnonymousXmlPullParser.getAttributeValue(null, "name");
      if (str == null) {
        throw new XmlPullParserException("A package name must be specified.", paramAnonymousXmlPullParser, null);
      }
      return new SenderPackageFilter(str);
    }
  };
  public final String mPackageName;
  
  public SenderPackageFilter(String paramString)
  {
    this.mPackageName = paramString;
  }
  
  public boolean matches(IntentFirewall paramIntentFirewall, ComponentName paramComponentName, Intent paramIntent, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    paramIntentFirewall = AppGlobals.getPackageManager();
    paramInt2 = -1;
    try
    {
      paramInt3 = paramIntentFirewall.getPackageUid(this.mPackageName, 8192, 0);
      paramInt2 = paramInt3;
    }
    catch (RemoteException paramIntentFirewall)
    {
      for (;;) {}
    }
    if (paramInt2 == -1) {
      return false;
    }
    return UserHandle.isSameApp(paramInt2, paramInt1);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/SenderPackageFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */