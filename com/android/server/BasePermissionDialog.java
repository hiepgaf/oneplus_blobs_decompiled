package com.android.server;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class BasePermissionDialog
  extends AlertDialog
{
  private Handler mInfoHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      if (paramAnonymousMessage.what == 0)
      {
        BasePermissionDialog.-set0(BasePermissionDialog.this, false);
        BasePermissionDialog.-wrap0(BasePermissionDialog.this, true);
      }
    }
  };
  private boolean mState = true;
  
  public BasePermissionDialog(Context paramContext)
  {
    super(paramContext, 16974975);
    getWindow().setType(2003);
    getWindow().setFlags(131072, 131072);
    paramContext = getWindow().getAttributes();
    paramContext.setTitle("Permission Dialog");
    getWindow().setAttributes(paramContext);
    setIconAttribute(16843605);
  }
  
  private void setEnabled(boolean paramBoolean)
  {
    Button localButton = (Button)findViewById(16908313);
    if (localButton != null) {
      localButton.setEnabled(paramBoolean);
    }
    localButton = (Button)findViewById(16908314);
    if (localButton != null) {
      localButton.setEnabled(paramBoolean);
    }
    localButton = (Button)findViewById(16908315);
    if (localButton != null) {
      localButton.setEnabled(paramBoolean);
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (this.mState) {
      return true;
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  public void onStart()
  {
    super.onStart();
    setEnabled(false);
    this.mInfoHandler.sendMessage(this.mInfoHandler.obtainMessage(0));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/BasePermissionDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */