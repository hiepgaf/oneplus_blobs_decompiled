package com.android.server.telecom;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManagerInternal;
import android.content.pm.PackageManagerInternal.PackagesProvider;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.telecom.DefaultDialerManager;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.IntArray;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.telephony.SmsApplication;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.pm.UserManagerService;

public class TelecomLoaderService
  extends SystemService
{
  private static final String SERVICE_ACTION = "com.android.ITelecomService";
  private static final ComponentName SERVICE_COMPONENT = new ComponentName("com.android.server.telecom", "com.android.server.telecom.components.TelecomService");
  private static final String TAG = "TelecomLoaderService";
  private final Context mContext;
  @GuardedBy("mLock")
  private IntArray mDefaultDialerAppRequests;
  @GuardedBy("mLock")
  private IntArray mDefaultSimCallManagerRequests;
  @GuardedBy("mLock")
  private IntArray mDefaultSmsAppRequests;
  private final Object mLock = new Object();
  @GuardedBy("mLock")
  private TelecomServiceConnection mServiceConnection;
  
  public TelecomLoaderService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    registerDefaultAppProviders();
  }
  
  private void connectToTelecom()
  {
    synchronized (this.mLock)
    {
      if (this.mServiceConnection != null)
      {
        this.mContext.unbindService(this.mServiceConnection);
        this.mServiceConnection = null;
      }
      TelecomServiceConnection localTelecomServiceConnection = new TelecomServiceConnection(null);
      Intent localIntent = new Intent("com.android.ITelecomService");
      localIntent.setComponent(SERVICE_COMPONENT);
      if (this.mContext.bindServiceAsUser(localIntent, localTelecomServiceConnection, 67108929, UserHandle.SYSTEM)) {
        this.mServiceConnection = localTelecomServiceConnection;
      }
      return;
    }
  }
  
  private void registerCarrierConfigChangedReceiver()
  {
    BroadcastReceiver local5 = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (paramAnonymousIntent.getAction().equals("android.telephony.action.CARRIER_CONFIG_CHANGED"))
        {
          paramAnonymousContext = UserManagerService.getInstance().getUserIds();
          int i = 0;
          int j = paramAnonymousContext.length;
          while (i < j)
          {
            int k = paramAnonymousContext[i];
            TelecomLoaderService.-wrap1(TelecomLoaderService.this, this.val$packageManagerInternal, k);
            i += 1;
          }
        }
      }
    };
    this.mContext.registerReceiverAsUser(local5, UserHandle.ALL, new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED"), null, null);
  }
  
  private void registerDefaultAppNotifier()
  {
    final Object localObject = (PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class);
    final Uri localUri1 = Settings.Secure.getUriFor("sms_default_application");
    final Uri localUri2 = Settings.Secure.getUriFor("dialer_default_application");
    localObject = new ContentObserver(new Handler(Looper.getMainLooper()))
    {
      public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri, int paramAnonymousInt)
      {
        if (localUri1.equals(paramAnonymousUri))
        {
          paramAnonymousUri = SmsApplication.getDefaultSmsApplication(TelecomLoaderService.-get0(TelecomLoaderService.this), true);
          if (paramAnonymousUri != null) {
            localObject.grantDefaultPermissionsToDefaultSmsApp(paramAnonymousUri.getPackageName(), paramAnonymousInt);
          }
        }
        while (!localUri2.equals(paramAnonymousUri)) {
          return;
        }
        paramAnonymousUri = DefaultDialerManager.getDefaultDialerApplication(TelecomLoaderService.-get0(TelecomLoaderService.this));
        if (paramAnonymousUri != null) {
          localObject.grantDefaultPermissionsToDefaultDialerApp(paramAnonymousUri, paramAnonymousInt);
        }
        TelecomLoaderService.-wrap1(TelecomLoaderService.this, localObject, paramAnonymousInt);
      }
    };
    this.mContext.getContentResolver().registerContentObserver(localUri1, false, (ContentObserver)localObject, -1);
    this.mContext.getContentResolver().registerContentObserver(localUri2, false, (ContentObserver)localObject, -1);
  }
  
  private void registerDefaultAppProviders()
  {
    PackageManagerInternal localPackageManagerInternal = (PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class);
    localPackageManagerInternal.setSmsAppPackagesProvider(new PackageManagerInternal.PackagesProvider()
    {
      public String[] getPackages(int paramAnonymousInt)
      {
        synchronized (TelecomLoaderService.-get4(TelecomLoaderService.this))
        {
          if (TelecomLoaderService.-get5(TelecomLoaderService.this) == null)
          {
            if (TelecomLoaderService.-get3(TelecomLoaderService.this) == null) {
              TelecomLoaderService.-set2(TelecomLoaderService.this, new IntArray());
            }
            TelecomLoaderService.-get3(TelecomLoaderService.this).add(paramAnonymousInt);
            return null;
          }
          ??? = SmsApplication.getDefaultSmsApplication(TelecomLoaderService.-get0(TelecomLoaderService.this), true);
          if (??? != null) {
            return new String[] { ((ComponentName)???).getPackageName() };
          }
        }
        return null;
      }
    });
    localPackageManagerInternal.setDialerAppPackagesProvider(new PackageManagerInternal.PackagesProvider()
    {
      public String[] getPackages(int paramAnonymousInt)
      {
        synchronized (TelecomLoaderService.-get4(TelecomLoaderService.this))
        {
          if (TelecomLoaderService.-get5(TelecomLoaderService.this) == null)
          {
            if (TelecomLoaderService.-get1(TelecomLoaderService.this) == null) {
              TelecomLoaderService.-set0(TelecomLoaderService.this, new IntArray());
            }
            TelecomLoaderService.-get1(TelecomLoaderService.this).add(paramAnonymousInt);
            return null;
          }
          ??? = DefaultDialerManager.getDefaultDialerApplication(TelecomLoaderService.-get0(TelecomLoaderService.this));
          if (??? != null) {
            return new String[] { ??? };
          }
        }
        return null;
      }
    });
    localPackageManagerInternal.setSimCallManagerPackagesProvider(new PackageManagerInternal.PackagesProvider()
    {
      public String[] getPackages(int paramAnonymousInt)
      {
        synchronized (TelecomLoaderService.-get4(TelecomLoaderService.this))
        {
          if (TelecomLoaderService.-get5(TelecomLoaderService.this) == null)
          {
            if (TelecomLoaderService.-get2(TelecomLoaderService.this) == null) {
              TelecomLoaderService.-set1(TelecomLoaderService.this, new IntArray());
            }
            TelecomLoaderService.-get2(TelecomLoaderService.this).add(paramAnonymousInt);
            return null;
          }
          ??? = ((TelecomManager)TelecomLoaderService.-get0(TelecomLoaderService.this).getSystemService("telecom")).getSimCallManager(paramAnonymousInt);
          if (??? != null) {
            return new String[] { ((PhoneAccountHandle)???).getComponentName().getPackageName() };
          }
        }
        return null;
      }
    });
  }
  
  private void updateSimCallManagerPermissions(PackageManagerInternal paramPackageManagerInternal, int paramInt)
  {
    PhoneAccountHandle localPhoneAccountHandle = ((TelecomManager)this.mContext.getSystemService("telecom")).getSimCallManager(paramInt);
    if (localPhoneAccountHandle != null)
    {
      Slog.i("TelecomLoaderService", "updating sim call manager permissions for userId:" + paramInt);
      paramPackageManagerInternal.grantDefaultPermissionsToDefaultSimCallManager(localPhoneAccountHandle.getComponentName().getPackageName(), paramInt);
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 550)
    {
      registerDefaultAppNotifier();
      registerCarrierConfigChangedReceiver();
      connectToTelecom();
    }
  }
  
  public void onStart() {}
  
  private class TelecomServiceConnection
    implements ServiceConnection
  {
    private TelecomServiceConnection() {}
    
    public void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
    {
      try
      {
        paramIBinder.linkToDeath(new IBinder.DeathRecipient()
        {
          public void binderDied()
          {
            TelecomLoaderService.-wrap0(TelecomLoaderService.this);
          }
        }, 0);
        SmsApplication.getDefaultMmsApplication(TelecomLoaderService.-get0(TelecomLoaderService.this), false);
        ServiceManager.addService("telecom", paramIBinder);
        synchronized (TelecomLoaderService.-get4(TelecomLoaderService.this))
        {
          if ((TelecomLoaderService.-get3(TelecomLoaderService.this) != null) || (TelecomLoaderService.-get1(TelecomLoaderService.this) != null)) {}
          Object localObject;
          int i;
          do
          {
            paramIBinder = (PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class);
            if (TelecomLoaderService.-get3(TelecomLoaderService.this) == null) {
              break;
            }
            localObject = SmsApplication.getDefaultSmsApplication(TelecomLoaderService.-get0(TelecomLoaderService.this), true);
            if (localObject == null) {
              break;
            }
            i = TelecomLoaderService.-get3(TelecomLoaderService.this).size() - 1;
            while (i >= 0)
            {
              j = TelecomLoaderService.-get3(TelecomLoaderService.this).get(i);
              TelecomLoaderService.-get3(TelecomLoaderService.this).remove(i);
              paramIBinder.grantDefaultPermissionsToDefaultSmsApp(((ComponentName)localObject).getPackageName(), j);
              i -= 1;
            }
            paramIBinder = TelecomLoaderService.-get2(TelecomLoaderService.this);
          } while (paramIBinder != null);
          do
          {
            do
            {
              do
              {
                return;
                if (TelecomLoaderService.-get1(TelecomLoaderService.this) != null)
                {
                  localObject = DefaultDialerManager.getDefaultDialerApplication(TelecomLoaderService.-get0(TelecomLoaderService.this));
                  if (localObject != null)
                  {
                    i = TelecomLoaderService.-get1(TelecomLoaderService.this).size() - 1;
                    while (i >= 0)
                    {
                      j = TelecomLoaderService.-get1(TelecomLoaderService.this).get(i);
                      TelecomLoaderService.-get1(TelecomLoaderService.this).remove(i);
                      paramIBinder.grantDefaultPermissionsToDefaultDialerApp((String)localObject, j);
                      i -= 1;
                    }
                  }
                }
              } while (TelecomLoaderService.-get2(TelecomLoaderService.this) == null);
              localObject = ((TelecomManager)TelecomLoaderService.-get0(TelecomLoaderService.this).getSystemService("telecom")).getSimCallManager();
            } while (localObject == null);
            i = TelecomLoaderService.-get2(TelecomLoaderService.this).size();
            localObject = ((PhoneAccountHandle)localObject).getComponentName().getPackageName();
            i -= 1;
          } while (i < 0);
          int j = TelecomLoaderService.-get2(TelecomLoaderService.this).get(i);
          TelecomLoaderService.-get2(TelecomLoaderService.this).remove(i);
          paramIBinder.grantDefaultPermissionsToDefaultSimCallManager((String)localObject, j);
          i -= 1;
        }
        return;
      }
      catch (RemoteException ???)
      {
        Slog.w("TelecomLoaderService", "Failed linking to death.");
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      TelecomLoaderService.-wrap0(TelecomLoaderService.this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/telecom/TelecomLoaderService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */