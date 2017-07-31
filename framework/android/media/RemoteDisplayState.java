package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.ArrayList;

public final class RemoteDisplayState
  implements Parcelable
{
  public static final Parcelable.Creator<RemoteDisplayState> CREATOR = new Parcelable.Creator()
  {
    public RemoteDisplayState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RemoteDisplayState(paramAnonymousParcel);
    }
    
    public RemoteDisplayState[] newArray(int paramAnonymousInt)
    {
      return new RemoteDisplayState[paramAnonymousInt];
    }
  };
  public static final int DISCOVERY_MODE_ACTIVE = 2;
  public static final int DISCOVERY_MODE_NONE = 0;
  public static final int DISCOVERY_MODE_PASSIVE = 1;
  public static final String SERVICE_INTERFACE = "com.android.media.remotedisplay.RemoteDisplayProvider";
  public final ArrayList<RemoteDisplayInfo> displays;
  
  public RemoteDisplayState()
  {
    this.displays = new ArrayList();
  }
  
  RemoteDisplayState(Parcel paramParcel)
  {
    this.displays = paramParcel.createTypedArrayList(RemoteDisplayInfo.CREATOR);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean isValid()
  {
    if (this.displays == null) {
      return false;
    }
    int j = this.displays.size();
    int i = 0;
    while (i < j)
    {
      if (!((RemoteDisplayInfo)this.displays.get(i)).isValid()) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeTypedList(this.displays);
  }
  
  public static final class RemoteDisplayInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<RemoteDisplayInfo> CREATOR = new Parcelable.Creator()
    {
      public RemoteDisplayState.RemoteDisplayInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RemoteDisplayState.RemoteDisplayInfo(paramAnonymousParcel);
      }
      
      public RemoteDisplayState.RemoteDisplayInfo[] newArray(int paramAnonymousInt)
      {
        return new RemoteDisplayState.RemoteDisplayInfo[paramAnonymousInt];
      }
    };
    public static final int PLAYBACK_VOLUME_FIXED = 0;
    public static final int PLAYBACK_VOLUME_VARIABLE = 1;
    public static final int STATUS_AVAILABLE = 2;
    public static final int STATUS_CONNECTED = 4;
    public static final int STATUS_CONNECTING = 3;
    public static final int STATUS_IN_USE = 1;
    public static final int STATUS_NOT_AVAILABLE = 0;
    public String description;
    public String id;
    public String name;
    public int presentationDisplayId;
    public int status;
    public int volume;
    public int volumeHandling;
    public int volumeMax;
    
    public RemoteDisplayInfo(RemoteDisplayInfo paramRemoteDisplayInfo)
    {
      this.id = paramRemoteDisplayInfo.id;
      this.name = paramRemoteDisplayInfo.name;
      this.description = paramRemoteDisplayInfo.description;
      this.status = paramRemoteDisplayInfo.status;
      this.volume = paramRemoteDisplayInfo.volume;
      this.volumeMax = paramRemoteDisplayInfo.volumeMax;
      this.volumeHandling = paramRemoteDisplayInfo.volumeHandling;
      this.presentationDisplayId = paramRemoteDisplayInfo.presentationDisplayId;
    }
    
    RemoteDisplayInfo(Parcel paramParcel)
    {
      this.id = paramParcel.readString();
      this.name = paramParcel.readString();
      this.description = paramParcel.readString();
      this.status = paramParcel.readInt();
      this.volume = paramParcel.readInt();
      this.volumeMax = paramParcel.readInt();
      this.volumeHandling = paramParcel.readInt();
      this.presentationDisplayId = paramParcel.readInt();
    }
    
    public RemoteDisplayInfo(String paramString)
    {
      this.id = paramString;
      this.status = 0;
      this.volumeHandling = 0;
      this.presentationDisplayId = -1;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean isValid()
    {
      return (!TextUtils.isEmpty(this.id)) && (!TextUtils.isEmpty(this.name));
    }
    
    public String toString()
    {
      return "RemoteDisplayInfo{ id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", status=" + this.status + ", volume=" + this.volume + ", volumeMax=" + this.volumeMax + ", volumeHandling=" + this.volumeHandling + ", presentationDisplayId=" + this.presentationDisplayId + " }";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.id);
      paramParcel.writeString(this.name);
      paramParcel.writeString(this.description);
      paramParcel.writeInt(this.status);
      paramParcel.writeInt(this.volume);
      paramParcel.writeInt(this.volumeMax);
      paramParcel.writeInt(this.volumeHandling);
      paramParcel.writeInt(this.presentationDisplayId);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/RemoteDisplayState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */