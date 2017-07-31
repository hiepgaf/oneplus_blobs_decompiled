package com.android.server.job.controllers;

import android.app.usage.UsageStatsManagerInternal;
import android.app.usage.UsageStatsManagerInternal.AppIdleStateChangeListener;
import android.content.Context;
import android.os.UserHandle;
import com.android.server.LocalServices;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.JobStore;
import com.android.server.job.JobStore.JobStatusFunctor;
import com.android.server.job.StateChangedListener;
import java.io.PrintWriter;

public class AppIdleController
  extends StateController
{
  private static final boolean DEBUG = false;
  private static final String LOG_TAG = "AppIdleController";
  private static volatile AppIdleController sController;
  private static Object sCreationLock = new Object();
  boolean mAppIdleParoleOn;
  private boolean mInitializedParoleOn;
  private final JobSchedulerService mJobSchedulerService;
  private final UsageStatsManagerInternal mUsageStatsInternal;
  
  private AppIdleController(JobSchedulerService paramJobSchedulerService, Context paramContext, Object paramObject)
  {
    super(paramJobSchedulerService, paramContext, paramObject);
    this.mJobSchedulerService = paramJobSchedulerService;
    this.mUsageStatsInternal = ((UsageStatsManagerInternal)LocalServices.getService(UsageStatsManagerInternal.class));
    this.mAppIdleParoleOn = true;
    this.mUsageStatsInternal.addAppIdleStateChangeListener(new AppIdleStateChangeListener(null));
  }
  
  public static AppIdleController get(JobSchedulerService paramJobSchedulerService)
  {
    synchronized (sCreationLock)
    {
      if (sController == null) {
        sController = new AppIdleController(paramJobSchedulerService, paramJobSchedulerService.getContext(), paramJobSchedulerService.getLock());
      }
      paramJobSchedulerService = sController;
      return paramJobSchedulerService;
    }
  }
  
  public void dumpControllerStateLocked(final PrintWriter paramPrintWriter, final int paramInt)
  {
    paramPrintWriter.print("AppIdle: parole on = ");
    paramPrintWriter.println(this.mAppIdleParoleOn);
    this.mJobSchedulerService.getJobStore().forEachJob(new JobStore.JobStatusFunctor()
    {
      public void process(JobStatus paramAnonymousJobStatus)
      {
        if (!paramAnonymousJobStatus.shouldDump(paramInt)) {
          return;
        }
        paramPrintWriter.print("  #");
        paramAnonymousJobStatus.printUniqueId(paramPrintWriter);
        paramPrintWriter.print(" from ");
        UserHandle.formatUid(paramPrintWriter, paramAnonymousJobStatus.getSourceUid());
        paramPrintWriter.print(": ");
        paramPrintWriter.print(paramAnonymousJobStatus.getSourcePackageName());
        if ((paramAnonymousJobStatus.satisfiedConstraints & 0x40) != 0)
        {
          paramPrintWriter.println(" RUNNABLE");
          return;
        }
        paramPrintWriter.println(" WAITING");
      }
    });
  }
  
  public void maybeStartTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2)
  {
    boolean bool2 = false;
    if (!this.mInitializedParoleOn)
    {
      this.mInitializedParoleOn = true;
      this.mAppIdleParoleOn = this.mUsageStatsInternal.isAppIdleParoleOn();
    }
    paramJobStatus2 = paramJobStatus1.getSourcePackageName();
    if (!this.mAppIdleParoleOn)
    {
      bool1 = this.mUsageStatsInternal.isAppIdle(paramJobStatus2, paramJobStatus1.getSourceUid(), paramJobStatus1.getSourceUserId());
      if (!bool1) {
        break label74;
      }
    }
    label74:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      paramJobStatus1.setAppNotIdleConstraintSatisfied(bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  public void maybeStopTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2, boolean paramBoolean) {}
  
  void setAppIdleParoleOn(boolean paramBoolean)
  {
    int i = 0;
    synchronized (this.mLock)
    {
      boolean bool = this.mAppIdleParoleOn;
      if (bool == paramBoolean) {
        return;
      }
      this.mAppIdleParoleOn = paramBoolean;
      GlobalUpdateFunc localGlobalUpdateFunc = new GlobalUpdateFunc();
      this.mJobSchedulerService.getJobStore().forEachJob(localGlobalUpdateFunc);
      paramBoolean = localGlobalUpdateFunc.mChanged;
      if (paramBoolean) {
        i = 1;
      }
      if (i != 0) {
        this.mStateChangedListener.onControllerStateChanged();
      }
      return;
    }
  }
  
  private class AppIdleStateChangeListener
    extends UsageStatsManagerInternal.AppIdleStateChangeListener
  {
    private AppIdleStateChangeListener() {}
    
    public void onAppIdleStateChanged(String paramString, int paramInt, boolean paramBoolean)
    {
      int i = 0;
      synchronized (AppIdleController.this.mLock)
      {
        boolean bool = AppIdleController.this.mAppIdleParoleOn;
        if (bool) {
          return;
        }
        paramString = new AppIdleController.PackageUpdateFunc(paramInt, paramString, paramBoolean);
        AppIdleController.-get0(AppIdleController.this).getJobStore().forEachJob(paramString);
        paramBoolean = paramString.mChanged;
        paramInt = i;
        if (paramBoolean) {
          paramInt = 1;
        }
        if (paramInt != 0) {
          AppIdleController.this.mStateChangedListener.onControllerStateChanged();
        }
        return;
      }
    }
    
    public void onParoleStateChanged(boolean paramBoolean)
    {
      AppIdleController.this.setAppIdleParoleOn(paramBoolean);
    }
  }
  
  final class GlobalUpdateFunc
    implements JobStore.JobStatusFunctor
  {
    boolean mChanged;
    
    GlobalUpdateFunc() {}
    
    public void process(JobStatus paramJobStatus)
    {
      boolean bool2 = false;
      String str = paramJobStatus.getSourcePackageName();
      if (!AppIdleController.this.mAppIdleParoleOn)
      {
        bool1 = AppIdleController.-get1(AppIdleController.this).isAppIdle(str, paramJobStatus.getSourceUid(), paramJobStatus.getSourceUserId());
        if (!bool1) {
          break label64;
        }
      }
      label64:
      for (boolean bool1 = bool2;; bool1 = true)
      {
        if (paramJobStatus.setAppNotIdleConstraintSatisfied(bool1)) {
          this.mChanged = true;
        }
        return;
        bool1 = false;
        break;
      }
    }
  }
  
  static final class PackageUpdateFunc
    implements JobStore.JobStatusFunctor
  {
    boolean mChanged;
    final boolean mIdle;
    final String mPackage;
    final int mUserId;
    
    PackageUpdateFunc(int paramInt, String paramString, boolean paramBoolean)
    {
      this.mUserId = paramInt;
      this.mPackage = paramString;
      this.mIdle = paramBoolean;
    }
    
    public void process(JobStatus paramJobStatus)
    {
      if ((paramJobStatus.getSourcePackageName().equals(this.mPackage)) && (paramJobStatus.getSourceUserId() == this.mUserId)) {
        if (!this.mIdle) {
          break label48;
        }
      }
      label48:
      for (boolean bool = false;; bool = true)
      {
        if (paramJobStatus.setAppNotIdleConstraintSatisfied(bool)) {
          this.mChanged = true;
        }
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/controllers/AppIdleController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */