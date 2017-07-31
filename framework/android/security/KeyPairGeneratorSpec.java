package android.security;

import android.content.Context;
import android.security.keystore.KeyProperties.KeyAlgorithm;
import android.text.TextUtils;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;
import javax.security.auth.x500.X500Principal;

@Deprecated
public final class KeyPairGeneratorSpec
  implements AlgorithmParameterSpec
{
  private final Context mContext;
  private final Date mEndDate;
  private final int mFlags;
  private final int mKeySize;
  private final String mKeyType;
  private final String mKeystoreAlias;
  private final BigInteger mSerialNumber;
  private final AlgorithmParameterSpec mSpec;
  private final Date mStartDate;
  private final X500Principal mSubjectDN;
  
  public KeyPairGeneratorSpec(Context paramContext, String paramString1, String paramString2, int paramInt1, AlgorithmParameterSpec paramAlgorithmParameterSpec, X500Principal paramX500Principal, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, int paramInt2)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("context == null");
    }
    if (TextUtils.isEmpty(paramString1)) {
      throw new IllegalArgumentException("keyStoreAlias must not be empty");
    }
    if (paramX500Principal == null) {
      throw new IllegalArgumentException("subjectDN == null");
    }
    if (paramBigInteger == null) {
      throw new IllegalArgumentException("serialNumber == null");
    }
    if (paramDate1 == null) {
      throw new IllegalArgumentException("startDate == null");
    }
    if (paramDate2 == null) {
      throw new IllegalArgumentException("endDate == null");
    }
    if (paramDate2.before(paramDate1)) {
      throw new IllegalArgumentException("endDate < startDate");
    }
    if (paramDate2.before(paramDate1)) {
      throw new IllegalArgumentException("endDate < startDate");
    }
    this.mContext = paramContext;
    this.mKeystoreAlias = paramString1;
    this.mKeyType = paramString2;
    this.mKeySize = paramInt1;
    this.mSpec = paramAlgorithmParameterSpec;
    this.mSubjectDN = paramX500Principal;
    this.mSerialNumber = paramBigInteger;
    this.mStartDate = paramDate1;
    this.mEndDate = paramDate2;
    this.mFlags = paramInt2;
  }
  
  public AlgorithmParameterSpec getAlgorithmParameterSpec()
  {
    return this.mSpec;
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public Date getEndDate()
  {
    return this.mEndDate;
  }
  
  public int getFlags()
  {
    return this.mFlags;
  }
  
  public int getKeySize()
  {
    return this.mKeySize;
  }
  
  public String getKeyType()
  {
    return this.mKeyType;
  }
  
  public String getKeystoreAlias()
  {
    return this.mKeystoreAlias;
  }
  
  public BigInteger getSerialNumber()
  {
    return this.mSerialNumber;
  }
  
  public Date getStartDate()
  {
    return this.mStartDate;
  }
  
  public X500Principal getSubjectDN()
  {
    return this.mSubjectDN;
  }
  
  public boolean isEncryptionRequired()
  {
    boolean bool = false;
    if ((this.mFlags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  @Deprecated
  public static final class Builder
  {
    private final Context mContext;
    private Date mEndDate;
    private int mFlags;
    private int mKeySize = -1;
    private String mKeyType;
    private String mKeystoreAlias;
    private BigInteger mSerialNumber;
    private AlgorithmParameterSpec mSpec;
    private Date mStartDate;
    private X500Principal mSubjectDN;
    
    public Builder(Context paramContext)
    {
      if (paramContext == null) {
        throw new NullPointerException("context == null");
      }
      this.mContext = paramContext;
    }
    
    public KeyPairGeneratorSpec build()
    {
      return new KeyPairGeneratorSpec(this.mContext, this.mKeystoreAlias, this.mKeyType, this.mKeySize, this.mSpec, this.mSubjectDN, this.mSerialNumber, this.mStartDate, this.mEndDate, this.mFlags);
    }
    
    public Builder setAlgorithmParameterSpec(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    {
      if (paramAlgorithmParameterSpec == null) {
        throw new NullPointerException("spec == null");
      }
      this.mSpec = paramAlgorithmParameterSpec;
      return this;
    }
    
    public Builder setAlias(String paramString)
    {
      if (paramString == null) {
        throw new NullPointerException("alias == null");
      }
      this.mKeystoreAlias = paramString;
      return this;
    }
    
    public Builder setEncryptionRequired()
    {
      this.mFlags |= 0x1;
      return this;
    }
    
    public Builder setEndDate(Date paramDate)
    {
      if (paramDate == null) {
        throw new NullPointerException("endDate == null");
      }
      this.mEndDate = paramDate;
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
    
    public Builder setKeyType(String paramString)
      throws NoSuchAlgorithmException
    {
      if (paramString == null) {
        throw new NullPointerException("keyType == null");
      }
      try
      {
        KeyProperties.KeyAlgorithm.toKeymasterAsymmetricKeyAlgorithm(paramString);
        this.mKeyType = paramString;
        return this;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new NoSuchAlgorithmException("Unsupported key type: " + paramString);
      }
    }
    
    public Builder setSerialNumber(BigInteger paramBigInteger)
    {
      if (paramBigInteger == null) {
        throw new NullPointerException("serialNumber == null");
      }
      this.mSerialNumber = paramBigInteger;
      return this;
    }
    
    public Builder setStartDate(Date paramDate)
    {
      if (paramDate == null) {
        throw new NullPointerException("startDate == null");
      }
      this.mStartDate = paramDate;
      return this;
    }
    
    public Builder setSubject(X500Principal paramX500Principal)
    {
      if (paramX500Principal == null) {
        throw new NullPointerException("subject == null");
      }
      this.mSubjectDN = paramX500Principal;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/KeyPairGeneratorSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */