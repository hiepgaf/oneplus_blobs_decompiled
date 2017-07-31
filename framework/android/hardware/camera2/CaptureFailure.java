package android.hardware.camera2;

public class CaptureFailure
{
  public static final int REASON_ERROR = 0;
  public static final int REASON_FLUSHED = 1;
  private final boolean mDropped;
  private final long mFrameNumber;
  private final int mReason;
  private final CaptureRequest mRequest;
  private final int mSequenceId;
  
  public CaptureFailure(CaptureRequest paramCaptureRequest, int paramInt1, boolean paramBoolean, int paramInt2, long paramLong)
  {
    this.mRequest = paramCaptureRequest;
    this.mReason = paramInt1;
    this.mDropped = paramBoolean;
    this.mSequenceId = paramInt2;
    this.mFrameNumber = paramLong;
  }
  
  public long getFrameNumber()
  {
    return this.mFrameNumber;
  }
  
  public int getReason()
  {
    return this.mReason;
  }
  
  public CaptureRequest getRequest()
  {
    return this.mRequest;
  }
  
  public int getSequenceId()
  {
    return this.mSequenceId;
  }
  
  public boolean wasImageCaptured()
  {
    return !this.mDropped;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/CaptureFailure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */