package android.security.keystore;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.security.Credentials;
import android.security.KeyPairGeneratorSpec;
import android.security.KeyStore;
import android.security.KeyStore.State;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterArguments;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.android.org.bouncycastle.x509.X509V3CertificateGenerator;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import libcore.util.EmptyArray;

public class SoterKeyStoreKeyPairRSAGeneratorSpi
  extends KeyPairGeneratorSpi
{
  private static final int RSA_DEFAULT_KEY_SIZE = 2048;
  private static final int RSA_MAX_KEY_SIZE = 8192;
  private static final int RSA_MIN_KEY_SIZE = 512;
  public static final long UINT32_MAX_VALUE = 4294967295L;
  private static final long UINT32_RANGE = 4294967296L;
  public static final BigInteger UINT64_MAX_VALUE = UINT64_RANGE.subtract(BigInteger.ONE);
  private static final BigInteger UINT64_RANGE = BigInteger.ONE.shiftLeft(64);
  private static volatile SecureRandom sRng;
  private boolean isAutoAddCounterWhenGetPublicKey = false;
  private boolean isAutoSignedWithAttkWhenGetPublicKey = false;
  private boolean isAutoSignedWithCommonkWhenGetPublicKey = false;
  private boolean isForSoter = false;
  private boolean isNeedNextAttk = false;
  private boolean isSecmsgFidCounterSignedWhenSign = false;
  private String mAutoSignedKeyNameWhenGetPublicKey = "";
  private boolean mEncryptionAtRestRequired;
  private String mEntryAlias;
  private String mJcaKeyAlgorithm;
  private int mKeySizeBits;
  private KeyStore mKeyStore;
  private int mKeymasterAlgorithm = -1;
  private int[] mKeymasterBlockModes;
  private int[] mKeymasterDigests;
  private int[] mKeymasterEncryptionPaddings;
  private int[] mKeymasterPurposes;
  private int[] mKeymasterSignaturePaddings;
  private final int mOriginalKeymasterAlgorithm = 1;
  private BigInteger mRSAPublicExponent;
  private SecureRandom mRng;
  private KeyGenParameterSpec mSpec;
  
  private void addAlgorithmSpecificParameters(KeymasterArguments paramKeymasterArguments)
  {
    if (this.mRSAPublicExponent != null) {
      paramKeymasterArguments.addUnsignedLong(1342177480, this.mRSAPublicExponent);
    }
    if (this.isForSoter)
    {
      paramKeymasterArguments.addBoolean(1879059192);
      paramKeymasterArguments.addUnsignedInt(805317375, Process.myUid());
    }
    if (this.isAutoSignedWithAttkWhenGetPublicKey) {
      paramKeymasterArguments.addBoolean(1879059193);
    }
    if (this.isAutoSignedWithCommonkWhenGetPublicKey)
    {
      paramKeymasterArguments.addBoolean(1879059194);
      if (!SoterUtil.isNullOrNil(this.mAutoSignedKeyNameWhenGetPublicKey)) {
        paramKeymasterArguments.addBytes(-1879037189, ("USRPKEY_" + this.mAutoSignedKeyNameWhenGetPublicKey).getBytes());
      }
    }
    if (this.isAutoAddCounterWhenGetPublicKey) {
      paramKeymasterArguments.addBoolean(1879059196);
    }
    if (this.isSecmsgFidCounterSignedWhenSign) {
      paramKeymasterArguments.addBoolean(1879059197);
    }
    if (this.isNeedNextAttk) {
      paramKeymasterArguments.addBoolean(1879059198);
    }
  }
  
  private static void checkValidKeySize(int paramInt1, int paramInt2)
    throws InvalidAlgorithmParameterException
  {
    if ((paramInt2 < 512) || (paramInt2 > 8192)) {
      throw new InvalidAlgorithmParameterException("RSA key size must be >= 512 and <= 8192");
    }
  }
  
  private X509Certificate generateSelfSignedCertificate(PrivateKey paramPrivateKey, PublicKey paramPublicKey)
    throws Exception
  {
    Log.d("Soter", "generateSelfSignedCertificate");
    String str = getCertificateSignatureAlgorithm(this.mKeymasterAlgorithm, this.mKeySizeBits, this.mSpec);
    if (str == null)
    {
      Log.d("Soter", "generateSelfSignedCertificateWithFakeSignature1");
      return generateSelfSignedCertificateWithFakeSignature(paramPublicKey);
    }
    try
    {
      Log.d("Soter", "generateSelfSignedCertificateWithValidSignature");
      paramPrivateKey = generateSelfSignedCertificateWithValidSignature(paramPrivateKey, paramPublicKey, str);
      return paramPrivateKey;
    }
    catch (Exception paramPrivateKey)
    {
      Log.d("Soter", "generateSelfSignedCertificateWithFakeSignature2");
    }
    return generateSelfSignedCertificateWithFakeSignature(paramPublicKey);
  }
  
  /* Error */
  private X509Certificate generateSelfSignedCertificateWithFakeSignature(PublicKey paramPublicKey)
    throws Exception
  {
    // Byte code:
    //   0: new 203	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator
    //   3: dup
    //   4: invokespecial 204	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:<init>	()V
    //   7: astore 8
    //   9: aload_0
    //   10: getfield 75	android/security/keystore/SoterKeyStoreKeyPairRSAGeneratorSpi:mKeymasterAlgorithm	I
    //   13: tableswitch	default:+27->40, 1:+173->186, 2:+27->40, 3:+57->70
    //   40: new 206	java/security/ProviderException
    //   43: dup
    //   44: new 131	java/lang/StringBuilder
    //   47: dup
    //   48: invokespecial 132	java/lang/StringBuilder:<init>	()V
    //   51: ldc -48
    //   53: invokevirtual 138	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: aload_0
    //   57: getfield 75	android/security/keystore/SoterKeyStoreKeyPairRSAGeneratorSpi:mKeymasterAlgorithm	I
    //   60: invokevirtual 211	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   63: invokevirtual 142	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: invokespecial 212	java/security/ProviderException:<init>	(Ljava/lang/String;)V
    //   69: athrow
    //   70: new 214	com/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier
    //   73: dup
    //   74: getstatic 220	com/android/org/bouncycastle/asn1/x9/X9ObjectIdentifiers:ecdsa_with_SHA256	Lcom/android/org/bouncycastle/asn1/ASN1ObjectIdentifier;
    //   77: invokespecial 223	com/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier:<init>	(Lcom/android/org/bouncycastle/asn1/ASN1ObjectIdentifier;)V
    //   80: astore_2
    //   81: new 225	com/android/org/bouncycastle/asn1/ASN1EncodableVector
    //   84: dup
    //   85: invokespecial 226	com/android/org/bouncycastle/asn1/ASN1EncodableVector:<init>	()V
    //   88: astore_3
    //   89: aload_3
    //   90: new 228	com/android/org/bouncycastle/asn1/DERInteger
    //   93: dup
    //   94: lconst_0
    //   95: invokespecial 231	com/android/org/bouncycastle/asn1/DERInteger:<init>	(J)V
    //   98: invokevirtual 235	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   101: aload_3
    //   102: new 228	com/android/org/bouncycastle/asn1/DERInteger
    //   105: dup
    //   106: lconst_0
    //   107: invokespecial 231	com/android/org/bouncycastle/asn1/DERInteger:<init>	(J)V
    //   110: invokevirtual 235	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   113: new 237	com/android/org/bouncycastle/asn1/DERSequence
    //   116: dup
    //   117: invokespecial 238	com/android/org/bouncycastle/asn1/DERSequence:<init>	()V
    //   120: invokevirtual 243	com/android/org/bouncycastle/asn1/ASN1Object:getEncoded	()[B
    //   123: astore_3
    //   124: aconst_null
    //   125: astore 4
    //   127: aconst_null
    //   128: astore 6
    //   130: aconst_null
    //   131: astore 7
    //   133: aconst_null
    //   134: astore 5
    //   136: new 245	com/android/org/bouncycastle/asn1/ASN1InputStream
    //   139: dup
    //   140: aload_1
    //   141: invokeinterface 248 1 0
    //   146: invokespecial 251	com/android/org/bouncycastle/asn1/ASN1InputStream:<init>	([B)V
    //   149: astore_1
    //   150: aload 8
    //   152: aload_1
    //   153: invokevirtual 255	com/android/org/bouncycastle/asn1/ASN1InputStream:readObject	()Lcom/android/org/bouncycastle/asn1/ASN1Primitive;
    //   156: invokestatic 261	com/android/org/bouncycastle/asn1/x509/SubjectPublicKeyInfo:getInstance	(Ljava/lang/Object;)Lcom/android/org/bouncycastle/asn1/x509/SubjectPublicKeyInfo;
    //   159: invokevirtual 265	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setSubjectPublicKeyInfo	(Lcom/android/org/bouncycastle/asn1/x509/SubjectPublicKeyInfo;)V
    //   162: aload 6
    //   164: astore 4
    //   166: aload_1
    //   167: ifnull +11 -> 178
    //   170: aload_1
    //   171: invokevirtual 270	java/io/FilterInputStream:close	()V
    //   174: aload 6
    //   176: astore 4
    //   178: aload 4
    //   180: ifnull +99 -> 279
    //   183: aload 4
    //   185: athrow
    //   186: new 214	com/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier
    //   189: dup
    //   190: getstatic 275	com/android/org/bouncycastle/asn1/pkcs/PKCSObjectIdentifiers:sha256WithRSAEncryption	Lcom/android/org/bouncycastle/asn1/ASN1ObjectIdentifier;
    //   193: getstatic 281	com/android/org/bouncycastle/asn1/DERNull:INSTANCE	Lcom/android/org/bouncycastle/asn1/DERNull;
    //   196: invokespecial 284	com/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier:<init>	(Lcom/android/org/bouncycastle/asn1/ASN1ObjectIdentifier;Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   199: astore_2
    //   200: iconst_1
    //   201: newarray <illegal type>
    //   203: astore_3
    //   204: goto -80 -> 124
    //   207: astore 4
    //   209: goto -31 -> 178
    //   212: astore_2
    //   213: aload 5
    //   215: astore_1
    //   216: aload_2
    //   217: athrow
    //   218: astore 4
    //   220: aload_2
    //   221: astore_3
    //   222: aload 4
    //   224: astore_2
    //   225: aload_3
    //   226: astore 4
    //   228: aload_1
    //   229: ifnull +10 -> 239
    //   232: aload_1
    //   233: invokevirtual 270	java/io/FilterInputStream:close	()V
    //   236: aload_3
    //   237: astore 4
    //   239: aload 4
    //   241: ifnull +36 -> 277
    //   244: aload 4
    //   246: athrow
    //   247: astore_1
    //   248: aload_3
    //   249: ifnonnull +9 -> 258
    //   252: aload_1
    //   253: astore 4
    //   255: goto -16 -> 239
    //   258: aload_3
    //   259: astore 4
    //   261: aload_3
    //   262: aload_1
    //   263: if_acmpeq -24 -> 239
    //   266: aload_3
    //   267: aload_1
    //   268: invokevirtual 288	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   271: aload_3
    //   272: astore 4
    //   274: goto -35 -> 239
    //   277: aload_2
    //   278: athrow
    //   279: aload 8
    //   281: new 290	com/android/org/bouncycastle/asn1/ASN1Integer
    //   284: dup
    //   285: aload_0
    //   286: getfield 182	android/security/keystore/SoterKeyStoreKeyPairRSAGeneratorSpi:mSpec	Landroid/security/keystore/KeyGenParameterSpec;
    //   289: invokevirtual 296	android/security/keystore/KeyGenParameterSpec:getCertificateSerialNumber	()Ljava/math/BigInteger;
    //   292: invokespecial 299	com/android/org/bouncycastle/asn1/ASN1Integer:<init>	(Ljava/math/BigInteger;)V
    //   295: invokevirtual 303	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setSerialNumber	(Lcom/android/org/bouncycastle/asn1/ASN1Integer;)V
    //   298: new 305	com/android/org/bouncycastle/jce/X509Principal
    //   301: dup
    //   302: aload_0
    //   303: getfield 182	android/security/keystore/SoterKeyStoreKeyPairRSAGeneratorSpi:mSpec	Landroid/security/keystore/KeyGenParameterSpec;
    //   306: invokevirtual 309	android/security/keystore/KeyGenParameterSpec:getCertificateSubject	()Ljavax/security/auth/x500/X500Principal;
    //   309: invokevirtual 312	javax/security/auth/x500/X500Principal:getEncoded	()[B
    //   312: invokespecial 313	com/android/org/bouncycastle/jce/X509Principal:<init>	([B)V
    //   315: astore_1
    //   316: aload 8
    //   318: aload_1
    //   319: invokevirtual 317	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setSubject	(Lcom/android/org/bouncycastle/asn1/x509/X509Name;)V
    //   322: aload 8
    //   324: aload_1
    //   325: invokevirtual 320	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setIssuer	(Lcom/android/org/bouncycastle/asn1/x509/X509Name;)V
    //   328: aload 8
    //   330: new 322	com/android/org/bouncycastle/asn1/x509/Time
    //   333: dup
    //   334: aload_0
    //   335: getfield 182	android/security/keystore/SoterKeyStoreKeyPairRSAGeneratorSpi:mSpec	Landroid/security/keystore/KeyGenParameterSpec;
    //   338: invokevirtual 326	android/security/keystore/KeyGenParameterSpec:getCertificateNotBefore	()Ljava/util/Date;
    //   341: invokespecial 329	com/android/org/bouncycastle/asn1/x509/Time:<init>	(Ljava/util/Date;)V
    //   344: invokevirtual 333	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setStartDate	(Lcom/android/org/bouncycastle/asn1/x509/Time;)V
    //   347: aload 8
    //   349: new 322	com/android/org/bouncycastle/asn1/x509/Time
    //   352: dup
    //   353: aload_0
    //   354: getfield 182	android/security/keystore/SoterKeyStoreKeyPairRSAGeneratorSpi:mSpec	Landroid/security/keystore/KeyGenParameterSpec;
    //   357: invokevirtual 336	android/security/keystore/KeyGenParameterSpec:getCertificateNotAfter	()Ljava/util/Date;
    //   360: invokespecial 329	com/android/org/bouncycastle/asn1/x509/Time:<init>	(Ljava/util/Date;)V
    //   363: invokevirtual 339	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setEndDate	(Lcom/android/org/bouncycastle/asn1/x509/Time;)V
    //   366: aload 8
    //   368: aload_2
    //   369: invokevirtual 343	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:setSignature	(Lcom/android/org/bouncycastle/asn1/x509/AlgorithmIdentifier;)V
    //   372: aload 8
    //   374: invokevirtual 347	com/android/org/bouncycastle/asn1/x509/V3TBSCertificateGenerator:generateTBSCertificate	()Lcom/android/org/bouncycastle/asn1/x509/TBSCertificate;
    //   377: astore_1
    //   378: new 225	com/android/org/bouncycastle/asn1/ASN1EncodableVector
    //   381: dup
    //   382: invokespecial 226	com/android/org/bouncycastle/asn1/ASN1EncodableVector:<init>	()V
    //   385: astore 4
    //   387: aload 4
    //   389: aload_1
    //   390: invokevirtual 235	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   393: aload 4
    //   395: aload_2
    //   396: invokevirtual 235	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   399: aload 4
    //   401: new 349	com/android/org/bouncycastle/asn1/DERBitString
    //   404: dup
    //   405: aload_3
    //   406: invokespecial 350	com/android/org/bouncycastle/asn1/DERBitString:<init>	([B)V
    //   409: invokevirtual 235	com/android/org/bouncycastle/asn1/ASN1EncodableVector:add	(Lcom/android/org/bouncycastle/asn1/ASN1Encodable;)V
    //   412: new 352	com/android/org/bouncycastle/jce/provider/X509CertificateObject
    //   415: dup
    //   416: new 237	com/android/org/bouncycastle/asn1/DERSequence
    //   419: dup
    //   420: aload 4
    //   422: invokespecial 355	com/android/org/bouncycastle/asn1/DERSequence:<init>	(Lcom/android/org/bouncycastle/asn1/ASN1EncodableVector;)V
    //   425: invokestatic 360	com/android/org/bouncycastle/asn1/x509/Certificate:getInstance	(Ljava/lang/Object;)Lcom/android/org/bouncycastle/asn1/x509/Certificate;
    //   428: invokespecial 363	com/android/org/bouncycastle/jce/provider/X509CertificateObject:<init>	(Lcom/android/org/bouncycastle/asn1/x509/Certificate;)V
    //   431: areturn
    //   432: astore_2
    //   433: aload 7
    //   435: astore_1
    //   436: aload 4
    //   438: astore_3
    //   439: goto -214 -> 225
    //   442: astore_2
    //   443: aload 4
    //   445: astore_3
    //   446: goto -221 -> 225
    //   449: astore_2
    //   450: goto -234 -> 216
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	453	0	this	SoterKeyStoreKeyPairRSAGeneratorSpi
    //   0	453	1	paramPublicKey	PublicKey
    //   80	120	2	localAlgorithmIdentifier	com.android.org.bouncycastle.asn1.x509.AlgorithmIdentifier
    //   212	9	2	localThrowable1	Throwable
    //   224	172	2	localObject1	Object
    //   432	1	2	localObject2	Object
    //   442	1	2	localObject3	Object
    //   449	1	2	localThrowable2	Throwable
    //   88	358	3	localObject4	Object
    //   125	59	4	localObject5	Object
    //   207	1	4	localThrowable3	Throwable
    //   218	5	4	localObject6	Object
    //   226	218	4	localObject7	Object
    //   134	80	5	localObject8	Object
    //   128	47	6	localObject9	Object
    //   131	303	7	localObject10	Object
    //   7	366	8	localV3TBSCertificateGenerator	com.android.org.bouncycastle.asn1.x509.V3TBSCertificateGenerator
    // Exception table:
    //   from	to	target	type
    //   170	174	207	java/lang/Throwable
    //   136	150	212	java/lang/Throwable
    //   216	218	218	finally
    //   232	236	247	java/lang/Throwable
    //   136	150	432	finally
    //   150	162	442	finally
    //   150	162	449	java/lang/Throwable
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
  
  public static Context getApplicationContext()
  {
    Application localApplication = ActivityThread.currentApplication();
    if (localApplication == null) {
      throw new IllegalStateException("Failed to obtain application Context from ActivityThread");
    }
    return localApplication;
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
    if (!ArrayUtils.contains(KeyProperties.SignaturePadding.allToKeymaster(paramKeyGenParameterSpec.getSignaturePaddings()), 5)) {
      return null;
    }
    paramKeyGenParameterSpec = getAvailableKeymasterSignatureDigests(paramKeyGenParameterSpec.getDigests(), getSupportedEcdsaSignatureDigests());
    int i = -1;
    paramInt1 = -1;
    paramKeyGenParameterSpec = paramKeyGenParameterSpec.iterator();
    while (paramKeyGenParameterSpec.hasNext())
    {
      int k = ((Integer)paramKeyGenParameterSpec.next()).intValue();
      int j = getDigestOutputSizeBits(k);
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
    return 2048;
  }
  
  public static int getDigestOutputSizeBits(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown digest: " + paramInt);
    case 0: 
      return -1;
    case 1: 
      return 128;
    case 2: 
      return 160;
    case 3: 
      return 224;
    case 4: 
      return 256;
    case 5: 
      return 384;
    }
    return 512;
  }
  
  static byte[] getRandomBytesToMixIntoKeystoreRng(SecureRandom paramSecureRandom, int paramInt)
  {
    if (paramInt <= 0) {
      return EmptyArray.BYTE;
    }
    SecureRandom localSecureRandom = paramSecureRandom;
    if (paramSecureRandom == null) {
      localSecureRandom = getRng();
    }
    paramSecureRandom = new byte[paramInt];
    localSecureRandom.nextBytes(paramSecureRandom);
    return paramSecureRandom;
  }
  
  private byte[] getRealKeyBlobByKeyName(String paramString)
  {
    Log.d("Soter", "start retrieve key blob by key name: " + paramString);
    return this.mKeyStore.get("USRPKEY_" + paramString);
  }
  
  private static SecureRandom getRng()
  {
    if (sRng == null) {
      sRng = new SecureRandom();
    }
    return sRng;
  }
  
  private static String[] getSupportedEcdsaSignatureDigests()
  {
    return new String[] { "NONE", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512" };
  }
  
  private void initAlgorithmSpecificParameters()
    throws InvalidAlgorithmParameterException
  {
    Object localObject2 = this.mSpec.getAlgorithmParameterSpec();
    Object localObject1 = RSAKeyGenParameterSpec.F4;
    if ((localObject2 instanceof RSAKeyGenParameterSpec))
    {
      if (this.mKeySizeBits == -1) {
        this.mKeySizeBits = ((RSAKeyGenParameterSpec)localObject2).getKeysize();
      }
      while (this.mKeySizeBits == ((RSAKeyGenParameterSpec)localObject2).getKeysize())
      {
        localObject2 = ((RSAKeyGenParameterSpec)localObject2).getPublicExponent();
        if (((BigInteger)localObject2).compareTo(BigInteger.ZERO) >= 1) {
          break;
        }
        throw new InvalidAlgorithmParameterException("RSA public exponent must be positive: " + localObject2);
      }
      throw new InvalidAlgorithmParameterException("RSA key size must match  between " + this.mSpec + " and " + localObject2 + ": " + this.mKeySizeBits + " vs " + ((RSAKeyGenParameterSpec)localObject2).getKeysize());
      localObject1 = localObject2;
      if (((BigInteger)localObject2).compareTo(UINT64_MAX_VALUE) > 0) {
        throw new InvalidAlgorithmParameterException("Unsupported RSA public exponent: " + localObject2 + ". Maximum supported value: " + UINT64_MAX_VALUE);
      }
    }
    this.mRSAPublicExponent = ((BigInteger)localObject1);
    localObject1 = SoterUtil.convertKeyNameToParameterSpec(this.mSpec.getKeystoreAlias());
    if (localObject1 != null)
    {
      this.isForSoter = ((SoterRSAKeyGenParameterSpec)localObject1).isForSoter();
      this.isAutoSignedWithAttkWhenGetPublicKey = ((SoterRSAKeyGenParameterSpec)localObject1).isAutoSignedWithAttkWhenGetPublicKey();
      this.isAutoSignedWithCommonkWhenGetPublicKey = ((SoterRSAKeyGenParameterSpec)localObject1).isAutoSignedWithCommonkWhenGetPublicKey();
      this.mAutoSignedKeyNameWhenGetPublicKey = ((SoterRSAKeyGenParameterSpec)localObject1).getAutoSignedKeyNameWhenGetPublicKey();
      this.isSecmsgFidCounterSignedWhenSign = ((SoterRSAKeyGenParameterSpec)localObject1).isSecmsgFidCounterSignedWhenSign();
      this.isAutoAddCounterWhenGetPublicKey = ((SoterRSAKeyGenParameterSpec)localObject1).isAutoAddCounterWhenGetPublicKey();
      this.isNeedNextAttk = ((SoterRSAKeyGenParameterSpec)localObject1).isNeedUseNextAttk();
    }
  }
  
  public static byte[] intToByteArray(int paramInt)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
    localByteBuffer.order(ByteOrder.nativeOrder());
    localByteBuffer.putInt(paramInt);
    return localByteBuffer.array();
  }
  
  public static boolean isKeymasterPaddingSchemeIndCpaCompatibleWithAsymmetricCrypto(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
    default: 
      throw new IllegalArgumentException("Unsupported asymmetric encryption padding scheme: " + paramInt);
    case 1: 
      return false;
    }
    return true;
  }
  
  private void resetAll()
  {
    this.mEntryAlias = null;
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
    this.isForSoter = false;
    this.isAutoSignedWithAttkWhenGetPublicKey = false;
    this.isAutoSignedWithCommonkWhenGetPublicKey = false;
    this.mAutoSignedKeyNameWhenGetPublicKey = "";
    this.isSecmsgFidCounterSignedWhenSign = false;
    this.isAutoAddCounterWhenGetPublicKey = false;
    this.isNeedNextAttk = false;
  }
  
  public static BigInteger toUint64(long paramLong)
  {
    if (paramLong >= 0L) {
      return BigInteger.valueOf(paramLong);
    }
    return BigInteger.valueOf(paramLong).add(UINT64_RANGE);
  }
  
  public KeyPair generateKeyPair()
  {
    if ((this.mKeyStore == null) || (this.mSpec == null)) {
      throw new IllegalStateException("Not initialized");
    }
    if (this.mEncryptionAtRestRequired) {}
    for (int i = 1; ((i & 0x1) != 0) && (this.mKeyStore.state() != KeyStore.State.UNLOCKED); i = 0) {
      throw new IllegalStateException("Encryption at rest using secure lock screen credential requested for key pair, but the user has not yet entered the credential");
    }
    KeymasterArguments localKeymasterArguments = new KeymasterArguments();
    localKeymasterArguments.addUnsignedInt(805306371, this.mKeySizeBits);
    localKeymasterArguments.addEnum(268435458, this.mKeymasterAlgorithm);
    localKeymasterArguments.addEnums(536870913, this.mKeymasterPurposes);
    localKeymasterArguments.addEnums(536870916, this.mKeymasterBlockModes);
    localKeymasterArguments.addEnums(536870918, this.mKeymasterEncryptionPaddings);
    localKeymasterArguments.addEnums(536870918, this.mKeymasterSignaturePaddings);
    localKeymasterArguments.addEnums(536870917, this.mKeymasterDigests);
    KeymasterUtils.addUserAuthArgs(localKeymasterArguments, this.mSpec.isUserAuthenticationRequired(), this.mSpec.getUserAuthenticationValidityDurationSeconds(), false, true);
    if (this.mSpec.getKeyValidityStart() != null) {
      localKeymasterArguments.addDate(1610613136, this.mSpec.getKeyValidityStart());
    }
    if (this.mSpec.getKeyValidityForOriginationEnd() != null) {
      localKeymasterArguments.addDate(1610613137, this.mSpec.getKeyValidityForOriginationEnd());
    }
    if (this.mSpec.getKeyValidityForConsumptionEnd() != null) {
      localKeymasterArguments.addDate(1610613138, this.mSpec.getKeyValidityForConsumptionEnd());
    }
    addAlgorithmSpecificParameters(localKeymasterArguments);
    Object localObject2 = getRandomBytesToMixIntoKeystoreRng(this.mRng, (this.mKeySizeBits + 7) / 8);
    String str = "USRPKEY_" + this.mEntryAlias;
    try
    {
      Credentials.deleteAllTypesForAlias(this.mKeyStore, this.mEntryAlias);
      KeyCharacteristics localKeyCharacteristics = new KeyCharacteristics();
      int j = this.mKeyStore.generateKey(str, localKeymasterArguments, (byte[])localObject2, i, localKeyCharacteristics);
      if (j != 1) {
        throw new ProviderException("Failed to generate key pair", KeyStore.getKeyStoreException(j));
      }
    }
    finally
    {
      if (0 == 0) {
        Credentials.deleteAllTypesForAlias(this.mKeyStore, this.mEntryAlias);
      }
    }
    try
    {
      KeyPair localKeyPair = SoterKeyStoreProvider.loadAndroidKeyStoreKeyPairFromKeystore(this.mKeyStore, str);
      if (!this.mJcaKeyAlgorithm.equalsIgnoreCase(localKeyPair.getPrivate().getAlgorithm())) {
        throw new ProviderException("Generated key pair algorithm does not match requested algorithm: " + localKeyPair.getPrivate().getAlgorithm() + " vs " + this.mJcaKeyAlgorithm);
      }
    }
    catch (UnrecoverableKeyException localUnrecoverableKeyException)
    {
      throw new ProviderException("Failed to load generated key pair from keystore", localUnrecoverableKeyException);
    }
    try
    {
      localObject2 = generateSelfSignedCertificate(localUnrecoverableKeyException.getPrivate(), localUnrecoverableKeyException.getPublic());
      if (1 != 0) {
        return localCertificateEncodingException;
      }
    }
    catch (Exception localException)
    {
      try
      {
        localObject2 = ((Certificate)localObject2).getEncoded();
        if (this.mKeyStore.put("USRCERT_" + this.mEntryAlias, (byte[])localObject2, -1, i)) {
          break label557;
        }
        throw new ProviderException("Failed to store self-signed certificate");
      }
      catch (CertificateEncodingException localCertificateEncodingException)
      {
        throw new ProviderException("Failed to obtain encoded form of self-signed certificate", localCertificateEncodingException);
      }
      localException = localException;
      throw new ProviderException("Failed to generate self-signed certificate", localException);
    }
    label557:
    Credentials.deleteAllTypesForAlias(this.mKeyStore, this.mEntryAlias);
    return localCertificateEncodingException;
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom)
  {
    throw new IllegalArgumentException(KeyGenParameterSpec.class.getName() + " required to initialize this KeyPairGenerator");
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {
    int i = 0;
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
    int j = this.mOriginalKeymasterAlgorithm;
    if ((paramAlgorithmParameterSpec instanceof KeyGenParameterSpec))
    {
      this.mEntryAlias = SoterUtil.getPureKeyAliasFromKeyName(paramAlgorithmParameterSpec.getKeystoreAlias());
      this.mSpec = paramAlgorithmParameterSpec;
      this.mKeymasterAlgorithm = j;
      this.mEncryptionAtRestRequired = false;
      this.mKeySizeBits = paramAlgorithmParameterSpec.getKeySize();
      initAlgorithmSpecificParameters();
      if (this.mKeySizeBits == -1) {
        this.mKeySizeBits = getDefaultKeySize(j);
      }
      checkValidKeySize(j, this.mKeySizeBits);
      if (paramAlgorithmParameterSpec.getKeystoreAlias() == null) {
        throw new InvalidAlgorithmParameterException("KeyStore entry alias not provided");
      }
    }
    else
    {
      throw new InvalidAlgorithmParameterException("Unsupported params class: " + paramAlgorithmParameterSpec.getClass().getName() + ". Supported: " + KeyGenParameterSpec.class.getName() + ", " + KeyPairGeneratorSpec.class.getName());
    }
    String str;
    for (;;)
    {
      try
      {
        str = KeyProperties.KeyAlgorithm.fromKeymasterAsymmetricKeyAlgorithm(j);
        this.mKeymasterPurposes = KeyProperties.Purpose.allToKeymaster(paramAlgorithmParameterSpec.getPurposes());
        this.mKeymasterBlockModes = KeyProperties.BlockMode.allToKeymaster(paramAlgorithmParameterSpec.getBlockModes());
        this.mKeymasterEncryptionPaddings = KeyProperties.EncryptionPadding.allToKeymaster(paramAlgorithmParameterSpec.getEncryptionPaddings());
        if (((paramAlgorithmParameterSpec.getPurposes() & 0x1) == 0) || (!paramAlgorithmParameterSpec.isRandomizedEncryptionRequired())) {
          break;
        }
        int[] arrayOfInt = this.mKeymasterEncryptionPaddings;
        j = arrayOfInt.length;
        if (i >= j) {
          break;
        }
        int k = arrayOfInt[i];
        if (!isKeymasterPaddingSchemeIndCpaCompatibleWithAsymmetricCrypto(k)) {
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
      KeymasterUtils.addUserAuthArgs(new KeymasterArguments(), this.mSpec.isUserAuthenticationRequired(), this.mSpec.getUserAuthenticationValidityDurationSeconds(), false, true);
      this.mJcaKeyAlgorithm = str;
      this.mRng = paramSecureRandom;
      this.mKeyStore = KeyStore.getInstance();
      if (1 == 0) {
        resetAll();
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/SoterKeyStoreKeyPairRSAGeneratorSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */