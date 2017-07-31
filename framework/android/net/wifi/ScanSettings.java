package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ScanSettings
  implements Parcelable
{
  public static final Parcelable.Creator<ScanSettings> CREATOR = new Parcelable.Creator()
  {
    public ScanSettings createFromParcel(Parcel paramAnonymousParcel)
    {
      ScanSettings localScanSettings = new ScanSettings();
      int i = paramAnonymousParcel.readInt();
      if (i > 0)
      {
        localScanSettings.channelSet = new ArrayList(i);
        while (i > 0)
        {
          localScanSettings.channelSet.add((WifiChannel)WifiChannel.CREATOR.createFromParcel(paramAnonymousParcel));
          i -= 1;
        }
      }
      return localScanSettings;
    }
    
    public ScanSettings[] newArray(int paramAnonymousInt)
    {
      return new ScanSettings[paramAnonymousInt];
    }
  };
  public Collection<WifiChannel> channelSet;
  
  public ScanSettings() {}
  
  public ScanSettings(ScanSettings paramScanSettings)
  {
    if (paramScanSettings.channelSet != null) {
      this.channelSet = new ArrayList(paramScanSettings.channelSet);
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean isValid()
  {
    Iterator localIterator = this.channelSet.iterator();
    while (localIterator.hasNext()) {
      if (!((WifiChannel)localIterator.next()).isValid()) {
        return false;
      }
    }
    return true;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.channelSet == null) {}
    for (int i = 0;; i = this.channelSet.size())
    {
      paramParcel.writeInt(i);
      if (this.channelSet == null) {
        break;
      }
      Iterator localIterator = this.channelSet.iterator();
      while (localIterator.hasNext()) {
        ((WifiChannel)localIterator.next()).writeToParcel(paramParcel, paramInt);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/ScanSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */