package com.android.server.restrictions;

import android.app.AppGlobals;
import android.app.admin.IDevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IRestrictionsManager.Stub;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IUserManager;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.UserHandle;
import com.android.internal.util.ArrayUtils;
import com.android.server.SystemService;

public final class RestrictionsManagerService
  extends SystemService
{
  static final boolean DEBUG = false;
  static final String LOG_TAG = "RestrictionsManagerService";
  private final RestrictionsManagerImpl mRestrictionsManagerImpl;
  
  public RestrictionsManagerService(Context paramContext)
  {
    super(paramContext);
    this.mRestrictionsManagerImpl = new RestrictionsManagerImpl(paramContext);
  }
  
  public void onStart()
  {
    publishBinderService("restrictions", this.mRestrictionsManagerImpl);
  }
  
  class RestrictionsManagerImpl
    extends IRestrictionsManager.Stub
  {
    final Context mContext;
    private final IDevicePolicyManager mDpm;
    private final IUserManager mUm;
    
    public RestrictionsManagerImpl(Context paramContext)
    {
      this.mContext = paramContext;
      this.mUm = ((IUserManager)RestrictionsManagerService.-wrap0(RestrictionsManagerService.this, "user"));
      this.mDpm = ((IDevicePolicyManager)RestrictionsManagerService.-wrap0(RestrictionsManagerService.this, "device_policy"));
    }
    
    private void enforceCallerMatchesPackage(int paramInt, String paramString1, String paramString2)
    {
      try
      {
        String[] arrayOfString = AppGlobals.getPackageManager().getPackagesForUid(paramInt);
        if ((arrayOfString != null) && (!ArrayUtils.contains(arrayOfString, paramString1))) {
          throw new SecurityException(paramString2 + paramInt);
        }
      }
      catch (RemoteException paramString1) {}
    }
    
    public Intent createLocalApprovalIntent()
      throws RemoteException
    {
      int i = UserHandle.getCallingUserId();
      if (this.mDpm != null)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          ComponentName localComponentName = this.mDpm.getRestrictionsProvider(i);
          if (localComponentName == null) {
            throw new IllegalStateException("Cannot request permission without a restrictions provider registered");
          }
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        Object localObject2 = ((ComponentName)localObject1).getPackageName();
        Intent localIntent = new Intent("android.content.action.REQUEST_LOCAL_APPROVAL");
        localIntent.setPackage((String)localObject2);
        localObject2 = AppGlobals.getPackageManager().resolveIntent(localIntent, null, 0, i);
        if ((localObject2 != null) && (((ResolveInfo)localObject2).activityInfo != null) && (((ResolveInfo)localObject2).activityInfo.exported))
        {
          localIntent.setComponent(new ComponentName(((ResolveInfo)localObject2).activityInfo.packageName, ((ResolveInfo)localObject2).activityInfo.name));
          Binder.restoreCallingIdentity(l);
          return localIntent;
        }
        Binder.restoreCallingIdentity(l);
      }
      return null;
    }
    
    public Bundle getApplicationRestrictions(String paramString)
      throws RemoteException
    {
      return this.mUm.getApplicationRestrictions(paramString);
    }
    
    public boolean hasRestrictionsProvider()
      throws RemoteException
    {
      boolean bool = false;
      int i = UserHandle.getCallingUserId();
      if (this.mDpm != null)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          ComponentName localComponentName = this.mDpm.getRestrictionsProvider(i);
          if (localComponentName != null) {
            bool = true;
          }
          return bool;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
      return false;
    }
    
    public void notifyPermissionResponse(String paramString, PersistableBundle paramPersistableBundle)
      throws RemoteException
    {
      int i = Binder.getCallingUid();
      int j = UserHandle.getUserId(i);
      if (this.mDpm != null)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          localObject = this.mDpm.getRestrictionsProvider(j);
          if (localObject == null) {
            throw new SecurityException("No restrictions provider registered for user");
          }
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        enforceCallerMatchesPackage(i, ((ComponentName)localObject).getPackageName(), "Restrictions provider does not match caller ");
        Object localObject = new Intent("android.content.action.PERMISSION_RESPONSE_RECEIVED");
        ((Intent)localObject).setPackage(paramString);
        ((Intent)localObject).putExtra("android.content.extra.RESPONSE_BUNDLE", paramPersistableBundle);
        this.mContext.sendBroadcastAsUser((Intent)localObject, new UserHandle(j));
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void requestPermission(String paramString1, String paramString2, String paramString3, PersistableBundle paramPersistableBundle)
      throws RemoteException
    {
      int i = Binder.getCallingUid();
      int j = UserHandle.getUserId(i);
      if (this.mDpm != null)
      {
        long l = Binder.clearCallingIdentity();
        ComponentName localComponentName;
        try
        {
          localComponentName = this.mDpm.getRestrictionsProvider(j);
          if (localComponentName == null) {
            throw new IllegalStateException("Cannot request permission without a restrictions provider registered");
          }
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        enforceCallerMatchesPackage(i, paramString1, "Package name does not match caller ");
        Intent localIntent = new Intent("android.content.action.REQUEST_PERMISSION");
        localIntent.setComponent(localComponentName);
        localIntent.putExtra("android.content.extra.PACKAGE_NAME", paramString1);
        localIntent.putExtra("android.content.extra.REQUEST_TYPE", paramString2);
        localIntent.putExtra("android.content.extra.REQUEST_ID", paramString3);
        localIntent.putExtra("android.content.extra.REQUEST_BUNDLE", paramPersistableBundle);
        this.mContext.sendBroadcastAsUser(localIntent, new UserHandle(j));
        Binder.restoreCallingIdentity(l);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/restrictions/RestrictionsManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */