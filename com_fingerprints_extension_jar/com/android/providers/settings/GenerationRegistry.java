package com.android.providers.settings;

import android.os.Bundle;
import android.os.UserManager;
import android.util.MemoryIntArray;
import android.util.Slog;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import java.io.IOException;

final class GenerationRegistry
{
  @GuardedBy("mLock")
  private MemoryIntArray mBackingStore;
  @GuardedBy("mLock")
  private final SparseIntArray mKeyToIndexMap = new SparseIntArray();
  private final Object mLock;
  
  public GenerationRegistry(Object paramObject)
  {
    this.mLock = paramObject;
  }
  
  private void destroyBackingStore()
  {
    if (this.mBackingStore != null) {}
    try
    {
      this.mBackingStore.close();
      this.mBackingStore = null;
      return;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Slog.e("GenerationTracker", "Cannot close generation memory array", localIOException);
      }
    }
  }
  
  private static int findNextEmptyIndex(MemoryIntArray paramMemoryIntArray)
    throws IOException
  {
    int j = paramMemoryIntArray.size();
    int i = 0;
    while (i < j)
    {
      if (paramMemoryIntArray.get(i) == 0) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private MemoryIntArray getBackingStoreLocked()
  {
    int i;
    if (this.mBackingStore == null) {
      i = UserManager.getMaxSupportedUsers();
    }
    try
    {
      this.mBackingStore = new MemoryIntArray(i * 2 + 13);
      return this.mBackingStore;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Slog.e("GenerationTracker", "Error creating generation tracker", localIOException);
      }
    }
  }
  
  private static int getKeyIndexLocked(int paramInt, SparseIntArray paramSparseIntArray, MemoryIntArray paramMemoryIntArray)
    throws IOException
  {
    int j = paramSparseIntArray.get(paramInt, -1);
    int i = j;
    if (j < 0)
    {
      i = findNextEmptyIndex(paramMemoryIntArray);
      if (i >= 0)
      {
        paramMemoryIntArray.set(i, 1);
        paramSparseIntArray.append(paramInt, i);
      }
    }
    else
    {
      return i;
    }
    Slog.e("GenerationTracker", "Could not allocate generation index");
    return i;
  }
  
  private static void resetSlotForKeyLocked(int paramInt, SparseIntArray paramSparseIntArray, MemoryIntArray paramMemoryIntArray)
    throws IOException
  {
    int i = paramSparseIntArray.get(paramInt, -1);
    if (i >= 0)
    {
      paramSparseIntArray.delete(paramInt);
      paramMemoryIntArray.set(i, 0);
    }
  }
  
  public void addGenerationData(Bundle paramBundle, int paramInt)
  {
    synchronized (this.mLock)
    {
      MemoryIntArray localMemoryIntArray = getBackingStoreLocked();
      if (localMemoryIntArray != null) {}
      try
      {
        paramInt = getKeyIndexLocked(paramInt, this.mKeyToIndexMap, localMemoryIntArray);
        if (paramInt >= 0)
        {
          paramBundle.putParcelable("_track_generation", localMemoryIntArray);
          paramBundle.putInt("_generation_index", paramInt);
          paramBundle.putInt("_generation", localMemoryIntArray.get(paramInt));
        }
      }
      catch (IOException paramBundle)
      {
        for (;;)
        {
          Slog.e("GenerationTracker", "Error adding generation data", paramBundle);
          destroyBackingStore();
        }
      }
      return;
    }
  }
  
  public void incrementGeneration(int paramInt)
  {
    synchronized (this.mLock)
    {
      MemoryIntArray localMemoryIntArray = getBackingStoreLocked();
      if (localMemoryIntArray != null) {}
      try
      {
        paramInt = getKeyIndexLocked(paramInt, this.mKeyToIndexMap, localMemoryIntArray);
        if (paramInt >= 0) {
          localMemoryIntArray.set(paramInt, localMemoryIntArray.get(paramInt) + 1);
        }
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          Slog.e("GenerationTracker", "Error updating generation id", localIOException);
          destroyBackingStore();
        }
      }
      return;
    }
  }
  
  public void onUserRemoved(int paramInt)
  {
    synchronized (this.mLock)
    {
      MemoryIntArray localMemoryIntArray = getBackingStoreLocked();
      if (localMemoryIntArray != null)
      {
        int i = this.mKeyToIndexMap.size();
        if (i <= 0) {}
      }
      try
      {
        resetSlotForKeyLocked(SettingsProvider.makeKey(2, paramInt), this.mKeyToIndexMap, localMemoryIntArray);
        resetSlotForKeyLocked(SettingsProvider.makeKey(1, paramInt), this.mKeyToIndexMap, localMemoryIntArray);
        return;
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          Slog.e("GenerationTracker", "Error cleaning up for user", localIOException);
          destroyBackingStore();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/providers/settings/GenerationRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */