package android.media;

import android.app.ActivityThread;
import android.app.Application;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

public class MediaPlayer
  extends PlayerBase
  implements SubtitleController.Listener
{
  public static final boolean APPLY_METADATA_FILTER = true;
  public static final boolean BYPASS_METADATA_FILTER = false;
  static final boolean DBG = Log.isLoggable("MediaPlayer", 3);
  private static final String IMEDIA_PLAYER = "android.media.IMediaPlayer";
  private static final int INVOKE_ID_ADD_EXTERNAL_SOURCE = 2;
  private static final int INVOKE_ID_ADD_EXTERNAL_SOURCE_FD = 3;
  private static final int INVOKE_ID_DESELECT_TRACK = 5;
  private static final int INVOKE_ID_GET_SELECTED_TRACK = 7;
  private static final int INVOKE_ID_GET_TRACK_INFO = 1;
  private static final int INVOKE_ID_SELECT_TRACK = 4;
  private static final int INVOKE_ID_SET_VIDEO_SCALE_MODE = 6;
  private static final int KEY_PARAMETER_AUDIO_ATTRIBUTES = 1400;
  private static final int MEDIA_BUFFERING_UPDATE = 3;
  private static final int MEDIA_ERROR = 100;
  public static final int MEDIA_ERROR_IO = -1004;
  public static final int MEDIA_ERROR_MALFORMED = -1007;
  public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
  public static final int MEDIA_ERROR_SERVER_DIED = 100;
  public static final int MEDIA_ERROR_SYSTEM = Integer.MIN_VALUE;
  public static final int MEDIA_ERROR_TIMED_OUT = -110;
  public static final int MEDIA_ERROR_UNKNOWN = 1;
  public static final int MEDIA_ERROR_UNSUPPORTED = -1010;
  private static final int MEDIA_INFO = 200;
  public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;
  public static final int MEDIA_INFO_BUFFERING_END = 702;
  public static final int MEDIA_INFO_BUFFERING_START = 701;
  public static final int MEDIA_INFO_EXTERNAL_METADATA_UPDATE = 803;
  public static final int MEDIA_INFO_METADATA_UPDATE = 802;
  public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703;
  public static final int MEDIA_INFO_NOT_SEEKABLE = 801;
  public static final int MEDIA_INFO_STARTED_AS_NEXT = 2;
  public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;
  public static final int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
  public static final int MEDIA_INFO_UNKNOWN = 1;
  public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
  public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
  public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
  private static final int MEDIA_META_DATA = 202;
  public static final String MEDIA_MIMETYPE_TEXT_CEA_608 = "text/cea-608";
  public static final String MEDIA_MIMETYPE_TEXT_CEA_708 = "text/cea-708";
  public static final String MEDIA_MIMETYPE_TEXT_SUBRIP = "application/x-subrip";
  public static final String MEDIA_MIMETYPE_TEXT_VTT = "text/vtt";
  private static final int MEDIA_NOP = 0;
  private static final int MEDIA_PAUSED = 7;
  private static final int MEDIA_PLAYBACK_COMPLETE = 2;
  private static final int MEDIA_PREPARED = 1;
  private static final int MEDIA_SEEK_COMPLETE = 4;
  private static final int MEDIA_SET_VIDEO_SIZE = 5;
  private static final int MEDIA_SKIPPED = 9;
  private static final int MEDIA_STARTED = 6;
  private static final int MEDIA_STOPPED = 8;
  private static final int MEDIA_SUBTITLE_DATA = 201;
  private static final int MEDIA_TIMED_TEXT = 99;
  public static final boolean METADATA_ALL = false;
  public static final boolean METADATA_UPDATE_ONLY = true;
  public static final int PLAYBACK_RATE_AUDIO_MODE_DEFAULT = 0;
  public static final int PLAYBACK_RATE_AUDIO_MODE_RESAMPLE = 2;
  public static final int PLAYBACK_RATE_AUDIO_MODE_STRETCH = 1;
  private static final String TAG = "MediaPlayer";
  public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
  public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;
  private boolean mBypassInterruptionPolicy;
  private EventHandler mEventHandler;
  private BitSet mInbandTrackIndices = new BitSet();
  private Vector<Pair<Integer, SubtitleTrack>> mIndexTrackPairs = new Vector();
  private int mListenerContext;
  private long mNativeContext;
  private long mNativeSurfaceTexture;
  private final INotificationManager mNotificationManager;
  private OnBufferingUpdateListener mOnBufferingUpdateListener;
  private OnCompletionListener mOnCompletionListener;
  private OnErrorListener mOnErrorListener;
  private OnInfoListener mOnInfoListener;
  private OnPreparedListener mOnPreparedListener;
  private OnSeekCompleteListener mOnSeekCompleteListener;
  private OnSubtitleDataListener mOnSubtitleDataListener;
  private OnTimedMetaDataAvailableListener mOnTimedMetaDataAvailableListener;
  private OnTimedTextListener mOnTimedTextListener;
  private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
  private Vector<InputStream> mOpenSubtitleSources;
  private boolean mScreenOnWhilePlaying;
  private int mSelectedSubtitleTrackIndex = -1;
  private boolean mStayAwake;
  private int mStreamType = Integer.MIN_VALUE;
  private SubtitleController mSubtitleController;
  private OnSubtitleDataListener mSubtitleDataListener = new OnSubtitleDataListener()
  {
    public void onSubtitleData(MediaPlayer arg1, SubtitleData paramAnonymousSubtitleData)
    {
      int i = paramAnonymousSubtitleData.getTrackIndex();
      synchronized (MediaPlayer.-get1(MediaPlayer.this))
      {
        Iterator localIterator = MediaPlayer.-get1(MediaPlayer.this).iterator();
        while (localIterator.hasNext())
        {
          Pair localPair = (Pair)localIterator.next();
          if ((localPair.first != null) && (((Integer)localPair.first).intValue() == i) && (localPair.second != null)) {
            ((SubtitleTrack)localPair.second).onData(paramAnonymousSubtitleData);
          }
        }
      }
    }
  };
  private SurfaceHolder mSurfaceHolder;
  private TimeProvider mTimeProvider;
  private int mUsage = -1;
  private PowerManager.WakeLock mWakeLock = null;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  public MediaPlayer()
  {
    super(new AudioAttributes.Builder().build());
    Looper localLooper = Looper.myLooper();
    if (localLooper != null) {
      this.mEventHandler = new EventHandler(this, localLooper);
    }
    for (;;)
    {
      this.mTimeProvider = new TimeProvider(this);
      this.mOpenSubtitleSources = new Vector();
      this.mNotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
      native_setup(new WeakReference(this));
      return;
      localLooper = Looper.getMainLooper();
      if (localLooper != null) {
        this.mEventHandler = new EventHandler(this, localLooper);
      } else {
        this.mEventHandler = null;
      }
    }
  }
  
  private native int _getAudioStreamType()
    throws IllegalStateException;
  
  private native void _pause()
    throws IllegalStateException;
  
  private native void _prepare()
    throws IOException, IllegalStateException;
  
  private native void _release();
  
  private native void _reset();
  
  private native void _setAudioStreamType(int paramInt);
  
  private native void _setAuxEffectSendLevel(float paramFloat);
  
  private native void _setDataSource(MediaDataSource paramMediaDataSource)
    throws IllegalArgumentException, IllegalStateException;
  
  private native void _setDataSource(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2)
    throws IOException, IllegalArgumentException, IllegalStateException;
  
  private native void _setVideoSurface(Surface paramSurface);
  
  private native void _setVolume(float paramFloat1, float paramFloat2);
  
  private native void _start()
    throws IllegalStateException;
  
  private native void _stop()
    throws IllegalStateException;
  
  /* Error */
  private boolean attemptDataSource(ContentResolver paramContentResolver, Uri paramUri)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 6
    //   6: aconst_null
    //   7: astore 4
    //   9: aconst_null
    //   10: astore_3
    //   11: aload_1
    //   12: aload_2
    //   13: ldc_w 419
    //   16: invokevirtual 425	android/content/ContentResolver:openAssetFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;)Landroid/content/res/AssetFileDescriptor;
    //   19: astore_1
    //   20: aload_1
    //   21: astore_3
    //   22: aload_1
    //   23: astore 4
    //   25: aload_0
    //   26: aload_1
    //   27: invokevirtual 429	android/media/MediaPlayer:setDataSource	(Landroid/content/res/AssetFileDescriptor;)V
    //   30: aload 6
    //   32: astore_3
    //   33: aload_1
    //   34: ifnull +10 -> 44
    //   37: aload_1
    //   38: invokevirtual 434	android/content/res/AssetFileDescriptor:close	()V
    //   41: aload 6
    //   43: astore_3
    //   44: aload_3
    //   45: ifnull +48 -> 93
    //   48: aload_3
    //   49: athrow
    //   50: astore_1
    //   51: ldc -91
    //   53: new 436	java/lang/StringBuilder
    //   56: dup
    //   57: invokespecial 437	java/lang/StringBuilder:<init>	()V
    //   60: ldc_w 439
    //   63: invokevirtual 443	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: aload_2
    //   67: invokevirtual 446	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   70: ldc_w 448
    //   73: invokevirtual 443	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: aload_1
    //   77: invokevirtual 446	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   80: invokevirtual 452	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   83: invokestatic 456	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   86: pop
    //   87: iconst_0
    //   88: ireturn
    //   89: astore_3
    //   90: goto -46 -> 44
    //   93: iconst_1
    //   94: ireturn
    //   95: astore_1
    //   96: aload_1
    //   97: athrow
    //   98: astore 4
    //   100: aload_1
    //   101: astore 5
    //   103: aload_3
    //   104: ifnull +10 -> 114
    //   107: aload_3
    //   108: invokevirtual 434	android/content/res/AssetFileDescriptor:close	()V
    //   111: aload_1
    //   112: astore 5
    //   114: aload 5
    //   116: ifnull +25 -> 141
    //   119: aload 5
    //   121: athrow
    //   122: aload_1
    //   123: astore 5
    //   125: aload_1
    //   126: aload_3
    //   127: if_acmpeq -13 -> 114
    //   130: aload_1
    //   131: aload_3
    //   132: invokevirtual 460	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   135: aload_1
    //   136: astore 5
    //   138: goto -24 -> 114
    //   141: aload 4
    //   143: athrow
    //   144: astore_1
    //   145: aload 4
    //   147: astore_3
    //   148: aload_1
    //   149: astore 4
    //   151: aload 5
    //   153: astore_1
    //   154: goto -54 -> 100
    //   157: astore_3
    //   158: aload_1
    //   159: ifnonnull -37 -> 122
    //   162: aload_3
    //   163: astore 5
    //   165: goto -51 -> 114
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	168	0	this	MediaPlayer
    //   0	168	1	paramContentResolver	ContentResolver
    //   0	168	2	paramUri	Uri
    //   10	39	3	localObject1	Object
    //   89	43	3	localThrowable1	Throwable
    //   147	1	3	localObject2	Object
    //   157	6	3	localThrowable2	Throwable
    //   7	17	4	localContentResolver1	ContentResolver
    //   98	48	4	localObject3	Object
    //   149	1	4	localContentResolver2	ContentResolver
    //   1	163	5	localObject4	Object
    //   4	38	6	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   37	41	50	java/lang/NullPointerException
    //   37	41	50	java/lang/SecurityException
    //   37	41	50	java/io/IOException
    //   48	50	50	java/lang/NullPointerException
    //   48	50	50	java/lang/SecurityException
    //   48	50	50	java/io/IOException
    //   107	111	50	java/lang/NullPointerException
    //   107	111	50	java/lang/SecurityException
    //   107	111	50	java/io/IOException
    //   119	122	50	java/lang/NullPointerException
    //   119	122	50	java/lang/SecurityException
    //   119	122	50	java/io/IOException
    //   130	135	50	java/lang/NullPointerException
    //   130	135	50	java/lang/SecurityException
    //   130	135	50	java/io/IOException
    //   141	144	50	java/lang/NullPointerException
    //   141	144	50	java/lang/SecurityException
    //   141	144	50	java/io/IOException
    //   37	41	89	java/lang/Throwable
    //   11	20	95	java/lang/Throwable
    //   25	30	95	java/lang/Throwable
    //   96	98	98	finally
    //   11	20	144	finally
    //   25	30	144	finally
    //   107	111	157	java/lang/Throwable
  }
  
  private static boolean availableMimeTypeForExternalSource(String paramString)
  {
    return "application/x-subrip".equals(paramString);
  }
  
  public static MediaPlayer create(Context paramContext, int paramInt)
  {
    int i = AudioSystem.newAudioSessionId();
    if (i > 0) {}
    for (;;)
    {
      return create(paramContext, paramInt, null, i);
      i = 0;
    }
  }
  
  public static MediaPlayer create(Context paramContext, int paramInt1, AudioAttributes paramAudioAttributes, int paramInt2)
  {
    try
    {
      paramContext = paramContext.getResources().openRawResourceFd(paramInt1);
      if (paramContext == null) {
        return null;
      }
      MediaPlayer localMediaPlayer = new MediaPlayer();
      if (paramAudioAttributes != null) {}
      for (;;)
      {
        localMediaPlayer.setAudioAttributes(paramAudioAttributes);
        localMediaPlayer.setAudioSessionId(paramInt2);
        localMediaPlayer.setDataSource(paramContext.getFileDescriptor(), paramContext.getStartOffset(), paramContext.getLength());
        paramContext.close();
        localMediaPlayer.prepare();
        return localMediaPlayer;
        paramAudioAttributes = new AudioAttributes.Builder().build();
      }
      return null;
    }
    catch (SecurityException paramContext)
    {
      Log.d("MediaPlayer", "create failed:", paramContext);
      return null;
    }
    catch (IllegalArgumentException paramContext)
    {
      Log.d("MediaPlayer", "create failed:", paramContext);
      return null;
    }
    catch (IOException paramContext)
    {
      Log.d("MediaPlayer", "create failed:", paramContext);
    }
  }
  
  public static MediaPlayer create(Context paramContext, Uri paramUri)
  {
    return create(paramContext, paramUri, null);
  }
  
  public static MediaPlayer create(Context paramContext, Uri paramUri, SurfaceHolder paramSurfaceHolder)
  {
    int i = AudioSystem.newAudioSessionId();
    if (i > 0) {}
    for (;;)
    {
      return create(paramContext, paramUri, paramSurfaceHolder, null, i);
      i = 0;
    }
  }
  
  public static MediaPlayer create(Context paramContext, Uri paramUri, SurfaceHolder paramSurfaceHolder, AudioAttributes paramAudioAttributes, int paramInt)
  {
    try
    {
      MediaPlayer localMediaPlayer = new MediaPlayer();
      if (paramAudioAttributes != null) {}
      for (;;)
      {
        localMediaPlayer.setAudioAttributes(paramAudioAttributes);
        localMediaPlayer.setAudioSessionId(paramInt);
        localMediaPlayer.setDataSource(paramContext, paramUri);
        if (paramSurfaceHolder != null) {
          localMediaPlayer.setDisplay(paramSurfaceHolder);
        }
        localMediaPlayer.prepare();
        return localMediaPlayer;
        paramAudioAttributes = new AudioAttributes.Builder().build();
      }
      return null;
    }
    catch (SecurityException paramContext)
    {
      Log.d("MediaPlayer", "create failed:", paramContext);
      return null;
    }
    catch (IllegalArgumentException paramContext)
    {
      Log.d("MediaPlayer", "create failed:", paramContext);
      return null;
    }
    catch (IOException paramContext)
    {
      Log.d("MediaPlayer", "create failed:", paramContext);
    }
  }
  
  private int getAudioStreamType()
  {
    if (this.mStreamType == Integer.MIN_VALUE) {
      this.mStreamType = _getAudioStreamType();
    }
    return this.mStreamType;
  }
  
  private TrackInfo[] getInbandTrackInfo()
    throws IllegalStateException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.media.IMediaPlayer");
      localParcel1.writeInt(1);
      invoke(localParcel1, localParcel2);
      TrackInfo[] arrayOfTrackInfo = (TrackInfo[])localParcel2.createTypedArray(TrackInfo.CREATOR);
      return arrayOfTrackInfo;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  private boolean isRestricted()
  {
    try
    {
      if (DBG) {
        Log.w("MediaPlayer", "isRestricted getPackagePriority = " + this.mNotificationManager.getOnePlusPackagePriority(ActivityThread.currentPackageName(), Process.myUid()) + " getZenMode =  " + this.mNotificationManager.getZenMode() + " ActivityThread.currentPackageName() =  " + ActivityThread.currentPackageName() + " Process.myUid() =  " + Process.myUid() + " getAudioStreamType =  " + getAudioStreamType());
      }
      if ((ActivityThread.currentPackageName() != null) && (ActivityThread.currentPackageName().equals(new String("com.tencent.mm"))) && (1 == this.mNotificationManager.getZenMode()) && (5 == getAudioStreamType()))
      {
        int i = this.mNotificationManager.getOnePlusPackagePriority(ActivityThread.currentPackageName(), Process.myUid());
        if (i == 0) {
          return true;
        }
      }
      return false;
    }
    catch (Exception localException) {}
    return false;
  }
  
  private boolean isVideoScalingModeSupported(int paramInt)
  {
    return (paramInt == 1) || (paramInt == 2);
  }
  
  private native void nativeSetDataSource(IBinder paramIBinder, String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
    throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;
  
  private final native void native_finalize();
  
  private final native boolean native_getMetadata(boolean paramBoolean1, boolean paramBoolean2, Parcel paramParcel);
  
  private static final native void native_init();
  
  private final native int native_invoke(Parcel paramParcel1, Parcel paramParcel2);
  
  public static native int native_pullBatteryData(Parcel paramParcel);
  
  private final native int native_setMetadataFilter(Parcel paramParcel);
  
  private final native int native_setRetransmitEndpoint(String paramString, int paramInt);
  
  private final native void native_setup(Object paramObject);
  
  private void populateInbandTracks()
  {
    TrackInfo[] arrayOfTrackInfo = getInbandTrackInfo();
    Vector localVector = this.mIndexTrackPairs;
    int i = 0;
    for (;;)
    {
      try
      {
        if (i >= arrayOfTrackInfo.length) {
          break label111;
        }
        if (this.mInbandTrackIndices.get(i)) {
          break label114;
        }
        this.mInbandTrackIndices.set(i);
        if (arrayOfTrackInfo[i].getTrackType() == 4)
        {
          SubtitleTrack localSubtitleTrack = this.mSubtitleController.addTrack(arrayOfTrackInfo[i].getFormat());
          this.mIndexTrackPairs.add(Pair.create(Integer.valueOf(i), localSubtitleTrack));
        }
      }
      finally {}
      this.mIndexTrackPairs.add(Pair.create(Integer.valueOf(i), null));
      break label114;
      label111:
      return;
      label114:
      i += 1;
    }
  }
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (MediaPlayer)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if ((paramInt1 == 200) && (paramInt2 == 2)) {
      ((MediaPlayer)paramObject1).start();
    }
    if (((MediaPlayer)paramObject1).mEventHandler != null)
    {
      paramObject2 = ((MediaPlayer)paramObject1).mEventHandler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2);
      ((MediaPlayer)paramObject1).mEventHandler.sendMessage((Message)paramObject2);
    }
  }
  
  private void scanInternalSubtitleTracks()
  {
    if (this.mSubtitleController == null)
    {
      Log.d("MediaPlayer", "setSubtitleAnchor in MediaPlayer");
      setSubtitleAnchor();
    }
    populateInbandTracks();
    if (this.mSubtitleController != null) {
      this.mSubtitleController.selectDefaultTrack();
    }
  }
  
  /* Error */
  private void selectOrDeselectInbandTrack(int paramInt, boolean paramBoolean)
    throws IllegalStateException
  {
    // Byte code:
    //   0: invokestatic 544	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   3: astore 4
    //   5: invokestatic 544	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   8: astore 5
    //   10: aload 4
    //   12: ldc 69
    //   14: invokevirtual 547	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   17: iload_2
    //   18: ifeq +36 -> 54
    //   21: iconst_4
    //   22: istore_3
    //   23: aload 4
    //   25: iload_3
    //   26: invokevirtual 550	android/os/Parcel:writeInt	(I)V
    //   29: aload 4
    //   31: iload_1
    //   32: invokevirtual 550	android/os/Parcel:writeInt	(I)V
    //   35: aload_0
    //   36: aload 4
    //   38: aload 5
    //   40: invokevirtual 554	android/media/MediaPlayer:invoke	(Landroid/os/Parcel;Landroid/os/Parcel;)V
    //   43: aload 4
    //   45: invokevirtual 567	android/os/Parcel:recycle	()V
    //   48: aload 5
    //   50: invokevirtual 567	android/os/Parcel:recycle	()V
    //   53: return
    //   54: iconst_5
    //   55: istore_3
    //   56: goto -33 -> 23
    //   59: astore 6
    //   61: aload 4
    //   63: invokevirtual 567	android/os/Parcel:recycle	()V
    //   66: aload 5
    //   68: invokevirtual 567	android/os/Parcel:recycle	()V
    //   71: aload 6
    //   73: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	MediaPlayer
    //   0	74	1	paramInt	int
    //   0	74	2	paramBoolean	boolean
    //   22	34	3	i	int
    //   3	59	4	localParcel1	Parcel
    //   8	59	5	localParcel2	Parcel
    //   59	13	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	17	59	finally
    //   23	43	59	finally
  }
  
  private void selectOrDeselectTrack(int paramInt, boolean paramBoolean)
    throws IllegalStateException
  {
    populateInbandTracks();
    Object localObject2;
    try
    {
      localObject2 = (Pair)this.mIndexTrackPairs.get(paramInt);
      SubtitleTrack localSubtitleTrack = (SubtitleTrack)((Pair)localObject2).second;
      if (localSubtitleTrack == null)
      {
        selectOrDeselectInbandTrack(((Integer)((Pair)localObject2).first).intValue(), paramBoolean);
        return;
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      return;
    }
    if (this.mSubtitleController == null) {
      return;
    }
    if (!paramBoolean)
    {
      if (this.mSubtitleController.getSelectedTrack() == localArrayIndexOutOfBoundsException)
      {
        this.mSubtitleController.selectTrack(null);
        return;
      }
      Log.w("MediaPlayer", "trying to deselect track that was not selected");
      return;
    }
    if (localArrayIndexOutOfBoundsException.getTrackType() == 3)
    {
      paramInt = getSelectedTrack(3);
      localObject2 = this.mIndexTrackPairs;
      if (paramInt < 0) {}
    }
    try
    {
      if (paramInt < this.mIndexTrackPairs.size())
      {
        Pair localPair = (Pair)this.mIndexTrackPairs.get(paramInt);
        if ((localPair.first != null) && (localPair.second == null)) {
          selectOrDeselectInbandTrack(((Integer)localPair.first).intValue(), false);
        }
      }
      this.mSubtitleController.selectTrack(localArrayIndexOutOfBoundsException);
      return;
    }
    finally {}
  }
  
  private void setDataSource(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
    throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
  {
    Object localObject = Uri.parse(paramString);
    String str = ((Uri)localObject).getScheme();
    if ("file".equals(str)) {
      localObject = ((Uri)localObject).getPath();
    }
    do
    {
      paramString = new File((String)localObject);
      if (!paramString.exists()) {
        break;
      }
      paramString = new FileInputStream(paramString);
      setDataSource(paramString.getFD());
      paramString.close();
      return;
      localObject = paramString;
    } while (str == null);
    nativeSetDataSource(MediaHTTPService.createHttpServiceBinderIfNecessary(paramString), paramString, paramArrayOfString1, paramArrayOfString2);
    return;
    throw new IOException("setDataSource failed.");
  }
  
  private native boolean setParameter(int paramInt, Parcel paramParcel);
  
  /* Error */
  private void setSubtitleAnchor()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 245	android/media/MediaPlayer:mSubtitleController	Landroid/media/SubtitleController;
    //   6: ifnonnull +46 -> 52
    //   9: new 775	android/os/HandlerThread
    //   12: dup
    //   13: ldc_w 777
    //   16: invokespecial 778	android/os/HandlerThread:<init>	(Ljava/lang/String;)V
    //   19: astore_1
    //   20: aload_1
    //   21: invokevirtual 779	android/os/HandlerThread:start	()V
    //   24: new 781	android/os/Handler
    //   27: dup
    //   28: aload_1
    //   29: invokevirtual 784	android/os/HandlerThread:getLooper	()Landroid/os/Looper;
    //   32: invokespecial 787	android/os/Handler:<init>	(Landroid/os/Looper;)V
    //   35: new 10	android/media/MediaPlayer$2
    //   38: dup
    //   39: aload_0
    //   40: aload_1
    //   41: invokespecial 790	android/media/MediaPlayer$2:<init>	(Landroid/media/MediaPlayer;Landroid/os/HandlerThread;)V
    //   44: invokevirtual 794	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   47: pop
    //   48: aload_1
    //   49: invokevirtual 797	android/os/HandlerThread:join	()V
    //   52: aload_0
    //   53: monitorexit
    //   54: return
    //   55: astore_1
    //   56: invokestatic 803	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   59: invokevirtual 806	java/lang/Thread:interrupt	()V
    //   62: ldc -91
    //   64: ldc_w 808
    //   67: invokestatic 456	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   70: pop
    //   71: goto -19 -> 52
    //   74: astore_1
    //   75: aload_0
    //   76: monitorexit
    //   77: aload_1
    //   78: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	79	0	this	MediaPlayer
    //   19	30	1	localHandlerThread	HandlerThread
    //   55	1	1	localInterruptedException	InterruptedException
    //   74	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   48	52	55	java/lang/InterruptedException
    //   2	48	74	finally
    //   48	52	74	finally
    //   56	71	74	finally
  }
  
  private void stayAwake(boolean paramBoolean)
  {
    if (this.mWakeLock != null)
    {
      if ((paramBoolean) && (!this.mWakeLock.isHeld())) {
        break label52;
      }
      if ((!paramBoolean) && (this.mWakeLock.isHeld())) {
        this.mWakeLock.release();
      }
    }
    for (;;)
    {
      this.mStayAwake = paramBoolean;
      updateSurfaceScreenOn();
      return;
      label52:
      this.mWakeLock.acquire();
    }
  }
  
  private void updateSurfaceScreenOn()
  {
    SurfaceHolder localSurfaceHolder;
    if (this.mSurfaceHolder != null)
    {
      localSurfaceHolder = this.mSurfaceHolder;
      if (!this.mScreenOnWhilePlaying) {
        break label32;
      }
    }
    label32:
    for (boolean bool = this.mStayAwake;; bool = false)
    {
      localSurfaceHolder.setKeepScreenOn(bool);
      return;
    }
  }
  
  public void addSubtitleSource(final InputStream paramInputStream, final MediaFormat paramMediaFormat)
    throws IllegalStateException
  {
    if (paramInputStream != null) {}
    for (;;)
    {
      synchronized (this.mOpenSubtitleSources)
      {
        this.mOpenSubtitleSources.add(paramInputStream);
        getMediaTimeProvider();
        ??? = new HandlerThread("SubtitleReadThread", 9);
        ((HandlerThread)???).start();
        new Handler(((HandlerThread)???).getLooper()).post(new Runnable()
        {
          private int addTrack()
          {
            if ((paramInputStream == null) || (MediaPlayer.-get14(MediaPlayer.this) == null)) {
              return 901;
            }
            SubtitleTrack localSubtitleTrack = MediaPlayer.-get14(MediaPlayer.this).addTrack(paramMediaFormat);
            if (localSubtitleTrack == null) {
              return 901;
            }
            Scanner localScanner = new Scanner(paramInputStream, "UTF-8");
            String str = localScanner.useDelimiter("\\A").next();
            synchronized (MediaPlayer.-get13(MediaPlayer.this))
            {
              MediaPlayer.-get13(MediaPlayer.this).remove(paramInputStream);
              localScanner.close();
            }
            synchronized (MediaPlayer.-get1(MediaPlayer.this))
            {
              MediaPlayer.-get1(MediaPlayer.this).add(Pair.create(null, localSubtitleTrack));
              ??? = MediaPlayer.TimeProvider.-get0(MediaPlayer.-get15(MediaPlayer.this));
              ((Handler)???).sendMessage(((Handler)???).obtainMessage(1, 4, 0, Pair.create(localSubtitleTrack, str.getBytes())));
              return 803;
              localObject1 = finally;
              throw ((Throwable)localObject1);
            }
          }
          
          public void run()
          {
            int i = addTrack();
            if (MediaPlayer.-get0(MediaPlayer.this) != null)
            {
              Message localMessage = MediaPlayer.-get0(MediaPlayer.this).obtainMessage(200, i, 0, null);
              MediaPlayer.-get0(MediaPlayer.this).sendMessage(localMessage);
            }
            localObject.getLooper().quitSafely();
          }
        });
        return;
      }
      Log.w("MediaPlayer", "addSubtitleSource called with null InputStream");
    }
  }
  
  public void addTimedTextSource(Context paramContext, Uri paramUri, String paramString)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    Object localObject = paramUri.getScheme();
    if ((localObject == null) || (((String)localObject).equals("file")))
    {
      addTimedTextSource(paramUri.getPath(), paramString);
      return;
    }
    Context localContext1 = null;
    Context localContext2 = null;
    localObject = null;
    try
    {
      paramContext = paramContext.getContentResolver().openAssetFileDescriptor(paramUri, "r");
      if (paramContext == null) {
        return;
      }
      localObject = paramContext;
      localContext1 = paramContext;
      localContext2 = paramContext;
      addTimedTextSource(paramContext.getFileDescriptor(), paramString);
      return;
    }
    catch (IOException paramContext)
    {
      if (localObject != null) {
        ((AssetFileDescriptor)localObject).close();
      }
      return;
    }
    catch (SecurityException paramContext) {}finally
    {
      if (localContext2 != null) {
        localContext2.close();
      }
    }
  }
  
  /* Error */
  public void addTimedTextSource(final FileDescriptor paramFileDescriptor, final long paramLong1, long paramLong2, final String paramString)
    throws IllegalArgumentException, IllegalStateException
  {
    // Byte code:
    //   0: aload 6
    //   2: invokestatic 866	android/media/MediaPlayer:availableMimeTypeForExternalSource	(Ljava/lang/String;)Z
    //   5: ifne +32 -> 37
    //   8: new 402	java/lang/IllegalArgumentException
    //   11: dup
    //   12: new 436	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 437	java/lang/StringBuilder:<init>	()V
    //   19: ldc_w 868
    //   22: invokevirtual 443	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: aload 6
    //   27: invokevirtual 443	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   30: invokevirtual 452	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   33: invokespecial 869	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   36: athrow
    //   37: getstatic 875	libcore/io/Libcore:os	Llibcore/io/Os;
    //   40: aload_1
    //   41: invokeinterface 881 2 0
    //   46: astore_1
    //   47: new 883	android/media/MediaFormat
    //   50: dup
    //   51: invokespecial 884	android/media/MediaFormat:<init>	()V
    //   54: astore 7
    //   56: aload 7
    //   58: ldc_w 886
    //   61: aload 6
    //   63: invokevirtual 889	android/media/MediaFormat:setString	(Ljava/lang/String;Ljava/lang/String;)V
    //   66: aload 7
    //   68: ldc_w 891
    //   71: iconst_1
    //   72: invokevirtual 894	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   75: aload_0
    //   76: getfield 245	android/media/MediaPlayer:mSubtitleController	Landroid/media/SubtitleController;
    //   79: ifnonnull +7 -> 86
    //   82: aload_0
    //   83: invokespecial 682	android/media/MediaPlayer:setSubtitleAnchor	()V
    //   86: aload_0
    //   87: getfield 245	android/media/MediaPlayer:mSubtitleController	Landroid/media/SubtitleController;
    //   90: aload 7
    //   92: invokevirtual 898	android/media/SubtitleController:hasRendererFor	(Landroid/media/MediaFormat;)Z
    //   95: ifne +28 -> 123
    //   98: invokestatic 902	android/app/ActivityThread:currentApplication	()Landroid/app/Application;
    //   101: astore 6
    //   103: aload_0
    //   104: getfield 245	android/media/MediaPlayer:mSubtitleController	Landroid/media/SubtitleController;
    //   107: new 904	android/media/SRTRenderer
    //   110: dup
    //   111: aload 6
    //   113: aload_0
    //   114: getfield 221	android/media/MediaPlayer:mEventHandler	Landroid/media/MediaPlayer$EventHandler;
    //   117: invokespecial 907	android/media/SRTRenderer:<init>	(Landroid/content/Context;Landroid/os/Handler;)V
    //   120: invokevirtual 911	android/media/SubtitleController:registerRenderer	(Landroid/media/SubtitleController$Renderer;)V
    //   123: aload_0
    //   124: getfield 245	android/media/MediaPlayer:mSubtitleController	Landroid/media/SubtitleController;
    //   127: aload 7
    //   129: invokevirtual 645	android/media/SubtitleController:addTrack	(Landroid/media/MediaFormat;)Landroid/media/SubtitleTrack;
    //   132: astore 6
    //   134: aload_0
    //   135: getfield 226	android/media/MediaPlayer:mIndexTrackPairs	Ljava/util/Vector;
    //   138: astore 7
    //   140: aload 7
    //   142: monitorenter
    //   143: aload_0
    //   144: getfield 226	android/media/MediaPlayer:mIndexTrackPairs	Ljava/util/Vector;
    //   147: aconst_null
    //   148: aload 6
    //   150: invokestatic 656	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   153: invokevirtual 659	java/util/Vector:add	(Ljava/lang/Object;)Z
    //   156: pop
    //   157: aload 7
    //   159: monitorexit
    //   160: aload_0
    //   161: invokevirtual 839	android/media/MediaPlayer:getMediaTimeProvider	()Landroid/media/MediaTimeProvider;
    //   164: pop
    //   165: new 775	android/os/HandlerThread
    //   168: dup
    //   169: ldc_w 913
    //   172: bipush 9
    //   174: invokespecial 844	android/os/HandlerThread:<init>	(Ljava/lang/String;I)V
    //   177: astore 7
    //   179: aload 7
    //   181: invokevirtual 779	android/os/HandlerThread:start	()V
    //   184: new 781	android/os/Handler
    //   187: dup
    //   188: aload 7
    //   190: invokevirtual 784	android/os/HandlerThread:getLooper	()Landroid/os/Looper;
    //   193: invokespecial 787	android/os/Handler:<init>	(Landroid/os/Looper;)V
    //   196: new 16	android/media/MediaPlayer$4
    //   199: dup
    //   200: aload_0
    //   201: aload_1
    //   202: lload_2
    //   203: lload 4
    //   205: aload 6
    //   207: aload 7
    //   209: invokespecial 916	android/media/MediaPlayer$4:<init>	(Landroid/media/MediaPlayer;Ljava/io/FileDescriptor;JJLandroid/media/SubtitleTrack;Landroid/os/HandlerThread;)V
    //   212: invokevirtual 794	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   215: pop
    //   216: return
    //   217: astore_1
    //   218: ldc -91
    //   220: aload_1
    //   221: invokevirtual 919	android/system/ErrnoException:getMessage	()Ljava/lang/String;
    //   224: aload_1
    //   225: invokestatic 922	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   228: pop
    //   229: new 924	java/lang/RuntimeException
    //   232: dup
    //   233: aload_1
    //   234: invokespecial 926	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   237: athrow
    //   238: astore_1
    //   239: aload 7
    //   241: monitorexit
    //   242: aload_1
    //   243: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	244	0	this	MediaPlayer
    //   0	244	1	paramFileDescriptor	FileDescriptor
    //   0	244	2	paramLong1	long
    //   0	244	4	paramLong2	long
    //   0	244	6	paramString	String
    // Exception table:
    //   from	to	target	type
    //   37	47	217	android/system/ErrnoException
    //   143	157	238	finally
  }
  
  public void addTimedTextSource(FileDescriptor paramFileDescriptor, String paramString)
    throws IllegalArgumentException, IllegalStateException
  {
    addTimedTextSource(paramFileDescriptor, 0L, 576460752303423487L, paramString);
  }
  
  public void addTimedTextSource(String paramString1, String paramString2)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    if (!availableMimeTypeForExternalSource(paramString2)) {
      throw new IllegalArgumentException("Illegal mimeType for timed text source: " + paramString2);
    }
    File localFile = new File(paramString1);
    if (localFile.exists())
    {
      paramString1 = new FileInputStream(localFile);
      addTimedTextSource(paramString1.getFD(), paramString2);
      paramString1.close();
      return;
    }
    throw new IOException(paramString1);
  }
  
  public native void attachAuxEffect(int paramInt);
  
  public void deselectTrack(int paramInt)
    throws IllegalStateException
  {
    selectOrDeselectTrack(paramInt, false);
  }
  
  public PlaybackParams easyPlaybackParams(float paramFloat, int paramInt)
  {
    PlaybackParams localPlaybackParams = new PlaybackParams();
    localPlaybackParams.allowDefaults();
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Audio playback mode " + paramInt + " is not supported");
    case 0: 
      localPlaybackParams.setSpeed(paramFloat).setPitch(1.0F);
      return localPlaybackParams;
    case 1: 
      localPlaybackParams.setSpeed(paramFloat).setPitch(1.0F).setAudioFallbackMode(2);
      return localPlaybackParams;
    }
    localPlaybackParams.setSpeed(paramFloat).setPitch(paramFloat);
    return localPlaybackParams;
  }
  
  protected void finalize()
  {
    baseRelease();
    native_finalize();
  }
  
  public native int getAudioSessionId();
  
  public native int getCurrentPosition();
  
  public native int getDuration();
  
  public MediaTimeProvider getMediaTimeProvider()
  {
    if (this.mTimeProvider == null) {
      this.mTimeProvider = new TimeProvider(this);
    }
    return this.mTimeProvider;
  }
  
  public Metadata getMetadata(boolean paramBoolean1, boolean paramBoolean2)
  {
    Parcel localParcel = Parcel.obtain();
    Metadata localMetadata = new Metadata();
    if (!native_getMetadata(paramBoolean1, paramBoolean2, localParcel))
    {
      localParcel.recycle();
      return null;
    }
    if (!localMetadata.parse(localParcel))
    {
      localParcel.recycle();
      return null;
    }
    return localMetadata;
  }
  
  public native PlaybackParams getPlaybackParams();
  
  /* Error */
  public int getSelectedTrack(int paramInt)
    throws IllegalStateException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 245	android/media/MediaPlayer:mSubtitleController	Landroid/media/SubtitleController;
    //   4: ifnull +94 -> 98
    //   7: iload_1
    //   8: iconst_4
    //   9: if_icmpeq +8 -> 17
    //   12: iload_1
    //   13: iconst_3
    //   14: if_icmpne +84 -> 98
    //   17: aload_0
    //   18: getfield 245	android/media/MediaPlayer:mSubtitleController	Landroid/media/SubtitleController;
    //   21: invokevirtual 713	android/media/SubtitleController:getSelectedTrack	()Landroid/media/SubtitleTrack;
    //   24: astore 5
    //   26: aload 5
    //   28: ifnull +70 -> 98
    //   31: aload_0
    //   32: getfield 226	android/media/MediaPlayer:mIndexTrackPairs	Ljava/util/Vector;
    //   35: astore 4
    //   37: aload 4
    //   39: monitorenter
    //   40: iconst_0
    //   41: istore_2
    //   42: iload_2
    //   43: aload_0
    //   44: getfield 226	android/media/MediaPlayer:mIndexTrackPairs	Ljava/util/Vector;
    //   47: invokevirtual 726	java/util/Vector:size	()I
    //   50: if_icmpge +45 -> 95
    //   53: aload_0
    //   54: getfield 226	android/media/MediaPlayer:mIndexTrackPairs	Ljava/util/Vector;
    //   57: iload_2
    //   58: invokevirtual 695	java/util/Vector:get	(I)Ljava/lang/Object;
    //   61: checkcast 653	android/util/Pair
    //   64: getfield 699	android/util/Pair:second	Ljava/lang/Object;
    //   67: aload 5
    //   69: if_acmpne +19 -> 88
    //   72: aload 5
    //   74: invokevirtual 720	android/media/SubtitleTrack:getTrackType	()I
    //   77: istore_3
    //   78: iload_3
    //   79: iload_1
    //   80: if_icmpne +8 -> 88
    //   83: aload 4
    //   85: monitorexit
    //   86: iload_2
    //   87: ireturn
    //   88: iload_2
    //   89: iconst_1
    //   90: iadd
    //   91: istore_2
    //   92: goto -50 -> 42
    //   95: aload 4
    //   97: monitorexit
    //   98: invokestatic 544	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   101: astore 4
    //   103: invokestatic 544	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   106: astore 5
    //   108: aload 4
    //   110: ldc 69
    //   112: invokevirtual 547	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   115: aload 4
    //   117: bipush 7
    //   119: invokevirtual 550	android/os/Parcel:writeInt	(I)V
    //   122: aload 4
    //   124: iload_1
    //   125: invokevirtual 550	android/os/Parcel:writeInt	(I)V
    //   128: aload_0
    //   129: aload 4
    //   131: aload 5
    //   133: invokevirtual 554	android/media/MediaPlayer:invoke	(Landroid/os/Parcel;Landroid/os/Parcel;)V
    //   136: aload 5
    //   138: invokevirtual 981	android/os/Parcel:readInt	()I
    //   141: istore_2
    //   142: aload_0
    //   143: getfield 226	android/media/MediaPlayer:mIndexTrackPairs	Ljava/util/Vector;
    //   146: astore 6
    //   148: aload 6
    //   150: monitorenter
    //   151: iconst_0
    //   152: istore_1
    //   153: iload_1
    //   154: aload_0
    //   155: getfield 226	android/media/MediaPlayer:mIndexTrackPairs	Ljava/util/Vector;
    //   158: invokevirtual 726	java/util/Vector:size	()I
    //   161: if_icmpge +71 -> 232
    //   164: aload_0
    //   165: getfield 226	android/media/MediaPlayer:mIndexTrackPairs	Ljava/util/Vector;
    //   168: iload_1
    //   169: invokevirtual 695	java/util/Vector:get	(I)Ljava/lang/Object;
    //   172: checkcast 653	android/util/Pair
    //   175: astore 7
    //   177: aload 7
    //   179: getfield 704	android/util/Pair:first	Ljava/lang/Object;
    //   182: ifnull +43 -> 225
    //   185: aload 7
    //   187: getfield 704	android/util/Pair:first	Ljava/lang/Object;
    //   190: checkcast 647	java/lang/Integer
    //   193: invokevirtual 707	java/lang/Integer:intValue	()I
    //   196: istore_3
    //   197: iload_3
    //   198: iload_2
    //   199: if_icmpne +26 -> 225
    //   202: aload 6
    //   204: monitorexit
    //   205: aload 4
    //   207: invokevirtual 567	android/os/Parcel:recycle	()V
    //   210: aload 5
    //   212: invokevirtual 567	android/os/Parcel:recycle	()V
    //   215: iload_1
    //   216: ireturn
    //   217: astore 5
    //   219: aload 4
    //   221: monitorexit
    //   222: aload 5
    //   224: athrow
    //   225: iload_1
    //   226: iconst_1
    //   227: iadd
    //   228: istore_1
    //   229: goto -76 -> 153
    //   232: aload 6
    //   234: monitorexit
    //   235: aload 4
    //   237: invokevirtual 567	android/os/Parcel:recycle	()V
    //   240: aload 5
    //   242: invokevirtual 567	android/os/Parcel:recycle	()V
    //   245: iconst_m1
    //   246: ireturn
    //   247: astore 7
    //   249: aload 6
    //   251: monitorexit
    //   252: aload 7
    //   254: athrow
    //   255: astore 6
    //   257: aload 4
    //   259: invokevirtual 567	android/os/Parcel:recycle	()V
    //   262: aload 5
    //   264: invokevirtual 567	android/os/Parcel:recycle	()V
    //   267: aload 6
    //   269: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	270	0	this	MediaPlayer
    //   0	270	1	paramInt	int
    //   41	159	2	i	int
    //   77	123	3	j	int
    //   35	223	4	localObject1	Object
    //   24	187	5	localObject2	Object
    //   217	46	5	localObject3	Object
    //   146	104	6	localVector	Vector
    //   255	13	6	localObject4	Object
    //   175	11	7	localPair	Pair
    //   247	6	7	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   42	78	217	finally
    //   153	197	247	finally
    //   108	151	255	finally
    //   202	205	255	finally
    //   232	235	255	finally
    //   249	255	255	finally
  }
  
  public native SyncParams getSyncParams();
  
  public MediaTimestamp getTimestamp()
  {
    try
    {
      long l1 = getCurrentPosition();
      long l2 = System.nanoTime();
      if (isPlaying()) {}
      for (float f = getPlaybackParams().getSpeed();; f = 0.0F)
      {
        MediaTimestamp localMediaTimestamp = new MediaTimestamp(l1 * 1000L, l2, f);
        return localMediaTimestamp;
      }
      return null;
    }
    catch (IllegalStateException localIllegalStateException) {}
  }
  
  public TrackInfo[] getTrackInfo()
    throws IllegalStateException
  {
    TrackInfo[] arrayOfTrackInfo1 = getInbandTrackInfo();
    for (;;)
    {
      TrackInfo[] arrayOfTrackInfo2;
      int i;
      synchronized (this.mIndexTrackPairs)
      {
        arrayOfTrackInfo2 = new TrackInfo[this.mIndexTrackPairs.size()];
        i = 0;
        if (i < arrayOfTrackInfo2.length)
        {
          Object localObject2 = (Pair)this.mIndexTrackPairs.get(i);
          if (((Pair)localObject2).first != null)
          {
            arrayOfTrackInfo2[i] = arrayOfTrackInfo1[((Integer)localObject2.first).intValue()];
          }
          else
          {
            localObject2 = (SubtitleTrack)((Pair)localObject2).second;
            arrayOfTrackInfo2[i] = new TrackInfo(((SubtitleTrack)localObject2).getTrackType(), ((SubtitleTrack)localObject2).getFormat());
          }
        }
      }
      return arrayOfTrackInfo2;
      i += 1;
    }
  }
  
  public native int getVideoHeight();
  
  public native int getVideoWidth();
  
  public void invoke(Parcel paramParcel1, Parcel paramParcel2)
  {
    int i = native_invoke(paramParcel1, paramParcel2);
    paramParcel2.setDataPosition(0);
    if (i != 0) {
      throw new RuntimeException("failure code: " + i);
    }
  }
  
  public native boolean isLooping();
  
  public native boolean isPlaying();
  
  public Parcel newRequest()
  {
    Parcel localParcel = Parcel.obtain();
    localParcel.writeInterfaceToken("android.media.IMediaPlayer");
    return localParcel;
  }
  
  public void onSubtitleTrackSelected(SubtitleTrack paramSubtitleTrack)
  {
    if (this.mSelectedSubtitleTrackIndex >= 0) {}
    try
    {
      selectOrDeselectInbandTrack(this.mSelectedSubtitleTrackIndex, false);
      this.mSelectedSubtitleTrackIndex = -1;
      setOnSubtitleDataListener(null);
      if (paramSubtitleTrack == null) {
        return;
      }
      synchronized (this.mIndexTrackPairs)
      {
        Iterator localIterator = this.mIndexTrackPairs.iterator();
        while (localIterator.hasNext())
        {
          Pair localPair = (Pair)localIterator.next();
          if ((localPair.first != null) && (localPair.second == paramSubtitleTrack)) {
            this.mSelectedSubtitleTrackIndex = ((Integer)localPair.first).intValue();
          }
        }
        if (this.mSelectedSubtitleTrackIndex < 0) {}
      }
      try
      {
        selectOrDeselectInbandTrack(this.mSelectedSubtitleTrackIndex, true);
        setOnSubtitleDataListener(this.mSubtitleDataListener);
        return;
        paramSubtitleTrack = finally;
        throw paramSubtitleTrack;
      }
      catch (IllegalStateException paramSubtitleTrack)
      {
        for (;;) {}
      }
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;) {}
    }
  }
  
  public void pause()
    throws IllegalStateException
  {
    stayAwake(false);
    _pause();
  }
  
  int playerSetAuxEffectSendLevel(float paramFloat)
  {
    _setAuxEffectSendLevel(paramFloat);
    return 0;
  }
  
  void playerSetVolume(float paramFloat1, float paramFloat2)
  {
    _setVolume(paramFloat1, paramFloat2);
  }
  
  public void prepare()
    throws IOException, IllegalStateException
  {
    _prepare();
    scanInternalSubtitleTracks();
  }
  
  public native void prepareAsync()
    throws IllegalStateException;
  
  public void release()
  {
    baseRelease();
    stayAwake(false);
    updateSurfaceScreenOn();
    this.mOnPreparedListener = null;
    this.mOnBufferingUpdateListener = null;
    this.mOnCompletionListener = null;
    this.mOnSeekCompleteListener = null;
    this.mOnErrorListener = null;
    this.mOnInfoListener = null;
    this.mOnVideoSizeChangedListener = null;
    this.mOnTimedTextListener = null;
    if (this.mTimeProvider != null)
    {
      this.mTimeProvider.close();
      this.mTimeProvider = null;
    }
    this.mOnSubtitleDataListener = null;
    _release();
  }
  
  public void reset()
  {
    this.mSelectedSubtitleTrackIndex = -1;
    synchronized (this.mOpenSubtitleSources)
    {
      Iterator localIterator = this.mOpenSubtitleSources.iterator();
      while (localIterator.hasNext())
      {
        InputStream localInputStream = (InputStream)localIterator.next();
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException) {}
      }
      this.mOpenSubtitleSources.clear();
      if (this.mSubtitleController != null) {
        this.mSubtitleController.reset();
      }
      if (this.mTimeProvider != null)
      {
        this.mTimeProvider.close();
        this.mTimeProvider = null;
      }
      stayAwake(false);
      _reset();
      if (this.mEventHandler != null) {
        this.mEventHandler.removeCallbacksAndMessages(null);
      }
    }
    synchronized (this.mIndexTrackPairs)
    {
      this.mIndexTrackPairs.clear();
      this.mInbandTrackIndices.clear();
      return;
      localObject1 = finally;
      throw ((Throwable)localObject1);
    }
  }
  
  public native void seekTo(int paramInt)
    throws IllegalStateException;
  
  public void selectTrack(int paramInt)
    throws IllegalStateException
  {
    selectOrDeselectTrack(paramInt, true);
  }
  
  public void setAudioAttributes(AudioAttributes paramAudioAttributes)
    throws IllegalArgumentException
  {
    boolean bool = false;
    if (paramAudioAttributes == null) {
      throw new IllegalArgumentException("Cannot set AudioAttributes to null");
    }
    baseUpdateAudioAttributes(paramAudioAttributes);
    this.mUsage = paramAudioAttributes.getUsage();
    if ((paramAudioAttributes.getAllFlags() & 0x40) != 0) {
      bool = true;
    }
    this.mBypassInterruptionPolicy = bool;
    Parcel localParcel = Parcel.obtain();
    paramAudioAttributes.writeToParcel(localParcel, 1);
    setParameter(1400, localParcel);
    localParcel.recycle();
  }
  
  public native void setAudioSessionId(int paramInt)
    throws IllegalArgumentException, IllegalStateException;
  
  public void setAudioStreamType(int paramInt)
  {
    baseUpdateAudioAttributes(new AudioAttributes.Builder().setInternalLegacyStreamType(paramInt).build());
    _setAudioStreamType(paramInt);
    this.mStreamType = paramInt;
  }
  
  public void setAuxEffectSendLevel(float paramFloat)
  {
    baseSetAuxEffectSendLevel(paramFloat);
  }
  
  public void setDataSource(Context paramContext, Uri paramUri)
    throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
  {
    setDataSource(paramContext, paramUri, null);
  }
  
  public void setDataSource(Context paramContext, Uri paramUri, Map<String, String> paramMap)
    throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    Object localObject = paramUri.getScheme();
    if ("file".equals(localObject))
    {
      setDataSource(paramUri.getPath());
      return;
    }
    if (("content".equals(localObject)) && ("settings".equals(paramUri.getAuthority())))
    {
      int i = RingtoneManager.getDefaultType(paramUri);
      localObject = RingtoneManager.getCacheForType(i);
      paramContext = RingtoneManager.getActualDefaultRingtoneUri(paramContext, i);
      if (attemptDataSource(localContentResolver, (Uri)localObject)) {
        return;
      }
      if (attemptDataSource(localContentResolver, paramContext)) {
        return;
      }
      setDataSource(paramUri.toString(), paramMap);
      return;
    }
    if (attemptDataSource(localContentResolver, paramUri)) {
      return;
    }
    setDataSource(paramUri.toString(), paramMap);
  }
  
  public void setDataSource(AssetFileDescriptor paramAssetFileDescriptor)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    Preconditions.checkNotNull(paramAssetFileDescriptor);
    if (paramAssetFileDescriptor.getDeclaredLength() < 0L)
    {
      setDataSource(paramAssetFileDescriptor.getFileDescriptor());
      return;
    }
    setDataSource(paramAssetFileDescriptor.getFileDescriptor(), paramAssetFileDescriptor.getStartOffset(), paramAssetFileDescriptor.getDeclaredLength());
  }
  
  public void setDataSource(MediaDataSource paramMediaDataSource)
    throws IllegalArgumentException, IllegalStateException
  {
    _setDataSource(paramMediaDataSource);
  }
  
  public void setDataSource(FileDescriptor paramFileDescriptor)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    setDataSource(paramFileDescriptor, 0L, 576460752303423487L);
  }
  
  public void setDataSource(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    _setDataSource(paramFileDescriptor, paramLong1, paramLong2);
  }
  
  public void setDataSource(String paramString)
    throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
  {
    setDataSource(paramString, null, null);
  }
  
  public void setDataSource(String paramString, Map<String, String> paramMap)
    throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
  {
    Object localObject1 = null;
    Object localObject2 = null;
    if (paramMap != null)
    {
      String[] arrayOfString1 = new String[paramMap.size()];
      String[] arrayOfString2 = new String[paramMap.size()];
      int i = 0;
      paramMap = paramMap.entrySet().iterator();
      for (;;)
      {
        localObject1 = arrayOfString1;
        localObject2 = arrayOfString2;
        if (!paramMap.hasNext()) {
          break;
        }
        localObject1 = (Map.Entry)paramMap.next();
        arrayOfString1[i] = ((String)((Map.Entry)localObject1).getKey());
        arrayOfString2[i] = ((String)((Map.Entry)localObject1).getValue());
        i += 1;
      }
    }
    setDataSource(paramString, (String[])localObject1, (String[])localObject2);
  }
  
  public void setDisplay(SurfaceHolder paramSurfaceHolder)
  {
    this.mSurfaceHolder = paramSurfaceHolder;
    if (paramSurfaceHolder != null) {}
    for (paramSurfaceHolder = paramSurfaceHolder.getSurface();; paramSurfaceHolder = null)
    {
      _setVideoSurface(paramSurfaceHolder);
      updateSurfaceScreenOn();
      return;
    }
  }
  
  public native void setLooping(boolean paramBoolean);
  
  public int setMetadataFilter(Set<Integer> paramSet1, Set<Integer> paramSet2)
  {
    Parcel localParcel = newRequest();
    int i = localParcel.dataSize() + (paramSet1.size() + 1 + 1 + paramSet2.size()) * 4;
    if (localParcel.dataCapacity() < i) {
      localParcel.setDataCapacity(i);
    }
    localParcel.writeInt(paramSet1.size());
    paramSet1 = paramSet1.iterator();
    while (paramSet1.hasNext()) {
      localParcel.writeInt(((Integer)paramSet1.next()).intValue());
    }
    localParcel.writeInt(paramSet2.size());
    paramSet1 = paramSet2.iterator();
    while (paramSet1.hasNext()) {
      localParcel.writeInt(((Integer)paramSet1.next()).intValue());
    }
    return native_setMetadataFilter(localParcel);
  }
  
  public native void setNextMediaPlayer(MediaPlayer paramMediaPlayer);
  
  public void setOnBufferingUpdateListener(OnBufferingUpdateListener paramOnBufferingUpdateListener)
  {
    this.mOnBufferingUpdateListener = paramOnBufferingUpdateListener;
  }
  
  public void setOnCompletionListener(OnCompletionListener paramOnCompletionListener)
  {
    this.mOnCompletionListener = paramOnCompletionListener;
  }
  
  public void setOnErrorListener(OnErrorListener paramOnErrorListener)
  {
    this.mOnErrorListener = paramOnErrorListener;
  }
  
  public void setOnInfoListener(OnInfoListener paramOnInfoListener)
  {
    this.mOnInfoListener = paramOnInfoListener;
  }
  
  public void setOnPreparedListener(OnPreparedListener paramOnPreparedListener)
  {
    this.mOnPreparedListener = paramOnPreparedListener;
  }
  
  public void setOnSeekCompleteListener(OnSeekCompleteListener paramOnSeekCompleteListener)
  {
    this.mOnSeekCompleteListener = paramOnSeekCompleteListener;
  }
  
  public void setOnSubtitleDataListener(OnSubtitleDataListener paramOnSubtitleDataListener)
  {
    this.mOnSubtitleDataListener = paramOnSubtitleDataListener;
  }
  
  public void setOnTimedMetaDataAvailableListener(OnTimedMetaDataAvailableListener paramOnTimedMetaDataAvailableListener)
  {
    this.mOnTimedMetaDataAvailableListener = paramOnTimedMetaDataAvailableListener;
  }
  
  public void setOnTimedTextListener(OnTimedTextListener paramOnTimedTextListener)
  {
    this.mOnTimedTextListener = paramOnTimedTextListener;
  }
  
  public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener paramOnVideoSizeChangedListener)
  {
    this.mOnVideoSizeChangedListener = paramOnVideoSizeChangedListener;
  }
  
  public native void setPlaybackParams(PlaybackParams paramPlaybackParams);
  
  public void setRetransmitEndpoint(InetSocketAddress paramInetSocketAddress)
    throws IllegalStateException, IllegalArgumentException
  {
    String str = null;
    int i = 0;
    if (paramInetSocketAddress != null)
    {
      str = paramInetSocketAddress.getAddress().getHostAddress();
      i = paramInetSocketAddress.getPort();
    }
    i = native_setRetransmitEndpoint(str, i);
    if (i != 0) {
      throw new IllegalArgumentException("Illegal re-transmit endpoint; native ret " + i);
    }
  }
  
  public void setScreenOnWhilePlaying(boolean paramBoolean)
  {
    if (this.mScreenOnWhilePlaying != paramBoolean)
    {
      if ((paramBoolean) && (this.mSurfaceHolder == null)) {
        Log.w("MediaPlayer", "setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder");
      }
      this.mScreenOnWhilePlaying = paramBoolean;
      updateSurfaceScreenOn();
    }
  }
  
  public void setSubtitleAnchor(SubtitleController paramSubtitleController, SubtitleController.Anchor paramAnchor)
  {
    this.mSubtitleController = paramSubtitleController;
    this.mSubtitleController.setAnchor(paramAnchor);
  }
  
  public void setSurface(Surface paramSurface)
  {
    if ((this.mScreenOnWhilePlaying) && (paramSurface != null)) {
      Log.w("MediaPlayer", "setScreenOnWhilePlaying(true) is ineffective for Surface");
    }
    this.mSurfaceHolder = null;
    _setVideoSurface(paramSurface);
    updateSurfaceScreenOn();
  }
  
  public native void setSyncParams(SyncParams paramSyncParams);
  
  public void setVideoScalingMode(int paramInt)
  {
    if (!isVideoScalingModeSupported(paramInt)) {
      throw new IllegalArgumentException("Scaling mode " + paramInt + " is not supported");
    }
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.media.IMediaPlayer");
      localParcel1.writeInt(6);
      localParcel1.writeInt(paramInt);
      invoke(localParcel1, localParcel2);
      return;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public void setVolume(float paramFloat)
  {
    setVolume(paramFloat, paramFloat);
  }
  
  public void setVolume(float paramFloat1, float paramFloat2)
  {
    baseSetVolume(paramFloat1, paramFloat2);
  }
  
  public void setWakeMode(Context paramContext, int paramInt)
  {
    int i = 0;
    int j = 0;
    if (SystemProperties.getBoolean("audio.offload.ignore_setawake", false))
    {
      Log.w("MediaPlayer", "IGNORING setWakeMode " + paramInt);
      return;
    }
    if (this.mWakeLock != null)
    {
      i = j;
      if (this.mWakeLock.isHeld())
      {
        i = 1;
        this.mWakeLock.release();
      }
      this.mWakeLock = null;
    }
    this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(0x20000000 | paramInt, MediaPlayer.class.getName());
    this.mWakeLock.setReferenceCounted(false);
    if (i != 0) {
      this.mWakeLock.acquire();
    }
  }
  
  public void start()
    throws IllegalStateException
  {
    baseStart();
    if (isRestricted()) {
      _setVolume(0.0F, 0.0F);
    }
    stayAwake(true);
    _start();
  }
  
  public void stop()
    throws IllegalStateException
  {
    stayAwake(false);
    _stop();
  }
  
  private class EventHandler
    extends Handler
  {
    private MediaPlayer mMediaPlayer;
    
    public EventHandler(MediaPlayer paramMediaPlayer, Looper paramLooper)
    {
      super();
      this.mMediaPlayer = paramMediaPlayer;
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (MediaPlayer.-get2(this.mMediaPlayer) == 0L)
      {
        Log.w("MediaPlayer", "mediaplayer went away with unhandled events");
        return;
      }
      Object localObject1;
      boolean bool;
      Object localObject3;
      switch (paramMessage.what)
      {
      default: 
        Log.e("MediaPlayer", "Unknown message type " + paramMessage.what);
        return;
      case 1: 
        try
        {
          MediaPlayer.-wrap0(MediaPlayer.this);
          paramMessage = MediaPlayer.-get7(MediaPlayer.this);
          if (paramMessage != null) {
            paramMessage.onPrepared(this.mMediaPlayer);
          }
          return;
        }
        catch (RuntimeException paramMessage)
        {
          for (;;)
          {
            sendMessage(obtainMessage(100, 1, 64526, null));
          }
        }
      case 2: 
        paramMessage = MediaPlayer.-get4(MediaPlayer.this);
        if (paramMessage != null) {
          paramMessage.onCompletion(this.mMediaPlayer);
        }
        MediaPlayer.-wrap1(MediaPlayer.this, false);
        return;
      case 8: 
        paramMessage = MediaPlayer.-get15(MediaPlayer.this);
        if (paramMessage != null) {
          paramMessage.onStopped();
        }
      case 0: 
      case 6: 
      case 7: 
        do
        {
          return;
          localObject1 = MediaPlayer.-get15(MediaPlayer.this);
        } while (localObject1 == null);
        if (paramMessage.what == 7) {}
        for (bool = true;; bool = false)
        {
          ((MediaPlayer.TimeProvider)localObject1).onPaused(bool);
          return;
        }
      case 3: 
        localObject1 = MediaPlayer.-get3(MediaPlayer.this);
        if (localObject1 != null) {
          ((MediaPlayer.OnBufferingUpdateListener)localObject1).onBufferingUpdate(this.mMediaPlayer, paramMessage.arg1);
        }
        return;
      case 4: 
        paramMessage = MediaPlayer.-get8(MediaPlayer.this);
        if (paramMessage != null) {
          paramMessage.onSeekComplete(this.mMediaPlayer);
        }
      case 9: 
        paramMessage = MediaPlayer.-get15(MediaPlayer.this);
        if (paramMessage != null) {
          paramMessage.onSeekComplete(this.mMediaPlayer);
        }
        return;
      case 5: 
        localObject1 = MediaPlayer.-get12(MediaPlayer.this);
        if (localObject1 != null) {
          ((MediaPlayer.OnVideoSizeChangedListener)localObject1).onVideoSizeChanged(this.mMediaPlayer, paramMessage.arg1, paramMessage.arg2);
        }
        return;
      case 100: 
        Log.e("MediaPlayer", "Error (" + paramMessage.arg1 + "," + paramMessage.arg2 + ")");
        bool = false;
        localObject1 = MediaPlayer.-get5(MediaPlayer.this);
        if (localObject1 != null) {
          bool = ((MediaPlayer.OnErrorListener)localObject1).onError(this.mMediaPlayer, paramMessage.arg1, paramMessage.arg2);
        }
        paramMessage = MediaPlayer.-get4(MediaPlayer.this);
        if ((paramMessage == null) || (bool)) {}
        for (;;)
        {
          MediaPlayer.-wrap1(MediaPlayer.this, false);
          return;
          paramMessage.onCompletion(this.mMediaPlayer);
        }
      case 200: 
        switch (paramMessage.arg1)
        {
        }
        do
        {
          for (;;)
          {
            localObject1 = MediaPlayer.-get6(MediaPlayer.this);
            if (localObject1 != null) {
              ((MediaPlayer.OnInfoListener)localObject1).onInfo(this.mMediaPlayer, paramMessage.arg1, paramMessage.arg2);
            }
            return;
            Log.i("MediaPlayer", "Info (" + paramMessage.arg1 + "," + paramMessage.arg2 + ")");
            continue;
            try
            {
              MediaPlayer.-wrap0(MediaPlayer.this);
              paramMessage.arg1 = 802;
              if (MediaPlayer.-get14(MediaPlayer.this) != null) {
                MediaPlayer.-get14(MediaPlayer.this).selectDefaultTrack();
              }
            }
            catch (RuntimeException localRuntimeException)
            {
              for (;;)
              {
                sendMessage(obtainMessage(100, 1, 64526, null));
              }
              localObject2 = MediaPlayer.-get15(MediaPlayer.this);
            }
          }
        } while (localObject2 == null);
        if (paramMessage.arg1 == 701) {}
        for (bool = true;; bool = false)
        {
          ((MediaPlayer.TimeProvider)localObject2).onBuffering(bool);
          break;
        }
      case 99: 
        localObject2 = MediaPlayer.-get11(MediaPlayer.this);
        if (localObject2 == null) {
          return;
        }
        if (paramMessage.obj == null) {
          ((MediaPlayer.OnTimedTextListener)localObject2).onTimedText(this.mMediaPlayer, null);
        }
        while (!(paramMessage.obj instanceof Parcel)) {
          return;
        }
        paramMessage = (Parcel)paramMessage.obj;
        localObject3 = new TimedText(paramMessage);
        paramMessage.recycle();
        ((MediaPlayer.OnTimedTextListener)localObject2).onTimedText(this.mMediaPlayer, (TimedText)localObject3);
        return;
      case 201: 
        localObject2 = MediaPlayer.-get9(MediaPlayer.this);
        if (localObject2 == null) {
          return;
        }
        if ((paramMessage.obj instanceof Parcel))
        {
          paramMessage = (Parcel)paramMessage.obj;
          localObject3 = new SubtitleData(paramMessage);
          paramMessage.recycle();
          ((MediaPlayer.OnSubtitleDataListener)localObject2).onSubtitleData(this.mMediaPlayer, (SubtitleData)localObject3);
        }
        return;
      }
      Object localObject2 = MediaPlayer.-get10(MediaPlayer.this);
      if (localObject2 == null) {
        return;
      }
      if ((paramMessage.obj instanceof Parcel))
      {
        paramMessage = (Parcel)paramMessage.obj;
        localObject3 = TimedMetaData.createTimedMetaDataFromParcel(paramMessage);
        paramMessage.recycle();
        ((MediaPlayer.OnTimedMetaDataAvailableListener)localObject2).onTimedMetaDataAvailable(this.mMediaPlayer, (TimedMetaData)localObject3);
      }
    }
  }
  
  public static abstract interface OnBufferingUpdateListener
  {
    public abstract void onBufferingUpdate(MediaPlayer paramMediaPlayer, int paramInt);
  }
  
  public static abstract interface OnCompletionListener
  {
    public abstract void onCompletion(MediaPlayer paramMediaPlayer);
  }
  
  public static abstract interface OnErrorListener
  {
    public abstract boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2);
  }
  
  public static abstract interface OnInfoListener
  {
    public abstract boolean onInfo(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2);
  }
  
  public static abstract interface OnPreparedListener
  {
    public abstract void onPrepared(MediaPlayer paramMediaPlayer);
  }
  
  public static abstract interface OnSeekCompleteListener
  {
    public abstract void onSeekComplete(MediaPlayer paramMediaPlayer);
  }
  
  public static abstract interface OnSubtitleDataListener
  {
    public abstract void onSubtitleData(MediaPlayer paramMediaPlayer, SubtitleData paramSubtitleData);
  }
  
  public static abstract interface OnTimedMetaDataAvailableListener
  {
    public abstract void onTimedMetaDataAvailable(MediaPlayer paramMediaPlayer, TimedMetaData paramTimedMetaData);
  }
  
  public static abstract interface OnTimedTextListener
  {
    public abstract void onTimedText(MediaPlayer paramMediaPlayer, TimedText paramTimedText);
  }
  
  public static abstract interface OnVideoSizeChangedListener
  {
    public abstract void onVideoSizeChanged(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2);
  }
  
  static class TimeProvider
    implements MediaPlayer.OnSeekCompleteListener, MediaTimeProvider
  {
    private static final long MAX_EARLY_CALLBACK_US = 1000L;
    private static final long MAX_NS_WITHOUT_POSITION_CHECK = 5000000000L;
    private static final int NOTIFY = 1;
    private static final int NOTIFY_SEEK = 3;
    private static final int NOTIFY_STOP = 2;
    private static final int NOTIFY_TIME = 0;
    private static final int NOTIFY_TRACK_DATA = 4;
    private static final int REFRESH_AND_NOTIFY_TIME = 1;
    private static final String TAG = "MTP";
    private static final long TIME_ADJUSTMENT_RATE = 2L;
    public boolean DEBUG = false;
    private boolean mBuffering;
    private Handler mEventHandler;
    private HandlerThread mHandlerThread;
    private long mLastNanoTime;
    private long mLastReportedTime;
    private long mLastTimeUs = 0L;
    private MediaTimeProvider.OnMediaTimeListener[] mListeners;
    private boolean mPaused = true;
    private boolean mPausing = false;
    private MediaPlayer mPlayer;
    private boolean mRefresh = false;
    private boolean mSeeking = false;
    private boolean mStopped = true;
    private long mTimeAdjustment;
    private long[] mTimes;
    
    public TimeProvider(MediaPlayer paramMediaPlayer)
    {
      this.mPlayer = paramMediaPlayer;
      try
      {
        getCurrentTimeUs(true, false);
        Looper localLooper = Looper.myLooper();
        paramMediaPlayer = localLooper;
        if (localLooper == null)
        {
          localLooper = Looper.getMainLooper();
          paramMediaPlayer = localLooper;
          if (localLooper == null)
          {
            this.mHandlerThread = new HandlerThread("MediaPlayerMTPEventThread", -2);
            this.mHandlerThread.start();
            paramMediaPlayer = this.mHandlerThread.getLooper();
          }
        }
        this.mEventHandler = new EventHandler(paramMediaPlayer);
        this.mListeners = new MediaTimeProvider.OnMediaTimeListener[0];
        this.mTimes = new long[0];
        this.mLastTimeUs = 0L;
        this.mTimeAdjustment = 0L;
        return;
      }
      catch (IllegalStateException paramMediaPlayer)
      {
        for (;;)
        {
          this.mRefresh = true;
        }
      }
    }
    
    private long getEstimatedTime(long paramLong, boolean paramBoolean)
    {
      if (this.mPaused) {
        this.mLastReportedTime = (this.mLastTimeUs + this.mTimeAdjustment);
      }
      for (;;)
      {
        return this.mLastReportedTime;
        paramLong = (paramLong - this.mLastNanoTime) / 1000L;
        this.mLastReportedTime = (this.mLastTimeUs + paramLong);
        if (this.mTimeAdjustment > 0L)
        {
          paramLong = this.mTimeAdjustment - paramLong / 2L;
          if (paramLong <= 0L) {
            this.mTimeAdjustment = 0L;
          } else {
            this.mLastReportedTime += paramLong;
          }
        }
      }
    }
    
    private void notifySeek()
    {
      int i = 0;
      for (;;)
      {
        try
        {
          this.mSeeking = false;
          try
          {
            l = getCurrentTimeUs(true, false);
            if (this.DEBUG) {
              Log.d("MTP", "onSeekComplete at " + l);
            }
            MediaTimeProvider.OnMediaTimeListener[] arrayOfOnMediaTimeListener = this.mListeners;
            int j = arrayOfOnMediaTimeListener.length;
            if (i < j)
            {
              localOnMediaTimeListener = arrayOfOnMediaTimeListener[i];
              if (localOnMediaTimeListener != null) {
                continue;
              }
            }
          }
          catch (IllegalStateException localIllegalStateException)
          {
            long l;
            MediaTimeProvider.OnMediaTimeListener localOnMediaTimeListener;
            if (!this.DEBUG) {
              continue;
            }
            Log.d("MTP", "onSeekComplete but no player");
            this.mPausing = true;
            notifyTimedEvent(false);
            continue;
          }
          return;
        }
        finally {}
        localOnMediaTimeListener.onSeek(l);
        i += 1;
      }
    }
    
    /* Error */
    private void notifyStop()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 146	android/media/MediaPlayer$TimeProvider:mListeners	[Landroid/media/MediaTimeProvider$OnMediaTimeListener;
      //   6: astore_3
      //   7: iconst_0
      //   8: istore_1
      //   9: aload_3
      //   10: arraylength
      //   11: istore_2
      //   12: iload_1
      //   13: iload_2
      //   14: if_icmpge +13 -> 27
      //   17: aload_3
      //   18: iload_1
      //   19: aaload
      //   20: astore 4
      //   22: aload 4
      //   24: ifnonnull +6 -> 30
      //   27: aload_0
      //   28: monitorexit
      //   29: return
      //   30: aload 4
      //   32: invokeinterface 187 1 0
      //   37: iload_1
      //   38: iconst_1
      //   39: iadd
      //   40: istore_1
      //   41: goto -29 -> 12
      //   44: astore_3
      //   45: aload_0
      //   46: monitorexit
      //   47: aload_3
      //   48: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	49	0	this	TimeProvider
      //   8	33	1	i	int
      //   11	4	2	j	int
      //   6	12	3	arrayOfOnMediaTimeListener	MediaTimeProvider.OnMediaTimeListener[]
      //   44	4	3	localObject	Object
      //   20	11	4	localOnMediaTimeListener	MediaTimeProvider.OnMediaTimeListener
      // Exception table:
      //   from	to	target	type
      //   2	7	44	finally
      //   9	12	44	finally
      //   30	37	44	finally
    }
    
    private void notifyTimedEvent(boolean paramBoolean)
    {
      for (;;)
      {
        long l1;
        int i;
        long[] arrayOfLong;
        int j;
        int k;
        try
        {
          l1 = getCurrentTimeUs(paramBoolean, true);
          l2 = l1;
          paramBoolean = this.mSeeking;
          if (paramBoolean) {
            return;
          }
        }
        catch (IllegalStateException localIllegalStateException)
        {
          this.mRefresh = true;
          this.mPausing = true;
          l1 = getCurrentTimeUs(paramBoolean, true);
          continue;
          if (this.DEBUG)
          {
            localObject2 = new StringBuilder();
            ((StringBuilder)localObject2).append("notifyTimedEvent(").append(this.mLastTimeUs).append(" -> ").append(l1).append(") from {");
            i = 1;
            arrayOfLong = this.mTimes;
            j = 0;
            k = arrayOfLong.length;
            break label428;
            if (i == 0) {
              ((StringBuilder)localObject2).append(", ");
            }
            Object localObject1;
            ((StringBuilder)localObject2).append(localObject1);
            i = 0;
            break label449;
            ((StringBuilder)localObject2).append("}");
            Log.d("MTP", ((StringBuilder)localObject2).toString());
          }
          Object localObject2 = new Vector();
          i = 0;
          if ((i >= this.mTimes.length) || (this.mListeners[i] == null))
          {
            if ((l2 > l1) && (!this.mPaused)) {
              break label369;
            }
            this.mEventHandler.removeMessages(1);
            localObject2 = ((Iterable)localObject2).iterator();
            if (!((Iterator)localObject2).hasNext()) {
              break label425;
            }
            ((MediaTimeProvider.OnMediaTimeListener)((Iterator)localObject2).next()).onTimedEvent(l1);
            continue;
          }
        }
        finally {}
        long l3;
        if (this.mTimes[i] <= -1L)
        {
          l3 = l2;
        }
        else if (this.mTimes[i] <= 1000L + l1)
        {
          ((Vector)localObject3).add(this.mListeners[i]);
          if (this.DEBUG) {
            Log.d("MTP", "removed");
          }
          this.mTimes[i] = -1L;
          l3 = l2;
        }
        else if (l2 != l1)
        {
          l3 = l2;
          if (this.mTimes[i] >= l2) {}
        }
        else
        {
          l3 = this.mTimes[i];
          break label456;
          label369:
          if (this.DEBUG) {
            Log.d("MTP", "scheduling for " + l2 + " and " + l1);
          }
          scheduleNotification(0, l2 - l1);
          continue;
          label425:
          return;
          for (;;)
          {
            label428:
            if (j >= k) {
              break label454;
            }
            l3 = arrayOfLong[j];
            if (l3 != -1L) {
              break;
            }
            label449:
            j += 1;
          }
          label454:
          continue;
        }
        label456:
        i += 1;
        long l2 = l3;
      }
    }
    
    private void notifyTrackData(Pair<SubtitleTrack, byte[]> paramPair)
    {
      try
      {
        ((SubtitleTrack)paramPair.first).onData((byte[])paramPair.second, true, -1L);
        return;
      }
      finally
      {
        paramPair = finally;
        throw paramPair;
      }
    }
    
    private int registerListener(MediaTimeProvider.OnMediaTimeListener paramOnMediaTimeListener)
    {
      int i = 0;
      for (;;)
      {
        if ((i >= this.mListeners.length) || (this.mListeners[i] == paramOnMediaTimeListener) || (this.mListeners[i] == null))
        {
          if (i >= this.mListeners.length)
          {
            MediaTimeProvider.OnMediaTimeListener[] arrayOfOnMediaTimeListener = new MediaTimeProvider.OnMediaTimeListener[i + 1];
            long[] arrayOfLong = new long[i + 1];
            System.arraycopy(this.mListeners, 0, arrayOfOnMediaTimeListener, 0, this.mListeners.length);
            System.arraycopy(this.mTimes, 0, arrayOfLong, 0, this.mTimes.length);
            this.mListeners = arrayOfOnMediaTimeListener;
            this.mTimes = arrayOfLong;
          }
          if (this.mListeners[i] == null)
          {
            this.mListeners[i] = paramOnMediaTimeListener;
            this.mTimes[i] = -1L;
          }
          return i;
        }
        i += 1;
      }
    }
    
    private void scheduleNotification(int paramInt, long paramLong)
    {
      if ((this.mSeeking) && ((paramInt == 0) || (paramInt == 1))) {
        return;
      }
      if (this.DEBUG) {
        Log.v("MTP", "scheduleNotification " + paramInt + " in " + paramLong);
      }
      this.mEventHandler.removeMessages(1);
      Message localMessage = this.mEventHandler.obtainMessage(1, paramInt, 0);
      this.mEventHandler.sendMessageDelayed(localMessage, (int)(paramLong / 1000L));
    }
    
    /* Error */
    public void cancelNotifications(MediaTimeProvider.OnMediaTimeListener paramOnMediaTimeListener)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: iconst_0
      //   3: istore_2
      //   4: iload_2
      //   5: aload_0
      //   6: getfield 146	android/media/MediaPlayer$TimeProvider:mListeners	[Landroid/media/MediaTimeProvider$OnMediaTimeListener;
      //   9: arraylength
      //   10: if_icmpge +89 -> 99
      //   13: aload_0
      //   14: getfield 146	android/media/MediaPlayer$TimeProvider:mListeners	[Landroid/media/MediaTimeProvider$OnMediaTimeListener;
      //   17: iload_2
      //   18: aaload
      //   19: aload_1
      //   20: if_acmpne +88 -> 108
      //   23: aload_0
      //   24: getfield 146	android/media/MediaPlayer$TimeProvider:mListeners	[Landroid/media/MediaTimeProvider$OnMediaTimeListener;
      //   27: iload_2
      //   28: iconst_1
      //   29: iadd
      //   30: aload_0
      //   31: getfield 146	android/media/MediaPlayer$TimeProvider:mListeners	[Landroid/media/MediaTimeProvider$OnMediaTimeListener;
      //   34: iload_2
      //   35: aload_0
      //   36: getfield 146	android/media/MediaPlayer$TimeProvider:mListeners	[Landroid/media/MediaTimeProvider$OnMediaTimeListener;
      //   39: arraylength
      //   40: iload_2
      //   41: isub
      //   42: iconst_1
      //   43: isub
      //   44: invokestatic 268	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   47: aload_0
      //   48: getfield 148	android/media/MediaPlayer$TimeProvider:mTimes	[J
      //   51: iload_2
      //   52: iconst_1
      //   53: iadd
      //   54: aload_0
      //   55: getfield 148	android/media/MediaPlayer$TimeProvider:mTimes	[J
      //   58: iload_2
      //   59: aload_0
      //   60: getfield 148	android/media/MediaPlayer$TimeProvider:mTimes	[J
      //   63: arraylength
      //   64: iload_2
      //   65: isub
      //   66: iconst_1
      //   67: isub
      //   68: invokestatic 271	java/lang/System:arraycopy	([JI[JII)V
      //   71: aload_0
      //   72: getfield 146	android/media/MediaPlayer$TimeProvider:mListeners	[Landroid/media/MediaTimeProvider$OnMediaTimeListener;
      //   75: aload_0
      //   76: getfield 146	android/media/MediaPlayer$TimeProvider:mListeners	[Landroid/media/MediaTimeProvider$OnMediaTimeListener;
      //   79: arraylength
      //   80: iconst_1
      //   81: isub
      //   82: aconst_null
      //   83: aastore
      //   84: aload_0
      //   85: getfield 148	android/media/MediaPlayer$TimeProvider:mTimes	[J
      //   88: aload_0
      //   89: getfield 148	android/media/MediaPlayer$TimeProvider:mTimes	[J
      //   92: arraylength
      //   93: iconst_1
      //   94: isub
      //   95: ldc2_w 226
      //   98: lastore
      //   99: aload_0
      //   100: iconst_0
      //   101: lconst_0
      //   102: invokespecial 241	android/media/MediaPlayer$TimeProvider:scheduleNotification	(IJ)V
      //   105: aload_0
      //   106: monitorexit
      //   107: return
      //   108: aload_0
      //   109: getfield 146	android/media/MediaPlayer$TimeProvider:mListeners	[Landroid/media/MediaTimeProvider$OnMediaTimeListener;
      //   112: iload_2
      //   113: aaload
      //   114: astore_3
      //   115: aload_3
      //   116: ifnull -17 -> 99
      //   119: iload_2
      //   120: iconst_1
      //   121: iadd
      //   122: istore_2
      //   123: goto -119 -> 4
      //   126: astore_1
      //   127: aload_0
      //   128: monitorexit
      //   129: aload_1
      //   130: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	131	0	this	TimeProvider
      //   0	131	1	paramOnMediaTimeListener	MediaTimeProvider.OnMediaTimeListener
      //   3	120	2	i	int
      //   114	2	3	localOnMediaTimeListener	MediaTimeProvider.OnMediaTimeListener
      // Exception table:
      //   from	to	target	type
      //   4	99	126	finally
      //   99	105	126	finally
      //   108	115	126	finally
    }
    
    public void close()
    {
      this.mEventHandler.removeMessages(1);
      if (this.mHandlerThread != null)
      {
        this.mHandlerThread.quitSafely();
        this.mHandlerThread = null;
      }
    }
    
    protected void finalize()
    {
      if (this.mHandlerThread != null) {
        this.mHandlerThread.quitSafely();
      }
    }
    
    public long getCurrentTimeUs(boolean paramBoolean1, boolean paramBoolean2)
      throws IllegalStateException
    {
      boolean bool = true;
      label316:
      for (;;)
      {
        try
        {
          long l1;
          if ((!this.mPaused) || (paramBoolean1))
          {
            l1 = System.nanoTime();
            if (!paramBoolean1)
            {
              long l2 = this.mLastNanoTime;
              if (l1 < l2 + 5000000000L) {
                continue;
              }
            }
          }
          try
          {
            this.mLastTimeUs = (this.mPlayer.getCurrentPosition() * 1000L);
            paramBoolean1 = bool;
            if (this.mPlayer.isPlaying()) {
              paramBoolean1 = this.mBuffering;
            }
            this.mPaused = paramBoolean1;
            if (this.DEBUG)
            {
              StringBuilder localStringBuilder = new StringBuilder();
              if (this.mPaused)
              {
                str = "paused";
                Log.v("MTP", str + " at " + this.mLastTimeUs);
              }
            }
            else
            {
              this.mLastNanoTime = l1;
              if ((!paramBoolean2) || (this.mLastTimeUs >= this.mLastReportedTime)) {
                break label316;
              }
              this.mTimeAdjustment = (this.mLastReportedTime - this.mLastTimeUs);
              if (this.mTimeAdjustment > 1000000L)
              {
                this.mStopped = false;
                this.mSeeking = true;
                scheduleNotification(3, 0L);
              }
              l1 = getEstimatedTime(l1, paramBoolean2);
              return l1;
              l1 = this.mLastReportedTime;
              return l1;
            }
            String str = "playing";
            continue;
            localObject = finally;
          }
          catch (IllegalStateException localIllegalStateException)
          {
            if (this.mPausing)
            {
              this.mPausing = false;
              getEstimatedTime(l1, paramBoolean2);
              this.mPaused = true;
              if (this.DEBUG) {
                Log.d("MTP", "illegal state, but pausing: estimating at " + this.mLastReportedTime);
              }
              l1 = this.mLastReportedTime;
              return l1;
            }
            throw localIllegalStateException;
          }
          this.mTimeAdjustment = 0L;
        }
        finally {}
      }
    }
    
    public void notifyAt(long paramLong, MediaTimeProvider.OnMediaTimeListener paramOnMediaTimeListener)
    {
      try
      {
        if (this.DEBUG) {
          Log.d("MTP", "notifyAt " + paramLong);
        }
        this.mTimes[registerListener(paramOnMediaTimeListener)] = paramLong;
        scheduleNotification(0, 0L);
        return;
      }
      finally {}
    }
    
    public void onBuffering(boolean paramBoolean)
    {
      try
      {
        if (this.DEBUG) {
          Log.d("MTP", "onBuffering: " + paramBoolean);
        }
        this.mBuffering = paramBoolean;
        scheduleNotification(1, 0L);
        return;
      }
      finally {}
    }
    
    public void onNewPlayer()
    {
      if (this.mRefresh) {}
      try
      {
        this.mStopped = false;
        this.mSeeking = true;
        this.mBuffering = false;
        scheduleNotification(3, 0L);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    /* Error */
    public void onPaused(boolean paramBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 109	android/media/MediaPlayer$TimeProvider:DEBUG	Z
      //   6: ifeq +29 -> 35
      //   9: ldc 37
      //   11: new 158	java/lang/StringBuilder
      //   14: dup
      //   15: invokespecial 159	java/lang/StringBuilder:<init>	()V
      //   18: ldc_w 338
      //   21: invokevirtual 165	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   24: iload_1
      //   25: invokevirtual 334	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   28: invokevirtual 172	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   31: invokestatic 178	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   34: pop
      //   35: aload_0
      //   36: getfield 101	android/media/MediaPlayer$TimeProvider:mStopped	Z
      //   39: ifeq +22 -> 61
      //   42: aload_0
      //   43: iconst_0
      //   44: putfield 101	android/media/MediaPlayer$TimeProvider:mStopped	Z
      //   47: aload_0
      //   48: iconst_1
      //   49: putfield 107	android/media/MediaPlayer$TimeProvider:mSeeking	Z
      //   52: aload_0
      //   53: iconst_3
      //   54: lconst_0
      //   55: invokespecial 241	android/media/MediaPlayer$TimeProvider:scheduleNotification	(IJ)V
      //   58: aload_0
      //   59: monitorexit
      //   60: return
      //   61: aload_0
      //   62: iload_1
      //   63: putfield 105	android/media/MediaPlayer$TimeProvider:mPausing	Z
      //   66: aload_0
      //   67: iconst_0
      //   68: putfield 107	android/media/MediaPlayer$TimeProvider:mSeeking	Z
      //   71: aload_0
      //   72: iconst_1
      //   73: lconst_0
      //   74: invokespecial 241	android/media/MediaPlayer$TimeProvider:scheduleNotification	(IJ)V
      //   77: goto -19 -> 58
      //   80: astore_2
      //   81: aload_0
      //   82: monitorexit
      //   83: aload_2
      //   84: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	85	0	this	TimeProvider
      //   0	85	1	paramBoolean	boolean
      //   80	4	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	35	80	finally
      //   35	58	80	finally
      //   61	77	80	finally
    }
    
    public void onSeekComplete(MediaPlayer paramMediaPlayer)
    {
      try
      {
        this.mStopped = false;
        this.mSeeking = true;
        scheduleNotification(3, 0L);
        return;
      }
      finally
      {
        paramMediaPlayer = finally;
        throw paramMediaPlayer;
      }
    }
    
    public void onStopped()
    {
      try
      {
        if (this.DEBUG) {
          Log.d("MTP", "onStopped");
        }
        this.mPaused = true;
        this.mStopped = true;
        this.mSeeking = false;
        this.mBuffering = false;
        scheduleNotification(2, 0L);
        return;
      }
      finally {}
    }
    
    public void scheduleUpdate(MediaTimeProvider.OnMediaTimeListener paramOnMediaTimeListener)
    {
      try
      {
        if (this.DEBUG) {
          Log.d("MTP", "scheduleUpdate");
        }
        int i = registerListener(paramOnMediaTimeListener);
        if (!this.mStopped)
        {
          this.mTimes[i] = 0L;
          scheduleNotification(0, 0L);
        }
        return;
      }
      finally {}
    }
    
    private class EventHandler
      extends Handler
    {
      public EventHandler(Looper paramLooper)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        if (paramMessage.what == 1) {}
        switch (paramMessage.arg1)
        {
        default: 
          return;
        case 0: 
          MediaPlayer.TimeProvider.-wrap2(MediaPlayer.TimeProvider.this, false);
          return;
        case 1: 
          MediaPlayer.TimeProvider.-wrap2(MediaPlayer.TimeProvider.this, true);
          return;
        case 2: 
          MediaPlayer.TimeProvider.-wrap1(MediaPlayer.TimeProvider.this);
          return;
        case 3: 
          MediaPlayer.TimeProvider.-wrap0(MediaPlayer.TimeProvider.this);
          return;
        }
        MediaPlayer.TimeProvider.-wrap3(MediaPlayer.TimeProvider.this, (Pair)paramMessage.obj);
      }
    }
  }
  
  public static class TrackInfo
    implements Parcelable
  {
    static final Parcelable.Creator<TrackInfo> CREATOR = new Parcelable.Creator()
    {
      public MediaPlayer.TrackInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaPlayer.TrackInfo(paramAnonymousParcel);
      }
      
      public MediaPlayer.TrackInfo[] newArray(int paramAnonymousInt)
      {
        return new MediaPlayer.TrackInfo[paramAnonymousInt];
      }
    };
    public static final int MEDIA_TRACK_TYPE_AUDIO = 2;
    public static final int MEDIA_TRACK_TYPE_METADATA = 5;
    public static final int MEDIA_TRACK_TYPE_SUBTITLE = 4;
    public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = 3;
    public static final int MEDIA_TRACK_TYPE_UNKNOWN = 0;
    public static final int MEDIA_TRACK_TYPE_VIDEO = 1;
    final MediaFormat mFormat;
    final int mTrackType;
    
    TrackInfo(int paramInt, MediaFormat paramMediaFormat)
    {
      this.mTrackType = paramInt;
      this.mFormat = paramMediaFormat;
    }
    
    TrackInfo(Parcel paramParcel)
    {
      this.mTrackType = paramParcel.readInt();
      this.mFormat = MediaFormat.createSubtitleFormat(paramParcel.readString(), paramParcel.readString());
      if (this.mTrackType == 4)
      {
        this.mFormat.setInteger("is-autoselect", paramParcel.readInt());
        this.mFormat.setInteger("is-default", paramParcel.readInt());
        this.mFormat.setInteger("is-forced-subtitle", paramParcel.readInt());
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public MediaFormat getFormat()
    {
      if ((this.mTrackType == 3) || (this.mTrackType == 4)) {
        return this.mFormat;
      }
      return null;
    }
    
    public String getLanguage()
    {
      String str2 = this.mFormat.getString("language");
      String str1 = str2;
      if (str2 == null) {
        str1 = "und";
      }
      return str1;
    }
    
    public int getTrackType()
    {
      return this.mTrackType;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append(getClass().getName());
      localStringBuilder.append('{');
      switch (this.mTrackType)
      {
      default: 
        localStringBuilder.append("UNKNOWN");
      }
      for (;;)
      {
        localStringBuilder.append(", ").append(this.mFormat.toString());
        localStringBuilder.append("}");
        return localStringBuilder.toString();
        localStringBuilder.append("VIDEO");
        continue;
        localStringBuilder.append("AUDIO");
        continue;
        localStringBuilder.append("TIMEDTEXT");
        continue;
        localStringBuilder.append("SUBTITLE");
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mTrackType);
      paramParcel.writeString(getLanguage());
      if (this.mTrackType == 4)
      {
        paramParcel.writeString(this.mFormat.getString("mime"));
        paramParcel.writeInt(this.mFormat.getInteger("is-autoselect"));
        paramParcel.writeInt(this.mFormat.getInteger("is-default"));
        paramParcel.writeInt(this.mFormat.getInteger("is-forced-subtitle"));
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */