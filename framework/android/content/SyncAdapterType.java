package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class SyncAdapterType
  implements Parcelable
{
  public static final Parcelable.Creator<SyncAdapterType> CREATOR = new Parcelable.Creator()
  {
    public SyncAdapterType createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SyncAdapterType(paramAnonymousParcel);
    }
    
    public SyncAdapterType[] newArray(int paramAnonymousInt)
    {
      return new SyncAdapterType[paramAnonymousInt];
    }
  };
  public final String accountType;
  private final boolean allowParallelSyncs;
  public final String authority;
  private final boolean isAlwaysSyncable;
  public final boolean isKey;
  private final String packageName;
  private final String settingsActivity;
  private final boolean supportsUploading;
  private final boolean userVisible;
  
  public SyncAdapterType(Parcel paramParcel) {}
  
  private SyncAdapterType(String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString1)) {
      throw new IllegalArgumentException("the authority must not be empty: " + paramString1);
    }
    if (TextUtils.isEmpty(paramString2)) {
      throw new IllegalArgumentException("the accountType must not be empty: " + paramString2);
    }
    this.authority = paramString1;
    this.accountType = paramString2;
    this.userVisible = true;
    this.supportsUploading = true;
    this.isAlwaysSyncable = false;
    this.allowParallelSyncs = false;
    this.settingsActivity = null;
    this.isKey = true;
    this.packageName = null;
  }
  
  public SyncAdapterType(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (TextUtils.isEmpty(paramString1)) {
      throw new IllegalArgumentException("the authority must not be empty: " + paramString1);
    }
    if (TextUtils.isEmpty(paramString2)) {
      throw new IllegalArgumentException("the accountType must not be empty: " + paramString2);
    }
    this.authority = paramString1;
    this.accountType = paramString2;
    this.userVisible = paramBoolean1;
    this.supportsUploading = paramBoolean2;
    this.isAlwaysSyncable = false;
    this.allowParallelSyncs = false;
    this.settingsActivity = null;
    this.isKey = false;
    this.packageName = null;
  }
  
  public SyncAdapterType(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, String paramString3, String paramString4)
  {
    if (TextUtils.isEmpty(paramString1)) {
      throw new IllegalArgumentException("the authority must not be empty: " + paramString1);
    }
    if (TextUtils.isEmpty(paramString2)) {
      throw new IllegalArgumentException("the accountType must not be empty: " + paramString2);
    }
    this.authority = paramString1;
    this.accountType = paramString2;
    this.userVisible = paramBoolean1;
    this.supportsUploading = paramBoolean2;
    this.isAlwaysSyncable = paramBoolean3;
    this.allowParallelSyncs = paramBoolean4;
    this.settingsActivity = paramString3;
    this.isKey = false;
    this.packageName = paramString4;
  }
  
  public static SyncAdapterType newKey(String paramString1, String paramString2)
  {
    return new SyncAdapterType(paramString1, paramString2);
  }
  
  public boolean allowParallelSyncs()
  {
    if (this.isKey) {
      throw new IllegalStateException("this method is not allowed to be called when this is a key");
    }
    return this.allowParallelSyncs;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof SyncAdapterType)) {
      return false;
    }
    paramObject = (SyncAdapterType)paramObject;
    if (this.authority.equals(((SyncAdapterType)paramObject).authority)) {
      bool = this.accountType.equals(((SyncAdapterType)paramObject).accountType);
    }
    return bool;
  }
  
  public String getPackageName()
  {
    return this.packageName;
  }
  
  public String getSettingsActivity()
  {
    if (this.isKey) {
      throw new IllegalStateException("this method is not allowed to be called when this is a key");
    }
    return this.settingsActivity;
  }
  
  public int hashCode()
  {
    return (this.authority.hashCode() + 527) * 31 + this.accountType.hashCode();
  }
  
  public boolean isAlwaysSyncable()
  {
    if (this.isKey) {
      throw new IllegalStateException("this method is not allowed to be called when this is a key");
    }
    return this.isAlwaysSyncable;
  }
  
  public boolean isUserVisible()
  {
    if (this.isKey) {
      throw new IllegalStateException("this method is not allowed to be called when this is a key");
    }
    return this.userVisible;
  }
  
  public boolean supportsUploading()
  {
    if (this.isKey) {
      throw new IllegalStateException("this method is not allowed to be called when this is a key");
    }
    return this.supportsUploading;
  }
  
  public String toString()
  {
    if (this.isKey) {
      return "SyncAdapterType Key {name=" + this.authority + ", type=" + this.accountType + "}";
    }
    return "SyncAdapterType {name=" + this.authority + ", type=" + this.accountType + ", userVisible=" + this.userVisible + ", supportsUploading=" + this.supportsUploading + ", isAlwaysSyncable=" + this.isAlwaysSyncable + ", allowParallelSyncs=" + this.allowParallelSyncs + ", settingsActivity=" + this.settingsActivity + ", packageName=" + this.packageName + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    if (this.isKey) {
      throw new IllegalStateException("keys aren't parcelable");
    }
    paramParcel.writeString(this.authority);
    paramParcel.writeString(this.accountType);
    if (this.userVisible)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.supportsUploading) {
        break label113;
      }
      paramInt = 1;
      label58:
      paramParcel.writeInt(paramInt);
      if (!this.isAlwaysSyncable) {
        break label118;
      }
      paramInt = 1;
      label72:
      paramParcel.writeInt(paramInt);
      if (!this.allowParallelSyncs) {
        break label123;
      }
    }
    label113:
    label118:
    label123:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeString(this.settingsActivity);
      paramParcel.writeString(this.packageName);
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label58;
      paramInt = 0;
      break label72;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/SyncAdapterType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */