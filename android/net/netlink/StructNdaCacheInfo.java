package android.net.netlink;

import android.system.Os;
import android.system.OsConstants;
import java.nio.ByteBuffer;

public class StructNdaCacheInfo
{
  private static final long CLOCK_TICKS_PER_SECOND = Os.sysconf(OsConstants._SC_CLK_TCK);
  public static final int STRUCT_SIZE = 16;
  public int ndm_confirmed;
  public int ndm_refcnt;
  public int ndm_updated;
  public int ndm_used;
  
  private static boolean hasAvailableSpace(ByteBuffer paramByteBuffer)
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
  
  public static StructNdaCacheInfo parse(ByteBuffer paramByteBuffer)
  {
    if (!hasAvailableSpace(paramByteBuffer)) {
      return null;
    }
    StructNdaCacheInfo localStructNdaCacheInfo = new StructNdaCacheInfo();
    localStructNdaCacheInfo.ndm_used = paramByteBuffer.getInt();
    localStructNdaCacheInfo.ndm_confirmed = paramByteBuffer.getInt();
    localStructNdaCacheInfo.ndm_updated = paramByteBuffer.getInt();
    localStructNdaCacheInfo.ndm_refcnt = paramByteBuffer.getInt();
    return localStructNdaCacheInfo;
  }
  
  private static long ticksToMilliSeconds(int paramInt)
  {
    return 1000L * (paramInt & 0xFFFFFFFFFFFFFFFF) / CLOCK_TICKS_PER_SECOND;
  }
  
  public long lastConfirmed()
  {
    return ticksToMilliSeconds(this.ndm_confirmed);
  }
  
  public long lastUpdated()
  {
    return ticksToMilliSeconds(this.ndm_updated);
  }
  
  public long lastUsed()
  {
    return ticksToMilliSeconds(this.ndm_used);
  }
  
  public String toString()
  {
    return "NdaCacheInfo{ ndm_used{" + lastUsed() + "}, " + "ndm_confirmed{" + lastConfirmed() + "}, " + "ndm_updated{" + lastUpdated() + "}, " + "ndm_refcnt{" + this.ndm_refcnt + "} " + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/netlink/StructNdaCacheInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */