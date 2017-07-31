package com.android.server.firewall;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class FilterFactory
{
  private final String mTag;
  
  protected FilterFactory(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    this.mTag = paramString;
  }
  
  public String getTagName()
  {
    return this.mTag;
  }
  
  public abstract Filter newFilter(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/FilterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */