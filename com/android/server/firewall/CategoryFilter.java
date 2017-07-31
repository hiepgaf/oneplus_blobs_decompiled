package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import java.io.IOException;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class CategoryFilter
  implements Filter
{
  private static final String ATTR_NAME = "name";
  public static final FilterFactory FACTORY = new FilterFactory("category")
  {
    public Filter newFilter(XmlPullParser paramAnonymousXmlPullParser)
      throws IOException, XmlPullParserException
    {
      String str = paramAnonymousXmlPullParser.getAttributeValue(null, "name");
      if (str == null) {
        throw new XmlPullParserException("Category name must be specified.", paramAnonymousXmlPullParser, null);
      }
      return new CategoryFilter(str, null);
    }
  };
  private final String mCategoryName;
  
  private CategoryFilter(String paramString)
  {
    this.mCategoryName = paramString;
  }
  
  public boolean matches(IntentFirewall paramIntentFirewall, ComponentName paramComponentName, Intent paramIntent, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    paramIntentFirewall = paramIntent.getCategories();
    if (paramIntentFirewall == null) {
      return false;
    }
    return paramIntentFirewall.contains(this.mCategoryName);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/CategoryFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */