package com.android.settings;

import android.app.ActivityManagerNative;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminInfo.PolicyInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteCallback.OnResultListener;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.EventLog;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AppSecurityPermissions;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.users.UserDialogs;
import com.oneplus.settings.BaseActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceAdminAdd
  extends BaseActivity
{
  static final int DIALOG_WARNING = 1;
  public static final String EXTRA_CALLED_FROM_SUPPORT_DIALOG = "android.app.extra.CALLED_FROM_SUPPORT_DIALOG";
  public static final String EXTRA_DEVICE_ADMIN_PACKAGE_NAME = "android.app.extra.DEVICE_ADMIN_PACKAGE_NAME";
  private static final int MAX_ADD_MSG_LINES = 15;
  private static final int MAX_ADD_MSG_LINES_LANDSCAPE = 2;
  private static final int MAX_ADD_MSG_LINES_PORTRAIT = 5;
  static final String TAG = "DeviceAdminAdd";
  Button mActionButton;
  TextView mAddMsg;
  boolean mAddMsgEllipsized = true;
  ImageView mAddMsgExpander;
  CharSequence mAddMsgText;
  boolean mAdding;
  boolean mAddingProfileOwner;
  TextView mAdminDescription;
  ImageView mAdminIcon;
  TextView mAdminName;
  ViewGroup mAdminPolicies;
  boolean mAdminPoliciesInitialized;
  TextView mAdminWarning;
  AppOpsManager mAppOps;
  Button mCancelButton;
  int mCurSysAppOpMode;
  int mCurToastAppOpMode;
  DevicePolicyManager mDPM;
  DeviceAdminInfo mDeviceAdmin;
  Handler mHandler;
  boolean mIsCalledFromSupportDialog = false;
  String mProfileOwnerName;
  TextView mProfileOwnerWarning;
  boolean mRefreshing;
  TextView mSupportMessage;
  Button mUninstallButton;
  boolean mUninstalling = false;
  boolean mWaitingForRemoveMsg;
  
  private void addDeviceAdminPolicies(boolean paramBoolean)
  {
    if (!this.mAdminPoliciesInitialized)
    {
      boolean bool = UserManager.get(this).isAdminUser();
      Iterator localIterator = this.mDeviceAdmin.getUsedPolicies().iterator();
      if (localIterator.hasNext())
      {
        Object localObject = (DeviceAdminInfo.PolicyInfo)localIterator.next();
        int i;
        label63:
        int j;
        label74:
        CharSequence localCharSequence;
        if (bool)
        {
          i = ((DeviceAdminInfo.PolicyInfo)localObject).description;
          if (!bool) {
            break label149;
          }
          j = ((DeviceAdminInfo.PolicyInfo)localObject).label;
          localCharSequence = getText(j);
          if (!paramBoolean) {
            break label158;
          }
        }
        label149:
        label158:
        for (localObject = getText(i);; localObject = "")
        {
          localObject = AppSecurityPermissions.getPermissionItemView(this, localCharSequence, (CharSequence)localObject, true);
          ((ImageView)((View)localObject).findViewById(16909120)).getDrawable().setTint(getResources().getColor(2131427576));
          this.mAdminPolicies.addView((View)localObject);
          break;
          i = ((DeviceAdminInfo.PolicyInfo)localObject).descriptionForSecondaryUsers;
          break label63;
          j = ((DeviceAdminInfo.PolicyInfo)localObject).labelForSecondaryUsers;
          break label74;
        }
      }
      this.mAdminPoliciesInitialized = true;
    }
  }
  
  private boolean isAdminUninstallable()
  {
    return !this.mDeviceAdmin.getActivityInfo().applicationInfo.isSystemApp();
  }
  
  private boolean isManagedProfile(DeviceAdminInfo paramDeviceAdminInfo)
  {
    paramDeviceAdminInfo = UserManager.get(this).getUserInfo(UserHandle.getUserId(paramDeviceAdminInfo.getActivityInfo().applicationInfo.uid));
    if (paramDeviceAdminInfo != null) {
      return paramDeviceAdminInfo.isManagedProfile();
    }
    return false;
  }
  
  void addAndFinish()
  {
    try
    {
      this.mDPM.setActiveAdmin(this.mDeviceAdmin.getComponent(), this.mRefreshing);
      EventLog.writeEvent(90201, this.mDeviceAdmin.getActivityInfo().applicationInfo.uid);
      setResult(-1);
      if (!this.mAddingProfileOwner) {}
    }
    catch (RuntimeException localRuntimeException1)
    {
      try
      {
        do
        {
          this.mDPM.setProfileOwner(this.mDeviceAdmin.getComponent(), this.mProfileOwnerName, UserHandle.myUserId());
          finish();
          return;
          localRuntimeException1 = localRuntimeException1;
          Log.w("DeviceAdminAdd", "Exception trying to activate admin " + this.mDeviceAdmin.getComponent(), localRuntimeException1);
        } while (!this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent()));
        setResult(-1);
      }
      catch (RuntimeException localRuntimeException2)
      {
        for (;;)
        {
          setResult(0);
        }
      }
    }
  }
  
  void continueRemoveAction(CharSequence paramCharSequence)
  {
    if (!this.mWaitingForRemoveMsg) {
      return;
    }
    this.mWaitingForRemoveMsg = false;
    if (paramCharSequence == null) {}
    try
    {
      ActivityManagerNative.getDefault().resumeAppSwitches();
      this.mDPM.removeActiveAdmin(this.mDeviceAdmin.getComponent());
      finish();
      return;
      try
      {
        ActivityManagerNative.getDefault().stopAppSwitches();
        Bundle localBundle = new Bundle();
        localBundle.putCharSequence("android.app.extra.DISABLE_WARNING", paramCharSequence);
        showDialog(1, localBundle);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
    catch (RemoteException paramCharSequence)
    {
      for (;;) {}
    }
  }
  
  int getEllipsizedLines()
  {
    Display localDisplay = ((WindowManager)getSystemService("window")).getDefaultDisplay();
    if (localDisplay.getHeight() > localDisplay.getWidth()) {
      return 5;
    }
    return 2;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mHandler = new Handler(getMainLooper());
    this.mDPM = ((DevicePolicyManager)getSystemService("device_policy"));
    this.mAppOps = ((AppOpsManager)getSystemService("appops"));
    Object localObject3 = getPackageManager();
    if ((getIntent().getFlags() & 0x10000000) != 0)
    {
      Log.w("DeviceAdminAdd", "Cannot start ADD_DEVICE_ADMIN as a new task");
      finish();
      return;
    }
    this.mIsCalledFromSupportDialog = getIntent().getBooleanExtra("android.app.extra.CALLED_FROM_SUPPORT_DIALOG", false);
    String str = getIntent().getAction();
    Object localObject2 = (ComponentName)getIntent().getParcelableExtra("android.app.extra.DEVICE_ADMIN");
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = getIntent().getStringExtra("android.app.extra.DEVICE_ADMIN_PACKAGE_NAME");
      Iterator localIterator = this.mDPM.getActiveAdmins().iterator();
      do
      {
        paramBundle = (Bundle)localObject2;
        if (!localIterator.hasNext()) {
          break;
        }
        paramBundle = (ComponentName)localIterator.next();
      } while (!paramBundle.getPackageName().equals(localObject1));
      this.mUninstalling = true;
      localObject1 = paramBundle;
      if (paramBundle == null)
      {
        Log.w("DeviceAdminAdd", "No component specified in " + str);
        finish();
        return;
      }
    }
    if ((str != null) && (str.equals("android.app.action.SET_PROFILE_OWNER")))
    {
      setResult(0);
      setFinishOnTouchOutside(true);
      this.mAddingProfileOwner = true;
      this.mProfileOwnerName = getIntent().getStringExtra("android.app.extra.PROFILE_OWNER_NAME");
      paramBundle = getCallingPackage();
      if ((paramBundle != null) && (paramBundle.equals(((ComponentName)localObject1).getPackageName()))) {}
      try
      {
        if ((((PackageManager)localObject3).getPackageInfo(paramBundle, 0).applicationInfo.flags & 0x1) != 0) {
          break label379;
        }
        Log.e("DeviceAdminAdd", "Cannot set a non-system app as a profile owner");
        finish();
        return;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("DeviceAdminAdd", "Cannot find the package " + paramBundle);
        finish();
        return;
      }
      Log.e("DeviceAdminAdd", "Unknown or incorrect caller");
      finish();
      return;
    }
    for (;;)
    {
      label379:
      int m;
      int j;
      int k;
      try
      {
        paramBundle = ((PackageManager)localObject3).getReceiverInfo(localNameNotFoundException, 128);
        if (this.mDPM.isAdminActive(localNameNotFoundException)) {
          break;
        }
        localObject3 = ((PackageManager)localObject3).queryBroadcastReceivers(new Intent("android.app.action.DEVICE_ADMIN_ENABLED"), 32768);
        if (localObject3 == null)
        {
          i = 0;
          m = 0;
          j = 0;
          k = m;
          if (j < i)
          {
            localObject2 = (ResolveInfo)((List)localObject3).get(j);
            if ((!paramBundle.packageName.equals(((ResolveInfo)localObject2).activityInfo.packageName)) || (!paramBundle.name.equals(((ResolveInfo)localObject2).activityInfo.name))) {
              break label676;
            }
          }
        }
      }
      catch (PackageManager.NameNotFoundException paramBundle)
      {
        Log.w("DeviceAdminAdd", "Unable to retrieve device policy " + localNameNotFoundException, paramBundle);
        finish();
        return;
      }
      try
      {
        ((ResolveInfo)localObject2).activityInfo = paramBundle;
        new DeviceAdminInfo(this, (ResolveInfo)localObject2);
        k = 1;
      }
      catch (IOException localIOException)
      {
        Log.w("DeviceAdminAdd", "Bad " + ((ResolveInfo)localObject2).activityInfo, localIOException);
        k = m;
        continue;
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        Log.w("DeviceAdminAdd", "Bad " + ((ResolveInfo)localObject2).activityInfo, localXmlPullParserException);
        k = m;
        continue;
      }
      if (k != 0) {
        break;
      }
      Log.w("DeviceAdminAdd", "Request to add invalid device admin: " + localNameNotFoundException);
      finish();
      return;
      i = ((List)localObject3).size();
      continue;
      label676:
      j += 1;
    }
    localObject2 = new ResolveInfo();
    ((ResolveInfo)localObject2).activityInfo = paramBundle;
    try
    {
      this.mDeviceAdmin = new DeviceAdminInfo(this, (ResolveInfo)localObject2);
      if (!"android.app.action.ADD_DEVICE_ADMIN".equals(getIntent().getAction())) {
        break label937;
      }
      this.mRefreshing = false;
      if (!this.mDPM.isAdminActive(localNameNotFoundException)) {
        break label937;
      }
      if (this.mDPM.isRemovingAdmin(localNameNotFoundException, Process.myUserHandle().getIdentifier()))
      {
        Log.w("DeviceAdminAdd", "Requested admin is already being removed: " + localNameNotFoundException);
        finish();
        return;
      }
    }
    catch (IOException paramBundle)
    {
      Log.w("DeviceAdminAdd", "Unable to retrieve device policy " + localNameNotFoundException, paramBundle);
      finish();
      return;
    }
    catch (XmlPullParserException paramBundle)
    {
      Log.w("DeviceAdminAdd", "Unable to retrieve device policy " + localNameNotFoundException, paramBundle);
      finish();
      return;
    }
    paramBundle = this.mDeviceAdmin.getUsedPolicies();
    int i = 0;
    for (;;)
    {
      if (i < paramBundle.size())
      {
        localObject2 = (DeviceAdminInfo.PolicyInfo)paramBundle.get(i);
        if (!this.mDPM.hasGrantedPolicy(localNameNotFoundException, ((DeviceAdminInfo.PolicyInfo)localObject2).ident)) {
          this.mRefreshing = true;
        }
      }
      else
      {
        if (this.mRefreshing) {
          break;
        }
        setResult(-1);
        finish();
        return;
      }
      i += 1;
    }
    label937:
    if ((!this.mAddingProfileOwner) || (this.mDPM.hasUserSetupCompleted()))
    {
      this.mAddMsgText = getIntent().getCharSequenceExtra("android.app.extra.ADD_EXPLANATION");
      setContentView(2130968683);
      this.mAdminIcon = ((ImageView)findViewById(2131820859));
      this.mAdminName = ((TextView)findViewById(2131820860));
      this.mAdminDescription = ((TextView)findViewById(2131820862));
      this.mProfileOwnerWarning = ((TextView)findViewById(2131820861));
      this.mAddMsg = ((TextView)findViewById(2131820864));
      this.mAddMsgExpander = ((ImageView)findViewById(2131820863));
      paramBundle = new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          DeviceAdminAdd.this.toggleMessageEllipsis(DeviceAdminAdd.this.mAddMsg);
        }
      };
      this.mAddMsgExpander.setOnClickListener(paramBundle);
      this.mAddMsg.setOnClickListener(paramBundle);
      this.mAddMsg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          int i = DeviceAdminAdd.this.getEllipsizedLines();
          ImageView localImageView;
          if (DeviceAdminAdd.this.mAddMsg.getLineCount() <= i)
          {
            i = 1;
            localImageView = DeviceAdminAdd.this.mAddMsgExpander;
            if (i == 0) {
              break label95;
            }
          }
          label95:
          for (int j = 8;; j = 0)
          {
            localImageView.setVisibility(j);
            if (i != 0)
            {
              DeviceAdminAdd.this.mAddMsg.setOnClickListener(null);
              ((View)DeviceAdminAdd.this.mAddMsgExpander.getParent()).invalidate();
            }
            DeviceAdminAdd.this.mAddMsg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            return;
            i = 0;
            break;
          }
        }
      });
      toggleMessageEllipsis(this.mAddMsg);
      this.mAdminWarning = ((TextView)findViewById(2131820865));
      this.mAdminPolicies = ((ViewGroup)findViewById(2131820866));
      this.mSupportMessage = ((TextView)findViewById(2131820867));
      this.mCancelButton = ((Button)findViewById(2131820761));
      this.mCancelButton.setFilterTouchesWhenObscured(true);
      this.mCancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          EventLog.writeEvent(90202, DeviceAdminAdd.this.mDeviceAdmin.getActivityInfo().applicationInfo.uid);
          DeviceAdminAdd.this.finish();
        }
      });
      this.mUninstallButton = ((Button)findViewById(2131820868));
      this.mUninstallButton.setFilterTouchesWhenObscured(true);
      this.mUninstallButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          EventLog.writeEvent(90203, DeviceAdminAdd.this.mDeviceAdmin.getActivityInfo().applicationInfo.uid);
          DeviceAdminAdd.this.mDPM.uninstallPackageWithActiveAdmins(DeviceAdminAdd.this.mDeviceAdmin.getPackageName());
          DeviceAdminAdd.this.finish();
        }
      });
      this.mActionButton = ((Button)findViewById(2131820548));
      this.mActionButton.setFilterTouchesWhenObscured(true);
      this.mActionButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (DeviceAdminAdd.this.mAdding) {
            DeviceAdminAdd.this.addAndFinish();
          }
          do
          {
            return;
            if ((DeviceAdminAdd.-wrap0(DeviceAdminAdd.this, DeviceAdminAdd.this.mDeviceAdmin)) && (DeviceAdminAdd.this.mDeviceAdmin.getComponent().equals(DeviceAdminAdd.this.mDPM.getProfileOwner())))
            {
              final int i = UserHandle.myUserId();
              UserDialogs.createRemoveDialog(DeviceAdminAdd.this, i, new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  UserManager.get(DeviceAdminAdd.this).removeUser(i);
                  DeviceAdminAdd.this.finish();
                }
              }).show();
              return;
            }
            if (DeviceAdminAdd.this.mUninstalling)
            {
              DeviceAdminAdd.this.mDPM.uninstallPackageWithActiveAdmins(DeviceAdminAdd.this.mDeviceAdmin.getPackageName());
              DeviceAdminAdd.this.finish();
              return;
            }
          } while (DeviceAdminAdd.this.mWaitingForRemoveMsg);
          try
          {
            ActivityManagerNative.getDefault().stopAppSwitches();
            DeviceAdminAdd.this.mWaitingForRemoveMsg = true;
            DeviceAdminAdd.this.mDPM.getRemoveWarning(DeviceAdminAdd.this.mDeviceAdmin.getComponent(), new RemoteCallback(new RemoteCallback.OnResultListener()
            {
              public void onResult(Bundle paramAnonymous2Bundle)
              {
                if (paramAnonymous2Bundle != null) {}
                for (paramAnonymous2Bundle = paramAnonymous2Bundle.getCharSequence("android.app.extra.DISABLE_WARNING");; paramAnonymous2Bundle = null)
                {
                  DeviceAdminAdd.this.continueRemoveAction(paramAnonymous2Bundle);
                  return;
                }
              }
            }, DeviceAdminAdd.this.mHandler));
            DeviceAdminAdd.this.getWindow().getDecorView().getHandler().postDelayed(new Runnable()
            {
              public void run()
              {
                DeviceAdminAdd.this.continueRemoveAction(null);
              }
            }, 2000L);
            return;
          }
          catch (RemoteException paramAnonymousView)
          {
            for (;;) {}
          }
        }
      });
      return;
    }
    addAndFinish();
  }
  
  protected Dialog onCreateDialog(int paramInt, Bundle paramBundle)
  {
    switch (paramInt)
    {
    default: 
      return super.onCreateDialog(paramInt, paramBundle);
    }
    paramBundle = paramBundle.getCharSequence("android.app.extra.DISABLE_WARNING");
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setMessage(paramBundle);
    localBuilder.setPositiveButton(2131626495, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        try
        {
          ActivityManagerNative.getDefault().resumeAppSwitches();
          DeviceAdminAdd.this.mDPM.removeActiveAdmin(DeviceAdminAdd.this.mDeviceAdmin.getComponent());
          DeviceAdminAdd.this.finish();
          return;
        }
        catch (RemoteException paramAnonymousDialogInterface)
        {
          for (;;) {}
        }
      }
    });
    localBuilder.setNegativeButton(2131626496, null);
    return localBuilder.create();
  }
  
  protected void onPause()
  {
    super.onPause();
    int i = this.mDeviceAdmin.getActivityInfo().applicationInfo.uid;
    String str = this.mDeviceAdmin.getActivityInfo().applicationInfo.packageName;
    this.mAppOps.setMode(24, i, str, this.mCurSysAppOpMode);
    this.mAppOps.setMode(45, i, str, this.mCurToastAppOpMode);
    try
    {
      ActivityManagerNative.getDefault().resumeAppSwitches();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  protected void onResume()
  {
    super.onResume();
    updateInterface();
    int i = this.mDeviceAdmin.getActivityInfo().applicationInfo.uid;
    String str = this.mDeviceAdmin.getActivityInfo().applicationInfo.packageName;
    try
    {
      this.mCurSysAppOpMode = this.mAppOps.checkOp(24, i, str);
      this.mCurToastAppOpMode = this.mAppOps.checkOp(45, i, str);
      this.mAppOps.setMode(24, i, str, 1);
      this.mAppOps.setMode(45, i, str, 1);
      return;
    }
    catch (SecurityException localSecurityException) {}
  }
  
  protected void onUserLeaveHint()
  {
    super.onUserLeaveHint();
    if (this.mIsCalledFromSupportDialog) {
      finish();
    }
  }
  
  void toggleMessageEllipsis(View paramView)
  {
    TextView localTextView = (TextView)paramView;
    boolean bool;
    if (this.mAddMsgEllipsized)
    {
      bool = false;
      this.mAddMsgEllipsized = bool;
      if (!this.mAddMsgEllipsized) {
        break label82;
      }
      paramView = TextUtils.TruncateAt.END;
      label31:
      localTextView.setEllipsize(paramView);
      if (!this.mAddMsgEllipsized) {
        break label87;
      }
      i = getEllipsizedLines();
      label49:
      localTextView.setMaxLines(i);
      paramView = this.mAddMsgExpander;
      if (!this.mAddMsgEllipsized) {
        break label93;
      }
    }
    label82:
    label87:
    label93:
    for (int i = 17302196;; i = 17302195)
    {
      paramView.setImageResource(i);
      return;
      bool = true;
      break;
      paramView = null;
      break label31;
      i = 15;
      break label49;
    }
  }
  
  void updateInterface()
  {
    this.mAdminIcon.setImageDrawable(this.mDeviceAdmin.loadIcon(getPackageManager()));
    this.mAdminName.setText(this.mDeviceAdmin.loadLabel(getPackageManager()));
    for (;;)
    {
      try
      {
        this.mAdminDescription.setText(this.mDeviceAdmin.loadDescription(getPackageManager()));
        this.mAdminDescription.setVisibility(0);
        if (this.mAddingProfileOwner) {
          this.mProfileOwnerWarning.setVisibility(0);
        }
        if (this.mAddMsgText != null)
        {
          this.mAddMsg.setText(this.mAddMsgText);
          this.mAddMsg.setVisibility(0);
          if ((!this.mRefreshing) && (!this.mAddingProfileOwner)) {
            continue;
          }
          addDeviceAdminPolicies(true);
          this.mAdminWarning.setText(getString(2131627013, new Object[] { this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager()) }));
          if (!this.mAddingProfileOwner) {
            break label540;
          }
          setTitle(getText(2131627015));
          this.mActionButton.setText(getText(2131627011));
          if (isAdminUninstallable()) {
            this.mUninstallButton.setVisibility(0);
          }
          this.mSupportMessage.setVisibility(8);
          this.mAdding = true;
        }
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        this.mAdminDescription.setVisibility(8);
        continue;
        this.mAddMsg.setVisibility(8);
        this.mAddMsgExpander.setVisibility(8);
        continue;
        if (!this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent())) {
          continue;
        }
        this.mAdding = false;
        boolean bool1 = this.mDeviceAdmin.getComponent().equals(this.mDPM.getProfileOwner());
        boolean bool2 = isManagedProfile(this.mDeviceAdmin);
        if ((bool1) && (bool2))
        {
          this.mAdminWarning.setText(2131627960);
          this.mActionButton.setText(2131627065);
          CharSequence localCharSequence = this.mDPM.getLongSupportMessageForUser(this.mDeviceAdmin.getComponent(), UserHandle.myUserId());
          if (!TextUtils.isEmpty(localCharSequence))
          {
            this.mSupportMessage.setText(localCharSequence);
            this.mSupportMessage.setVisibility(0);
          }
        }
        else
        {
          if ((bool1) || (this.mDeviceAdmin.getComponent().equals(this.mDPM.getDeviceOwnerComponentOnCallingUser())))
          {
            if (bool1)
            {
              this.mAdminWarning.setText(2131627961);
              this.mActionButton.setText(2131627002);
              this.mActionButton.setEnabled(false);
              continue;
            }
            this.mAdminWarning.setText(2131627962);
            continue;
          }
          addDeviceAdminPolicies(false);
          this.mAdminWarning.setText(getString(2131627014, new Object[] { this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager()) }));
          setTitle(2131627001);
          if (this.mUninstalling)
          {
            this.mActionButton.setText(2131627004);
            continue;
          }
          this.mActionButton.setText(2131627002);
          continue;
        }
        this.mSupportMessage.setVisibility(8);
        return;
      }
      label540:
      setTitle(getText(2131627010));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/settings/DeviceAdminAdd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */