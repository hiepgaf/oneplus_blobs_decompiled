package android.net.dhcp;

import java.nio.ByteBuffer;

class DhcpDiscoverPacket
  extends DhcpPacket
{
  DhcpDiscoverPacket(int paramInt, short paramShort, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    super(paramInt, paramShort, INADDR_ANY, INADDR_ANY, INADDR_ANY, INADDR_ANY, paramArrayOfByte, paramBoolean);
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
    addTlv(paramByteBuffer, (byte)53, (byte)1);
    addTlv(paramByteBuffer, (byte)61, getClientId());
    addCommonClientTlvs(paramByteBuffer);
    addTlv(paramByteBuffer, (byte)55, this.mRequestedParams);
    addTlvEnd(paramByteBuffer);
  }
  
  public String toString()
  {
    String str = super.toString();
    StringBuilder localStringBuilder = new StringBuilder().append(str).append(" DISCOVER ");
    if (this.mBroadcast) {}
    for (str = "broadcast ";; str = "unicast ") {
      return str;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/dhcp/DhcpDiscoverPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */