package com.android.server.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ParceledListSlice;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.IPrintService;
import android.printservice.IPrintService.Stub;
import android.printservice.IPrintServiceClient.Stub;
import android.util.Slog;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

final class RemotePrintService
  implements IBinder.DeathRecipient
{
  private static final boolean DEBUG = false;
  private static final String LOG_TAG = "RemotePrintService";
  private boolean mBinding;
  private final PrintServiceCallbacks mCallbacks;
  private final ComponentName mComponentName;
  private final Context mContext;
  private boolean mDestroyed;
  private List<PrinterId> mDiscoveryPriorityList;
  private final Handler mHandler;
  private boolean mHasActivePrintJobs;
  private boolean mHasPrinterDiscoverySession;
  private final Intent mIntent;
  private final List<Runnable> mPendingCommands = new ArrayList();
  private IPrintService mPrintService;
  private final RemotePrintServiceClient mPrintServiceClient;
  private final ServiceConnection mServiceConnection = new RemoteServiceConneciton(null);
  private boolean mServiceDied;
  private final RemotePrintSpooler mSpooler;
  private List<PrinterId> mTrackedPrinterList;
  private final int mUserId;
  
  public RemotePrintService(Context paramContext, ComponentName paramComponentName, int paramInt, RemotePrintSpooler paramRemotePrintSpooler, PrintServiceCallbacks paramPrintServiceCallbacks)
  {
    this.mContext = paramContext;
    this.mCallbacks = paramPrintServiceCallbacks;
    this.mComponentName = paramComponentName;
    this.mIntent = new Intent().setComponent(this.mComponentName);
    this.mUserId = paramInt;
    this.mSpooler = paramRemotePrintSpooler;
    this.mHandler = new MyHandler(paramContext.getMainLooper());
    this.mPrintServiceClient = new RemotePrintServiceClient(this);
  }
  
  private void ensureBound()
  {
    if ((isBound()) || (this.mBinding)) {
      return;
    }
    this.mBinding = true;
    this.mContext.bindServiceAsUser(this.mIntent, this.mServiceConnection, 67108865, new UserHandle(this.mUserId));
  }
  
  private void ensureUnbound()
  {
    if ((isBound()) || (this.mBinding))
    {
      this.mBinding = false;
      this.mPendingCommands.clear();
      this.mHasActivePrintJobs = false;
      this.mHasPrinterDiscoverySession = false;
      this.mDiscoveryPriorityList = null;
      this.mTrackedPrinterList = null;
      if (!isBound()) {}
    }
    try
    {
      this.mPrintService.setClient(null);
      this.mPrintService.asBinder().unlinkToDeath(this, 0);
      this.mPrintService = null;
      this.mContext.unbindService(this.mServiceConnection);
      return;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  private void handleBinderDied()
  {
    if (this.mPrintService != null) {
      this.mPrintService.asBinder().unlinkToDeath(this, 0);
    }
    this.mPrintService = null;
    this.mServiceDied = true;
    this.mCallbacks.onServiceDied(this);
  }
  
  private void handleCreatePrinterDiscoverySession()
  {
    throwIfDestroyed();
    this.mHasPrinterDiscoverySession = true;
    if (!isBound())
    {
      ensureBound();
      this.mPendingCommands.add(new Runnable()
      {
        public void run()
        {
          RemotePrintService.-wrap2(RemotePrintService.this);
        }
      });
      return;
    }
    try
    {
      this.mPrintService.createPrinterDiscoverySession();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("RemotePrintService", "Error creating printer discovery session.", localRemoteException);
    }
  }
  
  private void handleDestroy()
  {
    throwIfDestroyed();
    stopTrackingAllPrinters();
    if (this.mDiscoveryPriorityList != null) {
      handleStopPrinterDiscovery();
    }
    if (this.mHasPrinterDiscoverySession) {
      handleDestroyPrinterDiscoverySession();
    }
    ensureUnbound();
    this.mDestroyed = true;
  }
  
  private void handleDestroyPrinterDiscoverySession()
  {
    throwIfDestroyed();
    this.mHasPrinterDiscoverySession = false;
    if (!isBound()) {
      if ((!this.mServiceDied) || (this.mHasActivePrintJobs))
      {
        ensureBound();
        this.mPendingCommands.add(new Runnable()
        {
          public void run()
          {
            RemotePrintService.-wrap3(RemotePrintService.this);
          }
        });
      }
    }
    for (;;)
    {
      return;
      ensureUnbound();
      return;
      try
      {
        this.mPrintService.destroyPrinterDiscoverySession();
        if (this.mHasActivePrintJobs) {
          continue;
        }
        ensureUnbound();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Slog.e("RemotePrintService", "Error destroying printer dicovery session.", localRemoteException);
        }
      }
    }
  }
  
  private void handleOnAllPrintJobsHandled()
  {
    throwIfDestroyed();
    this.mHasActivePrintJobs = false;
    if (!isBound()) {
      if ((!this.mServiceDied) || (this.mHasPrinterDiscoverySession))
      {
        ensureBound();
        this.mPendingCommands.add(new Runnable()
        {
          public void run()
          {
            RemotePrintService.-wrap5(RemotePrintService.this);
          }
        });
      }
    }
    while (this.mHasPrinterDiscoverySession)
    {
      return;
      ensureUnbound();
      return;
    }
    ensureUnbound();
  }
  
  private void handleOnPrintJobQueued(final PrintJobInfo paramPrintJobInfo)
  {
    throwIfDestroyed();
    this.mHasActivePrintJobs = true;
    if (!isBound())
    {
      ensureBound();
      this.mPendingCommands.add(new Runnable()
      {
        public void run()
        {
          RemotePrintService.-wrap6(RemotePrintService.this, paramPrintJobInfo);
        }
      });
      return;
    }
    try
    {
      this.mPrintService.onPrintJobQueued(paramPrintJobInfo);
      return;
    }
    catch (RemoteException paramPrintJobInfo)
    {
      Slog.e("RemotePrintService", "Error announcing queued pring job.", paramPrintJobInfo);
    }
  }
  
  private void handleRequestCancelPrintJob(final PrintJobInfo paramPrintJobInfo)
  {
    throwIfDestroyed();
    if (!isBound())
    {
      ensureBound();
      this.mPendingCommands.add(new Runnable()
      {
        public void run()
        {
          RemotePrintService.-wrap7(RemotePrintService.this, paramPrintJobInfo);
        }
      });
      return;
    }
    try
    {
      this.mPrintService.requestCancelPrintJob(paramPrintJobInfo);
      return;
    }
    catch (RemoteException paramPrintJobInfo)
    {
      Slog.e("RemotePrintService", "Error canceling a pring job.", paramPrintJobInfo);
    }
  }
  
  private void handleStartPrinterDiscovery(final List<PrinterId> paramList)
  {
    throwIfDestroyed();
    this.mDiscoveryPriorityList = new ArrayList();
    if (paramList != null) {
      this.mDiscoveryPriorityList.addAll(paramList);
    }
    if (!isBound())
    {
      ensureBound();
      this.mPendingCommands.add(new Runnable()
      {
        public void run()
        {
          RemotePrintService.-wrap8(RemotePrintService.this, paramList);
        }
      });
      return;
    }
    try
    {
      this.mPrintService.startPrinterDiscovery(paramList);
      return;
    }
    catch (RemoteException paramList)
    {
      Slog.e("RemotePrintService", "Error starting printer dicovery.", paramList);
    }
  }
  
  private void handleStartPrinterStateTracking(final PrinterId paramPrinterId)
  {
    throwIfDestroyed();
    if (this.mTrackedPrinterList == null) {
      this.mTrackedPrinterList = new ArrayList();
    }
    this.mTrackedPrinterList.add(paramPrinterId);
    if (!isBound())
    {
      ensureBound();
      this.mPendingCommands.add(new Runnable()
      {
        public void run()
        {
          RemotePrintService.-wrap9(RemotePrintService.this, paramPrinterId);
        }
      });
      return;
    }
    try
    {
      this.mPrintService.startPrinterStateTracking(paramPrinterId);
      return;
    }
    catch (RemoteException paramPrinterId)
    {
      Slog.e("RemotePrintService", "Error requesting start printer tracking.", paramPrinterId);
    }
  }
  
  private void handleStopPrinterDiscovery()
  {
    throwIfDestroyed();
    this.mDiscoveryPriorityList = null;
    if (!isBound())
    {
      ensureBound();
      this.mPendingCommands.add(new Runnable()
      {
        public void run()
        {
          RemotePrintService.-wrap10(RemotePrintService.this);
        }
      });
      return;
    }
    stopTrackingAllPrinters();
    try
    {
      this.mPrintService.stopPrinterDiscovery();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("RemotePrintService", "Error stopping printer discovery.", localRemoteException);
    }
  }
  
  private void handleStopPrinterStateTracking(final PrinterId paramPrinterId)
  {
    throwIfDestroyed();
    if ((this.mTrackedPrinterList != null) && (this.mTrackedPrinterList.remove(paramPrinterId)))
    {
      if (this.mTrackedPrinterList.isEmpty()) {
        this.mTrackedPrinterList = null;
      }
      if (!isBound())
      {
        ensureBound();
        this.mPendingCommands.add(new Runnable()
        {
          public void run()
          {
            RemotePrintService.-wrap11(RemotePrintService.this, paramPrinterId);
          }
        });
      }
    }
    else
    {
      return;
    }
    try
    {
      this.mPrintService.stopPrinterStateTracking(paramPrinterId);
      return;
    }
    catch (RemoteException paramPrinterId)
    {
      Slog.e("RemotePrintService", "Error requesting stop printer tracking.", paramPrinterId);
    }
  }
  
  private void handleValidatePrinters(final List<PrinterId> paramList)
  {
    throwIfDestroyed();
    if (!isBound())
    {
      ensureBound();
      this.mPendingCommands.add(new Runnable()
      {
        public void run()
        {
          RemotePrintService.-wrap12(RemotePrintService.this, paramList);
        }
      });
      return;
    }
    try
    {
      this.mPrintService.validatePrinters(paramList);
      return;
    }
    catch (RemoteException paramList)
    {
      Slog.e("RemotePrintService", "Error requesting printers validation.", paramList);
    }
  }
  
  private boolean isBound()
  {
    return this.mPrintService != null;
  }
  
  private void stopTrackingAllPrinters()
  {
    if (this.mTrackedPrinterList == null) {
      return;
    }
    int i = this.mTrackedPrinterList.size() - 1;
    while (i >= 0)
    {
      PrinterId localPrinterId = (PrinterId)this.mTrackedPrinterList.get(i);
      if (localPrinterId.getServiceName().equals(this.mComponentName)) {
        handleStopPrinterStateTracking(localPrinterId);
      }
      i -= 1;
    }
  }
  
  private void throwIfDestroyed()
  {
    if (this.mDestroyed) {
      throw new IllegalStateException("Cannot interact with a destroyed service");
    }
  }
  
  public void binderDied()
  {
    this.mHandler.sendEmptyMessage(12);
  }
  
  public void createPrinterDiscoverySession()
  {
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void destroy()
  {
    this.mHandler.sendEmptyMessage(11);
  }
  
  public void destroyPrinterDiscoverySession()
  {
    this.mHandler.sendEmptyMessage(2);
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.append(paramString).append("service:").println();
    paramPrintWriter.append(paramString).append("  ").append("componentName=").append(this.mComponentName.flattenToString()).println();
    paramPrintWriter.append(paramString).append("  ").append("destroyed=").append(String.valueOf(this.mDestroyed)).println();
    paramPrintWriter.append(paramString).append("  ").append("bound=").append(String.valueOf(isBound())).println();
    paramPrintWriter.append(paramString).append("  ").append("hasDicoverySession=").append(String.valueOf(this.mHasPrinterDiscoverySession)).println();
    paramPrintWriter.append(paramString).append("  ").append("hasActivePrintJobs=").append(String.valueOf(this.mHasActivePrintJobs)).println();
    PrintWriter localPrintWriter = paramPrintWriter.append(paramString).append("  ").append("isDiscoveringPrinters=");
    boolean bool;
    if (this.mDiscoveryPriorityList != null)
    {
      bool = true;
      localPrintWriter.append(String.valueOf(bool)).println();
      paramString = paramPrintWriter.append(paramString).append("  ").append("trackedPrinters=");
      if (this.mTrackedPrinterList == null) {
        break label249;
      }
    }
    label249:
    for (paramPrintWriter = this.mTrackedPrinterList.toString();; paramPrintWriter = "null")
    {
      paramString.append(paramPrintWriter);
      return;
      bool = false;
      break;
    }
  }
  
  public ComponentName getComponentName()
  {
    return this.mComponentName;
  }
  
  public void onAllPrintJobsHandled()
  {
    this.mHandler.sendEmptyMessage(8);
  }
  
  public void onPrintJobQueued(PrintJobInfo paramPrintJobInfo)
  {
    this.mHandler.obtainMessage(10, paramPrintJobInfo).sendToTarget();
  }
  
  public void onRequestCancelPrintJob(PrintJobInfo paramPrintJobInfo)
  {
    this.mHandler.obtainMessage(9, paramPrintJobInfo).sendToTarget();
  }
  
  public void requestCustomPrinterIcon(PrinterId paramPrinterId)
  {
    try
    {
      if (isBound()) {
        this.mPrintService.requestCustomPrinterIcon(paramPrinterId);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("RemotePrintService", "Error requesting icon for " + paramPrinterId, localRemoteException);
    }
  }
  
  public void startPrinterDiscovery(List<PrinterId> paramList)
  {
    this.mHandler.obtainMessage(3, paramList).sendToTarget();
  }
  
  public void startPrinterStateTracking(PrinterId paramPrinterId)
  {
    this.mHandler.obtainMessage(6, paramPrinterId).sendToTarget();
  }
  
  public void stopPrinterDiscovery()
  {
    this.mHandler.sendEmptyMessage(4);
  }
  
  public void stopPrinterStateTracking(PrinterId paramPrinterId)
  {
    this.mHandler.obtainMessage(7, paramPrinterId).sendToTarget();
  }
  
  public void validatePrinters(List<PrinterId> paramList)
  {
    this.mHandler.obtainMessage(5, paramList).sendToTarget();
  }
  
  private final class MyHandler
    extends Handler
  {
    public static final int MSG_BINDER_DIED = 12;
    public static final int MSG_CREATE_PRINTER_DISCOVERY_SESSION = 1;
    public static final int MSG_DESTROY = 11;
    public static final int MSG_DESTROY_PRINTER_DISCOVERY_SESSION = 2;
    public static final int MSG_ON_ALL_PRINT_JOBS_HANDLED = 8;
    public static final int MSG_ON_PRINT_JOB_QUEUED = 10;
    public static final int MSG_ON_REQUEST_CANCEL_PRINT_JOB = 9;
    public static final int MSG_START_PRINTER_DISCOVERY = 3;
    public static final int MSG_START_PRINTER_STATE_TRACKING = 6;
    public static final int MSG_STOP_PRINTER_DISCOVERY = 4;
    public static final int MSG_STOP_PRINTER_STATE_TRACKING = 7;
    public static final int MSG_VALIDATE_PRINTERS = 5;
    
    public MyHandler(Looper paramLooper)
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
        RemotePrintService.-wrap2(RemotePrintService.this);
        return;
      case 2: 
        RemotePrintService.-wrap3(RemotePrintService.this);
        return;
      case 3: 
        paramMessage = (ArrayList)paramMessage.obj;
        RemotePrintService.-wrap8(RemotePrintService.this, paramMessage);
        return;
      case 4: 
        RemotePrintService.-wrap10(RemotePrintService.this);
        return;
      case 5: 
        paramMessage = (List)paramMessage.obj;
        RemotePrintService.-wrap12(RemotePrintService.this, paramMessage);
        return;
      case 6: 
        paramMessage = (PrinterId)paramMessage.obj;
        RemotePrintService.-wrap9(RemotePrintService.this, paramMessage);
        return;
      case 7: 
        paramMessage = (PrinterId)paramMessage.obj;
        RemotePrintService.-wrap11(RemotePrintService.this, paramMessage);
        return;
      case 8: 
        RemotePrintService.-wrap5(RemotePrintService.this);
        return;
      case 9: 
        paramMessage = (PrintJobInfo)paramMessage.obj;
        RemotePrintService.-wrap7(RemotePrintService.this, paramMessage);
        return;
      case 10: 
        paramMessage = (PrintJobInfo)paramMessage.obj;
        RemotePrintService.-wrap6(RemotePrintService.this, paramMessage);
        return;
      case 11: 
        RemotePrintService.-wrap4(RemotePrintService.this);
        return;
      }
      RemotePrintService.-wrap1(RemotePrintService.this);
    }
  }
  
  public static abstract interface PrintServiceCallbacks
  {
    public abstract void onCustomPrinterIconLoaded(PrinterId paramPrinterId, Icon paramIcon);
    
    public abstract void onPrintersAdded(List<PrinterInfo> paramList);
    
    public abstract void onPrintersRemoved(List<PrinterId> paramList);
    
    public abstract void onServiceDied(RemotePrintService paramRemotePrintService);
  }
  
  private static final class RemotePrintServiceClient
    extends IPrintServiceClient.Stub
  {
    private final WeakReference<RemotePrintService> mWeakService;
    
    public RemotePrintServiceClient(RemotePrintService paramRemotePrintService)
    {
      this.mWeakService = new WeakReference(paramRemotePrintService);
    }
    
    private void throwIfPrinterIdTampered(ComponentName paramComponentName, PrinterId paramPrinterId)
    {
      if ((paramPrinterId != null) && (paramPrinterId.getServiceName().equals(paramComponentName))) {
        return;
      }
      throw new IllegalArgumentException("Invalid printer id: " + paramPrinterId);
    }
    
    private void throwIfPrinterIdsForPrinterInfoTampered(ComponentName paramComponentName, List<PrinterInfo> paramList)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        throwIfPrinterIdTampered(paramComponentName, ((PrinterInfo)paramList.get(i)).getId());
        i += 1;
      }
    }
    
    private void throwIfPrinterIdsTampered(ComponentName paramComponentName, List<PrinterId> paramList)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        throwIfPrinterIdTampered(paramComponentName, (PrinterId)paramList.get(i));
        i += 1;
      }
    }
    
    public PrintJobInfo getPrintJobInfo(PrintJobId paramPrintJobId)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      if (localRemotePrintService != null)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          paramPrintJobId = RemotePrintService.-get13(localRemotePrintService).getPrintJobInfo(paramPrintJobId, -2);
          return paramPrintJobId;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
      return null;
    }
    
    public List<PrintJobInfo> getPrintJobInfos()
    {
      Object localObject1 = (RemotePrintService)this.mWeakService.get();
      if (localObject1 != null)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          localObject1 = RemotePrintService.-get13((RemotePrintService)localObject1).getPrintJobInfos(RemotePrintService.-get2((RemotePrintService)localObject1), -4, -2);
          return (List<PrintJobInfo>)localObject1;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
      return null;
    }
    
    public void onCustomPrinterIconLoaded(PrinterId paramPrinterId, Icon paramIcon)
      throws RemoteException
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      long l;
      if (localRemotePrintService != null) {
        l = Binder.clearCallingIdentity();
      }
      try
      {
        RemotePrintService.-get1(localRemotePrintService).onCustomPrinterIconLoaded(paramPrinterId, paramIcon);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void onPrintersAdded(ParceledListSlice paramParceledListSlice)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      long l;
      if (localRemotePrintService != null)
      {
        paramParceledListSlice = paramParceledListSlice.getList();
        throwIfPrinterIdsForPrinterInfoTampered(RemotePrintService.-get2(localRemotePrintService), paramParceledListSlice);
        l = Binder.clearCallingIdentity();
      }
      try
      {
        RemotePrintService.-get1(localRemotePrintService).onPrintersAdded(paramParceledListSlice);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void onPrintersRemoved(ParceledListSlice paramParceledListSlice)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      long l;
      if (localRemotePrintService != null)
      {
        paramParceledListSlice = paramParceledListSlice.getList();
        throwIfPrinterIdsTampered(RemotePrintService.-get2(localRemotePrintService), paramParceledListSlice);
        l = Binder.clearCallingIdentity();
      }
      try
      {
        RemotePrintService.-get1(localRemotePrintService).onPrintersRemoved(paramParceledListSlice);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean setPrintJobState(PrintJobId paramPrintJobId, int paramInt, String paramString)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      if (localRemotePrintService != null)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          boolean bool = RemotePrintService.-get13(localRemotePrintService).setPrintJobState(paramPrintJobId, paramInt, paramString);
          return bool;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
      return false;
    }
    
    public boolean setPrintJobTag(PrintJobId paramPrintJobId, String paramString)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      if (localRemotePrintService != null)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          boolean bool = RemotePrintService.-get13(localRemotePrintService).setPrintJobTag(paramPrintJobId, paramString);
          return bool;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
      return false;
    }
    
    public void setProgress(PrintJobId paramPrintJobId, float paramFloat)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      long l;
      if (localRemotePrintService != null) {
        l = Binder.clearCallingIdentity();
      }
      try
      {
        RemotePrintService.-get13(localRemotePrintService).setProgress(paramPrintJobId, paramFloat);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setStatus(PrintJobId paramPrintJobId, CharSequence paramCharSequence)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      long l;
      if (localRemotePrintService != null) {
        l = Binder.clearCallingIdentity();
      }
      try
      {
        RemotePrintService.-get13(localRemotePrintService).setStatus(paramPrintJobId, paramCharSequence);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setStatusRes(PrintJobId paramPrintJobId, int paramInt, CharSequence paramCharSequence)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      long l;
      if (localRemotePrintService != null) {
        l = Binder.clearCallingIdentity();
      }
      try
      {
        RemotePrintService.-get13(localRemotePrintService).setStatus(paramPrintJobId, paramInt, paramCharSequence);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void writePrintJobData(ParcelFileDescriptor paramParcelFileDescriptor, PrintJobId paramPrintJobId)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mWeakService.get();
      long l;
      if (localRemotePrintService != null) {
        l = Binder.clearCallingIdentity();
      }
      try
      {
        RemotePrintService.-get13(localRemotePrintService).writePrintJobData(paramParcelFileDescriptor, paramPrintJobId);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  private class RemoteServiceConneciton
    implements ServiceConnection
  {
    private RemoteServiceConneciton() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      if ((!RemotePrintService.-get4(RemotePrintService.this)) && (RemotePrintService.-get0(RemotePrintService.this)))
      {
        RemotePrintService.-set0(RemotePrintService.this, false);
        RemotePrintService.-set1(RemotePrintService.this, IPrintService.Stub.asInterface(paramIBinder));
      }
      try
      {
        paramIBinder.linkToDeath(RemotePrintService.this, 0);
        int j;
        int i;
        if (RemotePrintService.-get8(RemotePrintService.this).isEmpty()) {
          break label305;
        }
      }
      catch (RemoteException paramComponentName)
      {
        try
        {
          RemotePrintService.-get9(RemotePrintService.this).setClient(RemotePrintService.-get10(RemotePrintService.this));
          if ((RemotePrintService.-get12(RemotePrintService.this)) && (RemotePrintService.-get7(RemotePrintService.this))) {
            RemotePrintService.-wrap2(RemotePrintService.this);
          }
          if ((RemotePrintService.-get12(RemotePrintService.this)) && (RemotePrintService.-get5(RemotePrintService.this) != null)) {
            RemotePrintService.-wrap8(RemotePrintService.this, RemotePrintService.-get5(RemotePrintService.this));
          }
          if ((!RemotePrintService.-get12(RemotePrintService.this)) || (RemotePrintService.-get14(RemotePrintService.this) == null)) {
            break label266;
          }
          j = RemotePrintService.-get14(RemotePrintService.this).size();
          i = 0;
          while (i < j)
          {
            RemotePrintService.-wrap9(RemotePrintService.this, (PrinterId)RemotePrintService.-get14(RemotePrintService.this).get(i));
            i += 1;
          }
          RemotePrintService.-get3(RemotePrintService.this).unbindService(RemotePrintService.-get11(RemotePrintService.this));
          return;
        }
        catch (RemoteException paramComponentName)
        {
          Slog.e("RemotePrintService", "Error setting client for: " + paramIBinder, paramComponentName);
          RemotePrintService.-wrap1(RemotePrintService.this);
          return;
        }
        paramComponentName = paramComponentName;
        RemotePrintService.-wrap1(RemotePrintService.this);
        return;
      }
      for (;;)
      {
        label266:
        ((Runnable)RemotePrintService.-get8(RemotePrintService.this).remove(0)).run();
      }
      label305:
      if ((RemotePrintService.-get7(RemotePrintService.this)) || (RemotePrintService.-get6(RemotePrintService.this))) {}
      for (;;)
      {
        RemotePrintService.-set2(RemotePrintService.this, false);
        return;
        RemotePrintService.-wrap0(RemotePrintService.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      RemotePrintService.-set0(RemotePrintService.this, true);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/print/RemotePrintService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */