package android.net.netlink;

import java.nio.ByteBuffer;

public class StructNlMsgHdr
{
  public static final short NLM_F_ACK = 4;
  public static final short NLM_F_APPEND = 2048;
  public static final short NLM_F_CREATE = 1024;
  public static final short NLM_F_DUMP = 768;
  public static final short NLM_F_ECHO = 8;
  public static final short NLM_F_EXCL = 512;
  public static final short NLM_F_MATCH = 512;
  public static final short NLM_F_MULTI = 2;
  public static final short NLM_F_REPLACE = 256;
  public static final short NLM_F_REQUEST = 1;
  public static final short NLM_F_ROOT = 256;
  public static final int STRUCT_SIZE = 16;
  public short nlmsg_flags = 0;
  public int nlmsg_len = 0;
  public int nlmsg_pid = 0;
  public int nlmsg_seq = 0;
  public short nlmsg_type = 0;
  
  public static boolean hasAvailableSpace(ByteBuffer paramByteBuffer)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramByteBuffer != null)
    {
      bool1 = bool2;
      if (paramByteBuffer.remaining() >= 16) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static StructNlMsgHdr parse(ByteBuffer paramByteBuffer)
  {
    if (!hasAvailableSpace(paramByteBuffer)) {
      return null;
    }
    StructNlMsgHdr localStructNlMsgHdr = new StructNlMsgHdr();
    localStructNlMsgHdr.nlmsg_len = paramByteBuffer.getInt();
    localStructNlMsgHdr.nlmsg_type = paramByteBuffer.getShort();
    localStructNlMsgHdr.nlmsg_flags = paramByteBuffer.getShort();
    localStructNlMsgHdr.nlmsg_seq = paramByteBuffer.getInt();
    localStructNlMsgHdr.nlmsg_pid = paramByteBuffer.getInt();
    if (localStructNlMsgHdr.nlmsg_len < 16) {
      return null;
    }
    return localStructNlMsgHdr;
  }
  
  public static String stringForNlMsgFlags(short paramShort)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramShort & 0x1) != 0) {
      localStringBuilder.append("NLM_F_REQUEST");
    }
    if ((paramShort & 0x2) != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("NLM_F_MULTI");
    }
    if ((paramShort & 0x4) != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("NLM_F_ACK");
    }
    if ((paramShort & 0x8) != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("NLM_F_ECHO");
    }
    if ((paramShort & 0x100) != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("NLM_F_ROOT");
    }
    if ((paramShort & 0x200) != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("NLM_F_MATCH");
    }
    return localStringBuilder.toString();
  }
  
  public void pack(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.putInt(this.nlmsg_len);
    paramByteBuffer.putShort(this.nlmsg_type);
    paramByteBuffer.putShort(this.nlmsg_flags);
    paramByteBuffer.putInt(this.nlmsg_seq);
    paramByteBuffer.putInt(this.nlmsg_pid);
  }
  
  public String toString()
  {
    String str1 = "" + this.nlmsg_type + "(" + NetlinkConstants.stringForNlMsgType(this.nlmsg_type) + ")";
    String str2 = "" + this.nlmsg_flags + "(" + stringForNlMsgFlags(this.nlmsg_flags) + ")";
    return "StructNlMsgHdr{ nlmsg_len{" + this.nlmsg_len + "}, " + "nlmsg_type{" + str1 + "}, " + "nlmsg_flags{" + str2 + ")}, " + "nlmsg_seq{" + this.nlmsg_seq + "}, " + "nlmsg_pid{" + this.nlmsg_pid + "} " + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/netlink/StructNlMsgHdr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */