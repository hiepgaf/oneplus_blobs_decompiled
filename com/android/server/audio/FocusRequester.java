package com.android.server.audio;

import android.media.AudioAttributes;
import android.media.AudioFocusInfo;
import android.media.IAudioFocusDispatcher;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

public class FocusRequester
{
  private static final boolean DEBUG = false;
  private static final String TAG = "MediaFocusControl";
  private final AudioAttributes mAttributes;
  private final int mCallingUid;
  private final String mClientId;
  private MediaFocusControl.AudioFocusDeathHandler mDeathHandler;
  private final MediaFocusControl mFocusController;
  private IAudioFocusDispatcher mFocusDispatcher;
  private final int mFocusGainRequest;
  private int mFocusLossReceived;
  private final int mGrantFlags;
  private final String mPackageName;
  private final IBinder mSourceRef;
  
  FocusRequester(AudioAttributes paramAudioAttributes, int paramInt1, int paramInt2, IAudioFocusDispatcher paramIAudioFocusDispatcher, IBinder paramIBinder, String paramString1, MediaFocusControl.AudioFocusDeathHandler paramAudioFocusDeathHandler, String paramString2, int paramInt3, MediaFocusControl paramMediaFocusControl)
  {
    this.mAttributes = paramAudioAttributes;
    this.mFocusDispatcher = paramIAudioFocusDispatcher;
    this.mSourceRef = paramIBinder;
    this.mClientId = paramString1;
    this.mDeathHandler = paramAudioFocusDeathHandler;
    this.mPackageName = paramString2;
    this.mCallingUid = paramInt3;
    this.mFocusGainRequest = paramInt1;
    this.mGrantFlags = paramInt2;
    this.mFocusLossReceived = 0;
    this.mFocusController = paramMediaFocusControl;
  }
  
  private static String flagsToString(int paramInt)
  {
    Object localObject2 = new String();
    Object localObject1 = localObject2;
    if ((paramInt & 0x1) != 0) {
      localObject1 = (String)localObject2 + "DELAY_OK";
    }
    localObject2 = localObject1;
    if ((paramInt & 0x4) != 0)
    {
      localObject2 = localObject1;
      if (!((String)localObject1).isEmpty()) {
        localObject2 = (String)localObject1 + "|";
      }
      localObject2 = (String)localObject2 + "LOCK";
    }
    localObject1 = localObject2;
    if ((paramInt & 0x2) != 0)
    {
      localObject1 = localObject2;
      if (!((String)localObject2).isEmpty()) {
        localObject1 = (String)localObject2 + "|";
      }
      localObject1 = (String)localObject1 + "PAUSES_ON_DUCKABLE_LOSS";
    }
    return (String)localObject1;
  }
  
  private static String focusChangeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "[invalid focus change" + paramInt + "]";
    case 0: 
      return "none";
    case 1: 
      return "GAIN";
    case 2: 
      return "GAIN_TRANSIENT";
    case 3: 
      return "GAIN_TRANSIENT_MAY_DUCK";
    case 4: 
      return "GAIN_TRANSIENT_EXCLUSIVE";
    case -1: 
      return "LOSS";
    case -2: 
      return "LOSS_TRANSIENT";
    }
    return "LOSS_TRANSIENT_CAN_DUCK";
  }
  
  private String focusGainToString()
  {
    return focusChangeToString(this.mFocusGainRequest);
  }
  
  private int focusLossForGainRequest(int paramInt)
  {
    switch (paramInt)
    {
    }
    for (;;)
    {
      Log.e("MediaFocusControl", "focusLossForGainRequest() for invalid focus request " + paramInt);
      return 0;
      switch (this.mFocusLossReceived)
      {
      default: 
        switch (this.mFocusLossReceived)
        {
        default: 
          switch (this.mFocusLossReceived)
          {
          }
          break;
        }
        break;
      }
    }
    return -3;
    return -1;
    return -2;
    return -1;
    return -2;
    return -1;
  }
  
  private String focusLossToString()
  {
    return focusChangeToString(this.mFocusLossReceived);
  }
  
  void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("  source:" + this.mSourceRef + " -- pack: " + this.mPackageName + " -- client: " + this.mClientId + " -- gain: " + focusGainToString() + " -- flags: " + flagsToString(this.mGrantFlags) + " -- loss: " + focusLossToString() + " -- uid: " + this.mCallingUid + " -- attr: " + this.mAttributes);
  }
  
  protected void finalize()
    throws Throwable
  {
    release();
    super.finalize();
  }
  
  AudioAttributes getAudioAttributes()
  {
    return this.mAttributes;
  }
  
  String getClientId()
  {
    return this.mClientId;
  }
  
  int getGainRequest()
  {
    return this.mFocusGainRequest;
  }
  
  int getGrantFlags()
  {
    return this.mGrantFlags;
  }
  
  void handleExternalFocusGain(int paramInt)
  {
    handleFocusLoss(focusLossForGainRequest(paramInt));
  }
  
  void handleFocusGain(int paramInt)
  {
    try
    {
      this.mFocusLossReceived = 0;
      this.mFocusController.notifyExtPolicyFocusGrant_syncAf(toAudioFocusInfo(), 1);
      IAudioFocusDispatcher localIAudioFocusDispatcher = this.mFocusDispatcher;
      if (localIAudioFocusDispatcher != null) {
        localIAudioFocusDispatcher.dispatchAudioFocusChange(paramInt, this.mClientId);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MediaFocusControl", "Failure to signal gain of audio focus due to: ", localRemoteException);
    }
  }
  
  void handleFocusLoss(int paramInt)
  {
    try
    {
      if (paramInt != this.mFocusLossReceived)
      {
        this.mFocusLossReceived = paramInt;
        if ((!this.mFocusController.mustNotifyFocusOwnerOnDuck()) && (this.mFocusLossReceived == -3) && ((this.mGrantFlags & 0x2) == 0))
        {
          this.mFocusController.notifyExtPolicyFocusLoss_syncAf(toAudioFocusInfo(), false);
          return;
        }
        IAudioFocusDispatcher localIAudioFocusDispatcher = this.mFocusDispatcher;
        if (localIAudioFocusDispatcher != null)
        {
          this.mFocusController.notifyExtPolicyFocusLoss_syncAf(toAudioFocusInfo(), true);
          localIAudioFocusDispatcher.dispatchAudioFocusChange(this.mFocusLossReceived, this.mClientId);
        }
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MediaFocusControl", "Failure to signal loss of audio focus due to:", localRemoteException);
    }
  }
  
  boolean hasSameBinder(IBinder paramIBinder)
  {
    if (this.mSourceRef != null) {
      return this.mSourceRef.equals(paramIBinder);
    }
    return false;
  }
  
  boolean hasSameClient(String paramString)
  {
    boolean bool = false;
    try
    {
      int i = this.mClientId.compareTo(paramString);
      if (i == 0) {
        bool = true;
      }
      return bool;
    }
    catch (NullPointerException paramString) {}
    return false;
  }
  
  boolean hasSamePackage(String paramString)
  {
    boolean bool = false;
    try
    {
      int i = this.mPackageName.compareTo(paramString);
      if (i == 0) {
        bool = true;
      }
      return bool;
    }
    catch (NullPointerException paramString) {}
    return false;
  }
  
  boolean hasSameUid(int paramInt)
  {
    return this.mCallingUid == paramInt;
  }
  
  boolean isLockedFocusOwner()
  {
    boolean bool = false;
    if ((this.mGrantFlags & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  void release()
  {
    try
    {
      if ((this.mSourceRef != null) && (this.mDeathHandler != null))
      {
        this.mSourceRef.unlinkToDeath(this.mDeathHandler, 0);
        this.mDeathHandler = null;
        this.mFocusDispatcher = null;
      }
      return;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      Log.e("MediaFocusControl", "FocusRequester.release() hit ", localNoSuchElementException);
    }
  }
  
  AudioFocusInfo toAudioFocusInfo()
  {
    return new AudioFocusInfo(this.mAttributes, this.mClientId, this.mPackageName, this.mFocusGainRequest, this.mFocusLossReceived, this.mGrantFlags);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/audio/FocusRequester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */