package com.android.server;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import android.util.Slog;
import com.android.internal.telephony.IMms;
import com.android.internal.telephony.IMms.Stub;
import java.util.List;

public class MmsServiceBroker
  extends SystemService
{
  private static final Uri FAKE_MMS_DRAFT_URI = Uri.parse("content://mms/draft/0");
  private static final Uri FAKE_MMS_SENT_URI;
  private static final Uri FAKE_SMS_DRAFT_URI;
  private static final Uri FAKE_SMS_SENT_URI;
  private static final ComponentName MMS_SERVICE_COMPONENT = new ComponentName("com.android.mms.service", "com.android.mms.service.MmsService");
  private static final int MSG_TRY_CONNECTING = 1;
  private static final long RETRY_DELAY_ON_DISCONNECTION_MS = 3000L;
  private static final long SERVICE_CONNECTION_WAIT_TIME_MS = 4000L;
  private static final String TAG = "MmsServiceBroker";
  private volatile AppOpsManager mAppOpsManager = null;
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName arg1, IBinder paramAnonymousIBinder)
    {
      Slog.i("MmsServiceBroker", "MmsService connected");
      synchronized (MmsServiceBroker.this)
      {
        MmsServiceBroker.-set0(MmsServiceBroker.this, IMms.Stub.asInterface(paramAnonymousIBinder));
        MmsServiceBroker.this.notifyAll();
        return;
      }
    }
    
    public void onServiceDisconnected(ComponentName arg1)
    {
      Slog.i("MmsServiceBroker", "MmsService unexpectedly disconnected");
      synchronized (MmsServiceBroker.this)
      {
        MmsServiceBroker.-set0(MmsServiceBroker.this, null);
        MmsServiceBroker.this.notifyAll();
        MmsServiceBroker.-get4(MmsServiceBroker.this).sendMessageDelayed(MmsServiceBroker.-get4(MmsServiceBroker.this).obtainMessage(1), 3000L);
        return;
      }
    }
  };
  private final Handler mConnectionHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        Slog.e("MmsServiceBroker", "Unknown message");
        return;
      }
      MmsServiceBroker.-wrap3(MmsServiceBroker.this);
    }
  };
  private Context mContext;
  private volatile PackageManager mPackageManager = null;
  private volatile IMms mService;
  private final IMms mServiceStubForFailure = new IMms()
  {
    private void returnPendingIntentWithError(PendingIntent paramAnonymousPendingIntent)
    {
      try
      {
        paramAnonymousPendingIntent.send(MmsServiceBroker.-get5(MmsServiceBroker.this), 1, null);
        return;
      }
      catch (PendingIntent.CanceledException paramAnonymousPendingIntent)
      {
        Slog.e("MmsServiceBroker", "Failed to return pending intent result", paramAnonymousPendingIntent);
      }
    }
    
    public Uri addMultimediaMessageDraft(String paramAnonymousString, Uri paramAnonymousUri)
      throws RemoteException
    {
      return null;
    }
    
    public Uri addTextMessageDraft(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
      throws RemoteException
    {
      return null;
    }
    
    public boolean archiveStoredConversation(String paramAnonymousString, long paramAnonymousLong, boolean paramAnonymousBoolean)
      throws RemoteException
    {
      return false;
    }
    
    public IBinder asBinder()
    {
      return null;
    }
    
    public boolean deleteStoredConversation(String paramAnonymousString, long paramAnonymousLong)
      throws RemoteException
    {
      return false;
    }
    
    public boolean deleteStoredMessage(String paramAnonymousString, Uri paramAnonymousUri)
      throws RemoteException
    {
      return false;
    }
    
    public void downloadMessage(int paramAnonymousInt, String paramAnonymousString1, String paramAnonymousString2, Uri paramAnonymousUri, Bundle paramAnonymousBundle, PendingIntent paramAnonymousPendingIntent)
      throws RemoteException
    {
      returnPendingIntentWithError(paramAnonymousPendingIntent);
    }
    
    public boolean getAutoPersisting()
      throws RemoteException
    {
      return false;
    }
    
    public Bundle getCarrierConfigValues(int paramAnonymousInt)
      throws RemoteException
    {
      return null;
    }
    
    public Uri importMultimediaMessage(String paramAnonymousString1, Uri paramAnonymousUri, String paramAnonymousString2, long paramAnonymousLong, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
      throws RemoteException
    {
      return null;
    }
    
    public Uri importTextMessage(String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt, String paramAnonymousString3, long paramAnonymousLong, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
      throws RemoteException
    {
      return null;
    }
    
    public void sendMessage(int paramAnonymousInt, String paramAnonymousString1, Uri paramAnonymousUri, String paramAnonymousString2, Bundle paramAnonymousBundle, PendingIntent paramAnonymousPendingIntent)
      throws RemoteException
    {
      returnPendingIntentWithError(paramAnonymousPendingIntent);
    }
    
    public void sendStoredMessage(int paramAnonymousInt, String paramAnonymousString, Uri paramAnonymousUri, Bundle paramAnonymousBundle, PendingIntent paramAnonymousPendingIntent)
      throws RemoteException
    {
      returnPendingIntentWithError(paramAnonymousPendingIntent);
    }
    
    public void setAutoPersisting(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws RemoteException
    {}
    
    public boolean updateStoredMessageStatus(String paramAnonymousString, Uri paramAnonymousUri, ContentValues paramAnonymousContentValues)
      throws RemoteException
    {
      return false;
    }
  };
  private volatile TelephonyManager mTelephonyManager = null;
  
  static
  {
    FAKE_SMS_SENT_URI = Uri.parse("content://sms/sent/0");
    FAKE_MMS_SENT_URI = Uri.parse("content://mms/sent/0");
    FAKE_SMS_DRAFT_URI = Uri.parse("content://sms/draft/0");
  }
  
  public MmsServiceBroker(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mService = null;
  }
  
  private AppOpsManager getAppOpsManager()
  {
    if (this.mAppOpsManager == null) {
      this.mAppOpsManager = ((AppOpsManager)this.mContext.getSystemService("appops"));
    }
    return this.mAppOpsManager;
  }
  
  private String getCallingPackageName()
  {
    String[] arrayOfString = getPackageManager().getPackagesForUid(Binder.getCallingUid());
    if ((arrayOfString != null) && (arrayOfString.length > 0)) {
      return arrayOfString[0];
    }
    return "unknown";
  }
  
  private IMms getOrConnectService()
  {
    for (;;)
    {
      try
      {
        IMms localIMms;
        if (this.mService != null)
        {
          localIMms = this.mService;
          return localIMms;
        }
        Slog.w("MmsServiceBroker", "MmsService not connected. Try connecting...");
        this.mConnectionHandler.sendMessage(this.mConnectionHandler.obtainMessage(1));
        long l2 = SystemClock.elapsedRealtime();
        long l1 = 4000L;
        if (l1 <= 0L) {
          break;
        }
        try
        {
          wait(l1);
          if (this.mService != null)
          {
            localIMms = this.mService;
            return localIMms;
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          Slog.w("MmsServiceBroker", "Connection wait interrupted", localInterruptedException);
          continue;
        }
        l1 = l2 + 4000L - SystemClock.elapsedRealtime();
      }
      finally {}
    }
    Slog.e("MmsServiceBroker", "Can not connect to MmsService (timed out)");
    return null;
  }
  
  private PackageManager getPackageManager()
  {
    if (this.mPackageManager == null) {
      this.mPackageManager = this.mContext.getPackageManager();
    }
    return this.mPackageManager;
  }
  
  private IMms getServiceGuarded()
  {
    IMms localIMms = getOrConnectService();
    if (localIMms != null) {
      return localIMms;
    }
    return this.mServiceStubForFailure;
  }
  
  private TelephonyManager getTelephonyManager()
  {
    if (this.mTelephonyManager == null) {
      this.mTelephonyManager = ((TelephonyManager)this.mContext.getSystemService("phone"));
    }
    return this.mTelephonyManager;
  }
  
  /* Error */
  private void tryConnecting()
  {
    // Byte code:
    //   0: ldc 34
    //   2: ldc -31
    //   4: invokestatic 228	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   7: pop
    //   8: aload_0
    //   9: monitorenter
    //   10: aload_0
    //   11: getfield 75	com/android/server/MmsServiceBroker:mService	Lcom/android/internal/telephony/IMms;
    //   14: ifnull +14 -> 28
    //   17: ldc 34
    //   19: ldc -26
    //   21: invokestatic 233	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   24: pop
    //   25: aload_0
    //   26: monitorexit
    //   27: return
    //   28: new 235	android/content/Intent
    //   31: dup
    //   32: invokespecial 237	android/content/Intent:<init>	()V
    //   35: astore_1
    //   36: aload_1
    //   37: getstatic 112	com/android/server/MmsServiceBroker:MMS_SERVICE_COMPONENT	Landroid/content/ComponentName;
    //   40: invokevirtual 241	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   43: pop
    //   44: aload_0
    //   45: getfield 71	com/android/server/MmsServiceBroker:mContext	Landroid/content/Context;
    //   48: aload_1
    //   49: aload_0
    //   50: getfield 140	com/android/server/MmsServiceBroker:mConnection	Landroid/content/ServiceConnection;
    //   53: iconst_1
    //   54: invokevirtual 245	android/content/Context:bindService	(Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z
    //   57: ifne +11 -> 68
    //   60: ldc 34
    //   62: ldc -9
    //   64: invokestatic 212	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   67: pop
    //   68: aload_0
    //   69: monitorexit
    //   70: return
    //   71: astore_1
    //   72: ldc 34
    //   74: ldc -7
    //   76: aload_1
    //   77: invokestatic 251	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   80: pop
    //   81: goto -13 -> 68
    //   84: astore_1
    //   85: aload_0
    //   86: monitorexit
    //   87: aload_1
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	MmsServiceBroker
    //   35	14	1	localIntent	Intent
    //   71	6	1	localSecurityException	SecurityException
    //   84	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   44	68	71	java/lang/SecurityException
    //   10	25	84	finally
    //   28	44	84	finally
    //   44	68	84	finally
    //   72	81	84	finally
  }
  
  public void onStart()
  {
    publishBinderService("imms", new BinderService(null));
  }
  
  public void systemRunning()
  {
    Slog.i("MmsServiceBroker", "Delay connecting to MmsService until an API is called");
  }
  
  private final class BinderService
    extends IMms.Stub
  {
    private static final String PHONE_PACKAGE_NAME = "com.android.phone";
    
    private BinderService() {}
    
    private Uri adjustUriForUserAndGrantPermission(Uri paramUri, String paramString, int paramInt)
    {
      int i = UserHandle.getCallingUserId();
      Uri localUri = paramUri;
      if (i != 0) {
        localUri = ContentProvider.maybeAddUserId(paramUri, i);
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        MmsServiceBroker.-get5(MmsServiceBroker.this).grantUriPermission("com.android.phone", localUri, paramInt);
        paramUri = new Intent(paramString);
        paramUri = ((TelephonyManager)MmsServiceBroker.-get5(MmsServiceBroker.this).getSystemService("phone")).getCarrierPackageNamesForIntent(paramUri);
        if ((paramUri != null) && (paramUri.size() == 1)) {
          MmsServiceBroker.-get5(MmsServiceBroker.this).grantUriPermission((String)paramUri.get(0), localUri, paramInt);
        }
        return localUri;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public Uri addMultimediaMessageDraft(String paramString, Uri paramUri)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(15, Binder.getCallingUid(), paramString) != 0) {
        return MmsServiceBroker.-get0();
      }
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).addMultimediaMessageDraft(paramString, paramUri);
    }
    
    public Uri addTextMessageDraft(String paramString1, String paramString2, String paramString3)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(15, Binder.getCallingUid(), paramString1) != 0) {
        return MmsServiceBroker.-get2();
      }
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).addTextMessageDraft(paramString1, paramString2, paramString3);
    }
    
    public boolean archiveStoredConversation(String paramString, long paramLong, boolean paramBoolean)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(15, Binder.getCallingUid(), paramString) != 0) {
        return false;
      }
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).archiveStoredConversation(paramString, paramLong, paramBoolean);
    }
    
    public boolean deleteStoredConversation(String paramString, long paramLong)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(15, Binder.getCallingUid(), paramString) != 0) {
        return false;
      }
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).deleteStoredConversation(paramString, paramLong);
    }
    
    public boolean deleteStoredMessage(String paramString, Uri paramUri)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(15, Binder.getCallingUid(), paramString) != 0) {
        return false;
      }
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).deleteStoredMessage(paramString, paramUri);
    }
    
    public void downloadMessage(int paramInt, String paramString1, String paramString2, Uri paramUri, Bundle paramBundle, PendingIntent paramPendingIntent)
      throws RemoteException
    {
      Slog.d("MmsServiceBroker", "downloadMessage() by " + paramString1);
      MmsServiceBroker.-get5(MmsServiceBroker.this).enforceCallingPermission("android.permission.RECEIVE_MMS", "Download MMS message");
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(18, Binder.getCallingUid(), paramString1) != 0) {
        return;
      }
      paramUri = adjustUriForUserAndGrantPermission(paramUri, "android.service.carrier.CarrierMessagingService", 3);
      MmsServiceBroker.-wrap1(MmsServiceBroker.this).downloadMessage(paramInt, paramString1, paramString2, paramUri, paramBundle, paramPendingIntent);
    }
    
    public boolean getAutoPersisting()
      throws RemoteException
    {
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).getAutoPersisting();
    }
    
    public Bundle getCarrierConfigValues(int paramInt)
      throws RemoteException
    {
      Slog.d("MmsServiceBroker", "getCarrierConfigValues() by " + MmsServiceBroker.-wrap2(MmsServiceBroker.this));
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).getCarrierConfigValues(paramInt);
    }
    
    public Uri importMultimediaMessage(String paramString1, Uri paramUri, String paramString2, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(15, Binder.getCallingUid(), paramString1) != 0) {
        return MmsServiceBroker.-get1();
      }
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).importMultimediaMessage(paramString1, paramUri, paramString2, paramLong, paramBoolean1, paramBoolean2);
    }
    
    public Uri importTextMessage(String paramString1, String paramString2, int paramInt, String paramString3, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(15, Binder.getCallingUid(), paramString1) != 0) {
        return MmsServiceBroker.-get3();
      }
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).importTextMessage(paramString1, paramString2, paramInt, paramString3, paramLong, paramBoolean1, paramBoolean2);
    }
    
    public void sendMessage(int paramInt, String paramString1, Uri paramUri, String paramString2, Bundle paramBundle, PendingIntent paramPendingIntent)
      throws RemoteException
    {
      Slog.d("MmsServiceBroker", "sendMessage() by " + paramString1);
      MmsServiceBroker.-get5(MmsServiceBroker.this).enforceCallingPermission("android.permission.SEND_SMS", "Send MMS message");
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(20, Binder.getCallingUid(), paramString1) != 0) {
        return;
      }
      paramUri = adjustUriForUserAndGrantPermission(paramUri, "android.service.carrier.CarrierMessagingService", 1);
      MmsServiceBroker.-wrap1(MmsServiceBroker.this).sendMessage(paramInt, paramString1, paramUri, paramString2, paramBundle, paramPendingIntent);
    }
    
    public void sendStoredMessage(int paramInt, String paramString, Uri paramUri, Bundle paramBundle, PendingIntent paramPendingIntent)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(20, Binder.getCallingUid(), paramString) != 0) {
        return;
      }
      MmsServiceBroker.-wrap1(MmsServiceBroker.this).sendStoredMessage(paramInt, paramString, paramUri, paramBundle, paramPendingIntent);
    }
    
    public void setAutoPersisting(String paramString, boolean paramBoolean)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(15, Binder.getCallingUid(), paramString) != 0) {
        return;
      }
      MmsServiceBroker.-wrap1(MmsServiceBroker.this).setAutoPersisting(paramString, paramBoolean);
    }
    
    public boolean updateStoredMessageStatus(String paramString, Uri paramUri, ContentValues paramContentValues)
      throws RemoteException
    {
      if (MmsServiceBroker.-wrap0(MmsServiceBroker.this).noteOp(15, Binder.getCallingUid(), paramString) != 0) {
        return false;
      }
      return MmsServiceBroker.-wrap1(MmsServiceBroker.this).updateStoredMessageStatus(paramString, paramUri, paramContentValues);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/MmsServiceBroker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */