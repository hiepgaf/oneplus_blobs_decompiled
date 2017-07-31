package android.print;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.printservice.PrintServiceInfo;
import android.printservice.recommendation.IRecommendationsChangeListener.Stub;
import android.printservice.recommendation.RecommendationInfo;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.Preconditions;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import libcore.io.IoUtils;

public final class PrintManager
{
  public static final String ACTION_PRINT_DIALOG = "android.print.PRINT_DIALOG";
  public static final int ALL_SERVICES = 3;
  public static final int APP_ID_ANY = -2;
  private static final boolean DEBUG = false;
  public static final int DISABLED_SERVICES = 2;
  public static final int ENABLED_SERVICES = 1;
  public static final String EXTRA_PRINT_DIALOG_INTENT = "android.print.intent.extra.EXTRA_PRINT_DIALOG_INTENT";
  public static final String EXTRA_PRINT_DOCUMENT_ADAPTER = "android.print.intent.extra.EXTRA_PRINT_DOCUMENT_ADAPTER";
  public static final String EXTRA_PRINT_JOB = "android.print.intent.extra.EXTRA_PRINT_JOB";
  private static final String LOG_TAG = "PrintManager";
  private static final int MSG_NOTIFY_PRINT_JOB_STATE_CHANGED = 1;
  private static final int MSG_NOTIFY_PRINT_SERVICES_CHANGED = 2;
  private static final int MSG_NOTIFY_PRINT_SERVICE_RECOMMENDATIONS_CHANGED = 3;
  public static final String PRINT_SPOOLER_PACKAGE_NAME = "com.android.printspooler";
  private final int mAppId;
  private final Context mContext;
  private final Handler mHandler;
  private Map<PrintJobStateChangeListener, PrintJobStateChangeListenerWrapper> mPrintJobStateChangeListeners;
  private Map<PrintServiceRecommendationsChangeListener, PrintServiceRecommendationsChangeListenerWrapper> mPrintServiceRecommendationsChangeListeners;
  private Map<PrintServicesChangeListener, PrintServicesChangeListenerWrapper> mPrintServicesChangeListeners;
  private final IPrintManager mService;
  private final int mUserId;
  
  public PrintManager(Context paramContext, IPrintManager paramIPrintManager, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    this.mService = paramIPrintManager;
    this.mUserId = paramInt1;
    this.mAppId = paramInt2;
    this.mHandler = new Handler(paramContext.getMainLooper(), null, false)
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        }
        do
        {
          do
          {
            return;
            paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
            PrintManager.PrintJobStateChangeListener localPrintJobStateChangeListener = ((PrintManager.PrintJobStateChangeListenerWrapper)paramAnonymousMessage.arg1).getListener();
            if (localPrintJobStateChangeListener != null) {
              localPrintJobStateChangeListener.onPrintJobStateChanged((PrintJobId)paramAnonymousMessage.arg2);
            }
            paramAnonymousMessage.recycle();
            return;
            paramAnonymousMessage = ((PrintManager.PrintServicesChangeListenerWrapper)paramAnonymousMessage.obj).getListener();
          } while (paramAnonymousMessage == null);
          paramAnonymousMessage.onPrintServicesChanged();
          return;
          paramAnonymousMessage = ((PrintManager.PrintServiceRecommendationsChangeListenerWrapper)paramAnonymousMessage.obj).getListener();
        } while (paramAnonymousMessage == null);
        paramAnonymousMessage.onPrintServiceRecommendationsChanged();
      }
    };
  }
  
  public void addPrintJobStateChangeListener(PrintJobStateChangeListener paramPrintJobStateChangeListener)
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return;
    }
    if (this.mPrintJobStateChangeListeners == null) {
      this.mPrintJobStateChangeListeners = new ArrayMap();
    }
    PrintJobStateChangeListenerWrapper localPrintJobStateChangeListenerWrapper = new PrintJobStateChangeListenerWrapper(paramPrintJobStateChangeListener, this.mHandler);
    try
    {
      this.mService.addPrintJobStateChangeListener(localPrintJobStateChangeListenerWrapper, this.mAppId, this.mUserId);
      this.mPrintJobStateChangeListeners.put(paramPrintJobStateChangeListener, localPrintJobStateChangeListenerWrapper);
      return;
    }
    catch (RemoteException paramPrintJobStateChangeListener)
    {
      throw paramPrintJobStateChangeListener.rethrowFromSystemServer();
    }
  }
  
  void addPrintServiceRecommendationsChangeListener(PrintServiceRecommendationsChangeListener paramPrintServiceRecommendationsChangeListener)
  {
    Preconditions.checkNotNull(paramPrintServiceRecommendationsChangeListener);
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return;
    }
    if (this.mPrintServiceRecommendationsChangeListeners == null) {
      this.mPrintServiceRecommendationsChangeListeners = new ArrayMap();
    }
    PrintServiceRecommendationsChangeListenerWrapper localPrintServiceRecommendationsChangeListenerWrapper = new PrintServiceRecommendationsChangeListenerWrapper(paramPrintServiceRecommendationsChangeListener, this.mHandler);
    try
    {
      this.mService.addPrintServiceRecommendationsChangeListener(localPrintServiceRecommendationsChangeListenerWrapper, this.mUserId);
      this.mPrintServiceRecommendationsChangeListeners.put(paramPrintServiceRecommendationsChangeListener, localPrintServiceRecommendationsChangeListenerWrapper);
      return;
    }
    catch (RemoteException paramPrintServiceRecommendationsChangeListener)
    {
      throw paramPrintServiceRecommendationsChangeListener.rethrowFromSystemServer();
    }
  }
  
  void addPrintServicesChangeListener(PrintServicesChangeListener paramPrintServicesChangeListener)
  {
    Preconditions.checkNotNull(paramPrintServicesChangeListener);
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return;
    }
    if (this.mPrintServicesChangeListeners == null) {
      this.mPrintServicesChangeListeners = new ArrayMap();
    }
    PrintServicesChangeListenerWrapper localPrintServicesChangeListenerWrapper = new PrintServicesChangeListenerWrapper(paramPrintServicesChangeListener, this.mHandler);
    try
    {
      this.mService.addPrintServicesChangeListener(localPrintServicesChangeListenerWrapper, this.mUserId);
      this.mPrintServicesChangeListeners.put(paramPrintServicesChangeListener, localPrintServicesChangeListenerWrapper);
      return;
    }
    catch (RemoteException paramPrintServicesChangeListener)
    {
      throw paramPrintServicesChangeListener.rethrowFromSystemServer();
    }
  }
  
  void cancelPrintJob(PrintJobId paramPrintJobId)
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return;
    }
    try
    {
      this.mService.cancelPrintJob(paramPrintJobId, this.mAppId, this.mUserId);
      return;
    }
    catch (RemoteException paramPrintJobId)
    {
      throw paramPrintJobId.rethrowFromSystemServer();
    }
  }
  
  public PrinterDiscoverySession createPrinterDiscoverySession()
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return null;
    }
    return new PrinterDiscoverySession(this.mService, this.mContext, this.mUserId);
  }
  
  public Icon getCustomPrinterIcon(PrinterId paramPrinterId)
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return null;
    }
    try
    {
      paramPrinterId = this.mService.getCustomPrinterIcon(paramPrinterId, this.mUserId);
      return paramPrinterId;
    }
    catch (RemoteException paramPrinterId)
    {
      throw paramPrinterId.rethrowFromSystemServer();
    }
  }
  
  public PrintManager getGlobalPrintManagerForUser(int paramInt)
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return null;
    }
    return new PrintManager(this.mContext, this.mService, paramInt, -2);
  }
  
  public PrintJob getPrintJob(PrintJobId paramPrintJobId)
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return null;
    }
    try
    {
      paramPrintJobId = this.mService.getPrintJobInfo(paramPrintJobId, this.mAppId, this.mUserId);
      if (paramPrintJobId != null)
      {
        paramPrintJobId = new PrintJob(paramPrintJobId, this);
        return paramPrintJobId;
      }
    }
    catch (RemoteException paramPrintJobId)
    {
      throw paramPrintJobId.rethrowFromSystemServer();
    }
    return null;
  }
  
  PrintJobInfo getPrintJobInfo(PrintJobId paramPrintJobId)
  {
    try
    {
      paramPrintJobId = this.mService.getPrintJobInfo(paramPrintJobId, this.mAppId, this.mUserId);
      return paramPrintJobId;
    }
    catch (RemoteException paramPrintJobId)
    {
      throw paramPrintJobId.rethrowFromSystemServer();
    }
  }
  
  public List<PrintJob> getPrintJobs()
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return Collections.emptyList();
    }
    try
    {
      List localList = this.mService.getPrintJobInfos(this.mAppId, this.mUserId);
      if (localList == null) {
        return Collections.emptyList();
      }
      int j = localList.size();
      ArrayList localArrayList = new ArrayList(j);
      int i = 0;
      while (i < j)
      {
        localArrayList.add(new PrintJob((PrintJobInfo)localList.get(i), this));
        i += 1;
      }
      return localArrayList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<RecommendationInfo> getPrintServiceRecommendations()
  {
    try
    {
      List localList = this.mService.getPrintServiceRecommendations(this.mUserId);
      if (localList != null) {
        return localList;
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    return Collections.emptyList();
  }
  
  public List<PrintServiceInfo> getPrintServices(int paramInt)
  {
    Preconditions.checkFlagsArgument(paramInt, 3);
    try
    {
      List localList = this.mService.getPrintServices(paramInt, this.mUserId);
      if (localList != null) {
        return localList;
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    return Collections.emptyList();
  }
  
  public PrintJob print(String paramString, PrintDocumentAdapter paramPrintDocumentAdapter, PrintAttributes paramPrintAttributes)
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return null;
    }
    if (!(this.mContext instanceof Activity)) {
      throw new IllegalStateException("Can print only from an activity");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("printJobName cannot be empty");
    }
    if (paramPrintDocumentAdapter == null) {
      throw new IllegalArgumentException("documentAdapter cannot be null");
    }
    paramPrintDocumentAdapter = new PrintDocumentAdapterDelegate((Activity)this.mContext, paramPrintDocumentAdapter);
    try
    {
      paramPrintDocumentAdapter = this.mService.print(paramString, paramPrintDocumentAdapter, paramPrintAttributes, this.mContext.getPackageName(), this.mAppId, this.mUserId);
      if (paramPrintDocumentAdapter != null)
      {
        paramString = (PrintJobInfo)paramPrintDocumentAdapter.getParcelable("android.print.intent.extra.EXTRA_PRINT_JOB");
        paramPrintDocumentAdapter = (IntentSender)paramPrintDocumentAdapter.getParcelable("android.print.intent.extra.EXTRA_PRINT_DIALOG_INTENT");
        if ((paramString == null) || (paramPrintDocumentAdapter == null)) {
          return null;
        }
        try
        {
          this.mContext.startIntentSender(paramPrintDocumentAdapter, null, 0, 0, 0);
          paramString = new PrintJob(paramString, this);
          return paramString;
        }
        catch (IntentSender.SendIntentException paramString)
        {
          Log.e("PrintManager", "Couldn't start print job config activity.", paramString);
        }
      }
      return null;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void removePrintJobStateChangeListener(PrintJobStateChangeListener paramPrintJobStateChangeListener)
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return;
    }
    if (this.mPrintJobStateChangeListeners == null) {
      return;
    }
    paramPrintJobStateChangeListener = (PrintJobStateChangeListenerWrapper)this.mPrintJobStateChangeListeners.remove(paramPrintJobStateChangeListener);
    if (paramPrintJobStateChangeListener == null) {
      return;
    }
    if (this.mPrintJobStateChangeListeners.isEmpty()) {
      this.mPrintJobStateChangeListeners = null;
    }
    paramPrintJobStateChangeListener.destroy();
    try
    {
      this.mService.removePrintJobStateChangeListener(paramPrintJobStateChangeListener, this.mUserId);
      return;
    }
    catch (RemoteException paramPrintJobStateChangeListener)
    {
      throw paramPrintJobStateChangeListener.rethrowFromSystemServer();
    }
  }
  
  void removePrintServiceRecommendationsChangeListener(PrintServiceRecommendationsChangeListener paramPrintServiceRecommendationsChangeListener)
  {
    Preconditions.checkNotNull(paramPrintServiceRecommendationsChangeListener);
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return;
    }
    if (this.mPrintServiceRecommendationsChangeListeners == null) {
      return;
    }
    paramPrintServiceRecommendationsChangeListener = (PrintServiceRecommendationsChangeListenerWrapper)this.mPrintServiceRecommendationsChangeListeners.remove(paramPrintServiceRecommendationsChangeListener);
    if (paramPrintServiceRecommendationsChangeListener == null) {
      return;
    }
    if (this.mPrintServiceRecommendationsChangeListeners.isEmpty()) {
      this.mPrintServiceRecommendationsChangeListeners = null;
    }
    paramPrintServiceRecommendationsChangeListener.destroy();
    try
    {
      this.mService.removePrintServiceRecommendationsChangeListener(paramPrintServiceRecommendationsChangeListener, this.mUserId);
      return;
    }
    catch (RemoteException paramPrintServiceRecommendationsChangeListener)
    {
      throw paramPrintServiceRecommendationsChangeListener.rethrowFromSystemServer();
    }
  }
  
  void removePrintServicesChangeListener(PrintServicesChangeListener paramPrintServicesChangeListener)
  {
    Preconditions.checkNotNull(paramPrintServicesChangeListener);
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return;
    }
    if (this.mPrintServicesChangeListeners == null) {
      return;
    }
    paramPrintServicesChangeListener = (PrintServicesChangeListenerWrapper)this.mPrintServicesChangeListeners.remove(paramPrintServicesChangeListener);
    if (paramPrintServicesChangeListener == null) {
      return;
    }
    if (this.mPrintServicesChangeListeners.isEmpty()) {
      this.mPrintServicesChangeListeners = null;
    }
    paramPrintServicesChangeListener.destroy();
    try
    {
      this.mService.removePrintServicesChangeListener(paramPrintServicesChangeListener, this.mUserId);
      return;
    }
    catch (RemoteException paramPrintServicesChangeListener)
    {
      Log.e("PrintManager", "Error removing print services change listener", paramPrintServicesChangeListener);
    }
  }
  
  void restartPrintJob(PrintJobId paramPrintJobId)
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return;
    }
    try
    {
      this.mService.restartPrintJob(paramPrintJobId, this.mAppId, this.mUserId);
      return;
    }
    catch (RemoteException paramPrintJobId)
    {
      throw paramPrintJobId.rethrowFromSystemServer();
    }
  }
  
  public void setPrintServiceEnabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (this.mService == null)
    {
      Log.w("PrintManager", "Feature android.software.print not available");
      return;
    }
    try
    {
      this.mService.setPrintServiceEnabled(paramComponentName, paramBoolean, this.mUserId);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("PrintManager", "Error enabling or disabling " + paramComponentName, localRemoteException);
    }
  }
  
  public static final class PrintDocumentAdapterDelegate
    extends IPrintDocumentAdapter.Stub
    implements Application.ActivityLifecycleCallbacks
  {
    private Activity mActivity;
    private PrintDocumentAdapter mDocumentAdapter;
    private Handler mHandler;
    private final Object mLock = new Object();
    private IPrintDocumentAdapterObserver mObserver;
    private DestroyableCallback mPendingCallback;
    
    public PrintDocumentAdapterDelegate(Activity paramActivity, PrintDocumentAdapter paramPrintDocumentAdapter)
    {
      if (paramActivity.isFinishing()) {
        throw new IllegalStateException("Cannot start printing for finishing activity");
      }
      this.mActivity = paramActivity;
      this.mDocumentAdapter = paramPrintDocumentAdapter;
      this.mHandler = new MyHandler(this.mActivity.getMainLooper());
      this.mActivity.getApplication().registerActivityLifecycleCallbacks(this);
    }
    
    private void destroyLocked()
    {
      this.mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
      this.mActivity = null;
      this.mDocumentAdapter = null;
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      this.mHandler.removeMessages(4);
      this.mHandler = null;
      this.mObserver = null;
      if (this.mPendingCallback != null)
      {
        this.mPendingCallback.destroy();
        this.mPendingCallback = null;
      }
    }
    
    private boolean isDestroyedLocked()
    {
      return this.mActivity == null;
    }
    
    public void finish()
    {
      synchronized (this.mLock)
      {
        if (!isDestroyedLocked()) {
          this.mHandler.obtainMessage(4, this.mDocumentAdapter).sendToTarget();
        }
        return;
      }
    }
    
    public void kill(String paramString)
    {
      synchronized (this.mLock)
      {
        if (!isDestroyedLocked()) {
          this.mHandler.obtainMessage(5, paramString).sendToTarget();
        }
        return;
      }
    }
    
    /* Error */
    public void layout(PrintAttributes paramPrintAttributes1, PrintAttributes paramPrintAttributes2, ILayoutResultCallback paramILayoutResultCallback, Bundle paramBundle, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 131	android/os/CancellationSignal:createTransport	()Landroid/os/ICancellationSignal;
      //   3: astore 8
      //   5: aload_3
      //   6: aload 8
      //   8: iload 5
      //   10: invokeinterface 137 3 0
      //   15: aload_0
      //   16: getfield 37	android/print/PrintManager$PrintDocumentAdapterDelegate:mLock	Ljava/lang/Object;
      //   19: astore 7
      //   21: aload 7
      //   23: monitorenter
      //   24: aload_0
      //   25: invokespecial 111	android/print/PrintManager$PrintDocumentAdapterDelegate:isDestroyedLocked	()Z
      //   28: istore 6
      //   30: iload 6
      //   32: ifeq +18 -> 50
      //   35: aload 7
      //   37: monitorexit
      //   38: return
      //   39: astore_1
      //   40: ldc -117
      //   42: ldc -115
      //   44: aload_1
      //   45: invokestatic 147	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   48: pop
      //   49: return
      //   50: aload 8
      //   52: invokestatic 151	android/os/CancellationSignal:fromTransport	(Landroid/os/ICancellationSignal;)Landroid/os/CancellationSignal;
      //   55: astore 8
      //   57: invokestatic 157	com/android/internal/os/SomeArgs:obtain	()Lcom/android/internal/os/SomeArgs;
      //   60: astore 9
      //   62: aload 9
      //   64: aload_0
      //   65: getfield 72	android/print/PrintManager$PrintDocumentAdapterDelegate:mDocumentAdapter	Landroid/print/PrintDocumentAdapter;
      //   68: putfield 160	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
      //   71: aload 9
      //   73: aload_1
      //   74: putfield 163	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
      //   77: aload 9
      //   79: aload_2
      //   80: putfield 166	com/android/internal/os/SomeArgs:arg3	Ljava/lang/Object;
      //   83: aload 9
      //   85: aload 8
      //   87: putfield 169	com/android/internal/os/SomeArgs:arg4	Ljava/lang/Object;
      //   90: aload 9
      //   92: new 17	android/print/PrintManager$PrintDocumentAdapterDelegate$MyLayoutResultCallback
      //   95: dup
      //   96: aload_0
      //   97: aload_3
      //   98: iload 5
      //   100: invokespecial 172	android/print/PrintManager$PrintDocumentAdapterDelegate$MyLayoutResultCallback:<init>	(Landroid/print/PrintManager$PrintDocumentAdapterDelegate;Landroid/print/ILayoutResultCallback;I)V
      //   103: putfield 175	com/android/internal/os/SomeArgs:arg5	Ljava/lang/Object;
      //   106: aload 9
      //   108: aload 4
      //   110: putfield 178	com/android/internal/os/SomeArgs:arg6	Ljava/lang/Object;
      //   113: aload_0
      //   114: getfield 83	android/print/PrintManager$PrintDocumentAdapterDelegate:mHandler	Landroid/os/Handler;
      //   117: iconst_2
      //   118: aload 9
      //   120: invokevirtual 115	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   123: invokevirtual 120	android/os/Message:sendToTarget	()V
      //   126: aload 7
      //   128: monitorexit
      //   129: return
      //   130: astore_1
      //   131: aload 7
      //   133: monitorexit
      //   134: aload_1
      //   135: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	136	0	this	PrintDocumentAdapterDelegate
      //   0	136	1	paramPrintAttributes1	PrintAttributes
      //   0	136	2	paramPrintAttributes2	PrintAttributes
      //   0	136	3	paramILayoutResultCallback	ILayoutResultCallback
      //   0	136	4	paramBundle	Bundle
      //   0	136	5	paramInt	int
      //   28	3	6	bool	boolean
      //   3	83	8	localObject2	Object
      //   60	59	9	localSomeArgs	SomeArgs
      // Exception table:
      //   from	to	target	type
      //   5	15	39	android/os/RemoteException
      //   24	30	130	finally
      //   50	126	130	finally
    }
    
    public void onActivityCreated(Activity paramActivity, Bundle paramBundle) {}
    
    public void onActivityDestroyed(Activity paramActivity)
    {
      IPrintDocumentAdapterObserver localIPrintDocumentAdapterObserver = null;
      synchronized (this.mLock)
      {
        if (paramActivity == this.mActivity)
        {
          localIPrintDocumentAdapterObserver = this.mObserver;
          destroyLocked();
        }
        if (localIPrintDocumentAdapterObserver == null) {}
      }
    }
    
    public void onActivityPaused(Activity paramActivity) {}
    
    public void onActivityResumed(Activity paramActivity) {}
    
    public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
    
    public void onActivityStarted(Activity paramActivity) {}
    
    public void onActivityStopped(Activity paramActivity) {}
    
    public void setObserver(IPrintDocumentAdapterObserver paramIPrintDocumentAdapterObserver)
    {
      synchronized (this.mLock)
      {
        this.mObserver = paramIPrintDocumentAdapterObserver;
        boolean bool = isDestroyedLocked();
        if ((!bool) || (paramIPrintDocumentAdapterObserver == null)) {}
      }
    }
    
    public void start()
    {
      synchronized (this.mLock)
      {
        if (!isDestroyedLocked()) {
          this.mHandler.obtainMessage(1, this.mDocumentAdapter).sendToTarget();
        }
        return;
      }
    }
    
    /* Error */
    public void write(PageRange[] paramArrayOfPageRange, ParcelFileDescriptor paramParcelFileDescriptor, IWriteResultCallback paramIWriteResultCallback, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 131	android/os/CancellationSignal:createTransport	()Landroid/os/ICancellationSignal;
      //   3: astore 7
      //   5: aload_3
      //   6: aload 7
      //   8: iload 4
      //   10: invokeinterface 204 3 0
      //   15: aload_0
      //   16: getfield 37	android/print/PrintManager$PrintDocumentAdapterDelegate:mLock	Ljava/lang/Object;
      //   19: astore 6
      //   21: aload 6
      //   23: monitorenter
      //   24: aload_0
      //   25: invokespecial 111	android/print/PrintManager$PrintDocumentAdapterDelegate:isDestroyedLocked	()Z
      //   28: istore 5
      //   30: iload 5
      //   32: ifeq +18 -> 50
      //   35: aload 6
      //   37: monitorexit
      //   38: return
      //   39: astore_1
      //   40: ldc -117
      //   42: ldc -50
      //   44: aload_1
      //   45: invokestatic 147	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   48: pop
      //   49: return
      //   50: aload 7
      //   52: invokestatic 151	android/os/CancellationSignal:fromTransport	(Landroid/os/ICancellationSignal;)Landroid/os/CancellationSignal;
      //   55: astore 7
      //   57: invokestatic 157	com/android/internal/os/SomeArgs:obtain	()Lcom/android/internal/os/SomeArgs;
      //   60: astore 8
      //   62: aload 8
      //   64: aload_0
      //   65: getfield 72	android/print/PrintManager$PrintDocumentAdapterDelegate:mDocumentAdapter	Landroid/print/PrintDocumentAdapter;
      //   68: putfield 160	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
      //   71: aload 8
      //   73: aload_1
      //   74: putfield 163	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
      //   77: aload 8
      //   79: aload_2
      //   80: putfield 166	com/android/internal/os/SomeArgs:arg3	Ljava/lang/Object;
      //   83: aload 8
      //   85: aload 7
      //   87: putfield 169	com/android/internal/os/SomeArgs:arg4	Ljava/lang/Object;
      //   90: aload 8
      //   92: new 20	android/print/PrintManager$PrintDocumentAdapterDelegate$MyWriteResultCallback
      //   95: dup
      //   96: aload_0
      //   97: aload_3
      //   98: aload_2
      //   99: iload 4
      //   101: invokespecial 209	android/print/PrintManager$PrintDocumentAdapterDelegate$MyWriteResultCallback:<init>	(Landroid/print/PrintManager$PrintDocumentAdapterDelegate;Landroid/print/IWriteResultCallback;Landroid/os/ParcelFileDescriptor;I)V
      //   104: putfield 175	com/android/internal/os/SomeArgs:arg5	Ljava/lang/Object;
      //   107: aload_0
      //   108: getfield 83	android/print/PrintManager$PrintDocumentAdapterDelegate:mHandler	Landroid/os/Handler;
      //   111: iconst_3
      //   112: aload 8
      //   114: invokevirtual 115	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   117: invokevirtual 120	android/os/Message:sendToTarget	()V
      //   120: aload 6
      //   122: monitorexit
      //   123: return
      //   124: astore_1
      //   125: aload 6
      //   127: monitorexit
      //   128: aload_1
      //   129: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	130	0	this	PrintDocumentAdapterDelegate
      //   0	130	1	paramArrayOfPageRange	PageRange[]
      //   0	130	2	paramParcelFileDescriptor	ParcelFileDescriptor
      //   0	130	3	paramIWriteResultCallback	IWriteResultCallback
      //   0	130	4	paramInt	int
      //   28	3	5	bool	boolean
      //   3	83	7	localObject2	Object
      //   60	53	8	localSomeArgs	SomeArgs
      // Exception table:
      //   from	to	target	type
      //   5	15	39	android/os/RemoteException
      //   24	30	124	finally
      //   50	120	124	finally
    }
    
    private static abstract interface DestroyableCallback
    {
      public abstract void destroy();
    }
    
    private final class MyHandler
      extends Handler
    {
      public static final int MSG_ON_FINISH = 4;
      public static final int MSG_ON_KILL = 5;
      public static final int MSG_ON_LAYOUT = 2;
      public static final int MSG_ON_START = 1;
      public static final int MSG_ON_WRITE = 3;
      
      public MyHandler(Looper paramLooper)
      {
        super(null, true);
      }
      
      public void handleMessage(Message arg1)
      {
        PrintDocumentAdapter localPrintDocumentAdapter;
        Object localObject2;
        Object localObject3;
        CancellationSignal localCancellationSignal;
        Object localObject4;
        switch (???.what)
        {
        default: 
          throw new IllegalArgumentException("Unknown message: " + ???.what);
        case 1: 
          ((PrintDocumentAdapter)???.obj).onStart();
          return;
        case 2: 
          ??? = (SomeArgs)???.obj;
          localPrintDocumentAdapter = (PrintDocumentAdapter)???.arg1;
          localObject2 = (PrintAttributes)???.arg2;
          localObject3 = (PrintAttributes)???.arg3;
          localCancellationSignal = (CancellationSignal)???.arg4;
          localObject4 = (PrintDocumentAdapter.LayoutResultCallback)???.arg5;
          Bundle localBundle = (Bundle)???.arg6;
          ???.recycle();
          localPrintDocumentAdapter.onLayout((PrintAttributes)localObject2, (PrintAttributes)localObject3, localCancellationSignal, (PrintDocumentAdapter.LayoutResultCallback)localObject4, localBundle);
          return;
        case 3: 
          ??? = (SomeArgs)???.obj;
          localPrintDocumentAdapter = (PrintDocumentAdapter)???.arg1;
          localObject2 = (PageRange[])???.arg2;
          localObject3 = (ParcelFileDescriptor)???.arg3;
          localCancellationSignal = (CancellationSignal)???.arg4;
          localObject4 = (PrintDocumentAdapter.WriteResultCallback)???.arg5;
          ???.recycle();
          localPrintDocumentAdapter.onWrite((PageRange[])localObject2, (ParcelFileDescriptor)localObject3, localCancellationSignal, (PrintDocumentAdapter.WriteResultCallback)localObject4);
          return;
        case 4: 
          ((PrintDocumentAdapter)???.obj).onFinish();
          synchronized (PrintManager.PrintDocumentAdapterDelegate.-get0(PrintManager.PrintDocumentAdapterDelegate.this))
          {
            PrintManager.PrintDocumentAdapterDelegate.-wrap0(PrintManager.PrintDocumentAdapterDelegate.this);
            return;
          }
        }
        throw new RuntimeException((String)???.obj);
      }
    }
    
    private final class MyLayoutResultCallback
      extends PrintDocumentAdapter.LayoutResultCallback
      implements PrintManager.PrintDocumentAdapterDelegate.DestroyableCallback
    {
      private ILayoutResultCallback mCallback;
      private final int mSequence;
      
      public MyLayoutResultCallback(ILayoutResultCallback paramILayoutResultCallback, int paramInt)
      {
        this.mCallback = paramILayoutResultCallback;
        this.mSequence = paramInt;
      }
      
      public void destroy()
      {
        synchronized (PrintManager.PrintDocumentAdapterDelegate.-get0(PrintManager.PrintDocumentAdapterDelegate.this))
        {
          this.mCallback = null;
          PrintManager.PrintDocumentAdapterDelegate.-set0(PrintManager.PrintDocumentAdapterDelegate.this, null);
          return;
        }
      }
      
      public void onLayoutCancelled()
      {
        synchronized (PrintManager.PrintDocumentAdapterDelegate.-get0(PrintManager.PrintDocumentAdapterDelegate.this))
        {
          ILayoutResultCallback localILayoutResultCallback = this.mCallback;
          if (localILayoutResultCallback == null)
          {
            Log.e("PrintManager", "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion or did you invoke a callback after finish?");
            return;
          }
        }
        try
        {
          ((ILayoutResultCallback)localObject3).onLayoutCanceled(this.mSequence);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("PrintManager", "Error calling onLayoutFailed", localRemoteException);
          return;
        }
        finally
        {
          destroy();
        }
      }
      
      public void onLayoutFailed(CharSequence paramCharSequence)
      {
        ILayoutResultCallback localILayoutResultCallback;
        synchronized (PrintManager.PrintDocumentAdapterDelegate.-get0(PrintManager.PrintDocumentAdapterDelegate.this))
        {
          localILayoutResultCallback = this.mCallback;
          if (localILayoutResultCallback == null)
          {
            Log.e("PrintManager", "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion or did you invoke a callback after finish?");
            return;
          }
        }
        try
        {
          localILayoutResultCallback.onLayoutFailed(paramCharSequence, this.mSequence);
          return;
        }
        catch (RemoteException paramCharSequence)
        {
          Log.e("PrintManager", "Error calling onLayoutFailed", paramCharSequence);
          return;
        }
        finally
        {
          destroy();
        }
      }
      
      public void onLayoutFinished(PrintDocumentInfo paramPrintDocumentInfo, boolean paramBoolean)
      {
        ILayoutResultCallback localILayoutResultCallback;
        synchronized (PrintManager.PrintDocumentAdapterDelegate.-get0(PrintManager.PrintDocumentAdapterDelegate.this))
        {
          localILayoutResultCallback = this.mCallback;
          if (localILayoutResultCallback == null)
          {
            Log.e("PrintManager", "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion or did you invoke a callback after finish?");
            return;
          }
        }
        if (paramPrintDocumentInfo == null) {
          try
          {
            throw new NullPointerException("document info cannot be null");
          }
          finally
          {
            destroy();
          }
        }
        try
        {
          localILayoutResultCallback.onLayoutFinished(paramPrintDocumentInfo, paramBoolean, this.mSequence);
          destroy();
          return;
        }
        catch (RemoteException paramPrintDocumentInfo)
        {
          for (;;)
          {
            Log.e("PrintManager", "Error calling onLayoutFinished", paramPrintDocumentInfo);
          }
        }
      }
    }
    
    private final class MyWriteResultCallback
      extends PrintDocumentAdapter.WriteResultCallback
      implements PrintManager.PrintDocumentAdapterDelegate.DestroyableCallback
    {
      private IWriteResultCallback mCallback;
      private ParcelFileDescriptor mFd;
      private final int mSequence;
      
      public MyWriteResultCallback(IWriteResultCallback paramIWriteResultCallback, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
      {
        this.mFd = paramParcelFileDescriptor;
        this.mSequence = paramInt;
        this.mCallback = paramIWriteResultCallback;
      }
      
      public void destroy()
      {
        synchronized (PrintManager.PrintDocumentAdapterDelegate.-get0(PrintManager.PrintDocumentAdapterDelegate.this))
        {
          IoUtils.closeQuietly(this.mFd);
          this.mCallback = null;
          this.mFd = null;
          PrintManager.PrintDocumentAdapterDelegate.-set0(PrintManager.PrintDocumentAdapterDelegate.this, null);
          return;
        }
      }
      
      public void onWriteCancelled()
      {
        synchronized (PrintManager.PrintDocumentAdapterDelegate.-get0(PrintManager.PrintDocumentAdapterDelegate.this))
        {
          IWriteResultCallback localIWriteResultCallback = this.mCallback;
          if (localIWriteResultCallback == null)
          {
            Log.e("PrintManager", "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion or did you invoke a callback after finish?");
            return;
          }
        }
        try
        {
          ((IWriteResultCallback)localObject3).onWriteCanceled(this.mSequence);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("PrintManager", "Error calling onWriteCanceled", localRemoteException);
          return;
        }
        finally
        {
          destroy();
        }
      }
      
      public void onWriteFailed(CharSequence paramCharSequence)
      {
        IWriteResultCallback localIWriteResultCallback;
        synchronized (PrintManager.PrintDocumentAdapterDelegate.-get0(PrintManager.PrintDocumentAdapterDelegate.this))
        {
          localIWriteResultCallback = this.mCallback;
          if (localIWriteResultCallback == null)
          {
            Log.e("PrintManager", "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion or did you invoke a callback after finish?");
            return;
          }
        }
        try
        {
          localIWriteResultCallback.onWriteFailed(paramCharSequence, this.mSequence);
          return;
        }
        catch (RemoteException paramCharSequence)
        {
          Log.e("PrintManager", "Error calling onWriteFailed", paramCharSequence);
          return;
        }
        finally
        {
          destroy();
        }
      }
      
      public void onWriteFinished(PageRange[] paramArrayOfPageRange)
      {
        IWriteResultCallback localIWriteResultCallback;
        synchronized (PrintManager.PrintDocumentAdapterDelegate.-get0(PrintManager.PrintDocumentAdapterDelegate.this))
        {
          localIWriteResultCallback = this.mCallback;
          if (localIWriteResultCallback == null)
          {
            Log.e("PrintManager", "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion or did you invoke a callback after finish?");
            return;
          }
        }
        if (paramArrayOfPageRange == null) {
          try
          {
            throw new IllegalArgumentException("pages cannot be null");
          }
          finally
          {
            destroy();
          }
        }
        if (paramArrayOfPageRange.length == 0) {
          throw new IllegalArgumentException("pages cannot be empty");
        }
        try
        {
          localIWriteResultCallback.onWriteFinished(paramArrayOfPageRange, this.mSequence);
          destroy();
          return;
        }
        catch (RemoteException paramArrayOfPageRange)
        {
          for (;;)
          {
            Log.e("PrintManager", "Error calling onWriteFinished", paramArrayOfPageRange);
          }
        }
      }
    }
  }
  
  public static abstract interface PrintJobStateChangeListener
  {
    public abstract void onPrintJobStateChanged(PrintJobId paramPrintJobId);
  }
  
  public static final class PrintJobStateChangeListenerWrapper
    extends IPrintJobStateChangeListener.Stub
  {
    private final WeakReference<Handler> mWeakHandler;
    private final WeakReference<PrintManager.PrintJobStateChangeListener> mWeakListener;
    
    public PrintJobStateChangeListenerWrapper(PrintManager.PrintJobStateChangeListener paramPrintJobStateChangeListener, Handler paramHandler)
    {
      this.mWeakListener = new WeakReference(paramPrintJobStateChangeListener);
      this.mWeakHandler = new WeakReference(paramHandler);
    }
    
    public void destroy()
    {
      this.mWeakListener.clear();
    }
    
    public PrintManager.PrintJobStateChangeListener getListener()
    {
      return (PrintManager.PrintJobStateChangeListener)this.mWeakListener.get();
    }
    
    public void onPrintJobStateChanged(PrintJobId paramPrintJobId)
    {
      Handler localHandler = (Handler)this.mWeakHandler.get();
      Object localObject = (PrintManager.PrintJobStateChangeListener)this.mWeakListener.get();
      if ((localHandler != null) && (localObject != null))
      {
        localObject = SomeArgs.obtain();
        ((SomeArgs)localObject).arg1 = this;
        ((SomeArgs)localObject).arg2 = paramPrintJobId;
        localHandler.obtainMessage(1, localObject).sendToTarget();
      }
    }
  }
  
  public static abstract interface PrintServiceRecommendationsChangeListener
  {
    public abstract void onPrintServiceRecommendationsChanged();
  }
  
  public static final class PrintServiceRecommendationsChangeListenerWrapper
    extends IRecommendationsChangeListener.Stub
  {
    private final WeakReference<Handler> mWeakHandler;
    private final WeakReference<PrintManager.PrintServiceRecommendationsChangeListener> mWeakListener;
    
    public PrintServiceRecommendationsChangeListenerWrapper(PrintManager.PrintServiceRecommendationsChangeListener paramPrintServiceRecommendationsChangeListener, Handler paramHandler)
    {
      this.mWeakListener = new WeakReference(paramPrintServiceRecommendationsChangeListener);
      this.mWeakHandler = new WeakReference(paramHandler);
    }
    
    public void destroy()
    {
      this.mWeakListener.clear();
    }
    
    public PrintManager.PrintServiceRecommendationsChangeListener getListener()
    {
      return (PrintManager.PrintServiceRecommendationsChangeListener)this.mWeakListener.get();
    }
    
    public void onRecommendationsChanged()
    {
      Handler localHandler = (Handler)this.mWeakHandler.get();
      PrintManager.PrintServiceRecommendationsChangeListener localPrintServiceRecommendationsChangeListener = (PrintManager.PrintServiceRecommendationsChangeListener)this.mWeakListener.get();
      if ((localHandler != null) && (localPrintServiceRecommendationsChangeListener != null)) {
        localHandler.obtainMessage(3, this).sendToTarget();
      }
    }
  }
  
  public static abstract interface PrintServicesChangeListener
  {
    public abstract void onPrintServicesChanged();
  }
  
  public static final class PrintServicesChangeListenerWrapper
    extends IPrintServicesChangeListener.Stub
  {
    private final WeakReference<Handler> mWeakHandler;
    private final WeakReference<PrintManager.PrintServicesChangeListener> mWeakListener;
    
    public PrintServicesChangeListenerWrapper(PrintManager.PrintServicesChangeListener paramPrintServicesChangeListener, Handler paramHandler)
    {
      this.mWeakListener = new WeakReference(paramPrintServicesChangeListener);
      this.mWeakHandler = new WeakReference(paramHandler);
    }
    
    public void destroy()
    {
      this.mWeakListener.clear();
    }
    
    public PrintManager.PrintServicesChangeListener getListener()
    {
      return (PrintManager.PrintServicesChangeListener)this.mWeakListener.get();
    }
    
    public void onPrintServicesChanged()
    {
      Handler localHandler = (Handler)this.mWeakHandler.get();
      PrintManager.PrintServicesChangeListener localPrintServicesChangeListener = (PrintManager.PrintServicesChangeListener)this.mWeakListener.get();
      if ((localHandler != null) && (localPrintServicesChangeListener != null)) {
        localHandler.obtainMessage(2, this).sendToTarget();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */