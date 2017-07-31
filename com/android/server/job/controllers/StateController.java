package com.android.server.job.controllers;

import android.content.Context;
import com.android.server.job.StateChangedListener;
import java.io.PrintWriter;

public abstract class StateController
{
  protected static final boolean DEBUG = false;
  protected final Context mContext;
  protected final Object mLock;
  protected final StateChangedListener mStateChangedListener;
  
  public StateController(StateChangedListener paramStateChangedListener, Context paramContext, Object paramObject)
  {
    this.mStateChangedListener = paramStateChangedListener;
    this.mContext = paramContext;
    this.mLock = paramObject;
  }
  
  public abstract void dumpControllerStateLocked(PrintWriter paramPrintWriter, int paramInt);
  
  public abstract void maybeStartTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2);
  
  public abstract void maybeStopTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2, boolean paramBoolean);
  
  public void prepareForExecutionLocked(JobStatus paramJobStatus) {}
  
  public void rescheduleForFailure(JobStatus paramJobStatus1, JobStatus paramJobStatus2) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/controllers/StateController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */