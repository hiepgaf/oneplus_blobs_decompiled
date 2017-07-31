package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraConstrainedHighSpeedCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.utils.SurfaceUtils;
import android.os.Handler;
import android.util.Range;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CameraConstrainedHighSpeedCaptureSessionImpl
  extends CameraConstrainedHighSpeedCaptureSession
  implements CameraCaptureSessionCore
{
  private final CameraCharacteristics mCharacteristics;
  private final CameraCaptureSessionImpl mSessionImpl;
  
  CameraConstrainedHighSpeedCaptureSessionImpl(int paramInt, List<Surface> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler1, CameraDeviceImpl paramCameraDeviceImpl, Handler paramHandler2, boolean paramBoolean, CameraCharacteristics paramCameraCharacteristics)
  {
    this.mCharacteristics = paramCameraCharacteristics;
    this.mSessionImpl = new CameraCaptureSessionImpl(paramInt, null, paramList, new WrapperCallback(paramStateCallback), paramHandler1, paramCameraDeviceImpl, paramHandler2, paramBoolean);
  }
  
  private boolean isConstrainedHighSpeedRequestList(List<CaptureRequest> paramList)
  {
    Preconditions.checkCollectionNotEmpty(paramList, "High speed request list");
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      if (!((CaptureRequest)paramList.next()).isPartOfCRequestList()) {
        return false;
      }
    }
    return true;
  }
  
  public void abortCaptures()
    throws CameraAccessException
  {
    this.mSessionImpl.abortCaptures();
  }
  
  public int capture(CaptureRequest paramCaptureRequest, CameraCaptureSession.CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
  }
  
  public int captureBurst(List<CaptureRequest> paramList, CameraCaptureSession.CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if (!isConstrainedHighSpeedRequestList(paramList)) {
      throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
    }
    return this.mSessionImpl.captureBurst(paramList, paramCaptureCallback, paramHandler);
  }
  
  public void close()
  {
    this.mSessionImpl.close();
  }
  
  public List<CaptureRequest> createHighSpeedRequestList(CaptureRequest paramCaptureRequest)
    throws CameraAccessException
  {
    if (paramCaptureRequest == null) {
      throw new IllegalArgumentException("Input capture request must not be null");
    }
    Object localObject2 = paramCaptureRequest.getTargets();
    Object localObject1 = (Range)paramCaptureRequest.get(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE);
    SurfaceUtils.checkConstrainedHighSpeedSurfaces((Collection)localObject2, (Range)localObject1, (StreamConfigurationMap)this.mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP));
    int j = ((Integer)((Range)localObject1).getUpper()).intValue() / 30;
    ArrayList localArrayList = new ArrayList();
    CaptureRequest.Builder localBuilder2 = new CaptureRequest.Builder(new CameraMetadataNative(paramCaptureRequest.getNativeCopy()), false, -1);
    Iterator localIterator = ((Collection)localObject2).iterator();
    localObject1 = (Surface)localIterator.next();
    CaptureRequest.Builder localBuilder1;
    label259:
    int i;
    if ((((Collection)localObject2).size() == 1) && (SurfaceUtils.isSurfaceForHwVideoEncoder((Surface)localObject1)))
    {
      localBuilder2.set(CaptureRequest.CONTROL_CAPTURE_INTENT, Integer.valueOf(1));
      localBuilder2.setPartOfCHSRequestList(true);
      localBuilder1 = null;
      if (((Collection)localObject2).size() != 2) {
        break label308;
      }
      localBuilder1 = new CaptureRequest.Builder(new CameraMetadataNative(paramCaptureRequest.getNativeCopy()), false, -1);
      localBuilder1.set(CaptureRequest.CONTROL_CAPTURE_INTENT, Integer.valueOf(3));
      localBuilder1.addTarget((Surface)localObject1);
      localObject2 = (Surface)localIterator.next();
      localBuilder1.addTarget((Surface)localObject2);
      localBuilder1.setPartOfCHSRequestList(true);
      paramCaptureRequest = (CaptureRequest)localObject1;
      if (!SurfaceUtils.isSurfaceForHwVideoEncoder((Surface)localObject1)) {
        paramCaptureRequest = (CaptureRequest)localObject2;
      }
      localBuilder2.addTarget(paramCaptureRequest);
      paramCaptureRequest = localBuilder1;
      i = 0;
      label261:
      if (i >= j) {
        break label337;
      }
      if ((i != 0) || (paramCaptureRequest == null)) {
        break label321;
      }
      localArrayList.add(paramCaptureRequest.build());
    }
    for (;;)
    {
      i += 1;
      break label261;
      localBuilder2.set(CaptureRequest.CONTROL_CAPTURE_INTENT, Integer.valueOf(3));
      break;
      label308:
      localBuilder2.addTarget((Surface)localObject1);
      paramCaptureRequest = localBuilder1;
      break label259;
      label321:
      localArrayList.add(localBuilder2.build());
    }
    label337:
    return Collections.unmodifiableList(localArrayList);
  }
  
  public void finishDeferredConfiguration(List<OutputConfiguration> paramList)
    throws CameraAccessException
  {
    this.mSessionImpl.finishDeferredConfiguration(paramList);
  }
  
  public CameraDevice getDevice()
  {
    return this.mSessionImpl.getDevice();
  }
  
  public CameraDeviceImpl.StateCallbackKK getDeviceStateCallback()
  {
    return this.mSessionImpl.getDeviceStateCallback();
  }
  
  public Surface getInputSurface()
  {
    return null;
  }
  
  public boolean isAborting()
  {
    return this.mSessionImpl.isAborting();
  }
  
  public boolean isReprocessable()
  {
    return false;
  }
  
  public void prepare(int paramInt, Surface paramSurface)
    throws CameraAccessException
  {
    this.mSessionImpl.prepare(paramInt, paramSurface);
  }
  
  public void prepare(Surface paramSurface)
    throws CameraAccessException
  {
    this.mSessionImpl.prepare(paramSurface);
  }
  
  public void replaceSessionClose()
  {
    this.mSessionImpl.replaceSessionClose();
  }
  
  public int setRepeatingBurst(List<CaptureRequest> paramList, CameraCaptureSession.CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if (!isConstrainedHighSpeedRequestList(paramList)) {
      throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
    }
    return this.mSessionImpl.setRepeatingBurst(paramList, paramCaptureCallback, paramHandler);
  }
  
  public int setRepeatingRequest(CaptureRequest paramCaptureRequest, CameraCaptureSession.CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
  }
  
  public void stopRepeating()
    throws CameraAccessException
  {
    this.mSessionImpl.stopRepeating();
  }
  
  public void tearDown(Surface paramSurface)
    throws CameraAccessException
  {
    this.mSessionImpl.tearDown(paramSurface);
  }
  
  private class WrapperCallback
    extends CameraCaptureSession.StateCallback
  {
    private final CameraCaptureSession.StateCallback mCallback;
    
    public WrapperCallback(CameraCaptureSession.StateCallback paramStateCallback)
    {
      this.mCallback = paramStateCallback;
    }
    
    public void onActive(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mCallback.onActive(CameraConstrainedHighSpeedCaptureSessionImpl.this);
    }
    
    public void onClosed(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mCallback.onClosed(CameraConstrainedHighSpeedCaptureSessionImpl.this);
    }
    
    public void onConfigureFailed(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mCallback.onConfigureFailed(CameraConstrainedHighSpeedCaptureSessionImpl.this);
    }
    
    public void onConfigured(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mCallback.onConfigured(CameraConstrainedHighSpeedCaptureSessionImpl.this);
    }
    
    public void onReady(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mCallback.onReady(CameraConstrainedHighSpeedCaptureSessionImpl.this);
    }
    
    public void onSurfacePrepared(CameraCaptureSession paramCameraCaptureSession, Surface paramSurface)
    {
      this.mCallback.onSurfacePrepared(CameraConstrainedHighSpeedCaptureSessionImpl.this, paramSurface);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/impl/CameraConstrainedHighSpeedCaptureSessionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */