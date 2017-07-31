package android.net.dhcp;

import java.net.Inet4Address;
import java.nio.ByteBuffer;

class DhcpRequestPacket
  extends DhcpPacket
{
  DhcpRequestPacket(int paramInt, short paramShort, Inet4Address paramInet4Address, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    super(paramInt, paramShort, paramInet4Address, INADDR_ANY, INADDR_ANY, INADDR_ANY, paramArrayOfByte, paramBoolean);
  }
  
  public ByteBuffer buildPacket(int paramInt, short paramShort1, short paramShort2)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1500);
    fillInPacket(paramInt, INADDR_BROADCAST, INADDR_ANY, paramShort1, paramShort2, localByteBuffer, (byte)1, this.mBroadcast);
    localByteBuffer.flip();
    return localByteBuffer;
  }
  
  void finishPacket(ByteBuffer paramByteBuffer)
  {
    addTlv(paramByteBuffer, (byte)53, (byte)3);
    addTlv(paramByteBuffer, (byte)61, getClientId());
    if (!INADDR_ANY.equals(this.mRequestedIp)) {
      addTlv(paramByteBuffer, (byte)50, this.mRequestedIp);
    }
    if (!INADDR_ANY.equals(this.mServerIdentifier)) {
      addTlv(paramByteBuffer, (byte)54, this.mServerIdentifier);
    }
    addCommonClientTlvs(paramByteBuffer);
    addTlv(paramByteBuffer, (byte)55, this.mRequestedParams);
    addTlvEnd(paramByteBuffer);
  }
  
  public String toString()
  {
    Object localObject = super.toString();
    localObject = new StringBuilder().append((String)localObject).append(" REQUEST, desired IP ").append(this.mRequestedIp).append(" from host '").append(this.mHostName).append("', param list length ");
    if (this.mRequestedParams == null) {}
    for (int i = 0;; i = this.mRequestedParams.length) {
      return i;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/dhcp/DhcpRequestPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */