package com.oneplus.base;

import com.oneplus.io.FileUtils;

public final class MemoryMonitor
{
  private static final long DURATION_MONITOR_DANGEROUS = 500L;
  private static final long DURATION_MONITOR_HIGH_RISK = 1000L;
  private static final long DURATION_MONITOR_NORMAL = 3000L;
  private static final long DURATION_MONITOR_WARNING = 2000L;
  private static final int MEMORY_STATE_DANGEROUS = 3;
  private static final int MEMORY_STATE_HIGH_RISK = 2;
  private static final int MEMORY_STATE_NORMAL = 0;
  private static final int MEMORY_STATE_WARNING = 1;
  private static final String TAG = "MemoryMonitor";
  private static volatile long m_DangerousThreshold;
  private static volatile long m_HighRiskThreshold;
  private static int m_MemoryState = 0;
  private static volatile Thread m_MonitorThread;
  private static Runtime m_Runtime;
  private static volatile long m_WarningThreshold;
  
  private static void checkMemory()
  {
    long l = m_Runtime.totalMemory() - m_Runtime.freeMemory();
    if (l >= m_DangerousThreshold)
    {
      Log.e("MemoryMonitor", "[DANGEROUS] " + FileUtils.getFileSizeDescription(l));
      m_MemoryState = 3;
      return;
    }
    if (l >= m_HighRiskThreshold)
    {
      Log.w("MemoryMonitor", "[HIGH-RISK] " + FileUtils.getFileSizeDescription(l));
      m_MemoryState = 2;
      return;
    }
    if (l >= m_WarningThreshold)
    {
      Log.w("MemoryMonitor", "[WARNING] " + FileUtils.getFileSizeDescription(l));
      m_MemoryState = 1;
      return;
    }
    if (m_MemoryState != 0) {
      Log.w("MemoryMonitor", "Memory state becomes normal");
    }
    m_MemoryState = 0;
  }
  
  private static void monitorProc()
  {
    for (;;)
    {
      try
      {
        Log.w("MemoryMonitor", "Monitor started");
        m_Runtime = Runtime.getRuntime();
        checkMemory();
        try
        {
          switch (m_MemoryState)
          {
          case 1: 
            Object localObject;
            Thread.sleep(localObject);
          }
        }
        catch (InterruptedException localInterruptedException) {}
        continue;
        long l = 2000L;
        continue;
        l = 1000L;
        continue;
        l = 500L;
        continue;
        l = 3000L;
      }
      catch (Throwable localThrowable)
      {
        Log.e("MemoryMonitor", "monitorProc() - Unhandled error", localThrowable);
        return;
      }
    }
  }
  
  public static void start(long paramLong1, long paramLong2, long paramLong3)
  {
    try
    {
      Thread localThread = m_MonitorThread;
      if (localThread != null) {
        return;
      }
      Log.w("MemoryMonitor", "start() - Thresholds : " + FileUtils.getFileSizeDescription(paramLong1) + " / " + FileUtils.getFileSizeDescription(paramLong2) + " / " + FileUtils.getFileSizeDescription(paramLong3));
      m_WarningThreshold = paramLong1;
      m_HighRiskThreshold = paramLong2;
      m_DangerousThreshold = paramLong3;
      m_MonitorThread = new Thread("Memory monitor thread")
      {
        public void run() {}
      };
      m_MonitorThread.start();
      return;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/MemoryMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */