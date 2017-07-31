package android.hardware.camera2.legacy;

import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.Collection;
import java.util.Iterator;

public class RequestHolder
{
  private static final String TAG = "RequestHolder";
  private volatile boolean mFailed = false;
  private final long mFrameNumber;
  private final Collection<Long> mJpegSurfaceIds;
  private final int mNumJpegTargets;
  private final int mNumPreviewTargets;
  private boolean mOutputAbandoned = false;
  private final boolean mRepeating;
  private final CaptureRequest mRequest;
  private final int mRequestId;
  private final int mSubsequeceId;
  
  private RequestHolder(int paramInt1, int paramInt2, CaptureRequest paramCaptureRequest, boolean paramBoolean, long paramLong, int paramInt3, int paramInt4, Collection<Long> paramCollection)
  {
    this.mRepeating = paramBoolean;
    this.mRequest = paramCaptureRequest;
    this.mRequestId = paramInt1;
    this.mSubsequeceId = paramInt2;
    this.mFrameNumber = paramLong;
    this.mNumJpegTargets = paramInt3;
    this.mNumPreviewTargets = paramInt4;
    this.mJpegSurfaceIds = paramCollection;
  }
  
  public void failRequest()
  {
    Log.w("RequestHolder", "Capture failed for request: " + getRequestId());
    this.mFailed = true;
  }
  
  public long getFrameNumber()
  {
    return this.mFrameNumber;
  }
  
  public Collection<Surface> getHolderTargets()
  {
    return getRequest().getTargets();
  }
  
  public CaptureRequest getRequest()
  {
    return this.mRequest;
  }
  
  public int getRequestId()
  {
    return this.mRequestId;
  }
  
  public int getSubsequeceId()
  {
    return this.mSubsequeceId;
  }
  
  public boolean hasJpegTargets()
  {
    boolean bool = false;
    if (this.mNumJpegTargets > 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasPreviewTargets()
  {
    boolean bool = false;
    if (this.mNumPreviewTargets > 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isOutputAbandoned()
  {
    return this.mOutputAbandoned;
  }
  
  public boolean isRepeating()
  {
    return this.mRepeating;
  }
  
  public boolean jpegType(Surface paramSurface)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    return LegacyCameraDevice.containsSurfaceId(paramSurface, this.mJpegSurfaceIds);
  }
  
  public int numJpegTargets()
  {
    return this.mNumJpegTargets;
  }
  
  public int numPreviewTargets()
  {
    return this.mNumPreviewTargets;
  }
  
  public boolean requestFailed()
  {
    return this.mFailed;
  }
  
  public void setOutputAbandoned()
  {
    this.mOutputAbandoned = true;
  }
  
  public static final class Builder
  {
    private final Collection<Long> mJpegSurfaceIds;
    private final int mNumJpegTargets;
    private final int mNumPreviewTargets;
    private final boolean mRepeating;
    private final CaptureRequest mRequest;
    private final int mRequestId;
    private final int mSubsequenceId;
    
    public Builder(int paramInt1, int paramInt2, CaptureRequest paramCaptureRequest, boolean paramBoolean, Collection<Long> paramCollection)
    {
      Preconditions.checkNotNull(paramCaptureRequest, "request must not be null");
      this.mRequestId = paramInt1;
      this.mSubsequenceId = paramInt2;
      this.mRequest = paramCaptureRequest;
      this.mRepeating = paramBoolean;
      this.mJpegSurfaceIds = paramCollection;
      this.mNumJpegTargets = numJpegTargets(this.mRequest);
      this.mNumPreviewTargets = numPreviewTargets(this.mRequest);
    }
    
    private boolean jpegType(Surface paramSurface)
      throws LegacyExceptionUtils.BufferQueueAbandonedException
    {
      return LegacyCameraDevice.containsSurfaceId(paramSurface, this.mJpegSurfaceIds);
    }
    
    private int numJpegTargets(CaptureRequest paramCaptureRequest)
    {
      int i = 0;
      paramCaptureRequest = paramCaptureRequest.getTargets().iterator();
      while (paramCaptureRequest.hasNext())
      {
        Surface localSurface = (Surface)paramCaptureRequest.next();
        try
        {
          boolean bool = jpegType(localSurface);
          if (bool) {
            i += 1;
          }
        }
        catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException)
        {
          Log.d("RequestHolder", "Surface abandoned, skipping...", localBufferQueueAbandonedException);
        }
      }
      return i;
    }
    
    private int numPreviewTargets(CaptureRequest paramCaptureRequest)
    {
      int i = 0;
      paramCaptureRequest = paramCaptureRequest.getTargets().iterator();
      while (paramCaptureRequest.hasNext())
      {
        Surface localSurface = (Surface)paramCaptureRequest.next();
        try
        {
          boolean bool = previewType(localSurface);
          if (bool) {
            i += 1;
          }
        }
        catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException)
        {
          Log.d("RequestHolder", "Surface abandoned, skipping...", localBufferQueueAbandonedException);
        }
      }
      return i;
    }
    
    private boolean previewType(Surface paramSurface)
      throws LegacyExceptionUtils.BufferQueueAbandonedException
    {
      return !jpegType(paramSurface);
    }
    
    public RequestHolder build(long paramLong)
    {
      return new RequestHolder(this.mRequestId, this.mSubsequenceId, this.mRequest, this.mRepeating, paramLong, this.mNumJpegTargets, this.mNumPreviewTargets, this.mJpegSurfaceIds, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/RequestHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */