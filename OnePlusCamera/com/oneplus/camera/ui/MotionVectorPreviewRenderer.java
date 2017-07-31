package com.oneplus.camera.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Message;
import android.os.SystemClock;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.MotionVectorEventArgs;
import com.oneplus.camera.UIComponent;

public class MotionVectorPreviewRenderer
  extends UIComponent
{
  private static final float CIRCLE_RADIUS = 8.0F;
  private static final boolean ENABLE_DRAW_CIRCLE = false;
  private static final boolean ENABLE_DRAW_MOTION_STATE = true;
  private static final boolean ENABLE_DRAW_MOTION_VECTOR = false;
  private static final int MSG_UPDATE_MOTION_STATE = 10005;
  private static final int MSG_UPDATE_MOTION_VECTORS = 10001;
  private static final float STROKE_WIDTH = 4.0F;
  private CameraPreviewOverlay m_CameraPreviewOverlay;
  private PropertyChangedCallback<Integer> m_MotionStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Integer> paramAnonymousPropertyKey, PropertyChangeEventArgs<Integer> paramAnonymousPropertyChangeEventArgs)
    {
      HandlerUtils.sendMessage(MotionVectorPreviewRenderer.this, 10005, ((Integer)paramAnonymousPropertyChangeEventArgs.getNewValue()).intValue(), 0, null);
    }
  };
  private MotionStateHandle m_MotionStateHandle;
  private EventHandler<MotionVectorEventArgs> m_MotionVectorsEventHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MotionVectorEventArgs> paramAnonymousEventKey, MotionVectorEventArgs paramAnonymousMotionVectorEventArgs)
    {
      HandlerUtils.sendMessage(MotionVectorPreviewRenderer.this, 10001, new MotionVectorPreviewRenderer.MotionVectorHandle(MotionVectorPreviewRenderer.this, paramAnonymousMotionVectorEventArgs.getVectors()));
    }
  };
  private MotionVectorHandle m_MotionVectorsHandle;
  private Paint m_Paint;
  private Handle m_PreviewRendererHandle;
  private CameraPreviewOverlay.Renderer m_Renderer = new CameraPreviewOverlay.Renderer()
  {
    public void onRender(Canvas paramAnonymousCanvas, CameraPreviewOverlay.RenderingParams paramAnonymousRenderingParams)
    {
      MotionVectorPreviewRenderer.-wrap2(MotionVectorPreviewRenderer.this, paramAnonymousCanvas);
    }
  };
  private Viewfinder m_Viewfinder;
  
  protected MotionVectorPreviewRenderer(CameraActivity paramCameraActivity)
  {
    super("Motion Vector Preview Renderer", paramCameraActivity, true);
  }
  
  private String getMotionStateText(int paramInt)
  {
    String str = "Motion state: " + "unknown";
    return str + "(" + paramInt + ")";
  }
  
  private void onCameraChanged(final Camera paramCamera1, final Camera paramCamera2)
  {
    if (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_DEBUG_MODE)).booleanValue())
    {
      if (paramCamera2 == null) {
        return;
      }
      HandlerUtils.post(paramCamera2, new Runnable()
      {
        public void run()
        {
          paramCamera1.removeHandler(Camera.EVENT_MOTION_VECTOR_RECEIVED, MotionVectorPreviewRenderer.-get1(MotionVectorPreviewRenderer.this));
          paramCamera1.removeCallback(Camera.PROP_MOTION_STATE, MotionVectorPreviewRenderer.-get0(MotionVectorPreviewRenderer.this));
          paramCamera2.addHandler(Camera.EVENT_MOTION_VECTOR_RECEIVED, MotionVectorPreviewRenderer.-get1(MotionVectorPreviewRenderer.this));
          paramCamera2.addCallback(Camera.PROP_MOTION_STATE, MotionVectorPreviewRenderer.-get0(MotionVectorPreviewRenderer.this));
          HandlerUtils.sendMessage(MotionVectorPreviewRenderer.this, 10005, ((Integer)paramCamera2.get(Camera.PROP_MOTION_STATE)).intValue(), 0, null);
        }
      });
    }
  }
  
  private void onDebugModeChanged()
  {
    final boolean bool = ((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_DEBUG_MODE)).booleanValue();
    Log.v(this.TAG, "onDebugModeChanged() - Debug: ", Boolean.valueOf(bool));
    final Camera localCamera = getCamera();
    if (localCamera == null) {
      return;
    }
    HandlerUtils.post(localCamera, new Runnable()
    {
      public void run()
      {
        if (bool)
        {
          localCamera.addHandler(Camera.EVENT_MOTION_VECTOR_RECEIVED, MotionVectorPreviewRenderer.-get1(MotionVectorPreviewRenderer.this));
          localCamera.addCallback(Camera.PROP_MOTION_STATE, MotionVectorPreviewRenderer.-get0(MotionVectorPreviewRenderer.this));
          HandlerUtils.sendMessage(MotionVectorPreviewRenderer.this, 10005, ((Integer)localCamera.get(Camera.PROP_MOTION_STATE)).intValue(), 0, null);
          return;
        }
        localCamera.removeHandler(Camera.EVENT_MOTION_VECTOR_RECEIVED, MotionVectorPreviewRenderer.-get1(MotionVectorPreviewRenderer.this));
        localCamera.removeCallback(Camera.PROP_MOTION_STATE, MotionVectorPreviewRenderer.-get0(MotionVectorPreviewRenderer.this));
      }
    });
    updateRenderer();
  }
  
  private void onPreviewRenderer(Canvas paramCanvas)
  {
    Object localObject1;
    Object localObject2;
    if (Handle.isValid(this.m_MotionStateHandle))
    {
      localObject1 = getMotionStateText(this.m_MotionStateHandle.motionState);
      if (this.m_Paint == null)
      {
        this.m_Paint = new Paint();
        this.m_Paint.setAntiAlias(true);
        this.m_Paint.setStrokeWidth(4.0F);
        this.m_Paint.setTextSize(48.0F);
      }
      localObject2 = new Rect();
      this.m_Paint.setColor(-65536);
      this.m_Paint.getTextBounds((String)localObject1, 0, ((String)localObject1).length(), (Rect)localObject2);
      paramCanvas.drawText((String)localObject1, 20.0F, ((Rect)localObject2).height() + 180, this.m_Paint);
      this.m_MotionStateHandle = ((MotionStateHandle)Handle.close(this.m_MotionStateHandle));
    }
    if (Handle.isValid(this.m_MotionVectorsHandle))
    {
      localObject1 = this.m_MotionVectorsHandle.vectors;
      if (localObject1 == null) {
        return;
      }
      if (this.m_Viewfinder == null) {
        return;
      }
      if (this.m_Paint == null)
      {
        this.m_Paint = new Paint();
        this.m_Paint.setAntiAlias(true);
        this.m_Paint.setStrokeWidth(4.0F);
        this.m_Paint.setStyle(Paint.Style.STROKE);
      }
      int k = localObject1.length;
      if (k <= 0) {
        return;
      }
      int m = localObject1[0].length;
      if (m <= 0) {
        return;
      }
      localObject2 = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
      float f1 = ((RectF)localObject2).width() / m;
      float f2 = ((RectF)localObject2).height() / k;
      if ((f1 <= 0.0F) || (f2 <= 0.0F)) {
        return;
      }
      paramCanvas.save();
      paramCanvas.translate(((RectF)localObject2).left, ((RectF)localObject2).top);
      int i = 0;
      while (i < k)
      {
        int j = 0;
        while (j < m)
        {
          localObject2 = localObject1[i][(m - 1 - j)];
          float f3 = (j * f1 + (j + 1) * f1) / 2.0F;
          float f4 = (i * f2 + (i + 1) * f2) / 2.0F;
          this.m_Paint.setColor(-16777216);
          paramCanvas.drawLine(f3, f4, f3 - ((PointF)localObject2).y, f4 + ((PointF)localObject2).x, this.m_Paint);
          j += 1;
        }
        i += 1;
      }
      paramCanvas.restore();
      this.m_MotionVectorsHandle = ((MotionVectorHandle)Handle.close(this.m_MotionVectorsHandle));
    }
  }
  
  private void updateMotionState(MotionStateHandle paramMotionStateHandle)
  {
    this.m_MotionStateHandle = paramMotionStateHandle;
    if (Handle.isValid(this.m_PreviewRendererHandle)) {
      this.m_CameraPreviewOverlay.invalidateCameraPreviewOverlay();
    }
  }
  
  private void updateMotionVectors(MotionVectorHandle paramMotionVectorHandle) {}
  
  private void updateRenderer()
  {
    if (this.m_CameraPreviewOverlay == null) {
      return;
    }
    if (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_DEBUG_MODE)).booleanValue()) {}
    for (this.m_PreviewRendererHandle = this.m_CameraPreviewOverlay.addRenderer(this.m_Renderer, 0);; this.m_PreviewRendererHandle = Handle.close(this.m_PreviewRendererHandle))
    {
      this.m_CameraPreviewOverlay.invalidateCameraPreviewOverlay();
      return;
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return;
    case 10005: 
      updateMotionState(new MotionStateHandle(paramMessage.arg1));
      return;
    }
    updateMotionVectors((MotionVectorHandle)paramMessage.obj);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    findComponent(Viewfinder.class, new ComponentSearchCallback()
    {
      public void onComponentFound(Viewfinder paramAnonymousViewfinder)
      {
        MotionVectorPreviewRenderer.-set1(MotionVectorPreviewRenderer.this, paramAnonymousViewfinder);
      }
    });
    findComponent(CameraPreviewOverlay.class, new ComponentSearchCallback()
    {
      public void onComponentFound(CameraPreviewOverlay paramAnonymousCameraPreviewOverlay)
      {
        MotionVectorPreviewRenderer.-set0(MotionVectorPreviewRenderer.this, paramAnonymousCameraPreviewOverlay);
        MotionVectorPreviewRenderer.-wrap3(MotionVectorPreviewRenderer.this);
      }
    });
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addCallback(CameraActivity.PROP_IS_DEBUG_MODE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        MotionVectorPreviewRenderer.-wrap1(MotionVectorPreviewRenderer.this);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        MotionVectorPreviewRenderer.-wrap0(MotionVectorPreviewRenderer.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    if (((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_DEBUG_MODE)).booleanValue()) {
      onDebugModeChanged();
    }
  }
  
  private class MotionStateHandle
    extends Handle
  {
    public int motionState;
    
    MotionStateHandle(int paramInt)
    {
      super();
      this.motionState = paramInt;
    }
    
    protected void onClose(int paramInt) {}
  }
  
  private class MotionVectorHandle
    extends Handle
  {
    private long startTime;
    public PointF[][] vectors;
    
    MotionVectorHandle(PointF[][] paramArrayOfPointF)
    {
      super();
      this.vectors = paramArrayOfPointF;
      this.startTime = SystemClock.elapsedRealtime();
    }
    
    protected void onClose(int paramInt) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/MotionVectorPreviewRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */