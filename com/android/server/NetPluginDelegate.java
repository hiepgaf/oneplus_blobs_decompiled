package com.android.server;

import android.net.Network;
import android.net.NetworkStats;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NetPluginDelegate
{
  private static final boolean LOGV = false;
  private static final String TAG = "ConnectivityExtension";
  private static Class tetherExtensionClass = null;
  private static Object tetherExtensionObj = null;
  
  public static void getTetherStats(NetworkStats paramNetworkStats1, NetworkStats paramNetworkStats2, NetworkStats paramNetworkStats3)
  {
    if (!loadTetherExtJar()) {
      return;
    }
    try
    {
      tetherExtensionClass.getMethod("getTetherStats", new Class[] { NetworkStats.class, NetworkStats.class, NetworkStats.class }).invoke(tetherExtensionObj, new Object[] { paramNetworkStats1, paramNetworkStats2, paramNetworkStats3 });
      return;
    }
    catch (Exception paramNetworkStats1)
    {
      paramNetworkStats1.printStackTrace();
      Log.w("ConnectivityExtension", "Error calling getTetherStats Method on extension jar");
      return;
    }
    catch (InvocationTargetException|SecurityException|NoSuchMethodException paramNetworkStats1)
    {
      paramNetworkStats1.printStackTrace();
      Log.w("ConnectivityExtension", "Failed to invoke getTetherStats()");
    }
  }
  
  /* Error */
  private static boolean loadTetherExtJar()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: new 75	java/lang/StringBuilder
    //   6: dup
    //   7: invokespecial 76	java/lang/StringBuilder:<init>	()V
    //   10: invokestatic 82	android/os/Environment:getRootDirectory	()Ljava/io/File;
    //   13: invokevirtual 88	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   16: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: ldc 94
    //   21: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 97	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: astore_0
    //   28: getstatic 19	com/android/server/NetPluginDelegate:tetherExtensionClass	Ljava/lang/Class;
    //   31: ifnull +16 -> 47
    //   34: getstatic 21	com/android/server/NetPluginDelegate:tetherExtensionObj	Ljava/lang/Object;
    //   37: astore_1
    //   38: aload_1
    //   39: ifnull +8 -> 47
    //   42: ldc 2
    //   44: monitorexit
    //   45: iconst_1
    //   46: ireturn
    //   47: new 84	java/io/File
    //   50: dup
    //   51: aload_0
    //   52: invokespecial 100	java/io/File:<init>	(Ljava/lang/String;)V
    //   55: invokevirtual 103	java/io/File:exists	()Z
    //   58: ifne +16 -> 74
    //   61: ldc 11
    //   63: ldc 105
    //   65: invokestatic 65	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   68: pop
    //   69: ldc 2
    //   71: monitorexit
    //   72: iconst_0
    //   73: ireturn
    //   74: getstatic 19	com/android/server/NetPluginDelegate:tetherExtensionClass	Ljava/lang/Class;
    //   77: ifnonnull +39 -> 116
    //   80: getstatic 21	com/android/server/NetPluginDelegate:tetherExtensionObj	Ljava/lang/Object;
    //   83: astore_1
    //   84: aload_1
    //   85: ifnonnull +31 -> 116
    //   88: new 107	dalvik/system/PathClassLoader
    //   91: dup
    //   92: aload_0
    //   93: invokestatic 113	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   96: invokespecial 116	dalvik/system/PathClassLoader:<init>	(Ljava/lang/String;Ljava/lang/ClassLoader;)V
    //   99: ldc 118
    //   101: invokevirtual 122	dalvik/system/PathClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   104: putstatic 19	com/android/server/NetPluginDelegate:tetherExtensionClass	Ljava/lang/Class;
    //   107: getstatic 19	com/android/server/NetPluginDelegate:tetherExtensionClass	Ljava/lang/Class;
    //   110: invokevirtual 126	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   113: putstatic 21	com/android/server/NetPluginDelegate:tetherExtensionObj	Ljava/lang/Object;
    //   116: ldc 2
    //   118: monitorexit
    //   119: iconst_1
    //   120: ireturn
    //   121: astore_0
    //   122: aload_0
    //   123: invokevirtual 57	java/lang/Exception:printStackTrace	()V
    //   126: ldc 11
    //   128: ldc -128
    //   130: invokestatic 65	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   133: pop
    //   134: ldc 2
    //   136: monitorexit
    //   137: iconst_0
    //   138: ireturn
    //   139: astore_0
    //   140: aload_0
    //   141: invokevirtual 131	java/lang/ReflectiveOperationException:printStackTrace	()V
    //   144: ldc 11
    //   146: ldc -123
    //   148: invokestatic 65	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   151: pop
    //   152: ldc 2
    //   154: monitorexit
    //   155: iconst_0
    //   156: ireturn
    //   157: astore_0
    //   158: ldc 2
    //   160: monitorexit
    //   161: aload_0
    //   162: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   27	66	0	str	String
    //   121	2	0	localException	Exception
    //   139	2	0	localClassNotFoundException	ClassNotFoundException
    //   157	5	0	localObject1	Object
    //   37	48	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   88	116	121	java/lang/Exception
    //   88	116	139	java/lang/ClassNotFoundException
    //   88	116	139	java/lang/InstantiationException
    //   88	116	139	java/lang/IllegalAccessException
    //   3	38	157	finally
    //   47	69	157	finally
    //   74	84	157	finally
    //   88	116	157	finally
    //   122	134	157	finally
    //   140	152	157	finally
  }
  
  public static void natStarted(String paramString1, String paramString2)
  {
    if (!loadTetherExtJar()) {
      return;
    }
    try
    {
      tetherExtensionClass.getMethod("natStarted", new Class[] { String.class, String.class }).invoke(tetherExtensionObj, new Object[] { paramString1, paramString2 });
      return;
    }
    catch (Exception paramString1)
    {
      paramString1.printStackTrace();
      Log.w("ConnectivityExtension", "Error calling natStarted Method on extension jar");
      return;
    }
    catch (InvocationTargetException|SecurityException|NoSuchMethodException paramString1)
    {
      paramString1.printStackTrace();
      Log.w("ConnectivityExtension", "Failed to invoke natStarted()");
    }
  }
  
  public static void natStopped(String paramString1, String paramString2)
  {
    if (!loadTetherExtJar()) {
      return;
    }
    try
    {
      tetherExtensionClass.getMethod("natStopped", new Class[] { String.class, String.class }).invoke(tetherExtensionObj, new Object[] { paramString1, paramString2 });
      return;
    }
    catch (Exception paramString1)
    {
      paramString1.printStackTrace();
      Log.w("ConnectivityExtension", "Error calling natStopped Method on extension jar");
      return;
    }
    catch (InvocationTargetException|SecurityException|NoSuchMethodException paramString1)
    {
      paramString1.printStackTrace();
      Log.w("ConnectivityExtension", "Failed to invoke natStopped()");
    }
  }
  
  public static NetworkStats peekTetherStats()
  {
    if (!loadTetherExtJar()) {
      return null;
    }
    try
    {
      NetworkStats localNetworkStats = (NetworkStats)tetherExtensionClass.getMethod("peekTetherStats", new Class[0]).invoke(tetherExtensionObj, new Object[0]);
      return localNetworkStats;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      Log.w("ConnectivityExtension", "Error calling peekTetherStats Method on extension jar");
      return null;
    }
    catch (InvocationTargetException|SecurityException|NoSuchMethodException localInvocationTargetException)
    {
      localInvocationTargetException.printStackTrace();
      Log.w("ConnectivityExtension", "Failed to invoke peekTetherStats()");
    }
    return null;
  }
  
  public static void setQuota(String paramString, long paramLong)
  {
    if (!loadTetherExtJar()) {
      return;
    }
    try
    {
      tetherExtensionClass.getMethod("setQuota", new Class[] { String.class, Long.TYPE }).invoke(tetherExtensionObj, new Object[] { paramString, Long.valueOf(paramLong) });
      return;
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
      Log.w("ConnectivityExtension", "Error calling setQuota Method on extension jar");
      return;
    }
    catch (InvocationTargetException|SecurityException|NoSuchMethodException paramString)
    {
      paramString.printStackTrace();
      Log.w("ConnectivityExtension", "Failed to invoke setQuota()");
    }
  }
  
  public static void setUpstream(Network paramNetwork)
  {
    if (!loadTetherExtJar()) {
      return;
    }
    try
    {
      tetherExtensionClass.getMethod("setUpstream", new Class[] { Network.class }).invoke(tetherExtensionObj, new Object[] { paramNetwork });
      return;
    }
    catch (Exception paramNetwork)
    {
      paramNetwork.printStackTrace();
      Log.w("ConnectivityExtension", "Error calling setUpstream Method on extension jar");
      return;
    }
    catch (InvocationTargetException|SecurityException|NoSuchMethodException paramNetwork)
    {
      paramNetwork.printStackTrace();
      Log.w("ConnectivityExtension", "Failed to invoke setUpstream()");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/NetPluginDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */