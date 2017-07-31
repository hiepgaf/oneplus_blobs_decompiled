package android.security.net.config;

import java.util.Locale;

public final class Domain
{
  public final String hostname;
  public final boolean subdomainsIncluded;
  
  public Domain(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      throw new NullPointerException("Hostname must not be null");
    }
    this.hostname = paramString.toLowerCase(Locale.US);
    this.subdomainsIncluded = paramBoolean;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Domain)) {
      return false;
    }
    if (((Domain)paramObject).subdomainsIncluded == this.subdomainsIncluded) {
      bool = ((Domain)paramObject).hostname.equals(this.hostname);
    }
    return bool;
  }
  
  public int hashCode()
  {
    int j = this.hostname.hashCode();
    if (this.subdomainsIncluded) {}
    for (int i = 1231;; i = 1237) {
      return i ^ j;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/Domain.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */