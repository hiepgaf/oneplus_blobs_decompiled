package com.android.server.job;

import com.android.server.job.controllers.JobStatus;

public abstract interface JobCompletedListener
{
  public abstract void onJobCompleted(JobStatus paramJobStatus, boolean paramBoolean);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/JobCompletedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */