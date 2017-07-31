package android.net;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.util.Log;

public final class ZeroBalanceHelper
{
  public static final String BACKGROUND_DATA_BROADCAST = "org.codeaurora.background.data";
  public static final String BACKGROUND_DATA_PROPERTY = "sys.background.data.disable";
  public static final String TAG = "ZeroBalance";
  private static int sRedirectCount = 0;
  private static int sRedirectMaxCount = 3;
  private Context mContext = null;
  
  private String getConfiguredRedirectURL()
  {
    String str = this.mContext.getResources().getString(17039481);
    Log.d("ZeroBalance", "Returning the configured redirect URL   :   " + str);
    return str;
  }
  
  public String getBgDataProperty()
  {
    String str = SystemProperties.get("sys.background.data.disable", "false");
    if (Boolean.valueOf(str).booleanValue()) {
      sRedirectCount = 0;
    }
    return str;
  }
  
  public boolean getFeatureConfigValue()
  {
    return this.mContext.getResources().getBoolean(17957061);
  }
  
  public void setBgDataProperty(String paramString)
  {
    Intent localIntent = new Intent();
    localIntent.setAction("org.codeaurora.background.data");
    localIntent.putExtra("enabled", paramString);
    this.mContext.sendBroadcast(localIntent);
  }
  
  /* Error */
  public void setHttpRedirectCount(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 117	android/net/ZeroBalanceHelper:getConfiguredRedirectURL	()Ljava/lang/String;
    //   6: astore_2
    //   7: aload_2
    //   8: ifnull +76 -> 84
    //   11: aload_1
    //   12: aload_2
    //   13: invokevirtual 123	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   16: ifeq +68 -> 84
    //   19: getstatic 23	android/net/ZeroBalanceHelper:sRedirectCount	I
    //   22: iconst_1
    //   23: iadd
    //   24: putstatic 23	android/net/ZeroBalanceHelper:sRedirectCount	I
    //   27: ldc 14
    //   29: new 54	java/lang/StringBuilder
    //   32: dup
    //   33: invokespecial 55	java/lang/StringBuilder:<init>	()V
    //   36: ldc 125
    //   38: invokevirtual 61	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   41: getstatic 23	android/net/ZeroBalanceHelper:sRedirectCount	I
    //   44: invokevirtual 128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   47: invokevirtual 64	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   50: invokestatic 70	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   53: pop
    //   54: getstatic 23	android/net/ZeroBalanceHelper:sRedirectCount	I
    //   57: getstatic 25	android/net/ZeroBalanceHelper:sRedirectMaxCount	I
    //   60: if_icmplt +21 -> 81
    //   63: ldc 14
    //   65: ldc -126
    //   67: invokestatic 70	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   70: pop
    //   71: aload_0
    //   72: ldc -124
    //   74: invokevirtual 134	android/net/ZeroBalanceHelper:setBgDataProperty	(Ljava/lang/String;)V
    //   77: iconst_0
    //   78: putstatic 23	android/net/ZeroBalanceHelper:sRedirectCount	I
    //   81: aload_0
    //   82: monitorexit
    //   83: return
    //   84: ldc 14
    //   86: ldc -120
    //   88: invokestatic 70	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   91: pop
    //   92: iconst_0
    //   93: putstatic 23	android/net/ZeroBalanceHelper:sRedirectCount	I
    //   96: goto -15 -> 81
    //   99: astore_1
    //   100: aload_0
    //   101: monitorexit
    //   102: aload_1
    //   103: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	104	0	this	ZeroBalanceHelper
    //   0	104	1	paramString	String
    //   6	7	2	str	String
    // Exception table:
    //   from	to	target	type
    //   2	7	99	finally
    //   11	81	99	finally
    //   84	96	99	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ZeroBalanceHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */