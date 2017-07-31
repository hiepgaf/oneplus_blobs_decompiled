package android.net.wifi.p2p;

public class WifiP2pProvDiscEvent
{
  public static final int ENTER_PIN = 3;
  public static final int PBC_REQ = 1;
  public static final int PBC_RSP = 2;
  public static final int SHOW_PIN = 4;
  private static final String TAG = "WifiP2pProvDiscEvent";
  public WifiP2pDevice device;
  public int event;
  public String pin;
  
  public WifiP2pProvDiscEvent()
  {
    this.device = new WifiP2pDevice();
  }
  
  public WifiP2pProvDiscEvent(String paramString)
    throws IllegalArgumentException
  {
    String[] arrayOfString = paramString.split(" ");
    if (arrayOfString.length < 2) {
      throw new IllegalArgumentException("Malformed event " + paramString);
    }
    if (arrayOfString[0].endsWith("PBC-REQ")) {
      this.event = 1;
    }
    for (;;)
    {
      this.device = new WifiP2pDevice();
      this.device.deviceAddress = arrayOfString[1];
      if (this.event == 4) {
        this.pin = arrayOfString[2];
      }
      return;
      if (arrayOfString[0].endsWith("PBC-RESP"))
      {
        this.event = 2;
      }
      else if (arrayOfString[0].endsWith("ENTER-PIN"))
      {
        this.event = 3;
      }
      else
      {
        if (!arrayOfString[0].endsWith("SHOW-PIN")) {
          break;
        }
        this.event = 4;
      }
    }
    throw new IllegalArgumentException("Malformed event " + paramString);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(this.device);
    localStringBuffer.append("\n event: ").append(this.event);
    localStringBuffer.append("\n pin: ").append(this.pin);
    return localStringBuffer.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/WifiP2pProvDiscEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */