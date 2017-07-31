package com.android.server.pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class PreferredActivity
  extends IntentFilter
  implements PreferredComponent.Callbacks
{
  private static final boolean DEBUG_FILTERS = false;
  private static final String TAG = "PreferredActivity";
  final PreferredComponent mPref;
  
  PreferredActivity(IntentFilter paramIntentFilter, int paramInt, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, boolean paramBoolean)
  {
    super(paramIntentFilter);
    this.mPref = new PreferredComponent(this, paramInt, paramArrayOfComponentName, paramComponentName, paramBoolean);
  }
  
  PreferredActivity(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    this.mPref = new PreferredComponent(this, paramXmlPullParser);
  }
  
  public boolean onReadTag(String paramString, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    if (paramString.equals("filter")) {
      readFromXml(paramXmlPullParser);
    }
    for (;;)
    {
      return true;
      PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + paramXmlPullParser.getName());
      XmlUtils.skipCurrentTag(paramXmlPullParser);
    }
  }
  
  public String toString()
  {
    return "PreferredActivity{0x" + Integer.toHexString(System.identityHashCode(this)) + " " + this.mPref.mComponent.flattenToShortString() + "}";
  }
  
  public void writeToXml(XmlSerializer paramXmlSerializer, boolean paramBoolean)
    throws IOException
  {
    this.mPref.writeToXml(paramXmlSerializer, paramBoolean);
    paramXmlSerializer.startTag(null, "filter");
    super.writeToXml(paramXmlSerializer);
    paramXmlSerializer.endTag(null, "filter");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PreferredActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */