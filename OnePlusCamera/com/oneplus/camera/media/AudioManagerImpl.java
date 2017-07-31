package com.oneplus.camera.media;

import android.media.AudioAttributes.Builder;
import android.media.SoundPool;
import android.media.SoundPool.Builder;
import android.media.SoundPool.OnLoadCompleteListener;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.CameraThread;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

final class AudioManagerImpl
  extends CameraComponent
  implements AudioManager
{
  private final SoundPool.OnLoadCompleteListener m_LoadCompleteListener = new SoundPool.OnLoadCompleteListener()
  {
    public void onLoadComplete(SoundPool paramAnonymousSoundPool, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      AudioManagerImpl.-wrap0(AudioManagerImpl.this, paramAnonymousSoundPool, paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  private final Hashtable<Integer, List<SoundHandle>> m_SoundHandles = new Hashtable();
  private final Hashtable<Integer, SoundPool> m_SoundPools = new Hashtable();
  
  AudioManagerImpl(CameraActivity paramCameraActivity)
  {
    super("Camera Audio Manager", paramCameraActivity, false);
  }
  
  AudioManagerImpl(CameraThread paramCameraThread)
  {
    super("Camera Audio Manager", paramCameraThread, false);
  }
  
  private SoundPool getSoundPool(int paramInt, boolean paramBoolean)
  {
    Object localObject2 = (SoundPool)this.m_SoundPools.get(Integer.valueOf(paramInt));
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = localObject2;
      if (paramBoolean)
      {
        Log.v(this.TAG, "getSoundPool() - Create sound pool for stream type ", Integer.valueOf(paramInt));
        localObject1 = new AudioAttributes.Builder();
        localObject2 = new SoundPool.Builder();
        ((AudioAttributes.Builder)localObject1).setLegacyStreamType(paramInt);
        ((AudioAttributes.Builder)localObject1).setContentType(4);
        ((SoundPool.Builder)localObject2).setAudioAttributes(((AudioAttributes.Builder)localObject1).build());
        ((SoundPool.Builder)localObject2).setMaxStreams(4);
        localObject1 = ((SoundPool.Builder)localObject2).build();
        ((SoundPool)localObject1).setOnLoadCompleteListener(this.m_LoadCompleteListener);
        this.m_SoundPools.put(Integer.valueOf(paramInt), localObject1);
      }
    }
    return (SoundPool)localObject1;
  }
  
  private void onSoundLoaded(SoundPool paramSoundPool, int paramInt1, int paramInt2)
  {
    Object localObject2 = null;
    Object localObject3 = this.m_SoundPools.entrySet().iterator();
    do
    {
      localObject1 = localObject2;
      if (!((Iterator)localObject3).hasNext()) {
        break;
      }
      localObject1 = (Map.Entry)((Iterator)localObject3).next();
    } while (((Map.Entry)localObject1).getValue() != paramSoundPool);
    Object localObject1 = (Integer)((Map.Entry)localObject1).getKey();
    if (localObject1 == null)
    {
      Log.e(this.TAG, "onSoundLoaded() - Unknown sound pool");
      if (paramInt1 != 0) {
        paramSoundPool.unload(paramInt1);
      }
      return;
    }
    localObject2 = null;
    localObject3 = (List)this.m_SoundHandles.get(localObject1);
    localObject1 = localObject2;
    if (localObject3 != null) {
      paramInt2 = ((List)localObject3).size() - 1;
    }
    for (;;)
    {
      localObject1 = localObject2;
      if (paramInt2 >= 0)
      {
        localObject1 = (SoundHandle)((List)localObject3).get(paramInt2);
        if (((SoundHandle)localObject1).soundId != paramInt1) {}
      }
      else
      {
        if (localObject1 != null) {
          break;
        }
        Log.e(this.TAG, "onSoundLoaded() - Unknown sound ID : " + paramInt1);
        if (paramInt1 != 0) {
          paramSoundPool.unload(paramInt1);
        }
        return;
      }
      paramInt2 -= 1;
    }
    Log.v(this.TAG, "onSoundLoaded() - Handle : ", localObject1);
    ((SoundHandle)localObject1).isLoaded = true;
    if (((SoundHandle)localObject1).pendingStreams != null)
    {
      paramInt1 = ((SoundHandle)localObject1).pendingStreams.size() - 1;
      while (paramInt1 >= 0)
      {
        playSound((StreamHandle)((SoundHandle)localObject1).pendingStreams.get(paramInt1));
        paramInt1 -= 1;
      }
      ((SoundHandle)localObject1).pendingStreams.clear();
    }
  }
  
  private boolean playSound(StreamHandle paramStreamHandle)
  {
    Log.v(this.TAG, "playSound() - Sound handle : ", paramStreamHandle.sound, ", stream handle : ", paramStreamHandle);
    SoundPool localSoundPool = getSoundPool(paramStreamHandle.sound.streamType, true);
    int j = paramStreamHandle.sound.soundId;
    if ((paramStreamHandle.flags & 0x1) == 0) {}
    for (int i = 0;; i = -1)
    {
      i = localSoundPool.play(j, 1.0F, 1.0F, 2, i, 1.0F);
      if (i != 0) {
        break;
      }
      Log.e(this.TAG, "playSound() - Fail to play sound " + paramStreamHandle.sound);
      return false;
    }
    paramStreamHandle.streamId = i;
    return true;
  }
  
  private void stopSound(StreamHandle paramStreamHandle)
  {
    verifyAccess();
    Log.v(this.TAG, "stopSound() - Handle : ", paramStreamHandle);
    Object localObject = paramStreamHandle.sound;
    if ((((SoundHandle)localObject).pendingStreams != null) && (((SoundHandle)localObject).pendingStreams.remove(paramStreamHandle))) {
      return;
    }
    if (!((SoundHandle)localObject).isLoaded) {
      return;
    }
    localObject = getSoundPool(((SoundHandle)localObject).streamType, false);
    if (localObject == null)
    {
      Log.w(this.TAG, "stopSound() - No sound pool to stop");
      return;
    }
    if (paramStreamHandle.streamId != 0)
    {
      ((SoundPool)localObject).stop(paramStreamHandle.streamId);
      paramStreamHandle.streamId = 0;
    }
  }
  
  private void unloadSound(SoundHandle paramSoundHandle)
  {
    verifyAccess();
    Log.v(this.TAG, "unloadSound() - Handle : ", paramSoundHandle);
    paramSoundHandle.pendingStreams = null;
    Object localObject = getSoundPool(paramSoundHandle.streamType, false);
    if (paramSoundHandle.soundId != 0)
    {
      if (localObject == null) {
        break label91;
      }
      ((SoundPool)localObject).unload(paramSoundHandle.soundId);
    }
    for (;;)
    {
      paramSoundHandle.soundId = 0;
      paramSoundHandle.isLoaded = false;
      localObject = (List)this.m_SoundHandles.get(Integer.valueOf(paramSoundHandle.streamType));
      if (localObject != null) {
        ((List)localObject).remove(paramSoundHandle);
      }
      return;
      label91:
      Log.w(this.TAG, "unloadSound() - No sound pool to unload");
    }
  }
  
  public Handle loadSound(int paramInt1, int paramInt2, int paramInt3)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "loadSound() - Component is not running");
      return null;
    }
    List localList = (List)this.m_SoundHandles.get(Integer.valueOf(paramInt2));
    if (localList != null)
    {
      paramInt3 = localList.size() - 1;
      while (paramInt3 >= 0)
      {
        localObject = (SoundHandle)localList.get(paramInt3);
        if (((SoundHandle)localObject).resourceId == paramInt1) {
          return (Handle)localObject;
        }
        paramInt3 -= 1;
      }
    }
    SoundHandle localSoundHandle = new SoundHandle(paramInt1, paramInt2, getSoundPool(paramInt2, true).load(getContext(), paramInt1, 1));
    Log.v(this.TAG, "loadSound() - Resource : ", Integer.valueOf(paramInt1), ", handle : ", localSoundHandle);
    Object localObject = localList;
    if (localList == null)
    {
      localObject = new ArrayList();
      this.m_SoundHandles.put(Integer.valueOf(paramInt2), localObject);
    }
    ((List)localObject).add(localSoundHandle);
    return localSoundHandle;
  }
  
  protected void onDeinitialize()
  {
    Iterator localIterator = this.m_SoundHandles.entrySet().iterator();
    while (localIterator.hasNext())
    {
      List localList = (List)((Map.Entry)localIterator.next()).getValue();
      if (localList != null) {
        while (!localList.isEmpty())
        {
          SoundHandle localSoundHandle = (SoundHandle)localList.get(0);
          if (Handle.isValid(localSoundHandle)) {
            Handle.close(localSoundHandle);
          }
        }
      }
    }
    this.m_SoundHandles.clear();
    localIterator = this.m_SoundPools.values().iterator();
    while (localIterator.hasNext()) {
      ((SoundPool)localIterator.next()).release();
    }
    this.m_SoundPools.clear();
    super.onDeinitialize();
  }
  
  public Handle playSound(Handle paramHandle, int paramInt)
  {
    if (paramHandle == null)
    {
      Log.e(this.TAG, "playSound() - Null handle");
      return null;
    }
    if (!(paramHandle instanceof SoundHandle))
    {
      Log.e(this.TAG, "playSound() - Invalid handle");
      return null;
    }
    verifyAccess();
    paramHandle = (SoundHandle)paramHandle;
    Object localObject = (List)this.m_SoundHandles.get(Integer.valueOf(paramHandle.streamType));
    if ((localObject != null) && (((List)localObject).contains(paramHandle)))
    {
      localObject = new StreamHandle(paramHandle, paramInt);
      if (!paramHandle.isLoaded)
      {
        Log.w(this.TAG, "playSound() - Sound " + paramHandle + " is not loaded yet, play later");
        if (paramHandle.pendingStreams == null) {
          paramHandle.pendingStreams = new ArrayList();
        }
        paramHandle.pendingStreams.add(localObject);
        return (Handle)localObject;
      }
    }
    else
    {
      Log.e(this.TAG, "playSound() - Invalid handle");
      return null;
    }
    if (!playSound((StreamHandle)localObject)) {
      return null;
    }
    return (Handle)localObject;
  }
  
  private final class SoundHandle
    extends Handle
  {
    public boolean isLoaded;
    public List<AudioManagerImpl.StreamHandle> pendingStreams;
    public final int resourceId;
    public int soundId;
    public final int streamType;
    
    public SoundHandle(int paramInt1, int paramInt2, int paramInt3)
    {
      super();
      this.resourceId = paramInt1;
      this.streamType = paramInt2;
      this.soundId = paramInt3;
    }
    
    protected void onClose(int paramInt)
    {
      AudioManagerImpl.-wrap2(AudioManagerImpl.this, this);
    }
  }
  
  private final class StreamHandle
    extends Handle
  {
    public final int flags;
    public final AudioManagerImpl.SoundHandle sound;
    public int streamId;
    
    public StreamHandle(AudioManagerImpl.SoundHandle paramSoundHandle, int paramInt)
    {
      super();
      this.sound = paramSoundHandle;
      this.flags = paramInt;
    }
    
    protected void onClose(int paramInt)
    {
      AudioManagerImpl.-wrap1(AudioManagerImpl.this, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/AudioManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */