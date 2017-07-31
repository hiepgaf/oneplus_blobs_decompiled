package com.android.server.audio;

import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.media.AudioRecordingConfiguration;
import android.media.AudioSystem;
import android.media.AudioSystem.AudioRecordingCallback;
import android.media.IRecordingConfigDispatcher;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class RecordingActivityMonitor
  implements AudioSystem.AudioRecordingCallback
{
  public static final String TAG = "AudioService.RecordingActivityMonitor";
  private ArrayList<RecMonitorClient> mClients = new ArrayList();
  private HashMap<Integer, AudioRecordingConfiguration> mRecordConfigs = new HashMap();
  
  RecordingActivityMonitor()
  {
    RecMonitorClient.sMonitor = this;
  }
  
  private List<AudioRecordingConfiguration> updateSnapshot(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    HashMap localHashMap = this.mRecordConfigs;
    switch (paramInt1)
    {
    }
    for (;;)
    {
      try
      {
        Log.e("AudioService.RecordingActivityMonitor", String.format("Unknown event %d for session %d, source %d", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) }));
        paramInt1 = 0;
        if (paramInt1 != 0)
        {
          paramArrayOfInt = new ArrayList(this.mRecordConfigs.values());
          return paramArrayOfInt;
          if (this.mRecordConfigs.remove(new Integer(paramInt2)) != null)
          {
            paramInt1 = 1;
            continue;
            Object localObject = new AudioFormat.Builder().setEncoding(paramArrayOfInt[0]).setChannelMask(paramArrayOfInt[1]).setSampleRate(paramArrayOfInt[2]).build();
            AudioFormat localAudioFormat = new AudioFormat.Builder().setEncoding(paramArrayOfInt[3]).setChannelMask(paramArrayOfInt[4]).setSampleRate(paramArrayOfInt[5]).build();
            paramInt1 = paramArrayOfInt[6];
            paramArrayOfInt = new Integer(paramInt2);
            if (this.mRecordConfigs.containsKey(paramArrayOfInt))
            {
              localObject = new AudioRecordingConfiguration(paramInt2, paramInt3, (AudioFormat)localObject, localAudioFormat, paramInt1);
              if (((AudioRecordingConfiguration)localObject).equals(this.mRecordConfigs.get(paramArrayOfInt)))
              {
                paramInt1 = 0;
                continue;
              }
              this.mRecordConfigs.remove(paramArrayOfInt);
              this.mRecordConfigs.put(paramArrayOfInt, localObject);
              paramInt1 = 1;
              continue;
            }
            this.mRecordConfigs.put(paramArrayOfInt, new AudioRecordingConfiguration(paramInt2, paramInt3, (AudioFormat)localObject, localAudioFormat, paramInt1));
            paramInt1 = 1;
          }
        }
        else
        {
          paramArrayOfInt = null;
          continue;
        }
        paramInt1 = 0;
      }
      finally {}
    }
  }
  
  List<AudioRecordingConfiguration> getActiveRecordingConfigurations()
  {
    synchronized (this.mRecordConfigs)
    {
      ArrayList localArrayList = new ArrayList(this.mRecordConfigs.values());
      return localArrayList;
    }
  }
  
  void initMonitor()
  {
    AudioSystem.setRecordingCallback(this);
  }
  
  public void onRecordingConfigurationChanged(int paramInt1, int paramInt2, int paramInt3, int[] arg4)
  {
    if (MediaRecorder.isSystemOnlyAudioSource(paramInt3)) {
      return;
    }
    List localList = updateSnapshot(paramInt1, paramInt2, paramInt3, ???);
    if (localList != null) {
      synchronized (this.mClients)
      {
        Iterator localIterator = this.mClients.iterator();
        for (;;)
        {
          boolean bool = localIterator.hasNext();
          if (bool) {
            try
            {
              ((RecMonitorClient)localIterator.next()).mDispatcherCb.dispatchRecordingConfigChange(localList);
            }
            catch (RemoteException localRemoteException)
            {
              Log.w("AudioService.RecordingActivityMonitor", "Could not call dispatchRecordingConfigChange() on client", localRemoteException);
            }
          }
        }
      }
    }
  }
  
  void registerRecordingCallback(IRecordingConfigDispatcher paramIRecordingConfigDispatcher)
  {
    if (paramIRecordingConfigDispatcher == null) {
      return;
    }
    synchronized (this.mClients)
    {
      paramIRecordingConfigDispatcher = new RecMonitorClient(paramIRecordingConfigDispatcher);
      if (paramIRecordingConfigDispatcher.init()) {
        this.mClients.add(paramIRecordingConfigDispatcher);
      }
      return;
    }
  }
  
  void unregisterRecordingCallback(IRecordingConfigDispatcher paramIRecordingConfigDispatcher)
  {
    if (paramIRecordingConfigDispatcher == null) {
      return;
    }
    synchronized (this.mClients)
    {
      Iterator localIterator = this.mClients.iterator();
      while (localIterator.hasNext())
      {
        RecMonitorClient localRecMonitorClient = (RecMonitorClient)localIterator.next();
        if (paramIRecordingConfigDispatcher.equals(localRecMonitorClient.mDispatcherCb))
        {
          localRecMonitorClient.release();
          localIterator.remove();
        }
      }
      return;
    }
  }
  
  private static final class RecMonitorClient
    implements IBinder.DeathRecipient
  {
    static RecordingActivityMonitor sMonitor;
    final IRecordingConfigDispatcher mDispatcherCb;
    
    RecMonitorClient(IRecordingConfigDispatcher paramIRecordingConfigDispatcher)
    {
      this.mDispatcherCb = paramIRecordingConfigDispatcher;
    }
    
    public void binderDied()
    {
      Log.w("AudioService.RecordingActivityMonitor", "client died");
      sMonitor.unregisterRecordingCallback(this.mDispatcherCb);
    }
    
    boolean init()
    {
      try
      {
        this.mDispatcherCb.asBinder().linkToDeath(this, 0);
        return true;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("AudioService.RecordingActivityMonitor", "Could not link to client death", localRemoteException);
      }
      return false;
    }
    
    void release()
    {
      this.mDispatcherCb.asBinder().unlinkToDeath(this, 0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/audio/RecordingActivityMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */