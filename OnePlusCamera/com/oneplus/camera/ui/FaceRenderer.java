package com.oneplus.camera.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.SystemClock;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.Face;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.FaceTracker;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.VideoCaptureState;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class FaceRenderer
  extends UIComponent
  implements CameraPreviewOverlay.Renderer
{
  private static final long DURATION_FACE_FRAME_VISIBLE = 3000L;
  private static final float FACE_FRAME_MOVE_SPEED = 0.5F;
  private static final float FACE_FRAME_MOVE_SPEED_FAST = 0.7F;
  private static final float FACE_FRAME_SCALE = 1.3F;
  private static final float THRESHOLD_MOVE_FACE_FASTER = 0.2F;
  private static final float THRESHOLD_STOP_MOVING_FACE = 0.01F;
  private CameraActivity m_CameraActivity;
  private final List<FaceInfo> m_CurrentFaceInfos = new ArrayList();
  private Paint m_FacePaint;
  private FaceTracker m_FaceTracker;
  private final Queue<FaceInfo> m_FreeFaceInfos = new ArrayDeque();
  private CameraPreviewOverlay m_PreviewOverlay;
  
  FaceRenderer(CameraActivity paramCameraActivity)
  {
    super("Face renderer", paramCameraActivity, false);
  }
  
  private void clearFaces()
  {
    if (!this.m_CurrentFaceInfos.isEmpty())
    {
      int i = this.m_CurrentFaceInfos.size() - 1;
      while (i >= 0)
      {
        FaceInfo localFaceInfo = (FaceInfo)this.m_CurrentFaceInfos.get(i);
        localFaceInfo.face = null;
        this.m_FreeFaceInfos.add(localFaceInfo);
        i -= 1;
      }
      this.m_CurrentFaceInfos.clear();
      if (this.m_PreviewOverlay != null) {
        this.m_PreviewOverlay.invalidateCameraPreviewOverlay();
      }
    }
  }
  
  private boolean drawFace(Canvas paramCanvas, CameraPreviewOverlay.RenderingParams paramRenderingParams, FaceInfo paramFaceInfo, long paramLong, boolean paramBoolean)
  {
    if (paramLong - paramFaceInfo.startTime > 3000L) {
      return false;
    }
    if (this.m_FacePaint == null)
    {
      localObject = getCameraActivity().getResources();
      this.m_FacePaint = new Paint();
      this.m_FacePaint.setStyle(Paint.Style.STROKE);
      this.m_FacePaint.setAntiAlias(true);
      this.m_FacePaint.setStrokeWidth(((Resources)localObject).getDimensionPixelSize(2131296424));
      this.m_FacePaint.setColor(((Resources)localObject).getColor(2131230721));
    }
    Object localObject = paramFaceInfo.currentBounds;
    paramFaceInfo = paramFaceInfo.face.getBounds();
    paramRenderingParams = paramRenderingParams.getPreviewBounds();
    boolean bool = false;
    float f5;
    float f4;
    float f3;
    float f2;
    float f6;
    float f1;
    label212:
    label244:
    label276:
    label308:
    label321:
    label380:
    float f8;
    float f9;
    float f10;
    if (!((RectF)localObject).isEmpty())
    {
      f5 = paramFaceInfo.left - ((RectF)localObject).left;
      f4 = paramFaceInfo.top - ((RectF)localObject).top;
      f3 = paramFaceInfo.right - ((RectF)localObject).right;
      f2 = paramFaceInfo.bottom - ((RectF)localObject).bottom;
      if ((Math.abs(f5) > 0.01F) || (Math.abs(f4) > 0.01F))
      {
        bool = true;
        f6 = ((RectF)localObject).left;
        if (f5 >= 0.2F) {
          break label583;
        }
        f1 = 0.5F;
        ((RectF)localObject).left = (f1 * f5 + f6);
        f5 = ((RectF)localObject).top;
        if (f4 >= 0.2F) {
          break label590;
        }
        f1 = 0.5F;
        ((RectF)localObject).top = (f1 * f4 + f5);
        f4 = ((RectF)localObject).right;
        if (f3 >= 0.2F) {
          break label597;
        }
        f1 = 0.5F;
        ((RectF)localObject).right = (f1 * f3 + f4);
        f3 = ((RectF)localObject).bottom;
        if (f2 >= 0.2F) {
          break label604;
        }
        f1 = 0.5F;
        ((RectF)localObject).bottom = (f1 * f2 + f3);
        switch (-getcom-oneplus-base-RotationSwitchesValues()[getCameraActivityRotation().ordinal()])
        {
        default: 
          f4 = ((RectF)localObject).left;
          f3 = ((RectF)localObject).top;
          f2 = ((RectF)localObject).width();
          f1 = ((RectF)localObject).height();
          float f7 = paramRenderingParams.left + paramRenderingParams.width() * f4;
          f8 = paramRenderingParams.top + paramRenderingParams.height() * f3;
          f9 = f7 + paramRenderingParams.width() * f2;
          f10 = f8 + paramRenderingParams.height() * f1;
          f3 = f7;
          f4 = f8;
          f5 = f9;
          f6 = f10;
          if (paramBoolean)
          {
            if (!getCameraActivityRotation().isPortrait()) {
              break label653;
            }
            f4 = paramRenderingParams.top + (paramRenderingParams.bottom - f10);
            f6 = f4 + paramRenderingParams.height() * f1;
            f5 = f9;
            f3 = f7;
          }
          break;
        }
      }
    }
    for (;;)
    {
      f1 = (f5 - f3) * 0.29999995F / 2.0F;
      f2 = (f6 - f4) * 0.29999995F / 2.0F;
      paramCanvas.drawOval(f3 - f1, f4 - f2, f5 + f1, f6 + f2, this.m_FacePaint);
      return bool;
      if ((Math.abs(f3) > 0.01F) || (Math.abs(f2) > 0.01F)) {
        break;
      }
      ((RectF)localObject).set(paramFaceInfo);
      break label321;
      label583:
      f1 = 0.7F;
      break label212;
      label590:
      f1 = 0.7F;
      break label244;
      label597:
      f1 = 0.7F;
      break label276;
      label604:
      f1 = 0.7F;
      break label308;
      ((RectF)localObject).set(paramFaceInfo);
      break label321;
      f4 = 1.0F - ((RectF)localObject).bottom;
      f3 = ((RectF)localObject).left;
      f2 = ((RectF)localObject).height();
      f1 = ((RectF)localObject).width();
      break label380;
      label653:
      f3 = paramRenderingParams.left + (paramRenderingParams.right - f9);
      f5 = f3 + paramRenderingParams.width() * f2;
      f4 = f8;
      f6 = f10;
    }
  }
  
  private void onFacesChanged(List<Camera.Face> paramList)
  {
    Object localObject = getCameraActivity();
    if ((paramList.isEmpty()) || (!((Boolean)((CameraActivity)localObject).get(CameraActivity.PROP_IS_RUNNING)).booleanValue()) || (((Boolean)((CameraActivity)localObject).get(CameraActivity.PROP_IS_CAMERA_SWITCHING)).booleanValue()) || (((Boolean)((CameraActivity)localObject).get(CameraActivity.PROP_IS_BURST_PHOTO_ON_CAPTURE)).booleanValue()))
    {
      clearFaces();
      return;
    }
    int i = this.m_CurrentFaceInfos.size() - 1;
    int m;
    int k;
    int j;
    Camera.Face localFace;
    if (i >= 0)
    {
      localObject = (FaceInfo)this.m_CurrentFaceInfos.get(i);
      m = 0;
      k = m;
      if (((FaceInfo)localObject).face.getId() >= 0) {
        j = paramList.size() - 1;
      }
      for (;;)
      {
        k = m;
        if (j >= 0)
        {
          localFace = (Camera.Face)paramList.get(j);
          if (((FaceInfo)localObject).face.getId() == localFace.getId())
          {
            ((FaceInfo)localObject).face = localFace;
            k = 1;
          }
        }
        else
        {
          if (k == 0)
          {
            ((FaceInfo)localObject).face = null;
            ((FaceInfo)localObject).currentBounds.setEmpty();
            this.m_CurrentFaceInfos.remove(i);
            this.m_FreeFaceInfos.add(localObject);
          }
          i -= 1;
          break;
        }
        j -= 1;
      }
    }
    long l = SystemClock.elapsedRealtime();
    i = paramList.size() - 1;
    if (i >= 0)
    {
      localFace = (Camera.Face)paramList.get(i);
      int n = localFace.getId();
      m = 1;
      k = m;
      if (n >= 0)
      {
        j = this.m_CurrentFaceInfos.size() - 1;
        label292:
        k = m;
        if (j >= 0)
        {
          if (((FaceInfo)this.m_CurrentFaceInfos.get(j)).face.getId() != n) {
            break label399;
          }
          k = 0;
        }
      }
      if (k != 0) {
        if (this.m_FreeFaceInfos.isEmpty()) {
          break label406;
        }
      }
      label399:
      label406:
      for (localObject = (FaceInfo)this.m_FreeFaceInfos.poll();; localObject = new FaceInfo(null))
      {
        ((FaceInfo)localObject).face = localFace;
        ((FaceInfo)localObject).currentBounds.setEmpty();
        ((FaceInfo)localObject).startTime = l;
        this.m_CurrentFaceInfos.add(localObject);
        i -= 1;
        break;
        j -= 1;
        break label292;
      }
    }
    if (this.m_PreviewOverlay != null) {
      this.m_PreviewOverlay.invalidateCameraPreviewOverlay();
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_FaceTracker = ((FaceTracker)findComponent(FaceTracker.class));
    this.m_PreviewOverlay = ((CameraPreviewOverlay)findComponent(CameraPreviewOverlay.class));
    if (this.m_PreviewOverlay != null) {
      this.m_PreviewOverlay.addRenderer(this, 0);
    }
    this.m_CameraActivity = getCameraActivity();
    this.m_CameraActivity.addCallback(CameraActivity.PROP_IS_BURST_PHOTO_ON_CAPTURE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          FaceRenderer.-wrap0(FaceRenderer.this);
        }
      }
    });
    this.m_CameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_SWITCHING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          FaceRenderer.-wrap0(FaceRenderer.this);
        }
      }
    });
    this.m_CameraActivity.addCallback(CameraActivity.PROP_IS_RUNNING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          FaceRenderer.-wrap0(FaceRenderer.this);
        }
      }
    });
    this.m_CameraActivity.addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == PhotoCaptureState.REVIEWING) {
          FaceRenderer.-wrap0(FaceRenderer.this);
        }
      }
    });
    this.m_CameraActivity.addCallback(CameraActivity.PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == VideoCaptureState.REVIEWING) {
          FaceRenderer.-wrap0(FaceRenderer.this);
        }
      }
    });
    if (this.m_FaceTracker != null) {
      this.m_FaceTracker.addCallback(FaceTracker.PROP_FACES, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera.Face>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera.Face>> paramAnonymousPropertyChangeEventArgs)
        {
          if (((Boolean)FaceRenderer.-get0(FaceRenderer.this).get(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue()) {
            FaceRenderer.-wrap1(FaceRenderer.this, (List)paramAnonymousPropertyChangeEventArgs.getNewValue());
          }
        }
      });
    }
  }
  
  public void onRender(Canvas paramCanvas, CameraPreviewOverlay.RenderingParams paramRenderingParams)
  {
    if (this.m_CurrentFaceInfos.isEmpty()) {
      return;
    }
    paramCanvas.save();
    paramCanvas.clipRect(paramRenderingParams.getPreviewBounds());
    for (;;)
    {
      try
      {
        Camera localCamera = getCamera();
        if ((localCamera != null) && (localCamera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT))
        {
          bool2 = true;
          boolean bool1 = false;
          long l = SystemClock.elapsedRealtime();
          int i = this.m_CurrentFaceInfos.size() - 1;
          if (i >= 0)
          {
            bool1 |= drawFace(paramCanvas, paramRenderingParams, (FaceInfo)this.m_CurrentFaceInfos.get(i), l, bool2);
            i -= 1;
            continue;
          }
          if ((bool1) && (this.m_PreviewOverlay != null)) {
            this.m_PreviewOverlay.invalidateCameraPreviewOverlay();
          }
          return;
        }
      }
      finally
      {
        paramCanvas.restore();
      }
      boolean bool2 = false;
    }
  }
  
  private static final class FaceInfo
  {
    public final RectF currentBounds = new RectF();
    public Camera.Face face;
    public long startTime;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/FaceRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */