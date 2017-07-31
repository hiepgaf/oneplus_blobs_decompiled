package android.app;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public final class AutomaticZenRule
  implements Parcelable
{
  public static final Parcelable.Creator<AutomaticZenRule> CREATOR = new Parcelable.Creator()
  {
    public AutomaticZenRule createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AutomaticZenRule(paramAnonymousParcel);
    }
    
    public AutomaticZenRule[] newArray(int paramAnonymousInt)
    {
      return new AutomaticZenRule[paramAnonymousInt];
    }
  };
  private Uri conditionId;
  private long creationTime;
  private boolean enabled = false;
  private int interruptionFilter;
  private String name;
  private ComponentName owner;
  
  public AutomaticZenRule(Parcel paramParcel)
  {
    if (paramParcel.readInt() == 1) {
      bool = true;
    }
    this.enabled = bool;
    if (paramParcel.readInt() == 1) {
      this.name = paramParcel.readString();
    }
    this.interruptionFilter = paramParcel.readInt();
    this.conditionId = ((Uri)paramParcel.readParcelable(null));
    this.owner = ((ComponentName)paramParcel.readParcelable(null));
    this.creationTime = paramParcel.readLong();
  }
  
  public AutomaticZenRule(String paramString, ComponentName paramComponentName, Uri paramUri, int paramInt, boolean paramBoolean)
  {
    this.name = paramString;
    this.owner = paramComponentName;
    this.conditionId = paramUri;
    this.interruptionFilter = paramInt;
    this.enabled = paramBoolean;
  }
  
  public AutomaticZenRule(String paramString, ComponentName paramComponentName, Uri paramUri, int paramInt, boolean paramBoolean, long paramLong)
  {
    this(paramString, paramComponentName, paramUri, paramInt, paramBoolean);
    this.creationTime = paramLong;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AutomaticZenRule)) {
      return false;
    }
    if (paramObject == this) {
      return true;
    }
    paramObject = (AutomaticZenRule)paramObject;
    if ((((AutomaticZenRule)paramObject).enabled == this.enabled) && (Objects.equals(((AutomaticZenRule)paramObject).name, this.name)) && (((AutomaticZenRule)paramObject).interruptionFilter == this.interruptionFilter) && (Objects.equals(((AutomaticZenRule)paramObject).conditionId, this.conditionId)) && (Objects.equals(((AutomaticZenRule)paramObject).owner, this.owner))) {
      return ((AutomaticZenRule)paramObject).creationTime == this.creationTime;
    }
    return false;
  }
  
  public Uri getConditionId()
  {
    return this.conditionId;
  }
  
  public long getCreationTime()
  {
    return this.creationTime;
  }
  
  public int getInterruptionFilter()
  {
    return this.interruptionFilter;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public ComponentName getOwner()
  {
    return this.owner;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Boolean.valueOf(this.enabled), this.name, Integer.valueOf(this.interruptionFilter), this.conditionId, this.owner, Long.valueOf(this.creationTime) });
  }
  
  public boolean isEnabled()
  {
    return this.enabled;
  }
  
  public void setConditionId(Uri paramUri)
  {
    this.conditionId = paramUri;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.enabled = paramBoolean;
  }
  
  public void setInterruptionFilter(int paramInt)
  {
    this.interruptionFilter = paramInt;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public String toString()
  {
    return AutomaticZenRule.class.getSimpleName() + '[' + "enabled=" + this.enabled + ",name=" + this.name + ",interruptionFilter=" + this.interruptionFilter + ",conditionId=" + this.conditionId + ",owner=" + this.owner + ",creationTime=" + this.creationTime + ']';
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.enabled)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (this.name == null) {
        break label74;
      }
      paramParcel.writeInt(1);
      paramParcel.writeString(this.name);
    }
    for (;;)
    {
      paramParcel.writeInt(this.interruptionFilter);
      paramParcel.writeParcelable(this.conditionId, 0);
      paramParcel.writeParcelable(this.owner, 0);
      paramParcel.writeLong(this.creationTime);
      return;
      paramInt = 0;
      break;
      label74:
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/AutomaticZenRule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */