package com.android.server.firewall;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.os.Process;
import android.os.RemoteException;
import android.util.Slog;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class SenderFilter
{
  private static final String ATTR_TYPE = "type";
  public static final FilterFactory FACTORY = new FilterFactory("sender")
  {
    public Filter newFilter(XmlPullParser paramAnonymousXmlPullParser)
      throws IOException, XmlPullParserException
    {
      String str = paramAnonymousXmlPullParser.getAttributeValue(null, "type");
      if (str == null) {
        throw new XmlPullParserException("type attribute must be specified for <sender>", paramAnonymousXmlPullParser, null);
      }
      if (str.equals("system")) {
        return SenderFilter.-get1();
      }
      if (str.equals("signature")) {
        return SenderFilter.-get0();
      }
      if (str.equals("system|signature")) {
        return SenderFilter.-get2();
      }
      if (str.equals("userId")) {
        return SenderFilter.-get3();
      }
      throw new XmlPullParserException("Invalid type attribute for <sender>: " + str, paramAnonymousXmlPullParser, null);
    }
  };
  private static final Filter SIGNATURE = new Filter()
  {
    public boolean matches(IntentFirewall paramAnonymousIntentFirewall, ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString, int paramAnonymousInt3)
    {
      return paramAnonymousIntentFirewall.signaturesMatch(paramAnonymousInt1, paramAnonymousInt3);
    }
  };
  private static final Filter SYSTEM = new Filter()
  {
    public boolean matches(IntentFirewall paramAnonymousIntentFirewall, ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString, int paramAnonymousInt3)
    {
      return SenderFilter.isPrivilegedApp(paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  private static final Filter SYSTEM_OR_SIGNATURE = new Filter()
  {
    public boolean matches(IntentFirewall paramAnonymousIntentFirewall, ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString, int paramAnonymousInt3)
    {
      if (!SenderFilter.isPrivilegedApp(paramAnonymousInt1, paramAnonymousInt2)) {
        return paramAnonymousIntentFirewall.signaturesMatch(paramAnonymousInt1, paramAnonymousInt3);
      }
      return true;
    }
  };
  private static final Filter USER_ID = new Filter()
  {
    public boolean matches(IntentFirewall paramAnonymousIntentFirewall, ComponentName paramAnonymousComponentName, Intent paramAnonymousIntent, int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString, int paramAnonymousInt3)
    {
      return paramAnonymousIntentFirewall.checkComponentPermission(null, paramAnonymousInt2, paramAnonymousInt1, paramAnonymousInt3, false);
    }
  };
  private static final String VAL_SIGNATURE = "signature";
  private static final String VAL_SYSTEM = "system";
  private static final String VAL_SYSTEM_OR_SIGNATURE = "system|signature";
  private static final String VAL_USER_ID = "userId";
  
  static boolean isPrivilegedApp(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 1000) || (paramInt1 == 0)) {}
    while ((paramInt2 == Process.myPid()) || (paramInt2 == 0)) {
      return true;
    }
    IPackageManager localIPackageManager = AppGlobals.getPackageManager();
    try
    {
      paramInt1 = localIPackageManager.getPrivateFlagsForUid(paramInt1);
      return (paramInt1 & 0x8) != 0;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("IntentFirewall", "Remote exception while retrieving uid flags", localRemoteException);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/SenderFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */