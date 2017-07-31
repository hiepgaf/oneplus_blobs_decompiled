package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class BluetoothAvrcpPlayerSettings
  implements Parcelable
{
  public static final Parcelable.Creator<BluetoothAvrcpPlayerSettings> CREATOR = new Parcelable.Creator()
  {
    public BluetoothAvrcpPlayerSettings createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothAvrcpPlayerSettings(paramAnonymousParcel, null);
    }
    
    public BluetoothAvrcpPlayerSettings[] newArray(int paramAnonymousInt)
    {
      return new BluetoothAvrcpPlayerSettings[paramAnonymousInt];
    }
  };
  public static final int SETTING_EQUALIZER = 1;
  public static final int SETTING_REPEAT = 2;
  public static final int SETTING_SCAN = 8;
  public static final int SETTING_SHUFFLE = 4;
  public static final int STATE_ALL_TRACK = 3;
  public static final int STATE_GROUP = 4;
  public static final int STATE_INVALID = -1;
  public static final int STATE_OFF = 0;
  public static final int STATE_ON = 1;
  public static final int STATE_SINGLE_TRACK = 2;
  public static final String TAG = "BluetoothAvrcpPlayerSettings";
  private int mSettings;
  private Map<Integer, Integer> mSettingsValue = new HashMap();
  
  public BluetoothAvrcpPlayerSettings(int paramInt)
  {
    this.mSettings = paramInt;
  }
  
  private BluetoothAvrcpPlayerSettings(Parcel paramParcel)
  {
    this.mSettings = paramParcel.readInt();
    int j = paramParcel.readInt();
    int i = 0;
    while (i < j)
    {
      this.mSettingsValue.put(Integer.valueOf(paramParcel.readInt()), Integer.valueOf(paramParcel.readInt()));
      i += 1;
    }
  }
  
  public void addSettingValue(int paramInt1, int paramInt2)
  {
    if ((this.mSettings & paramInt1) == 0)
    {
      Log.e("BluetoothAvrcpPlayerSettings", "Setting not supported: " + paramInt1 + " " + this.mSettings);
      throw new IllegalStateException("Setting not supported: " + paramInt1);
    }
    this.mSettingsValue.put(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getSettingValue(int paramInt)
  {
    if ((this.mSettings & paramInt) == 0)
    {
      Log.e("BluetoothAvrcpPlayerSettings", "Setting not supported: " + paramInt + " " + this.mSettings);
      throw new IllegalStateException("Setting not supported: " + paramInt);
    }
    Integer localInteger = (Integer)this.mSettingsValue.get(Integer.valueOf(paramInt));
    if (localInteger == null) {
      return -1;
    }
    return localInteger.intValue();
  }
  
  public int getSettings()
  {
    return this.mSettings;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSettings);
    paramParcel.writeInt(this.mSettingsValue.size());
    Iterator localIterator = this.mSettingsValue.keySet().iterator();
    while (localIterator.hasNext())
    {
      paramInt = ((Integer)localIterator.next()).intValue();
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(((Integer)this.mSettingsValue.get(Integer.valueOf(paramInt))).intValue());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothAvrcpPlayerSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */