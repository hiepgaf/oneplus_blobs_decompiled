package com.android.server.am;

import android.app.IActivityContainer;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender.Stub;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.util.Slog;
import android.util.TimeUtils;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

final class PendingIntentRecord
  extends IIntentSender.Stub
{
  private static final String TAG = "ActivityManager";
  boolean canceled = false;
  final Key key;
  String lastTag;
  String lastTagPrefix;
  final ActivityManagerService owner;
  final WeakReference<PendingIntentRecord> ref;
  boolean sent = false;
  String stringName;
  final int uid;
  private long whitelistDuration = 0L;
  
  PendingIntentRecord(ActivityManagerService paramActivityManagerService, Key paramKey, int paramInt)
  {
    this.owner = paramActivityManagerService;
    this.key = paramKey;
    this.uid = paramInt;
    this.ref = new WeakReference(this);
  }
  
  public void completeFinalize()
  {
    synchronized (this.owner)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      if ((WeakReference)this.owner.mIntentSenderRecords.get(this.key) == this.ref) {
        this.owner.mIntentSenderRecords.remove(this.key);
      }
      ActivityManagerService.resetPriorityAfterLockedSection();
      return;
    }
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("uid=");
    paramPrintWriter.print(this.uid);
    paramPrintWriter.print(" packageName=");
    paramPrintWriter.print(this.key.packageName);
    paramPrintWriter.print(" type=");
    paramPrintWriter.print(this.key.typeName());
    paramPrintWriter.print(" flags=0x");
    paramPrintWriter.println(Integer.toHexString(this.key.flags));
    if ((this.key.activity != null) || (this.key.who != null))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("activity=");
      paramPrintWriter.print(this.key.activity);
      paramPrintWriter.print(" who=");
      paramPrintWriter.println(this.key.who);
    }
    if ((this.key.requestCode != 0) || (this.key.requestResolvedType != null))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("requestCode=");
      paramPrintWriter.print(this.key.requestCode);
      paramPrintWriter.print(" requestResolvedType=");
      paramPrintWriter.println(this.key.requestResolvedType);
    }
    if (this.key.requestIntent != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("requestIntent=");
      paramPrintWriter.println(this.key.requestIntent.toShortString(false, true, true, true));
    }
    if ((this.sent) || (this.canceled))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("sent=");
      paramPrintWriter.print(this.sent);
      paramPrintWriter.print(" canceled=");
      paramPrintWriter.println(this.canceled);
    }
    if (this.whitelistDuration != 0L)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("whitelistDuration=");
      TimeUtils.formatDuration(this.whitelistDuration, paramPrintWriter);
      paramPrintWriter.println();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (!this.canceled) {
        this.owner.mHandler.sendMessage(this.owner.mHandler.obtainMessage(23, this));
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void send(int paramInt, Intent paramIntent, String paramString1, IIntentReceiver paramIIntentReceiver, String paramString2, Bundle paramBundle)
  {
    sendInner(paramInt, paramIntent, paramString1, paramIIntentReceiver, paramString2, null, null, 0, 0, 0, paramBundle, null);
  }
  
  int sendInner(int paramInt1, Intent paramIntent, String paramString1, IIntentReceiver paramIIntentReceiver, String paramString2, IBinder paramIBinder, String paramString3, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle, IActivityContainer paramIActivityContainer)
  {
    if (paramIntent != null) {
      paramIntent.setDefusable(true);
    }
    if (paramBundle != null) {
      paramBundle.setDefusable(true);
    }
    if ((this.whitelistDuration <= 0L) || (this.canceled)) {}
    for (;;)
    {
      Object localObject;
      int i;
      long l;
      synchronized (this.owner)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        localObject = (ActivityStackSupervisor.ActivityContainer)paramIActivityContainer;
        if ((localObject != null) && (((ActivityStackSupervisor.ActivityContainer)localObject).mParentActivity != null))
        {
          localObject = ((ActivityStackSupervisor.ActivityContainer)localObject).mParentActivity.state;
          ActivityStack.ActivityState localActivityState = ActivityStack.ActivityState.RESUMED;
          if (localObject != localActivityState)
          {
            ActivityManagerService.resetPriorityAfterLockedSection();
            return -6;
            this.owner.tempWhitelistAppForPowerSave(Binder.getCallingPid(), Binder.getCallingUid(), this.uid, this.whitelistDuration);
            continue;
          }
        }
        if (!this.canceled)
        {
          this.sent = true;
          if ((this.key.flags & 0x40000000) != 0)
          {
            this.owner.cancelIntentSenderLocked(this, true);
            this.canceled = true;
          }
          if (this.key.requestIntent != null)
          {
            localObject = new Intent(this.key.requestIntent);
            if ((this.key.flags & 0x4000000) == 0) {
              break label439;
            }
            i = 1;
            if (i != 0) {
              break label456;
            }
            if (paramIntent == null) {
              break label445;
            }
            if ((((Intent)localObject).fillIn(paramIntent, this.key.flags) & 0x2) == 0) {
              paramString1 = this.key.requestResolvedType;
            }
            paramInt3 &= 0xFF3C;
            ((Intent)localObject).setFlags(((Intent)localObject).getFlags() & paramInt3 | paramInt4 & paramInt3);
            l = Binder.clearCallingIdentity();
            if (paramIIntentReceiver == null) {
              break label1074;
            }
            paramInt3 = 1;
            i = this.key.userId;
            paramInt4 = i;
            if (i == -2) {
              paramInt4 = this.owner.mUserController.getCurrentOrTargetUserIdLocked();
            }
            i = 0;
            int j = this.key.type;
            switch (j)
            {
            default: 
              paramInt4 = paramInt3;
              paramInt2 = i;
              if ((paramInt4 == 0) || (paramInt2 == -6)) {}
              break;
            }
          }
        }
      }
      try
      {
        paramIIntentReceiver.performReceive(new Intent((Intent)localObject), 0, null, null, false, false, this.key.userId);
        Binder.restoreCallingIdentity(l);
        ActivityManagerService.resetPriorityAfterLockedSection();
        return paramInt2;
        localObject = new Intent();
        continue;
        paramIntent = finally;
        ActivityManagerService.resetPriorityAfterLockedSection();
        throw paramIntent;
        label439:
        i = 0;
        continue;
        label445:
        paramString1 = this.key.requestResolvedType;
        continue;
        label456:
        paramString1 = this.key.requestResolvedType;
        continue;
        if (paramBundle == null) {
          paramIntent = this.key.options;
        }
        for (;;)
        {
          try
          {
            if ((this.key.allIntents == null) || (this.key.allIntents.length <= 1)) {
              break label700;
            }
            paramString2 = new Intent[this.key.allIntents.length];
            paramString3 = new String[this.key.allIntents.length];
            System.arraycopy(this.key.allIntents, 0, paramString2, 0, this.key.allIntents.length);
            if (this.key.allResolvedTypes != null) {
              System.arraycopy(this.key.allResolvedTypes, 0, paramString3, 0, this.key.allResolvedTypes.length);
            }
            paramString2[(paramString2.length - 1)] = localObject;
            paramString3[(paramString3.length - 1)] = paramString1;
            this.owner.startActivitiesInPackage(this.uid, this.key.packageName, paramString2, paramString3, paramIBinder, paramIntent, paramInt4);
            paramInt2 = i;
            paramInt4 = paramInt3;
          }
          catch (RuntimeException paramIntent)
          {
            Slog.w(TAG, "Unable to send startActivity intent", paramIntent);
            paramInt2 = i;
            paramInt4 = paramInt3;
          }
          break;
          paramIntent = paramBundle;
          if (this.key.options != null)
          {
            paramIntent = new Bundle(this.key.options);
            paramIntent.putAll(paramBundle);
          }
        }
        label700:
        paramString2 = paramString1;
        if (paramString1 == null)
        {
          paramString2 = ((Intent)localObject).resolveTypeIfNeeded(this.owner.mContext.getContentResolver());
          Slog.i(TAG, "sendInner # force resolve:resolvedType=" + paramString2 + ", finalIntent=" + localObject);
        }
        this.owner.startActivityInPackage(this.uid, this.key.packageName, (Intent)localObject, paramString2, paramIBinder, paramString3, paramInt2, 0, paramIntent, paramInt4, paramIActivityContainer, null);
        paramInt2 = i;
        paramInt4 = paramInt3;
        continue;
        paramInt2 = i;
        paramInt4 = paramInt3;
        if (this.key.activity.task.stack == null) {
          continue;
        }
        this.key.activity.task.stack.sendActivityResultLocked(-1, this.key.activity, this.key.who, this.key.requestCode, paramInt1, (Intent)localObject);
        paramInt2 = i;
        paramInt4 = paramInt3;
        continue;
        try
        {
          paramIntent = this.owner;
          paramIBinder = this.key.packageName;
          paramInt2 = this.uid;
          if (paramIIntentReceiver != null) {}
          for (boolean bool = true;; bool = false)
          {
            paramInt1 = paramIntent.broadcastIntentInPackage(paramIBinder, paramInt2, (Intent)localObject, paramString1, paramIIntentReceiver, paramInt1, null, null, paramString2, paramBundle, bool, false, paramInt4);
            paramInt2 = i;
            paramInt4 = paramInt3;
            if (paramInt1 != 0) {
              break;
            }
            paramInt4 = 0;
            paramInt2 = i;
            break;
          }
        }
        catch (RuntimeException paramIntent)
        {
          Slog.w(TAG, "Unable to send startActivity intent", paramIntent);
          paramInt2 = i;
          paramInt4 = paramInt3;
        }
        try
        {
          this.owner.startServiceInPackage(this.uid, (Intent)localObject, paramString1, this.key.packageName, paramInt4);
          paramInt2 = i;
          paramInt4 = paramInt3;
        }
        catch (RuntimeException paramIntent)
        {
          Slog.w(TAG, "Unable to send startService intent", paramIntent);
          paramInt2 = i;
          paramInt4 = paramInt3;
        }
        catch (TransactionTooLargeException paramIntent)
        {
          paramInt2 = -6;
          paramInt4 = paramInt3;
        }
        continue;
        ActivityManagerService.resetPriorityAfterLockedSection();
        return -6;
      }
      catch (RemoteException paramIntent)
      {
        for (;;) {}
      }
      label1074:
      paramInt3 = 0;
    }
  }
  
  public int sendWithResult(int paramInt, Intent paramIntent, String paramString1, IIntentReceiver paramIIntentReceiver, String paramString2, Bundle paramBundle)
  {
    return sendInner(paramInt, paramIntent, paramString1, paramIIntentReceiver, paramString2, null, null, 0, 0, 0, paramBundle, null);
  }
  
  void setWhitelistDuration(long paramLong)
  {
    this.whitelistDuration = paramLong;
    this.stringName = null;
  }
  
  public String toString()
  {
    if (this.stringName != null) {
      return this.stringName;
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("PendingIntentRecord{");
    ((StringBuilder)localObject).append(Integer.toHexString(System.identityHashCode(this)));
    ((StringBuilder)localObject).append(' ');
    ((StringBuilder)localObject).append(this.key.packageName);
    ((StringBuilder)localObject).append(' ');
    ((StringBuilder)localObject).append(this.key.typeName());
    if (this.whitelistDuration > 0L)
    {
      ((StringBuilder)localObject).append(" (whitelist: ");
      TimeUtils.formatDuration(this.whitelistDuration, (StringBuilder)localObject);
      ((StringBuilder)localObject).append(")");
    }
    ((StringBuilder)localObject).append('}');
    localObject = ((StringBuilder)localObject).toString();
    this.stringName = ((String)localObject);
    return (String)localObject;
  }
  
  static final class Key
  {
    private static final int ODD_PRIME_NUMBER = 37;
    final ActivityRecord activity;
    Intent[] allIntents;
    String[] allResolvedTypes;
    final int flags;
    final int hashCode;
    final Bundle options;
    final String packageName;
    final int requestCode;
    final Intent requestIntent;
    final String requestResolvedType;
    final int type;
    final int userId;
    final String who;
    
    Key(int paramInt1, String paramString1, ActivityRecord paramActivityRecord, String paramString2, int paramInt2, Intent[] paramArrayOfIntent, String[] paramArrayOfString, int paramInt3, Bundle paramBundle, int paramInt4)
    {
      this.type = paramInt1;
      this.packageName = paramString1;
      this.activity = paramActivityRecord;
      this.who = paramString2;
      this.requestCode = paramInt2;
      Object localObject1;
      if (paramArrayOfIntent != null)
      {
        localObject1 = paramArrayOfIntent[(paramArrayOfIntent.length - 1)];
        this.requestIntent = ((Intent)localObject1);
        localObject1 = localObject2;
        if (paramArrayOfString != null) {
          localObject1 = paramArrayOfString[(paramArrayOfString.length - 1)];
        }
        this.requestResolvedType = ((String)localObject1);
        this.allIntents = paramArrayOfIntent;
        this.allResolvedTypes = paramArrayOfString;
        this.flags = paramInt3;
        this.options = paramBundle;
        this.userId = paramInt4;
        paramInt3 = ((paramInt3 + 851) * 37 + paramInt2) * 37 + paramInt4;
        paramInt2 = paramInt3;
        if (paramString2 != null) {
          paramInt2 = paramInt3 * 37 + paramString2.hashCode();
        }
        paramInt3 = paramInt2;
        if (paramActivityRecord != null) {
          paramInt3 = paramInt2 * 37 + paramActivityRecord.hashCode();
        }
        paramInt2 = paramInt3;
        if (this.requestIntent != null) {
          paramInt2 = paramInt3 * 37 + this.requestIntent.filterHashCode();
        }
        paramInt3 = paramInt2;
        if (this.requestResolvedType != null) {
          paramInt3 = paramInt2 * 37 + this.requestResolvedType.hashCode();
        }
        if (paramString1 == null) {
          break label258;
        }
      }
      label258:
      for (paramInt2 = paramString1.hashCode();; paramInt2 = 0)
      {
        this.hashCode = ((paramInt3 * 37 + paramInt2) * 37 + paramInt1);
        return;
        localObject1 = null;
        break;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      try
      {
        paramObject = (Key)paramObject;
        if (this.type != ((Key)paramObject).type) {
          return false;
        }
        if (this.userId != ((Key)paramObject).userId) {
          return false;
        }
        if (!Objects.equals(this.packageName, ((Key)paramObject).packageName)) {
          return false;
        }
        if (this.activity != ((Key)paramObject).activity) {
          return false;
        }
        if (!Objects.equals(this.who, ((Key)paramObject).who)) {
          return false;
        }
        if (this.requestCode != ((Key)paramObject).requestCode) {
          return false;
        }
        if (this.requestIntent != ((Key)paramObject).requestIntent) {
          if (this.requestIntent != null)
          {
            if (!this.requestIntent.filterEquals(((Key)paramObject).requestIntent)) {
              return false;
            }
          }
          else if (((Key)paramObject).requestIntent != null) {
            return false;
          }
        }
        if (!Objects.equals(this.requestResolvedType, ((Key)paramObject).requestResolvedType)) {
          return false;
        }
        int i = this.flags;
        int j = ((Key)paramObject).flags;
        return i == j;
      }
      catch (ClassCastException paramObject) {}
      return false;
    }
    
    public int hashCode()
    {
      return this.hashCode;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Key{").append(typeName()).append(" pkg=").append(this.packageName).append(" intent=");
      if (this.requestIntent != null) {}
      for (String str = this.requestIntent.toShortString(false, true, false, false);; str = "<null>") {
        return str + " flags=0x" + Integer.toHexString(this.flags) + " u=" + this.userId + "}";
      }
    }
    
    String typeName()
    {
      switch (this.type)
      {
      default: 
        return Integer.toString(this.type);
      case 2: 
        return "startActivity";
      case 1: 
        return "broadcastIntent";
      case 4: 
        return "startService";
      }
      return "activityResult";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/PendingIntentRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */