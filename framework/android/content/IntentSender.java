package android.content;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.AndroidException;

public class IntentSender
  implements Parcelable
{
  public static final Parcelable.Creator<IntentSender> CREATOR = new Parcelable.Creator()
  {
    public IntentSender createFromParcel(Parcel paramAnonymousParcel)
    {
      Object localObject = null;
      IBinder localIBinder = paramAnonymousParcel.readStrongBinder();
      paramAnonymousParcel = (Parcel)localObject;
      if (localIBinder != null) {
        paramAnonymousParcel = new IntentSender(localIBinder);
      }
      return paramAnonymousParcel;
    }
    
    public IntentSender[] newArray(int paramAnonymousInt)
    {
      return new IntentSender[paramAnonymousInt];
    }
  };
  private final IIntentSender mTarget;
  
  public IntentSender(IIntentSender paramIIntentSender)
  {
    this.mTarget = paramIIntentSender;
  }
  
  public IntentSender(IBinder paramIBinder)
  {
    this.mTarget = IIntentSender.Stub.asInterface(paramIBinder);
  }
  
  public static IntentSender readIntentSenderOrNullFromParcel(Parcel paramParcel)
  {
    Object localObject = null;
    IBinder localIBinder = paramParcel.readStrongBinder();
    paramParcel = (Parcel)localObject;
    if (localIBinder != null) {
      paramParcel = new IntentSender(localIBinder);
    }
    return paramParcel;
  }
  
  public static void writeIntentSenderOrNullToParcel(IntentSender paramIntentSender, Parcel paramParcel)
  {
    IBinder localIBinder = null;
    if (paramIntentSender != null) {
      localIBinder = paramIntentSender.mTarget.asBinder();
    }
    paramParcel.writeStrongBinder(localIBinder);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof IntentSender)) {
      return this.mTarget.asBinder().equals(((IntentSender)paramObject).mTarget.asBinder());
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
  
  public void sendIntent(Context paramContext, int paramInt, Intent paramIntent, OnFinished paramOnFinished, Handler paramHandler)
    throws IntentSender.SendIntentException
  {
    sendIntent(paramContext, paramInt, paramIntent, paramOnFinished, paramHandler, null);
  }
  
  public void sendIntent(Context paramContext, int paramInt, Intent paramIntent, OnFinished paramOnFinished, Handler paramHandler, String paramString)
    throws IntentSender.SendIntentException
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
          break label85;
        }
        paramOnFinished = new FinishedDispatcher(this, paramOnFinished, paramHandler);
        if (localIActivityManager.sendIntentSender(localIIntentSender, paramInt, paramIntent, paramContext, paramOnFinished, paramString, null) >= 0) {
          break;
        }
        throw new SendIntentException();
      }
      catch (RemoteException paramContext)
      {
        throw new SendIntentException();
      }
      paramContext = null;
      continue;
      label85:
      paramOnFinished = null;
    }
  }
  
  public String toString()
  {
    IBinder localIBinder = null;
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("IntentSender{");
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
  }
  
  private static class FinishedDispatcher
    extends IIntentReceiver.Stub
    implements Runnable
  {
    private final Handler mHandler;
    private Intent mIntent;
    private final IntentSender mIntentSender;
    private int mResultCode;
    private String mResultData;
    private Bundle mResultExtras;
    private final IntentSender.OnFinished mWho;
    
    FinishedDispatcher(IntentSender paramIntentSender, IntentSender.OnFinished paramOnFinished, Handler paramHandler)
    {
      this.mIntentSender = paramIntentSender;
      this.mWho = paramOnFinished;
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
      this.mWho.onSendFinished(this.mIntentSender, this.mIntent, this.mResultCode, this.mResultData, this.mResultExtras);
    }
  }
  
  public static abstract interface OnFinished
  {
    public abstract void onSendFinished(IntentSender paramIntentSender, Intent paramIntent, int paramInt, String paramString, Bundle paramBundle);
  }
  
  public static class SendIntentException
    extends AndroidException
  {
    public SendIntentException() {}
    
    public SendIntentException(Exception paramException)
    {
      super();
    }
    
    public SendIntentException(String paramString)
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/IntentSender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */