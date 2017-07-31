package com.android.server.retaildemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.TextView;

public class UserInactivityCountdownDialog
  extends AlertDialog
{
  private long mCountDownDuration;
  private CountDownTimer mCountDownTimer;
  private OnCountDownExpiredListener mOnCountDownExpiredListener;
  private long mRefreshInterval;
  
  UserInactivityCountdownDialog(Context paramContext, long paramLong1, long paramLong2)
  {
    super(paramContext);
    this.mCountDownDuration = paramLong1;
    this.mRefreshInterval = paramLong2;
    getWindow().setType(2010);
    paramContext = getWindow().getAttributes();
    paramContext.privateFlags = 16;
    getWindow().setAttributes(paramContext);
    setTitle(17040912);
    setMessage(getContext().getString(17040913, new Object[] { Long.valueOf(paramLong1) }));
  }
  
  public void onStop()
  {
    if (this.mCountDownTimer != null) {
      this.mCountDownTimer.cancel();
    }
  }
  
  public void setNegativeButtonClickListener(DialogInterface.OnClickListener paramOnClickListener)
  {
    setButton(-2, getContext().getString(17040914), paramOnClickListener);
  }
  
  public void setOnCountDownExpiredListener(OnCountDownExpiredListener paramOnCountDownExpiredListener)
  {
    this.mOnCountDownExpiredListener = paramOnCountDownExpiredListener;
  }
  
  public void setPositiveButtonClickListener(DialogInterface.OnClickListener paramOnClickListener)
  {
    setButton(-1, getContext().getString(17040915), paramOnClickListener);
  }
  
  public void show()
  {
    super.show();
    final TextView localTextView = (TextView)findViewById(16908299);
    localTextView.post(new Runnable()
    {
      public void run()
      {
        UserInactivityCountdownDialog.-set0(UserInactivityCountdownDialog.this, new CountDownTimer(UserInactivityCountdownDialog.-get0(UserInactivityCountdownDialog.this), UserInactivityCountdownDialog.-get2(UserInactivityCountdownDialog.this))
        {
          public void onFinish()
          {
            UserInactivityCountdownDialog.this.dismiss();
            if (UserInactivityCountdownDialog.-get1(UserInactivityCountdownDialog.this) != null) {
              UserInactivityCountdownDialog.-get1(UserInactivityCountdownDialog.this).onCountDownExpired();
            }
          }
          
          public void onTick(long paramAnonymous2Long)
          {
            String str = UserInactivityCountdownDialog.this.getContext().getString(17040913, new Object[] { Long.valueOf(paramAnonymous2Long / 1000L) });
            this.val$messageView.setText(str);
          }
        }.start());
      }
    });
  }
  
  static abstract interface OnCountDownExpiredListener
  {
    public abstract void onCountDownExpired();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/retaildemo/UserInactivityCountdownDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */