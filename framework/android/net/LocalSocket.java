package android.net;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocalSocket
  implements Closeable
{
  public static final int SOCKET_DGRAM = 1;
  public static final int SOCKET_SEQPACKET = 3;
  public static final int SOCKET_STREAM = 2;
  static final int SOCKET_UNKNOWN = 0;
  private final LocalSocketImpl impl;
  private volatile boolean implCreated;
  private boolean isBound;
  private boolean isConnected;
  private LocalSocketAddress localAddress;
  private final int sockType;
  
  public LocalSocket()
  {
    this(2);
  }
  
  public LocalSocket(int paramInt)
  {
    this(new LocalSocketImpl(), paramInt);
    this.isBound = false;
    this.isConnected = false;
  }
  
  LocalSocket(LocalSocketImpl paramLocalSocketImpl, int paramInt)
  {
    this.impl = paramLocalSocketImpl;
    this.sockType = paramInt;
    this.isConnected = false;
    this.isBound = false;
  }
  
  public LocalSocket(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    this(new LocalSocketImpl(paramFileDescriptor), 0);
    this.isBound = true;
    this.isConnected = true;
  }
  
  /* Error */
  private void implCreateIfNeeded()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 55	android/net/LocalSocket:implCreated	Z
    //   4: ifne +32 -> 36
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 55	android/net/LocalSocket:implCreated	Z
    //   13: istore_1
    //   14: iload_1
    //   15: ifne +19 -> 34
    //   18: aload_0
    //   19: getfield 44	android/net/LocalSocket:impl	Landroid/net/LocalSocketImpl;
    //   22: aload_0
    //   23: getfield 46	android/net/LocalSocket:sockType	I
    //   26: invokevirtual 58	android/net/LocalSocketImpl:create	(I)V
    //   29: aload_0
    //   30: iconst_1
    //   31: putfield 55	android/net/LocalSocket:implCreated	Z
    //   34: aload_0
    //   35: monitorexit
    //   36: return
    //   37: astore_2
    //   38: aload_0
    //   39: iconst_1
    //   40: putfield 55	android/net/LocalSocket:implCreated	Z
    //   43: aload_2
    //   44: athrow
    //   45: astore_2
    //   46: aload_0
    //   47: monitorexit
    //   48: aload_2
    //   49: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	LocalSocket
    //   13	2	1	bool	boolean
    //   37	7	2	localObject1	Object
    //   45	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   18	29	37	finally
    //   9	14	45	finally
    //   29	34	45	finally
    //   38	45	45	finally
  }
  
  public void bind(LocalSocketAddress paramLocalSocketAddress)
    throws IOException
  {
    implCreateIfNeeded();
    try
    {
      if (this.isBound) {
        throw new IOException("already bound");
      }
    }
    finally {}
    this.localAddress = paramLocalSocketAddress;
    this.impl.bind(this.localAddress);
    this.isBound = true;
  }
  
  public void close()
    throws IOException
  {
    implCreateIfNeeded();
    this.impl.close();
  }
  
  public void connect(LocalSocketAddress paramLocalSocketAddress)
    throws IOException
  {
    try
    {
      if (this.isConnected) {
        throw new IOException("already connected");
      }
    }
    finally {}
    implCreateIfNeeded();
    this.impl.connect(paramLocalSocketAddress, 0);
    this.isConnected = true;
    this.isBound = true;
  }
  
  public void connect(LocalSocketAddress paramLocalSocketAddress, int paramInt)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public FileDescriptor[] getAncillaryFileDescriptors()
    throws IOException
  {
    return this.impl.getAncillaryFileDescriptors();
  }
  
  public FileDescriptor getFileDescriptor()
  {
    return this.impl.getFileDescriptor();
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    implCreateIfNeeded();
    return this.impl.getInputStream();
  }
  
  public LocalSocketAddress getLocalSocketAddress()
  {
    return this.localAddress;
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    implCreateIfNeeded();
    return this.impl.getOutputStream();
  }
  
  public Credentials getPeerCredentials()
    throws IOException
  {
    return this.impl.getPeerCredentials();
  }
  
  public int getReceiveBufferSize()
    throws IOException
  {
    return ((Integer)this.impl.getOption(4098)).intValue();
  }
  
  public LocalSocketAddress getRemoteSocketAddress()
  {
    throw new UnsupportedOperationException();
  }
  
  public int getSendBufferSize()
    throws IOException
  {
    return ((Integer)this.impl.getOption(4097)).intValue();
  }
  
  public int getSoTimeout()
    throws IOException
  {
    return ((Integer)this.impl.getOption(4102)).intValue();
  }
  
  public boolean isBound()
  {
    try
    {
      boolean bool = this.isBound;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isClosed()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean isConnected()
  {
    try
    {
      boolean bool = this.isConnected;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isInputShutdown()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean isOutputShutdown()
  {
    throw new UnsupportedOperationException();
  }
  
  public void setFileDescriptorsForSend(FileDescriptor[] paramArrayOfFileDescriptor)
  {
    this.impl.setFileDescriptorsForSend(paramArrayOfFileDescriptor);
  }
  
  public void setReceiveBufferSize(int paramInt)
    throws IOException
  {
    this.impl.setOption(4098, Integer.valueOf(paramInt));
  }
  
  public void setSendBufferSize(int paramInt)
    throws IOException
  {
    this.impl.setOption(4097, Integer.valueOf(paramInt));
  }
  
  public void setSoTimeout(int paramInt)
    throws IOException
  {
    this.impl.setOption(4102, Integer.valueOf(paramInt));
  }
  
  public void shutdownInput()
    throws IOException
  {
    implCreateIfNeeded();
    this.impl.shutdownInput();
  }
  
  public void shutdownOutput()
    throws IOException
  {
    implCreateIfNeeded();
    this.impl.shutdownOutput();
  }
  
  public String toString()
  {
    return super.toString() + " impl:" + this.impl;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/LocalSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */