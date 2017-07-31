package android.net;

import android.content.Context;
import android.text.TextUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Proxy
{
  private static final Pattern EXCLLIST_PATTERN = Pattern.compile("^$|^[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*(,[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*)*$");
  private static final String EXCLLIST_REGEXP = "^$|^[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*(,[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*)*$";
  private static final String EXCL_REGEX = "[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*";
  public static final String EXTRA_PROXY_INFO = "android.intent.extra.PROXY_INFO";
  private static final Pattern HOSTNAME_PATTERN;
  private static final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$";
  private static final String NAME_IP_REGEX = "[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*";
  public static final String PROXY_CHANGE_ACTION = "android.intent.action.PROXY_CHANGE";
  public static final int PROXY_EXCLLIST_INVALID = 5;
  public static final int PROXY_HOSTNAME_EMPTY = 1;
  public static final int PROXY_HOSTNAME_INVALID = 2;
  public static final int PROXY_PORT_EMPTY = 3;
  public static final int PROXY_PORT_INVALID = 4;
  public static final int PROXY_VALID = 0;
  private static final String TAG = "Proxy";
  private static ConnectivityManager sConnectivityManager = null;
  private static final ProxySelector sDefaultProxySelector = ProxySelector.getDefault();
  
  static
  {
    HOSTNAME_PATTERN = Pattern.compile("^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$");
  }
  
  public static final String getDefaultHost()
  {
    String str = System.getProperty("http.proxyHost");
    if (TextUtils.isEmpty(str)) {
      return null;
    }
    return str;
  }
  
  public static final int getDefaultPort()
  {
    if (getDefaultHost() == null) {
      return -1;
    }
    try
    {
      int i = Integer.parseInt(System.getProperty("http.proxyPort"));
      return i;
    }
    catch (NumberFormatException localNumberFormatException) {}
    return -1;
  }
  
  public static final String getHost(Context paramContext)
  {
    paramContext = getProxy(paramContext, null);
    if (paramContext == java.net.Proxy.NO_PROXY) {
      return null;
    }
    try
    {
      paramContext = ((InetSocketAddress)paramContext.address()).getHostName();
      return paramContext;
    }
    catch (Exception paramContext) {}
    return null;
  }
  
  public static final int getPort(Context paramContext)
  {
    paramContext = getProxy(paramContext, null);
    if (paramContext == java.net.Proxy.NO_PROXY) {
      return -1;
    }
    try
    {
      int i = ((InetSocketAddress)paramContext.address()).getPort();
      return i;
    }
    catch (Exception paramContext) {}
    return -1;
  }
  
  public static final java.net.Proxy getProxy(Context paramContext, String paramString)
  {
    if ((paramString == null) || (isLocalHost(""))) {}
    do
    {
      return java.net.Proxy.NO_PROXY;
      paramContext = URI.create(paramString);
      paramContext = ProxySelector.getDefault().select(paramContext);
    } while (paramContext.size() <= 0);
    return (java.net.Proxy)paramContext.get(0);
  }
  
  private static final boolean isLocalHost(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if (paramString != null) {
      try
      {
        if (paramString.equalsIgnoreCase("localhost")) {
          return true;
        }
        boolean bool = NetworkUtils.numericToInetAddress(paramString).isLoopbackAddress();
        if (bool) {
          return true;
        }
      }
      catch (IllegalArgumentException paramString) {}
    }
    return false;
  }
  
  public static final void setHttpProxySystemProperty(ProxyInfo paramProxyInfo)
  {
    String str2 = null;
    String str3 = null;
    String str1 = null;
    Uri localUri = Uri.EMPTY;
    if (paramProxyInfo != null)
    {
      str2 = paramProxyInfo.getHost();
      str3 = Integer.toString(paramProxyInfo.getPort());
      str1 = paramProxyInfo.getExclusionListAsString();
      localUri = paramProxyInfo.getPacFileUrl();
    }
    setHttpProxySystemProperty(str2, str3, str1, localUri);
  }
  
  public static final void setHttpProxySystemProperty(String paramString1, String paramString2, String paramString3, Uri paramUri)
  {
    String str = paramString3;
    if (paramString3 != null) {
      str = paramString3.replace(",", "|");
    }
    if (paramString1 != null)
    {
      System.setProperty("http.proxyHost", paramString1);
      System.setProperty("https.proxyHost", paramString1);
      if (paramString2 == null) {
        break label110;
      }
      System.setProperty("http.proxyPort", paramString2);
      System.setProperty("https.proxyPort", paramString2);
      label53:
      if (str == null) {
        break label125;
      }
      System.setProperty("http.nonProxyHosts", str);
      System.setProperty("https.nonProxyHosts", str);
    }
    for (;;)
    {
      if (Uri.EMPTY.equals(paramUri)) {
        break label140;
      }
      ProxySelector.setDefault(new PacProxySelector());
      return;
      System.clearProperty("http.proxyHost");
      System.clearProperty("https.proxyHost");
      break;
      label110:
      System.clearProperty("http.proxyPort");
      System.clearProperty("https.proxyPort");
      break label53;
      label125:
      System.clearProperty("http.nonProxyHosts");
      System.clearProperty("https.nonProxyHosts");
    }
    label140:
    ProxySelector.setDefault(sDefaultProxySelector);
  }
  
  public static int validate(String paramString1, String paramString2, String paramString3)
  {
    Matcher localMatcher = HOSTNAME_PATTERN.matcher(paramString1);
    paramString3 = EXCLLIST_PATTERN.matcher(paramString3);
    if (!localMatcher.matches()) {
      return 2;
    }
    if (!paramString3.matches()) {
      return 5;
    }
    if ((paramString1.length() > 0) && (paramString2.length() == 0)) {
      return 3;
    }
    if (paramString2.length() > 0)
    {
      if (paramString1.length() == 0) {
        return 1;
      }
      try
      {
        int i = Integer.parseInt(paramString2);
        if ((i <= 0) || (i > 65535)) {
          return 4;
        }
      }
      catch (NumberFormatException paramString1)
      {
        return 4;
      }
    }
    return 0;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/Proxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */