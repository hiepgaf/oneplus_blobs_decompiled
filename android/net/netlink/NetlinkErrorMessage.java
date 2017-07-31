package android.net.netlink;

import java.nio.ByteBuffer;

public class NetlinkErrorMessage
  extends NetlinkMessage
{
  private StructNlMsgErr mNlMsgErr = null;
  
  NetlinkErrorMessage(StructNlMsgHdr paramStructNlMsgHdr)
  {
    super(paramStructNlMsgHdr);
  }
  
  public static NetlinkErrorMessage parse(StructNlMsgHdr paramStructNlMsgHdr, ByteBuffer paramByteBuffer)
  {
    paramStructNlMsgHdr = new NetlinkErrorMessage(paramStructNlMsgHdr);
    paramStructNlMsgHdr.mNlMsgErr = StructNlMsgErr.parse(paramByteBuffer);
    if (paramStructNlMsgHdr.mNlMsgErr == null) {
      return null;
    }
    return paramStructNlMsgHdr;
  }
  
  public StructNlMsgErr getNlMsgError()
  {
    return this.mNlMsgErr;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("NetlinkErrorMessage{ nlmsghdr{");
    if (this.mHeader == null)
    {
      str = "";
      localStringBuilder = localStringBuilder.append(str).append("}, ").append("nlmsgerr{");
      if (this.mNlMsgErr != null) {
        break label79;
      }
    }
    label79:
    for (String str = "";; str = this.mNlMsgErr.toString())
    {
      return str + "} " + "}";
      str = this.mHeader.toString();
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/netlink/NetlinkErrorMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */