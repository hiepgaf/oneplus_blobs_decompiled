package android.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.SurfaceTexture;
import android.os.IBinder;
import android.os.OperationCanceledException;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.internal.annotations.GuardedBy;
import dalvik.system.CloseGuard;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityView
  extends ViewGroup
{
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final boolean DEBUG = false;
  private static final int KEEP_ALIVE = 1;
  private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
  private static final int MINIMUM_POOL_SIZE = 1;
  private static final int MSG_SET_SURFACE = 1;
  private static final String TAG = "ActivityView";
  private static final Executor sExecutor = new ThreadPoolExecutor(1, MAXIMUM_POOL_SIZE, 1L, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
  private static final BlockingQueue<Runnable> sPoolWorkQueue;
  private static final ThreadFactory sThreadFactory = new ThreadFactory()
  {
    private final AtomicInteger mCount = new AtomicInteger(1);
    
    public Thread newThread(Runnable paramAnonymousRunnable)
    {
      return new Thread(paramAnonymousRunnable, "ActivityView #" + this.mCount.getAndIncrement());
    }
  };
  private Activity mActivity;
  @GuardedBy("mActivityContainerLock")
  private ActivityContainerWrapper mActivityContainer;
  private Object mActivityContainerLock = new Object();
  private ActivityViewCallback mActivityViewCallback;
  private final int mDensityDpi;
  private final SerialExecutor mExecutor = new SerialExecutor(null);
  private int mHeight;
  private int mLastVisibility;
  private Surface mSurface;
  private final TextureView mTextureView;
  private int mWidth;
  
  static
  {
    sPoolWorkQueue = new LinkedBlockingQueue(128);
  }
  
  public ActivityView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActivityView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ActivityView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    for (;;)
    {
      if ((paramContext instanceof ContextWrapper))
      {
        if ((paramContext instanceof Activity)) {
          this.mActivity = paramContext;
        }
      }
      else
      {
        if (this.mActivity != null) {
          break;
        }
        throw new IllegalStateException("The ActivityView's Context is not an Activity.");
      }
      paramContext = paramContext.getBaseContext();
    }
    try
    {
      this.mActivityContainer = new ActivityContainerWrapper(ActivityManagerNative.getDefault().createVirtualActivityContainer(this.mActivity.getActivityToken(), new ActivityContainerCallback(this)));
      this.mTextureView = new TextureView(paramContext);
      this.mTextureView.setSurfaceTextureListener(new ActivityViewSurfaceTextureListener(null));
      addView(this.mTextureView);
      paramContext = (WindowManager)this.mActivity.getSystemService("window");
      paramAttributeSet = new DisplayMetrics();
      paramContext.getDefaultDisplay().getMetrics(paramAttributeSet);
      this.mDensityDpi = paramAttributeSet.densityDpi;
      this.mLastVisibility = getVisibility();
      return;
    }
    catch (RemoteException paramContext)
    {
      throw new RuntimeException("ActivityView: Unable to create ActivityContainer. " + paramContext);
    }
  }
  
  private boolean injectInputEvent(InputEvent paramInputEvent)
  {
    if (this.mActivityContainer != null) {
      return this.mActivityContainer.injectEvent(paramInputEvent);
    }
    return false;
  }
  
  private void setSurfaceAsync(final Surface paramSurface, final int paramInt1, final int paramInt2, final int paramInt3, final boolean paramBoolean)
  {
    this.mExecutor.execute(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 27	android/app/ActivityView$2:this$0	Landroid/app/ActivityView;
        //   4: invokestatic 48	android/app/ActivityView:-get1	(Landroid/app/ActivityView;)Ljava/lang/Object;
        //   7: astore_1
        //   8: aload_1
        //   9: monitorenter
        //   10: aload_0
        //   11: getfield 27	android/app/ActivityView$2:this$0	Landroid/app/ActivityView;
        //   14: invokestatic 52	android/app/ActivityView:-get0	(Landroid/app/ActivityView;)Landroid/app/ActivityView$ActivityContainerWrapper;
        //   17: ifnull +29 -> 46
        //   20: aload_0
        //   21: getfield 27	android/app/ActivityView$2:this$0	Landroid/app/ActivityView;
        //   24: invokestatic 52	android/app/ActivityView:-get0	(Landroid/app/ActivityView;)Landroid/app/ActivityView$ActivityContainerWrapper;
        //   27: aload_0
        //   28: getfield 29	android/app/ActivityView$2:val$surface	Landroid/view/Surface;
        //   31: aload_0
        //   32: getfield 31	android/app/ActivityView$2:val$width	I
        //   35: aload_0
        //   36: getfield 33	android/app/ActivityView$2:val$height	I
        //   39: aload_0
        //   40: getfield 35	android/app/ActivityView$2:val$densityDpi	I
        //   43: invokevirtual 58	android/app/ActivityView$ActivityContainerWrapper:setSurface	(Landroid/view/Surface;III)V
        //   46: aload_1
        //   47: monitorexit
        //   48: aload_0
        //   49: getfield 37	android/app/ActivityView$2:val$callback	Z
        //   52: ifeq +23 -> 75
        //   55: aload_0
        //   56: getfield 27	android/app/ActivityView$2:this$0	Landroid/app/ActivityView;
        //   59: new 13	android/app/ActivityView$2$1
        //   62: dup
        //   63: aload_0
        //   64: aload_0
        //   65: getfield 29	android/app/ActivityView$2:val$surface	Landroid/view/Surface;
        //   68: invokespecial 61	android/app/ActivityView$2$1:<init>	(Landroid/app/ActivityView$2;Landroid/view/Surface;)V
        //   71: invokevirtual 67	android/view/View:post	(Ljava/lang/Runnable;)Z
        //   74: pop
        //   75: return
        //   76: astore_2
        //   77: aload_1
        //   78: monitorexit
        //   79: aload_2
        //   80: athrow
        //   81: astore_1
        //   82: new 69	java/lang/RuntimeException
        //   85: dup
        //   86: ldc 71
        //   88: aload_1
        //   89: invokespecial 74	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	2
        //   81	8	1	localRemoteException	RemoteException
        //   76	4	2	localObject2	Object
        // Exception table:
        //   from	to	target	type
        //   10	46	76	finally
        //   0	10	81	android/os/RemoteException
        //   46	48	81	android/os/RemoteException
        //   77	81	81	android/os/RemoteException
      }
    });
  }
  
  public boolean isAttachedToDisplay()
  {
    return this.mSurface != null;
  }
  
  public void onAttachedToWindow() {}
  
  public void onDetachedFromWindow() {}
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.isFromSource(2)) && (injectInputEvent(paramMotionEvent))) {
      return true;
    }
    return super.onGenericMotionEvent(paramMotionEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mTextureView.layout(0, 0, paramInt3 - paramInt1, paramInt4 - paramInt2);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!injectInputEvent(paramMotionEvent)) {
      return super.onTouchEvent(paramMotionEvent);
    }
    return true;
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if ((this.mSurface != null) && ((paramInt == 8) || (this.mLastVisibility == 8))) {
      if (paramInt != 8) {
        break label60;
      }
    }
    label60:
    for (paramView = null;; paramView = this.mSurface)
    {
      setSurfaceAsync(paramView, this.mWidth, this.mHeight, this.mDensityDpi, false);
      this.mLastVisibility = paramInt;
      return;
    }
  }
  
  public void release()
  {
    if (this.mActivityContainer == null)
    {
      Log.e("ActivityView", "Duplicate call to release");
      return;
    }
    synchronized (this.mActivityContainerLock)
    {
      this.mActivityContainer.release();
      this.mActivityContainer = null;
      if (this.mSurface != null)
      {
        this.mSurface.release();
        this.mSurface = null;
      }
      this.mTextureView.setSurfaceTextureListener(null);
      return;
    }
  }
  
  public void setCallback(ActivityViewCallback paramActivityViewCallback)
  {
    this.mActivityViewCallback = paramActivityViewCallback;
    if (this.mSurface != null) {
      this.mActivityViewCallback.onSurfaceAvailable(this);
    }
  }
  
  public void startActivity(PendingIntent paramPendingIntent)
  {
    if (this.mActivityContainer == null) {
      throw new IllegalStateException("Attempt to call startActivity after release");
    }
    if (this.mSurface == null) {
      throw new IllegalStateException("Surface not yet created.");
    }
    paramPendingIntent = paramPendingIntent.getTarget();
    if (this.mActivityContainer.startActivityIntentSender(paramPendingIntent) == -6) {
      throw new OperationCanceledException();
    }
  }
  
  public void startActivity(Intent paramIntent)
  {
    if (this.mActivityContainer == null) {
      throw new IllegalStateException("Attempt to call startActivity after release");
    }
    if (this.mSurface == null) {
      throw new IllegalStateException("Surface not yet created.");
    }
    if (this.mActivityContainer.startActivity(paramIntent) == -6) {
      throw new OperationCanceledException();
    }
  }
  
  public void startActivity(IntentSender paramIntentSender)
  {
    if (this.mActivityContainer == null) {
      throw new IllegalStateException("Attempt to call startActivity after release");
    }
    if (this.mSurface == null) {
      throw new IllegalStateException("Surface not yet created.");
    }
    paramIntentSender = paramIntentSender.getTarget();
    if (this.mActivityContainer.startActivityIntentSender(paramIntentSender) == -6) {
      throw new OperationCanceledException();
    }
  }
  
  private static class ActivityContainerCallback
    extends IActivityContainerCallback.Stub
  {
    private final WeakReference<ActivityView> mActivityViewWeakReference;
    
    ActivityContainerCallback(ActivityView paramActivityView)
    {
      this.mActivityViewWeakReference = new WeakReference(paramActivityView);
    }
    
    public void onAllActivitiesComplete(final IBinder paramIBinder)
    {
      paramIBinder = (ActivityView)this.mActivityViewWeakReference.get();
      if (paramIBinder != null)
      {
        ActivityView.ActivityViewCallback localActivityViewCallback = ActivityView.-get2(paramIBinder);
        if (localActivityViewCallback != null) {
          paramIBinder.post(new Runnable()
          {
            public void run()
            {
              ActivityView.ActivityViewCallback localActivityViewCallback = (ActivityView.ActivityViewCallback)this.val$callbackRef.get();
              if (localActivityViewCallback != null) {
                localActivityViewCallback.onAllActivitiesComplete(paramIBinder);
              }
            }
          });
        }
      }
    }
    
    public void setVisible(IBinder paramIBinder, boolean paramBoolean) {}
  }
  
  private static class ActivityContainerWrapper
  {
    private final CloseGuard mGuard = CloseGuard.get();
    private final IActivityContainer mIActivityContainer;
    boolean mOpened;
    
    ActivityContainerWrapper(IActivityContainer paramIActivityContainer)
    {
      this.mIActivityContainer = paramIActivityContainer;
      this.mOpened = true;
      this.mGuard.open("release");
    }
    
    void attachToDisplay(int paramInt)
    {
      try
      {
        this.mIActivityContainer.attachToDisplay(paramInt);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    protected void finalize()
      throws Throwable
    {
      try
      {
        if (this.mGuard != null)
        {
          this.mGuard.warnIfOpen();
          release();
        }
        return;
      }
      finally
      {
        super.finalize();
      }
    }
    
    int getDisplayId()
    {
      try
      {
        int i = this.mIActivityContainer.getDisplayId();
        return i;
      }
      catch (RemoteException localRemoteException) {}
      return -1;
    }
    
    boolean injectEvent(InputEvent paramInputEvent)
    {
      try
      {
        boolean bool = this.mIActivityContainer.injectEvent(paramInputEvent);
        return bool;
      }
      catch (RemoteException paramInputEvent) {}
      return false;
    }
    
    void release()
    {
      synchronized (this.mGuard)
      {
        boolean bool = this.mOpened;
        if (!bool) {}
      }
      try
      {
        this.mIActivityContainer.release();
        this.mGuard.close();
        this.mOpened = false;
        return;
        localObject = finally;
        throw ((Throwable)localObject);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
    
    void setSurface(Surface paramSurface, int paramInt1, int paramInt2, int paramInt3)
      throws RemoteException
    {
      this.mIActivityContainer.setSurface(paramSurface, paramInt1, paramInt2, paramInt3);
    }
    
    int startActivity(Intent paramIntent)
    {
      try
      {
        int i = this.mIActivityContainer.startActivity(paramIntent);
        return i;
      }
      catch (RemoteException paramIntent)
      {
        throw new RuntimeException("ActivityView: Unable to startActivity. " + paramIntent);
      }
    }
    
    int startActivityIntentSender(IIntentSender paramIIntentSender)
    {
      try
      {
        int i = this.mIActivityContainer.startActivityIntentSender(paramIIntentSender);
        return i;
      }
      catch (RemoteException paramIIntentSender)
      {
        throw new RuntimeException("ActivityView: Unable to startActivity from IntentSender. " + paramIIntentSender);
      }
    }
  }
  
  public static abstract class ActivityViewCallback
  {
    public abstract void onAllActivitiesComplete(ActivityView paramActivityView);
    
    public abstract void onSurfaceAvailable(ActivityView paramActivityView);
    
    public abstract void onSurfaceDestroyed(ActivityView paramActivityView);
  }
  
  private class ActivityViewSurfaceTextureListener
    implements TextureView.SurfaceTextureListener
  {
    private ActivityViewSurfaceTextureListener() {}
    
    public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
    {
      if (ActivityView.-get0(ActivityView.this) == null) {
        return;
      }
      ActivityView.-set2(ActivityView.this, paramInt1);
      ActivityView.-set0(ActivityView.this, paramInt2);
      ActivityView.-set1(ActivityView.this, new Surface(paramSurfaceTexture));
      ActivityView.-wrap0(ActivityView.this, ActivityView.-get5(ActivityView.this), ActivityView.-get6(ActivityView.this), ActivityView.-get4(ActivityView.this), ActivityView.-get3(ActivityView.this), true);
    }
    
    public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
    {
      if (ActivityView.-get0(ActivityView.this) == null) {
        return true;
      }
      ActivityView.-get5(ActivityView.this).release();
      ActivityView.-set1(ActivityView.this, null);
      ActivityView.-wrap0(ActivityView.this, null, ActivityView.-get6(ActivityView.this), ActivityView.-get4(ActivityView.this), ActivityView.-get3(ActivityView.this), true);
      return true;
    }
    
    public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
    {
      if (ActivityView.-get0(ActivityView.this) == null) {}
    }
    
    public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture) {}
  }
  
  private static class SerialExecutor
    implements Executor
  {
    private Runnable mActive;
    private final ArrayDeque<Runnable> mTasks = new ArrayDeque();
    
    public void execute(final Runnable paramRunnable)
    {
      try
      {
        this.mTasks.offer(new Runnable()
        {
          public void run()
          {
            try
            {
              paramRunnable.run();
              return;
            }
            finally
            {
              ActivityView.SerialExecutor.this.scheduleNext();
            }
          }
        });
        if (this.mActive == null) {
          scheduleNext();
        }
        return;
      }
      finally
      {
        paramRunnable = finally;
        throw paramRunnable;
      }
    }
    
    protected void scheduleNext()
    {
      try
      {
        Runnable localRunnable = (Runnable)this.mTasks.poll();
        this.mActive = localRunnable;
        if (localRunnable != null) {
          ActivityView.-get7().execute(this.mActive);
        }
        return;
      }
      finally {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */