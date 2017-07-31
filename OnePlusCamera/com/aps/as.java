package com.aps;

final class as
  extends Thread
{
  as(y paramy, String paramString)
  {
    super(paramString);
  }
  
  /* Error */
  public final void run()
  {
    // Byte code:
    //   0: invokestatic 23	android/os/Looper:prepare	()V
    //   3: aload_0
    //   4: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   7: invokestatic 27	android/os/Looper:myLooper	()Landroid/os/Looper;
    //   10: invokestatic 32	com/aps/y:a	(Lcom/aps/y;Landroid/os/Looper;)Landroid/os/Looper;
    //   13: pop
    //   14: aload_0
    //   15: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   18: new 34	com/aps/au
    //   21: dup
    //   22: aload_0
    //   23: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   26: invokespecial 37	com/aps/au:<init>	(Lcom/aps/y;)V
    //   29: invokestatic 40	com/aps/y:a	(Lcom/aps/y;Lcom/aps/au;)Lcom/aps/au;
    //   32: pop
    //   33: aload_0
    //   34: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   37: invokestatic 44	com/aps/y:e	(Lcom/aps/y;)Landroid/location/LocationManager;
    //   40: aload_0
    //   41: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   44: invokestatic 48	com/aps/y:d	(Lcom/aps/y;)Lcom/aps/au;
    //   47: invokevirtual 54	android/location/LocationManager:addGpsStatusListener	(Landroid/location/GpsStatus$Listener;)Z
    //   50: pop
    //   51: aload_0
    //   52: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   55: invokestatic 44	com/aps/y:e	(Lcom/aps/y;)Landroid/location/LocationManager;
    //   58: aload_0
    //   59: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   62: invokestatic 48	com/aps/y:d	(Lcom/aps/y;)Lcom/aps/au;
    //   65: invokevirtual 58	android/location/LocationManager:addNmeaListener	(Landroid/location/GpsStatus$NmeaListener;)Z
    //   68: pop
    //   69: aload_0
    //   70: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   73: new 60	com/aps/at
    //   76: dup
    //   77: aload_0
    //   78: invokespecial 63	com/aps/at:<init>	(Lcom/aps/as;)V
    //   81: invokestatic 66	com/aps/y:a	(Lcom/aps/y;Landroid/os/Handler;)Landroid/os/Handler;
    //   84: pop
    //   85: aload_0
    //   86: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   89: invokestatic 44	com/aps/y:e	(Lcom/aps/y;)Landroid/location/LocationManager;
    //   92: invokevirtual 70	android/location/LocationManager:getAllProviders	()Ljava/util/List;
    //   95: astore_1
    //   96: aload_1
    //   97: ifnonnull +33 -> 130
    //   100: aload_0
    //   101: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   104: invokestatic 44	com/aps/y:e	(Lcom/aps/y;)Landroid/location/LocationManager;
    //   107: ldc 72
    //   109: ldc2_w 73
    //   112: invokestatic 78	com/aps/y:l	()I
    //   115: i2f
    //   116: aload_0
    //   117: getfield 10	com/aps/as:a	Lcom/aps/y;
    //   120: invokestatic 82	com/aps/y:f	(Lcom/aps/y;)Landroid/location/LocationListener;
    //   123: invokevirtual 86	android/location/LocationManager:requestLocationUpdates	(Ljava/lang/String;JFLandroid/location/LocationListener;)V
    //   126: invokestatic 89	android/os/Looper:loop	()V
    //   129: return
    //   130: aload_1
    //   131: ldc 91
    //   133: invokeinterface 97 2 0
    //   138: ifeq -38 -> 100
    //   141: aload_1
    //   142: ldc 72
    //   144: invokeinterface 97 2 0
    //   149: pop
    //   150: goto -50 -> 100
    //   153: astore_1
    //   154: return
    //   155: astore_1
    //   156: goto -30 -> 126
    //   159: astore_1
    //   160: goto -91 -> 69
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	163	0	this	as
    //   95	47	1	localList	java.util.List
    //   153	1	1	localException1	Exception
    //   155	1	1	localException2	Exception
    //   159	1	1	localException3	Exception
    // Exception table:
    //   from	to	target	type
    //   0	33	153	java/lang/Exception
    //   69	96	153	java/lang/Exception
    //   126	129	153	java/lang/Exception
    //   130	150	153	java/lang/Exception
    //   100	126	155	java/lang/Exception
    //   33	69	159	java/lang/Exception
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/as.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */