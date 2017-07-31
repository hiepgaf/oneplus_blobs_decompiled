package android.printservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PrintService
  extends Service
{
  private static final boolean DEBUG = false;
  public static final String EXTRA_PRINTER_INFO = "android.intent.extra.print.EXTRA_PRINTER_INFO";
  public static final String EXTRA_PRINT_DOCUMENT_INFO = "android.printservice.extra.PRINT_DOCUMENT_INFO";
  public static final String EXTRA_PRINT_JOB_INFO = "android.intent.extra.print.PRINT_JOB_INFO";
  private static final String LOG_TAG = "PrintService";
  public static final String SERVICE_INTERFACE = "android.printservice.PrintService";
  public static final String SERVICE_META_DATA = "android.printservice";
  private IPrintServiceClient mClient;
  private PrinterDiscoverySession mDiscoverySession;
  private Handler mHandler;
  private int mLastSessionId = -1;
  
  static void throwIfNotCalledOnMainThread()
  {
    if (!Looper.getMainLooper().isCurrentThread()) {
      throw new IllegalAccessError("must be called from the main thread");
    }
  }
  
  protected final void attachBaseContext(Context paramContext)
  {
    super.attachBaseContext(paramContext);
    this.mHandler = new ServiceHandler(paramContext.getMainLooper());
  }
  
  public final PrinterId generatePrinterId(String paramString)
  {
    throwIfNotCalledOnMainThread();
    paramString = (String)Preconditions.checkNotNull(paramString, "localId cannot be null");
    return new PrinterId(new ComponentName(getPackageName(), getClass().getName()), paramString);
  }
  
  public final List<PrintJob> getActivePrintJobs()
  {
    
    if (this.mClient == null) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = null;
    for (;;)
    {
      List localList;
      int i;
      try
      {
        localList = this.mClient.getPrintJobInfos();
        if (localList != null)
        {
          int j = localList.size();
          localArrayList = new ArrayList(j);
          i = 0;
          if (i >= j) {}
        }
      }
      catch (RemoteException localRemoteException1) {}
      try
      {
        localArrayList.add(new PrintJob(this, (PrintJobInfo)localList.get(i), this.mClient));
        i += 1;
      }
      catch (RemoteException localRemoteException2)
      {
        for (;;) {}
      }
    }
    if (localArrayList != null)
    {
      return localArrayList;
      Log.e("PrintService", "Error calling getPrintJobs()", localRemoteException1);
    }
    return Collections.emptyList();
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    new IPrintService.Stub()
    {
      public void createPrinterDiscoverySession()
      {
        PrintService.-get2(PrintService.this).sendEmptyMessage(1);
      }
      
      public void destroyPrinterDiscoverySession()
      {
        PrintService.-get2(PrintService.this).sendEmptyMessage(2);
      }
      
      public void onPrintJobQueued(PrintJobInfo paramAnonymousPrintJobInfo)
      {
        PrintService.-get2(PrintService.this).obtainMessage(9, paramAnonymousPrintJobInfo).sendToTarget();
      }
      
      public void requestCancelPrintJob(PrintJobInfo paramAnonymousPrintJobInfo)
      {
        PrintService.-get2(PrintService.this).obtainMessage(10, paramAnonymousPrintJobInfo).sendToTarget();
      }
      
      public void requestCustomPrinterIcon(PrinterId paramAnonymousPrinterId)
      {
        PrintService.-get2(PrintService.this).obtainMessage(7, paramAnonymousPrinterId).sendToTarget();
      }
      
      public void setClient(IPrintServiceClient paramAnonymousIPrintServiceClient)
      {
        PrintService.-get2(PrintService.this).obtainMessage(11, paramAnonymousIPrintServiceClient).sendToTarget();
      }
      
      public void startPrinterDiscovery(List<PrinterId> paramAnonymousList)
      {
        PrintService.-get2(PrintService.this).obtainMessage(3, paramAnonymousList).sendToTarget();
      }
      
      public void startPrinterStateTracking(PrinterId paramAnonymousPrinterId)
      {
        PrintService.-get2(PrintService.this).obtainMessage(6, paramAnonymousPrinterId).sendToTarget();
      }
      
      public void stopPrinterDiscovery()
      {
        PrintService.-get2(PrintService.this).sendEmptyMessage(4);
      }
      
      public void stopPrinterStateTracking(PrinterId paramAnonymousPrinterId)
      {
        PrintService.-get2(PrintService.this).obtainMessage(8, paramAnonymousPrinterId).sendToTarget();
      }
      
      public void validatePrinters(List<PrinterId> paramAnonymousList)
      {
        PrintService.-get2(PrintService.this).obtainMessage(5, paramAnonymousList).sendToTarget();
      }
    };
  }
  
  protected void onConnected() {}
  
  protected abstract PrinterDiscoverySession onCreatePrinterDiscoverySession();
  
  protected void onDisconnected() {}
  
  protected abstract void onPrintJobQueued(PrintJob paramPrintJob);
  
  protected abstract void onRequestCancelPrintJob(PrintJob paramPrintJob);
  
  private final class ServiceHandler
    extends Handler
  {
    public static final int MSG_CREATE_PRINTER_DISCOVERY_SESSION = 1;
    public static final int MSG_DESTROY_PRINTER_DISCOVERY_SESSION = 2;
    public static final int MSG_ON_PRINTJOB_QUEUED = 9;
    public static final int MSG_ON_REQUEST_CANCEL_PRINTJOB = 10;
    public static final int MSG_REQUEST_CUSTOM_PRINTER_ICON = 7;
    public static final int MSG_SET_CLIENT = 11;
    public static final int MSG_START_PRINTER_DISCOVERY = 3;
    public static final int MSG_START_PRINTER_STATE_TRACKING = 6;
    public static final int MSG_STOP_PRINTER_DISCOVERY = 4;
    public static final int MSG_STOP_PRINTER_STATE_TRACKING = 8;
    public static final int MSG_VALIDATE_PRINTERS = 5;
    
    public ServiceHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Unknown message: " + i);
      case 1: 
        paramMessage = PrintService.this.onCreatePrinterDiscoverySession();
        if (paramMessage == null) {
          throw new NullPointerException("session cannot be null");
        }
        if (paramMessage.getId() == PrintService.-get3(PrintService.this)) {
          throw new IllegalStateException("cannot reuse session instances");
        }
        PrintService.-set1(PrintService.this, paramMessage);
        PrintService.-set2(PrintService.this, paramMessage.getId());
        paramMessage.setObserver(PrintService.-get0(PrintService.this));
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
      case 8: 
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      return;
                    } while (PrintService.-get1(PrintService.this) == null);
                    PrintService.-get1(PrintService.this).destroy();
                    PrintService.-set1(PrintService.this, null);
                    return;
                  } while (PrintService.-get1(PrintService.this) == null);
                  paramMessage = (ArrayList)paramMessage.obj;
                  PrintService.-get1(PrintService.this).startPrinterDiscovery(paramMessage);
                  return;
                } while (PrintService.-get1(PrintService.this) == null);
                PrintService.-get1(PrintService.this).stopPrinterDiscovery();
                return;
              } while (PrintService.-get1(PrintService.this) == null);
              paramMessage = (List)paramMessage.obj;
              PrintService.-get1(PrintService.this).validatePrinters(paramMessage);
              return;
            } while (PrintService.-get1(PrintService.this) == null);
            paramMessage = (PrinterId)paramMessage.obj;
            PrintService.-get1(PrintService.this).startPrinterStateTracking(paramMessage);
            return;
          } while (PrintService.-get1(PrintService.this) == null);
          paramMessage = (PrinterId)paramMessage.obj;
          PrintService.-get1(PrintService.this).requestCustomPrinterIcon(paramMessage);
          return;
        } while (PrintService.-get1(PrintService.this) == null);
        paramMessage = (PrinterId)paramMessage.obj;
        PrintService.-get1(PrintService.this).stopPrinterStateTracking(paramMessage);
        return;
      case 10: 
        paramMessage = (PrintJobInfo)paramMessage.obj;
        PrintService.this.onRequestCancelPrintJob(new PrintJob(PrintService.this, paramMessage, PrintService.-get0(PrintService.this)));
        return;
      case 9: 
        paramMessage = (PrintJobInfo)paramMessage.obj;
        PrintService.this.onPrintJobQueued(new PrintJob(PrintService.this, paramMessage, PrintService.-get0(PrintService.this)));
        return;
      }
      PrintService.-set0(PrintService.this, (IPrintServiceClient)paramMessage.obj);
      if (PrintService.-get0(PrintService.this) != null)
      {
        PrintService.this.onConnected();
        return;
      }
      PrintService.this.onDisconnected();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/PrintService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */