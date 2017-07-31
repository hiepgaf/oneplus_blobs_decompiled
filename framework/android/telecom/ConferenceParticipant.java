package android.telecom;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ConferenceParticipant
  implements Parcelable
{
  public static final Parcelable.Creator<ConferenceParticipant> CREATOR = new Parcelable.Creator()
  {
    public ConferenceParticipant createFromParcel(Parcel paramAnonymousParcel)
    {
      ClassLoader localClassLoader = ParcelableCall.class.getClassLoader();
      return new ConferenceParticipant((Uri)paramAnonymousParcel.readParcelable(localClassLoader), paramAnonymousParcel.readString(), (Uri)paramAnonymousParcel.readParcelable(localClassLoader), paramAnonymousParcel.readInt());
    }
    
    public ConferenceParticipant[] newArray(int paramAnonymousInt)
    {
      return new ConferenceParticipant[paramAnonymousInt];
    }
  };
  private final String mDisplayName;
  private final Uri mEndpoint;
  private final Uri mHandle;
  private final int mState;
  
  public ConferenceParticipant(Uri paramUri1, String paramString, Uri paramUri2, int paramInt)
  {
    this.mHandle = paramUri1;
    this.mDisplayName = paramString;
    this.mEndpoint = paramUri2;
    this.mState = paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getDisplayName()
  {
    return this.mDisplayName;
  }
  
  public Uri getEndpoint()
  {
    return this.mEndpoint;
  }
  
  public Uri getHandle()
  {
    return this.mHandle;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[ConferenceParticipant Handle: ");
    localStringBuilder.append(Log.pii(this.mHandle));
    localStringBuilder.append(" DisplayName: ");
    localStringBuilder.append(Log.pii(this.mDisplayName));
    localStringBuilder.append(" Endpoint: ");
    localStringBuilder.append(Log.pii(this.mEndpoint));
    localStringBuilder.append(" State: ");
    localStringBuilder.append(Connection.stateToString(this.mState));
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mHandle, 0);
    paramParcel.writeString(this.mDisplayName);
    paramParcel.writeParcelable(this.mEndpoint, 0);
    paramParcel.writeInt(this.mState);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/ConferenceParticipant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */