package com.android.server.firewall;

import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

abstract class FilterList
  implements Filter
{
  protected final ArrayList<Filter> children = new ArrayList();
  
  protected void readChild(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    paramXmlPullParser = IntentFirewall.parseFilter(paramXmlPullParser);
    this.children.add(paramXmlPullParser);
  }
  
  public FilterList readFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      readChild(paramXmlPullParser);
    }
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/FilterList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */