package android.security.keystore;

import android.security.Credentials;
import android.security.KeyStore;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterArguments;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore.Entry;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.crypto.SecretKey;
import libcore.util.EmptyArray;

public class AndroidKeyStoreSpi
  extends KeyStoreSpi
{
  public static final String NAME = "AndroidKeyStore";
  private KeyStore mKeyStore;
  private int mUid = -1;
  
  private Certificate getCertificateForPrivateKeyEntry(String paramString, byte[] paramArrayOfByte)
  {
    paramArrayOfByte = toCertificate(paramArrayOfByte);
    if (paramArrayOfByte == null) {
      return null;
    }
    paramString = "USRPKEY_" + paramString;
    if (this.mKeyStore.contains(paramString, this.mUid)) {
      return wrapIntoKeyStoreCertificate(paramString, this.mUid, paramArrayOfByte);
    }
    return paramArrayOfByte;
  }
  
  private Certificate getCertificateForTrustedCertificateEntry(byte[] paramArrayOfByte)
  {
    return toCertificate(paramArrayOfByte);
  }
  
  private static KeyProtection getLegacyKeyProtectionParameter(PrivateKey paramPrivateKey)
    throws KeyStoreException
  {
    paramPrivateKey = paramPrivateKey.getAlgorithm();
    if ("EC".equalsIgnoreCase(paramPrivateKey))
    {
      paramPrivateKey = new KeyProtection.Builder(12);
      paramPrivateKey.setDigests(new String[] { "NONE", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512" });
    }
    for (;;)
    {
      paramPrivateKey.setUserAuthenticationRequired(false);
      return paramPrivateKey.build();
      if (!"RSA".equalsIgnoreCase(paramPrivateKey)) {
        break;
      }
      paramPrivateKey = new KeyProtection.Builder(15);
      paramPrivateKey.setDigests(new String[] { "NONE", "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512" });
      paramPrivateKey.setEncryptionPaddings(new String[] { "NoPadding", "PKCS1Padding", "OAEPPadding" });
      paramPrivateKey.setSignaturePaddings(new String[] { "PKCS1", "PSS" });
      paramPrivateKey.setRandomizedEncryptionRequired(false);
    }
    throw new KeyStoreException("Unsupported key algorithm: " + paramPrivateKey);
  }
  
  private Date getModificationDate(String paramString)
  {
    long l = this.mKeyStore.getmtime(paramString, this.mUid);
    if (l == -1L) {
      return null;
    }
    return new Date(l);
  }
  
  private Set<String> getUniqueAliases()
  {
    String[] arrayOfString = this.mKeyStore.list("", this.mUid);
    if (arrayOfString == null) {
      return new HashSet();
    }
    HashSet localHashSet = new HashSet(arrayOfString.length);
    int i = 0;
    int j = arrayOfString.length;
    if (i < j)
    {
      String str = arrayOfString[i];
      int k = str.indexOf('_');
      if ((k == -1) || (str.length() <= k)) {
        Log.e("AndroidKeyStore", "invalid alias: " + str);
      }
      for (;;)
      {
        i += 1;
        break;
        localHashSet.add(new String(str.substring(k + 1)));
      }
    }
    return localHashSet;
  }
  
  private boolean isCertificateEntry(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    return this.mKeyStore.contains("CACERT_" + paramString, this.mUid);
  }
  
  private boolean isKeyEntry(String paramString)
  {
    if (!isPrivateKeyEntry(paramString)) {
      return isSecretKeyEntry(paramString);
    }
    return true;
  }
  
  private boolean isPrivateKeyEntry(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    return this.mKeyStore.contains("USRPKEY_" + paramString, this.mUid);
  }
  
  private boolean isSecretKeyEntry(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    return this.mKeyStore.contains("USRSKEY_" + paramString, this.mUid);
  }
  
  /* Error */
  private void setPrivateKeyEntry(String paramString, PrivateKey paramPrivateKey, Certificate[] paramArrayOfCertificate, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 5
    //   3: aload 4
    //   5: ifnonnull +28 -> 33
    //   8: aload_2
    //   9: invokestatic 211	android/security/keystore/AndroidKeyStoreSpi:getLegacyKeyProtectionParameter	(Ljava/security/PrivateKey;)Landroid/security/keystore/KeyProtection;
    //   12: astore 10
    //   14: aload_3
    //   15: ifnull +8 -> 23
    //   18: aload_3
    //   19: arraylength
    //   20: ifne +130 -> 150
    //   23: new 59	java/security/KeyStoreException
    //   26: dup
    //   27: ldc -43
    //   29: invokespecial 129	java/security/KeyStoreException:<init>	(Ljava/lang/String;)V
    //   32: athrow
    //   33: aload 4
    //   35: instanceof 215
    //   38: ifeq +34 -> 72
    //   41: aload_2
    //   42: invokestatic 211	android/security/keystore/AndroidKeyStoreSpi:getLegacyKeyProtectionParameter	(Ljava/security/PrivateKey;)Landroid/security/keystore/KeyProtection;
    //   45: astore 11
    //   47: aload 11
    //   49: astore 10
    //   51: aload 4
    //   53: checkcast 215	android/security/KeyStoreParameter
    //   56: invokevirtual 219	android/security/KeyStoreParameter:isEncryptionRequired	()Z
    //   59: ifeq -45 -> 14
    //   62: iconst_1
    //   63: istore 5
    //   65: aload 11
    //   67: astore 10
    //   69: goto -55 -> 14
    //   72: aload 4
    //   74: instanceof 221
    //   77: ifeq +13 -> 90
    //   80: aload 4
    //   82: checkcast 221	android/security/keystore/KeyProtection
    //   85: astore 10
    //   87: goto -73 -> 14
    //   90: new 59	java/security/KeyStoreException
    //   93: dup
    //   94: new 30	java/lang/StringBuilder
    //   97: dup
    //   98: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   101: ldc -33
    //   103: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   106: aload 4
    //   108: invokevirtual 229	java/lang/Object:getClass	()Ljava/lang/Class;
    //   111: invokevirtual 234	java/lang/Class:getName	()Ljava/lang/String;
    //   114: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   117: ldc -20
    //   119: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   122: ldc -35
    //   124: invokevirtual 234	java/lang/Class:getName	()Ljava/lang/String;
    //   127: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: ldc -18
    //   132: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: ldc -41
    //   137: invokevirtual 234	java/lang/Class:getName	()Ljava/lang/String;
    //   140: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   146: invokespecial 129	java/security/KeyStoreException:<init>	(Ljava/lang/String;)V
    //   149: athrow
    //   150: aload_3
    //   151: arraylength
    //   152: anewarray 240	java/security/cert/X509Certificate
    //   155: astore 4
    //   157: iconst_0
    //   158: istore 6
    //   160: iload 6
    //   162: aload_3
    //   163: arraylength
    //   164: if_icmpge +105 -> 269
    //   167: ldc -14
    //   169: aload_3
    //   170: iload 6
    //   172: aaload
    //   173: invokevirtual 247	java/security/cert/Certificate:getType	()Ljava/lang/String;
    //   176: invokevirtual 250	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   179: ifne +31 -> 210
    //   182: new 59	java/security/KeyStoreException
    //   185: dup
    //   186: new 30	java/lang/StringBuilder
    //   189: dup
    //   190: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   193: ldc -4
    //   195: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: iload 6
    //   200: invokevirtual 255	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   203: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   206: invokespecial 129	java/security/KeyStoreException:<init>	(Ljava/lang/String;)V
    //   209: athrow
    //   210: aload_3
    //   211: iload 6
    //   213: aaload
    //   214: instanceof 240
    //   217: ifne +31 -> 248
    //   220: new 59	java/security/KeyStoreException
    //   223: dup
    //   224: new 30	java/lang/StringBuilder
    //   227: dup
    //   228: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   231: ldc -4
    //   233: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   236: iload 6
    //   238: invokevirtual 255	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   241: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   244: invokespecial 129	java/security/KeyStoreException:<init>	(Ljava/lang/String;)V
    //   247: athrow
    //   248: aload 4
    //   250: iload 6
    //   252: aload_3
    //   253: iload 6
    //   255: aaload
    //   256: checkcast 240	java/security/cert/X509Certificate
    //   259: aastore
    //   260: iload 6
    //   262: iconst_1
    //   263: iadd
    //   264: istore 6
    //   266: goto -106 -> 160
    //   269: aload 4
    //   271: iconst_0
    //   272: aaload
    //   273: invokevirtual 259	java/security/cert/X509Certificate:getEncoded	()[B
    //   276: astore 12
    //   278: aload_3
    //   279: arraylength
    //   280: iconst_1
    //   281: if_icmple +178 -> 459
    //   284: aload 4
    //   286: arraylength
    //   287: iconst_1
    //   288: isub
    //   289: anewarray 261	[B
    //   292: astore 11
    //   294: iconst_0
    //   295: istore 7
    //   297: iconst_0
    //   298: istore 6
    //   300: iload 6
    //   302: aload 11
    //   304: arraylength
    //   305: if_icmpge +86 -> 391
    //   308: aload 11
    //   310: iload 6
    //   312: aload 4
    //   314: iload 6
    //   316: iconst_1
    //   317: iadd
    //   318: aaload
    //   319: invokevirtual 259	java/security/cert/X509Certificate:getEncoded	()[B
    //   322: aastore
    //   323: aload 11
    //   325: iload 6
    //   327: aaload
    //   328: arraylength
    //   329: istore 8
    //   331: iload 7
    //   333: iload 8
    //   335: iadd
    //   336: istore 7
    //   338: iload 6
    //   340: iconst_1
    //   341: iadd
    //   342: istore 6
    //   344: goto -44 -> 300
    //   347: astore_1
    //   348: new 59	java/security/KeyStoreException
    //   351: dup
    //   352: ldc_w 263
    //   355: aload_1
    //   356: invokespecial 266	java/security/KeyStoreException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   359: athrow
    //   360: astore_1
    //   361: new 59	java/security/KeyStoreException
    //   364: dup
    //   365: new 30	java/lang/StringBuilder
    //   368: dup
    //   369: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   372: ldc_w 268
    //   375: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   378: iload 6
    //   380: invokevirtual 255	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   383: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   386: aload_1
    //   387: invokespecial 266	java/security/KeyStoreException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   390: athrow
    //   391: iload 7
    //   393: newarray <illegal type>
    //   395: astore 4
    //   397: iconst_0
    //   398: istore 7
    //   400: iconst_0
    //   401: istore 6
    //   403: aload 4
    //   405: astore_3
    //   406: iload 6
    //   408: aload 11
    //   410: arraylength
    //   411: if_icmpge +50 -> 461
    //   414: aload 11
    //   416: iload 6
    //   418: aaload
    //   419: arraylength
    //   420: istore 8
    //   422: aload 11
    //   424: iload 6
    //   426: aaload
    //   427: iconst_0
    //   428: aload 4
    //   430: iload 7
    //   432: iload 8
    //   434: invokestatic 274	java/lang/System:arraycopy	([BI[BII)V
    //   437: iload 7
    //   439: iload 8
    //   441: iadd
    //   442: istore 7
    //   444: aload 11
    //   446: iload 6
    //   448: aconst_null
    //   449: aastore
    //   450: iload 6
    //   452: iconst_1
    //   453: iadd
    //   454: istore 6
    //   456: goto -53 -> 403
    //   459: aconst_null
    //   460: astore_3
    //   461: aload_2
    //   462: instanceof 276
    //   465: ifeq +84 -> 549
    //   468: aload_2
    //   469: checkcast 278	android/security/keystore/AndroidKeyStoreKey
    //   472: invokevirtual 281	android/security/keystore/AndroidKeyStoreKey:getAlias	()Ljava/lang/String;
    //   475: astore 4
    //   477: aload 4
    //   479: ifnull +198 -> 677
    //   482: aload 4
    //   484: ldc 33
    //   486: invokevirtual 284	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   489: ifeq +188 -> 677
    //   492: aload 4
    //   494: ldc 33
    //   496: invokevirtual 163	java/lang/String:length	()I
    //   499: invokevirtual 175	java/lang/String:substring	(I)Ljava/lang/String;
    //   502: astore_2
    //   503: aload_1
    //   504: aload_2
    //   505: invokevirtual 250	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   508: ifne +47 -> 555
    //   511: new 59	java/security/KeyStoreException
    //   514: dup
    //   515: new 30	java/lang/StringBuilder
    //   518: dup
    //   519: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   522: ldc_w 286
    //   525: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   528: aload_1
    //   529: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   532: ldc_w 288
    //   535: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   538: aload_2
    //   539: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   542: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   545: invokespecial 129	java/security/KeyStoreException:<init>	(Ljava/lang/String;)V
    //   548: athrow
    //   549: aconst_null
    //   550: astore 4
    //   552: goto -75 -> 477
    //   555: iconst_0
    //   556: istore 6
    //   558: aconst_null
    //   559: astore_2
    //   560: aconst_null
    //   561: astore 4
    //   563: iload 6
    //   565: ifeq +498 -> 1063
    //   568: aload_0
    //   569: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   572: aload_1
    //   573: aload_0
    //   574: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   577: invokestatic 294	android/security/Credentials:deleteAllTypesForAlias	(Landroid/security/KeyStore;Ljava/lang/String;I)Z
    //   580: pop
    //   581: new 296	android/security/keymaster/KeyCharacteristics
    //   584: dup
    //   585: invokespecial 297	android/security/keymaster/KeyCharacteristics:<init>	()V
    //   588: astore 10
    //   590: aload_0
    //   591: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   594: new 30	java/lang/StringBuilder
    //   597: dup
    //   598: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   601: ldc 33
    //   603: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   606: aload_1
    //   607: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   610: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   613: aload_2
    //   614: iconst_1
    //   615: aload 4
    //   617: aload_0
    //   618: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   621: iload 5
    //   623: aload 10
    //   625: invokevirtual 301	android/security/KeyStore:importKey	(Ljava/lang/String;Landroid/security/keymaster/KeymasterArguments;I[BIILandroid/security/keymaster/KeyCharacteristics;)I
    //   628: istore 7
    //   630: iload 7
    //   632: iconst_1
    //   633: if_icmpeq +456 -> 1089
    //   636: new 59	java/security/KeyStoreException
    //   639: dup
    //   640: ldc_w 303
    //   643: iload 7
    //   645: invokestatic 307	android/security/KeyStore:getKeyStoreException	(I)Landroid/security/KeyStoreException;
    //   648: invokespecial 266	java/security/KeyStoreException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   651: athrow
    //   652: astore_2
    //   653: iconst_0
    //   654: ifne +21 -> 675
    //   657: iload 6
    //   659: ifeq +596 -> 1255
    //   662: aload_0
    //   663: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   666: aload_1
    //   667: aload_0
    //   668: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   671: invokestatic 294	android/security/Credentials:deleteAllTypesForAlias	(Landroid/security/KeyStore;Ljava/lang/String;I)Z
    //   674: pop
    //   675: aload_2
    //   676: athrow
    //   677: iconst_1
    //   678: istore 7
    //   680: aload_2
    //   681: invokeinterface 310 1 0
    //   686: astore 4
    //   688: aload 4
    //   690: ifnull +38 -> 728
    //   693: ldc_w 312
    //   696: aload 4
    //   698: invokevirtual 250	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   701: ifeq +27 -> 728
    //   704: aload_2
    //   705: invokeinterface 313 1 0
    //   710: astore 4
    //   712: aload 4
    //   714: ifnonnull +55 -> 769
    //   717: new 59	java/security/KeyStoreException
    //   720: dup
    //   721: ldc_w 315
    //   724: invokespecial 129	java/security/KeyStoreException:<init>	(Ljava/lang/String;)V
    //   727: athrow
    //   728: new 59	java/security/KeyStoreException
    //   731: dup
    //   732: new 30	java/lang/StringBuilder
    //   735: dup
    //   736: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   739: ldc_w 317
    //   742: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   745: aload 4
    //   747: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   750: ldc_w 319
    //   753: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   756: ldc_w 321
    //   759: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   762: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   765: invokespecial 129	java/security/KeyStoreException:<init>	(Ljava/lang/String;)V
    //   768: athrow
    //   769: new 323	android/security/keymaster/KeymasterArguments
    //   772: dup
    //   773: invokespecial 324	android/security/keymaster/KeymasterArguments:<init>	()V
    //   776: astore 11
    //   778: aload 11
    //   780: ldc_w 325
    //   783: aload_2
    //   784: invokeinterface 64 1 0
    //   789: invokestatic 331	android/security/keystore/KeyProperties$KeyAlgorithm:toKeymasterAsymmetricKeyAlgorithm	(Ljava/lang/String;)I
    //   792: invokevirtual 335	android/security/keymaster/KeymasterArguments:addEnum	(II)V
    //   795: aload 10
    //   797: invokevirtual 338	android/security/keystore/KeyProtection:getPurposes	()I
    //   800: istore 6
    //   802: aload 11
    //   804: ldc_w 339
    //   807: iload 6
    //   809: invokestatic 345	android/security/keystore/KeyProperties$Purpose:allToKeymaster	(I)[I
    //   812: invokevirtual 349	android/security/keymaster/KeymasterArguments:addEnums	(I[I)V
    //   815: aload 10
    //   817: invokevirtual 352	android/security/keystore/KeyProtection:isDigestsSpecified	()Z
    //   820: ifeq +19 -> 839
    //   823: aload 11
    //   825: ldc_w 353
    //   828: aload 10
    //   830: invokevirtual 357	android/security/keystore/KeyProtection:getDigests	()[Ljava/lang/String;
    //   833: invokestatic 362	android/security/keystore/KeyProperties$Digest:allToKeymaster	([Ljava/lang/String;)[I
    //   836: invokevirtual 349	android/security/keymaster/KeymasterArguments:addEnums	(I[I)V
    //   839: aload 11
    //   841: ldc_w 363
    //   844: aload 10
    //   846: invokevirtual 366	android/security/keystore/KeyProtection:getBlockModes	()[Ljava/lang/String;
    //   849: invokestatic 369	android/security/keystore/KeyProperties$BlockMode:allToKeymaster	([Ljava/lang/String;)[I
    //   852: invokevirtual 349	android/security/keymaster/KeymasterArguments:addEnums	(I[I)V
    //   855: aload 10
    //   857: invokevirtual 372	android/security/keystore/KeyProtection:getEncryptionPaddings	()[Ljava/lang/String;
    //   860: invokestatic 375	android/security/keystore/KeyProperties$EncryptionPadding:allToKeymaster	([Ljava/lang/String;)[I
    //   863: astore_2
    //   864: iload 6
    //   866: iconst_1
    //   867: iand
    //   868: ifeq +96 -> 964
    //   871: aload 10
    //   873: invokevirtual 378	android/security/keystore/KeyProtection:isRandomizedEncryptionRequired	()Z
    //   876: ifeq +88 -> 964
    //   879: iconst_0
    //   880: istore 6
    //   882: aload_2
    //   883: arraylength
    //   884: istore 8
    //   886: iload 6
    //   888: iload 8
    //   890: if_icmpge +74 -> 964
    //   893: aload_2
    //   894: iload 6
    //   896: iaload
    //   897: istore 9
    //   899: iload 9
    //   901: invokestatic 384	android/security/keystore/KeymasterUtils:isKeymasterPaddingSchemeIndCpaCompatibleWithAsymmetricCrypto	(I)Z
    //   904: ifne +51 -> 955
    //   907: new 59	java/security/KeyStoreException
    //   910: dup
    //   911: new 30	java/lang/StringBuilder
    //   914: dup
    //   915: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   918: ldc_w 386
    //   921: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   924: iload 9
    //   926: invokestatic 389	android/security/keystore/KeyProperties$EncryptionPadding:fromKeymaster	(I)Ljava/lang/String;
    //   929: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   932: ldc_w 391
    //   935: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   938: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   941: invokespecial 129	java/security/KeyStoreException:<init>	(Ljava/lang/String;)V
    //   944: athrow
    //   945: astore_1
    //   946: new 59	java/security/KeyStoreException
    //   949: dup
    //   950: aload_1
    //   951: invokespecial 394	java/security/KeyStoreException:<init>	(Ljava/lang/Throwable;)V
    //   954: athrow
    //   955: iload 6
    //   957: iconst_1
    //   958: iadd
    //   959: istore 6
    //   961: goto -75 -> 886
    //   964: aload 11
    //   966: ldc_w 395
    //   969: aload_2
    //   970: invokevirtual 349	android/security/keymaster/KeymasterArguments:addEnums	(I[I)V
    //   973: aload 11
    //   975: ldc_w 395
    //   978: aload 10
    //   980: invokevirtual 398	android/security/keystore/KeyProtection:getSignaturePaddings	()[Ljava/lang/String;
    //   983: invokestatic 401	android/security/keystore/KeyProperties$SignaturePadding:allToKeymaster	([Ljava/lang/String;)[I
    //   986: invokevirtual 349	android/security/keymaster/KeymasterArguments:addEnums	(I[I)V
    //   989: aload 11
    //   991: aload 10
    //   993: invokevirtual 404	android/security/keystore/KeyProtection:isUserAuthenticationRequired	()Z
    //   996: aload 10
    //   998: invokevirtual 407	android/security/keystore/KeyProtection:getUserAuthenticationValidityDurationSeconds	()I
    //   1001: aload 10
    //   1003: invokevirtual 410	android/security/keystore/KeyProtection:isUserAuthenticationValidWhileOnBody	()Z
    //   1006: aload 10
    //   1008: invokevirtual 413	android/security/keystore/KeyProtection:isInvalidatedByBiometricEnrollment	()Z
    //   1011: invokestatic 417	android/security/keystore/KeymasterUtils:addUserAuthArgs	(Landroid/security/keymaster/KeymasterArguments;ZIZZ)V
    //   1014: aload 11
    //   1016: ldc_w 418
    //   1019: aload 10
    //   1021: invokevirtual 422	android/security/keystore/KeyProtection:getKeyValidityStart	()Ljava/util/Date;
    //   1024: invokevirtual 426	android/security/keymaster/KeymasterArguments:addDateIfNotNull	(ILjava/util/Date;)V
    //   1027: aload 11
    //   1029: ldc_w 427
    //   1032: aload 10
    //   1034: invokevirtual 430	android/security/keystore/KeyProtection:getKeyValidityForOriginationEnd	()Ljava/util/Date;
    //   1037: invokevirtual 426	android/security/keymaster/KeymasterArguments:addDateIfNotNull	(ILjava/util/Date;)V
    //   1040: aload 11
    //   1042: ldc_w 431
    //   1045: aload 10
    //   1047: invokevirtual 434	android/security/keystore/KeyProtection:getKeyValidityForConsumptionEnd	()Ljava/util/Date;
    //   1050: invokevirtual 426	android/security/keymaster/KeymasterArguments:addDateIfNotNull	(ILjava/util/Date;)V
    //   1053: aload 11
    //   1055: astore_2
    //   1056: iload 7
    //   1058: istore 6
    //   1060: goto -497 -> 563
    //   1063: aload_0
    //   1064: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   1067: aload_1
    //   1068: aload_0
    //   1069: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   1072: invokestatic 437	android/security/Credentials:deleteCertificateTypesForAlias	(Landroid/security/KeyStore;Ljava/lang/String;I)Z
    //   1075: pop
    //   1076: aload_0
    //   1077: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   1080: aload_1
    //   1081: aload_0
    //   1082: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   1085: invokestatic 440	android/security/Credentials:deleteSecretKeyTypeForAlias	(Landroid/security/KeyStore;Ljava/lang/String;I)Z
    //   1088: pop
    //   1089: aload_0
    //   1090: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   1093: new 30	java/lang/StringBuilder
    //   1096: dup
    //   1097: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   1100: ldc_w 442
    //   1103: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1106: aload_1
    //   1107: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1110: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1113: aload 12
    //   1115: aload_0
    //   1116: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   1119: iload 5
    //   1121: invokevirtual 446	android/security/KeyStore:insert	(Ljava/lang/String;[BII)I
    //   1124: istore 7
    //   1126: iload 7
    //   1128: iconst_1
    //   1129: if_icmpeq +19 -> 1148
    //   1132: new 59	java/security/KeyStoreException
    //   1135: dup
    //   1136: ldc_w 448
    //   1139: iload 7
    //   1141: invokestatic 307	android/security/KeyStore:getKeyStoreException	(I)Landroid/security/KeyStoreException;
    //   1144: invokespecial 266	java/security/KeyStoreException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1147: athrow
    //   1148: aload_0
    //   1149: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   1152: new 30	java/lang/StringBuilder
    //   1155: dup
    //   1156: invokespecial 31	java/lang/StringBuilder:<init>	()V
    //   1159: ldc -64
    //   1161: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1164: aload_1
    //   1165: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1168: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1171: aload_3
    //   1172: aload_0
    //   1173: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   1176: iload 5
    //   1178: invokevirtual 446	android/security/KeyStore:insert	(Ljava/lang/String;[BII)I
    //   1181: istore 5
    //   1183: iload 5
    //   1185: iconst_1
    //   1186: if_icmpeq +19 -> 1205
    //   1189: new 59	java/security/KeyStoreException
    //   1192: dup
    //   1193: ldc_w 450
    //   1196: iload 5
    //   1198: invokestatic 307	android/security/KeyStore:getKeyStoreException	(I)Landroid/security/KeyStoreException;
    //   1201: invokespecial 266	java/security/KeyStoreException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1204: athrow
    //   1205: iconst_1
    //   1206: ifne +21 -> 1227
    //   1209: iload 6
    //   1211: ifeq +17 -> 1228
    //   1214: aload_0
    //   1215: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   1218: aload_1
    //   1219: aload_0
    //   1220: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   1223: invokestatic 294	android/security/Credentials:deleteAllTypesForAlias	(Landroid/security/KeyStore;Ljava/lang/String;I)Z
    //   1226: pop
    //   1227: return
    //   1228: aload_0
    //   1229: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   1232: aload_1
    //   1233: aload_0
    //   1234: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   1237: invokestatic 437	android/security/Credentials:deleteCertificateTypesForAlias	(Landroid/security/KeyStore;Ljava/lang/String;I)Z
    //   1240: pop
    //   1241: aload_0
    //   1242: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   1245: aload_1
    //   1246: aload_0
    //   1247: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   1250: invokestatic 440	android/security/Credentials:deleteSecretKeyTypeForAlias	(Landroid/security/KeyStore;Ljava/lang/String;I)Z
    //   1253: pop
    //   1254: return
    //   1255: aload_0
    //   1256: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   1259: aload_1
    //   1260: aload_0
    //   1261: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   1264: invokestatic 437	android/security/Credentials:deleteCertificateTypesForAlias	(Landroid/security/KeyStore;Ljava/lang/String;I)Z
    //   1267: pop
    //   1268: aload_0
    //   1269: getfield 43	android/security/keystore/AndroidKeyStoreSpi:mKeyStore	Landroid/security/KeyStore;
    //   1272: aload_1
    //   1273: aload_0
    //   1274: getfield 21	android/security/keystore/AndroidKeyStoreSpi:mUid	I
    //   1277: invokestatic 440	android/security/Credentials:deleteSecretKeyTypeForAlias	(Landroid/security/KeyStore;Ljava/lang/String;I)Z
    //   1280: pop
    //   1281: goto -606 -> 675
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1284	0	this	AndroidKeyStoreSpi
    //   0	1284	1	paramString	String
    //   0	1284	2	paramPrivateKey	PrivateKey
    //   0	1284	3	paramArrayOfCertificate	Certificate[]
    //   0	1284	4	paramProtectionParameter	KeyStore.ProtectionParameter
    //   1	1196	5	i	int
    //   158	1052	6	j	int
    //   295	845	7	k	int
    //   329	562	8	m	int
    //   897	28	9	n	int
    //   12	1034	10	localObject1	Object
    //   45	1009	11	localObject2	Object
    //   276	838	12	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   269	278	347	java/security/cert/CertificateEncodingException
    //   308	331	360	java/security/cert/CertificateEncodingException
    //   568	630	652	finally
    //   636	652	652	finally
    //   1063	1089	652	finally
    //   1089	1126	652	finally
    //   1132	1148	652	finally
    //   1148	1183	652	finally
    //   1189	1205	652	finally
    //   778	839	945	java/lang/IllegalArgumentException
    //   778	839	945	java/lang/IllegalStateException
    //   839	864	945	java/lang/IllegalArgumentException
    //   839	864	945	java/lang/IllegalStateException
    //   871	879	945	java/lang/IllegalArgumentException
    //   871	879	945	java/lang/IllegalStateException
    //   882	886	945	java/lang/IllegalArgumentException
    //   882	886	945	java/lang/IllegalStateException
    //   899	945	945	java/lang/IllegalArgumentException
    //   899	945	945	java/lang/IllegalStateException
    //   964	1053	945	java/lang/IllegalArgumentException
    //   964	1053	945	java/lang/IllegalStateException
  }
  
  private void setSecretKeyEntry(String paramString, SecretKey paramSecretKey, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException
  {
    KeyProtection localKeyProtection;
    if ((paramProtectionParameter == null) || ((paramProtectionParameter instanceof KeyProtection)))
    {
      localKeyProtection = (KeyProtection)paramProtectionParameter;
      if (!(paramSecretKey instanceof AndroidKeyStoreSecretKey)) {
        break label204;
      }
      paramSecretKey = ((AndroidKeyStoreSecretKey)paramSecretKey).getAlias();
      if (paramSecretKey == null) {
        throw new KeyStoreException("KeyStore-backed secret key does not have an alias");
      }
    }
    else
    {
      throw new KeyStoreException("Unsupported protection parameter class: " + paramProtectionParameter.getClass().getName() + ". Supported: " + KeyProtection.class.getName());
    }
    if (!paramSecretKey.startsWith("USRSKEY_")) {
      throw new KeyStoreException("KeyStore-backed secret key has invalid alias: " + paramSecretKey);
    }
    paramSecretKey = paramSecretKey.substring("USRSKEY_".length());
    if (!paramString.equals(paramSecretKey)) {
      throw new KeyStoreException("Can only replace KeyStore-backed keys with same alias: " + paramString + " != " + paramSecretKey);
    }
    if (localKeyProtection != null) {
      throw new KeyStoreException("Modifying KeyStore-backed key using protection parameters not supported");
    }
    return;
    label204:
    if (localKeyProtection == null) {
      throw new KeyStoreException("Protection parameters must be specified when importing a symmetric key");
    }
    paramProtectionParameter = paramSecretKey.getFormat();
    if (paramProtectionParameter == null) {
      throw new KeyStoreException("Only secret keys that export their key material are supported");
    }
    if (!"RAW".equals(paramProtectionParameter)) {
      throw new KeyStoreException("Unsupported secret key material export format: " + paramProtectionParameter);
    }
    byte[] arrayOfByte = paramSecretKey.getEncoded();
    if (arrayOfByte == null) {
      throw new KeyStoreException("Key did not export its key material despite supporting RAW format export");
    }
    KeymasterArguments localKeymasterArguments = new KeymasterArguments();
    int j;
    int i;
    try
    {
      j = KeyProperties.KeyAlgorithm.toKeymasterSecretKeyAlgorithm(paramSecretKey.getAlgorithm());
      localKeymasterArguments.addEnum(268435458, j);
      if (j != 128) {
        break label517;
      }
      i = KeyProperties.KeyAlgorithm.toKeymasterDigest(paramSecretKey.getAlgorithm());
      if (i == -1) {
        throw new ProviderException("HMAC key algorithm digest unknown for key algorithm " + paramSecretKey.getAlgorithm());
      }
    }
    catch (IllegalArgumentException|IllegalStateException paramString)
    {
      throw new KeyStoreException(paramString);
    }
    int[] arrayOfInt1 = new int[1];
    arrayOfInt1[0] = i;
    paramProtectionParameter = arrayOfInt1;
    if (localKeyProtection.isDigestsSpecified())
    {
      int[] arrayOfInt2 = KeyProperties.Digest.allToKeymaster(localKeyProtection.getDigests());
      if (arrayOfInt2.length == 1)
      {
        paramProtectionParameter = arrayOfInt1;
        if (arrayOfInt2[0] == i) {}
      }
      else
      {
        throw new KeyStoreException("Unsupported digests specification: " + Arrays.asList(localKeyProtection.getDigests()) + ". Only " + KeyProperties.Digest.fromKeymaster(i) + " supported for HMAC key algorithm " + paramSecretKey.getAlgorithm());
        label517:
        if (!localKeyProtection.isDigestsSpecified()) {
          break label640;
        }
        paramProtectionParameter = KeyProperties.Digest.allToKeymaster(localKeyProtection.getDigests());
      }
    }
    localKeymasterArguments.addEnums(536870917, paramProtectionParameter);
    int k = localKeyProtection.getPurposes();
    paramSecretKey = KeyProperties.BlockMode.allToKeymaster(localKeyProtection.getBlockModes());
    int m;
    if (((k & 0x1) != 0) && (localKeyProtection.isRandomizedEncryptionRequired()))
    {
      i = 0;
      m = paramSecretKey.length;
    }
    for (;;)
    {
      if (i < m)
      {
        int n = paramSecretKey[i];
        if (KeymasterUtils.isKeymasterBlockModeIndCpaCompatibleWithSymmetricCrypto(n)) {
          break label904;
        }
        throw new KeyStoreException("Randomized encryption (IND-CPA) required but may be violated by block mode: " + KeyProperties.BlockMode.fromKeymaster(n) + ". See KeyProtection documentation.");
        label640:
        paramProtectionParameter = EmptyArray.INT;
        break;
      }
      localKeymasterArguments.addEnums(536870913, KeyProperties.Purpose.allToKeymaster(k));
      localKeymasterArguments.addEnums(536870916, paramSecretKey);
      if (localKeyProtection.getSignaturePaddings().length > 0) {
        throw new KeyStoreException("Signature paddings not supported for symmetric keys");
      }
      localKeymasterArguments.addEnums(536870918, KeyProperties.EncryptionPadding.allToKeymaster(localKeyProtection.getEncryptionPaddings()));
      KeymasterUtils.addUserAuthArgs(localKeymasterArguments, localKeyProtection.isUserAuthenticationRequired(), localKeyProtection.getUserAuthenticationValidityDurationSeconds(), localKeyProtection.isUserAuthenticationValidWhileOnBody(), localKeyProtection.isInvalidatedByBiometricEnrollment());
      KeymasterUtils.addMinMacLengthAuthorizationIfNecessary(localKeymasterArguments, j, paramSecretKey, paramProtectionParameter);
      localKeymasterArguments.addDateIfNotNull(1610613136, localKeyProtection.getKeyValidityStart());
      localKeymasterArguments.addDateIfNotNull(1610613137, localKeyProtection.getKeyValidityForOriginationEnd());
      localKeymasterArguments.addDateIfNotNull(1610613138, localKeyProtection.getKeyValidityForConsumptionEnd());
      if ((k & 0x1) != 0)
      {
        boolean bool = localKeyProtection.isRandomizedEncryptionRequired();
        if (!bool) {
          break label892;
        }
      }
      for (;;)
      {
        Credentials.deleteAllTypesForAlias(this.mKeyStore, paramString, this.mUid);
        paramString = "USRSKEY_" + paramString;
        i = this.mKeyStore.importKey(paramString, localKeymasterArguments, 3, arrayOfByte, this.mUid, 0, new KeyCharacteristics());
        if (i == 1) {
          break;
        }
        throw new KeyStoreException("Failed to import secret key. Keystore error code: " + i);
        label892:
        localKeymasterArguments.addBoolean(1879048199);
      }
      return;
      label904:
      i += 1;
    }
  }
  
  private static X509Certificate toCertificate(byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(paramArrayOfByte));
      return paramArrayOfByte;
    }
    catch (CertificateException paramArrayOfByte)
    {
      Log.w("AndroidKeyStore", "Couldn't parse certificate in keystore", paramArrayOfByte);
    }
    return null;
  }
  
  private static Collection<X509Certificate> toCertificates(byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = CertificateFactory.getInstance("X.509").generateCertificates(new ByteArrayInputStream(paramArrayOfByte));
      return paramArrayOfByte;
    }
    catch (CertificateException paramArrayOfByte)
    {
      Log.w("AndroidKeyStore", "Couldn't parse certificates in keystore", paramArrayOfByte);
    }
    return new ArrayList();
  }
  
  private static KeyStoreX509Certificate wrapIntoKeyStoreCertificate(String paramString, int paramInt, X509Certificate paramX509Certificate)
  {
    KeyStoreX509Certificate localKeyStoreX509Certificate = null;
    if (paramX509Certificate != null) {
      localKeyStoreX509Certificate = new KeyStoreX509Certificate(paramString, paramInt, paramX509Certificate);
    }
    return localKeyStoreX509Certificate;
  }
  
  public Enumeration<String> engineAliases()
  {
    return Collections.enumeration(getUniqueAliases());
  }
  
  public boolean engineContainsAlias(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    if ((!this.mKeyStore.contains("USRPKEY_" + paramString, this.mUid)) && (!this.mKeyStore.contains("USRSKEY_" + paramString, this.mUid)) && (!this.mKeyStore.contains("USRCERT_" + paramString, this.mUid))) {
      return this.mKeyStore.contains("CACERT_" + paramString, this.mUid);
    }
    return true;
  }
  
  public void engineDeleteEntry(String paramString)
    throws KeyStoreException
  {
    if (!Credentials.deleteAllTypesForAlias(this.mKeyStore, paramString, this.mUid)) {
      throw new KeyStoreException("Failed to delete entry: " + paramString);
    }
  }
  
  public Certificate engineGetCertificate(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    byte[] arrayOfByte = this.mKeyStore.get("USRCERT_" + paramString, this.mUid);
    if (arrayOfByte != null) {
      return getCertificateForPrivateKeyEntry(paramString, arrayOfByte);
    }
    paramString = this.mKeyStore.get("CACERT_" + paramString, this.mUid);
    if (paramString != null) {
      return getCertificateForTrustedCertificateEntry(paramString);
    }
    return null;
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate)
  {
    int j = 0;
    if (paramCertificate == null) {
      return null;
    }
    if (!"X.509".equalsIgnoreCase(paramCertificate.getType())) {
      return null;
    }
    try
    {
      paramCertificate = paramCertificate.getEncoded();
      if (paramCertificate == null) {
        return null;
      }
    }
    catch (CertificateEncodingException paramCertificate)
    {
      return null;
    }
    HashSet localHashSet = new HashSet();
    String[] arrayOfString = this.mKeyStore.list("USRCERT_", this.mUid);
    int k;
    int i;
    byte[] arrayOfByte;
    if (arrayOfString != null)
    {
      k = arrayOfString.length;
      i = 0;
      if (i < k)
      {
        localObject = arrayOfString[i];
        arrayOfByte = this.mKeyStore.get("USRCERT_" + (String)localObject, this.mUid);
        if (arrayOfByte == null) {}
        do
        {
          i += 1;
          break;
          localHashSet.add(localObject);
        } while (!Arrays.equals(arrayOfByte, paramCertificate));
        return (String)localObject;
      }
    }
    Object localObject = this.mKeyStore.list("CACERT_", this.mUid);
    if (arrayOfString != null)
    {
      k = localObject.length;
      i = j;
      if (i < k)
      {
        arrayOfString = localObject[i];
        if (localHashSet.contains(arrayOfString)) {}
        do
        {
          i += 1;
          break;
          arrayOfByte = this.mKeyStore.get("CACERT_" + arrayOfString, this.mUid);
        } while ((arrayOfByte == null) || (!Arrays.equals(arrayOfByte, paramCertificate)));
        return arrayOfString;
      }
    }
    return null;
  }
  
  public Certificate[] engineGetCertificateChain(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    X509Certificate localX509Certificate = (X509Certificate)engineGetCertificate(paramString);
    if (localX509Certificate == null) {
      return null;
    }
    paramString = this.mKeyStore.get("CACERT_" + paramString, this.mUid);
    if (paramString != null)
    {
      paramString = toCertificates(paramString);
      Certificate[] arrayOfCertificate = new Certificate[paramString.size() + 1];
      Iterator localIterator = paramString.iterator();
      int i = 1;
      for (;;)
      {
        paramString = arrayOfCertificate;
        if (!localIterator.hasNext()) {
          break;
        }
        arrayOfCertificate[i] = ((Certificate)localIterator.next());
        i += 1;
      }
    }
    paramString = new Certificate[1];
    paramString[0] = localX509Certificate;
    return paramString;
  }
  
  public Date engineGetCreationDate(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    Date localDate = getModificationDate("USRPKEY_" + paramString);
    if (localDate != null) {
      return localDate;
    }
    localDate = getModificationDate("USRSKEY_" + paramString);
    if (localDate != null) {
      return localDate;
    }
    localDate = getModificationDate("USRCERT_" + paramString);
    if (localDate != null) {
      return localDate;
    }
    return getModificationDate("CACERT_" + paramString);
  }
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar)
    throws NoSuchAlgorithmException, UnrecoverableKeyException
  {
    if (isPrivateKeyEntry(paramString))
    {
      paramString = "USRPKEY_" + paramString;
      return AndroidKeyStoreProvider.loadAndroidKeyStorePrivateKeyFromKeystore(this.mKeyStore, paramString, this.mUid);
    }
    if (isSecretKeyEntry(paramString))
    {
      paramString = "USRSKEY_" + paramString;
      return AndroidKeyStoreProvider.loadAndroidKeyStoreSecretKeyFromKeystore(this.mKeyStore, paramString, this.mUid);
    }
    return null;
  }
  
  public boolean engineIsCertificateEntry(String paramString)
  {
    if (!isKeyEntry(paramString)) {
      return isCertificateEntry(paramString);
    }
    return false;
  }
  
  public boolean engineIsKeyEntry(String paramString)
  {
    return isKeyEntry(paramString);
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    if (paramInputStream != null) {
      throw new IllegalArgumentException("InputStream not supported");
    }
    if (paramArrayOfChar != null) {
      throw new IllegalArgumentException("password not supported");
    }
    this.mKeyStore = KeyStore.getInstance();
    this.mUid = -1;
  }
  
  public void engineLoad(KeyStore.LoadStoreParameter paramLoadStoreParameter)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    int i = -1;
    if (paramLoadStoreParameter != null)
    {
      if ((paramLoadStoreParameter instanceof AndroidKeyStoreLoadStoreParameter)) {
        i = ((AndroidKeyStoreLoadStoreParameter)paramLoadStoreParameter).getUid();
      }
    }
    else
    {
      this.mKeyStore = KeyStore.getInstance();
      this.mUid = i;
      return;
    }
    throw new IllegalArgumentException("Unsupported param type: " + paramLoadStoreParameter.getClass());
  }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
    throws KeyStoreException
  {
    if (isKeyEntry(paramString)) {
      throw new KeyStoreException("Entry exists and is not a trusted certificate");
    }
    if (paramCertificate == null) {
      throw new NullPointerException("cert == null");
    }
    try
    {
      paramCertificate = paramCertificate.getEncoded();
      if (!this.mKeyStore.put("CACERT_" + paramString, paramCertificate, this.mUid, 0)) {
        throw new KeyStoreException("Couldn't insert certificate; is KeyStore initialized?");
      }
    }
    catch (CertificateEncodingException paramString)
    {
      throw new KeyStoreException(paramString);
    }
  }
  
  public void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter)
    throws KeyStoreException
  {
    if (paramEntry == null) {
      throw new KeyStoreException("entry == null");
    }
    Credentials.deleteAllTypesForAlias(this.mKeyStore, paramString, this.mUid);
    if ((paramEntry instanceof KeyStore.TrustedCertificateEntry))
    {
      engineSetCertificateEntry(paramString, ((KeyStore.TrustedCertificateEntry)paramEntry).getTrustedCertificate());
      return;
    }
    if ((paramEntry instanceof KeyStore.PrivateKeyEntry))
    {
      paramEntry = (KeyStore.PrivateKeyEntry)paramEntry;
      setPrivateKeyEntry(paramString, paramEntry.getPrivateKey(), paramEntry.getCertificateChain(), paramProtectionParameter);
      return;
    }
    if ((paramEntry instanceof KeyStore.SecretKeyEntry))
    {
      setSecretKeyEntry(paramString, ((KeyStore.SecretKeyEntry)paramEntry).getSecretKey(), paramProtectionParameter);
      return;
    }
    throw new KeyStoreException("Entry must be a PrivateKeyEntry, SecretKeyEntry or TrustedCertificateEntry; was " + paramEntry);
  }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    if ((paramArrayOfChar != null) && (paramArrayOfChar.length > 0)) {
      throw new KeyStoreException("entries cannot be protected with passwords");
    }
    if ((paramKey instanceof PrivateKey))
    {
      setPrivateKeyEntry(paramString, (PrivateKey)paramKey, paramArrayOfCertificate, null);
      return;
    }
    if ((paramKey instanceof SecretKey))
    {
      setSecretKeyEntry(paramString, (SecretKey)paramKey, null);
      return;
    }
    throw new KeyStoreException("Only PrivateKey and SecretKey are supported");
  }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    throw new KeyStoreException("Operation not supported because key encoding is unknown");
  }
  
  public int engineSize()
  {
    return getUniqueAliases().size();
  }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    throw new UnsupportedOperationException("Can not serialize AndroidKeyStore to OutputStream");
  }
  
  static class KeyStoreX509Certificate
    extends DelegatingX509Certificate
  {
    private final String mPrivateKeyAlias;
    private final int mPrivateKeyUid;
    
    KeyStoreX509Certificate(String paramString, int paramInt, X509Certificate paramX509Certificate)
    {
      super();
      this.mPrivateKeyAlias = paramString;
      this.mPrivateKeyUid = paramInt;
    }
    
    public PublicKey getPublicKey()
    {
      PublicKey localPublicKey = super.getPublicKey();
      return AndroidKeyStoreProvider.getAndroidKeyStorePublicKey(this.mPrivateKeyAlias, this.mPrivateKeyUid, localPublicKey.getAlgorithm(), localPublicKey.getEncoded());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */