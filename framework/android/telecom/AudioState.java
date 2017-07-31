package android.telecom;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Locale;

@Deprecated
public class AudioState
  implements Parcelable
{
  public static final Parcelable.Creator<AudioState> CREATOR = new Parcelable.Creator()
  {
    public AudioState createFromParcel(Parcel paramAnonymousParcel)
    {
      if (paramAnonymousParcel.readByte() == 0) {}
      for (boolean bool = false;; bool = true) {
        return new AudioState(bool, paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
      }
    }
    
    public AudioState[] newArray(int paramAnonymousInt)
    {
      return new AudioState[paramAnonymousInt];
    }
  };
  private static final int ROUTE_ALL = 15;
  public static final int ROUTE_BLUETOOTH = 2;
  public static final int ROUTE_EARPIECE = 1;
  public static final int ROUTE_SPEAKER = 8;
  public static final int ROUTE_WIRED_HEADSET = 4;
  public static final int ROUTE_WIRED_OR_EARPIECE = 5;
  private final boolean isMuted;
  private final int route;
  private final int supportedRouteMask;
  
  public AudioState(AudioState paramAudioState)
  {
    this.isMuted = paramAudioState.isMuted();
    this.route = paramAudioState.getRoute();
    this.supportedRouteMask = paramAudioState.getSupportedRouteMask();
  }
  
  public AudioState(CallAudioState paramCallAudioState)
  {
    this.isMuted = paramCallAudioState.isMuted();
    this.route = paramCallAudioState.getRoute();
    this.supportedRouteMask = paramCallAudioState.getSupportedRouteMask();
  }
  
  public AudioState(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this.isMuted = paramBoolean;
    this.route = paramInt1;
    this.supportedRouteMask = paramInt2;
  }
  
  public static String audioRouteToString(int paramInt)
  {
    if ((paramInt == 0) || ((paramInt & 0xFFFFFFF0) != 0)) {
      return "UNKNOWN";
    }
    StringBuffer localStringBuffer = new StringBuffer();
    if ((paramInt & 0x1) == 1) {
      listAppend(localStringBuffer, "EARPIECE");
    }
    if ((paramInt & 0x2) == 2) {
      listAppend(localStringBuffer, "BLUETOOTH");
    }
    if ((paramInt & 0x4) == 4) {
      listAppend(localStringBuffer, "WIRED_HEADSET");
    }
    if ((paramInt & 0x8) == 8) {
      listAppend(localStringBuffer, "SPEAKER");
    }
    return localStringBuffer.toString();
  }
  
  private static void listAppend(StringBuffer paramStringBuffer, String paramString)
  {
    if (paramStringBuffer.length() > 0) {
      paramStringBuffer.append(", ");
    }
    paramStringBuffer.append(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof AudioState)) {
      return false;
    }
    paramObject = (AudioState)paramObject;
    boolean bool1 = bool2;
    if (isMuted() == ((AudioState)paramObject).isMuted())
    {
      bool1 = bool2;
      if (getRoute() == ((AudioState)paramObject).getRoute())
      {
        bool1 = bool2;
        if (getSupportedRouteMask() == ((AudioState)paramObject).getSupportedRouteMask()) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public int getRoute()
  {
    return this.route;
  }
  
  public int getSupportedRouteMask()
  {
    return this.supportedRouteMask;
  }
  
  public boolean isMuted()
  {
    return this.isMuted;
  }
  
  public String toString()
  {
    return String.format(Locale.US, "[AudioState isMuted: %b, route: %s, supportedRouteMask: %s]", new Object[] { Boolean.valueOf(this.isMuted), audioRouteToString(this.route), audioRouteToString(this.supportedRouteMask) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.isMuted) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeInt(this.route);
      paramParcel.writeInt(this.supportedRouteMask);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/AudioState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */