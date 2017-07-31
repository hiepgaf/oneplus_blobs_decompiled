package com.android.server.am;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class LockTaskNotify
{
  private static final String TAG = "LockTaskNotify";
  private final Context mContext;
  private final H mHandler;
  private Toast mLastToast;
  
  public LockTaskNotify(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new H(null);
  }
  
  private Toast makeAllUserToastAndShow(String paramString)
  {
    paramString = Toast.makeText(this.mContext, paramString, 1);
    WindowManager.LayoutParams localLayoutParams = paramString.getWindowParams();
    localLayoutParams.privateFlags |= 0x10;
    paramString.show();
    return paramString;
  }
  
  public void handleShowToast(int paramInt)
  {
    String str = null;
    if (paramInt == 1) {
      str = this.mContext.getString(17040836);
    }
    while (str == null)
    {
      return;
      if (paramInt == 2) {
        str = this.mContext.getString(17040835);
      }
    }
    if (this.mLastToast != null) {
      this.mLastToast.cancel();
    }
    this.mLastToast = makeAllUserToastAndShow(str);
  }
  
  public void show(boolean paramBoolean)
  {
    int i = 17040838;
    if (paramBoolean) {
      i = 17040837;
    }
    makeAllUserToastAndShow(this.mContext.getString(i));
  }
  
  public void showToast(int paramInt)
  {
    this.mHandler.obtainMessage(3, paramInt, 0).sendToTarget();
  }
  
  private final class H
    extends Handler
  {
    private static final int SHOW_TOAST = 3;
    
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      LockTaskNotify.this.handleShowToast(paramMessage.arg1);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/LockTaskNotify.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */