package android.security.keystore;

import java.security.KeyStore.ProtectionParameter;
import java.util.Date;

public final class KeyProtection
  implements KeyStore.ProtectionParameter
{
  private final String[] mBlockModes;
  private final String[] mDigests;
  private final String[] mEncryptionPaddings;
  private final boolean mInvalidatedByBiometricEnrollment;
  private final Date mKeyValidityForConsumptionEnd;
  private final Date mKeyValidityForOriginationEnd;
  private final Date mKeyValidityStart;
  private final int mPurposes;
  private final boolean mRandomizedEncryptionRequired;
  private final String[] mSignaturePaddings;
  private final boolean mUserAuthenticationRequired;
  private final boolean mUserAuthenticationValidWhileOnBody;
  private final int mUserAuthenticationValidityDurationSeconds;
  
  private KeyProtection(Date paramDate1, Date paramDate2, Date paramDate3, int paramInt1, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, String[] paramArrayOfString4, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, boolean paramBoolean3, boolean paramBoolean4)
  {
    this.mKeyValidityStart = Utils.cloneIfNotNull(paramDate1);
    this.mKeyValidityForOriginationEnd = Utils.cloneIfNotNull(paramDate2);
    this.mKeyValidityForConsumptionEnd = Utils.cloneIfNotNull(paramDate3);
    this.mPurposes = paramInt1;
    this.mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString1));
    this.mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString2));
    this.mDigests = ArrayUtils.cloneIfNotEmpty(paramArrayOfString3);
    this.mBlockModes = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString4));
    this.mRandomizedEncryptionRequired = paramBoolean1;
    this.mUserAuthenticationRequired = paramBoolean2;
    this.mUserAuthenticationValidityDurationSeconds = paramInt2;
    this.mUserAuthenticationValidWhileOnBody = paramBoolean3;
    this.mInvalidatedByBiometricEnrollment = paramBoolean4;
  }
  
  public String[] getBlockModes()
  {
    return ArrayUtils.cloneIfNotEmpty(this.mBlockModes);
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
  
  public int getPurposes()
  {
    return this.mPurposes;
  }
  
  public String[] getSignaturePaddings()
  {
    return ArrayUtils.cloneIfNotEmpty(this.mSignaturePaddings);
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
    private String[] mBlockModes;
    private String[] mDigests;
    private String[] mEncryptionPaddings;
    private boolean mInvalidatedByBiometricEnrollment = true;
    private Date mKeyValidityForConsumptionEnd;
    private Date mKeyValidityForOriginationEnd;
    private Date mKeyValidityStart;
    private int mPurposes;
    private boolean mRandomizedEncryptionRequired = true;
    private String[] mSignaturePaddings;
    private boolean mUserAuthenticationRequired;
    private boolean mUserAuthenticationValidWhileOnBody;
    private int mUserAuthenticationValidityDurationSeconds = -1;
    
    public Builder(int paramInt)
    {
      this.mPurposes = paramInt;
    }
    
    public KeyProtection build()
    {
      return new KeyProtection(this.mKeyValidityStart, this.mKeyValidityForOriginationEnd, this.mKeyValidityForConsumptionEnd, this.mPurposes, this.mEncryptionPaddings, this.mSignaturePaddings, this.mDigests, this.mBlockModes, this.mRandomizedEncryptionRequired, this.mUserAuthenticationRequired, this.mUserAuthenticationValidityDurationSeconds, this.mUserAuthenticationValidWhileOnBody, this.mInvalidatedByBiometricEnrollment, null);
    }
    
    public Builder setBlockModes(String... paramVarArgs)
    {
      this.mBlockModes = ArrayUtils.cloneIfNotEmpty(paramVarArgs);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyProtection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */