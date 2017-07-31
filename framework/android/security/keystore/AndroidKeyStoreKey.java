package android.security.keystore;

import java.security.Key;

public class AndroidKeyStoreKey
  implements Key
{
  private final String mAlgorithm;
  private final String mAlias;
  private final int mUid;
  
  public AndroidKeyStoreKey(String paramString1, int paramInt, String paramString2)
  {
    this.mAlias = paramString1;
    this.mUid = paramInt;
    this.mAlgorithm = paramString2;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (AndroidKeyStoreKey)paramObject;
    if (this.mAlgorithm == null)
    {
      if (((AndroidKeyStoreKey)paramObject).mAlgorithm != null) {
        return false;
      }
    }
    else if (!this.mAlgorithm.equals(((AndroidKeyStoreKey)paramObject).mAlgorithm)) {
      return false;
    }
    if (this.mAlias == null)
    {
      if (((AndroidKeyStoreKey)paramObject).mAlias != null) {
        return false;
      }
    }
    else if (!this.mAlias.equals(((AndroidKeyStoreKey)paramObject).mAlias)) {
      return false;
    }
    return this.mUid == ((AndroidKeyStoreKey)paramObject).mUid;
  }
  
  public String getAlgorithm()
  {
    return this.mAlgorithm;
  }
  
  String getAlias()
  {
    return this.mAlias;
  }
  
  public byte[] getEncoded()
  {
    return null;
  }
  
  public String getFormat()
  {
    return null;
  }
  
  int getUid()
  {
    return this.mUid;
  }
  
  public int hashCode()
  {
    int j = 0;
    int i;
    if (this.mAlgorithm == null)
    {
      i = 0;
      if (this.mAlias != null) {
        break label47;
      }
    }
    for (;;)
    {
      return ((i + 31) * 31 + j) * 31 + this.mUid;
      i = this.mAlgorithm.hashCode();
      break;
      label47:
      j = this.mAlias.hashCode();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */