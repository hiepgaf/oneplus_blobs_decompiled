package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class AudioRoutesInfo
  implements Parcelable
{
  public static final Parcelable.Creator<AudioRoutesInfo> CREATOR = new Parcelable.Creator()
  {
    public AudioRoutesInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AudioRoutesInfo(paramAnonymousParcel);
    }
    
    public AudioRoutesInfo[] newArray(int paramAnonymousInt)
    {
      return new AudioRoutesInfo[paramAnonymousInt];
    }
  };
  public static final int MAIN_DOCK_SPEAKERS = 4;
  public static final int MAIN_HDMI = 8;
  public static final int MAIN_HEADPHONES = 2;
  public static final int MAIN_HEADSET = 1;
  public static final int MAIN_SPEAKER = 0;
  public static final int MAIN_USB = 16;
  public CharSequence bluetoothName;
  public int mainType = 0;
  
  public AudioRoutesInfo() {}
  
  public AudioRoutesInfo(AudioRoutesInfo paramAudioRoutesInfo)
  {
    this.bluetoothName = paramAudioRoutesInfo.bluetoothName;
    this.mainType = paramAudioRoutesInfo.mainType;
  }
  
  AudioRoutesInfo(Parcel paramParcel)
  {
    this.bluetoothName = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mainType = paramParcel.readInt();
  }
  
  private static String typeToString(int paramInt)
  {
    if (paramInt == 0) {
      return "SPEAKER";
    }
    if ((paramInt & 0x1) != 0) {
      return "HEADSET";
    }
    if ((paramInt & 0x2) != 0) {
      return "HEADPHONES";
    }
    if ((paramInt & 0x4) != 0) {
      return "DOCK_SPEAKERS";
    }
    if ((paramInt & 0x8) != 0) {
      return "HDMI";
    }
    if ((paramInt & 0x10) != 0) {
      return "USB";
    }
    return Integer.toHexString(paramInt);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append(getClass().getSimpleName()).append("{ type=").append(typeToString(this.mainType));
    if (TextUtils.isEmpty(this.bluetoothName)) {}
    for (String str = "";; str = ", bluetoothName=" + this.bluetoothName) {
      return str + " }";
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    TextUtils.writeToParcel(this.bluetoothName, paramParcel, paramInt);
    paramParcel.writeInt(this.mainType);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioRoutesInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */