package android.hardware.display;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class WifiDisplayStatus
  implements Parcelable
{
  public static final Parcelable.Creator<WifiDisplayStatus> CREATOR = new Parcelable.Creator()
  {
    public WifiDisplayStatus createFromParcel(Parcel paramAnonymousParcel)
    {
      int j = paramAnonymousParcel.readInt();
      int k = paramAnonymousParcel.readInt();
      int m = paramAnonymousParcel.readInt();
      WifiDisplay localWifiDisplay = null;
      if (paramAnonymousParcel.readInt() != 0) {
        localWifiDisplay = (WifiDisplay)WifiDisplay.CREATOR.createFromParcel(paramAnonymousParcel);
      }
      WifiDisplay[] arrayOfWifiDisplay = (WifiDisplay[])WifiDisplay.CREATOR.newArray(paramAnonymousParcel.readInt());
      int i = 0;
      while (i < arrayOfWifiDisplay.length)
      {
        arrayOfWifiDisplay[i] = ((WifiDisplay)WifiDisplay.CREATOR.createFromParcel(paramAnonymousParcel));
        i += 1;
      }
      return new WifiDisplayStatus(j, k, m, localWifiDisplay, arrayOfWifiDisplay, (WifiDisplaySessionInfo)WifiDisplaySessionInfo.CREATOR.createFromParcel(paramAnonymousParcel));
    }
    
    public WifiDisplayStatus[] newArray(int paramAnonymousInt)
    {
      return new WifiDisplayStatus[paramAnonymousInt];
    }
  };
  public static final int DISPLAY_STATE_CONNECTED = 2;
  public static final int DISPLAY_STATE_CONNECTING = 1;
  public static final int DISPLAY_STATE_NOT_CONNECTED = 0;
  public static final int FEATURE_STATE_DISABLED = 1;
  public static final int FEATURE_STATE_OFF = 2;
  public static final int FEATURE_STATE_ON = 3;
  public static final int FEATURE_STATE_UNAVAILABLE = 0;
  public static final int SCAN_STATE_NOT_SCANNING = 0;
  public static final int SCAN_STATE_SCANNING = 1;
  private final WifiDisplay mActiveDisplay;
  private final int mActiveDisplayState;
  private final WifiDisplay[] mDisplays;
  private final int mFeatureState;
  private final int mScanState;
  private final WifiDisplaySessionInfo mSessionInfo;
  
  public WifiDisplayStatus()
  {
    this(0, 0, 0, null, WifiDisplay.EMPTY_ARRAY, null);
  }
  
  public WifiDisplayStatus(int paramInt1, int paramInt2, int paramInt3, WifiDisplay paramWifiDisplay, WifiDisplay[] paramArrayOfWifiDisplay, WifiDisplaySessionInfo paramWifiDisplaySessionInfo)
  {
    if (paramArrayOfWifiDisplay == null) {
      throw new IllegalArgumentException("displays must not be null");
    }
    this.mFeatureState = paramInt1;
    this.mScanState = paramInt2;
    this.mActiveDisplayState = paramInt3;
    this.mActiveDisplay = paramWifiDisplay;
    this.mDisplays = paramArrayOfWifiDisplay;
    if (paramWifiDisplaySessionInfo != null) {}
    for (;;)
    {
      this.mSessionInfo = paramWifiDisplaySessionInfo;
      return;
      paramWifiDisplaySessionInfo = new WifiDisplaySessionInfo();
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public WifiDisplay getActiveDisplay()
  {
    return this.mActiveDisplay;
  }
  
  public int getActiveDisplayState()
  {
    return this.mActiveDisplayState;
  }
  
  public WifiDisplay[] getDisplays()
  {
    return this.mDisplays;
  }
  
  public int getFeatureState()
  {
    return this.mFeatureState;
  }
  
  public int getScanState()
  {
    return this.mScanState;
  }
  
  public WifiDisplaySessionInfo getSessionInfo()
  {
    return this.mSessionInfo;
  }
  
  public String toString()
  {
    return "WifiDisplayStatus{featureState=" + this.mFeatureState + ", scanState=" + this.mScanState + ", activeDisplayState=" + this.mActiveDisplayState + ", activeDisplay=" + this.mActiveDisplay + ", displays=" + Arrays.toString(this.mDisplays) + ", sessionInfo=" + this.mSessionInfo + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 0;
    paramParcel.writeInt(this.mFeatureState);
    paramParcel.writeInt(this.mScanState);
    paramParcel.writeInt(this.mActiveDisplayState);
    if (this.mActiveDisplay != null)
    {
      paramParcel.writeInt(1);
      this.mActiveDisplay.writeToParcel(paramParcel, paramInt);
    }
    for (;;)
    {
      paramParcel.writeInt(this.mDisplays.length);
      WifiDisplay[] arrayOfWifiDisplay = this.mDisplays;
      int j = arrayOfWifiDisplay.length;
      while (i < j)
      {
        arrayOfWifiDisplay[i].writeToParcel(paramParcel, paramInt);
        i += 1;
      }
      paramParcel.writeInt(0);
    }
    this.mSessionInfo.writeToParcel(paramParcel, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/WifiDisplayStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */