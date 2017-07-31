package com.android.server.job;

import com.android.server.job.controllers.JobStatus;

public abstract interface StateChangedListener
{
  public abstract void onControllerStateChanged();
  
  public abstract void onDeviceIdleStateChanged(boolean paramBoolean);
  
  public abstract void onRunJobNow(JobStatus paramJobStatus);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/StateChangedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */