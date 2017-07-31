package android.net.http;

import java.security.cert.X509Certificate;

public class SslError
{
  public static final int SSL_DATE_INVALID = 4;
  public static final int SSL_EXPIRED = 1;
  public static final int SSL_IDMISMATCH = 2;
  public static final int SSL_INVALID = 5;
  @Deprecated
  public static final int SSL_MAX_ERROR = 6;
  public static final int SSL_NOTYETVALID = 0;
  public static final int SSL_UNTRUSTED = 3;
  final SslCertificate mCertificate;
  int mErrors;
  final String mUrl;
  
  static
  {
    if (SslError.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      return;
    }
  }
  
  @Deprecated
  public SslError(int paramInt, SslCertificate paramSslCertificate)
  {
    this(paramInt, paramSslCertificate, "");
  }
  
  public SslError(int paramInt, SslCertificate paramSslCertificate, String paramString)
  {
    int i;
    if (!-assertionsDisabled)
    {
      if (paramSslCertificate != null) {}
      for (i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    if (!-assertionsDisabled)
    {
      if (paramString != null) {}
      for (i = j; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    addError(paramInt);
    this.mCertificate = paramSslCertificate;
    this.mUrl = paramString;
  }
  
  @Deprecated
  public SslError(int paramInt, X509Certificate paramX509Certificate)
  {
    this(paramInt, paramX509Certificate, "");
  }
  
  public SslError(int paramInt, X509Certificate paramX509Certificate, String paramString)
  {
    this(paramInt, new SslCertificate(paramX509Certificate), paramString);
  }
  
  public static SslError SslErrorFromChromiumErrorCode(int paramInt, SslCertificate paramSslCertificate, String paramString)
  {
    int j = 0;
    if (!-assertionsDisabled)
    {
      int i = j;
      if (paramInt >= 65237)
      {
        i = j;
        if (paramInt <= 65336) {
          i = 1;
        }
      }
      if (i == 0) {
        throw new AssertionError();
      }
    }
    if (paramInt == 65336) {
      return new SslError(2, paramSslCertificate, paramString);
    }
    if (paramInt == 65335) {
      return new SslError(4, paramSslCertificate, paramString);
    }
    if (paramInt == 65334) {
      return new SslError(3, paramSslCertificate, paramString);
    }
    return new SslError(5, paramSslCertificate, paramString);
  }
  
  public boolean addError(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt < 6) {
        bool1 = true;
      }
    }
    if (bool1) {
      this.mErrors = (1 << paramInt | this.mErrors);
    }
    return bool1;
  }
  
  public SslCertificate getCertificate()
  {
    return this.mCertificate;
  }
  
  public int getPrimaryError()
  {
    if (this.mErrors != 0)
    {
      int i = 5;
      while (i >= 0)
      {
        if ((this.mErrors & 1 << i) != 0) {
          return i;
        }
        i -= 1;
      }
      if (!-assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return -1;
  }
  
  public String getUrl()
  {
    return this.mUrl;
  }
  
  public boolean hasError(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt < 6) {
        bool1 = true;
      }
    }
    bool2 = bool1;
    if (bool1)
    {
      if ((1 << paramInt & this.mErrors) != 0) {
        bool2 = true;
      }
    }
    else {
      return bool2;
    }
    return false;
  }
  
  public String toString()
  {
    return "primary error: " + getPrimaryError() + " certificate: " + getCertificate() + " on URL: " + getUrl();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/http/SslError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */