package android.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class MailTo
{
  private static final String BODY = "body";
  private static final String CC = "cc";
  public static final String MAILTO_SCHEME = "mailto:";
  private static final String SUBJECT = "subject";
  private static final String TO = "to";
  private HashMap<String, String> mHeaders = new HashMap();
  
  public static boolean isMailTo(String paramString)
  {
    return (paramString != null) && (paramString.startsWith("mailto:"));
  }
  
  public static MailTo parse(String paramString)
    throws ParseException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    if (!isMailTo(paramString)) {
      throw new ParseException("Not a mailto scheme");
    }
    Object localObject1 = Uri.parse(paramString.substring("mailto:".length()));
    MailTo localMailTo = new MailTo();
    paramString = ((Uri)localObject1).getQuery();
    Object localObject2;
    if (paramString != null)
    {
      localObject2 = paramString.split("&");
      int j = localObject2.length;
      int i = 0;
      while (i < j)
      {
        paramString = localObject2[i].split("=");
        if (paramString.length == 0)
        {
          i += 1;
        }
        else
        {
          HashMap localHashMap = localMailTo.mHeaders;
          String str = Uri.decode(paramString[0]).toLowerCase(Locale.ROOT);
          if (paramString.length > 1) {}
          for (paramString = Uri.decode(paramString[1]);; paramString = null)
          {
            localHashMap.put(str, paramString);
            break;
          }
        }
      }
    }
    localObject1 = ((Uri)localObject1).getPath();
    if (localObject1 != null)
    {
      localObject2 = localMailTo.getTo();
      paramString = (String)localObject1;
      if (localObject2 != null) {
        paramString = (String)localObject1 + ", " + (String)localObject2;
      }
      localMailTo.mHeaders.put("to", paramString);
    }
    return localMailTo;
  }
  
  public String getBody()
  {
    return (String)this.mHeaders.get("body");
  }
  
  public String getCc()
  {
    return (String)this.mHeaders.get("cc");
  }
  
  public Map<String, String> getHeaders()
  {
    return this.mHeaders;
  }
  
  public String getSubject()
  {
    return (String)this.mHeaders.get("subject");
  }
  
  public String getTo()
  {
    return (String)this.mHeaders.get("to");
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("mailto:");
    localStringBuilder.append('?');
    Iterator localIterator = this.mHeaders.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localStringBuilder.append(Uri.encode((String)localEntry.getKey()));
      localStringBuilder.append('=');
      localStringBuilder.append(Uri.encode((String)localEntry.getValue()));
      localStringBuilder.append('&');
    }
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/MailTo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */