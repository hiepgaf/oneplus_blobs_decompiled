package com.oneplus.camera.panorama;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.widget.ImageView;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandleSet;
import com.oneplus.base.HandlerObject;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.Settings;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.HardwareLevel;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CaptureEventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.camera.FaceTracker;
import com.oneplus.camera.FaceTracker.FaceDetectionDisabledReason;
import com.oneplus.camera.FlashController;
import com.oneplus.camera.FlashController.FlashDisabledReason;
import com.oneplus.camera.ModeUI;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.ZoomController;
import com.oneplus.camera.media.DefaultPhotoResolutionSelector;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.Resolution;
import com.oneplus.camera.media.ResolutionManager;
import com.oneplus.camera.media.ResolutionSelector.Restriction;
import com.oneplus.camera.media.YuvToBitmapWorker;
import com.oneplus.camera.scene.Scene;
import com.oneplus.camera.scene.SceneManager;
import com.oneplus.camera.ui.CameraGallery;
import com.oneplus.camera.ui.CameraGallery.UIState;
import com.oneplus.camera.ui.CaptureButtonEventArgs;
import com.oneplus.camera.ui.CaptureButtons;
import com.oneplus.camera.ui.CaptureButtons.Button;
import com.oneplus.camera.ui.CaptureControlPanel;
import com.oneplus.camera.ui.CaptureControlPanel.Style;
import com.oneplus.camera.ui.OnScreenHint;
import com.oneplus.camera.ui.ProcessingDialog;
import com.oneplus.camera.watermark.WatermarkUI;
import com.oneplus.util.AspectRatio;
import com.oneplus.widget.ViewUtils;
import java.util.ArrayList;
import java.util.List;

public final class PanoramaUI
  extends ModeUI<PanoramaController>
{
  static final int ERROR_MESSAGE_TIMEOUT = 3000;
  private static final int MAX_PHOTO_SIDE = 4096;
  private static final int MAX_PREVIEW_SIDE = 1920;
  static final int MSG_CAPTURE_COMPLETED = 10004;
  static final int MSG_CAPTURE_STARTED = 10000;
  private static final int MSG_CLOSE_ERROR_HINT = 10023;
  static final int MSG_DISPLACEMENT_CHANGED = 10020;
  static final int MSG_FRAME_ADD_FAILED = 10002;
  static final int MSG_MOVING_SPEED_CHANGED = 10021;
  static final int MSG_PREVIEW_FRAME_RECEIVED = 10011;
  static final int MSG_RESULT_SIZE_CHANGED = 10001;
  static final int MSG_STITCHING = 10003;
  static final int MSG_YUV_TO_BMP_CONVERTER_CREATED = 10010;
  public static final PropertyKey<Boolean> PROP_IS_STOPPED_BY_USER = new PropertyKey("IsStoppedByUser", Boolean.class, PanoramaUI.class, Boolean.valueOf(false));
  public static final PropertyKey<Integer> PROP_LAST_FRAME_ADD_RESULT = new PropertyKey("LastFrameAddResult", Integer.class, PanoramaUI.class, Integer.valueOf(0));
  public static final PropertyKey<Float> PROP_PANORAMA_CAPTURE_LENGTH = new PropertyKey("PanoramaCaptureLength", Float.class, PanoramaUI.class, Float.valueOf(0.0F));
  private static final boolean USE_PHOTO_SIZE_FOR_PREVIEW = true;
  private View m_BaseView;
  private Paint m_BlackSolidPaint;
  private Handle m_CaptureButtonBgHandle;
  private CaptureButtons m_CaptureButtons;
  private CaptureControlPanel m_CaptureControlPanel;
  private Handle m_CaptureControlPanelStyleHandle;
  private CaptureHandle m_CaptureHandle;
  private Handle m_CaptureUIDisableHandle;
  private PropertyChangedCallback<Boolean> m_CaptureUIInflatedCallback;
  private View m_CurrentFrame;
  private int m_CurrentFrameThickness;
  private Drawable m_DirIndicatorDrawable;
  private Drawable m_DirIndicatorDrawableWarning;
  private ViewGroup m_DirIndicatorsContainer;
  private ViewPropertyAnimator m_DirIndicatorsContainerAnimator;
  private int m_Direction = 17;
  private int m_Displacement = 0;
  private Handle m_ErrorHintHandle;
  private Handle m_FaceDetectionHandle;
  private FaceTracker m_FaceTracker;
  private FlashController m_FlashController;
  private HandleSet m_Handles;
  private OnScreenHint m_Hint;
  private Handle m_HintHandle;
  private boolean m_IsControllerCaptureStarted;
  private boolean m_IsMovingTooFast;
  private int m_LastFrameAddFailedResult;
  private Bitmap m_PreviewFrameBitmap;
  private ImageView m_PreviewFrameView;
  private Bitmap m_PreviewThumbBitmap;
  private View m_PreviewThumbContainer;
  private int m_PreviewThumbHeight = 0;
  private ImageView m_PreviewThumbView;
  private ProcessingDialog m_ProcessingDialog;
  private Handle m_ProcessingDialogHandle;
  private ResolutionManager m_ResolutionManager;
  private ResolutionSelector m_ResolutionSelector;
  private Handle m_RotationLockHandle;
  private SceneManager m_SceneManager;
  private final SensorEventListener m_SensorEventListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      PanoramaUI.-wrap4(PanoramaUI.this, paramAnonymousSensorEvent);
    }
  };
  private SensorManager m_SensorManager;
  private int m_TargetHeight;
  private int m_TargetWidth;
  private WatermarkUI m_WatermarkUI;
  private YuvToBitmapWorker m_YuvToBitmapWorker;
  private ZoomController m_ZoomController;
  
  public PanoramaUI(CameraActivity paramCameraActivity)
  {
    super("Panorama UI", paramCameraActivity, PanoramaController.class);
  }
  
  private void completeCapture()
  {
    Log.v(this.TAG, "completeCapture()");
    this.m_ProcessingDialogHandle = Handle.close(this.m_ProcessingDialogHandle);
    CaptureHandle localCaptureHandle = this.m_CaptureHandle;
    this.m_CaptureHandle = null;
    this.m_PreviewThumbBitmap = null;
    this.m_IsControllerCaptureStarted = false;
    if (this.m_PreviewThumbView != null) {
      this.m_PreviewThumbView.setImageDrawable(null);
    }
    setViewVisibility(this.m_PreviewThumbContainer, true);
    setViewVisibility(this.m_PreviewFrameView, true);
    setViewVisibility(this.m_CurrentFrame, true);
    ViewUtils.setMargins(this.m_CurrentFrame, 0, 0, 0, 0);
    if (this.m_CaptureButtons != null)
    {
      Handle localHandle = this.m_CaptureButtons.setButtonBackground(CaptureButtons.Button.PRIMARY, getCameraActivity().getDrawable(2130837523), 0);
      Handle.close(this.m_CaptureButtonBgHandle);
      this.m_CaptureButtonBgHandle = localHandle;
    }
    if (this.m_DirIndicatorsContainer != null)
    {
      int i = this.m_DirIndicatorsContainer.getChildCount() - 1;
      while (i >= 0)
      {
        this.m_DirIndicatorsContainer.getChildAt(i).setBackground(this.m_DirIndicatorDrawable);
        i -= 1;
      }
      if (this.m_DirIndicatorsContainerAnimator != null) {
        this.m_DirIndicatorsContainerAnimator.cancel();
      }
      this.m_DirIndicatorsContainer.setRotation(0.0F);
    }
    this.m_CaptureUIDisableHandle = Handle.close(this.m_CaptureUIDisableHandle);
    if (this.m_SensorManager != null) {
      this.m_SensorManager.unregisterListener(this.m_SensorEventListener);
    }
    getCameraActivity().completeCapture(localCaptureHandle);
  }
  
  private void onCaptureButtonPressed(CaptureButtonEventArgs paramCaptureButtonEventArgs)
  {
    if (isEntered()) {
      paramCaptureButtonEventArgs.setHandled();
    }
  }
  
  private void onCaptureButtonReleased(CaptureButtonEventArgs paramCaptureButtonEventArgs)
  {
    if (!isEntered()) {
      return;
    }
    for (;;)
    {
      try
      {
        boolean bool = isCaptureUIEnabled();
        if (!bool) {
          return;
        }
        if (!Handle.isValid(this.m_CaptureHandle))
        {
          Log.v(this.TAG, "onCaptureButtonReleased() - Capture");
          this.m_CaptureHandle = getCameraActivity().capturePhoto();
          if (!Handle.isValid(this.m_CaptureHandle)) {
            Log.e(this.TAG, "onCaptureButtonReleased() - Fail to capture");
          }
          return;
        }
        if (this.m_IsControllerCaptureStarted)
        {
          setReadOnly(PROP_IS_STOPPED_BY_USER, Boolean.valueOf(true));
          stopCapture(true);
        }
        else
        {
          Log.w(this.TAG, "onCaptureButtonReleased() - Capture is not started in controller yet");
        }
      }
      finally
      {
        paramCaptureButtonEventArgs.setHandled();
      }
    }
  }
  
  private void onCaptureCompleted(int paramInt)
  {
    completeCapture();
  }
  
  private void onCaptureStarted(int paramInt1, int paramInt2)
  {
    if (getCameraActivityRotation().isPortrait())
    {
      this.m_TargetWidth = paramInt2;
      this.m_TargetHeight = paramInt1;
    }
    for (;;)
    {
      if (this.m_PreviewThumbHeight == 0) {
        this.m_PreviewThumbHeight = getCameraActivity().getResources().getDimensionPixelSize(2131296492);
      }
      Object localObject = getScreenSize();
      try
      {
        this.m_PreviewThumbBitmap = Bitmap.createBitmap(((ScreenSize)localObject).getWidth(), this.m_PreviewThumbHeight, Bitmap.Config.ARGB_8888);
        if (this.m_PreviewThumbView != null) {
          this.m_PreviewThumbView.setImageBitmap(this.m_PreviewThumbBitmap);
        }
        setViewVisibility(this.m_PreviewThumbContainer, true);
        setViewVisibility(this.m_CurrentFrame, true);
        if (this.m_SensorManager == null) {
          this.m_SensorManager = ((SensorManager)getCameraActivity().getSystemService("sensor"));
        }
        localObject = this.m_SensorManager.getDefaultSensor(4);
        if (localObject != null)
        {
          this.m_SensorManager.registerListener(this.m_SensorEventListener, (Sensor)localObject, 1);
          this.m_Displacement = 0;
          this.m_IsMovingTooFast = false;
          this.m_LastFrameAddFailedResult = -1;
          setReadOnly(PROP_LAST_FRAME_ADD_RESULT, Integer.valueOf(0));
          setReadOnly(PROP_IS_STOPPED_BY_USER, Boolean.valueOf(false));
          setReadOnly(PROP_PANORAMA_CAPTURE_LENGTH, Float.valueOf(0.0F));
          this.m_IsControllerCaptureStarted = true;
          HandlerUtils.removeMessages(this, 10023);
          Handle.close(this.m_ErrorHintHandle);
          updateHint();
          return;
          this.m_TargetWidth = paramInt1;
          this.m_TargetHeight = paramInt2;
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          Log.e(this.TAG, "onCaptureStarted() - Fail to create preview thumbnail bitmap", localThrowable);
          continue;
          Log.w(this.TAG, "onCaptureStarted() - No gyroscope on this device");
        }
      }
    }
  }
  
  private void onDisplacementChanged(int paramInt)
  {
    if ((this.m_Displacement != paramInt) && (Handle.isValid(this.m_CaptureHandle)))
    {
      Log.v(this.TAG, "onDisplacementChanged() - ", Integer.valueOf(this.m_Displacement), " -> ", Integer.valueOf(paramInt));
      this.m_Displacement = paramInt;
      switch (paramInt)
      {
      default: 
        paramInt = 0;
      }
      for (;;)
      {
        updateHint();
        if (this.m_DirIndicatorsContainer == null) {
          return;
        }
        if (paramInt != 0) {
          break;
        }
        i = this.m_DirIndicatorsContainer.getChildCount() - 1;
        while (i >= 0)
        {
          this.m_DirIndicatorsContainer.getChildAt(i).setBackground(this.m_DirIndicatorDrawable);
          i -= 1;
        }
        paramInt = 10;
        continue;
        paramInt = -10;
      }
      int i = this.m_DirIndicatorsContainer.getChildCount() - 1;
      while (i >= 0)
      {
        this.m_DirIndicatorsContainer.getChildAt(i).setBackground(this.m_DirIndicatorDrawableWarning);
        i -= 1;
      }
      this.m_DirIndicatorsContainerAnimator = this.m_DirIndicatorsContainer.animate().rotation(paramInt).setDuration(200L);
      if (this.m_DirIndicatorsContainerAnimator != null) {
        this.m_DirIndicatorsContainerAnimator.start();
      }
    }
  }
  
  private void onFrameAddFailed(int paramInt)
  {
    this.m_LastFrameAddFailedResult = paramInt;
  }
  
  private void onMovingSpeedChanged(boolean paramBoolean)
  {
    if (!Handle.isValid(this.m_CaptureHandle))
    {
      Log.w(this.TAG, "onMovingSpeedChanged() - Not capturing");
      return;
    }
    this.m_IsMovingTooFast = paramBoolean;
    updateHint();
  }
  
  private void onPreviewFrameReceived(Bitmap paramBitmap)
  {
    if ((this.m_PreviewFrameBitmap != null) && (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_RUNNING)).booleanValue()))
    {
      Canvas localCanvas = new Canvas(this.m_PreviewFrameBitmap);
      localCanvas.rotate(90.0F);
      localCanvas.translate(0.0F, -paramBitmap.getHeight() + this.m_CurrentFrameThickness);
      localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, null);
      if (this.m_PreviewFrameView != null) {
        this.m_PreviewFrameView.invalidate();
      }
      return;
    }
  }
  
  private void onResultSizeChanged(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, Bitmap paramBitmap)
  {
    if (getCameraActivityRotation().isPortrait()) {
      paramInt1 = paramInt2;
    }
    Log.v(this.TAG, "onResultSizeChanged() - width : ", Integer.valueOf(paramInt1), ", target width : ", Integer.valueOf(this.m_TargetWidth));
    int i = 0;
    paramInt2 = i;
    if (this.m_PreviewThumbBitmap != null)
    {
      paramInt2 = i;
      if (paramBitmap != null)
      {
        if (this.m_BlackSolidPaint == null)
        {
          this.m_BlackSolidPaint = new Paint();
          this.m_BlackSolidPaint.setColor(-16777216);
        }
        Canvas localCanvas = new Canvas(this.m_PreviewThumbBitmap);
        paramInt2 = this.m_Direction;
        paramFloat2 = this.m_PreviewThumbBitmap.getWidth() / this.m_TargetWidth;
        i = paramBitmap.getHeight();
        paramInt2 = Math.max(i, (int)((paramInt1 + (int)(i * 0.4F / paramFloat2)) * paramFloat2));
        paramInt1 = paramInt2 - i;
        Matrix localMatrix = new Matrix();
        localMatrix.postRotate(90.0F);
        localMatrix.postTranslate(paramInt2, -(int)(paramBitmap.getWidth() * paramFloat1));
        localCanvas.drawRect(paramInt1, 0.0F, paramInt2, this.m_PreviewThumbBitmap.getHeight(), this.m_BlackSolidPaint);
        localCanvas.drawBitmap(paramBitmap, localMatrix, null);
        ViewUtils.setMargins(this.m_CurrentFrame, Math.max(0, paramInt1), 0, 0, 0);
        if (paramInt2 < this.m_PreviewThumbBitmap.getWidth()) {
          break label341;
        }
        paramInt1 = 1;
        paramBitmap = PROP_PANORAMA_CAPTURE_LENGTH;
        if (paramInt2 - this.m_PreviewThumbBitmap.getWidth() < 1.0F) {
          break label346;
        }
      }
    }
    label341:
    label346:
    for (paramFloat1 = 1.0F;; paramFloat1 = paramInt2 / this.m_PreviewThumbBitmap.getWidth())
    {
      setReadOnly(paramBitmap, Float.valueOf(paramFloat1));
      if (this.m_PreviewThumbView != null) {
        this.m_PreviewThumbView.invalidate();
      }
      setViewVisibility(this.m_CurrentFrame, true, 0L, null);
      paramInt2 = paramInt1;
      if (paramInt2 != 0)
      {
        Log.w(this.TAG, "onResultSizeChanged() - Target size reached, stop capture");
        stopCapture(true);
      }
      this.m_PreviewFrameView.setVisibility(8);
      return;
      paramInt1 = 0;
      break;
    }
  }
  
  private void onSensorChanged(SensorEvent paramSensorEvent)
  {
    float[] arrayOfFloat = (float[])paramSensorEvent.values.clone();
    HandlerUtils.sendMessage(getController(), 10020, 0, 0, new Object[] { Long.valueOf(paramSensorEvent.timestamp), arrayOfFloat });
  }
  
  private void onStitching(boolean paramBoolean)
  {
    if (!isEntered())
    {
      Log.e(this.TAG, "onStitching() - Not entered");
      return;
    }
    int i;
    if (paramBoolean)
    {
      if (this.m_IsMovingTooFast)
      {
        i = 2131558499;
        setReadOnly(PROP_LAST_FRAME_ADD_RESULT, Integer.valueOf(-11));
        Handle.close(this.m_ErrorHintHandle);
        this.m_ErrorHintHandle = this.m_Hint.showHint(getCameraActivity().getString(i), 9);
        HandlerUtils.sendMessage(this, 10023, true, 3000L);
      }
    }
    else
    {
      setViewVisibility(this.m_PreviewThumbContainer, false);
      this.m_HintHandle = Handle.close(this.m_HintHandle);
      if (this.m_CaptureButtons != null)
      {
        Handle localHandle = this.m_CaptureButtons.setButtonBackground(CaptureButtons.Button.PRIMARY, getCameraActivity().getDrawable(2130837523), 0);
        Handle.close(this.m_CaptureButtonBgHandle);
        this.m_CaptureButtonBgHandle = localHandle;
      }
      if (this.m_ProcessingDialog == null) {
        this.m_ProcessingDialog = ((ProcessingDialog)findComponent(ProcessingDialog.class));
      }
      if ((this.m_ProcessingDialog != null) && (!Handle.isValid(this.m_ProcessingDialogHandle))) {
        break label288;
      }
    }
    for (;;)
    {
      if (!Handle.isValid(this.m_CaptureUIDisableHandle)) {
        this.m_CaptureUIDisableHandle = getCameraActivity().disableCaptureUI("PanoramaStitch");
      }
      return;
      setReadOnly(PROP_LAST_FRAME_ADD_RESULT, Integer.valueOf(this.m_LastFrameAddFailedResult));
      switch (this.m_LastFrameAddFailedResult)
      {
      default: 
        i = 2131558495;
        break;
      case -12: 
        i = 2131558493;
        break;
      case -11: 
        i = 2131558499;
        break;
      case -10: 
        i = 2131558502;
        break;
        label288:
        this.m_ProcessingDialogHandle = this.m_ProcessingDialog.showProcessingDialog(getCameraActivity().getString(2131558501), 0);
      }
    }
  }
  
  private void setupCaptureButton()
  {
    if ((!Handle.isValid(this.m_CaptureControlPanelStyleHandle)) && (this.m_CaptureControlPanel != null)) {
      this.m_CaptureControlPanelStyleHandle = this.m_CaptureControlPanel.setPanelStyle(CaptureControlPanel.Style.TRANSPARENT, 0);
    }
    if ((!Handle.isValid(this.m_CaptureButtonBgHandle)) && (this.m_CaptureButtons != null)) {
      this.m_CaptureButtonBgHandle = this.m_CaptureButtons.setButtonBackground(CaptureButtons.Button.PRIMARY, getCameraActivity().getDrawable(2130837523), 0);
    }
  }
  
  private void setupUI()
  {
    if (this.m_BaseView != null)
    {
      setViewVisibility(this.m_BaseView, true, 0L, null);
      if (this.m_PreviewFrameView != null) {
        this.m_PreviewFrameView.setImageBitmap(this.m_PreviewFrameBitmap);
      }
      return;
    }
    Log.v(this.TAG, "setupUI()");
    this.m_BaseView = ((ViewStub)((OPCameraActivity)getCameraActivity()).getCaptureUIContainer().findViewById(2131361954)).inflate();
    this.m_PreviewThumbContainer = this.m_BaseView.findViewById(2131362017);
    this.m_PreviewThumbView = ((ImageView)this.m_PreviewThumbContainer.findViewById(2131362018));
    this.m_PreviewFrameView = ((ImageView)this.m_PreviewThumbContainer.findViewById(2131362019));
    this.m_CurrentFrame = this.m_PreviewThumbContainer.findViewById(2131362020);
    this.m_DirIndicatorsContainer = ((ViewGroup)this.m_PreviewThumbContainer.findViewById(2131362021));
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_DirIndicatorDrawable = localCameraActivity.getDrawable(2130837820);
    this.m_DirIndicatorDrawableWarning = localCameraActivity.getDrawable(2130837821);
    this.m_CurrentFrameThickness = localCameraActivity.getResources().getDimensionPixelSize(2131296490);
    if (this.m_PreviewFrameView != null) {
      this.m_PreviewFrameView.setImageBitmap(this.m_PreviewFrameBitmap);
    }
  }
  
  private void stopCapture(boolean paramBoolean)
  {
    Object localObject = getCameraActivity();
    if (!isEntered()) {
      return;
    }
    if (!Handle.isValid(this.m_CaptureHandle))
    {
      Log.w(this.TAG, "stopCapture() - Not capturing");
      return;
    }
    if (((CameraActivity)localObject).get(CameraActivity.PROP_PHOTO_CAPTURE_STATE) != PhotoCaptureState.CAPTURING)
    {
      Log.w(this.TAG, "stopCapture() - Photo capture state is " + ((CameraActivity)localObject).get(CameraActivity.PROP_PHOTO_CAPTURE_STATE));
      return;
    }
    Log.w(this.TAG, "stopCapture() - Stitch : " + paramBoolean);
    if (!Handle.isValid(this.m_CaptureUIDisableHandle)) {
      this.m_CaptureUIDisableHandle = ((CameraActivity)localObject).disableCaptureUI("PanoramaStop");
    }
    localObject = getController();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      HandlerUtils.sendMessage((HandlerObject)localObject, 10001, i, 0, null);
      return;
    }
  }
  
  private void updateHint()
  {
    if (this.m_Hint == null) {
      return;
    }
    Object localObject = getCameraActivity();
    if (!Handle.isValid(this.m_HintHandle)) {
      this.m_HintHandle = this.m_Hint.showHint(null, 1);
    }
    if (this.m_IsControllerCaptureStarted)
    {
      if (this.m_IsMovingTooFast) {
        this.m_Hint.updateHint(this.m_HintHandle, ((CameraActivity)localObject).getString(2131558498), 0);
      }
    }
    else {
      return;
    }
    if (this.m_Displacement != 0)
    {
      switch (this.m_Displacement)
      {
      default: 
        localObject = ((CameraActivity)localObject).getString(2131558496);
      }
      for (;;)
      {
        this.m_Hint.updateHint(this.m_HintHandle, (CharSequence)localObject, 0);
        return;
        localObject = ((CameraActivity)localObject).getString(2131558497);
        continue;
        localObject = ((CameraActivity)localObject).getString(2131558500);
      }
    }
    this.m_Hint.updateHint(this.m_HintHandle, ((CameraActivity)localObject).getString(2131558496), 0);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    boolean bool2 = true;
    boolean bool1 = true;
    switch (paramMessage.what)
    {
    case 10005: 
    case 10006: 
    case 10007: 
    case 10008: 
    case 10009: 
    case 10012: 
    case 10013: 
    case 10014: 
    case 10015: 
    case 10016: 
    case 10017: 
    case 10018: 
    case 10019: 
    case 10022: 
    default: 
      super.handleMessage(paramMessage);
    case 10004: 
    case 10000: 
    case 10020: 
    case 10002: 
    case 10021: 
    case 10011: 
    case 10001: 
    case 10003: 
    case 10010: 
      do
      {
        for (;;)
        {
          return;
          onCaptureCompleted(paramMessage.arg1);
          return;
          onCaptureStarted(paramMessage.arg1, paramMessage.arg2);
          return;
          onDisplacementChanged(paramMessage.arg1);
          return;
          onFrameAddFailed(paramMessage.arg1);
          return;
          if (paramMessage.arg1 != 0) {}
          for (;;)
          {
            onMovingSpeedChanged(bool1);
            return;
            bool1 = false;
          }
          paramMessage = (Bitmap)paramMessage.obj;
          if (paramMessage != null)
          {
            try
            {
              onPreviewFrameReceived(paramMessage);
              return;
            }
            finally
            {
              if (this.m_YuvToBitmapWorker != null) {
                this.m_YuvToBitmapWorker.releaseBitmap(paramMessage);
              }
            }
            Object localObject2 = (Object[])paramMessage.obj;
            Bitmap localBitmap = (Bitmap)localObject2[1];
            localObject2 = (float[])localObject2[0];
            try
            {
              onResultSizeChanged(paramMessage.arg1, paramMessage.arg2, localObject2[0], localObject2[1], localBitmap);
              return;
            }
            finally
            {
              if ((this.m_YuvToBitmapWorker != null) && (localBitmap != null)) {
                this.m_YuvToBitmapWorker.releaseBitmap(localBitmap);
              }
            }
          }
        }
        if (paramMessage.arg1 != 0) {}
        for (bool1 = bool2;; bool1 = false)
        {
          onStitching(bool1);
          return;
        }
      } while (!isEntered());
      this.m_YuvToBitmapWorker = ((YuvToBitmapWorker)paramMessage.obj);
      return;
    }
    this.m_ErrorHintHandle = Handle.close(this.m_ErrorHintHandle);
  }
  
  protected void onControllerLinked(PanoramaController paramPanoramaController)
  {
    if ((isEntered()) && (this.m_PreviewFrameBitmap != null))
    {
      if (!getCameraActivityRotation().isPortrait()) {
        break label53;
      }
      HandlerUtils.sendMessage(paramPanoramaController, 10010, this.m_PreviewFrameBitmap.getHeight(), this.m_PreviewFrameBitmap.getWidth(), null);
    }
    for (;;)
    {
      super.onControllerLinked(paramPanoramaController);
      return;
      label53:
      HandlerUtils.sendMessage(paramPanoramaController, 10010, this.m_PreviewFrameBitmap.getWidth(), this.m_PreviewFrameBitmap.getHeight(), null);
    }
  }
  
  protected boolean onEnter(int paramInt)
  {
    this.m_Handles = new HandleSet(new Handle[0]);
    if (this.m_SceneManager == null) {
      this.m_SceneManager = ((SceneManager)findComponent(SceneManager.class));
    }
    if (this.m_SceneManager != null) {
      this.m_Handles.addHandle(this.m_SceneManager.setDefaultScene(Scene.NO_SCENE, 2));
    }
    if (!super.onEnter(paramInt))
    {
      this.m_Handles = ((HandleSet)Handle.close(this.m_Handles));
      return false;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    if (!localCameraActivity.setMediaType(MediaType.PHOTO))
    {
      Log.e(this.TAG, "onEnter() - Fail to change to photo mode");
      return false;
    }
    if (this.m_CaptureControlPanel == null) {
      findComponent(CaptureControlPanel.class, new ComponentSearchCallback()
      {
        public void onComponentFound(CaptureControlPanel paramAnonymousCaptureControlPanel)
        {
          PanoramaUI.-set2(PanoramaUI.this, paramAnonymousCaptureControlPanel);
          if (PanoramaUI.-wrap0(PanoramaUI.this)) {
            PanoramaUI.-wrap5(PanoramaUI.this);
          }
        }
      });
    }
    if (this.m_CaptureButtons == null) {
      findComponent(CaptureButtons.class, new ComponentSearchCallback()
      {
        public void onComponentFound(CaptureButtons paramAnonymousCaptureButtons)
        {
          PanoramaUI.-set1(PanoramaUI.this, paramAnonymousCaptureButtons);
          paramAnonymousCaptureButtons.addHandler(CaptureButtons.EVENT_BUTTON_PRESSED, new EventHandler()
          {
            public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<CaptureButtonEventArgs> paramAnonymous2EventKey, CaptureButtonEventArgs paramAnonymous2CaptureButtonEventArgs)
            {
              PanoramaUI.-wrap2(PanoramaUI.this, paramAnonymous2CaptureButtonEventArgs);
            }
          });
          paramAnonymousCaptureButtons.addHandler(CaptureButtons.EVENT_BUTTON_RELEASED, new EventHandler()
          {
            public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey<CaptureButtonEventArgs> paramAnonymous2EventKey, CaptureButtonEventArgs paramAnonymous2CaptureButtonEventArgs)
            {
              PanoramaUI.-wrap3(PanoramaUI.this, paramAnonymous2CaptureButtonEventArgs);
            }
          });
          if (PanoramaUI.-wrap0(PanoramaUI.this)) {
            PanoramaUI.-wrap5(PanoramaUI.this);
          }
        }
      });
    }
    if (this.m_FlashController == null) {
      this.m_FlashController = ((FlashController)findComponent(FlashController.class));
    }
    if (this.m_Hint == null) {
      this.m_Hint = ((OnScreenHint)findComponent(OnScreenHint.class));
    }
    if (this.m_WatermarkUI == null) {
      this.m_WatermarkUI = ((WatermarkUI)findComponent(WatermarkUI.class));
    }
    if (this.m_ZoomController == null) {
      this.m_ZoomController = ((ZoomController)findComponent(ZoomController.class));
    }
    if (this.m_ResolutionManager == null) {
      findComponent(ResolutionManager.class, new ComponentSearchCallback()
      {
        public void onComponentFound(ResolutionManager paramAnonymousResolutionManager)
        {
          PanoramaUI.-set3(PanoramaUI.this, paramAnonymousResolutionManager);
          if ((PanoramaUI.-wrap0(PanoramaUI.this)) && (Handle.isValid(PanoramaUI.-get3(PanoramaUI.this)))) {
            PanoramaUI.-get3(PanoramaUI.this).addHandle(PanoramaUI.-get8(PanoramaUI.this).setResolutionSelector(PanoramaUI.-get9(PanoramaUI.this), 0));
          }
        }
      });
    }
    if (this.m_ResolutionSelector == null) {
      this.m_ResolutionSelector = new ResolutionSelector(localCameraActivity);
    }
    if (this.m_ResolutionManager != null) {
      this.m_Handles.addHandle(this.m_ResolutionManager.setResolutionSelector(this.m_ResolutionSelector, 0));
    }
    this.m_Handles.addHandle(localCameraActivity.disableSelfTimer());
    if (this.m_FlashController != null) {
      this.m_Handles.addHandle(this.m_FlashController.disableFlash(FlashController.FlashDisabledReason.NOT_SUPPORTED_IN_CAPTURE_MODE, 0));
    }
    this.m_Handles.addHandle(localCameraActivity.lockCamera(Camera.LensFacing.BACK));
    this.m_RotationLockHandle = localCameraActivity.lockRotation(Rotation.PORTRAIT);
    if (this.m_ZoomController != null) {
      this.m_Handles.addHandle(this.m_ZoomController.lockZoom(0));
    }
    this.m_Handles.addHandle(localCameraActivity.disableBurstPhotoCapture());
    this.m_Handles.addHandle(localCameraActivity.requestPreCaptureFocusLock());
    if (this.m_CaptureButtons != null) {
      setupCaptureButton();
    }
    if (this.m_PreviewFrameBitmap == null)
    {
      Resources localResources = localCameraActivity.getResources();
      this.m_PreviewFrameBitmap = Bitmap.createBitmap(localResources.getDimensionPixelSize(2131296494), localResources.getDimensionPixelSize(2131296492), Bitmap.Config.RGB_565);
      if (isControllerLinked())
      {
        if (!getCameraActivityRotation().isPortrait()) {
          break label591;
        }
        HandlerUtils.sendMessage(getController(), 10010, this.m_PreviewFrameBitmap.getHeight(), this.m_PreviewFrameBitmap.getWidth(), null);
      }
    }
    if (((Boolean)localCameraActivity.get(OPCameraActivity.PROP_IS_CAPTURE_UI_INFLATED)).booleanValue()) {
      setupUI();
    }
    for (;;)
    {
      if (this.m_FaceTracker == null) {
        this.m_FaceTracker = ((FaceTracker)getCameraActivity().findComponent(FaceTracker.class));
      }
      if (this.m_FaceTracker != null) {
        this.m_FaceDetectionHandle = this.m_FaceTracker.disableFaceDetection(FaceTracker.FaceDetectionDisabledReason.NOT_SUPPORTED_IN_CAPTURE_MODE, 0);
      }
      return true;
      label591:
      HandlerUtils.sendMessage(getController(), 10010, this.m_PreviewFrameBitmap.getWidth(), this.m_PreviewFrameBitmap.getHeight(), null);
      break;
      Log.w(this.TAG, "onEnter() - Setup UI when capture UI inflated");
      if (this.m_CaptureUIInflatedCallback == null)
      {
        this.m_CaptureUIInflatedCallback = new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
          {
            if (PanoramaUI.-wrap0(PanoramaUI.this)) {
              PanoramaUI.-wrap6(PanoramaUI.this);
            }
          }
        };
        localCameraActivity.addCallback(OPCameraActivity.PROP_IS_CAPTURE_UI_INFLATED, this.m_CaptureUIInflatedCallback);
      }
    }
  }
  
  protected void onExit(int paramInt)
  {
    this.m_CaptureControlPanelStyleHandle = Handle.close(this.m_CaptureControlPanelStyleHandle);
    this.m_CaptureButtonBgHandle = Handle.close(this.m_CaptureButtonBgHandle);
    this.m_CaptureUIDisableHandle = Handle.close(this.m_CaptureUIDisableHandle);
    this.m_RotationLockHandle = Handle.close(this.m_RotationLockHandle);
    this.m_Handles = ((HandleSet)Handle.close(this.m_Handles));
    setViewVisibility(this.m_BaseView, false);
    this.m_HintHandle = Handle.close(this.m_HintHandle);
    HandlerUtils.removeMessages(this, 10023);
    this.m_ErrorHintHandle = Handle.close(this.m_ErrorHintHandle);
    if (this.m_PreviewFrameView != null) {
      this.m_PreviewFrameView.setImageDrawable(null);
    }
    this.m_PreviewFrameBitmap = null;
    this.m_YuvToBitmapWorker = null;
    this.m_FaceDetectionHandle = Handle.close(this.m_FaceDetectionHandle);
    super.onExit(paramInt);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    findComponent(CameraGallery.class, new ComponentSearchCallback()
    {
      public void onComponentFound(CameraGallery paramAnonymousCameraGallery)
      {
        paramAnonymousCameraGallery.addCallback(CameraGallery.PROP_UI_STATE, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<CameraGallery.UIState> paramAnonymous2PropertyKey, PropertyChangeEventArgs<CameraGallery.UIState> paramAnonymous2PropertyChangeEventArgs)
          {
            switch (-getcom-oneplus-camera-ui-CameraGallery$UIStateSwitchesValues()[((CameraGallery.UIState)paramAnonymous2PropertyChangeEventArgs.getNewValue()).ordinal()])
            {
            }
            do
            {
              return;
              PanoramaUI.-set4(PanoramaUI.this, Handle.close(PanoramaUI.-get10(PanoramaUI.this)));
              return;
            } while ((!PanoramaUI.-wrap0(PanoramaUI.this)) || (Handle.isValid(PanoramaUI.-get10(PanoramaUI.this))));
            PanoramaUI.-set4(PanoramaUI.this, PanoramaUI.this.getCameraActivity().lockRotation(Rotation.PORTRAIT));
          }
        });
      }
    });
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addHandler(CameraActivity.EVENT_CAPTURE_FAILED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
      {
        if (paramAnonymousCaptureEventArgs.getCaptureHandle() == PanoramaUI.-get2(PanoramaUI.this)) {
          PanoramaUI.-wrap1(PanoramaUI.this);
        }
      }
    });
    localCameraActivity.addHandler(CameraActivity.EVENT_CAPTURE_STARTED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
      {
        if (PanoramaUI.-wrap0(PanoramaUI.this))
        {
          if (PanoramaUI.-get1(PanoramaUI.this) != null)
          {
            paramAnonymousEventSource = PanoramaUI.-get1(PanoramaUI.this).setButtonBackground(CaptureButtons.Button.PRIMARY, PanoramaUI.this.getCameraActivity().getDrawable(2130837524), 0);
            Handle.close(PanoramaUI.-get0(PanoramaUI.this));
            PanoramaUI.-set0(PanoramaUI.this, paramAnonymousEventSource);
          }
          if (Handle.isValid(PanoramaUI.-get5(PanoramaUI.this))) {
            PanoramaUI.-get4(PanoramaUI.this).updateHint(PanoramaUI.-get5(PanoramaUI.this), PanoramaUI.this.getCameraActivity().getString(2131558496), 0);
          }
          paramAnonymousEventSource = paramAnonymousCaptureEventArgs.getCaptureHandle();
          if ((PanoramaUI.-get11(PanoramaUI.this) != null) && (paramAnonymousEventSource != null)) {
            PanoramaUI.-get11(PanoramaUI.this).excludeCapture(paramAnonymousEventSource.getInternalCaptureHandle());
          }
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_RUNNING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if ((!PanoramaUI.-wrap0(PanoramaUI.this)) || (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())) {}
        do
        {
          do
          {
            return;
          } while (PanoramaUI.-get6(PanoramaUI.this) == null);
          new Canvas(PanoramaUI.-get6(PanoramaUI.this)).drawColor(-16777216);
        } while (PanoramaUI.-get7(PanoramaUI.this) == null);
        PanoramaUI.-get7(PanoramaUI.this).invalidate();
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        if (PanoramaUI.-wrap0(PanoramaUI.this))
        {
          switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
          {
          default: 
            return;
          }
          if (PanoramaUI.-get1(PanoramaUI.this) != null)
          {
            paramAnonymousPropertySource = PanoramaUI.-get1(PanoramaUI.this).setButtonBackground(CaptureButtons.Button.PRIMARY, PanoramaUI.this.getCameraActivity().getDrawable(2130837523), 0);
            Handle.close(PanoramaUI.-get0(PanoramaUI.this));
            PanoramaUI.-set0(PanoramaUI.this, paramAnonymousPropertySource);
          }
        }
      }
    });
  }
  
  private static final class ResolutionSelector
    extends DefaultPhotoResolutionSelector
  {
    private static final AspectRatio[] PHOTO_RATIOS = { AspectRatio.RATIO_16x9 };
    
    public ResolutionSelector(CameraActivity paramCameraActivity)
    {
      super();
    }
    
    public Size selectPreviewSize(Camera paramCamera, Settings paramSettings, Size paramSize, Resolution paramResolution)
    {
      return super.selectPreviewSize(paramCamera, paramSettings, new Size(4096, 4096), paramResolution);
    }
    
    public List<Resolution> selectResolutions(Camera paramCamera, Settings paramSettings, ResolutionSelector.Restriction paramRestriction)
    {
      Object localObject;
      Size localSize1;
      int i;
      if (paramRestriction == null)
      {
        localObject = new ResolutionSelector.Restriction(new Size(4096, 4096));
        paramRestriction = super.selectResolutions(paramCamera, paramSettings, PHOTO_RATIOS, 8, (ResolutionSelector.Restriction)localObject);
        if (paramCamera.get(Camera.PROP_HARDWARE_LEVEL) != Camera.HardwareLevel.FULL)
        {
          localObject = new ArrayList();
          localSize1 = new Size(4096, 4096);
          i = 0;
        }
      }
      else
      {
        for (;;)
        {
          if (i < paramRestriction.size())
          {
            Resolution localResolution = (Resolution)paramRestriction.get(i);
            Size localSize2 = super.selectPreviewSize(paramCamera, paramSettings, localSize1, localResolution);
            if ((localSize2 != null) && (localSize2.getWidth() >= localResolution.getWidth()) && (localSize2.getHeight() >= localResolution.getHeight())) {
              ((List)localObject).add(localResolution);
            }
          }
          else
          {
            return (List<Resolution>)localObject;
            localObject = paramRestriction;
            if (paramRestriction.maxSize == null) {
              break;
            }
            if (paramRestriction.maxSize.getWidth() <= 4096)
            {
              localObject = paramRestriction;
              if (paramRestriction.maxSize.getHeight() <= 4096) {
                break;
              }
            }
            localObject = new ResolutionSelector.Restriction(new Size(Math.min(4096, paramRestriction.maxSize.getWidth()), Math.min(4096, paramRestriction.maxSize.getHeight())));
            break;
          }
          i += 1;
        }
      }
      return paramRestriction;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/panorama/PanoramaUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */