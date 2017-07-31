package com.oneplus.camera.bokeh;

import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import com.oneplus.base.Handle;
import com.oneplus.base.HandleSet;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.camera.BokehDebugInfo;
import com.oneplus.camera.BokehState;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.FlashController;
import com.oneplus.camera.FlashController.FlashDisabledReason;
import com.oneplus.camera.ModeUI;
import com.oneplus.camera.ZoomController;
import com.oneplus.camera.media.DefaultPhotoResolutionSelector;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.Resolution;
import com.oneplus.camera.media.ResolutionManager;
import com.oneplus.camera.media.ResolutionSelector.Restriction;
import com.oneplus.camera.scene.Scene;
import com.oneplus.camera.scene.SceneManager;
import com.oneplus.camera.ui.OnScreenHint;
import com.oneplus.util.AspectRatio;
import java.util.List;

public final class BokehUI
  extends ModeUI<BokehController>
{
  static final int MSG_BOKEH_STATE_CHANGED = 10001;
  static final int MSG_DEBUG_INFO_UPDATED = 10010;
  public static final PropertyKey<Boolean> PROP_HAS_BOKEH_EFFECT = new PropertyKey("HasBokehEffect", Boolean.class, BokehUI.class, Boolean.valueOf(false));
  private BokehState m_BokehState = BokehState.DISABLED;
  private TextView m_DebugInfoTextView;
  private FlashController m_FlashController;
  private HandleSet m_Handles;
  private Handle m_HintHandle;
  private OnScreenHint m_OnScreenHint;
  private ResolutionManager m_ResolutionManager;
  private ResolutionSelector m_ResolutionSelector;
  private SceneManager m_SceneManager;
  private Handle m_StateHintHandle;
  private ZoomController m_ZoomController;
  
  BokehUI(CameraActivity paramCameraActivity)
  {
    super("Portrait UI", paramCameraActivity, BokehController.class);
  }
  
  private void onBokehStateChanged(BokehState paramBokehState)
  {
    if (!isEntered()) {
      return;
    }
    this.m_BokehState = paramBokehState;
    boolean bool = false;
    int j = 5;
    int i;
    int k;
    switch (-getcom-oneplus-camera-BokehStateSwitchesValues()[paramBokehState.ordinal()])
    {
    default: 
      i = 2131558456;
      k = 2131558450;
      j = 69;
    }
    while (this.m_OnScreenHint == null)
    {
      findComponent(OnScreenHint.class, new ComponentSearchCallback()
      {
        public void onComponentFound(OnScreenHint paramAnonymousOnScreenHint)
        {
          BokehUI.-set0(BokehUI.this, paramAnonymousOnScreenHint);
          BokehUI.-wrap2(BokehUI.this, BokehUI.-get0(BokehUI.this));
        }
      });
      return;
      i = 2131558454;
      k = 2131558450;
      j = 69;
      continue;
      i = 2131558455;
      k = 2131558450;
      j = 69;
      continue;
      i = 0;
      k = 0;
      continue;
      i = 2131558451;
      k = 2131558450;
      j = 69;
      continue;
      bool = true;
      i = 0;
      k = 2131558450;
      j = 37;
    }
    paramBokehState = getCameraActivity();
    String str;
    if (k != 0)
    {
      str = paramBokehState.getString(k);
      if (Handle.isValid(this.m_StateHintHandle))
      {
        this.m_OnScreenHint.updateHint(this.m_StateHintHandle, str, j);
        if (i == 0) {
          break label301;
        }
        paramBokehState = paramBokehState.getString(i);
        if (!Handle.isValid(this.m_HintHandle)) {
          break label283;
        }
        this.m_OnScreenHint.updateHint(this.m_HintHandle, paramBokehState, 1);
      }
    }
    for (;;)
    {
      setReadOnly(PROP_HAS_BOKEH_EFFECT, Boolean.valueOf(bool));
      return;
      this.m_StateHintHandle = this.m_OnScreenHint.showHint(str, j);
      break;
      this.m_StateHintHandle = Handle.close(this.m_StateHintHandle);
      break;
      label283:
      this.m_HintHandle = this.m_OnScreenHint.showSecondaryHint(paramBokehState, 1);
      continue;
      label301:
      this.m_HintHandle = Handle.close(this.m_HintHandle);
    }
  }
  
  private void updateDebugInfo(BokehDebugInfo[] paramArrayOfBokehDebugInfo)
  {
    if (!isEntered()) {
      return;
    }
    if (this.m_DebugInfoTextView == null) {
      this.m_DebugInfoTextView = ((TextView)((ViewStub)getCameraActivity().findViewById(2131361961)).inflate().findViewById(2131361855));
    }
    if ((paramArrayOfBokehDebugInfo != null) && (paramArrayOfBokehDebugInfo.length > 0))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int i = paramArrayOfBokehDebugInfo.length - 1;
      while (i >= 0)
      {
        BokehDebugInfo localBokehDebugInfo = paramArrayOfBokehDebugInfo[i];
        localStringBuilder.append("{\n");
        localStringBuilder.append("  cameraRole = ").append(localBokehDebugInfo.cameraRole).append("\n");
        localStringBuilder.append("  startX = ").append(localBokehDebugInfo.startX).append("\n");
        localStringBuilder.append("  startY = ").append(localBokehDebugInfo.startY).append("\n");
        localStringBuilder.append("  width = ").append(localBokehDebugInfo.width).append("\n");
        localStringBuilder.append("  height = ").append(localBokehDebugInfo.height).append("\n");
        localStringBuilder.append("  exposureTime = ").append(localBokehDebugInfo.exposureTime).append("\n");
        localStringBuilder.append("  realGain = ").append(localBokehDebugInfo.realGain).append("\n");
        localStringBuilder.append("  aecStatus = ").append(localBokehDebugInfo.aecStatus).append("\n");
        localStringBuilder.append("  lensShiftUm = ").append(localBokehDebugInfo.lensShiftUm).append("\n");
        localStringBuilder.append("  afStatus = ").append(localBokehDebugInfo.afStatus).append("\n");
        localStringBuilder.append("}\n");
        i -= 1;
      }
      this.m_DebugInfoTextView.setText(localStringBuilder);
      return;
    }
    this.m_DebugInfoTextView.setText(null);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10001: 
      onBokehStateChanged((BokehState)paramMessage.obj);
      return;
    }
    updateDebugInfo((BokehDebugInfo[])paramMessage.obj);
  }
  
  protected boolean onEnter(int paramInt)
  {
    CameraActivity localCameraActivity = getCameraActivity();
    if (!localCameraActivity.switchCamera(Camera.LensFacing.BACK, 36))
    {
      Log.e(this.TAG, "onEnter() - Fail to switch to BACK camera");
      return false;
    }
    Handle localHandle = localCameraActivity.lockCamera(Camera.LensFacing.BACK);
    if (!Handle.isValid(localHandle))
    {
      Log.e(this.TAG, "onEnter() - Fail to lock camera");
      return false;
    }
    this.m_Handles = new HandleSet(new Handle[] { localHandle });
    if (!super.onEnter(paramInt))
    {
      this.m_Handles = ((HandleSet)Handle.close(this.m_Handles));
      return false;
    }
    if (!localCameraActivity.setMediaType(MediaType.PHOTO))
    {
      Log.e(this.TAG, "onEnter() - Fail to change to photo mode");
      this.m_Handles = ((HandleSet)Handle.close(this.m_Handles));
      return false;
    }
    if (this.m_ZoomController != null) {
      this.m_Handles.addHandle(this.m_ZoomController.lockZoom(0));
    }
    if (this.m_FlashController != null) {
      this.m_Handles.addHandle(this.m_FlashController.disableFlash(FlashController.FlashDisabledReason.NOT_SUPPORTED_IN_CAPTURE_MODE, 0));
    }
    this.m_Handles.addHandle(localCameraActivity.disableBurstPhotoCapture());
    if (this.m_SceneManager != null) {
      this.m_Handles.addHandle(this.m_SceneManager.setDefaultScene(Scene.NO_SCENE, 2));
    }
    if (this.m_ResolutionManager != null)
    {
      if (this.m_ResolutionSelector == null) {
        this.m_ResolutionSelector = new ResolutionSelector(getCameraActivity());
      }
      this.m_Handles.addHandle(this.m_ResolutionManager.setResolutionSelector(this.m_ResolutionSelector, 0));
    }
    onBokehStateChanged(BokehState.NORMAL);
    if (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_DEBUG_MODE)).booleanValue())
    {
      if (this.m_DebugInfoTextView != null) {
        this.m_DebugInfoTextView.setVisibility(0);
      }
      HandlerUtils.sendMessage(getController(), 10001);
    }
    return true;
  }
  
  protected void onExit(int paramInt)
  {
    this.m_Handles = ((HandleSet)Handle.close(this.m_Handles));
    this.m_BokehState = BokehState.DISABLED;
    setReadOnly(PROP_HAS_BOKEH_EFFECT, Boolean.valueOf(false));
    this.m_StateHintHandle = Handle.close(this.m_StateHintHandle);
    this.m_HintHandle = Handle.close(this.m_HintHandle);
    if (this.m_DebugInfoTextView != null)
    {
      this.m_DebugInfoTextView.setText(null);
      this.m_DebugInfoTextView.setVisibility(8);
    }
    super.onExit(paramInt);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_FlashController = ((FlashController)localCameraActivity.findComponent(FlashController.class));
    this.m_ResolutionManager = ((ResolutionManager)localCameraActivity.findComponent(ResolutionManager.class));
    this.m_SceneManager = ((SceneManager)localCameraActivity.findComponent(SceneManager.class));
    this.m_ZoomController = ((ZoomController)localCameraActivity.findComponent(ZoomController.class));
    localCameraActivity.addCallback(CameraActivity.PROP_IS_DEBUG_MODE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!BokehUI.-wrap0(BokehUI.this)) {
          return;
        }
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
        {
          HandlerUtils.sendMessage(BokehUI.-wrap1(BokehUI.this), 10001);
          return;
        }
        if (BokehUI.-get1(BokehUI.this) != null)
        {
          BokehUI.-get1(BokehUI.this).setText(null);
          BokehUI.-get1(BokehUI.this).setVisibility(8);
        }
        HandlerUtils.sendMessage(BokehUI.-wrap1(BokehUI.this), 10002);
      }
    });
  }
  
  private static final class ResolutionSelector
    extends DefaultPhotoResolutionSelector
  {
    private static final AspectRatio[] PHOTO_RATIOS = { AspectRatio.RATIO_4x3 };
    
    public ResolutionSelector(CameraActivity paramCameraActivity)
    {
      super();
    }
    
    public List<Resolution> selectResolutions(Camera paramCamera, Settings paramSettings, ResolutionSelector.Restriction paramRestriction)
    {
      return super.selectResolutions(paramCamera, paramSettings, PHOTO_RATIOS, 1, paramRestriction);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/bokeh/BokehUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */