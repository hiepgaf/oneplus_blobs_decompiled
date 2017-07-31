package com.oneplus.camera.ui;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Rotation;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.UIComponent;

final class ToastManagerImpl
  extends UIComponent
  implements ToastManager
{
  private static final long DURATION_TOAST = 3000L;
  private static final int MSG_HIDE_TOAST = 10001;
  private ToastHandle m_CurrentToastHandle;
  private OnScreenHint m_OnScreenHint;
  private Handle m_OnScreenHintHandle;
  
  ToastManagerImpl(CameraActivity paramCameraActivity)
  {
    super("Toast manager", paramCameraActivity, true);
  }
  
  private void hideToast(ToastHandle paramToastHandle)
  {
    verifyAccess();
    if (this.m_CurrentToastHandle != paramToastHandle) {
      return;
    }
    getHandler().removeMessages(10001);
    this.m_OnScreenHintHandle = Handle.close(this.m_OnScreenHintHandle);
    this.m_CurrentToastHandle = ((ToastHandle)Handle.close(this.m_CurrentToastHandle));
  }
  
  private void showToast(ToastHandle paramToastHandle, int paramInt)
  {
    if (paramToastHandle == null) {
      return;
    }
    if (this.m_OnScreenHint != null)
    {
      if (this.m_CurrentToastHandle != null) {
        hideToast(this.m_CurrentToastHandle);
      }
      getHandler().removeMessages(10001);
      this.m_OnScreenHintHandle = this.m_OnScreenHint.showHint(paramToastHandle.message, paramInt | 0x8);
      HandlerUtils.sendMessage(this, 10001, 0, 0, null, true, 3000L);
    }
    this.m_CurrentToastHandle = paramToastHandle;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    hideToast(this.m_CurrentToastHandle);
  }
  
  protected void onDeinitialize()
  {
    if (Handle.isValid(this.m_CurrentToastHandle)) {
      hideToast(this.m_CurrentToastHandle);
    }
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_OnScreenHint = ((OnScreenHint)findComponent(OnScreenHint.class));
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    super.onRotationChanged(paramRotation1, paramRotation2);
    if (Handle.isValid(this.m_CurrentToastHandle))
    {
      getHandler().removeMessages(10001);
      HandlerUtils.sendMessage(this, 10001, 0, 0, null, true, 3000L);
    }
  }
  
  public Handle showToast(CharSequence paramCharSequence, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing(true)) {
      return null;
    }
    paramCharSequence = new ToastHandle(paramCharSequence);
    showToast(paramCharSequence, paramInt);
    return paramCharSequence;
  }
  
  private final class ToastHandle
    extends Handle
  {
    public final CharSequence message;
    public long showTime;
    public Toast toast;
    
    public ToastHandle(CharSequence paramCharSequence)
    {
      super();
      this.message = paramCharSequence;
    }
    
    protected void onClose(int paramInt)
    {
      ToastManagerImpl.-wrap0(ToastManagerImpl.this, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ToastManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */