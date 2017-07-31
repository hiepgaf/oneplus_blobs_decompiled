package android.security;

import android.os.Environment;
import android.os.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import libcore.io.IoUtils;

public class SystemKeyStore
{
  private static final String KEY_FILE_EXTENSION = ".sks";
  private static final String SYSTEM_KEYSTORE_DIRECTORY = "misc/systemkeys";
  private static SystemKeyStore mInstance = new SystemKeyStore();
  
  public static SystemKeyStore getInstance()
  {
    return mInstance;
  }
  
  private File getKeyFile(String paramString)
  {
    return new File(new File(Environment.getDataDirectory(), "misc/systemkeys"), paramString + ".sks");
  }
  
  public static String toHexString(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    int i = paramArrayOfByte.length;
    StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length * 2);
    i = 0;
    while (i < paramArrayOfByte.length)
    {
      String str2 = Integer.toString(paramArrayOfByte[i] & 0xFF, 16);
      String str1 = str2;
      if (str2.length() == 1) {
        str1 = "0" + str2;
      }
      localStringBuilder.append(str1);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public void deleteKey(String paramString)
  {
    paramString = getKeyFile(paramString);
    if (!paramString.exists()) {
      throw new IllegalArgumentException();
    }
    paramString.delete();
  }
  
  public byte[] generateNewKey(int paramInt, String paramString1, String paramString2)
    throws NoSuchAlgorithmException
  {
    paramString2 = getKeyFile(paramString2);
    if (paramString2.exists()) {
      throw new IllegalArgumentException();
    }
    paramString1 = KeyGenerator.getInstance(paramString1);
    paramString1.init(paramInt, SecureRandom.getInstance("SHA1PRNG"));
    paramString1 = paramString1.generateKey().getEncoded();
    try
    {
      if (!paramString2.createNewFile()) {
        throw new IllegalArgumentException();
      }
      FileOutputStream localFileOutputStream = new FileOutputStream(paramString2);
      localFileOutputStream.write(paramString1);
      localFileOutputStream.flush();
      FileUtils.sync(localFileOutputStream);
      localFileOutputStream.close();
      FileUtils.setPermissions(paramString2.getName(), 384, -1, -1);
      return paramString1;
    }
    catch (IOException paramString1) {}
    return null;
  }
  
  public String generateNewKeyHexString(int paramInt, String paramString1, String paramString2)
    throws NoSuchAlgorithmException
  {
    return toHexString(generateNewKey(paramInt, paramString1, paramString2));
  }
  
  public byte[] retrieveKey(String paramString)
    throws IOException
  {
    paramString = getKeyFile(paramString);
    if (!paramString.exists()) {
      return null;
    }
    return IoUtils.readFileAsByteArray(paramString.toString());
  }
  
  public String retrieveKeyHexString(String paramString)
    throws IOException
  {
    return toHexString(retrieveKey(paramString));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/SystemKeyStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */