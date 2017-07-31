package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class SenderPermissionFilter
  implements Filter
{
  private static final String ATTR_NAME = "name";
  public static final FilterFactory FACTORY = new FilterFactory("sender-permission")
  {
    public Filter newFilter(XmlPullParser paramAnonymousXmlPullParser)
      throws IOException, XmlPullParserException
    {
      String str = paramAnonymousXmlPullParser.getAttributeValue(null, "name");
      if (str == null) {
        throw new XmlPullParserException("Permission name must be specified.", paramAnonymousXmlPullParser, null);
      }
      return new SenderPermissionFilter(str, null);
    }
  };
  private final String mPermission;
  
  private SenderPermissionFilter(String paramString)
  {
    this.mPermission = paramString;
  }
  
  public boolean matches(IntentFirewall paramIntentFirewall, ComponentName paramComponentName, Intent paramIntent, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    return paramIntentFirewall.checkComponentPermission(this.mPermission, paramInt2, paramInt1, paramInt3, true);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/SenderPermissionFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */