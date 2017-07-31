package android.net;

import android.content.Context;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.format.Time;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.TimeZone;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class OemHttpClient
{
  private static final long AVERAGE_RECEIVE_TIME = 832L;
  private static final boolean DEBUG = true;
  private static final long GMT_BEIJING_OFFSET = 28800000L;
  private static final String TAG = "OemHttpClient";
  private static final String oemServerURL_RANDOM = "http://newds01.myoppo.com/autotime/dateandtime.xml?number=";
  private static final String oemServerURL_RANDOM2 = "http://newds02.myoppo.com/autotime/dateandtime.xml?number=";
  private long mHttpTime;
  private long mHttpTimeReference;
  private long mRoundTripTime;
  
  private boolean forceRefreshTimeFromOemServer(Context paramContext, int paramInt1, int paramInt2)
  {
    Log.d("OemHttpClient", "Enter forceRefreshTimeFromOemServer run");
    Object localObject1 = "http://newds01.myoppo.com/autotime/dateandtime.xml?number=";
    if (paramInt1 > 0) {
      localObject1 = "http://newds02.myoppo.com/autotime/dateandtime.xml?number=";
    }
    try
    {
      Object localObject2 = (String)localObject1 + System.currentTimeMillis();
      localObject1 = new URL((String)localObject2);
      long l2;
      InputStreamReader localInputStreamReader;
      BufferedReader localBufferedReader;
      long l1;
      label393:
      long l3;
      long l4;
      long l5;
      Log.e("OemHttpClient", "OemServer exception: " + paramContext);
    }
    catch (Exception paramContext)
    {
      try
      {
        Log.i("OemHttpClient", "Cur http request:" + (String)localObject2);
        localObject2 = Proxy.getDefaultHost();
        paramInt1 = Proxy.getDefaultPort();
        Log.d("OemHttpClient", "OemServer proxyHost = " + (String)localObject2 + " proxyPort = " + paramInt1);
        if (getNetType(paramContext))
        {
          Log.d("OemHttpClient", "Get network type success!");
          localObject1 = (HttpURLConnection)((URL)localObject1).openConnection();
          Log.d("OemHttpClient", "HttpURLConnection open openConnection success!");
        }
        for (;;)
        {
          ((URLConnection)localObject1).setDoInput(true);
          ((URLConnection)localObject1).setUseCaches(false);
          Log.d("OemHttpClient", "timeout:" + paramInt2);
          ((URLConnection)localObject1).setConnectTimeout(paramInt2);
          ((URLConnection)localObject1).setReadTimeout(paramInt2);
          l2 = SystemClock.elapsedRealtime();
          Log.d("OemHttpClient", "Strart to connect http server!");
          ((URLConnection)localObject1).connect();
          Log.d("OemHttpClient", "Connect http server success!");
          localInputStreamReader = null;
          localBufferedReader = null;
          paramContext = "";
          l1 = 0L;
          this.mHttpTimeReference = 0L;
          paramInt1 = ((HttpURLConnection)localObject1).getResponseCode();
          Log.d("OemHttpClient", "Http responseCode:" + paramInt1);
          localObject2 = paramContext;
          if (paramInt1 != 200) {
            break label393;
          }
          l1 = System.currentTimeMillis();
          localInputStreamReader = new InputStreamReader(((URLConnection)localObject1).getInputStream(), "utf-8");
          localBufferedReader = new BufferedReader(localInputStreamReader);
          for (;;)
          {
            localObject2 = localBufferedReader.readLine();
            if (localObject2 == null) {
              break;
            }
            paramContext = (Context)localObject2;
          }
          Log.d("OemHttpClient", "Use http proxy!");
          localObject1 = (HttpURLConnection)((URL)localObject1).openConnection(new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress((String)localObject2, paramInt1)));
        }
        Log.d("OemHttpClient", "Read response data success!");
        localObject2 = paramContext;
        l3 = SystemClock.elapsedRealtime();
        this.mHttpTimeReference = SystemClock.elapsedRealtime();
        localBufferedReader.close();
        localInputStreamReader.close();
        ((HttpURLConnection)localObject1).disconnect();
        Log.d("OemHttpClient", "Start to parser http response data!");
        paramContext = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        localObject1 = new DateTimeXmlParseHandler();
        paramContext.setContentHandler((ContentHandler)localObject1);
        paramContext.parse(new InputSource(new StringReader((String)localObject2)));
        localObject2 = ((DateTimeXmlParseHandler)localObject1).getDate().split("-");
        paramContext = new int[3];
        paramInt1 = 0;
        while (paramInt1 < localObject2.length)
        {
          paramContext[paramInt1] = Integer.parseInt(localObject2[paramInt1]);
          paramInt1 += 1;
        }
        localObject2 = ((DateTimeXmlParseHandler)localObject1).getTime().split(":");
        localObject1 = new int[3];
        paramInt1 = 0;
        while (paramInt1 < localObject2.length)
        {
          localObject1[paramInt1] = Integer.parseInt(localObject2[paramInt1]);
          paramInt1 += 1;
        }
        localObject2 = new Time();
        Log.d("OemHttpClient", "Parser time success, hour= " + localObject1[0] + " minute = " + localObject1[1] + "seconds =" + localObject1[2]);
        ((Time)localObject2).set(localObject1[2], localObject1[1], localObject1[0], paramContext[2], paramContext[1] - 1, paramContext[0]);
        l4 = ((Time)localObject2).toMillis(true) - 28800000L;
        l5 = System.currentTimeMillis();
        l1 = TimeZone.getDefault().getRawOffset() + l4 + (l5 - l1) + 832L;
        this.mHttpTime = (TimeZone.getDefault().getOffset(l1) - TimeZone.getDefault().getRawOffset() + l1);
        this.mRoundTripTime = (l3 - l2);
        SystemProperties.set("persist.sys.lasttime", Long.toString(l4));
        return true;
      }
      catch (Exception paramContext)
      {
        for (;;) {}
      }
      paramContext = paramContext;
    }
    return false;
  }
  
  private boolean getNetType(Context paramContext)
  {
    paramContext = (ConnectivityManager)paramContext.getSystemService("connectivity");
    if (paramContext == null) {
      return false;
    }
    paramContext = paramContext.getActiveNetworkInfo();
    if (paramContext == null) {
      return false;
    }
    String str = paramContext.getTypeName();
    if (str.equalsIgnoreCase("WIFI")) {
      return true;
    }
    if ((str.equalsIgnoreCase("MOBILE")) || (str.equalsIgnoreCase("GPRS")))
    {
      paramContext = paramContext.getExtraInfo();
      return (paramContext == null) || (!paramContext.equalsIgnoreCase("cmwap"));
    }
    return true;
  }
  
  public long getHttpTime()
  {
    return this.mHttpTime;
  }
  
  public long getHttpTimeReference()
  {
    return this.mHttpTimeReference;
  }
  
  public long getRoundTripTime()
  {
    return this.mRoundTripTime;
  }
  
  public boolean requestTime(Context paramContext, int paramInt1, int paramInt2)
  {
    return forceRefreshTimeFromOemServer(paramContext, paramInt1, paramInt2);
  }
  
  public class DateTimeXmlParseHandler
    extends DefaultHandler
  {
    private String mDateString = "";
    private boolean mIsDateFlag = false;
    private boolean mIsTimeFlag = false;
    private boolean mIsTimeZoneFlag = false;
    private String mTimeString = "";
    private String mTimeZoneString = "";
    
    public DateTimeXmlParseHandler() {}
    
    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws SAXException
    {
      super.characters(paramArrayOfChar, paramInt1, paramInt2);
      if (this.mIsTimeZoneFlag) {
        this.mTimeZoneString = new String(paramArrayOfChar, paramInt1, paramInt2);
      }
      do
      {
        return;
        if (this.mIsDateFlag)
        {
          this.mDateString = new String(paramArrayOfChar, paramInt1, paramInt2);
          return;
        }
      } while (!this.mIsTimeFlag);
      this.mTimeString = new String(paramArrayOfChar, paramInt1, paramInt2);
    }
    
    public void endDocument()
      throws SAXException
    {
      super.endDocument();
    }
    
    public void endElement(String paramString1, String paramString2, String paramString3)
      throws SAXException
    {
      super.endElement(paramString1, paramString2, paramString3);
      if (paramString2.equals("TimeZone")) {
        this.mIsTimeZoneFlag = false;
      }
      do
      {
        return;
        if (paramString2.equals("Date"))
        {
          this.mIsDateFlag = false;
          return;
        }
      } while (!paramString2.equals("Time"));
      this.mIsTimeFlag = false;
    }
    
    public String getDate()
    {
      return this.mDateString;
    }
    
    public String getTime()
    {
      return this.mTimeString;
    }
    
    public String getTimeZone()
    {
      return this.mTimeZoneString;
    }
    
    public void startDocument()
      throws SAXException
    {
      super.startDocument();
    }
    
    public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
      throws SAXException
    {
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
      if (paramString2.equals("TimeZone")) {
        this.mIsTimeZoneFlag = true;
      }
      do
      {
        return;
        if (paramString2.equals("Date"))
        {
          this.mIsDateFlag = true;
          return;
        }
      } while (!paramString2.equals("Time"));
      this.mIsTimeFlag = true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/OemHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */