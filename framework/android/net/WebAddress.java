package android.net;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebAddress
{
  static final int MATCH_GROUP_AUTHORITY = 2;
  static final int MATCH_GROUP_HOST = 3;
  static final int MATCH_GROUP_PATH = 5;
  static final int MATCH_GROUP_PORT = 4;
  static final int MATCH_GROUP_SCHEME = 1;
  static Pattern sAddressPattern = Pattern.compile("(?:(http|https|file)\\:\\/\\/)?(?:([-A-Za-z0-9$_.+!*'(),;?&=]+(?:\\:[-A-Za-z0-9$_.+!*'(),;?&=]+)?)@)?([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯%_-][a-zA-Z0-9 -퟿豈-﷏ﷰ-￯%_\\.-]*|\\[[0-9a-fA-F:\\.]+\\])?(?:\\:([0-9]*))?(\\/?[^#]*)?.*", 2);
  private String mAuthInfo;
  private String mHost;
  private String mPath;
  private int mPort;
  private String mScheme;
  
  public WebAddress(String paramString)
    throws ParseException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    this.mScheme = "";
    this.mHost = "";
    this.mPort = -1;
    this.mPath = "/";
    this.mAuthInfo = "";
    paramString = sAddressPattern.matcher(paramString);
    String str;
    if (paramString.matches())
    {
      str = paramString.group(1);
      if (str != null) {
        this.mScheme = str.toLowerCase(Locale.ROOT);
      }
      str = paramString.group(2);
      if (str != null) {
        this.mAuthInfo = str;
      }
      str = paramString.group(3);
      if (str != null) {
        this.mHost = str;
      }
      str = paramString.group(4);
      if ((str == null) || (str.length() <= 0)) {}
    }
    for (;;)
    {
      try
      {
        this.mPort = Integer.parseInt(str);
        paramString = paramString.group(5);
        if ((paramString != null) && (paramString.length() > 0))
        {
          if (paramString.charAt(0) == '/') {
            this.mPath = paramString;
          }
        }
        else
        {
          if ((this.mPort != 443) || (!this.mScheme.equals(""))) {
            break label262;
          }
          this.mScheme = "https";
          if (this.mScheme.equals("")) {
            this.mScheme = "http";
          }
          return;
        }
      }
      catch (NumberFormatException paramString)
      {
        throw new ParseException("Bad port");
      }
      this.mPath = ("/" + paramString);
      continue;
      throw new ParseException("Bad address");
      label262:
      if (this.mPort == -1) {
        if (this.mScheme.equals("https")) {
          this.mPort = 443;
        } else {
          this.mPort = 80;
        }
      }
    }
  }
  
  public String getAuthInfo()
  {
    return this.mAuthInfo;
  }
  
  public String getHost()
  {
    return this.mHost;
  }
  
  public String getPath()
  {
    return this.mPath;
  }
  
  public int getPort()
  {
    return this.mPort;
  }
  
  public String getScheme()
  {
    return this.mScheme;
  }
  
  public void setAuthInfo(String paramString)
  {
    this.mAuthInfo = paramString;
  }
  
  public void setHost(String paramString)
  {
    this.mHost = paramString;
  }
  
  public void setPath(String paramString)
  {
    this.mPath = paramString;
  }
  
  public void setPort(int paramInt)
  {
    this.mPort = paramInt;
  }
  
  public void setScheme(String paramString)
  {
    this.mScheme = paramString;
  }
  
  public String toString()
  {
    String str2 = "";
    String str1;
    if ((this.mPort == 443) || (!this.mScheme.equals("https")))
    {
      str1 = str2;
      if (this.mPort != 80)
      {
        str1 = str2;
        if (!this.mScheme.equals("http")) {}
      }
    }
    else
    {
      str1 = ":" + Integer.toString(this.mPort);
    }
    str2 = "";
    if (this.mAuthInfo.length() > 0) {
      str2 = this.mAuthInfo + "@";
    }
    return this.mScheme + "://" + str2 + this.mHost + str1 + this.mPath;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/WebAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */