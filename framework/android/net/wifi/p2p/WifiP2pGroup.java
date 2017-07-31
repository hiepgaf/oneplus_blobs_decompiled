package android.net.wifi.p2p;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiP2pGroup
  implements Parcelable
{
  public static final Parcelable.Creator<WifiP2pGroup> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pGroup createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiP2pGroup localWifiP2pGroup = new WifiP2pGroup();
      localWifiP2pGroup.setNetworkName(paramAnonymousParcel.readString());
      localWifiP2pGroup.setOwner((WifiP2pDevice)paramAnonymousParcel.readParcelable(null));
      if (paramAnonymousParcel.readByte() == 1) {}
      for (boolean bool = true;; bool = false)
      {
        localWifiP2pGroup.setIsGroupOwner(bool);
        int j = paramAnonymousParcel.readInt();
        int i = 0;
        while (i < j)
        {
          localWifiP2pGroup.addClient((WifiP2pDevice)paramAnonymousParcel.readParcelable(null));
          i += 1;
        }
      }
      localWifiP2pGroup.setPassphrase(paramAnonymousParcel.readString());
      localWifiP2pGroup.setInterface(paramAnonymousParcel.readString());
      localWifiP2pGroup.setNetworkId(paramAnonymousParcel.readInt());
      return localWifiP2pGroup;
    }
    
    public WifiP2pGroup[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pGroup[paramAnonymousInt];
    }
  };
  public static final int PERSISTENT_NET_ID = -2;
  public static final int TEMPORARY_NET_ID = -1;
  private static final Pattern groupStartedPattern = Pattern.compile("ssid=\"(.+)\" freq=(\\d+) (?:psk=)?([0-9a-fA-F]{64})?(?:passphrase=)?(?:\"(.{0,63})\")? go_dev_addr=((?:[0-9a-f]{2}:){5}[0-9a-f]{2}) ?(\\[PERSISTENT\\])?");
  private List<WifiP2pDevice> mClients = new ArrayList();
  private String mInterface;
  private boolean mIsGroupOwner;
  private int mNetId;
  private String mNetworkName;
  private WifiP2pDevice mOwner;
  private String mPassphrase;
  
  public WifiP2pGroup() {}
  
  public WifiP2pGroup(WifiP2pGroup paramWifiP2pGroup)
  {
    if (paramWifiP2pGroup != null)
    {
      this.mNetworkName = paramWifiP2pGroup.getNetworkName();
      this.mOwner = new WifiP2pDevice(paramWifiP2pGroup.getOwner());
      this.mIsGroupOwner = paramWifiP2pGroup.mIsGroupOwner;
      Iterator localIterator = paramWifiP2pGroup.getClientList().iterator();
      while (localIterator.hasNext())
      {
        WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)localIterator.next();
        this.mClients.add(localWifiP2pDevice);
      }
      this.mPassphrase = paramWifiP2pGroup.getPassphrase();
      this.mInterface = paramWifiP2pGroup.getInterface();
      this.mNetId = paramWifiP2pGroup.getNetworkId();
    }
  }
  
  public WifiP2pGroup(String paramString)
    throws IllegalArgumentException
  {
    String[] arrayOfString = paramString.split(" ");
    if (arrayOfString.length < 3) {
      throw new IllegalArgumentException("Malformed supplicant event");
    }
    if (arrayOfString[0].startsWith("P2P-GROUP"))
    {
      this.mInterface = arrayOfString[1];
      this.mIsGroupOwner = arrayOfString[2].equals("GO");
      paramString = groupStartedPattern.matcher(paramString);
      if (!paramString.find()) {
        return;
      }
      this.mNetworkName = paramString.group(1);
      this.mPassphrase = paramString.group(4);
      this.mOwner = new WifiP2pDevice(paramString.group(5));
      if (paramString.group(6) != null)
      {
        this.mNetId = -2;
        return;
      }
      this.mNetId = -1;
      return;
    }
    if (arrayOfString[0].equals("P2P-INVITATION-RECEIVED"))
    {
      this.mNetId = -2;
      int j = arrayOfString.length;
      int i = 0;
      label169:
      if (i < j)
      {
        paramString = arrayOfString[i].split("=");
        if (paramString.length == 2) {
          break label197;
        }
      }
      for (;;)
      {
        i += 1;
        break label169;
        break;
        label197:
        if (paramString[0].equals("sa"))
        {
          WifiP2pDevice localWifiP2pDevice = paramString[1];
          localWifiP2pDevice = new WifiP2pDevice();
          localWifiP2pDevice.deviceAddress = paramString[1];
          this.mClients.add(localWifiP2pDevice);
        }
        else if (paramString[0].equals("go_dev_addr"))
        {
          this.mOwner = new WifiP2pDevice(paramString[1]);
        }
        else if (paramString[0].equals("persistent"))
        {
          this.mNetId = Integer.parseInt(paramString[1]);
        }
      }
    }
    throw new IllegalArgumentException("Malformed supplicant event");
  }
  
  public void addClient(WifiP2pDevice paramWifiP2pDevice)
  {
    Iterator localIterator = this.mClients.iterator();
    while (localIterator.hasNext()) {
      if (((WifiP2pDevice)localIterator.next()).equals(paramWifiP2pDevice)) {
        return;
      }
    }
    this.mClients.add(paramWifiP2pDevice);
  }
  
  public void addClient(String paramString)
  {
    addClient(new WifiP2pDevice(paramString));
  }
  
  public boolean contains(WifiP2pDevice paramWifiP2pDevice)
  {
    return (this.mOwner.equals(paramWifiP2pDevice)) || (this.mClients.contains(paramWifiP2pDevice));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Collection<WifiP2pDevice> getClientList()
  {
    return Collections.unmodifiableCollection(this.mClients);
  }
  
  public String getInterface()
  {
    return this.mInterface;
  }
  
  public int getNetworkId()
  {
    return this.mNetId;
  }
  
  public String getNetworkName()
  {
    return this.mNetworkName;
  }
  
  public WifiP2pDevice getOwner()
  {
    return this.mOwner;
  }
  
  public String getPassphrase()
  {
    return this.mPassphrase;
  }
  
  public boolean isClientListEmpty()
  {
    boolean bool = false;
    if (this.mClients.size() == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isGroupOwner()
  {
    return this.mIsGroupOwner;
  }
  
  public boolean removeClient(WifiP2pDevice paramWifiP2pDevice)
  {
    return this.mClients.remove(paramWifiP2pDevice);
  }
  
  public boolean removeClient(String paramString)
  {
    return this.mClients.remove(new WifiP2pDevice(paramString));
  }
  
  public void setInterface(String paramString)
  {
    this.mInterface = paramString;
  }
  
  public void setIsGroupOwner(boolean paramBoolean)
  {
    this.mIsGroupOwner = paramBoolean;
  }
  
  public void setNetworkId(int paramInt)
  {
    this.mNetId = paramInt;
  }
  
  public void setNetworkName(String paramString)
  {
    this.mNetworkName = paramString;
  }
  
  public void setOwner(WifiP2pDevice paramWifiP2pDevice)
  {
    this.mOwner = paramWifiP2pDevice;
  }
  
  public void setPassphrase(String paramString)
  {
    this.mPassphrase = paramString;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("network: ").append(this.mNetworkName);
    localStringBuffer.append("\n isGO: ").append(this.mIsGroupOwner);
    localStringBuffer.append("\n GO: ").append(this.mOwner);
    Iterator localIterator = this.mClients.iterator();
    while (localIterator.hasNext())
    {
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)localIterator.next();
      localStringBuffer.append("\n Client: ").append(localWifiP2pDevice);
    }
    localStringBuffer.append("\n interface: ").append(this.mInterface);
    localStringBuffer.append("\n networkId: ").append(this.mNetId);
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mNetworkName);
    paramParcel.writeParcelable(this.mOwner, paramInt);
    if (this.mIsGroupOwner) {}
    for (byte b = 1;; b = 0)
    {
      paramParcel.writeByte(b);
      paramParcel.writeInt(this.mClients.size());
      Iterator localIterator = this.mClients.iterator();
      while (localIterator.hasNext()) {
        paramParcel.writeParcelable((WifiP2pDevice)localIterator.next(), paramInt);
      }
    }
    paramParcel.writeString(this.mPassphrase);
    paramParcel.writeString(this.mInterface);
    paramParcel.writeInt(this.mNetId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/WifiP2pGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */