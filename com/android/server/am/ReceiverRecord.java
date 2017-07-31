package com.android.server.am;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Slog;

final class ReceiverRecord
  implements IBinder.DeathRecipient
{
  static final int BROADCAST_TIMEOUT_MSG_APP = 1;
  static final boolean DEBUG = false;
  static final String TAG = "ReceiverRecord";
  int FF_Flag = 0;
  boolean binderLinked = false;
  ProcessRecord curApp;
  final BroadcastAppHandler mHandler;
  Intent mIntent;
  Looper mLooper;
  BroadcastQueue mQueue;
  IBinder mReceiver;
  final ActivityManagerService mService;
  int pidSentTo;
  BroadcastRecord r;
  
  ReceiverRecord(ActivityManagerService paramActivityManagerService, BroadcastQueue paramBroadcastQueue, BroadcastRecord paramBroadcastRecord, ProcessRecord paramProcessRecord, IBinder paramIBinder, Intent paramIntent, Looper paramLooper, int paramInt)
  {
    this.mService = paramActivityManagerService;
    this.mQueue = paramBroadcastQueue;
    this.r = paramBroadcastRecord;
    this.curApp = paramProcessRecord;
    this.mReceiver = paramIBinder;
    this.mIntent = paramIntent;
    this.mLooper = paramLooper;
    this.mHandler = new BroadcastAppHandler(this.mLooper);
    this.pidSentTo = this.curApp.pid;
    this.FF_Flag = paramInt;
  }
  
  public void binderDied()
  {
    Slog.w("ReceiverRecord", "remote process died: " + this.pidSentTo + ", drop the sent broadcast: " + this.mIntent + " and cancel timeout. ReceiverRecord: " + this);
    cancelBroadcastTimeoutLocked();
  }
  
  final void broadcastTimeoutLocked(boolean paramBoolean)
  {
    label220:
    for (;;)
    {
      try
      {
        Object localObject1 = Thread.currentThread();
        if (this.curApp != null)
        {
          Slog.v("ReceiverRecord", "ReceiverRecordbroadcastTimeoutLocked : " + this + ", pid : " + this.curApp.pid + ", thread : " + ((Thread)localObject1).getName());
          Object localObject3 = null;
          localObject1 = localObject3;
          if (this.curApp != null)
          {
            localObject1 = localObject3;
            if (this.curApp.pid != 0)
            {
              if (this.r.intent == null) {
                break label220;
              }
              localObject1 = "Broadcast of " + this.r.intent.toString();
            }
          }
          if (localObject1 != null)
          {
            this.mHandler.post(new AppNotResponding(this.curApp, (String)localObject1));
            this.r = null;
            this.curApp = null;
            this.mIntent = null;
            this.mLooper = null;
          }
        }
        else
        {
          Slog.v("ReceiverRecord", "ReceiverRecordbroadcastTimeoutLocked : " + this + ", pid : null, thread : " + ((Thread)localObject1).getName());
          continue;
        }
        String str = "Broadcast of " + this.r;
      }
      finally {}
    }
  }
  
  final void cancelBroadcastTimeoutLocked()
  {
    try
    {
      this.mHandler.removeMessages(1, this);
      if ((this.binderLinked) && (this.mReceiver != null))
      {
        this.mReceiver.unlinkToDeath(this, 0);
        this.binderLinked = false;
      }
      this.r = null;
      this.curApp = null;
      this.mIntent = null;
      this.mLooper = null;
      return;
    }
    finally {}
  }
  
  public ProcessRecord getApp()
  {
    return this.curApp;
  }
  
  public IBinder getBinder()
  {
    return this.mReceiver;
  }
  
  public Intent getIntent()
  {
    return this.mIntent;
  }
  
  public void linkBinder()
    throws RemoteException
  {
    if (this.mReceiver != null)
    {
      this.mReceiver.linkToDeath(this, 0);
      this.binderLinked = true;
    }
  }
  
  final void setBroadcastTimeoutLocked(long paramLong)
  {
    try
    {
      Message localMessage = this.mHandler.obtainMessage(1, this);
      this.mHandler.sendMessageDelayed(localMessage, paramLong);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public String toString()
  {
    return "ReceiverRecord{" + Integer.toHexString(System.identityHashCode(this)) + " FF_Flag=" + this.FF_Flag + " mQueue.mQueueName=" + this.mQueue.mQueueName + " r= " + this.r + " " + " curApp " + this.curApp + "mIntent " + this.mIntent + " mHandler " + this.mHandler + "}";
  }
  
  private final class AppNotResponding
    implements Runnable
  {
    private final String mAnnotation;
    private final ProcessRecord mApp;
    
    public AppNotResponding(ProcessRecord paramProcessRecord, String paramString)
    {
      this.mApp = paramProcessRecord;
      this.mAnnotation = paramString;
    }
    
    public void run()
    {
      ReceiverRecord.this.mService.mAppErrors.appNotResponding(this.mApp, null, null, false, this.mAnnotation);
    }
  }
  
  private final class BroadcastAppHandler
    extends Handler
  {
    public BroadcastAppHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      ReceiverRecord.this.broadcastTimeoutLocked(true);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ReceiverRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */