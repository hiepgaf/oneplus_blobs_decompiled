package com.android.server.job.controllers;

import android.app.job.JobInfo;
import android.app.job.JobInfo.TriggerContentUri;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.StateChangedListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContentObserverController
  extends StateController
{
  private static final boolean DEBUG = false;
  private static final int MAX_URIS_REPORTED = 50;
  private static final String TAG = "JobScheduler.Content";
  private static final int URIS_URGENT_THRESHOLD = 40;
  private static volatile ContentObserverController sController;
  private static final Object sCreationLock = new Object();
  final Handler mHandler;
  SparseArray<ArrayMap<JobInfo.TriggerContentUri, ObserverInstance>> mObservers = new SparseArray();
  private final List<JobStatus> mTrackedTasks = new ArrayList();
  
  private ContentObserverController(StateChangedListener paramStateChangedListener, Context paramContext, Object paramObject)
  {
    super(paramStateChangedListener, paramContext, paramObject);
    this.mHandler = new Handler(paramContext.getMainLooper());
  }
  
  public static ContentObserverController get(JobSchedulerService paramJobSchedulerService)
  {
    synchronized (sCreationLock)
    {
      if (sController == null) {
        sController = new ContentObserverController(paramJobSchedulerService, paramJobSchedulerService.getContext(), paramJobSchedulerService.getLock());
      }
      return sController;
    }
  }
  
  public static ContentObserverController getForTesting(StateChangedListener paramStateChangedListener, Context paramContext)
  {
    return new ContentObserverController(paramStateChangedListener, paramContext, new Object());
  }
  
  public void dumpControllerStateLocked(PrintWriter paramPrintWriter, int paramInt)
  {
    paramPrintWriter.println("Content:");
    Object localObject1 = this.mTrackedTasks.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (JobStatus)((Iterator)localObject1).next();
      if (((JobStatus)localObject2).shouldDump(paramInt))
      {
        paramPrintWriter.print("  #");
        ((JobStatus)localObject2).printUniqueId(paramPrintWriter);
        paramPrintWriter.print(" from ");
        UserHandle.formatUid(paramPrintWriter, ((JobStatus)localObject2).getSourceUid());
        paramPrintWriter.println();
      }
    }
    int i1 = this.mObservers.size();
    if (i1 > 0)
    {
      paramPrintWriter.println("  Observers:");
      int i = 0;
      while (i < i1)
      {
        int j = this.mObservers.keyAt(i);
        localObject1 = (ArrayMap)this.mObservers.get(j);
        int i2 = ((ArrayMap)localObject1).size();
        j = 0;
        if (j < i2)
        {
          localObject2 = (ObserverInstance)((ArrayMap)localObject1).valueAt(j);
          int i3 = ((ObserverInstance)localObject2).mJobs.size();
          int n = 0;
          int k = 0;
          label179:
          int m = n;
          if (k < i3)
          {
            if (((JobInstance)((ObserverInstance)localObject2).mJobs.valueAt(k)).mJobStatus.shouldDump(paramInt)) {
              m = 1;
            }
          }
          else {
            if (m != 0) {
              break label239;
            }
          }
          for (;;)
          {
            j += 1;
            break;
            k += 1;
            break label179;
            label239:
            paramPrintWriter.print("    ");
            Object localObject3 = (JobInfo.TriggerContentUri)((ArrayMap)localObject1).keyAt(j);
            paramPrintWriter.print(((JobInfo.TriggerContentUri)localObject3).getUri());
            paramPrintWriter.print(" 0x");
            paramPrintWriter.print(Integer.toHexString(((JobInfo.TriggerContentUri)localObject3).getFlags()));
            paramPrintWriter.print(" (");
            paramPrintWriter.print(System.identityHashCode(localObject2));
            paramPrintWriter.println("):");
            paramPrintWriter.println("      Jobs:");
            k = 0;
            while (k < i3)
            {
              localObject3 = (JobInstance)((ObserverInstance)localObject2).mJobs.valueAt(k);
              paramPrintWriter.print("        #");
              ((JobInstance)localObject3).mJobStatus.printUniqueId(paramPrintWriter);
              paramPrintWriter.print(" from ");
              UserHandle.formatUid(paramPrintWriter, ((JobInstance)localObject3).mJobStatus.getSourceUid());
              if (((JobInstance)localObject3).mChangedAuthorities != null)
              {
                paramPrintWriter.println(":");
                if (((JobInstance)localObject3).mTriggerPending)
                {
                  paramPrintWriter.print("          Trigger pending: update=");
                  TimeUtils.formatDuration(((JobInstance)localObject3).mJobStatus.getTriggerContentUpdateDelay(), paramPrintWriter);
                  paramPrintWriter.print(", max=");
                  TimeUtils.formatDuration(((JobInstance)localObject3).mJobStatus.getTriggerContentMaxDelay(), paramPrintWriter);
                  paramPrintWriter.println();
                }
                paramPrintWriter.println("          Changed Authorities:");
                m = 0;
                while (m < ((JobInstance)localObject3).mChangedAuthorities.size())
                {
                  paramPrintWriter.print("          ");
                  paramPrintWriter.println((String)((JobInstance)localObject3).mChangedAuthorities.valueAt(m));
                  m += 1;
                }
                if (((JobInstance)localObject3).mChangedUris != null)
                {
                  paramPrintWriter.println("          Changed URIs:");
                  m = 0;
                  while (m < ((JobInstance)localObject3).mChangedUris.size())
                  {
                    paramPrintWriter.print("          ");
                    paramPrintWriter.println(((JobInstance)localObject3).mChangedUris.valueAt(m));
                    m += 1;
                  }
                }
              }
              else
              {
                paramPrintWriter.println();
              }
              k += 1;
            }
          }
        }
        i += 1;
      }
    }
  }
  
  public void maybeStartTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2)
  {
    if (paramJobStatus1.hasContentTriggerConstraint())
    {
      if (paramJobStatus1.contentObserverJobInstance == null) {
        paramJobStatus1.contentObserverJobInstance = new JobInstance(paramJobStatus1);
      }
      this.mTrackedTasks.add(paramJobStatus1);
      boolean bool = false;
      if (paramJobStatus1.contentObserverJobInstance.mChangedAuthorities != null) {
        bool = true;
      }
      if (paramJobStatus1.changedAuthorities != null)
      {
        bool = true;
        if (paramJobStatus1.contentObserverJobInstance.mChangedAuthorities == null) {
          paramJobStatus1.contentObserverJobInstance.mChangedAuthorities = new ArraySet();
        }
        Iterator localIterator = paramJobStatus1.changedAuthorities.iterator();
        Object localObject;
        while (localIterator.hasNext())
        {
          localObject = (String)localIterator.next();
          paramJobStatus1.contentObserverJobInstance.mChangedAuthorities.add(localObject);
        }
        if (paramJobStatus1.changedUris != null)
        {
          if (paramJobStatus1.contentObserverJobInstance.mChangedUris == null) {
            paramJobStatus1.contentObserverJobInstance.mChangedUris = new ArraySet();
          }
          localIterator = paramJobStatus1.changedUris.iterator();
          while (localIterator.hasNext())
          {
            localObject = (Uri)localIterator.next();
            paramJobStatus1.contentObserverJobInstance.mChangedUris.add(localObject);
          }
        }
        paramJobStatus1.changedAuthorities = null;
        paramJobStatus1.changedUris = null;
      }
      paramJobStatus1.changedAuthorities = null;
      paramJobStatus1.changedUris = null;
      paramJobStatus1.setContentTriggerConstraintSatisfied(bool);
    }
    if ((paramJobStatus2 != null) && (paramJobStatus2.contentObserverJobInstance != null))
    {
      paramJobStatus2.contentObserverJobInstance.detachLocked();
      paramJobStatus2.contentObserverJobInstance = null;
    }
  }
  
  public void maybeStopTrackingJobLocked(JobStatus paramJobStatus1, JobStatus paramJobStatus2, boolean paramBoolean)
  {
    if (paramJobStatus1.hasContentTriggerConstraint()) {
      if (paramJobStatus1.contentObserverJobInstance != null)
      {
        paramJobStatus1.contentObserverJobInstance.unscheduleLocked();
        if (paramJobStatus2 == null) {
          break label118;
        }
        if ((paramJobStatus1.contentObserverJobInstance != null) && (paramJobStatus1.contentObserverJobInstance.mChangedAuthorities != null))
        {
          if (paramJobStatus2.contentObserverJobInstance == null) {
            paramJobStatus2.contentObserverJobInstance = new JobInstance(paramJobStatus2);
          }
          paramJobStatus2.contentObserverJobInstance.mChangedAuthorities = paramJobStatus1.contentObserverJobInstance.mChangedAuthorities;
          paramJobStatus2.contentObserverJobInstance.mChangedUris = paramJobStatus1.contentObserverJobInstance.mChangedUris;
          paramJobStatus1.contentObserverJobInstance.mChangedAuthorities = null;
          paramJobStatus1.contentObserverJobInstance.mChangedUris = null;
        }
      }
    }
    for (;;)
    {
      this.mTrackedTasks.remove(paramJobStatus1);
      return;
      label118:
      paramJobStatus1.contentObserverJobInstance.detachLocked();
      paramJobStatus1.contentObserverJobInstance = null;
    }
  }
  
  public void prepareForExecutionLocked(JobStatus paramJobStatus)
  {
    if ((paramJobStatus.hasContentTriggerConstraint()) && (paramJobStatus.contentObserverJobInstance != null))
    {
      paramJobStatus.changedUris = paramJobStatus.contentObserverJobInstance.mChangedUris;
      paramJobStatus.changedAuthorities = paramJobStatus.contentObserverJobInstance.mChangedAuthorities;
      paramJobStatus.contentObserverJobInstance.mChangedUris = null;
      paramJobStatus.contentObserverJobInstance.mChangedAuthorities = null;
    }
  }
  
  public void rescheduleForFailure(JobStatus paramJobStatus1, JobStatus paramJobStatus2)
  {
    if ((paramJobStatus2.hasContentTriggerConstraint()) && (paramJobStatus1.hasContentTriggerConstraint())) {}
    synchronized (this.mLock)
    {
      paramJobStatus1.changedAuthorities = paramJobStatus2.changedAuthorities;
      paramJobStatus1.changedUris = paramJobStatus2.changedUris;
      return;
    }
  }
  
  final class JobInstance
  {
    ArraySet<String> mChangedAuthorities;
    ArraySet<Uri> mChangedUris;
    final Runnable mExecuteRunner;
    final JobStatus mJobStatus;
    final ArrayList<ContentObserverController.ObserverInstance> mMyObservers = new ArrayList();
    final Runnable mTimeoutRunner;
    boolean mTriggerPending;
    
    JobInstance(JobStatus paramJobStatus)
    {
      this.mJobStatus = paramJobStatus;
      this.mExecuteRunner = new ContentObserverController.TriggerRunnable(this);
      this.mTimeoutRunner = new ContentObserverController.TriggerRunnable(this);
      JobInfo.TriggerContentUri[] arrayOfTriggerContentUri = paramJobStatus.getJob().getTriggerContentUris();
      int j = paramJobStatus.getSourceUserId();
      Object localObject2 = (ArrayMap)ContentObserverController.this.mObservers.get(j);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayMap();
        ContentObserverController.this.mObservers.put(j, localObject1);
      }
      if (arrayOfTriggerContentUri != null)
      {
        int k = arrayOfTriggerContentUri.length;
        if (i < k)
        {
          JobInfo.TriggerContentUri localTriggerContentUri = arrayOfTriggerContentUri[i];
          ContentObserverController.ObserverInstance localObserverInstance = (ContentObserverController.ObserverInstance)((ArrayMap)localObject1).get(localTriggerContentUri);
          localObject2 = localObserverInstance;
          if (localObserverInstance == null)
          {
            localObject2 = new ContentObserverController.ObserverInstance(ContentObserverController.this, ContentObserverController.this.mHandler, localTriggerContentUri, paramJobStatus.getSourceUserId());
            ((ArrayMap)localObject1).put(localTriggerContentUri, localObject2);
            if ((localTriggerContentUri.getFlags() & 0x1) == 0) {
              break label243;
            }
          }
          label243:
          for (boolean bool = true;; bool = false)
          {
            ContentObserverController.this.mContext.getContentResolver().registerContentObserver(localTriggerContentUri.getUri(), bool, (ContentObserver)localObject2, j);
            ((ContentObserverController.ObserverInstance)localObject2).mJobs.add(this);
            this.mMyObservers.add(localObject2);
            i += 1;
            break;
          }
        }
      }
    }
    
    void detachLocked()
    {
      int j = this.mMyObservers.size();
      int i = 0;
      while (i < j)
      {
        ContentObserverController.ObserverInstance localObserverInstance = (ContentObserverController.ObserverInstance)this.mMyObservers.get(i);
        localObserverInstance.mJobs.remove(this);
        if (localObserverInstance.mJobs.size() == 0)
        {
          ContentObserverController.this.mContext.getContentResolver().unregisterContentObserver(localObserverInstance);
          ArrayMap localArrayMap = (ArrayMap)ContentObserverController.this.mObservers.get(localObserverInstance.mUserId);
          if (localArrayMap != null) {
            localArrayMap.remove(localObserverInstance.mUri);
          }
        }
        i += 1;
      }
    }
    
    void scheduleLocked()
    {
      if (!this.mTriggerPending)
      {
        this.mTriggerPending = true;
        ContentObserverController.this.mHandler.postDelayed(this.mTimeoutRunner, this.mJobStatus.getTriggerContentMaxDelay());
      }
      ContentObserverController.this.mHandler.removeCallbacks(this.mExecuteRunner);
      if (this.mChangedUris.size() >= 40)
      {
        ContentObserverController.this.mHandler.post(this.mExecuteRunner);
        return;
      }
      ContentObserverController.this.mHandler.postDelayed(this.mExecuteRunner, this.mJobStatus.getTriggerContentUpdateDelay());
    }
    
    void trigger()
    {
      int i = 0;
      int j = 0;
      synchronized (ContentObserverController.this.mLock)
      {
        if (this.mTriggerPending)
        {
          i = j;
          if (this.mJobStatus.setContentTriggerConstraintSatisfied(true)) {
            i = 1;
          }
          unscheduleLocked();
        }
        if (i != 0) {
          ContentObserverController.this.mStateChangedListener.onControllerStateChanged();
        }
        return;
      }
    }
    
    void unscheduleLocked()
    {
      if (this.mTriggerPending)
      {
        ContentObserverController.this.mHandler.removeCallbacks(this.mExecuteRunner);
        ContentObserverController.this.mHandler.removeCallbacks(this.mTimeoutRunner);
        this.mTriggerPending = false;
      }
    }
  }
  
  final class ObserverInstance
    extends ContentObserver
  {
    final ArraySet<ContentObserverController.JobInstance> mJobs = new ArraySet();
    final JobInfo.TriggerContentUri mUri;
    final int mUserId;
    
    public ObserverInstance(Handler paramHandler, JobInfo.TriggerContentUri paramTriggerContentUri, int paramInt)
    {
      super();
      this.mUri = paramTriggerContentUri;
      this.mUserId = paramInt;
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      synchronized (ContentObserverController.this.mLock)
      {
        int j = this.mJobs.size();
        int i = 0;
        while (i < j)
        {
          ContentObserverController.JobInstance localJobInstance = (ContentObserverController.JobInstance)this.mJobs.valueAt(i);
          if (localJobInstance.mChangedUris == null) {
            localJobInstance.mChangedUris = new ArraySet();
          }
          if (localJobInstance.mChangedUris.size() < 50) {
            localJobInstance.mChangedUris.add(paramUri);
          }
          if (localJobInstance.mChangedAuthorities == null) {
            localJobInstance.mChangedAuthorities = new ArraySet();
          }
          localJobInstance.mChangedAuthorities.add(paramUri.getAuthority());
          localJobInstance.scheduleLocked();
          i += 1;
        }
        return;
      }
    }
  }
  
  static final class TriggerRunnable
    implements Runnable
  {
    final ContentObserverController.JobInstance mInstance;
    
    TriggerRunnable(ContentObserverController.JobInstance paramJobInstance)
    {
      this.mInstance = paramJobInstance;
    }
    
    public void run()
    {
      this.mInstance.trigger();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/controllers/ContentObserverController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */