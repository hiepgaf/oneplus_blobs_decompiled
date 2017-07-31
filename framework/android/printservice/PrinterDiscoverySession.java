package android.printservice;

import android.content.pm.ParceledListSlice;
import android.os.CancellationSignal;
import android.os.RemoteException;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.util.ArrayMap;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class PrinterDiscoverySession
{
  private static final String LOG_TAG = "PrinterDiscoverySession";
  private static int sIdCounter = 0;
  private final int mId;
  private boolean mIsDestroyed;
  private boolean mIsDiscoveryStarted;
  private ArrayMap<PrinterId, PrinterInfo> mLastSentPrinters;
  private IPrintServiceClient mObserver;
  private final ArrayMap<PrinterId, PrinterInfo> mPrinters = new ArrayMap();
  private final List<PrinterId> mTrackedPrinters = new ArrayList();
  
  public PrinterDiscoverySession()
  {
    int i = sIdCounter;
    sIdCounter = i + 1;
    this.mId = i;
  }
  
  private void sendOutOfDiscoveryPeriodPrinterChanges()
  {
    if ((this.mLastSentPrinters == null) || (this.mLastSentPrinters.isEmpty()))
    {
      this.mLastSentPrinters = null;
      return;
    }
    Object localObject1 = null;
    Iterator localIterator = this.mPrinters.values().iterator();
    PrinterInfo localPrinterInfo;
    Object localObject2;
    while (localIterator.hasNext())
    {
      localPrinterInfo = (PrinterInfo)localIterator.next();
      localObject2 = (PrinterInfo)this.mLastSentPrinters.get(localPrinterInfo.getId());
      if ((localObject2 == null) || (!((PrinterInfo)localObject2).equals(localPrinterInfo)))
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((List)localObject2).add(localPrinterInfo);
        localObject1 = localObject2;
      }
    }
    if (localObject1 != null) {}
    try
    {
      this.mObserver.onPrintersAdded(new ParceledListSlice((List)localObject1));
      localObject1 = null;
      localIterator = this.mLastSentPrinters.values().iterator();
      while (localIterator.hasNext())
      {
        localPrinterInfo = (PrinterInfo)localIterator.next();
        if (!this.mPrinters.containsKey(localPrinterInfo.getId()))
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((List)localObject2).add(localPrinterInfo.getId());
          localObject1 = localObject2;
        }
      }
    }
    catch (RemoteException localRemoteException1)
    {
      for (;;)
      {
        Log.e("PrinterDiscoverySession", "Error sending added printers", localRemoteException1);
      }
      if (localRemoteException1 == null) {}
    }
    try
    {
      this.mObserver.onPrintersRemoved(new ParceledListSlice(localRemoteException1));
      this.mLastSentPrinters = null;
      return;
    }
    catch (RemoteException localRemoteException2)
    {
      for (;;)
      {
        Log.e("PrinterDiscoverySession", "Error sending removed printers", localRemoteException2);
      }
    }
  }
  
  public final void addPrinters(List<PrinterInfo> paramList)
  {
    
    if (this.mIsDestroyed)
    {
      Log.w("PrinterDiscoverySession", "Not adding printers - session destroyed.");
      return;
    }
    Object localObject1;
    int j;
    int i;
    if (this.mIsDiscoveryStarted)
    {
      localObject1 = null;
      j = paramList.size();
      i = 0;
      if (i < j)
      {
        PrinterInfo localPrinterInfo = (PrinterInfo)paramList.get(i);
        Object localObject2 = (PrinterInfo)this.mPrinters.put(localPrinterInfo.getId(), localPrinterInfo);
        if ((localObject2 != null) && (((PrinterInfo)localObject2).equals(localPrinterInfo))) {}
        for (;;)
        {
          i += 1;
          break;
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((List)localObject2).add(localPrinterInfo);
          localObject1 = localObject2;
        }
      }
      if (localObject1 == null) {}
    }
    for (;;)
    {
      try
      {
        this.mObserver.onPrintersAdded(new ParceledListSlice((List)localObject1));
        return;
      }
      catch (RemoteException paramList)
      {
        Log.e("PrinterDiscoverySession", "Error sending added printers", paramList);
        return;
      }
      if (this.mLastSentPrinters == null) {
        this.mLastSentPrinters = new ArrayMap(this.mPrinters);
      }
      j = paramList.size();
      i = 0;
      while (i < j)
      {
        localObject1 = (PrinterInfo)paramList.get(i);
        if (this.mPrinters.get(((PrinterInfo)localObject1).getId()) == null) {
          this.mPrinters.put(((PrinterInfo)localObject1).getId(), localObject1);
        }
        i += 1;
      }
    }
  }
  
  void destroy()
  {
    if (!this.mIsDestroyed)
    {
      this.mIsDestroyed = true;
      this.mIsDiscoveryStarted = false;
      this.mPrinters.clear();
      this.mLastSentPrinters = null;
      this.mObserver = null;
      onDestroy();
    }
  }
  
  int getId()
  {
    return this.mId;
  }
  
  public final List<PrinterInfo> getPrinters()
  {
    
    if (this.mIsDestroyed) {
      return Collections.emptyList();
    }
    return new ArrayList(this.mPrinters.values());
  }
  
  public final List<PrinterId> getTrackedPrinters()
  {
    
    if (this.mIsDestroyed) {
      return Collections.emptyList();
    }
    return new ArrayList(this.mTrackedPrinters);
  }
  
  public final boolean isDestroyed()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return this.mIsDestroyed;
  }
  
  public final boolean isPrinterDiscoveryStarted()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return this.mIsDiscoveryStarted;
  }
  
  public abstract void onDestroy();
  
  public void onRequestCustomPrinterIcon(PrinterId paramPrinterId, CancellationSignal paramCancellationSignal, CustomPrinterIconCallback paramCustomPrinterIconCallback) {}
  
  public abstract void onStartPrinterDiscovery(List<PrinterId> paramList);
  
  public abstract void onStartPrinterStateTracking(PrinterId paramPrinterId);
  
  public abstract void onStopPrinterDiscovery();
  
  public abstract void onStopPrinterStateTracking(PrinterId paramPrinterId);
  
  public abstract void onValidatePrinters(List<PrinterId> paramList);
  
  public final void removePrinters(List<PrinterId> paramList)
  {
    
    if (this.mIsDestroyed)
    {
      Log.w("PrinterDiscoverySession", "Not removing printers - session destroyed.");
      return;
    }
    Object localObject;
    int j;
    int i;
    if (this.mIsDiscoveryStarted)
    {
      localObject = new ArrayList();
      j = paramList.size();
      i = 0;
      while (i < j)
      {
        PrinterId localPrinterId = (PrinterId)paramList.get(i);
        if (this.mPrinters.remove(localPrinterId) != null) {
          ((List)localObject).add(localPrinterId);
        }
        i += 1;
      }
      if (((List)localObject).isEmpty()) {}
    }
    for (;;)
    {
      try
      {
        this.mObserver.onPrintersRemoved(new ParceledListSlice((List)localObject));
        return;
      }
      catch (RemoteException paramList)
      {
        Log.e("PrinterDiscoverySession", "Error sending removed printers", paramList);
        return;
      }
      if (this.mLastSentPrinters == null) {
        this.mLastSentPrinters = new ArrayMap(this.mPrinters);
      }
      j = paramList.size();
      i = 0;
      while (i < j)
      {
        localObject = (PrinterId)paramList.get(i);
        this.mPrinters.remove(localObject);
        i += 1;
      }
    }
  }
  
  void requestCustomPrinterIcon(PrinterId paramPrinterId)
  {
    if ((!this.mIsDestroyed) && (this.mObserver != null))
    {
      CustomPrinterIconCallback localCustomPrinterIconCallback = new CustomPrinterIconCallback(paramPrinterId, this.mObserver);
      onRequestCustomPrinterIcon(paramPrinterId, new CancellationSignal(), localCustomPrinterIconCallback);
    }
  }
  
  void setObserver(IPrintServiceClient paramIPrintServiceClient)
  {
    this.mObserver = paramIPrintServiceClient;
    if (!this.mPrinters.isEmpty()) {}
    try
    {
      this.mObserver.onPrintersAdded(new ParceledListSlice(getPrinters()));
      return;
    }
    catch (RemoteException paramIPrintServiceClient)
    {
      Log.e("PrinterDiscoverySession", "Error sending added printers", paramIPrintServiceClient);
    }
  }
  
  void startPrinterDiscovery(List<PrinterId> paramList)
  {
    if (!this.mIsDestroyed)
    {
      this.mIsDiscoveryStarted = true;
      sendOutOfDiscoveryPeriodPrinterChanges();
      Object localObject = paramList;
      if (paramList == null) {
        localObject = Collections.emptyList();
      }
      onStartPrinterDiscovery((List)localObject);
    }
  }
  
  void startPrinterStateTracking(PrinterId paramPrinterId)
  {
    if ((this.mIsDestroyed) || (this.mObserver == null) || (this.mTrackedPrinters.contains(paramPrinterId))) {
      return;
    }
    this.mTrackedPrinters.add(paramPrinterId);
    onStartPrinterStateTracking(paramPrinterId);
  }
  
  void stopPrinterDiscovery()
  {
    if (!this.mIsDestroyed)
    {
      this.mIsDiscoveryStarted = false;
      onStopPrinterDiscovery();
    }
  }
  
  void stopPrinterStateTracking(PrinterId paramPrinterId)
  {
    if ((!this.mIsDestroyed) && (this.mObserver != null) && (this.mTrackedPrinters.remove(paramPrinterId))) {
      onStopPrinterStateTracking(paramPrinterId);
    }
  }
  
  void validatePrinters(List<PrinterId> paramList)
  {
    if ((!this.mIsDestroyed) && (this.mObserver != null)) {
      onValidatePrinters(paramList);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/PrinterDiscoverySession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */