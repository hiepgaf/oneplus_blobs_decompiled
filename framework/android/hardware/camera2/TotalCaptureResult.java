package android.hardware.camera2;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.CaptureResultExtras;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TotalCaptureResult
  extends CaptureResult
{
  private final List<CaptureResult> mPartialResults;
  private final int mSessionId;
  
  public TotalCaptureResult(CameraMetadataNative paramCameraMetadataNative, int paramInt)
  {
    super(paramCameraMetadataNative, paramInt);
    this.mPartialResults = new ArrayList();
    this.mSessionId = -1;
  }
  
  public TotalCaptureResult(CameraMetadataNative paramCameraMetadataNative, CaptureRequest paramCaptureRequest, CaptureResultExtras paramCaptureResultExtras, List<CaptureResult> paramList, int paramInt)
  {
    super(paramCameraMetadataNative, paramCaptureRequest, paramCaptureResultExtras);
    if (paramList == null) {}
    for (this.mPartialResults = new ArrayList();; this.mPartialResults = paramList)
    {
      this.mSessionId = paramInt;
      return;
    }
  }
  
  public List<CaptureResult> getPartialResults()
  {
    return Collections.unmodifiableList(this.mPartialResults);
  }
  
  public int getSessionId()
  {
    return this.mSessionId;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/TotalCaptureResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */