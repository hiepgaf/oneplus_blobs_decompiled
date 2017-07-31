package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Slog;
import com.oem.os.IOnePlusNfcService.Stub;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class OnePlusNfcService
  extends IOnePlusNfcService.Stub
{
  public static final String CARD_CONFIG_PATH = "/etc/nfc_card_config.conf";
  public static final String CARD_CONFIG_PROPERTY = "persist.oem.nfc.rf.card";
  private static String TAG = "OnePlusNfcService";
  private Map<String, String> mCardConfigMap = new HashMap();
  private Context mContext;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.nfc.action.ADAPTER_STATE_CHANGED"))
      {
        int i = paramAnonymousIntent.getIntExtra("android.nfc.extra.ADAPTER_STATE", -1);
        Slog.d(OnePlusNfcService.-get0(), "[NfcBroadcast] state change state " + i);
        if (i == 1)
        {
          NfcAdapter.getDefaultAdapter(OnePlusNfcService.-get1(OnePlusNfcService.this)).enable();
          OnePlusNfcService.-get1(OnePlusNfcService.this).unregisterReceiver(OnePlusNfcService.-get2(OnePlusNfcService.this));
        }
      }
    }
  };
  
  public OnePlusNfcService(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private String getCurrentConfig()
  {
    return SystemProperties.get("persist.oem.nfc.rf.card", "0");
  }
  
  private void loadConfigMap()
  {
    parseConfigs();
    dumpConfigs();
  }
  
  /* Error */
  private boolean parseConfigs()
  {
    // Byte code:
    //   0: new 73	java/io/File
    //   3: dup
    //   4: invokestatic 79	android/os/Environment:getRootDirectory	()Ljava/io/File;
    //   7: ldc 10
    //   9: invokespecial 82	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   12: astore_3
    //   13: aload_3
    //   14: invokevirtual 85	java/io/File:exists	()Z
    //   17: ifne +14 -> 31
    //   20: getstatic 25	com/android/server/OnePlusNfcService:TAG	Ljava/lang/String;
    //   23: ldc 87
    //   25: invokestatic 93	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   28: pop
    //   29: iconst_0
    //   30: ireturn
    //   31: getstatic 25	com/android/server/OnePlusNfcService:TAG	Ljava/lang/String;
    //   34: ldc 95
    //   36: invokestatic 98	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   39: pop
    //   40: invokestatic 104	org/xmlpull/v1/XmlPullParserFactory:newInstance	()Lorg/xmlpull/v1/XmlPullParserFactory;
    //   43: invokevirtual 108	org/xmlpull/v1/XmlPullParserFactory:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   46: astore_2
    //   47: new 110	java/io/FileReader
    //   50: dup
    //   51: aload_3
    //   52: invokespecial 113	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   55: astore_3
    //   56: aload_2
    //   57: aload_3
    //   58: invokeinterface 119 2 0
    //   63: aload_2
    //   64: invokeinterface 123 1 0
    //   69: istore_1
    //   70: iload_1
    //   71: iconst_1
    //   72: if_icmpeq +63 -> 135
    //   75: iload_1
    //   76: iconst_2
    //   77: if_icmpne +48 -> 125
    //   80: aload_2
    //   81: invokeinterface 126 1 0
    //   86: ldc -128
    //   88: invokevirtual 134	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   91: ifeq -21 -> 70
    //   94: aload_2
    //   95: aconst_null
    //   96: ldc -120
    //   98: invokeinterface 139 3 0
    //   103: astore_3
    //   104: aload_2
    //   105: invokeinterface 142 1 0
    //   110: astore 4
    //   112: aload_0
    //   113: getfield 47	com/android/server/OnePlusNfcService:mCardConfigMap	Ljava/util/Map;
    //   116: aload_3
    //   117: aload 4
    //   119: invokeinterface 148 3 0
    //   124: pop
    //   125: aload_2
    //   126: invokeinterface 151 1 0
    //   131: istore_1
    //   132: goto -62 -> 70
    //   135: iconst_1
    //   136: ireturn
    //   137: astore_2
    //   138: iconst_1
    //   139: ireturn
    //   140: astore_2
    //   141: iconst_1
    //   142: ireturn
    //   143: astore_2
    //   144: iconst_1
    //   145: ireturn
    //   146: astore_2
    //   147: iconst_1
    //   148: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	OnePlusNfcService
    //   69	63	1	i	int
    //   46	80	2	localXmlPullParser	org.xmlpull.v1.XmlPullParser
    //   137	1	2	localXmlPullParserException1	org.xmlpull.v1.XmlPullParserException
    //   140	1	2	localXmlPullParserException2	org.xmlpull.v1.XmlPullParserException
    //   143	1	2	localException1	Exception
    //   146	1	2	localException2	Exception
    //   12	105	3	localObject	Object
    //   110	8	4	str	String
    // Exception table:
    //   from	to	target	type
    //   40	56	137	org/xmlpull/v1/XmlPullParserException
    //   56	70	140	org/xmlpull/v1/XmlPullParserException
    //   80	125	140	org/xmlpull/v1/XmlPullParserException
    //   125	132	140	org/xmlpull/v1/XmlPullParserException
    //   40	56	143	java/lang/Exception
    //   56	70	146	java/lang/Exception
    //   80	125	146	java/lang/Exception
    //   125	132	146	java/lang/Exception
  }
  
  private void resetNfcService()
  {
    NfcAdapter localNfcAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
    if (!localNfcAdapter.isEnabled())
    {
      Slog.w(TAG, "[resetNfcService] nfc is disable,no need to reset");
      return;
    }
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.nfc.action.ADAPTER_STATE_CHANGED");
    this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, localIntentFilter, null, null);
    localNfcAdapter.disable();
  }
  
  public boolean applyConfig()
  {
    Slog.d(TAG, "[applyConfig]");
    return false;
  }
  
  public void dumpConfigs()
  {
    Iterator localIterator = this.mCardConfigMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      String str = (String)((Map.Entry)localObject).getKey();
      localObject = (String)((Map.Entry)localObject).getValue();
      Slog.d(TAG, "[dumpConfigs] " + str + " " + (String)localObject);
    }
  }
  
  public String getDieId()
  {
    Slog.d(TAG, "[getDieid]");
    return NfcAdapter.getDefaultAdapter(this.mContext).getDieId();
  }
  
  public List<String> getSupportCardTypes()
  {
    Slog.d(TAG, "[getSupportCardTypes]");
    return new ArrayList();
  }
  
  public List<String> getSupportNfcConfigs()
  {
    Slog.d(TAG, "[getSupportNfcConfigs]");
    return null;
  }
  
  public void setCardType(String paramString)
  {
    Slog.d(TAG, "[setCardType] type " + paramString);
    setNfcConfig((String)this.mCardConfigMap.get(paramString));
  }
  
  public void setNfcConfig(String paramString)
  {
    if (paramString == null) {
      return;
    }
    Slog.d(TAG, "[setNfcConfig] config " + paramString);
    SystemProperties.set("persist.oem.nfc.rf.card", paramString);
    resetNfcService();
  }
  
  public void setSupportCardTypes(List<String> paramList)
  {
    Slog.d(TAG, "[setSupportCardTypes] cardTypes" + paramList);
  }
  
  public void systemRunning()
  {
    Slog.d(TAG, "[systemRunning]");
    loadConfigMap();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/OnePlusNfcService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */