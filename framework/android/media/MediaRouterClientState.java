package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public final class MediaRouterClientState
  implements Parcelable
{
  public static final Parcelable.Creator<MediaRouterClientState> CREATOR = new Parcelable.Creator()
  {
    public MediaRouterClientState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MediaRouterClientState(paramAnonymousParcel);
    }
    
    public MediaRouterClientState[] newArray(int paramAnonymousInt)
    {
      return new MediaRouterClientState[paramAnonymousInt];
    }
  };
  public String globallySelectedRouteId;
  public final ArrayList<RouteInfo> routes;
  
  public MediaRouterClientState()
  {
    this.routes = new ArrayList();
  }
  
  MediaRouterClientState(Parcel paramParcel)
  {
    this.routes = paramParcel.createTypedArrayList(RouteInfo.CREATOR);
    this.globallySelectedRouteId = paramParcel.readString();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public RouteInfo getRoute(String paramString)
  {
    int j = this.routes.size();
    int i = 0;
    while (i < j)
    {
      RouteInfo localRouteInfo = (RouteInfo)this.routes.get(i);
      if (localRouteInfo.id.equals(paramString)) {
        return localRouteInfo;
      }
      i += 1;
    }
    return null;
  }
  
  public String toString()
  {
    return "MediaRouterClientState{ globallySelectedRouteId=" + this.globallySelectedRouteId + ", routes=" + this.routes.toString() + " }";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeTypedList(this.routes);
    paramParcel.writeString(this.globallySelectedRouteId);
  }
  
  public static final class RouteInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<RouteInfo> CREATOR = new Parcelable.Creator()
    {
      public MediaRouterClientState.RouteInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaRouterClientState.RouteInfo(paramAnonymousParcel);
      }
      
      public MediaRouterClientState.RouteInfo[] newArray(int paramAnonymousInt)
      {
        return new MediaRouterClientState.RouteInfo[paramAnonymousInt];
      }
    };
    public String description;
    public int deviceType;
    public boolean enabled;
    public String id;
    public String name;
    public int playbackStream;
    public int playbackType;
    public int presentationDisplayId;
    public int statusCode;
    public int supportedTypes;
    public int volume;
    public int volumeHandling;
    public int volumeMax;
    
    public RouteInfo(RouteInfo paramRouteInfo)
    {
      this.id = paramRouteInfo.id;
      this.name = paramRouteInfo.name;
      this.description = paramRouteInfo.description;
      this.supportedTypes = paramRouteInfo.supportedTypes;
      this.enabled = paramRouteInfo.enabled;
      this.statusCode = paramRouteInfo.statusCode;
      this.playbackType = paramRouteInfo.playbackType;
      this.playbackStream = paramRouteInfo.playbackStream;
      this.volume = paramRouteInfo.volume;
      this.volumeMax = paramRouteInfo.volumeMax;
      this.volumeHandling = paramRouteInfo.volumeHandling;
      this.presentationDisplayId = paramRouteInfo.presentationDisplayId;
      this.deviceType = paramRouteInfo.deviceType;
    }
    
    RouteInfo(Parcel paramParcel)
    {
      this.id = paramParcel.readString();
      this.name = paramParcel.readString();
      this.description = paramParcel.readString();
      this.supportedTypes = paramParcel.readInt();
      if (paramParcel.readInt() != 0) {
        bool = true;
      }
      this.enabled = bool;
      this.statusCode = paramParcel.readInt();
      this.playbackType = paramParcel.readInt();
      this.playbackStream = paramParcel.readInt();
      this.volume = paramParcel.readInt();
      this.volumeMax = paramParcel.readInt();
      this.volumeHandling = paramParcel.readInt();
      this.presentationDisplayId = paramParcel.readInt();
      this.deviceType = paramParcel.readInt();
    }
    
    public RouteInfo(String paramString)
    {
      this.id = paramString;
      this.enabled = true;
      this.statusCode = 0;
      this.playbackType = 1;
      this.playbackStream = -1;
      this.volumeHandling = 0;
      this.presentationDisplayId = -1;
      this.deviceType = 0;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      return "RouteInfo{ id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", supportedTypes=0x" + Integer.toHexString(this.supportedTypes) + ", enabled=" + this.enabled + ", statusCode=" + this.statusCode + ", playbackType=" + this.playbackType + ", playbackStream=" + this.playbackStream + ", volume=" + this.volume + ", volumeMax=" + this.volumeMax + ", volumeHandling=" + this.volumeHandling + ", presentationDisplayId=" + this.presentationDisplayId + ", deviceType=" + this.deviceType + " }";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.id);
      paramParcel.writeString(this.name);
      paramParcel.writeString(this.description);
      paramParcel.writeInt(this.supportedTypes);
      if (this.enabled) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(this.statusCode);
        paramParcel.writeInt(this.playbackType);
        paramParcel.writeInt(this.playbackStream);
        paramParcel.writeInt(this.volume);
        paramParcel.writeInt(this.volumeMax);
        paramParcel.writeInt(this.volumeHandling);
        paramParcel.writeInt(this.presentationDisplayId);
        paramParcel.writeInt(this.deviceType);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaRouterClientState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */