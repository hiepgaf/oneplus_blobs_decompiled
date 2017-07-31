package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class NotFilter
  implements Filter
{
  public static final FilterFactory FACTORY = new FilterFactory("not")
  {
    public Filter newFilter(XmlPullParser paramAnonymousXmlPullParser)
      throws IOException, XmlPullParserException
    {
      Object localObject = null;
      int i = paramAnonymousXmlPullParser.getDepth();
      while (XmlUtils.nextElementWithin(paramAnonymousXmlPullParser, i))
      {
        Filter localFilter = IntentFirewall.parseFilter(paramAnonymousXmlPullParser);
        if (localObject == null) {
          localObject = localFilter;
        } else {
          throw new XmlPullParserException("<not> tag can only contain a single child filter.", paramAnonymousXmlPullParser, null);
        }
      }
      return new NotFilter((Filter)localObject, null);
    }
  };
  private final Filter mChild;
  
  private NotFilter(Filter paramFilter)
  {
    this.mChild = paramFilter;
  }
  
  public boolean matches(IntentFirewall paramIntentFirewall, ComponentName paramComponentName, Intent paramIntent, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    return !this.mChild.matches(paramIntentFirewall, paramComponentName, paramIntent, paramInt1, paramInt2, paramString, paramInt3);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/NotFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */