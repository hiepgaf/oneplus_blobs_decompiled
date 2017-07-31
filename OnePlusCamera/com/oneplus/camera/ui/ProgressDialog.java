package com.oneplus.camera.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.DialogManager;

public class ProgressDialog
{
  private final CameraActivity m_Activity;
  private Dialog m_Dialog;
  private Handle m_DialogHandle;
  private float m_Progress;
  private ProgressBar m_ProgressBar;
  private CharSequence m_Title;
  
  public ProgressDialog(CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity == null) {
      throw new IllegalArgumentException("No activity");
    }
    this.m_Activity = paramCameraActivity;
  }
  
  public void dismiss()
  {
    if (this.m_Dialog == null) {
      return;
    }
    this.m_DialogHandle = Handle.close(this.m_DialogHandle);
  }
  
  public void setProgress(float paramFloat)
  {
    float f;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    for (;;)
    {
      this.m_Progress = f;
      if (this.m_ProgressBar != null) {
        this.m_ProgressBar.setProgress(Math.round(this.m_ProgressBar.getMax() * f));
      }
      return;
      f = paramFloat;
      if (paramFloat > 1.0F) {
        f = 1.0F;
      }
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.m_Title = paramCharSequence;
    if (this.m_Dialog != null) {
      this.m_Dialog.setTitle(paramCharSequence);
    }
  }
  
  public boolean show()
  {
    if (Handle.isValid(this.m_DialogHandle)) {
      return true;
    }
    DialogManager localDialogManager = (DialogManager)this.m_Activity.findComponent(DialogManager.class);
    if (localDialogManager == null)
    {
      Log.e(ProgressDialog.class.getSimpleName(), "show() - No DialogManager");
      return false;
    }
    if (((Boolean)this.m_Activity.get(CameraActivity.PROP_IS_BLACK_MODE)).booleanValue()) {}
    for (int i = 2131492907;; i = 2131492905)
    {
      this.m_Dialog = new AlertDialog.Builder(this.m_Activity, i).setTitle(this.m_Title).setView(2130903048).setCancelable(false).setOnKeyListener(new DialogInterface.OnKeyListener()
      {
        public boolean onKey(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          return true;
        }
      }).create();
      this.m_DialogHandle = localDialogManager.showDialog(this.m_Dialog, null, null, null, 0);
      this.m_ProgressBar = ((ProgressBar)this.m_Dialog.findViewById(16908301));
      this.m_ProgressBar.setProgress(Math.round(this.m_Progress * this.m_ProgressBar.getMax()));
      return Handle.isValid(this.m_DialogHandle);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ProgressDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */