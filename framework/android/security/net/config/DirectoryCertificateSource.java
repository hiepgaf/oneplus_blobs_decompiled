package android.security.net.config;

import android.util.ArraySet;
import com.android.org.conscrypt.Hex;
import com.android.org.conscrypt.NativeCrypto;
import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

abstract class DirectoryCertificateSource
  implements CertificateSource
{
  private static final String LOG_TAG = "DirectoryCertificateSrc";
  private final CertificateFactory mCertFactory;
  private Set<X509Certificate> mCertificates;
  private final File mDir;
  private final Object mLock = new Object();
  
  protected DirectoryCertificateSource(File paramFile)
  {
    this.mDir = paramFile;
    try
    {
      this.mCertFactory = CertificateFactory.getInstance("X.509");
      return;
    }
    catch (CertificateException paramFile)
    {
      throw new RuntimeException("Failed to obtain X.509 CertificateFactory", paramFile);
    }
  }
  
  private X509Certificate findCert(X500Principal paramX500Principal, CertSelector paramCertSelector)
  {
    String str = getHash(paramX500Principal);
    int i = 0;
    Object localObject;
    if (i >= 0)
    {
      localObject = str + "." + i;
      if (new File(this.mDir, (String)localObject).exists()) {}
    }
    else
    {
      return null;
    }
    if (isCertMarkedAsRemoved((String)localObject)) {}
    do
    {
      i += 1;
      break;
      localObject = readCertificate((String)localObject);
    } while ((localObject == null) || (!paramX500Principal.equals(((X509Certificate)localObject).getSubjectX500Principal())) || (!paramCertSelector.match((X509Certificate)localObject)));
    return (X509Certificate)localObject;
  }
  
  private Set<X509Certificate> findCerts(X500Principal paramX500Principal, CertSelector paramCertSelector)
  {
    String str = getHash(paramX500Principal);
    Object localObject1 = null;
    int i = 0;
    Object localObject2;
    if (i >= 0)
    {
      localObject2 = str + "." + i;
      if (new File(this.mDir, (String)localObject2).exists()) {}
    }
    else
    {
      if (localObject1 == null) {
        break label172;
      }
      return (Set<X509Certificate>)localObject1;
    }
    if (isCertMarkedAsRemoved((String)localObject2)) {
      localObject2 = localObject1;
    }
    for (;;)
    {
      i += 1;
      localObject1 = localObject2;
      break;
      X509Certificate localX509Certificate = readCertificate((String)localObject2);
      localObject2 = localObject1;
      if (localX509Certificate != null)
      {
        localObject2 = localObject1;
        if (paramX500Principal.equals(localX509Certificate.getSubjectX500Principal()))
        {
          localObject2 = localObject1;
          if (paramCertSelector.match(localX509Certificate))
          {
            localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = new ArraySet();
            }
            ((Set)localObject2).add(localX509Certificate);
          }
        }
      }
    }
    label172:
    return Collections.emptySet();
  }
  
  private String getHash(X500Principal paramX500Principal)
  {
    return Hex.intToHexString(NativeCrypto.X509_NAME_hash_old(paramX500Principal), 8);
  }
  
  /* Error */
  private X509Certificate readCertificate(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore 4
    //   5: new 146	java/io/BufferedInputStream
    //   8: dup
    //   9: new 148	java/io/FileInputStream
    //   12: dup
    //   13: new 81	java/io/File
    //   16: dup
    //   17: aload_0
    //   18: getfield 39	android/security/net/config/DirectoryCertificateSource:mDir	Ljava/io/File;
    //   21: aload_1
    //   22: invokespecial 84	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   25: invokespecial 150	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   28: invokespecial 153	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   31: astore_3
    //   32: aload_0
    //   33: getfield 49	android/security/net/config/DirectoryCertificateSource:mCertFactory	Ljava/security/cert/CertificateFactory;
    //   36: aload_3
    //   37: invokevirtual 157	java/security/cert/CertificateFactory:generateCertificate	(Ljava/io/InputStream;)Ljava/security/cert/Certificate;
    //   40: checkcast 98	java/security/cert/X509Certificate
    //   43: astore_2
    //   44: aload_3
    //   45: invokestatic 163	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   48: aload_2
    //   49: areturn
    //   50: astore_2
    //   51: aload 4
    //   53: astore_3
    //   54: aload_2
    //   55: astore 4
    //   57: aload_3
    //   58: astore_2
    //   59: ldc 19
    //   61: new 65	java/lang/StringBuilder
    //   64: dup
    //   65: invokespecial 66	java/lang/StringBuilder:<init>	()V
    //   68: ldc -91
    //   70: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: aload_1
    //   74: invokevirtual 70	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: invokevirtual 79	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   80: aload 4
    //   82: invokestatic 171	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   85: pop
    //   86: aload_3
    //   87: invokestatic 163	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   90: aconst_null
    //   91: areturn
    //   92: astore_1
    //   93: aload_2
    //   94: invokestatic 163	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   97: aload_1
    //   98: athrow
    //   99: astore_1
    //   100: aload_3
    //   101: astore_2
    //   102: goto -9 -> 93
    //   105: astore 4
    //   107: goto -50 -> 57
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	110	0	this	DirectoryCertificateSource
    //   0	110	1	paramString	String
    //   1	48	2	localX509Certificate	X509Certificate
    //   50	5	2	localCertificateException1	CertificateException
    //   58	44	2	localObject1	Object
    //   31	70	3	localObject2	Object
    //   3	78	4	localCertificateException2	CertificateException
    //   105	1	4	localCertificateException3	CertificateException
    // Exception table:
    //   from	to	target	type
    //   5	32	50	java/security/cert/CertificateException
    //   5	32	50	java/io/IOException
    //   5	32	92	finally
    //   59	86	92	finally
    //   32	44	99	finally
    //   32	44	105	java/security/cert/CertificateException
    //   32	44	105	java/io/IOException
  }
  
  public Set<X509Certificate> findAllByIssuerAndSignature(final X509Certificate paramX509Certificate)
  {
    findCerts(paramX509Certificate.getIssuerX500Principal(), new CertSelector()
    {
      public boolean match(X509Certificate paramAnonymousX509Certificate)
      {
        try
        {
          paramX509Certificate.verify(paramAnonymousX509Certificate.getPublicKey());
          return true;
        }
        catch (Exception paramAnonymousX509Certificate) {}
        return false;
      }
    });
  }
  
  public X509Certificate findByIssuerAndSignature(final X509Certificate paramX509Certificate)
  {
    findCert(paramX509Certificate.getIssuerX500Principal(), new CertSelector()
    {
      public boolean match(X509Certificate paramAnonymousX509Certificate)
      {
        try
        {
          paramX509Certificate.verify(paramAnonymousX509Certificate.getPublicKey());
          return true;
        }
        catch (Exception paramAnonymousX509Certificate) {}
        return false;
      }
    });
  }
  
  public X509Certificate findBySubjectAndPublicKey(final X509Certificate paramX509Certificate)
  {
    findCert(paramX509Certificate.getSubjectX500Principal(), new CertSelector()
    {
      public boolean match(X509Certificate paramAnonymousX509Certificate)
      {
        return paramAnonymousX509Certificate.getPublicKey().equals(paramX509Certificate.getPublicKey());
      }
    });
  }
  
  public Set<X509Certificate> getCertificates()
  {
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        if (this.mCertificates != null)
        {
          localObject2 = this.mCertificates;
          return (Set<X509Certificate>)localObject2;
        }
        Object localObject2 = new ArraySet();
        if (this.mDir.isDirectory())
        {
          String[] arrayOfString = this.mDir.list();
          i = 0;
          int j = arrayOfString.length;
          if (i < j)
          {
            Object localObject3 = arrayOfString[i];
            if (isCertMarkedAsRemoved((String)localObject3)) {
              break label132;
            }
            localObject3 = readCertificate((String)localObject3);
            if (localObject3 == null) {
              break label132;
            }
            ((Set)localObject2).add(localObject3);
          }
        }
      }
      this.mCertificates = localSet1;
      Set localSet2 = this.mCertificates;
      return localSet2;
      label132:
      i += 1;
    }
  }
  
  public void handleTrustStorageUpdate()
  {
    synchronized (this.mLock)
    {
      this.mCertificates = null;
      return;
    }
  }
  
  protected abstract boolean isCertMarkedAsRemoved(String paramString);
  
  private static abstract interface CertSelector
  {
    public abstract boolean match(X509Certificate paramX509Certificate);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/DirectoryCertificateSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */