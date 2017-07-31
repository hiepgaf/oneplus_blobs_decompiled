package com.android.server.am;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.ThemeController;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnWindowShownListener;
import android.view.Window;
import android.widget.TextView;
import com.android.internal.annotations.GuardedBy;

final class UserSwitchingDialog
  extends AlertDialog
  implements ViewTreeObserver.OnWindowShownListener
{
  private static final int MSG_START_USER = 1;
  private static final String TAG = "ActivityManagerUserSwitchingDialog";
  private static final int WINDOW_SHOWN_TIMEOUT_MS = 3000;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      UserSwitchingDialog.this.startUser();
    }
  };
  private final ActivityManagerService mService;
  @GuardedBy("this")
  private boolean mStartedUser;
  private TextView mTextView;
  private final int mUserId;
  
  public UserSwitchingDialog(ActivityManagerService paramActivityManagerService, Context paramContext, UserInfo paramUserInfo1, UserInfo paramUserInfo2, boolean paramBoolean)
  {
    super(paramContext);
    this.mService = paramActivityManagerService;
    this.mUserId = paramUserInfo2.id;
    setCancelable(false);
    paramActivityManagerService = getContext().getResources();
    View localView = LayoutInflater.from(getContext()).inflate(17367302, null);
    this.mTextView = ((TextView)localView.findViewById(16908299));
    if ((UserManager.isSplitSystemUser()) && (paramUserInfo2.id == 0)) {
      paramActivityManagerService = paramActivityManagerService.getString(17040712, new Object[] { paramUserInfo1.name });
    }
    for (;;)
    {
      ((TextView)localView.findViewById(16908299)).setText(paramActivityManagerService);
      setView(localView);
      if (paramBoolean) {
        getWindow().setType(2010);
      }
      paramActivityManagerService = getWindow().getAttributes();
      paramActivityManagerService.privateFlags = 272;
      getWindow().setAttributes(paramActivityManagerService);
      return;
      if (UserManager.isDeviceInDemoMode(paramContext))
      {
        if (paramUserInfo1.isDemo()) {
          paramActivityManagerService = paramActivityManagerService.getString(17040911);
        } else {
          paramActivityManagerService = paramActivityManagerService.getString(17040910);
        }
      }
      else {
        paramActivityManagerService = paramActivityManagerService.getString(17040711, new Object[] { paramUserInfo2.name });
      }
    }
  }
  
  public void onWindowShown()
  {
    startUser();
  }
  
  public void show()
  {
    super.show();
    View localView = getWindow().getDecorView();
    if (localView != null)
    {
      localView.getViewTreeObserver().addOnWindowShownListener(this);
      localView = localView.findViewById(16908290);
      Context localContext = getContext();
      localView.setBackgroundColor(ThemeController.getInstance(localContext).getCorrectThemeResource(new int[] { -1, localContext.getColor(17170551) }));
      this.mTextView.setTextColor(ThemeController.getInstance(localContext).getCorrectThemeResource(new int[] { -16777216, -1 }));
    }
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), 3000L);
  }
  
  void startUser()
  {
    try
    {
      if (!this.mStartedUser)
      {
        this.mService.mUserController.startUserInForeground(this.mUserId, this);
        this.mStartedUser = true;
        View localView = getWindow().getDecorView();
        if (localView != null) {
          localView.getViewTreeObserver().removeOnWindowShownListener(this);
        }
        this.mHandler.removeMessages(1);
      }
      return;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/UserSwitchingDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */