package android.net;

import android.os.Parcel;
import android.util.Log;
import android.util.Pair;
import java.io.FileDescriptor;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

public class NetworkUtils
{
  private static final String TAG = "NetworkUtils";
  
  public static boolean addressTypeMatches(InetAddress paramInetAddress1, InetAddress paramInetAddress2)
  {
    if ((!(paramInetAddress1 instanceof Inet4Address)) || (!(paramInetAddress2 instanceof Inet4Address)))
    {
      if ((paramInetAddress1 instanceof Inet6Address)) {
        return paramInetAddress2 instanceof Inet6Address;
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  public static native void attachDhcpFilter(FileDescriptor paramFileDescriptor)
    throws SocketException;
  
  public static native void attachRaFilter(FileDescriptor paramFileDescriptor, int paramInt)
    throws SocketException;
  
  public static native boolean bindProcessToNetwork(int paramInt);
  
  public static native boolean bindProcessToNetworkForHostResolution(int paramInt);
  
  public static native int bindSocketToNetwork(int paramInt1, int paramInt2);
  
  public static native int enableInterface(String paramString);
  
  public static native int getBoundNetworkForProcess();
  
  public static int getImplicitNetmask(Inet4Address paramInet4Address)
  {
    int i = paramInet4Address.getAddress()[0] & 0xFF;
    if (i < 128) {
      return 8;
    }
    if (i < 192) {
      return 16;
    }
    if (i < 224) {
      return 24;
    }
    return 32;
  }
  
  public static InetAddress getNetworkPart(InetAddress paramInetAddress, int paramInt)
  {
    paramInetAddress = paramInetAddress.getAddress();
    maskRawAddress(paramInetAddress, paramInt);
    try
    {
      paramInetAddress = InetAddress.getByAddress(paramInetAddress);
      return paramInetAddress;
    }
    catch (UnknownHostException paramInetAddress)
    {
      throw new RuntimeException("getNetworkPart error - " + paramInetAddress.toString());
    }
  }
  
  public static InetAddress hexToInet6Address(String paramString)
    throws IllegalArgumentException
  {
    try
    {
      InetAddress localInetAddress = numericToInetAddress(String.format(Locale.US, "%s:%s:%s:%s:%s:%s:%s:%s", new Object[] { paramString.substring(0, 4), paramString.substring(4, 8), paramString.substring(8, 12), paramString.substring(12, 16), paramString.substring(16, 20), paramString.substring(20, 24), paramString.substring(24, 28), paramString.substring(28, 32) }));
      return localInetAddress;
    }
    catch (Exception localException)
    {
      Log.e("NetworkUtils", "error in hexToInet6Address(" + paramString + "): " + localException);
      throw new IllegalArgumentException(localException);
    }
  }
  
  public static int inetAddressToInt(Inet4Address paramInet4Address)
    throws IllegalArgumentException
  {
    paramInet4Address = paramInet4Address.getAddress();
    return (paramInet4Address[3] & 0xFF) << 24 | (paramInet4Address[2] & 0xFF) << 16 | (paramInet4Address[1] & 0xFF) << 8 | paramInet4Address[0] & 0xFF;
  }
  
  public static InetAddress intToInetAddress(int paramInt)
  {
    int i = (byte)(paramInt & 0xFF);
    int j = (byte)(paramInt >> 8 & 0xFF);
    int k = (byte)(paramInt >> 16 & 0xFF);
    int m = (byte)(paramInt >> 24 & 0xFF);
    try
    {
      InetAddress localInetAddress = InetAddress.getByAddress(new byte[] { i, j, k, m });
      return localInetAddress;
    }
    catch (UnknownHostException localUnknownHostException)
    {
      throw new AssertionError();
    }
  }
  
  public static String[] makeStrings(Collection<InetAddress> paramCollection)
  {
    String[] arrayOfString = new String[paramCollection.size()];
    int i = 0;
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      arrayOfString[i] = ((InetAddress)paramCollection.next()).getHostAddress();
      i += 1;
    }
    return arrayOfString;
  }
  
  public static void maskRawAddress(byte[] paramArrayOfByte, int paramInt)
  {
    if ((paramInt < 0) || (paramInt > paramArrayOfByte.length * 8)) {
      throw new RuntimeException("IP address with " + paramArrayOfByte.length + " bytes has invalid prefix length " + paramInt);
    }
    int i = paramInt / 8;
    paramInt = (byte)(255 << 8 - paramInt % 8);
    if (i < paramArrayOfByte.length) {
      paramArrayOfByte[i] = ((byte)(paramArrayOfByte[i] & paramInt));
    }
    paramInt = i + 1;
    while (paramInt < paramArrayOfByte.length)
    {
      paramArrayOfByte[paramInt] = 0;
      paramInt += 1;
    }
  }
  
  public static int netmaskIntToPrefixLength(int paramInt)
  {
    return Integer.bitCount(paramInt);
  }
  
  public static int netmaskToPrefixLength(Inet4Address paramInet4Address)
  {
    int i = Integer.reverseBytes(inetAddressToInt(paramInet4Address));
    int j = Integer.bitCount(i);
    if (Integer.numberOfTrailingZeros(i) != 32 - j) {
      throw new IllegalArgumentException("Non-contiguous netmask: " + Integer.toHexString(i));
    }
    return j;
  }
  
  public static InetAddress numericToInetAddress(String paramString)
    throws IllegalArgumentException
  {
    return InetAddress.parseNumericAddress(paramString);
  }
  
  protected static void parcelInetAddress(Parcel paramParcel, InetAddress paramInetAddress, int paramInt)
  {
    byte[] arrayOfByte = null;
    if (paramInetAddress != null) {
      arrayOfByte = paramInetAddress.getAddress();
    }
    paramParcel.writeByteArray(arrayOfByte);
  }
  
  public static Pair<InetAddress, Integer> parseIpAndMask(String paramString)
  {
    Object localObject1 = null;
    i = -1;
    j = i;
    k = i;
    m = i;
    n = i;
    try
    {
      Object localObject2 = paramString.split("/", 2);
      j = i;
      k = i;
      m = i;
      n = i;
      i = Integer.parseInt(localObject2[1]);
      j = i;
      k = i;
      m = i;
      n = i;
      localObject2 = InetAddress.parseNumericAddress(localObject2[0]);
      localObject1 = localObject2;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        i = j;
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      for (;;)
      {
        i = k;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;)
      {
        i = m;
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;)
      {
        i = n;
      }
    }
    if ((localObject1 == null) || (i == -1)) {
      throw new IllegalArgumentException("Invalid IP address and mask " + paramString);
    }
    return new Pair(localObject1, Integer.valueOf(i));
  }
  
  public static int prefixLengthToNetmaskInt(int paramInt)
    throws IllegalArgumentException
  {
    if ((paramInt < 0) || (paramInt > 32)) {
      throw new IllegalArgumentException("Invalid prefix length (0 <= prefix <= 32)");
    }
    return Integer.reverseBytes(-1 << 32 - paramInt);
  }
  
  public static native boolean protectFromVpn(int paramInt);
  
  public static boolean protectFromVpn(FileDescriptor paramFileDescriptor)
  {
    return protectFromVpn(paramFileDescriptor.getInt$());
  }
  
  public static native boolean queryUserAccess(int paramInt1, int paramInt2);
  
  public static native void setupRaSocket(FileDescriptor paramFileDescriptor, int paramInt)
    throws SocketException;
  
  public static String trimV4AddrZeros(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    String[] arrayOfString = paramString.split("\\.");
    if (arrayOfString.length != 4) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder(16);
    int i = 0;
    while (i < 4) {
      try
      {
        if (arrayOfString[i].length() > 3) {
          return paramString;
        }
        localStringBuilder.append(Integer.parseInt(arrayOfString[i]));
        if (i < 3) {
          localStringBuilder.append('.');
        }
        i += 1;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return paramString;
      }
    }
    return localStringBuilder.toString();
  }
  
  protected static InetAddress unparcelInetAddress(Parcel paramParcel)
  {
    paramParcel = paramParcel.createByteArray();
    if (paramParcel == null) {
      return null;
    }
    try
    {
      paramParcel = InetAddress.getByAddress(paramParcel);
      return paramParcel;
    }
    catch (UnknownHostException paramParcel) {}
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */