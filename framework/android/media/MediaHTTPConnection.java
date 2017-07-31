package android.media;

import android.net.NetworkUtils;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MediaHTTPConnection
  extends IMediaHTTPConnection.Stub
{
  private static final int CONNECT_TIMEOUT_MS = 30000;
  private static final int HTTP_TEMP_REDIRECT = 307;
  private static final int MAX_REDIRECTS = 20;
  private static final String TAG = "MediaHTTPConnection";
  private static final boolean VERBOSE = false;
  private boolean mAllowCrossDomainRedirect = true;
  private boolean mAllowCrossProtocolRedirect = true;
  private HttpURLConnection mConnection = null;
  private long mCurrentOffset = -1L;
  private Map<String, String> mHeaders = null;
  private InputStream mInputStream = null;
  private long mNativeContext;
  private long mTotalSize = -1L;
  private URL mURL = null;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  public MediaHTTPConnection()
  {
    if (CookieHandler.getDefault() == null) {
      CookieHandler.setDefault(new CookieManager());
    }
    native_setup();
  }
  
  private Map<String, String> convertHeaderStringToMap(String paramString)
  {
    HashMap localHashMap = new HashMap();
    paramString = paramString.split("\r\n");
    int j = paramString.length;
    int i = 0;
    while (i < j)
    {
      String str2 = paramString[i];
      int k = str2.indexOf(":");
      if (k >= 0)
      {
        String str1 = str2.substring(0, k);
        str2 = str2.substring(k + 1);
        if (!filterOutInternalHeaders(str1, str2)) {
          localHashMap.put(str1, str2);
        }
      }
      i += 1;
    }
    return localHashMap;
  }
  
  private boolean filterOutInternalHeaders(String paramString1, String paramString2)
  {
    if ("android-allow-cross-domain-redirect".equalsIgnoreCase(paramString1))
    {
      this.mAllowCrossDomainRedirect = parseBoolean(paramString2);
      this.mAllowCrossProtocolRedirect = this.mAllowCrossDomainRedirect;
      return true;
    }
    return false;
  }
  
  private static final boolean isLocalHost(URL paramURL)
  {
    if (paramURL == null) {
      return false;
    }
    paramURL = paramURL.getHost();
    if (paramURL == null) {
      return false;
    }
    try
    {
      if (paramURL.equalsIgnoreCase("localhost")) {
        return true;
      }
      boolean bool = NetworkUtils.numericToInetAddress(paramURL).isLoopbackAddress();
      if (bool) {
        return true;
      }
    }
    catch (IllegalArgumentException paramURL) {}
    return false;
  }
  
  private final native void native_finalize();
  
  private final native IBinder native_getIMemory();
  
  private static final native void native_init();
  
  private final native int native_readAt(long paramLong, int paramInt);
  
  private final native void native_setup();
  
  private boolean parseBoolean(String paramString)
  {
    boolean bool = true;
    try
    {
      long l = Long.parseLong(paramString);
      return l != 0L;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      if (!"true".equalsIgnoreCase(paramString)) {
        bool = "yes".equalsIgnoreCase(paramString);
      }
    }
    return bool;
  }
  
  private int readAt(long paramLong, byte[] paramArrayOfByte, int paramInt)
  {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
    try
    {
      if (paramLong != this.mCurrentOffset) {
        seekTo(paramLong);
      }
      int j = this.mInputStream.read(paramArrayOfByte, 0, paramInt);
      int i = j;
      if (j == -1) {
        i = 0;
      }
      this.mCurrentOffset += i;
      return i;
    }
    catch (Exception paramArrayOfByte)
    {
      return -1;
    }
    catch (IOException paramArrayOfByte)
    {
      return -1;
    }
    catch (UnknownServiceException paramArrayOfByte)
    {
      Log.w("MediaHTTPConnection", "readAt " + paramLong + " / " + paramInt + " => " + paramArrayOfByte);
      return 64526;
    }
    catch (NoRouteToHostException paramArrayOfByte)
    {
      Log.w("MediaHTTPConnection", "readAt " + paramLong + " / " + paramInt + " => " + paramArrayOfByte);
      return 64526;
    }
    catch (ProtocolException paramArrayOfByte)
    {
      Log.w("MediaHTTPConnection", "readAt " + paramLong + " / " + paramInt + " => " + paramArrayOfByte);
    }
    return 64526;
  }
  
  private void seekTo(long paramLong)
    throws IOException
  {
    teardownConnection();
    int i = 0;
    for (;;)
    {
      Object localObject3;
      try
      {
        Object localObject1 = this.mURL;
        boolean bool1 = isLocalHost((URL)localObject1);
        if (bool1)
        {
          this.mConnection = ((HttpURLConnection)((URL)localObject1).openConnection(Proxy.NO_PROXY));
          this.mConnection.setConnectTimeout(30000);
          this.mConnection.setInstanceFollowRedirects(this.mAllowCrossDomainRedirect);
          if (this.mHeaders == null) {
            break label179;
          }
          localObject1 = this.mHeaders.entrySet().iterator();
          if (!((Iterator)localObject1).hasNext()) {
            break label179;
          }
          localObject3 = (Map.Entry)((Iterator)localObject1).next();
          this.mConnection.setRequestProperty((String)((Map.Entry)localObject3).getKey(), (String)((Map.Entry)localObject3).getValue());
          continue;
        }
        this.mConnection = ((HttpURLConnection)localIOException.openConnection());
      }
      catch (IOException localIOException)
      {
        this.mTotalSize = -1L;
        this.mInputStream = null;
        this.mConnection = null;
        this.mCurrentOffset = -1L;
        throw localIOException;
      }
      continue;
      label179:
      if (paramLong > 0L) {
        this.mConnection.setRequestProperty("Range", "bytes=" + paramLong + "-");
      }
      int k = this.mConnection.getResponseCode();
      Object localObject2;
      if ((k != 300) && (k != 301) && (k != 302) && (k != 303) && (k != 307))
      {
        if (this.mAllowCrossDomainRedirect) {
          this.mURL = this.mConnection.getURL();
        }
        if (k != 206) {
          break label657;
        }
        localObject2 = this.mConnection.getHeaderField("Content-Range");
        this.mTotalSize = -1L;
        if (localObject2 != null)
        {
          i = ((String)localObject2).lastIndexOf('/');
          if (i >= 0) {
            localObject2 = ((String)localObject2).substring(i + 1);
          }
        }
      }
      try
      {
        for (this.mTotalSize = Long.parseLong((String)localObject2);; this.mTotalSize = this.mConnection.getContentLength())
        {
          if ((paramLong <= 0L) || (k == 206)) {
            break label688;
          }
          throw new ProtocolException();
          int j = i + 1;
          if (j > 20) {
            throw new NoRouteToHostException("Too many redirects: " + j);
          }
          localObject2 = this.mConnection.getRequestMethod();
          if ((k != 307) || (((String)localObject2).equals("GET"))) {}
          while (((String)localObject2).equals("HEAD"))
          {
            localObject2 = this.mConnection.getHeaderField("Location");
            if (localObject2 != null) {
              break;
            }
            throw new NoRouteToHostException("Invalid redirect");
          }
          throw new NoRouteToHostException("Invalid redirect");
          localObject3 = new URL(this.mURL, (String)localObject2);
          if ((((URL)localObject3).getProtocol().equals("https")) || (((URL)localObject3).getProtocol().equals("http")))
          {
            boolean bool2 = this.mURL.getProtocol().equals(((URL)localObject3).getProtocol());
            if ((!this.mAllowCrossProtocolRedirect) && (!bool2)) {
              break label635;
            }
            bool2 = this.mURL.getHost().equals(((URL)localObject3).getHost());
            if ((!this.mAllowCrossDomainRedirect) && (!bool2)) {
              break label646;
            }
            i = j;
            localObject2 = localObject3;
            if (k == 307) {
              break;
            }
            this.mURL = ((URL)localObject3);
            i = j;
            localObject2 = localObject3;
            break;
          }
          throw new NoRouteToHostException("Unsupported protocol redirect");
          label635:
          throw new NoRouteToHostException("Cross-protocol redirects are disallowed");
          label646:
          throw new NoRouteToHostException("Cross-domain redirects are disallowed");
          label657:
          if (k != 200) {
            throw new IOException();
          }
        }
        label688:
        this.mInputStream = new BufferedInputStream(this.mConnection.getInputStream());
        this.mCurrentOffset = paramLong;
        return;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        for (;;) {}
      }
    }
  }
  
  private void teardownConnection()
  {
    if (this.mConnection != null)
    {
      this.mInputStream = null;
      this.mConnection.disconnect();
      this.mConnection = null;
      this.mCurrentOffset = -1L;
    }
  }
  
  public IBinder connect(String paramString1, String paramString2)
  {
    try
    {
      disconnect();
      this.mAllowCrossDomainRedirect = true;
      this.mURL = new URL(paramString1);
      this.mHeaders = convertHeaderStringToMap(paramString2);
      return native_getIMemory();
    }
    catch (MalformedURLException paramString1) {}
    return null;
  }
  
  public void disconnect()
  {
    teardownConnection();
    this.mHeaders = null;
    this.mURL = null;
  }
  
  protected void finalize()
  {
    native_finalize();
  }
  
  public String getMIMEType()
  {
    if (this.mConnection == null) {}
    try
    {
      seekTo(0L);
      return this.mConnection.getContentType();
    }
    catch (IOException localIOException) {}
    return "application/octet-stream";
  }
  
  public long getSize()
  {
    if (this.mConnection == null) {}
    try
    {
      seekTo(0L);
      return this.mTotalSize;
    }
    catch (IOException localIOException) {}
    return -1L;
  }
  
  public String getUri()
  {
    return this.mURL.toString();
  }
  
  public int readAt(long paramLong, int paramInt)
  {
    return native_readAt(paramLong, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaHTTPConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */