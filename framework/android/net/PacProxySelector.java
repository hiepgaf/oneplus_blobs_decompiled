package android.net;

import android.os.ServiceManager;
import android.util.Log;
import com.android.net.IProxyService;
import com.android.net.IProxyService.Stub;
import com.google.android.collect.Lists;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PacProxySelector
  extends ProxySelector
{
  private static final String PROXY = "PROXY ";
  public static final String PROXY_SERVICE = "com.android.net.IProxyService";
  private static final String SOCKS = "SOCKS ";
  private static final String TAG = "PacProxySelector";
  private final List<Proxy> mDefaultList;
  private IProxyService mProxyService = IProxyService.Stub.asInterface(ServiceManager.getService("com.android.net.IProxyService"));
  
  public PacProxySelector()
  {
    if (this.mProxyService == null) {
      Log.e("PacProxySelector", "PacManager: no proxy service");
    }
    this.mDefaultList = Lists.newArrayList(new Proxy[] { Proxy.NO_PROXY });
  }
  
  private static List<Proxy> parseResponse(String paramString)
  {
    int i = 0;
    paramString = paramString.split(";");
    ArrayList localArrayList = Lists.newArrayList();
    int j = paramString.length;
    if (i < j)
    {
      Object localObject = paramString[i].trim();
      if (((String)localObject).equals("DIRECT")) {
        localArrayList.add(Proxy.NO_PROXY);
      }
      for (;;)
      {
        i += 1;
        break;
        if (((String)localObject).startsWith("PROXY "))
        {
          localObject = proxyFromHostPort(Proxy.Type.HTTP, ((String)localObject).substring("PROXY ".length()));
          if (localObject != null) {
            localArrayList.add(localObject);
          }
        }
        else if (((String)localObject).startsWith("SOCKS "))
        {
          localObject = proxyFromHostPort(Proxy.Type.SOCKS, ((String)localObject).substring("SOCKS ".length()));
          if (localObject != null) {
            localArrayList.add(localObject);
          }
        }
      }
    }
    if (localArrayList.size() == 0) {
      localArrayList.add(Proxy.NO_PROXY);
    }
    return localArrayList;
  }
  
  private static Proxy proxyFromHostPort(Proxy.Type paramType, String paramString)
  {
    try
    {
      String[] arrayOfString = paramString.split(":");
      paramType = new Proxy(paramType, InetSocketAddress.createUnresolved(arrayOfString[0], Integer.parseInt(arrayOfString[1])));
      return paramType;
    }
    catch (NumberFormatException|ArrayIndexOutOfBoundsException paramType)
    {
      Log.d("PacProxySelector", "Unable to parse proxy " + paramString + " " + paramType);
    }
    return null;
  }
  
  public void connectFailed(URI paramURI, SocketAddress paramSocketAddress, IOException paramIOException) {}
  
  /* Error */
  public List<Proxy> select(URI paramURI)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 40	android/net/PacProxySelector:mProxyService	Lcom/android/net/IProxyService;
    //   4: ifnonnull +15 -> 19
    //   7: aload_0
    //   8: ldc 11
    //   10: invokestatic 32	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   13: invokestatic 38	com/android/net/IProxyService$Stub:asInterface	(Landroid/os/IBinder;)Lcom/android/net/IProxyService;
    //   16: putfield 40	android/net/PacProxySelector:mProxyService	Lcom/android/net/IProxyService;
    //   19: aload_0
    //   20: getfield 40	android/net/PacProxySelector:mProxyService	Lcom/android/net/IProxyService;
    //   23: ifnonnull +25 -> 48
    //   26: ldc 17
    //   28: ldc -83
    //   30: invokestatic 48	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   33: pop
    //   34: iconst_1
    //   35: anewarray 50	java/net/Proxy
    //   38: dup
    //   39: iconst_0
    //   40: getstatic 54	java/net/Proxy:NO_PROXY	Ljava/net/Proxy;
    //   43: aastore
    //   44: invokestatic 60	com/google/android/collect/Lists:newArrayList	([Ljava/lang/Object;)Ljava/util/ArrayList;
    //   47: areturn
    //   48: aconst_null
    //   49: astore 5
    //   51: aload_1
    //   52: astore_2
    //   53: aload_1
    //   54: astore_3
    //   55: aload_1
    //   56: astore 4
    //   58: ldc -81
    //   60: aload_1
    //   61: invokevirtual 180	java/net/URI:getScheme	()Ljava/lang/String;
    //   64: invokevirtual 183	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   67: ifne +33 -> 100
    //   70: aload_1
    //   71: astore_3
    //   72: aload_1
    //   73: astore 4
    //   75: new 177	java/net/URI
    //   78: dup
    //   79: aload_1
    //   80: invokevirtual 180	java/net/URI:getScheme	()Ljava/lang/String;
    //   83: aconst_null
    //   84: aload_1
    //   85: invokevirtual 186	java/net/URI:getHost	()Ljava/lang/String;
    //   88: aload_1
    //   89: invokevirtual 189	java/net/URI:getPort	()I
    //   92: ldc -65
    //   94: aconst_null
    //   95: aconst_null
    //   96: invokespecial 194	java/net/URI:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   99: astore_2
    //   100: aload_2
    //   101: astore_3
    //   102: aload_2
    //   103: astore 4
    //   105: aload_2
    //   106: invokevirtual 198	java/net/URI:toURL	()Ljava/net/URL;
    //   109: invokevirtual 201	java/net/URL:toString	()Ljava/lang/String;
    //   112: astore_1
    //   113: aload_0
    //   114: getfield 40	android/net/PacProxySelector:mProxyService	Lcom/android/net/IProxyService;
    //   117: aload_2
    //   118: invokevirtual 186	java/net/URI:getHost	()Ljava/lang/String;
    //   121: aload_1
    //   122: invokeinterface 207 3 0
    //   127: astore_1
    //   128: aload_1
    //   129: ifnonnull +48 -> 177
    //   132: aload_0
    //   133: getfield 62	android/net/PacProxySelector:mDefaultList	Ljava/util/List;
    //   136: areturn
    //   137: astore_1
    //   138: aload_3
    //   139: invokevirtual 186	java/net/URI:getHost	()Ljava/lang/String;
    //   142: astore_1
    //   143: aload_3
    //   144: astore_2
    //   145: goto -32 -> 113
    //   148: astore_1
    //   149: aload 4
    //   151: invokevirtual 186	java/net/URI:getHost	()Ljava/lang/String;
    //   154: astore_1
    //   155: aload 4
    //   157: astore_2
    //   158: goto -45 -> 113
    //   161: astore_1
    //   162: ldc 17
    //   164: ldc -47
    //   166: aload_1
    //   167: invokestatic 212	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   170: pop
    //   171: aload 5
    //   173: astore_1
    //   174: goto -46 -> 128
    //   177: aload_1
    //   178: invokestatic 214	android/net/PacProxySelector:parseResponse	(Ljava/lang/String;)Ljava/util/List;
    //   181: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	182	0	this	PacProxySelector
    //   0	182	1	paramURI	URI
    //   52	106	2	localObject1	Object
    //   54	90	3	localObject2	Object
    //   56	100	4	localObject3	Object
    //   49	123	5	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   58	70	137	java/net/MalformedURLException
    //   75	100	137	java/net/MalformedURLException
    //   105	113	137	java/net/MalformedURLException
    //   58	70	148	java/net/URISyntaxException
    //   75	100	148	java/net/URISyntaxException
    //   105	113	148	java/net/URISyntaxException
    //   113	128	161	java/lang/Exception
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/PacProxySelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */