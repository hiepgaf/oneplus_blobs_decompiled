package android.print;

import java.util.Objects;

public final class PrintJob
{
  private PrintJobInfo mCachedInfo;
  private final PrintManager mPrintManager;
  
  PrintJob(PrintJobInfo paramPrintJobInfo, PrintManager paramPrintManager)
  {
    this.mCachedInfo = paramPrintJobInfo;
    this.mPrintManager = paramPrintManager;
  }
  
  private boolean isInImmutableState()
  {
    int i = this.mCachedInfo.getState();
    return (i == 5) || (i == 7);
  }
  
  public void cancel()
  {
    int i = getInfo().getState();
    if ((i == 2) || (i == 3)) {
      break label32;
    }
    for (;;)
    {
      this.mPrintManager.cancelPrintJob(this.mCachedInfo.getId());
      label32:
      return;
      if (i != 4) {
        if (i != 6) {
          break;
        }
      }
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (PrintJob)paramObject;
    return Objects.equals(this.mCachedInfo.getId(), ((PrintJob)paramObject).mCachedInfo.getId());
  }
  
  public PrintJobId getId()
  {
    return this.mCachedInfo.getId();
  }
  
  public PrintJobInfo getInfo()
  {
    if (isInImmutableState()) {
      return this.mCachedInfo;
    }
    PrintJobInfo localPrintJobInfo = this.mPrintManager.getPrintJobInfo(this.mCachedInfo.getId());
    if (localPrintJobInfo != null) {
      this.mCachedInfo = localPrintJobInfo;
    }
    return this.mCachedInfo;
  }
  
  public int hashCode()
  {
    PrintJobId localPrintJobId = this.mCachedInfo.getId();
    if (localPrintJobId == null) {
      return 0;
    }
    return localPrintJobId.hashCode();
  }
  
  public boolean isBlocked()
  {
    return getInfo().getState() == 4;
  }
  
  public boolean isCancelled()
  {
    return getInfo().getState() == 7;
  }
  
  public boolean isCompleted()
  {
    return getInfo().getState() == 5;
  }
  
  public boolean isFailed()
  {
    return getInfo().getState() == 6;
  }
  
  public boolean isQueued()
  {
    return getInfo().getState() == 2;
  }
  
  public boolean isStarted()
  {
    return getInfo().getState() == 3;
  }
  
  public void restart()
  {
    if (isFailed()) {
      this.mPrintManager.restartPrintJob(this.mCachedInfo.getId());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintJob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */