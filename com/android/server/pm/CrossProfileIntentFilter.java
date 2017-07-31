package com.android.server.pm;

import android.content.IntentFilter;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class CrossProfileIntentFilter
  extends IntentFilter
{
  private static final String ATTR_FILTER = "filter";
  private static final String ATTR_FLAGS = "flags";
  private static final String ATTR_OWNER_PACKAGE = "ownerPackage";
  private static final String ATTR_TARGET_USER_ID = "targetUserId";
  private static final String TAG = "CrossProfileIntentFilter";
  final int mFlags;
  final String mOwnerPackage;
  final int mTargetUserId;
  
  CrossProfileIntentFilter(IntentFilter paramIntentFilter, String paramString, int paramInt1, int paramInt2)
  {
    super(paramIntentFilter);
    this.mTargetUserId = paramInt1;
    this.mOwnerPackage = paramString;
    this.mFlags = paramInt2;
  }
  
  CrossProfileIntentFilter(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    this.mTargetUserId = getIntFromXml(paramXmlPullParser, "targetUserId", 55536);
    this.mOwnerPackage = getStringFromXml(paramXmlPullParser, "ownerPackage", "");
    this.mFlags = getIntFromXml(paramXmlPullParser, "flags", 0);
    int i = paramXmlPullParser.getDepth();
    Object localObject1 = paramXmlPullParser.getName();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      Object localObject2 = localObject1;
      if (j != 1) {
        if (j == 3)
        {
          localObject2 = localObject1;
          if (paramXmlPullParser.getDepth() <= i) {}
        }
        else
        {
          localObject2 = paramXmlPullParser.getName();
          localObject1 = localObject2;
          if (j == 3) {
            continue;
          }
          localObject1 = localObject2;
          if (j == 4) {
            continue;
          }
          localObject1 = localObject2;
          if (j != 2) {
            continue;
          }
          if (!((String)localObject2).equals("filter")) {
            break label154;
          }
        }
      }
      if (!((String)localObject2).equals("filter")) {
        break;
      }
      readFromXml(paramXmlPullParser);
      return;
      label154:
      PackageManagerService.reportSettingsProblem(5, "Unknown element under crossProfile-intent-filters: " + (String)localObject2 + " at " + paramXmlPullParser.getPositionDescription());
      XmlUtils.skipCurrentTag(paramXmlPullParser);
      localObject1 = localObject2;
    }
    PackageManagerService.reportSettingsProblem(5, "Missing element under CrossProfileIntentFilter: filter at " + paramXmlPullParser.getPositionDescription());
    XmlUtils.skipCurrentTag(paramXmlPullParser);
  }
  
  boolean equalsIgnoreFilter(CrossProfileIntentFilter paramCrossProfileIntentFilter)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mTargetUserId == paramCrossProfileIntentFilter.mTargetUserId)
    {
      bool1 = bool2;
      if (this.mOwnerPackage.equals(paramCrossProfileIntentFilter.mOwnerPackage))
      {
        bool1 = bool2;
        if (this.mFlags == paramCrossProfileIntentFilter.mFlags) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public int getFlags()
  {
    return this.mFlags;
  }
  
  int getIntFromXml(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    paramXmlPullParser = getStringFromXml(paramXmlPullParser, paramString, null);
    if (paramXmlPullParser != null) {
      return Integer.parseInt(paramXmlPullParser);
    }
    return paramInt;
  }
  
  public String getOwnerPackage()
  {
    return this.mOwnerPackage;
  }
  
  String getStringFromXml(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    String str = paramXmlPullParser.getAttributeValue(null, paramString1);
    if (str == null)
    {
      PackageManagerService.reportSettingsProblem(5, "Missing element under CrossProfileIntentFilter: " + paramString1 + " at " + paramXmlPullParser.getPositionDescription());
      return paramString2;
    }
    return str;
  }
  
  public int getTargetUserId()
  {
    return this.mTargetUserId;
  }
  
  public String toString()
  {
    return "CrossProfileIntentFilter{0x" + Integer.toHexString(System.identityHashCode(this)) + " " + Integer.toString(this.mTargetUserId) + "}";
  }
  
  public void writeToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.attribute(null, "targetUserId", Integer.toString(this.mTargetUserId));
    paramXmlSerializer.attribute(null, "flags", Integer.toString(this.mFlags));
    paramXmlSerializer.attribute(null, "ownerPackage", this.mOwnerPackage);
    paramXmlSerializer.startTag(null, "filter");
    super.writeToXml(paramXmlSerializer);
    paramXmlSerializer.endTag(null, "filter");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/CrossProfileIntentFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */