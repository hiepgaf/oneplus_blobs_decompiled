package com.android.server.pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class PersistentPreferredActivity
  extends IntentFilter
{
  private static final String ATTR_FILTER = "filter";
  private static final String ATTR_NAME = "name";
  private static final boolean DEBUG_FILTERS = false;
  private static final String TAG = "PersistentPreferredActivity";
  final ComponentName mComponent;
  
  PersistentPreferredActivity(IntentFilter paramIntentFilter, ComponentName paramComponentName)
  {
    super(paramIntentFilter);
    this.mComponent = paramComponentName;
  }
  
  PersistentPreferredActivity(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject1 = paramXmlPullParser.getAttributeValue(null, "name");
    this.mComponent = ComponentName.unflattenFromString((String)localObject1);
    if (this.mComponent == null) {
      PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: Bad activity name " + (String)localObject1 + " at " + paramXmlPullParser.getPositionDescription());
    }
    int i = paramXmlPullParser.getDepth();
    localObject1 = paramXmlPullParser.getName();
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
            break label180;
          }
        }
      }
      if (!((String)localObject2).equals("filter")) {
        break;
      }
      readFromXml(paramXmlPullParser);
      return;
      label180:
      PackageManagerService.reportSettingsProblem(5, "Unknown element: " + (String)localObject2 + " at " + paramXmlPullParser.getPositionDescription());
      XmlUtils.skipCurrentTag(paramXmlPullParser);
      localObject1 = localObject2;
    }
    PackageManagerService.reportSettingsProblem(5, "Missing element filter at " + paramXmlPullParser.getPositionDescription());
    XmlUtils.skipCurrentTag(paramXmlPullParser);
  }
  
  public String toString()
  {
    return "PersistentPreferredActivity{0x" + Integer.toHexString(System.identityHashCode(this)) + " " + this.mComponent.flattenToShortString() + "}";
  }
  
  public void writeToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.attribute(null, "name", this.mComponent.flattenToShortString());
    paramXmlSerializer.startTag(null, "filter");
    super.writeToXml(paramXmlSerializer);
    paramXmlSerializer.endTag(null, "filter");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PersistentPreferredActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */