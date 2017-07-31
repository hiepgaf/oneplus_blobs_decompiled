package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Locale;

public class WifiSsid
  implements Parcelable
{
  public static final Parcelable.Creator<WifiSsid> CREATOR = new Parcelable.Creator()
  {
    public WifiSsid createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiSsid localWifiSsid = new WifiSsid(null);
      int i = paramAnonymousParcel.readInt();
      byte[] arrayOfByte = new byte[i];
      paramAnonymousParcel.readByteArray(arrayOfByte);
      localWifiSsid.octets.write(arrayOfByte, 0, i);
      return localWifiSsid;
    }
    
    public WifiSsid[] newArray(int paramAnonymousInt)
    {
      return new WifiSsid[paramAnonymousInt];
    }
  };
  private static final int HEX_RADIX = 16;
  public static final String NONE = "<unknown ssid>";
  private static final String TAG = "WifiSsid";
  public final ByteArrayOutputStream octets = new ByteArrayOutputStream(32);
  
  private void convertToBytes(String paramString)
  {
    int i = 0;
    while (i < paramString.length())
    {
      int j = paramString.charAt(i);
      switch (j)
      {
      default: 
        this.octets.write(j);
        i += 1;
        break;
      case 92: 
        i += 1;
        int k;
        switch (paramString.charAt(i))
        {
        default: 
          break;
        case '"': 
          this.octets.write(34);
          i += 1;
          break;
        case '\\': 
          this.octets.write(92);
          i += 1;
          break;
        case 'n': 
          this.octets.write(10);
          i += 1;
          break;
        case 'r': 
          this.octets.write(13);
          i += 1;
          break;
        case 't': 
          this.octets.write(9);
          i += 1;
          break;
        case 'e': 
          this.octets.write(27);
          i += 1;
          break;
        case 'x': 
          j = i + 1;
          if (j + 2 > paramString.length()) {
            i = -1;
          }
          for (;;)
          {
            if (i >= 0) {
              break label370;
            }
            k = Character.digit(paramString.charAt(j), 16);
            i = j;
            if (k < 0) {
              break;
            }
            this.octets.write(k);
            i = j + 1;
            break;
            try
            {
              i = Integer.parseInt(paramString.substring(j, j + 2), 16);
            }
            catch (NumberFormatException localNumberFormatException)
            {
              i = -1;
            }
          }
          this.octets.write(i);
          i = j + 2;
          break;
        case '0': 
        case '1': 
        case '2': 
        case '3': 
        case '4': 
        case '5': 
        case '6': 
        case '7': 
          label370:
          k = paramString.charAt(i) - '0';
          int m = i + 1;
          i = m;
          j = k;
          if (paramString.charAt(m) >= '0')
          {
            i = m;
            j = k;
            if (paramString.charAt(m) <= '7')
            {
              j = k * 8 + paramString.charAt(m) - 48;
              i = m + 1;
            }
          }
          k = i;
          m = j;
          if (paramString.charAt(i) >= '0')
          {
            k = i;
            m = j;
            if (paramString.charAt(i) <= '7')
            {
              m = j * 8 + paramString.charAt(i) - 48;
              k = i + 1;
            }
          }
          this.octets.write(m);
          i = k;
        }
        break;
      }
    }
  }
  
  public static WifiSsid createFromAsciiEncoded(String paramString)
  {
    WifiSsid localWifiSsid = new WifiSsid();
    localWifiSsid.convertToBytes(paramString);
    return localWifiSsid;
  }
  
  public static WifiSsid createFromHex(String paramString)
  {
    WifiSsid localWifiSsid = new WifiSsid();
    if (paramString == null) {
      return localWifiSsid;
    }
    String str;
    if (!paramString.startsWith("0x"))
    {
      str = paramString;
      if (!paramString.startsWith("0X")) {}
    }
    else
    {
      str = paramString.substring(2);
    }
    int i = 0;
    for (;;)
    {
      if (i < str.length() - 1) {
        try
        {
          j = Integer.parseInt(str.substring(i, i + 2), 16);
          localWifiSsid.octets.write(j);
          i += 2;
        }
        catch (NumberFormatException paramString)
        {
          for (;;)
          {
            int j = 0;
          }
        }
      }
    }
    return localWifiSsid;
  }
  
  private boolean isArrayAllZeroes(byte[] paramArrayOfByte)
  {
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      if (paramArrayOfByte[i] != 0) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getHexString()
  {
    String str = "0x";
    byte[] arrayOfByte = getOctets();
    int i = 0;
    while (i < this.octets.size())
    {
      str = str + String.format(Locale.US, "%02x", new Object[] { Byte.valueOf(arrayOfByte[i]) });
      i += 1;
    }
    if (this.octets.size() > 0) {
      return str;
    }
    return null;
  }
  
  public byte[] getOctets()
  {
    return this.octets.toByteArray();
  }
  
  public boolean isHidden()
  {
    return isArrayAllZeroes(this.octets.toByteArray());
  }
  
  public String toString()
  {
    Object localObject = this.octets.toByteArray();
    if ((this.octets.size() <= 0) || (isArrayAllZeroes((byte[])localObject))) {
      return "";
    }
    CharsetDecoder localCharsetDecoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
    CharBuffer localCharBuffer = CharBuffer.allocate(32);
    localObject = localCharsetDecoder.decode(ByteBuffer.wrap((byte[])localObject), localCharBuffer, true);
    localCharBuffer.flip();
    if (((CoderResult)localObject).isError()) {
      return "<unknown ssid>";
    }
    return localCharBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.octets.size());
    paramParcel.writeByteArray(this.octets.toByteArray());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiSsid.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */