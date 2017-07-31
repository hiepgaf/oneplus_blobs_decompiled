package android.print;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PrinterDiscoverySession
{
  private static final String LOG_TAG = "PrinterDiscoverySession";
  private static final int MSG_PRINTERS_ADDED = 1;
  private static final int MSG_PRINTERS_REMOVED = 2;
  private final Handler mHandler;
  private boolean mIsPrinterDiscoveryStarted;
  private OnPrintersChangeListener mListener;
  private IPrinterDiscoveryObserver mObserver;
  private final IPrintManager mPrintManager;
  private final LinkedHashMap<PrinterId, PrinterInfo> mPrinters = new LinkedHashMap();
  private final int mUserId;
  
  PrinterDiscoverySession(IPrintManager paramIPrintManager, Context paramContext, int paramInt)
  {
    this.mPrintManager = paramIPrintManager;
    this.mUserId = paramInt;
    this.mHandler = new SessionHandler(paramContext.getMainLooper());
    this.mObserver = new PrinterDiscoveryObserver(this);
    try
    {
      this.mPrintManager.createPrinterDiscoverySession(this.mObserver, this.mUserId);
      return;
    }
    catch (RemoteException paramIPrintManager)
    {
      Log.e("PrinterDiscoverySession", "Error creating printer discovery session", paramIPrintManager);
    }
  }
  
  private void destroyNoCheck()
  {
    stopPrinterDiscovery();
    try
    {
      this.mPrintManager.destroyPrinterDiscoverySession(this.mObserver, this.mUserId);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("PrinterDiscoverySession", "Error destroying printer discovery session", localRemoteException);
      return;
    }
    finally
    {
      this.mObserver = null;
      this.mPrinters.clear();
    }
  }
  
  private void handlePrintersAdded(List<PrinterInfo> paramList)
  {
    if (isDestroyed()) {
      return;
    }
    if (this.mPrinters.isEmpty())
    {
      j = paramList.size();
      i = 0;
      while (i < j)
      {
        localObject1 = (PrinterInfo)paramList.get(i);
        this.mPrinters.put(((PrinterInfo)localObject1).getId(), localObject1);
        i += 1;
      }
      notifyOnPrintersChanged();
      return;
    }
    Object localObject1 = new ArrayMap();
    int j = paramList.size();
    int i = 0;
    Object localObject2;
    while (i < j)
    {
      localObject2 = (PrinterInfo)paramList.get(i);
      ((ArrayMap)localObject1).put(((PrinterInfo)localObject2).getId(), localObject2);
      i += 1;
    }
    paramList = this.mPrinters.keySet().iterator();
    while (paramList.hasNext())
    {
      localObject2 = (PrinterId)paramList.next();
      PrinterInfo localPrinterInfo = (PrinterInfo)((ArrayMap)localObject1).remove(localObject2);
      if (localPrinterInfo != null) {
        this.mPrinters.put(localObject2, localPrinterInfo);
      }
    }
    this.mPrinters.putAll((Map)localObject1);
    notifyOnPrintersChanged();
  }
  
  private void handlePrintersRemoved(List<PrinterId> paramList)
  {
    if (isDestroyed()) {
      return;
    }
    int j = 0;
    int k = paramList.size();
    int i = 0;
    while (i < k)
    {
      PrinterId localPrinterId = (PrinterId)paramList.get(i);
      if (this.mPrinters.remove(localPrinterId) != null) {
        j = 1;
      }
      i += 1;
    }
    if (j != 0) {
      notifyOnPrintersChanged();
    }
  }
  
  private boolean isDestroyedNoCheck()
  {
    return this.mObserver == null;
  }
  
  private void notifyOnPrintersChanged()
  {
    if (this.mListener != null) {
      this.mListener.onPrintersChanged();
    }
  }
  
  private static void throwIfNotCalledOnMainThread()
  {
    if (!Looper.getMainLooper().isCurrentThread()) {
      throw new IllegalAccessError("must be called from the main thread");
    }
  }
  
  public final void destroy()
  {
    if (isDestroyed()) {
      Log.w("PrinterDiscoverySession", "Ignoring destroy - session destroyed");
    }
    destroyNoCheck();
  }
  
  protected final void finalize()
    throws Throwable
  {
    if (!isDestroyedNoCheck())
    {
      Log.e("PrinterDiscoverySession", "Destroying leaked printer discovery session");
      destroyNoCheck();
    }
    super.finalize();
  }
  
  public final List<PrinterInfo> getPrinters()
  {
    if (isDestroyed())
    {
      Log.w("PrinterDiscoverySession", "Ignoring get printers - session destroyed");
      return Collections.emptyList();
    }
    return new ArrayList(this.mPrinters.values());
  }
  
  public final boolean isDestroyed()
  {
    throwIfNotCalledOnMainThread();
    return isDestroyedNoCheck();
  }
  
  public final boolean isPrinterDiscoveryStarted()
  {
    throwIfNotCalledOnMainThread();
    return this.mIsPrinterDiscoveryStarted;
  }
  
  public final void setOnPrintersChangeListener(OnPrintersChangeListener paramOnPrintersChangeListener)
  {
    throwIfNotCalledOnMainThread();
    this.mListener = paramOnPrintersChangeListener;
  }
  
  public final void startPrinterDiscovery(List<PrinterId> paramList)
  {
    if (isDestroyed())
    {
      Log.w("PrinterDiscoverySession", "Ignoring start printers discovery - session destroyed");
      return;
    }
    if (!this.mIsPrinterDiscoveryStarted) {
      this.mIsPrinterDiscoveryStarted = true;
    }
    try
    {
      this.mPrintManager.startPrinterDiscovery(this.mObserver, paramList, this.mUserId);
      return;
    }
    catch (RemoteException paramList)
    {
      Log.e("PrinterDiscoverySession", "Error starting printer discovery", paramList);
    }
  }
  
  public final void startPrinterStateTracking(PrinterId paramPrinterId)
  {
    if (isDestroyed())
    {
      Log.w("PrinterDiscoverySession", "Ignoring start printer state tracking - session destroyed");
      return;
    }
    try
    {
      this.mPrintManager.startPrinterStateTracking(paramPrinterId, this.mUserId);
      return;
    }
    catch (RemoteException paramPrinterId)
    {
      Log.e("PrinterDiscoverySession", "Error starting printer state tracking", paramPrinterId);
    }
  }
  
  public final void stopPrinterDiscovery()
  {
    if (isDestroyed())
    {
      Log.w("PrinterDiscoverySession", "Ignoring stop printers discovery - session destroyed");
      return;
    }
    if (this.mIsPrinterDiscoveryStarted) {
      this.mIsPrinterDiscoveryStarted = false;
    }
    try
    {
      this.mPrintManager.stopPrinterDiscovery(this.mObserver, this.mUserId);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("PrinterDiscoverySession", "Error stopping printer discovery", localRemoteException);
    }
  }
  
  public final void stopPrinterStateTracking(PrinterId paramPrinterId)
  {
    if (isDestroyed())
    {
      Log.w("PrinterDiscoverySession", "Ignoring stop printer state tracking - session destroyed");
      return;
    }
    try
    {
      this.mPrintManager.stopPrinterStateTracking(paramPrinterId, this.mUserId);
      return;
    }
    catch (RemoteException paramPrinterId)
    {
      Log.e("PrinterDiscoverySession", "Error stopping printer state tracking", paramPrinterId);
    }
  }
  
  public final void validatePrinters(List<PrinterId> paramList)
  {
    if (isDestroyed())
    {
      Log.w("PrinterDiscoverySession", "Ignoring validate printers - session destroyed");
      return;
    }
    try
    {
      this.mPrintManager.validatePrinters(paramList, this.mUserId);
      return;
    }
    catch (RemoteException paramList)
    {
      Log.e("PrinterDiscoverySession", "Error validating printers", paramList);
    }
  }
  
  public static abstract interface OnPrintersChangeListener
  {
    public abstract void onPrintersChanged();
  }
  
  public static final class PrinterDiscoveryObserver
    extends IPrinterDiscoveryObserver.Stub
  {
    private final WeakReference<PrinterDiscoverySession> mWeakSession;
    
    public PrinterDiscoveryObserver(PrinterDiscoverySession paramPrinterDiscoverySession)
    {
      this.mWeakSession = new WeakReference(paramPrinterDiscoverySession);
    }
    
    public void onPrintersAdded(ParceledListSlice paramParceledListSlice)
    {
      PrinterDiscoverySession localPrinterDiscoverySession = (PrinterDiscoverySession)this.mWeakSession.get();
      if (localPrinterDiscoverySession != null) {
        PrinterDiscoverySession.-get0(localPrinterDiscoverySession).obtainMessage(1, paramParceledListSlice.getList()).sendToTarget();
      }
    }
    
    public void onPrintersRemoved(ParceledListSlice paramParceledListSlice)
    {
      PrinterDiscoverySession localPrinterDiscoverySession = (PrinterDiscoverySession)this.mWeakSession.get();
      if (localPrinterDiscoverySession != null) {
        PrinterDiscoverySession.-get0(localPrinterDiscoverySession).obtainMessage(2, paramParceledListSlice.getList()).sendToTarget();
      }
    }
  }
  
  private final class SessionHandler
    extends Handler
  {
    public SessionHandler(Looper paramLooper)
    {
      super(null, false);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        paramMessage = (List)paramMessage.obj;
        PrinterDiscoverySession.-wrap0(PrinterDiscoverySession.this, paramMessage);
        return;
      }
      paramMessage = (List)paramMessage.obj;
      PrinterDiscoverySession.-wrap1(PrinterDiscoverySession.this, paramMessage);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrinterDiscoverySession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */