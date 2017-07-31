package com.oneplus.base;

import com.oneplus.io.FileUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MemoryUsage
{
  private static final Object SYNC_OBJ = new Object();
  private static final String TAG = MemoryUsage.class.getSimpleName();
  private final long m_MaxMemoryUsageBytes;
  private final List<MemoryUsageHandle> m_MemoryUsageHandles = new ArrayList();
  private volatile long m_TotalMemoryUsage;
  private final LinkedList<Object> m_WaitingMemoryUsageHandles = new LinkedList();
  
  public MemoryUsage(long paramLong)
  {
    this.m_MaxMemoryUsageBytes = paramLong;
  }
  
  private void releaseMemoryUsage(MemoryUsageHandle paramMemoryUsageHandle)
  {
    synchronized (SYNC_OBJ)
    {
      this.m_MemoryUsageHandles.remove(paramMemoryUsageHandle);
      this.m_TotalMemoryUsage -= paramMemoryUsageHandle.memoryUsage;
      Log.d(TAG, "releaseMemoryUsage() - Remaining memory usage: ", FileUtils.getFileSizeDescription(this.m_TotalMemoryUsage));
      SYNC_OBJ.notifyAll();
      return;
    }
  }
  
  public long getCurrentMemoryUsage()
  {
    return this.m_TotalMemoryUsage;
  }
  
  public Handle requestMemoryUsage(long paramLong)
  {
    synchronized (SYNC_OBJ)
    {
      MemoryUsageHandle localMemoryUsageHandle = new MemoryUsageHandle(paramLong);
      if (this.m_TotalMemoryUsage + paramLong > this.m_MaxMemoryUsageBytes) {
        this.m_WaitingMemoryUsageHandles.add(localMemoryUsageHandle);
      }
      try
      {
        do
        {
          SYNC_OBJ.wait();
        } while ((this.m_WaitingMemoryUsageHandles.peek() != localMemoryUsageHandle) || (this.m_TotalMemoryUsage + paramLong > this.m_MaxMemoryUsageBytes));
        this.m_WaitingMemoryUsageHandles.remove();
        this.m_MemoryUsageHandles.add(localMemoryUsageHandle);
        this.m_TotalMemoryUsage += paramLong;
        Log.d(TAG, "requestMemoryUsage() - Total memory usage: ", FileUtils.getFileSizeDescription(this.m_TotalMemoryUsage));
        return localMemoryUsageHandle;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          Log.e(TAG, "Error to wait for memory usage", localInterruptedException);
        }
      }
    }
  }
  
  private class MemoryUsageHandle
    extends Handle
  {
    long memoryUsage;
    
    MemoryUsageHandle(long paramLong)
    {
      super();
      this.memoryUsage = paramLong;
    }
    
    protected void onClose(int paramInt)
    {
      MemoryUsage.-wrap0(MemoryUsage.this, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/MemoryUsage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */