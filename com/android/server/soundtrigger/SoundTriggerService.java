package com.android.server.soundtrigger;

import android.content.Context;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger.GenericSoundModel;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel;
import android.hardware.soundtrigger.SoundTrigger.ModuleProperties;
import android.hardware.soundtrigger.SoundTrigger.RecognitionConfig;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.app.ISoundTriggerService.Stub;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class SoundTriggerService
  extends SystemService
{
  private static final boolean DEBUG = true;
  private static final String TAG = "SoundTriggerService";
  final Context mContext;
  private SoundTriggerDbHelper mDbHelper;
  private final LocalSoundTriggerService mLocalSoundTriggerService;
  private final SoundTriggerServiceStub mServiceStub;
  private SoundTriggerHelper mSoundTriggerHelper;
  
  public SoundTriggerService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mServiceStub = new SoundTriggerServiceStub();
    this.mLocalSoundTriggerService = new LocalSoundTriggerService(paramContext);
  }
  
  private void enforceCallingPermission(String paramString)
  {
    if (this.mContext.checkCallingOrSelfPermission(paramString) != 0) {
      throw new SecurityException("Caller does not hold the permission " + paramString);
    }
  }
  
  private void initSoundTriggerHelper()
  {
    try
    {
      if (this.mSoundTriggerHelper == null) {
        this.mSoundTriggerHelper = new SoundTriggerHelper(this.mContext);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private boolean isInitialized()
  {
    try
    {
      if (this.mSoundTriggerHelper == null)
      {
        Slog.e("SoundTriggerService", "SoundTriggerHelper not initialized.");
        return false;
      }
      return true;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    if (500 == paramInt)
    {
      initSoundTriggerHelper();
      this.mLocalSoundTriggerService.setSoundTriggerHelper(this.mSoundTriggerHelper);
    }
    while (600 != paramInt) {
      return;
    }
    this.mDbHelper = new SoundTriggerDbHelper(this.mContext);
  }
  
  public void onStart()
  {
    publishBinderService("soundtrigger", this.mServiceStub);
    publishLocalService(SoundTriggerInternal.class, this.mLocalSoundTriggerService);
  }
  
  public void onStartUser(int paramInt) {}
  
  public void onSwitchUser(int paramInt) {}
  
  public final class LocalSoundTriggerService
    extends SoundTriggerInternal
  {
    private final Context mContext;
    private SoundTriggerHelper mSoundTriggerHelper;
    
    LocalSoundTriggerService(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    private boolean isInitialized()
    {
      try
      {
        if (this.mSoundTriggerHelper == null)
        {
          Slog.e("SoundTriggerService", "SoundTriggerHelper not initialized.");
          return false;
        }
        return true;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (!isInitialized()) {
        return;
      }
      this.mSoundTriggerHelper.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public SoundTrigger.ModuleProperties getModuleProperties()
    {
      if (!isInitialized()) {
        return null;
      }
      return this.mSoundTriggerHelper.getModuleProperties();
    }
    
    void setSoundTriggerHelper(SoundTriggerHelper paramSoundTriggerHelper)
    {
      try
      {
        this.mSoundTriggerHelper = paramSoundTriggerHelper;
        return;
      }
      finally
      {
        paramSoundTriggerHelper = finally;
        throw paramSoundTriggerHelper;
      }
    }
    
    public int startRecognition(int paramInt, SoundTrigger.KeyphraseSoundModel paramKeyphraseSoundModel, IRecognitionStatusCallback paramIRecognitionStatusCallback, SoundTrigger.RecognitionConfig paramRecognitionConfig)
    {
      if (!isInitialized()) {
        return Integer.MIN_VALUE;
      }
      return this.mSoundTriggerHelper.startKeyphraseRecognition(paramInt, paramKeyphraseSoundModel, paramIRecognitionStatusCallback, paramRecognitionConfig);
    }
    
    public int stopRecognition(int paramInt, IRecognitionStatusCallback paramIRecognitionStatusCallback)
    {
      try
      {
        boolean bool = isInitialized();
        if (!bool) {
          return Integer.MIN_VALUE;
        }
        paramInt = this.mSoundTriggerHelper.stopKeyphraseRecognition(paramInt, paramIRecognitionStatusCallback);
        return paramInt;
      }
      finally {}
    }
    
    public int unloadKeyphraseModel(int paramInt)
    {
      if (!isInitialized()) {
        return Integer.MIN_VALUE;
      }
      return this.mSoundTriggerHelper.unloadKeyphraseSoundModel(paramInt);
    }
  }
  
  class SoundTriggerServiceStub
    extends ISoundTriggerService.Stub
  {
    SoundTriggerServiceStub() {}
    
    public void deleteSoundModel(ParcelUuid paramParcelUuid)
    {
      SoundTriggerService.-wrap1(SoundTriggerService.this, "android.permission.MANAGE_SOUND_TRIGGER");
      Slog.i("SoundTriggerService", "deleteSoundModel(): id = " + paramParcelUuid);
      SoundTriggerService.-get1(SoundTriggerService.this).unloadGenericSoundModel(paramParcelUuid.getUuid());
      SoundTriggerService.-get0(SoundTriggerService.this).deleteGenericSoundModel(paramParcelUuid.getUuid());
    }
    
    public SoundTrigger.GenericSoundModel getSoundModel(ParcelUuid paramParcelUuid)
    {
      SoundTriggerService.-wrap1(SoundTriggerService.this, "android.permission.MANAGE_SOUND_TRIGGER");
      Slog.i("SoundTriggerService", "getSoundModel(): id = " + paramParcelUuid);
      return SoundTriggerService.-get0(SoundTriggerService.this).getGenericSoundModel(paramParcelUuid.getUuid());
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      try
      {
        boolean bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        return bool;
      }
      catch (RuntimeException paramParcel1)
      {
        if (!(paramParcel1 instanceof SecurityException)) {
          Slog.wtf("SoundTriggerService", "SoundTriggerService Crash", paramParcel1);
        }
        throw paramParcel1;
      }
    }
    
    public int startRecognition(ParcelUuid paramParcelUuid, IRecognitionStatusCallback paramIRecognitionStatusCallback, SoundTrigger.RecognitionConfig paramRecognitionConfig)
    {
      SoundTriggerService.-wrap1(SoundTriggerService.this, "android.permission.MANAGE_SOUND_TRIGGER");
      if (!SoundTriggerService.-wrap0(SoundTriggerService.this)) {
        return Integer.MIN_VALUE;
      }
      Slog.i("SoundTriggerService", "startRecognition(): Uuid : " + paramParcelUuid);
      SoundTrigger.GenericSoundModel localGenericSoundModel = getSoundModel(paramParcelUuid);
      if (localGenericSoundModel == null)
      {
        Slog.e("SoundTriggerService", "Null model in database for id: " + paramParcelUuid);
        return Integer.MIN_VALUE;
      }
      return SoundTriggerService.-get1(SoundTriggerService.this).startGenericRecognition(paramParcelUuid.getUuid(), localGenericSoundModel, paramIRecognitionStatusCallback, paramRecognitionConfig);
    }
    
    public int stopRecognition(ParcelUuid paramParcelUuid, IRecognitionStatusCallback paramIRecognitionStatusCallback)
    {
      SoundTriggerService.-wrap1(SoundTriggerService.this, "android.permission.MANAGE_SOUND_TRIGGER");
      Slog.i("SoundTriggerService", "stopRecognition(): Uuid : " + paramParcelUuid);
      if (!SoundTriggerService.-wrap0(SoundTriggerService.this)) {
        return Integer.MIN_VALUE;
      }
      return SoundTriggerService.-get1(SoundTriggerService.this).stopGenericRecognition(paramParcelUuid.getUuid(), paramIRecognitionStatusCallback);
    }
    
    public void updateSoundModel(SoundTrigger.GenericSoundModel paramGenericSoundModel)
    {
      SoundTriggerService.-wrap1(SoundTriggerService.this, "android.permission.MANAGE_SOUND_TRIGGER");
      Slog.i("SoundTriggerService", "updateSoundModel(): model = " + paramGenericSoundModel);
      SoundTriggerService.-get0(SoundTriggerService.this).updateGenericSoundModel(paramGenericSoundModel);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/soundtrigger/SoundTriggerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */