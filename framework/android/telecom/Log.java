package android.telecom;

import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import java.security.MessageDigest;
import java.util.IllegalFormatException;
import java.util.Locale;

public final class Log
{
  public static final boolean DEBUG = isLoggable(3);
  public static final boolean ERROR = isLoggable(6);
  public static final boolean FORCE_LOGGING = false;
  public static final boolean INFO = isLoggable(4);
  private static final String TAG = "TelecomFramework";
  public static final boolean VERBOSE = isLoggable(2);
  public static final boolean WARN = isLoggable(5);
  private static MessageDigest sMessageDigest;
  private static final Object sMessageDigestLock = new Object();
  
  private static String buildMessage(String paramString1, String paramString2, Object... paramVarArgs)
  {
    if (paramVarArgs != null) {}
    for (;;)
    {
      try
      {
        int i = paramVarArgs.length;
        if (i != 0) {
          continue;
        }
      }
      catch (IllegalFormatException localIllegalFormatException)
      {
        String str;
        wtf("Log", localIllegalFormatException, "IllegalFormatException: formatString='%s' numArgs=%d", new Object[] { paramString2, Integer.valueOf(paramVarArgs.length) });
        paramString2 = paramString2 + " (An error occurred while formatting the message.)";
        continue;
      }
      return String.format(Locale.US, "%s: %s", new Object[] { paramString1, paramString2 });
      str = String.format(Locale.US, paramString2, paramVarArgs);
      paramString2 = str;
    }
  }
  
  public static void d(Object paramObject, String paramString, Object... paramVarArgs)
  {
    if (DEBUG) {
      android.util.Log.d("TelecomFramework", buildMessage(getPrefixFromObject(paramObject), paramString, paramVarArgs));
    }
  }
  
  public static void d(String paramString1, String paramString2, Object... paramVarArgs)
  {
    if (DEBUG) {
      android.util.Log.d("TelecomFramework", buildMessage(paramString1, paramString2, paramVarArgs));
    }
  }
  
  public static void e(Object paramObject, Throwable paramThrowable, String paramString, Object... paramVarArgs)
  {
    if (ERROR) {
      android.util.Log.e("TelecomFramework", buildMessage(getPrefixFromObject(paramObject), paramString, paramVarArgs), paramThrowable);
    }
  }
  
  public static void e(String paramString1, Throwable paramThrowable, String paramString2, Object... paramVarArgs)
  {
    if (ERROR) {
      android.util.Log.e("TelecomFramework", buildMessage(paramString1, paramString2, paramVarArgs), paramThrowable);
    }
  }
  
  private static String encodeHex(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 2);
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i] & 0xFF;
      if (j < 16) {
        localStringBuffer.append("0");
      }
      localStringBuffer.append(Integer.toString(j, 16));
      i += 1;
    }
    return localStringBuffer.toString();
  }
  
  private static String getPrefixFromObject(Object paramObject)
  {
    if (paramObject == null) {
      return "<null>";
    }
    return paramObject.getClass().getSimpleName();
  }
  
  public static void i(Object paramObject, String paramString, Object... paramVarArgs)
  {
    if (INFO) {
      android.util.Log.i("TelecomFramework", buildMessage(getPrefixFromObject(paramObject), paramString, paramVarArgs));
    }
  }
  
  public static void i(String paramString1, String paramString2, Object... paramVarArgs)
  {
    if (INFO) {
      android.util.Log.i("TelecomFramework", buildMessage(paramString1, paramString2, paramVarArgs));
    }
  }
  
  public static void initMd5Sum()
  {
    new AsyncTask()
    {
      /* Error */
      public Void doInBackground(Void... paramAnonymousVarArgs)
      {
        // Byte code:
        //   0: ldc 25
        //   2: invokestatic 31	java/security/MessageDigest:getInstance	(Ljava/lang/String;)Ljava/security/MessageDigest;
        //   5: astore_1
        //   6: invokestatic 35	android/telecom/Log:-get0	()Ljava/lang/Object;
        //   9: astore_2
        //   10: aload_2
        //   11: monitorenter
        //   12: aload_1
        //   13: invokestatic 39	android/telecom/Log:-set0	(Ljava/security/MessageDigest;)Ljava/security/MessageDigest;
        //   16: pop
        //   17: aload_2
        //   18: monitorexit
        //   19: aconst_null
        //   20: areturn
        //   21: astore_1
        //   22: aconst_null
        //   23: astore_1
        //   24: goto -18 -> 6
        //   27: astore_1
        //   28: aload_2
        //   29: monitorexit
        //   30: aload_1
        //   31: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	32	0	this	1
        //   0	32	1	paramAnonymousVarArgs	Void[]
        // Exception table:
        //   from	to	target	type
        //   0	6	21	java/security/NoSuchAlgorithmException
        //   12	17	27	finally
      }
    }.execute(new Void[0]);
  }
  
  public static boolean isLoggable(int paramInt)
  {
    return android.util.Log.isLoggable("TelecomFramework", paramInt);
  }
  
  public static String pii(Object paramObject)
  {
    if ((paramObject == null) || (VERBOSE)) {
      return String.valueOf(paramObject);
    }
    if ((paramObject instanceof Uri)) {
      return piiUri((Uri)paramObject);
    }
    return "[" + secureHash(String.valueOf(paramObject).getBytes()) + "]";
  }
  
  private static String piiUri(Uri paramUri)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    String str = paramUri.getScheme();
    if (!TextUtils.isEmpty(str)) {
      localStringBuilder.append(str).append(":");
    }
    paramUri = paramUri.getSchemeSpecificPart();
    if (!TextUtils.isEmpty(paramUri))
    {
      int i = 0;
      if (i < paramUri.length())
      {
        char c = paramUri.charAt(i);
        if (PhoneNumberUtils.isStartsPostDial(c)) {
          localStringBuilder.append(c);
        }
        for (;;)
        {
          i += 1;
          break;
          if (PhoneNumberUtils.isDialable(c))
          {
            localStringBuilder.append("*");
          }
          else
          {
            if (('a' <= c) && (c <= 'z')) {}
            while (('A' <= c) && (c <= 'Z'))
            {
              localStringBuilder.append("*");
              break;
            }
            localStringBuilder.append(c);
          }
        }
      }
    }
    return localStringBuilder.toString();
  }
  
  private static String secureHash(byte[] paramArrayOfByte)
  {
    synchronized (sMessageDigestLock)
    {
      if (sMessageDigest != null)
      {
        sMessageDigest.reset();
        sMessageDigest.update(paramArrayOfByte);
        paramArrayOfByte = encodeHex(sMessageDigest.digest());
        return paramArrayOfByte;
      }
      return "Uninitialized SHA1";
    }
  }
  
  public static void v(Object paramObject, String paramString, Object... paramVarArgs)
  {
    if (VERBOSE) {
      android.util.Log.v("TelecomFramework", buildMessage(getPrefixFromObject(paramObject), paramString, paramVarArgs));
    }
  }
  
  public static void v(String paramString1, String paramString2, Object... paramVarArgs)
  {
    if (VERBOSE) {
      android.util.Log.v("TelecomFramework", buildMessage(paramString1, paramString2, paramVarArgs));
    }
  }
  
  public static void w(Object paramObject, String paramString, Object... paramVarArgs)
  {
    if (WARN) {
      android.util.Log.w("TelecomFramework", buildMessage(getPrefixFromObject(paramObject), paramString, paramVarArgs));
    }
  }
  
  public static void w(String paramString1, String paramString2, Object... paramVarArgs)
  {
    if (WARN) {
      android.util.Log.w("TelecomFramework", buildMessage(paramString1, paramString2, paramVarArgs));
    }
  }
  
  public static void wtf(Object paramObject, String paramString, Object... paramVarArgs)
  {
    paramObject = buildMessage(getPrefixFromObject(paramObject), paramString, paramVarArgs);
    android.util.Log.wtf("TelecomFramework", (String)paramObject, new IllegalStateException((String)paramObject));
  }
  
  public static void wtf(Object paramObject, Throwable paramThrowable, String paramString, Object... paramVarArgs)
  {
    android.util.Log.wtf("TelecomFramework", buildMessage(getPrefixFromObject(paramObject), paramString, paramVarArgs), paramThrowable);
  }
  
  public static void wtf(String paramString1, String paramString2, Object... paramVarArgs)
  {
    paramString1 = buildMessage(paramString1, paramString2, paramVarArgs);
    android.util.Log.wtf("TelecomFramework", paramString1, new IllegalStateException(paramString1));
  }
  
  public static void wtf(String paramString1, Throwable paramThrowable, String paramString2, Object... paramVarArgs)
  {
    android.util.Log.wtf("TelecomFramework", buildMessage(paramString1, paramString2, paramVarArgs), paramThrowable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/Log.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */