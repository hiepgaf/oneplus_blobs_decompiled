package android.net.netlink;

import java.nio.ByteBuffer;

public class StructNlMsgErr
{
  public static final int STRUCT_SIZE = 20;
  public int error;
  public StructNlMsgHdr msg;
  
  public static boolean hasAvailableSpace(ByteBuffer paramByteBuffer)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramByteBuffer != null)
    {
      bool1 = bool2;
      if (paramByteBuffer.remaining() >= 20) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static StructNlMsgErr parse(ByteBuffer paramByteBuffer)
  {
    if (!hasAvailableSpace(paramByteBuffer)) {
      return null;
    }
    StructNlMsgErr localStructNlMsgErr = new StructNlMsgErr();
    localStructNlMsgErr.error = paramByteBuffer.getInt();
    localStructNlMsgErr.msg = StructNlMsgHdr.parse(paramByteBuffer);
    return localStructNlMsgErr;
  }
  
  public void pack(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.putInt(this.error);
    if (this.msg != null) {
      this.msg.pack(paramByteBuffer);
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("StructNlMsgErr{ error{").append(this.error).append("}, ").append("msg{");
    if (this.msg == null) {}
    for (String str = "";; str = this.msg.toString()) {
      return str + "} " + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/netlink/StructNlMsgErr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */