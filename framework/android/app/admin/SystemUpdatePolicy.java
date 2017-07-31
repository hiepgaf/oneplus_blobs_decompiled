package android.app.admin;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class SystemUpdatePolicy
  implements Parcelable
{
  public static final Parcelable.Creator<SystemUpdatePolicy> CREATOR = new Parcelable.Creator()
  {
    public SystemUpdatePolicy createFromParcel(Parcel paramAnonymousParcel)
    {
      SystemUpdatePolicy localSystemUpdatePolicy = new SystemUpdatePolicy(null);
      SystemUpdatePolicy.-set2(localSystemUpdatePolicy, paramAnonymousParcel.readInt());
      SystemUpdatePolicy.-set1(localSystemUpdatePolicy, paramAnonymousParcel.readInt());
      SystemUpdatePolicy.-set0(localSystemUpdatePolicy, paramAnonymousParcel.readInt());
      return localSystemUpdatePolicy;
    }
    
    public SystemUpdatePolicy[] newArray(int paramAnonymousInt)
    {
      return new SystemUpdatePolicy[paramAnonymousInt];
    }
  };
  private static final String KEY_INSTALL_WINDOW_END = "install_window_end";
  private static final String KEY_INSTALL_WINDOW_START = "install_window_start";
  private static final String KEY_POLICY_TYPE = "policy_type";
  public static final int TYPE_INSTALL_AUTOMATIC = 1;
  public static final int TYPE_INSTALL_WINDOWED = 2;
  public static final int TYPE_POSTPONE = 3;
  private static final int TYPE_UNKNOWN = -1;
  private static final int WINDOW_BOUNDARY = 1440;
  private int mMaintenanceWindowEnd;
  private int mMaintenanceWindowStart;
  private int mPolicyType = -1;
  
  public static SystemUpdatePolicy createAutomaticInstallPolicy()
  {
    SystemUpdatePolicy localSystemUpdatePolicy = new SystemUpdatePolicy();
    localSystemUpdatePolicy.mPolicyType = 1;
    return localSystemUpdatePolicy;
  }
  
  public static SystemUpdatePolicy createPostponeInstallPolicy()
  {
    SystemUpdatePolicy localSystemUpdatePolicy = new SystemUpdatePolicy();
    localSystemUpdatePolicy.mPolicyType = 3;
    return localSystemUpdatePolicy;
  }
  
  public static SystemUpdatePolicy createWindowedInstallPolicy(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= 1440)) {}
    while ((paramInt2 < 0) || (paramInt2 >= 1440)) {
      throw new IllegalArgumentException("startTime and endTime must be inside [0, 1440)");
    }
    SystemUpdatePolicy localSystemUpdatePolicy = new SystemUpdatePolicy();
    localSystemUpdatePolicy.mPolicyType = 2;
    localSystemUpdatePolicy.mMaintenanceWindowStart = paramInt1;
    localSystemUpdatePolicy.mMaintenanceWindowEnd = paramInt2;
    return localSystemUpdatePolicy;
  }
  
  public static SystemUpdatePolicy restoreFromXml(XmlPullParser paramXmlPullParser)
  {
    try
    {
      SystemUpdatePolicy localSystemUpdatePolicy = new SystemUpdatePolicy();
      String str = paramXmlPullParser.getAttributeValue(null, "policy_type");
      if (str != null)
      {
        localSystemUpdatePolicy.mPolicyType = Integer.parseInt(str);
        str = paramXmlPullParser.getAttributeValue(null, "install_window_start");
        if (str != null) {
          localSystemUpdatePolicy.mMaintenanceWindowStart = Integer.parseInt(str);
        }
        paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "install_window_end");
        if (paramXmlPullParser != null) {
          localSystemUpdatePolicy.mMaintenanceWindowEnd = Integer.parseInt(paramXmlPullParser);
        }
        return localSystemUpdatePolicy;
      }
    }
    catch (NumberFormatException paramXmlPullParser) {}
    return null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getInstallWindowEnd()
  {
    if (this.mPolicyType == 2) {
      return this.mMaintenanceWindowEnd;
    }
    return -1;
  }
  
  public int getInstallWindowStart()
  {
    if (this.mPolicyType == 2) {
      return this.mMaintenanceWindowStart;
    }
    return -1;
  }
  
  public int getPolicyType()
  {
    return this.mPolicyType;
  }
  
  public boolean isValid()
  {
    if ((this.mPolicyType == 1) || (this.mPolicyType == 3)) {
      return true;
    }
    if (this.mPolicyType == 2)
    {
      if ((this.mMaintenanceWindowStart >= 0) && (this.mMaintenanceWindowStart < 1440) && (this.mMaintenanceWindowEnd >= 0)) {
        return this.mMaintenanceWindowEnd < 1440;
      }
      return false;
    }
    return false;
  }
  
  public void saveToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.attribute(null, "policy_type", Integer.toString(this.mPolicyType));
    paramXmlSerializer.attribute(null, "install_window_start", Integer.toString(this.mMaintenanceWindowStart));
    paramXmlSerializer.attribute(null, "install_window_end", Integer.toString(this.mMaintenanceWindowEnd));
  }
  
  public String toString()
  {
    return String.format("SystemUpdatePolicy (type: %d, windowStart: %d, windowEnd: %d)", new Object[] { Integer.valueOf(this.mPolicyType), Integer.valueOf(this.mMaintenanceWindowStart), Integer.valueOf(this.mMaintenanceWindowEnd) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mPolicyType);
    paramParcel.writeInt(this.mMaintenanceWindowStart);
    paramParcel.writeInt(this.mMaintenanceWindowEnd);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/admin/SystemUpdatePolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */