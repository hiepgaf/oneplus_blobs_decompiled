package android.security.keystore;

import android.security.Credentials;
import android.security.KeyPairGeneratorSpec;
import android.security.KeyStore;
import android.security.KeyStore.State;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.KeymasterCertificateChain;
import com.android.internal.util.ArrayUtils;
import com.android.org.bouncycastle.x509.X509V3CertificateGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import libcore.util.EmptyArray;

public abstract class AndroidKeyStoreKeyPairGeneratorSpi
  extends KeyPairGeneratorSpi
{
  private static final int EC_DEFAULT_KEY_SIZE = 256;
  private static final int RSA_DEFAULT_KEY_SIZE = 2048;
  private static final int RSA_MAX_KEY_SIZE = 8192;
  private static final int RSA_MIN_KEY_SIZE = 512;
  private static final int SP_RSA_MAX_KEY_SIZE = 2048;
  private static final int SP_RSA_MIN_KEY_SIZE = 2048;
  private static final List<String> SUPPORTED_EC_NIST_CURVE_NAMES;
  private static final Map<String, Integer> SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE = new HashMap();
  private static final List<Integer> SUPPORTED_EC_NIST_CURVE_SIZES;
  private static final Map<String, Integer> SUPPORTED_SP_EC_NIST_CURVE_NAME_TO_SIZE = new HashMap();
  private static final List<Integer> SUPPORTED_SP_EC_NIST_CURVE_SIZES;
  private boolean mEncryptionAtRestRequired;
  private String mEntryAlias;
  private int mEntryUid;
  private String mJcaKeyAlgorithm;
  private int mKeySizeBits;
  private KeyStore mKeyStore;
  private int mKeymasterAlgorithm = -1;
  private int[] mKeymasterBlockModes;
  private int[] mKeymasterDigests;
  private int[] mKeymasterEncryptionPaddings;
  private int[] mKeymasterPurposes;
  private int[] mKeymasterSignaturePaddings;
  private final int mOriginalKeymasterAlgorithm;
  private BigInteger mRSAPublicExponent;
  private SecureRandom mRng;
  private KeyGenParameterSpec mSpec;
  
  static
  {
    SUPPORTED_EC_NIST_CURVE_NAMES = new ArrayList();
    SUPPORTED_EC_NIST_CURVE_SIZES = new ArrayList();
    SUPPORTED_SP_EC_NIST_CURVE_SIZES = new ArrayList();
    SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.put("p-224", Integer.valueOf(224));
    SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.put("secp224r1", Integer.valueOf(224));
    SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.put("p-256", Integer.valueOf(256));
    SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.put("secp256r1", Integer.valueOf(256));
    SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.put("prime256v1", Integer.valueOf(256));
    SUPPORTED_SP_EC_NIST_CURVE_NAME_TO_SIZE.put("p-256", Integer.valueOf(256));
    SUPPORTED_SP_EC_NIST_CURVE_NAME_TO_SIZE.put("secp256r1", Integer.valueOf(256));
    SUPPORTED_SP_EC_NIST_CURVE_NAME_TO_SIZE.put("prime256v1", Integer.valueOf(256));
    SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.put("p-384", Integer.valueOf(384));
    SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.put("secp384r1", Integer.valueOf(384));
    SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.put("p-521", Integer.valueOf(521));
    SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.put("secp521r1", Integer.valueOf(521));
    SUPPORTED_EC_NIST_CURVE_NAMES.addAll(SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.keySet());
    Collections.sort(SUPPORTED_EC_NIST_CURVE_NAMES);
    SUPPORTED_EC_NIST_CURVE_SIZES.addAll(new HashSet(SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.values()));
    Collections.sort(SUPPORTED_EC_NIST_CURVE_SIZES);
    SUPPORTED_SP_EC_NIST_CURVE_SIZES.addAll(new HashSet(SUPPORTED_SP_EC_NIST_CURVE_NAME_TO_SIZE.values()));
    Collections.sort(SUPPORTED_SP_EC_NIST_CURVE_SIZES);
  }
  
  protected AndroidKeyStoreKeyPairGeneratorSpi(int paramInt)
  {
    this.mOriginalKeymasterAlgorithm = paramInt;
  }
  
  private void addAlgorithmSpecificParameters(KeymasterArguments paramKeymasterArguments)
  {
    switch (this.mKeymasterAlgorithm)
    {
    case 2: 
    default: 
      throw new ProviderException("Unsupported algorithm: " + this.mKeymasterAlgorithm);
    case 1: 
      paramKeymasterArguments.addUnsignedLong(1342177480, this.mRSAPublicExponent);
    }
  }
  
  private static void checkSecureProcessorValidKeySize(int paramInt1, int paramInt2)
    throws InvalidAlgorithmParameterException
  {
    switch (paramInt1)
    {
    case 2: 
    default: 
      throw new ProviderException("Unsupported algorithm: " + paramInt1);
    case 3: 
      if (!SUPPORTED_SP_EC_NIST_CURVE_SIZES.contains(Integer.valueOf(paramInt2))) {
        throw new InvalidAlgorithmParameterException("Unsupported EC key size: " + paramInt2 + " bits. Supported: " + SUPPORTED_SP_EC_NIST_CURVE_SIZES);
      }
      break;
    case 1: 
      if ((paramInt2 < 2048) || (paramInt2 > 2048)) {
        throw new InvalidAlgorithmParameterException("RSA key size must be >= 2048 and <= 2048");
      }
      break;
    }
  }
  
  private static void checkValidKeySize(int paramInt1, int paramInt2)
    throws InvalidAlgorithmParameterException
  {
    switch (paramInt1)
    {
    case 2: 
    default: 
      throw new ProviderException("Unsupported algorithm: " + paramInt1);
    case 3: 
      if (!SUPPORTED_EC_NIST_CURVE_SIZES.contains(Integer.valueOf(paramInt2))) {
        throw new InvalidAlgorithmParameterException("Unsupported EC key size: " + paramInt2 + " bits. Supported: " + SUPPORTED_EC_NIST_CURVE_SIZES);
      }
      break;
    case 1: 
      if ((paramInt2 < 512) || (paramInt2 > 8192)) {
        throw new InvalidAlgorithmParameterException("RSA key size must be >= 512 and <= 8192");
      }
      break;
    }
  }
  
  private KeymasterArguments constructKeyGenerationArguments()
  {
    KeymasterArguments localKeymasterArguments = new KeymasterArguments();
    localKeymasterArguments.addUnsignedInt(805306371, this.mKeySizeBits);
    localKeymasterArguments.addEnum(268435458, this.mKeymasterAlgorithm);
    localKeymasterArguments.addEnums(536870913, this.mKeymasterPurposes);
    localKeymasterArguments.addEnums(536870916, this.mKeymasterBlockModes);
    localKeymasterArguments.addEnums(536870918, this.mKeymasterEncryptionPaddings);
    localKeymasterArguments.addEnums(536870918, this.mKeymasterSignaturePaddings);
    localKeymasterArguments.addEnums(536870917, this.mKeymasterDigests);
    KeymasterUtils.addUserAuthArgs(localKeymasterArguments, this.mSpec.isUserAuthenticationRequired(), this.mSpec.getUserAuthenticationValidityDurationSeconds(), this.mSpec.isUserAuthenticationValidWhileOnBody(), this.mSpec.isInvalidatedByBiometricEnrollment());
    localKeymasterArguments.addDateIfNotNull(1610613136, this.mSpec.getKeyValidityStart());
    localKeymasterArguments.addDateIfNotNull(1610613137, this.mSpec.getKeyValidityForOriginationEnd());
    localKeymasterArguments.addDateIfNotNull(1610613138, this.mSpec.getKeyValidityForConsumptionEnd());
    addAlgorithmSpecificParameters(localKeymasterArguments);
    if (this.mSpec.isUniqueIdIncluded()) {
      localKeymasterArguments.addBoolean(1879048394);
    }
    if (this.mSpec.isUseSecureProcessor()) {
      localKeymasterArguments.addBoolean(1879063192);
    }
    return localKeymasterArguments;
  }
  
  private Iterable<byte[]> createCertificateChain(String paramString, KeyPair paramKeyPair)
    throws ProviderException
  {
    byte[] arrayOfByte = this.mSpec.getAttestationChallenge();
    if (arrayOfByte != null)
    {
      KeymasterArguments localKeymasterArguments = new KeymasterArguments();
      localKeymasterArguments.addBytes(-1879047484, arrayOfByte);
      return getAttestationChain(paramString, paramKeyPair, localKeymasterArguments);
    }
    return Collections.singleton(generateSelfSignedCertificateBytes(paramKeyPair));
  }
  
  private void generateKeystoreKeyPair(String paramString, KeymasterArguments paramKeymasterArguments, byte[] paramArrayOfByte, int paramInt)
    throws ProviderException
  {
    KeyCharacteristics localKeyCharacteristics = new KeyCharacteristics();
    paramInt = this.mKeyStore.generateKey(paramString, paramKeymasterArguments, paramArrayOfByte, this.mEntryUid, paramInt, localKeyCharacteristics);
    if (paramInt != 1) {
      throw new ProviderException("Failed to generate key pair", KeyStore.getKeyStoreException(paramInt));
    }
  }
  
  private X509Certificate generateSelfSignedCertificate(PrivateKey paramPrivateKey, PublicKey paramPublicKey)
    throws CertificateParsingException, IOException
  {
    String str = getCertificateSignatureAlgorithm(this.mKeymasterAlgorithm, this.mKeySizeBits, this.mSpec);
    if (str == null) {
      return generateSelfSignedCertificateWithFakeSignature(paramPublicKey);
    }
    try
    {
      paramPrivateKey = generateSelfSignedCertificateWithValidSignature(paramPrivateKey, paramPublicKey, str);
      return paramPrivateKey;
    }
    catch (Exception paramPrivateKey) {}
    return generateSelfSignedCertificateWithFakeSignature(paramPublicKey);
  }
  
  private byte[] generateSelfSignedCertificateBytes(KeyPair paramKeyPair)
    throws ProviderException
  {
    try
    {
      paramKeyPair = generateSelfSignedCertificate(paramKeyPair.getPrivate(), paramKeyPair.getPublic()).getEncoded();
      return paramKeyPair;
    }
    catch (CertificateEncodingException paramKeyPair)
    {
      throw new ProviderException("Failed to obtain encoded form of self-signed certificate", paramKeyPair);
    }
    catch (IOException|CertificateParsingException paramKeyPair)
    {
      throw new ProviderException("Failed to generate self-signed certificate", paramKeyPair);
    }
  }
  
  /* Error */
  private X509Certificate generateSelfSignedCertificateWithFakeSignature(PublicKey paramPublicKey)
    throws IOException, CertificateParsingException
  {
    // Byte code:
    //   0: new 372	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator
    //   3: dup
    //   4: invokespecial 373	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:<init>	()V
    //   7: astore 8
    //   9: aload_0
    //   10: getfield 134	android/security/keystore/AndroidKeyStoreKeyPairGeneratorSpi:mKeymasterAlgorithm	I
    //   13: tableswitch	default:+27->40, 1:+174->187, 2:+27->40, 3:+58->71
    //   40: new 140	java/security/ProviderException
    //   43: dup
    //   44: new 142	java/lang/StringBuilder
    //   47: dup
    //   48: invokespecial 143	java/lang/StringBuilder:<init>	()V
    //   51: ldc_w 375
    //   54: invokevirtual 149	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: aload_0
    //   58: getfield 134	android/security/keystore/AndroidKeyStoreKeyPairGeneratorSpi:mKeymasterAlgorithm	I
    //   61: invokevirtual 152	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   64: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   67: invokespecial 159	java/security/ProviderException:<init>	(Ljava/lang/String;)V
    //   70: athrow
    //   71: new 377	com/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier
    //   74: dup
    //   75: getstatic 383	com/android/org/bouncycastle/asn1/x9/X9ObjectIdentifiers:ecdsa_with_SHA256	Lcom/android/org/bouncycastle/asn1/ASN1ObjectIdentifier;
    //   78: invokespecial 386	com/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier:<init>	(Lcom/android/org/bouncycastle/asn1/ASN1ObjectIdentifier;)V
    //   81: astore_2
    //   82: new 388	com/android/org/bouncycastle/asn1/ASN1EncodableVector
    //   85: dup
    //   86: invokespecial 389	com/android/org/bouncycastle/asn1/ASN1EncodableVector:<init>	()V
    //   89: astore_3
    //   90: aload_3
    //   91: new 391	com/android/org/bouncycastle/asn1/DERInteger
    //   94: dup
    //   95: lconst_0
    //   96: invokespecial 394	com/android/org/bouncycastle/asn1/DERInteger:<init>	(J)V
    //   99: invokevirtual 398	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   102: aload_3
    //   103: new 391	com/android/org/bouncycastle/asn1/DERInteger
    //   106: dup
    //   107: lconst_0
    //   108: invokespecial 394	com/android/org/bouncycastle/asn1/DERInteger:<init>	(J)V
    //   111: invokevirtual 398	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   114: new 400	com/android/org/bouncycastle/asn1/DERSequence
    //   117: dup
    //   118: invokespecial 401	com/android/org/bouncycastle/asn1/DERSequence:<init>	()V
    //   121: invokevirtual 404	com/android/org/bouncycastle/asn1/ASN1Object:getEncoded	()[B
    //   124: astore_3
    //   125: aconst_null
    //   126: astore 4
    //   128: aconst_null
    //   129: astore 6
    //   131: aconst_null
    //   132: astore 7
    //   134: aconst_null
    //   135: astore 5
    //   137: new 406	com/android/org/bouncycastle/asn1/ASN1InputStream
    //   140: dup
    //   141: aload_1
    //   142: invokeinterface 409 1 0
    //   147: invokespecial 412	com/android/org/bouncycastle/asn1/ASN1InputStream:<init>	([B)V
    //   150: astore_1
    //   151: aload 8
    //   153: aload_1
    //   154: invokevirtual 416	com/android/org/bouncycastle/asn1/ASN1InputStream:readObject	()Lcom/android/org/bouncycastle/asn1/ASN1Primitive;
    //   157: invokestatic 422	com/android/org/bouncycastle/asn1/x509/SubjectPublicKeyInfo:getInstance	(Ljava/lang/Object;)Lcom/android/org/bouncycastle/asn1/x509/SubjectPublicKeyInfo;
    //   160: invokevirtual 426	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setSubjectPublicKeyInfo	(Lcom/android/org/bouncycastle/asn1/x509/SubjectPublicKeyInfo;)V
    //   163: aload 6
    //   165: astore 4
    //   167: aload_1
    //   168: ifnull +11 -> 179
    //   171: aload_1
    //   172: invokevirtual 431	java/io/FilterInputStream:close	()V
    //   175: aload 6
    //   177: astore 4
    //   179: aload 4
    //   181: ifnull +99 -> 280
    //   184: aload 4
    //   186: athrow
    //   187: new 377	com/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier
    //   190: dup
    //   191: getstatic 436	com/android/org/bouncycastle/asn1/pkcs/PKCSObjectIdentifiers:sha256WithRSAEncryption	Lcom/android/org/bouncycastle/asn1/ASN1ObjectIdentifier;
    //   194: getstatic 442	com/android/org/bouncycastle/asn1/DERNull:INSTANCE	Lcom/android/org/bouncycastle/asn1/DERNull;
    //   197: invokespecial 445	com/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier:<init>	(Lcom/android/org/bouncycastle/asn1/ASN1ObjectIdentifier;Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   200: astore_2
    //   201: iconst_1
    //   202: newarray <illegal type>
    //   204: astore_3
    //   205: goto -80 -> 125
    //   208: astore 4
    //   210: goto -31 -> 179
    //   213: astore_2
    //   214: aload 5
    //   216: astore_1
    //   217: aload_2
    //   218: athrow
    //   219: astore 4
    //   221: aload_2
    //   222: astore_3
    //   223: aload 4
    //   225: astore_2
    //   226: aload_3
    //   227: astore 4
    //   229: aload_1
    //   230: ifnull +10 -> 240
    //   233: aload_1
    //   234: invokevirtual 431	java/io/FilterInputStream:close	()V
    //   237: aload_3
    //   238: astore 4
    //   240: aload 4
    //   242: ifnull +36 -> 278
    //   245: aload 4
    //   247: athrow
    //   248: astore_1
    //   249: aload_3
    //   250: ifnonnull +9 -> 259
    //   253: aload_1
    //   254: astore 4
    //   256: goto -16 -> 240
    //   259: aload_3
    //   260: astore 4
    //   262: aload_3
    //   263: aload_1
    //   264: if_acmpeq -24 -> 240
    //   267: aload_3
    //   268: aload_1
    //   269: invokevirtual 449	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   272: aload_3
    //   273: astore 4
    //   275: goto -35 -> 240
    //   278: aload_2
    //   279: athrow
    //   280: aload 8
    //   282: new 451	com/android/org/bouncycastle/asn1/ASN1Integer
    //   285: dup
    //   286: aload_0
    //   287: getfield 224	android/security/keystore/AndroidKeyStoreKeyPairGeneratorSpi:mSpec	Landroid/security/keystore/KeyGenParameterSpec;
    //   290: invokevirtual 455	android/security/keystore/KeyGenParameterSpec:getCertificateSerialNumber	()Ljava/math/BigInteger;
    //   293: invokespecial 458	com/android/org/bouncycastle/asn1/ASN1Integer:<init>	(Ljava/math/BigInteger;)V
    //   296: invokevirtual 462	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setSerialNumber	(Lcom/android/org/bouncycastle/asn1/ASN1Integer;)V
    //   299: new 464	com/android/org/bouncycastle/jce/X509Principal
    //   302: dup
    //   303: aload_0
    //   304: getfield 224	android/security/keystore/AndroidKeyStoreKeyPairGeneratorSpi:mSpec	Landroid/security/keystore/KeyGenParameterSpec;
    //   307: invokevirtual 468	android/security/keystore/KeyGenParameterSpec:getCertificateSubject	()Ljavax/security/auth/x500/X500Principal;
    //   310: invokevirtual 471	javax/security/auth/x500/X500Principal:getEncoded	()[B
    //   313: invokespecial 472	com/android/org/bouncycastle/jce/X509Principal:<init>	([B)V
    //   316: astore_1
    //   317: aload 8
    //   319: aload_1
    //   320: invokevirtual 476	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setSubject	(Lcom/android/org/bouncycastle/asn1/x509/X509Name;)V
    //   323: aload 8
    //   325: aload_1
    //   326: invokevirtual 479	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setIssuer	(Lcom/android/org/bouncycastle/asn1/x509/X509Name;)V
    //   329: aload 8
    //   331: new 481	com/android/org/bouncycastle/asn1/x509/Time
    //   334: dup
    //   335: aload_0
    //   336: getfield 224	android/security/keystore/AndroidKeyStoreKeyPairGeneratorSpi:mSpec	Landroid/security/keystore/KeyGenParameterSpec;
    //   339: invokevirtual 484	android/security/keystore/KeyGenParameterSpec:getCertificateNotBefore	()Ljava/util/Date;
    //   342: invokespecial 487	com/android/org/bouncycastle/asn1/x509/Time:<init>	(Ljava/util/Date;)V
    //   345: invokevirtual 491	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setStartDate	(Lcom/android/org/bouncycastle/asn1/x509/Time;)V
    //   348: aload 8
    //   350: new 481	com/android/org/bouncycastle/asn1/x509/Time
    //   353: dup
    //   354: aload_0
    //   355: getfield 224	android/security/keystore/AndroidKeyStoreKeyPairGeneratorSpi:mSpec	Landroid/security/keystore/KeyGenParameterSpec;
    //   358: invokevirtual 494	android/security/keystore/KeyGenParameterSpec:getCertificateNotAfter	()Ljava/util/Date;
    //   361: invokespecial 487	com/android/org/bouncycastle/asn1/x509/Time:<init>	(Ljava/util/Date;)V
    //   364: invokevirtual 497	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setEndDate	(Lcom/android/org/bouncycastle/asn1/x509/Time;)V
    //   367: aload 8
    //   369: aload_2
    //   370: invokevirtual 501	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setSignature	(Lcom/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier;)V
    //   373: aload 8
    //   375: invokevirtual 505	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:generateTBSCertificate	()Lcom/android/org/bouncycastle/asn1/x509/TBSCertificate;
    //   378: astore_1
    //   379: new 388	com/android/org/bouncycastle/asn1/ASN1EncodableVector
    //   382: dup
    //   383: invokespecial 389	com/android/org/bouncycastle/asn1/ASN1EncodableVector:<init>	()V
    //   386: astore 4
    //   388: aload 4
    //   390: aload_1
    //   391: invokevirtual 398	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   394: aload 4
    //   396: aload_2
    //   397: invokevirtual 398	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   400: aload 4
    //   402: new 507	com/android/org/bouncycastle/asn1/DERBitString
    //   405: dup
    //   406: aload_3
    //   407: invokespecial 508	com/android/org/bouncycastle/asn1/DERBitString:<init>	([B)V
    //   410: invokevirtual 398	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   413: new 510	com/android/org/bouncycastle/jce/provider/X509CertificateObject
    //   416: dup
    //   417: new 400	com/android/org/bouncycastle/asn1/DERSequence
    //   420: dup
    //   421: aload 4
    //   423: invokespecial 513	com/android/org/bouncycastle/asn1/DERSequence:<init>	(Lcom/android/org/bouncycastle/asn1/ASN1EncodableVector;)V
    //   426: invokestatic 518	com/android/org/bouncycastle/asn1/x509/Certificate:getInstance	(Ljava/lang/Object;)Lcom/android/org/bouncycastle/asn1/x509/Certificate;
    //   429: invokespecial 521	com/android/org/bouncycastle/jce/provider/X509CertificateObject:<init>	(Lcom/android/org/bouncycastle/asn1/x509/Certificate;)V
    //   432: areturn
    //   433: astore_2
    //   434: aload 7
    //   436: astore_1
    //   437: aload 4
    //   439: astore_3
    //   440: goto -214 -> 226
    //   443: astore_2
    //   444: aload 4
    //   446: astore_3
    //   447: goto -221 -> 226
    //   450: astore_2
    //   451: goto -234 -> 217
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	454	0	this	AndroidKeyStoreKeyPairGeneratorSpi
    //   0	454	1	paramPublicKey	PublicKey
    //   81	120	2	localAlgorithmIdentifier	com.android.org.bouncycastle.asn1.x509.AlgorithmIdentifier
    //   213	9	2	localThrowable1	Throwable
    //   225	172	2	localObject1	Object
    //   433	1	2	localObject2	Object
    //   443	1	2	localObject3	Object
    //   450	1	2	localThrowable2	Throwable
    //   89	358	3	localObject4	Object
    //   126	59	4	localObject5	Object
    //   208	1	4	localThrowable3	Throwable
    //   219	5	4	localObject6	Object
    //   227	218	4	localObject7	Object
    //   135	80	5	localObject8	Object
    //   129	47	6	localObject9	Object
    //   132	303	7	localObject10	Object
    //   7	367	8	localV3TBSCertificateGenerator	com.android.org.bouncycastle.asn1.x509.V3TBSCertificateGenerator
    // Exception table:
    //   from	to	target	type
    //   171	175	208	java/lang/Throwable
    //   137	151	213	java/lang/Throwable
    //   217	219	219	finally
    //   233	237	248	java/lang/Throwable
    //   137	151	433	finally
    //   151	163	443	finally
    //   151	163	450	java/lang/Throwable
  }
  
  private X509Certificate generateSelfSignedCertificateWithValidSignature(PrivateKey paramPrivateKey, PublicKey paramPublicKey, String paramString)
    throws Exception
  {
    X509V3CertificateGenerator localX509V3CertificateGenerator = new X509V3CertificateGenerator();
    localX509V3CertificateGenerator.setPublicKey(paramPublicKey);
    localX509V3CertificateGenerator.setSerialNumber(this.mSpec.getCertificateSerialNumber());
    localX509V3CertificateGenerator.setSubjectDN(this.mSpec.getCertificateSubject());
    localX509V3CertificateGenerator.setIssuerDN(this.mSpec.getCertificateSubject());
    localX509V3CertificateGenerator.setNotBefore(this.mSpec.getCertificateNotBefore());
    localX509V3CertificateGenerator.setNotAfter(this.mSpec.getCertificateNotAfter());
    localX509V3CertificateGenerator.setSignatureAlgorithm(paramString);
    return localX509V3CertificateGenerator.generate(paramPrivateKey);
  }
  
  private Iterable<byte[]> getAttestationChain(String paramString, KeyPair paramKeyPair, KeymasterArguments paramKeymasterArguments)
    throws ProviderException
  {
    paramKeyPair = new KeymasterCertificateChain();
    int i = this.mKeyStore.attestKey(paramString, paramKeymasterArguments, paramKeyPair);
    if (i != 1) {
      throw new ProviderException("Failed to generate attestation certificate chain", KeyStore.getKeyStoreException(i));
    }
    paramString = paramKeyPair.getCertificates();
    if (paramString.size() < 2) {
      throw new ProviderException("Attestation certificate chain contained " + paramString.size() + " entries. At least two are required.");
    }
    return paramString;
  }
  
  private static Set<Integer> getAvailableKeymasterSignatureDigests(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    int j = 0;
    HashSet localHashSet = new HashSet();
    paramArrayOfString1 = KeyProperties.Digest.allToKeymaster(paramArrayOfString1);
    int k = paramArrayOfString1.length;
    int i = 0;
    while (i < k)
    {
      localHashSet.add(Integer.valueOf(paramArrayOfString1[i]));
      i += 1;
    }
    paramArrayOfString1 = new HashSet();
    paramArrayOfString2 = KeyProperties.Digest.allToKeymaster(paramArrayOfString2);
    k = paramArrayOfString2.length;
    i = j;
    while (i < k)
    {
      paramArrayOfString1.add(Integer.valueOf(paramArrayOfString2[i]));
      i += 1;
    }
    paramArrayOfString1 = new HashSet(paramArrayOfString1);
    paramArrayOfString1.retainAll(localHashSet);
    return paramArrayOfString1;
  }
  
  private static String getCertificateSignatureAlgorithm(int paramInt1, int paramInt2, KeyGenParameterSpec paramKeyGenParameterSpec)
  {
    if ((paramKeyGenParameterSpec.getPurposes() & 0x4) == 0) {
      return null;
    }
    if (paramKeyGenParameterSpec.isUserAuthenticationRequired()) {
      return null;
    }
    if (!paramKeyGenParameterSpec.isDigestsSpecified()) {
      return null;
    }
    int j;
    int k;
    switch (paramInt1)
    {
    case 2: 
    default: 
      throw new ProviderException("Unsupported algorithm: " + paramInt1);
    case 3: 
      paramKeyGenParameterSpec = getAvailableKeymasterSignatureDigests(paramKeyGenParameterSpec.getDigests(), AndroidKeyStoreBCWorkaroundProvider.getSupportedEcdsaSignatureDigests());
      paramInt1 = -1;
      i = -1;
      paramKeyGenParameterSpec = paramKeyGenParameterSpec.iterator();
      for (;;)
      {
        j = paramInt1;
        if (paramKeyGenParameterSpec.hasNext())
        {
          j = ((Integer)paramKeyGenParameterSpec.next()).intValue();
          k = KeymasterUtils.getDigestOutputSizeBits(j);
          if (k != paramInt2) {}
        }
        else
        {
          if (j != -1) {
            break;
          }
          return null;
        }
        if (paramInt1 == -1)
        {
          paramInt1 = j;
          i = k;
        }
        else if (i < paramInt2)
        {
          if (k > i)
          {
            paramInt1 = j;
            i = k;
          }
        }
        else if ((k < i) && (k >= paramInt2))
        {
          paramInt1 = j;
          i = k;
        }
      }
      return KeyProperties.Digest.fromKeymasterToSignatureAlgorithmDigest(j) + "WithECDSA";
    }
    if (!ArrayUtils.contains(KeyProperties.SignaturePadding.allToKeymaster(paramKeyGenParameterSpec.getSignaturePaddings()), 5)) {
      return null;
    }
    paramKeyGenParameterSpec = getAvailableKeymasterSignatureDigests(paramKeyGenParameterSpec.getDigests(), AndroidKeyStoreBCWorkaroundProvider.getSupportedEcdsaSignatureDigests());
    int i = -1;
    paramInt1 = -1;
    paramKeyGenParameterSpec = paramKeyGenParameterSpec.iterator();
    while (paramKeyGenParameterSpec.hasNext())
    {
      k = ((Integer)paramKeyGenParameterSpec.next()).intValue();
      j = KeymasterUtils.getDigestOutputSizeBits(k);
      if (j <= paramInt2 - 240) {
        if (i == -1)
        {
          i = k;
          paramInt1 = j;
        }
        else if (j > paramInt1)
        {
          i = k;
          paramInt1 = j;
        }
      }
    }
    if (i == -1) {
      return null;
    }
    return KeyProperties.Digest.fromKeymasterToSignatureAlgorithmDigest(i) + "WithRSA";
  }
  
  private static int getDefaultKeySize(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
    default: 
      throw new ProviderException("Unsupported algorithm: " + paramInt);
    case 3: 
      return 256;
    }
    return 2048;
  }
  
  private void initAlgorithmSpecificParameters()
    throws InvalidAlgorithmParameterException
  {
    Object localObject2 = this.mSpec.getAlgorithmParameterSpec();
    Object localObject1;
    switch (this.mKeymasterAlgorithm)
    {
    case 2: 
    default: 
      throw new ProviderException("Unsupported algorithm: " + this.mKeymasterAlgorithm);
    case 1: 
      localObject1 = null;
      if ((localObject2 instanceof RSAKeyGenParameterSpec)) {
        if (this.mKeySizeBits == -1)
        {
          this.mKeySizeBits = ((RSAKeyGenParameterSpec)localObject2).getKeysize();
          localObject1 = ((RSAKeyGenParameterSpec)localObject2).getPublicExponent();
        }
      }
      while (localObject2 == null)
      {
        do
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = RSAKeyGenParameterSpec.F4;
          }
          if (((BigInteger)localObject2).compareTo(BigInteger.ZERO) >= 1) {
            break;
          }
          throw new InvalidAlgorithmParameterException("RSA public exponent must be positive: " + localObject2);
        } while (this.mKeySizeBits == ((RSAKeyGenParameterSpec)localObject2).getKeysize());
        throw new InvalidAlgorithmParameterException("RSA key size must match  between " + this.mSpec + " and " + localObject2 + ": " + this.mKeySizeBits + " vs " + ((RSAKeyGenParameterSpec)localObject2).getKeysize());
      }
      throw new InvalidAlgorithmParameterException("RSA may only use RSAKeyGenParameterSpec");
      if (((BigInteger)localObject2).compareTo(KeymasterArguments.UINT64_MAX_VALUE) > 0) {
        throw new InvalidAlgorithmParameterException("Unsupported RSA public exponent: " + localObject2 + ". Maximum supported value: " + KeymasterArguments.UINT64_MAX_VALUE);
      }
      this.mRSAPublicExponent = ((BigInteger)localObject2);
    }
    do
    {
      Integer localInteger;
      do
      {
        return;
        if (!(localObject2 instanceof ECGenParameterSpec)) {
          break;
        }
        localObject1 = ((ECGenParameterSpec)localObject2).getName();
        localInteger = (Integer)SUPPORTED_EC_NIST_CURVE_NAME_TO_SIZE.get(((String)localObject1).toLowerCase(Locale.US));
        if (localInteger == null) {
          throw new InvalidAlgorithmParameterException("Unsupported EC curve name: " + (String)localObject1 + ". Supported: " + SUPPORTED_EC_NIST_CURVE_NAMES);
        }
        if (this.mKeySizeBits == -1)
        {
          this.mKeySizeBits = localInteger.intValue();
          return;
        }
      } while (this.mKeySizeBits == localInteger.intValue());
      throw new InvalidAlgorithmParameterException("EC key size must match  between " + this.mSpec + " and " + localObject2 + ": " + this.mKeySizeBits + " vs " + localInteger);
    } while (localObject2 == null);
    throw new InvalidAlgorithmParameterException("EC may only use ECGenParameterSpec");
  }
  
  private KeyPair loadKeystoreKeyPair(String paramString)
    throws ProviderException
  {
    try
    {
      paramString = AndroidKeyStoreProvider.loadAndroidKeyStoreKeyPairFromKeystore(this.mKeyStore, paramString, this.mEntryUid);
      if (!this.mJcaKeyAlgorithm.equalsIgnoreCase(paramString.getPrivate().getAlgorithm())) {
        throw new ProviderException("Generated key pair algorithm does not match requested algorithm: " + paramString.getPrivate().getAlgorithm() + " vs " + this.mJcaKeyAlgorithm);
      }
    }
    catch (UnrecoverableKeyException paramString)
    {
      throw new ProviderException("Failed to load generated key pair from keystore", paramString);
    }
    return paramString;
  }
  
  private void resetAll()
  {
    this.mEntryAlias = null;
    this.mEntryUid = -1;
    this.mJcaKeyAlgorithm = null;
    this.mKeymasterAlgorithm = -1;
    this.mKeymasterPurposes = null;
    this.mKeymasterBlockModes = null;
    this.mKeymasterEncryptionPaddings = null;
    this.mKeymasterSignaturePaddings = null;
    this.mKeymasterDigests = null;
    this.mKeySizeBits = 0;
    this.mSpec = null;
    this.mRSAPublicExponent = null;
    this.mEncryptionAtRestRequired = false;
    this.mRng = null;
    this.mKeyStore = null;
  }
  
  private void storeCertificate(String paramString1, byte[] paramArrayOfByte, int paramInt, String paramString2)
    throws ProviderException
  {
    paramInt = this.mKeyStore.insert(paramString1 + this.mEntryAlias, paramArrayOfByte, this.mEntryUid, paramInt);
    if (paramInt != 1) {
      throw new ProviderException(paramString2, KeyStore.getKeyStoreException(paramInt));
    }
  }
  
  private void storeCertificateChain(int paramInt, Iterable<byte[]> paramIterable)
    throws ProviderException
  {
    paramIterable = paramIterable.iterator();
    storeCertificate("USRCERT_", (byte[])paramIterable.next(), paramInt, "Failed to store certificate");
    if (!paramIterable.hasNext()) {
      return;
    }
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    while (paramIterable.hasNext())
    {
      byte[] arrayOfByte = (byte[])paramIterable.next();
      localByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
    }
    storeCertificate("CACERT_", localByteArrayOutputStream.toByteArray(), paramInt, "Failed to store attestation CA certificate");
  }
  
  public KeyPair generateKeyPair()
  {
    int i = 0;
    if ((this.mKeyStore == null) || (this.mSpec == null)) {
      throw new IllegalStateException("Not initialized");
    }
    if (this.mEncryptionAtRestRequired) {
      i = 1;
    }
    if (((i & 0x1) != 0) && (this.mKeyStore.state() != KeyStore.State.UNLOCKED)) {
      throw new IllegalStateException("Encryption at rest using secure lock screen credential requested for key pair, but the user has not yet entered the credential");
    }
    Object localObject2 = KeyStoreCryptoOperationUtils.getRandomBytesToMixIntoKeystoreRng(this.mRng, (this.mKeySizeBits + 7) / 8);
    Credentials.deleteAllTypesForAlias(this.mKeyStore, this.mEntryAlias, this.mEntryUid);
    String str = "USRPKEY_" + this.mEntryAlias;
    try
    {
      generateKeystoreKeyPair(str, constructKeyGenerationArguments(), (byte[])localObject2, i);
      localObject2 = loadKeystoreKeyPair(str);
      storeCertificateChain(i, createCertificateChain(str, (KeyPair)localObject2));
      if (1 == 0) {
        Credentials.deleteAllTypesForAlias(this.mKeyStore, this.mEntryAlias, this.mEntryUid);
      }
      return (KeyPair)localObject2;
    }
    finally
    {
      if (0 == 0) {
        Credentials.deleteAllTypesForAlias(this.mKeyStore, this.mEntryAlias, this.mEntryUid);
      }
    }
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom)
  {
    throw new IllegalArgumentException(KeyGenParameterSpec.class.getName() + " or " + KeyPairGeneratorSpec.class.getName() + " required to initialize this KeyPairGenerator");
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {
    resetAll();
    if (paramAlgorithmParameterSpec == null) {
      try
      {
        throw new InvalidAlgorithmParameterException("Must supply params of type " + KeyGenParameterSpec.class.getName() + " or " + KeyPairGeneratorSpec.class.getName());
      }
      finally
      {
        if (0 == 0) {
          resetAll();
        }
      }
    }
    boolean bool = false;
    int i = this.mOriginalKeymasterAlgorithm;
    Object localObject;
    if ((paramAlgorithmParameterSpec instanceof KeyGenParameterSpec))
    {
      this.mEntryAlias = paramAlgorithmParameterSpec.getKeystoreAlias();
      this.mEntryUid = paramAlgorithmParameterSpec.getUid();
      this.mSpec = paramAlgorithmParameterSpec;
      this.mKeymasterAlgorithm = i;
      this.mEncryptionAtRestRequired = bool;
      this.mKeySizeBits = paramAlgorithmParameterSpec.getKeySize();
      initAlgorithmSpecificParameters();
      if (this.mKeySizeBits == -1) {
        this.mKeySizeBits = getDefaultKeySize(i);
      }
      checkValidKeySize(i, this.mKeySizeBits);
      if (paramAlgorithmParameterSpec.isUseSecureProcessor()) {
        checkSecureProcessorValidKeySize(i, this.mKeySizeBits);
      }
      if (paramAlgorithmParameterSpec.getKeystoreAlias() == null) {
        throw new InvalidAlgorithmParameterException("KeyStore entry alias not provided");
      }
    }
    else
    {
      if ((paramAlgorithmParameterSpec instanceof KeyPairGeneratorSpec))
      {
        try
        {
          localObject = paramAlgorithmParameterSpec.getKeyType();
          if (localObject != null) {}
          localObject = new KeyGenParameterSpec.Builder(paramAlgorithmParameterSpec.getKeystoreAlias(), 12);
        }
        catch (NullPointerException|IllegalArgumentException paramAlgorithmParameterSpec)
        {
          try
          {
            i = KeyProperties.KeyAlgorithm.toKeymasterAsymmetricKeyAlgorithm((String)localObject);
            switch (i)
            {
            case 2: 
            default: 
              throw new ProviderException("Unsupported algorithm: " + this.mKeymasterAlgorithm);
            }
          }
          catch (IllegalArgumentException paramAlgorithmParameterSpec)
          {
            throw new InvalidAlgorithmParameterException("Invalid key type in parameters", paramAlgorithmParameterSpec);
          }
          paramAlgorithmParameterSpec = paramAlgorithmParameterSpec;
          throw new InvalidAlgorithmParameterException(paramAlgorithmParameterSpec);
        }
        ((KeyGenParameterSpec.Builder)localObject).setDigests(new String[] { "NONE", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512" });
        for (;;)
        {
          if (paramAlgorithmParameterSpec.getKeySize() != -1) {
            ((KeyGenParameterSpec.Builder)localObject).setKeySize(paramAlgorithmParameterSpec.getKeySize());
          }
          if (paramAlgorithmParameterSpec.getAlgorithmParameterSpec() != null) {
            ((KeyGenParameterSpec.Builder)localObject).setAlgorithmParameterSpec(paramAlgorithmParameterSpec.getAlgorithmParameterSpec());
          }
          ((KeyGenParameterSpec.Builder)localObject).setCertificateSubject(paramAlgorithmParameterSpec.getSubjectDN());
          ((KeyGenParameterSpec.Builder)localObject).setCertificateSerialNumber(paramAlgorithmParameterSpec.getSerialNumber());
          ((KeyGenParameterSpec.Builder)localObject).setCertificateNotBefore(paramAlgorithmParameterSpec.getStartDate());
          ((KeyGenParameterSpec.Builder)localObject).setCertificateNotAfter(paramAlgorithmParameterSpec.getEndDate());
          bool = paramAlgorithmParameterSpec.isEncryptionRequired();
          ((KeyGenParameterSpec.Builder)localObject).setUserAuthenticationRequired(false);
          paramAlgorithmParameterSpec = ((KeyGenParameterSpec.Builder)localObject).build();
          break;
          localObject = new KeyGenParameterSpec.Builder(paramAlgorithmParameterSpec.getKeystoreAlias(), 15);
          ((KeyGenParameterSpec.Builder)localObject).setDigests(new String[] { "NONE", "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512" });
          ((KeyGenParameterSpec.Builder)localObject).setEncryptionPaddings(new String[] { "NoPadding", "PKCS1Padding", "OAEPPadding" });
          ((KeyGenParameterSpec.Builder)localObject).setSignaturePaddings(new String[] { "PKCS1", "PSS" });
          ((KeyGenParameterSpec.Builder)localObject).setRandomizedEncryptionRequired(false);
        }
      }
      throw new InvalidAlgorithmParameterException("Unsupported params class: " + paramAlgorithmParameterSpec.getClass().getName() + ". Supported: " + KeyGenParameterSpec.class.getName() + ", " + KeyPairGeneratorSpec.class.getName());
    }
    for (;;)
    {
      try
      {
        localObject = KeyProperties.KeyAlgorithm.fromKeymasterAsymmetricKeyAlgorithm(i);
        this.mKeymasterPurposes = KeyProperties.Purpose.allToKeymaster(paramAlgorithmParameterSpec.getPurposes());
        this.mKeymasterBlockModes = KeyProperties.BlockMode.allToKeymaster(paramAlgorithmParameterSpec.getBlockModes());
        this.mKeymasterEncryptionPaddings = KeyProperties.EncryptionPadding.allToKeymaster(paramAlgorithmParameterSpec.getEncryptionPaddings());
        if (((paramAlgorithmParameterSpec.getPurposes() & 0x1) == 0) || (!paramAlgorithmParameterSpec.isRandomizedEncryptionRequired())) {
          break;
        }
        int[] arrayOfInt = this.mKeymasterEncryptionPaddings;
        i = 0;
        int j = arrayOfInt.length;
        if (i >= j) {
          break;
        }
        int k = arrayOfInt[i];
        if (!KeymasterUtils.isKeymasterPaddingSchemeIndCpaCompatibleWithAsymmetricCrypto(k)) {
          throw new InvalidAlgorithmParameterException("Randomized encryption (IND-CPA) required but may be violated by padding scheme: " + KeyProperties.EncryptionPadding.fromKeymaster(k) + ". See " + KeyGenParameterSpec.class.getName() + " documentation.");
        }
      }
      catch (IllegalArgumentException|IllegalStateException paramAlgorithmParameterSpec)
      {
        throw new InvalidAlgorithmParameterException(paramAlgorithmParameterSpec);
      }
      i += 1;
    }
    this.mKeymasterSignaturePaddings = KeyProperties.SignaturePadding.allToKeymaster(paramAlgorithmParameterSpec.getSignaturePaddings());
    if (paramAlgorithmParameterSpec.isDigestsSpecified()) {}
    for (this.mKeymasterDigests = KeyProperties.Digest.allToKeymaster(paramAlgorithmParameterSpec.getDigests());; this.mKeymasterDigests = EmptyArray.INT)
    {
      KeymasterUtils.addUserAuthArgs(new KeymasterArguments(), this.mSpec.isUserAuthenticationRequired(), this.mSpec.getUserAuthenticationValidityDurationSeconds(), this.mSpec.isUserAuthenticationValidWhileOnBody(), this.mSpec.isInvalidatedByBiometricEnrollment());
      this.mJcaKeyAlgorithm = ((String)localObject);
      this.mRng = paramSecureRandom;
      this.mKeyStore = KeyStore.getInstance();
      if (1 == 0) {
        resetAll();
      }
      return;
    }
  }
  
  public static class EC
    extends AndroidKeyStoreKeyPairGeneratorSpi
  {
    public EC()
    {
      super();
    }
  }
  
  public static class RSA
    extends AndroidKeyStoreKeyPairGeneratorSpi
  {
    public RSA()
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreKeyPairGeneratorSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */