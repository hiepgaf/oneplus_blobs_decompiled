package android.media;

import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pair;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

public abstract class SubtitleTrack
  implements MediaTimeProvider.OnMediaTimeListener
{
  private static final String TAG = "SubtitleTrack";
  public boolean DEBUG = false;
  protected final Vector<Cue> mActiveCues = new Vector();
  protected CueList mCues;
  private MediaFormat mFormat;
  protected Handler mHandler = new Handler();
  private long mLastTimeMs;
  private long mLastUpdateTimeMs;
  private long mNextScheduledTimeMs = -1L;
  private Runnable mRunnable;
  protected final LongSparseArray<Run> mRunsByEndTime = new LongSparseArray();
  protected final LongSparseArray<Run> mRunsByID = new LongSparseArray();
  protected MediaTimeProvider mTimeProvider;
  protected boolean mVisible;
  
  public SubtitleTrack(MediaFormat paramMediaFormat)
  {
    this.mFormat = paramMediaFormat;
    this.mCues = new CueList();
    clearActiveCues();
    this.mLastTimeMs = -1L;
  }
  
  private void removeRunsByEndTimeIndex(int paramInt)
  {
    Object localObject2;
    for (Object localObject1 = (Run)this.mRunsByEndTime.valueAt(paramInt); localObject1 != null; localObject1 = localObject2)
    {
      Cue localCue;
      for (localObject2 = ((Run)localObject1).mFirstCue; localObject2 != null; localObject2 = localCue)
      {
        this.mCues.remove((Cue)localObject2);
        localCue = ((Cue)localObject2).mNextInRun;
        ((Cue)localObject2).mNextInRun = null;
      }
      this.mRunsByID.remove(((Run)localObject1).mRunID);
      localObject2 = ((Run)localObject1).mNextRunAtEndTimeMs;
      ((Run)localObject1).mPrevRunAtEndTimeMs = null;
      ((Run)localObject1).mNextRunAtEndTimeMs = null;
    }
    this.mRunsByEndTime.removeAt(paramInt);
  }
  
  private void takeTime(long paramLong)
  {
    try
    {
      this.mLastTimeMs = paramLong;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  protected boolean addCue(Cue paramCue)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 94	android/media/SubtitleTrack:mCues	Landroid/media/SubtitleTrack$CueList;
    //   6: aload_1
    //   7: invokevirtual 140	android/media/SubtitleTrack$CueList:add	(Landroid/media/SubtitleTrack$Cue;)V
    //   10: aload_1
    //   11: getfield 141	android/media/SubtitleTrack$Cue:mRunID	J
    //   14: lconst_0
    //   15: lcmp
    //   16: ifeq +71 -> 87
    //   19: aload_0
    //   20: getfield 73	android/media/SubtitleTrack:mRunsByID	Landroid/util/LongSparseArray;
    //   23: aload_1
    //   24: getfield 141	android/media/SubtitleTrack$Cue:mRunID	J
    //   27: invokevirtual 145	android/util/LongSparseArray:get	(J)Ljava/lang/Object;
    //   30: checkcast 27	android/media/SubtitleTrack$Run
    //   33: astore 7
    //   35: aload 7
    //   37: ifnonnull +261 -> 298
    //   40: new 27	android/media/SubtitleTrack$Run
    //   43: dup
    //   44: aconst_null
    //   45: invokespecial 148	android/media/SubtitleTrack$Run:<init>	(Landroid/media/SubtitleTrack$Run;)V
    //   48: astore 6
    //   50: aload_0
    //   51: getfield 73	android/media/SubtitleTrack:mRunsByID	Landroid/util/LongSparseArray;
    //   54: aload_1
    //   55: getfield 141	android/media/SubtitleTrack$Cue:mRunID	J
    //   58: aload 6
    //   60: invokevirtual 152	android/util/LongSparseArray:put	(JLjava/lang/Object;)V
    //   63: aload 6
    //   65: aload_1
    //   66: getfield 155	android/media/SubtitleTrack$Cue:mEndTimeMs	J
    //   69: putfield 156	android/media/SubtitleTrack$Run:mEndTimeMs	J
    //   72: aload_1
    //   73: aload 6
    //   75: getfield 109	android/media/SubtitleTrack$Run:mFirstCue	Landroid/media/SubtitleTrack$Cue;
    //   78: putfield 116	android/media/SubtitleTrack$Cue:mNextInRun	Landroid/media/SubtitleTrack$Cue;
    //   81: aload 6
    //   83: aload_1
    //   84: putfield 109	android/media/SubtitleTrack$Run:mFirstCue	Landroid/media/SubtitleTrack$Cue;
    //   87: ldc2_w 86
    //   90: lstore 4
    //   92: aload_0
    //   93: getfield 158	android/media/SubtitleTrack:mTimeProvider	Landroid/media/MediaTimeProvider;
    //   96: astore 6
    //   98: lload 4
    //   100: lstore_2
    //   101: aload 6
    //   103: ifnull +19 -> 122
    //   106: aload_0
    //   107: getfield 158	android/media/SubtitleTrack:mTimeProvider	Landroid/media/MediaTimeProvider;
    //   110: iconst_0
    //   111: iconst_1
    //   112: invokeinterface 164 3 0
    //   117: ldc2_w 165
    //   120: ldiv
    //   121: lstore_2
    //   122: aload_0
    //   123: getfield 80	android/media/SubtitleTrack:DEBUG	Z
    //   126: ifeq +76 -> 202
    //   129: ldc 32
    //   131: new 168	java/lang/StringBuilder
    //   134: dup
    //   135: invokespecial 169	java/lang/StringBuilder:<init>	()V
    //   138: ldc -85
    //   140: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: aload_0
    //   144: getfield 177	android/media/SubtitleTrack:mVisible	Z
    //   147: invokevirtual 180	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   150: ldc -74
    //   152: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   155: aload_1
    //   156: getfield 185	android/media/SubtitleTrack$Cue:mStartTimeMs	J
    //   159: invokevirtual 188	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   162: ldc -66
    //   164: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   167: lload_2
    //   168: invokevirtual 188	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   171: ldc -74
    //   173: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   176: aload_1
    //   177: getfield 155	android/media/SubtitleTrack$Cue:mEndTimeMs	J
    //   180: invokevirtual 188	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   183: ldc -64
    //   185: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: aload_0
    //   189: getfield 99	android/media/SubtitleTrack:mLastTimeMs	J
    //   192: invokevirtual 188	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   195: invokevirtual 196	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   198: invokestatic 202	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   201: pop
    //   202: aload_0
    //   203: getfield 177	android/media/SubtitleTrack:mVisible	Z
    //   206: ifeq +148 -> 354
    //   209: aload_1
    //   210: getfield 185	android/media/SubtitleTrack$Cue:mStartTimeMs	J
    //   213: lload_2
    //   214: lcmp
    //   215: ifgt +139 -> 354
    //   218: aload_1
    //   219: getfield 155	android/media/SubtitleTrack$Cue:mEndTimeMs	J
    //   222: aload_0
    //   223: getfield 99	android/media/SubtitleTrack:mLastTimeMs	J
    //   226: lcmp
    //   227: iflt +127 -> 354
    //   230: aload_0
    //   231: getfield 60	android/media/SubtitleTrack:mRunnable	Ljava/lang/Runnable;
    //   234: ifnull +14 -> 248
    //   237: aload_0
    //   238: getfield 85	android/media/SubtitleTrack:mHandler	Landroid/os/Handler;
    //   241: aload_0
    //   242: getfield 60	android/media/SubtitleTrack:mRunnable	Ljava/lang/Runnable;
    //   245: invokevirtual 206	android/os/Handler:removeCallbacks	(Ljava/lang/Runnable;)V
    //   248: aload_0
    //   249: new 8	android/media/SubtitleTrack$1
    //   252: dup
    //   253: aload_0
    //   254: aload_0
    //   255: lload_2
    //   256: invokespecial 209	android/media/SubtitleTrack$1:<init>	(Landroid/media/SubtitleTrack;Landroid/media/SubtitleTrack;J)V
    //   259: putfield 60	android/media/SubtitleTrack:mRunnable	Ljava/lang/Runnable;
    //   262: aload_0
    //   263: getfield 85	android/media/SubtitleTrack:mHandler	Landroid/os/Handler;
    //   266: aload_0
    //   267: getfield 60	android/media/SubtitleTrack:mRunnable	Ljava/lang/Runnable;
    //   270: ldc2_w 210
    //   273: invokevirtual 215	android/os/Handler:postDelayed	(Ljava/lang/Runnable;J)Z
    //   276: ifeq +60 -> 336
    //   279: aload_0
    //   280: getfield 80	android/media/SubtitleTrack:DEBUG	Z
    //   283: ifeq +11 -> 294
    //   286: ldc 32
    //   288: ldc -39
    //   290: invokestatic 202	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   293: pop
    //   294: aload_0
    //   295: monitorexit
    //   296: iconst_1
    //   297: ireturn
    //   298: aload 7
    //   300: astore 6
    //   302: aload 7
    //   304: getfield 156	android/media/SubtitleTrack$Run:mEndTimeMs	J
    //   307: aload_1
    //   308: getfield 155	android/media/SubtitleTrack$Cue:mEndTimeMs	J
    //   311: lcmp
    //   312: ifge -240 -> 72
    //   315: aload 7
    //   317: aload_1
    //   318: getfield 155	android/media/SubtitleTrack$Cue:mEndTimeMs	J
    //   321: putfield 156	android/media/SubtitleTrack$Run:mEndTimeMs	J
    //   324: aload 7
    //   326: astore 6
    //   328: goto -256 -> 72
    //   331: astore_1
    //   332: aload_0
    //   333: monitorexit
    //   334: aload_1
    //   335: athrow
    //   336: aload_0
    //   337: getfield 80	android/media/SubtitleTrack:DEBUG	Z
    //   340: ifeq -46 -> 294
    //   343: ldc 32
    //   345: ldc -37
    //   347: invokestatic 222	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   350: pop
    //   351: goto -57 -> 294
    //   354: aload_0
    //   355: getfield 177	android/media/SubtitleTrack:mVisible	Z
    //   358: ifeq +40 -> 398
    //   361: aload_1
    //   362: getfield 155	android/media/SubtitleTrack$Cue:mEndTimeMs	J
    //   365: aload_0
    //   366: getfield 99	android/media/SubtitleTrack:mLastTimeMs	J
    //   369: lcmp
    //   370: iflt +28 -> 398
    //   373: aload_1
    //   374: getfield 185	android/media/SubtitleTrack$Cue:mStartTimeMs	J
    //   377: aload_0
    //   378: getfield 89	android/media/SubtitleTrack:mNextScheduledTimeMs	J
    //   381: lcmp
    //   382: iflt +12 -> 394
    //   385: aload_0
    //   386: getfield 89	android/media/SubtitleTrack:mNextScheduledTimeMs	J
    //   389: lconst_0
    //   390: lcmp
    //   391: ifge +7 -> 398
    //   394: aload_0
    //   395: invokevirtual 225	android/media/SubtitleTrack:scheduleTimedEvents	()V
    //   398: aload_0
    //   399: monitorexit
    //   400: iconst_0
    //   401: ireturn
    //   402: astore 6
    //   404: lload 4
    //   406: lstore_2
    //   407: goto -285 -> 122
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	410	0	this	SubtitleTrack
    //   0	410	1	paramCue	Cue
    //   100	307	2	l1	long
    //   90	315	4	l2	long
    //   48	279	6	localObject	Object
    //   402	1	6	localIllegalStateException	IllegalStateException
    //   33	292	7	localRun	Run
    // Exception table:
    //   from	to	target	type
    //   2	35	331	finally
    //   40	72	331	finally
    //   72	87	331	finally
    //   92	98	331	finally
    //   106	122	331	finally
    //   122	202	331	finally
    //   202	248	331	finally
    //   248	294	331	finally
    //   302	324	331	finally
    //   336	351	331	finally
    //   354	394	331	finally
    //   394	398	331	finally
    //   106	122	402	java/lang/IllegalStateException
  }
  
  protected void clearActiveCues()
  {
    try
    {
      if (this.DEBUG) {
        Log.v("SubtitleTrack", "Clearing " + this.mActiveCues.size() + " active cues");
      }
      this.mActiveCues.clear();
      this.mLastUpdateTimeMs = -1L;
      return;
    }
    finally {}
  }
  
  protected void finalize()
    throws Throwable
  {
    int i = this.mRunsByEndTime.size() - 1;
    while (i >= 0)
    {
      removeRunsByEndTimeIndex(i);
      i -= 1;
    }
    super.finalize();
  }
  
  protected void finishedRun(long paramLong)
  {
    if ((paramLong != 0L) && (paramLong != -1L))
    {
      Run localRun = (Run)this.mRunsByID.get(paramLong);
      if (localRun != null) {
        localRun.storeByEndTimeMs(this.mRunsByEndTime);
      }
    }
  }
  
  public final MediaFormat getFormat()
  {
    return this.mFormat;
  }
  
  public abstract RenderingWidget getRenderingWidget();
  
  public int getTrackType()
  {
    if (getRenderingWidget() == null) {
      return 3;
    }
    return 4;
  }
  
  public void hide()
  {
    if (!this.mVisible) {
      return;
    }
    if (this.mTimeProvider != null) {
      this.mTimeProvider.cancelNotifications(this);
    }
    RenderingWidget localRenderingWidget = getRenderingWidget();
    if (localRenderingWidget != null) {
      localRenderingWidget.setVisible(false);
    }
    this.mVisible = false;
  }
  
  protected void onData(SubtitleData paramSubtitleData)
  {
    long l = paramSubtitleData.getStartTimeUs() + 1L;
    onData(paramSubtitleData.getData(), true, l);
    setRunDiscardTimeMs(l, (paramSubtitleData.getStartTimeUs() + paramSubtitleData.getDurationUs()) / 1000L);
  }
  
  public abstract void onData(byte[] paramArrayOfByte, boolean paramBoolean, long paramLong);
  
  public void onSeek(long paramLong)
  {
    if (this.DEBUG) {
      Log.d("SubtitleTrack", "onSeek " + paramLong);
    }
    try
    {
      paramLong /= 1000L;
      updateActiveCues(true, paramLong);
      takeTime(paramLong);
      updateView(this.mActiveCues);
      scheduleTimedEvents();
      return;
    }
    finally {}
  }
  
  public void onStop()
  {
    try
    {
      if (this.DEBUG) {
        Log.d("SubtitleTrack", "onStop");
      }
      clearActiveCues();
      this.mLastTimeMs = -1L;
      updateView(this.mActiveCues);
      this.mNextScheduledTimeMs = -1L;
      this.mTimeProvider.notifyAt(-1L, this);
      return;
    }
    finally {}
  }
  
  public void onTimedEvent(long paramLong)
  {
    if (this.DEBUG) {
      Log.d("SubtitleTrack", "onTimedEvent " + paramLong);
    }
    try
    {
      paramLong /= 1000L;
      updateActiveCues(false, paramLong);
      takeTime(paramLong);
      updateView(this.mActiveCues);
      scheduleTimedEvents();
      return;
    }
    finally {}
  }
  
  protected void scheduleTimedEvents()
  {
    MediaTimeProvider localMediaTimeProvider;
    if (this.mTimeProvider != null)
    {
      this.mNextScheduledTimeMs = this.mCues.nextTimeAfter(this.mLastTimeMs);
      if (this.DEBUG) {
        Log.d("SubtitleTrack", "sched @" + this.mNextScheduledTimeMs + " after " + this.mLastTimeMs);
      }
      localMediaTimeProvider = this.mTimeProvider;
      if (this.mNextScheduledTimeMs < 0L) {
        break label103;
      }
    }
    label103:
    for (long l = this.mNextScheduledTimeMs * 1000L;; l = -1L)
    {
      localMediaTimeProvider.notifyAt(l, this);
      return;
    }
  }
  
  public void setRunDiscardTimeMs(long paramLong1, long paramLong2)
  {
    if ((paramLong1 != 0L) && (paramLong1 != -1L))
    {
      Run localRun = (Run)this.mRunsByID.get(paramLong1);
      if (localRun != null)
      {
        localRun.mEndTimeMs = paramLong2;
        localRun.storeByEndTimeMs(this.mRunsByEndTime);
      }
    }
  }
  
  public void setTimeProvider(MediaTimeProvider paramMediaTimeProvider)
  {
    try
    {
      MediaTimeProvider localMediaTimeProvider = this.mTimeProvider;
      if (localMediaTimeProvider == paramMediaTimeProvider) {
        return;
      }
      if (this.mTimeProvider != null) {
        this.mTimeProvider.cancelNotifications(this);
      }
      this.mTimeProvider = paramMediaTimeProvider;
      if (this.mTimeProvider != null) {
        this.mTimeProvider.scheduleUpdate(this);
      }
      return;
    }
    finally {}
  }
  
  public void show()
  {
    if (this.mVisible) {
      return;
    }
    this.mVisible = true;
    RenderingWidget localRenderingWidget = getRenderingWidget();
    if (localRenderingWidget != null) {
      localRenderingWidget.setVisible(true);
    }
    if (this.mTimeProvider != null) {
      this.mTimeProvider.scheduleUpdate(this);
    }
  }
  
  protected void updateActiveCues(boolean paramBoolean, long paramLong)
  {
    if (!paramBoolean) {}
    for (;;)
    {
      Cue localCue;
      try
      {
        if (this.mLastUpdateTimeMs > paramLong) {
          clearActiveCues();
        }
        Iterator localIterator = this.mCues.entriesBetween(this.mLastUpdateTimeMs, paramLong).iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        Pair localPair = (Pair)localIterator.next();
        localCue = (Cue)localPair.second;
        if (localCue.mEndTimeMs == ((Long)localPair.first).longValue())
        {
          if (this.DEBUG) {
            Log.v("SubtitleTrack", "Removing " + localCue);
          }
          this.mActiveCues.remove(localCue);
          if (localCue.mRunID != 0L) {
            continue;
          }
          localIterator.remove();
          continue;
        }
        if (localCue.mStartTimeMs != ((Long)localPair.first).longValue()) {
          break label242;
        }
      }
      finally {}
      if (this.DEBUG) {
        Log.v("SubtitleTrack", "Adding " + localCue);
      }
      if (localCue.mInnerTimesMs != null) {
        localCue.onTime(paramLong);
      }
      this.mActiveCues.add(localCue);
      continue;
      label242:
      if (localCue.mInnerTimesMs != null) {
        localCue.onTime(paramLong);
      }
    }
    while ((this.mRunsByEndTime.size() > 0) && (this.mRunsByEndTime.keyAt(0) <= paramLong)) {
      removeRunsByEndTimeIndex(0);
    }
    this.mLastUpdateTimeMs = paramLong;
  }
  
  public abstract void updateView(Vector<Cue> paramVector);
  
  public static class Cue
  {
    public long mEndTimeMs;
    public long[] mInnerTimesMs;
    public Cue mNextInRun;
    public long mRunID;
    public long mStartTimeMs;
    
    public void onTime(long paramLong) {}
  }
  
  static class CueList
  {
    private static final String TAG = "CueList";
    public boolean DEBUG = false;
    private SortedMap<Long, Vector<SubtitleTrack.Cue>> mCues = new TreeMap();
    
    private boolean addEvent(SubtitleTrack.Cue paramCue, long paramLong)
    {
      Vector localVector2 = (Vector)this.mCues.get(Long.valueOf(paramLong));
      Vector localVector1;
      if (localVector2 == null)
      {
        localVector1 = new Vector(2);
        this.mCues.put(Long.valueOf(paramLong), localVector1);
      }
      do
      {
        localVector1.add(paramCue);
        return true;
        localVector1 = localVector2;
      } while (!localVector2.contains(paramCue));
      return false;
    }
    
    private void removeEvent(SubtitleTrack.Cue paramCue, long paramLong)
    {
      Vector localVector = (Vector)this.mCues.get(Long.valueOf(paramLong));
      if (localVector != null)
      {
        localVector.remove(paramCue);
        if (localVector.size() == 0) {
          this.mCues.remove(Long.valueOf(paramLong));
        }
      }
    }
    
    public void add(SubtitleTrack.Cue paramCue)
    {
      if (paramCue.mStartTimeMs >= paramCue.mEndTimeMs) {
        return;
      }
      if (!addEvent(paramCue, paramCue.mStartTimeMs)) {
        return;
      }
      long l1 = paramCue.mStartTimeMs;
      if (paramCue.mInnerTimesMs != null)
      {
        long[] arrayOfLong = paramCue.mInnerTimesMs;
        int i = 0;
        int j = arrayOfLong.length;
        while (i < j)
        {
          long l3 = arrayOfLong[i];
          long l2 = l1;
          if (l3 > l1)
          {
            l2 = l1;
            if (l3 < paramCue.mEndTimeMs)
            {
              addEvent(paramCue, l3);
              l2 = l3;
            }
          }
          i += 1;
          l1 = l2;
        }
      }
      addEvent(paramCue, paramCue.mEndTimeMs);
    }
    
    public Iterable<Pair<Long, SubtitleTrack.Cue>> entriesBetween(final long paramLong1, long paramLong2)
    {
      new Iterable()
      {
        public Iterator<Pair<Long, SubtitleTrack.Cue>> iterator()
        {
          if (SubtitleTrack.CueList.this.DEBUG) {
            Log.d("CueList", "slice (" + paramLong1 + ", " + this.val$timeMs + "]=");
          }
          try
          {
            SubtitleTrack.CueList.EntryIterator localEntryIterator = new SubtitleTrack.CueList.EntryIterator(SubtitleTrack.CueList.this, SubtitleTrack.CueList.-get0(SubtitleTrack.CueList.this).subMap(Long.valueOf(paramLong1 + 1L), Long.valueOf(this.val$timeMs + 1L)));
            return localEntryIterator;
          }
          catch (IllegalArgumentException localIllegalArgumentException) {}
          return new SubtitleTrack.CueList.EntryIterator(SubtitleTrack.CueList.this, null);
        }
      };
    }
    
    public long nextTimeAfter(long paramLong)
    {
      try
      {
        SortedMap localSortedMap = this.mCues.tailMap(Long.valueOf(1L + paramLong));
        if (localSortedMap != null)
        {
          paramLong = ((Long)localSortedMap.firstKey()).longValue();
          return paramLong;
        }
        return -1L;
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
        return -1L;
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      return -1L;
    }
    
    public void remove(SubtitleTrack.Cue paramCue)
    {
      removeEvent(paramCue, paramCue.mStartTimeMs);
      if (paramCue.mInnerTimesMs != null)
      {
        long[] arrayOfLong = paramCue.mInnerTimesMs;
        int i = 0;
        int j = arrayOfLong.length;
        while (i < j)
        {
          removeEvent(paramCue, arrayOfLong[i]);
          i += 1;
        }
      }
      removeEvent(paramCue, paramCue.mEndTimeMs);
    }
    
    class EntryIterator
      implements Iterator<Pair<Long, SubtitleTrack.Cue>>
    {
      private long mCurrentTimeMs;
      private boolean mDone;
      private Pair<Long, SubtitleTrack.Cue> mLastEntry;
      private Iterator<SubtitleTrack.Cue> mLastListIterator;
      private Iterator<SubtitleTrack.Cue> mListIterator;
      private SortedMap<Long, Vector<SubtitleTrack.Cue>> mRemainingCues;
      
      public EntryIterator()
      {
        Object localObject;
        if (SubtitleTrack.CueList.this.DEBUG) {
          Log.v("CueList", localObject + "");
        }
        this.mRemainingCues = ((SortedMap)localObject);
        this.mLastListIterator = null;
        nextKey();
      }
      
      private void nextKey()
      {
        for (;;)
        {
          try
          {
            if (this.mRemainingCues == null) {
              throw new NoSuchElementException("");
            }
          }
          catch (NoSuchElementException localNoSuchElementException)
          {
            this.mDone = true;
            this.mRemainingCues = null;
            this.mListIterator = null;
            return;
          }
          this.mCurrentTimeMs = ((Long)this.mRemainingCues.firstKey()).longValue();
          this.mListIterator = ((Vector)this.mRemainingCues.get(Long.valueOf(this.mCurrentTimeMs))).iterator();
          try
          {
            this.mRemainingCues = this.mRemainingCues.tailMap(Long.valueOf(this.mCurrentTimeMs + 1L));
            this.mDone = false;
            if (!this.mListIterator.hasNext()) {
              continue;
            }
            return;
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            for (;;)
            {
              this.mRemainingCues = null;
            }
          }
        }
      }
      
      public boolean hasNext()
      {
        return !this.mDone;
      }
      
      public Pair<Long, SubtitleTrack.Cue> next()
      {
        if (this.mDone) {
          throw new NoSuchElementException("");
        }
        this.mLastEntry = new Pair(Long.valueOf(this.mCurrentTimeMs), (SubtitleTrack.Cue)this.mListIterator.next());
        this.mLastListIterator = this.mListIterator;
        if (!this.mListIterator.hasNext()) {
          nextKey();
        }
        return this.mLastEntry;
      }
      
      public void remove()
      {
        if ((this.mLastListIterator == null) || (((SubtitleTrack.Cue)this.mLastEntry.second).mEndTimeMs != ((Long)this.mLastEntry.first).longValue())) {
          throw new IllegalStateException("");
        }
        this.mLastListIterator.remove();
        this.mLastListIterator = null;
        if (((Vector)SubtitleTrack.CueList.-get0(SubtitleTrack.CueList.this).get(this.mLastEntry.first)).size() == 0) {
          SubtitleTrack.CueList.-get0(SubtitleTrack.CueList.this).remove(this.mLastEntry.first);
        }
        SubtitleTrack.Cue localCue = (SubtitleTrack.Cue)this.mLastEntry.second;
        SubtitleTrack.CueList.-wrap0(SubtitleTrack.CueList.this, localCue, localCue.mStartTimeMs);
        if (localCue.mInnerTimesMs != null)
        {
          long[] arrayOfLong = localCue.mInnerTimesMs;
          int j = arrayOfLong.length;
          int i = 0;
          while (i < j)
          {
            long l = arrayOfLong[i];
            SubtitleTrack.CueList.-wrap0(SubtitleTrack.CueList.this, localCue, l);
            i += 1;
          }
        }
      }
    }
  }
  
  public static abstract interface RenderingWidget
  {
    public abstract void draw(Canvas paramCanvas);
    
    public abstract void onAttachedToWindow();
    
    public abstract void onDetachedFromWindow();
    
    public abstract void setOnChangedListener(OnChangedListener paramOnChangedListener);
    
    public abstract void setSize(int paramInt1, int paramInt2);
    
    public abstract void setVisible(boolean paramBoolean);
    
    public static abstract interface OnChangedListener
    {
      public abstract void onChanged(SubtitleTrack.RenderingWidget paramRenderingWidget);
    }
  }
  
  private static class Run
  {
    public long mEndTimeMs = -1L;
    public SubtitleTrack.Cue mFirstCue;
    public Run mNextRunAtEndTimeMs;
    public Run mPrevRunAtEndTimeMs;
    public long mRunID = 0L;
    private long mStoredEndTimeMs = -1L;
    
    static
    {
      if (Run.class.desiredAssertionStatus()) {}
      for (boolean bool = false;; bool = true)
      {
        -assertionsDisabled = bool;
        return;
      }
    }
    
    public void removeAtEndTimeMs()
    {
      Run localRun = this.mPrevRunAtEndTimeMs;
      if (this.mPrevRunAtEndTimeMs != null)
      {
        this.mPrevRunAtEndTimeMs.mNextRunAtEndTimeMs = this.mNextRunAtEndTimeMs;
        this.mPrevRunAtEndTimeMs = null;
      }
      if (this.mNextRunAtEndTimeMs != null)
      {
        this.mNextRunAtEndTimeMs.mPrevRunAtEndTimeMs = localRun;
        this.mNextRunAtEndTimeMs = null;
      }
    }
    
    public void storeByEndTimeMs(LongSparseArray<Run> paramLongSparseArray)
    {
      int i = 0;
      int j = paramLongSparseArray.indexOfKey(this.mStoredEndTimeMs);
      if (j >= 0) {
        if (this.mPrevRunAtEndTimeMs == null)
        {
          if (!-assertionsDisabled)
          {
            if (this == paramLongSparseArray.valueAt(j)) {
              i = 1;
            }
            if (i == 0) {
              throw new AssertionError();
            }
          }
          if (this.mNextRunAtEndTimeMs != null) {
            break label129;
          }
          paramLongSparseArray.removeAt(j);
        }
      }
      for (;;)
      {
        removeAtEndTimeMs();
        if (this.mEndTimeMs >= 0L)
        {
          this.mPrevRunAtEndTimeMs = null;
          this.mNextRunAtEndTimeMs = ((Run)paramLongSparseArray.get(this.mEndTimeMs));
          if (this.mNextRunAtEndTimeMs != null) {
            this.mNextRunAtEndTimeMs.mPrevRunAtEndTimeMs = this;
          }
          paramLongSparseArray.put(this.mEndTimeMs, this);
          this.mStoredEndTimeMs = this.mEndTimeMs;
        }
        return;
        label129:
        paramLongSparseArray.setValueAt(j, this.mNextRunAtEndTimeMs);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/SubtitleTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */