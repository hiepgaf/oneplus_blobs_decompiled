package com.oneplus.camera.media;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore.Files;
import android.util.Pair;
import com.oneplus.base.EventArgs;
import com.oneplus.base.ListHandlerBaseObject;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.base.SimpleRef;
import com.oneplus.camera.CameraApplication;
import com.oneplus.database.CursorUtils;
import com.oneplus.io.Path;
import com.oneplus.io.StorageManager;
import com.oneplus.io.StorageUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class MediaListManager
{
  private static final long DURATION_CHECK_MEDIA_LISTS = 1000L;
  private static final long DURATION_CLEAR_MEDIA_LISTS = 3000L;
  public static final int FLAG_SECURE = 1;
  private static final String[] MEDIA_COLUMNS = { "_id", "_data", "date_modified", "_size", "mime_type", "datetaken", "width", "height", "orientation", "duration", "latitude", "longitude" };
  private static final String MEDIA_QUERY_BASE_CONDITIONS = "(media_type=1 OR media_type=3)";
  private static final int MSG_ADD_MEDIA = 10010;
  private static final int MSG_CHECK_MEDIA_LISTS = 10002;
  private static final int MSG_CLEAR_MEDIA_LISTS = 10000;
  private static final int MSG_REFRESH_MEDIA_LISTS = 10001;
  private static final int MSG_REMOVE_MEDIA = 10011;
  private static final Uri MTP_URI_OBJECT = Uri.parse("content://media/external/object");
  private static final int PARTIAL_QUERY_SIZE = 64;
  private static final String TAG = "MediaListManager";
  private static final List<MediaListImpl> m_ActiveMediaLists = new ArrayList();
  private static final Set<MediaInfo> m_BaseMediaSet = new HashSet();
  private static ContentObserver m_ContentObserver;
  private static volatile HandlerThread m_ContentThread;
  private static volatile Handler m_ContentThreadHandler;
  private static volatile Context m_Context;
  private static final Object m_Lock = new Object();
  private static final Comparator<MediaInfo> m_MediaInfoComparator = new Comparator()
  {
    public int compare(MediaInfo paramAnonymousMediaInfo1, MediaInfo paramAnonymousMediaInfo2)
    {
      long l = paramAnonymousMediaInfo2.getTakenTime() - paramAnonymousMediaInfo1.getTakenTime();
      if (l < 0L) {
        return -1;
      }
      if (l > 0L) {
        return 1;
      }
      paramAnonymousMediaInfo1 = paramAnonymousMediaInfo1.getFilePath();
      paramAnonymousMediaInfo2 = paramAnonymousMediaInfo2.getFilePath();
      if (paramAnonymousMediaInfo1 != null)
      {
        if (paramAnonymousMediaInfo2 != null) {
          return -paramAnonymousMediaInfo1.compareTo(paramAnonymousMediaInfo2);
        }
        return -1;
      }
      if (paramAnonymousMediaInfo2 != null) {
        return 1;
      }
      return 0;
    }
  };
  private static final LinkedList<NewMediaInfo> m_PendingNewMedia = new LinkedList();
  
  private static void addMedia(String paramString, long paramLong)
  {
    if (m_Context == null)
    {
      Log.w("MediaListManager", "addMedia() - No context");
      return;
    }
    if (m_ContentThreadHandler.hasMessages(10000))
    {
      Log.w("MediaListManager", "addMedia() - Cancelled");
      return;
    }
    Object localObject6 = null;
    ContentProviderClient localContentProviderClient2 = null;
    MediaInfo localMediaInfo = null;
    Object localObject4 = null;
    localContentProviderClient1 = localContentProviderClient2;
    localObject3 = localObject4;
    localObject1 = localObject6;
    localObject2 = localMediaInfo;
    for (;;)
    {
      try
      {
        Uri localUri = MediaStore.Files.getContentUri("external");
        localContentProviderClient1 = localContentProviderClient2;
        localObject3 = localObject4;
        localObject1 = localObject6;
        localObject2 = localMediaInfo;
        localContentProviderClient2 = m_Context.getContentResolver().acquireUnstableContentProviderClient(localUri);
        localContentProviderClient1 = localContentProviderClient2;
        localObject3 = localObject4;
        localObject1 = localContentProviderClient2;
        localObject2 = localMediaInfo;
        paramString = localContentProviderClient2.query(localUri, MEDIA_COLUMNS, "_data=?", new String[] { paramString }, null);
        localContentProviderClient1 = localContentProviderClient2;
        localObject3 = paramString;
        localObject1 = localContentProviderClient2;
        localObject2 = paramString;
        if (paramString.moveToNext())
        {
          localContentProviderClient1 = localContentProviderClient2;
          localObject3 = paramString;
          localObject1 = localContentProviderClient2;
          localObject2 = paramString;
          localMediaInfo = createMediaInfo(localUri, paramString);
          if (localMediaInfo == null)
          {
            if (paramString != null) {
              paramString.close();
            }
            if (localContentProviderClient2 != null) {
              localContentProviderClient2.release();
            }
            return;
          }
          localContentProviderClient1 = localContentProviderClient2;
          localObject3 = paramString;
          localObject1 = localContentProviderClient2;
          localObject2 = paramString;
          localObject4 = m_Lock;
          localContentProviderClient1 = localContentProviderClient2;
          localObject3 = paramString;
          localObject1 = localContentProviderClient2;
          localObject2 = paramString;
        }
      }
      catch (Throwable paramString)
      {
        boolean bool;
        int i;
        localObject1 = localContentProviderClient1;
        localObject2 = localObject3;
        Log.e("MediaListManager", "addMedia() - Unhandled exception", paramString);
        return;
      }
      finally
      {
        if (localObject2 == null) {
          continue;
        }
        ((Cursor)localObject2).close();
        if (localObject1 == null) {
          continue;
        }
        ((ContentProviderClient)localObject1).release();
      }
      try
      {
        bool = m_BaseMediaSet.add(localMediaInfo);
        if (!bool)
        {
          localContentProviderClient1 = localContentProviderClient2;
          localObject3 = paramString;
          localObject1 = localContentProviderClient2;
          localObject2 = paramString;
          if (paramString != null) {
            paramString.close();
          }
          if (localContentProviderClient2 != null) {
            localContentProviderClient2.release();
          }
          return;
        }
        i = m_ActiveMediaLists.size() - 1;
        if (i >= 0)
        {
          localObject1 = (MediaListImpl)m_ActiveMediaLists.get(i);
          if ((!((MediaListImpl)localObject1).isSecureMode) || (((MediaListImpl)localObject1).creationTime < paramLong)) {
            ((MediaListImpl)localObject1).addMedia(localMediaInfo);
          }
          i -= 1;
          continue;
        }
        localContentProviderClient1 = localContentProviderClient2;
        localObject3 = paramString;
        localObject1 = localContentProviderClient2;
        localObject2 = paramString;
        if (paramString != null) {
          paramString.close();
        }
        if (localContentProviderClient2 != null) {
          localContentProviderClient2.release();
        }
        return;
      }
      finally
      {
        localContentProviderClient1 = localContentProviderClient2;
        localObject3 = paramString;
        localObject1 = localContentProviderClient2;
        localObject2 = paramString;
        localContentProviderClient1 = localContentProviderClient2;
        localObject3 = paramString;
        localObject1 = localContentProviderClient2;
        localObject2 = paramString;
      }
    }
  }
  
  private static boolean checkContentThread(boolean paramBoolean)
  {
    if (m_ContentThread != null) {
      return true;
    }
    synchronized (m_Lock)
    {
      HandlerThread localHandlerThread = m_ContentThread;
      if (localHandlerThread == null)
      {
        if (!paramBoolean) {
          return false;
        }
        m_ContentThread = new HandlerThread("Media manager content thread");
        Log.v("MediaListManager", "checkContentThread() - Start content thread");
        m_ContentThread.start();
        m_ContentThreadHandler = new Handler(m_ContentThread.getLooper())
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            MediaListManager.-wrap0(paramAnonymousMessage);
          }
        };
        Log.v("MediaListManager", "checkContentThread() - Content thread started");
      }
      return true;
    }
  }
  
  private static void checkMediaLists()
  {
    Log.v("MediaListManager", "checkMediaLists()");
    Object localObject7;
    Object localObject8;
    ArrayList localArrayList2;
    Object localObject9;
    Object localObject3;
    long l1;
    Object localObject1;
    synchronized (m_Lock)
    {
      localObject7 = new ArrayList();
      localObject8 = new ArrayList();
      localArrayList2 = new ArrayList();
      localObject9 = new Hashtable();
      try
      {
        Iterator localIterator = m_BaseMediaSet.iterator();
        while (localIterator.hasNext())
        {
          localObject3 = (MediaInfo)localIterator.next();
          ((Hashtable)localObject9).put(Integer.valueOf(Path.getFileName(((MediaInfo)localObject3).getContentUri().toString())), localObject3);
        }
        l1 = SystemClock.elapsedRealtime();
      }
      catch (Throwable localThrowable1)
      {
        Log.e("MediaListManager", "checkMediaLists() - Cannot create media list content IDs", localThrowable1);
        return;
      }
      ArrayList localArrayList1 = null;
      ContentProviderClient localContentProviderClient = null;
      localObject3 = localContentProviderClient;
      localObject1 = localArrayList1;
      for (;;)
      {
        try
        {
          localUri = MediaStore.Files.getContentUri("external");
          localObject3 = localContentProviderClient;
          localObject1 = localArrayList1;
          localContentProviderClient = m_Context.getContentResolver().acquireUnstableContentProviderClient(localUri);
          localObject3 = localContentProviderClient;
          localObject1 = localContentProviderClient;
          if (m_ContentThreadHandler.hasMessages(10000))
          {
            localObject3 = localContentProviderClient;
            localObject1 = localContentProviderClient;
            Log.w("MediaListManager", "checkMediaLists() - Cancelled");
            if (localContentProviderClient != null) {
              localContentProviderClient.release();
            }
            Log.v("MediaListManager", "checkMediaLists() - " + (SystemClock.elapsedRealtime() - l1) + " ms to check media lists");
            return;
          }
          localObject3 = localContentProviderClient;
          localObject1 = localContentProviderClient;
          localArrayList1 = getRecycledMediaListFromGallery();
          localObject3 = localContentProviderClient;
          localObject1 = localContentProviderClient;
          localObject10 = new SimpleRef();
          localObject3 = localContentProviderClient;
          localObject1 = localContentProviderClient;
          SimpleRef localSimpleRef = new SimpleRef();
          localObject3 = localContentProviderClient;
          localObject1 = localContentProviderClient;
          prepareMediaQueryConditions((Ref)localObject10, localSimpleRef);
          localObject3 = localContentProviderClient;
          localObject1 = localContentProviderClient;
          l2 = SystemClock.elapsedRealtime();
          localObject3 = localContentProviderClient;
          localObject1 = localContentProviderClient;
          localObject10 = localContentProviderClient.query(localUri, MEDIA_COLUMNS, (String)((Ref)localObject10).get(), (String[])localSimpleRef.get(), "datetaken DESC");
          if (localObject10 == null) {
            continue;
          }
        }
        catch (Throwable localObject2)
        {
          Uri localUri;
          Object localObject10;
          long l2;
          Log.e("MediaListManager", "checkMediaLists() - Unhandled exception", localThrowable2);
          if (localObject3 == null) {
            continue;
          }
          ((ContentProviderClient)localObject3).release();
          Log.v("MediaListManager", "checkMediaLists() - " + (SystemClock.elapsedRealtime() - l1) + " ms to check media lists");
          return;
          localObject1 = createMediaInfo(localUri, (Cursor)localObject10);
          if (localObject1 == null) {
            continue;
          }
          if (!((ArrayList)localObject5).contains(((MediaInfo)localObject1).getContentUri().toString())) {
            continue;
          }
          Log.v("MediaListManager", "checkMediaLists() - media is recycled : " + ((MediaInfo)localObject1).getContentUri());
          continue;
          ((List)localObject7).add(localObject1);
          continue;
          localObject1 = ((Hashtable)localObject9).values().iterator();
          if (!((Iterator)localObject1).hasNext()) {
            continue;
          }
          ((List)localObject8).add((MediaInfo)((Iterator)localObject1).next());
          continue;
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          ((Cursor)localObject10).close();
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          long l3 = SystemClock.elapsedRealtime();
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          Log.v("MediaListManager", "checkMediaLists() - Take " + (l3 - l2) + " ms to check, added: ", Integer.valueOf(((List)localObject7).size()), ", deleted: ", Integer.valueOf(((List)localObject8).size()), ", replaced: ", Integer.valueOf(localArrayList2.size()));
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          i = ((List)localObject7).size() - 1;
          if (i < 0) {
            continue;
          }
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          localMediaInfo = (MediaInfo)((List)localObject7).get(i);
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          if (!m_BaseMediaSet.add(localMediaInfo)) {
            break label1382;
          }
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          j = m_ActiveMediaLists.size() - 1;
          if (j < 0) {
            break label1382;
          }
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          localObject9 = (MediaListImpl)m_ActiveMediaLists.get(j);
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          if (((MediaListImpl)localObject9).isSecureMode) {
            break label1389;
          }
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          ((MediaListImpl)localObject9).addMedia(localMediaInfo);
          break label1389;
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          i = ((List)localObject8).size() - 1;
          if (i < 0) {
            continue;
          }
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          m_BaseMediaSet.remove(((List)localObject8).get(i));
          i -= 1;
          continue;
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          i = m_ActiveMediaLists.size() - 1;
          if (i < 0) {
            continue;
          }
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          MediaListImpl.-wrap4((MediaListImpl)m_ActiveMediaLists.get(i), (List)localObject8);
          i -= 1;
          continue;
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          i = localArrayList2.size() - 1;
          if (i < 0) {
            continue;
          }
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          localMediaInfo = (MediaInfo)((Pair)localArrayList2.get(i)).first;
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          localObject7 = (MediaInfo)((Pair)localArrayList2.get(i)).second;
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          m_BaseMediaSet.remove(localMediaInfo);
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          m_BaseMediaSet.add(localObject7);
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          j = m_ActiveMediaLists.size() - 1;
          if (j < 0) {
            continue;
          }
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          localObject8 = (MediaListImpl)m_ActiveMediaLists.get(j);
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          if (((MediaListImpl)localObject8).size() <= 0) {
            continue;
          }
          localObject3 = localThrowable2;
          localObject1 = localThrowable2;
          ((MediaListImpl)localObject8).replaceMedia(localMediaInfo, (MediaInfo)localObject7);
          j -= 1;
          continue;
          i -= 1;
          continue;
          if (localThrowable2 == null) {
            continue;
          }
          localThrowable2.release();
          Log.v("MediaListManager", "checkMediaLists() - " + (SystemClock.elapsedRealtime() - l1) + " ms to check media lists");
          continue;
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
        finally
        {
          if (localObject2 == null) {
            continue;
          }
          ((ContentProviderClient)localObject2).release();
          Log.v("MediaListManager", "checkMediaLists() - " + (SystemClock.elapsedRealtime() - l1) + " ms to check media lists");
        }
        try
        {
          if (((Cursor)localObject10).moveToNext())
          {
            localObject3 = Integer.valueOf(CursorUtils.getInt((Cursor)localObject10, "_id", 0));
            l3 = CursorUtils.getLong((Cursor)localObject10, "date_modified", 0L);
            localObject1 = CursorUtils.getString((Cursor)localObject10, "_data");
            if (((Integer)localObject3).intValue() <= 0) {
              continue;
            }
            localObject3 = (MediaInfo)((Hashtable)localObject9).remove(localObject3);
            if (localObject3 != null)
            {
              if ((((MediaInfo)localObject3).getLastModifiedTime() == l3) && (((MediaInfo)localObject3).getFilePath().equals(localObject1))) {
                continue;
              }
              localArrayList2.add(Pair.create(localObject3, createMediaInfo(localUri, (Cursor)localObject10)));
              continue;
              localThrowable2 = localThrowable2;
            }
          }
        }
        finally
        {
          localObject3 = localContentProviderClient;
          localObject1 = localContentProviderClient;
          ((Cursor)localObject10).close();
          localObject3 = localContentProviderClient;
          localObject1 = localContentProviderClient;
        }
      }
    }
    for (;;)
    {
      int i;
      MediaInfo localMediaInfo;
      int j;
      label1382:
      i -= 1;
      continue;
      label1389:
      j -= 1;
    }
  }
  
  private static void clearMediaLists()
  {
    synchronized (m_Lock)
    {
      int i = m_ActiveMediaLists.size() - 1;
      while (i >= 0)
      {
        ((MediaListImpl)m_ActiveMediaLists.get(i)).clearMedia();
        i -= 1;
      }
      m_BaseMediaSet.clear();
      return;
    }
  }
  
  private static MediaInfo createMediaInfo(Uri paramUri, Cursor paramCursor)
  {
    String str = paramCursor.getString(paramCursor.getColumnIndex("mime_type"));
    if (str == null) {
      return null;
    }
    if (str.startsWith("image/")) {
      return new PhotoMediaInfo(paramUri, paramCursor);
    }
    if (str.startsWith("video/")) {
      return new VideoMediaInfo(paramUri, paramCursor);
    }
    return null;
  }
  
  public static MediaList createMediaList(Context paramContext, int paramInt)
  {
    boolean bool;
    MediaListImpl localMediaListImpl;
    if ((paramInt & 0x1) != 0)
    {
      bool = true;
      localMediaListImpl = new MediaListImpl(bool);
    }
    for (;;)
    {
      synchronized (m_Lock)
      {
        m_Context = paramContext.getApplicationContext();
        m_ActiveMediaLists.add(localMediaListImpl);
        if (m_ContentThreadHandler != null) {
          m_ContentThreadHandler.removeMessages(10000);
        }
        if (m_ActiveMediaLists.size() == 1)
        {
          if (checkContentThread(true))
          {
            if (!bool) {
              m_ContentThreadHandler.sendEmptyMessage(10001);
            }
            return localMediaListImpl;
            bool = false;
            break;
          }
          Log.e("MediaListManager", "createMediaList() - Fail to start content thread");
        }
      }
      if (!bool) {
        localMediaListImpl.addMedia(m_BaseMediaSet, false);
      }
    }
  }
  
  /* Error */
  public static android.util.SparseIntArray getMediaListFromGallery()
  {
    // Byte code:
    //   0: new 521	android/util/SparseIntArray
    //   3: dup
    //   4: invokespecial 522	android/util/SparseIntArray:<init>	()V
    //   7: astore_3
    //   8: ldc_w 524
    //   11: invokestatic 141	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   14: astore 4
    //   16: aconst_null
    //   17: astore_1
    //   18: aconst_null
    //   19: astore_0
    //   20: getstatic 170	com/oneplus/camera/media/MediaListManager:m_Context	Landroid/content/Context;
    //   23: invokevirtual 201	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   26: aload 4
    //   28: invokevirtual 207	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
    //   31: astore_2
    //   32: aload_2
    //   33: ifnonnull +26 -> 59
    //   36: aload_2
    //   37: astore_0
    //   38: aload_2
    //   39: astore_1
    //   40: ldc 61
    //   42: ldc_w 526
    //   45: invokestatic 178	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   48: aload_2
    //   49: ifnull +8 -> 57
    //   52: aload_2
    //   53: invokevirtual 231	android/content/ContentProviderClient:release	()Z
    //   56: pop
    //   57: aload_3
    //   58: areturn
    //   59: aload_2
    //   60: astore_0
    //   61: aload_2
    //   62: astore_1
    //   63: aload_2
    //   64: aload 4
    //   66: iconst_2
    //   67: anewarray 107	java/lang/String
    //   70: dup
    //   71: iconst_0
    //   72: ldc_w 528
    //   75: aastore
    //   76: dup
    //   77: iconst_1
    //   78: ldc_w 530
    //   81: aastore
    //   82: aconst_null
    //   83: aconst_null
    //   84: aconst_null
    //   85: invokevirtual 215	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   88: astore 4
    //   90: aload 4
    //   92: ifnull +117 -> 209
    //   95: aload 4
    //   97: invokeinterface 221 1 0
    //   102: ifeq +81 -> 183
    //   105: aload_3
    //   106: aload 4
    //   108: iconst_0
    //   109: invokeinterface 476 2 0
    //   114: invokestatic 333	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   117: invokevirtual 400	java/lang/Integer:intValue	()I
    //   120: aload 4
    //   122: iconst_1
    //   123: invokeinterface 476 2 0
    //   128: invokestatic 333	java/lang/Integer:valueOf	(Ljava/lang/String;)Ljava/lang/Integer;
    //   131: invokevirtual 400	java/lang/Integer:intValue	()I
    //   134: invokevirtual 533	android/util/SparseIntArray:append	(II)V
    //   137: goto -42 -> 95
    //   140: astore 5
    //   142: aload_2
    //   143: astore_0
    //   144: aload_2
    //   145: astore_1
    //   146: aload 4
    //   148: invokeinterface 228 1 0
    //   153: aload_2
    //   154: astore_0
    //   155: aload_2
    //   156: astore_1
    //   157: aload 5
    //   159: athrow
    //   160: astore_2
    //   161: aload_0
    //   162: astore_1
    //   163: ldc 61
    //   165: ldc_w 535
    //   168: aload_2
    //   169: invokestatic 263	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   172: aload_0
    //   173: ifnull -116 -> 57
    //   176: aload_0
    //   177: invokevirtual 231	android/content/ContentProviderClient:release	()Z
    //   180: pop
    //   181: aload_3
    //   182: areturn
    //   183: aload_2
    //   184: astore_0
    //   185: aload_2
    //   186: astore_1
    //   187: aload 4
    //   189: invokeinterface 228 1 0
    //   194: goto -146 -> 48
    //   197: astore_0
    //   198: aload_1
    //   199: ifnull +8 -> 207
    //   202: aload_1
    //   203: invokevirtual 231	android/content/ContentProviderClient:release	()Z
    //   206: pop
    //   207: aload_0
    //   208: athrow
    //   209: aload_2
    //   210: astore_0
    //   211: aload_2
    //   212: astore_1
    //   213: ldc 61
    //   215: ldc_w 537
    //   218: invokestatic 178	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   221: goto -173 -> 48
    // Local variable table:
    //   start	length	slot	name	signature
    //   19	166	0	localObject1	Object
    //   197	11	0	localObject2	Object
    //   210	1	0	localObject3	Object
    //   17	196	1	localObject4	Object
    //   31	125	2	localContentProviderClient	ContentProviderClient
    //   160	52	2	localThrowable	Throwable
    //   7	175	3	localSparseIntArray	android.util.SparseIntArray
    //   14	174	4	localObject5	Object
    //   140	18	5	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   95	137	140	finally
    //   20	32	160	java/lang/Throwable
    //   40	48	160	java/lang/Throwable
    //   63	90	160	java/lang/Throwable
    //   146	153	160	java/lang/Throwable
    //   157	160	160	java/lang/Throwable
    //   187	194	160	java/lang/Throwable
    //   213	221	160	java/lang/Throwable
    //   20	32	197	finally
    //   40	48	197	finally
    //   63	90	197	finally
    //   146	153	197	finally
    //   157	160	197	finally
    //   163	172	197	finally
    //   187	194	197	finally
    //   213	221	197	finally
  }
  
  /* Error */
  private static ArrayList<String> getRecycledMediaListFromGallery()
  {
    // Byte code:
    //   0: new 145	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 148	java/util/ArrayList:<init>	()V
    //   7: astore_3
    //   8: ldc_w 539
    //   11: invokestatic 141	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   14: astore 4
    //   16: aconst_null
    //   17: astore_1
    //   18: aconst_null
    //   19: astore_0
    //   20: getstatic 170	com/oneplus/camera/media/MediaListManager:m_Context	Landroid/content/Context;
    //   23: invokevirtual 201	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   26: aload 4
    //   28: invokevirtual 207	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
    //   31: astore_2
    //   32: aload_2
    //   33: ifnonnull +26 -> 59
    //   36: aload_2
    //   37: astore_0
    //   38: aload_2
    //   39: astore_1
    //   40: ldc 61
    //   42: ldc_w 541
    //   45: invokestatic 178	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   48: aload_2
    //   49: ifnull +8 -> 57
    //   52: aload_2
    //   53: invokevirtual 231	android/content/ContentProviderClient:release	()Z
    //   56: pop
    //   57: aload_3
    //   58: areturn
    //   59: aload_2
    //   60: astore_0
    //   61: aload_2
    //   62: astore_1
    //   63: aload_2
    //   64: aload 4
    //   66: iconst_1
    //   67: anewarray 107	java/lang/String
    //   70: dup
    //   71: iconst_0
    //   72: ldc_w 543
    //   75: aastore
    //   76: aconst_null
    //   77: aconst_null
    //   78: aconst_null
    //   79: invokevirtual 215	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   82: astore 4
    //   84: aload 4
    //   86: ifnull +130 -> 216
    //   89: aload 4
    //   91: invokeinterface 221 1 0
    //   96: ifeq +94 -> 190
    //   99: ldc 61
    //   101: new 349	java/lang/StringBuilder
    //   104: dup
    //   105: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   108: ldc_w 545
    //   111: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: aload 4
    //   116: iconst_0
    //   117: invokeinterface 476 2 0
    //   122: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   125: invokevirtual 362	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   128: invokestatic 279	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   131: aload_3
    //   132: aload 4
    //   134: iconst_0
    //   135: invokeinterface 476 2 0
    //   140: invokevirtual 546	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   143: pop
    //   144: goto -55 -> 89
    //   147: astore 5
    //   149: aload_2
    //   150: astore_0
    //   151: aload_2
    //   152: astore_1
    //   153: aload 4
    //   155: invokeinterface 228 1 0
    //   160: aload_2
    //   161: astore_0
    //   162: aload_2
    //   163: astore_1
    //   164: aload 5
    //   166: athrow
    //   167: astore_2
    //   168: aload_0
    //   169: astore_1
    //   170: ldc 61
    //   172: ldc_w 548
    //   175: aload_2
    //   176: invokestatic 263	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   179: aload_0
    //   180: ifnull -123 -> 57
    //   183: aload_0
    //   184: invokevirtual 231	android/content/ContentProviderClient:release	()Z
    //   187: pop
    //   188: aload_3
    //   189: areturn
    //   190: aload_2
    //   191: astore_0
    //   192: aload_2
    //   193: astore_1
    //   194: aload 4
    //   196: invokeinterface 228 1 0
    //   201: goto -153 -> 48
    //   204: astore_0
    //   205: aload_1
    //   206: ifnull +8 -> 214
    //   209: aload_1
    //   210: invokevirtual 231	android/content/ContentProviderClient:release	()Z
    //   213: pop
    //   214: aload_0
    //   215: athrow
    //   216: aload_2
    //   217: astore_0
    //   218: aload_2
    //   219: astore_1
    //   220: ldc 61
    //   222: ldc_w 550
    //   225: invokestatic 178	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   228: goto -180 -> 48
    // Local variable table:
    //   start	length	slot	name	signature
    //   19	173	0	localObject1	Object
    //   204	11	0	localObject2	Object
    //   217	1	0	localObject3	Object
    //   17	203	1	localObject4	Object
    //   31	132	2	localContentProviderClient	ContentProviderClient
    //   167	52	2	localThrowable	Throwable
    //   7	182	3	localArrayList	ArrayList
    //   14	181	4	localObject5	Object
    //   147	18	5	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   89	144	147	finally
    //   20	32	167	java/lang/Throwable
    //   40	48	167	java/lang/Throwable
    //   63	84	167	java/lang/Throwable
    //   153	160	167	java/lang/Throwable
    //   164	167	167	java/lang/Throwable
    //   194	201	167	java/lang/Throwable
    //   220	228	167	java/lang/Throwable
    //   20	32	204	finally
    //   40	48	204	finally
    //   63	84	204	finally
    //   153	160	204	finally
    //   164	167	204	finally
    //   170	179	204	finally
    //   194	201	204	finally
    //   220	228	204	finally
  }
  
  private static void handleContentThreadMessage(Message arg0)
  {
    switch (???.what)
    {
    }
    for (;;)
    {
      return;
      synchronized (m_PendingNewMedia)
      {
        NewMediaInfo localNewMediaInfo = (NewMediaInfo)m_PendingNewMedia.pollFirst();
        if (localNewMediaInfo != null) {
          addMedia(localNewMediaInfo.filePath, localNewMediaInfo.time);
        }
      }
    }
  }
  
  public static void notifyFileAdded(String paramString, long paramLong)
  {
    if (paramString == null) {
      return;
    }
    if (checkContentThread(true)) {}
    synchronized (m_PendingNewMedia)
    {
      m_PendingNewMedia.add(new NewMediaInfo(paramString, paramLong));
      m_ContentThreadHandler.sendEmptyMessage(10010);
      return;
    }
  }
  
  public static void notifyFileDeleted(String paramString)
  {
    if (paramString == null) {
      return;
    }
    if (checkContentThread(true)) {
      Message.obtain(m_ContentThreadHandler, 10011, paramString).sendToTarget();
    }
  }
  
  private static void onMediaListReleased(MediaListImpl paramMediaListImpl)
  {
    synchronized (m_Lock)
    {
      if ((m_ActiveMediaLists.remove(paramMediaListImpl)) && (m_ActiveMediaLists.isEmpty()))
      {
        Log.v("MediaListManager", "onMediaListReleased() - No active media lists, clear media list later");
        m_ContentThreadHandler.removeMessages(10002);
        m_ContentThreadHandler.sendEmptyMessageDelayed(10000, 3000L);
      }
      return;
    }
  }
  
  private static void onMediaStoreContentChanged(Uri paramUri)
  {
    if (!m_ContentThreadHandler.hasMessages(10002))
    {
      Log.v("MediaListManager", "onMediaStoreContentChanged() - Check media lists later");
      m_ContentThreadHandler.sendEmptyMessageDelayed(10002, 1000L);
    }
  }
  
  private static void prepareMediaQueryConditions(Ref<String> paramRef, Ref<String[]> paramRef1)
  {
    if ((paramRef == null) || (paramRef1 == null)) {
      return;
    }
    List localList = StorageUtils.getAllDcimPath((StorageManager)CameraApplication.current().findComponent(StorageManager.class));
    StringBuffer localStringBuffer = new StringBuffer("(media_type=1 OR media_type=3)");
    ArrayList localArrayList = new ArrayList();
    int i = localList.size() - 1;
    if (i >= 0)
    {
      if (localArrayList.isEmpty()) {
        localStringBuffer.append(" AND (_data LIKE ?");
      }
      for (;;)
      {
        localArrayList.add((String)localList.get(i) + "/%");
        i -= 1;
        break;
        localStringBuffer.append(" OR _data LIKE ?");
      }
    }
    if (!localArrayList.isEmpty()) {
      localStringBuffer.append(')');
    }
    paramRef.set(localStringBuffer.toString());
    paramRef1.set((String[])localArrayList.toArray(new String[0]));
  }
  
  private static void refreshMediaLists()
  {
    clearMediaLists();
    m_ContentThreadHandler.removeMessages(10002);
    long l1 = SystemClock.elapsedRealtime();
    ContentProviderClient localContentProviderClient = null;
    LinkedList localLinkedList = null;
    int i = 0;
    Object localObject6 = localLinkedList;
    Object localObject5 = localContentProviderClient;
    for (;;)
    {
      Uri localUri;
      try
      {
        localUri = MediaStore.Files.getContentUri("external");
        localObject6 = localLinkedList;
        localObject5 = localContentProviderClient;
        localContentProviderClient = m_Context.getContentResolver().acquireUnstableContentProviderClient(localUri);
        localObject6 = localContentProviderClient;
        localObject5 = localContentProviderClient;
        if (m_ContentThreadHandler.hasMessages(10000))
        {
          localObject6 = localContentProviderClient;
          localObject5 = localContentProviderClient;
          Log.w("MediaListManager", "refreshMediaLists() - Cancelled");
          return;
        }
        localObject6 = localContentProviderClient;
        localObject5 = localContentProviderClient;
        if (m_ActiveMediaLists.size() == 1)
        {
          localObject6 = localContentProviderClient;
          localObject5 = localContentProviderClient;
          if (((MediaListImpl)m_ActiveMediaLists.get(0)).isSecureMode)
          {
            localObject6 = localContentProviderClient;
            localObject5 = localContentProviderClient;
            Log.w("MediaListManager", "refreshMediaLists() - List is secureMode. Cancelled");
            return;
          }
        }
        localObject6 = localContentProviderClient;
        localObject5 = localContentProviderClient;
        localLinkedList = m_PendingNewMedia;
        localObject6 = localContentProviderClient;
        localObject5 = localContentProviderClient;
        NewMediaInfo localNewMediaInfo;
        localObject6 = localContentProviderClient;
      }
      catch (Throwable localObject1) {}finally
      {
        if (localObject5 != null) {
          ((ContentProviderClient)localObject5).release();
        }
        Log.v("MediaListManager", "refreshMediaLists() - " + (SystemClock.elapsedRealtime() - l1) + " ms to refresh media lists");
      }
      localObject5 = localContentProviderClient;
      ArrayList localArrayList = getRecycledMediaListFromGallery();
      localObject6 = localContentProviderClient;
      localObject5 = localContentProviderClient;
      Object localObject10 = new SimpleRef();
      localObject6 = localContentProviderClient;
      localObject5 = localContentProviderClient;
      SimpleRef localSimpleRef = new SimpleRef();
      localObject6 = localContentProviderClient;
      localObject5 = localContentProviderClient;
      prepareMediaQueryConditions((Ref)localObject10, localSimpleRef);
      localObject6 = localContentProviderClient;
      localObject5 = localContentProviderClient;
      long l2 = SystemClock.elapsedRealtime();
      Object localObject2 = null;
      Object localObject9 = null;
      localObject6 = localContentProviderClient;
      localObject5 = localContentProviderClient;
      localObject10 = localContentProviderClient.query(localUri, MEDIA_COLUMNS, (String)((Ref)localObject10).get(), (String[])localSimpleRef.get(), "datetaken DESC ,_data DESC LIMIT 64 OFFSET " + i);
      int j = 0;
      if (localObject10 != null)
      {
        localObject2 = localObject9;
        for (;;)
        {
          try
          {
            if (!((Cursor)localObject10).moveToNext()) {
              continue;
            }
            localObject5 = createMediaInfo(localUri, (Cursor)localObject10);
            if (localObject5 == null) {
              continue;
            }
            if (localObject2 != null) {
              continue;
            }
            localObject2 = new ArrayList();
          }
          finally
          {
            long l3;
            continue;
            continue;
          }
          try
          {
            if (localArrayList.contains(((MediaInfo)localObject5).getContentUri().toString())) {
              Log.v("MediaListManager", "refreshMediaLists() - media is recycled : " + ((MediaInfo)localObject5).getContentUri());
            } else {
              ((List)localObject2).add(localObject5);
            }
          }
          finally {}
        }
        localObject6 = localContentProviderClient;
        localObject5 = localContentProviderClient;
        ((Cursor)localObject10).close();
        localObject6 = localContentProviderClient;
        localObject5 = localContentProviderClient;
        throw localCollection;
        j = ((Cursor)localObject10).getCount();
        localObject6 = localContentProviderClient;
        localObject5 = localContentProviderClient;
        ((Cursor)localObject10).close();
      }
      if (j > 0)
      {
        j = i + j;
        localObject6 = localContentProviderClient;
        localObject5 = localContentProviderClient;
        l3 = SystemClock.elapsedRealtime();
        localObject6 = localContentProviderClient;
        localObject5 = localContentProviderClient;
        localObject9 = new StringBuilder().append("refreshMediaLists() - Take ").append(l3 - l2).append(" ms to create ");
        if (localCollection != null)
        {
          localObject6 = localContentProviderClient;
          localObject5 = localContentProviderClient;
          i = localCollection.size();
          localObject6 = localContentProviderClient;
          localObject5 = localContentProviderClient;
          Log.v("MediaListManager", i + " media info");
          i = j;
          if (localCollection != null)
          {
            localObject6 = localContentProviderClient;
            localObject5 = localContentProviderClient;
            localObject9 = m_Lock;
            localObject6 = localContentProviderClient;
            localObject5 = localContentProviderClient;
          }
        }
        else
        {
          try
          {
            m_BaseMediaSet.addAll(localCollection);
            i = m_ActiveMediaLists.size() - 1;
            for (;;)
            {
              if (i >= 0)
              {
                localObject5 = (MediaListImpl)m_ActiveMediaLists.get(i);
                if (!((MediaListImpl)localObject5).isSecureMode) {
                  ((MediaListImpl)localObject5).addMedia(localCollection, true);
                }
                i -= 1;
                continue;
                i = 0;
                break;
              }
            }
            localObject6 = localContentProviderClient;
            localObject5 = localContentProviderClient;
            i = j;
            continue;
          }
          finally
          {
            localObject6 = localContentProviderClient;
            localObject5 = localContentProviderClient;
            localObject6 = localContentProviderClient;
            localObject5 = localContentProviderClient;
          }
        }
      }
      else
      {
        if (localContentProviderClient != null) {
          localContentProviderClient.release();
        }
        Log.v("MediaListManager", "refreshMediaLists() - " + (SystemClock.elapsedRealtime() - l1) + " ms to refresh media lists");
      }
    }
  }
  
  private static void removeMedia(String paramString)
  {
    Object localObject3 = m_Lock;
    Object localObject2 = null;
    try
    {
      Iterator localIterator = m_BaseMediaSet.iterator();
      Object localObject1;
      do
      {
        localObject1 = localObject2;
        if (!localIterator.hasNext()) {
          break;
        }
        localObject1 = (MediaInfo)localIterator.next();
      } while (!paramString.equals(((MediaInfo)localObject1).getFilePath()));
      m_BaseMediaSet.remove(localObject1);
      if (localObject1 != null)
      {
        int i = m_ActiveMediaLists.size() - 1;
        while (i >= 0)
        {
          MediaListImpl.-wrap3((MediaListImpl)m_ActiveMediaLists.get(i), (MediaInfo)localObject1);
          i -= 1;
        }
      }
      return;
    }
    finally {}
  }
  
  private static final class MediaListImpl
    extends ListHandlerBaseObject<MediaInfo>
    implements MediaList
  {
    public final long creationTime = SystemClock.elapsedRealtime();
    public final boolean isSecureMode;
    private final List<MediaInfo> m_List = new ArrayList();
    
    public MediaListImpl(boolean paramBoolean)
    {
      this.isSecureMode = paramBoolean;
    }
    
    private void onMediaAdded(Collection<MediaInfo> paramCollection, boolean paramBoolean)
    {
      if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
        return;
      }
      Object localObject;
      if ((this.m_List.isEmpty()) && (paramBoolean))
      {
        localObject = MediaListChangeEventArgs.obtain(this.m_List.size(), this.m_List.size() + paramCollection.size() - 1);
        this.m_List.addAll(paramCollection);
        raise(EVENT_MEDIA_ADDED, (EventArgs)localObject);
        ((MediaListChangeEventArgs)localObject).recycle();
        return;
      }
      int j = -1;
      int i = -1;
      paramCollection = paramCollection.iterator();
      while (paramCollection.hasNext())
      {
        localObject = (MediaInfo)paramCollection.next();
        int k = Collections.binarySearch(this.m_List, localObject, MediaListManager.-get0());
        if (k < 0)
        {
          k = k;
          int n = i;
          int m = j;
          if (j >= 0) {
            if (k >= j)
            {
              n = i;
              m = j;
              if (k <= i + 1) {}
            }
            else
            {
              MediaListChangeEventArgs localMediaListChangeEventArgs = MediaListChangeEventArgs.obtain(j, i);
              raise(EVENT_MEDIA_ADDED, localMediaListChangeEventArgs);
              localMediaListChangeEventArgs.recycle();
              m = -1;
              n = -1;
            }
          }
          this.m_List.add(k, localObject);
          if (m >= 0)
          {
            if (k >= m)
            {
              i = n + 1;
              j = m;
            }
            else
            {
              j = m - 1;
              i = n;
            }
          }
          else
          {
            i = k;
            j = k;
          }
        }
      }
      if (j >= 0)
      {
        paramCollection = MediaListChangeEventArgs.obtain(j, i);
        raise(EVENT_MEDIA_ADDED, paramCollection);
        paramCollection.recycle();
      }
    }
    
    private void onMediaCleared()
    {
      if (!this.m_List.isEmpty())
      {
        MediaListChangeEventArgs localMediaListChangeEventArgs = MediaListChangeEventArgs.obtain(0, this.m_List.size() - 1);
        raise(EVENT_MEDIA_REMOVING, localMediaListChangeEventArgs);
        this.m_List.clear();
        raise(EVENT_MEDIA_REMOVED, localMediaListChangeEventArgs);
        localMediaListChangeEventArgs.recycle();
      }
    }
    
    private boolean onMediaRemoved(Object... paramVarArgs)
    {
      if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
        return false;
      }
      if ((paramVarArgs[0] instanceof MediaInfo))
      {
        i = this.m_List.indexOf(paramVarArgs[0]);
        if (i >= 0)
        {
          paramVarArgs = MediaListChangeEventArgs.obtain(i);
          raise(EVENT_MEDIA_REMOVING, paramVarArgs);
          this.m_List.remove(i);
          raise(EVENT_MEDIA_REMOVED, paramVarArgs);
          paramVarArgs.recycle();
          return true;
        }
        return false;
      }
      boolean bool1 = false;
      paramVarArgs = (List)paramVarArgs[0];
      int i1 = -1;
      int i = -1;
      int n = paramVarArgs.size() - 1;
      if (n >= 0)
      {
        int m = this.m_List.indexOf(paramVarArgs.get(n));
        int j = i;
        boolean bool2 = bool1;
        int k = i1;
        if (m >= 0)
        {
          if (i1 >= 0) {
            break label183;
          }
          k = m;
          j = m;
          bool2 = bool1;
        }
        for (;;)
        {
          n -= 1;
          i = j;
          bool1 = bool2;
          i1 = k;
          break;
          label183:
          if (m == i + 1)
          {
            j = i + 1;
            bool2 = bool1;
            k = i1;
          }
          else if (m == i1 - 1)
          {
            k = i1 - 1;
            j = i;
            bool2 = bool1;
          }
          else
          {
            j = m;
            if (m > i) {
              j = m - (i - i1 + 1);
            }
            MediaListChangeEventArgs localMediaListChangeEventArgs = MediaListChangeEventArgs.obtain(i1, i);
            raise(EVENT_MEDIA_REMOVING, localMediaListChangeEventArgs);
            while (i >= i1)
            {
              this.m_List.remove(i);
              i -= 1;
            }
            raise(EVENT_MEDIA_REMOVED, localMediaListChangeEventArgs);
            localMediaListChangeEventArgs.recycle();
            bool2 = true;
            k = j;
          }
        }
      }
      if (i1 >= 0)
      {
        paramVarArgs = MediaListChangeEventArgs.obtain(i1, i);
        raise(EVENT_MEDIA_REMOVING, paramVarArgs);
        while (i >= i1)
        {
          this.m_List.remove(i);
          i -= 1;
        }
        raise(EVENT_MEDIA_REMOVED, paramVarArgs);
        paramVarArgs.recycle();
        bool1 = true;
      }
      return bool1;
    }
    
    private void removeMedia(final MediaInfo paramMediaInfo)
    {
      if (!isDependencyThread())
      {
        if (!getHandler().post(new Runnable()
        {
          public void run()
          {
            MediaListManager.MediaListImpl.-wrap0(MediaListManager.MediaListImpl.this, new Object[] { paramMediaInfo });
          }
        })) {
          Log.e(this.TAG, "removeMedia() - Fail to perform cross-thread operation");
        }
        return;
      }
      onMediaRemoved(new Object[] { paramMediaInfo });
    }
    
    private void removeMedia(final List<MediaInfo> paramList)
    {
      if (!isDependencyThread())
      {
        if (!getHandler().post(new Runnable()
        {
          public void run()
          {
            MediaListManager.MediaListImpl.-wrap0(MediaListManager.MediaListImpl.this, new Object[] { paramList });
          }
        })) {
          Log.e(this.TAG, "removeMedia() - Fail to perform cross-thread operation");
        }
        return;
      }
      onMediaRemoved(new Object[] { paramList });
    }
    
    public boolean add(MediaInfo paramMediaInfo)
    {
      verifyAccess();
      int i = Collections.binarySearch(this.m_List, paramMediaInfo, MediaListManager.-get0());
      if (i < 0)
      {
        this.m_List.add(i, paramMediaInfo);
        paramMediaInfo = MediaListChangeEventArgs.obtain(i);
        raise(EVENT_MEDIA_ADDED, paramMediaInfo);
        paramMediaInfo.recycle();
        return true;
      }
      return false;
    }
    
    public void addMedia(final MediaInfo paramMediaInfo)
    {
      if (!isDependencyThread())
      {
        if (!getHandler().post(new Runnable()
        {
          public void run()
          {
            MediaListManager.MediaListImpl.this.add(paramMediaInfo);
          }
        })) {
          Log.e(this.TAG, "addMedia() - Fail to perform cross-thread operation");
        }
        return;
      }
      add(paramMediaInfo);
    }
    
    public void addMedia(final Collection<MediaInfo> paramCollection, final boolean paramBoolean)
    {
      if (!isDependencyThread())
      {
        if (!getHandler().post(new Runnable()
        {
          public void run()
          {
            MediaListManager.MediaListImpl.-wrap1(MediaListManager.MediaListImpl.this, paramCollection, paramBoolean);
          }
        })) {
          Log.e(this.TAG, "addMedia() - Fail to perform cross-thread operation");
        }
        return;
      }
      onMediaAdded(paramCollection, paramBoolean);
    }
    
    public void clearMedia()
    {
      if (!isDependencyThread())
      {
        if (!getHandler().post(new Runnable()
        {
          public void run()
          {
            MediaListManager.MediaListImpl.-wrap2(MediaListManager.MediaListImpl.this);
          }
        })) {
          Log.e(this.TAG, "clearMedia() - Fail to perform cross-thread operation");
        }
        return;
      }
      onMediaCleared();
    }
    
    public boolean equals(Object paramObject)
    {
      return paramObject == this;
    }
    
    public MediaInfo get(int paramInt)
    {
      return (MediaInfo)this.m_List.get(paramInt);
    }
    
    public void release()
    {
      super.release();
      onMediaCleared();
      MediaListManager.-wrap1(this);
    }
    
    public MediaInfo remove(int paramInt)
    {
      verifyAccess();
      MediaListChangeEventArgs localMediaListChangeEventArgs = MediaListChangeEventArgs.obtain(paramInt);
      raise(EVENT_MEDIA_REMOVING, localMediaListChangeEventArgs);
      MediaInfo localMediaInfo = (MediaInfo)this.m_List.remove(paramInt);
      raise(EVENT_MEDIA_REMOVED, localMediaListChangeEventArgs);
      localMediaListChangeEventArgs.recycle();
      return localMediaInfo;
    }
    
    public boolean remove(Object paramObject)
    {
      verifyAccess();
      return onMediaRemoved(new Object[] { (MediaInfo)paramObject });
    }
    
    public boolean replace(MediaInfo paramMediaInfo1, MediaInfo paramMediaInfo2)
    {
      verifyAccess();
      int k = Collections.binarySearch(this.m_List, paramMediaInfo1, MediaListManager.-get0());
      int i = Collections.binarySearch(this.m_List, paramMediaInfo2, MediaListManager.-get0());
      Log.v(this.TAG, "replace() - Index changed from ", Integer.valueOf(k), " to ", Integer.valueOf(i), ", media : ", paramMediaInfo2);
      if (k == i)
      {
        if (k < 0) {
          return false;
        }
        paramMediaInfo1 = MediaListChangeEventArgs.obtain(i);
        raise(EVENT_MEDIA_REPLACING, paramMediaInfo1);
        this.m_List.set(k, paramMediaInfo2);
        raise(EVENT_MEDIA_REPLACED, paramMediaInfo1);
        paramMediaInfo1.recycle();
        return true;
      }
      int j = i;
      i = j;
      if (k >= 0)
      {
        i = j;
        if (k < j) {
          i = j - 1;
        }
        paramMediaInfo1 = MediaListChangeEventArgs.obtain(k);
        raise(EVENT_MEDIA_REMOVING, paramMediaInfo1);
        this.m_List.remove(k);
        raise(EVENT_MEDIA_REMOVED, paramMediaInfo1);
        paramMediaInfo1.recycle();
      }
      if (i < 0) {
        return false;
      }
      paramMediaInfo1 = MediaListChangeEventArgs.obtain(i);
      this.m_List.add(i, paramMediaInfo2);
      raise(EVENT_MEDIA_ADDED, paramMediaInfo1);
      paramMediaInfo1.recycle();
      return true;
    }
    
    public void replaceMedia(final MediaInfo paramMediaInfo1, final MediaInfo paramMediaInfo2)
    {
      if (!isDependencyThread())
      {
        if (!getHandler().post(new Runnable()
        {
          public void run()
          {
            MediaListManager.MediaListImpl.this.replace(paramMediaInfo1, paramMediaInfo2);
          }
        })) {
          Log.e(this.TAG, "replaceMedia() - Fail to perform cross-thread operation");
        }
        return;
      }
      replace(paramMediaInfo1, paramMediaInfo2);
    }
    
    public int size()
    {
      return this.m_List.size();
    }
  }
  
  private static final class NewMediaInfo
  {
    public final String filePath;
    public final long time;
    
    public NewMediaInfo(String paramString, long paramLong)
    {
      this.filePath = paramString;
      this.time = paramLong;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/MediaListManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */