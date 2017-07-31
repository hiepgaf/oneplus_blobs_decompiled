package com.android.server.job;

import android.app.job.JobInfo;
import java.util.List;

public abstract interface JobSchedulerInternal
{
  public abstract List<JobInfo> getSystemScheduledPendingJobs();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/JobSchedulerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */