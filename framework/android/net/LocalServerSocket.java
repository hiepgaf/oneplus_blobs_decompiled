package android.net;

import java.io.FileDescriptor;
import java.io.IOException;

public class LocalServerSocket
{
  private static final int LISTEN_BACKLOG = 50;
  private final LocalSocketImpl impl;
  private final LocalSocketAddress localAddress;
  
  public LocalServerSocket(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    this.impl = new LocalSocketImpl(paramFileDescriptor);
    this.impl.listen(50);
    this.localAddress = this.impl.getSockAddress();
  }
  
  public LocalServerSocket(String paramString)
    throws IOException
  {
    this.impl = new LocalSocketImpl();
    this.impl.create(2);
    this.localAddress = new LocalSocketAddress(paramString);
    this.impl.bind(this.localAddress);
    this.impl.listen(50);
  }
  
  public LocalSocket accept()
    throws IOException
  {
    LocalSocketImpl localLocalSocketImpl = new LocalSocketImpl();
    this.impl.accept(localLocalSocketImpl);
    return new LocalSocket(localLocalSocketImpl, 0);
  }
  
  public void close()
    throws IOException
  {
    this.impl.close();
  }
  
  public FileDescriptor getFileDescriptor()
  {
    return this.impl.getFileDescriptor();
  }
  
  public LocalSocketAddress getLocalSocketAddress()
  {
    return this.localAddress;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/LocalServerSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */