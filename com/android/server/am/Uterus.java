package com.android.server.am;

import android.content.pm.ApplicationInfo;
import android.os.Debug.MemoryInfo;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONObject;

class Uterus
{
  private static final boolean DEBUG = true;
  private static int MAX = SystemProperties.getInt("persist.sys.embryo.limit", 32);
  private static final String PATH = "/data/system/embryo";
  private static final long SCALE = SystemProperties.getLong("persist.sys.embryo.scale", 3600000L);
  private static final String TAG = "Embryo_Uterus";
  private static final ArrayList<String> mBlackList = new ArrayList();
  private static Uterus sInstance;
  private final Map<String, EmbryoSupervisor> mAllSupervisorMap = new HashMap();
  private final Runnable mConfigUpdateRunnable = new Runnable()
  {
    public void run()
    {
      Uterus.-set0(SystemProperties.getInt("persist.sys.embryo.limit", 32));
      Log.i("Embryo_Uterus", "update MAX " + Uterus.-get0());
    }
  };
  private EmbryoSupervisor mCurrentSupervisor;
  private final Handler mHandler;
  private EmbryoHelper mHelper = EmbryoHelper.getInstance();
  private String mHomePackageName;
  private List<EmbryoSupervisor> mLRUHistory = new ArrayList();
  private final List<EmbryoSupervisor> mSupervisors = new ArrayList(MAX * 2);
  private final HandlerThread mThread = new HandlerThread("EmbryoManager");
  private final Runnable mTrimRunnable = new Runnable()
  {
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: new 21	java/util/ArrayList
      //   3: dup
      //   4: invokespecial 22	java/util/ArrayList:<init>	()V
      //   7: astore_3
      //   8: aload_0
      //   9: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   12: invokestatic 26	com/android/server/am/Uterus:-get3	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   15: astore 4
      //   17: aload 4
      //   19: monitorenter
      //   20: ldc 28
      //   22: new 30	java/lang/StringBuilder
      //   25: dup
      //   26: invokespecial 31	java/lang/StringBuilder:<init>	()V
      //   29: ldc 33
      //   31: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   34: aload_0
      //   35: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   38: invokestatic 26	com/android/server/am/Uterus:-get3	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   41: invokeinterface 43 1 0
      //   46: invokevirtual 46	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   49: invokevirtual 50	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   52: invokestatic 56	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   55: pop
      //   56: aload_0
      //   57: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   60: invokestatic 26	com/android/server/am/Uterus:-get3	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   63: invokeinterface 43 1 0
      //   68: iconst_1
      //   69: isub
      //   70: istore_1
      //   71: iload_1
      //   72: iflt +75 -> 147
      //   75: aload_0
      //   76: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   79: invokestatic 26	com/android/server/am/Uterus:-get3	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   82: iload_1
      //   83: invokeinterface 60 2 0
      //   88: checkcast 62	com/android/server/am/EmbryoSupervisor
      //   91: astore 5
      //   93: aload 5
      //   95: invokevirtual 66	com/android/server/am/EmbryoSupervisor:hasEmbryo	()Z
      //   98: ifne +464 -> 562
      //   101: ldc 28
      //   103: new 30	java/lang/StringBuilder
      //   106: dup
      //   107: invokespecial 31	java/lang/StringBuilder:<init>	()V
      //   110: ldc 68
      //   112: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   115: aload 5
      //   117: invokevirtual 71	com/android/server/am/EmbryoSupervisor:getPackageName	()Ljava/lang/String;
      //   120: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   123: invokevirtual 50	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   126: invokestatic 56	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   129: pop
      //   130: aload_0
      //   131: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   134: invokestatic 26	com/android/server/am/Uterus:-get3	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   137: iload_1
      //   138: invokeinterface 74 2 0
      //   143: pop
      //   144: goto +418 -> 562
      //   147: aload_0
      //   148: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   151: invokestatic 26	com/android/server/am/Uterus:-get3	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   154: invokeinterface 43 1 0
      //   159: istore_1
      //   160: invokestatic 77	com/android/server/am/Uterus:-get0	()I
      //   163: istore_2
      //   164: iload_1
      //   165: iload_2
      //   166: if_icmpgt +7 -> 173
      //   169: aload 4
      //   171: monitorexit
      //   172: return
      //   173: aload_3
      //   174: aload_0
      //   175: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   178: invokestatic 26	com/android/server/am/Uterus:-get3	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   181: invokeinterface 81 2 0
      //   186: pop
      //   187: aload 4
      //   189: monitorexit
      //   190: ldc 28
      //   192: new 30	java/lang/StringBuilder
      //   195: dup
      //   196: invokespecial 31	java/lang/StringBuilder:<init>	()V
      //   199: ldc 83
      //   201: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   204: aload_3
      //   205: invokeinterface 43 1 0
      //   210: invokevirtual 46	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   213: invokevirtual 50	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   216: invokestatic 56	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   219: pop
      //   220: aload_3
      //   221: new 85	com/android/server/am/EmbryoSupervisor$LowToHighComparator
      //   224: dup
      //   225: invokespecial 86	com/android/server/am/EmbryoSupervisor$LowToHighComparator:<init>	()V
      //   228: invokestatic 92	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
      //   231: aload_0
      //   232: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   235: invokestatic 95	com/android/server/am/Uterus:-get2	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   238: astore 4
      //   240: aload 4
      //   242: monitorenter
      //   243: aload_3
      //   244: invokeinterface 101 1 0
      //   249: astore 5
      //   251: iconst_1
      //   252: istore_1
      //   253: aload 5
      //   255: invokeinterface 106 1 0
      //   260: ifeq +51 -> 311
      //   263: aload 5
      //   265: invokeinterface 110 1 0
      //   270: checkcast 62	com/android/server/am/EmbryoSupervisor
      //   273: astore 6
      //   275: aload_0
      //   276: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   279: invokestatic 95	com/android/server/am/Uterus:-get2	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   282: aload 6
      //   284: invokeinterface 114 2 0
      //   289: istore_2
      //   290: aload 6
      //   292: iload_2
      //   293: iload_1
      //   294: iadd
      //   295: invokevirtual 118	com/android/server/am/EmbryoSupervisor:setRank	(I)V
      //   298: iload_1
      //   299: iconst_1
      //   300: iadd
      //   301: istore_1
      //   302: goto -49 -> 253
      //   305: astore_3
      //   306: aload 4
      //   308: monitorexit
      //   309: aload_3
      //   310: athrow
      //   311: aload 4
      //   313: monitorexit
      //   314: aload_3
      //   315: new 120	com/android/server/am/EmbryoSupervisor$RankComparator
      //   318: dup
      //   319: invokespecial 121	com/android/server/am/EmbryoSupervisor$RankComparator:<init>	()V
      //   322: invokestatic 92	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
      //   325: aload_3
      //   326: invokeinterface 101 1 0
      //   331: astore 4
      //   333: aload 4
      //   335: invokeinterface 106 1 0
      //   340: ifeq +79 -> 419
      //   343: aload 4
      //   345: invokeinterface 110 1 0
      //   350: checkcast 62	com/android/server/am/EmbryoSupervisor
      //   353: astore 5
      //   355: ldc 28
      //   357: new 30	java/lang/StringBuilder
      //   360: dup
      //   361: invokespecial 31	java/lang/StringBuilder:<init>	()V
      //   364: ldc 123
      //   366: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   369: aload 5
      //   371: invokevirtual 126	com/android/server/am/EmbryoSupervisor:getRank	()I
      //   374: invokevirtual 46	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   377: ldc -128
      //   379: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   382: aload 5
      //   384: invokevirtual 71	com/android/server/am/EmbryoSupervisor:getPackageName	()Ljava/lang/String;
      //   387: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   390: ldc -128
      //   392: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   395: aload 5
      //   397: invokevirtual 132	com/android/server/am/EmbryoSupervisor:getForegroundTime	()J
      //   400: invokevirtual 135	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
      //   403: invokevirtual 50	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   406: invokestatic 56	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   409: pop
      //   410: goto -77 -> 333
      //   413: astore_3
      //   414: aload 4
      //   416: monitorexit
      //   417: aload_3
      //   418: athrow
      //   419: aload_3
      //   420: invokeinterface 43 1 0
      //   425: invokestatic 77	com/android/server/am/Uterus:-get0	()I
      //   428: isub
      //   429: iconst_1
      //   430: isub
      //   431: istore_1
      //   432: iload_1
      //   433: iflt +116 -> 549
      //   436: aload_3
      //   437: iload_1
      //   438: invokeinterface 60 2 0
      //   443: checkcast 62	com/android/server/am/EmbryoSupervisor
      //   446: astore 4
      //   448: aload_0
      //   449: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   452: invokestatic 26	com/android/server/am/Uterus:-get3	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   455: astore 5
      //   457: aload 5
      //   459: monitorenter
      //   460: aload_0
      //   461: getfield 14	com/android/server/am/Uterus$1:this$0	Lcom/android/server/am/Uterus;
      //   464: invokestatic 26	com/android/server/am/Uterus:-get3	(Lcom/android/server/am/Uterus;)Ljava/util/List;
      //   467: aload 4
      //   469: invokeinterface 138 2 0
      //   474: pop
      //   475: aload 5
      //   477: monitorexit
      //   478: aload 4
      //   480: monitorenter
      //   481: ldc 28
      //   483: new 30	java/lang/StringBuilder
      //   486: dup
      //   487: invokespecial 31	java/lang/StringBuilder:<init>	()V
      //   490: ldc -116
      //   492: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   495: aload 4
      //   497: invokevirtual 71	com/android/server/am/EmbryoSupervisor:getPackageName	()Ljava/lang/String;
      //   500: invokevirtual 37	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   503: invokevirtual 50	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   506: invokestatic 56	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   509: pop
      //   510: aload 4
      //   512: invokevirtual 144	com/android/server/am/EmbryoSupervisor:detach	()Lcom/android/server/am/Embryo;
      //   515: astore 5
      //   517: aload 5
      //   519: ifnull +8 -> 527
      //   522: aload 5
      //   524: invokevirtual 149	com/android/server/am/Embryo:destroy	()V
      //   527: aload 4
      //   529: monitorexit
      //   530: iload_1
      //   531: iconst_1
      //   532: isub
      //   533: istore_1
      //   534: goto -102 -> 432
      //   537: astore_3
      //   538: aload 5
      //   540: monitorexit
      //   541: aload_3
      //   542: athrow
      //   543: astore_3
      //   544: aload 4
      //   546: monitorexit
      //   547: aload_3
      //   548: athrow
      //   549: ldc 28
      //   551: ldc -105
      //   553: invokestatic 56	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   556: pop
      //   557: return
      //   558: astore_3
      //   559: goto -145 -> 414
      //   562: iload_1
      //   563: iconst_1
      //   564: isub
      //   565: istore_1
      //   566: goto -495 -> 71
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	569	0	this	1
      //   70	496	1	i	int
      //   163	132	2	j	int
      //   7	237	3	localArrayList	ArrayList
      //   305	21	3	localList	List
      //   413	24	3	localObject1	Object
      //   537	5	3	localObject2	Object
      //   543	5	3	localObject3	Object
      //   558	1	3	localObject4	Object
      //   15	530	4	localObject5	Object
      //   273	18	6	localEmbryoSupervisor	EmbryoSupervisor
      // Exception table:
      //   from	to	target	type
      //   20	71	305	finally
      //   75	144	305	finally
      //   147	164	305	finally
      //   173	187	305	finally
      //   243	251	413	finally
      //   290	298	413	finally
      //   460	475	537	finally
      //   481	517	543	finally
      //   522	527	543	finally
      //   253	290	558	finally
    }
  };
  private final Map<Integer, Embryo> mWaitingForAttach = new HashMap();
  
  private Uterus()
  {
    this.mThread.start();
    this.mHandler = new Handler(this.mThread.getLooper());
  }
  
  private void flushToStorage()
  {
    for (;;)
    {
      double d1;
      try
      {
        Log.d("Embryo_Uterus", "Flush to storage");
        Object localObject3 = new ArrayList();
        synchronized (this.mAllSupervisorMap)
        {
          int i = this.mAllSupervisorMap.size();
          if (i == 0) {
            return;
          }
          ((List)localObject3).addAll(this.mAllSupervisorMap.values());
          Collections.sort((List)localObject3, new EmbryoSupervisor.HighToLowComparator());
          try
          {
            ??? = new JSONObject();
            i = 1;
            long l = SCALE;
            d1 = 1.0D;
            localObject3 = ((Iterable)localObject3).iterator();
            if (((Iterator)localObject3).hasNext())
            {
              EmbryoSupervisor localEmbryoSupervisor = (EmbryoSupervisor)((Iterator)localObject3).next();
              if (localEmbryoSupervisor.getPackageName().equals(this.mHomePackageName)) {
                continue;
              }
              j = i;
              d2 = d1;
              if (i != 0)
              {
                if (localEmbryoSupervisor.getForegroundTime() <= l) {
                  break label279;
                }
                d1 = l / localEmbryoSupervisor.getForegroundTime();
                break label279;
              }
              JSONObject localJSONObject = new JSONObject();
              localJSONObject.put("fg", (localEmbryoSupervisor.getForegroundTime() * d2));
              ((JSONObject)???).put(localEmbryoSupervisor.getPackageName(), localJSONObject);
              localEmbryoSupervisor.dump();
              i = j;
              d1 = d2;
              continue;
              localObject4 = finally;
            }
          }
          catch (Exception localException)
          {
            Log.e("Embryo_Uterus", "Embryo flush failed", localException);
            return;
          }
        }
        FileUtils.stringToFile("/data/system/embryo", ((JSONObject)localObject2).toString());
      }
      finally {}
      continue;
      label279:
      int j = 0;
      double d2 = d1;
    }
  }
  
  public static Uterus getInstance()
  {
    if (sInstance == null) {
      sInstance = new Uterus();
    }
    return sInstance;
  }
  
  /* Error */
  public boolean attach(android.app.IApplicationThread arg1, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 80	com/android/server/am/Uterus:mWaitingForAttach	Ljava/util/Map;
    //   4: astore_3
    //   5: aload_3
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 80	com/android/server/am/Uterus:mWaitingForAttach	Ljava/util/Map;
    //   11: iload_2
    //   12: invokestatic 269	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   15: invokeinterface 273 2 0
    //   20: checkcast 275	com/android/server/am/Embryo
    //   23: astore 4
    //   25: aload_3
    //   26: monitorexit
    //   27: aload 4
    //   29: ifnonnull +10 -> 39
    //   32: iconst_0
    //   33: ireturn
    //   34: astore_1
    //   35: aload_3
    //   36: monitorexit
    //   37: aload_1
    //   38: athrow
    //   39: aload_0
    //   40: aload 4
    //   42: invokevirtual 276	com/android/server/am/Embryo:getPackageName	()Ljava/lang/String;
    //   45: invokevirtual 280	com/android/server/am/Uterus:findSupervisor	(Ljava/lang/String;)Lcom/android/server/am/EmbryoSupervisor;
    //   48: astore_3
    //   49: aload_3
    //   50: ifnonnull +5 -> 55
    //   53: iconst_0
    //   54: ireturn
    //   55: aload_3
    //   56: monitorenter
    //   57: aload_3
    //   58: invokevirtual 283	com/android/server/am/EmbryoSupervisor:needAbortion	()Z
    //   61: ifeq +38 -> 99
    //   64: iload_2
    //   65: invokestatic 288	android/os/Process:killProcessQuiet	(I)V
    //   68: ldc 34
    //   70: new 290	java/lang/StringBuilder
    //   73: dup
    //   74: invokespecial 291	java/lang/StringBuilder:<init>	()V
    //   77: ldc_w 293
    //   80: invokevirtual 297	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: iload_2
    //   84: invokevirtual 300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   87: invokevirtual 301	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   90: invokestatic 169	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   93: pop
    //   94: aload_3
    //   95: iconst_0
    //   96: invokevirtual 305	com/android/server/am/EmbryoSupervisor:setWaitingForFork	(Z)V
    //   99: aload_3
    //   100: monitorexit
    //   101: iload_2
    //   102: iconst_0
    //   103: invokestatic 309	android/os/Process:setProcessGroup	(II)V
    //   106: aload 4
    //   108: invokevirtual 313	com/android/server/am/Embryo:getInfo	()Landroid/content/pm/ApplicationInfo;
    //   111: astore 5
    //   113: aload_1
    //   114: aload 5
    //   116: aload_0
    //   117: getfield 69	com/android/server/am/Uterus:mHelper	Lcom/android/server/am/EmbryoHelper;
    //   120: aload 5
    //   122: invokevirtual 317	com/android/server/am/EmbryoHelper:compatibilityInfoForPackageLocked	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/CompatibilityInfo;
    //   125: aload_0
    //   126: getfield 69	com/android/server/am/Uterus:mHelper	Lcom/android/server/am/EmbryoHelper;
    //   129: invokevirtual 321	com/android/server/am/EmbryoHelper:getConfiguration	()Landroid/content/res/Configuration;
    //   132: aload_0
    //   133: getfield 69	com/android/server/am/Uterus:mHelper	Lcom/android/server/am/EmbryoHelper;
    //   136: iconst_0
    //   137: invokevirtual 325	com/android/server/am/EmbryoHelper:getCommonServicesLocked	(Z)Ljava/util/HashMap;
    //   140: invokeinterface 331 5 0
    //   145: aload_3
    //   146: monitorenter
    //   147: aload 4
    //   149: aload_1
    //   150: invokevirtual 335	com/android/server/am/Embryo:setThread	(Landroid/app/IApplicationThread;)V
    //   153: aload_3
    //   154: aload 4
    //   156: invokevirtual 338	com/android/server/am/EmbryoSupervisor:attach	(Lcom/android/server/am/Embryo;)V
    //   159: aload_3
    //   160: iconst_0
    //   161: invokevirtual 305	com/android/server/am/EmbryoSupervisor:setWaitingForFork	(Z)V
    //   164: aload_3
    //   165: monitorexit
    //   166: aload_0
    //   167: getfield 76	com/android/server/am/Uterus:mSupervisors	Ljava/util/List;
    //   170: astore_1
    //   171: aload_1
    //   172: monitorenter
    //   173: aload_0
    //   174: getfield 76	com/android/server/am/Uterus:mSupervisors	Ljava/util/List;
    //   177: aload_3
    //   178: invokeinterface 341 2 0
    //   183: ifne +14 -> 197
    //   186: aload_0
    //   187: getfield 76	com/android/server/am/Uterus:mSupervisors	Ljava/util/List;
    //   190: aload_3
    //   191: invokeinterface 344 2 0
    //   196: pop
    //   197: aload_1
    //   198: monitorexit
    //   199: iconst_1
    //   200: ireturn
    //   201: astore_1
    //   202: aload_3
    //   203: monitorexit
    //   204: aload_1
    //   205: athrow
    //   206: astore_1
    //   207: aload_3
    //   208: monitorexit
    //   209: aload_1
    //   210: athrow
    //   211: astore_1
    //   212: aload 4
    //   214: invokevirtual 347	com/android/server/am/Embryo:destroy	()V
    //   217: aload_3
    //   218: monitorenter
    //   219: aload_3
    //   220: iconst_0
    //   221: invokevirtual 305	com/android/server/am/EmbryoSupervisor:setWaitingForFork	(Z)V
    //   224: aload_3
    //   225: monitorexit
    //   226: iconst_0
    //   227: ireturn
    //   228: astore 5
    //   230: aload_1
    //   231: monitorexit
    //   232: aload 5
    //   234: athrow
    //   235: astore_1
    //   236: aload_3
    //   237: monitorexit
    //   238: aload_1
    //   239: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	240	0	this	Uterus
    //   0	240	2	paramInt	int
    //   4	233	3	localObject1	Object
    //   23	190	4	localEmbryo	Embryo
    //   111	10	5	localApplicationInfo	ApplicationInfo
    //   228	5	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   7	25	34	finally
    //   57	99	201	finally
    //   147	164	206	finally
    //   101	147	211	java/lang/Exception
    //   164	173	211	java/lang/Exception
    //   197	199	211	java/lang/Exception
    //   207	211	211	java/lang/Exception
    //   230	235	211	java/lang/Exception
    //   173	197	228	finally
    //   219	224	235	finally
  }
  
  public void cleanup()
  {
    this.mThread.quit();
    synchronized (this.mSupervisors)
    {
      Iterator localIterator = this.mSupervisors.iterator();
      if (localIterator.hasNext()) {
        ((EmbryoSupervisor)localIterator.next()).destroy();
      }
    }
    this.mSupervisors.clear();
    synchronized (this.mAllSupervisorMap)
    {
      this.mAllSupervisorMap.clear();
      synchronized (this.mWaitingForAttach)
      {
        this.mWaitingForAttach.clear();
      }
    }
    synchronized (this.mLRUHistory)
    {
      this.mLRUHistory.clear();
      sInstance = null;
      return;
      localObject3 = finally;
      throw ((Throwable)localObject3);
      localObject4 = finally;
      throw ((Throwable)localObject4);
    }
  }
  
  public void dumpsys(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("max_count:" + MAX);
    Object localObject3;
    synchronized (this.mWaitingForAttach)
    {
      paramPrintWriter.println("waiting_for_attach:" + this.mWaitingForAttach.size());
      if (this.mWaitingForAttach.size() > 0)
      {
        localObject2 = this.mWaitingForAttach.entrySet().iterator();
        if (((Iterator)localObject2).hasNext())
        {
          localObject3 = (Map.Entry)((Iterator)localObject2).next();
          Embryo localEmbryo = (Embryo)((Map.Entry)localObject3).getValue();
          paramPrintWriter.println("waiting pid " + ((Map.Entry)localObject3).getKey() + " " + localEmbryo.getPackageName());
        }
      }
    }
    Object localObject2 = new EmbryoMemory();
    synchronized (this.mSupervisors)
    {
      paramPrintWriter.println("supervisors:" + this.mSupervisors.size());
      localObject3 = this.mSupervisors.iterator();
      if (((Iterator)localObject3).hasNext()) {
        ((EmbryoSupervisor)((Iterator)localObject3).next()).dump(paramPrintWriter, (EmbryoMemory)localObject2);
      }
    }
    if (((EmbryoMemory)localObject2).count == 0) {
      return;
    }
    paramPrintWriter.println("Total Memory Usage: PSS " + ((EmbryoMemory)localObject2).pss + "KB, USS " + ((EmbryoMemory)localObject2).uss + "KB");
    paramPrintWriter.println("Avg. Memory Usage: PSS " + ((EmbryoMemory)localObject2).pss / ((EmbryoMemory)localObject2).count + "KB, USS " + ((EmbryoMemory)localObject2).uss / ((EmbryoMemory)localObject2).count + "KB");
  }
  
  public EmbryoSupervisor findSupervisor(String paramString)
  {
    synchronized (this.mAllSupervisorMap)
    {
      paramString = (EmbryoSupervisor)this.mAllSupervisorMap.get(paramString);
      return paramString;
    }
  }
  
  public void finish(EmbryoSupervisor paramEmbryoSupervisor)
  {
    synchronized (this.mSupervisors)
    {
      this.mSupervisors.remove(paramEmbryoSupervisor);
      return;
    }
  }
  
  public EmbryoSupervisor getOrCreateSupervisor(String paramString)
  {
    synchronized (mBlackList)
    {
      boolean bool = mBlackList.contains(paramString);
      if (bool) {
        return null;
      }
    }
    synchronized (this.mAllSupervisorMap)
    {
      EmbryoSupervisor localEmbryoSupervisor = (EmbryoSupervisor)this.mAllSupervisorMap.get(paramString);
      ??? = localEmbryoSupervisor;
      if (localEmbryoSupervisor == null)
      {
        ??? = new EmbryoSupervisor(paramString);
        this.mAllSupervisorMap.put(paramString, ???);
      }
      return (EmbryoSupervisor)???;
      paramString = finally;
      throw paramString;
    }
  }
  
  public void goingToSleep()
  {
    if (this.mCurrentSupervisor != null) {}
    synchronized (this.mCurrentSupervisor)
    {
      this.mCurrentSupervisor.stop();
      this.mCurrentSupervisor = null;
      return;
    }
  }
  
  public void initiate()
  {
    if (!new File("/data/system/embryo").exists()) {
      return;
    }
    this.mHandler.postDelayed(new InitiateRunnable(null), 10000L);
  }
  
  public void prepare(EmbryoSupervisor paramEmbryoSupervisor, int paramInt, boolean paramBoolean)
  {
    try
    {
      if (paramEmbryoSupervisor.isLaunchable())
      {
        localApplicationInfo = paramEmbryoSupervisor.getInfo();
        if (localApplicationInfo != null) {
          break label23;
        }
      }
      label23:
      while ((paramEmbryoSupervisor.hasEmbryo()) || (paramEmbryoSupervisor.isWaitingForFork())) {
        return;
      }
      paramEmbryoSupervisor.setWaitingForFork(true);
      ApplicationInfo localApplicationInfo = paramEmbryoSupervisor.getInfo();
      paramEmbryoSupervisor = new BirthRunnable(paramEmbryoSupervisor, localApplicationInfo, null);
      paramEmbryoSupervisor.setCheckProcess(paramBoolean);
      if (paramInt > 0)
      {
        this.mHandler.postDelayed(paramEmbryoSupervisor, paramInt * 1000);
        return;
      }
    }
    finally {}
    this.mHandler.postAtFrontOfQueue(paramEmbryoSupervisor);
  }
  
  /* Error */
  public void resume(String arg1, boolean paramBoolean)
  {
    // Byte code:
    //   0: iload_2
    //   1: ifeq +8 -> 9
    //   4: aload_0
    //   5: aload_1
    //   6: putfield 220	com/android/server/am/Uterus:mHomePackageName	Ljava/lang/String;
    //   9: aload_0
    //   10: aload_1
    //   11: invokevirtual 474	com/android/server/am/Uterus:getOrCreateSupervisor	(Ljava/lang/String;)Lcom/android/server/am/EmbryoSupervisor;
    //   14: astore_1
    //   15: aload_1
    //   16: aload_0
    //   17: getfield 429	com/android/server/am/Uterus:mCurrentSupervisor	Lcom/android/server/am/EmbryoSupervisor;
    //   20: if_acmpne +4 -> 24
    //   23: return
    //   24: aload_0
    //   25: getfield 429	com/android/server/am/Uterus:mCurrentSupervisor	Lcom/android/server/am/EmbryoSupervisor;
    //   28: ifnull +19 -> 47
    //   31: aload_0
    //   32: getfield 429	com/android/server/am/Uterus:mCurrentSupervisor	Lcom/android/server/am/EmbryoSupervisor;
    //   35: astore_3
    //   36: aload_3
    //   37: monitorenter
    //   38: aload_0
    //   39: getfield 429	com/android/server/am/Uterus:mCurrentSupervisor	Lcom/android/server/am/EmbryoSupervisor;
    //   42: invokevirtual 432	com/android/server/am/EmbryoSupervisor:stop	()V
    //   45: aload_3
    //   46: monitorexit
    //   47: aload_1
    //   48: ifnonnull +14 -> 62
    //   51: aload_0
    //   52: aconst_null
    //   53: putfield 429	com/android/server/am/Uterus:mCurrentSupervisor	Lcom/android/server/am/EmbryoSupervisor;
    //   56: return
    //   57: astore_1
    //   58: aload_3
    //   59: monitorexit
    //   60: aload_1
    //   61: athrow
    //   62: aload_1
    //   63: monitorenter
    //   64: aload_0
    //   65: aload_1
    //   66: putfield 429	com/android/server/am/Uterus:mCurrentSupervisor	Lcom/android/server/am/EmbryoSupervisor;
    //   69: aload_0
    //   70: getfield 429	com/android/server/am/Uterus:mCurrentSupervisor	Lcom/android/server/am/EmbryoSupervisor;
    //   73: invokevirtual 475	com/android/server/am/EmbryoSupervisor:start	()V
    //   76: aload_1
    //   77: monitorexit
    //   78: aload_0
    //   79: getfield 73	com/android/server/am/Uterus:mLRUHistory	Ljava/util/List;
    //   82: astore_1
    //   83: aload_1
    //   84: monitorenter
    //   85: aload_0
    //   86: getfield 73	com/android/server/am/Uterus:mLRUHistory	Ljava/util/List;
    //   89: aload_0
    //   90: getfield 429	com/android/server/am/Uterus:mCurrentSupervisor	Lcom/android/server/am/EmbryoSupervisor;
    //   93: invokeinterface 420 2 0
    //   98: pop
    //   99: aload_0
    //   100: getfield 73	com/android/server/am/Uterus:mLRUHistory	Ljava/util/List;
    //   103: aload_0
    //   104: getfield 429	com/android/server/am/Uterus:mCurrentSupervisor	Lcom/android/server/am/EmbryoSupervisor;
    //   107: invokeinterface 344 2 0
    //   112: pop
    //   113: aload_1
    //   114: monitorexit
    //   115: return
    //   116: astore_3
    //   117: aload_1
    //   118: monitorexit
    //   119: aload_3
    //   120: athrow
    //   121: astore_3
    //   122: aload_1
    //   123: monitorexit
    //   124: aload_3
    //   125: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	Uterus
    //   0	126	2	paramBoolean	boolean
    //   35	24	3	localEmbryoSupervisor	EmbryoSupervisor
    //   116	4	3	localObject1	Object
    //   121	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   38	45	57	finally
    //   64	76	116	finally
    //   85	113	121	finally
  }
  
  public void scheduleBackup()
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        Uterus.-wrap0(Uterus.this);
      }
    });
  }
  
  public void setBlackList(List arg1)
  {
    Object localObject1 = new HashSet(???);
    Object localObject3;
    synchronized (mBlackList)
    {
      localObject3 = mBlackList.iterator();
      if (((Iterator)localObject3).hasNext()) {
        ((Set)localObject1).remove((String)((Iterator)localObject3).next());
      }
    }
    mBlackList.clear();
    mBlackList.addAll(???);
    if (((Set)localObject1).isEmpty()) {
      return;
    }
    ??? = new ArrayList();
    synchronized (this.mAllSupervisorMap)
    {
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject3 = (String)((Iterator)localObject1).next();
        localObject3 = (EmbryoSupervisor)this.mAllSupervisorMap.remove(localObject3);
        if (localObject3 != null) {
          ???.add(localObject3);
        }
      }
    }
    if (???.isEmpty()) {
      return;
    }
    ??? = ???.iterator();
    while (???.hasNext())
    {
      EmbryoSupervisor localEmbryoSupervisor = (EmbryoSupervisor)???.next();
      if (localEmbryoSupervisor.isWaitingForFork()) {
        localEmbryoSupervisor.setAbortion();
      } else {
        localEmbryoSupervisor.destroy();
      }
    }
  }
  
  public void shutdown()
  {
    flushToStorage();
  }
  
  public void trim()
  {
    this.mHandler.removeCallbacks(this.mTrimRunnable);
    this.mHandler.postDelayed(this.mTrimRunnable, 10000L);
  }
  
  public void updateConfig()
  {
    this.mHandler.removeCallbacks(this.mConfigUpdateRunnable);
    this.mHandler.post(this.mConfigUpdateRunnable);
  }
  
  private final class BirthRunnable
    implements Runnable
  {
    private boolean checkProcess = false;
    private final ApplicationInfo info;
    private EmbryoSupervisor supervisor;
    
    private BirthRunnable(EmbryoSupervisor paramEmbryoSupervisor, ApplicationInfo paramApplicationInfo)
    {
      this.supervisor = paramEmbryoSupervisor;
      this.info = paramApplicationInfo;
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 26	com/android/server/am/Uterus$BirthRunnable:checkProcess	Z
      //   4: ifeq +43 -> 47
      //   7: aload_0
      //   8: getfield 21	com/android/server/am/Uterus$BirthRunnable:this$0	Lcom/android/server/am/Uterus;
      //   11: invokestatic 41	com/android/server/am/Uterus:-get1	(Lcom/android/server/am/Uterus;)Lcom/android/server/am/EmbryoHelper;
      //   14: aload_0
      //   15: getfield 30	com/android/server/am/Uterus$BirthRunnable:info	Landroid/content/pm/ApplicationInfo;
      //   18: invokevirtual 47	com/android/server/am/EmbryoHelper:checkIfProcessExist	(Landroid/content/pm/ApplicationInfo;)Z
      //   21: ifeq +26 -> 47
      //   24: aload_0
      //   25: getfield 28	com/android/server/am/Uterus$BirthRunnable:supervisor	Lcom/android/server/am/EmbryoSupervisor;
      //   28: astore_1
      //   29: aload_1
      //   30: monitorenter
      //   31: aload_0
      //   32: getfield 28	com/android/server/am/Uterus$BirthRunnable:supervisor	Lcom/android/server/am/EmbryoSupervisor;
      //   35: iconst_0
      //   36: invokevirtual 53	com/android/server/am/EmbryoSupervisor:setWaitingForFork	(Z)V
      //   39: aload_1
      //   40: monitorexit
      //   41: return
      //   42: astore_2
      //   43: aload_1
      //   44: monitorexit
      //   45: aload_2
      //   46: athrow
      //   47: new 55	com/android/server/am/ProcessRecord
      //   50: dup
      //   51: aconst_null
      //   52: aload_0
      //   53: getfield 30	com/android/server/am/Uterus$BirthRunnable:info	Landroid/content/pm/ApplicationInfo;
      //   56: aload_0
      //   57: getfield 30	com/android/server/am/Uterus$BirthRunnable:info	Landroid/content/pm/ApplicationInfo;
      //   60: getfield 61	android/content/pm/ApplicationInfo:processName	Ljava/lang/String;
      //   63: aload_0
      //   64: getfield 30	com/android/server/am/Uterus$BirthRunnable:info	Landroid/content/pm/ApplicationInfo;
      //   67: getfield 65	android/content/pm/ApplicationInfo:uid	I
      //   70: invokespecial 68	com/android/server/am/ProcessRecord:<init>	(Lcom/android/internal/os/BatteryStatsImpl;Landroid/content/pm/ApplicationInfo;Ljava/lang/String;I)V
      //   73: astore_2
      //   74: aload_0
      //   75: getfield 21	com/android/server/am/Uterus$BirthRunnable:this$0	Lcom/android/server/am/Uterus;
      //   78: invokestatic 41	com/android/server/am/Uterus:-get1	(Lcom/android/server/am/Uterus;)Lcom/android/server/am/EmbryoHelper;
      //   81: aload_2
      //   82: ldc 70
      //   84: aload_2
      //   85: getfield 71	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
      //   88: invokevirtual 75	com/android/server/am/EmbryoHelper:startProcessLocked	(Lcom/android/server/am/ProcessRecord;Ljava/lang/String;Ljava/lang/String;)V
      //   91: aload_2
      //   92: getfield 78	com/android/server/am/ProcessRecord:pid	I
      //   95: ifne +113 -> 208
      //   98: ldc 80
      //   100: new 82	java/lang/StringBuilder
      //   103: dup
      //   104: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   107: ldc 85
      //   109: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   112: aload_0
      //   113: getfield 28	com/android/server/am/Uterus$BirthRunnable:supervisor	Lcom/android/server/am/EmbryoSupervisor;
      //   116: invokevirtual 93	com/android/server/am/EmbryoSupervisor:getPackageName	()Ljava/lang/String;
      //   119: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   122: ldc 95
      //   124: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   127: invokevirtual 98	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   130: invokestatic 104	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   133: pop
      //   134: aload_0
      //   135: getfield 28	com/android/server/am/Uterus$BirthRunnable:supervisor	Lcom/android/server/am/EmbryoSupervisor;
      //   138: astore_1
      //   139: aload_1
      //   140: monitorenter
      //   141: aload_0
      //   142: getfield 28	com/android/server/am/Uterus$BirthRunnable:supervisor	Lcom/android/server/am/EmbryoSupervisor;
      //   145: iconst_0
      //   146: invokevirtual 53	com/android/server/am/EmbryoSupervisor:setWaitingForFork	(Z)V
      //   149: aload_1
      //   150: monitorexit
      //   151: return
      //   152: astore_2
      //   153: aload_1
      //   154: monitorexit
      //   155: aload_2
      //   156: athrow
      //   157: astore_2
      //   158: aload_0
      //   159: getfield 28	com/android/server/am/Uterus$BirthRunnable:supervisor	Lcom/android/server/am/EmbryoSupervisor;
      //   162: astore_1
      //   163: aload_1
      //   164: monitorenter
      //   165: aload_0
      //   166: getfield 28	com/android/server/am/Uterus$BirthRunnable:supervisor	Lcom/android/server/am/EmbryoSupervisor;
      //   169: iconst_0
      //   170: invokevirtual 53	com/android/server/am/EmbryoSupervisor:setWaitingForFork	(Z)V
      //   173: aload_1
      //   174: monitorexit
      //   175: ldc 80
      //   177: new 82	java/lang/StringBuilder
      //   180: dup
      //   181: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   184: ldc 85
      //   186: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   189: aload_0
      //   190: getfield 28	com/android/server/am/Uterus$BirthRunnable:supervisor	Lcom/android/server/am/EmbryoSupervisor;
      //   193: invokevirtual 93	com/android/server/am/EmbryoSupervisor:getPackageName	()Ljava/lang/String;
      //   196: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   199: invokevirtual 98	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   202: aload_2
      //   203: invokestatic 107	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   206: pop
      //   207: return
      //   208: new 109	com/android/server/am/Embryo
      //   211: dup
      //   212: aload_0
      //   213: getfield 30	com/android/server/am/Uterus$BirthRunnable:info	Landroid/content/pm/ApplicationInfo;
      //   216: getfield 112	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
      //   219: aload_0
      //   220: getfield 30	com/android/server/am/Uterus$BirthRunnable:info	Landroid/content/pm/ApplicationInfo;
      //   223: invokespecial 115	com/android/server/am/Embryo:<init>	(Ljava/lang/String;Landroid/content/pm/ApplicationInfo;)V
      //   226: astore_1
      //   227: aload_1
      //   228: aload_2
      //   229: getfield 78	com/android/server/am/ProcessRecord:pid	I
      //   232: invokevirtual 119	com/android/server/am/Embryo:setPid	(I)V
      //   235: aload_0
      //   236: getfield 21	com/android/server/am/Uterus$BirthRunnable:this$0	Lcom/android/server/am/Uterus;
      //   239: invokestatic 123	com/android/server/am/Uterus:-get4	(Lcom/android/server/am/Uterus;)Ljava/util/Map;
      //   242: astore_2
      //   243: aload_2
      //   244: monitorenter
      //   245: aload_0
      //   246: getfield 21	com/android/server/am/Uterus$BirthRunnable:this$0	Lcom/android/server/am/Uterus;
      //   249: invokestatic 123	com/android/server/am/Uterus:-get4	(Lcom/android/server/am/Uterus;)Ljava/util/Map;
      //   252: aload_1
      //   253: invokevirtual 127	com/android/server/am/Embryo:getPid	()I
      //   256: invokestatic 133	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   259: aload_1
      //   260: invokeinterface 139 3 0
      //   265: pop
      //   266: aload_2
      //   267: monitorexit
      //   268: ldc 80
      //   270: new 82	java/lang/StringBuilder
      //   273: dup
      //   274: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   277: ldc -115
      //   279: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   282: aload_0
      //   283: getfield 30	com/android/server/am/Uterus$BirthRunnable:info	Landroid/content/pm/ApplicationInfo;
      //   286: getfield 112	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
      //   289: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   292: ldc -113
      //   294: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   297: aload_1
      //   298: invokevirtual 127	com/android/server/am/Embryo:getPid	()I
      //   301: invokevirtual 146	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   304: invokevirtual 98	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   307: invokestatic 104	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   310: pop
      //   311: return
      //   312: astore_1
      //   313: aload_2
      //   314: monitorexit
      //   315: aload_1
      //   316: athrow
      //   317: astore_2
      //   318: aload_1
      //   319: monitorexit
      //   320: aload_2
      //   321: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	322	0	this	BirthRunnable
      //   312	7	1	localObject2	Object
      //   42	4	2	localObject3	Object
      //   73	19	2	localProcessRecord	ProcessRecord
      //   152	4	2	localObject4	Object
      //   157	72	2	localException	Exception
      //   317	4	2	localObject5	Object
      // Exception table:
      //   from	to	target	type
      //   31	39	42	finally
      //   141	149	152	finally
      //   47	141	157	java/lang/Exception
      //   149	151	157	java/lang/Exception
      //   153	157	157	java/lang/Exception
      //   208	245	157	java/lang/Exception
      //   266	311	157	java/lang/Exception
      //   313	317	157	java/lang/Exception
      //   245	266	312	finally
      //   165	173	317	finally
    }
    
    void setCheckProcess(boolean paramBoolean)
    {
      this.checkProcess = paramBoolean;
    }
  }
  
  static final class EmbryoMemory
  {
    int count = 0;
    long pss = 0L;
    long uss = 0L;
    
    void update(Debug.MemoryInfo paramMemoryInfo)
    {
      this.pss += paramMemoryInfo.getTotalPss();
      this.uss += paramMemoryInfo.getTotalUss();
      this.count += 1;
    }
  }
  
  private final class InitiateRunnable
    implements Runnable
  {
    private InitiateRunnable() {}
    
    public void run()
    {
      int m = UserHandle.getCallingUserId();
      try
      {
        JSONObject localJSONObject = new JSONObject(FileUtils.readTextFile(new File("/data/system/embryo"), 0, null));
        Iterator localIterator = localJSONObject.keys();
        int i = 0;
        int j = 5;
        while (localIterator.hasNext())
        {
          Object localObject = (String)localIterator.next();
          if (Uterus.-get1(Uterus.this).isPackageAvailable((String)localObject, m))
          {
            EmbryoSupervisor localEmbryoSupervisor = Uterus.this.getOrCreateSupervisor((String)localObject);
            if (localEmbryoSupervisor != null)
            {
              localEmbryoSupervisor.restoreForegroundTime(localJSONObject.getJSONObject((String)localObject).getLong("fg"));
              if ((!Uterus.-get1(Uterus.this).checkIfProcessExist(localEmbryoSupervisor.getInfo())) && (localEmbryoSupervisor.isLaunchable()))
              {
                int k = i + 1;
                i = k;
                if (k < Uterus.-get0())
                {
                  localObject = Uterus.this;
                  j += 1;
                  ((Uterus)localObject).prepare(localEmbryoSupervisor, j, true);
                  i = k;
                }
              }
            }
          }
        }
        return;
      }
      catch (Exception localException)
      {
        Log.d("Embryo_Uterus", "Embryo initiate failed", localException);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/Uterus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */