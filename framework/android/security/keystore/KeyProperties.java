package android.security.keystore;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import libcore.util.EmptyArray;

public abstract class KeyProperties
{
  public static final String BLOCK_MODE_CBC = "CBC";
  public static final String BLOCK_MODE_CTR = "CTR";
  public static final String BLOCK_MODE_ECB = "ECB";
  public static final String BLOCK_MODE_GCM = "GCM";
  public static final String DIGEST_MD5 = "MD5";
  public static final String DIGEST_NONE = "NONE";
  public static final String DIGEST_SHA1 = "SHA-1";
  public static final String DIGEST_SHA224 = "SHA-224";
  public static final String DIGEST_SHA256 = "SHA-256";
  public static final String DIGEST_SHA384 = "SHA-384";
  public static final String DIGEST_SHA512 = "SHA-512";
  public static final String ENCRYPTION_PADDING_NONE = "NoPadding";
  public static final String ENCRYPTION_PADDING_PKCS7 = "PKCS7Padding";
  public static final String ENCRYPTION_PADDING_RSA_OAEP = "OAEPPadding";
  public static final String ENCRYPTION_PADDING_RSA_PKCS1 = "PKCS1Padding";
  public static final String KEY_ALGORITHM_AES = "AES";
  public static final String KEY_ALGORITHM_EC = "EC";
  public static final String KEY_ALGORITHM_HMAC_SHA1 = "HmacSHA1";
  public static final String KEY_ALGORITHM_HMAC_SHA224 = "HmacSHA224";
  public static final String KEY_ALGORITHM_HMAC_SHA256 = "HmacSHA256";
  public static final String KEY_ALGORITHM_HMAC_SHA384 = "HmacSHA384";
  public static final String KEY_ALGORITHM_HMAC_SHA512 = "HmacSHA512";
  public static final String KEY_ALGORITHM_RSA = "RSA";
  public static final int ORIGIN_GENERATED = 1;
  public static final int ORIGIN_IMPORTED = 2;
  public static final int ORIGIN_UNKNOWN = 4;
  public static final int PURPOSE_DECRYPT = 2;
  public static final int PURPOSE_ENCRYPT = 1;
  public static final int PURPOSE_SIGN = 4;
  public static final int PURPOSE_VERIFY = 8;
  public static final String SIGNATURE_PADDING_RSA_PKCS1 = "PKCS1";
  public static final String SIGNATURE_PADDING_RSA_PSS = "PSS";
  
  private static int getSetBitCount(int paramInt)
  {
    if (paramInt == 0) {
      return 0;
    }
    int j;
    for (int i = 0; paramInt != 0; i = j)
    {
      j = i;
      if ((paramInt & 0x1) != 0) {
        j = i + 1;
      }
      paramInt >>>= 1;
    }
    return i;
  }
  
  private static int[] getSetFlags(int paramInt)
  {
    if (paramInt == 0) {
      return EmptyArray.INT;
    }
    int[] arrayOfInt = new int[getSetBitCount(paramInt)];
    int j = 0;
    int i = 1;
    while (paramInt != 0)
    {
      int k = j;
      if ((paramInt & 0x1) != 0)
      {
        arrayOfInt[j] = i;
        k = j + 1;
      }
      paramInt >>>= 1;
      i <<= 1;
      j = k;
    }
    return arrayOfInt;
  }
  
  public static abstract class BlockMode
  {
    public static String[] allFromKeymaster(Collection<Integer> paramCollection)
    {
      if ((paramCollection == null) || (paramCollection.isEmpty())) {
        return EmptyArray.STRING;
      }
      String[] arrayOfString = new String[paramCollection.size()];
      int i = 0;
      paramCollection = paramCollection.iterator();
      while (paramCollection.hasNext())
      {
        arrayOfString[i] = fromKeymaster(((Integer)paramCollection.next()).intValue());
        i += 1;
      }
      return arrayOfString;
    }
    
    public static int[] allToKeymaster(String[] paramArrayOfString)
    {
      if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
        return EmptyArray.INT;
      }
      int[] arrayOfInt = new int[paramArrayOfString.length];
      int i = 0;
      while (i < paramArrayOfString.length)
      {
        arrayOfInt[i] = toKeymaster(paramArrayOfString[i]);
        i += 1;
      }
      return arrayOfInt;
    }
    
    public static String fromKeymaster(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Unsupported block mode: " + paramInt);
      case 1: 
        return "ECB";
      case 2: 
        return "CBC";
      case 3: 
        return "CTR";
      }
      return "GCM";
    }
    
    public static int toKeymaster(String paramString)
    {
      if ("ECB".equalsIgnoreCase(paramString)) {
        return 1;
      }
      if ("CBC".equalsIgnoreCase(paramString)) {
        return 2;
      }
      if ("CTR".equalsIgnoreCase(paramString)) {
        return 3;
      }
      if ("GCM".equalsIgnoreCase(paramString)) {
        return 32;
      }
      throw new IllegalArgumentException("Unsupported block mode: " + paramString);
    }
  }
  
  public static abstract class Digest
  {
    public static String[] allFromKeymaster(Collection<Integer> paramCollection)
    {
      if (paramCollection.isEmpty()) {
        return EmptyArray.STRING;
      }
      String[] arrayOfString = new String[paramCollection.size()];
      int i = 0;
      paramCollection = paramCollection.iterator();
      while (paramCollection.hasNext())
      {
        arrayOfString[i] = fromKeymaster(((Integer)paramCollection.next()).intValue());
        i += 1;
      }
      return arrayOfString;
    }
    
    public static int[] allToKeymaster(String[] paramArrayOfString)
    {
      int i = 0;
      if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
        return EmptyArray.INT;
      }
      int[] arrayOfInt = new int[paramArrayOfString.length];
      int j = 0;
      int k = paramArrayOfString.length;
      while (i < k)
      {
        arrayOfInt[j] = toKeymaster(paramArrayOfString[i]);
        j += 1;
        i += 1;
      }
      return arrayOfInt;
    }
    
    public static String fromKeymaster(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Unsupported digest algorithm: " + paramInt);
      case 0: 
        return "NONE";
      case 1: 
        return "MD5";
      case 2: 
        return "SHA-1";
      case 3: 
        return "SHA-224";
      case 4: 
        return "SHA-256";
      case 5: 
        return "SHA-384";
      }
      return "SHA-512";
    }
    
    public static String fromKeymasterToSignatureAlgorithmDigest(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Unsupported digest algorithm: " + paramInt);
      case 0: 
        return "NONE";
      case 1: 
        return "MD5";
      case 2: 
        return "SHA1";
      case 3: 
        return "SHA224";
      case 4: 
        return "SHA256";
      case 5: 
        return "SHA384";
      }
      return "SHA512";
    }
    
    public static int toKeymaster(String paramString)
    {
      String str = paramString.toUpperCase(Locale.US);
      if (str.equals("SHA-1")) {
        return 2;
      }
      if (str.equals("SHA-224")) {
        return 3;
      }
      if (str.equals("SHA-256")) {
        return 4;
      }
      if (str.equals("SHA-384")) {
        return 5;
      }
      if (str.equals("SHA-512")) {
        return 6;
      }
      if (str.equals("NONE")) {
        return 0;
      }
      if (str.equals("MD5")) {
        return 1;
      }
      throw new IllegalArgumentException("Unsupported digest algorithm: " + paramString);
    }
  }
  
  public static abstract class EncryptionPadding
  {
    public static int[] allToKeymaster(String[] paramArrayOfString)
    {
      if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
        return EmptyArray.INT;
      }
      int[] arrayOfInt = new int[paramArrayOfString.length];
      int i = 0;
      while (i < paramArrayOfString.length)
      {
        arrayOfInt[i] = toKeymaster(paramArrayOfString[i]);
        i += 1;
      }
      return arrayOfInt;
    }
    
    public static String fromKeymaster(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Unsupported encryption padding: " + paramInt);
      case 1: 
        return "NoPadding";
      case 64: 
        return "PKCS7Padding";
      case 4: 
        return "PKCS1Padding";
      }
      return "OAEPPadding";
    }
    
    public static int toKeymaster(String paramString)
    {
      if ("NoPadding".equalsIgnoreCase(paramString)) {
        return 1;
      }
      if ("PKCS7Padding".equalsIgnoreCase(paramString)) {
        return 64;
      }
      if ("PKCS1Padding".equalsIgnoreCase(paramString)) {
        return 4;
      }
      if ("OAEPPadding".equalsIgnoreCase(paramString)) {
        return 2;
      }
      throw new IllegalArgumentException("Unsupported encryption padding scheme: " + paramString);
    }
  }
  
  public static abstract class KeyAlgorithm
  {
    public static String fromKeymasterAsymmetricKeyAlgorithm(int paramInt)
    {
      switch (paramInt)
      {
      case 2: 
      default: 
        throw new IllegalArgumentException("Unsupported key algorithm: " + paramInt);
      case 3: 
        return "EC";
      }
      return "RSA";
    }
    
    public static String fromKeymasterSecretKeyAlgorithm(int paramInt1, int paramInt2)
    {
      switch (paramInt1)
      {
      default: 
        throw new IllegalArgumentException("Unsupported key algorithm: " + paramInt1);
      case 32: 
        return "AES";
      }
      switch (paramInt2)
      {
      default: 
        throw new IllegalArgumentException("Unsupported HMAC digest: " + KeyProperties.Digest.fromKeymaster(paramInt2));
      case 2: 
        return "HmacSHA1";
      case 3: 
        return "HmacSHA224";
      case 4: 
        return "HmacSHA256";
      case 5: 
        return "HmacSHA384";
      }
      return "HmacSHA512";
    }
    
    public static int toKeymasterAsymmetricKeyAlgorithm(String paramString)
    {
      if ("EC".equalsIgnoreCase(paramString)) {
        return 3;
      }
      if ("RSA".equalsIgnoreCase(paramString)) {
        return 1;
      }
      throw new IllegalArgumentException("Unsupported key algorithm: " + paramString);
    }
    
    public static int toKeymasterDigest(String paramString)
    {
      paramString = paramString.toUpperCase(Locale.US);
      if (paramString.startsWith("HMAC"))
      {
        paramString = paramString.substring("HMAC".length());
        if (paramString.equals("SHA1")) {
          return 2;
        }
        if (paramString.equals("SHA224")) {
          return 3;
        }
        if (paramString.equals("SHA256")) {
          return 4;
        }
        if (paramString.equals("SHA384")) {
          return 5;
        }
        if (paramString.equals("SHA512")) {
          return 6;
        }
        throw new IllegalArgumentException("Unsupported HMAC digest: " + paramString);
      }
      return -1;
    }
    
    public static int toKeymasterSecretKeyAlgorithm(String paramString)
    {
      if ("AES".equalsIgnoreCase(paramString)) {
        return 32;
      }
      if (paramString.toUpperCase(Locale.US).startsWith("HMAC")) {
        return 128;
      }
      throw new IllegalArgumentException("Unsupported secret key algorithm: " + paramString);
    }
  }
  
  public static abstract class Origin
  {
    public static int fromKeymaster(int paramInt)
    {
      switch (paramInt)
      {
      case 1: 
      default: 
        throw new IllegalArgumentException("Unknown origin: " + paramInt);
      case 0: 
        return 1;
      case 2: 
        return 2;
      }
      return 4;
    }
  }
  
  public static abstract class Purpose
  {
    public static int allFromKeymaster(Collection<Integer> paramCollection)
    {
      int i = 0;
      paramCollection = paramCollection.iterator();
      while (paramCollection.hasNext()) {
        i |= fromKeymaster(((Integer)paramCollection.next()).intValue());
      }
      return i;
    }
    
    public static int[] allToKeymaster(int paramInt)
    {
      int[] arrayOfInt = KeyProperties.-wrap0(paramInt);
      paramInt = 0;
      while (paramInt < arrayOfInt.length)
      {
        arrayOfInt[paramInt] = toKeymaster(arrayOfInt[paramInt]);
        paramInt += 1;
      }
      return arrayOfInt;
    }
    
    public static int fromKeymaster(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Unknown purpose: " + paramInt);
      case 0: 
        return 1;
      case 1: 
        return 2;
      case 2: 
        return 4;
      }
      return 8;
    }
    
    public static int toKeymaster(int paramInt)
    {
      switch (paramInt)
      {
      case 3: 
      case 5: 
      case 6: 
      case 7: 
      default: 
        throw new IllegalArgumentException("Unknown purpose: " + paramInt);
      case 1: 
        return 0;
      case 2: 
        return 1;
      case 4: 
        return 2;
      }
      return 3;
    }
  }
  
  static abstract class SignaturePadding
  {
    static int[] allToKeymaster(String[] paramArrayOfString)
    {
      if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
        return EmptyArray.INT;
      }
      int[] arrayOfInt = new int[paramArrayOfString.length];
      int i = 0;
      while (i < paramArrayOfString.length)
      {
        arrayOfInt[i] = toKeymaster(paramArrayOfString[i]);
        i += 1;
      }
      return arrayOfInt;
    }
    
    static String fromKeymaster(int paramInt)
    {
      switch (paramInt)
      {
      case 4: 
      default: 
        throw new IllegalArgumentException("Unsupported signature padding: " + paramInt);
      case 5: 
        return "PKCS1";
      }
      return "PSS";
    }
    
    static int toKeymaster(String paramString)
    {
      String str = paramString.toUpperCase(Locale.US);
      if (str.equals("PKCS1")) {
        return 5;
      }
      if (str.equals("PSS")) {
        return 3;
      }
      throw new IllegalArgumentException("Unsupported signature padding scheme: " + paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */