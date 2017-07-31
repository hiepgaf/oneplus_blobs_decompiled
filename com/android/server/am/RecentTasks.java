package com.android.server.am;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.google.android.collect.Sets;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

class RecentTasks
  extends ArrayList<TaskRecord>
{
  private static final int DEFAULT_INITIAL_CAPACITY = 5;
  private static final int MAX_RECENT_BITMAPS = 3;
  private static final boolean MOVE_AFFILIATED_TASKS_TO_FRONT = false;
  private static final String TAG = "ActivityManager";
  private static final String TAG_RECENTS = TAG + ActivityManagerDebugConfig.POSTFIX_RECENTS;
  private static final String TAG_TASKS = TAG + ActivityManagerDebugConfig.POSTFIX_TASKS;
  private static Comparator<TaskRecord> sTaskRecordComparator = new Comparator()
  {
    public int compare(TaskRecord paramAnonymousTaskRecord1, TaskRecord paramAnonymousTaskRecord2)
    {
      return paramAnonymousTaskRecord2.taskId - paramAnonymousTaskRecord1.taskId;
    }
  };
  final SparseArray<SparseBooleanArray> mPersistedTaskIds = new SparseArray(5);
  private final ActivityManagerService mService;
  private final TaskPersister mTaskPersister;
  private final ActivityInfo mTmpActivityInfo = new ActivityInfo();
  private final ApplicationInfo mTmpAppInfo = new ApplicationInfo();
  private final HashMap<ComponentName, ActivityInfo> mTmpAvailActCache = new HashMap();
  private final HashMap<String, ApplicationInfo> mTmpAvailAppCache = new HashMap();
  private final ArrayList<TaskRecord> mTmpRecents = new ArrayList();
  private final SparseBooleanArray mUsersWithRecentsLoaded = new SparseBooleanArray(5);
  
  RecentTasks(ActivityManagerService paramActivityManagerService, ActivityStackSupervisor paramActivityStackSupervisor)
  {
    File localFile = Environment.getDataSystemDirectory();
    this.mService = paramActivityManagerService;
    this.mTaskPersister = new TaskPersister(localFile, paramActivityStackSupervisor, paramActivityManagerService, this);
    paramActivityStackSupervisor.setRecentTasks(this);
  }
  
  private void loadPersistedTaskIdsForUserLocked(int paramInt)
  {
    if (this.mPersistedTaskIds.get(paramInt) == null)
    {
      this.mPersistedTaskIds.put(paramInt, this.mTaskPersister.loadPersistedTaskIdsForUser(paramInt));
      Slog.i(TAG, "Loaded persisted task ids for user " + paramInt);
    }
  }
  
  private final boolean moveAffiliatedTasksToFront(TaskRecord paramTaskRecord, int paramInt)
  {
    int i1 = size();
    TaskRecord localTaskRecord1 = paramTaskRecord;
    int j = paramInt;
    while ((localTaskRecord1.mNextAffiliate != null) && (j > 0))
    {
      localTaskRecord1 = localTaskRecord1.mNextAffiliate;
      j -= 1;
    }
    if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
      Slog.d(TAG_RECENTS, "addRecent: adding affilliates starting at " + j + " from intial " + paramInt);
    }
    int k;
    int m;
    Object localObject;
    label106:
    int n;
    int i;
    TaskRecord localTaskRecord2;
    if (localTaskRecord1.mAffiliatedTaskId == paramTaskRecord.mAffiliatedTaskId)
    {
      k = 1;
      m = j;
      localObject = localTaskRecord1;
      n = m;
      i = k;
      if (m < i1)
      {
        localTaskRecord2 = (TaskRecord)get(m);
        if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
          Slog.d(TAG_RECENTS, "addRecent: looking at next chain @" + m + " " + localTaskRecord2);
        }
        if (localTaskRecord2 != localTaskRecord1) {
          break label401;
        }
        if ((localTaskRecord2.mNextAffiliate == null) && (localTaskRecord2.mNextAffiliateTaskId == -1)) {
          break label516;
        }
        Slog.wtf(TAG, "Bad chain @" + m + ": first task has next affiliate: " + localObject);
        i = 0;
        n = m;
      }
    }
    for (;;)
    {
      k = i;
      if (i != 0)
      {
        k = i;
        if (n < paramInt)
        {
          Slog.wtf(TAG, "Bad chain @" + n + ": did not extend to task " + paramTaskRecord + " @" + paramInt);
          k = 0;
        }
      }
      if (k == 0) {
        break label907;
      }
      paramInt = j;
      while (paramInt <= n)
      {
        if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
          Slog.d(TAG_RECENTS, "addRecent: moving affiliated " + paramTaskRecord + " from " + paramInt + " to " + (paramInt - j));
        }
        add(paramInt - j, (TaskRecord)remove(paramInt));
        paramInt += 1;
      }
      k = 0;
      break;
      label401:
      if ((localTaskRecord2.mNextAffiliate != localObject) || (localTaskRecord2.mNextAffiliateTaskId != ((TaskRecord)localObject).taskId))
      {
        Slog.wtf(TAG, "Bad chain @" + m + ": middle task " + localTaskRecord2 + " @" + m + " has bad next affiliate " + localTaskRecord2.mNextAffiliate + " id " + localTaskRecord2.mNextAffiliateTaskId + ", expected " + localObject);
        i = 0;
        n = m;
      }
      else
      {
        label516:
        if (localTaskRecord2.mPrevAffiliateTaskId == -1)
        {
          if (localTaskRecord2.mPrevAffiliate != null)
          {
            Slog.wtf(TAG, "Bad chain @" + m + ": last task " + localTaskRecord2 + " has previous affiliate " + localTaskRecord2.mPrevAffiliate);
            k = 0;
          }
          n = m;
          i = k;
          if (ActivityManagerDebugConfig.DEBUG_RECENTS)
          {
            Slog.d(TAG_RECENTS, "addRecent: end of chain @" + m);
            n = m;
            i = k;
          }
        }
        else if (localTaskRecord2.mPrevAffiliate == null)
        {
          Slog.wtf(TAG, "Bad chain @" + m + ": task " + localTaskRecord2 + " has previous affiliate " + localTaskRecord2.mPrevAffiliate + " but should be id " + localTaskRecord2.mPrevAffiliate);
          i = 0;
          n = m;
        }
        else if (localTaskRecord2.mAffiliatedTaskId != paramTaskRecord.mAffiliatedTaskId)
        {
          Slog.wtf(TAG, "Bad chain @" + m + ": task " + localTaskRecord2 + " has affiliated id " + localTaskRecord2.mAffiliatedTaskId + " but should be " + paramTaskRecord.mAffiliatedTaskId);
          i = 0;
          n = m;
        }
        else
        {
          localObject = localTaskRecord2;
          n = m + 1;
          m = n;
          if (n < i1) {
            break label106;
          }
          Slog.wtf(TAG, "Bad chain ran off index " + n + ": last task " + localTaskRecord2);
          i = 0;
        }
      }
    }
    if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
      Slog.d(TAG_RECENTS, "addRecent: done moving tasks  " + j + " to " + n);
    }
    return true;
    label907:
    return false;
  }
  
  private int processNextAffiliateChainLocked(int paramInt)
  {
    TaskRecord localTaskRecord1 = (TaskRecord)get(paramInt);
    int j = localTaskRecord1.mAffiliatedTaskId;
    if ((localTaskRecord1.taskId == j) && (localTaskRecord1.mPrevAffiliate == null) && (localTaskRecord1.mNextAffiliate == null))
    {
      localTaskRecord1.inRecents = true;
      return paramInt + 1;
    }
    this.mTmpRecents.clear();
    int i = size() - 1;
    while (i >= paramInt)
    {
      localTaskRecord1 = (TaskRecord)get(i);
      if (localTaskRecord1.mAffiliatedTaskId == j)
      {
        remove(i);
        this.mTmpRecents.add(localTaskRecord1);
      }
      i -= 1;
    }
    Collections.sort(this.mTmpRecents, sTaskRecordComparator);
    localTaskRecord1 = (TaskRecord)this.mTmpRecents.get(0);
    localTaskRecord1.inRecents = true;
    if (localTaskRecord1.mNextAffiliate != null)
    {
      Slog.w(TAG, "Link error 1 first.next=" + localTaskRecord1.mNextAffiliate);
      localTaskRecord1.setNextAffiliate(null);
      notifyTaskPersisterLocked(localTaskRecord1, false);
    }
    j = this.mTmpRecents.size();
    i = 0;
    while (i < j - 1)
    {
      localTaskRecord1 = (TaskRecord)this.mTmpRecents.get(i);
      TaskRecord localTaskRecord2 = (TaskRecord)this.mTmpRecents.get(i + 1);
      if (localTaskRecord1.mPrevAffiliate != localTaskRecord2)
      {
        Slog.w(TAG, "Link error 2 next=" + localTaskRecord1 + " prev=" + localTaskRecord1.mPrevAffiliate + " setting prev=" + localTaskRecord2);
        localTaskRecord1.setPrevAffiliate(localTaskRecord2);
        notifyTaskPersisterLocked(localTaskRecord1, false);
      }
      if (localTaskRecord2.mNextAffiliate != localTaskRecord1)
      {
        Slog.w(TAG, "Link error 3 prev=" + localTaskRecord2 + " next=" + localTaskRecord2.mNextAffiliate + " setting next=" + localTaskRecord1);
        localTaskRecord2.setNextAffiliate(localTaskRecord1);
        notifyTaskPersisterLocked(localTaskRecord2, false);
      }
      localTaskRecord2.inRecents = true;
      i += 1;
    }
    localTaskRecord1 = (TaskRecord)this.mTmpRecents.get(j - 1);
    if (localTaskRecord1.mPrevAffiliate != null)
    {
      Slog.w(TAG, "Link error 4 last.prev=" + localTaskRecord1.mPrevAffiliate);
      localTaskRecord1.setPrevAffiliate(null);
      notifyTaskPersisterLocked(localTaskRecord1, false);
    }
    addAll(paramInt, this.mTmpRecents);
    this.mTmpRecents.clear();
    return paramInt + j;
  }
  
  private void syncPersistentTaskIdsLocked()
  {
    int i = this.mPersistedTaskIds.size() - 1;
    while (i >= 0)
    {
      int j = this.mPersistedTaskIds.keyAt(i);
      if (this.mUsersWithRecentsLoaded.get(j)) {
        ((SparseBooleanArray)this.mPersistedTaskIds.valueAt(i)).clear();
      }
      i -= 1;
    }
    i = size() - 1;
    if (i >= 0)
    {
      TaskRecord localTaskRecord = (TaskRecord)get(i);
      if ((!localTaskRecord.isPersistable) || ((localTaskRecord.stack != null) && (localTaskRecord.stack.isHomeStack()))) {}
      for (;;)
      {
        i -= 1;
        break;
        if (this.mPersistedTaskIds.get(localTaskRecord.userId) == null)
        {
          Slog.wtf(TAG, "No task ids found for userId " + localTaskRecord.userId + ". task=" + localTaskRecord + " mPersistedTaskIds=" + this.mPersistedTaskIds);
          this.mPersistedTaskIds.put(localTaskRecord.userId, new SparseBooleanArray());
        }
        ((SparseBooleanArray)this.mPersistedTaskIds.get(localTaskRecord.userId)).put(localTaskRecord.taskId, true);
      }
    }
  }
  
  private void unloadUserRecentsLocked(int paramInt)
  {
    if (this.mUsersWithRecentsLoaded.get(paramInt))
    {
      Slog.i(TAG, "Unloading recents for user " + paramInt + " from memory.");
      this.mUsersWithRecentsLoaded.delete(paramInt);
      removeTasksForUserLocked(paramInt);
    }
  }
  
  final void addLocked(TaskRecord paramTaskRecord)
  {
    int k;
    if ((paramTaskRecord.mAffiliatedTaskId != paramTaskRecord.taskId) || (paramTaskRecord.mNextAffiliateTaskId != -1)) {
      k = 1;
    }
    for (;;)
    {
      i = size();
      if (paramTaskRecord.voiceSession == null) {
        break;
      }
      if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
        Slog.d(TAG_RECENTS, "addRecent: not adding voice interaction " + paramTaskRecord);
      }
      return;
      if (paramTaskRecord.mPrevAffiliateTaskId != -1) {
        k = 1;
      } else {
        k = 0;
      }
    }
    if ((k == 0) && (i > 0) && (get(0) == paramTaskRecord))
    {
      if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
        Slog.d(TAG_RECENTS, "addRecent: already at top: " + paramTaskRecord);
      }
      return;
    }
    if ((k != 0) && (i > 0) && (paramTaskRecord.inRecents) && (paramTaskRecord.mAffiliatedTaskId == ((TaskRecord)get(0)).mAffiliatedTaskId))
    {
      if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
        Slog.d(TAG_RECENTS, "addRecent: affiliated " + get(0) + " at top when adding " + paramTaskRecord);
      }
      return;
    }
    int i = 0;
    if (paramTaskRecord.inRecents)
    {
      i = indexOf(paramTaskRecord);
      if (i < 0) {
        break label395;
      }
      if (k != 0)
      {
        if (!moveAffiliatedTasksToFront(paramTaskRecord, i)) {}
      }
      else
      {
        remove(i);
        add(0, paramTaskRecord);
        notifyTaskPersisterLocked(paramTaskRecord, false);
        if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
          Slog.d(TAG_RECENTS, "addRecent: moving to top " + paramTaskRecord + " from " + i);
        }
        return;
      }
    }
    int j;
    for (i = 1;; i = 1)
    {
      if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
        Slog.d(TAG_RECENTS, "addRecent: trimming tasks for " + paramTaskRecord);
      }
      trimForTaskLocked(paramTaskRecord, true);
      j = size();
      int m = ActivityManager.getMaxRecentTasksStatic();
      while (j >= m)
      {
        ((TaskRecord)remove(j - 1)).removedFromRecents();
        j -= 1;
      }
      label395:
      Slog.wtf(TAG, "Task with inRecent not in recents: " + paramTaskRecord);
    }
    paramTaskRecord.inRecents = true;
    if ((k == 0) || (i != 0))
    {
      add(0, paramTaskRecord);
      j = i;
      if (ActivityManagerDebugConfig.DEBUG_RECENTS)
      {
        Slog.d(TAG_RECENTS, "addRecent: adding " + paramTaskRecord);
        j = i;
      }
    }
    for (;;)
    {
      if (j != 0)
      {
        if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
          Slog.d(TAG_RECENTS, "addRecent: regrouping affiliations");
        }
        cleanupLocked(paramTaskRecord.userId);
      }
      return;
      j = i;
      if (k != 0)
      {
        TaskRecord localTaskRecord2 = paramTaskRecord.mNextAffiliate;
        TaskRecord localTaskRecord1 = localTaskRecord2;
        if (localTaskRecord2 == null) {
          localTaskRecord1 = paramTaskRecord.mPrevAffiliate;
        }
        if (localTaskRecord1 != null)
        {
          i = indexOf(localTaskRecord1);
          if (i >= 0)
          {
            if (localTaskRecord1 == paramTaskRecord.mNextAffiliate) {
              i += 1;
            }
            for (;;)
            {
              if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
                Slog.d(TAG_RECENTS, "addRecent: new affiliated task added at " + i + ": " + paramTaskRecord);
              }
              add(i, paramTaskRecord);
              if (!moveAffiliatedTasksToFront(paramTaskRecord, i)) {
                break;
              }
              return;
            }
            j = 1;
          }
          else
          {
            if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
              Slog.d(TAG_RECENTS, "addRecent: couldn't find other affiliation " + localTaskRecord1);
            }
            j = 1;
          }
        }
        else
        {
          if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
            Slog.d(TAG_RECENTS, "addRecent: adding affiliated task without next/prev:" + paramTaskRecord);
          }
          j = 1;
        }
      }
    }
  }
  
  void cleanupLocked(int paramInt)
  {
    int i = size();
    if (i == 0) {
      return;
    }
    IPackageManager localIPackageManager = AppGlobals.getPackageManager();
    i -= 1;
    TaskRecord localTaskRecord;
    if (i >= 0)
    {
      localTaskRecord = (TaskRecord)get(i);
      if ((paramInt == -1) || (localTaskRecord.userId == paramInt)) {}
    }
    for (;;)
    {
      i -= 1;
      break;
      if ((localTaskRecord.autoRemoveRecents) && (localTaskRecord.getTopActivity() == null))
      {
        remove(i);
        localTaskRecord.removedFromRecents();
        Slog.w(TAG, "Removing auto-remove without activity: " + localTaskRecord);
      }
      else if (localTaskRecord.realActivity != null)
      {
        Object localObject2 = (ActivityInfo)this.mTmpAvailActCache.get(localTaskRecord.realActivity);
        Object localObject1 = localObject2;
        if (localObject2 == null) {}
        try
        {
          localObject2 = localIPackageManager.getActivityInfo(localTaskRecord.realActivity, 268435456, paramInt);
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = this.mTmpActivityInfo;
          }
          this.mTmpAvailActCache.put(localTaskRecord.realActivity, localObject1);
          if (localObject1 == this.mTmpActivityInfo)
          {
            localObject2 = (ApplicationInfo)this.mTmpAvailAppCache.get(localTaskRecord.realActivity.getPackageName());
            localObject1 = localObject2;
            if (localObject2 != null) {}
          }
          try
          {
            localObject2 = localIPackageManager.getApplicationInfo(localTaskRecord.realActivity.getPackageName(), 8192, paramInt);
            localObject1 = localObject2;
            if (localObject2 == null) {
              localObject1 = this.mTmpAppInfo;
            }
            this.mTmpAvailAppCache.put(localTaskRecord.realActivity.getPackageName(), localObject1);
            if ((localObject1 == this.mTmpAppInfo) || ((((ApplicationInfo)localObject1).flags & 0x800000) == 0))
            {
              remove(i);
              localTaskRecord.removedFromRecents();
              Slog.w(TAG, "Removing no longer valid recent: " + localTaskRecord);
              continue;
            }
            if ((ActivityManagerDebugConfig.DEBUG_RECENTS) && (localTaskRecord.isAvailable)) {
              Slog.d(TAG_RECENTS, "Making recent unavailable: " + localTaskRecord);
            }
            localTaskRecord.isAvailable = false;
          }
          catch (RemoteException localRemoteException1) {}
          if ((!((ActivityInfo)localObject1).enabled) || (!((ActivityInfo)localObject1).applicationInfo.enabled) || ((((ActivityInfo)localObject1).applicationInfo.flags & 0x800000) == 0))
          {
            if ((ActivityManagerDebugConfig.DEBUG_RECENTS) && (localTaskRecord.isAvailable)) {
              Slog.d(TAG_RECENTS, "Making recent unavailable: " + localTaskRecord + " (enabled=" + ((ActivityInfo)localObject1).enabled + "/" + ((ActivityInfo)localObject1).applicationInfo.enabled + " flags=" + Integer.toHexString(((ActivityInfo)localObject1).applicationInfo.flags) + ")");
            }
            localTaskRecord.isAvailable = false;
            continue;
          }
          if ((!ActivityManagerDebugConfig.DEBUG_RECENTS) || (localTaskRecord.isAvailable)) {}
          for (;;)
          {
            localTaskRecord.isAvailable = true;
            break;
            Slog.d(TAG_RECENTS, "Making recent available: " + localTaskRecord);
          }
          paramInt = 0;
          i = size();
          while (paramInt < i) {
            paramInt = processNextAffiliateChainLocked(paramInt);
          }
          return;
        }
        catch (RemoteException localRemoteException2) {}
      }
    }
  }
  
  void flush()
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      syncPersistentTaskIdsLocked();
      ActivityManagerService.resetPriorityAfterLockedSection();
      this.mTaskPersister.flush();
      return;
    }
  }
  
  Bitmap getImageFromWriteQueue(String paramString)
  {
    return this.mTaskPersister.getImageFromWriteQueue(paramString);
  }
  
  Bitmap getTaskDescriptionIcon(String paramString)
  {
    return this.mTaskPersister.getTaskDescriptionIcon(paramString);
  }
  
  void loadUserRecentsLocked(int paramInt)
  {
    if (!this.mUsersWithRecentsLoaded.get(paramInt))
    {
      loadPersistedTaskIdsForUserLocked(paramInt);
      Slog.i(TAG, "Loading recents for user " + paramInt + " into memory.");
      addAll(this.mTaskPersister.restoreTasksForUserLocked(paramInt));
      cleanupLocked(paramInt);
      this.mUsersWithRecentsLoaded.put(paramInt, true);
    }
  }
  
  void notifyTaskPersisterLocked(TaskRecord paramTaskRecord, boolean paramBoolean)
  {
    if ((paramTaskRecord != null) && (paramTaskRecord.stack != null) && (paramTaskRecord.stack.isHomeStack())) {
      return;
    }
    syncPersistentTaskIdsLocked();
    this.mTaskPersister.wakeup(paramTaskRecord, paramBoolean);
  }
  
  void onPackagesSuspendedChanged(String[] paramArrayOfString, boolean paramBoolean, int paramInt)
  {
    paramArrayOfString = Sets.newHashSet(paramArrayOfString);
    int i = size() - 1;
    while (i >= 0)
    {
      TaskRecord localTaskRecord = (TaskRecord)get(i);
      if ((localTaskRecord.realActivity != null) && (paramArrayOfString.contains(localTaskRecord.realActivity.getPackageName())) && (localTaskRecord.userId == paramInt) && (localTaskRecord.realActivitySuspended != paramBoolean))
      {
        localTaskRecord.realActivitySuspended = paramBoolean;
        notifyTaskPersisterLocked(localTaskRecord, false);
      }
      i -= 1;
    }
  }
  
  void onSystemReadyLocked()
  {
    clear();
    this.mTaskPersister.startPersisting();
  }
  
  void removeTasksForUserLocked(int paramInt)
  {
    if (paramInt <= 0)
    {
      Slog.i(TAG, "Can't remove recent task on user " + paramInt);
      return;
    }
    int i = size() - 1;
    while (i >= 0)
    {
      TaskRecord localTaskRecord = (TaskRecord)get(i);
      if (localTaskRecord.userId == paramInt)
      {
        if (ActivityManagerDebugConfig.DEBUG_TASKS) {
          Slog.i(TAG_TASKS, "remove RecentTask " + localTaskRecord + " when finishing user" + paramInt);
        }
        remove(i);
        localTaskRecord.removedFromRecents();
      }
      i -= 1;
    }
  }
  
  void saveImage(Bitmap paramBitmap, String paramString)
  {
    this.mTaskPersister.saveImage(paramBitmap, paramString);
  }
  
  TaskRecord taskForIdLocked(int paramInt)
  {
    int j = size();
    int i = 0;
    while (i < j)
    {
      TaskRecord localTaskRecord = (TaskRecord)get(i);
      if (localTaskRecord.taskId == paramInt) {
        return localTaskRecord;
      }
      i += 1;
    }
    return null;
  }
  
  boolean taskIdTakenForUserLocked(int paramInt1, int paramInt2)
  {
    loadPersistedTaskIdsForUserLocked(paramInt2);
    return ((SparseBooleanArray)this.mPersistedTaskIds.get(paramInt2)).get(paramInt1);
  }
  
  int trimForTaskLocked(TaskRecord paramTaskRecord, boolean paramBoolean)
  {
    int m = size();
    Intent localIntent = paramTaskRecord.intent;
    boolean bool2;
    int j;
    int i;
    label34:
    TaskRecord localTaskRecord;
    int k;
    Object localObject;
    int i2;
    int i1;
    if (localIntent != null)
    {
      bool2 = localIntent.isDocument();
      j = paramTaskRecord.maxRecents - 1;
      i = 0;
      if (i >= m) {
        break label562;
      }
      localTaskRecord = (TaskRecord)get(i);
      k = j;
      if (paramTaskRecord == localTaskRecord) {
        break label402;
      }
      if ((paramTaskRecord.stack == null) || (localTaskRecord.stack == null) || (paramTaskRecord.stack == localTaskRecord.stack)) {
        break label126;
      }
      localObject = paramTaskRecord;
      i2 = m;
      k = j;
      i1 = i;
    }
    for (;;)
    {
      i = i1 + 1;
      j = k;
      m = i2;
      paramTaskRecord = (TaskRecord)localObject;
      break label34;
      bool2 = false;
      break;
      label126:
      i1 = i;
      k = j;
      i2 = m;
      localObject = paramTaskRecord;
      if (paramTaskRecord.userId == localTaskRecord.userId)
      {
        if (i > 3) {
          localTaskRecord.freeLastThumbnail();
        }
        localObject = localTaskRecord.intent;
        boolean bool4;
        label190:
        boolean bool3;
        label204:
        boolean bool1;
        label255:
        boolean bool5;
        if (paramTaskRecord.affinity != null)
        {
          bool4 = paramTaskRecord.affinity.equals(localTaskRecord.affinity);
          if (localIntent == null) {
            break label414;
          }
          bool3 = localIntent.filterEquals((Intent)localObject);
          k = 0;
          i1 = localIntent.getFlags();
          int n = k;
          if ((0x10080000 & i1) != 0)
          {
            n = k;
            if ((0x8000000 & i1) != 0) {
              n = 1;
            }
          }
          if (localObject == null) {
            break label420;
          }
          bool1 = ((Intent)localObject).isDocument();
          if (!bool2) {
            break label426;
          }
          bool5 = bool1;
          label264:
          if ((!bool4) && (!bool3)) {
            break label432;
          }
          label274:
          if (!bool5) {
            break label460;
          }
          if ((paramTaskRecord.realActivity == null) || (localTaskRecord.realActivity == null)) {
            break label454;
          }
          bool1 = paramTaskRecord.realActivity.equals(localTaskRecord.realActivity);
          label308:
          i1 = i;
          k = j;
          i2 = m;
          localObject = paramTaskRecord;
          if (!bool1) {
            continue;
          }
          k = j;
          if (j > 0)
          {
            j -= 1;
            i1 = i;
            k = j;
            i2 = m;
            localObject = paramTaskRecord;
            if (!paramBoolean) {
              continue;
            }
            i1 = i;
            k = j;
            i2 = m;
            localObject = paramTaskRecord;
            if (!bool3) {
              continue;
            }
            i1 = i;
            k = j;
            i2 = m;
            localObject = paramTaskRecord;
            if (n != 0) {
              continue;
            }
            k = j;
          }
        }
        for (;;)
        {
          label402:
          if (!paramBoolean)
          {
            return i;
            bool4 = false;
            break label190;
            label414:
            bool3 = false;
            break label204;
            label420:
            bool1 = false;
            break label255;
            label426:
            bool5 = false;
            break label264;
            label432:
            i1 = i;
            k = j;
            i2 = m;
            localObject = paramTaskRecord;
            if (!bool5) {
              break;
            }
            break label274;
            label454:
            bool1 = false;
            break label308;
            label460:
            i1 = i;
            k = j;
            i2 = m;
            localObject = paramTaskRecord;
            if (bool2) {
              break;
            }
            k = j;
            if (bool1)
            {
              i1 = i;
              k = j;
              i2 = m;
              localObject = paramTaskRecord;
              break;
            }
          }
        }
        localTaskRecord.disposeThumbnail();
        remove(i);
        if (paramTaskRecord != localTaskRecord) {
          localTaskRecord.removedFromRecents();
        }
        i1 = i - 1;
        i2 = m - 1;
        localObject = paramTaskRecord;
        if (paramTaskRecord.intent == null) {
          localObject = localTaskRecord;
        }
        notifyTaskPersisterLocked(localTaskRecord, false);
      }
    }
    label562:
    return -1;
  }
  
  void unloadUserDataFromMemoryLocked(int paramInt)
  {
    unloadUserRecentsLocked(paramInt);
    this.mPersistedTaskIds.delete(paramInt);
    this.mTaskPersister.unloadUserDataFromMemory(paramInt);
  }
  
  int[] usersWithRecentsLoadedLocked()
  {
    int[] arrayOfInt = new int[this.mUsersWithRecentsLoaded.size()];
    int j = 0;
    int i = 0;
    while (i < arrayOfInt.length)
    {
      int m = this.mUsersWithRecentsLoaded.keyAt(i);
      int k = j;
      if (this.mUsersWithRecentsLoaded.valueAt(i))
      {
        arrayOfInt[j] = m;
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    if (j < arrayOfInt.length) {
      return Arrays.copyOf(arrayOfInt, j);
    }
    return arrayOfInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/RecentTasks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */