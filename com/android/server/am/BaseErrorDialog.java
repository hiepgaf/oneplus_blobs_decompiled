package com.android.server.am;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

class BaseErrorDialog
  extends AlertDialog
{
  private static final int DISABLE_BUTTONS = 1;
  private static final int ENABLE_BUTTONS = 0;
  private boolean mConsuming = true;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      if (paramAnonymousMessage.what == 0)
      {
        BaseErrorDialog.-set0(BaseErrorDialog.this, false);
        BaseErrorDialog.-wrap0(BaseErrorDialog.this, true);
      }
      while (paramAnonymousMessage.what != 1) {
        return;
      }
      BaseErrorDialog.-wrap0(BaseErrorDialog.this, false);
    }
  };
  
  public BaseErrorDialog(Context paramContext)
  {
    super(paramContext, 16974975);
    getWindow().setType(2003);
    getWindow().setFlags(131072, 131072);
    paramContext = getWindow().getAttributes();
    paramContext.setTitle("Error Dialog");
    getWindow().setAttributes(paramContext);
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
    if (this.mConsuming) {
      return true;
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  public void onStart()
  {
    super.onStart();
    this.mHandler.sendEmptyMessage(1);
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0), 1000L);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/BaseErrorDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */