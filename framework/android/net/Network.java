package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import com.android.okhttp.ConnectionPool;
import com.android.okhttp.HttpHandler;
import com.android.okhttp.HttpsHandler;
import com.android.okhttp.OkHttpClient;
import com.android.okhttp.OkUrlFactory;
import com.android.okhttp.internal.Internal;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import javax.net.SocketFactory;

public class Network
  implements Parcelable
{
  public static final Parcelable.Creator<Network> CREATOR;
  private static final boolean httpKeepAlive = Boolean.parseBoolean(System.getProperty("http.keepAlive", "true"));
  private static final long httpKeepAliveDurationMs;
  private static final int httpMaxConnections;
  private volatile ConnectionPool mConnectionPool = null;
  private final Object mLock = new Object();
  private volatile com.android.okhttp.internal.Network mNetwork = null;
  private volatile NetworkBoundSocketFactory mNetworkBoundSocketFactory = null;
  public final int netId;
  
  static
  {
    if (httpKeepAlive) {}
    for (int i = Integer.parseInt(System.getProperty("http.maxConnections", "5"));; i = 0)
    {
      httpMaxConnections = i;
      httpKeepAliveDurationMs = Long.parseLong(System.getProperty("http.keepAliveDuration", "300000"));
      CREATOR = new Parcelable.Creator()
      {
        public Network createFromParcel(Parcel paramAnonymousParcel)
        {
          return new Network(paramAnonymousParcel.readInt());
        }
        
        public Network[] newArray(int paramAnonymousInt)
        {
          return new Network[paramAnonymousInt];
        }
      };
      return;
    }
  }
  
  public Network(int paramInt)
  {
    this.netId = paramInt;
  }
  
  public Network(Network paramNetwork)
  {
    this.netId = paramNetwork.netId;
  }
  
  private void maybeInitHttpClient()
  {
    synchronized (this.mLock)
    {
      if (this.mNetwork == null) {
        this.mNetwork = new com.android.okhttp.internal.Network()
        {
          public InetAddress[] resolveInetAddresses(String paramAnonymousString)
            throws UnknownHostException
          {
            return Network.this.getAllByName(paramAnonymousString);
          }
        };
      }
      if (this.mConnectionPool == null) {
        this.mConnectionPool = new ConnectionPool(httpMaxConnections, httpKeepAliveDurationMs);
      }
      return;
    }
  }
  
  public void bindSocket(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    try
    {
      if (!((InetSocketAddress)Os.getpeername(paramFileDescriptor)).getAddress().isAnyLocalAddress()) {
        throw new SocketException("Socket is connected");
      }
    }
    catch (ErrnoException localErrnoException)
    {
      if (localErrnoException.errno != OsConstants.ENOTCONN) {
        throw localErrnoException.rethrowAsSocketException();
      }
    }
    catch (ClassCastException paramFileDescriptor)
    {
      throw new SocketException("Only AF_INET/AF_INET6 sockets supported");
    }
    int i = NetworkUtils.bindSocketToNetwork(paramFileDescriptor.getInt$(), this.netId);
    if (i != 0) {
      throw new ErrnoException("Binding socket to network " + this.netId, -i).rethrowAsSocketException();
    }
  }
  
  public void bindSocket(DatagramSocket paramDatagramSocket)
    throws IOException
  {
    paramDatagramSocket.getReuseAddress();
    bindSocket(paramDatagramSocket.getFileDescriptor$());
  }
  
  public void bindSocket(Socket paramSocket)
    throws IOException
  {
    paramSocket.getReuseAddress();
    bindSocket(paramSocket.getFileDescriptor$());
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (!(paramObject instanceof Network)) {
      return false;
    }
    paramObject = (Network)paramObject;
    if (this.netId == ((Network)paramObject).netId) {
      bool = true;
    }
    return bool;
  }
  
  public InetAddress[] getAllByName(String paramString)
    throws UnknownHostException
  {
    return InetAddress.getAllByNameOnNet(paramString, this.netId);
  }
  
  public InetAddress getByName(String paramString)
    throws UnknownHostException
  {
    return InetAddress.getByNameOnNet(paramString, this.netId);
  }
  
  public long getNetworkHandle()
  {
    if (this.netId == 0) {
      return 0L;
    }
    return this.netId << 32 | 0xFACADE;
  }
  
  public SocketFactory getSocketFactory()
  {
    if (this.mNetworkBoundSocketFactory == null) {}
    synchronized (this.mLock)
    {
      if (this.mNetworkBoundSocketFactory == null) {
        this.mNetworkBoundSocketFactory = new NetworkBoundSocketFactory(this.netId);
      }
      return this.mNetworkBoundSocketFactory;
    }
  }
  
  public int hashCode()
  {
    return this.netId * 11;
  }
  
  public URLConnection openConnection(URL paramURL)
    throws IOException
  {
    Object localObject = ConnectivityManager.getInstanceOrNull();
    if (localObject == null) {
      throw new IOException("No ConnectivityManager yet constructed, please construct one");
    }
    localObject = ((ConnectivityManager)localObject).getProxyForNetwork(this);
    if (localObject != null) {}
    for (localObject = ((ProxyInfo)localObject).makeProxy();; localObject = Proxy.NO_PROXY) {
      return openConnection(paramURL, (Proxy)localObject);
    }
  }
  
  public URLConnection openConnection(URL paramURL, Proxy paramProxy)
    throws IOException
  {
    if (paramProxy == null) {
      throw new IllegalArgumentException("proxy is null");
    }
    maybeInitHttpClient();
    Object localObject = paramURL.getProtocol();
    if (((String)localObject).equals("http")) {}
    for (paramProxy = HttpHandler.createHttpOkUrlFactory(paramProxy);; paramProxy = HttpsHandler.createHttpsOkUrlFactory(paramProxy))
    {
      localObject = paramProxy.client();
      ((OkHttpClient)localObject).setSocketFactory(getSocketFactory()).setConnectionPool(this.mConnectionPool);
      Internal.instance.setNetwork((OkHttpClient)localObject, this.mNetwork);
      return paramProxy.open(paramURL);
      if (!((String)localObject).equals("https")) {
        break;
      }
    }
    throw new MalformedURLException("Invalid URL or unrecognized protocol " + (String)localObject);
  }
  
  public String toString()
  {
    return Integer.toString(this.netId);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.netId);
  }
  
  private class NetworkBoundSocketFactory
    extends SocketFactory
  {
    private final int mNetId;
    
    public NetworkBoundSocketFactory(int paramInt)
    {
      this.mNetId = paramInt;
    }
    
    private Socket connectToHost(String paramString, int paramInt, SocketAddress paramSocketAddress)
      throws IOException
    {
      InetAddress[] arrayOfInetAddress = Network.this.getAllByName(paramString);
      int i = 0;
      while (i < arrayOfInetAddress.length) {
        try
        {
          Socket localSocket = createSocket();
          if (paramSocketAddress != null) {
            localSocket.bind(paramSocketAddress);
          }
          localSocket.connect(new InetSocketAddress(arrayOfInetAddress[i], paramInt));
          return localSocket;
        }
        catch (IOException localIOException)
        {
          if (i == arrayOfInetAddress.length - 1) {
            throw localIOException;
          }
          i += 1;
        }
      }
      throw new UnknownHostException(paramString);
    }
    
    public Socket createSocket()
      throws IOException
    {
      Socket localSocket = new Socket();
      Network.this.bindSocket(localSocket);
      return localSocket;
    }
    
    public Socket createSocket(String paramString, int paramInt)
      throws IOException
    {
      return connectToHost(paramString, paramInt, null);
    }
    
    public Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2)
      throws IOException
    {
      return connectToHost(paramString, paramInt1, new InetSocketAddress(paramInetAddress, paramInt2));
    }
    
    public Socket createSocket(InetAddress paramInetAddress, int paramInt)
      throws IOException
    {
      Socket localSocket = createSocket();
      localSocket.connect(new InetSocketAddress(paramInetAddress, paramInt));
      return localSocket;
    }
    
    public Socket createSocket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2)
      throws IOException
    {
      Socket localSocket = createSocket();
      localSocket.bind(new InetSocketAddress(paramInetAddress2, paramInt2));
      localSocket.connect(new InetSocketAddress(paramInetAddress1, paramInt1));
      return localSocket;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/Network.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */