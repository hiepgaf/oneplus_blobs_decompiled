package android.printservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public final class PrintServiceInfo
  implements Parcelable
{
  public static final Parcelable.Creator<PrintServiceInfo> CREATOR = new Parcelable.Creator()
  {
    public PrintServiceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrintServiceInfo(paramAnonymousParcel);
    }
    
    public PrintServiceInfo[] newArray(int paramAnonymousInt)
    {
      return new PrintServiceInfo[paramAnonymousInt];
    }
  };
  private static final String LOG_TAG = PrintServiceInfo.class.getSimpleName();
  private static final String TAG_PRINT_SERVICE = "print-service";
  private final String mAddPrintersActivityName;
  private final String mAdvancedPrintOptionsActivityName;
  private final String mId;
  private boolean mIsEnabled;
  private final ResolveInfo mResolveInfo;
  private final String mSettingsActivityName;
  
  public PrintServiceInfo(ResolveInfo paramResolveInfo, String paramString1, String paramString2, String paramString3)
  {
    this.mId = new ComponentName(paramResolveInfo.serviceInfo.packageName, paramResolveInfo.serviceInfo.name).flattenToString();
    this.mResolveInfo = paramResolveInfo;
    this.mSettingsActivityName = paramString1;
    this.mAddPrintersActivityName = paramString2;
    this.mAdvancedPrintOptionsActivityName = paramString3;
  }
  
  public PrintServiceInfo(Parcel paramParcel)
  {
    this.mId = paramParcel.readString();
    if (paramParcel.readByte() != 0) {
      bool = true;
    }
    this.mIsEnabled = bool;
    this.mResolveInfo = ((ResolveInfo)paramParcel.readParcelable(null));
    this.mSettingsActivityName = paramParcel.readString();
    this.mAddPrintersActivityName = paramParcel.readString();
    this.mAdvancedPrintOptionsActivityName = paramParcel.readString();
  }
  
  public static PrintServiceInfo create(ResolveInfo paramResolveInfo, Context paramContext)
  {
    localObject2 = null;
    Object localObject19 = null;
    Object localObject20 = null;
    Object localObject21 = null;
    Object localObject22 = null;
    localObject3 = null;
    Object localObject15 = null;
    Object localObject16 = null;
    Object localObject17 = null;
    TypedArray localTypedArray = null;
    localObject1 = null;
    Object localObject12 = null;
    Object localObject13 = null;
    Object localObject14 = null;
    Object localObject18 = null;
    PackageManager localPackageManager = paramContext.getPackageManager();
    localXmlResourceParser = paramResolveInfo.serviceInfo.loadXmlMetaData(localPackageManager, "android.printservice");
    paramContext = (Context)localObject3;
    int i;
    if (localXmlResourceParser != null) {
      i = 0;
    }
    for (;;)
    {
      Object localObject4;
      Object localObject5;
      Object localObject6;
      Object localObject7;
      Object localObject8;
      Object localObject9;
      Object localObject10;
      Object localObject11;
      if ((i != 1) && (i != 2))
      {
        localObject3 = localObject15;
        localObject4 = localObject12;
        localObject5 = localObject19;
        localObject6 = localObject16;
        localObject7 = localObject13;
        localObject8 = localObject20;
        localObject9 = localObject17;
        localObject10 = localObject14;
        localObject11 = localObject21;
      }
      try
      {
        i = localXmlResourceParser.next();
      }
      catch (IOException paramContext)
      {
        for (;;)
        {
          Log.w(LOG_TAG, "Error reading meta-data:" + paramContext);
          paramContext = (Context)localObject3;
          localObject1 = localObject4;
          localObject2 = localObject5;
          if (localXmlResourceParser != null)
          {
            localXmlResourceParser.close();
            paramContext = (Context)localObject3;
            localObject1 = localObject4;
            localObject2 = localObject5;
          }
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        for (;;)
        {
          Log.e(LOG_TAG, "Unable to load resources for: " + paramResolveInfo.serviceInfo.packageName);
          paramContext = (Context)localObject6;
          localObject1 = localObject7;
          localObject2 = localObject8;
          if (localXmlResourceParser != null)
          {
            localXmlResourceParser.close();
            paramContext = (Context)localObject6;
            localObject1 = localObject7;
            localObject2 = localObject8;
          }
        }
      }
      catch (XmlPullParserException paramContext)
      {
        for (;;)
        {
          Log.w(LOG_TAG, "Error reading meta-data:" + paramContext);
          paramContext = (Context)localObject9;
          localObject1 = localObject10;
          localObject2 = localObject11;
          if (localXmlResourceParser != null)
          {
            localXmlResourceParser.close();
            paramContext = (Context)localObject9;
            localObject1 = localObject10;
            localObject2 = localObject11;
          }
        }
      }
      finally
      {
        if (localXmlResourceParser == null) {
          break label718;
        }
        localXmlResourceParser.close();
      }
    }
    localObject3 = localObject15;
    localObject4 = localObject12;
    localObject5 = localObject19;
    localObject6 = localObject16;
    localObject7 = localObject13;
    localObject8 = localObject20;
    localObject9 = localObject17;
    localObject10 = localObject14;
    localObject11 = localObject21;
    if (!"print-service".equals(localXmlResourceParser.getName()))
    {
      localObject3 = localObject15;
      localObject4 = localObject12;
      localObject5 = localObject19;
      localObject6 = localObject16;
      localObject7 = localObject13;
      localObject8 = localObject20;
      localObject9 = localObject17;
      localObject10 = localObject14;
      localObject11 = localObject21;
      Log.e(LOG_TAG, "Ignoring meta-data that does not start with print-service tag");
      localObject5 = localObject22;
      localObject4 = localObject18;
      localObject3 = localTypedArray;
    }
    for (;;)
    {
      paramContext = (Context)localObject3;
      localObject1 = localObject4;
      localObject2 = localObject5;
      if (localXmlResourceParser != null)
      {
        localXmlResourceParser.close();
        localObject2 = localObject5;
        localObject1 = localObject4;
        paramContext = (Context)localObject3;
      }
      return new PrintServiceInfo(paramResolveInfo, (String)localObject2, paramContext, (String)localObject1);
      localObject3 = localObject15;
      localObject4 = localObject12;
      localObject5 = localObject19;
      localObject6 = localObject16;
      localObject7 = localObject13;
      localObject8 = localObject20;
      localObject9 = localObject17;
      localObject10 = localObject14;
      localObject11 = localObject21;
      localTypedArray = localPackageManager.getResourcesForApplication(paramResolveInfo.serviceInfo.applicationInfo).obtainAttributes(Xml.asAttributeSet(localXmlResourceParser), R.styleable.PrintService);
      localObject3 = localObject15;
      localObject4 = localObject12;
      localObject5 = localObject19;
      localObject6 = localObject16;
      localObject7 = localObject13;
      localObject8 = localObject20;
      localObject9 = localObject17;
      localObject10 = localObject14;
      localObject11 = localObject21;
      paramContext = localTypedArray.getString(0);
      localObject3 = localObject15;
      localObject4 = localObject12;
      localObject5 = paramContext;
      localObject6 = localObject16;
      localObject7 = localObject13;
      localObject8 = paramContext;
      localObject9 = localObject17;
      localObject10 = localObject14;
      localObject11 = paramContext;
      localObject1 = localTypedArray.getString(1);
      localObject3 = localObject1;
      localObject4 = localObject12;
      localObject5 = paramContext;
      localObject6 = localObject1;
      localObject7 = localObject13;
      localObject8 = paramContext;
      localObject9 = localObject1;
      localObject10 = localObject14;
      localObject11 = paramContext;
      localObject2 = localTypedArray.getString(3);
      localObject3 = localObject1;
      localObject4 = localObject2;
      localObject5 = paramContext;
      localObject6 = localObject1;
      localObject7 = localObject2;
      localObject8 = paramContext;
      localObject9 = localObject1;
      localObject10 = localObject2;
      localObject11 = paramContext;
      localTypedArray.recycle();
      localObject3 = localObject1;
      localObject4 = localObject2;
      localObject5 = paramContext;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (PrintServiceInfo)paramObject;
    if (this.mId == null)
    {
      if (((PrintServiceInfo)paramObject).mId != null) {
        return false;
      }
    }
    else if (!this.mId.equals(((PrintServiceInfo)paramObject).mId)) {
      return false;
    }
    return true;
  }
  
  public String getAddPrintersActivityName()
  {
    return this.mAddPrintersActivityName;
  }
  
  public String getAdvancedOptionsActivityName()
  {
    return this.mAdvancedPrintOptionsActivityName;
  }
  
  public ComponentName getComponentName()
  {
    return new ComponentName(this.mResolveInfo.serviceInfo.packageName, this.mResolveInfo.serviceInfo.name);
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public ResolveInfo getResolveInfo()
  {
    return this.mResolveInfo;
  }
  
  public String getSettingsActivityName()
  {
    return this.mSettingsActivityName;
  }
  
  public int hashCode()
  {
    if (this.mId == null) {}
    for (int i = 0;; i = this.mId.hashCode()) {
      return i + 31;
    }
  }
  
  public boolean isEnabled()
  {
    return this.mIsEnabled;
  }
  
  public void setIsEnabled(boolean paramBoolean)
  {
    this.mIsEnabled = paramBoolean;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PrintServiceInfo{");
    localStringBuilder.append("id=").append(this.mId);
    localStringBuilder.append("isEnabled=").append(this.mIsEnabled);
    localStringBuilder.append(", resolveInfo=").append(this.mResolveInfo);
    localStringBuilder.append(", settingsActivityName=").append(this.mSettingsActivityName);
    localStringBuilder.append(", addPrintersActivityName=").append(this.mAddPrintersActivityName);
    localStringBuilder.append(", advancedPrintOptionsActivityName=").append(this.mAdvancedPrintOptionsActivityName);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mId);
    if (this.mIsEnabled) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeParcelable(this.mResolveInfo, 0);
      paramParcel.writeString(this.mSettingsActivityName);
      paramParcel.writeString(this.mAddPrintersActivityName);
      paramParcel.writeString(this.mAdvancedPrintOptionsActivityName);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/PrintServiceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */