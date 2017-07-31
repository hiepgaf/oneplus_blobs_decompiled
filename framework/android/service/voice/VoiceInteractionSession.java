package android.service.voice;

import android.R.styleable;
import android.app.Dialog;
import android.app.Instrumentation;
import android.app.VoiceInteractor.PickOptionRequest.Option;
import android.app.VoiceInteractor.Prompt;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region;
import android.inputmethodservice.SoftInputWindow;
import android.inputmethodservice.SoftInputWindow.Callback;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.DebugUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.IVoiceInteractor.Stub;
import com.android.internal.app.IVoiceInteractorCallback;
import com.android.internal.app.IVoiceInteractorRequest;
import com.android.internal.app.IVoiceInteractorRequest.Stub;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.os.SomeArgs;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class VoiceInteractionSession
  implements KeyEvent.Callback, ComponentCallbacks2
{
  static final boolean DEBUG = false;
  public static final String KEY_CONTENT = "content";
  public static final String KEY_DATA = "data";
  public static final String KEY_RECEIVER_EXTRAS = "receiverExtras";
  public static final String KEY_STRUCTURE = "structure";
  static final int MSG_CANCEL = 7;
  static final int MSG_CLOSE_SYSTEM_DIALOGS = 102;
  static final int MSG_DESTROY = 103;
  static final int MSG_HANDLE_ASSIST = 104;
  static final int MSG_HANDLE_SCREENSHOT = 105;
  static final int MSG_HIDE = 107;
  static final int MSG_ON_LOCKSCREEN_SHOWN = 108;
  static final int MSG_SHOW = 106;
  static final int MSG_START_ABORT_VOICE = 4;
  static final int MSG_START_COMMAND = 5;
  static final int MSG_START_COMPLETE_VOICE = 3;
  static final int MSG_START_CONFIRMATION = 1;
  static final int MSG_START_PICK_OPTION = 2;
  static final int MSG_SUPPORTS_COMMANDS = 6;
  static final int MSG_TASK_FINISHED = 101;
  static final int MSG_TASK_STARTED = 100;
  public static final int SHOW_SOURCE_ACTIVITY = 16;
  public static final int SHOW_SOURCE_APPLICATION = 8;
  public static final int SHOW_SOURCE_ASSIST_GESTURE = 4;
  public static final int SHOW_WITH_ASSIST = 1;
  public static final int SHOW_WITH_SCREENSHOT = 2;
  static final String TAG = "VoiceInteractionSession";
  final ArrayMap<IBinder, Request> mActiveRequests = new ArrayMap();
  final MyCallbacks mCallbacks = new MyCallbacks();
  FrameLayout mContentFrame;
  final Context mContext;
  final KeyEvent.DispatcherState mDispatcherState = new KeyEvent.DispatcherState();
  final HandlerCaller mHandlerCaller;
  boolean mInShowWindow;
  LayoutInflater mInflater;
  boolean mInitialized;
  final ViewTreeObserver.OnComputeInternalInsetsListener mInsetsComputer = new ViewTreeObserver.OnComputeInternalInsetsListener()
  {
    public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo paramAnonymousInternalInsetsInfo)
    {
      VoiceInteractionSession.this.onComputeInsets(VoiceInteractionSession.this.mTmpInsets);
      paramAnonymousInternalInsetsInfo.contentInsets.set(VoiceInteractionSession.this.mTmpInsets.contentInsets);
      paramAnonymousInternalInsetsInfo.visibleInsets.set(VoiceInteractionSession.this.mTmpInsets.contentInsets);
      paramAnonymousInternalInsetsInfo.touchableRegion.set(VoiceInteractionSession.this.mTmpInsets.touchableRegion);
      paramAnonymousInternalInsetsInfo.setTouchableInsets(VoiceInteractionSession.this.mTmpInsets.touchableInsets);
    }
  };
  final IVoiceInteractor mInteractor = new IVoiceInteractor.Stub()
  {
    public IVoiceInteractorRequest startAbortVoice(String paramAnonymousString, IVoiceInteractorCallback paramAnonymousIVoiceInteractorCallback, VoiceInteractor.Prompt paramAnonymousPrompt, Bundle paramAnonymousBundle)
    {
      paramAnonymousString = new VoiceInteractionSession.AbortVoiceRequest(paramAnonymousString, Binder.getCallingUid(), paramAnonymousIVoiceInteractorCallback, VoiceInteractionSession.this, paramAnonymousPrompt, paramAnonymousBundle);
      VoiceInteractionSession.this.addRequest(paramAnonymousString);
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(4, paramAnonymousString));
      return paramAnonymousString.mInterface;
    }
    
    public IVoiceInteractorRequest startCommand(String paramAnonymousString1, IVoiceInteractorCallback paramAnonymousIVoiceInteractorCallback, String paramAnonymousString2, Bundle paramAnonymousBundle)
    {
      paramAnonymousString1 = new VoiceInteractionSession.CommandRequest(paramAnonymousString1, Binder.getCallingUid(), paramAnonymousIVoiceInteractorCallback, VoiceInteractionSession.this, paramAnonymousString2, paramAnonymousBundle);
      VoiceInteractionSession.this.addRequest(paramAnonymousString1);
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(5, paramAnonymousString1));
      return paramAnonymousString1.mInterface;
    }
    
    public IVoiceInteractorRequest startCompleteVoice(String paramAnonymousString, IVoiceInteractorCallback paramAnonymousIVoiceInteractorCallback, VoiceInteractor.Prompt paramAnonymousPrompt, Bundle paramAnonymousBundle)
    {
      paramAnonymousString = new VoiceInteractionSession.CompleteVoiceRequest(paramAnonymousString, Binder.getCallingUid(), paramAnonymousIVoiceInteractorCallback, VoiceInteractionSession.this, paramAnonymousPrompt, paramAnonymousBundle);
      VoiceInteractionSession.this.addRequest(paramAnonymousString);
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(3, paramAnonymousString));
      return paramAnonymousString.mInterface;
    }
    
    public IVoiceInteractorRequest startConfirmation(String paramAnonymousString, IVoiceInteractorCallback paramAnonymousIVoiceInteractorCallback, VoiceInteractor.Prompt paramAnonymousPrompt, Bundle paramAnonymousBundle)
    {
      paramAnonymousString = new VoiceInteractionSession.ConfirmationRequest(paramAnonymousString, Binder.getCallingUid(), paramAnonymousIVoiceInteractorCallback, VoiceInteractionSession.this, paramAnonymousPrompt, paramAnonymousBundle);
      VoiceInteractionSession.this.addRequest(paramAnonymousString);
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(1, paramAnonymousString));
      return paramAnonymousString.mInterface;
    }
    
    public IVoiceInteractorRequest startPickOption(String paramAnonymousString, IVoiceInteractorCallback paramAnonymousIVoiceInteractorCallback, VoiceInteractor.Prompt paramAnonymousPrompt, VoiceInteractor.PickOptionRequest.Option[] paramAnonymousArrayOfOption, Bundle paramAnonymousBundle)
    {
      paramAnonymousString = new VoiceInteractionSession.PickOptionRequest(paramAnonymousString, Binder.getCallingUid(), paramAnonymousIVoiceInteractorCallback, VoiceInteractionSession.this, paramAnonymousPrompt, paramAnonymousArrayOfOption, paramAnonymousBundle);
      VoiceInteractionSession.this.addRequest(paramAnonymousString);
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(2, paramAnonymousString));
      return paramAnonymousString.mInterface;
    }
    
    public boolean[] supportsCommands(String paramAnonymousString, String[] paramAnonymousArrayOfString)
    {
      paramAnonymousString = VoiceInteractionSession.this.mHandlerCaller.obtainMessageIOO(6, 0, paramAnonymousArrayOfString, null);
      paramAnonymousString = VoiceInteractionSession.this.mHandlerCaller.sendMessageAndWait(paramAnonymousString);
      if (paramAnonymousString != null)
      {
        paramAnonymousArrayOfString = (boolean[])paramAnonymousString.arg1;
        paramAnonymousString.recycle();
        return paramAnonymousArrayOfString;
      }
      return new boolean[paramAnonymousArrayOfString.length];
    }
  };
  View mRootView;
  final IVoiceInteractionSession mSession = new IVoiceInteractionSession.Stub()
  {
    public void closeSystemDialogs()
    {
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(102));
    }
    
    public void destroy()
    {
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(103));
    }
    
    public void handleAssist(final Bundle paramAnonymousBundle, final AssistStructure paramAnonymousAssistStructure, final AssistContent paramAnonymousAssistContent, final int paramAnonymousInt1, final int paramAnonymousInt2)
    {
      new Thread("AssistStructure retriever")
      {
        public void run()
        {
          AssistStructure localAssistStructure = null;
          HandlerCaller localHandlerCaller2 = null;
          HandlerCaller localHandlerCaller1 = localHandlerCaller2;
          if (paramAnonymousAssistStructure != null) {}
          try
          {
            paramAnonymousAssistStructure.ensureData();
            localHandlerCaller1 = localHandlerCaller2;
          }
          catch (Throwable localThrowable)
          {
            for (;;)
            {
              HandlerCaller localHandlerCaller3;
              Bundle localBundle;
              Log.w("VoiceInteractionSession", "Failure retrieving AssistStructure", localThrowable);
            }
          }
          localHandlerCaller2 = VoiceInteractionSession.this.mHandlerCaller;
          localHandlerCaller3 = VoiceInteractionSession.this.mHandlerCaller;
          localBundle = paramAnonymousBundle;
          if (localHandlerCaller1 == null) {
            localAssistStructure = paramAnonymousAssistStructure;
          }
          localHandlerCaller2.sendMessage(localHandlerCaller3.obtainMessageOOOOII(104, localBundle, localAssistStructure, localHandlerCaller1, paramAnonymousAssistContent, paramAnonymousInt1, paramAnonymousInt2));
        }
      }.start();
    }
    
    public void handleScreenshot(Bitmap paramAnonymousBitmap)
    {
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(105, paramAnonymousBitmap));
    }
    
    public void hide()
    {
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(107));
    }
    
    public void onLockscreenShown()
    {
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(108));
    }
    
    public void show(Bundle paramAnonymousBundle, int paramAnonymousInt, IVoiceInteractionSessionShowCallback paramAnonymousIVoiceInteractionSessionShowCallback)
    {
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIOO(106, paramAnonymousInt, paramAnonymousBundle, paramAnonymousIVoiceInteractionSessionShowCallback));
    }
    
    public void taskFinished(Intent paramAnonymousIntent, int paramAnonymousInt)
    {
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIO(101, paramAnonymousInt, paramAnonymousIntent));
    }
    
    public void taskStarted(Intent paramAnonymousIntent, int paramAnonymousInt)
    {
      VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIO(100, paramAnonymousInt, paramAnonymousIntent));
    }
  };
  IVoiceInteractionManagerService mSystemService;
  int mTheme = 0;
  TypedArray mThemeAttrs;
  final Insets mTmpInsets = new Insets();
  IBinder mToken;
  final WeakReference<VoiceInteractionSession> mWeakRef = new WeakReference(this);
  SoftInputWindow mWindow;
  boolean mWindowAdded;
  boolean mWindowVisible;
  boolean mWindowWasVisible;
  
  public VoiceInteractionSession(Context paramContext)
  {
    this(paramContext, new Handler());
  }
  
  public VoiceInteractionSession(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mHandlerCaller = new HandlerCaller(paramContext, paramHandler.getLooper(), this.mCallbacks, true);
  }
  
  private void doOnCreate()
  {
    if (this.mTheme != 0) {}
    for (int i = this.mTheme;; i = 16974985)
    {
      this.mTheme = i;
      this.mInflater = ((LayoutInflater)this.mContext.getSystemService("layout_inflater"));
      this.mWindow = new SoftInputWindow(this.mContext, "VoiceInteractionSession", this.mTheme, this.mCallbacks, this, this.mDispatcherState, 2031, 80, true);
      this.mWindow.getWindow().addFlags(16843008);
      initViews();
      this.mWindow.getWindow().setLayout(-1, -1);
      this.mWindow.setToken(this.mToken);
      return;
    }
  }
  
  void addRequest(Request paramRequest)
  {
    try
    {
      this.mActiveRequests.put(paramRequest.mInterface.asBinder(), paramRequest);
      return;
    }
    finally
    {
      paramRequest = finally;
      throw paramRequest;
    }
  }
  
  public void closeSystemDialogs()
  {
    if (this.mToken == null) {
      throw new IllegalStateException("Can't call before onCreate()");
    }
    try
    {
      this.mSystemService.closeSystemDialogs(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  void doCreate(IVoiceInteractionManagerService paramIVoiceInteractionManagerService, IBinder paramIBinder)
  {
    this.mSystemService = paramIVoiceInteractionManagerService;
    this.mToken = paramIBinder;
    onCreate();
  }
  
  void doDestroy()
  {
    onDestroy();
    if (this.mInitialized)
    {
      this.mRootView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsComputer);
      if (this.mWindowAdded)
      {
        this.mWindow.dismiss();
        this.mWindowAdded = false;
      }
      this.mInitialized = false;
    }
  }
  
  void doHide()
  {
    if (this.mWindowVisible)
    {
      this.mWindow.hide();
      this.mWindowVisible = false;
      onHide();
    }
  }
  
  void doOnHandleAssist(Bundle paramBundle, AssistStructure paramAssistStructure, Throwable paramThrowable, AssistContent paramAssistContent)
  {
    if (paramThrowable != null) {
      onAssistStructureFailure(paramThrowable);
    }
    onHandleAssist(paramBundle, paramAssistStructure, paramAssistContent);
  }
  
  void doOnHandleAssistSecondary(Bundle paramBundle, AssistStructure paramAssistStructure, Throwable paramThrowable, AssistContent paramAssistContent, int paramInt1, int paramInt2)
  {
    if (paramThrowable != null) {
      onAssistStructureFailure(paramThrowable);
    }
    onHandleAssistSecondary(paramBundle, paramAssistStructure, paramAssistContent, paramInt1, paramInt2);
  }
  
  void doShow(Bundle paramBundle, int paramInt, final IVoiceInteractionSessionShowCallback paramIVoiceInteractionSessionShowCallback)
  {
    if (this.mInShowWindow)
    {
      Log.w("VoiceInteractionSession", "Re-entrance in to showWindow");
      return;
    }
    try
    {
      this.mInShowWindow = true;
      if ((!this.mWindowVisible) && (!this.mWindowAdded))
      {
        this.mWindowAdded = true;
        View localView = onCreateContentView();
        if (localView != null) {
          setContentView(localView);
        }
      }
      onShow(paramBundle, paramInt);
      if (!this.mWindowVisible)
      {
        this.mWindowVisible = true;
        this.mWindow.show();
      }
      if (paramIVoiceInteractionSessionShowCallback != null)
      {
        this.mRootView.invalidate();
        this.mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            VoiceInteractionSession.this.mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
            try
            {
              paramIVoiceInteractionSessionShowCallback.onShown();
              return true;
            }
            catch (RemoteException localRemoteException)
            {
              for (;;)
              {
                Log.w("VoiceInteractionSession", "Error calling onShown", localRemoteException);
              }
            }
          }
        });
      }
      return;
    }
    finally
    {
      this.mWindowWasVisible = true;
      this.mInShowWindow = false;
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mToken=");
    paramPrintWriter.println(this.mToken);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mTheme=#");
    paramPrintWriter.println(Integer.toHexString(this.mTheme));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mInitialized=");
    paramPrintWriter.println(this.mInitialized);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mWindowAdded=");
    paramPrintWriter.print(this.mWindowAdded);
    paramPrintWriter.print(" mWindowVisible=");
    paramPrintWriter.println(this.mWindowVisible);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mWindowWasVisible=");
    paramPrintWriter.print(this.mWindowWasVisible);
    paramPrintWriter.print(" mInShowWindow=");
    paramPrintWriter.println(this.mInShowWindow);
    if (this.mActiveRequests.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Active requests:");
      String str = paramString + "    ";
      int i = 0;
      while (i < this.mActiveRequests.size())
      {
        Request localRequest = (Request)this.mActiveRequests.valueAt(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(": ");
        paramPrintWriter.println(localRequest);
        localRequest.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        i += 1;
      }
    }
  }
  
  public void finish()
  {
    if (this.mToken == null) {
      throw new IllegalStateException("Can't call before onCreate()");
    }
    try
    {
      this.mSystemService.finish(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public int getDisabledShowContext()
  {
    try
    {
      int i = this.mSystemService.getDisabledShowContext();
      return i;
    }
    catch (RemoteException localRemoteException) {}
    return 0;
  }
  
  public LayoutInflater getLayoutInflater()
  {
    return this.mInflater;
  }
  
  public int getUserDisabledShowContext()
  {
    try
    {
      int i = this.mSystemService.getUserDisabledShowContext();
      return i;
    }
    catch (RemoteException localRemoteException) {}
    return 0;
  }
  
  public Dialog getWindow()
  {
    return this.mWindow;
  }
  
  public void hide()
  {
    if (this.mToken == null) {
      throw new IllegalStateException("Can't call before onCreate()");
    }
    try
    {
      this.mSystemService.hideSessionFromSession(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  void initViews()
  {
    this.mInitialized = true;
    this.mThemeAttrs = this.mContext.obtainStyledAttributes(R.styleable.VoiceInteractionSession);
    this.mRootView = this.mInflater.inflate(17367303, null);
    this.mRootView.setSystemUiVisibility(1792);
    this.mWindow.setContentView(this.mRootView);
    this.mRootView.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsComputer);
    this.mContentFrame = ((FrameLayout)this.mRootView.findViewById(16908290));
  }
  
  boolean isRequestActive(IBinder paramIBinder)
  {
    try
    {
      boolean bool = this.mActiveRequests.containsKey(paramIBinder);
      return bool;
    }
    finally
    {
      paramIBinder = finally;
      throw paramIBinder;
    }
  }
  
  public void onAssistStructureFailure(Throwable paramThrowable) {}
  
  public void onBackPressed()
  {
    hide();
  }
  
  public void onCancelRequest(Request paramRequest) {}
  
  public void onCloseSystemDialogs()
  {
    hide();
  }
  
  public void onComputeInsets(Insets paramInsets)
  {
    paramInsets.contentInsets.left = 0;
    paramInsets.contentInsets.bottom = 0;
    paramInsets.contentInsets.right = 0;
    View localView = getWindow().getWindow().getDecorView();
    paramInsets.contentInsets.top = localView.getHeight();
    paramInsets.touchableInsets = 0;
    paramInsets.touchableRegion.setEmpty();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onCreate()
  {
    doOnCreate();
  }
  
  public View onCreateContentView()
  {
    return null;
  }
  
  public void onDestroy() {}
  
  public boolean[] onGetSupportedCommands(String[] paramArrayOfString)
  {
    return new boolean[paramArrayOfString.length];
  }
  
  public void onHandleAssist(Bundle paramBundle, AssistStructure paramAssistStructure, AssistContent paramAssistContent) {}
  
  public void onHandleAssistSecondary(Bundle paramBundle, AssistStructure paramAssistStructure, AssistContent paramAssistContent, int paramInt1, int paramInt2) {}
  
  public void onHandleScreenshot(Bitmap paramBitmap) {}
  
  public void onHide() {}
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public void onLockscreenShown()
  {
    hide();
  }
  
  public void onLowMemory() {}
  
  public void onRequestAbortVoice(AbortVoiceRequest paramAbortVoiceRequest) {}
  
  public void onRequestCommand(CommandRequest paramCommandRequest) {}
  
  public void onRequestCompleteVoice(CompleteVoiceRequest paramCompleteVoiceRequest) {}
  
  public void onRequestConfirmation(ConfirmationRequest paramConfirmationRequest) {}
  
  public void onRequestPickOption(PickOptionRequest paramPickOptionRequest) {}
  
  public void onShow(Bundle paramBundle, int paramInt) {}
  
  public void onTaskFinished(Intent paramIntent, int paramInt)
  {
    hide();
  }
  
  public void onTaskStarted(Intent paramIntent, int paramInt) {}
  
  public void onTrimMemory(int paramInt) {}
  
  Request removeRequest(IBinder paramIBinder)
  {
    try
    {
      paramIBinder = (Request)this.mActiveRequests.remove(paramIBinder);
      return paramIBinder;
    }
    finally
    {
      paramIBinder = finally;
      throw paramIBinder;
    }
  }
  
  public void setContentView(View paramView)
  {
    this.mContentFrame.removeAllViews();
    this.mContentFrame.addView(paramView, new FrameLayout.LayoutParams(-1, -1));
    this.mContentFrame.requestApplyInsets();
  }
  
  public void setDisabledShowContext(int paramInt)
  {
    try
    {
      this.mSystemService.setDisabledShowContext(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void setKeepAwake(boolean paramBoolean)
  {
    if (this.mToken == null) {
      throw new IllegalStateException("Can't call before onCreate()");
    }
    try
    {
      this.mSystemService.setKeepAwake(this.mToken, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void setTheme(int paramInt)
  {
    if (this.mWindow != null) {
      throw new IllegalStateException("Must be called before onCreate()");
    }
    this.mTheme = paramInt;
  }
  
  public void show(Bundle paramBundle, int paramInt)
  {
    if (this.mToken == null) {
      throw new IllegalStateException("Can't call before onCreate()");
    }
    try
    {
      this.mSystemService.showSessionFromSession(this.mToken, paramBundle, paramInt);
      return;
    }
    catch (RemoteException paramBundle) {}
  }
  
  public void startVoiceActivity(Intent paramIntent)
  {
    if (this.mToken == null) {
      throw new IllegalStateException("Can't call before onCreate()");
    }
    try
    {
      paramIntent.migrateExtraStreamToClipData();
      paramIntent.prepareToLeaveProcess(this.mContext);
      Instrumentation.checkStartActivityResult(this.mSystemService.startVoiceActivity(this.mToken, paramIntent, paramIntent.resolveType(this.mContext.getContentResolver())), paramIntent);
      return;
    }
    catch (RemoteException paramIntent) {}
  }
  
  public static final class AbortVoiceRequest
    extends VoiceInteractionSession.Request
  {
    final VoiceInteractor.Prompt mPrompt;
    
    AbortVoiceRequest(String paramString, int paramInt, IVoiceInteractorCallback paramIVoiceInteractorCallback, VoiceInteractionSession paramVoiceInteractionSession, VoiceInteractor.Prompt paramPrompt, Bundle paramBundle)
    {
      super(paramInt, paramIVoiceInteractorCallback, paramVoiceInteractionSession, paramBundle);
      this.mPrompt = paramPrompt;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPrompt=");
      paramPrintWriter.println(this.mPrompt);
    }
    
    public CharSequence getMessage()
    {
      CharSequence localCharSequence = null;
      if (this.mPrompt != null) {
        localCharSequence = this.mPrompt.getVoicePromptAt(0);
      }
      return localCharSequence;
    }
    
    public VoiceInteractor.Prompt getVoicePrompt()
    {
      return this.mPrompt;
    }
    
    public void sendAbortResult(Bundle paramBundle)
    {
      try
      {
        finishRequest();
        this.mCallback.deliverAbortVoiceResult(this.mInterface, paramBundle);
        return;
      }
      catch (RemoteException paramBundle) {}
    }
  }
  
  public static final class CommandRequest
    extends VoiceInteractionSession.Request
  {
    final String mCommand;
    
    CommandRequest(String paramString1, int paramInt, IVoiceInteractorCallback paramIVoiceInteractorCallback, VoiceInteractionSession paramVoiceInteractionSession, String paramString2, Bundle paramBundle)
    {
      super(paramInt, paramIVoiceInteractorCallback, paramVoiceInteractionSession, paramBundle);
      this.mCommand = paramString2;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCommand=");
      paramPrintWriter.println(this.mCommand);
    }
    
    public String getCommand()
    {
      return this.mCommand;
    }
    
    void sendCommandResult(boolean paramBoolean, Bundle paramBundle)
    {
      if (paramBoolean) {}
      try
      {
        finishRequest();
        this.mCallback.deliverCommandResult(this.mInterface, paramBoolean, paramBundle);
        return;
      }
      catch (RemoteException paramBundle) {}
    }
    
    public void sendIntermediateResult(Bundle paramBundle)
    {
      sendCommandResult(false, paramBundle);
    }
    
    public void sendResult(Bundle paramBundle)
    {
      sendCommandResult(true, paramBundle);
    }
  }
  
  public static final class CompleteVoiceRequest
    extends VoiceInteractionSession.Request
  {
    final VoiceInteractor.Prompt mPrompt;
    
    CompleteVoiceRequest(String paramString, int paramInt, IVoiceInteractorCallback paramIVoiceInteractorCallback, VoiceInteractionSession paramVoiceInteractionSession, VoiceInteractor.Prompt paramPrompt, Bundle paramBundle)
    {
      super(paramInt, paramIVoiceInteractorCallback, paramVoiceInteractionSession, paramBundle);
      this.mPrompt = paramPrompt;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPrompt=");
      paramPrintWriter.println(this.mPrompt);
    }
    
    public CharSequence getMessage()
    {
      CharSequence localCharSequence = null;
      if (this.mPrompt != null) {
        localCharSequence = this.mPrompt.getVoicePromptAt(0);
      }
      return localCharSequence;
    }
    
    public VoiceInteractor.Prompt getVoicePrompt()
    {
      return this.mPrompt;
    }
    
    public void sendCompleteResult(Bundle paramBundle)
    {
      try
      {
        finishRequest();
        this.mCallback.deliverCompleteVoiceResult(this.mInterface, paramBundle);
        return;
      }
      catch (RemoteException paramBundle) {}
    }
  }
  
  public static final class ConfirmationRequest
    extends VoiceInteractionSession.Request
  {
    final VoiceInteractor.Prompt mPrompt;
    
    ConfirmationRequest(String paramString, int paramInt, IVoiceInteractorCallback paramIVoiceInteractorCallback, VoiceInteractionSession paramVoiceInteractionSession, VoiceInteractor.Prompt paramPrompt, Bundle paramBundle)
    {
      super(paramInt, paramIVoiceInteractorCallback, paramVoiceInteractionSession, paramBundle);
      this.mPrompt = paramPrompt;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPrompt=");
      paramPrintWriter.println(this.mPrompt);
    }
    
    public CharSequence getPrompt()
    {
      CharSequence localCharSequence = null;
      if (this.mPrompt != null) {
        localCharSequence = this.mPrompt.getVoicePromptAt(0);
      }
      return localCharSequence;
    }
    
    public VoiceInteractor.Prompt getVoicePrompt()
    {
      return this.mPrompt;
    }
    
    public void sendConfirmationResult(boolean paramBoolean, Bundle paramBundle)
    {
      try
      {
        finishRequest();
        this.mCallback.deliverConfirmationResult(this.mInterface, paramBoolean, paramBundle);
        return;
      }
      catch (RemoteException paramBundle) {}
    }
  }
  
  public static final class Insets
  {
    public static final int TOUCHABLE_INSETS_CONTENT = 1;
    public static final int TOUCHABLE_INSETS_FRAME = 0;
    public static final int TOUCHABLE_INSETS_REGION = 3;
    public final Rect contentInsets = new Rect();
    public int touchableInsets;
    public final Region touchableRegion = new Region();
  }
  
  class MyCallbacks
    implements HandlerCaller.Callback, SoftInputWindow.Callback
  {
    MyCallbacks() {}
    
    public void executeMessage(Message paramMessage)
    {
      SomeArgs localSomeArgs = null;
      switch (paramMessage.what)
      {
      default: 
        paramMessage = localSomeArgs;
      }
      for (;;)
      {
        if (paramMessage != null) {
          paramMessage.recycle();
        }
        return;
        VoiceInteractionSession.this.onRequestConfirmation((VoiceInteractionSession.ConfirmationRequest)paramMessage.obj);
        paramMessage = localSomeArgs;
        continue;
        VoiceInteractionSession.this.onRequestPickOption((VoiceInteractionSession.PickOptionRequest)paramMessage.obj);
        paramMessage = localSomeArgs;
        continue;
        VoiceInteractionSession.this.onRequestCompleteVoice((VoiceInteractionSession.CompleteVoiceRequest)paramMessage.obj);
        paramMessage = localSomeArgs;
        continue;
        VoiceInteractionSession.this.onRequestAbortVoice((VoiceInteractionSession.AbortVoiceRequest)paramMessage.obj);
        paramMessage = localSomeArgs;
        continue;
        VoiceInteractionSession.this.onRequestCommand((VoiceInteractionSession.CommandRequest)paramMessage.obj);
        paramMessage = localSomeArgs;
        continue;
        paramMessage = (SomeArgs)paramMessage.obj;
        paramMessage.arg1 = VoiceInteractionSession.this.onGetSupportedCommands((String[])paramMessage.arg1);
        paramMessage.complete();
        paramMessage = null;
        continue;
        VoiceInteractionSession.this.onCancelRequest((VoiceInteractionSession.Request)paramMessage.obj);
        paramMessage = localSomeArgs;
        continue;
        VoiceInteractionSession.this.onTaskStarted((Intent)paramMessage.obj, paramMessage.arg1);
        paramMessage = localSomeArgs;
        continue;
        VoiceInteractionSession.this.onTaskFinished((Intent)paramMessage.obj, paramMessage.arg1);
        paramMessage = localSomeArgs;
        continue;
        VoiceInteractionSession.this.onCloseSystemDialogs();
        paramMessage = localSomeArgs;
        continue;
        VoiceInteractionSession.this.doDestroy();
        paramMessage = localSomeArgs;
        continue;
        paramMessage = (SomeArgs)paramMessage.obj;
        if (paramMessage.argi5 == 0)
        {
          VoiceInteractionSession.this.doOnHandleAssist((Bundle)paramMessage.arg1, (AssistStructure)paramMessage.arg2, (Throwable)paramMessage.arg3, (AssistContent)paramMessage.arg4);
        }
        else
        {
          VoiceInteractionSession.this.doOnHandleAssistSecondary((Bundle)paramMessage.arg1, (AssistStructure)paramMessage.arg2, (Throwable)paramMessage.arg3, (AssistContent)paramMessage.arg4, paramMessage.argi5, paramMessage.argi6);
          continue;
          VoiceInteractionSession.this.onHandleScreenshot((Bitmap)paramMessage.obj);
          paramMessage = localSomeArgs;
          continue;
          localSomeArgs = (SomeArgs)paramMessage.obj;
          VoiceInteractionSession.this.doShow((Bundle)localSomeArgs.arg1, paramMessage.arg1, (IVoiceInteractionSessionShowCallback)localSomeArgs.arg2);
          paramMessage = localSomeArgs;
          continue;
          VoiceInteractionSession.this.doHide();
          paramMessage = localSomeArgs;
          continue;
          VoiceInteractionSession.this.onLockscreenShown();
          paramMessage = localSomeArgs;
        }
      }
    }
    
    public void onBackPressed()
    {
      VoiceInteractionSession.this.onBackPressed();
    }
  }
  
  public static final class PickOptionRequest
    extends VoiceInteractionSession.Request
  {
    final VoiceInteractor.PickOptionRequest.Option[] mOptions;
    final VoiceInteractor.Prompt mPrompt;
    
    PickOptionRequest(String paramString, int paramInt, IVoiceInteractorCallback paramIVoiceInteractorCallback, VoiceInteractionSession paramVoiceInteractionSession, VoiceInteractor.Prompt paramPrompt, VoiceInteractor.PickOptionRequest.Option[] paramArrayOfOption, Bundle paramBundle)
    {
      super(paramInt, paramIVoiceInteractorCallback, paramVoiceInteractionSession, paramBundle);
      this.mPrompt = paramPrompt;
      this.mOptions = paramArrayOfOption;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPrompt=");
      paramPrintWriter.println(this.mPrompt);
      if (this.mOptions != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Options:");
        int i = 0;
        while (i < this.mOptions.length)
        {
          paramFileDescriptor = this.mOptions[i];
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i);
          paramPrintWriter.println(":");
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("    mLabel=");
          paramPrintWriter.println(paramFileDescriptor.getLabel());
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("    mIndex=");
          paramPrintWriter.println(paramFileDescriptor.getIndex());
          if (paramFileDescriptor.countSynonyms() > 0)
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.println("    Synonyms:");
            int j = 0;
            while (j < paramFileDescriptor.countSynonyms())
            {
              paramPrintWriter.print(paramString);
              paramPrintWriter.print("      #");
              paramPrintWriter.print(j);
              paramPrintWriter.print(": ");
              paramPrintWriter.println(paramFileDescriptor.getSynonymAt(j));
              j += 1;
            }
          }
          if (paramFileDescriptor.getExtras() != null)
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("    mExtras=");
            paramPrintWriter.println(paramFileDescriptor.getExtras());
          }
          i += 1;
        }
      }
    }
    
    public VoiceInteractor.PickOptionRequest.Option[] getOptions()
    {
      return this.mOptions;
    }
    
    public CharSequence getPrompt()
    {
      CharSequence localCharSequence = null;
      if (this.mPrompt != null) {
        localCharSequence = this.mPrompt.getVoicePromptAt(0);
      }
      return localCharSequence;
    }
    
    public VoiceInteractor.Prompt getVoicePrompt()
    {
      return this.mPrompt;
    }
    
    public void sendIntermediatePickOptionResult(VoiceInteractor.PickOptionRequest.Option[] paramArrayOfOption, Bundle paramBundle)
    {
      sendPickOptionResult(false, paramArrayOfOption, paramBundle);
    }
    
    void sendPickOptionResult(boolean paramBoolean, VoiceInteractor.PickOptionRequest.Option[] paramArrayOfOption, Bundle paramBundle)
    {
      if (paramBoolean) {}
      try
      {
        finishRequest();
        this.mCallback.deliverPickOptionResult(this.mInterface, paramBoolean, paramArrayOfOption, paramBundle);
        return;
      }
      catch (RemoteException paramArrayOfOption) {}
    }
    
    public void sendPickOptionResult(VoiceInteractor.PickOptionRequest.Option[] paramArrayOfOption, Bundle paramBundle)
    {
      sendPickOptionResult(true, paramArrayOfOption, paramBundle);
    }
  }
  
  public static class Request
  {
    final IVoiceInteractorCallback mCallback;
    final String mCallingPackage;
    final int mCallingUid;
    final Bundle mExtras;
    final IVoiceInteractorRequest mInterface = new IVoiceInteractorRequest.Stub()
    {
      public void cancel()
        throws RemoteException
      {
        VoiceInteractionSession localVoiceInteractionSession = (VoiceInteractionSession)VoiceInteractionSession.Request.this.mSession.get();
        if (localVoiceInteractionSession != null) {
          localVoiceInteractionSession.mHandlerCaller.sendMessage(localVoiceInteractionSession.mHandlerCaller.obtainMessageO(7, VoiceInteractionSession.Request.this));
        }
      }
    };
    final WeakReference<VoiceInteractionSession> mSession;
    
    Request(String paramString, int paramInt, IVoiceInteractorCallback paramIVoiceInteractorCallback, VoiceInteractionSession paramVoiceInteractionSession, Bundle paramBundle)
    {
      this.mCallingPackage = paramString;
      this.mCallingUid = paramInt;
      this.mCallback = paramIVoiceInteractorCallback;
      this.mSession = paramVoiceInteractionSession.mWeakRef;
      this.mExtras = paramBundle;
    }
    
    public void cancel()
    {
      try
      {
        finishRequest();
        this.mCallback.deliverCancel(this.mInterface);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mInterface=");
      paramPrintWriter.println(this.mInterface.asBinder());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCallingPackage=");
      paramPrintWriter.print(this.mCallingPackage);
      paramPrintWriter.print(" mCallingUid=");
      UserHandle.formatUid(paramPrintWriter, this.mCallingUid);
      paramPrintWriter.println();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCallback=");
      paramPrintWriter.println(this.mCallback.asBinder());
      if (this.mExtras != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mExtras=");
        paramPrintWriter.println(this.mExtras);
      }
    }
    
    void finishRequest()
    {
      Object localObject = (VoiceInteractionSession)this.mSession.get();
      if (localObject == null) {
        throw new IllegalStateException("VoiceInteractionSession has been destroyed");
      }
      localObject = ((VoiceInteractionSession)localObject).removeRequest(this.mInterface.asBinder());
      if (localObject == null) {
        throw new IllegalStateException("Request not active: " + this);
      }
      if (localObject != this) {
        throw new IllegalStateException("Current active request " + localObject + " not same as calling request " + this);
      }
    }
    
    public String getCallingPackage()
    {
      return this.mCallingPackage;
    }
    
    public int getCallingUid()
    {
      return this.mCallingUid;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public boolean isActive()
    {
      VoiceInteractionSession localVoiceInteractionSession = (VoiceInteractionSession)this.mSession.get();
      if (localVoiceInteractionSession == null) {
        return false;
      }
      return localVoiceInteractionSession.isRequestActive(this.mInterface.asBinder());
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      DebugUtils.buildShortClassTag(this, localStringBuilder);
      localStringBuilder.append(" ");
      localStringBuilder.append(this.mInterface.asBinder());
      localStringBuilder.append(" pkg=");
      localStringBuilder.append(this.mCallingPackage);
      localStringBuilder.append(" uid=");
      UserHandle.formatUid(localStringBuilder, this.mCallingUid);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/voice/VoiceInteractionSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */