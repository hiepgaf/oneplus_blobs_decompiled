package com.android.server.content;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;

public class SyncJobService
  extends JobService
{
  public static final String EXTRA_MESSENGER = "messenger";
  private static final String TAG = "SyncManager";
  private SparseArray<JobParameters> jobParamsMap = new SparseArray();
  private Messenger mMessenger;
  
  private void sendMessage(Message paramMessage)
  {
    if (this.mMessenger == null)
    {
      Slog.e("SyncManager", "Messenger not initialized.");
      return;
    }
    try
    {
      this.mMessenger.send(paramMessage);
      return;
    }
    catch (RemoteException paramMessage)
    {
      Slog.e("SyncManager", paramMessage.toString());
    }
  }
  
  public void callJobFinished(int paramInt, boolean paramBoolean)
  {
    synchronized (this.jobParamsMap)
    {
      JobParameters localJobParameters = (JobParameters)this.jobParamsMap.get(paramInt);
      if (localJobParameters != null)
      {
        jobFinished(localJobParameters, paramBoolean);
        this.jobParamsMap.remove(paramInt);
        return;
      }
      Slog.e("SyncManager", "Job params not found for " + String.valueOf(paramInt));
    }
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    this.mMessenger = ((Messenger)paramIntent.getParcelableExtra("messenger"));
    paramIntent = Message.obtain();
    paramIntent.what = 7;
    paramIntent.obj = this;
    sendMessage(paramIntent);
    return 2;
  }
  
  public boolean onStartJob(JobParameters paramJobParameters)
  {
    boolean bool = Log.isLoggable("SyncManager", 2);
    SyncOperation localSyncOperation;
    synchronized (this.jobParamsMap)
    {
      this.jobParamsMap.put(paramJobParameters.getJobId(), paramJobParameters);
      ??? = Message.obtain();
      ((Message)???).what = 10;
      localSyncOperation = SyncOperation.maybeCreateFromJobExtras(paramJobParameters.getExtras());
      if (localSyncOperation == null)
      {
        Slog.e("SyncManager", "Got invalid job " + paramJobParameters.getJobId());
        return false;
      }
    }
    if (bool) {
      Slog.v("SyncManager", "Got start job message " + localSyncOperation.target);
    }
    ((Message)???).obj = localSyncOperation;
    sendMessage((Message)???);
    return true;
  }
  
  public boolean onStopJob(JobParameters paramJobParameters)
  {
    int j = 1;
    if (Log.isLoggable("SyncManager", 2)) {
      Slog.v("SyncManager", "onStopJob called " + paramJobParameters.getJobId() + ", reason: " + paramJobParameters.getStopReason());
    }
    synchronized (this.jobParamsMap)
    {
      this.jobParamsMap.remove(paramJobParameters.getJobId());
      ??? = Message.obtain();
      ((Message)???).what = 11;
      ((Message)???).obj = SyncOperation.maybeCreateFromJobExtras(paramJobParameters.getExtras());
      if (((Message)???).obj == null) {
        return false;
      }
    }
    if (paramJobParameters.getStopReason() != 0)
    {
      i = 1;
      ((Message)???).arg1 = i;
      if (paramJobParameters.getStopReason() != 3) {
        break label158;
      }
    }
    label158:
    for (int i = j;; i = 0)
    {
      ((Message)???).arg2 = i;
      sendMessage((Message)???);
      return false;
      i = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/content/SyncJobService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */