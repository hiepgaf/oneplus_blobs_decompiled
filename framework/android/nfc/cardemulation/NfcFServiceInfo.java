package android.nfc.cardemulation;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import org.xmlpull.v1.XmlPullParserException;

public final class NfcFServiceInfo
  implements Parcelable
{
  public static final Parcelable.Creator<NfcFServiceInfo> CREATOR = new Parcelable.Creator()
  {
    public NfcFServiceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)ResolveInfo.CREATOR.createFromParcel(paramAnonymousParcel);
      String str3 = paramAnonymousParcel.readString();
      String str4 = paramAnonymousParcel.readString();
      String str1 = null;
      if (paramAnonymousParcel.readInt() != 0) {
        str1 = paramAnonymousParcel.readString();
      }
      String str5 = paramAnonymousParcel.readString();
      String str2 = null;
      if (paramAnonymousParcel.readInt() != 0) {
        str2 = paramAnonymousParcel.readString();
      }
      return new NfcFServiceInfo(localResolveInfo, str3, str4, str1, str5, str2, paramAnonymousParcel.readInt());
    }
    
    public NfcFServiceInfo[] newArray(int paramAnonymousInt)
    {
      return new NfcFServiceInfo[paramAnonymousInt];
    }
  };
  static final String TAG = "NfcFServiceInfo";
  final String mDescription;
  String mDynamicNfcid2;
  String mDynamicSystemCode;
  final String mNfcid2;
  final ResolveInfo mService;
  final String mSystemCode;
  final int mUid;
  
  public NfcFServiceInfo(PackageManager paramPackageManager, ResolveInfo paramResolveInfo)
    throws XmlPullParserException, IOException
  {
    ServiceInfo localServiceInfo = paramResolveInfo.serviceInfo;
    Object localObject2 = null;
    Object localObject1 = null;
    XmlResourceParser localXmlResourceParser;
    try
    {
      localXmlResourceParser = localServiceInfo.loadXmlMetaData(paramPackageManager, "android.nfc.cardemulation.host_nfcf_service");
      if (localXmlResourceParser == null)
      {
        localObject1 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        throw new XmlPullParserException("No android.nfc.cardemulation.host_nfcf_service meta-data");
      }
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      localObject2 = localObject1;
      throw new XmlPullParserException("Unable to create context for: " + localServiceInfo.packageName);
    }
    finally
    {
      if (localObject2 != null) {
        ((XmlResourceParser)localObject2).close();
      }
    }
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    for (int i = localXmlResourceParser.getEventType(); (i != 2) && (i != 1); i = localXmlResourceParser.next())
    {
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
    }
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    if (!"host-nfcf-service".equals(localXmlResourceParser.getName()))
    {
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      throw new XmlPullParserException("Meta-data does not start with <host-nfcf-service> tag");
    }
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    Resources localResources = paramPackageManager.getResourcesForApplication(localServiceInfo.applicationInfo);
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    paramPackageManager = localResources.obtainAttributes(localAttributeSet, R.styleable.HostNfcFService);
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    this.mService = paramResolveInfo;
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    this.mDescription = paramPackageManager.getString(0);
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    this.mDynamicSystemCode = null;
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    this.mDynamicNfcid2 = null;
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    paramPackageManager.recycle();
    paramResolveInfo = null;
    paramPackageManager = null;
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    i = localXmlResourceParser.getDepth();
    label549:
    do
    {
      int j;
      do
      {
        localObject1 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        j = localXmlResourceParser.next();
        if (j == 3)
        {
          localObject1 = localXmlResourceParser;
          localObject2 = localXmlResourceParser;
          if (localXmlResourceParser.getDepth() <= i) {
            break;
          }
        }
        if (j == 1) {
          break;
        }
        localObject1 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser.getName();
        if (j == 2)
        {
          localObject1 = localXmlResourceParser;
          localObject2 = localXmlResourceParser;
          if (("system-code-filter".equals(localObject3)) && (paramResolveInfo == null))
          {
            localObject1 = localXmlResourceParser;
            localObject2 = localXmlResourceParser;
            localTypedArray = localResources.obtainAttributes(localAttributeSet, R.styleable.SystemCodeFilter);
            localObject1 = localXmlResourceParser;
            localObject2 = localXmlResourceParser;
            localObject3 = localTypedArray.getString(0).toUpperCase();
            localObject1 = localXmlResourceParser;
            localObject2 = localXmlResourceParser;
            Log.d("NfcFServiceInfo", "systemCode: " + (String)localObject3);
            localObject1 = localXmlResourceParser;
            localObject2 = localXmlResourceParser;
            paramResolveInfo = (ResolveInfo)localObject3;
            if (!NfcFCardEmulation.isValidSystemCode((String)localObject3))
            {
              localObject1 = localXmlResourceParser;
              localObject2 = localXmlResourceParser;
              if (!((String)localObject3).equalsIgnoreCase("NULL")) {
                break label549;
              }
            }
            for (paramResolveInfo = (ResolveInfo)localObject3;; paramResolveInfo = null)
            {
              localObject1 = localXmlResourceParser;
              localObject2 = localXmlResourceParser;
              localTypedArray.recycle();
              break;
              localObject1 = localXmlResourceParser;
              localObject2 = localXmlResourceParser;
              Log.e("NfcFServiceInfo", "Invalid System Code: " + (String)localObject3);
            }
          }
        }
      } while (j != 2);
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
    } while ((!"nfcid2-filter".equals(localObject3)) || (paramPackageManager != null));
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    TypedArray localTypedArray = localResources.obtainAttributes(localAttributeSet, R.styleable.Nfcid2Filter);
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    Object localObject3 = localTypedArray.getString(0).toUpperCase();
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    paramPackageManager = (PackageManager)localObject3;
    if (!((String)localObject3).equalsIgnoreCase("RANDOM"))
    {
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      if (!((String)localObject3).equalsIgnoreCase("NULL")) {
        break label713;
      }
      paramPackageManager = (PackageManager)localObject3;
    }
    for (;;)
    {
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      localTypedArray.recycle();
      break;
      label713:
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      paramPackageManager = (PackageManager)localObject3;
      if (!NfcFCardEmulation.isValidNfcid2((String)localObject3))
      {
        localObject1 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        Log.e("NfcFServiceInfo", "Invalid NFCID2: " + (String)localObject3);
        paramPackageManager = null;
      }
    }
    for (;;)
    {
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      this.mSystemCode = ((String)localObject3);
      paramResolveInfo = paramPackageManager;
      if (paramPackageManager == null) {
        paramResolveInfo = "NULL";
      }
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      this.mNfcid2 = paramResolveInfo;
      if (localXmlResourceParser != null) {
        localXmlResourceParser.close();
      }
      this.mUid = localServiceInfo.applicationInfo.uid;
      return;
      localObject3 = paramResolveInfo;
      if (paramResolveInfo == null) {
        localObject3 = "NULL";
      }
    }
  }
  
  public NfcFServiceInfo(ResolveInfo paramResolveInfo, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt)
  {
    this.mService = paramResolveInfo;
    this.mDescription = paramString1;
    this.mSystemCode = paramString2;
    this.mDynamicSystemCode = paramString3;
    this.mNfcid2 = paramString4;
    this.mDynamicNfcid2 = paramString5;
    this.mUid = paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("    " + getComponent() + " (Description: " + getDescription() + ")");
    paramPrintWriter.println("    System Code: " + getSystemCode());
    paramPrintWriter.println("    NFCID2: " + getNfcid2());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof NfcFServiceInfo)) {
      return false;
    }
    paramObject = (NfcFServiceInfo)paramObject;
    if (!((NfcFServiceInfo)paramObject).getComponent().equals(getComponent())) {
      return false;
    }
    if (!((NfcFServiceInfo)paramObject).mSystemCode.equalsIgnoreCase(this.mSystemCode)) {
      return false;
    }
    return ((NfcFServiceInfo)paramObject).mNfcid2.equalsIgnoreCase(this.mNfcid2);
  }
  
  public ComponentName getComponent()
  {
    return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
  }
  
  public String getDescription()
  {
    return this.mDescription;
  }
  
  public String getNfcid2()
  {
    if (this.mDynamicNfcid2 == null) {
      return this.mNfcid2;
    }
    return this.mDynamicNfcid2;
  }
  
  public String getSystemCode()
  {
    if (this.mDynamicSystemCode == null) {
      return this.mSystemCode;
    }
    return this.mDynamicSystemCode;
  }
  
  public int getUid()
  {
    return this.mUid;
  }
  
  public int hashCode()
  {
    return getComponent().hashCode();
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    return this.mService.loadIcon(paramPackageManager);
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    return this.mService.loadLabel(paramPackageManager);
  }
  
  public void setOrReplaceDynamicNfcid2(String paramString)
  {
    this.mDynamicNfcid2 = paramString;
  }
  
  public void setOrReplaceDynamicSystemCode(String paramString)
  {
    this.mDynamicSystemCode = paramString;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("NfcFService: ");
    localStringBuilder.append(getComponent());
    localStringBuilder.append(", description: ").append(this.mDescription);
    localStringBuilder.append(", System Code: ").append(this.mSystemCode);
    if (this.mDynamicSystemCode != null) {
      localStringBuilder.append(", dynamic System Code: ").append(this.mDynamicSystemCode);
    }
    localStringBuilder.append(", NFCID2: ").append(this.mNfcid2);
    if (this.mDynamicNfcid2 != null) {
      localStringBuilder.append(", dynamic NFCID2: ").append(this.mDynamicNfcid2);
    }
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    this.mService.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.mDescription);
    paramParcel.writeString(this.mSystemCode);
    if (this.mDynamicSystemCode != null)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (this.mDynamicSystemCode != null) {
        paramParcel.writeString(this.mDynamicSystemCode);
      }
      paramParcel.writeString(this.mNfcid2);
      if (this.mDynamicNfcid2 == null) {
        break label107;
      }
    }
    label107:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      if (this.mDynamicNfcid2 != null) {
        paramParcel.writeString(this.mDynamicNfcid2);
      }
      paramParcel.writeInt(this.mUid);
      return;
      paramInt = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/cardemulation/NfcFServiceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */