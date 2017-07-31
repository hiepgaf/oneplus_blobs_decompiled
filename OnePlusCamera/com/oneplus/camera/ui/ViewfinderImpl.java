package com.oneplus.camera.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.UIComponent;
import java.util.LinkedList;

final class ViewfinderImpl
  extends UIComponent
  implements Viewfinder, CameraPreviewOverlay
{
  private static final int MSG_RECREATE_DIRECT_OUTPUT_SURFACE = 10000;
  private static final int PREVIEW_BOTTOM_1x1 = 1230;
  private static final boolean USE_TEXTURE_VIEW = false;
  private Surface m_DirectOutputSurface;
  private int m_DirectOutputSurfaceFormat;
  private SurfaceHolder m_DirectOutputSurfaceHolder;
  private Size m_DirectOutputSurfaceSize;
  private SurfaceTexture m_DirectOutputSurfaceTexture;
  private SurfaceView m_DirectOutputSurfaceView;
  private TextureView m_DirectOutputTextureView;
  private boolean m_IsDirectOutputSurfaceCreated;
  private final LinkedList<OverlayRendererHandle> m_OverlayRendererHandles = new LinkedList();
  private View m_OverlayView;
  private RectF m_PreferredBounds;
  private Viewfinder.PreviewRenderingMode m_PreviewRenderingMode = Viewfinder.PreviewRenderingMode.DIRECT;
  private Size m_ScreenSize = new Size(0, 0);
  
  ViewfinderImpl(CameraActivity paramCameraActivity)
  {
    super("Viewfinder", paramCameraActivity, true);
    enablePropertyLogs(PROP_PREVIEW_RECEIVER, 1);
    enablePropertyLogs(PROP_PREVIEW_BOUNDS, 1);
    enablePropertyLogs(PROP_PREVIEW_CONTAINER_SIZE, 1);
  }
  
  private void calculatePreviewBounds(Size paramSize1, Rotation paramRotation, Size paramSize2, boolean paramBoolean, RectF paramRectF)
  {
    Size localSize = paramSize2;
    if (paramRotation.isPortrait()) {
      localSize = new Size(paramSize2.getHeight(), paramSize2.getWidth());
    }
    float f1 = Math.min(paramSize1.getWidth() / localSize.getWidth(), paramSize1.getHeight() / localSize.getHeight());
    int i = (int)(localSize.getWidth() * f1 + 0.5F);
    int j = (int)(localSize.getHeight() * f1 + 0.5F);
    float f2 = 0.0F;
    float f4 = 0.0F;
    float f3 = 0.0F;
    float f5 = 0.0F;
    if (paramBoolean)
    {
      if (paramRotation.isLandscape())
      {
        int k = paramSize1.getHeight() * 2 / 3;
        paramRectF.left = 0.0F;
        paramRectF.top = ((paramSize1.getHeight() - j) / 2);
        paramRectF.right = (paramRectF.left + i);
        paramRectF.bottom = (paramRectF.top + j);
        f1 = f5;
        if (this.m_PreferredBounds != null)
        {
          f2 = paramRectF.left - this.m_PreferredBounds.top;
          f4 = paramRectF.top - this.m_PreferredBounds.right;
          f3 = paramRectF.right - this.m_PreferredBounds.bottom;
          f1 = paramRectF.bottom - this.m_PreferredBounds.left;
        }
        if (this.m_PreferredBounds != null)
        {
          if ((f2 > 0.0F) && (f3 > 0.0F) && (f3 <= f2)) {
            paramRectF.offset(-f3, 0.0F);
          }
          if ((f4 > 0.0F) && (f1 > 0.0F) && (f1 <= f4)) {
            paramRectF.offset(0.0F, -f1);
          }
        }
        return;
      }
      if (i == j)
      {
        paramRectF.bottom = 1230.0F;
        paramRectF.top = (paramRectF.bottom - j);
        label341:
        if (this.m_PreferredBounds == null) {
          break label490;
        }
      }
      label490:
      for (f1 = this.m_PreferredBounds.centerY();; f1 = paramSize1.getWidth() * 2 / 3)
      {
        paramRectF.left = ((paramSize1.getWidth() - i) / 2);
        f1 = j / 2;
        paramRectF.right = (paramRectF.left + i);
        f1 = f5;
        if (this.m_PreferredBounds == null) {
          break;
        }
        f2 = paramRectF.left - this.m_PreferredBounds.left;
        f4 = paramRectF.top - this.m_PreferredBounds.top;
        f3 = paramRectF.right - this.m_PreferredBounds.right;
        f1 = paramRectF.bottom - this.m_PreferredBounds.bottom;
        break;
        paramRectF.top = 0.0F;
        paramRectF.bottom = (paramRectF.top + j);
        break label341;
      }
    }
    paramRectF.left = ((paramSize1.getWidth() - i) / 2);
    paramRectF.top = ((paramSize1.getHeight() - j) / 2);
    paramRectF.right = (paramRectF.left + i);
    paramRectF.bottom = (paramRectF.top + j);
  }
  
  private void createDirectOutputSurfaceView(View paramView)
  {
    boolean bool = ((ViewGroup)paramView).getChildAt(0) instanceof SurfaceView;
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
    if (getCameraActivityRotation().isLandscape())
    {
      localLayoutParams.addRule(9);
      localLayoutParams.addRule(15);
    }
    for (;;)
    {
      this.m_DirectOutputSurfaceView = new SurfaceView(getCameraActivity());
      this.m_DirectOutputSurfaceView.setVisibility(4);
      this.m_DirectOutputSurfaceHolder = this.m_DirectOutputSurfaceView.getHolder();
      this.m_DirectOutputSurfaceHolder.addCallback(new SurfaceHolder.Callback()
      {
        public void surfaceChanged(SurfaceHolder paramAnonymousSurfaceHolder, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          ViewfinderImpl.-wrap3(ViewfinderImpl.this, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
        }
        
        public void surfaceCreated(SurfaceHolder paramAnonymousSurfaceHolder)
        {
          ViewfinderImpl.-wrap4(ViewfinderImpl.this);
        }
        
        public void surfaceDestroyed(SurfaceHolder paramAnonymousSurfaceHolder)
        {
          ViewfinderImpl.-wrap5(ViewfinderImpl.this);
        }
      });
      if (!bool) {
        break;
      }
      this.m_DirectOutputSurfaceView.setZOrderMediaOverlay(true);
      ((ViewGroup)paramView).addView(this.m_DirectOutputSurfaceView, 1, localLayoutParams);
      return;
      localLayoutParams.addRule(10);
      localLayoutParams.addRule(14);
    }
    ((ViewGroup)paramView).addView(this.m_DirectOutputSurfaceView, 0, localLayoutParams);
  }
  
  private void initializeUI(View paramView)
  {
    if (!(paramView instanceof RelativeLayout)) {
      throw new RuntimeException("Activity root layout must be RelativeLayout.");
    }
    boolean bool = ((ViewGroup)paramView).getChildAt(0) instanceof SurfaceView;
    createDirectOutputSurfaceView(paramView);
    this.m_OverlayView = new View(getCameraActivity())
    {
      protected void onDraw(Canvas paramAnonymousCanvas)
      {
        ViewfinderImpl.-wrap6(ViewfinderImpl.this, paramAnonymousCanvas);
      }
    };
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-1, -1);
    paramView = (ViewGroup)paramView;
    View localView = this.m_OverlayView;
    if (bool) {}
    for (int i = 2;; i = 1)
    {
      paramView.addView(localView, i, localLayoutParams);
      updatePreviewBounds();
      return;
    }
  }
  
  private void onDirectOutputSurfaceChanged(int paramInt1, int paramInt2, int paramInt3)
  {
    Log.w(this.TAG, "onDirectOutputSurfaceChanged() - Format : " + paramInt1 + ", size : " + paramInt2 + "x" + paramInt3);
    this.m_DirectOutputSurfaceFormat = paramInt1;
    this.m_DirectOutputSurfaceSize = new Size(paramInt2, paramInt3);
    switch (-getcom-oneplus-camera-ui-Viewfinder$PreviewRenderingModeSwitchesValues()[this.m_PreviewRenderingMode.ordinal()])
    {
    case 2: 
    default: 
      return;
    }
    Size localSize = (Size)getCameraActivity().get(CameraActivity.PROP_CAMERA_PREVIEW_SIZE);
    Camera localCamera = getCamera();
    if (localCamera != null) {}
    for (boolean bool = ((Boolean)localCamera.get(Camera.PROP_IS_FIXED_SIZE_PREVIEW_RECEIVER_NEEDED)).booleanValue(); (!bool) || (this.m_DirectOutputSurfaceSize.equals(localSize)); bool = true)
    {
      updatePreviewReceiverState();
      return;
    }
    updateDirectOutputSurfaceSize(localSize);
  }
  
  private void onDirectOutputSurfaceCreated()
  {
    Log.w(this.TAG, "onDirectOutputSurfaceCreated()");
    this.m_IsDirectOutputSurfaceCreated = true;
    updateDirectOutputSurfaceSize((Size)getCameraActivity().get(CameraActivity.PROP_CAMERA_PREVIEW_SIZE));
  }
  
  private void onDirectOutputSurfaceDestroyed()
  {
    Log.w(this.TAG, "onDirectOutputSurfaceDestroyed()");
    this.m_IsDirectOutputSurfaceCreated = false;
    setReadOnly(PROP_PREVIEW_RECEIVER, null);
  }
  
  private void onDirectOutputSurfaceTextureChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    Log.w(this.TAG, "onDirectOutputSurfaceTextureChanged() - Size : " + paramInt1 + "x" + paramInt2);
    this.m_DirectOutputSurfaceSize = new Size(paramInt1, paramInt2);
    Surface localSurface = this.m_DirectOutputSurface;
    this.m_DirectOutputSurface = new Surface(paramSurfaceTexture);
    switch (-getcom-oneplus-camera-ui-Viewfinder$PreviewRenderingModeSwitchesValues()[this.m_PreviewRenderingMode.ordinal()])
    {
    }
    for (;;)
    {
      if (localSurface != null) {
        localSurface.release();
      }
      return;
      paramSurfaceTexture = (Size)getCameraActivity().get(CameraActivity.PROP_CAMERA_PREVIEW_SIZE);
      Camera localCamera = getCamera();
      if (localCamera != null) {}
      for (boolean bool = ((Boolean)localCamera.get(Camera.PROP_IS_FIXED_SIZE_PREVIEW_RECEIVER_NEEDED)).booleanValue();; bool = true)
      {
        if ((bool) && (!this.m_DirectOutputSurfaceSize.equals(paramSurfaceTexture))) {
          break label183;
        }
        updatePreviewReceiverState();
        break;
      }
      label183:
      updateDirectOutputSurfaceSize(paramSurfaceTexture);
    }
  }
  
  private void onDirectOutputSurfaceTextureCreated(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    Log.w(this.TAG, "onDirectOutputSurfaceTextureCreated()");
    this.m_DirectOutputSurfaceTexture = paramSurfaceTexture;
    this.m_DirectOutputSurfaceSize = new Size(paramInt1, paramInt2);
    this.m_IsDirectOutputSurfaceCreated = true;
    if (((Integer)getCameraActivity().get(CameraActivity.PROP_CONFIG_ORIENTATION)).intValue() == 1) {}
    for (int i = 1; i == getCameraActivityRotation().isPortrait(); i = 0)
    {
      updateDirectOutputSurfaceSize((Size)getCameraActivity().get(CameraActivity.PROP_CAMERA_PREVIEW_SIZE));
      return;
    }
    Log.w(this.TAG, "onDirectOutputSurfaceTextureCreated() - Configuration orientation is incorrect, update surface txture later");
  }
  
  private void onDirectOutputSurfaceTextureDestroyed()
  {
    Log.w(this.TAG, "onDirectOutputSurfaceTextureDestroyed()");
    this.m_DirectOutputSurfaceTexture = null;
    this.m_IsDirectOutputSurfaceCreated = false;
    setReadOnly(PROP_PREVIEW_RECEIVER, null);
    if (this.m_DirectOutputSurface != null)
    {
      this.m_DirectOutputSurface.release();
      this.m_DirectOutputSurface = null;
    }
  }
  
  private void onDrawOverlay(Canvas paramCanvas)
  {
    if (!this.m_OverlayRendererHandles.isEmpty())
    {
      CameraPreviewOverlay.RenderingParams localRenderingParams = CameraPreviewOverlay.RenderingParams.obtain((RectF)get(PROP_PREVIEW_BOUNDS));
      int i = this.m_OverlayRendererHandles.size() - 1;
      while (i >= 0)
      {
        ((OverlayRendererHandle)this.m_OverlayRendererHandles.get(i)).renderer.onRender(paramCanvas, localRenderingParams);
        i -= 1;
      }
      localRenderingParams.recycle();
    }
  }
  
  private void onPreviewSizeChanged(Size paramSize)
  {
    setReadOnly(PROP_PREVIEW_RECEIVER, null);
    if ((this.m_DirectOutputSurfaceView != null) && (this.m_DirectOutputSurfaceView.getVisibility() == 4))
    {
      Log.v(this.TAG, "onPreviewSizeChanged() - Change surface view to visible");
      this.m_DirectOutputSurfaceView.setVisibility(0);
    }
    updateDirectOutputSurfaceSize(paramSize);
    updatePreviewBounds(paramSize);
  }
  
  private void onScreenSizeChanged(ScreenSize paramScreenSize, boolean paramBoolean)
  {
    if (!paramBoolean) {
      Log.w(this.TAG, "onScreenSizeChanged() - Changed to " + paramScreenSize);
    }
    this.m_ScreenSize = paramScreenSize.toSize();
    if (getCameraActivityRotation().isLandscape()) {}
    for (paramScreenSize = this.m_ScreenSize;; paramScreenSize = new Size(this.m_ScreenSize.getHeight(), this.m_ScreenSize.getWidth()))
    {
      setReadOnly(PROP_PREVIEW_CONTAINER_SIZE, paramScreenSize);
      updatePreviewBounds();
      return;
    }
  }
  
  private void recreateDirectOutputSurface()
  {
    if (this.m_DirectOutputTextureView != null)
    {
      Log.v(this.TAG, "recreateDirectOutputSurface()");
      this.m_DirectOutputTextureView.setAlpha(0.0F);
    }
    while (this.m_DirectOutputSurfaceView == null) {
      return;
    }
    Log.v(this.TAG, "recreateDirectOutputSurface()");
    this.m_DirectOutputSurfaceView.setVisibility(8);
    ((ViewGroup)getCameraActivity().get(CameraActivity.PROP_CONTENT_VIEW)).removeView(this.m_DirectOutputSurfaceView);
    this.m_DirectOutputSurfaceView = null;
  }
  
  private void removeRenderer(OverlayRendererHandle paramOverlayRendererHandle)
  {
    verifyAccess();
    if (!this.m_OverlayRendererHandles.remove(paramOverlayRendererHandle)) {
      return;
    }
    if (this.m_OverlayView != null) {
      this.m_OverlayView.invalidate();
    }
  }
  
  private void updateDirectOutputSurfaceSize(Size paramSize)
  {
    Camera localCamera;
    if ((paramSize.getWidth() > 0) && (paramSize.getHeight() > 0))
    {
      localCamera = getCamera();
      if (localCamera == null) {
        break label106;
      }
    }
    label106:
    for (boolean bool = ((Boolean)localCamera.get(Camera.PROP_IS_FIXED_SIZE_PREVIEW_RECEIVER_NEEDED)).booleanValue();; bool = true)
    {
      if ((bool) && (this.m_DirectOutputSurfaceHolder != null)) {
        this.m_DirectOutputSurfaceHolder.setFixedSize(paramSize.getWidth(), paramSize.getHeight());
      }
      if ((this.m_DirectOutputSurfaceSize != null) && ((!bool) || (this.m_DirectOutputSurfaceSize.equals(paramSize)))) {
        onDirectOutputSurfaceChanged(this.m_DirectOutputSurfaceFormat, paramSize.getWidth(), paramSize.getHeight());
      }
      return;
    }
  }
  
  private void updateDirectOutputSurfaceTrxtureTransform(RectF paramRectF)
  {
    Matrix localMatrix = new Matrix();
    localMatrix.postScale(paramRectF.width() / this.m_ScreenSize.getWidth(), paramRectF.height() / this.m_ScreenSize.getHeight());
    localMatrix.postTranslate(paramRectF.left, paramRectF.top);
    this.m_DirectOutputTextureView.setTransform(localMatrix);
  }
  
  private void updatePreviewBounds()
  {
    updatePreviewBounds((Size)getCameraActivity().get(CameraActivity.PROP_CAMERA_PREVIEW_SIZE));
  }
  
  private void updatePreviewBounds(Size paramSize)
  {
    if ((this.m_ScreenSize.getWidth() <= 0) || (this.m_ScreenSize.getHeight() <= 0)) {}
    while ((paramSize.getWidth() <= 0) || (paramSize.getHeight() <= 0)) {
      return;
    }
    RectF localRectF = new RectF();
    calculatePreviewBounds(this.m_ScreenSize, getCameraActivityRotation(), paramSize, true, localRectF);
    int j;
    if (this.m_DirectOutputTextureView != null)
    {
      updateDirectOutputSurfaceTrxtureTransform(localRectF);
      setReadOnly(PROP_PREVIEW_BOUNDS, new RectF(localRectF));
      if (this.m_OverlayView != null) {
        this.m_OverlayView.invalidate();
      }
      if ((this.m_PreviewRenderingMode == Viewfinder.PreviewRenderingMode.DIRECT) && (this.m_IsDirectOutputSurfaceCreated) && (this.m_DirectOutputSurfaceSize != null))
      {
        if (!getCameraActivityRotation().isLandscape()) {
          break label276;
        }
        j = paramSize.getWidth();
      }
    }
    for (int i = paramSize.getHeight();; i = paramSize.getWidth())
    {
      if ((this.m_DirectOutputSurfaceSize.getWidth() == j) && (this.m_DirectOutputSurfaceSize.getHeight() == i))
      {
        Log.v(this.TAG, "updatePreviewBounds() - Target size is same as current surface size");
        onDirectOutputSurfaceChanged(this.m_DirectOutputSurfaceFormat, this.m_DirectOutputSurfaceSize.getWidth(), this.m_DirectOutputSurfaceSize.getHeight());
      }
      return;
      if (this.m_DirectOutputSurfaceView == null) {
        break;
      }
      ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.m_DirectOutputSurfaceView.getLayoutParams();
      localMarginLayoutParams.width = ((int)localRectF.width());
      localMarginLayoutParams.height = ((int)localRectF.height());
      localMarginLayoutParams.topMargin = ((int)localRectF.top);
      localMarginLayoutParams.leftMargin = ((int)localRectF.left);
      this.m_DirectOutputSurfaceView.requestLayout();
      break;
      label276:
      j = paramSize.getHeight();
    }
  }
  
  private void updatePreviewReceiverState()
  {
    int i = 1;
    CameraActivity localCameraActivity = getCameraActivity();
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)localCameraActivity.get(CameraActivity.PROP_STATE)).ordinal()])
    {
    default: 
      i = 0;
    }
    Object localObject = null;
    Size localSize = (Size)getCameraActivity().get(CameraActivity.PROP_CAMERA_PREVIEW_SIZE);
    int j;
    switch (-getcom-oneplus-camera-ui-Viewfinder$PreviewRenderingModeSwitchesValues()[this.m_PreviewRenderingMode.ordinal()])
    {
    default: 
      j = i;
      if (i != 0)
      {
        if (!getCameraActivityRotation().isLandscape()) {
          break label224;
        }
        j = i;
        if (((Integer)localCameraActivity.get(CameraActivity.PROP_CONFIG_ORIENTATION)).intValue() != 2) {
          j = 0;
        }
      }
      break;
    }
    for (;;)
    {
      if (j == 0) {
        break label249;
      }
      setReadOnly(PROP_PREVIEW_RECEIVER, localObject);
      return;
      if (!this.m_IsDirectOutputSurfaceCreated) {
        j = 0;
      }
      for (;;)
      {
        localObject = this.m_DirectOutputSurfaceHolder;
        i = j;
        break;
        localObject = getCamera();
        if (localObject != null)
        {
          j = i;
          if (!((Boolean)((Camera)localObject).get(Camera.PROP_IS_FIXED_SIZE_PREVIEW_RECEIVER_NEEDED)).booleanValue()) {}
        }
        else
        {
          j = i;
          if (!this.m_DirectOutputSurfaceSize.equals(localSize)) {
            j = 0;
          }
        }
      }
      localObject = null;
      break;
      label224:
      j = i;
      if (((Integer)localCameraActivity.get(CameraActivity.PROP_CONFIG_ORIENTATION)).intValue() != 1) {
        j = 0;
      }
    }
    label249:
    setReadOnly(PROP_PREVIEW_RECEIVER, null);
  }
  
  public Handle addRenderer(CameraPreviewOverlay.Renderer paramRenderer, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "addRenderer() - Component is not running");
      return null;
    }
    if (paramRenderer == null)
    {
      Log.e(this.TAG, "addRenderer() - No renderer to add");
      return null;
    }
    paramRenderer = new OverlayRendererHandle(paramRenderer);
    this.m_OverlayRendererHandles.add(paramRenderer);
    if (this.m_OverlayView != null) {
      this.m_OverlayView.invalidate();
    }
    return paramRenderer;
  }
  
  public boolean copyDisplayPreviewFrame(Bitmap paramBitmap, int paramInt)
  {
    verifyAccess();
    if (this.m_DirectOutputTextureView == null)
    {
      Log.e(this.TAG, "copyPreviewFrame() - No direct output TextureView");
      return false;
    }
    if (paramBitmap == null)
    {
      Log.e(this.TAG, "copyPreviewFrame() - No bitmap to receive preview frame");
      return false;
    }
    this.m_DirectOutputTextureView.getBitmap(paramBitmap);
    return true;
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_IS_DISPLAY_PREVIEW_FRAME_COPY_SUPPORTED) {
      return Boolean.valueOf(false);
    }
    if (paramPropertyKey == PROP_PREVIEW_RENDERING_MODE) {
      return this.m_PreviewRenderingMode;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    recreateDirectOutputSurface();
  }
  
  public void invalidateCameraPreviewOverlay()
  {
    verifyAccess();
    if (this.m_OverlayView != null) {
      this.m_OverlayView.invalidate();
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    final CameraActivity localCameraActivity = getCameraActivity();
    View localView = (View)localCameraActivity.get(CameraActivity.PROP_CONTENT_VIEW);
    if (localView != null) {
      initializeUI(localView);
    }
    for (;;)
    {
      localCameraActivity.addCallback(CameraActivity.PROP_SCREEN_SIZE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<ScreenSize> paramAnonymousPropertyKey, PropertyChangeEventArgs<ScreenSize> paramAnonymousPropertyChangeEventArgs)
        {
          ViewfinderImpl.-wrap8(ViewfinderImpl.this, (ScreenSize)paramAnonymousPropertyChangeEventArgs.getNewValue(), false);
        }
      });
      onScreenSizeChanged((ScreenSize)localCameraActivity.get(CameraActivity.PROP_SCREEN_SIZE), true);
      localCameraActivity.addCallback(CameraActivity.PROP_CAMERA_PREVIEW_SIZE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Size> paramAnonymousPropertyKey, PropertyChangeEventArgs<Size> paramAnonymousPropertyChangeEventArgs)
        {
          ViewfinderImpl.-wrap7(ViewfinderImpl.this, (Size)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
      localCameraActivity.addCallback(CameraActivity.PROP_CONFIG_ORIENTATION, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Integer> paramAnonymousPropertyKey, PropertyChangeEventArgs<Integer> paramAnonymousPropertyChangeEventArgs)
        {
          ViewfinderImpl.-wrap11(ViewfinderImpl.this);
        }
      });
      localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
        {
          switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
          {
          }
          do
          {
            do
            {
              do
              {
                return;
              } while (ViewfinderImpl.-get2(ViewfinderImpl.this) != null);
              paramAnonymousPropertySource = (View)localCameraActivity.get(CameraActivity.PROP_CONTENT_VIEW);
              if (paramAnonymousPropertySource != null) {
                ViewfinderImpl.-wrap1(ViewfinderImpl.this, paramAnonymousPropertySource);
              }
              ViewfinderImpl.-wrap10(ViewfinderImpl.this);
            } while ((ViewfinderImpl.-get4(ViewfinderImpl.this) == null) || (ViewfinderImpl.-get4(ViewfinderImpl.this).getVisibility() == 0));
            ViewfinderImpl.-get4(ViewfinderImpl.this).setVisibility(0);
            return;
            if ((ViewfinderImpl.-get2(ViewfinderImpl.this) != null) && (ViewfinderImpl.-get2(ViewfinderImpl.this).getVisibility() == 0)) {
              ViewfinderImpl.this.getHandler().sendMessageAtFrontOfQueue(Message.obtain(ViewfinderImpl.this.getHandler(), 10000));
            }
            if ((ViewfinderImpl.-get4(ViewfinderImpl.this) != null) && (ViewfinderImpl.-get4(ViewfinderImpl.this).getVisibility() == 0)) {
              ViewfinderImpl.-get4(ViewfinderImpl.this).setVisibility(4);
            }
            ViewfinderImpl.this.getHandler().postAtFrontOfQueue(new Runnable()
            {
              public void run()
              {
                if (!((Boolean)this.val$cameraActivity.get(CameraActivity.PROP_IS_RUNNING)).booleanValue()) {
                  ViewfinderImpl.-wrap0(ViewfinderImpl.this, ViewfinderImpl.PROP_PREVIEW_RECEIVER, null);
                }
              }
            });
            return;
            ViewfinderImpl.this.getHandler().removeMessages(10000);
            if (ViewfinderImpl.-get2(ViewfinderImpl.this) == null)
            {
              paramAnonymousPropertySource = (View)localCameraActivity.get(CameraActivity.PROP_CONTENT_VIEW);
              if (paramAnonymousPropertySource != null) {
                ViewfinderImpl.-wrap1(ViewfinderImpl.this, paramAnonymousPropertySource);
              }
              ViewfinderImpl.-wrap10(ViewfinderImpl.this);
            }
            if ((ViewfinderImpl.-get2(ViewfinderImpl.this) != null) && (ViewfinderImpl.-get2(ViewfinderImpl.this).getVisibility() != 0)) {
              ViewfinderImpl.-get2(ViewfinderImpl.this).setVisibility(0);
            }
            if ((ViewfinderImpl.-get4(ViewfinderImpl.this) != null) && (ViewfinderImpl.-get4(ViewfinderImpl.this).getVisibility() != 0)) {
              ViewfinderImpl.-get4(ViewfinderImpl.this).setVisibility(0);
            }
            switch (-getcom-oneplus-camera-ui-Viewfinder$PreviewRenderingModeSwitchesValues()[ViewfinderImpl.-get5(ViewfinderImpl.this).ordinal()])
            {
            case 2: 
            default: 
              return;
            }
          } while ((!ViewfinderImpl.-get3(ViewfinderImpl.this)) || (ViewfinderImpl.this.get(ViewfinderImpl.PROP_PREVIEW_RECEIVER) != null));
          ViewfinderImpl.-wrap3(ViewfinderImpl.this, ViewfinderImpl.-get0(ViewfinderImpl.this), ViewfinderImpl.-get1(ViewfinderImpl.this).getWidth(), ViewfinderImpl.-get1(ViewfinderImpl.this).getHeight());
        }
      });
      return;
      localCameraActivity.addCallback(CameraActivity.PROP_CONTENT_VIEW, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<View> paramAnonymousPropertyKey, PropertyChangeEventArgs<View> paramAnonymousPropertyChangeEventArgs)
        {
          ViewfinderImpl.-wrap2(ViewfinderImpl.this, (View)paramAnonymousPropertyChangeEventArgs.getNewValue());
          paramAnonymousPropertySource.removeCallback(CameraActivity.PROP_CONTENT_VIEW, this);
        }
      });
    }
  }
  
  public boolean pointFromPreview(float paramFloat1, float paramFloat2, PointF paramPointF, int paramInt)
  {
    if (paramPointF == null) {
      return false;
    }
    if ((paramInt & 0x1) == 0)
    {
      if ((paramFloat1 < 0.0F) || (paramFloat1 > 1.0F)) {}
      while ((paramFloat2 < 0.0F) || (paramFloat2 > 1.0F)) {
        return false;
      }
    }
    RectF localRectF = (RectF)get(PROP_PREVIEW_BOUNDS);
    float f2;
    float f1;
    if (getCameraActivityRotation().isLandscape())
    {
      f2 = localRectF.width();
      f1 = localRectF.height();
      paramFloat1 = f2 * paramFloat1;
      paramFloat2 = f1 * paramFloat2;
      switch (-getcom-oneplus-base-RotationSwitchesValues()[getCameraActivityRotation().ordinal()])
      {
      }
    }
    for (;;)
    {
      paramPointF.x = (localRectF.left + paramFloat1);
      paramPointF.y = (localRectF.top + paramFloat2);
      return true;
      f2 = localRectF.height();
      f1 = localRectF.width();
      break;
      f2 = paramFloat1;
      paramFloat1 = f1 - paramFloat2;
      paramFloat2 = f2;
      continue;
      f1 = f2 - paramFloat1;
      paramFloat1 = paramFloat2;
      paramFloat2 = f1;
      continue;
      paramFloat1 = f2 - paramFloat1;
      paramFloat2 = f1 - paramFloat2;
    }
  }
  
  public boolean pointToPreview(float paramFloat1, float paramFloat2, PointF paramPointF, int paramInt)
  {
    if (paramPointF == null) {
      return false;
    }
    RectF localRectF = (RectF)get(PROP_PREVIEW_BOUNDS);
    float f2;
    float f3;
    if (((paramInt & 0x1) != 0) || (localRectF.contains(paramFloat1, paramFloat2)))
    {
      f2 = localRectF.width();
      f3 = localRectF.height();
      paramFloat1 -= localRectF.left;
      paramFloat2 -= localRectF.top;
      switch (-getcom-oneplus-base-RotationSwitchesValues()[getCameraActivityRotation().ordinal()])
      {
      default: 
        if (getCameraActivityRotation().isLandscape()) {
          paramPointF.x = (paramFloat1 / f2);
        }
        break;
      }
    }
    for (paramPointF.y = (paramFloat2 / f3);; paramPointF.y = (paramFloat2 / f2))
    {
      return true;
      return false;
      float f1 = f2 - paramFloat1;
      paramFloat1 = paramFloat2;
      paramFloat2 = f1;
      break;
      f1 = paramFloat1;
      paramFloat1 = f3 - paramFloat2;
      paramFloat2 = f1;
      break;
      paramFloat1 = f2 - paramFloat1;
      paramFloat2 = f3 - paramFloat2;
      break;
      paramPointF.x = (paramFloat1 / f3);
    }
  }
  
  public void setPreferredPreviewBounds(RectF paramRectF, int paramInt)
  {
    this.m_PreferredBounds = paramRectF;
  }
  
  private final class OverlayRendererHandle
    extends Handle
  {
    public final CameraPreviewOverlay.Renderer renderer;
    
    public OverlayRendererHandle(CameraPreviewOverlay.Renderer paramRenderer)
    {
      super();
      this.renderer = paramRenderer;
    }
    
    protected void onClose(int paramInt)
    {
      ViewfinderImpl.-wrap9(ViewfinderImpl.this, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ViewfinderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */