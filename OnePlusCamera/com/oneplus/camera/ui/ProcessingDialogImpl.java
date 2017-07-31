package com.oneplus.camera.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.Rotation;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.UIComponent.ViewRotationCallback;
import com.oneplus.camera.widget.RotateRelativeLayout;
import com.oneplus.util.ListUtils;
import java.util.ArrayList;
import java.util.List;

final class ProcessingDialogImpl
  extends UIComponent
  implements ProcessingDialog
{
  private View m_BaseView;
  private Handle m_CaptureUIDisableHandle;
  private RotateRelativeLayout m_Container;
  private final UIComponent.ViewRotationCallback m_ContainerRotationCallback = new UIComponent.ViewRotationCallback()
  {
    public void onRotated(View paramAnonymousView, Rotation paramAnonymousRotation)
    {
      if (ProcessingDialogImpl.-get1(ProcessingDialogImpl.this).size() == 0) {
        ProcessingDialogImpl.-wrap1(ProcessingDialogImpl.this, ProcessingDialogImpl.-get0(ProcessingDialogImpl.this), false);
      }
    }
  };
  private final List<DialogHandle> m_DialogHandles = new ArrayList();
  private AnimationDrawable m_IconDrawable;
  private TextView m_MessageTextView;
  private View m_TouchReceiver;
  
  ProcessingDialogImpl(CameraActivity paramCameraActivity)
  {
    super("Processing dialog", paramCameraActivity, false);
  }
  
  private void closeProcessingDialog(DialogHandle paramDialogHandle)
  {
    verifyAccess();
    boolean bool = ListUtils.isLastObject(this.m_DialogHandles, paramDialogHandle);
    if (!this.m_DialogHandles.remove(paramDialogHandle)) {
      return;
    }
    if (bool)
    {
      if (this.m_DialogHandles.isEmpty())
      {
        setViewVisibility(this.m_BaseView, false);
        this.m_IconDrawable.stop();
        this.m_CaptureUIDisableHandle = Handle.close(this.m_CaptureUIDisableHandle);
      }
    }
    else {
      return;
    }
    showProcessingDialog((DialogHandle)this.m_DialogHandles.get(this.m_DialogHandles.size() - 1));
  }
  
  private void showProcessingDialog(DialogHandle paramDialogHandle)
  {
    if (this.m_Container == null)
    {
      localObject = getCameraActivity();
      View localView = (View)((CameraActivity)localObject).get(CameraActivity.PROP_CONTENT_VIEW);
      if (!(localView instanceof ViewGroup))
      {
        Log.e(this.TAG, "showProcessingDialog() - Content view is not a ViewGroup");
        return;
      }
      this.m_BaseView = View.inflate((Context)localObject, 2130903091, null);
      this.m_Container = ((RotateRelativeLayout)this.m_BaseView.findViewById(2131362048));
      this.m_TouchReceiver = this.m_BaseView.findViewById(2131362047);
      this.m_TouchReceiver.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      this.m_MessageTextView = ((TextView)this.m_Container.findViewById(2131362050));
      this.m_IconDrawable = ((AnimationDrawable)((ImageView)this.m_Container.findViewById(2131362049)).getDrawable());
      ((ViewGroup)localView).addView(this.m_BaseView, -1, -1);
    }
    Object localObject = this.m_MessageTextView;
    if (paramDialogHandle.message == null) {}
    for (paramDialogHandle = getCameraActivity().getString(2131558503);; paramDialogHandle = paramDialogHandle.message)
    {
      ((TextView)localObject).setText(paramDialogHandle);
      rotateLayout(this.m_Container, 0L);
      setViewVisibility(this.m_BaseView, true);
      this.m_IconDrawable.start();
      if (!Handle.isValid(this.m_CaptureUIDisableHandle)) {
        this.m_CaptureUIDisableHandle = getCameraActivity().disableCaptureUI("ProcessingDialog");
      }
      return;
    }
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    super.onRotationChanged(paramRotation1, paramRotation2);
    if ((this.m_BaseView != null) && (this.m_BaseView.getVisibility() == 0))
    {
      rotateLayout(this.m_Container, this.m_ContainerRotationCallback);
      return;
    }
    rotateLayout(this.m_Container, 0L, this.m_ContainerRotationCallback);
  }
  
  public Handle showProcessingDialog(CharSequence paramCharSequence, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing(true)) {
      return null;
    }
    paramCharSequence = new DialogHandle(paramCharSequence);
    this.m_DialogHandles.add(paramCharSequence);
    showProcessingDialog(paramCharSequence);
    return paramCharSequence;
  }
  
  private final class DialogHandle
    extends Handle
  {
    public final CharSequence message;
    
    public DialogHandle(CharSequence paramCharSequence)
    {
      super();
      this.message = paramCharSequence;
    }
    
    protected void onClose(int paramInt)
    {
      ProcessingDialogImpl.-wrap0(ProcessingDialogImpl.this, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ProcessingDialogImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */