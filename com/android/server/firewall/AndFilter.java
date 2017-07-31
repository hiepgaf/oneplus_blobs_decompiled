package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class AndFilter
  extends FilterList
{
  public static final FilterFactory FACTORY = new FilterFactory("and")
  {
    public Filter newFilter(XmlPullParser paramAnonymousXmlPullParser)
      throws IOException, XmlPullParserException
    {
      return new AndFilter().readFromXml(paramAnonymousXmlPullParser);
    }
  };
  
  public boolean matches(IntentFirewall paramIntentFirewall, ComponentName paramComponentName, Intent paramIntent, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    int i = 0;
    while (i < this.children.size())
    {
      if (!((Filter)this.children.get(i)).matches(paramIntentFirewall, paramComponentName, paramIntent, paramInt1, paramInt2, paramString, paramInt3)) {
        return false;
      }
      i += 1;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/AndFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */