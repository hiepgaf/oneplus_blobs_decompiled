package com.oneplus.gallery2.media;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore.Video.Media;
import android.util.Size;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.media.VideoMetadata;
import com.oneplus.media.VideoUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

final class VideoMediaStoreMedia
  extends MediaStoreMedia
  implements VideoMedia
{
  private static final String CONTENT_URI_STRING_VIDEO = CONTENT_URI_VIDEO.toString();
  private static final Uri CONTENT_URI_VIDEO;
  private static final ExecutorService FILE_INFO_EXECUTOR = Executors.newFixedThreadPool(1);
  private static final int INTERNAL_FLAG_SLOW_MOTION = 1;
  private static final int INTERNAL_FLAG_TIME_LAPSE = 2;
  private static final ExecutorService LARGE_FILE_INFO_EXECUTOR = Executors.newFixedThreadPool(1);
  private static final long LARGE_VIDEO_FILE_SIZE = 1073741824L;
  private static final String TAG = VideoMediaStoreMedia.class.getSimpleName();
  private Long m_Duration;
  private List<CallbackHandle<VideoMedia.DurationCallback>> m_DurationCallbackHandles;
  private FileParsingTask m_FileParsingTask;
  private int m_InternalFlags;
  private Size m_Size;
  private List<CallbackHandle<Media.SizeCallback>> m_SizeCallbackHandles;
  
  static
  {
    CONTENT_URI_VIDEO = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
  }
  
  public VideoMediaStoreMedia(MediaStoreMediaSource paramMediaStoreMediaSource, MediaStoreMedia.DbValues paramDbValues, MediaStoreMedia.FileInfo paramFileInfo)
  {
    super(paramMediaStoreMediaSource, paramDbValues, paramFileInfo);
  }
  
  public static Uri createContentUri(long paramLong)
  {
    return Uri.parse(CONTENT_URI_STRING_VIDEO + "/" + paramLong);
  }
  
  private void onVideoFileParsed(FileParsingTask paramFileParsingTask)
  {
    int k = 0;
    int i;
    if (this.m_FileParsingTask == paramFileParsingTask)
    {
      this.m_FileParsingTask = null;
      if (this.m_Duration != null) {
        break label70;
      }
      if (this.m_Duration != null) {
        break label90;
      }
      i = 0;
      label32:
      this.m_Duration = Long.valueOf(paramFileParsingTask.duration);
      label43:
      if (paramFileParsingTask.size != null) {
        break label99;
      }
      label50:
      if (this.m_DurationCallbackHandles != null) {
        break label140;
      }
      label57:
      if (this.m_SizeCallbackHandles != null) {
        break label216;
      }
    }
    for (;;)
    {
      if (i != 0) {
        break label328;
      }
      return;
      return;
      label70:
      if (this.m_Duration.longValue() != paramFileParsingTask.duration) {
        break;
      }
      i = 0;
      break label43;
      label90:
      i = FLAG_DURATION_CHANGED | 0x0;
      break label32;
      label99:
      if (paramFileParsingTask.size.equals(this.m_Size)) {
        break label50;
      }
      if (this.m_Size == null) {}
      for (;;)
      {
        this.m_Size = paramFileParsingTask.size;
        break;
        i |= FLAG_SIZE_CHANGED;
      }
      label140:
      long l = this.m_Duration.longValue();
      int j = this.m_DurationCallbackHandles.size() - 1;
      if (j >= 0)
      {
        paramFileParsingTask = (VideoMedia.DurationCallback)((CallbackHandle)this.m_DurationCallbackHandles.get(j)).getCallback();
        if (paramFileParsingTask == null) {}
        for (;;)
        {
          j -= 1;
          break;
          paramFileParsingTask.onDurationObtained(this, l);
        }
      }
      this.m_DurationCallbackHandles = null;
      break label57;
      label216:
      label232:
      int m;
      if (this.m_Size == null)
      {
        j = 0;
        if (this.m_Size != null) {
          break label295;
        }
        m = this.m_SizeCallbackHandles.size() - 1;
        label245:
        if (m < 0) {
          break label320;
        }
        paramFileParsingTask = (Media.SizeCallback)((CallbackHandle)this.m_SizeCallbackHandles.get(m)).getCallback();
        if (paramFileParsingTask != null) {
          break label307;
        }
      }
      for (;;)
      {
        m -= 1;
        break label245;
        j = this.m_Size.getWidth();
        break;
        label295:
        k = this.m_Size.getHeight();
        break label232;
        label307:
        paramFileParsingTask.onSizeObtained(this, j, k);
      }
      label320:
      this.m_SizeCallbackHandles = null;
    }
    label328:
    ((MediaStoreMediaSource)getSource()).notifyMediaUpdatedByItself(this, i);
  }
  
  static final boolean parseVideoContentUri(Uri paramUri, Ref<Long> paramRef)
  {
    return parseContentUri(CONTENT_URI_VIDEO, paramUri, paramRef);
  }
  
  private void parseVideoFile(final FileParsingTask paramFileParsingTask)
  {
    long l1 = SystemClock.elapsedRealtime();
    Object localObject = getContentUri();
    if (localObject == null)
    {
      localObject = getFilePath();
      if (localObject != null) {
        break label103;
      }
      localObject = null;
    }
    for (;;)
    {
      long l2 = SystemClock.elapsedRealtime();
      Log.d(TAG, "parseVideoFile() - Take " + (l2 - l1) + " ms to read metadata from " + this);
      if (localObject != null) {
        break;
      }
      HandlerUtils.post(this, new Runnable()
      {
        public void run()
        {
          VideoMediaStoreMedia.this.onVideoFileParsed(paramFileParsingTask);
        }
      });
      return;
      localObject = VideoUtils.readMetadata(BaseApplication.current(), (Uri)localObject);
      continue;
      label103:
      localObject = VideoUtils.readMetadata((String)localObject);
    }
    int i = ((Integer)((VideoMetadata)localObject).get(VideoMetadata.PROP_WIDTH)).intValue();
    int j = ((Integer)((VideoMetadata)localObject).get(VideoMetadata.PROP_HEIGHT)).intValue();
    int k = ((Integer)((VideoMetadata)localObject).get(VideoMetadata.PROP_ORIENTATION)).intValue();
    if (i <= 0) {}
    for (;;)
    {
      Log.d(TAG, "parseVideoFile() - Media : ", this, ", size : ", paramFileParsingTask.size, ", orientation : ", Integer.valueOf(k));
      paramFileParsingTask.duration = ((Long)((VideoMetadata)localObject).get(VideoMetadata.PROP_DURATION)).longValue();
      break;
      if (j > 0) {
        if (k % 180 != 0) {
          paramFileParsingTask.size = new Size(j, i);
        } else {
          paramFileParsingTask.size = new Size(i, j);
        }
      }
    }
  }
  
  protected Uri createContentUri(MediaStoreMedia.DbValues paramDbValues)
  {
    return createContentUri(paramDbValues.id);
  }
  
  public Handle getDetails(Media.DetailsCallback paramDetailsCallback)
  {
    if (paramDetailsCallback == null) {}
    for (;;)
    {
      return new EmptyHandle("GetVideoDetails");
      paramDetailsCallback.onDetailsObtained(this, null);
    }
  }
  
  public Handle getDuration(VideoMedia.DurationCallback paramDurationCallback)
  {
    verifyAccess();
    if (this.m_Duration == null)
    {
      paramDurationCallback = new CallbackHandle("GetVideoDuration", paramDurationCallback, null)
      {
        protected void onClose(int paramAnonymousInt)
        {
          if (VideoMediaStoreMedia.this.m_DurationCallbackHandles == null) {
            if (VideoMediaStoreMedia.this.m_SizeCallbackHandles == null) {
              break label64;
            }
          }
          label64:
          while ((VideoMediaStoreMedia.this.m_FileParsingTask == null) || (!VideoMediaStoreMedia.this.m_FileParsingTask.cancel()))
          {
            return;
            if (!VideoMediaStoreMedia.this.m_DurationCallbackHandles.remove(this)) {}
            while (!VideoMediaStoreMedia.this.m_DurationCallbackHandles.isEmpty()) {
              return;
            }
            VideoMediaStoreMedia.this.m_DurationCallbackHandles = null;
            break;
          }
          VideoMediaStoreMedia.this.m_FileParsingTask = null;
        }
      };
      if (this.m_DurationCallbackHandles == null) {
        break label84;
      }
    }
    for (;;)
    {
      this.m_DurationCallbackHandles.add(paramDurationCallback);
      if (this.m_FileParsingTask == null) {
        break;
      }
      return paramDurationCallback;
      if (paramDurationCallback == null) {}
      for (;;)
      {
        return new EmptyHandle("GetVideoDuration");
        paramDurationCallback.onDurationObtained(this, this.m_Duration.longValue());
      }
      label84:
      this.m_DurationCallbackHandles = new ArrayList();
    }
    this.m_FileParsingTask = new FileParsingTask(null);
    if (getFileSize() < 1073741824L) {}
    for (int i = 1; i == 0; i = 0)
    {
      this.m_FileParsingTask.executorFuture = LARGE_FILE_INFO_EXECUTOR.submit(this.m_FileParsingTask);
      return paramDurationCallback;
    }
    this.m_FileParsingTask.executorFuture = FILE_INFO_EXECUTOR.submit(this.m_FileParsingTask);
    return paramDurationCallback;
  }
  
  public Handle getSize(Media.SizeCallback paramSizeCallback)
  {
    verifyAccess();
    if (this.m_Size == null)
    {
      paramSizeCallback = new CallbackHandle("GetVideoSize", paramSizeCallback, null)
      {
        protected void onClose(int paramAnonymousInt)
        {
          if (VideoMediaStoreMedia.this.m_SizeCallbackHandles == null) {
            if (VideoMediaStoreMedia.this.m_DurationCallbackHandles == null) {
              break label64;
            }
          }
          label64:
          while ((VideoMediaStoreMedia.this.m_FileParsingTask == null) || (!VideoMediaStoreMedia.this.m_FileParsingTask.cancel()))
          {
            return;
            if (!VideoMediaStoreMedia.this.m_SizeCallbackHandles.remove(this)) {}
            while (!VideoMediaStoreMedia.this.m_SizeCallbackHandles.isEmpty()) {
              return;
            }
            VideoMediaStoreMedia.this.m_SizeCallbackHandles = null;
            break;
          }
          VideoMediaStoreMedia.this.m_FileParsingTask = null;
        }
      };
      if (this.m_SizeCallbackHandles == null) {
        break label91;
      }
    }
    for (;;)
    {
      this.m_SizeCallbackHandles.add(paramSizeCallback);
      if (this.m_FileParsingTask == null) {
        break;
      }
      return paramSizeCallback;
      if (paramSizeCallback == null) {}
      for (;;)
      {
        return new EmptyHandle("GetVideoSize");
        paramSizeCallback.onSizeObtained(this, this.m_Size.getWidth(), this.m_Size.getHeight());
      }
      label91:
      this.m_SizeCallbackHandles = new ArrayList();
    }
    this.m_FileParsingTask = new FileParsingTask(null);
    if (getFileSize() < 1073741824L) {}
    for (int i = 1; i == 0; i = 0)
    {
      this.m_FileParsingTask.executorFuture = LARGE_FILE_INFO_EXECUTOR.submit(this.m_FileParsingTask);
      return paramSizeCallback;
    }
    this.m_FileParsingTask.executorFuture = FILE_INFO_EXECUTOR.submit(this.m_FileParsingTask);
    return paramSizeCallback;
  }
  
  public boolean isSlowMotion()
  {
    return (this.m_InternalFlags & 0x1) != 0;
  }
  
  public boolean isTimeLapse()
  {
    return (this.m_InternalFlags & 0x2) != 0;
  }
  
  protected int onUpdate(MediaStoreMedia.DbValues paramDbValues, MediaStoreMedia.FileInfo paramFileInfo, boolean paramBoolean)
  {
    int i = 0;
    int k = super.onUpdate(paramDbValues, paramFileInfo, paramBoolean);
    paramFileInfo = paramDbValues.extraInfo;
    label32:
    label39:
    label50:
    int j;
    if (paramFileInfo == null)
    {
      this.m_InternalFlags &= 0xFFFFFFFE;
      if (!isSlowMotion()) {
        break label135;
      }
      this.m_InternalFlags &= 0xFFFFFFFD;
      if (paramDbValues.duration <= 0L) {
        i = 1;
      }
      j = k;
      if (i == 0)
      {
        if (this.m_Duration != null) {
          break label165;
        }
        label78:
        this.m_Duration = Long.valueOf(paramDbValues.duration);
        j = k | FLAG_DURATION_CHANGED;
      }
      label97:
      if ((FLAG_LAST_MODIFIED_TIME_CHANGED & j) != 0) {
        break label187;
      }
    }
    label135:
    label165:
    label187:
    do
    {
      return j;
      if ((paramFileInfo.oneplusFlags & 0x4) == 0L) {
        break;
      }
      this.m_InternalFlags |= 0x1;
      break label32;
      if ((paramFileInfo == null) || ((paramFileInfo.oneplusFlags & 0x8) == 0L)) {
        break label39;
      }
      this.m_InternalFlags |= 0x2;
      break label50;
      j = k;
      if (this.m_Duration.longValue() == paramDbValues.duration) {
        break label97;
      }
      break label78;
      this.m_Size = null;
    } while (this.m_FileParsingTask == null);
    Log.v(TAG, "onUpdate() - Media ", getContentUri(), " has been modified, restart video file parsing");
    this.m_FileParsingTask.cancel();
    this.m_FileParsingTask = new FileParsingTask(null);
    this.m_FileParsingTask.executorFuture = FILE_INFO_EXECUTOR.submit(this.m_FileParsingTask);
    return j;
  }
  
  public Long peekDuration()
  {
    return this.m_Duration;
  }
  
  public Size peekSize()
  {
    return this.m_Size;
  }
  
  private final class FileParsingTask
    implements Runnable
  {
    public volatile long duration;
    public volatile Future<?> executorFuture;
    public volatile Size size;
    
    private FileParsingTask() {}
    
    public boolean cancel()
    {
      if (this.executorFuture != null)
      {
        if (!this.executorFuture.cancel(true)) {
          return false;
        }
      }
      else {
        return true;
      }
      this.executorFuture = null;
      return true;
    }
    
    public void run()
    {
      VideoMediaStoreMedia.this.parseVideoFile(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/VideoMediaStoreMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */