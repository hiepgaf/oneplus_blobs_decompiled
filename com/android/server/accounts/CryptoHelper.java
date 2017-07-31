package com.android.server.accounts;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

class CryptoHelper
{
  private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
  private static final int IV_LENGTH = 16;
  private static final String KEY_ALGORITHM = "AES";
  private static final String KEY_CIPHER = "cipher";
  private static final String KEY_IV = "iv";
  private static final String KEY_MAC = "mac";
  private static final String MAC_ALGORITHM = "HMACSHA256";
  private static final String TAG = "Account";
  private static CryptoHelper sInstance;
  private final SecretKey mEncryptionKey = KeyGenerator.getInstance("AES").generateKey();
  private final SecretKey mMacKey = KeyGenerator.getInstance("HMACSHA256").generateKey();
  
  private CryptoHelper()
    throws NoSuchAlgorithmException
  {}
  
  private static boolean constantTimeArrayEquals(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null)) {
      return paramArrayOfByte1 == paramArrayOfByte2;
    }
    if (paramArrayOfByte1.length != paramArrayOfByte2.length) {
      return false;
    }
    boolean bool2 = true;
    int i = 0;
    if (i < paramArrayOfByte2.length)
    {
      if (paramArrayOfByte1[i] == paramArrayOfByte2[i]) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        bool2 &= bool1;
        i += 1;
        break;
      }
    }
    return bool2;
  }
  
  private byte[] createMac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws GeneralSecurityException
  {
    Mac localMac = Mac.getInstance("HMACSHA256");
    localMac.init(this.mMacKey);
    localMac.update(paramArrayOfByte1);
    localMac.update(paramArrayOfByte2);
    return localMac.doFinal();
  }
  
  static CryptoHelper getInstance()
    throws NoSuchAlgorithmException
  {
    try
    {
      if (sInstance == null) {
        sInstance = new CryptoHelper();
      }
      CryptoHelper localCryptoHelper = sInstance;
      return localCryptoHelper;
    }
    finally {}
  }
  
  private boolean verifyMac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    throws GeneralSecurityException
  {
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte1.length == 0)) {}
    while ((paramArrayOfByte3 == null) || (paramArrayOfByte3.length == 0))
    {
      if (Log.isLoggable("Account", 2)) {
        Log.v("Account", "Cipher or MAC is empty!");
      }
      return false;
    }
    return constantTimeArrayEquals(paramArrayOfByte3, createMac(paramArrayOfByte1, paramArrayOfByte2));
  }
  
  Bundle decryptBundle(Bundle paramBundle)
    throws GeneralSecurityException
  {
    Preconditions.checkNotNull(paramBundle, "Cannot decrypt null bundle.");
    Object localObject2 = paramBundle.getByteArray("iv");
    Object localObject1 = paramBundle.getByteArray("cipher");
    if (!verifyMac((byte[])localObject1, (byte[])localObject2, paramBundle.getByteArray("mac")))
    {
      Log.w("Account", "Escrow mac mismatched!");
      return null;
    }
    paramBundle = new IvParameterSpec((byte[])localObject2);
    localObject2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
    ((Cipher)localObject2).init(2, this.mEncryptionKey, paramBundle);
    localObject1 = ((Cipher)localObject2).doFinal((byte[])localObject1);
    paramBundle = Parcel.obtain();
    paramBundle.unmarshall((byte[])localObject1, 0, localObject1.length);
    paramBundle.setDataPosition(0);
    localObject1 = new Bundle();
    ((Bundle)localObject1).readFromParcel(paramBundle);
    paramBundle.recycle();
    return (Bundle)localObject1;
  }
  
  Bundle encryptBundle(Bundle paramBundle)
    throws GeneralSecurityException
  {
    Preconditions.checkNotNull(paramBundle, "Cannot encrypt null bundle.");
    Object localObject = Parcel.obtain();
    paramBundle.writeToParcel((Parcel)localObject, 0);
    paramBundle = ((Parcel)localObject).marshall();
    ((Parcel)localObject).recycle();
    localObject = Cipher.getInstance("AES/CBC/PKCS5Padding");
    ((Cipher)localObject).init(1, this.mEncryptionKey);
    paramBundle = ((Cipher)localObject).doFinal(paramBundle);
    localObject = ((Cipher)localObject).getIV();
    byte[] arrayOfByte = createMac(paramBundle, (byte[])localObject);
    Bundle localBundle = new Bundle();
    localBundle.putByteArray("cipher", paramBundle);
    localBundle.putByteArray("mac", arrayOfByte);
    localBundle.putByteArray("iv", (byte[])localObject);
    return localBundle;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accounts/CryptoHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */