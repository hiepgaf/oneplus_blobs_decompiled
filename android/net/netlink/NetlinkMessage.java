package android.net.netlink;

import java.nio.ByteBuffer;

public class NetlinkMessage
{
  private static final String TAG = "NetlinkMessage";
  protected StructNlMsgHdr mHeader;
  
  public NetlinkMessage(StructNlMsgHdr paramStructNlMsgHdr)
  {
    this.mHeader = paramStructNlMsgHdr;
  }
  
  public static NetlinkMessage parse(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer != null) {
      paramByteBuffer.position();
    }
    StructNlMsgHdr localStructNlMsgHdr;
    for (;;)
    {
      localStructNlMsgHdr = StructNlMsgHdr.parse(paramByteBuffer);
      if (localStructNlMsgHdr != null) {
        break;
      }
      return null;
    }
    int i = NetlinkConstants.alignedLengthOf(localStructNlMsgHdr.nlmsg_len) - 16;
    if ((i < 0) || (i > paramByteBuffer.remaining()))
    {
      paramByteBuffer.position(paramByteBuffer.limit());
      return null;
    }
    switch (localStructNlMsgHdr.nlmsg_type)
    {
    default: 
      if (localStructNlMsgHdr.nlmsg_type <= 15)
      {
        paramByteBuffer.position(paramByteBuffer.position() + i);
        return new NetlinkMessage(localStructNlMsgHdr);
      }
      break;
    case 2: 
      return NetlinkErrorMessage.parse(localStructNlMsgHdr, paramByteBuffer);
    case 3: 
      paramByteBuffer.position(paramByteBuffer.position() + i);
      return new NetlinkMessage(localStructNlMsgHdr);
    case 28: 
    case 29: 
    case 30: 
      return RtNetlinkNeighborMessage.parse(localStructNlMsgHdr, paramByteBuffer);
    }
    return null;
  }
  
  public StructNlMsgHdr getHeader()
  {
    return this.mHeader;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("NetlinkMessage{");
    if (this.mHeader == null) {}
    for (String str = "";; str = this.mHeader.toString()) {
      return str + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/netlink/NetlinkMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */