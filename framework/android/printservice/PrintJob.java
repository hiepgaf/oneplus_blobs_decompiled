package android.printservice;

import android.content.Context;
import android.os.RemoteException;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.util.Log;

public final class PrintJob
{
  private static final String LOG_TAG = "PrintJob";
  private PrintJobInfo mCachedInfo;
  private final Context mContext;
  private final PrintDocument mDocument;
  private final IPrintServiceClient mPrintServiceClient;
  
  PrintJob(Context paramContext, PrintJobInfo paramPrintJobInfo, IPrintServiceClient paramIPrintServiceClient)
  {
    this.mContext = paramContext;
    this.mCachedInfo = paramPrintJobInfo;
    this.mPrintServiceClient = paramIPrintServiceClient;
    this.mDocument = new PrintDocument(this.mCachedInfo.getId(), paramIPrintServiceClient, paramPrintJobInfo.getDocumentInfo());
  }
  
  private boolean isInImmutableState()
  {
    int i = this.mCachedInfo.getState();
    if ((i == 5) || (i == 7)) {}
    while (i == 6) {
      return true;
    }
    return false;
  }
  
  private boolean setState(int paramInt, String paramString)
  {
    try
    {
      if (this.mPrintServiceClient.setPrintJobState(this.mCachedInfo.getId(), paramInt, paramString))
      {
        this.mCachedInfo.setState(paramInt);
        this.mCachedInfo.setStatus(paramString);
        return true;
      }
    }
    catch (RemoteException paramString)
    {
      Log.e("PrintJob", "Error setting the state of job: " + this.mCachedInfo.getId(), paramString);
    }
    return false;
  }
  
  public boolean block(String paramString)
  {
    PrintService.throwIfNotCalledOnMainThread();
    int i = getInfo().getState();
    if ((i == 3) || (i == 4)) {
      return setState(4, paramString);
    }
    return false;
  }
  
  public boolean cancel()
  {
    
    if (!isInImmutableState()) {
      return setState(7, null);
    }
    return false;
  }
  
  public boolean complete()
  {
    
    if (isStarted()) {
      return setState(5, null);
    }
    return false;
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
    return this.mCachedInfo.getId().equals(((PrintJob)paramObject).mCachedInfo.getId());
  }
  
  public boolean fail(String paramString)
  {
    
    if (!isInImmutableState()) {
      return setState(6, paramString);
    }
    return false;
  }
  
  public int getAdvancedIntOption(String paramString)
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().getAdvancedIntOption(paramString);
  }
  
  public String getAdvancedStringOption(String paramString)
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().getAdvancedStringOption(paramString);
  }
  
  public PrintDocument getDocument()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return this.mDocument;
  }
  
  public PrintJobId getId()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return this.mCachedInfo.getId();
  }
  
  public PrintJobInfo getInfo()
  {
    
    if (isInImmutableState()) {
      return this.mCachedInfo;
    }
    Object localObject = null;
    try
    {
      PrintJobInfo localPrintJobInfo = this.mPrintServiceClient.getPrintJobInfo(this.mCachedInfo.getId());
      localObject = localPrintJobInfo;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("PrintJob", "Couldn't get info for job: " + this.mCachedInfo.getId(), localRemoteException);
      }
    }
    if (localObject != null) {
      this.mCachedInfo = ((PrintJobInfo)localObject);
    }
    return this.mCachedInfo;
  }
  
  public String getTag()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().getTag();
  }
  
  public boolean hasAdvancedOption(String paramString)
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().hasAdvancedOption(paramString);
  }
  
  public int hashCode()
  {
    return this.mCachedInfo.getId().hashCode();
  }
  
  public boolean isBlocked()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().getState() == 4;
  }
  
  public boolean isCancelled()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().getState() == 7;
  }
  
  public boolean isCompleted()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().getState() == 5;
  }
  
  public boolean isFailed()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().getState() == 6;
  }
  
  public boolean isQueued()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().getState() == 2;
  }
  
  public boolean isStarted()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return getInfo().getState() == 3;
  }
  
  public void setProgress(float paramFloat)
  {
    
    try
    {
      this.mPrintServiceClient.setProgress(this.mCachedInfo.getId(), paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("PrintJob", "Error setting progress for job: " + this.mCachedInfo.getId(), localRemoteException);
    }
  }
  
  public void setStatus(int paramInt)
  {
    
    try
    {
      this.mPrintServiceClient.setStatusRes(this.mCachedInfo.getId(), paramInt, this.mContext.getPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("PrintJob", "Error setting status for job: " + this.mCachedInfo.getId(), localRemoteException);
    }
  }
  
  public void setStatus(CharSequence paramCharSequence)
  {
    
    try
    {
      this.mPrintServiceClient.setStatus(this.mCachedInfo.getId(), paramCharSequence);
      return;
    }
    catch (RemoteException paramCharSequence)
    {
      Log.e("PrintJob", "Error setting status for job: " + this.mCachedInfo.getId(), paramCharSequence);
    }
  }
  
  public boolean setTag(String paramString)
  {
    
    if (isInImmutableState()) {
      return false;
    }
    try
    {
      boolean bool = this.mPrintServiceClient.setPrintJobTag(this.mCachedInfo.getId(), paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      Log.e("PrintJob", "Error setting tag for job: " + this.mCachedInfo.getId(), paramString);
    }
    return false;
  }
  
  public boolean start()
  {
    PrintService.throwIfNotCalledOnMainThread();
    int i = getInfo().getState();
    if ((i == 2) || (i == 4)) {
      return setState(3, null);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/PrintJob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */