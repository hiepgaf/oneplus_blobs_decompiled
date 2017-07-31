package android.os;

public class BatteryProperties
  implements Parcelable
{
  public static final Parcelable.Creator<BatteryProperties> CREATOR = new Parcelable.Creator()
  {
    public BatteryProperties createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BatteryProperties(paramAnonymousParcel, null);
    }
    
    public BatteryProperties[] newArray(int paramAnonymousInt)
    {
      return new BatteryProperties[paramAnonymousInt];
    }
  };
  public int batteryChargeCounter;
  public int batteryHealth;
  public int batteryLevel;
  public boolean batteryPresent;
  public int batteryStatus;
  public String batteryTechnology;
  public int batteryTemperature;
  public int batteryVoltage;
  public boolean chargerAcOnline;
  public boolean chargerUsbOnline;
  public boolean chargerWirelessOnline;
  public int maxChargingCurrent;
  public int maxChargingVoltage;
  
  public BatteryProperties() {}
  
  private BatteryProperties(Parcel paramParcel)
  {
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.chargerAcOnline = bool1;
      if (paramParcel.readInt() != 1) {
        break label144;
      }
      bool1 = true;
      label31:
      this.chargerUsbOnline = bool1;
      if (paramParcel.readInt() != 1) {
        break label149;
      }
      bool1 = true;
      label46:
      this.chargerWirelessOnline = bool1;
      this.maxChargingCurrent = paramParcel.readInt();
      this.maxChargingVoltage = paramParcel.readInt();
      this.batteryStatus = paramParcel.readInt();
      this.batteryHealth = paramParcel.readInt();
      if (paramParcel.readInt() != 1) {
        break label154;
      }
    }
    label144:
    label149:
    label154:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.batteryPresent = bool1;
      this.batteryLevel = paramParcel.readInt();
      this.batteryVoltage = paramParcel.readInt();
      this.batteryTemperature = paramParcel.readInt();
      this.batteryChargeCounter = paramParcel.readInt();
      this.batteryTechnology = paramParcel.readString();
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label31;
      bool1 = false;
      break label46;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void set(BatteryProperties paramBatteryProperties)
  {
    this.chargerAcOnline = paramBatteryProperties.chargerAcOnline;
    this.chargerUsbOnline = paramBatteryProperties.chargerUsbOnline;
    this.chargerWirelessOnline = paramBatteryProperties.chargerWirelessOnline;
    this.maxChargingCurrent = paramBatteryProperties.maxChargingCurrent;
    this.maxChargingVoltage = paramBatteryProperties.maxChargingVoltage;
    this.batteryStatus = paramBatteryProperties.batteryStatus;
    this.batteryHealth = paramBatteryProperties.batteryHealth;
    this.batteryPresent = paramBatteryProperties.batteryPresent;
    this.batteryLevel = paramBatteryProperties.batteryLevel;
    this.batteryVoltage = paramBatteryProperties.batteryVoltage;
    this.batteryTemperature = paramBatteryProperties.batteryTemperature;
    this.batteryChargeCounter = paramBatteryProperties.batteryChargeCounter;
    this.batteryTechnology = paramBatteryProperties.batteryTechnology;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    if (this.chargerAcOnline)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.chargerUsbOnline) {
        break label136;
      }
      paramInt = 1;
      label25:
      paramParcel.writeInt(paramInt);
      if (!this.chargerWirelessOnline) {
        break label141;
      }
      paramInt = 1;
      label39:
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.maxChargingCurrent);
      paramParcel.writeInt(this.maxChargingVoltage);
      paramParcel.writeInt(this.batteryStatus);
      paramParcel.writeInt(this.batteryHealth);
      if (!this.batteryPresent) {
        break label146;
      }
    }
    label136:
    label141:
    label146:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.batteryLevel);
      paramParcel.writeInt(this.batteryVoltage);
      paramParcel.writeInt(this.batteryTemperature);
      paramParcel.writeInt(this.batteryChargeCounter);
      paramParcel.writeString(this.batteryTechnology);
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label25;
      paramInt = 0;
      break label39;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/BatteryProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */