package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public final class VolumePolicy
  implements Parcelable
{
  public static final Parcelable.Creator<VolumePolicy> CREATOR = new Parcelable.Creator()
  {
    public VolumePolicy createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool3 = true;
      boolean bool1;
      boolean bool2;
      if (paramAnonymousParcel.readInt() != 0)
      {
        bool1 = true;
        if (paramAnonymousParcel.readInt() == 0) {
          break label49;
        }
        bool2 = true;
        label21:
        if (paramAnonymousParcel.readInt() == 0) {
          break label54;
        }
      }
      for (;;)
      {
        return new VolumePolicy(bool1, bool2, bool3, paramAnonymousParcel.readInt());
        bool1 = false;
        break;
        label49:
        bool2 = false;
        break label21;
        label54:
        bool3 = false;
      }
    }
    
    public VolumePolicy[] newArray(int paramAnonymousInt)
    {
      return new VolumePolicy[paramAnonymousInt];
    }
  };
  public static final VolumePolicy DEFAULT = new VolumePolicy(false, false, true, 400);
  public final boolean doNotDisturbWhenSilent;
  public final int vibrateToSilentDebounce;
  public final boolean volumeDownToEnterSilent;
  public final boolean volumeUpToExitSilent;
  
  public VolumePolicy(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    this.volumeDownToEnterSilent = paramBoolean1;
    this.volumeUpToExitSilent = paramBoolean2;
    this.doNotDisturbWhenSilent = paramBoolean3;
    this.vibrateToSilentDebounce = paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof VolumePolicy)) {
      return false;
    }
    if (paramObject == this) {
      return true;
    }
    paramObject = (VolumePolicy)paramObject;
    if ((((VolumePolicy)paramObject).volumeDownToEnterSilent == this.volumeDownToEnterSilent) && (((VolumePolicy)paramObject).volumeUpToExitSilent == this.volumeUpToExitSilent) && (((VolumePolicy)paramObject).doNotDisturbWhenSilent == this.doNotDisturbWhenSilent)) {
      return ((VolumePolicy)paramObject).vibrateToSilentDebounce == this.vibrateToSilentDebounce;
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Boolean.valueOf(this.volumeDownToEnterSilent), Boolean.valueOf(this.volumeUpToExitSilent), Boolean.valueOf(this.doNotDisturbWhenSilent), Integer.valueOf(this.vibrateToSilentDebounce) });
  }
  
  public String toString()
  {
    return "VolumePolicy[volumeDownToEnterSilent=" + this.volumeDownToEnterSilent + ",volumeUpToExitSilent=" + this.volumeUpToExitSilent + ",doNotDisturbWhenSilent=" + this.doNotDisturbWhenSilent + ",vibrateToSilentDebounce=" + this.vibrateToSilentDebounce + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    if (this.volumeDownToEnterSilent)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.volumeUpToExitSilent) {
        break label58;
      }
      paramInt = 1;
      label25:
      paramParcel.writeInt(paramInt);
      if (!this.doNotDisturbWhenSilent) {
        break label63;
      }
    }
    label58:
    label63:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.vibrateToSilentDebounce);
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label25;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/VolumePolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */