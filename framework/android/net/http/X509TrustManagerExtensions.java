package android.net.http;

import android.security.net.config.UserCertificateSource;
import com.android.org.conscrypt.TrustManagerImpl;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.X509TrustManager;

public class X509TrustManagerExtensions
{
  private final Method mCheckServerTrusted;
  private final TrustManagerImpl mDelegate;
  private final Method mIsSameTrustConfiguration;
  private final X509TrustManager mTrustManager;
  
  /* Error */
  public X509TrustManagerExtensions(X509TrustManager paramX509TrustManager)
    throws java.lang.IllegalArgumentException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 22	java/lang/Object:<init>	()V
    //   4: aload_1
    //   5: instanceof 24
    //   8: ifeq +27 -> 35
    //   11: aload_0
    //   12: aload_1
    //   13: checkcast 24	com/android/org/conscrypt/TrustManagerImpl
    //   16: putfield 26	android/net/http/X509TrustManagerExtensions:mDelegate	Lcom/android/org/conscrypt/TrustManagerImpl;
    //   19: aload_0
    //   20: aconst_null
    //   21: putfield 28	android/net/http/X509TrustManagerExtensions:mTrustManager	Ljavax/net/ssl/X509TrustManager;
    //   24: aload_0
    //   25: aconst_null
    //   26: putfield 30	android/net/http/X509TrustManagerExtensions:mCheckServerTrusted	Ljava/lang/reflect/Method;
    //   29: aload_0
    //   30: aconst_null
    //   31: putfield 32	android/net/http/X509TrustManagerExtensions:mIsSameTrustConfiguration	Ljava/lang/reflect/Method;
    //   34: return
    //   35: aload_0
    //   36: aconst_null
    //   37: putfield 26	android/net/http/X509TrustManagerExtensions:mDelegate	Lcom/android/org/conscrypt/TrustManagerImpl;
    //   40: aload_0
    //   41: aload_1
    //   42: putfield 28	android/net/http/X509TrustManagerExtensions:mTrustManager	Ljavax/net/ssl/X509TrustManager;
    //   45: aload_0
    //   46: aload_1
    //   47: invokevirtual 36	java/lang/Object:getClass	()Ljava/lang/Class;
    //   50: ldc 38
    //   52: iconst_3
    //   53: anewarray 40	java/lang/Class
    //   56: dup
    //   57: iconst_0
    //   58: ldc 42
    //   60: aastore
    //   61: dup
    //   62: iconst_1
    //   63: ldc 44
    //   65: aastore
    //   66: dup
    //   67: iconst_2
    //   68: ldc 44
    //   70: aastore
    //   71: invokevirtual 48	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   74: putfield 30	android/net/http/X509TrustManagerExtensions:mCheckServerTrusted	Ljava/lang/reflect/Method;
    //   77: aconst_null
    //   78: astore_2
    //   79: aload_1
    //   80: invokevirtual 36	java/lang/Object:getClass	()Ljava/lang/Class;
    //   83: ldc 50
    //   85: iconst_2
    //   86: anewarray 40	java/lang/Class
    //   89: dup
    //   90: iconst_0
    //   91: ldc 44
    //   93: aastore
    //   94: dup
    //   95: iconst_1
    //   96: ldc 44
    //   98: aastore
    //   99: invokevirtual 48	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   102: astore_1
    //   103: aload_0
    //   104: aload_1
    //   105: putfield 32	android/net/http/X509TrustManagerExtensions:mIsSameTrustConfiguration	Ljava/lang/reflect/Method;
    //   108: return
    //   109: astore_1
    //   110: new 15	java/lang/IllegalArgumentException
    //   113: dup
    //   114: ldc 52
    //   116: invokespecial 55	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   119: athrow
    //   120: astore_1
    //   121: aload_2
    //   122: astore_1
    //   123: goto -20 -> 103
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	X509TrustManagerExtensions
    //   0	126	1	paramX509TrustManager	X509TrustManager
    //   78	44	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   45	77	109	java/lang/NoSuchMethodException
    //   79	103	120	java/lang/ReflectiveOperationException
  }
  
  public List<X509Certificate> checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString1, String paramString2)
    throws CertificateException
  {
    if (this.mDelegate != null) {
      return this.mDelegate.checkServerTrusted(paramArrayOfX509Certificate, paramString1, paramString2);
    }
    try
    {
      paramArrayOfX509Certificate = (List)this.mCheckServerTrusted.invoke(this.mTrustManager, new Object[] { paramArrayOfX509Certificate, paramString1, paramString2 });
      return paramArrayOfX509Certificate;
    }
    catch (InvocationTargetException paramArrayOfX509Certificate)
    {
      if ((paramArrayOfX509Certificate.getCause() instanceof CertificateException)) {
        throw ((CertificateException)paramArrayOfX509Certificate.getCause());
      }
      if ((paramArrayOfX509Certificate.getCause() instanceof RuntimeException)) {
        throw ((RuntimeException)paramArrayOfX509Certificate.getCause());
      }
      throw new CertificateException("checkServerTrusted failed", paramArrayOfX509Certificate.getCause());
    }
    catch (IllegalAccessException paramArrayOfX509Certificate)
    {
      throw new CertificateException("Failed to call checkServerTrusted", paramArrayOfX509Certificate);
    }
  }
  
  public boolean isSameTrustConfiguration(String paramString1, String paramString2)
  {
    if (this.mIsSameTrustConfiguration == null) {
      return true;
    }
    try
    {
      boolean bool = ((Boolean)this.mIsSameTrustConfiguration.invoke(this.mTrustManager, new Object[] { paramString1, paramString2 })).booleanValue();
      return bool;
    }
    catch (InvocationTargetException paramString1)
    {
      if ((paramString1.getCause() instanceof RuntimeException)) {
        throw ((RuntimeException)paramString1.getCause());
      }
      throw new RuntimeException("isSameTrustConfiguration failed", paramString1.getCause());
    }
    catch (IllegalAccessException paramString1)
    {
      throw new RuntimeException("Failed to call isSameTrustConfiguration", paramString1);
    }
  }
  
  public boolean isUserAddedCertificate(X509Certificate paramX509Certificate)
  {
    return UserCertificateSource.getInstance().findBySubjectAndPublicKey(paramX509Certificate) != null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/http/X509TrustManagerExtensions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */