package android.security;

import android.content.Context;
import java.security.KeyStore.ProtectionParameter;

@Deprecated
public final class KeyStoreParameter
  implements KeyStore.ProtectionParameter
{
  private final int mFlags;
  
  private KeyStoreParameter(int paramInt)
  {
    this.mFlags = paramInt;
  }
  
  public int getFlags()
  {
    return this.mFlags;
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
    private int mFlags;
    
    public Builder(Context paramContext)
    {
      if (paramContext == null) {
        throw new NullPointerException("context == null");
      }
    }
    
    public KeyStoreParameter build()
    {
      return new KeyStoreParameter(this.mFlags, null);
    }
    
    public Builder setEncryptionRequired(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mFlags |= 0x1;
        return this;
      }
      this.mFlags &= 0xFFFFFFFE;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/KeyStoreParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */