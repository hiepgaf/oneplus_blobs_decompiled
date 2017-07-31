package android.net.netlink;

import android.system.ErrnoException;
import android.system.NetlinkSocketAddress;
import android.system.OsConstants;
import android.system.StructTimeval;
import android.util.Log;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import libcore.io.IoUtils;
import libcore.io.Libcore;

public class NetlinkSocket
  implements Closeable
{
  private static final int DEFAULT_RECV_BUFSIZE = 8192;
  private static final int SOCKET_RECV_BUFSIZE = 65536;
  private static final String TAG = "NetlinkSocket";
  private NetlinkSocketAddress mAddr;
  private final FileDescriptor mDescriptor;
  private long mLastRecvTimeoutMs;
  private long mLastSendTimeoutMs;
  
  public NetlinkSocket(int paramInt)
    throws ErrnoException
  {
    this.mDescriptor = android.system.Os.socket(OsConstants.AF_NETLINK, OsConstants.SOCK_DGRAM, paramInt);
    Libcore.os.setsockoptInt(this.mDescriptor, OsConstants.SOL_SOCKET, OsConstants.SO_RCVBUF, 65536);
  }
  
  private void checkTimeout(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative timeouts not permitted");
    }
  }
  
  public void bind(NetlinkSocketAddress paramNetlinkSocketAddress)
    throws ErrnoException, SocketException
  {
    android.system.Os.bind(this.mDescriptor, paramNetlinkSocketAddress);
  }
  
  public void close()
  {
    IoUtils.closeQuietly(this.mDescriptor);
  }
  
  public void connectTo(NetlinkSocketAddress paramNetlinkSocketAddress)
    throws ErrnoException, SocketException
  {
    android.system.Os.connect(this.mDescriptor, paramNetlinkSocketAddress);
  }
  
  public void connectToKernel()
    throws ErrnoException, SocketException
  {
    connectTo(new NetlinkSocketAddress(0, 0));
  }
  
  public NetlinkSocketAddress getLocalAddress()
    throws ErrnoException
  {
    return (NetlinkSocketAddress)android.system.Os.getsockname(this.mDescriptor);
  }
  
  public ByteBuffer recvMessage()
    throws ErrnoException, InterruptedIOException
  {
    return recvMessage(8192, 0L);
  }
  
  public ByteBuffer recvMessage(int paramInt, long paramLong)
    throws ErrnoException, IllegalArgumentException, InterruptedIOException
  {
    checkTimeout(paramLong);
    synchronized (this.mDescriptor)
    {
      if (this.mLastRecvTimeoutMs != paramLong)
      {
        android.system.Os.setsockoptTimeval(this.mDescriptor, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, StructTimeval.fromMillis(paramLong));
        this.mLastRecvTimeoutMs = paramLong;
      }
      ??? = ByteBuffer.allocate(paramInt);
      int i = android.system.Os.read(this.mDescriptor, (ByteBuffer)???);
      if (i == paramInt) {
        Log.w("NetlinkSocket", "maximum read");
      }
      ((ByteBuffer)???).position(0);
      ((ByteBuffer)???).limit(i);
      ((ByteBuffer)???).order(ByteOrder.nativeOrder());
      return (ByteBuffer)???;
    }
  }
  
  public ByteBuffer recvMessage(long paramLong)
    throws ErrnoException, InterruptedIOException
  {
    return recvMessage(8192, paramLong);
  }
  
  public boolean sendMessage(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ErrnoException, InterruptedIOException
  {
    return sendMessage(paramArrayOfByte, paramInt1, paramInt2, 0L);
  }
  
  public boolean sendMessage(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong)
    throws ErrnoException, IllegalArgumentException, InterruptedIOException
  {
    checkTimeout(paramLong);
    synchronized (this.mDescriptor)
    {
      if (this.mLastSendTimeoutMs != paramLong)
      {
        android.system.Os.setsockoptTimeval(this.mDescriptor, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO, StructTimeval.fromMillis(paramLong));
        this.mLastSendTimeoutMs = paramLong;
      }
      if (paramInt2 == android.system.Os.write(this.mDescriptor, paramArrayOfByte, paramInt1, paramInt2)) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/netlink/NetlinkSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */