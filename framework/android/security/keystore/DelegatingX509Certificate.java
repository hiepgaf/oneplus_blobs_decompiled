package android.security.keystore;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

class DelegatingX509Certificate
  extends X509Certificate
{
  private final X509Certificate mDelegate;
  
  DelegatingX509Certificate(X509Certificate paramX509Certificate)
  {
    this.mDelegate = paramX509Certificate;
  }
  
  public void checkValidity()
    throws CertificateExpiredException, CertificateNotYetValidException
  {
    this.mDelegate.checkValidity();
  }
  
  public void checkValidity(Date paramDate)
    throws CertificateExpiredException, CertificateNotYetValidException
  {
    this.mDelegate.checkValidity(paramDate);
  }
  
  public int getBasicConstraints()
  {
    return this.mDelegate.getBasicConstraints();
  }
  
  public Set<String> getCriticalExtensionOIDs()
  {
    return this.mDelegate.getCriticalExtensionOIDs();
  }
  
  public byte[] getEncoded()
    throws CertificateEncodingException
  {
    return this.mDelegate.getEncoded();
  }
  
  public List<String> getExtendedKeyUsage()
    throws CertificateParsingException
  {
    return this.mDelegate.getExtendedKeyUsage();
  }
  
  public byte[] getExtensionValue(String paramString)
  {
    return this.mDelegate.getExtensionValue(paramString);
  }
  
  public Collection<List<?>> getIssuerAlternativeNames()
    throws CertificateParsingException
  {
    return this.mDelegate.getIssuerAlternativeNames();
  }
  
  public Principal getIssuerDN()
  {
    return this.mDelegate.getIssuerDN();
  }
  
  public boolean[] getIssuerUniqueID()
  {
    return this.mDelegate.getIssuerUniqueID();
  }
  
  public X500Principal getIssuerX500Principal()
  {
    return this.mDelegate.getIssuerX500Principal();
  }
  
  public boolean[] getKeyUsage()
  {
    return this.mDelegate.getKeyUsage();
  }
  
  public Set<String> getNonCriticalExtensionOIDs()
  {
    return this.mDelegate.getNonCriticalExtensionOIDs();
  }
  
  public Date getNotAfter()
  {
    return this.mDelegate.getNotAfter();
  }
  
  public Date getNotBefore()
  {
    return this.mDelegate.getNotBefore();
  }
  
  public PublicKey getPublicKey()
  {
    return this.mDelegate.getPublicKey();
  }
  
  public BigInteger getSerialNumber()
  {
    return this.mDelegate.getSerialNumber();
  }
  
  public String getSigAlgName()
  {
    return this.mDelegate.getSigAlgName();
  }
  
  public String getSigAlgOID()
  {
    return this.mDelegate.getSigAlgOID();
  }
  
  public byte[] getSigAlgParams()
  {
    return this.mDelegate.getSigAlgParams();
  }
  
  public byte[] getSignature()
  {
    return this.mDelegate.getSignature();
  }
  
  public Collection<List<?>> getSubjectAlternativeNames()
    throws CertificateParsingException
  {
    return this.mDelegate.getSubjectAlternativeNames();
  }
  
  public Principal getSubjectDN()
  {
    return this.mDelegate.getSubjectDN();
  }
  
  public boolean[] getSubjectUniqueID()
  {
    return this.mDelegate.getSubjectUniqueID();
  }
  
  public X500Principal getSubjectX500Principal()
  {
    return this.mDelegate.getSubjectX500Principal();
  }
  
  public byte[] getTBSCertificate()
    throws CertificateEncodingException
  {
    return this.mDelegate.getTBSCertificate();
  }
  
  public int getVersion()
  {
    return this.mDelegate.getVersion();
  }
  
  public boolean hasUnsupportedCriticalExtension()
  {
    return this.mDelegate.hasUnsupportedCriticalExtension();
  }
  
  public String toString()
  {
    return this.mDelegate.toString();
  }
  
  public void verify(PublicKey paramPublicKey)
    throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
    this.mDelegate.verify(paramPublicKey);
  }
  
  public void verify(PublicKey paramPublicKey, String paramString)
    throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
    this.mDelegate.verify(paramPublicKey, paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/DelegatingX509Certificate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */