package com.android.server.am;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

final class FactoryErrorDialog
  extends BaseErrorDialog
{
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      throw new RuntimeException("Rebooting from failed factory test");
    }
  };
  
  public FactoryErrorDialog(Context paramContext, CharSequence paramCharSequence)
  {
    super(paramContext);
    setCancelable(false);
    setTitle(paramContext.getText(17040118));
    setMessage(paramCharSequence);
    setButton(-1, paramContext.getText(17040121), this.mHandler.obtainMessage(0));
    paramContext = getWindow().getAttributes();
    paramContext.setTitle("Factory Error");
    getWindow().setAttributes(paramContext);
  }
  
  public void onStop() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/FactoryErrorDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */