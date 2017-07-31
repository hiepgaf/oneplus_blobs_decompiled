package com.oneplus.camera.ui;

import android.os.Message;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.VideoCaptureState;

public class LongMediaProcessingUI
  extends UIComponent
{
  private static final long DURATION_SHOW_VIDEO_DIALOG_MIN = 250L;
  private static final long DURATION_VIDEO_SHOW_PROCESSING_DIALOG_DELAY = 500L;
  private static final int MSG_CLOSE_VIDEO_PROCESSING_DIALOG = 10006;
  private static final int MSG_SHOW_VIDEO_PROCESSING_DIALOG = 10002;
  private long m_LastTimeShowVideoDialog;
  private ProcessingDialog m_ProcessingDialog;
  private Handle m_VideoProcessingDialogHandle;
  
  public LongMediaProcessingUI(CameraActivity paramCameraActivity)
  {
    super("Long Media Processing UI", paramCameraActivity, true);
  }
  
  private void closeVideoProcessingDialog()
  {
    if (!Handle.isValid(this.m_VideoProcessingDialogHandle)) {
      return;
    }
    long l = System.currentTimeMillis() - this.m_LastTimeShowVideoDialog;
    if (l >= 250L)
    {
      Log.v(this.TAG, "closeVideoProcessingDialog()");
      HandlerUtils.removeMessages(this, 10002);
      this.m_VideoProcessingDialogHandle = Handle.close(this.m_VideoProcessingDialogHandle);
      return;
    }
    HandlerUtils.sendMessage(this, 10006, 250L - l + 50L);
  }
  
  private void onVideoCaptureStateChanged(VideoCaptureState paramVideoCaptureState)
  {
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[paramVideoCaptureState.ordinal()])
    {
    default: 
      return;
    case 4: 
      HandlerUtils.removeMessages(this, 10002);
      HandlerUtils.sendMessage(this, 10002, 500L);
      return;
    }
    HandlerUtils.removeMessages(this, 10002);
    HandlerUtils.removeMessages(this, 10006);
    HandlerUtils.sendMessage(this, 10006);
  }
  
  private void showVideoProcessingDialog()
  {
    if (Handle.isValid(this.m_VideoProcessingDialogHandle)) {
      return;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    if (this.m_ProcessingDialog == null)
    {
      this.m_ProcessingDialog = ((ProcessingDialog)localCameraActivity.findComponent(ProcessingDialog.class));
      if (this.m_ProcessingDialog == null)
      {
        Log.w(this.TAG, "showVideoProcessingDialog() - No ProcessingDialog interface");
        return;
      }
    }
    Log.v(this.TAG, "showVideoProcessingDialog()");
    this.m_LastTimeShowVideoDialog = System.currentTimeMillis();
    this.m_VideoProcessingDialogHandle = this.m_ProcessingDialog.showProcessingDialog(localCameraActivity.getString(2131558504), 0);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10006: 
      closeVideoProcessingDialog();
      return;
    }
    showVideoProcessingDialog();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraActivity().addCallback(CameraActivity.PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        LongMediaProcessingUI.-wrap0(LongMediaProcessingUI.this, (VideoCaptureState)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/LongMediaProcessingUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */