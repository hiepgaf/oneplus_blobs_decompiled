package android.net.dhcp;

import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.Iterator;

class DhcpAckPacket
  extends DhcpPacket
{
  private final Inet4Address mSrcIp;
  
  DhcpAckPacket(int paramInt, short paramShort, boolean paramBoolean, Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, Inet4Address paramInet4Address3, byte[] paramArrayOfByte)
  {
    super(paramInt, paramShort, paramInet4Address2, paramInet4Address3, paramInet4Address1, INADDR_ANY, paramArrayOfByte, paramBoolean);
    this.mBroadcast = paramBoolean;
    this.mSrcIp = paramInet4Address1;
  }
  
  private static final int getInt(Integer paramInteger)
  {
    if (paramInteger == null) {
      return 0;
    }
    return paramInteger.intValue();
  }
  
  public ByteBuffer buildPacket(int paramInt, short paramShort1, short paramShort2)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1500);
    Inet4Address localInet4Address1;
    if (this.mBroadcast)
    {
      localInet4Address1 = INADDR_BROADCAST;
      if (!this.mBroadcast) {
        break label68;
      }
    }
    label68:
    for (Inet4Address localInet4Address2 = INADDR_ANY;; localInet4Address2 = this.mSrcIp)
    {
      fillInPacket(paramInt, localInet4Address1, localInet4Address2, paramShort1, paramShort2, localByteBuffer, (byte)2, this.mBroadcast);
      localByteBuffer.flip();
      return localByteBuffer;
      localInet4Address1 = this.mYourIp;
      break;
    }
  }
  
  void finishPacket(ByteBuffer paramByteBuffer)
  {
    addTlv(paramByteBuffer, (byte)53, (byte)5);
    addTlv(paramByteBuffer, (byte)54, this.mServerIdentifier);
    addTlv(paramByteBuffer, (byte)51, this.mLeaseTime);
    if (this.mLeaseTime != null) {
      addTlv(paramByteBuffer, (byte)58, Integer.valueOf(this.mLeaseTime.intValue() / 2));
    }
    addTlv(paramByteBuffer, (byte)1, this.mSubnetMask);
    addTlv(paramByteBuffer, (byte)3, this.mGateways);
    addTlv(paramByteBuffer, (byte)15, this.mDomainName);
    addTlv(paramByteBuffer, (byte)28, this.mBroadcastAddress);
    addTlv(paramByteBuffer, (byte)6, this.mDnsServers);
    addTlvEnd(paramByteBuffer);
  }
  
  public String toString()
  {
    String str2 = super.toString();
    String str1 = " DNS servers: ";
    Iterator localIterator = this.mDnsServers.iterator();
    while (localIterator.hasNext())
    {
      Inet4Address localInet4Address = (Inet4Address)localIterator.next();
      str1 = str1 + localInet4Address.toString() + " ";
    }
    return str2 + " ACK: your new IP " + this.mYourIp + ", netmask " + this.mSubnetMask + ", gateways " + this.mGateways + str1 + ", lease time " + this.mLeaseTime;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/dhcp/DhcpAckPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */