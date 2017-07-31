package android.hardware.display;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class WifiDisplaySessionInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WifiDisplaySessionInfo> CREATOR = new Parcelable.Creator()
  {
    public WifiDisplaySessionInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      if (paramAnonymousParcel.readInt() != 0) {}
      for (boolean bool = true;; bool = false) {
        return new WifiDisplaySessionInfo(bool, paramAnonymousParcel.readInt(), paramAnonymousParcel.readString(), paramAnonymousParcel.readString(), paramAnonymousParcel.readString());
      }
    }
    
    public WifiDisplaySessionInfo[] newArray(int paramAnonymousInt)
    {
      return new WifiDisplaySessionInfo[paramAnonymousInt];
    }
  };
  private final boolean mClient;
  private final String mGroupId;
  private final String mIP;
  private final String mPassphrase;
  private final int mSessionId;
  
  public WifiDisplaySessionInfo()
  {
    this(true, 0, "", "", "");
  }
  
  public WifiDisplaySessionInfo(boolean paramBoolean, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.mClient = paramBoolean;
    this.mSessionId = paramInt;
    this.mGroupId = paramString1;
    this.mPassphrase = paramString2;
    this.mIP = paramString3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getGroupId()
  {
    return this.mGroupId;
  }
  
  public String getIP()
  {
    return this.mIP;
  }
  
  public String getPassphrase()
  {
    return this.mPassphrase;
  }
  
  public int getSessionId()
  {
    return this.mSessionId;
  }
  
  public boolean isClient()
  {
    return this.mClient;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("WifiDisplaySessionInfo:\n    Client/Owner: ");
    if (this.mClient) {}
    for (String str = "Client";; str = "Owner") {
      return str + "\n    GroupId: " + this.mGroupId + "\n    Passphrase: " + this.mPassphrase + "\n    SessionId: " + this.mSessionId + "\n    IP Address: " + this.mIP;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mClient) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.mSessionId);
      paramParcel.writeString(this.mGroupId);
      paramParcel.writeString(this.mPassphrase);
      paramParcel.writeString(this.mIP);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/WifiDisplaySessionInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */