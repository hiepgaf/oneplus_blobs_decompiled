package android.media.soundtrigger;

import android.content.Context;
import android.hardware.soundtrigger.SoundTrigger.GenericSoundModel;
import android.hardware.soundtrigger.SoundTrigger.SoundModel;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.RemoteException;
import com.android.internal.app.ISoundTriggerService;
import java.util.HashMap;
import java.util.UUID;

public final class SoundTriggerManager
{
  private static final boolean DBG = false;
  private static final String TAG = "SoundTriggerManager";
  private final Context mContext;
  private final HashMap<UUID, SoundTriggerDetector> mReceiverInstanceMap;
  private final ISoundTriggerService mSoundTriggerService;
  
  public SoundTriggerManager(Context paramContext, ISoundTriggerService paramISoundTriggerService)
  {
    this.mSoundTriggerService = paramISoundTriggerService;
    this.mContext = paramContext;
    this.mReceiverInstanceMap = new HashMap();
  }
  
  public SoundTriggerDetector createSoundTriggerDetector(UUID paramUUID, SoundTriggerDetector.Callback paramCallback, Handler paramHandler)
  {
    if (paramUUID == null) {
      return null;
    }
    if ((SoundTriggerDetector)this.mReceiverInstanceMap.get(paramUUID) != null) {}
    paramCallback = new SoundTriggerDetector(this.mSoundTriggerService, paramUUID, paramCallback, paramHandler);
    this.mReceiverInstanceMap.put(paramUUID, paramCallback);
    return paramCallback;
  }
  
  public void deleteModel(UUID paramUUID)
  {
    try
    {
      this.mSoundTriggerService.deleteSoundModel(new ParcelUuid(paramUUID));
      return;
    }
    catch (RemoteException paramUUID)
    {
      throw paramUUID.rethrowFromSystemServer();
    }
  }
  
  public Model getModel(UUID paramUUID)
  {
    try
    {
      paramUUID = new Model(this.mSoundTriggerService.getSoundModel(new ParcelUuid(paramUUID)));
      return paramUUID;
    }
    catch (RemoteException paramUUID)
    {
      throw paramUUID.rethrowFromSystemServer();
    }
  }
  
  public void updateModel(Model paramModel)
  {
    try
    {
      this.mSoundTriggerService.updateSoundModel(paramModel.getGenericSoundModel());
      return;
    }
    catch (RemoteException paramModel)
    {
      throw paramModel.rethrowFromSystemServer();
    }
  }
  
  public static class Model
  {
    private SoundTrigger.GenericSoundModel mGenericSoundModel;
    
    Model(SoundTrigger.GenericSoundModel paramGenericSoundModel)
    {
      this.mGenericSoundModel = paramGenericSoundModel;
    }
    
    public static Model create(UUID paramUUID1, UUID paramUUID2, byte[] paramArrayOfByte)
    {
      return new Model(new SoundTrigger.GenericSoundModel(paramUUID1, paramUUID2, paramArrayOfByte));
    }
    
    SoundTrigger.GenericSoundModel getGenericSoundModel()
    {
      return this.mGenericSoundModel;
    }
    
    public byte[] getModelData()
    {
      return this.mGenericSoundModel.data;
    }
    
    public UUID getModelUuid()
    {
      return this.mGenericSoundModel.uuid;
    }
    
    public UUID getVendorUuid()
    {
      return this.mGenericSoundModel.vendorUuid;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/soundtrigger/SoundTriggerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */