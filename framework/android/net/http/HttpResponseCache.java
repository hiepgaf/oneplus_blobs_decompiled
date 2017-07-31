package android.net.http;

import com.android.okhttp.AndroidShimResponseCache;
import com.android.okhttp.Cache;
import com.android.okhttp.OkCacheContainer;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public final class HttpResponseCache
  extends ResponseCache
  implements Closeable, OkCacheContainer
{
  private final AndroidShimResponseCache delegate;
  
  private HttpResponseCache(AndroidShimResponseCache paramAndroidShimResponseCache)
  {
    this.delegate = paramAndroidShimResponseCache;
  }
  
  public static HttpResponseCache getInstalled()
  {
    ResponseCache localResponseCache = ResponseCache.getDefault();
    if ((localResponseCache instanceof HttpResponseCache)) {
      return (HttpResponseCache)localResponseCache;
    }
    return null;
  }
  
  public static HttpResponseCache install(File paramFile, long paramLong)
    throws IOException
  {
    try
    {
      Object localObject = ResponseCache.getDefault();
      if ((localObject instanceof HttpResponseCache))
      {
        localObject = (HttpResponseCache)localObject;
        AndroidShimResponseCache localAndroidShimResponseCache = ((HttpResponseCache)localObject).delegate;
        boolean bool = localAndroidShimResponseCache.isEquivalent(paramFile, paramLong);
        if (bool) {
          return (HttpResponseCache)localObject;
        }
        localAndroidShimResponseCache.close();
      }
      paramFile = new HttpResponseCache(AndroidShimResponseCache.create(paramFile, paramLong));
      ResponseCache.setDefault(paramFile);
      return paramFile;
    }
    finally {}
  }
  
  public void close()
    throws IOException
  {
    if (ResponseCache.getDefault() == this) {
      ResponseCache.setDefault(null);
    }
    this.delegate.close();
  }
  
  public void delete()
    throws IOException
  {
    if (ResponseCache.getDefault() == this) {
      ResponseCache.setDefault(null);
    }
    this.delegate.delete();
  }
  
  public void flush()
  {
    try
    {
      this.delegate.flush();
      return;
    }
    catch (IOException localIOException) {}
  }
  
  public CacheResponse get(URI paramURI, String paramString, Map<String, List<String>> paramMap)
    throws IOException
  {
    return this.delegate.get(paramURI, paramString, paramMap);
  }
  
  public Cache getCache()
  {
    return this.delegate.getCache();
  }
  
  public int getHitCount()
  {
    return this.delegate.getHitCount();
  }
  
  public int getNetworkCount()
  {
    return this.delegate.getNetworkCount();
  }
  
  public int getRequestCount()
  {
    return this.delegate.getRequestCount();
  }
  
  public long maxSize()
  {
    return this.delegate.maxSize();
  }
  
  public CacheRequest put(URI paramURI, URLConnection paramURLConnection)
    throws IOException
  {
    return this.delegate.put(paramURI, paramURLConnection);
  }
  
  public long size()
  {
    try
    {
      long l = this.delegate.size();
      return l;
    }
    catch (IOException localIOException) {}
    return -1L;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/http/HttpResponseCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */