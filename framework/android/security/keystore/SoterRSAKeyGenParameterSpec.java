package android.security.keystore;

import java.math.BigInteger;
import java.security.spec.RSAKeyGenParameterSpec;

public class SoterRSAKeyGenParameterSpec
  extends RSAKeyGenParameterSpec
{
  private boolean isAutoAddCounterWhenGetPublicKey = false;
  private boolean isAutoSignedWithAttkWhenGetPublicKey = false;
  private boolean isAutoSignedWithCommonkWhenGetPublicKey = false;
  private boolean isForSoter = false;
  private boolean isNeedUseNextAttk = false;
  private boolean isSecmsgFidCounterSignedWhenSign = false;
  private String mAutoSignedKeyNameWhenGetPublicKey = "";
  
  public SoterRSAKeyGenParameterSpec(int paramInt, BigInteger paramBigInteger, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    super(paramInt, paramBigInteger);
    this.isForSoter = paramBoolean1;
    this.isAutoSignedWithAttkWhenGetPublicKey = paramBoolean2;
    this.isAutoSignedWithCommonkWhenGetPublicKey = paramBoolean3;
    this.mAutoSignedKeyNameWhenGetPublicKey = paramString;
    this.isSecmsgFidCounterSignedWhenSign = paramBoolean4;
    this.isAutoAddCounterWhenGetPublicKey = paramBoolean5;
    this.isNeedUseNextAttk = paramBoolean6;
  }
  
  public SoterRSAKeyGenParameterSpec(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    this(2048, RSAKeyGenParameterSpec.F4, paramBoolean1, paramBoolean2, paramBoolean3, paramString, paramBoolean4, paramBoolean5, paramBoolean6);
  }
  
  public String getAutoSignedKeyNameWhenGetPublicKey()
  {
    return this.mAutoSignedKeyNameWhenGetPublicKey;
  }
  
  public boolean isAutoAddCounterWhenGetPublicKey()
  {
    return this.isAutoAddCounterWhenGetPublicKey;
  }
  
  public boolean isAutoSignedWithAttkWhenGetPublicKey()
  {
    return this.isAutoSignedWithAttkWhenGetPublicKey;
  }
  
  public boolean isAutoSignedWithCommonkWhenGetPublicKey()
  {
    return this.isAutoSignedWithCommonkWhenGetPublicKey;
  }
  
  public boolean isForSoter()
  {
    return this.isForSoter;
  }
  
  public boolean isNeedUseNextAttk()
  {
    return this.isNeedUseNextAttk;
  }
  
  public boolean isSecmsgFidCounterSignedWhenSign()
  {
    return this.isSecmsgFidCounterSignedWhenSign;
  }
  
  public void setAutoSignedKeyNameWhenGetPublicKey(String paramString)
  {
    this.mAutoSignedKeyNameWhenGetPublicKey = paramString;
  }
  
  public void setIsAutoAddCounterWhenGetPublicKey(boolean paramBoolean)
  {
    this.isAutoAddCounterWhenGetPublicKey = paramBoolean;
  }
  
  public void setIsAutoSignedWithAttkWhenGetPublicKey(boolean paramBoolean)
  {
    this.isAutoSignedWithAttkWhenGetPublicKey = paramBoolean;
  }
  
  public void setIsAutoSignedWithCommonkWhenGetPublicKey(boolean paramBoolean)
  {
    this.isAutoSignedWithCommonkWhenGetPublicKey = paramBoolean;
  }
  
  public void setIsForSoter(boolean paramBoolean)
  {
    this.isForSoter = paramBoolean;
  }
  
  public void setIsNeedUseNextAttk(boolean paramBoolean)
  {
    this.isNeedUseNextAttk = paramBoolean;
  }
  
  public void setIsSecmsgFidCounterSignedWhenSign(boolean paramBoolean)
  {
    this.isSecmsgFidCounterSignedWhenSign = paramBoolean;
  }
  
  public String toString()
  {
    return "SoterRSAKeyGenParameterSpec{isForSoter=" + this.isForSoter + ", isAutoSignedWithAttkWhenGetPublicKey=" + this.isAutoSignedWithAttkWhenGetPublicKey + ", isAutoSignedWithCommonkWhenGetPublicKey=" + this.isAutoSignedWithCommonkWhenGetPublicKey + ", mAutoSignedKeyNameWhenGetPublicKey='" + this.mAutoSignedKeyNameWhenGetPublicKey + '\'' + ", isSecmsgFidCounterSignedWhenSign=" + this.isSecmsgFidCounterSignedWhenSign + ", isAutoAddCounterWhenGetPublicKey=" + this.isAutoAddCounterWhenGetPublicKey + ", isNeedUseNextAttk=" + this.isNeedUseNextAttk + '}';
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/SoterRSAKeyGenParameterSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */