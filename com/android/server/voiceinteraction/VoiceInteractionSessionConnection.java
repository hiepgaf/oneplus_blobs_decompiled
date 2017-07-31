package com.android.server.voiceinteraction;

import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.service.voice.IVoiceInteractionSession;
import android.service.voice.IVoiceInteractionSessionService;
import android.service.voice.IVoiceInteractionSessionService.Stub;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IAssistScreenshotReceiver;
import com.android.internal.app.IAssistScreenshotReceiver.Stub;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractionSessionShowCallback.Stub;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.IResultReceiver.Stub;
import com.android.server.LocalServices;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

final class VoiceInteractionSessionConnection
  implements ServiceConnection
{
  private static final String KEY_RECEIVER_EXTRA_COUNT = "count";
  private static final String KEY_RECEIVER_EXTRA_INDEX = "index";
  static final String TAG = "VoiceInteractionServiceManager";
  final IActivityManager mAm;
  final AppOpsManager mAppOps;
  ArrayList<AssistDataForActivity> mAssistData = new ArrayList();
  final IResultReceiver mAssistReceiver = new IResultReceiver.Stub()
  {
    public void send(int paramAnonymousInt, Bundle paramAnonymousBundle)
      throws RemoteException
    {
      synchronized (VoiceInteractionSessionConnection.this.mLock)
      {
        if (VoiceInteractionSessionConnection.this.mShown)
        {
          VoiceInteractionSessionConnection.this.mHaveAssistData = true;
          VoiceInteractionSessionConnection.this.mAssistData.add(new VoiceInteractionSessionConnection.AssistDataForActivity(paramAnonymousBundle));
          VoiceInteractionSessionConnection.this.deliverSessionDataLocked();
        }
        return;
      }
    }
  };
  final Intent mBindIntent;
  boolean mBound;
  final Callback mCallback;
  final int mCallingUid;
  boolean mCanceled;
  final Context mContext;
  final ServiceConnection mFullConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder) {}
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName) {}
  };
  boolean mFullyBound;
  final Handler mHandler;
  boolean mHaveAssistData;
  boolean mHaveScreenshot;
  final IWindowManager mIWindowManager;
  IVoiceInteractor mInteractor;
  final Object mLock;
  int mPendingAssistDataCount;
  ArrayList<IVoiceInteractionSessionShowCallback> mPendingShowCallbacks = new ArrayList();
  final IBinder mPermissionOwner;
  Bitmap mScreenshot;
  final IAssistScreenshotReceiver mScreenshotReceiver = new IAssistScreenshotReceiver.Stub()
  {
    public void send(Bitmap paramAnonymousBitmap)
      throws RemoteException
    {
      synchronized (VoiceInteractionSessionConnection.this.mLock)
      {
        if (VoiceInteractionSessionConnection.this.mShown)
        {
          VoiceInteractionSessionConnection.this.mHaveScreenshot = true;
          VoiceInteractionSessionConnection.this.mScreenshot = paramAnonymousBitmap;
          VoiceInteractionSessionConnection.this.deliverSessionDataLocked();
        }
        return;
      }
    }
  };
  IVoiceInteractionSessionService mService;
  IVoiceInteractionSession mSession;
  final ComponentName mSessionComponentName;
  Bundle mShowArgs;
  private Runnable mShowAssistDisclosureRunnable = new Runnable()
  {
    public void run()
    {
      StatusBarManagerInternal localStatusBarManagerInternal = (StatusBarManagerInternal)LocalServices.getService(StatusBarManagerInternal.class);
      if (localStatusBarManagerInternal != null) {
        localStatusBarManagerInternal.showAssistDisclosure();
      }
    }
  };
  IVoiceInteractionSessionShowCallback mShowCallback = new IVoiceInteractionSessionShowCallback.Stub()
  {
    public void onFailed()
      throws RemoteException
    {
      synchronized (VoiceInteractionSessionConnection.this.mLock)
      {
        VoiceInteractionSessionConnection.-wrap0(VoiceInteractionSessionConnection.this);
        return;
      }
    }
    
    public void onShown()
      throws RemoteException
    {
      synchronized (VoiceInteractionSessionConnection.this.mLock)
      {
        VoiceInteractionSessionConnection.-wrap1(VoiceInteractionSessionConnection.this);
        return;
      }
    }
  };
  int mShowFlags;
  boolean mShown;
  final IBinder mToken = new Binder();
  final int mUser;
  
  public VoiceInteractionSessionConnection(Object paramObject, ComponentName paramComponentName, int paramInt1, Context paramContext, Callback paramCallback, int paramInt2, Handler paramHandler)
  {
    this.mLock = paramObject;
    this.mSessionComponentName = paramComponentName;
    this.mUser = paramInt1;
    this.mContext = paramContext;
    this.mCallback = paramCallback;
    this.mCallingUid = paramInt2;
    this.mHandler = paramHandler;
    this.mAm = ActivityManagerNative.getDefault();
    this.mIWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService(AppOpsManager.class));
    paramObject = null;
    try
    {
      paramComponentName = this.mAm.newUriPermissionOwner("voicesession:" + paramComponentName.flattenToShortString());
      paramObject = paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      for (;;)
      {
        try
        {
          this.mIWindowManager.addWindowToken(this.mToken, 2031);
          return;
        }
        catch (RemoteException paramObject)
        {
          Slog.w("VoiceInteractionServiceManager", "Failed adding window token", (Throwable)paramObject);
          return;
        }
        paramComponentName = paramComponentName;
        Slog.w("voicesession", "AM dead", paramComponentName);
      }
    }
    this.mPermissionOwner = ((IBinder)paramObject);
    this.mBindIntent = new Intent("android.service.voice.VoiceInteractionService");
    this.mBindIntent.setComponent(this.mSessionComponentName);
    this.mBound = this.mContext.bindServiceAsUser(this.mBindIntent, this, 49, new UserHandle(this.mUser));
    if (this.mBound) {}
    Slog.w("VoiceInteractionServiceManager", "Failed binding to voice interaction session service " + this.mSessionComponentName);
  }
  
  private void deliverSessionDataLocked(AssistDataForActivity paramAssistDataForActivity)
  {
    Bundle localBundle = paramAssistDataForActivity.data.getBundle("data");
    AssistStructure localAssistStructure = (AssistStructure)paramAssistDataForActivity.data.getParcelable("structure");
    AssistContent localAssistContent = (AssistContent)paramAssistDataForActivity.data.getParcelable("content");
    int i = paramAssistDataForActivity.data.getInt("android.intent.extra.ASSIST_UID", -1);
    if ((i >= 0) && (localAssistContent != null))
    {
      Object localObject = localAssistContent.getIntent();
      if (localObject != null)
      {
        ClipData localClipData = ((Intent)localObject).getClipData();
        if ((localClipData != null) && (Intent.isAccessUriMode(((Intent)localObject).getFlags()))) {
          grantClipDataPermissions(localClipData, ((Intent)localObject).getFlags(), i, this.mCallingUid, this.mSessionComponentName.getPackageName());
        }
      }
      localObject = localAssistContent.getClipData();
      if (localObject != null) {
        grantClipDataPermissions((ClipData)localObject, 1, i, this.mCallingUid, this.mSessionComponentName.getPackageName());
      }
    }
    try
    {
      this.mSession.handleAssist(localBundle, localAssistStructure, localAssistContent, paramAssistDataForActivity.activityIndex, paramAssistDataForActivity.activityCount);
      return;
    }
    catch (RemoteException paramAssistDataForActivity) {}
  }
  
  private void notifyPendingShowCallbacksFailedLocked()
  {
    int i = 0;
    for (;;)
    {
      if (i < this.mPendingShowCallbacks.size()) {}
      try
      {
        ((IVoiceInteractionSessionShowCallback)this.mPendingShowCallbacks.get(i)).onFailed();
        i += 1;
        continue;
        this.mPendingShowCallbacks.clear();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  private void notifyPendingShowCallbacksShownLocked()
  {
    int i = 0;
    for (;;)
    {
      if (i < this.mPendingShowCallbacks.size()) {}
      try
      {
        ((IVoiceInteractionSessionShowCallback)this.mPendingShowCallbacks.get(i)).onShown();
        i += 1;
        continue;
        this.mPendingShowCallbacks.clear();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  public void cancelLocked(boolean paramBoolean)
  {
    hideLocked();
    this.mCanceled = true;
    if ((!this.mBound) || (this.mSession != null)) {}
    try
    {
      this.mSession.destroy();
      if ((!paramBoolean) || (this.mSession == null)) {}
    }
    catch (RemoteException localRuntimeException)
    {
      try
      {
        for (;;)
        {
          this.mAm.finishVoiceTask(this.mSession);
          try
          {
            this.mContext.unbindService(this);
          }
          catch (RuntimeException localRuntimeException)
          {
            try
            {
              for (;;)
              {
                this.mIWindowManager.removeWindowToken(this.mToken);
                this.mBound = false;
                this.mService = null;
                this.mSession = null;
                this.mInteractor = null;
                if (this.mFullyBound)
                {
                  this.mContext.unbindService(this.mFullConnection);
                  this.mFullyBound = false;
                }
                return;
                localRemoteException1 = localRemoteException1;
                Slog.w("VoiceInteractionServiceManager", "Voice interation session already dead");
                break;
                localRuntimeException = localRuntimeException;
                Slog.w("VoiceInteractionServiceManager", "there is some exception in VoiceInteractionSession , ignore unbind this service ");
                localRuntimeException.printStackTrace();
              }
            }
            catch (RemoteException localRemoteException2)
            {
              for (;;)
              {
                Slog.w("VoiceInteractionServiceManager", "Failed removing window token", localRemoteException2);
              }
            }
          }
        }
      }
      catch (RemoteException localRemoteException3)
      {
        for (;;) {}
      }
    }
  }
  
  public boolean deliverNewSessionLocked(IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor)
  {
    this.mSession = paramIVoiceInteractionSession;
    this.mInteractor = paramIVoiceInteractor;
    if (this.mShown) {}
    try
    {
      paramIVoiceInteractionSession.show(this.mShowArgs, this.mShowFlags, this.mShowCallback);
      this.mShowArgs = null;
      this.mShowFlags = 0;
      deliverSessionDataLocked();
      return true;
    }
    catch (RemoteException paramIVoiceInteractionSession)
    {
      for (;;) {}
    }
  }
  
  void deliverSessionDataLocked()
  {
    if (this.mSession == null) {
      return;
    }
    if ((!this.mHaveAssistData) || (this.mAssistData.isEmpty())) {}
    try
    {
      this.mSession.handleAssist(null, null, null, 0, 0);
      for (;;)
      {
        if (this.mPendingAssistDataCount <= 0) {
          this.mHaveAssistData = false;
        }
        if (this.mHaveScreenshot) {}
        try
        {
          this.mSession.handleScreenshot(this.mScreenshot);
          this.mScreenshot = null;
          this.mHaveScreenshot = false;
          return;
          while (!this.mAssistData.isEmpty())
          {
            if (this.mPendingAssistDataCount <= 0) {
              Slog.e("VoiceInteractionServiceManager", "mPendingAssistDataCount is " + this.mPendingAssistDataCount);
            }
            this.mPendingAssistDataCount -= 1;
            AssistDataForActivity localAssistDataForActivity = (AssistDataForActivity)this.mAssistData.remove(0);
            if (localAssistDataForActivity.data == null) {
              try
              {
                this.mSession.handleAssist(null, null, null, localAssistDataForActivity.activityIndex, localAssistDataForActivity.activityCount);
              }
              catch (RemoteException localRemoteException1) {}
            } else {
              deliverSessionDataLocked(localRemoteException1);
            }
          }
        }
        catch (RemoteException localRemoteException2)
        {
          for (;;) {}
        }
      }
    }
    catch (RemoteException localRemoteException3)
    {
      for (;;) {}
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mToken=");
    paramPrintWriter.println(this.mToken);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mShown=");
    paramPrintWriter.println(this.mShown);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mShowArgs=");
    paramPrintWriter.println(this.mShowArgs);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mShowFlags=0x");
    paramPrintWriter.println(Integer.toHexString(this.mShowFlags));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mBound=");
    paramPrintWriter.println(this.mBound);
    if (this.mBound)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mService=");
      paramPrintWriter.println(this.mService);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSession=");
      paramPrintWriter.println(this.mSession);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mInteractor=");
      paramPrintWriter.println(this.mInteractor);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mHaveAssistData=");
    paramPrintWriter.println(this.mHaveAssistData);
    if (this.mHaveAssistData)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAssistData=");
      paramPrintWriter.println(this.mAssistData);
    }
  }
  
  public int getUserDisabledShowContextLocked()
  {
    int i = 0;
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "assist_structure_enabled", 1, this.mUser) == 0) {
      i = 1;
    }
    int j = i;
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "assist_screenshot_enabled", 1, this.mUser) == 0) {
      j = i | 0x2;
    }
    return j;
  }
  
  void grantClipDataItemPermission(ClipData.Item paramItem, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    if (paramItem.getUri() != null) {
      grantUriPermission(paramItem.getUri(), paramInt1, paramInt2, paramInt3, paramString);
    }
    paramItem = paramItem.getIntent();
    if ((paramItem != null) && (paramItem.getData() != null)) {
      grantUriPermission(paramItem.getData(), paramInt1, paramInt2, paramInt3, paramString);
    }
  }
  
  void grantClipDataPermissions(ClipData paramClipData, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    int j = paramClipData.getItemCount();
    int i = 0;
    while (i < j)
    {
      grantClipDataItemPermission(paramClipData.getItemAt(i), paramInt1, paramInt2, paramInt3, paramString);
      i += 1;
    }
  }
  
  void grantUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    if (!"content".equals(paramUri.getScheme())) {
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mAm.checkGrantUriPermission(paramInt2, null, ContentProvider.getUriWithoutUserId(paramUri), paramInt1, ContentProvider.getUserIdFromUri(paramUri, UserHandle.getUserId(paramInt2)));
      paramInt1 = ContentProvider.getUserIdFromUri(paramUri, this.mUser);
      paramUri = ContentProvider.getUriWithoutUserId(paramUri);
      this.mAm.grantUriPermissionFromOwner(this.mPermissionOwner, paramInt2, paramString, paramUri, 1, paramInt1, this.mUser);
      return;
    }
    catch (SecurityException paramUri)
    {
      Slog.w("VoiceInteractionServiceManager", "Can't propagate permission", paramUri);
      return;
    }
    catch (RemoteException paramUri) {}finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public boolean hideLocked()
  {
    if (this.mBound) {
      if (this.mShown)
      {
        this.mShown = false;
        this.mShowArgs = null;
        this.mShowFlags = 0;
        this.mHaveAssistData = false;
        this.mAssistData.clear();
        if (this.mSession == null) {}
      }
    }
    try
    {
      this.mSession.hide();
      try
      {
        this.mAm.revokeUriPermissionFromOwner(this.mPermissionOwner, null, 3, this.mUser);
        if (this.mSession != null) {}
        try
        {
          this.mAm.finishVoiceTask(this.mSession);
          this.mCallback.onSessionHidden(this);
          if (this.mFullyBound)
          {
            this.mContext.unbindService(this.mFullConnection);
            this.mFullyBound = false;
          }
          return true;
          return false;
        }
        catch (RemoteException localRemoteException1)
        {
          for (;;) {}
        }
      }
      catch (RemoteException localRemoteException2)
      {
        for (;;) {}
      }
    }
    catch (RemoteException localRemoteException3)
    {
      for (;;) {}
    }
  }
  
  public void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
  {
    synchronized (this.mLock)
    {
      this.mService = IVoiceInteractionSessionService.Stub.asInterface(paramIBinder);
      boolean bool = this.mCanceled;
      if (!bool) {}
      try
      {
        this.mService.newSession(this.mToken, this.mShowArgs, this.mShowFlags);
        return;
      }
      catch (RemoteException paramIBinder)
      {
        for (;;)
        {
          Slog.w("VoiceInteractionServiceManager", "Failed adding window token", paramIBinder);
        }
      }
    }
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    this.mCallback.sessionConnectionGone(this);
    this.mService = null;
  }
  
  public boolean showLocked(Bundle paramBundle, int paramInt1, int paramInt2, IVoiceInteractionSessionShowCallback paramIVoiceInteractionSessionShowCallback, IBinder paramIBinder, List<IBinder> paramList)
  {
    boolean bool1;
    if (this.mBound)
    {
      if (!this.mFullyBound) {
        this.mFullyBound = this.mContext.bindServiceAsUser(this.mBindIntent, this.mFullConnection, 201326593, new UserHandle(this.mUser));
      }
      this.mShown = true;
      bool1 = true;
    }
    try
    {
      bool2 = this.mAm.isAssistDataAllowedOnCurrentActivity();
      bool1 = bool2;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        boolean bool2;
        int i;
        int k;
        int j;
        int m;
        int n;
        continue;
        if (m == 0) {
          bool2 = true;
        }
      }
    }
    paramInt2 |= getUserDisabledShowContextLocked();
    if (bool1) {
      if ((paramInt2 & 0x1) == 0)
      {
        i = 1;
        if ((!bool1) || (i == 0)) {
          break label386;
        }
        if ((paramInt2 & 0x2) != 0) {
          break label381;
        }
        paramInt2 = 1;
        label109:
        this.mShowArgs = paramBundle;
        this.mShowFlags = paramInt1;
        this.mHaveAssistData = false;
        this.mPendingAssistDataCount = 0;
        k = 0;
        j = 0;
        if ((paramInt1 & 0x1) == 0) {
          break label644;
        }
        if ((this.mAppOps.noteOpNoThrow(49, this.mCallingUid, this.mSessionComponentName.getPackageName()) != 0) || (i == 0)) {
          break label622;
        }
        this.mAssistData.clear();
        if (paramIBinder == null) {
          break label391;
        }
        k = 1;
        label184:
        m = 0;
        i = j;
        label191:
        j = i;
        n = paramInt2;
        if (m >= k) {
          break label467;
        }
        j = i;
        n = paramInt2;
        if (m >= 1) {
          break label467;
        }
        if (k != 1) {
          break label403;
        }
        paramBundle = paramIBinder;
        label227:
        j = i;
      }
    }
    try
    {
      MetricsLogger.count(this.mContext, "assist_with_context", 1);
      j = i;
      localBundle = new Bundle();
      j = i;
      localBundle.putInt("index", m);
      j = i;
      localBundle.putInt("count", k);
      j = i;
      localIActivityManager = this.mAm;
      j = i;
      localIResultReceiver = this.mAssistReceiver;
      if (m != 0) {
        break label419;
      }
      bool1 = true;
    }
    catch (RemoteException paramBundle)
    {
      Bundle localBundle;
      IActivityManager localIActivityManager;
      IResultReceiver localIResultReceiver;
      label312:
      for (;;) {}
    }
    j = i;
    if (localIActivityManager.requestAssistContextExtras(1, localIResultReceiver, localBundle, paramBundle, bool1, bool2))
    {
      j = 1;
      i = 1;
      this.mPendingAssistDataCount += 1;
      j = i;
    }
    label381:
    label386:
    label391:
    label403:
    label419:
    do
    {
      m += 1;
      i = j;
      break label191;
      i = 0;
      break;
      i = 0;
      break;
      paramInt2 = 0;
      break label109;
      paramInt2 = 0;
      break label109;
      k = paramList.size();
      break label184;
      paramBundle = (IBinder)paramList.get(m);
      break label227;
      bool1 = false;
      break label735;
      bool2 = false;
      break label312;
      j = i;
    } while (m != 0);
    j = i;
    this.mHaveAssistData = true;
    j = i;
    this.mAssistData.clear();
    n = 0;
    j = i;
    for (;;)
    {
      label467:
      this.mHaveScreenshot = false;
      if (((paramInt1 & 0x2) == 0) || ((this.mAppOps.noteOpNoThrow(50, this.mCallingUid, this.mSessionComponentName.getPackageName()) == 0) && (n != 0))) {}
      try
      {
        MetricsLogger.count(this.mContext, "assist_with_screen", 1);
        j = 1;
        paramInt1 = 1;
        this.mIWindowManager.requestAssistScreenshot(this.mScreenshotReceiver);
        j = paramInt1;
      }
      catch (RemoteException paramBundle)
      {
        label539:
        label622:
        label644:
        for (;;) {}
      }
      if ((j != 0) && (AssistUtils.shouldDisclose(this.mContext, this.mSessionComponentName))) {
        this.mHandler.post(this.mShowAssistDisclosureRunnable);
      }
      if (this.mSession != null) {}
      try
      {
        this.mSession.show(this.mShowArgs, this.mShowFlags, paramIVoiceInteractionSessionShowCallback);
        this.mShowArgs = null;
        this.mShowFlags = 0;
        deliverSessionDataLocked();
        for (;;)
        {
          this.mCallback.onSessionShown(this);
          return true;
          this.mHaveAssistData = true;
          this.mAssistData.clear();
          j = k;
          n = paramInt2;
          break;
          this.mAssistData.clear();
          j = k;
          n = paramInt2;
          break;
          this.mHaveScreenshot = true;
          this.mScreenshot = null;
          break label539;
          this.mScreenshot = null;
          break label539;
          if (paramIVoiceInteractionSessionShowCallback != null) {
            this.mPendingShowCallbacks.add(paramIVoiceInteractionSessionShowCallback);
          }
        }
        if (paramIVoiceInteractionSessionShowCallback != null) {}
        try
        {
          paramIVoiceInteractionSessionShowCallback.onFailed();
          return false;
        }
        catch (RemoteException paramBundle)
        {
          for (;;) {}
        }
      }
      catch (RemoteException paramBundle)
      {
        for (;;) {}
      }
    }
  }
  
  static class AssistDataForActivity
  {
    int activityCount;
    int activityIndex;
    Bundle data;
    
    public AssistDataForActivity(Bundle paramBundle)
    {
      this.data = paramBundle;
      paramBundle = paramBundle.getBundle("receiverExtras");
      if (paramBundle != null)
      {
        this.activityIndex = paramBundle.getInt("index");
        this.activityCount = paramBundle.getInt("count");
      }
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onSessionHidden(VoiceInteractionSessionConnection paramVoiceInteractionSessionConnection);
    
    public abstract void onSessionShown(VoiceInteractionSessionConnection paramVoiceInteractionSessionConnection);
    
    public abstract void sessionConnectionGone(VoiceInteractionSessionConnection paramVoiceInteractionSessionConnection);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/voiceinteraction/VoiceInteractionSessionConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */