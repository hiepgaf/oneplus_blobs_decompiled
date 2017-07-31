package android.security.keystore;

import android.text.TextUtils;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;
import javax.security.auth.x500.X500Principal;

public final class KeyGenParameterSpec
  implements AlgorithmParameterSpec
{
  private static final Date DEFAULT_CERT_NOT_AFTER = new Date(2461449600000L);
  private static final Date DEFAULT_CERT_NOT_BEFORE;
  private static final BigInteger DEFAULT_CERT_SERIAL_NUMBER;
  private static final X500Principal DEFAULT_CERT_SUBJECT = new X500Principal("CN=fake");
  private final byte[] mAttestationChallenge;
  private final String[] mBlockModes;
  private final Date mCertificateNotAfter;
  private final Date mCertificateNotBefore;
  private final BigInteger mCertificateSerialNumber;
  private final X500Principal mCertificateSubject;
  private final String[] mDigests;
  private final String[] mEncryptionPaddings;
  private final boolean mInvalidatedByBiometricEnrollment;
  private final int mKeySize;
  private final Date mKeyValidityForConsumptionEnd;
  private final Date mKeyValidityForOriginationEnd;
  private final Date mKeyValidityStart;
  private final String mKeystoreAlias;
  private final int mPurposes;
  private final boolean mRandomizedEncryptionRequired;
  private final String[] mSignaturePaddings;
  private final AlgorithmParameterSpec mSpec;
  private final int mUid;
  private final boolean mUniqueIdIncluded;
  private final boolean mUseSecureProcessor;
  private final boolean mUserAuthenticationRequired;
  private final boolean mUserAuthenticationValidWhileOnBody;
  private final int mUserAuthenticationValidityDurationSeconds;
  
  static
  {
    DEFAULT_CERT_SERIAL_NUMBER = new BigInteger("1");
    DEFAULT_CERT_NOT_BEFORE = new Date(0L);
  }
  
  public KeyGenParameterSpec(String paramString, int paramInt1, int paramInt2, AlgorithmParameterSpec paramAlgorithmParameterSpec, X500Principal paramX500Principal, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, Date paramDate5, int paramInt3, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, String[] paramArrayOfString4, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, byte[] paramArrayOfByte, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("keyStoreAlias must not be empty");
    }
    X500Principal localX500Principal = paramX500Principal;
    if (paramX500Principal == null) {
      localX500Principal = DEFAULT_CERT_SUBJECT;
    }
    paramX500Principal = paramDate1;
    if (paramDate1 == null) {
      paramX500Principal = DEFAULT_CERT_NOT_BEFORE;
    }
    paramDate1 = paramDate2;
    if (paramDate2 == null) {
      paramDate1 = DEFAULT_CERT_NOT_AFTER;
    }
    paramDate2 = paramBigInteger;
    if (paramBigInteger == null) {
      paramDate2 = DEFAULT_CERT_SERIAL_NUMBER;
    }
    if (paramDate1.before(paramX500Principal)) {
      throw new IllegalArgumentException("certificateNotAfter < certificateNotBefore");
    }
    this.mKeystoreAlias = paramString;
    this.mUid = paramInt1;
    this.mKeySize = paramInt2;
    this.mSpec = paramAlgorithmParameterSpec;
    this.mCertificateSubject = localX500Principal;
    this.mCertificateSerialNumber = paramDate2;
    this.mCertificateNotBefore = Utils.cloneIfNotNull(paramX500Principal);
    this.mCertificateNotAfter = Utils.cloneIfNotNull(paramDate1);
    this.mKeyValidityStart = Utils.cloneIfNotNull(paramDate3);
    this.mKeyValidityForOriginationEnd = Utils.cloneIfNotNull(paramDate4);
    this.mKeyValidityForConsumptionEnd = Utils.cloneIfNotNull(paramDate5);
    this.mPurposes = paramInt3;
    this.mDigests = ArrayUtils.cloneIfNotEmpty(paramArrayOfString1);
    this.mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString2));
    this.mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString3));
    this.mBlockModes = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString4));
    this.mRandomizedEncryptionRequired = paramBoolean1;
    this.mUserAuthenticationRequired = paramBoolean2;
    this.mUserAuthenticationValidityDurationSeconds = paramInt4;
    this.mAttestationChallenge = Utils.cloneIfNotNull(paramArrayOfByte);
    this.mUniqueIdIncluded = paramBoolean3;
    this.mUserAuthenticationValidWhileOnBody = paramBoolean4;
    this.mInvalidatedByBiometricEnrollment = paramBoolean5;
    this.mUseSecureProcessor = paramBoolean6;
  }
  
  public AlgorithmParameterSpec getAlgorithmParameterSpec()
  {
    return this.mSpec;
  }
  
  public byte[] getAttestationChallenge()
  {
    return Utils.cloneIfNotNull(this.mAttestationChallenge);
  }
  
  public String[] getBlockModes()
  {
    return ArrayUtils.cloneIfNotEmpty(this.mBlockModes);
  }
  
  public Date getCertificateNotAfter()
  {
    return Utils.cloneIfNotNull(this.mCertificateNotAfter);
  }
  
  public Date getCertificateNotBefore()
  {
    return Utils.cloneIfNotNull(this.mCertificateNotBefore);
  }
  
  public BigInteger getCertificateSerialNumber()
  {
    return this.mCertificateSerialNumber;
  }
  
  public X500Principal getCertificateSubject()
  {
    return this.mCertificateSubject;
  }
  
  public String[] getDigests()
  {
    if (this.mDigests == null) {
      throw new IllegalStateException("Digests not specified");
    }
    return ArrayUtils.cloneIfNotEmpty(this.mDigests);
  }
  
  public String[] getEncryptionPaddings()
  {
    return ArrayUtils.cloneIfNotEmpty(this.mEncryptionPaddings);
  }
  
  public int getKeySize()
  {
    return this.mKeySize;
  }
  
  public Date getKeyValidityForConsumptionEnd()
  {
    return Utils.cloneIfNotNull(this.mKeyValidityForConsumptionEnd);
  }
  
  public Date getKeyValidityForOriginationEnd()
  {
    return Utils.cloneIfNotNull(this.mKeyValidityForOriginationEnd);
  }
  
  public Date getKeyValidityStart()
  {
    return Utils.cloneIfNotNull(this.mKeyValidityStart);
  }
  
  public String getKeystoreAlias()
  {
    return this.mKeystoreAlias;
  }
  
  public int getPurposes()
  {
    return this.mPurposes;
  }
  
  public String[] getSignaturePaddings()
  {
    return ArrayUtils.cloneIfNotEmpty(this.mSignaturePaddings);
  }
  
  public int getUid()
  {
    return this.mUid;
  }
  
  public int getUserAuthenticationValidityDurationSeconds()
  {
    return this.mUserAuthenticationValidityDurationSeconds;
  }
  
  public boolean isDigestsSpecified()
  {
    return this.mDigests != null;
  }
  
  public boolean isInvalidatedByBiometricEnrollment()
  {
    return this.mInvalidatedByBiometricEnrollment;
  }
  
  public boolean isRandomizedEncryptionRequired()
  {
    return this.mRandomizedEncryptionRequired;
  }
  
  public boolean isUniqueIdIncluded()
  {
    return this.mUniqueIdIncluded;
  }
  
  public boolean isUseSecureProcessor()
  {
    return this.mUseSecureProcessor;
  }
  
  public boolean isUserAuthenticationRequired()
  {
    return this.mUserAuthenticationRequired;
  }
  
  public boolean isUserAuthenticationValidWhileOnBody()
  {
    return this.mUserAuthenticationValidWhileOnBody;
  }
  
  public static final class Builder
  {
    private byte[] mAttestationChallenge = null;
    private String[] mBlockModes;
    private Date mCertificateNotAfter;
    private Date mCertificateNotBefore;
    private BigInteger mCertificateSerialNumber;
    private X500Principal mCertificateSubject;
    private String[] mDigests;
    private String[] mEncryptionPaddings;
    private boolean mInvalidatedByBiometricEnrollment = true;
    private int mKeySize = -1;
    private Date mKeyValidityForConsumptionEnd;
    private Date mKeyValidityForOriginationEnd;
    private Date mKeyValidityStart;
    private final String mKeystoreAlias;
    private int mPurposes;
    private boolean mRandomizedEncryptionRequired = true;
    private String[] mSignaturePaddings;
    private AlgorithmParameterSpec mSpec;
    private int mUid = -1;
    private boolean mUniqueIdIncluded = false;
    private boolean mUseSecureProcessor = false;
    private boolean mUserAuthenticationRequired;
    private boolean mUserAuthenticationValidWhileOnBody;
    private int mUserAuthenticationValidityDurationSeconds = -1;
    
    public Builder(String paramString, int paramInt)
    {
      if (paramString == null) {
        throw new NullPointerException("keystoreAlias == null");
      }
      if (paramString.isEmpty()) {
        throw new IllegalArgumentException("keystoreAlias must not be empty");
      }
      this.mKeystoreAlias = paramString;
      this.mPurposes = paramInt;
    }
    
    public KeyGenParameterSpec build()
    {
      return new KeyGenParameterSpec(this.mKeystoreAlias, this.mUid, this.mKeySize, this.mSpec, this.mCertificateSubject, this.mCertificateSerialNumber, this.mCertificateNotBefore, this.mCertificateNotAfter, this.mKeyValidityStart, this.mKeyValidityForOriginationEnd, this.mKeyValidityForConsumptionEnd, this.mPurposes, this.mDigests, this.mEncryptionPaddings, this.mSignaturePaddings, this.mBlockModes, this.mRandomizedEncryptionRequired, this.mUserAuthenticationRequired, this.mUserAuthenticationValidityDurationSeconds, this.mAttestationChallenge, this.mUniqueIdIncluded, this.mUserAuthenticationValidWhileOnBody, this.mInvalidatedByBiometricEnrollment, this.mUseSecureProcessor);
    }
    
    public Builder setAlgorithmParameterSpec(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    {
      if (paramAlgorithmParameterSpec == null) {
        throw new NullPointerException("spec == null");
      }
      this.mSpec = paramAlgorithmParameterSpec;
      return this;
    }
    
    public Builder setAttestationChallenge(byte[] paramArrayOfByte)
    {
      this.mAttestationChallenge = paramArrayOfByte;
      return this;
    }
    
    public Builder setBlockModes(String... paramVarArgs)
    {
      this.mBlockModes = ArrayUtils.cloneIfNotEmpty(paramVarArgs);
      return this;
    }
    
    public Builder setCertificateNotAfter(Date paramDate)
    {
      if (paramDate == null) {
        throw new NullPointerException("date == null");
      }
      this.mCertificateNotAfter = Utils.cloneIfNotNull(paramDate);
      return this;
    }
    
    public Builder setCertificateNotBefore(Date paramDate)
    {
      if (paramDate == null) {
        throw new NullPointerException("date == null");
      }
      this.mCertificateNotBefore = Utils.cloneIfNotNull(paramDate);
      return this;
    }
    
    public Builder setCertificateSerialNumber(BigInteger paramBigInteger)
    {
      if (paramBigInteger == null) {
        throw new NullPointerException("serialNumber == null");
      }
      this.mCertificateSerialNumber = paramBigInteger;
      return this;
    }
    
    public Builder setCertificateSubject(X500Principal paramX500Principal)
    {
      if (paramX500Principal == null) {
        throw new NullPointerException("subject == null");
      }
      this.mCertificateSubject = paramX500Principal;
      return this;
    }
    
    public Builder setDigests(String... paramVarArgs)
    {
      this.mDigests = ArrayUtils.cloneIfNotEmpty(paramVarArgs);
      return this;
    }
    
    public Builder setEncryptionPaddings(String... paramVarArgs)
    {
      this.mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(paramVarArgs);
      return this;
    }
    
    public Builder setInvalidatedByBiometricEnrollment(boolean paramBoolean)
    {
      this.mInvalidatedByBiometricEnrollment = paramBoolean;
      return this;
    }
    
    public Builder setKeySize(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("keySize < 0");
      }
      this.mKeySize = paramInt;
      return this;
    }
    
    public Builder setKeyValidityEnd(Date paramDate)
    {
      setKeyValidityForOriginationEnd(paramDate);
      setKeyValidityForConsumptionEnd(paramDate);
      return this;
    }
    
    public Builder setKeyValidityForConsumptionEnd(Date paramDate)
    {
      this.mKeyValidityForConsumptionEnd = Utils.cloneIfNotNull(paramDate);
      return this;
    }
    
    public Builder setKeyValidityForOriginationEnd(Date paramDate)
    {
      this.mKeyValidityForOriginationEnd = Utils.cloneIfNotNull(paramDate);
      return this;
    }
    
    public Builder setKeyValidityStart(Date paramDate)
    {
      this.mKeyValidityStart = Utils.cloneIfNotNull(paramDate);
      return this;
    }
    
    public Builder setRandomizedEncryptionRequired(boolean paramBoolean)
    {
      this.mRandomizedEncryptionRequired = paramBoolean;
      return this;
    }
    
    public Builder setSignaturePaddings(String... paramVarArgs)
    {
      this.mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(paramVarArgs);
      return this;
    }
    
    public Builder setUid(int paramInt)
    {
      this.mUid = paramInt;
      return this;
    }
    
    public Builder setUniqueIdIncluded(boolean paramBoolean)
    {
      this.mUniqueIdIncluded = paramBoolean;
      return this;
    }
    
    public Builder setUseSecureProcessor(boolean paramBoolean)
    {
      this.mUseSecureProcessor = paramBoolean;
      return this;
    }
    
    public Builder setUserAuthenticationRequired(boolean paramBoolean)
    {
      this.mUserAuthenticationRequired = paramBoolean;
      return this;
    }
    
    public Builder setUserAuthenticationValidWhileOnBody(boolean paramBoolean)
    {
      this.mUserAuthenticationValidWhileOnBody = paramBoolean;
      return this;
    }
    
    public Builder setUserAuthenticationValidityDurationSeconds(int paramInt)
    {
      if (paramInt < -1) {
        throw new IllegalArgumentException("seconds must be -1 or larger");
      }
      this.mUserAuthenticationValidityDurationSeconds = paramInt;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyGenParameterSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */