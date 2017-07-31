package com.android.server.audio;

import android.app.AppOpsManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusInfo;
import android.media.IAudioFocusDispatcher;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.Stack;

public class MediaFocusControl
{
  private static final String TAG = "MediaFocusControl";
  private static final Object mAudioFocusLock = new Object();
  private final AppOpsManager mAppOps;
  private final Context mContext;
  private ArrayList<IAudioPolicyCallback> mFocusFollowers = new ArrayList();
  private final Stack<FocusRequester> mFocusStack = new Stack();
  private boolean mNotifyFocusOwnerOnDuck = true;
  
  protected MediaFocusControl(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAppOps = ((AppOpsManager)this.mContext.getSystemService("appops"));
  }
  
  private boolean canReassignAudioFocus()
  {
    return (this.mFocusStack.isEmpty()) || (!isLockedFocusOwner((FocusRequester)this.mFocusStack.peek()));
  }
  
  private void dumpFocusStack(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("\nAudio Focus stack entries (last is top of stack):");
    synchronized (mAudioFocusLock)
    {
      Iterator localIterator = this.mFocusStack.iterator();
      if (localIterator.hasNext()) {
        ((FocusRequester)localIterator.next()).dump(paramPrintWriter);
      }
    }
    paramPrintWriter.println("\n Notify on duck: " + this.mNotifyFocusOwnerOnDuck + "\n");
  }
  
  private boolean isLockedFocusOwner(FocusRequester paramFocusRequester)
  {
    if (!paramFocusRequester.hasSameClient("AudioFocus_For_Phone_Ring_And_Calls")) {
      return paramFocusRequester.isLockedFocusOwner();
    }
    return true;
  }
  
  private void notifyTopOfAudioFocusStack()
  {
    if ((!this.mFocusStack.empty()) && (canReassignAudioFocus())) {
      ((FocusRequester)this.mFocusStack.peek()).handleFocusGain(1);
    }
  }
  
  private void propagateFocusLossFromGain_syncAf(int paramInt)
  {
    Iterator localIterator = this.mFocusStack.iterator();
    while (localIterator.hasNext()) {
      ((FocusRequester)localIterator.next()).handleExternalFocusGain(paramInt);
    }
  }
  
  private int pushBelowLockedFocusOwners(FocusRequester paramFocusRequester)
  {
    int j = this.mFocusStack.size();
    int i = this.mFocusStack.size() - 1;
    while (i >= 0)
    {
      if (isLockedFocusOwner((FocusRequester)this.mFocusStack.elementAt(i))) {
        j = i;
      }
      i -= 1;
    }
    if (j == this.mFocusStack.size())
    {
      Log.e("MediaFocusControl", "No exclusive focus owner found in propagateFocusLossFromGain_syncAf()", new Exception());
      propagateFocusLossFromGain_syncAf(paramFocusRequester.getGainRequest());
      this.mFocusStack.push(paramFocusRequester);
      return 1;
    }
    this.mFocusStack.insertElementAt(paramFocusRequester, j);
    return 2;
  }
  
  private void removeFocusStackEntry(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!this.mFocusStack.empty()) && (((FocusRequester)this.mFocusStack.peek()).hasSameClient(paramString)))
    {
      paramString = (FocusRequester)this.mFocusStack.pop();
      paramString.release();
      if (paramBoolean2)
      {
        paramString = paramString.toAudioFocusInfo();
        paramString.clearLossReceived();
        notifyExtPolicyFocusLoss_syncAf(paramString, false);
      }
      if (paramBoolean1) {
        notifyTopOfAudioFocusStack();
      }
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.mFocusStack.iterator();
      while (localIterator.hasNext())
      {
        FocusRequester localFocusRequester = (FocusRequester)localIterator.next();
        if (localFocusRequester.hasSameClient(paramString))
        {
          Log.i("MediaFocusControl", "AudioFocus  removeFocusStackEntry(): removing entry for " + paramString);
          localIterator.remove();
          localFocusRequester.release();
        }
      }
    }
  }
  
  private void removeFocusStackEntryOnDeath(IBinder paramIBinder)
  {
    if (!this.mFocusStack.isEmpty()) {}
    for (boolean bool = ((FocusRequester)this.mFocusStack.peek()).hasSameBinder(paramIBinder);; bool = false)
    {
      Iterator localIterator = this.mFocusStack.iterator();
      while (localIterator.hasNext())
      {
        FocusRequester localFocusRequester = (FocusRequester)localIterator.next();
        if (localFocusRequester.hasSameBinder(paramIBinder))
        {
          Log.i("MediaFocusControl", "AudioFocus  removeFocusStackEntryOnDeath(): removing entry for " + paramIBinder);
          localIterator.remove();
          localFocusRequester.release();
        }
      }
    }
    if (bool) {
      notifyTopOfAudioFocusStack();
    }
  }
  
  protected int abandonAudioFocus(IAudioFocusDispatcher arg1, String paramString, AudioAttributes paramAudioAttributes)
  {
    Log.i("MediaFocusControl", " AudioFocus  abandonAudioFocus() from uid/pid " + Binder.getCallingUid() + "/" + Binder.getCallingPid() + " clientId=" + paramString);
    try
    {
      synchronized (mAudioFocusLock)
      {
        removeFocusStackEntry(paramString, true, true);
        return 1;
      }
      return 1;
    }
    catch (ConcurrentModificationException ???)
    {
      Log.e("MediaFocusControl", "FATAL EXCEPTION AudioFocus  abandonAudioFocus() caused " + ???);
      ???.printStackTrace();
    }
  }
  
  void addFocusFollower(IAudioPolicyCallback paramIAudioPolicyCallback)
  {
    if (paramIAudioPolicyCallback == null) {
      return;
    }
    Object localObject = mAudioFocusLock;
    int j = 0;
    try
    {
      Iterator localIterator = this.mFocusFollowers.iterator();
      boolean bool;
      do
      {
        i = j;
        if (!localIterator.hasNext()) {
          break;
        }
        bool = ((IAudioPolicyCallback)localIterator.next()).asBinder().equals(paramIAudioPolicyCallback.asBinder());
      } while (!bool);
      int i = 1;
      if (i != 0) {
        return;
      }
      this.mFocusFollowers.add(paramIAudioPolicyCallback);
      notifyExtPolicyCurrentFocusAsync(paramIAudioPolicyCallback);
      return;
    }
    finally {}
  }
  
  protected void discardAudioFocusOwner()
  {
    synchronized (mAudioFocusLock)
    {
      if (!this.mFocusStack.empty())
      {
        FocusRequester localFocusRequester = (FocusRequester)this.mFocusStack.pop();
        localFocusRequester.handleFocusLoss(-1);
        localFocusRequester.release();
      }
      return;
    }
  }
  
  protected void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("\nMediaFocusControl dump time: " + DateFormat.getTimeInstance().format(new Date()));
    dumpFocusStack(paramPrintWriter);
  }
  
  protected int getCurrentAudioFocus()
  {
    synchronized (mAudioFocusLock)
    {
      boolean bool = this.mFocusStack.empty();
      if (bool) {
        return 0;
      }
      int i = ((FocusRequester)this.mFocusStack.peek()).getGainRequest();
      return i;
    }
  }
  
  boolean mustNotifyFocusOwnerOnDuck()
  {
    return this.mNotifyFocusOwnerOnDuck;
  }
  
  void notifyExtPolicyCurrentFocusAsync(final IAudioPolicyCallback paramIAudioPolicyCallback)
  {
    new Thread()
    {
      public void run()
      {
        synchronized ()
        {
          boolean bool = MediaFocusControl.-get1(MediaFocusControl.this).isEmpty();
          if (bool) {
            return;
          }
          try
          {
            paramIAudioPolicyCallback.notifyAudioFocusGrant(((FocusRequester)MediaFocusControl.-get1(MediaFocusControl.this).peek()).toAudioFocusInfo(), 1);
            return;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Log.e("MediaFocusControl", "Can't call notifyAudioFocusGrant() on IAudioPolicyCallback " + paramIAudioPolicyCallback.asBinder(), localRemoteException);
            }
          }
        }
      }
    }.start();
  }
  
  void notifyExtPolicyFocusGrant_syncAf(AudioFocusInfo paramAudioFocusInfo, int paramInt)
  {
    Iterator localIterator = this.mFocusFollowers.iterator();
    while (localIterator.hasNext())
    {
      IAudioPolicyCallback localIAudioPolicyCallback = (IAudioPolicyCallback)localIterator.next();
      try
      {
        localIAudioPolicyCallback.notifyAudioFocusGrant(paramAudioFocusInfo, paramInt);
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaFocusControl", "Can't call notifyAudioFocusGrant() on IAudioPolicyCallback " + localIAudioPolicyCallback.asBinder(), localRemoteException);
      }
    }
  }
  
  void notifyExtPolicyFocusLoss_syncAf(AudioFocusInfo paramAudioFocusInfo, boolean paramBoolean)
  {
    Iterator localIterator = this.mFocusFollowers.iterator();
    while (localIterator.hasNext())
    {
      IAudioPolicyCallback localIAudioPolicyCallback = (IAudioPolicyCallback)localIterator.next();
      try
      {
        localIAudioPolicyCallback.notifyAudioFocusLoss(paramAudioFocusInfo, paramBoolean);
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("MediaFocusControl", "Can't call notifyAudioFocusLoss() on IAudioPolicyCallback " + localIAudioPolicyCallback.asBinder(), localRemoteException);
      }
    }
  }
  
  void removeFocusFollower(IAudioPolicyCallback paramIAudioPolicyCallback)
  {
    if (paramIAudioPolicyCallback == null) {
      return;
    }
    synchronized (mAudioFocusLock)
    {
      Iterator localIterator = this.mFocusFollowers.iterator();
      while (localIterator.hasNext())
      {
        IAudioPolicyCallback localIAudioPolicyCallback = (IAudioPolicyCallback)localIterator.next();
        if (localIAudioPolicyCallback.asBinder().equals(paramIAudioPolicyCallback.asBinder())) {
          this.mFocusFollowers.remove(localIAudioPolicyCallback);
        }
      }
      return;
    }
  }
  
  protected int requestAudioFocus(AudioAttributes paramAudioAttributes, int paramInt1, IBinder paramIBinder, IAudioFocusDispatcher paramIAudioFocusDispatcher, String paramString1, String paramString2, int paramInt2)
  {
    Log.i("MediaFocusControl", " AudioFocus  requestAudioFocus() from [" + paramString2 + "], uid/pid " + Binder.getCallingUid() + "/" + Binder.getCallingPid() + ", clientId=" + paramString1 + ", req=" + paramInt1 + ", flags=0x" + Integer.toHexString(paramInt2));
    if (!paramIBinder.pingBinder())
    {
      Log.e("MediaFocusControl", " AudioFocus DOA client for requestAudioFocus(), aborting.");
      return 0;
    }
    if (this.mAppOps.noteOp(32, Binder.getCallingUid(), paramString2) != 0) {
      return 0;
    }
    Object localObject = mAudioFocusLock;
    int i = 0;
    try
    {
      boolean bool = canReassignAudioFocus();
      if (!bool)
      {
        if ((paramInt2 & 0x1) == 0) {
          return 0;
        }
        i = 1;
      }
      AudioFocusDeathHandler localAudioFocusDeathHandler = new AudioFocusDeathHandler(paramIBinder);
      FocusRequester localFocusRequester;
      try
      {
        paramIBinder.linkToDeath(localAudioFocusDeathHandler, 0);
        if ((this.mFocusStack.empty()) || (!((FocusRequester)this.mFocusStack.peek()).hasSameClient(paramString1))) {
          break label321;
        }
        localFocusRequester = (FocusRequester)this.mFocusStack.peek();
        if ((localFocusRequester.getGainRequest() == paramInt1) && (localFocusRequester.getGrantFlags() == paramInt2))
        {
          paramIBinder.unlinkToDeath(localAudioFocusDeathHandler, 0);
          notifyExtPolicyFocusGrant_syncAf(localFocusRequester.toAudioFocusInfo(), 1);
          return 1;
        }
      }
      catch (RemoteException paramAudioAttributes)
      {
        Log.w("MediaFocusControl", "AudioFocus  requestAudioFocus() could not link to " + paramIBinder + " binder death");
        return 0;
      }
      if (i == 0)
      {
        this.mFocusStack.pop();
        localFocusRequester.release();
      }
      label321:
      removeFocusStackEntry(paramString1, false, false);
      paramAudioAttributes = new FocusRequester(paramAudioAttributes, paramInt1, paramInt2, paramIAudioFocusDispatcher, paramIBinder, paramString1, localAudioFocusDeathHandler, paramString2, Binder.getCallingUid(), this);
      if (i != 0)
      {
        paramInt1 = pushBelowLockedFocusOwners(paramAudioAttributes);
        if (paramInt1 != 0) {
          notifyExtPolicyFocusGrant_syncAf(paramAudioAttributes.toAudioFocusInfo(), paramInt1);
        }
        return paramInt1;
      }
      if (!this.mFocusStack.empty()) {
        propagateFocusLossFromGain_syncAf(paramInt1);
      }
      this.mFocusStack.push(paramAudioAttributes);
      notifyExtPolicyFocusGrant_syncAf(paramAudioAttributes.toAudioFocusInfo(), 1);
      return 1;
    }
    finally {}
  }
  
  protected void setDuckingInExtPolicyAvailable(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mNotifyFocusOwnerOnDuck = paramBoolean;
      return;
    }
  }
  
  protected void unregisterAudioFocusClient(String paramString)
  {
    synchronized (mAudioFocusLock)
    {
      removeFocusStackEntry(paramString, false, true);
      return;
    }
  }
  
  protected class AudioFocusDeathHandler
    implements IBinder.DeathRecipient
  {
    private IBinder mCb;
    
    AudioFocusDeathHandler(IBinder paramIBinder)
    {
      this.mCb = paramIBinder;
    }
    
    public void binderDied()
    {
      synchronized ()
      {
        MediaFocusControl.-wrap0(MediaFocusControl.this, this.mCb);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/audio/MediaFocusControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */