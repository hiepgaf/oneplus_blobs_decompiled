package android.security.net.config;

import java.util.Arrays;

public final class Pin
{
  public final byte[] digest;
  public final String digestAlgorithm;
  private final int mHashCode;
  
  public Pin(String paramString, byte[] paramArrayOfByte)
  {
    this.digestAlgorithm = paramString;
    this.digest = paramArrayOfByte;
    this.mHashCode = (Arrays.hashCode(paramArrayOfByte) ^ paramString.hashCode());
  }
  
  public static int getDigestLength(String paramString)
  {
    if ("SHA-256".equalsIgnoreCase(paramString)) {
      return 32;
    }
    throw new IllegalArgumentException("Unsupported digest algorithm: " + paramString);
  }
  
  public static boolean isSupportedDigestAlgorithm(String paramString)
  {
    return "SHA-256".equalsIgnoreCase(paramString);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Pin)) {
      return false;
    }
    if (((Pin)paramObject).hashCode() != this.mHashCode) {
      return false;
    }
    if (!Arrays.equals(this.digest, ((Pin)paramObject).digest)) {
      return false;
    }
    return this.digestAlgorithm.equals(((Pin)paramObject).digestAlgorithm);
  }
  
  public int hashCode()
  {
    return this.mHashCode;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/Pin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */