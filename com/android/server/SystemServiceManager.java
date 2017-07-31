package com.android.server;

import android.content.Context;
import android.os.Trace;
import android.util.Slog;
import java.util.ArrayList;

public class SystemServiceManager
{
  private static final String TAG = "SystemServiceManager";
  private final Context mContext;
  private int mCurrentPhase = -1;
  private boolean mSafeMode;
  private final ArrayList<SystemService> mServices = new ArrayList();
  
  public SystemServiceManager(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public void cleanupUser(int paramInt)
  {
    int j = this.mServices.size();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        SystemService localSystemService = (SystemService)this.mServices.get(i);
        Trace.traceBegin(524288L, "onCleanupUser " + localSystemService.getClass().getName());
        try
        {
          localSystemService.onCleanupUser(paramInt);
          Trace.traceEnd(524288L);
          i += 1;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Slog.wtf("SystemServiceManager", "Failure reporting cleanup of user " + paramInt + " to service " + localSystemService.getClass().getName(), localException);
          }
        }
      }
    }
  }
  
  public void dump()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Current phase: ").append(this.mCurrentPhase).append("\n");
    localStringBuilder.append("Services:\n");
    int j = this.mServices.size();
    int i = 0;
    while (i < j)
    {
      SystemService localSystemService = (SystemService)this.mServices.get(i);
      localStringBuilder.append("\t").append(localSystemService.getClass().getSimpleName()).append("\n");
      i += 1;
    }
    Slog.e("SystemServiceManager", localStringBuilder.toString());
  }
  
  public boolean isSafeMode()
  {
    return this.mSafeMode;
  }
  
  public void setSafeMode(boolean paramBoolean)
  {
    this.mSafeMode = paramBoolean;
  }
  
  public void startBootPhase(int paramInt)
  {
    if (paramInt <= this.mCurrentPhase) {
      throw new IllegalArgumentException("Next phase must be larger than previous");
    }
    this.mCurrentPhase = paramInt;
    Slog.i("SystemServiceManager", "Starting phase " + this.mCurrentPhase);
    try
    {
      Trace.traceBegin(524288L, "OnBootPhase " + paramInt);
      int i = this.mServices.size();
      paramInt = 0;
      for (;;)
      {
        if (paramInt < i)
        {
          SystemService localSystemService = (SystemService)this.mServices.get(paramInt);
          try
          {
            localSystemService.onBootPhase(this.mCurrentPhase);
            paramInt += 1;
          }
          catch (Exception localException)
          {
            throw new RuntimeException("Failed to boot service " + localSystemService.getClass().getName() + ": onBootPhase threw an exception during phase " + this.mCurrentPhase, localException);
          }
        }
      }
    }
    finally
    {
      Trace.traceEnd(524288L);
    }
  }
  
  /* Error */
  public <T extends SystemService> T startService(Class<T> paramClass)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 67	java/lang/Class:getName	()Ljava/lang/String;
    //   4: astore_2
    //   5: ldc 8
    //   7: new 50	java/lang/StringBuilder
    //   10: dup
    //   11: invokespecial 51	java/lang/StringBuilder:<init>	()V
    //   14: ldc -99
    //   16: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: aload_2
    //   20: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   26: invokestatic 131	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   29: pop
    //   30: ldc2_w 47
    //   33: new 50	java/lang/StringBuilder
    //   36: dup
    //   37: invokespecial 51	java/lang/StringBuilder:<init>	()V
    //   40: ldc -97
    //   42: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: aload_2
    //   46: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   52: invokestatic 76	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   55: ldc 46
    //   57: aload_1
    //   58: invokevirtual 163	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   61: ifne +52 -> 113
    //   64: new 138	java/lang/RuntimeException
    //   67: dup
    //   68: new 50	java/lang/StringBuilder
    //   71: dup
    //   72: invokespecial 51	java/lang/StringBuilder:<init>	()V
    //   75: ldc -91
    //   77: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: aload_2
    //   81: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   84: ldc -89
    //   86: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: ldc 46
    //   91: invokevirtual 67	java/lang/Class:getName	()Ljava/lang/String;
    //   94: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   97: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   100: invokespecial 168	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   103: athrow
    //   104: astore_1
    //   105: ldc2_w 47
    //   108: invokestatic 83	android/os/Trace:traceEnd	(J)V
    //   111: aload_1
    //   112: athrow
    //   113: aload_1
    //   114: iconst_1
    //   115: anewarray 63	java/lang/Class
    //   118: dup
    //   119: iconst_0
    //   120: ldc -86
    //   122: aastore
    //   123: invokevirtual 174	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   126: iconst_1
    //   127: anewarray 4	java/lang/Object
    //   130: dup
    //   131: iconst_0
    //   132: aload_0
    //   133: getfield 31	com/android/server/SystemServiceManager:mContext	Landroid/content/Context;
    //   136: aastore
    //   137: invokevirtual 180	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   140: checkcast 46	com/android/server/SystemService
    //   143: astore_1
    //   144: aload_0
    //   145: getfield 27	com/android/server/SystemServiceManager:mServices	Ljava/util/ArrayList;
    //   148: aload_1
    //   149: invokevirtual 184	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   152: pop
    //   153: aload_1
    //   154: invokevirtual 187	com/android/server/SystemService:onStart	()V
    //   157: ldc2_w 47
    //   160: invokestatic 83	android/os/Trace:traceEnd	(J)V
    //   163: aload_1
    //   164: areturn
    //   165: astore_1
    //   166: new 138	java/lang/RuntimeException
    //   169: dup
    //   170: new 50	java/lang/StringBuilder
    //   173: dup
    //   174: invokespecial 51	java/lang/StringBuilder:<init>	()V
    //   177: ldc -67
    //   179: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: aload_2
    //   183: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   186: ldc -65
    //   188: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   191: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   194: aload_1
    //   195: invokespecial 145	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   198: athrow
    //   199: astore_1
    //   200: new 138	java/lang/RuntimeException
    //   203: dup
    //   204: new 50	java/lang/StringBuilder
    //   207: dup
    //   208: invokespecial 51	java/lang/StringBuilder:<init>	()V
    //   211: ldc -67
    //   213: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   216: aload_2
    //   217: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   220: ldc -63
    //   222: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   225: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   228: aload_1
    //   229: invokespecial 145	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   232: athrow
    //   233: astore_1
    //   234: new 138	java/lang/RuntimeException
    //   237: dup
    //   238: new 50	java/lang/StringBuilder
    //   241: dup
    //   242: invokespecial 51	java/lang/StringBuilder:<init>	()V
    //   245: ldc -67
    //   247: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   250: aload_2
    //   251: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   254: ldc -63
    //   256: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   259: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   262: aload_1
    //   263: invokespecial 145	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   266: athrow
    //   267: astore_1
    //   268: new 138	java/lang/RuntimeException
    //   271: dup
    //   272: new 50	java/lang/StringBuilder
    //   275: dup
    //   276: invokespecial 51	java/lang/StringBuilder:<init>	()V
    //   279: ldc -67
    //   281: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   284: aload_2
    //   285: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   288: ldc -61
    //   290: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   293: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   296: aload_1
    //   297: invokespecial 145	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   300: athrow
    //   301: astore_1
    //   302: new 138	java/lang/RuntimeException
    //   305: dup
    //   306: new 50	java/lang/StringBuilder
    //   309: dup
    //   310: invokespecial 51	java/lang/StringBuilder:<init>	()V
    //   313: ldc -59
    //   315: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   318: aload_2
    //   319: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   322: ldc -57
    //   324: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   327: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   330: aload_1
    //   331: invokespecial 145	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   334: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	335	0	this	SystemServiceManager
    //   0	335	1	paramClass	Class<T>
    //   4	315	2	str	String
    // Exception table:
    //   from	to	target	type
    //   0	104	104	finally
    //   113	144	104	finally
    //   144	153	104	finally
    //   153	157	104	finally
    //   166	199	104	finally
    //   200	233	104	finally
    //   234	267	104	finally
    //   268	301	104	finally
    //   302	335	104	finally
    //   113	144	165	java/lang/reflect/InvocationTargetException
    //   113	144	199	java/lang/NoSuchMethodException
    //   113	144	233	java/lang/IllegalAccessException
    //   113	144	267	java/lang/InstantiationException
    //   153	157	301	java/lang/RuntimeException
  }
  
  public SystemService startService(String paramString)
  {
    try
    {
      Class localClass = Class.forName(paramString);
      return startService(localClass);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      Slog.i("SystemServiceManager", "Starting " + paramString);
      throw new RuntimeException("Failed to create service " + paramString + ": service class not found, usually indicates that the caller should " + "have called PackageManager.hasSystemFeature() to check whether the " + "feature is available on this device before trying to start the " + "services that implement it", localClassNotFoundException);
    }
  }
  
  public void startUser(int paramInt)
  {
    int j = this.mServices.size();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        SystemService localSystemService = (SystemService)this.mServices.get(i);
        Trace.traceBegin(524288L, "onStartUser " + localSystemService.getClass().getName());
        try
        {
          localSystemService.onStartUser(paramInt);
          Trace.traceEnd(524288L);
          i += 1;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Slog.wtf("SystemServiceManager", "Failure reporting start of user " + paramInt + " to service " + localSystemService.getClass().getName(), localException);
          }
        }
      }
    }
  }
  
  public void stopUser(int paramInt)
  {
    int j = this.mServices.size();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        SystemService localSystemService = (SystemService)this.mServices.get(i);
        Trace.traceBegin(524288L, "onStopUser " + localSystemService.getClass().getName());
        try
        {
          localSystemService.onStopUser(paramInt);
          Trace.traceEnd(524288L);
          i += 1;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Slog.wtf("SystemServiceManager", "Failure reporting stop of user " + paramInt + " to service " + localSystemService.getClass().getName(), localException);
          }
        }
      }
    }
  }
  
  public void switchUser(int paramInt)
  {
    int j = this.mServices.size();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        SystemService localSystemService = (SystemService)this.mServices.get(i);
        Trace.traceBegin(524288L, "onSwitchUser " + localSystemService.getClass().getName());
        try
        {
          localSystemService.onSwitchUser(paramInt);
          Trace.traceEnd(524288L);
          i += 1;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Slog.wtf("SystemServiceManager", "Failure reporting switch of user " + paramInt + " to service " + localSystemService.getClass().getName(), localException);
          }
        }
      }
    }
  }
  
  public void unlockUser(int paramInt)
  {
    int j = this.mServices.size();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        SystemService localSystemService = (SystemService)this.mServices.get(i);
        Trace.traceBegin(524288L, "onUnlockUser " + localSystemService.getClass().getName());
        try
        {
          localSystemService.onUnlockUser(paramInt);
          Trace.traceEnd(524288L);
          i += 1;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Slog.wtf("SystemServiceManager", "Failure reporting unlock of user " + paramInt + " to service " + localSystemService.getClass().getName(), localException);
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/SystemServiceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */