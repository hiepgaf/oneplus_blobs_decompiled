package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PackageInfo
  implements Parcelable
{
  public static final Parcelable.Creator<PackageInfo> CREATOR = new Parcelable.Creator()
  {
    public PackageInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PackageInfo(paramAnonymousParcel, null);
    }
    
    public PackageInfo[] newArray(int paramAnonymousInt)
    {
      return new PackageInfo[paramAnonymousInt];
    }
  };
  public static final int INSTALL_LOCATION_AUTO = 0;
  public static final int INSTALL_LOCATION_INTERNAL_ONLY = 1;
  public static final int INSTALL_LOCATION_PREFER_EXTERNAL = 2;
  public static final int INSTALL_LOCATION_UNSPECIFIED = -1;
  public static final int REQUESTED_PERMISSION_GRANTED = 2;
  public static final int REQUESTED_PERMISSION_REQUIRED = 1;
  public ActivityInfo[] activities;
  public ApplicationInfo applicationInfo;
  public int baseRevisionCode;
  public ConfigurationInfo[] configPreferences;
  public boolean coreApp;
  public FeatureGroupInfo[] featureGroups;
  public long firstInstallTime;
  public int[] gids;
  public int installLocation = 1;
  public InstrumentationInfo[] instrumentation;
  public long lastUpdateTime;
  public String overlayTarget;
  public String packageName;
  public PermissionInfo[] permissions;
  public ProviderInfo[] providers;
  public ActivityInfo[] receivers;
  public FeatureInfo[] reqFeatures;
  public String[] requestedPermissions;
  public int[] requestedPermissionsFlags;
  public String requiredAccountType;
  public boolean requiredForAllUsers;
  public String restrictedAccountType;
  public ServiceInfo[] services;
  public String sharedUserId;
  public int sharedUserLabel;
  public Signature[] signatures;
  public String[] splitNames;
  public int[] splitRevisionCodes;
  public int versionCode;
  public String versionName;
  
  public PackageInfo() {}
  
  private PackageInfo(Parcel paramParcel)
  {
    this.packageName = paramParcel.readString();
    this.splitNames = paramParcel.createStringArray();
    this.versionCode = paramParcel.readInt();
    this.versionName = paramParcel.readString();
    this.baseRevisionCode = paramParcel.readInt();
    this.splitRevisionCodes = paramParcel.createIntArray();
    this.sharedUserId = paramParcel.readString();
    this.sharedUserLabel = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      this.applicationInfo = ((ApplicationInfo)ApplicationInfo.CREATOR.createFromParcel(paramParcel));
    }
    this.firstInstallTime = paramParcel.readLong();
    this.lastUpdateTime = paramParcel.readLong();
    this.gids = paramParcel.createIntArray();
    this.activities = ((ActivityInfo[])paramParcel.createTypedArray(ActivityInfo.CREATOR));
    this.receivers = ((ActivityInfo[])paramParcel.createTypedArray(ActivityInfo.CREATOR));
    this.services = ((ServiceInfo[])paramParcel.createTypedArray(ServiceInfo.CREATOR));
    this.providers = ((ProviderInfo[])paramParcel.createTypedArray(ProviderInfo.CREATOR));
    this.instrumentation = ((InstrumentationInfo[])paramParcel.createTypedArray(InstrumentationInfo.CREATOR));
    this.permissions = ((PermissionInfo[])paramParcel.createTypedArray(PermissionInfo.CREATOR));
    this.requestedPermissions = paramParcel.createStringArray();
    this.requestedPermissionsFlags = paramParcel.createIntArray();
    this.signatures = ((Signature[])paramParcel.createTypedArray(Signature.CREATOR));
    this.configPreferences = ((ConfigurationInfo[])paramParcel.createTypedArray(ConfigurationInfo.CREATOR));
    this.reqFeatures = ((FeatureInfo[])paramParcel.createTypedArray(FeatureInfo.CREATOR));
    this.featureGroups = ((FeatureGroupInfo[])paramParcel.createTypedArray(FeatureGroupInfo.CREATOR));
    this.installLocation = paramParcel.readInt();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.coreApp = bool1;
      if (paramParcel.readInt() == 0) {
        break label399;
      }
    }
    label399:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.requiredForAllUsers = bool1;
      this.restrictedAccountType = paramParcel.readString();
      this.requiredAccountType = paramParcel.readString();
      this.overlayTarget = paramParcel.readString();
      if (this.applicationInfo != null)
      {
        propagateApplicationInfo(this.applicationInfo, this.activities);
        propagateApplicationInfo(this.applicationInfo, this.receivers);
        propagateApplicationInfo(this.applicationInfo, this.services);
        propagateApplicationInfo(this.applicationInfo, this.providers);
      }
      return;
      bool1 = false;
      break;
    }
  }
  
  private void propagateApplicationInfo(ApplicationInfo paramApplicationInfo, ComponentInfo[] paramArrayOfComponentInfo)
  {
    if (paramArrayOfComponentInfo != null)
    {
      int i = 0;
      int j = paramArrayOfComponentInfo.length;
      while (i < j)
      {
        paramArrayOfComponentInfo[i].applicationInfo = paramApplicationInfo;
        i += 1;
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return "PackageInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeString(this.packageName);
    paramParcel.writeStringArray(this.splitNames);
    paramParcel.writeInt(this.versionCode);
    paramParcel.writeString(this.versionName);
    paramParcel.writeInt(this.baseRevisionCode);
    paramParcel.writeIntArray(this.splitRevisionCodes);
    paramParcel.writeString(this.sharedUserId);
    paramParcel.writeInt(this.sharedUserLabel);
    if (this.applicationInfo != null)
    {
      paramParcel.writeInt(1);
      this.applicationInfo.writeToParcel(paramParcel, paramInt);
      paramParcel.writeLong(this.firstInstallTime);
      paramParcel.writeLong(this.lastUpdateTime);
      paramParcel.writeIntArray(this.gids);
      paramParcel.writeTypedArray(this.activities, paramInt | 0x2);
      paramParcel.writeTypedArray(this.receivers, paramInt | 0x2);
      paramParcel.writeTypedArray(this.services, paramInt | 0x2);
      paramParcel.writeTypedArray(this.providers, paramInt | 0x2);
      paramParcel.writeTypedArray(this.instrumentation, paramInt);
      paramParcel.writeTypedArray(this.permissions, paramInt);
      paramParcel.writeStringArray(this.requestedPermissions);
      paramParcel.writeIntArray(this.requestedPermissionsFlags);
      paramParcel.writeTypedArray(this.signatures, paramInt);
      paramParcel.writeTypedArray(this.configPreferences, paramInt);
      paramParcel.writeTypedArray(this.reqFeatures, paramInt);
      paramParcel.writeTypedArray(this.featureGroups, paramInt);
      paramParcel.writeInt(this.installLocation);
      if (!this.coreApp) {
        break label294;
      }
      paramInt = 1;
      label242:
      paramParcel.writeInt(paramInt);
      if (!this.requiredForAllUsers) {
        break label299;
      }
    }
    label294:
    label299:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeString(this.restrictedAccountType);
      paramParcel.writeString(this.requiredAccountType);
      paramParcel.writeString(this.overlayTarget);
      return;
      paramParcel.writeInt(0);
      break;
      paramInt = 0;
      break label242;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */