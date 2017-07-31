package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class WifiEnterpriseConfig
  implements Parcelable
{
  public static final String ALTSUBJECT_MATCH_KEY = "altsubject_match";
  public static final String ANON_IDENTITY_KEY = "anonymous_identity";
  public static final String CA_CERT_ALIAS_DELIMITER = " ";
  public static final String CA_CERT_KEY = "ca_cert";
  public static final String CA_CERT_PREFIX = "keystore://CACERT_";
  public static final String CA_PATH_KEY = "ca_path";
  public static final String CLIENT_CERT_KEY = "client_cert";
  public static final String CLIENT_CERT_PREFIX = "keystore://USRCERT_";
  public static final Parcelable.Creator<WifiEnterpriseConfig> CREATOR = new Parcelable.Creator()
  {
    private X509Certificate readCertificate(Parcel paramAnonymousParcel)
    {
      Object localObject = null;
      int i = paramAnonymousParcel.readInt();
      if (i > 0) {}
      try
      {
        localObject = new byte[i];
        paramAnonymousParcel.readByteArray((byte[])localObject);
        localObject = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream((byte[])localObject));
        return (X509Certificate)localObject;
      }
      catch (CertificateException paramAnonymousParcel) {}
      return null;
    }
    
    private X509Certificate[] readCertificates(Parcel paramAnonymousParcel)
    {
      Object localObject = null;
      int j = paramAnonymousParcel.readInt();
      if (j > 0)
      {
        X509Certificate[] arrayOfX509Certificate = new X509Certificate[j];
        int i = 0;
        for (;;)
        {
          localObject = arrayOfX509Certificate;
          if (i >= j) {
            break;
          }
          arrayOfX509Certificate[i] = readCertificate(paramAnonymousParcel);
          i += 1;
        }
      }
      return (X509Certificate[])localObject;
    }
    
    public WifiEnterpriseConfig createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiEnterpriseConfig localWifiEnterpriseConfig = new WifiEnterpriseConfig();
      int j = paramAnonymousParcel.readInt();
      int i = 0;
      while (i < j)
      {
        localObject1 = paramAnonymousParcel.readString();
        String str = paramAnonymousParcel.readString();
        WifiEnterpriseConfig.-get0(localWifiEnterpriseConfig).put(localObject1, str);
        i += 1;
      }
      WifiEnterpriseConfig.-set3(localWifiEnterpriseConfig, paramAnonymousParcel.readInt());
      WifiEnterpriseConfig.-set4(localWifiEnterpriseConfig, paramAnonymousParcel.readInt());
      WifiEnterpriseConfig.-set0(localWifiEnterpriseConfig, readCertificates(paramAnonymousParcel));
      Object localObject1 = null;
      i = paramAnonymousParcel.readInt();
      if (i > 0) {}
      try
      {
        localObject1 = new byte[i];
        paramAnonymousParcel.readByteArray((byte[])localObject1);
        localObject1 = KeyFactory.getInstance(paramAnonymousParcel.readString()).generatePrivate(new PKCS8EncodedKeySpec((byte[])localObject1));
        WifiEnterpriseConfig.-set2(localWifiEnterpriseConfig, (PrivateKey)localObject1);
        WifiEnterpriseConfig.-set1(localWifiEnterpriseConfig, readCertificate(paramAnonymousParcel));
        return localWifiEnterpriseConfig;
      }
      catch (InvalidKeySpecException localInvalidKeySpecException)
      {
        for (;;)
        {
          Object localObject2 = null;
        }
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        for (;;)
        {
          Object localObject3 = null;
        }
      }
    }
    
    public WifiEnterpriseConfig[] newArray(int paramAnonymousInt)
    {
      return new WifiEnterpriseConfig[paramAnonymousInt];
    }
  };
  public static final String DOM_SUFFIX_MATCH_KEY = "domain_suffix_match";
  public static final String EAP_KEY = "eap";
  public static final String EMPTY_VALUE = "NULL";
  public static final String ENGINE_DISABLE = "0";
  public static final String ENGINE_ENABLE = "1";
  public static final String ENGINE_ID_KEY = "engine_id";
  public static final String ENGINE_ID_KEYSTORE = "keystore";
  public static final String ENGINE_KEY = "engine";
  public static final String IDENTITY_KEY = "identity";
  public static final String KEYSTORES_URI = "keystores://";
  public static final String KEYSTORE_URI = "keystore://";
  public static final String OPP_KEY_CACHING = "proactive_key_caching";
  public static final String PASSWORD_KEY = "password";
  public static final String PHASE2_KEY = "phase2";
  public static final String PLMN_KEY = "plmn";
  public static final String PRIVATE_KEY_ID_KEY = "key_id";
  public static final String REALM_KEY = "realm";
  public static final String SUBJECT_MATCH_KEY = "subject_match";
  private static final String[] SUPPLICANT_CONFIG_KEYS = { "identity", "anonymous_identity", "password", "client_cert", "ca_cert", "subject_match", "engine", "engine_id", "key_id", "altsubject_match", "domain_suffix_match", "ca_path" };
  private static final String TAG = "WifiEnterpriseConfig";
  private X509Certificate[] mCaCerts;
  private X509Certificate mClientCertificate;
  private PrivateKey mClientPrivateKey;
  private int mEapMethod = -1;
  private HashMap<String, String> mFields = new HashMap();
  private int mPhase2Method = 0;
  
  public WifiEnterpriseConfig() {}
  
  public WifiEnterpriseConfig(WifiEnterpriseConfig paramWifiEnterpriseConfig)
  {
    Iterator localIterator = paramWifiEnterpriseConfig.mFields.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      this.mFields.put(str, (String)paramWifiEnterpriseConfig.mFields.get(str));
    }
    this.mEapMethod = paramWifiEnterpriseConfig.mEapMethod;
    this.mPhase2Method = paramWifiEnterpriseConfig.mPhase2Method;
  }
  
  private String convertToQuotedString(String paramString)
  {
    return "\"" + paramString + "\"";
  }
  
  public static String decodeCaCertificateAlias(String paramString)
  {
    Object localObject = new byte[paramString.length() >> 1];
    int j = 0;
    int i = 0;
    while (j < paramString.length())
    {
      localObject[i] = ((byte)Integer.parseInt(paramString.substring(j, j + 2), 16));
      j += 2;
      i += 1;
    }
    try
    {
      localObject = new String((byte[])localObject, StandardCharsets.UTF_8);
      return (String)localObject;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      localNumberFormatException.printStackTrace();
    }
    return paramString;
  }
  
  public static String encodeCaCertificateAlias(String paramString)
  {
    paramString = paramString.getBytes(StandardCharsets.UTF_8);
    StringBuilder localStringBuilder = new StringBuilder(paramString.length * 2);
    int j = paramString.length;
    int i = 0;
    while (i < j)
    {
      localStringBuilder.append(String.format("%02x", new Object[] { Integer.valueOf(paramString[i] & 0xFF) }));
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private int getStringIndex(String[] paramArrayOfString, String paramString, int paramInt)
  {
    if (TextUtils.isEmpty(paramString)) {
      return paramInt;
    }
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      if (paramString.equals(paramArrayOfString[i])) {
        return i;
      }
      i += 1;
    }
    return paramInt;
  }
  
  private boolean isEapMethodValid()
  {
    if (this.mEapMethod == -1)
    {
      Log.e("WifiEnterpriseConfig", "WiFi enterprise configuration is invalid as it supplies no EAP method.");
      return false;
    }
    if ((this.mEapMethod < 0) || (this.mEapMethod >= Eap.strings.length))
    {
      Log.e("WifiEnterpriseConfig", "mEapMethod is invald for WiFi enterprise configuration: " + this.mEapMethod);
      return false;
    }
    if ((this.mPhase2Method < 0) || (this.mPhase2Method >= Phase2.strings.length))
    {
      Log.e("WifiEnterpriseConfig", "mPhase2Method is invald for WiFi enterprise configuration: " + this.mPhase2Method);
      return false;
    }
    return true;
  }
  
  private String removeDoubleQuotes(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return "";
    }
    int i = paramString.length();
    if ((i > 1) && (paramString.charAt(0) == '"') && (paramString.charAt(i - 1) == '"')) {
      return paramString.substring(1, i - 1);
    }
    return paramString;
  }
  
  private void writeCertificate(Parcel paramParcel, X509Certificate paramX509Certificate)
  {
    if (paramX509Certificate != null) {
      try
      {
        paramX509Certificate = paramX509Certificate.getEncoded();
        paramParcel.writeInt(paramX509Certificate.length);
        paramParcel.writeByteArray(paramX509Certificate);
        return;
      }
      catch (CertificateEncodingException paramX509Certificate)
      {
        paramParcel.writeInt(0);
        return;
      }
    }
    paramParcel.writeInt(0);
  }
  
  private void writeCertificates(Parcel paramParcel, X509Certificate[] paramArrayOfX509Certificate)
  {
    int i;
    if ((paramArrayOfX509Certificate != null) && (paramArrayOfX509Certificate.length != 0))
    {
      paramParcel.writeInt(paramArrayOfX509Certificate.length);
      i = 0;
    }
    while (i < paramArrayOfX509Certificate.length)
    {
      writeCertificate(paramParcel, paramArrayOfX509Certificate[i]);
      i += 1;
      continue;
      paramParcel.writeInt(0);
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getAltSubjectMatch()
  {
    return getFieldValue("altsubject_match", "");
  }
  
  public String getAnonymousIdentity()
  {
    return getFieldValue("anonymous_identity", "");
  }
  
  public X509Certificate getCaCertificate()
  {
    if ((this.mCaCerts != null) && (this.mCaCerts.length > 0)) {
      return this.mCaCerts[0];
    }
    return null;
  }
  
  public String getCaCertificateAlias()
  {
    return getFieldValue("ca_cert", "keystore://CACERT_");
  }
  
  public String[] getCaCertificateAliases()
  {
    Object localObject = getFieldValue("ca_cert", "");
    if (((String)localObject).startsWith("keystore://CACERT_")) {
      return new String[] { getFieldValue("ca_cert", "keystore://CACERT_") };
    }
    if (((String)localObject).startsWith("keystores://"))
    {
      localObject = TextUtils.split(((String)localObject).substring("keystores://".length()), " ");
      int i = 0;
      while (i < localObject.length)
      {
        localObject[i] = decodeCaCertificateAlias(localObject[i]);
        if (localObject[i].startsWith("CACERT_")) {
          localObject[i] = localObject[i].substring("CACERT_".length());
        }
        i += 1;
      }
      if (localObject.length != 0) {
        return (String[])localObject;
      }
      return null;
    }
    if (TextUtils.isEmpty((CharSequence)localObject)) {
      return null;
    }
    return new String[] { localObject };
  }
  
  public X509Certificate[] getCaCertificates()
  {
    if ((this.mCaCerts != null) && (this.mCaCerts.length > 0)) {
      return this.mCaCerts;
    }
    return null;
  }
  
  public String getCaPath()
  {
    return getFieldValue("ca_path", "");
  }
  
  public X509Certificate getClientCertificate()
  {
    return this.mClientCertificate;
  }
  
  public String getClientCertificateAlias()
  {
    return getFieldValue("client_cert", "keystore://USRCERT_");
  }
  
  public PrivateKey getClientPrivateKey()
  {
    return this.mClientPrivateKey;
  }
  
  public String getDomainSuffixMatch()
  {
    return getFieldValue("domain_suffix_match", "");
  }
  
  public int getEapMethod()
  {
    return this.mEapMethod;
  }
  
  public String getFieldValue(String paramString1, String paramString2)
  {
    paramString1 = (String)this.mFields.get(paramString1);
    if ((TextUtils.isEmpty(paramString1)) || ("NULL".equals(paramString1))) {
      return "";
    }
    paramString1 = removeDoubleQuotes(paramString1);
    if (paramString1.startsWith(paramString2)) {
      return paramString1.substring(paramString2.length());
    }
    return paramString1;
  }
  
  public String getIdentity()
  {
    return getFieldValue("identity", "");
  }
  
  public String getKeyId(WifiEnterpriseConfig paramWifiEnterpriseConfig)
  {
    if (this.mEapMethod == -1)
    {
      if (paramWifiEnterpriseConfig != null) {
        return paramWifiEnterpriseConfig.getKeyId(null);
      }
      return "NULL";
    }
    if (!isEapMethodValid()) {
      return "NULL";
    }
    return Eap.strings[this.mEapMethod] + "_" + Phase2.strings[this.mPhase2Method];
  }
  
  public String getPassword()
  {
    return getFieldValue("password", "");
  }
  
  public int getPhase2Method()
  {
    return this.mPhase2Method;
  }
  
  public String getPlmn()
  {
    return getFieldValue("plmn", "");
  }
  
  public String getRealm()
  {
    return getFieldValue("realm", "");
  }
  
  public String getSubjectMatch()
  {
    return getFieldValue("subject_match", "");
  }
  
  public void loadFromSupplicant(SupplicantLoader paramSupplicantLoader)
  {
    Object localObject = SUPPLICANT_CONFIG_KEYS;
    int j = localObject.length;
    int i = 0;
    if (i < j)
    {
      String str1 = localObject[i];
      String str2 = paramSupplicantLoader.loadValue(str1);
      if (str2 == null) {
        this.mFields.put(str1, "NULL");
      }
      for (;;)
      {
        i += 1;
        break;
        this.mFields.put(str1, str2);
      }
    }
    localObject = paramSupplicantLoader.loadValue("eap");
    this.mEapMethod = getStringIndex(Eap.strings, (String)localObject, -1);
    localObject = removeDoubleQuotes(paramSupplicantLoader.loadValue("phase2"));
    if (((String)localObject).startsWith("auth=")) {
      paramSupplicantLoader = ((String)localObject).substring("auth=".length());
    }
    for (;;)
    {
      this.mPhase2Method = getStringIndex(Phase2.strings, paramSupplicantLoader, 0);
      return;
      paramSupplicantLoader = (SupplicantLoader)localObject;
      if (((String)localObject).startsWith("autheap=")) {
        paramSupplicantLoader = ((String)localObject).substring("autheap=".length());
      }
    }
  }
  
  public void resetCaCertificate()
  {
    this.mCaCerts = null;
  }
  
  public void resetClientKeyEntry()
  {
    this.mClientPrivateKey = null;
    this.mClientCertificate = null;
  }
  
  public boolean saveToSupplicant(SupplicantSaver paramSupplicantSaver)
  {
    int j = 1;
    if (!isEapMethodValid()) {
      return false;
    }
    int i;
    if ((this.mEapMethod == 4) || (this.mEapMethod == 5)) {
      i = 1;
    }
    Object localObject;
    for (;;)
    {
      localObject = this.mFields.keySet().iterator();
      String str;
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
        str = (String)((Iterator)localObject).next();
      } while (((i != 0) && ("anonymous_identity".equals(str))) || (paramSupplicantSaver.saveValue(str, (String)this.mFields.get(str))));
      return false;
      if (this.mEapMethod == 6) {
        i = 1;
      } else {
        i = 0;
      }
    }
    if (!paramSupplicantSaver.saveValue("eap", Eap.strings[this.mEapMethod])) {
      return false;
    }
    if ((this.mEapMethod != 1) && (this.mPhase2Method != 0))
    {
      if ((this.mEapMethod == 2) && (this.mPhase2Method == 4))
      {
        i = j;
        if (i == 0) {
          break label230;
        }
      }
      label230:
      for (localObject = "autheap=";; localObject = "auth=")
      {
        return paramSupplicantSaver.saveValue("phase2", convertToQuotedString((String)localObject + Phase2.strings[this.mPhase2Method]));
        i = 0;
        break;
      }
    }
    if (this.mPhase2Method == 0) {
      return paramSupplicantSaver.saveValue("phase2", null);
    }
    Log.e("WifiEnterpriseConfig", "WiFi enterprise configuration is invalid as it supplies a phase 2 method but the phase1 method does not support it.");
    return false;
  }
  
  public void setAltSubjectMatch(String paramString)
  {
    setFieldValue("altsubject_match", paramString, "");
  }
  
  public void setAnonymousIdentity(String paramString)
  {
    setFieldValue("anonymous_identity", paramString, "");
  }
  
  public void setCaCertificate(X509Certificate paramX509Certificate)
  {
    if (paramX509Certificate != null)
    {
      if (paramX509Certificate.getBasicConstraints() >= 0)
      {
        this.mCaCerts = new X509Certificate[] { paramX509Certificate };
        return;
      }
      throw new IllegalArgumentException("Not a CA certificate");
    }
    this.mCaCerts = null;
  }
  
  public void setCaCertificateAlias(String paramString)
  {
    setFieldValue("ca_cert", paramString, "keystore://CACERT_");
  }
  
  public void setCaCertificateAliases(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null)
    {
      setFieldValue("ca_cert", null, "keystore://CACERT_");
      return;
    }
    if (paramArrayOfString.length == 1)
    {
      setCaCertificateAlias(paramArrayOfString[0]);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      if (i > 0) {
        localStringBuilder.append(" ");
      }
      localStringBuilder.append(encodeCaCertificateAlias("CACERT_" + paramArrayOfString[i]));
      i += 1;
    }
    setFieldValue("ca_cert", localStringBuilder.toString(), "keystores://");
  }
  
  public void setCaCertificates(X509Certificate[] paramArrayOfX509Certificate)
  {
    if (paramArrayOfX509Certificate != null)
    {
      X509Certificate[] arrayOfX509Certificate = new X509Certificate[paramArrayOfX509Certificate.length];
      int i = 0;
      while (i < paramArrayOfX509Certificate.length) {
        if (paramArrayOfX509Certificate[i].getBasicConstraints() >= 0)
        {
          arrayOfX509Certificate[i] = paramArrayOfX509Certificate[i];
          i += 1;
        }
        else
        {
          throw new IllegalArgumentException("Not a CA certificate");
        }
      }
      this.mCaCerts = arrayOfX509Certificate;
      return;
    }
    this.mCaCerts = null;
  }
  
  public void setCaPath(String paramString)
  {
    setFieldValue("ca_path", paramString);
  }
  
  public void setClientCertificateAlias(String paramString)
  {
    setFieldValue("client_cert", paramString, "keystore://USRCERT_");
    setFieldValue("key_id", paramString, "USRPKEY_");
    if (TextUtils.isEmpty(paramString))
    {
      this.mFields.put("engine", "0");
      this.mFields.put("engine_id", "NULL");
      return;
    }
    this.mFields.put("engine", "1");
    this.mFields.put("engine_id", convertToQuotedString("keystore"));
  }
  
  public void setClientKeyEntry(PrivateKey paramPrivateKey, X509Certificate paramX509Certificate)
  {
    if (paramX509Certificate != null)
    {
      if (paramX509Certificate.getBasicConstraints() != -1) {
        throw new IllegalArgumentException("Cannot be a CA certificate");
      }
      if (paramPrivateKey == null) {
        throw new IllegalArgumentException("Client cert without a private key");
      }
      if (paramPrivateKey.getEncoded() == null) {
        throw new IllegalArgumentException("Private key cannot be encoded");
      }
    }
    this.mClientPrivateKey = paramPrivateKey;
    this.mClientCertificate = paramX509Certificate;
  }
  
  public void setDomainSuffixMatch(String paramString)
  {
    setFieldValue("domain_suffix_match", paramString);
  }
  
  public void setEapMethod(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown EAP method");
    case 1: 
    case 7: 
      setPhase2Method(0);
    }
    this.mEapMethod = paramInt;
    this.mFields.put("proactive_key_caching", "1");
  }
  
  public void setFieldValue(String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString2))
    {
      this.mFields.put(paramString1, "NULL");
      return;
    }
    this.mFields.put(paramString1, convertToQuotedString(paramString2));
  }
  
  public void setFieldValue(String paramString1, String paramString2, String paramString3)
  {
    if (TextUtils.isEmpty(paramString2))
    {
      this.mFields.put(paramString1, "NULL");
      return;
    }
    this.mFields.put(paramString1, convertToQuotedString(paramString3 + paramString2));
  }
  
  public void setIdentity(String paramString)
  {
    setFieldValue("identity", paramString, "");
  }
  
  public void setPassword(String paramString)
  {
    setFieldValue("password", paramString, "");
  }
  
  public void setPhase2Method(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown Phase 2 method");
    }
    this.mPhase2Method = paramInt;
  }
  
  public void setPlmn(String paramString)
  {
    setFieldValue("plmn", paramString, "");
  }
  
  public void setRealm(String paramString)
  {
    setFieldValue("realm", paramString, "");
  }
  
  public void setSubjectMatch(String paramString)
  {
    setFieldValue("subject_match", paramString, "");
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = this.mFields.keySet().iterator();
    if (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      if ("password".equals(str2)) {}
      for (String str1 = "<removed>";; str1 = (String)this.mFields.get(str2))
      {
        localStringBuffer.append(str2).append(" ").append(str1).append("\n");
        break;
      }
    }
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mFields.size());
    Object localObject1 = this.mFields.entrySet().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      paramParcel.writeString((String)((Map.Entry)localObject2).getKey());
      paramParcel.writeString((String)((Map.Entry)localObject2).getValue());
    }
    paramParcel.writeInt(this.mEapMethod);
    paramParcel.writeInt(this.mPhase2Method);
    writeCertificates(paramParcel, this.mCaCerts);
    if (this.mClientPrivateKey != null)
    {
      localObject1 = this.mClientPrivateKey.getAlgorithm();
      localObject2 = this.mClientPrivateKey.getEncoded();
      paramParcel.writeInt(localObject2.length);
      paramParcel.writeByteArray((byte[])localObject2);
      paramParcel.writeString((String)localObject1);
    }
    for (;;)
    {
      writeCertificate(paramParcel, this.mClientCertificate);
      return;
      paramParcel.writeInt(0);
    }
  }
  
  public static final class Eap
  {
    public static final int AKA = 5;
    public static final int AKA_PRIME = 6;
    public static final int NONE = -1;
    public static final int PEAP = 0;
    public static final int PWD = 3;
    public static final int SIM = 4;
    public static final int TLS = 1;
    public static final int TTLS = 2;
    public static final int UNAUTH_TLS = 7;
    public static final String[] strings = { "PEAP", "TLS", "TTLS", "PWD", "SIM", "AKA", "AKA'", "WFA-UNAUTH-TLS" };
  }
  
  public static final class Phase2
  {
    private static final String AUTHEAP_PREFIX = "autheap=";
    private static final String AUTH_PREFIX = "auth=";
    public static final int GTC = 4;
    public static final int MSCHAP = 2;
    public static final int MSCHAPV2 = 3;
    public static final int NONE = 0;
    public static final int PAP = 1;
    public static final String[] strings = { "NULL", "PAP", "MSCHAP", "MSCHAPV2", "GTC" };
  }
  
  public static abstract interface SupplicantLoader
  {
    public abstract String loadValue(String paramString);
  }
  
  public static abstract interface SupplicantSaver
  {
    public abstract boolean saveValue(String paramString1, String paramString2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiEnterpriseConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */