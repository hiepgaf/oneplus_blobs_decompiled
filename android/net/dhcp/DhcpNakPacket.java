package android.net.dhcp;

import java.net.Inet4Address;
import java.nio.ByteBuffer;

class DhcpNakPacket
  extends DhcpPacket
{
  DhcpNakPacket(int paramInt, short paramShort, Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, Inet4Address paramInet4Address3, Inet4Address paramInet4Address4, byte[] paramArrayOfByte)
  {
    super(paramInt, paramShort, INADDR_ANY, INADDR_ANY, paramInet4Address3, paramInet4Address4, paramArrayOfByte, false);
  }
  
  public ByteBuffer buildPacket(int paramInt, short paramShort1, short paramShort2)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1500);
    fillInPacket(paramInt, this.mClientIp, this.mYourIp, paramShort1, paramShort2, localByteBuffer, (byte)2, this.mBroadcast);
    localByteBuffer.flip();
    return localByteBuffer;
  }
  
  void finishPacket(ByteBuffer paramByteBuffer)
  {
    addTlv(paramByteBuffer, (byte)53, (byte)6);
    addTlv(paramByteBuffer, (byte)54, this.mServerIdentifier);
    addTlv(paramByteBuffer, (byte)56, this.mMessage);
    addTlvEnd(paramByteBuffer);
  }
  
  public String toString()
  {
    String str = super.toString();
    StringBuilder localStringBuilder = new StringBuilder().append(str).append(" NAK, reason ");
    if (this.mMessage == null) {}
    for (str = "(none)";; str = this.mMessage) {
      return str;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/dhcp/DhcpNakPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */