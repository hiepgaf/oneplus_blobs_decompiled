package android.media.tv;

import android.graphics.Rect;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.Surface;
import java.util.ArrayList;
import java.util.List;

public abstract interface ITvInputManager
  extends IInterface
{
  public abstract ITvInputHardware acquireTvInputHardware(int paramInt1, ITvInputHardwareCallback paramITvInputHardwareCallback, TvInputInfo paramTvInputInfo, int paramInt2)
    throws RemoteException;
  
  public abstract void addBlockedRating(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean captureFrame(String paramString, Surface paramSurface, TvStreamConfig paramTvStreamConfig, int paramInt)
    throws RemoteException;
  
  public abstract void createOverlayView(IBinder paramIBinder1, IBinder paramIBinder2, Rect paramRect, int paramInt)
    throws RemoteException;
  
  public abstract void createSession(ITvInputClient paramITvInputClient, String paramString, boolean paramBoolean, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void dispatchSurfaceChanged(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public abstract List<TvStreamConfig> getAvailableTvStreamConfigList(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract List<String> getBlockedRatings(int paramInt)
    throws RemoteException;
  
  public abstract List<DvbDeviceInfo> getDvbDeviceList()
    throws RemoteException;
  
  public abstract List<TvInputHardwareInfo> getHardwareList()
    throws RemoteException;
  
  public abstract List<TvContentRatingSystemInfo> getTvContentRatingSystemList(int paramInt)
    throws RemoteException;
  
  public abstract TvInputInfo getTvInputInfo(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract List<TvInputInfo> getTvInputList(int paramInt)
    throws RemoteException;
  
  public abstract int getTvInputState(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isParentalControlsEnabled(int paramInt)
    throws RemoteException;
  
  public abstract boolean isRatingBlocked(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isSingleSessionActive(int paramInt)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openDvbDevice(DvbDeviceInfo paramDvbDeviceInfo, int paramInt)
    throws RemoteException;
  
  public abstract void registerCallback(ITvInputManagerCallback paramITvInputManagerCallback, int paramInt)
    throws RemoteException;
  
  public abstract void relayoutOverlayView(IBinder paramIBinder, Rect paramRect, int paramInt)
    throws RemoteException;
  
  public abstract void releaseSession(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void releaseTvInputHardware(int paramInt1, ITvInputHardware paramITvInputHardware, int paramInt2)
    throws RemoteException;
  
  public abstract void removeBlockedRating(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void removeOverlayView(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void selectTrack(IBinder paramIBinder, int paramInt1, String paramString, int paramInt2)
    throws RemoteException;
  
  public abstract void sendAppPrivateCommand(IBinder paramIBinder, String paramString, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public abstract void setCaptionEnabled(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setMainSession(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void setParentalControlsEnabled(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setSurface(IBinder paramIBinder, Surface paramSurface, int paramInt)
    throws RemoteException;
  
  public abstract void setVolume(IBinder paramIBinder, float paramFloat, int paramInt)
    throws RemoteException;
  
  public abstract void startRecording(IBinder paramIBinder, Uri paramUri, int paramInt)
    throws RemoteException;
  
  public abstract void stopRecording(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void timeShiftEnablePositionTracking(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void timeShiftPause(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void timeShiftPlay(IBinder paramIBinder, Uri paramUri, int paramInt)
    throws RemoteException;
  
  public abstract void timeShiftResume(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void timeShiftSeekTo(IBinder paramIBinder, long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void timeShiftSetPlaybackParams(IBinder paramIBinder, PlaybackParams paramPlaybackParams, int paramInt)
    throws RemoteException;
  
  public abstract void tune(IBinder paramIBinder, Uri paramUri, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public abstract void unblockContent(IBinder paramIBinder, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void unregisterCallback(ITvInputManagerCallback paramITvInputManagerCallback, int paramInt)
    throws RemoteException;
  
  public abstract void updateTvInputInfo(TvInputInfo paramTvInputInfo, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITvInputManager
  {
    private static final String DESCRIPTOR = "android.media.tv.ITvInputManager";
    static final int TRANSACTION_acquireTvInputHardware = 37;
    static final int TRANSACTION_addBlockedRating = 12;
    static final int TRANSACTION_captureFrame = 40;
    static final int TRANSACTION_createOverlayView = 24;
    static final int TRANSACTION_createSession = 14;
    static final int TRANSACTION_dispatchSurfaceChanged = 18;
    static final int TRANSACTION_getAvailableTvStreamConfigList = 39;
    static final int TRANSACTION_getBlockedRatings = 11;
    static final int TRANSACTION_getDvbDeviceList = 42;
    static final int TRANSACTION_getHardwareList = 36;
    static final int TRANSACTION_getTvContentRatingSystemList = 5;
    static final int TRANSACTION_getTvInputInfo = 2;
    static final int TRANSACTION_getTvInputList = 1;
    static final int TRANSACTION_getTvInputState = 4;
    static final int TRANSACTION_isParentalControlsEnabled = 8;
    static final int TRANSACTION_isRatingBlocked = 10;
    static final int TRANSACTION_isSingleSessionActive = 41;
    static final int TRANSACTION_openDvbDevice = 43;
    static final int TRANSACTION_registerCallback = 6;
    static final int TRANSACTION_relayoutOverlayView = 25;
    static final int TRANSACTION_releaseSession = 15;
    static final int TRANSACTION_releaseTvInputHardware = 38;
    static final int TRANSACTION_removeBlockedRating = 13;
    static final int TRANSACTION_removeOverlayView = 26;
    static final int TRANSACTION_selectTrack = 22;
    static final int TRANSACTION_sendAppPrivateCommand = 23;
    static final int TRANSACTION_setCaptionEnabled = 21;
    static final int TRANSACTION_setMainSession = 16;
    static final int TRANSACTION_setParentalControlsEnabled = 9;
    static final int TRANSACTION_setSurface = 17;
    static final int TRANSACTION_setVolume = 19;
    static final int TRANSACTION_startRecording = 34;
    static final int TRANSACTION_stopRecording = 35;
    static final int TRANSACTION_timeShiftEnablePositionTracking = 33;
    static final int TRANSACTION_timeShiftPause = 29;
    static final int TRANSACTION_timeShiftPlay = 28;
    static final int TRANSACTION_timeShiftResume = 30;
    static final int TRANSACTION_timeShiftSeekTo = 31;
    static final int TRANSACTION_timeShiftSetPlaybackParams = 32;
    static final int TRANSACTION_tune = 20;
    static final int TRANSACTION_unblockContent = 27;
    static final int TRANSACTION_unregisterCallback = 7;
    static final int TRANSACTION_updateTvInputInfo = 3;
    
    public Stub()
    {
      attachInterface(this, "android.media.tv.ITvInputManager");
    }
    
    public static ITvInputManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.tv.ITvInputManager");
      if ((localIInterface != null) && ((localIInterface instanceof ITvInputManager))) {
        return (ITvInputManager)localIInterface;
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
      boolean bool;
      Object localObject2;
      Object localObject3;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.media.tv.ITvInputManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        paramParcel1 = getTvInputList(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        paramParcel1 = getTvInputInfo(paramParcel1.readString(), paramParcel1.readInt());
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
      case 3: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (TvInputInfo)TvInputInfo.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          updateTvInputInfo((TvInputInfo)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        paramInt1 = getTvInputState(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        paramParcel1 = getTvContentRatingSystemList(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        registerCallback(ITvInputManagerCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        unregisterCallback(ITvInputManagerCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        bool = isParentalControlsEnabled(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setParentalControlsEnabled(bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        bool = isRatingBlocked(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        paramParcel1 = getBlockedRatings(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeStringList(paramParcel1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        addBlockedRating(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        removeBlockedRating(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject1 = ITvInputClient.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          createSession((ITvInputClient)localObject1, (String)localObject2, bool, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        releaseSession(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        setMainSession(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Surface)Surface.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setSurface((IBinder)localObject2, (Surface)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 18: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        dispatchSurfaceChanged(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        setVolume(paramParcel1.readStrongBinder(), paramParcel1.readFloat(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject3 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1119;
          }
        }
        for (localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          tune((IBinder)localObject3, (Uri)localObject1, (Bundle)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 21: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject1 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setCaptionEnabled((IBinder)localObject1, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 22: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        selectTrack(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject2 = paramParcel1.readStrongBinder();
        localObject3 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          sendAppPrivateCommand((IBinder)localObject2, (String)localObject3, (Bundle)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 24: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject2 = paramParcel1.readStrongBinder();
        localObject3 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          createOverlayView((IBinder)localObject2, (IBinder)localObject3, (Rect)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 25: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          relayoutOverlayView((IBinder)localObject2, (Rect)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 26: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        removeOverlayView(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        unblockContent(paramParcel1.readStrongBinder(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          timeShiftPlay((IBinder)localObject2, (Uri)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 29: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        timeShiftPause(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 30: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        timeShiftResume(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 31: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        timeShiftSeekTo(paramParcel1.readStrongBinder(), paramParcel1.readLong(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (PlaybackParams)PlaybackParams.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          timeShiftSetPlaybackParams((IBinder)localObject2, (PlaybackParams)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 33: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject1 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          timeShiftEnablePositionTracking((IBinder)localObject1, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 34: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          startRecording((IBinder)localObject2, (Uri)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 35: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        stopRecording(paramParcel1.readStrongBinder(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 36: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        paramParcel1 = getHardwareList();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 37: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        paramInt1 = paramParcel1.readInt();
        localObject2 = ITvInputHardwareCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (TvInputInfo)TvInputInfo.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = acquireTvInputHardware(paramInt1, (ITvInputHardwareCallback)localObject2, (TvInputInfo)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1864;
          }
        }
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
          localObject1 = null;
          break;
        }
      case 38: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        releaseTvInputHardware(paramParcel1.readInt(), ITvInputHardware.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 39: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        paramParcel1 = getAvailableTvStreamConfigList(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 40: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        localObject3 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Surface)Surface.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2024;
          }
          localObject2 = (TvStreamConfig)TvStreamConfig.CREATOR.createFromParcel(paramParcel1);
          bool = captureFrame((String)localObject3, (Surface)localObject1, (TvStreamConfig)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label2030;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label1984;
        }
      case 41: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        bool = isSingleSessionActive(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 42: 
        label1119:
        label1864:
        label1984:
        label2024:
        label2030:
        paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
        paramParcel1 = getDvbDeviceList();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.media.tv.ITvInputManager");
      if (paramParcel1.readInt() != 0)
      {
        localObject1 = (DvbDeviceInfo)DvbDeviceInfo.CREATOR.createFromParcel(paramParcel1);
        paramParcel1 = openDvbDevice((DvbDeviceInfo)localObject1, paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 == null) {
          break label2161;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      for (;;)
      {
        return true;
        localObject1 = null;
        break;
        label2161:
        paramParcel2.writeInt(0);
      }
    }
    
    private static class Proxy
      implements ITvInputManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public ITvInputHardware acquireTvInputHardware(int paramInt1, ITvInputHardwareCallback paramITvInputHardwareCallback, TvInputInfo paramTvInputInfo, int paramInt2)
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
        //   20: aload 6
        //   22: iload_1
        //   23: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   26: aload_2
        //   27: ifnull +11 -> 38
        //   30: aload_2
        //   31: invokeinterface 46 1 0
        //   36: astore 5
        //   38: aload 6
        //   40: aload 5
        //   42: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   45: aload_3
        //   46: ifnull +66 -> 112
        //   49: aload 6
        //   51: iconst_1
        //   52: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   55: aload_3
        //   56: aload 6
        //   58: iconst_0
        //   59: invokevirtual 55	android/media/tv/TvInputInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   62: aload 6
        //   64: iload 4
        //   66: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   69: aload_0
        //   70: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   73: bipush 37
        //   75: aload 6
        //   77: aload 7
        //   79: iconst_0
        //   80: invokeinterface 61 5 0
        //   85: pop
        //   86: aload 7
        //   88: invokevirtual 64	android/os/Parcel:readException	()V
        //   91: aload 7
        //   93: invokevirtual 67	android/os/Parcel:readStrongBinder	()Landroid/os/IBinder;
        //   96: invokestatic 73	android/media/tv/ITvInputHardware$Stub:asInterface	(Landroid/os/IBinder;)Landroid/media/tv/ITvInputHardware;
        //   99: astore_2
        //   100: aload 7
        //   102: invokevirtual 76	android/os/Parcel:recycle	()V
        //   105: aload 6
        //   107: invokevirtual 76	android/os/Parcel:recycle	()V
        //   110: aload_2
        //   111: areturn
        //   112: aload 6
        //   114: iconst_0
        //   115: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   118: goto -56 -> 62
        //   121: astore_2
        //   122: aload 7
        //   124: invokevirtual 76	android/os/Parcel:recycle	()V
        //   127: aload 6
        //   129: invokevirtual 76	android/os/Parcel:recycle	()V
        //   132: aload_2
        //   133: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	134	0	this	Proxy
        //   0	134	1	paramInt1	int
        //   0	134	2	paramITvInputHardwareCallback	ITvInputHardwareCallback
        //   0	134	3	paramTvInputInfo	TvInputInfo
        //   0	134	4	paramInt2	int
        //   1	40	5	localIBinder	IBinder
        //   6	122	6	localParcel1	Parcel
        //   11	112	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	121	finally
        //   30	38	121	finally
        //   38	45	121	finally
        //   49	62	121	finally
        //   62	100	121	finally
        //   112	118	121	finally
      }
      
      public void addBlockedRating(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public boolean captureFrame(String paramString, Surface paramSurface, TvStreamConfig paramTvStreamConfig, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
            localParcel1.writeString(paramString);
            if (paramSurface != null)
            {
              localParcel1.writeInt(1);
              paramSurface.writeToParcel(localParcel1, 0);
              if (paramTvStreamConfig != null)
              {
                localParcel1.writeInt(1);
                paramTvStreamConfig.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(40, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                if (paramInt == 0) {
                  break label145;
                }
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label145:
          boolean bool = false;
        }
      }
      
      /* Error */
      public void createOverlayView(IBinder paramIBinder1, IBinder paramIBinder2, Rect paramRect, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   29: aload_3
        //   30: ifnull +56 -> 86
        //   33: aload 5
        //   35: iconst_1
        //   36: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   39: aload_3
        //   40: aload 5
        //   42: iconst_0
        //   43: invokevirtual 99	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload 5
        //   48: iload 4
        //   50: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   53: aload_0
        //   54: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 24
        //   59: aload 5
        //   61: aload 6
        //   63: iconst_0
        //   64: invokeinterface 61 5 0
        //   69: pop
        //   70: aload 6
        //   72: invokevirtual 64	android/os/Parcel:readException	()V
        //   75: aload 6
        //   77: invokevirtual 76	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: invokevirtual 76	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 5
        //   88: iconst_0
        //   89: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   92: goto -46 -> 46
        //   95: astore_1
        //   96: aload 6
        //   98: invokevirtual 76	android/os/Parcel:recycle	()V
        //   101: aload 5
        //   103: invokevirtual 76	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramIBinder1	IBinder
        //   0	108	2	paramIBinder2	IBinder
        //   0	108	3	paramRect	Rect
        //   0	108	4	paramInt	int
        //   3	99	5	localParcel1	Parcel
        //   8	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	29	95	finally
        //   33	46	95	finally
        //   46	75	95	finally
        //   86	92	95	finally
      }
      
      public void createSession(ITvInputClient paramITvInputClient, String paramString, boolean paramBoolean, int paramInt1, int paramInt2)
        throws RemoteException
      {
        IBinder localIBinder = null;
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          if (paramITvInputClient != null) {
            localIBinder = paramITvInputClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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
      
      public void dispatchSurfaceChanged(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<TvStreamConfig> getAvailableTvStreamConfigList(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(39, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createTypedArrayList(TvStreamConfig.CREATOR);
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<String> getBlockedRatings(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createStringArrayList();
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<DvbDeviceInfo> getDvbDeviceList()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          this.mRemote.transact(42, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(DvbDeviceInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<TvInputHardwareInfo> getHardwareList()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          this.mRemote.transact(36, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(TvInputHardwareInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.media.tv.ITvInputManager";
      }
      
      public List<TvContentRatingSystemInfo> getTvContentRatingSystemList(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(TvContentRatingSystemInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public TvInputInfo getTvInputInfo(String paramString, int paramInt)
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
        //   16: aload_1
        //   17: invokevirtual 82	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 61 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 64	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 94	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 146	android/media/tv/TvInputInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   56: aload 4
        //   58: invokeinterface 152 2 0
        //   63: checkcast 51	android/media/tv/TvInputInfo
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 76	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 76	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 76	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 76	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramString	String
        //   0	95	2	paramInt	int
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
      
      public List<TvInputInfo> getTvInputList(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(TvInputInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getTvInputState(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      public boolean isParentalControlsEnabled(int paramInt)
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
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 8
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 61 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 64	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 94	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 76	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 76	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 76	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 76	android/os/Parcel:recycle	()V
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
      public boolean isRatingBlocked(String paramString, int paramInt)
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
        //   20: invokevirtual 82	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 10
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 61 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 64	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 94	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 76	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 76	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 76	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 76	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean isSingleSessionActive(int paramInt)
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
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 41
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 61 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 64	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 94	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 76	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 76	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 76	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 76	android/os/Parcel:recycle	()V
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
      
      public ParcelFileDescriptor openDvbDevice(DvbDeviceInfo paramDvbDeviceInfo, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
            if (paramDvbDeviceInfo != null)
            {
              localParcel1.writeInt(1);
              paramDvbDeviceInfo.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(43, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramDvbDeviceInfo = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(localParcel2);
                return paramDvbDeviceInfo;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramDvbDeviceInfo = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void registerCallback(ITvInputManagerCallback paramITvInputManagerCallback, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          if (paramITvInputManagerCallback != null) {
            localIBinder = paramITvInputManagerCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
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
      
      /* Error */
      public void relayoutOverlayView(IBinder paramIBinder, Rect paramRect, int paramInt)
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
        //   20: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 99	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 25
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 61 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 64	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 76	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 76	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 76	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 76	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramIBinder	IBinder
        //   0	101	2	paramRect	Rect
        //   0	101	3	paramInt	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      public void releaseSession(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
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
      
      public void releaseTvInputHardware(int paramInt1, ITvInputHardware paramITvInputHardware, int paramInt2)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeInt(paramInt1);
          if (paramITvInputHardware != null) {
            localIBinder = paramITvInputHardware.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(38, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeBlockedRating(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
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
      
      public void removeOverlayView(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void selectTrack(IBinder paramIBinder, int paramInt1, String paramString, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
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
      
      /* Error */
      public void sendAppPrivateCommand(IBinder paramIBinder, String paramString, Bundle paramBundle, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 82	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_3
        //   30: ifnull +56 -> 86
        //   33: aload 5
        //   35: iconst_1
        //   36: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   39: aload_3
        //   40: aload 5
        //   42: iconst_0
        //   43: invokevirtual 190	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload 5
        //   48: iload 4
        //   50: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   53: aload_0
        //   54: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 23
        //   59: aload 5
        //   61: aload 6
        //   63: iconst_0
        //   64: invokeinterface 61 5 0
        //   69: pop
        //   70: aload 6
        //   72: invokevirtual 64	android/os/Parcel:readException	()V
        //   75: aload 6
        //   77: invokevirtual 76	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: invokevirtual 76	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 5
        //   88: iconst_0
        //   89: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   92: goto -46 -> 46
        //   95: astore_1
        //   96: aload 6
        //   98: invokevirtual 76	android/os/Parcel:recycle	()V
        //   101: aload 5
        //   103: invokevirtual 76	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramIBinder	IBinder
        //   0	108	2	paramString	String
        //   0	108	3	paramBundle	Bundle
        //   0	108	4	paramInt	int
        //   3	99	5	localParcel1	Parcel
        //   8	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	29	95	finally
        //   33	46	95	finally
        //   46	75	95	finally
        //   86	92	95	finally
      }
      
      public void setCaptionEnabled(IBinder paramIBinder, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setMainSession(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setParentalControlsEnabled(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
      public void setSurface(IBinder paramIBinder, Surface paramSurface, int paramInt)
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
        //   20: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 87	android/view/Surface:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 17
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 61 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 64	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 76	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 76	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 76	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 76	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramIBinder	IBinder
        //   0	101	2	paramSurface	Surface
        //   0	101	3	paramInt	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      public void setVolume(IBinder paramIBinder, float paramFloat, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeFloat(paramFloat);
          localParcel1.writeInt(paramInt);
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
      
      /* Error */
      public void startRecording(IBinder paramIBinder, Uri paramUri, int paramInt)
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
        //   20: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 208	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 34
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 61 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 64	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 76	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 76	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 76	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 76	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramIBinder	IBinder
        //   0	101	2	paramUri	Uri
        //   0	101	3	paramInt	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      public void stopRecording(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(35, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void timeShiftEnablePositionTracking(IBinder paramIBinder, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
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
      
      public void timeShiftPause(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
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
      public void timeShiftPlay(IBinder paramIBinder, Uri paramUri, int paramInt)
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
        //   20: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 208	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 28
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 61 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 64	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 76	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 76	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 76	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 76	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramIBinder	IBinder
        //   0	101	2	paramUri	Uri
        //   0	101	3	paramInt	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      public void timeShiftResume(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
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
      
      public void timeShiftSeekTo(IBinder paramIBinder, long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
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
      public void timeShiftSetPlaybackParams(IBinder paramIBinder, PlaybackParams paramPlaybackParams, int paramInt)
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
        //   20: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 224	android/media/PlaybackParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 32
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 61 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 64	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 76	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 76	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 76	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 76	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramIBinder	IBinder
        //   0	101	2	paramPlaybackParams	PlaybackParams
        //   0	101	3	paramInt	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      public void tune(IBinder paramIBinder, Uri paramUri, Bundle paramBundle, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
            localParcel1.writeStrongBinder(paramIBinder);
            if (paramUri != null)
            {
              localParcel1.writeInt(1);
              paramUri.writeToParcel(localParcel1, 0);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(20, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void unblockContent(IBinder paramIBinder, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregisterCallback(ITvInputManagerCallback paramITvInputManagerCallback, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.tv.ITvInputManager");
          if (paramITvInputManagerCallback != null) {
            localIBinder = paramITvInputManagerCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
      public void updateTvInputInfo(TvInputInfo paramTvInputInfo, int paramInt)
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
        //   15: aload_1
        //   16: ifnull +49 -> 65
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 55	android/media/tv/TvInputInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/media/tv/ITvInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_3
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 61 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 64	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 76	android/os/Parcel:recycle	()V
        //   60: aload_3
        //   61: invokevirtual 76	android/os/Parcel:recycle	()V
        //   64: return
        //   65: aload_3
        //   66: iconst_0
        //   67: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   70: goto -40 -> 30
        //   73: astore_1
        //   74: aload 4
        //   76: invokevirtual 76	android/os/Parcel:recycle	()V
        //   79: aload_3
        //   80: invokevirtual 76	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramTvInputInfo	TvInputInfo
        //   0	85	2	paramInt	int
        //   3	77	3	localParcel1	Parcel
        //   7	68	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	73	finally
        //   19	30	73	finally
        //   30	55	73	finally
        //   65	70	73	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/ITvInputManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */