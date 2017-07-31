package android.net;

import android.content.Context;
import android.util.Log;
import com.android.org.conscrypt.ClientSessionContext;
import com.android.org.conscrypt.FileClientSessionCache;
import com.android.org.conscrypt.SSLClientSessionCache;
import java.io.File;
import java.io.IOException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSessionContext;

public final class SSLSessionCache
{
  private static final String TAG = "SSLSessionCache";
  final SSLClientSessionCache mSessionCache;
  
  public SSLSessionCache(Context paramContext)
  {
    localFile = paramContext.getDir("sslcache", 0);
    paramContext = null;
    try
    {
      SSLClientSessionCache localSSLClientSessionCache = FileClientSessionCache.usingDirectory(localFile);
      paramContext = localSSLClientSessionCache;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.w("SSLSessionCache", "Unable to create SSL session cache in " + localFile, localIOException);
      }
    }
    this.mSessionCache = paramContext;
  }
  
  public SSLSessionCache(File paramFile)
    throws IOException
  {
    this.mSessionCache = FileClientSessionCache.usingDirectory(paramFile);
  }
  
  public SSLSessionCache(Object paramObject)
  {
    this.mSessionCache = ((SSLClientSessionCache)paramObject);
  }
  
  public static void install(SSLSessionCache paramSSLSessionCache, SSLContext paramSSLContext)
  {
    Object localObject = null;
    SSLSessionContext localSSLSessionContext = paramSSLContext.getClientSessionContext();
    if ((localSSLSessionContext instanceof ClientSessionContext))
    {
      paramSSLContext = (ClientSessionContext)localSSLSessionContext;
      if (paramSSLSessionCache == null) {}
      for (paramSSLSessionCache = (SSLSessionCache)localObject;; paramSSLSessionCache = paramSSLSessionCache.mSessionCache)
      {
        paramSSLContext.setPersistentCache(paramSSLSessionCache);
        return;
      }
    }
    throw new IllegalArgumentException("Incompatible SSLContext: " + paramSSLContext);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/SSLSessionCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */