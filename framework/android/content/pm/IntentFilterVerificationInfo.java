package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class IntentFilterVerificationInfo
  implements Parcelable
{
  private static final String ATTR_DOMAIN_NAME = "name";
  private static final String ATTR_PACKAGE_NAME = "packageName";
  private static final String ATTR_STATUS = "status";
  public static final Parcelable.Creator<IntentFilterVerificationInfo> CREATOR = new Parcelable.Creator()
  {
    public IntentFilterVerificationInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new IntentFilterVerificationInfo(paramAnonymousParcel);
    }
    
    public IntentFilterVerificationInfo[] newArray(int paramAnonymousInt)
    {
      return new IntentFilterVerificationInfo[paramAnonymousInt];
    }
  };
  private static final String TAG = IntentFilterVerificationInfo.class.getName();
  private static final String TAG_DOMAIN = "domain";
  private ArraySet<String> mDomains = new ArraySet();
  private int mMainStatus;
  private String mPackageName;
  
  public IntentFilterVerificationInfo()
  {
    this.mPackageName = null;
    this.mMainStatus = 0;
  }
  
  public IntentFilterVerificationInfo(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public IntentFilterVerificationInfo(String paramString, ArrayList<String> paramArrayList)
  {
    this.mPackageName = paramString;
    this.mDomains.addAll(paramArrayList);
    this.mMainStatus = 0;
  }
  
  public IntentFilterVerificationInfo(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    readFromXml(paramXmlPullParser);
  }
  
  public static String getStatusStringFromValue(long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    switch ((int)(paramLong >> 32))
    {
    default: 
      localStringBuilder.append("undefined");
    }
    for (;;)
    {
      return localStringBuilder.toString();
      localStringBuilder.append("always : ");
      localStringBuilder.append(Long.toHexString(0xFFFFFFFFFFFFFFFF & paramLong));
      continue;
      localStringBuilder.append("ask");
      continue;
      localStringBuilder.append("never");
      continue;
      localStringBuilder.append("always-ask");
    }
  }
  
  private void readFromParcel(Parcel paramParcel)
  {
    this.mPackageName = paramParcel.readString();
    this.mMainStatus = paramParcel.readInt();
    ArrayList localArrayList = new ArrayList();
    paramParcel.readStringList(localArrayList);
    this.mDomains.addAll(localArrayList);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public ArraySet<String> getDomains()
  {
    return this.mDomains;
  }
  
  public String getDomainsString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.mDomains.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(" ");
      }
      localStringBuilder.append(str);
    }
    return localStringBuilder.toString();
  }
  
  int getIntFromXml(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    String str = paramXmlPullParser.getAttributeValue(null, paramString);
    if (TextUtils.isEmpty(str))
    {
      paramXmlPullParser = "Missing element under " + TAG + ": " + paramString + " at " + paramXmlPullParser.getPositionDescription();
      Log.w(TAG, paramXmlPullParser);
      return paramInt;
    }
    return Integer.parseInt(str);
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public int getStatus()
  {
    return this.mMainStatus;
  }
  
  public String getStatusString()
  {
    return getStatusStringFromValue(this.mMainStatus);
  }
  
  String getStringFromXml(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    String str = paramXmlPullParser.getAttributeValue(null, paramString1);
    if (str == null)
    {
      paramXmlPullParser = "Missing element under " + TAG + ": " + paramString1 + " at " + paramXmlPullParser.getPositionDescription();
      Log.w(TAG, paramXmlPullParser);
      return paramString2;
    }
    return str;
  }
  
  public void readFromXml(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    this.mPackageName = getStringFromXml(paramXmlPullParser, "packageName", null);
    if (this.mPackageName == null) {
      Log.e(TAG, "Package name cannot be null!");
    }
    int i = getIntFromXml(paramXmlPullParser, "status", -1);
    if (i == -1) {
      Log.e(TAG, "Unknown status value: " + i);
    }
    this.mMainStatus = i;
    i = paramXmlPullParser.getDepth();
    int j;
    do
    {
      j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
    } while ((j == 3) || (j == 4));
    String str = paramXmlPullParser.getName();
    if (str.equals("domain"))
    {
      str = getStringFromXml(paramXmlPullParser, "name", null);
      if (!TextUtils.isEmpty(str)) {
        this.mDomains.add(str);
      }
    }
    for (;;)
    {
      XmlUtils.skipCurrentTag(paramXmlPullParser);
      break;
      Log.w(TAG, "Unknown tag parsing IntentFilter: " + str);
    }
  }
  
  public void setDomains(ArrayList<String> paramArrayList)
  {
    this.mDomains = new ArraySet(paramArrayList);
  }
  
  public void setStatus(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt <= 3))
    {
      this.mMainStatus = paramInt;
      return;
    }
    Log.w(TAG, "Trying to set a non supported status: " + paramInt);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeInt(this.mMainStatus);
    paramParcel.writeStringList(new ArrayList(this.mDomains));
  }
  
  public void writeToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.attribute(null, "packageName", this.mPackageName);
    paramXmlSerializer.attribute(null, "status", String.valueOf(this.mMainStatus));
    Iterator localIterator = this.mDomains.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramXmlSerializer.startTag(null, "domain");
      paramXmlSerializer.attribute(null, "name", str);
      paramXmlSerializer.endTag(null, "domain");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/IntentFilterVerificationInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */