package com.oneplus.gallery2.media;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Size;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.media.VideoMetadata;
import com.oneplus.media.VideoUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class TempVideoMedia
  extends TempMedia
  implements VideoMedia
{
  private static final ExecutorService FILE_INFO_EXECUTOR = Executors.newFixedThreadPool(1);
  private static final String TAG = TempVideoMedia.class.getSimpleName();
  private Long m_Duration;
  private List<CallbackHandle<VideoMedia.DurationCallback>> m_DurationCallbackHandles;
  private Runnable m_FileParsingTask;
  private Size m_Size;
  private List<CallbackHandle<Media.SizeCallback>> m_SizeCallbackHandles;
  
  public TempVideoMedia(Uri paramUri, String paramString1, String paramString2)
  {
    super(MediaType.VIDEO, paramUri, paramString1, paramString2);
  }
  
  private void onVideoFileParsed(long paramLong, Size paramSize)
  {
    int j = 0;
    this.m_Duration = Long.valueOf(paramLong);
    this.m_Size = paramSize;
    this.m_FileParsingTask = null;
    if (this.m_DurationCallbackHandles == null) {}
    int i;
    while (this.m_SizeCallbackHandles == null)
    {
      return;
      i = this.m_DurationCallbackHandles.size() - 1;
      if (i >= 0)
      {
        VideoMedia.DurationCallback localDurationCallback = (VideoMedia.DurationCallback)((CallbackHandle)this.m_DurationCallbackHandles.get(i)).getCallback();
        if (localDurationCallback == null) {}
        for (;;)
        {
          i -= 1;
          break;
          localDurationCallback.onDurationObtained(this, paramLong);
        }
      }
      this.m_DurationCallbackHandles = null;
    }
    label121:
    int k;
    if (paramSize == null)
    {
      i = 0;
      if (paramSize != null) {
        break label182;
      }
      k = this.m_SizeCallbackHandles.size() - 1;
      label134:
      if (k < 0) {
        break label205;
      }
      paramSize = (Media.SizeCallback)((CallbackHandle)this.m_SizeCallbackHandles.get(k)).getCallback();
      if (paramSize != null) {
        break label191;
      }
    }
    for (;;)
    {
      k -= 1;
      break label134;
      i = paramSize.getWidth();
      break;
      label182:
      j = paramSize.getHeight();
      break label121;
      label191:
      paramSize.onSizeObtained(this, i, j);
    }
    label205:
    this.m_SizeCallbackHandles = null;
  }
  
  private void parseVideoFile()
  {
    if (this.m_FileParsingTask == null)
    {
      this.m_FileParsingTask = new Runnable()
      {
        public void run()
        {
          final long l1 = SystemClock.elapsedRealtime();
          VideoMetadata localVideoMetadata = VideoUtils.readMetadata(BaseApplication.current(), TempVideoMedia.this.getContentUri());
          long l2 = SystemClock.elapsedRealtime();
          Log.d(TempVideoMedia.TAG, "parseVideoFile() - Take " + (l2 - l1) + " ms to read metadata from " + TempVideoMedia.this);
          Size localSize;
          if (localVideoMetadata == null)
          {
            l1 = 0L;
            localSize = new Size(0, 0);
            HandlerUtils.post(TempVideoMedia.this, new Runnable()
            {
              public void run()
              {
                TempVideoMedia.this.onVideoFileParsed(l1, this.val$size);
              }
            });
            return;
          }
          int i = ((Integer)localVideoMetadata.get(VideoMetadata.PROP_WIDTH)).intValue();
          int j = ((Integer)localVideoMetadata.get(VideoMetadata.PROP_HEIGHT)).intValue();
          int k = ((Integer)localVideoMetadata.get(VideoMetadata.PROP_ORIENTATION)).intValue();
          if (i <= 0) {
            label158:
            localSize = new Size(0, 0);
          }
          for (;;)
          {
            Log.d(TempVideoMedia.TAG, "parseVideoFile() - Media : ", TempVideoMedia.this, ", size : ", localSize, ", orientation : ", Integer.valueOf(k));
            l1 = ((Long)localVideoMetadata.get(VideoMetadata.PROP_DURATION)).longValue();
            break;
            if (j <= 0) {
              break label158;
            }
            if (k % 180 != 0) {
              localSize = new Size(j, i);
            } else {
              localSize = new Size(i, j);
            }
          }
        }
      };
      FILE_INFO_EXECUTOR.submit(this.m_FileParsingTask);
      return;
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
          TempVideoMedia.this.verifyAccess();
          if (TempVideoMedia.this.m_DurationCallbackHandles == null) {}
          while ((!TempVideoMedia.this.m_DurationCallbackHandles.remove(this)) || (!TempVideoMedia.this.m_DurationCallbackHandles.isEmpty())) {
            return;
          }
          TempVideoMedia.this.m_DurationCallbackHandles = null;
        }
      };
      if (this.m_DurationCallbackHandles == null) {
        break label79;
      }
    }
    for (;;)
    {
      this.m_DurationCallbackHandles.add(paramDurationCallback);
      parseVideoFile();
      return paramDurationCallback;
      if (paramDurationCallback == null) {}
      for (;;)
      {
        return new EmptyHandle("GetVideoDuration");
        paramDurationCallback.onDurationObtained(this, this.m_Duration.longValue());
      }
      label79:
      this.m_DurationCallbackHandles = new ArrayList();
    }
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
          TempVideoMedia.this.verifyAccess();
          if (TempVideoMedia.this.m_SizeCallbackHandles == null) {}
          while ((!TempVideoMedia.this.m_SizeCallbackHandles.remove(this)) || (!TempVideoMedia.this.m_SizeCallbackHandles.isEmpty())) {
            return;
          }
          TempVideoMedia.this.m_SizeCallbackHandles = null;
        }
      };
      if (this.m_SizeCallbackHandles == null) {
        break label86;
      }
    }
    for (;;)
    {
      this.m_SizeCallbackHandles.add(paramSizeCallback);
      parseVideoFile();
      return paramSizeCallback;
      if (paramSizeCallback == null) {}
      for (;;)
      {
        return new EmptyHandle("GetVideoSize");
        paramSizeCallback.onSizeObtained(this, this.m_Size.getWidth(), this.m_Size.getHeight());
      }
      label86:
      this.m_SizeCallbackHandles = new ArrayList();
    }
  }
  
  public boolean isSlowMotion()
  {
    return false;
  }
  
  public boolean isTimeLapse()
  {
    return false;
  }
  
  public Long peekDuration()
  {
    return this.m_Duration;
  }
  
  public Size peekSize()
  {
    return this.m_Size;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/TempVideoMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */