package com.aps;

import android.content.BroadcastReceiver;

final class ao
  extends BroadcastReceiver
{
  private ao(ak paramak) {}
  
  /* Error */
  public final void onReceive(android.content.Context paramContext, android.content.Intent paramIntent)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +4 -> 5
    //   4: return
    //   5: aload_2
    //   6: ifnull -2 -> 4
    //   9: aload_0
    //   10: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   13: invokestatic 27	com/aps/ak:c	(Lcom/aps/ak;)Landroid/net/wifi/WifiManager;
    //   16: ifnull -12 -> 4
    //   19: aload_0
    //   20: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   23: invokestatic 31	com/aps/ak:d	(Lcom/aps/ak;)Ljava/util/Timer;
    //   26: ifnull -22 -> 4
    //   29: aload_0
    //   30: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   33: invokestatic 35	com/aps/ak:e	(Lcom/aps/ak;)Ljava/util/List;
    //   36: ifnull -32 -> 4
    //   39: aload_2
    //   40: invokevirtual 41	android/content/Intent:getAction	()Ljava/lang/String;
    //   43: ifnull -39 -> 4
    //   46: ldc 43
    //   48: aload_2
    //   49: invokevirtual 41	android/content/Intent:getAction	()Ljava/lang/String;
    //   52: invokevirtual 49	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   55: ifeq -51 -> 4
    //   58: aload_0
    //   59: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   62: invokestatic 27	com/aps/ak:c	(Lcom/aps/ak;)Landroid/net/wifi/WifiManager;
    //   65: invokevirtual 55	android/net/wifi/WifiManager:getScanResults	()Ljava/util/List;
    //   68: astore_1
    //   69: aload_0
    //   70: monitorenter
    //   71: aload_0
    //   72: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   75: invokestatic 35	com/aps/ak:e	(Lcom/aps/ak;)Ljava/util/List;
    //   78: invokeinterface 60 1 0
    //   83: aload_0
    //   84: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   87: invokestatic 66	java/lang/System:currentTimeMillis	()J
    //   90: invokestatic 69	com/aps/ak:a	(Lcom/aps/ak;J)J
    //   93: pop2
    //   94: aload_1
    //   95: ifnonnull +59 -> 154
    //   98: aload_0
    //   99: monitorexit
    //   100: new 71	com/aps/ap
    //   103: dup
    //   104: aload_0
    //   105: invokespecial 74	com/aps/ap:<init>	(Lcom/aps/ao;)V
    //   108: astore_1
    //   109: aload_0
    //   110: monitorenter
    //   111: aload_0
    //   112: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   115: invokestatic 31	com/aps/ak:d	(Lcom/aps/ak;)Ljava/util/Timer;
    //   118: ifnonnull +96 -> 214
    //   121: aload_0
    //   122: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   125: new 76	java/util/Timer
    //   128: dup
    //   129: invokespecial 77	java/util/Timer:<init>	()V
    //   132: invokestatic 80	com/aps/ak:a	(Lcom/aps/ak;Ljava/util/Timer;)Ljava/util/Timer;
    //   135: pop
    //   136: aload_0
    //   137: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   140: invokestatic 31	com/aps/ak:d	(Lcom/aps/ak;)Ljava/util/Timer;
    //   143: aload_1
    //   144: invokestatic 84	com/aps/ak:z	()I
    //   147: i2l
    //   148: invokevirtual 88	java/util/Timer:schedule	(Ljava/util/TimerTask;J)V
    //   151: aload_0
    //   152: monitorexit
    //   153: return
    //   154: aload_1
    //   155: invokeinterface 91 1 0
    //   160: ifle -62 -> 98
    //   163: iconst_0
    //   164: istore_3
    //   165: iload_3
    //   166: aload_1
    //   167: invokeinterface 91 1 0
    //   172: if_icmpge -74 -> 98
    //   175: aload_1
    //   176: iload_3
    //   177: invokeinterface 95 2 0
    //   182: checkcast 97	android/net/wifi/ScanResult
    //   185: astore_2
    //   186: aload_0
    //   187: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   190: invokestatic 35	com/aps/ak:e	(Lcom/aps/ak;)Ljava/util/List;
    //   193: aload_2
    //   194: invokeinterface 100 2 0
    //   199: pop
    //   200: iload_3
    //   201: iconst_1
    //   202: iadd
    //   203: istore_3
    //   204: goto -39 -> 165
    //   207: astore_1
    //   208: aload_0
    //   209: monitorexit
    //   210: aload_1
    //   211: athrow
    //   212: astore_1
    //   213: return
    //   214: aload_0
    //   215: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   218: invokestatic 31	com/aps/ak:d	(Lcom/aps/ak;)Ljava/util/Timer;
    //   221: invokevirtual 103	java/util/Timer:cancel	()V
    //   224: aload_0
    //   225: getfield 10	com/aps/ao:a	Lcom/aps/ak;
    //   228: aconst_null
    //   229: invokestatic 80	com/aps/ak:a	(Lcom/aps/ak;Ljava/util/Timer;)Ljava/util/Timer;
    //   232: pop
    //   233: goto -112 -> 121
    //   236: astore_1
    //   237: aload_0
    //   238: monitorexit
    //   239: aload_1
    //   240: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	241	0	this	ao
    //   0	241	1	paramContext	android.content.Context
    //   0	241	2	paramIntent	android.content.Intent
    //   164	40	3	i	int
    // Exception table:
    //   from	to	target	type
    //   71	94	207	finally
    //   98	100	207	finally
    //   154	163	207	finally
    //   165	200	207	finally
    //   9	71	212	java/lang/Exception
    //   100	111	212	java/lang/Exception
    //   208	212	212	java/lang/Exception
    //   237	241	212	java/lang/Exception
    //   111	121	236	finally
    //   121	153	236	finally
    //   214	233	236	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ao.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */