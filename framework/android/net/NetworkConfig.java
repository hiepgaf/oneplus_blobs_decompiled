package android.net;

import java.util.Locale;

public class NetworkConfig
{
  public boolean dependencyMet;
  public String name;
  public int priority;
  public int radio;
  public int restoreTime;
  public int type;
  
  public NetworkConfig(String paramString)
  {
    paramString = paramString.split(",");
    this.name = paramString[0].trim().toLowerCase(Locale.ROOT);
    this.type = Integer.parseInt(paramString[1]);
    this.radio = Integer.parseInt(paramString[2]);
    this.priority = Integer.parseInt(paramString[3]);
    this.restoreTime = Integer.parseInt(paramString[4]);
    this.dependencyMet = Boolean.parseBoolean(paramString[5]);
  }
  
  public boolean isDefault()
  {
    return this.type == this.radio;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */