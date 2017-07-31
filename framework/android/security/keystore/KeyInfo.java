package android.security.keystore;

import java.security.spec.KeySpec;
import java.util.Date;

public class KeyInfo
  implements KeySpec
{
  private final String[] mBlockModes;
  private final String[] mDigests;
  private final String[] mEncryptionPaddings;
  private final boolean mInsideSecureHardware;
  private final boolean mInvalidatedByBiometricEnrollment;
  private final int mKeySize;
  private final Date mKeyValidityForConsumptionEnd;
  private final Date mKeyValidityForOriginationEnd;
  private final Date mKeyValidityStart;
  private final String mKeystoreAlias;
  private final int mOrigin;
  private final int mPurposes;
  private final String[] mSignaturePaddings;
  private final boolean mUserAuthenticationRequired;
  private final boolean mUserAuthenticationRequirementEnforcedBySecureHardware;
  private final boolean mUserAuthenticationValidWhileOnBody;
  private final int mUserAuthenticationValidityDurationSeconds;
  
  public KeyInfo(String paramString, boolean paramBoolean1, int paramInt1, int paramInt2, Date paramDate1, Date paramDate2, Date paramDate3, int paramInt3, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, String[] paramArrayOfString4, boolean paramBoolean2, int paramInt4, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    this.mKeystoreAlias = paramString;
    this.mInsideSecureHardware = paramBoolean1;
    this.mOrigin = paramInt1;
    this.mKeySize = paramInt2;
    this.mKeyValidityStart = Utils.cloneIfNotNull(paramDate1);
    this.mKeyValidityForOriginationEnd = Utils.cloneIfNotNull(paramDate2);
    this.mKeyValidityForConsumptionEnd = Utils.cloneIfNotNull(paramDate3);
    this.mPurposes = paramInt3;
    this.mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString1));
    this.mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString2));
    this.mDigests = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString3));
    this.mBlockModes = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(paramArrayOfString4));
    this.mUserAuthenticationRequired = paramBoolean2;
    this.mUserAuthenticationValidityDurationSeconds = paramInt4;
    this.mUserAuthenticationRequirementEnforcedBySecureHardware = paramBoolean3;
    this.mUserAuthenticationValidWhileOnBody = paramBoolean4;
    this.mInvalidatedByBiometricEnrollment = paramBoolean5;
  }
  
  public String[] getBlockModes()
  {
    return ArrayUtils.cloneIfNotEmpty(this.mBlockModes);
  }
  
  public String[] getDigests()
  {
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
  
  public int getOrigin()
  {
    return this.mOrigin;
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
  
  public boolean isInsideSecureHardware()
  {
    return this.mInsideSecureHardware;
  }
  
  public boolean isInvalidatedByBiometricEnrollment()
  {
    return this.mInvalidatedByBiometricEnrollment;
  }
  
  public boolean isUserAuthenticationRequired()
  {
    return this.mUserAuthenticationRequired;
  }
  
  public boolean isUserAuthenticationRequirementEnforcedBySecureHardware()
  {
    return this.mUserAuthenticationRequirementEnforcedBySecureHardware;
  }
  
  public boolean isUserAuthenticationValidWhileOnBody()
  {
    return this.mUserAuthenticationValidWhileOnBody;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */