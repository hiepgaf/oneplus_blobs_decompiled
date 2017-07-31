package android.net.dhcp;

import java.net.Inet4Address;
import java.nio.ByteBuffer;

class DhcpInformPacket
  extends DhcpPacket
{
  DhcpInformPacket(int paramInt, short paramShort, Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, Inet4Address paramInet4Address3, Inet4Address paramInet4Address4, byte[] paramArrayOfByte)
  {
    super(paramInt, paramShort, paramInet4Address1, paramInet4Address2, paramInet4Address3, paramInet4Address4, paramArrayOfByte, false);
  }
  
  public ByteBuffer buildPacket(int paramInt, short paramShort1, short paramShort2)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1500);
    fillInPacket(paramInt, this.mClientIp, this.mYourIp, paramShort1, paramShort2, localByteBuffer, (byte)1, false);
    localByteBuffer.flip();
    return localByteBuffer;
  }
  
  void finishPacket(ByteBuffer paramByteBuffer)
  {
    addTlv(paramByteBuffer, (byte)53, (byte)8);
    addTlv(paramByteBuffer, (byte)61, getClientId());
    addCommonClientTlvs(paramByteBuffer);
    addTlv(paramByteBuffer, (byte)55, this.mRequestedParams);
    addTlvEnd(paramByteBuffer);
  }
  
  public String toString()
  {
    String str = super.toString();
    return str + " INFORM";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/dhcp/DhcpInformPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */