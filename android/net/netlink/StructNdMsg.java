package android.net.netlink;

import android.system.OsConstants;
import java.nio.ByteBuffer;

public class StructNdMsg
{
  public static byte NTF_MASTER = 4;
  public static byte NTF_PROXY = 8;
  public static byte NTF_ROUTER = Byte.MIN_VALUE;
  public static byte NTF_SELF = 0;
  public static byte NTF_USE = 1;
  public static final short NUD_DELAY = 8;
  public static final short NUD_FAILED = 32;
  public static final short NUD_INCOMPLETE = 1;
  public static final short NUD_NOARP = 64;
  public static final short NUD_NONE = 0;
  public static final short NUD_PERMANENT = 128;
  public static final short NUD_PROBE = 16;
  public static final short NUD_REACHABLE = 2;
  public static final short NUD_STALE = 4;
  public static final int STRUCT_SIZE = 12;
  public byte ndm_family = (byte)OsConstants.AF_UNSPEC;
  public byte ndm_flags;
  public int ndm_ifindex;
  public short ndm_state;
  public byte ndm_type;
  
  static
  {
    NTF_SELF = 2;
  }
  
  private static boolean hasAvailableSpace(ByteBuffer paramByteBuffer)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramByteBuffer != null)
    {
      bool1 = bool2;
      if (paramByteBuffer.remaining() >= 12) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isNudStateConnected(short paramShort)
  {
    boolean bool = false;
    if ((paramShort & 0xC2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static StructNdMsg parse(ByteBuffer paramByteBuffer)
  {
    if (!hasAvailableSpace(paramByteBuffer)) {
      return null;
    }
    StructNdMsg localStructNdMsg = new StructNdMsg();
    localStructNdMsg.ndm_family = paramByteBuffer.get();
    paramByteBuffer.get();
    paramByteBuffer.getShort();
    localStructNdMsg.ndm_ifindex = paramByteBuffer.getInt();
    localStructNdMsg.ndm_state = paramByteBuffer.getShort();
    localStructNdMsg.ndm_flags = paramByteBuffer.get();
    localStructNdMsg.ndm_type = paramByteBuffer.get();
    return localStructNdMsg;
  }
  
  public static String stringForNudFlags(byte paramByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((NTF_USE & paramByte) != 0) {
      localStringBuilder.append("NTF_USE");
    }
    if ((NTF_SELF & paramByte) != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("NTF_SELF");
    }
    if ((NTF_MASTER & paramByte) != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("NTF_MASTER");
    }
    if ((NTF_PROXY & paramByte) != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("NTF_PROXY");
    }
    if ((NTF_ROUTER & paramByte) != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("|");
      }
      localStringBuilder.append("NTF_ROUTER");
    }
    return localStringBuilder.toString();
  }
  
  public static String stringForNudState(short paramShort)
  {
    switch (paramShort)
    {
    default: 
      return "unknown NUD state: " + String.valueOf(paramShort);
    case 0: 
      return "NUD_NONE";
    case 1: 
      return "NUD_INCOMPLETE";
    case 2: 
      return "NUD_REACHABLE";
    case 4: 
      return "NUD_STALE";
    case 8: 
      return "NUD_DELAY";
    case 16: 
      return "NUD_PROBE";
    case 32: 
      return "NUD_FAILED";
    case 64: 
      return "NUD_NOARP";
    }
    return "NUD_PERMANENT";
  }
  
  public boolean nudConnected()
  {
    return isNudStateConnected(this.ndm_state);
  }
  
  public boolean nudValid()
  {
    return (nudConnected()) || ((this.ndm_state & 0x1C) != 0);
  }
  
  public void pack(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(this.ndm_family);
    paramByteBuffer.put((byte)0);
    paramByteBuffer.putShort((short)0);
    paramByteBuffer.putInt(this.ndm_ifindex);
    paramByteBuffer.putShort(this.ndm_state);
    paramByteBuffer.put(this.ndm_flags);
    paramByteBuffer.put(this.ndm_type);
  }
  
  public String toString()
  {
    String str1 = "" + this.ndm_state + " (" + stringForNudState(this.ndm_state) + ")";
    String str2 = "" + this.ndm_flags + " (" + stringForNudFlags(this.ndm_flags) + ")";
    return "StructNdMsg{ family{" + NetlinkConstants.stringForAddressFamily(this.ndm_family) + "}, " + "ifindex{" + this.ndm_ifindex + "}, " + "state{" + str1 + "}, " + "flags{" + str2 + "}, " + "type{" + this.ndm_type + "} " + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/netlink/StructNdMsg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */