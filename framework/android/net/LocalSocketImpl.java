package android.net;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructLinger;
import android.system.StructTimeval;
import android.util.MutableInt;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class LocalSocketImpl
{
  private FileDescriptor fd;
  private SocketInputStream fis;
  private SocketOutputStream fos;
  FileDescriptor[] inboundFileDescriptors;
  private boolean mFdCreatedInternally;
  FileDescriptor[] outboundFileDescriptors;
  private Object readMonitor = new Object();
  private Object writeMonitor = new Object();
  
  LocalSocketImpl() {}
  
  LocalSocketImpl(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    this.fd = paramFileDescriptor;
  }
  
  private native void bindLocal(FileDescriptor paramFileDescriptor, String paramString, int paramInt)
    throws IOException;
  
  private native void connectLocal(FileDescriptor paramFileDescriptor, String paramString, int paramInt)
    throws IOException;
  
  private native Credentials getPeerCredentials_native(FileDescriptor paramFileDescriptor)
    throws IOException;
  
  private static int javaSoToOsOpt(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new UnsupportedOperationException("Unknown option: " + paramInt);
    case 4097: 
      return OsConstants.SO_SNDBUF;
    case 4098: 
      return OsConstants.SO_RCVBUF;
    }
    return OsConstants.SO_REUSEADDR;
  }
  
  private native int read_native(FileDescriptor paramFileDescriptor)
    throws IOException;
  
  private native int readba_native(byte[] paramArrayOfByte, int paramInt1, int paramInt2, FileDescriptor paramFileDescriptor)
    throws IOException;
  
  private native void write_native(int paramInt, FileDescriptor paramFileDescriptor)
    throws IOException;
  
  private native void writeba_native(byte[] paramArrayOfByte, int paramInt1, int paramInt2, FileDescriptor paramFileDescriptor)
    throws IOException;
  
  protected void accept(LocalSocketImpl paramLocalSocketImpl)
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    try
    {
      paramLocalSocketImpl.fd = Os.accept(this.fd, null);
      paramLocalSocketImpl.mFdCreatedInternally = true;
      return;
    }
    catch (ErrnoException paramLocalSocketImpl)
    {
      throw paramLocalSocketImpl.rethrowAsIOException();
    }
  }
  
  protected int available()
    throws IOException
  {
    return getInputStream().available();
  }
  
  public void bind(LocalSocketAddress paramLocalSocketAddress)
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    bindLocal(this.fd, paramLocalSocketAddress.getName(), paramLocalSocketAddress.getNamespace().getId());
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 28	android/net/LocalSocketImpl:fd	Ljava/io/FileDescriptor;
    //   6: ifnull +10 -> 16
    //   9: aload_0
    //   10: getfield 122	android/net/LocalSocketImpl:mFdCreatedInternally	Z
    //   13: ifne +11 -> 24
    //   16: aload_0
    //   17: aconst_null
    //   18: putfield 28	android/net/LocalSocketImpl:fd	Ljava/io/FileDescriptor;
    //   21: aload_0
    //   22: monitorexit
    //   23: return
    //   24: aload_0
    //   25: getfield 28	android/net/LocalSocketImpl:fd	Ljava/io/FileDescriptor;
    //   28: invokestatic 157	android/system/Os:close	(Ljava/io/FileDescriptor;)V
    //   31: aload_0
    //   32: aconst_null
    //   33: putfield 28	android/net/LocalSocketImpl:fd	Ljava/io/FileDescriptor;
    //   36: aload_0
    //   37: monitorexit
    //   38: return
    //   39: astore_1
    //   40: aload_1
    //   41: invokevirtual 126	android/system/ErrnoException:rethrowAsIOException	()Ljava/io/IOException;
    //   44: pop
    //   45: goto -14 -> 31
    //   48: astore_1
    //   49: aload_0
    //   50: monitorexit
    //   51: aload_1
    //   52: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	53	0	this	LocalSocketImpl
    //   39	2	1	localErrnoException	ErrnoException
    //   48	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   24	31	39	android/system/ErrnoException
    //   2	16	48	finally
    //   16	21	48	finally
    //   24	31	48	finally
    //   31	36	48	finally
    //   40	45	48	finally
  }
  
  protected void connect(LocalSocketAddress paramLocalSocketAddress, int paramInt)
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    connectLocal(this.fd, paramLocalSocketAddress.getName(), paramLocalSocketAddress.getNamespace().getId());
  }
  
  public void create(int paramInt)
    throws IOException
  {
    if (this.fd == null) {
      switch (paramInt)
      {
      default: 
        throw new IllegalStateException("unknown sockType");
      case 1: 
        paramInt = OsConstants.SOCK_DGRAM;
      }
    }
    for (;;)
    {
      try
      {
        this.fd = Os.socket(OsConstants.AF_UNIX, paramInt, 0);
        this.mFdCreatedInternally = true;
        return;
      }
      catch (ErrnoException localErrnoException)
      {
        localErrnoException.rethrowAsIOException();
      }
      paramInt = OsConstants.SOCK_STREAM;
      continue;
      paramInt = OsConstants.SOCK_SEQPACKET;
    }
  }
  
  protected void finalize()
    throws IOException
  {
    close();
  }
  
  public FileDescriptor[] getAncillaryFileDescriptors()
    throws IOException
  {
    synchronized (this.readMonitor)
    {
      FileDescriptor[] arrayOfFileDescriptor = this.inboundFileDescriptors;
      this.inboundFileDescriptors = null;
      return arrayOfFileDescriptor;
    }
  }
  
  protected FileDescriptor getFileDescriptor()
  {
    return this.fd;
  }
  
  protected InputStream getInputStream()
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    try
    {
      if (this.fis == null) {
        this.fis = new SocketInputStream();
      }
      SocketInputStream localSocketInputStream = this.fis;
      return localSocketInputStream;
    }
    finally {}
  }
  
  public Object getOption(int paramInt)
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    switch (paramInt)
    {
    default: 
      try
      {
        throw new IOException("Unknown option: " + paramInt);
      }
      catch (ErrnoException localErrnoException)
      {
        throw localErrnoException.rethrowAsIOException();
      }
    case 4102: 
      return Integer.valueOf((int)Os.getsockoptTimeval(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO).toMillis());
    case 4: 
    case 4097: 
    case 4098: 
      paramInt = javaSoToOsOpt(paramInt);
      return Integer.valueOf(Os.getsockoptInt(this.fd, OsConstants.SOL_SOCKET, paramInt));
    case 128: 
      StructLinger localStructLinger = Os.getsockoptLinger(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_LINGER);
      if (!localStructLinger.isOn()) {
        return Integer.valueOf(-1);
      }
      return Integer.valueOf(localStructLinger.l_linger);
    }
    paramInt = Os.getsockoptInt(this.fd, OsConstants.IPPROTO_TCP, OsConstants.TCP_NODELAY);
    return Integer.valueOf(paramInt);
  }
  
  protected OutputStream getOutputStream()
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    try
    {
      if (this.fos == null) {
        this.fos = new SocketOutputStream();
      }
      SocketOutputStream localSocketOutputStream = this.fos;
      return localSocketOutputStream;
    }
    finally {}
  }
  
  public Credentials getPeerCredentials()
    throws IOException
  {
    return getPeerCredentials_native(this.fd);
  }
  
  public LocalSocketAddress getSockAddress()
    throws IOException
  {
    return null;
  }
  
  protected void listen(int paramInt)
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    try
    {
      Os.listen(this.fd, paramInt);
      return;
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  protected void sendUrgentData(int paramInt)
    throws IOException
  {
    throw new RuntimeException("not impled");
  }
  
  public void setFileDescriptorsForSend(FileDescriptor[] paramArrayOfFileDescriptor)
  {
    synchronized (this.writeMonitor)
    {
      this.outboundFileDescriptors = paramArrayOfFileDescriptor;
      return;
    }
  }
  
  public void setOption(int paramInt, Object paramObject)
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    int i = -1;
    int j = 0;
    if ((paramObject instanceof Integer)) {
      j = ((Integer)paramObject).intValue();
    }
    for (;;)
    {
      switch (paramInt)
      {
      default: 
        try
        {
          throw new IOException("Unknown option: " + paramInt);
        }
        catch (ErrnoException paramObject)
        {
          throw ((ErrnoException)paramObject).rethrowAsIOException();
        }
        if (!(paramObject instanceof Boolean)) {
          break label156;
        }
        if (((Boolean)paramObject).booleanValue()) {
          i = 1;
        } else {
          i = 0;
        }
        break;
      }
    }
    label156:
    throw new IOException("bad value: " + paramObject);
    paramObject = new StructLinger(i, j);
    Os.setsockoptLinger(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_LINGER, (StructLinger)paramObject);
    return;
    paramObject = StructTimeval.fromMillis(j);
    Os.setsockoptTimeval(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, (StructTimeval)paramObject);
    Os.setsockoptTimeval(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO, (StructTimeval)paramObject);
    return;
    paramInt = javaSoToOsOpt(paramInt);
    Os.setsockoptInt(this.fd, OsConstants.SOL_SOCKET, paramInt, j);
    return;
    Os.setsockoptInt(this.fd, OsConstants.IPPROTO_TCP, OsConstants.TCP_NODELAY, j);
  }
  
  protected void shutdownInput()
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    try
    {
      Os.shutdown(this.fd, OsConstants.SHUT_RD);
      return;
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  protected void shutdownOutput()
    throws IOException
  {
    if (this.fd == null) {
      throw new IOException("socket not created");
    }
    try
    {
      Os.shutdown(this.fd, OsConstants.SHUT_WR);
      return;
    }
    catch (ErrnoException localErrnoException)
    {
      throw localErrnoException.rethrowAsIOException();
    }
  }
  
  protected boolean supportsUrgentData()
  {
    return false;
  }
  
  public String toString()
  {
    return super.toString() + " fd:" + this.fd;
  }
  
  class SocketInputStream
    extends InputStream
  {
    SocketInputStream() {}
    
    public int available()
      throws IOException
    {
      FileDescriptor localFileDescriptor = LocalSocketImpl.-get0(LocalSocketImpl.this);
      if (localFileDescriptor == null) {
        throw new IOException("socket closed");
      }
      MutableInt localMutableInt = new MutableInt(0);
      try
      {
        Os.ioctlInt(localFileDescriptor, OsConstants.FIONREAD, localMutableInt);
        return localMutableInt.value;
      }
      catch (ErrnoException localErrnoException)
      {
        throw localErrnoException.rethrowAsIOException();
      }
    }
    
    public void close()
      throws IOException
    {
      LocalSocketImpl.this.close();
    }
    
    public int read()
      throws IOException
    {
      synchronized (LocalSocketImpl.-get1(LocalSocketImpl.this))
      {
        FileDescriptor localFileDescriptor1 = LocalSocketImpl.-get0(LocalSocketImpl.this);
        if (localFileDescriptor1 == null) {
          throw new IOException("socket closed");
        }
      }
      int i = LocalSocketImpl.-wrap0(LocalSocketImpl.this, localFileDescriptor2);
      return i;
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      FileDescriptor localFileDescriptor;
      synchronized (LocalSocketImpl.-get1(LocalSocketImpl.this))
      {
        localFileDescriptor = LocalSocketImpl.-get0(LocalSocketImpl.this);
        if (localFileDescriptor == null) {
          throw new IOException("socket closed");
        }
      }
      if ((paramInt1 < 0) || (paramInt2 < 0)) {}
      while (paramInt1 + paramInt2 > paramArrayOfByte.length) {
        throw new ArrayIndexOutOfBoundsException();
      }
      paramInt1 = LocalSocketImpl.-wrap1(LocalSocketImpl.this, paramArrayOfByte, paramInt1, paramInt2, localFileDescriptor);
      return paramInt1;
    }
  }
  
  class SocketOutputStream
    extends OutputStream
  {
    SocketOutputStream() {}
    
    public void close()
      throws IOException
    {
      LocalSocketImpl.this.close();
    }
    
    public void flush()
      throws IOException
    {
      FileDescriptor localFileDescriptor = LocalSocketImpl.-get0(LocalSocketImpl.this);
      if (localFileDescriptor == null) {
        throw new IOException("socket closed");
      }
      MutableInt localMutableInt = new MutableInt(0);
      for (;;)
      {
        try
        {
          Os.ioctlInt(localFileDescriptor, OsConstants.TIOCOUTQ, localMutableInt);
          if (localMutableInt.value <= 0) {
            return;
          }
        }
        catch (ErrnoException localErrnoException)
        {
          throw localErrnoException.rethrowAsIOException();
        }
        try
        {
          Thread.sleep(10L);
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
    
    public void write(int paramInt)
      throws IOException
    {
      synchronized (LocalSocketImpl.-get2(LocalSocketImpl.this))
      {
        FileDescriptor localFileDescriptor1 = LocalSocketImpl.-get0(LocalSocketImpl.this);
        if (localFileDescriptor1 == null) {
          throw new IOException("socket closed");
        }
      }
      LocalSocketImpl.-wrap2(LocalSocketImpl.this, paramInt, localFileDescriptor2);
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      write(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      FileDescriptor localFileDescriptor;
      synchronized (LocalSocketImpl.-get2(LocalSocketImpl.this))
      {
        localFileDescriptor = LocalSocketImpl.-get0(LocalSocketImpl.this);
        if (localFileDescriptor == null) {
          throw new IOException("socket closed");
        }
      }
      if ((paramInt1 < 0) || (paramInt2 < 0)) {}
      while (paramInt1 + paramInt2 > paramArrayOfByte.length) {
        throw new ArrayIndexOutOfBoundsException();
      }
      LocalSocketImpl.-wrap3(LocalSocketImpl.this, paramArrayOfByte, paramInt1, paramInt2, localFileDescriptor);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/LocalSocketImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */