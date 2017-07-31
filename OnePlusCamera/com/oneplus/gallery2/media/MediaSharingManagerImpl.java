package com.oneplus.gallery2.media;

import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.base.SimpleRef;
import com.oneplus.base.component.BasicComponent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class MediaSharingManagerImpl
  extends BasicComponent
  implements MediaSharingManager
{
  private static final String CACHED_DIR_NAME = "sharing_caches";
  private static final String CONTENT_URI_PREFIX = "content://oneplus.gallery/share/";
  private static final long DURATION_FILE_INFO_EXPIRED_TIME_MILLIS = 86400000L;
  private static final Object GET_FILE_INFO_LOCK = new Object();
  private static final String META_SUFFIX = "_meta";
  private static final int MSG_ON_PREPARED = 10005;
  private static final int MSG_WORKER_CHECK_EXPIRED_FILE_INFOS = 10011;
  private static final int MSG_WORKER_CLEAR_SHARING_CACHES = 10015;
  private static final int MSG_WORKER_READ_FILE_INFOS = 10010;
  private volatile boolean m_IsFileInfosReady;
  private volatile Map<String, MediaSharingManager.FileInfo> m_PreparedFileInfos = new HashMap();
  private Handler m_WorkerHandler;
  private HandlerThread m_WorkerThread;
  
  MediaSharingManagerImpl(BaseApplication paramBaseApplication)
  {
    super("Media Sharing Manager", paramBaseApplication, true);
  }
  
  private void checkExpiredFileInfos()
  {
    if (Thread.currentThread() != this.m_WorkerThread)
    {
      Message.obtain(this.m_WorkerHandler, 10011).sendToTarget();
      return;
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject4;
    for (;;)
    {
      synchronized (this.m_PreparedFileInfos)
      {
        localObject3 = new ArrayList();
        localObject4 = this.m_PreparedFileInfos.keySet().iterator();
        if (!((Iterator)localObject4).hasNext()) {
          break;
        }
        String str = (String)((Iterator)localObject4).next();
        MediaSharingManager.FileInfo localFileInfo2 = (MediaSharingManager.FileInfo)this.m_PreparedFileInfos.get(str);
        if (SystemClock.elapsedRealtime() - localFileInfo2.creationTime <= 86400000L)
        {
          i = 1;
          if (i != 0) {
            continue;
          }
          ((List)localObject3).add(str);
          localArrayList.add(localFileInfo2);
        }
      }
      int i = 0;
    }
    Object localObject3 = ((List)localObject3).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (String)((Iterator)localObject3).next();
      this.m_PreparedFileInfos.remove(localObject4);
    }
    ??? = ((List)localObject2).iterator();
    while (((Iterator)???).hasNext())
    {
      MediaSharingManager.FileInfo localFileInfo1 = (MediaSharingManager.FileInfo)((Iterator)???).next();
      localFileInfo1.file.delete();
      localFileInfo1.meta.delete();
      Log.v(this.TAG, "checkExpiredFileInfos() - Delete: " + localFileInfo1.file.getName());
    }
    ??? = Message.obtain(this.m_WorkerHandler, 10011);
    this.m_WorkerHandler.sendMessageDelayed((Message)???, 86400000L);
  }
  
  private void handleWorkerMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    case 10012: 
    case 10013: 
    case 10014: 
    default: 
      return;
    case 10011: 
      checkExpiredFileInfos();
      return;
    case 10015: 
      clearSharingCaches();
      return;
    }
    readFileInfos();
  }
  
  private void onPrepared(PrepareSharingCallback paramPrepareSharingCallback, Media paramMedia, Uri paramUri, String paramString, PrepareSharingResult paramPrepareSharingResult)
  {
    if (paramPrepareSharingCallback == null) {
      return;
    }
    paramPrepareSharingCallback.onPrepared(paramMedia, paramUri, paramString, paramPrepareSharingResult);
  }
  
  /* Error */
  private void readFileInfos()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: getfield 216	com/oneplus/gallery2/media/MediaSharingManagerImpl:m_IsFileInfosReady	Z
    //   6: ifne +27 -> 33
    //   9: invokestatic 87	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   12: aload_0
    //   13: getfield 89	com/oneplus/gallery2/media/MediaSharingManagerImpl:m_WorkerThread	Landroid/os/HandlerThread;
    //   16: if_acmpeq +18 -> 34
    //   19: aload_0
    //   20: getfield 91	com/oneplus/gallery2/media/MediaSharingManagerImpl:m_WorkerHandler	Landroid/os/Handler;
    //   23: sipush 10010
    //   26: invokestatic 97	android/os/Message:obtain	(Landroid/os/Handler;I)Landroid/os/Message;
    //   29: invokevirtual 100	android/os/Message:sendToTarget	()V
    //   32: return
    //   33: return
    //   34: aload_0
    //   35: getfield 80	com/oneplus/gallery2/media/MediaSharingManagerImpl:TAG	Ljava/lang/String;
    //   38: ldc -38
    //   40: invokestatic 188	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   43: invokestatic 224	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
    //   46: ldc 16
    //   48: iconst_0
    //   49: invokevirtual 228	com/oneplus/base/BaseApplication:getDir	(Ljava/lang/String;I)Ljava/io/File;
    //   52: astore 7
    //   54: aload 7
    //   56: invokevirtual 232	java/io/File:list	()[Ljava/lang/String;
    //   59: astore 4
    //   61: new 234	java/util/HashSet
    //   64: dup
    //   65: invokespecial 235	java/util/HashSet:<init>	()V
    //   68: astore 6
    //   70: new 234	java/util/HashSet
    //   73: dup
    //   74: invokespecial 235	java/util/HashSet:<init>	()V
    //   77: astore 5
    //   79: new 234	java/util/HashSet
    //   82: dup
    //   83: invokespecial 235	java/util/HashSet:<init>	()V
    //   86: astore_3
    //   87: aload 4
    //   89: arraylength
    //   90: istore_2
    //   91: iload_1
    //   92: iload_2
    //   93: if_icmpge +168 -> 261
    //   96: aload 4
    //   98: iload_1
    //   99: aaload
    //   100: astore 8
    //   102: aload 8
    //   104: ldc 28
    //   106: invokevirtual 239	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   109: ifne +28 -> 137
    //   112: aload 5
    //   114: aload 8
    //   116: invokeinterface 241 2 0
    //   121: ifne +128 -> 249
    //   124: aload 6
    //   126: aload 8
    //   128: invokeinterface 242 2 0
    //   133: pop
    //   134: goto +601 -> 735
    //   137: aload 8
    //   139: iconst_0
    //   140: aload 8
    //   142: invokevirtual 246	java/lang/String:length	()I
    //   145: ldc 28
    //   147: invokevirtual 246	java/lang/String:length	()I
    //   150: isub
    //   151: invokevirtual 250	java/lang/String:substring	(II)Ljava/lang/String;
    //   154: astore 8
    //   156: aload 6
    //   158: aload 8
    //   160: invokeinterface 241 2 0
    //   165: ifne +72 -> 237
    //   168: aload 5
    //   170: aload 8
    //   172: invokeinterface 242 2 0
    //   177: pop
    //   178: goto +557 -> 735
    //   181: astore_3
    //   182: aload_0
    //   183: getfield 80	com/oneplus/gallery2/media/MediaSharingManagerImpl:TAG	Ljava/lang/String;
    //   186: ldc -4
    //   188: aload_3
    //   189: invokestatic 256	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   192: getstatic 55	com/oneplus/gallery2/media/MediaSharingManagerImpl:GET_FILE_INFO_LOCK	Ljava/lang/Object;
    //   195: astore_3
    //   196: aload_3
    //   197: monitorenter
    //   198: aload_0
    //   199: iconst_1
    //   200: putfield 216	com/oneplus/gallery2/media/MediaSharingManagerImpl:m_IsFileInfosReady	Z
    //   203: getstatic 55	com/oneplus/gallery2/media/MediaSharingManagerImpl:GET_FILE_INFO_LOCK	Ljava/lang/Object;
    //   206: invokevirtual 259	java/lang/Object:notifyAll	()V
    //   209: aload_3
    //   210: monitorexit
    //   211: aload_0
    //   212: getfield 80	com/oneplus/gallery2/media/MediaSharingManagerImpl:TAG	Ljava/lang/String;
    //   215: ldc_w 261
    //   218: aload_0
    //   219: getfield 67	com/oneplus/gallery2/media/MediaSharingManagerImpl:m_PreparedFileInfos	Ljava/util/Map;
    //   222: invokeinterface 264 1 0
    //   227: invokestatic 270	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   230: ldc_w 272
    //   233: invokestatic 275	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   236: return
    //   237: aload_3
    //   238: aload 8
    //   240: invokeinterface 242 2 0
    //   245: pop
    //   246: goto +489 -> 735
    //   249: aload_3
    //   250: aload 8
    //   252: invokeinterface 242 2 0
    //   257: pop
    //   258: goto +477 -> 735
    //   261: new 64	java/util/HashMap
    //   264: dup
    //   265: invokespecial 65	java/util/HashMap:<init>	()V
    //   268: astore 8
    //   270: aload_3
    //   271: invokeinterface 115 1 0
    //   276: astore 9
    //   278: aload 9
    //   280: invokeinterface 121 1 0
    //   285: ifeq +275 -> 560
    //   288: aload 9
    //   290: invokeinterface 125 1 0
    //   295: checkcast 127	java/lang/String
    //   298: astore_3
    //   299: new 166	java/lang/StringBuilder
    //   302: dup
    //   303: invokespecial 276	java/lang/StringBuilder:<init>	()V
    //   306: aload 7
    //   308: invokevirtual 279	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   311: ldc_w 281
    //   314: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   317: aload_3
    //   318: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   321: ldc 28
    //   323: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   326: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   329: astore 10
    //   331: new 133	com/oneplus/gallery2/media/MediaSharingManager$FileInfo
    //   334: dup
    //   335: invokespecial 282	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:<init>	()V
    //   338: astore 4
    //   340: new 284	java/io/ObjectInputStream
    //   343: dup
    //   344: new 286	java/io/FileInputStream
    //   347: dup
    //   348: aload 10
    //   350: invokespecial 287	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   353: invokespecial 290	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   356: astore_3
    //   357: aload 4
    //   359: new 158	java/io/File
    //   362: dup
    //   363: aload_3
    //   364: invokevirtual 293	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   367: checkcast 127	java/lang/String
    //   370: invokespecial 294	java/io/File:<init>	(Ljava/lang/String;)V
    //   373: putfield 156	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:file	Ljava/io/File;
    //   376: aload 4
    //   378: new 158	java/io/File
    //   381: dup
    //   382: aload_3
    //   383: invokevirtual 293	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   386: checkcast 127	java/lang/String
    //   389: invokespecial 294	java/io/File:<init>	(Ljava/lang/String;)V
    //   392: putfield 164	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:meta	Ljava/io/File;
    //   395: aload 4
    //   397: aload_3
    //   398: invokevirtual 293	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   401: checkcast 127	java/lang/String
    //   404: putfield 297	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:mimeType	Ljava/lang/String;
    //   407: aload 4
    //   409: aload_3
    //   410: invokevirtual 300	java/io/ObjectInputStream:readLong	()J
    //   413: putfield 303	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:lastModifiedTime	J
    //   416: aload 4
    //   418: aload_3
    //   419: invokevirtual 293	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   422: checkcast 127	java/lang/String
    //   425: putfield 306	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:displayName	Ljava/lang/String;
    //   428: aload 4
    //   430: aload_3
    //   431: invokevirtual 293	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   434: checkcast 127	java/lang/String
    //   437: putfield 309	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:title	Ljava/lang/String;
    //   440: aload 4
    //   442: aload_3
    //   443: invokevirtual 300	java/io/ObjectInputStream:readLong	()J
    //   446: putfield 142	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:creationTime	J
    //   449: aload_3
    //   450: ifnonnull +24 -> 474
    //   453: aload 8
    //   455: aload 4
    //   457: getfield 156	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:file	Ljava/io/File;
    //   460: invokevirtual 175	java/io/File:getName	()Ljava/lang/String;
    //   463: aload 4
    //   465: invokeinterface 313 3 0
    //   470: pop
    //   471: goto -193 -> 278
    //   474: aload_3
    //   475: invokevirtual 316	java/io/ObjectInputStream:close	()V
    //   478: goto -25 -> 453
    //   481: astore_3
    //   482: aconst_null
    //   483: astore 4
    //   485: aload 4
    //   487: ifnull +58 -> 545
    //   490: aload 4
    //   492: aload_3
    //   493: if_acmpne +58 -> 551
    //   496: aload 4
    //   498: athrow
    //   499: astore_3
    //   500: aload_0
    //   501: getfield 80	com/oneplus/gallery2/media/MediaSharingManagerImpl:TAG	Ljava/lang/String;
    //   504: new 166	java/lang/StringBuilder
    //   507: dup
    //   508: ldc_w 318
    //   511: invokespecial 171	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   514: aload 10
    //   516: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   519: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   522: aload_3
    //   523: invokestatic 256	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   526: goto -248 -> 278
    //   529: astore 4
    //   531: aload_3
    //   532: ifnonnull +6 -> 538
    //   535: aload 4
    //   537: athrow
    //   538: aload_3
    //   539: invokevirtual 316	java/io/ObjectInputStream:close	()V
    //   542: goto -7 -> 535
    //   545: aload_3
    //   546: astore 4
    //   548: goto -52 -> 496
    //   551: aload 4
    //   553: aload_3
    //   554: invokevirtual 322	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   557: goto -61 -> 496
    //   560: aload_0
    //   561: getfield 67	com/oneplus/gallery2/media/MediaSharingManagerImpl:m_PreparedFileInfos	Ljava/util/Map;
    //   564: astore_3
    //   565: aload_3
    //   566: monitorenter
    //   567: aload_0
    //   568: getfield 67	com/oneplus/gallery2/media/MediaSharingManagerImpl:m_PreparedFileInfos	Ljava/util/Map;
    //   571: invokeinterface 325 1 0
    //   576: aload 8
    //   578: invokeinterface 109 1 0
    //   583: invokeinterface 115 1 0
    //   588: astore 4
    //   590: aload 4
    //   592: invokeinterface 121 1 0
    //   597: ifeq +49 -> 646
    //   600: aload 4
    //   602: invokeinterface 125 1 0
    //   607: checkcast 127	java/lang/String
    //   610: astore 7
    //   612: aload_0
    //   613: getfield 67	com/oneplus/gallery2/media/MediaSharingManagerImpl:m_PreparedFileInfos	Ljava/util/Map;
    //   616: aload 7
    //   618: aload 8
    //   620: aload 7
    //   622: invokeinterface 131 2 0
    //   627: checkcast 133	com/oneplus/gallery2/media/MediaSharingManager$FileInfo
    //   630: invokeinterface 313 3 0
    //   635: pop
    //   636: goto -46 -> 590
    //   639: astore 4
    //   641: aload_3
    //   642: monitorexit
    //   643: aload 4
    //   645: athrow
    //   646: aload_3
    //   647: monitorexit
    //   648: aload 6
    //   650: invokeinterface 115 1 0
    //   655: astore_3
    //   656: aload_3
    //   657: invokeinterface 121 1 0
    //   662: ifeq +26 -> 688
    //   665: new 158	java/io/File
    //   668: dup
    //   669: aload_3
    //   670: invokeinterface 125 1 0
    //   675: checkcast 127	java/lang/String
    //   678: invokespecial 294	java/io/File:<init>	(Ljava/lang/String;)V
    //   681: invokevirtual 161	java/io/File:delete	()Z
    //   684: pop
    //   685: goto -29 -> 656
    //   688: aload 5
    //   690: invokeinterface 115 1 0
    //   695: astore_3
    //   696: aload_3
    //   697: invokeinterface 121 1 0
    //   702: ifeq -510 -> 192
    //   705: new 158	java/io/File
    //   708: dup
    //   709: aload_3
    //   710: invokeinterface 125 1 0
    //   715: checkcast 127	java/lang/String
    //   718: invokespecial 294	java/io/File:<init>	(Ljava/lang/String;)V
    //   721: invokevirtual 161	java/io/File:delete	()Z
    //   724: pop
    //   725: goto -29 -> 696
    //   728: astore 4
    //   730: aload_3
    //   731: monitorexit
    //   732: aload 4
    //   734: athrow
    //   735: iload_1
    //   736: iconst_1
    //   737: iadd
    //   738: istore_1
    //   739: goto -648 -> 91
    //   742: astore_3
    //   743: goto -258 -> 485
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	746	0	this	MediaSharingManagerImpl
    //   1	738	1	i	int
    //   90	4	2	j	int
    //   86	1	3	localHashSet1	java.util.HashSet
    //   181	8	3	localThrowable1	Throwable
    //   195	280	3	localObject1	Object
    //   481	12	3	localObject2	Object
    //   499	55	3	localThrowable2	Throwable
    //   742	1	3	localObject4	Object
    //   59	438	4	localObject5	Object
    //   529	7	4	localObject6	Object
    //   546	55	4	localObject7	Object
    //   639	5	4	localObject8	Object
    //   728	5	4	localObject9	Object
    //   77	612	5	localHashSet2	java.util.HashSet
    //   68	581	6	localHashSet3	java.util.HashSet
    //   52	569	7	localObject10	Object
    //   100	519	8	localObject11	Object
    //   276	13	9	localIterator	Iterator
    //   329	186	10	str	String
    // Exception table:
    //   from	to	target	type
    //   43	91	181	java/lang/Throwable
    //   102	134	181	java/lang/Throwable
    //   137	178	181	java/lang/Throwable
    //   237	246	181	java/lang/Throwable
    //   249	258	181	java/lang/Throwable
    //   261	278	181	java/lang/Throwable
    //   278	340	181	java/lang/Throwable
    //   453	471	181	java/lang/Throwable
    //   500	526	181	java/lang/Throwable
    //   560	567	181	java/lang/Throwable
    //   643	646	181	java/lang/Throwable
    //   648	656	181	java/lang/Throwable
    //   656	685	181	java/lang/Throwable
    //   688	696	181	java/lang/Throwable
    //   696	725	181	java/lang/Throwable
    //   340	357	481	finally
    //   474	478	481	finally
    //   496	499	499	java/lang/Throwable
    //   551	557	499	java/lang/Throwable
    //   357	449	529	finally
    //   567	590	639	finally
    //   590	636	639	finally
    //   641	643	639	finally
    //   646	648	639	finally
    //   198	211	728	finally
    //   730	732	728	finally
    //   535	538	742	finally
    //   538	542	742	finally
  }
  
  public void clearSharingCaches()
  {
    int i = 0;
    if (Thread.currentThread() != this.m_WorkerThread)
    {
      Message.obtain(this.m_WorkerHandler, 10015).sendToTarget();
      return;
    }
    ??? = BaseApplication.current().getDir("sharing_caches", 0).listFiles();
    int j = ???.length;
    while (i < j)
    {
      ???[i].delete();
      i += 1;
    }
    synchronized (this.m_PreparedFileInfos)
    {
      this.m_PreparedFileInfos.clear();
      return;
    }
  }
  
  public MediaSharingManager.FileInfo getFileInfo(Uri arg1, int paramInt)
  {
    if (this.m_IsFileInfosReady) {}
    for (;;)
    {
      ??? = Uri.encode(???.getLastPathSegment());
      synchronized (this.m_PreparedFileInfos)
      {
        ??? = (MediaSharingManager.FileInfo)this.m_PreparedFileInfos.get(???);
        return (MediaSharingManager.FileInfo)???;
        if ((paramInt & 0x1) != 0) {
          Log.v(this.TAG, "getFileInfo() - Wait for file infos ready");
        }
        synchronized (GET_FILE_INFO_LOCK)
        {
          if (this.m_IsFileInfosReady) {}
          for (;;)
          {
            Log.v(this.TAG, "getFileInfo() - File infos are ready");
            break;
            return null;
            try
            {
              GET_FILE_INFO_LOCK.wait();
            }
            catch (InterruptedException localInterruptedException) {}
          }
        }
      }
    }
  }
  
  public String getMediaId(Uri paramUri)
  {
    if (paramUri != null) {
      return paramUri.getLastPathSegment();
    }
    return null;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    paramMessage = (Object[])paramMessage.obj;
    onPrepared((PrepareSharingCallback)paramMessage[0], (Media)paramMessage[1], (Uri)paramMessage[2], (String)paramMessage[3], (PrepareSharingResult)paramMessage[4]);
  }
  
  protected void onDeinitialize()
  {
    this.m_WorkerHandler.removeMessages(10011);
    this.m_WorkerHandler.removeMessages(10015);
    this.m_WorkerHandler.removeMessages(10010);
    if (this.m_WorkerHandler == null) {}
    for (;;)
    {
      super.onDeinitialize();
      return;
      this.m_WorkerHandler.getLooper().quitSafely();
      this.m_WorkerHandler = null;
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_WorkerThread = new HandlerThread("Media Sharing Manager Thread");
    this.m_WorkerThread.start();
    this.m_WorkerHandler = new Handler(this.m_WorkerThread.getLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        MediaSharingManagerImpl.this.handleWorkerMessage(paramAnonymousMessage);
      }
    };
    readFileInfos();
    checkExpiredFileInfos();
  }
  
  public Handle prepareSharing(final Media paramMedia, final PrepareSharingCallback paramPrepareSharingCallback, int paramInt)
  {
    verifyAccess();
    final String str1;
    final Uri localUri;
    String str2;
    if (isRunningOrInitializing(true))
    {
      if (paramMedia == null) {
        break label143;
      }
      str1 = Uri.encode(paramMedia.getId());
      localUri = Uri.parse("content://oneplus.gallery/share/" + str1);
      str2 = paramMedia.getMimeType();
    }
    MediaSharingManager.FileInfo localFileInfo;
    label143:
    do
    {
      synchronized (this.m_PreparedFileInfos)
      {
        localFileInfo = (MediaSharingManager.FileInfo)this.m_PreparedFileInfos.get(str1);
        if (localFileInfo == null)
        {
          ??? = new SimpleRef(Boolean.valueOf(false));
          this.m_WorkerHandler.post(new Runnable()
          {
            /* Error */
            public void run()
            {
              // Byte code:
              //   0: aconst_null
              //   1: astore 5
              //   3: aload_0
              //   4: getfield 29	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$isCanceled	Lcom/oneplus/base/Ref;
              //   7: invokeinterface 50 1 0
              //   12: checkcast 52	java/lang/Boolean
              //   15: invokevirtual 56	java/lang/Boolean:booleanValue	()Z
              //   18: ifne +486 -> 504
              //   21: getstatic 62	com/oneplus/gallery2/media/PrepareSharingResult:SUCCESS	Lcom/oneplus/gallery2/media/PrepareSharingResult;
              //   24: astore_2
              //   25: new 64	java/lang/StringBuilder
              //   28: dup
              //   29: invokestatic 70	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
              //   32: ldc 72
              //   34: iconst_0
              //   35: invokevirtual 76	com/oneplus/base/BaseApplication:getDir	(Ljava/lang/String;I)Ljava/io/File;
              //   38: invokevirtual 82	java/io/File:getAbsolutePath	()Ljava/lang/String;
              //   41: invokestatic 88	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
              //   44: invokespecial 91	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
              //   47: ldc 93
              //   49: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   52: aload_0
              //   53: getfield 37	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$encodedMediaId	Ljava/lang/String;
              //   56: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   59: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   62: astore 6
              //   64: new 64	java/lang/StringBuilder
              //   67: dup
              //   68: aload 6
              //   70: invokestatic 88	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
              //   73: invokespecial 91	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
              //   76: ldc 102
              //   78: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   81: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   84: astore 7
              //   86: aload_0
              //   87: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   90: invokestatic 106	com/oneplus/gallery2/media/MediaSharingManagerImpl:access$1	(Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;)Ljava/util/Map;
              //   93: astore_3
              //   94: aload_3
              //   95: monitorenter
              //   96: aload_0
              //   97: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   100: invokestatic 106	com/oneplus/gallery2/media/MediaSharingManagerImpl:access$1	(Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;)Ljava/util/Map;
              //   103: aload_0
              //   104: getfield 37	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$encodedMediaId	Ljava/lang/String;
              //   107: invokeinterface 111 2 0
              //   112: checkcast 113	com/oneplus/gallery2/media/MediaSharingManager$FileInfo
              //   115: astore 4
              //   117: aload_3
              //   118: monitorexit
              //   119: aload 4
              //   121: ifnonnull +451 -> 572
              //   124: aload_0
              //   125: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   128: invokestatic 117	com/oneplus/gallery2/media/MediaSharingManagerImpl:access$2	(Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;)Ljava/lang/String;
              //   131: ldc 119
              //   133: invokestatic 125	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
              //   136: aload_0
              //   137: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   140: aload_0
              //   141: getfield 29	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$isCanceled	Lcom/oneplus/base/Ref;
              //   144: getstatic 131	com/oneplus/gallery2/media/Media:FLAG_SHARE	I
              //   147: invokeinterface 135 3 0
              //   152: astore_3
              //   153: aload_3
              //   154: ifnull +564 -> 718
              //   157: new 137	java/io/FileOutputStream
              //   160: dup
              //   161: aload 6
              //   163: invokespecial 138	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
              //   166: astore 8
              //   168: sipush 1024
              //   171: newarray <illegal type>
              //   173: astore 4
              //   175: aload_3
              //   176: aload 4
              //   178: invokevirtual 144	java/io/InputStream:read	([B)I
              //   181: istore_1
              //   182: iload_1
              //   183: ifle +598 -> 781
              //   186: aload 8
              //   188: aload 4
              //   190: iconst_0
              //   191: iload_1
              //   192: invokevirtual 148	java/io/FileOutputStream:write	([BII)V
              //   195: goto -20 -> 175
              //   198: astore 4
              //   200: aload 8
              //   202: ifnonnull +599 -> 801
              //   205: aload 4
              //   207: athrow
              //   208: astore_3
              //   209: aload 4
              //   211: ifnull +598 -> 809
              //   214: aload 4
              //   216: aload_3
              //   217: if_acmpne +598 -> 815
              //   220: aload 4
              //   222: athrow
              //   223: astore_3
              //   224: aload_0
              //   225: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   228: invokestatic 117	com/oneplus/gallery2/media/MediaSharingManagerImpl:access$2	(Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;)Ljava/lang/String;
              //   231: ldc -106
              //   233: aload_3
              //   234: invokestatic 154	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
              //   237: aload_0
              //   238: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   241: invokeinterface 157 1 0
              //   246: astore_3
              //   247: aload_3
              //   248: ifnull +576 -> 824
              //   251: new 113	com/oneplus/gallery2/media/MediaSharingManager$FileInfo
              //   254: dup
              //   255: invokespecial 158	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:<init>	()V
              //   258: astore 4
              //   260: aload 4
              //   262: new 78	java/io/File
              //   265: dup
              //   266: aload 6
              //   268: invokespecial 159	java/io/File:<init>	(Ljava/lang/String;)V
              //   271: putfield 163	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:file	Ljava/io/File;
              //   274: aload 4
              //   276: new 78	java/io/File
              //   279: dup
              //   280: aload 7
              //   282: invokespecial 159	java/io/File:<init>	(Ljava/lang/String;)V
              //   285: putfield 166	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:meta	Ljava/io/File;
              //   288: aload 4
              //   290: aload_0
              //   291: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   294: invokeinterface 169 1 0
              //   299: putfield 172	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:mimeType	Ljava/lang/String;
              //   302: aload 4
              //   304: aload_0
              //   305: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   308: invokeinterface 176 1 0
              //   313: putfield 180	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:lastModifiedTime	J
              //   316: aload 4
              //   318: new 64	java/lang/StringBuilder
              //   321: dup
              //   322: aload_0
              //   323: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   326: invokeinterface 183 1 0
              //   331: invokestatic 88	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
              //   334: invokespecial 91	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
              //   337: aload_3
              //   338: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   341: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   344: putfield 186	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:displayName	Ljava/lang/String;
              //   347: aload 4
              //   349: aload_0
              //   350: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   353: invokeinterface 189 1 0
              //   358: putfield 192	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:title	Ljava/lang/String;
              //   361: aload 4
              //   363: invokestatic 197	android/os/SystemClock:elapsedRealtime	()J
              //   366: putfield 200	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:creationTime	J
              //   369: aload_0
              //   370: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   373: invokestatic 106	com/oneplus/gallery2/media/MediaSharingManagerImpl:access$1	(Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;)Ljava/util/Map;
              //   376: astore_3
              //   377: aload_3
              //   378: monitorenter
              //   379: aload_0
              //   380: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   383: invokestatic 106	com/oneplus/gallery2/media/MediaSharingManagerImpl:access$1	(Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;)Ljava/util/Map;
              //   386: aload_0
              //   387: getfield 37	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$encodedMediaId	Ljava/lang/String;
              //   390: aload 4
              //   392: invokeinterface 204 3 0
              //   397: pop
              //   398: aload_3
              //   399: monitorexit
              //   400: new 206	java/io/ObjectOutputStream
              //   403: dup
              //   404: new 137	java/io/FileOutputStream
              //   407: dup
              //   408: aload 7
              //   410: invokespecial 138	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
              //   413: invokespecial 209	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
              //   416: astore_3
              //   417: aload_3
              //   418: aload 6
              //   420: invokevirtual 213	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
              //   423: aload_3
              //   424: aload 7
              //   426: invokevirtual 213	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
              //   429: aload_3
              //   430: aload 4
              //   432: getfield 172	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:mimeType	Ljava/lang/String;
              //   435: invokevirtual 213	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
              //   438: aload_3
              //   439: aload 4
              //   441: getfield 180	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:lastModifiedTime	J
              //   444: invokevirtual 217	java/io/ObjectOutputStream:writeLong	(J)V
              //   447: aload_3
              //   448: aload 4
              //   450: getfield 186	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:displayName	Ljava/lang/String;
              //   453: invokevirtual 213	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
              //   456: aload_3
              //   457: aload 4
              //   459: getfield 192	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:title	Ljava/lang/String;
              //   462: invokevirtual 213	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
              //   465: aload_3
              //   466: aload 4
              //   468: getfield 200	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:creationTime	J
              //   471: invokevirtual 217	java/io/ObjectOutputStream:writeLong	(J)V
              //   474: aload_3
              //   475: ifnonnull +360 -> 835
              //   478: aload_0
              //   479: getfield 29	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$isCanceled	Lcom/oneplus/base/Ref;
              //   482: invokeinterface 50 1 0
              //   487: checkcast 52	java/lang/Boolean
              //   490: invokevirtual 56	java/lang/Boolean:booleanValue	()Z
              //   493: ifne +416 -> 909
              //   496: aload_0
              //   497: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   500: ifnonnull +472 -> 972
              //   503: return
              //   504: aload_0
              //   505: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   508: ifnonnull +4 -> 512
              //   511: return
              //   512: aload_0
              //   513: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   516: sipush 10005
              //   519: iconst_5
              //   520: anewarray 4	java/lang/Object
              //   523: dup
              //   524: iconst_0
              //   525: aload_0
              //   526: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   529: aastore
              //   530: dup
              //   531: iconst_1
              //   532: aload_0
              //   533: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   536: aastore
              //   537: dup
              //   538: iconst_2
              //   539: aload_0
              //   540: getfield 35	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$contentUri	Landroid/net/Uri;
              //   543: aastore
              //   544: dup
              //   545: iconst_3
              //   546: aload_0
              //   547: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   550: invokeinterface 169 1 0
              //   555: aastore
              //   556: dup
              //   557: iconst_4
              //   558: getstatic 220	com/oneplus/gallery2/media/PrepareSharingResult:CANCELED	Lcom/oneplus/gallery2/media/PrepareSharingResult;
              //   561: aastore
              //   562: invokestatic 226	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
              //   565: pop
              //   566: return
              //   567: astore_2
              //   568: aload_3
              //   569: monitorexit
              //   570: aload_2
              //   571: athrow
              //   572: aload 4
              //   574: getfield 180	com/oneplus/gallery2/media/MediaSharingManager$FileInfo:lastModifiedTime	J
              //   577: aload_0
              //   578: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   581: invokeinterface 176 1 0
              //   586: lcmp
              //   587: ifne -463 -> 124
              //   590: aload_0
              //   591: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   594: ifnonnull +4 -> 598
              //   597: return
              //   598: aload_0
              //   599: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   602: sipush 10005
              //   605: iconst_5
              //   606: anewarray 4	java/lang/Object
              //   609: dup
              //   610: iconst_0
              //   611: aload_0
              //   612: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   615: aastore
              //   616: dup
              //   617: iconst_1
              //   618: aload_0
              //   619: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   622: aastore
              //   623: dup
              //   624: iconst_2
              //   625: aload_0
              //   626: getfield 35	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$contentUri	Landroid/net/Uri;
              //   629: aastore
              //   630: dup
              //   631: iconst_3
              //   632: aload_0
              //   633: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   636: invokeinterface 169 1 0
              //   641: aastore
              //   642: dup
              //   643: iconst_4
              //   644: aload_2
              //   645: aastore
              //   646: invokestatic 226	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
              //   649: pop
              //   650: return
              //   651: astore_3
              //   652: aload_0
              //   653: getfield 29	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$isCanceled	Lcom/oneplus/base/Ref;
              //   656: invokeinterface 50 1 0
              //   661: checkcast 52	java/lang/Boolean
              //   664: invokevirtual 56	java/lang/Boolean:booleanValue	()Z
              //   667: ifne +35 -> 702
              //   670: aload_0
              //   671: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   674: instanceof 228
              //   677: ifne +34 -> 711
              //   680: getstatic 231	com/oneplus/gallery2/media/PrepareSharingResult:UNKNOWN_ERROR	Lcom/oneplus/gallery2/media/PrepareSharingResult;
              //   683: astore_2
              //   684: aload_0
              //   685: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   688: invokestatic 117	com/oneplus/gallery2/media/MediaSharingManagerImpl:access$2	(Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;)Ljava/lang/String;
              //   691: ldc -23
              //   693: aload_3
              //   694: invokestatic 154	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
              //   697: aconst_null
              //   698: astore_3
              //   699: goto -546 -> 153
              //   702: getstatic 220	com/oneplus/gallery2/media/PrepareSharingResult:CANCELED	Lcom/oneplus/gallery2/media/PrepareSharingResult;
              //   705: astore_2
              //   706: aconst_null
              //   707: astore_3
              //   708: goto -555 -> 153
              //   711: getstatic 236	com/oneplus/gallery2/media/PrepareSharingResult:NETWORK_ERROR	Lcom/oneplus/gallery2/media/PrepareSharingResult;
              //   714: astore_2
              //   715: goto -31 -> 684
              //   718: aload_0
              //   719: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   722: invokestatic 117	com/oneplus/gallery2/media/MediaSharingManagerImpl:access$2	(Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;)Ljava/lang/String;
              //   725: ldc -18
              //   727: invokestatic 240	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
              //   730: aload_0
              //   731: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   734: ifnonnull +4 -> 738
              //   737: return
              //   738: aload_0
              //   739: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   742: astore_3
              //   743: iconst_5
              //   744: anewarray 4	java/lang/Object
              //   747: astore 4
              //   749: aload 4
              //   751: iconst_0
              //   752: aload_0
              //   753: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   756: aastore
              //   757: aload 4
              //   759: iconst_1
              //   760: aload_0
              //   761: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   764: aastore
              //   765: aload 4
              //   767: iconst_4
              //   768: aload_2
              //   769: aastore
              //   770: aload_3
              //   771: sipush 10005
              //   774: aload 4
              //   776: invokestatic 226	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
              //   779: pop
              //   780: return
              //   781: aload 8
              //   783: ifnull -546 -> 237
              //   786: aload 8
              //   788: invokevirtual 243	java/io/FileOutputStream:close	()V
              //   791: goto -554 -> 237
              //   794: astore_3
              //   795: aconst_null
              //   796: astore 4
              //   798: goto -589 -> 209
              //   801: aload 8
              //   803: invokevirtual 243	java/io/FileOutputStream:close	()V
              //   806: goto -601 -> 205
              //   809: aload_3
              //   810: astore 4
              //   812: goto -592 -> 220
              //   815: aload 4
              //   817: aload_3
              //   818: invokevirtual 247	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
              //   821: goto -601 -> 220
              //   824: ldc -7
              //   826: astore_3
              //   827: goto -576 -> 251
              //   830: astore_2
              //   831: aload_3
              //   832: monitorexit
              //   833: aload_2
              //   834: athrow
              //   835: aload_3
              //   836: invokevirtual 250	java/io/ObjectOutputStream:close	()V
              //   839: goto -361 -> 478
              //   842: astore_3
              //   843: aload 5
              //   845: astore 4
              //   847: aload 4
              //   849: ifnull +45 -> 894
              //   852: aload 4
              //   854: aload_3
              //   855: if_acmpne +45 -> 900
              //   858: aload 4
              //   860: athrow
              //   861: astore_3
              //   862: aload_0
              //   863: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   866: invokestatic 117	com/oneplus/gallery2/media/MediaSharingManagerImpl:access$2	(Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;)Ljava/lang/String;
              //   869: ldc -106
              //   871: aload_3
              //   872: invokestatic 154	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
              //   875: goto -397 -> 478
              //   878: astore 4
              //   880: aload_3
              //   881: ifnonnull +6 -> 887
              //   884: aload 4
              //   886: athrow
              //   887: aload_3
              //   888: invokevirtual 250	java/io/ObjectOutputStream:close	()V
              //   891: goto -7 -> 884
              //   894: aload_3
              //   895: astore 4
              //   897: goto -39 -> 858
              //   900: aload 4
              //   902: aload_3
              //   903: invokevirtual 247	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
              //   906: goto -48 -> 858
              //   909: aload_0
              //   910: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   913: ifnonnull +4 -> 917
              //   916: return
              //   917: aload_0
              //   918: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   921: sipush 10005
              //   924: iconst_5
              //   925: anewarray 4	java/lang/Object
              //   928: dup
              //   929: iconst_0
              //   930: aload_0
              //   931: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   934: aastore
              //   935: dup
              //   936: iconst_1
              //   937: aload_0
              //   938: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   941: aastore
              //   942: dup
              //   943: iconst_2
              //   944: aload_0
              //   945: getfield 35	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$contentUri	Landroid/net/Uri;
              //   948: aastore
              //   949: dup
              //   950: iconst_3
              //   951: aload_0
              //   952: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   955: invokeinterface 169 1 0
              //   960: aastore
              //   961: dup
              //   962: iconst_4
              //   963: getstatic 220	com/oneplus/gallery2/media/PrepareSharingResult:CANCELED	Lcom/oneplus/gallery2/media/PrepareSharingResult;
              //   966: aastore
              //   967: invokestatic 226	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
              //   970: pop
              //   971: return
              //   972: aload_0
              //   973: getfield 27	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:this$0	Lcom/oneplus/gallery2/media/MediaSharingManagerImpl;
              //   976: sipush 10005
              //   979: iconst_5
              //   980: anewarray 4	java/lang/Object
              //   983: dup
              //   984: iconst_0
              //   985: aload_0
              //   986: getfield 31	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$callback	Lcom/oneplus/gallery2/media/PrepareSharingCallback;
              //   989: aastore
              //   990: dup
              //   991: iconst_1
              //   992: aload_0
              //   993: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   996: aastore
              //   997: dup
              //   998: iconst_2
              //   999: aload_0
              //   1000: getfield 35	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$contentUri	Landroid/net/Uri;
              //   1003: aastore
              //   1004: dup
              //   1005: iconst_3
              //   1006: aload_0
              //   1007: getfield 33	com/oneplus/gallery2/media/MediaSharingManagerImpl$2:val$media	Lcom/oneplus/gallery2/media/Media;
              //   1010: invokeinterface 169 1 0
              //   1015: aastore
              //   1016: dup
              //   1017: iconst_4
              //   1018: aload_2
              //   1019: aastore
              //   1020: invokestatic 226	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
              //   1023: pop
              //   1024: return
              //   1025: astore_3
              //   1026: goto -179 -> 847
              // Local variable table:
              //   start	length	slot	name	signature
              //   0	1029	0	this	2
              //   181	11	1	i	int
              //   24	1	2	localPrepareSharingResult1	PrepareSharingResult
              //   567	78	2	localObject1	Object
              //   683	86	2	localPrepareSharingResult2	PrepareSharingResult
              //   830	189	2	localObject2	Object
              //   93	83	3	localObject3	Object
              //   208	9	3	localObject4	Object
              //   223	11	3	localThrowable1	Throwable
              //   246	323	3	localObject5	Object
              //   651	43	3	localThrowable2	Throwable
              //   698	73	3	localMediaSharingManagerImpl	MediaSharingManagerImpl
              //   794	24	3	localThrowable3	Throwable
              //   826	10	3	str1	String
              //   842	13	3	localObject6	Object
              //   861	42	3	localThrowable4	Throwable
              //   1025	1	3	localObject7	Object
              //   115	74	4	localObject8	Object
              //   198	23	4	localObject9	Object
              //   258	601	4	localObject10	Object
              //   878	7	4	localObject11	Object
              //   895	6	4	localThrowable5	Throwable
              //   1	843	5	localObject12	Object
              //   62	357	6	str2	String
              //   84	341	7	str3	String
              //   166	636	8	localFileOutputStream	java.io.FileOutputStream
              // Exception table:
              //   from	to	target	type
              //   168	175	198	finally
              //   175	182	198	finally
              //   186	195	198	finally
              //   205	208	208	finally
              //   801	806	208	finally
              //   220	223	223	java/lang/Throwable
              //   815	821	223	java/lang/Throwable
              //   96	119	567	finally
              //   568	570	567	finally
              //   136	153	651	java/lang/Throwable
              //   157	168	794	finally
              //   786	791	794	finally
              //   379	400	830	finally
              //   831	833	830	finally
              //   400	417	842	finally
              //   835	839	842	finally
              //   858	861	861	java/lang/Throwable
              //   900	906	861	java/lang/Throwable
              //   417	474	878	finally
              //   884	887	1025	finally
              //   887	891	1025	finally
            }
          });
          new Handle("Prepare Sharing Handle")
          {
            protected void onClose(int paramAnonymousInt)
            {
              localObject.set(Boolean.valueOf(true));
            }
          };
          return null;
          return null;
        }
      }
    } while (localFileInfo.lastModifiedTime != paramMedia.getLastModifiedTime());
    if (paramPrepareSharingCallback == null) {}
    for (;;)
    {
      return new EmptyHandle("Prepare Sharing Handle");
      paramPrepareSharingCallback.onPrepared(paramMedia, localUri, str2, PrepareSharingResult.SUCCESS);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSharingManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */