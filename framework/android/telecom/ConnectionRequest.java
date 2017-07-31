package android.telecom;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ConnectionRequest
  implements Parcelable
{
  public static final Parcelable.Creator<ConnectionRequest> CREATOR = new Parcelable.Creator()
  {
    public ConnectionRequest createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ConnectionRequest(paramAnonymousParcel, null);
    }
    
    public ConnectionRequest[] newArray(int paramAnonymousInt)
    {
      return new ConnectionRequest[paramAnonymousInt];
    }
  };
  private PhoneAccountHandle mAccountHandle;
  private final Uri mAddress;
  private final Bundle mExtras;
  private final String mTelecomCallId;
  private final int mVideoState;
  
  private ConnectionRequest(Parcel paramParcel)
  {
    this.mAccountHandle = ((PhoneAccountHandle)paramParcel.readParcelable(getClass().getClassLoader()));
    this.mAddress = ((Uri)paramParcel.readParcelable(getClass().getClassLoader()));
    this.mExtras = ((Bundle)paramParcel.readParcelable(getClass().getClassLoader()));
    this.mVideoState = paramParcel.readInt();
    this.mTelecomCallId = paramParcel.readString();
  }
  
  public ConnectionRequest(PhoneAccountHandle paramPhoneAccountHandle, Uri paramUri, Bundle paramBundle)
  {
    this(paramPhoneAccountHandle, paramUri, paramBundle, 0, null);
  }
  
  public ConnectionRequest(PhoneAccountHandle paramPhoneAccountHandle, Uri paramUri, Bundle paramBundle, int paramInt)
  {
    this(paramPhoneAccountHandle, paramUri, paramBundle, paramInt, null);
  }
  
  public ConnectionRequest(PhoneAccountHandle paramPhoneAccountHandle, Uri paramUri, Bundle paramBundle, int paramInt, String paramString)
  {
    this.mAccountHandle = paramPhoneAccountHandle;
    this.mAddress = paramUri;
    this.mExtras = paramBundle;
    this.mVideoState = paramInt;
    this.mTelecomCallId = paramString;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public PhoneAccountHandle getAccountHandle()
  {
    return this.mAccountHandle;
  }
  
  public Uri getAddress()
  {
    return this.mAddress;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public String getTelecomCallId()
  {
    return this.mTelecomCallId;
  }
  
  public int getVideoState()
  {
    return this.mVideoState;
  }
  
  public void setAccountHandle(PhoneAccountHandle paramPhoneAccountHandle)
  {
    this.mAccountHandle = paramPhoneAccountHandle;
  }
  
  public String toString()
  {
    Object localObject1;
    if (this.mAddress == null)
    {
      localObject1 = Uri.EMPTY;
      if (this.mExtras != null) {
        break label53;
      }
    }
    label53:
    for (Object localObject2 = "";; localObject2 = this.mExtras)
    {
      return String.format("ConnectionRequest %s %s", new Object[] { localObject1, localObject2 });
      localObject1 = Connection.toLogSafePhoneNumber(this.mAddress.toString());
      break;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mAccountHandle, 0);
    paramParcel.writeParcelable(this.mAddress, 0);
    paramParcel.writeParcelable(this.mExtras, 0);
    paramParcel.writeInt(this.mVideoState);
    paramParcel.writeString(this.mTelecomCallId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/ConnectionRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */