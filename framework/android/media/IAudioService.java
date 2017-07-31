package android.media;

import android.bluetooth.BluetoothDevice;
import android.media.audiopolicy.AudioPolicyConfig;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.media.audiopolicy.IAudioPolicyCallback.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IAudioService
  extends IInterface
{
  public abstract int abandonAudioFocus(IAudioFocusDispatcher paramIAudioFocusDispatcher, String paramString, AudioAttributes paramAudioAttributes)
    throws RemoteException;
  
  public abstract void addMediaPlayerAndUpdateRemoteController(String paramString)
    throws RemoteException;
  
  public abstract void adjustStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString)
    throws RemoteException;
  
  public abstract void adjustSuggestedStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void avrcpSupportsAbsoluteVolume(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void disableSafeMediaVolume(String paramString)
    throws RemoteException;
  
  public abstract void forceRemoteSubmixFullVolume(boolean paramBoolean, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void forceVolumeControlStream(int paramInt, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract List<AudioRecordingConfiguration> getActiveRecordingConfigurations()
    throws RemoteException;
  
  public abstract int getCurrentAudioFocus()
    throws RemoteException;
  
  public abstract int getLastAudibleStreamVolume(int paramInt)
    throws RemoteException;
  
  public abstract int getMode()
    throws RemoteException;
  
  public abstract int getRingerModeExternal()
    throws RemoteException;
  
  public abstract int getRingerModeInternal()
    throws RemoteException;
  
  public abstract IRingtonePlayer getRingtonePlayer()
    throws RemoteException;
  
  public abstract int getStreamMaxVolume(int paramInt)
    throws RemoteException;
  
  public abstract int getStreamMinVolume(int paramInt)
    throws RemoteException;
  
  public abstract int getStreamVolume(int paramInt)
    throws RemoteException;
  
  public abstract int getUiSoundsStreamType()
    throws RemoteException;
  
  public abstract int getVibrateSetting(int paramInt)
    throws RemoteException;
  
  public abstract boolean isASBluetoothA2dpOn()
    throws RemoteException;
  
  public abstract boolean isBluetoothA2dpOn()
    throws RemoteException;
  
  public abstract boolean isBluetoothScoOn()
    throws RemoteException;
  
  public abstract boolean isCameraSoundForced()
    throws RemoteException;
  
  public abstract boolean isHasSpeakerAuthority(String paramString)
    throws RemoteException;
  
  public abstract boolean isHdmiSystemAudioSupported()
    throws RemoteException;
  
  public abstract boolean isMasterMute()
    throws RemoteException;
  
  public abstract boolean isSpeakerphoneOn()
    throws RemoteException;
  
  public abstract boolean isStreamAffectedByMute(int paramInt)
    throws RemoteException;
  
  public abstract boolean isStreamAffectedByRingerMode(int paramInt)
    throws RemoteException;
  
  public abstract boolean isStreamMute(int paramInt)
    throws RemoteException;
  
  public abstract boolean isValidRingerMode(int paramInt)
    throws RemoteException;
  
  public abstract boolean loadSoundEffects()
    throws RemoteException;
  
  public abstract void notifyVolumeControllerVisible(IVolumeController paramIVolumeController, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void playSoundEffect(int paramInt)
    throws RemoteException;
  
  public abstract void playSoundEffectVolume(int paramInt, float paramFloat)
    throws RemoteException;
  
  public abstract String registerAudioPolicy(AudioPolicyConfig paramAudioPolicyConfig, IAudioPolicyCallback paramIAudioPolicyCallback, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void registerRecordingCallback(IRecordingConfigDispatcher paramIRecordingConfigDispatcher)
    throws RemoteException;
  
  public abstract void reloadAudioSettings()
    throws RemoteException;
  
  public abstract void removeMediaPlayerAndUpdateRemoteController(String paramString)
    throws RemoteException;
  
  public abstract int requestAudioFocus(AudioAttributes paramAudioAttributes, int paramInt1, IBinder paramIBinder, IAudioFocusDispatcher paramIAudioFocusDispatcher, String paramString1, String paramString2, int paramInt2, IAudioPolicyCallback paramIAudioPolicyCallback)
    throws RemoteException;
  
  public abstract int setBluetoothA2dpDeviceConnectionState(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setBluetoothA2dpOn(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setBluetoothCtsScoOn(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setBluetoothScoOn(boolean paramBoolean)
    throws RemoteException;
  
  public abstract int setFocusPropertiesForPolicy(int paramInt, IAudioPolicyCallback paramIAudioPolicyCallback)
    throws RemoteException;
  
  public abstract int setHdmiSystemAudioSupported(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMasterMute(boolean paramBoolean, int paramInt1, String paramString, int paramInt2)
    throws RemoteException;
  
  public abstract void setMicrophoneMute(boolean paramBoolean, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setMode(int paramInt, IBinder paramIBinder, String paramString)
    throws RemoteException;
  
  public abstract void setOnePlusFixedRingerMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setOnePlusRingVolumeRange(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setRingerModeExternal(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void setRingerModeInternal(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void setRingtonePlayer(IRingtonePlayer paramIRingtonePlayer)
    throws RemoteException;
  
  public abstract void setSpeakerphoneOn(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString)
    throws RemoteException;
  
  public abstract void setVibrateSetting(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setVolumeController(IVolumeController paramIVolumeController)
    throws RemoteException;
  
  public abstract void setVolumePolicy(VolumePolicy paramVolumePolicy)
    throws RemoteException;
  
  public abstract void setWiredDeviceConnectionState(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract boolean shouldVibrate(int paramInt)
    throws RemoteException;
  
  public abstract void startBluetoothSco(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void startBluetoothScoVirtualCall(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver paramIAudioRoutesObserver)
    throws RemoteException;
  
  public abstract void stopBluetoothSco(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void threeKeySetStreamVolume(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public abstract void unloadSoundEffects()
    throws RemoteException;
  
  public abstract void unregisterAudioFocusClient(String paramString)
    throws RemoteException;
  
  public abstract void unregisterAudioPolicyAsync(IAudioPolicyCallback paramIAudioPolicyCallback)
    throws RemoteException;
  
  public abstract void unregisterRecordingCallback(IRecordingConfigDispatcher paramIRecordingConfigDispatcher)
    throws RemoteException;
  
  public abstract void updateRemoteControllerOnExistingMediaPlayers()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAudioService
  {
    private static final String DESCRIPTOR = "android.media.IAudioService";
    static final int TRANSACTION_abandonAudioFocus = 40;
    static final int TRANSACTION_addMediaPlayerAndUpdateRemoteController = 69;
    static final int TRANSACTION_adjustStreamVolume = 2;
    static final int TRANSACTION_adjustSuggestedStreamVolume = 1;
    static final int TRANSACTION_avrcpSupportsAbsoluteVolume = 29;
    static final int TRANSACTION_disableSafeMediaVolume = 58;
    static final int TRANSACTION_forceRemoteSubmixFullVolume = 6;
    static final int TRANSACTION_forceVolumeControlStream = 46;
    static final int TRANSACTION_getActiveRecordingConfigurations = 67;
    static final int TRANSACTION_getCurrentAudioFocus = 42;
    static final int TRANSACTION_getLastAudibleStreamVolume = 12;
    static final int TRANSACTION_getMode = 23;
    static final int TRANSACTION_getRingerModeExternal = 16;
    static final int TRANSACTION_getRingerModeInternal = 17;
    static final int TRANSACTION_getRingtonePlayer = 48;
    static final int TRANSACTION_getStreamMaxVolume = 11;
    static final int TRANSACTION_getStreamMinVolume = 10;
    static final int TRANSACTION_getStreamVolume = 9;
    static final int TRANSACTION_getUiSoundsStreamType = 49;
    static final int TRANSACTION_getVibrateSetting = 20;
    static final int TRANSACTION_isASBluetoothA2dpOn = 35;
    static final int TRANSACTION_isBluetoothA2dpOn = 38;
    static final int TRANSACTION_isBluetoothScoOn = 34;
    static final int TRANSACTION_isCameraSoundForced = 53;
    static final int TRANSACTION_isHasSpeakerAuthority = 36;
    static final int TRANSACTION_isHdmiSystemAudioSupported = 60;
    static final int TRANSACTION_isMasterMute = 7;
    static final int TRANSACTION_isSpeakerphoneOn = 31;
    static final int TRANSACTION_isStreamAffectedByMute = 57;
    static final int TRANSACTION_isStreamAffectedByRingerMode = 56;
    static final int TRANSACTION_isStreamMute = 5;
    static final int TRANSACTION_isValidRingerMode = 18;
    static final int TRANSACTION_loadSoundEffects = 26;
    static final int TRANSACTION_notifyVolumeControllerVisible = 55;
    static final int TRANSACTION_playSoundEffect = 24;
    static final int TRANSACTION_playSoundEffectVolume = 25;
    static final int TRANSACTION_registerAudioPolicy = 61;
    static final int TRANSACTION_registerRecordingCallback = 65;
    static final int TRANSACTION_reloadAudioSettings = 28;
    static final int TRANSACTION_removeMediaPlayerAndUpdateRemoteController = 70;
    static final int TRANSACTION_requestAudioFocus = 39;
    static final int TRANSACTION_setBluetoothA2dpDeviceConnectionState = 51;
    static final int TRANSACTION_setBluetoothA2dpOn = 37;
    static final int TRANSACTION_setBluetoothCtsScoOn = 33;
    static final int TRANSACTION_setBluetoothScoOn = 32;
    static final int TRANSACTION_setFocusPropertiesForPolicy = 63;
    static final int TRANSACTION_setHdmiSystemAudioSupported = 59;
    static final int TRANSACTION_setMasterMute = 8;
    static final int TRANSACTION_setMicrophoneMute = 13;
    static final int TRANSACTION_setMode = 22;
    static final int TRANSACTION_setOnePlusFixedRingerMode = 72;
    static final int TRANSACTION_setOnePlusRingVolumeRange = 71;
    static final int TRANSACTION_setRingerModeExternal = 14;
    static final int TRANSACTION_setRingerModeInternal = 15;
    static final int TRANSACTION_setRingtonePlayer = 47;
    static final int TRANSACTION_setSpeakerphoneOn = 30;
    static final int TRANSACTION_setStreamVolume = 3;
    static final int TRANSACTION_setVibrateSetting = 19;
    static final int TRANSACTION_setVolumeController = 54;
    static final int TRANSACTION_setVolumePolicy = 64;
    static final int TRANSACTION_setWiredDeviceConnectionState = 50;
    static final int TRANSACTION_shouldVibrate = 21;
    static final int TRANSACTION_startBluetoothSco = 43;
    static final int TRANSACTION_startBluetoothScoVirtualCall = 44;
    static final int TRANSACTION_startWatchingRoutes = 52;
    static final int TRANSACTION_stopBluetoothSco = 45;
    static final int TRANSACTION_threeKeySetStreamVolume = 4;
    static final int TRANSACTION_unloadSoundEffects = 27;
    static final int TRANSACTION_unregisterAudioFocusClient = 41;
    static final int TRANSACTION_unregisterAudioPolicyAsync = 62;
    static final int TRANSACTION_unregisterRecordingCallback = 66;
    static final int TRANSACTION_updateRemoteControllerOnExistingMediaPlayers = 68;
    
    public Stub()
    {
      attachInterface(this, "android.media.IAudioService");
    }
    
    public static IAudioService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.IAudioService");
      if ((localIInterface != null) && ((localIInterface instanceof IAudioService))) {
        return (IAudioService)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      Object localObject1;
      Object localObject2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.media.IAudioService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        adjustSuggestedStreamVolume(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        adjustStreamVolume(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        setStreamVolume(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        threeKeySetStreamVolume(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isStreamMute(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          forceRemoteSubmixFullVolume(bool, paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isMasterMute();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setMasterMute(bool, paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getStreamVolume(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getStreamMinVolume(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getStreamMaxVolume(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getLastAudibleStreamVolume(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setMicrophoneMute(bool, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        setRingerModeExternal(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        setRingerModeInternal(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getRingerModeExternal();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getRingerModeInternal();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isValidRingerMode(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        setVibrateSetting(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getVibrateSetting(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = shouldVibrate(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 22: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        setMode(paramParcel1.readInt(), paramParcel1.readStrongBinder(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getMode();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 24: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        playSoundEffect(paramParcel1.readInt());
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        playSoundEffectVolume(paramParcel1.readInt(), paramParcel1.readFloat());
        return true;
      case 26: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = loadSoundEffects();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 27: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        unloadSoundEffects();
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        reloadAudioSettings();
        return true;
      case 29: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          avrcpSupportsAbsoluteVolume(paramParcel2, bool);
          return true;
        }
      case 30: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setSpeakerphoneOn(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 31: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isSpeakerphoneOn();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 32: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setBluetoothScoOn(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 33: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setBluetoothCtsScoOn(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 34: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isBluetoothScoOn();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 35: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isASBluetoothA2dpOn();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 36: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isHasSpeakerAuthority(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 37: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setBluetoothA2dpOn(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 38: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isBluetoothA2dpOn();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 39: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (AudioAttributes)AudioAttributes.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          paramInt1 = requestAudioFocus((AudioAttributes)localObject1, paramParcel1.readInt(), paramParcel1.readStrongBinder(), IAudioFocusDispatcher.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt(), IAudioPolicyCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 40: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        localObject1 = IAudioFocusDispatcher.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (AudioAttributes)AudioAttributes.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = abandonAudioFocus((IAudioFocusDispatcher)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 41: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        unregisterAudioFocusClient(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 42: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getCurrentAudioFocus();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 43: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        startBluetoothSco(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 44: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        startBluetoothScoVirtualCall(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 45: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        stopBluetoothSco(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 46: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        forceVolumeControlStream(paramParcel1.readInt(), paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 47: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        setRingtonePlayer(IRingtonePlayer.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 48: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramParcel1 = getRingtonePlayer();
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 49: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = getUiSoundsStreamType();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 50: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        setWiredDeviceConnectionState(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 51: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          paramInt1 = setBluetoothA2dpDeviceConnectionState((BluetoothDevice)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 52: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramParcel1 = startWatchingRoutes(IAudioRoutesObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 53: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isCameraSoundForced();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 54: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        setVolumeController(IVolumeController.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 55: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        localObject1 = IVolumeController.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          notifyVolumeControllerVisible((IVolumeController)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 56: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isStreamAffectedByRingerMode(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 57: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isStreamAffectedByMute(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 58: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        disableSafeMediaVolume(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 59: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramInt1 = setHdmiSystemAudioSupported(bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 60: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        bool = isHdmiSystemAudioSupported();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 61: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (AudioPolicyConfig)AudioPolicyConfig.CREATOR.createFromParcel(paramParcel1);
          localObject2 = IAudioPolicyCallback.Stub.asInterface(paramParcel1.readStrongBinder());
          if (paramParcel1.readInt() == 0) {
            break label2619;
          }
        }
        for (bool = true;; bool = false)
        {
          paramParcel1 = registerAudioPolicy((AudioPolicyConfig)localObject1, (IAudioPolicyCallback)localObject2, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
          localObject1 = null;
          break;
        }
      case 62: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        unregisterAudioPolicyAsync(IAudioPolicyCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 63: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramInt1 = setFocusPropertiesForPolicy(paramParcel1.readInt(), IAudioPolicyCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 64: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (VolumePolicy)VolumePolicy.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setVolumePolicy(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 65: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        registerRecordingCallback(IRecordingConfigDispatcher.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 66: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        unregisterRecordingCallback(IRecordingConfigDispatcher.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 67: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        paramParcel1 = getActiveRecordingConfigurations();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 68: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        updateRemoteControllerOnExistingMediaPlayers();
        paramParcel2.writeNoException();
        return true;
      case 69: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        addMediaPlayerAndUpdateRemoteController(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 70: 
        paramParcel1.enforceInterface("android.media.IAudioService");
        removeMediaPlayerAndUpdateRemoteController(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 71: 
        label2619:
        paramParcel1.enforceInterface("android.media.IAudioService");
        setOnePlusRingVolumeRange(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.media.IAudioService");
      if (paramParcel1.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        setOnePlusFixedRingerMode(bool);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IAudioService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public int abandonAudioFocus(IAudioFocusDispatcher paramIAudioFocusDispatcher, String paramString, AudioAttributes paramAudioAttributes)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 42 1 0
        //   30: astore 5
        //   32: aload 6
        //   34: aload 5
        //   36: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload 6
        //   41: aload_2
        //   42: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   45: aload_3
        //   46: ifnull +58 -> 104
        //   49: aload 6
        //   51: iconst_1
        //   52: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   55: aload_3
        //   56: aload 6
        //   58: iconst_0
        //   59: invokevirtual 58	android/media/AudioAttributes:writeToParcel	(Landroid/os/Parcel;I)V
        //   62: aload_0
        //   63: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   66: bipush 40
        //   68: aload 6
        //   70: aload 7
        //   72: iconst_0
        //   73: invokeinterface 64 5 0
        //   78: pop
        //   79: aload 7
        //   81: invokevirtual 67	android/os/Parcel:readException	()V
        //   84: aload 7
        //   86: invokevirtual 71	android/os/Parcel:readInt	()I
        //   89: istore 4
        //   91: aload 7
        //   93: invokevirtual 74	android/os/Parcel:recycle	()V
        //   96: aload 6
        //   98: invokevirtual 74	android/os/Parcel:recycle	()V
        //   101: iload 4
        //   103: ireturn
        //   104: aload 6
        //   106: iconst_0
        //   107: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   110: goto -48 -> 62
        //   113: astore_1
        //   114: aload 7
        //   116: invokevirtual 74	android/os/Parcel:recycle	()V
        //   119: aload 6
        //   121: invokevirtual 74	android/os/Parcel:recycle	()V
        //   124: aload_1
        //   125: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	126	0	this	Proxy
        //   0	126	1	paramIAudioFocusDispatcher	IAudioFocusDispatcher
        //   0	126	2	paramString	String
        //   0	126	3	paramAudioAttributes	AudioAttributes
        //   89	13	4	i	int
        //   1	34	5	localIBinder	IBinder
        //   6	114	6	localParcel1	Parcel
        //   11	104	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	113	finally
        //   24	32	113	finally
        //   32	45	113	finally
        //   49	62	113	finally
        //   62	91	113	finally
        //   104	110	113	finally
      }
      
      public void addMediaPlayerAndUpdateRemoteController(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(69, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void adjustStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeString(paramString);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void adjustSuggestedStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IAudioService");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          localParcel.writeString(paramString1);
          localParcel.writeString(paramString2);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public void avrcpSupportsAbsoluteVolume(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload 4
        //   16: aload_1
        //   17: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: iload_2
        //   21: ifeq +31 -> 52
        //   24: aload 4
        //   26: iload_3
        //   27: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 29
        //   36: aload 4
        //   38: aconst_null
        //   39: iconst_1
        //   40: invokeinterface 64 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 74	android/os/Parcel:recycle	()V
        //   51: return
        //   52: iconst_0
        //   53: istore_3
        //   54: goto -30 -> 24
        //   57: astore_1
        //   58: aload 4
        //   60: invokevirtual 74	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramString	String
        //   0	65	2	paramBoolean	boolean
        //   1	53	3	i	int
        //   5	54	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	20	57	finally
        //   24	46	57	finally
      }
      
      public void disableSafeMediaVolume(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(58, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void forceRemoteSubmixFullVolume(boolean paramBoolean, IBinder paramIBinder)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void forceVolumeControlStream(int paramInt, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(46, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<AudioRecordingConfiguration> getActiveRecordingConfigurations()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(67, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(AudioRecordingConfiguration.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getCurrentAudioFocus()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(42, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.media.IAudioService";
      }
      
      public int getLastAudibleStreamVolume(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getMode()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getRingerModeExternal()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getRingerModeInternal()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IRingtonePlayer getRingtonePlayer()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(48, localParcel1, localParcel2, 0);
          localParcel2.readException();
          IRingtonePlayer localIRingtonePlayer = IRingtonePlayer.Stub.asInterface(localParcel2.readStrongBinder());
          return localIRingtonePlayer;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getStreamMaxVolume(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getStreamMinVolume(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getStreamVolume(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getUiSoundsStreamType()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(49, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getVibrateSetting(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isASBluetoothA2dpOn()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 35
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 64 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 67	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 74	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 74	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 74	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 74	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isBluetoothA2dpOn()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 38
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 64 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 67	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 74	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 74	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 74	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 74	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isBluetoothScoOn()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 34
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 64 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 67	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 74	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 74	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 74	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 74	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isCameraSoundForced()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 53
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 64 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 67	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 74	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 74	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 74	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 74	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isHasSpeakerAuthority(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 36
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 64 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 67	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 71	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 74	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 74	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 74	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 74	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean isHdmiSystemAudioSupported()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 60
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 64 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 67	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 74	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 74	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 74	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 74	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isMasterMute()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 7
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 64 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 67	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 74	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 74	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 74	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 74	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isSpeakerphoneOn()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 31
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 64 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 67	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 74	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 74	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 74	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 74	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isStreamAffectedByMute(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 57
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 64 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 67	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 74	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 74	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 74	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 74	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public boolean isStreamAffectedByRingerMode(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 56
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 64 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 67	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 74	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 74	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 74	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 74	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public boolean isStreamMute(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_5
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 64 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 67	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 71	android/os/Parcel:readInt	()I
        //   45: istore_1
        //   46: iload_1
        //   47: ifeq +16 -> 63
        //   50: iconst_1
        //   51: istore_2
        //   52: aload 4
        //   54: invokevirtual 74	android/os/Parcel:recycle	()V
        //   57: aload_3
        //   58: invokevirtual 74	android/os/Parcel:recycle	()V
        //   61: iload_2
        //   62: ireturn
        //   63: iconst_0
        //   64: istore_2
        //   65: goto -13 -> 52
        //   68: astore 5
        //   70: aload 4
        //   72: invokevirtual 74	android/os/Parcel:recycle	()V
        //   75: aload_3
        //   76: invokevirtual 74	android/os/Parcel:recycle	()V
        //   79: aload 5
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramInt	int
        //   51	14	2	bool	boolean
        //   3	73	3	localParcel1	Parcel
        //   7	64	4	localParcel2	Parcel
        //   68	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	46	68	finally
      }
      
      /* Error */
      public boolean isValidRingerMode(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 18
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 64 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 67	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 74	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 74	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 74	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 74	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public boolean loadSoundEffects()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 26
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 64 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 67	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 74	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 74	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 74	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 74	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public void notifyVolumeControllerVisible(IVolumeController paramIVolumeController, boolean paramBoolean)
        throws RemoteException
      {
        IBinder localIBinder = null;
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramIVolumeController != null) {
            localIBinder = paramIVolumeController.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(55, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void playSoundEffect(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IAudioService");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(24, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void playSoundEffectVolume(int paramInt, float paramFloat)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IAudioService");
          localParcel.writeInt(paramInt);
          localParcel.writeFloat(paramFloat);
          this.mRemote.transact(25, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String registerAudioPolicy(AudioPolicyConfig paramAudioPolicyConfig, IAudioPolicyCallback paramIAudioPolicyCallback, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.IAudioService");
            if (paramAudioPolicyConfig != null)
            {
              localParcel1.writeInt(1);
              paramAudioPolicyConfig.writeToParcel(localParcel1, 0);
              paramAudioPolicyConfig = (AudioPolicyConfig)localObject;
              if (paramIAudioPolicyCallback != null) {
                paramAudioPolicyConfig = paramIAudioPolicyCallback.asBinder();
              }
              localParcel1.writeStrongBinder(paramAudioPolicyConfig);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(61, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramAudioPolicyConfig = localParcel2.readString();
                return paramAudioPolicyConfig;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void registerRecordingCallback(IRecordingConfigDispatcher paramIRecordingConfigDispatcher)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramIRecordingConfigDispatcher != null) {
            localIBinder = paramIRecordingConfigDispatcher.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(65, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void reloadAudioSettings()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(28, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void removeMediaPlayerAndUpdateRemoteController(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(70, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int requestAudioFocus(AudioAttributes paramAudioAttributes, int paramInt1, IBinder paramIBinder, IAudioFocusDispatcher paramIAudioFocusDispatcher, String paramString1, String paramString2, int paramInt2, IAudioPolicyCallback paramIAudioPolicyCallback)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.IAudioService");
            if (paramAudioAttributes != null)
            {
              localParcel1.writeInt(1);
              paramAudioAttributes.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeStrongBinder(paramIBinder);
              if (paramIAudioFocusDispatcher != null)
              {
                paramAudioAttributes = paramIAudioFocusDispatcher.asBinder();
                localParcel1.writeStrongBinder(paramAudioAttributes);
                localParcel1.writeString(paramString1);
                localParcel1.writeString(paramString2);
                localParcel1.writeInt(paramInt2);
                paramAudioAttributes = (AudioAttributes)localObject;
                if (paramIAudioPolicyCallback != null) {
                  paramAudioAttributes = paramIAudioPolicyCallback.asBinder();
                }
                localParcel1.writeStrongBinder(paramAudioAttributes);
                this.mRemote.transact(39, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt1 = localParcel2.readInt();
                return paramInt1;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramAudioAttributes = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +68 -> 86
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 178	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: iload_2
        //   37: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 51
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 64 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 67	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 71	android/os/Parcel:readInt	()I
        //   73: istore_2
        //   74: aload 5
        //   76: invokevirtual 74	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 74	android/os/Parcel:recycle	()V
        //   84: iload_2
        //   85: ireturn
        //   86: aload 4
        //   88: iconst_0
        //   89: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   92: goto -58 -> 34
        //   95: astore_1
        //   96: aload 5
        //   98: invokevirtual 74	android/os/Parcel:recycle	()V
        //   101: aload 4
        //   103: invokevirtual 74	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramBluetoothDevice	BluetoothDevice
        //   0	108	2	paramInt1	int
        //   0	108	3	paramInt2	int
        //   3	99	4	localParcel1	Parcel
        //   8	89	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	95	finally
        //   21	34	95	finally
        //   34	74	95	finally
        //   86	92	95	finally
      }
      
      public void setBluetoothA2dpOn(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(37, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setBluetoothCtsScoOn(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(33, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setBluetoothScoOn(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(32, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int setFocusPropertiesForPolicy(int paramInt, IAudioPolicyCallback paramIAudioPolicyCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          if (paramIAudioPolicyCallback != null) {
            localIBinder = paramIAudioPolicyCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(63, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int setHdmiSystemAudioSupported(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(59, localParcel1, localParcel2, 0);
          localParcel2.readException();
          i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setMasterMute(boolean paramBoolean, int paramInt1, String paramString, int paramInt2)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setMicrophoneMute(boolean paramBoolean, String paramString, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setMode(int paramInt, IBinder paramIBinder, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeString(paramString);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setOnePlusFixedRingerMode(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(72, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setOnePlusRingVolumeRange(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(71, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setRingerModeExternal(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setRingerModeInternal(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setRingtonePlayer(IRingtonePlayer paramIRingtonePlayer)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramIRingtonePlayer != null) {
            localIBinder = paramIRingtonePlayer.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(47, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setSpeakerphoneOn(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(30, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setStreamVolume(int paramInt1, int paramInt2, int paramInt3, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeString(paramString);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setVibrateSetting(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setVolumeController(IVolumeController paramIVolumeController)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          if (paramIVolumeController != null) {
            localIBinder = paramIVolumeController.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(54, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void setVolumePolicy(VolumePolicy paramVolumePolicy)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 213	android/media/VolumePolicy:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 64
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 64 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 67	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 74	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 74	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 74	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 74	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramVolumePolicy	VolumePolicy
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void setWiredDeviceConnectionState(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.mRemote.transact(50, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean shouldVibrate(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 52	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 21
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 64 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 67	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 74	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 74	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 74	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 74	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      public void startBluetoothSco(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(43, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void startBluetoothScoVirtualCall(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(44, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver paramIAudioRoutesObserver)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +10 -> 28
        //   21: aload_1
        //   22: invokeinterface 224 1 0
        //   27: astore_2
        //   28: aload_3
        //   29: aload_2
        //   30: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   33: aload_0
        //   34: getfield 19	android/media/IAudioService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   37: bipush 52
        //   39: aload_3
        //   40: aload 4
        //   42: iconst_0
        //   43: invokeinterface 64 5 0
        //   48: pop
        //   49: aload 4
        //   51: invokevirtual 67	android/os/Parcel:readException	()V
        //   54: aload 4
        //   56: invokevirtual 71	android/os/Parcel:readInt	()I
        //   59: ifeq +28 -> 87
        //   62: getstatic 227	android/media/AudioRoutesInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   65: aload 4
        //   67: invokeinterface 233 2 0
        //   72: checkcast 226	android/media/AudioRoutesInfo
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 74	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 74	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: areturn
        //   87: aconst_null
        //   88: astore_1
        //   89: goto -13 -> 76
        //   92: astore_1
        //   93: aload 4
        //   95: invokevirtual 74	android/os/Parcel:recycle	()V
        //   98: aload_3
        //   99: invokevirtual 74	android/os/Parcel:recycle	()V
        //   102: aload_1
        //   103: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	104	0	this	Proxy
        //   0	104	1	paramIAudioRoutesObserver	IAudioRoutesObserver
        //   1	29	2	localIBinder	IBinder
        //   5	94	3	localParcel1	Parcel
        //   9	85	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   11	17	92	finally
        //   21	28	92	finally
        //   28	76	92	finally
      }
      
      public void stopBluetoothSco(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(45, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void threeKeySetStreamVolume(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unloadSoundEffects()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(27, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void unregisterAudioFocusClient(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(41, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregisterAudioPolicyAsync(IAudioPolicyCallback paramIAudioPolicyCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IAudioService");
          if (paramIAudioPolicyCallback != null) {
            localIBinder = paramIAudioPolicyCallback.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(62, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void unregisterRecordingCallback(IRecordingConfigDispatcher paramIRecordingConfigDispatcher)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.IAudioService");
          if (paramIRecordingConfigDispatcher != null) {
            localIBinder = paramIRecordingConfigDispatcher.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(66, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void updateRemoteControllerOnExistingMediaPlayers()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IAudioService");
          this.mRemote.transact(68, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/IAudioService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */