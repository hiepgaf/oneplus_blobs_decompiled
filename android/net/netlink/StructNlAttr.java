package android.net.netlink;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StructNlAttr
{
  public static final int NLA_HEADERLEN = 4;
  public ByteOrder mByteOrder = ByteOrder.nativeOrder();
  public short nla_len;
  public short nla_type;
  public byte[] nla_value;
  
  public static StructNlAttr parse(ByteBuffer paramByteBuffer)
  {
    StructNlAttr localStructNlAttr = peek(paramByteBuffer);
    if ((localStructNlAttr == null) || (paramByteBuffer.remaining() < localStructNlAttr.getAlignedLength())) {
      return null;
    }
    int i = paramByteBuffer.position();
    paramByteBuffer.position(i + 4);
    int j = (localStructNlAttr.nla_len & 0xFFFF) - 4;
    if (j > 0)
    {
      localStructNlAttr.nla_value = new byte[j];
      paramByteBuffer.get(localStructNlAttr.nla_value, 0, j);
      paramByteBuffer.position(localStructNlAttr.getAlignedLength() + i);
    }
    return localStructNlAttr;
  }
  
  public static StructNlAttr peek(ByteBuffer paramByteBuffer)
  {
    if ((paramByteBuffer == null) || (paramByteBuffer.remaining() < 4)) {
      return null;
    }
    int i = paramByteBuffer.position();
    StructNlAttr localStructNlAttr = new StructNlAttr();
    localStructNlAttr.nla_len = paramByteBuffer.getShort();
    localStructNlAttr.nla_type = paramByteBuffer.getShort();
    localStructNlAttr.mByteOrder = paramByteBuffer.order();
    paramByteBuffer.position(i);
    if (localStructNlAttr.nla_len < 4) {
      return null;
    }
    return localStructNlAttr;
  }
  
  public int getAlignedLength()
  {
    return NetlinkConstants.alignedLengthOf(this.nla_len);
  }
  
  public ByteBuffer getValueAsByteBuffer()
  {
    if (this.nla_value == null) {
      return null;
    }
    ByteBuffer localByteBuffer = ByteBuffer.wrap(this.nla_value);
    localByteBuffer.order(this.mByteOrder);
    return localByteBuffer;
  }
  
  public InetAddress getValueAsInetAddress()
  {
    if (this.nla_value == null) {
      return null;
    }
    try
    {
      InetAddress localInetAddress = InetAddress.getByAddress(this.nla_value);
      return localInetAddress;
    }
    catch (UnknownHostException localUnknownHostException) {}
    return null;
  }
  
  public int getValueAsInt(int paramInt)
  {
    ByteBuffer localByteBuffer = getValueAsByteBuffer();
    if ((localByteBuffer == null) || (localByteBuffer.remaining() != 4)) {
      return paramInt;
    }
    return getValueAsByteBuffer().getInt();
  }
  
  public void pack(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.position();
    paramByteBuffer.putShort(this.nla_len);
    paramByteBuffer.putShort(this.nla_type);
    paramByteBuffer.put(this.nla_value);
    paramByteBuffer.position(getAlignedLength() + i);
  }
  
  public String toString()
  {
    return "StructNlAttr{ nla_len{" + this.nla_len + "}, " + "nla_type{" + this.nla_type + "}, " + "nla_value{" + NetlinkConstants.hexify(this.nla_value) + "}, " + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/netlink/StructNlAttr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */