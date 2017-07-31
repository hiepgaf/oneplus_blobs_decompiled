package android.service.notification;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class Adjustment
  implements Parcelable
{
  public static final Parcelable.Creator<Adjustment> CREATOR = new Parcelable.Creator()
  {
    public Adjustment createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Adjustment(paramAnonymousParcel);
    }
    
    public Adjustment[] newArray(int paramAnonymousInt)
    {
      return new Adjustment[paramAnonymousInt];
    }
  };
  public static final String GROUP_KEY_OVERRIDE_KEY = "group_key_override";
  public static final String NEEDS_AUTOGROUPING_KEY = "autogroup_needed";
  private final CharSequence mExplanation;
  private final int mImportance;
  private final String mKey;
  private final String mPackage;
  private final Uri mReference;
  private final Bundle mSignals;
  private final int mUser;
  
  protected Adjustment(Parcel paramParcel)
  {
    if (paramParcel.readInt() == 1)
    {
      this.mPackage = paramParcel.readString();
      if (paramParcel.readInt() != 1) {
        break label101;
      }
      this.mKey = paramParcel.readString();
      label36:
      this.mImportance = paramParcel.readInt();
      if (paramParcel.readInt() != 1) {
        break label109;
      }
    }
    label101:
    label109:
    for (this.mExplanation = paramParcel.readCharSequence();; this.mExplanation = null)
    {
      this.mReference = ((Uri)paramParcel.readParcelable(Uri.class.getClassLoader()));
      this.mSignals = paramParcel.readBundle();
      this.mUser = paramParcel.readInt();
      return;
      this.mPackage = null;
      break;
      this.mKey = null;
      break label36;
    }
  }
  
  public Adjustment(String paramString1, String paramString2, int paramInt1, Bundle paramBundle, CharSequence paramCharSequence, Uri paramUri, int paramInt2)
  {
    this.mPackage = paramString1;
    this.mKey = paramString2;
    this.mImportance = paramInt1;
    this.mSignals = paramBundle;
    this.mExplanation = paramCharSequence;
    this.mReference = paramUri;
    this.mUser = paramInt2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public CharSequence getExplanation()
  {
    return this.mExplanation;
  }
  
  public int getImportance()
  {
    return this.mImportance;
  }
  
  public String getKey()
  {
    return this.mKey;
  }
  
  public String getPackage()
  {
    return this.mPackage;
  }
  
  public Uri getReference()
  {
    return this.mReference;
  }
  
  public Bundle getSignals()
  {
    return this.mSignals;
  }
  
  public int getUser()
  {
    return this.mUser;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mPackage != null)
    {
      paramParcel.writeInt(1);
      paramParcel.writeString(this.mPackage);
      if (this.mKey == null) {
        break label102;
      }
      paramParcel.writeInt(1);
      paramParcel.writeString(this.mKey);
      label40:
      paramParcel.writeInt(this.mImportance);
      if (this.mExplanation == null) {
        break label110;
      }
      paramParcel.writeInt(1);
      paramParcel.writeCharSequence(this.mExplanation);
    }
    for (;;)
    {
      paramParcel.writeParcelable(this.mReference, paramInt);
      paramParcel.writeBundle(this.mSignals);
      paramParcel.writeInt(this.mUser);
      return;
      paramParcel.writeInt(0);
      break;
      label102:
      paramParcel.writeInt(0);
      break label40;
      label110:
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/notification/Adjustment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */