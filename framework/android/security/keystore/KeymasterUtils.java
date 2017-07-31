package android.security.keystore;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.security.GateKeeper;
import android.security.KeyStore;
import android.security.keymaster.KeymasterArguments;
import com.android.internal.util.ArrayUtils;
import java.security.ProviderException;

public abstract class KeymasterUtils
{
  public static void addMinMacLengthAuthorizationIfNecessary(KeymasterArguments paramKeymasterArguments, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    switch (paramInt)
    {
    default: 
    case 32: 
      do
      {
        return;
      } while (!ArrayUtils.contains(paramArrayOfInt1, 32));
      paramKeymasterArguments.addUnsignedInt(805306376, 96L);
      return;
    }
    if (paramArrayOfInt2.length != 1) {
      throw new ProviderException("Unsupported number of authorized digests for HMAC key: " + paramArrayOfInt2.length + ". Exactly one digest must be authorized");
    }
    paramInt = paramArrayOfInt2[0];
    int i = getDigestOutputSizeBits(paramInt);
    if (i == -1) {
      throw new ProviderException("HMAC key authorized for unsupported digest: " + KeyProperties.Digest.fromKeymaster(paramInt));
    }
    paramKeymasterArguments.addUnsignedInt(805306376, i);
  }
  
  public static void addUserAuthArgs(KeymasterArguments paramKeymasterArguments, boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (!paramBoolean1)
    {
      paramKeymasterArguments.addBoolean(1879048695);
      return;
    }
    if (paramInt == -1)
    {
      FingerprintManager localFingerprintManager = (FingerprintManager)KeyStore.getApplicationContext().getSystemService(FingerprintManager.class);
      if (localFingerprintManager != null) {}
      for (long l = localFingerprintManager.getAuthenticatorId(); l == 0L; l = 0L) {
        throw new IllegalStateException("At least one fingerprint must be enrolled to create keys requiring user authentication for every use");
      }
      if (paramBoolean3) {}
      for (;;)
      {
        paramKeymasterArguments.addUnsignedLong(-1610612234, KeymasterArguments.toUint64(l));
        paramKeymasterArguments.addEnum(268435960, 2);
        if (!paramBoolean2) {
          break;
        }
        throw new ProviderException("Key validity extension while device is on-body is not supported for keys requiring fingerprint authentication");
        l = getRootSid();
      }
    }
    paramKeymasterArguments.addUnsignedLong(-1610612234, KeymasterArguments.toUint64(getRootSid()));
    paramKeymasterArguments.addEnum(268435960, 3);
    paramKeymasterArguments.addUnsignedInt(805306873, paramInt);
    if (paramBoolean2) {
      paramKeymasterArguments.addBoolean(1879048698);
    }
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
  
  private static long getRootSid()
  {
    long l = GateKeeper.getSecureUserId();
    if (l == 0L) {
      throw new IllegalStateException("Secure lock screen must be enabled to create keys requiring user authentication");
    }
    return l;
  }
  
  public static boolean isKeymasterBlockModeIndCpaCompatibleWithSymmetricCrypto(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unsupported block mode: " + paramInt);
    case 1: 
      return false;
    }
    return true;
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
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeymasterUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */