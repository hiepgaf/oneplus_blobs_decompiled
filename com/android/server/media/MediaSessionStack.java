package com.android.server.media;

import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.media.session.MediaSession;
import android.os.RemoteException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

class MediaSessionStack
{
  private static final int[] ALWAYS_PRIORITY_STATES = { 4, 5, 9, 10 };
  private static final int[] TRANSITION_PRIORITY_STATES = { 6, 8, 3 };
  private ArrayList<MediaSessionRecord> mCachedActiveList;
  private MediaSessionRecord mCachedButtonReceiver;
  private MediaSessionRecord mCachedDefault;
  private ArrayList<MediaSessionRecord> mCachedTransportControlList;
  private MediaSessionRecord mCachedVolumeDefault;
  private MediaSessionRecord mGlobalPrioritySession;
  private MediaSessionRecord mLastInterestingRecord;
  private final ArrayList<MediaSessionRecord> mSessions = new ArrayList();
  
  private void clearCache()
  {
    this.mCachedDefault = null;
    this.mCachedVolumeDefault = null;
    this.mCachedButtonReceiver = null;
    this.mCachedActiveList = null;
    this.mCachedTransportControlList = null;
  }
  
  private boolean containsState(int paramInt, int[] paramArrayOfInt)
  {
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      if (paramArrayOfInt[i] == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private ArrayList<MediaSessionRecord> getPriorityListLocked(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(Integer.valueOf(paramInt2));
    return getPriorityListLocked(paramBoolean, paramInt1, localArrayList);
  }
  
  private ArrayList<MediaSessionRecord> getPriorityListLocked(boolean paramBoolean, int paramInt, List<Integer> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    int i3 = 0;
    int i2 = 0;
    int i1 = 0;
    int m;
    int n;
    label46:
    MediaSessionRecord localMediaSessionRecord;
    int j;
    int k;
    int i;
    if (paramList.contains(Integer.valueOf(-1)))
    {
      m = 0;
      int i4 = this.mSessions.size();
      n = 0;
      if (n >= i4) {
        break label303;
      }
      localMediaSessionRecord = (MediaSessionRecord)this.mSessions.get(n);
      if (m != 0)
      {
        j = i2;
        k = i3;
        i = i1;
        if (!paramList.contains(Integer.valueOf(localMediaSessionRecord.getUserId()))) {}
      }
      else
      {
        if ((localMediaSessionRecord.getFlags() & paramInt) == paramInt) {
          break label154;
        }
        i = i1;
        k = i3;
        j = i2;
      }
    }
    for (;;)
    {
      n += 1;
      i2 = j;
      i3 = k;
      i1 = i;
      break label46;
      m = 1;
      break;
      label154:
      if (!localMediaSessionRecord.isActive())
      {
        j = i2;
        k = i3;
        i = i1;
        if (!paramBoolean)
        {
          localArrayList.add(localMediaSessionRecord);
          j = i2;
          k = i3;
          i = i1;
        }
      }
      else if (localMediaSessionRecord.isSystemPriority())
      {
        localArrayList.add(0, localMediaSessionRecord);
        k = i3 + 1;
        j = i2 + 1;
        i = i1 + 1;
      }
      else if (localMediaSessionRecord.isPlaybackActive(true))
      {
        localArrayList.add(i3, localMediaSessionRecord);
        k = i3 + 1;
        j = i2 + 1;
        i = i1 + 1;
      }
      else
      {
        localArrayList.add(i1, localMediaSessionRecord);
        i = i1 + 1;
        j = i2;
        k = i3;
      }
    }
    label303:
    return localArrayList;
  }
  
  private static boolean isFromMostRecentApp(MediaSessionRecord paramMediaSessionRecord)
  {
    try
    {
      Object localObject = ActivityManagerNative.getDefault().getRecentTasks(1, 15, paramMediaSessionRecord.getUserId()).getList();
      if (localObject != null)
      {
        if (((List)localObject).isEmpty()) {
          return false;
        }
        localObject = (ActivityManager.RecentTaskInfo)((List)localObject).get(0);
        if ((((ActivityManager.RecentTaskInfo)localObject).userId == paramMediaSessionRecord.getUserId()) && (((ActivityManager.RecentTaskInfo)localObject).baseIntent != null))
        {
          boolean bool = ((ActivityManager.RecentTaskInfo)localObject).baseIntent.getComponent().getPackageName().equals(paramMediaSessionRecord.getPackageName());
          return bool;
        }
      }
    }
    catch (RemoteException paramMediaSessionRecord)
    {
      return false;
    }
    return false;
  }
  
  private boolean shouldUpdatePriority(int paramInt1, int paramInt2)
  {
    if (containsState(paramInt2, ALWAYS_PRIORITY_STATES)) {
      return true;
    }
    return (!containsState(paramInt1, TRANSITION_PRIORITY_STATES)) && (containsState(paramInt2, TRANSITION_PRIORITY_STATES));
  }
  
  public void addSession(MediaSessionRecord paramMediaSessionRecord, boolean paramBoolean)
  {
    this.mSessions.add(paramMediaSessionRecord);
    clearCache();
    if ((paramBoolean) && (isFromMostRecentApp(paramMediaSessionRecord))) {
      this.mLastInterestingRecord = paramMediaSessionRecord;
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    ArrayList localArrayList = getPriorityListLocked(false, 0, -1);
    int j = localArrayList.size();
    paramPrintWriter.println(paramString + "Global priority session is " + this.mGlobalPrioritySession);
    paramPrintWriter.println(paramString + "Sessions Stack - have " + j + " sessions:");
    paramString = paramString + "  ";
    int i = 0;
    while (i < j)
    {
      ((MediaSessionRecord)localArrayList.get(i)).dump(paramPrintWriter, paramString);
      paramPrintWriter.println();
      i += 1;
    }
  }
  
  public ArrayList<MediaSessionRecord> getActiveSessions(int paramInt)
  {
    if (this.mCachedActiveList == null) {
      this.mCachedActiveList = getPriorityListLocked(true, 0, paramInt);
    }
    return this.mCachedActiveList;
  }
  
  public MediaSessionRecord getDefaultMediaButtonSession(List<Integer> paramList, boolean paramBoolean)
  {
    if ((this.mGlobalPrioritySession != null) && (this.mGlobalPrioritySession.isActive())) {
      return this.mGlobalPrioritySession;
    }
    if (this.mCachedButtonReceiver != null) {
      return this.mCachedButtonReceiver;
    }
    paramList = getPriorityListLocked(true, 1, paramList);
    MediaSessionRecord localMediaSessionRecord;
    if (paramList.size() > 0)
    {
      localMediaSessionRecord = (MediaSessionRecord)paramList.get(0);
      if (!localMediaSessionRecord.isPlaybackActive(false)) {
        break label97;
      }
      this.mLastInterestingRecord = localMediaSessionRecord;
      this.mCachedButtonReceiver = localMediaSessionRecord;
    }
    for (;;)
    {
      if ((paramBoolean) && (this.mCachedButtonReceiver == null)) {
        this.mCachedButtonReceiver = localMediaSessionRecord;
      }
      return this.mCachedButtonReceiver;
      label97:
      if (this.mLastInterestingRecord != null) {
        if (paramList.contains(this.mLastInterestingRecord)) {
          this.mCachedButtonReceiver = this.mLastInterestingRecord;
        } else {
          this.mLastInterestingRecord = null;
        }
      }
    }
  }
  
  public MediaSessionRecord getDefaultRemoteSession(int paramInt)
  {
    ArrayList localArrayList = getPriorityListLocked(true, 0, paramInt);
    int i = localArrayList.size();
    paramInt = 0;
    while (paramInt < i)
    {
      MediaSessionRecord localMediaSessionRecord = (MediaSessionRecord)localArrayList.get(paramInt);
      if (localMediaSessionRecord.getPlaybackType() == 2) {
        return localMediaSessionRecord;
      }
      paramInt += 1;
    }
    return null;
  }
  
  public MediaSessionRecord getDefaultSession(int paramInt)
  {
    if (this.mCachedDefault != null) {
      return this.mCachedDefault;
    }
    ArrayList localArrayList = getPriorityListLocked(true, 0, paramInt);
    if (localArrayList.size() > 0) {
      return (MediaSessionRecord)localArrayList.get(0);
    }
    return null;
  }
  
  public MediaSessionRecord getDefaultVolumeSession(List<Integer> paramList)
  {
    if ((this.mGlobalPrioritySession != null) && (this.mGlobalPrioritySession.isActive())) {
      return this.mGlobalPrioritySession;
    }
    if (this.mCachedVolumeDefault != null) {
      return this.mCachedVolumeDefault;
    }
    paramList = getPriorityListLocked(true, 0, paramList);
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      MediaSessionRecord localMediaSessionRecord = (MediaSessionRecord)paramList.get(i);
      if (localMediaSessionRecord.isPlaybackActive(false))
      {
        this.mCachedVolumeDefault = localMediaSessionRecord;
        return localMediaSessionRecord;
      }
      i += 1;
    }
    return null;
  }
  
  public ArrayList<MediaSessionRecord> getTransportControlSessions(int paramInt)
  {
    if (this.mCachedTransportControlList == null) {
      this.mCachedTransportControlList = getPriorityListLocked(true, 2, paramInt);
    }
    return this.mCachedTransportControlList;
  }
  
  public boolean isGlobalPriorityActive()
  {
    if (this.mGlobalPrioritySession == null) {
      return false;
    }
    return this.mGlobalPrioritySession.isActive();
  }
  
  public boolean onPlaystateChange(MediaSessionRecord paramMediaSessionRecord, int paramInt1, int paramInt2)
  {
    if (shouldUpdatePriority(paramInt1, paramInt2))
    {
      this.mSessions.remove(paramMediaSessionRecord);
      this.mSessions.add(0, paramMediaSessionRecord);
      clearCache();
      this.mLastInterestingRecord = paramMediaSessionRecord;
      return true;
    }
    if (!MediaSession.isActiveState(paramInt2)) {
      this.mCachedVolumeDefault = null;
    }
    return false;
  }
  
  public void onSessionStateChange(MediaSessionRecord paramMediaSessionRecord)
  {
    if ((paramMediaSessionRecord.getFlags() & 0x10000) != 0L) {
      this.mGlobalPrioritySession = paramMediaSessionRecord;
    }
    clearCache();
  }
  
  public void removeSession(MediaSessionRecord paramMediaSessionRecord)
  {
    this.mSessions.remove(paramMediaSessionRecord);
    if (paramMediaSessionRecord == this.mGlobalPrioritySession) {
      this.mGlobalPrioritySession = null;
    }
    clearCache();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/media/MediaSessionStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */