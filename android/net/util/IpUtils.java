package android.net.util;

import android.system.OsConstants;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class IpUtils
{
  public static String addressAndPortToString(InetAddress paramInetAddress, int paramInt)
  {
    if ((paramInetAddress instanceof Inet6Address)) {}
    for (String str = "[%s]:%d";; str = "%s:%d") {
      return String.format(str, new Object[] { paramInetAddress.getHostAddress(), Integer.valueOf(paramInt) });
    }
  }
  
  private static int checksum(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramByteBuffer.position();
    paramByteBuffer.position(paramInt2);
    ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
    paramByteBuffer.position(i);
    int j = (paramInt3 - paramInt2) / 2;
    i = 0;
    while (i < j)
    {
      paramInt1 += intAbs(localShortBuffer.get(i));
      i += 1;
    }
    i = paramInt2 + j * 2;
    paramInt2 = paramInt1;
    if (paramInt3 != i)
    {
      paramInt3 = (short)paramByteBuffer.get(i);
      paramInt2 = paramInt3;
      if (paramInt3 < 0) {
        paramInt2 = (short)(paramInt3 + 256);
      }
      paramInt2 = paramInt1 + paramInt2 * 256;
    }
    paramInt1 = (paramInt2 >> 16 & 0xFFFF) + (paramInt2 & 0xFFFF);
    return intAbs((short)((paramInt1 >> 16 & 0xFFFF) + paramInt1 & 0xFFFF));
  }
  
  private static int intAbs(short paramShort)
  {
    return 0xFFFF & paramShort;
  }
  
  public static short ipChecksum(ByteBuffer paramByteBuffer, int paramInt)
  {
    return (short)checksum(paramByteBuffer, 0, paramInt, (byte)(paramByteBuffer.get(paramInt) & 0xF) * 4 + paramInt);
  }
  
  private static byte ipversion(ByteBuffer paramByteBuffer, int paramInt)
  {
    return (byte)((paramByteBuffer.get(paramInt) & 0xFFFFFFF0) >> 4);
  }
  
  public static boolean isValidUdpOrTcpPort(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt > 0)
    {
      bool1 = bool2;
      if (paramInt < 65536) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static int pseudoChecksumIPv4(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt2 + paramInt3 + intAbs(paramByteBuffer.getShort(paramInt1 + 12)) + intAbs(paramByteBuffer.getShort(paramInt1 + 14)) + intAbs(paramByteBuffer.getShort(paramInt1 + 16)) + intAbs(paramByteBuffer.getShort(paramInt1 + 18));
  }
  
  private static int pseudoChecksumIPv6(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt3 = paramInt2 + paramInt3;
    paramInt2 = 8;
    while (paramInt2 < 40)
    {
      paramInt3 += intAbs(paramByteBuffer.getShort(paramInt1 + paramInt2));
      paramInt2 += 2;
    }
    return paramInt3;
  }
  
  public static short tcpChecksum(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    return transportChecksum(paramByteBuffer, OsConstants.IPPROTO_TCP, paramInt1, paramInt2, paramInt3);
  }
  
  private static short transportChecksum(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt4 < 0) {
      throw new IllegalArgumentException("Transport length < 0: " + paramInt4);
    }
    int i = ipversion(paramByteBuffer, paramInt2);
    if (i == 4) {}
    for (paramInt2 = pseudoChecksumIPv4(paramByteBuffer, paramInt2, paramInt1, paramInt4);; paramInt2 = pseudoChecksumIPv6(paramByteBuffer, paramInt2, paramInt1, paramInt4))
    {
      paramInt3 = checksum(paramByteBuffer, paramInt2, paramInt3, paramInt3 + paramInt4);
      paramInt2 = paramInt3;
      if (paramInt1 == OsConstants.IPPROTO_UDP)
      {
        paramInt2 = paramInt3;
        if (paramInt3 == 0) {
          paramInt2 = -1;
        }
      }
      return (short)paramInt2;
      if (i != 6) {
        break;
      }
    }
    throw new UnsupportedOperationException("Checksum must be IPv4 or IPv6");
  }
  
  public static short udpChecksum(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    int i = intAbs(paramByteBuffer.getShort(paramInt2 + 4));
    return transportChecksum(paramByteBuffer, OsConstants.IPPROTO_UDP, paramInt1, paramInt2, i);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/util/IpUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */