package com.oneplus.camera.timelapse;

import android.util.Size;
import com.oneplus.base.Handle;
import com.oneplus.base.HandleSet;
import com.oneplus.base.Log;
import com.oneplus.base.Settings;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.ModeUI;
import com.oneplus.camera.media.DefaultVideoResolutionSelector;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.Resolution;
import com.oneplus.camera.media.ResolutionManager;
import com.oneplus.camera.media.ResolutionSelector.Restriction;
import java.util.List;

final class TimelapseUI
  extends ModeUI<TimelapseController>
{
  private static final int MAX_VIDEO_SIDE = 2160;
  private HandleSet m_Handles;
  private Handle m_RecordingTimeRatioHandle;
  private ResolutionManager m_ResolutionManager;
  private ResolutionSelector m_ResolutionSelector;
  
  TimelapseUI(CameraActivity paramCameraActivity)
  {
    super("Time-lapse UI", paramCameraActivity, TimelapseController.class);
  }
  
  protected boolean onEnter(int paramInt)
  {
    Object localObject = getCameraActivity();
    if (!((CameraActivity)localObject).setMediaType(MediaType.VIDEO)) {
      return false;
    }
    if (!super.onEnter(paramInt)) {
      return false;
    }
    this.m_Handles = new HandleSet(new Handle[0]);
    if (this.m_ResolutionSelector == null) {
      this.m_ResolutionSelector = new ResolutionSelector((CameraActivity)localObject);
    }
    if (this.m_ResolutionManager != null)
    {
      localObject = this.m_ResolutionManager.setResolutionSelector(this.m_ResolutionSelector, 0);
      if (!Handle.isValid((Handle)localObject))
      {
        Log.e(this.TAG, "onEnter() - Fail to change resolution selector");
        return false;
      }
      this.m_Handles.addHandle((Handle)localObject);
    }
    this.m_RecordingTimeRatioHandle = getCameraActivity().setRecordingTimeRatio(0.16666667F);
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
        TimelapseUI.-set0(TimelapseUI.this, paramAnonymousResolutionManager);
        if (TimelapseUI.-wrap0(TimelapseUI.this)) {
          TimelapseUI.-get0(TimelapseUI.this).addHandle(TimelapseUI.-get1(TimelapseUI.this).setResolutionSelector(TimelapseUI.-get2(TimelapseUI.this), 0));
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
      ResolutionSelector.Restriction localRestriction;
      if (paramRestriction == null) {
        localRestriction = new ResolutionSelector.Restriction(new Size(2160, 2160), NaN.0F, 0);
      }
      for (;;)
      {
        return super.selectResolutions(paramCamera, paramSettings, localRestriction);
        localRestriction = paramRestriction;
        if (paramRestriction.maxSize != null) {
          if (paramRestriction.maxSize.getWidth() <= 2160)
          {
            localRestriction = paramRestriction;
            if (paramRestriction.maxSize.getHeight() <= 2160) {}
          }
          else
          {
            localRestriction = new ResolutionSelector.Restriction(new Size(Math.min(2160, paramRestriction.maxSize.getWidth()), Math.min(2160, paramRestriction.maxSize.getHeight())), NaN.0F, 0);
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/timelapse/TimelapseUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */