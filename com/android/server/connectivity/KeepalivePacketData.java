package com.android.server.connectivity;

import android.net.util.IpUtils;
import android.system.OsConstants;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class KeepalivePacketData
{
  private static final int IPV4_HEADER_LENGTH = 20;
  private static final int UDP_HEADER_LENGTH = 8;
  public final byte[] data;
  public final InetAddress dstAddress;
  public byte[] dstMac;
  public final int dstPort;
  public final int protocol;
  public final InetAddress srcAddress;
  public final int srcPort;
  
  protected KeepalivePacketData(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2, byte[] paramArrayOfByte)
    throws KeepalivePacketData.InvalidPacketException
  {
    this.srcAddress = paramInetAddress1;
    this.dstAddress = paramInetAddress2;
    this.srcPort = paramInt1;
    this.dstPort = paramInt2;
    this.data = paramArrayOfByte;
    if ((paramInetAddress1 == null) || (paramInetAddress2 == null)) {}
    while (!paramInetAddress1.getClass().getName().equals(paramInetAddress2.getClass().getName())) {
      throw new InvalidPacketException(-21);
    }
    if ((this.dstAddress instanceof Inet4Address)) {
      this.protocol = OsConstants.ETH_P_IP;
    }
    while ((IpUtils.isValidUdpOrTcpPort(paramInt1)) && (IpUtils.isValidUdpOrTcpPort(paramInt2)))
    {
      return;
      if ((this.dstAddress instanceof Inet6Address)) {
        this.protocol = OsConstants.ETH_P_IPV6;
      } else {
        throw new InvalidPacketException(-21);
      }
    }
    throw new InvalidPacketException(-22);
  }
  
  public static KeepalivePacketData nattKeepalivePacket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2)
    throws KeepalivePacketData.InvalidPacketException
  {
    if (((paramInetAddress1 instanceof Inet4Address)) && ((paramInetAddress2 instanceof Inet4Address)))
    {
      if (paramInt2 != 4500) {
        throw new InvalidPacketException(-22);
      }
    }
    else {
      throw new InvalidPacketException(-21);
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocate(29);
    localByteBuffer.order(ByteOrder.BIG_ENDIAN);
    localByteBuffer.putShort((short)17664);
    localByteBuffer.putShort((short)29);
    localByteBuffer.putInt(0);
    localByteBuffer.put((byte)64);
    localByteBuffer.put((byte)OsConstants.IPPROTO_UDP);
    int i = localByteBuffer.position();
    localByteBuffer.putShort((short)0);
    localByteBuffer.put(paramInetAddress1.getAddress());
    localByteBuffer.put(paramInetAddress2.getAddress());
    localByteBuffer.putShort((short)paramInt1);
    localByteBuffer.putShort((short)paramInt2);
    localByteBuffer.putShort((short)9);
    int j = localByteBuffer.position();
    localByteBuffer.putShort((short)0);
    localByteBuffer.put((byte)-1);
    localByteBuffer.putShort(i, IpUtils.ipChecksum(localByteBuffer, 0));
    localByteBuffer.putShort(j, IpUtils.udpChecksum(localByteBuffer, 0, 20));
    return new KeepalivePacketData(paramInetAddress1, paramInt1, paramInetAddress2, paramInt2, localByteBuffer.array());
  }
  
  public static class InvalidPacketException
    extends Exception
  {
    public final int error;
    
    public InvalidPacketException(int paramInt)
    {
      this.error = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/KeepalivePacketData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */