package android.security.keystore;

import android.util.Base64;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class SoterUtil
{
  public static final String JSON_KEY_PUBLIC = "pub_key";
  private static final String PARAM_NEED_AUTO_ADD_COUNTER_WHEN_GET_PUBLIC_KEY = "addcounter";
  private static final String PARAM_NEED_AUTO_ADD_SECMSG_FID_COUNTER_WHEN_SIGN = "secmsg_and_counter_signed_when_sign";
  private static final String PARAM_NEED_AUTO_SIGNED_WITH_ATTK_WHEN_GET_PUBLIC_KEY = "auto_signed_when_get_pubkey_attk";
  private static final String PARAM_NEED_AUTO_SIGNED_WITH_COMMON_KEY_WHEN_GET_PUBLIC_KEY = "auto_signed_when_get_pubkey";
  private static final String PARAM_NEED_NEXT_ATTK = "next_attk";
  private static final int RAW_LENGTH_PREFIX = 4;
  public static final String TAG = "Soter.Util";
  
  private static boolean contains(String paramString, String[] paramArrayOfString)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {}
    while (isNullOrNil(paramString))
    {
      Log.e("Soter.Util", "hy: param error");
      throw new IllegalArgumentException("param error");
    }
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(paramArrayOfString[i])) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static String containsPrefix(String paramString, String[] paramArrayOfString)
  {
    int i = 0;
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {}
    while (isNullOrNil(paramString))
    {
      Log.e("Soter.Util", "hy: param error");
      throw new IllegalArgumentException("param error");
    }
    int j = paramArrayOfString.length;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      if ((!isNullOrNil(str)) && (str.startsWith(paramString))) {
        return str;
      }
      i += 1;
    }
    return null;
  }
  
  public static SoterRSAKeyGenParameterSpec convertKeyNameToParameterSpec(String paramString)
  {
    if (isNullOrNil(paramString))
    {
      Log.e("Soter.Util", "hy: null or nil when convert key name to parameter");
      return null;
    }
    String[] arrayOfString = paramString.split("\\.");
    if ((arrayOfString == null) || (arrayOfString.length <= 1))
    {
      Log.w("Soter.Util", "hy: pure alias, no parameter");
      return null;
    }
    boolean bool5 = false;
    boolean bool7 = false;
    String str1 = "";
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool6 = false;
    boolean bool1;
    boolean bool2;
    if (contains("auto_signed_when_get_pubkey_attk", arrayOfString))
    {
      bool1 = true;
      paramString = str1;
      bool2 = bool7;
    }
    for (;;)
    {
      if (contains("secmsg_and_counter_signed_when_sign", arrayOfString)) {
        bool3 = true;
      }
      bool5 = bool6;
      if (contains("addcounter", arrayOfString))
      {
        bool7 = true;
        bool4 = bool7;
        bool5 = bool6;
        if (contains("next_attk", arrayOfString))
        {
          bool5 = true;
          bool4 = bool7;
        }
      }
      paramString = new SoterRSAKeyGenParameterSpec(true, bool1, bool2, paramString, bool3, bool4, bool5);
      Log.i("Soter.Util", "hy: spec: " + paramString.toString());
      return paramString;
      String str2 = containsPrefix("auto_signed_when_get_pubkey", arrayOfString);
      bool1 = bool5;
      bool2 = bool7;
      paramString = str1;
      if (!isNullOrNil(str2))
      {
        str2 = retrieveKeyNameFromExpr(str2);
        bool1 = bool5;
        bool2 = bool7;
        paramString = str1;
        if (!isNullOrNil(str2))
        {
          bool2 = true;
          paramString = str2;
          bool1 = bool5;
        }
      }
    }
  }
  
  public static byte[] getDataFromRaw(byte[] paramArrayOfByte, String paramString)
    throws JSONException
  {
    if (isNullOrNil(paramString))
    {
      Log.e("Soter", "hy: json keyname error");
      return null;
    }
    if (paramArrayOfByte == null)
    {
      Log.e("Soter", "hy: json origin null");
      return null;
    }
    paramArrayOfByte = retriveJsonFromExportedData(paramArrayOfByte);
    if ((paramArrayOfByte != null) && (paramArrayOfByte.has(paramString)))
    {
      paramArrayOfByte = paramArrayOfByte.getString(paramString);
      Log.d("Soter", "base64 encoded public key: " + paramArrayOfByte);
      paramArrayOfByte = paramArrayOfByte.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\\n", "");
      Log.d("Soter", "pure base64 encoded public key: " + paramArrayOfByte);
      return Base64.decode(paramArrayOfByte, 0);
    }
    return null;
  }
  
  public static String getPureKeyAliasFromKeyName(String paramString)
  {
    Log.i("Soter.Util", "hy: retrieving pure name from: " + paramString);
    if (isNullOrNil(paramString))
    {
      Log.e("Soter.Util", "hy: null or nil when get pure key alias");
      return null;
    }
    String[] arrayOfString = paramString.split("\\.");
    if ((arrayOfString == null) || (arrayOfString.length <= 1))
    {
      Log.d("Soter.Util", "hy: pure alias");
      return paramString;
    }
    return arrayOfString[0];
  }
  
  public static boolean isNullOrNil(String paramString)
  {
    if (paramString != null) {
      return paramString.equals("");
    }
    return true;
  }
  
  private static String retrieveKeyNameFromExpr(String paramString)
  {
    if (!isNullOrNil(paramString))
    {
      int i = paramString.indexOf("(");
      int j = paramString.indexOf(")");
      if ((i >= 0) && (j > i)) {
        return paramString.substring(i + 1, j);
      }
      Log.e("Soter.Util", "hy: no key name");
      return null;
    }
    Log.e("Soter.Util", "hy: expr is null");
    return null;
  }
  
  private static JSONObject retriveJsonFromExportedData(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
    {
      Log.e("Soter", "raw data is null");
      return null;
    }
    if (paramArrayOfByte.length < 4) {
      Log.e("Soter", "raw data length smaller than 4");
    }
    byte[] arrayOfByte = new byte[4];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, 4);
    int i = toInt(arrayOfByte);
    Log.d("Soter", "parsed raw length: " + i);
    arrayOfByte = new byte[i];
    if (paramArrayOfByte.length <= i + 4)
    {
      Log.e("Soter", "length not correct 2");
      return null;
    }
    System.arraycopy(paramArrayOfByte, 4, arrayOfByte, 0, i);
    paramArrayOfByte = new String(arrayOfByte);
    Log.d("Soter", "to convert json: " + paramArrayOfByte);
    try
    {
      paramArrayOfByte = new JSONObject(paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (JSONException paramArrayOfByte)
    {
      Log.e("Soter", "hy: can not convert to json");
    }
    return null;
  }
  
  public static int toInt(byte[] paramArrayOfByte)
  {
    int j = 0;
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      j += ((paramArrayOfByte[i] & 0xFF) << i * 8);
      i += 1;
    }
    return j;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/SoterUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */