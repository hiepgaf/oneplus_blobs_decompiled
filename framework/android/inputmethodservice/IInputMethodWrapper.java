package android.inputmethodservice;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.InputChannel;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethod.SessionCallback;
import android.view.inputmethod.InputMethodSession;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputContext.Stub;
import com.android.internal.view.IInputMethod.Stub;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.IInputSessionCallback;
import com.android.internal.view.InputConnectionWrapper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class IInputMethodWrapper
  extends IInputMethod.Stub
  implements HandlerCaller.Callback
{
  private static final int DO_ATTACH_TOKEN = 10;
  private static final int DO_CHANGE_INPUTMETHOD_SUBTYPE = 80;
  private static final int DO_CREATE_SESSION = 40;
  private static final int DO_DUMP = 1;
  private static final int DO_HIDE_SOFT_INPUT = 70;
  private static final int DO_RESTART_INPUT = 34;
  private static final int DO_REVOKE_SESSION = 50;
  private static final int DO_SET_INPUT_CONTEXT = 20;
  private static final int DO_SET_SESSION_ENABLED = 45;
  private static final int DO_SHOW_SOFT_INPUT = 60;
  private static final int DO_START_INPUT = 32;
  private static final int DO_UNSET_INPUT_CONTEXT = 30;
  private static final String TAG = "InputMethodWrapper";
  final HandlerCaller mCaller;
  final Context mContext;
  final WeakReference<InputMethod> mInputMethod;
  final WeakReference<AbstractInputMethodService> mTarget;
  final int mTargetSdkVersion;
  
  public IInputMethodWrapper(AbstractInputMethodService paramAbstractInputMethodService, InputMethod paramInputMethod)
  {
    this.mTarget = new WeakReference(paramAbstractInputMethodService);
    this.mContext = paramAbstractInputMethodService.getApplicationContext();
    this.mCaller = new HandlerCaller(this.mContext, null, this, true);
    this.mInputMethod = new WeakReference(paramInputMethod);
    this.mTargetSdkVersion = paramAbstractInputMethodService.getApplicationInfo().targetSdkVersion;
  }
  
  public void attachToken(IBinder paramIBinder)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(10, paramIBinder));
  }
  
  public void bindInput(InputBinding paramInputBinding)
  {
    paramInputBinding = new InputBinding(new InputConnectionWrapper(this.mTarget, IInputContext.Stub.asInterface(paramInputBinding.getConnectionToken()), 0), paramInputBinding);
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(20, paramInputBinding));
  }
  
  public void changeInputMethodSubtype(InputMethodSubtype paramInputMethodSubtype)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(80, paramInputMethodSubtype));
  }
  
  public void createSession(InputChannel paramInputChannel, IInputSessionCallback paramIInputSessionCallback)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOO(40, paramInputChannel, paramIInputSessionCallback));
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    Object localObject = (AbstractInputMethodService)this.mTarget.get();
    if (localObject == null) {
      return;
    }
    if (((AbstractInputMethodService)localObject).checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump InputMethodManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    localObject = new CountDownLatch(1);
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOOOO(1, paramFileDescriptor, paramPrintWriter, paramArrayOfString, localObject));
    try
    {
      if (!((CountDownLatch)localObject).await(5L, TimeUnit.SECONDS)) {
        paramPrintWriter.println("Timeout waiting for dump");
      }
      return;
    }
    catch (InterruptedException paramFileDescriptor)
    {
      paramPrintWriter.println("Interrupted waiting for dump");
    }
  }
  
  /* Error */
  public void executeMessage(android.os.Message paramMessage)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 80	android/inputmethodservice/IInputMethodWrapper:mInputMethod	Ljava/lang/ref/WeakReference;
    //   6: invokevirtual 140	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
    //   9: checkcast 209	android/view/inputmethod/InputMethod
    //   12: astore 4
    //   14: aload 4
    //   16: ifnonnull +40 -> 56
    //   19: aload_1
    //   20: getfield 214	android/os/Message:what	I
    //   23: iconst_1
    //   24: if_icmpeq +32 -> 56
    //   27: ldc 41
    //   29: new 148	java/lang/StringBuilder
    //   32: dup
    //   33: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   36: ldc -40
    //   38: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   41: aload_1
    //   42: getfield 214	android/os/Message:what	I
    //   45: invokevirtual 164	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   48: invokevirtual 173	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   51: invokestatic 222	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   54: pop
    //   55: return
    //   56: aload_1
    //   57: getfield 214	android/os/Message:what	I
    //   60: lookupswitch	default:+108->168, 1:+137->197, 10:+258->318, 20:+273->333, 30:+288->348, 32:+296->356, 34:+377->437, 40:+458->518, 45:+503->563, 50:+535->595, 60:+550->610, 70:+569->629, 80:+588->648
    //   168: ldc 41
    //   170: new 148	java/lang/StringBuilder
    //   173: dup
    //   174: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   177: ldc -32
    //   179: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: aload_1
    //   183: getfield 214	android/os/Message:what	I
    //   186: invokevirtual 164	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   189: invokevirtual 173	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokestatic 222	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   195: pop
    //   196: return
    //   197: aload_0
    //   198: getfield 63	android/inputmethodservice/IInputMethodWrapper:mTarget	Ljava/lang/ref/WeakReference;
    //   201: invokevirtual 140	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
    //   204: checkcast 65	android/inputmethodservice/AbstractInputMethodService
    //   207: astore 4
    //   209: aload 4
    //   211: ifnonnull +4 -> 215
    //   214: return
    //   215: aload_1
    //   216: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   219: checkcast 230	com/android/internal/os/SomeArgs
    //   222: astore_1
    //   223: aload 4
    //   225: aload_1
    //   226: getfield 233	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
    //   229: checkcast 235	java/io/FileDescriptor
    //   232: aload_1
    //   233: getfield 238	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
    //   236: checkcast 175	java/io/PrintWriter
    //   239: aload_1
    //   240: getfield 241	com/android/internal/os/SomeArgs:arg3	Ljava/lang/Object;
    //   243: checkcast 243	[Ljava/lang/String;
    //   246: invokevirtual 245	android/inputmethodservice/AbstractInputMethodService:dump	(Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    //   249: aload_1
    //   250: getfield 248	com/android/internal/os/SomeArgs:arg4	Ljava/lang/Object;
    //   253: astore 4
    //   255: aload 4
    //   257: monitorenter
    //   258: aload_1
    //   259: getfield 248	com/android/internal/os/SomeArgs:arg4	Ljava/lang/Object;
    //   262: checkcast 181	java/util/concurrent/CountDownLatch
    //   265: invokevirtual 251	java/util/concurrent/CountDownLatch:countDown	()V
    //   268: aload 4
    //   270: monitorexit
    //   271: aload_1
    //   272: invokevirtual 254	com/android/internal/os/SomeArgs:recycle	()V
    //   275: return
    //   276: astore 4
    //   278: aload_1
    //   279: getfield 238	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
    //   282: checkcast 175	java/io/PrintWriter
    //   285: new 148	java/lang/StringBuilder
    //   288: dup
    //   289: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   292: ldc_w 256
    //   295: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   298: aload 4
    //   300: invokevirtual 259	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   303: invokevirtual 173	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   306: invokevirtual 179	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   309: goto -60 -> 249
    //   312: astore_1
    //   313: aload 4
    //   315: monitorexit
    //   316: aload_1
    //   317: athrow
    //   318: aload 4
    //   320: aload_1
    //   321: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   324: checkcast 261	android/os/IBinder
    //   327: invokeinterface 263 2 0
    //   332: return
    //   333: aload 4
    //   335: aload_1
    //   336: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   339: checkcast 106	android/view/inputmethod/InputBinding
    //   342: invokeinterface 265 2 0
    //   347: return
    //   348: aload 4
    //   350: invokeinterface 268 1 0
    //   355: return
    //   356: aload_1
    //   357: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   360: checkcast 230	com/android/internal/os/SomeArgs
    //   363: astore 5
    //   365: aload_1
    //   366: getfield 270	android/os/Message:arg1	I
    //   369: istore_2
    //   370: aload 5
    //   372: getfield 233	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
    //   375: checkcast 272	com/android/internal/view/IInputContext
    //   378: astore_1
    //   379: aload_1
    //   380: ifnull +52 -> 432
    //   383: new 108	com/android/internal/view/InputConnectionWrapper
    //   386: dup
    //   387: aload_0
    //   388: getfield 63	android/inputmethodservice/IInputMethodWrapper:mTarget	Ljava/lang/ref/WeakReference;
    //   391: aload_1
    //   392: iload_2
    //   393: invokespecial 121	com/android/internal/view/InputConnectionWrapper:<init>	(Ljava/lang/ref/WeakReference;Lcom/android/internal/view/IInputContext;I)V
    //   396: astore_1
    //   397: aload 5
    //   399: getfield 238	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
    //   402: checkcast 274	android/view/inputmethod/EditorInfo
    //   405: astore 6
    //   407: aload 6
    //   409: aload_0
    //   410: getfield 91	android/inputmethodservice/IInputMethodWrapper:mTargetSdkVersion	I
    //   413: invokevirtual 277	android/view/inputmethod/EditorInfo:makeCompatible	(I)V
    //   416: aload 4
    //   418: aload_1
    //   419: aload 6
    //   421: invokeinterface 281 3 0
    //   426: aload 5
    //   428: invokevirtual 254	com/android/internal/os/SomeArgs:recycle	()V
    //   431: return
    //   432: aconst_null
    //   433: astore_1
    //   434: goto -37 -> 397
    //   437: aload_1
    //   438: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   441: checkcast 230	com/android/internal/os/SomeArgs
    //   444: astore 5
    //   446: aload_1
    //   447: getfield 270	android/os/Message:arg1	I
    //   450: istore_2
    //   451: aload 5
    //   453: getfield 233	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
    //   456: checkcast 272	com/android/internal/view/IInputContext
    //   459: astore_1
    //   460: aload_1
    //   461: ifnull +52 -> 513
    //   464: new 108	com/android/internal/view/InputConnectionWrapper
    //   467: dup
    //   468: aload_0
    //   469: getfield 63	android/inputmethodservice/IInputMethodWrapper:mTarget	Ljava/lang/ref/WeakReference;
    //   472: aload_1
    //   473: iload_2
    //   474: invokespecial 121	com/android/internal/view/InputConnectionWrapper:<init>	(Ljava/lang/ref/WeakReference;Lcom/android/internal/view/IInputContext;I)V
    //   477: astore_1
    //   478: aload 5
    //   480: getfield 238	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
    //   483: checkcast 274	android/view/inputmethod/EditorInfo
    //   486: astore 6
    //   488: aload 6
    //   490: aload_0
    //   491: getfield 91	android/inputmethodservice/IInputMethodWrapper:mTargetSdkVersion	I
    //   494: invokevirtual 277	android/view/inputmethod/EditorInfo:makeCompatible	(I)V
    //   497: aload 4
    //   499: aload_1
    //   500: aload 6
    //   502: invokeinterface 284 3 0
    //   507: aload 5
    //   509: invokevirtual 254	com/android/internal/os/SomeArgs:recycle	()V
    //   512: return
    //   513: aconst_null
    //   514: astore_1
    //   515: goto -37 -> 478
    //   518: aload_1
    //   519: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   522: checkcast 230	com/android/internal/os/SomeArgs
    //   525: astore_1
    //   526: aload 4
    //   528: new 8	android/inputmethodservice/IInputMethodWrapper$InputMethodSessionCallbackWrapper
    //   531: dup
    //   532: aload_0
    //   533: getfield 71	android/inputmethodservice/IInputMethodWrapper:mContext	Landroid/content/Context;
    //   536: aload_1
    //   537: getfield 233	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
    //   540: checkcast 286	android/view/InputChannel
    //   543: aload_1
    //   544: getfield 238	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
    //   547: checkcast 288	com/android/internal/view/IInputSessionCallback
    //   550: invokespecial 291	android/inputmethodservice/IInputMethodWrapper$InputMethodSessionCallbackWrapper:<init>	(Landroid/content/Context;Landroid/view/InputChannel;Lcom/android/internal/view/IInputSessionCallback;)V
    //   553: invokeinterface 294 2 0
    //   558: aload_1
    //   559: invokevirtual 254	com/android/internal/os/SomeArgs:recycle	()V
    //   562: return
    //   563: aload_1
    //   564: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   567: checkcast 296	android/view/inputmethod/InputMethodSession
    //   570: astore 5
    //   572: aload_1
    //   573: getfield 270	android/os/Message:arg1	I
    //   576: ifeq +14 -> 590
    //   579: aload 4
    //   581: aload 5
    //   583: iload_3
    //   584: invokeinterface 300 3 0
    //   589: return
    //   590: iconst_0
    //   591: istore_3
    //   592: goto -13 -> 579
    //   595: aload 4
    //   597: aload_1
    //   598: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   601: checkcast 296	android/view/inputmethod/InputMethodSession
    //   604: invokeinterface 304 2 0
    //   609: return
    //   610: aload 4
    //   612: aload_1
    //   613: getfield 270	android/os/Message:arg1	I
    //   616: aload_1
    //   617: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   620: checkcast 306	android/os/ResultReceiver
    //   623: invokeinterface 310 3 0
    //   628: return
    //   629: aload 4
    //   631: aload_1
    //   632: getfield 270	android/os/Message:arg1	I
    //   635: aload_1
    //   636: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   639: checkcast 306	android/os/ResultReceiver
    //   642: invokeinterface 313 3 0
    //   647: return
    //   648: aload 4
    //   650: aload_1
    //   651: getfield 228	android/os/Message:obj	Ljava/lang/Object;
    //   654: checkcast 315	android/view/inputmethod/InputMethodSubtype
    //   657: invokeinterface 317 2 0
    //   662: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	663	0	this	IInputMethodWrapper
    //   0	663	1	paramMessage	android.os.Message
    //   369	105	2	i	int
    //   1	591	3	bool	boolean
    //   276	373	4	localRuntimeException	RuntimeException
    //   363	219	5	localObject2	Object
    //   405	96	6	localEditorInfo	EditorInfo
    // Exception table:
    //   from	to	target	type
    //   223	249	276	java/lang/RuntimeException
    //   258	268	312	finally
  }
  
  public InputMethod getInternalInputMethod()
  {
    return (InputMethod)this.mInputMethod.get();
  }
  
  public void hideSoftInput(int paramInt, ResultReceiver paramResultReceiver)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIO(70, paramInt, paramResultReceiver));
  }
  
  public void restartInput(IInputContext paramIInputContext, int paramInt, EditorInfo paramEditorInfo)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIOO(34, paramInt, paramIInputContext, paramEditorInfo));
  }
  
  public void revokeSession(IInputMethodSession paramIInputMethodSession)
  {
    try
    {
      InputMethodSession localInputMethodSession = ((IInputMethodSessionWrapper)paramIInputMethodSession).getInternalInputMethodSession();
      if (localInputMethodSession == null)
      {
        Log.w("InputMethodWrapper", "Session is already finished: " + paramIInputMethodSession);
        return;
      }
      this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(50, localInputMethodSession));
      return;
    }
    catch (ClassCastException localClassCastException)
    {
      Log.w("InputMethodWrapper", "Incoming session not of correct type: " + paramIInputMethodSession, localClassCastException);
    }
  }
  
  public void setSessionEnabled(IInputMethodSession paramIInputMethodSession, boolean paramBoolean)
  {
    try
    {
      InputMethodSession localInputMethodSession = ((IInputMethodSessionWrapper)paramIInputMethodSession).getInternalInputMethodSession();
      if (localInputMethodSession == null)
      {
        Log.w("InputMethodWrapper", "Session is already finished: " + paramIInputMethodSession);
        return;
      }
      HandlerCaller localHandlerCaller1 = this.mCaller;
      HandlerCaller localHandlerCaller2 = this.mCaller;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandlerCaller1.executeOrSendMessage(localHandlerCaller2.obtainMessageIO(45, i, localInputMethodSession));
        return;
      }
      return;
    }
    catch (ClassCastException localClassCastException)
    {
      Log.w("InputMethodWrapper", "Incoming session not of correct type: " + paramIInputMethodSession, localClassCastException);
    }
  }
  
  public void showSoftInput(int paramInt, ResultReceiver paramResultReceiver)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIO(60, paramInt, paramResultReceiver));
  }
  
  public void startInput(IInputContext paramIInputContext, int paramInt, EditorInfo paramEditorInfo)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIOO(32, paramInt, paramIInputContext, paramEditorInfo));
  }
  
  public void unbindInput()
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessage(30));
  }
  
  static final class InputMethodSessionCallbackWrapper
    implements InputMethod.SessionCallback
  {
    final IInputSessionCallback mCb;
    final InputChannel mChannel;
    final Context mContext;
    
    InputMethodSessionCallbackWrapper(Context paramContext, InputChannel paramInputChannel, IInputSessionCallback paramIInputSessionCallback)
    {
      this.mContext = paramContext;
      this.mChannel = paramInputChannel;
      this.mCb = paramIInputSessionCallback;
    }
    
    public void sessionCreated(InputMethodSession paramInputMethodSession)
    {
      if (paramInputMethodSession != null) {}
      try
      {
        paramInputMethodSession = new IInputMethodSessionWrapper(this.mContext, paramInputMethodSession, this.mChannel);
        this.mCb.sessionCreated(paramInputMethodSession);
        return;
      }
      catch (RemoteException paramInputMethodSession) {}
      if (this.mChannel != null) {
        this.mChannel.dispose();
      }
      this.mCb.sessionCreated(null);
      return;
    }
  }
  
  static class Notifier
  {
    boolean notified;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/IInputMethodWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */