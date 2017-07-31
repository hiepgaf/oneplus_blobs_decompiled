package com.oneplus.camera.slowmotion;

import com.oneplus.base.Handle;
import com.oneplus.base.HandleSet;
import com.oneplus.base.Log;
import com.oneplus.base.Settings;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.ModeUI;
import com.oneplus.camera.media.DefaultVideoResolutionSelector;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.Resolution;
import com.oneplus.camera.media.ResolutionManager;
import com.oneplus.camera.media.ResolutionSelector.Restriction;
import java.util.Arrays;
import java.util.List;

final class SlowMotionUI
  extends ModeUI<SlowMotionController>
{
  private static final int PREVIEW_FPS = 120;
  private HandleSet m_Handles;
  private Handle m_RecordingTimeRatioHandle;
  private ResolutionManager m_ResolutionManager;
  private ResolutionSelector m_ResolutionSelector;
  
  SlowMotionUI(CameraActivity paramCameraActivity)
  {
    super("Slow-motion UI", paramCameraActivity, SlowMotionController.class);
  }
  
  protected boolean onEnter(int paramInt)
  {
    CameraActivity localCameraActivity = getCameraActivity();
    if (!localCameraActivity.setMediaType(MediaType.VIDEO)) {
      return false;
    }
    if (!super.onEnter(paramInt)) {
      return false;
    }
    this.m_Handles = new HandleSet(new Handle[0]);
    if (this.m_ResolutionSelector == null) {
      this.m_ResolutionSelector = new ResolutionSelector(localCameraActivity);
    }
    if (this.m_ResolutionManager != null)
    {
      Handle localHandle = this.m_ResolutionManager.setResolutionSelector(this.m_ResolutionSelector, 0);
      if (!Handle.isValid(localHandle))
      {
        Log.e(this.TAG, "onEnter() - Fail to change resolution selector");
        return false;
      }
      this.m_Handles.addHandle(localHandle);
    }
    this.m_RecordingTimeRatioHandle = localCameraActivity.setRecordingTimeRatio(4.0F);
    this.m_Handles.addHandle(localCameraActivity.lockCamera(Camera.LensFacing.BACK));
    return true;
  }
  
  protected void onExit(int paramInt)
  {
    this.m_RecordingTimeRatioHandle = Handle.close(this.m_RecordingTimeRatioHandle);
    this.m_Handles = ((HandleSet)Handle.close(this.m_Handles));
    super.onExit(paramInt);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    ComponentUtils.findComponent(getCameraActivity(), ResolutionManager.class, this, new ComponentSearchCallback()
    {
      public void onComponentFound(ResolutionManager paramAnonymousResolutionManager)
      {
        SlowMotionUI.-set0(SlowMotionUI.this, paramAnonymousResolutionManager);
        if (SlowMotionUI.-wrap0(SlowMotionUI.this)) {
          SlowMotionUI.-get0(SlowMotionUI.this).addHandle(SlowMotionUI.-get1(SlowMotionUI.this).setResolutionSelector(SlowMotionUI.-get2(SlowMotionUI.this), 0));
        }
      }
    });
  }
  
  private static final class ResolutionSelector
    extends DefaultVideoResolutionSelector
  {
    public ResolutionSelector(CameraActivity paramCameraActivity)
    {
      super();
    }
    
    public List<Resolution> selectResolutions(Camera paramCamera, Settings paramSettings, ResolutionSelector.Restriction paramRestriction)
    {
      paramCamera = super.selectResolutions(paramCamera, paramSettings, paramRestriction);
      if (paramCamera != null)
      {
        int i = paramCamera.size() - 1;
        while (i >= 0)
        {
          paramSettings = (Resolution)paramCamera.get(i);
          if (paramSettings.is720pVideo()) {
            return Arrays.asList(new Resolution[] { new Resolution(paramSettings, 120) });
          }
          i -= 1;
        }
      }
      return null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/slowmotion/SlowMotionUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */