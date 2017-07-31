package android.app;

import android.content.Context;
import android.content.IIntentReceiver.Stub;
import android.content.IIntentSender;
import android.content.IIntentSender.Stub;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.AndroidException;

public final class PendingIntent
  implements Parcelable
{
  public static final Parcelable.Creator<PendingIntent> CREATOR = new Parcelable.Creator()
  {
    public PendingIntent createFromParcel(Parcel paramAnonymousParcel)
    {
      Object localObject = null;
      IBinder localIBinder = paramAnonymousParcel.readStrongBinder();
      paramAnonymousParcel = (Parcel)localObject;
      if (localIBinder != null) {
        paramAnonymousParcel = new PendingIntent(localIBinder);
      }
      return paramAnonymousParcel;
    }
    
    public PendingIntent[] newArray(int paramAnonymousInt)
    {
      return new PendingIntent[paramAnonymousInt];
    }
  };
  public static final int FLAG_CANCEL_CURRENT = 268435456;
  public static final int FLAG_IMMUTABLE = 67108864;
  public static final int FLAG_NO_CREATE = 536870912;
  public static final int FLAG_ONE_SHOT = 1073741824;
  public static final int FLAG_UPDATE_CURRENT = 134217728;
  private static final ThreadLocal<OnMarshaledListener> sOnMarshaledListener = new ThreadLocal();
  private final IIntentSender mTarget;
  
  PendingIntent(IIntentSender paramIIntentSender)
  {
    this.mTarget = paramIIntentSender;
  }
  
  PendingIntent(IBinder paramIBinder)
  {
    this.mTarget = IIntentSender.Stub.asInterface(paramIBinder);
  }
  
  public static PendingIntent getActivities(Context paramContext, int paramInt1, Intent[] paramArrayOfIntent, int paramInt2)
  {
    return getActivities(paramContext, paramInt1, paramArrayOfIntent, paramInt2, null);
  }
  
  public static PendingIntent getActivities(Context paramContext, int paramInt1, Intent[] paramArrayOfIntent, int paramInt2, Bundle paramBundle)
  {
    String str = paramContext.getPackageName();
    String[] arrayOfString = new String[paramArrayOfIntent.length];
    int i = 0;
    while (i < paramArrayOfIntent.length)
    {
      paramArrayOfIntent[i].migrateExtraStreamToClipData();
      paramArrayOfIntent[i].prepareToLeaveProcess(paramContext);
      arrayOfString[i] = paramArrayOfIntent[i].resolveTypeIfNeeded(paramContext.getContentResolver());
      i += 1;
    }
    try
    {
      paramContext = ActivityManagerNative.getDefault().getIntentSender(2, str, null, null, paramInt1, paramArrayOfIntent, arrayOfString, paramInt2, paramBundle, UserHandle.myUserId());
      if (paramContext != null)
      {
        paramContext = new PendingIntent(paramContext);
        return paramContext;
      }
      return null;
    }
    catch (RemoteException paramContext) {}
    return null;
  }
  
  public static PendingIntent getActivitiesAsUser(Context paramContext, int paramInt1, Intent[] paramArrayOfIntent, int paramInt2, Bundle paramBundle, UserHandle paramUserHandle)
  {
    String str = paramContext.getPackageName();
    String[] arrayOfString = new String[paramArrayOfIntent.length];
    int i = 0;
    while (i < paramArrayOfIntent.length)
    {
      paramArrayOfIntent[i].migrateExtraStreamToClipData();
      paramArrayOfIntent[i].prepareToLeaveProcess(paramContext);
      arrayOfString[i] = paramArrayOfIntent[i].resolveTypeIfNeeded(paramContext.getContentResolver());
      i += 1;
    }
    try
    {
      paramContext = ActivityManagerNative.getDefault().getIntentSender(2, str, null, null, paramInt1, paramArrayOfIntent, arrayOfString, paramInt2, paramBundle, paramUserHandle.getIdentifier());
      if (paramContext != null)
      {
        paramContext = new PendingIntent(paramContext);
        return paramContext;
      }
      return null;
    }
    catch (RemoteException paramContext) {}
    return null;
  }
  
  public static PendingIntent getActivity(Context paramContext, int paramInt1, Intent paramIntent, int paramInt2)
  {
    return getActivity(paramContext, paramInt1, paramIntent, paramInt2, null);
  }
  
  public static PendingIntent getActivity(Context paramContext, int paramInt1, Intent paramIntent, int paramInt2, Bundle paramBundle)
  {
    String str2 = paramContext.getPackageName();
    String str1;
    if (paramIntent != null) {
      str1 = paramIntent.resolveTypeIfNeeded(paramContext.getContentResolver());
    }
    try
    {
      paramIntent.migrateExtraStreamToClipData();
      paramIntent.prepareToLeaveProcess(paramContext);
      IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
      if (str1 != null)
      {
        paramContext = new String[1];
        paramContext[0] = str1;
      }
      for (;;)
      {
        int i = UserHandle.myUserId();
        paramContext = localIActivityManager.getIntentSender(2, str2, null, null, paramInt1, new Intent[] { paramIntent }, paramContext, paramInt2, paramBundle, i);
        if (paramContext == null) {
          break label109;
        }
        paramContext = new PendingIntent(paramContext);
        return paramContext;
        str1 = null;
        break;
        paramContext = null;
      }
      label109:
      return null;
    }
    catch (RemoteException paramContext) {}
    return null;
  }
  
  public static PendingIntent getActivityAsUser(Context paramContext, int paramInt1, Intent paramIntent, int paramInt2, Bundle paramBundle, UserHandle paramUserHandle)
  {
    String str2 = paramContext.getPackageName();
    String str1;
    if (paramIntent != null) {
      str1 = paramIntent.resolveTypeIfNeeded(paramContext.getContentResolver());
    }
    try
    {
      paramIntent.migrateExtraStreamToClipData();
      paramIntent.prepareToLeaveProcess(paramContext);
      IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
      if (str1 != null)
      {
        paramContext = new String[1];
        paramContext[0] = str1;
      }
      for (;;)
      {
        int i = paramUserHandle.getIdentifier();
        paramContext = localIActivityManager.getIntentSender(2, str2, null, null, paramInt1, new Intent[] { paramIntent }, paramContext, paramInt2, paramBundle, i);
        if (paramContext == null) {
          break label111;
        }
        paramContext = new PendingIntent(paramContext);
        return paramContext;
        str1 = null;
        break;
        paramContext = null;
      }
      label111:
      return null;
    }
    catch (RemoteException paramContext) {}
    return null;
  }
  
  public static PendingIntent getBroadcast(Context paramContext, int paramInt1, Intent paramIntent, int paramInt2)
  {
    return getBroadcastAsUser(paramContext, paramInt1, paramIntent, paramInt2, new UserHandle(UserHandle.myUserId()));
  }
  
  public static PendingIntent getBroadcastAsUser(Context paramContext, int paramInt1, Intent paramIntent, int paramInt2, UserHandle paramUserHandle)
  {
    String str2 = paramContext.getPackageName();
    String str1;
    if (paramIntent != null) {
      str1 = paramIntent.resolveTypeIfNeeded(paramContext.getContentResolver());
    }
    try
    {
      paramIntent.prepareToLeaveProcess(paramContext);
      IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
      if (str1 != null)
      {
        paramContext = new String[1];
        paramContext[0] = str1;
      }
      for (;;)
      {
        int i = paramUserHandle.getIdentifier();
        paramContext = localIActivityManager.getIntentSender(1, str2, null, null, paramInt1, new Intent[] { paramIntent }, paramContext, paramInt2, null, i);
        if (paramContext == null) {
          break label105;
        }
        paramContext = new PendingIntent(paramContext);
        return paramContext;
        str1 = null;
        break;
        paramContext = null;
      }
      label105:
      return null;
    }
    catch (RemoteException paramContext) {}
    return null;
  }
  
  public static PendingIntent getService(Context paramContext, int paramInt1, Intent paramIntent, int paramInt2)
  {
    String str2 = paramContext.getPackageName();
    String str1;
    if (paramIntent != null) {
      str1 = paramIntent.resolveTypeIfNeeded(paramContext.getContentResolver());
    }
    try
    {
      paramIntent.prepareToLeaveProcess(paramContext);
      IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
      if (str1 != null)
      {
        paramContext = new String[1];
        paramContext[0] = str1;
      }
      for (;;)
      {
        int i = UserHandle.myUserId();
        paramContext = localIActivityManager.getIntentSender(4, str2, null, null, paramInt1, new Intent[] { paramIntent }, paramContext, paramInt2, null, i);
        if (paramContext == null) {
          break label103;
        }
        paramContext = new PendingIntent(paramContext);
        return paramContext;
        str1 = null;
        break;
        paramContext = null;
      }
      label103:
      return null;
    }
    catch (RemoteException paramContext) {}
    return null;
  }
  
  public static PendingIntent readPendingIntentOrNullFromParcel(Parcel paramParcel)
  {
    Object localObject = null;
    IBinder localIBinder = paramParcel.readStrongBinder();
    paramParcel = (Parcel)localObject;
    if (localIBinder != null) {
      paramParcel = new PendingIntent(localIBinder);
    }
    return paramParcel;
  }
  
  public static void setOnMarshaledListener(OnMarshaledListener paramOnMarshaledListener)
  {
    sOnMarshaledListener.set(paramOnMarshaledListener);
  }
  
  public static void writePendingIntentOrNullToParcel(PendingIntent paramPendingIntent, Parcel paramParcel)
  {
    IBinder localIBinder = null;
    if (paramPendingIntent != null) {
      localIBinder = paramPendingIntent.mTarget.asBinder();
    }
    paramParcel.writeStrongBinder(localIBinder);
  }
  
  public void cancel()
  {
    try
    {
      ActivityManagerNative.getDefault().cancelIntentSender(this.mTarget);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof PendingIntent)) {
      return this.mTarget.asBinder().equals(((PendingIntent)paramObject).mTarget.asBinder());
    }
    return false;
  }
  
  public String getCreatorPackage()
  {
    try
    {
      String str = ActivityManagerNative.getDefault().getPackageForIntentSender(this.mTarget);
      return str;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public int getCreatorUid()
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getUidForIntentSender(this.mTarget);
      return i;
    }
    catch (RemoteException localRemoteException) {}
    return -1;
  }
  
  public UserHandle getCreatorUserHandle()
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getUidForIntentSender(this.mTarget);
      if (i > 0)
      {
        UserHandle localUserHandle = new UserHandle(UserHandle.getUserId(i));
        return localUserHandle;
      }
      return null;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public Intent getIntent()
  {
    try
    {
      Intent localIntent = ActivityManagerNative.getDefault().getIntentForIntentSender(this.mTarget);
      return localIntent;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public IntentSender getIntentSender()
  {
    return new IntentSender(this.mTarget);
  }
  
  public String getTag(String paramString)
  {
    try
    {
      paramString = ActivityManagerNative.getDefault().getTagForIntentSender(this.mTarget, paramString);
      return paramString;
    }
    catch (RemoteException paramString) {}
    return null;
  }
  
  public IIntentSender getTarget()
  {
    return this.mTarget;
  }
  
  @Deprecated
  public String getTargetPackage()
  {
    try
    {
      String str = ActivityManagerNative.getDefault().getPackageForIntentSender(this.mTarget);
      return str;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public int hashCode()
  {
    return this.mTarget.asBinder().hashCode();
  }
  
  public boolean isActivity()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isIntentSenderAnActivity(this.mTarget);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isTargetedToPackage()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isIntentSenderTargetedToPackage(this.mTarget);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public void send()
    throws PendingIntent.CanceledException
  {
    send(null, 0, null, null, null, null, null);
  }
  
  public void send(int paramInt)
    throws PendingIntent.CanceledException
  {
    send(null, paramInt, null, null, null, null, null);
  }
  
  public void send(int paramInt, OnFinished paramOnFinished, Handler paramHandler)
    throws PendingIntent.CanceledException
  {
    send(null, paramInt, null, paramOnFinished, paramHandler, null, null);
  }
  
  public void send(Context paramContext, int paramInt, Intent paramIntent)
    throws PendingIntent.CanceledException
  {
    send(paramContext, paramInt, paramIntent, null, null, null, null);
  }
  
  public void send(Context paramContext, int paramInt, Intent paramIntent, OnFinished paramOnFinished, Handler paramHandler)
    throws PendingIntent.CanceledException
  {
    send(paramContext, paramInt, paramIntent, paramOnFinished, paramHandler, null, null);
  }
  
  public void send(Context paramContext, int paramInt, Intent paramIntent, OnFinished paramOnFinished, Handler paramHandler, String paramString)
    throws PendingIntent.CanceledException
  {
    send(paramContext, paramInt, paramIntent, paramOnFinished, paramHandler, paramString, null);
  }
  
  public void send(Context paramContext, int paramInt, Intent paramIntent, OnFinished paramOnFinished, Handler paramHandler, String paramString, Bundle paramBundle)
    throws PendingIntent.CanceledException
  {
    if (paramIntent != null) {}
    for (;;)
    {
      try
      {
        paramContext = paramIntent.resolveTypeIfNeeded(paramContext.getContentResolver());
        IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
        IIntentSender localIIntentSender = this.mTarget;
        if (paramOnFinished == null) {
          break label87;
        }
        paramOnFinished = new FinishedDispatcher(this, paramOnFinished, paramHandler);
        if (localIActivityManager.sendIntentSender(localIIntentSender, paramInt, paramIntent, paramContext, paramOnFinished, paramString, paramBundle) >= 0) {
          break;
        }
        throw new CanceledException();
      }
      catch (RemoteException paramContext)
      {
        throw new CanceledException(paramContext);
      }
      paramContext = null;
      continue;
      label87:
      paramOnFinished = null;
    }
  }
  
  public String toString()
  {
    IBinder localIBinder = null;
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("PendingIntent{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(": ");
    if (this.mTarget != null) {
      localIBinder = this.mTarget.asBinder();
    }
    localStringBuilder.append(localIBinder);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.mTarget.asBinder());
    OnMarshaledListener localOnMarshaledListener = (OnMarshaledListener)sOnMarshaledListener.get();
    if (localOnMarshaledListener != null) {
      localOnMarshaledListener.onMarshaled(this, paramParcel, paramInt);
    }
  }
  
  public static class CanceledException
    extends AndroidException
  {
    public CanceledException() {}
    
    public CanceledException(Exception paramException)
    {
      super();
    }
    
    public CanceledException(String paramString)
    {
      super();
    }
  }
  
  private static class FinishedDispatcher
    extends IIntentReceiver.Stub
    implements Runnable
  {
    private static Handler sDefaultSystemHandler;
    private final Handler mHandler;
    private Intent mIntent;
    private final PendingIntent mPendingIntent;
    private int mResultCode;
    private String mResultData;
    private Bundle mResultExtras;
    private final PendingIntent.OnFinished mWho;
    
    FinishedDispatcher(PendingIntent paramPendingIntent, PendingIntent.OnFinished paramOnFinished, Handler paramHandler)
    {
      this.mPendingIntent = paramPendingIntent;
      this.mWho = paramOnFinished;
      if ((paramHandler == null) && (ActivityThread.isSystem()))
      {
        if (sDefaultSystemHandler == null) {
          sDefaultSystemHandler = new Handler(Looper.getMainLooper());
        }
        this.mHandler = sDefaultSystemHandler;
        return;
      }
      this.mHandler = paramHandler;
    }
    
    public void performReceive(Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
    {
      this.mIntent = paramIntent;
      this.mResultCode = paramInt1;
      this.mResultData = paramString;
      this.mResultExtras = paramBundle;
      if (this.mHandler == null)
      {
        run();
        return;
      }
      this.mHandler.post(this);
    }
    
    public void run()
    {
      this.mWho.onSendFinished(this.mPendingIntent, this.mIntent, this.mResultCode, this.mResultData, this.mResultExtras);
    }
  }
  
  public static abstract interface OnFinished
  {
    public abstract void onSendFinished(PendingIntent paramPendingIntent, Intent paramIntent, int paramInt, String paramString, Bundle paramBundle);
  }
  
  public static abstract interface OnMarshaledListener
  {
    public abstract void onMarshaled(PendingIntent paramPendingIntent, Parcel paramParcel, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/PendingIntent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */