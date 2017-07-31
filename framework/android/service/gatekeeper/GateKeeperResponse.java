package android.service.gatekeeper;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class GateKeeperResponse
  implements Parcelable
{
  public static final Parcelable.Creator<GateKeeperResponse> CREATOR = new Parcelable.Creator()
  {
    public GateKeeperResponse createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool = true;
      int i = paramAnonymousParcel.readInt();
      GateKeeperResponse localGateKeeperResponse = new GateKeeperResponse(i, null);
      if (i == 1) {
        GateKeeperResponse.-wrap2(localGateKeeperResponse, paramAnonymousParcel.readInt());
      }
      while (i != 0) {
        return localGateKeeperResponse;
      }
      if (paramAnonymousParcel.readInt() == 1) {}
      for (;;)
      {
        GateKeeperResponse.-wrap1(localGateKeeperResponse, bool);
        i = paramAnonymousParcel.readInt();
        if (i <= 0) {
          break;
        }
        byte[] arrayOfByte = new byte[i];
        paramAnonymousParcel.readByteArray(arrayOfByte);
        GateKeeperResponse.-wrap0(localGateKeeperResponse, arrayOfByte);
        return localGateKeeperResponse;
        bool = false;
      }
    }
    
    public GateKeeperResponse[] newArray(int paramAnonymousInt)
    {
      return new GateKeeperResponse[paramAnonymousInt];
    }
  };
  public static final int RESPONSE_ERROR = -1;
  public static final int RESPONSE_OK = 0;
  public static final int RESPONSE_RETRY = 1;
  private byte[] mPayload;
  private final int mResponseCode;
  private boolean mShouldReEnroll;
  private int mTimeout;
  
  private GateKeeperResponse(int paramInt)
  {
    this.mResponseCode = paramInt;
  }
  
  private GateKeeperResponse(int paramInt1, int paramInt2)
  {
    this.mResponseCode = paramInt1;
  }
  
  private void setPayload(byte[] paramArrayOfByte)
  {
    this.mPayload = paramArrayOfByte;
  }
  
  private void setShouldReEnroll(boolean paramBoolean)
  {
    this.mShouldReEnroll = paramBoolean;
  }
  
  private void setTimeout(int paramInt)
  {
    this.mTimeout = paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public byte[] getPayload()
  {
    return this.mPayload;
  }
  
  public int getResponseCode()
  {
    return this.mResponseCode;
  }
  
  public boolean getShouldReEnroll()
  {
    return this.mShouldReEnroll;
  }
  
  public int getTimeout()
  {
    return this.mTimeout;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = 1;
    paramParcel.writeInt(this.mResponseCode);
    if (this.mResponseCode == 1) {
      paramParcel.writeInt(this.mTimeout);
    }
    while (this.mResponseCode != 0) {
      return;
    }
    if (this.mShouldReEnroll) {}
    for (;;)
    {
      paramParcel.writeInt(paramInt);
      if (this.mPayload == null) {
        break;
      }
      paramParcel.writeInt(this.mPayload.length);
      paramParcel.writeByteArray(this.mPayload);
      return;
      paramInt = 0;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/gatekeeper/GateKeeperResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */