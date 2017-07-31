package android.hardware.location;

import android.content.Context;
import android.location.IFusedGeofenceHardware;
import android.location.IGpsGeofenceHardware;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Iterator;

public final class GeofenceHardwareImpl
{
  private static final int ADD_GEOFENCE_CALLBACK = 2;
  private static final int CALLBACK_ADD = 2;
  private static final int CALLBACK_REMOVE = 3;
  private static final int CAPABILITY_GNSS = 1;
  private static final boolean DEBUG = Log.isLoggable("GeofenceHardwareImpl", 3);
  private static final int FIRST_VERSION_WITH_CAPABILITIES = 2;
  private static final int GEOFENCE_CALLBACK_BINDER_DIED = 6;
  private static final int GEOFENCE_STATUS = 1;
  private static final int GEOFENCE_TRANSITION_CALLBACK = 1;
  private static final int LOCATION_HAS_ACCURACY = 16;
  private static final int LOCATION_HAS_ALTITUDE = 2;
  private static final int LOCATION_HAS_BEARING = 8;
  private static final int LOCATION_HAS_LAT_LONG = 1;
  private static final int LOCATION_HAS_SPEED = 4;
  private static final int LOCATION_INVALID = 0;
  private static final int MONITOR_CALLBACK_BINDER_DIED = 4;
  private static final int PAUSE_GEOFENCE_CALLBACK = 4;
  private static final int REAPER_GEOFENCE_ADDED = 1;
  private static final int REAPER_MONITOR_CALLBACK_ADDED = 2;
  private static final int REAPER_REMOVED = 3;
  private static final int REMOVE_GEOFENCE_CALLBACK = 3;
  private static final int RESOLUTION_LEVEL_COARSE = 2;
  private static final int RESOLUTION_LEVEL_FINE = 3;
  private static final int RESOLUTION_LEVEL_NONE = 1;
  private static final int RESUME_GEOFENCE_CALLBACK = 5;
  private static final String TAG = "GeofenceHardwareImpl";
  private static GeofenceHardwareImpl sInstance;
  private final ArrayList<IGeofenceHardwareMonitorCallback>[] mCallbacks = new ArrayList[2];
  private Handler mCallbacksHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      }
      Object localObject;
      do
      {
        do
        {
          IGeofenceHardwareMonitorCallback localIGeofenceHardwareMonitorCallback2;
          do
          {
            return;
            paramAnonymousMessage = (GeofenceHardwareMonitorEvent)paramAnonymousMessage.obj;
            localObject = GeofenceHardwareImpl.-get1(GeofenceHardwareImpl.this)[paramAnonymousMessage.getMonitoringType()];
            if (localObject != null)
            {
              if (GeofenceHardwareImpl.-get0()) {
                Log.d("GeofenceHardwareImpl", "MonitoringSystemChangeCallback: " + paramAnonymousMessage);
              }
              localObject = ((Iterable)localObject).iterator();
              while (((Iterator)localObject).hasNext())
              {
                IGeofenceHardwareMonitorCallback localIGeofenceHardwareMonitorCallback1 = (IGeofenceHardwareMonitorCallback)((Iterator)localObject).next();
                try
                {
                  localIGeofenceHardwareMonitorCallback1.onMonitoringSystemChange(paramAnonymousMessage);
                }
                catch (RemoteException localRemoteException)
                {
                  Log.d("GeofenceHardwareImpl", "Error reporting onMonitoringSystemChange.", localRemoteException);
                }
              }
            }
            GeofenceHardwareImpl.-wrap0(GeofenceHardwareImpl.this);
            return;
            i = paramAnonymousMessage.arg1;
            localIGeofenceHardwareMonitorCallback2 = (IGeofenceHardwareMonitorCallback)paramAnonymousMessage.obj;
            localObject = GeofenceHardwareImpl.-get1(GeofenceHardwareImpl.this)[i];
            paramAnonymousMessage = (Message)localObject;
            if (localObject == null)
            {
              paramAnonymousMessage = new ArrayList();
              GeofenceHardwareImpl.-get1(GeofenceHardwareImpl.this)[i] = paramAnonymousMessage;
            }
          } while (paramAnonymousMessage.contains(localIGeofenceHardwareMonitorCallback2));
          paramAnonymousMessage.add(localIGeofenceHardwareMonitorCallback2);
          return;
          int i = paramAnonymousMessage.arg1;
          paramAnonymousMessage = (IGeofenceHardwareMonitorCallback)paramAnonymousMessage.obj;
          localObject = GeofenceHardwareImpl.-get1(GeofenceHardwareImpl.this)[i];
        } while (localObject == null);
        ((ArrayList)localObject).remove(paramAnonymousMessage);
        return;
        localObject = (IGeofenceHardwareMonitorCallback)paramAnonymousMessage.obj;
        if (GeofenceHardwareImpl.-get0()) {
          Log.d("GeofenceHardwareImpl", "Monitor callback reaped:" + localObject);
        }
        paramAnonymousMessage = GeofenceHardwareImpl.-get1(GeofenceHardwareImpl.this)[paramAnonymousMessage.arg1];
      } while ((paramAnonymousMessage == null) || (!paramAnonymousMessage.contains(localObject)));
      paramAnonymousMessage.remove(localObject);
    }
  };
  private int mCapabilities;
  private final Context mContext;
  private IFusedGeofenceHardware mFusedService;
  private Handler mGeofenceHandler = new Handler()
  {
    public void handleMessage(Message arg1)
    {
      int i;
      Object localObject4;
      switch (???.what)
      {
      default: 
        return;
      case 2: 
        i = ???.arg1;
        synchronized (GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this))
        {
          localObject4 = (IGeofenceHardwareCallback)GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).get(i);
          if (localObject4 == null) {}
        }
        try
        {
          ((IGeofenceHardwareCallback)localObject4).onGeofenceAdd(i, ???.arg2);
          GeofenceHardwareImpl.-wrap0(GeofenceHardwareImpl.this);
          return;
          ??? = finally;
          throw ???;
        }
        catch (RemoteException ???)
        {
          for (;;)
          {
            Log.i("GeofenceHardwareImpl", "Remote Exception:" + ???);
          }
        }
      case 3: 
        i = ???.arg1;
        synchronized (GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this))
        {
          localObject4 = (IGeofenceHardwareCallback)GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).get(i);
          if (localObject4 == null) {}
        }
      }
      try
      {
        ((IGeofenceHardwareCallback)localObject4).onGeofenceRemove(i, ???.arg2);
        ??? = ((IGeofenceHardwareCallback)localObject4).asBinder();
        int k = 0;
        int j;
        synchronized (GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this))
        {
          GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).remove(i);
          i = 0;
          j = k;
          if (i < GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).size())
          {
            localObject4 = ((IGeofenceHardwareCallback)GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).valueAt(i)).asBinder();
            if (localObject4 == ???) {
              j = 1;
            }
          }
          else
          {
            if (j != 0) {
              break label404;
            }
            ??? = GeofenceHardwareImpl.-get6(GeofenceHardwareImpl.this).iterator();
            while (((Iterator)???).hasNext())
            {
              localObject4 = (GeofenceHardwareImpl.Reaper)((Iterator)???).next();
              if ((GeofenceHardwareImpl.Reaper.-get0((GeofenceHardwareImpl.Reaper)localObject4) != null) && (GeofenceHardwareImpl.Reaper.-get0((GeofenceHardwareImpl.Reaper)localObject4).asBinder() == ???))
              {
                ((Iterator)???).remove();
                GeofenceHardwareImpl.Reaper.-wrap0((GeofenceHardwareImpl.Reaper)localObject4);
                if (GeofenceHardwareImpl.-get0()) {
                  Log.d("GeofenceHardwareImpl", String.format("Removed reaper %s because binder %s is no longer needed.", new Object[] { localObject4, ??? }));
                }
              }
            }
            ??? = finally;
            throw ???;
          }
          i += 1;
        }
        label404:
        GeofenceHardwareImpl.-wrap0(GeofenceHardwareImpl.this);
        return;
        i = ???.arg1;
        synchronized (GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this))
        {
          localObject4 = (IGeofenceHardwareCallback)GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).get(i);
          if (localObject4 == null) {}
        }
        try
        {
          ((IGeofenceHardwareCallback)localObject4).onGeofencePause(i, ???.arg2);
          GeofenceHardwareImpl.-wrap0(GeofenceHardwareImpl.this);
          return;
          ??? = finally;
          throw ???;
          i = ???.arg1;
          synchronized (GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this))
          {
            localObject4 = (IGeofenceHardwareCallback)GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).get(i);
            if (localObject4 == null) {}
          }
          try
          {
            ((IGeofenceHardwareCallback)localObject4).onGeofenceResume(i, ???.arg2);
            GeofenceHardwareImpl.-wrap0(GeofenceHardwareImpl.this);
            return;
            ??? = finally;
            throw ???;
            ??? = (GeofenceHardwareImpl.GeofenceTransition)???.obj;
            synchronized (GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this))
            {
              localObject4 = (IGeofenceHardwareCallback)GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).get(GeofenceHardwareImpl.GeofenceTransition.-get0((GeofenceHardwareImpl.GeofenceTransition)???));
              if (GeofenceHardwareImpl.-get0()) {
                Log.d("GeofenceHardwareImpl", "GeofenceTransistionCallback: GPS : GeofenceId: " + GeofenceHardwareImpl.GeofenceTransition.-get0((GeofenceHardwareImpl.GeofenceTransition)???) + " Transition: " + GeofenceHardwareImpl.GeofenceTransition.-get4((GeofenceHardwareImpl.GeofenceTransition)???) + " Location: " + GeofenceHardwareImpl.GeofenceTransition.-get1((GeofenceHardwareImpl.GeofenceTransition)???) + ":" + GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this));
              }
              if (localObject4 == null) {}
            }
            try
            {
              ((IGeofenceHardwareCallback)localObject4).onGeofenceTransition(GeofenceHardwareImpl.GeofenceTransition.-get0((GeofenceHardwareImpl.GeofenceTransition)???), GeofenceHardwareImpl.GeofenceTransition.-get4((GeofenceHardwareImpl.GeofenceTransition)???), GeofenceHardwareImpl.GeofenceTransition.-get1((GeofenceHardwareImpl.GeofenceTransition)???), GeofenceHardwareImpl.GeofenceTransition.-get3((GeofenceHardwareImpl.GeofenceTransition)???), GeofenceHardwareImpl.GeofenceTransition.-get2((GeofenceHardwareImpl.GeofenceTransition)???));
              GeofenceHardwareImpl.-wrap0(GeofenceHardwareImpl.this);
              return;
              localObject2 = finally;
              throw ((Throwable)localObject2);
              IGeofenceHardwareCallback localIGeofenceHardwareCallback = (IGeofenceHardwareCallback)???.obj;
              if (GeofenceHardwareImpl.-get0()) {
                Log.d("GeofenceHardwareImpl", "Geofence callback reaped:" + localIGeofenceHardwareCallback);
              }
              j = ???.arg1;
              ??? = GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this);
              i = 0;
              try
              {
                while (i < GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).size())
                {
                  if (((IGeofenceHardwareCallback)GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).valueAt(i)).equals(localIGeofenceHardwareCallback))
                  {
                    k = GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).keyAt(i);
                    GeofenceHardwareImpl.this.removeGeofence(GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).keyAt(i), j);
                    GeofenceHardwareImpl.-get4(GeofenceHardwareImpl.this).remove(k);
                  }
                  i += 1;
                }
                return;
              }
              finally
              {
                localObject3 = finally;
                throw ((Throwable)localObject3);
              }
            }
            catch (RemoteException ???)
            {
              for (;;) {}
            }
          }
          catch (RemoteException ???)
          {
            for (;;) {}
          }
        }
        catch (RemoteException ???)
        {
          for (;;) {}
        }
      }
      catch (RemoteException ???)
      {
        for (;;) {}
      }
    }
  };
  private final SparseArray<IGeofenceHardwareCallback> mGeofences = new SparseArray();
  private IGpsGeofenceHardware mGpsService;
  private Handler mReaperHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
      case 1: 
      case 2: 
        do
        {
          do
          {
            return;
            localObject = (IGeofenceHardwareCallback)paramAnonymousMessage.obj;
            i = paramAnonymousMessage.arg1;
            paramAnonymousMessage = new GeofenceHardwareImpl.Reaper(GeofenceHardwareImpl.this, (IGeofenceHardwareCallback)localObject, i);
          } while (GeofenceHardwareImpl.-get6(GeofenceHardwareImpl.this).contains(paramAnonymousMessage));
          GeofenceHardwareImpl.-get6(GeofenceHardwareImpl.this).add(paramAnonymousMessage);
          localObject = ((IGeofenceHardwareCallback)localObject).asBinder();
          try
          {
            ((IBinder)localObject).linkToDeath(paramAnonymousMessage, 0);
            return;
          }
          catch (RemoteException paramAnonymousMessage)
          {
            return;
          }
          localObject = (IGeofenceHardwareMonitorCallback)paramAnonymousMessage.obj;
          int i = paramAnonymousMessage.arg1;
          paramAnonymousMessage = new GeofenceHardwareImpl.Reaper(GeofenceHardwareImpl.this, (IGeofenceHardwareMonitorCallback)localObject, i);
        } while (GeofenceHardwareImpl.-get6(GeofenceHardwareImpl.this).contains(paramAnonymousMessage));
        GeofenceHardwareImpl.-get6(GeofenceHardwareImpl.this).add(paramAnonymousMessage);
        Object localObject = ((IGeofenceHardwareMonitorCallback)localObject).asBinder();
        try
        {
          ((IBinder)localObject).linkToDeath(paramAnonymousMessage, 0);
          return;
        }
        catch (RemoteException paramAnonymousMessage)
        {
          return;
        }
      }
      paramAnonymousMessage = (GeofenceHardwareImpl.Reaper)paramAnonymousMessage.obj;
      GeofenceHardwareImpl.-get6(GeofenceHardwareImpl.this).remove(paramAnonymousMessage);
    }
  };
  private final ArrayList<Reaper> mReapers = new ArrayList();
  private int[] mSupportedMonitorTypes = new int[2];
  private int mVersion = 1;
  private PowerManager.WakeLock mWakeLock;
  
  private GeofenceHardwareImpl(Context paramContext)
  {
    this.mContext = paramContext;
    setMonitorAvailability(0, 2);
    setMonitorAvailability(1, 2);
  }
  
  private void acquireWakeLock()
  {
    if (this.mWakeLock == null) {
      this.mWakeLock = ((PowerManager)this.mContext.getSystemService("power")).newWakeLock(1, "GeofenceHardwareImpl");
    }
    this.mWakeLock.acquire();
  }
  
  public static GeofenceHardwareImpl getInstance(Context paramContext)
  {
    try
    {
      if (sInstance == null) {
        sInstance = new GeofenceHardwareImpl(paramContext);
      }
      paramContext = sInstance;
      return paramContext;
    }
    finally {}
  }
  
  private void releaseWakeLock()
  {
    if (this.mWakeLock.isHeld()) {
      this.mWakeLock.release();
    }
  }
  
  private void reportGeofenceOperationStatus(int paramInt1, int paramInt2, int paramInt3)
  {
    acquireWakeLock();
    Message localMessage = this.mGeofenceHandler.obtainMessage(paramInt1);
    localMessage.arg1 = paramInt2;
    localMessage.arg2 = paramInt3;
    localMessage.sendToTarget();
  }
  
  private void setMonitorAvailability(int paramInt1, int paramInt2)
  {
    synchronized (this.mSupportedMonitorTypes)
    {
      this.mSupportedMonitorTypes[paramInt1] = paramInt2;
      return;
    }
  }
  
  private void updateFusedHardwareAvailability()
  {
    for (;;)
    {
      try
      {
        if (this.mVersion < 2) {
          continue;
        }
        if ((this.mCapabilities & 0x1) == 0) {
          continue;
        }
        i = 1;
        if (this.mFusedService == null) {
          continue;
        }
        boolean bool = this.mFusedService.isSupported();
        if (!bool) {
          continue;
        }
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("GeofenceHardwareImpl", "RemoteException calling LocationManagerService");
        int i = 0;
        continue;
      }
      if (i != 0) {
        setMonitorAvailability(1, 0);
      }
      return;
      i = 1;
      continue;
      i = 0;
      continue;
      i = 0;
      continue;
      i = 0;
    }
  }
  
  private void updateGpsHardwareAvailability()
  {
    try
    {
      bool = this.mGpsService.isHardwareGeofenceSupported();
      if (bool) {
        setMonitorAvailability(0, 0);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("GeofenceHardwareImpl", "Remote Exception calling LocationManagerService");
        boolean bool = false;
      }
    }
  }
  
  public boolean addCircularFence(int paramInt, GeofenceHardwareRequestParcelable arg2, IGeofenceHardwareCallback paramIGeofenceHardwareCallback)
  {
    int i = ???.getId();
    if (DEBUG) {
      Log.d("GeofenceHardwareImpl", String.format("addCircularFence: monitoringType=%d, %s", new Object[] { Integer.valueOf(paramInt), ??? }));
    }
    for (;;)
    {
      boolean bool;
      synchronized (this.mGeofences)
      {
        this.mGeofences.put(i, paramIGeofenceHardwareCallback);
        switch (paramInt)
        {
        default: 
          bool = false;
          if (!bool) {
            break label277;
          }
          ??? = this.mReaperHandler.obtainMessage(1, paramIGeofenceHardwareCallback);
          ???.arg1 = paramInt;
          this.mReaperHandler.sendMessage(???);
          if (DEBUG) {
            Log.d("GeofenceHardwareImpl", "addCircularFence: Result is: " + bool);
          }
          return bool;
        }
      }
      if (this.mGpsService == null) {
        return false;
      }
      try
      {
        bool = this.mGpsService.addCircularHardwareGeofence(???.getId(), ???.getLatitude(), ???.getLongitude(), ???.getRadius(), ???.getLastTransition(), ???.getMonitorTransitions(), ???.getNotificationResponsiveness(), ???.getUnknownTimer());
      }
      catch (RemoteException ???)
      {
        Log.e("GeofenceHardwareImpl", "AddGeofence: Remote Exception calling LocationManagerService");
        bool = false;
      }
      continue;
      if (this.mFusedService == null) {
        return false;
      }
      try
      {
        this.mFusedService.addGeofences(new GeofenceHardwareRequestParcelable[] { ??? });
        bool = true;
      }
      catch (RemoteException ???)
      {
        Log.e("GeofenceHardwareImpl", "AddGeofence: RemoteException calling LocationManagerService");
        bool = false;
      }
      continue;
      label277:
      synchronized (this.mGeofences)
      {
        this.mGeofences.remove(i);
      }
    }
  }
  
  int getAllowedResolutionLevel(int paramInt1, int paramInt2)
  {
    if (this.mContext.checkPermission("android.permission.ACCESS_FINE_LOCATION", paramInt1, paramInt2) == 0) {
      return 3;
    }
    if (this.mContext.checkPermission("android.permission.ACCESS_COARSE_LOCATION", paramInt1, paramInt2) == 0) {
      return 2;
    }
    return 1;
  }
  
  public int getCapabilitiesForMonitoringType(int paramInt)
  {
    switch (this.mSupportedMonitorTypes[paramInt])
    {
    }
    for (;;)
    {
      return 0;
      switch (paramInt)
      {
      }
    }
    return 1;
    if (this.mVersion >= 2) {
      return this.mCapabilities;
    }
    return 1;
  }
  
  int getMonitoringResolutionLevel(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 1;
    case 0: 
      return 3;
    }
    return 3;
  }
  
  public int[] getMonitoringTypes()
  {
    int j;
    synchronized (this.mSupportedMonitorTypes)
    {
      if (this.mSupportedMonitorTypes[0] != 2) {}
      for (int i = 1;; i = 0)
      {
        j = this.mSupportedMonitorTypes[1];
        if (j == 2) {
          break;
        }
        j = 1;
        if (i == 0) {
          break label80;
        }
        if (j == 0) {
          break label72;
        }
        return new int[] { 0, 1 };
      }
      j = 0;
    }
    label72:
    return new int[] { 0 };
    label80:
    if (j != 0) {
      return new int[] { 1 };
    }
    return new int[0];
  }
  
  public int getStatusOfMonitoringType(int paramInt)
  {
    synchronized (this.mSupportedMonitorTypes)
    {
      if ((paramInt >= this.mSupportedMonitorTypes.length) || (paramInt < 0)) {
        throw new IllegalArgumentException("Unknown monitoring type");
      }
    }
    paramInt = this.mSupportedMonitorTypes[paramInt];
    return paramInt;
  }
  
  public void onCapabilities(int paramInt)
  {
    this.mCapabilities = paramInt;
    updateFusedHardwareAvailability();
  }
  
  public boolean pauseGeofence(int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.d("GeofenceHardwareImpl", "Pause Geofence: GeofenceId: " + paramInt1);
    }
    synchronized (this.mGeofences)
    {
      if (this.mGeofences.get(paramInt1) == null) {
        throw new IllegalArgumentException("Geofence " + paramInt1 + " not registered.");
      }
    }
    boolean bool;
    switch (paramInt2)
    {
    default: 
      bool = false;
    }
    for (;;)
    {
      if (DEBUG) {
        Log.d("GeofenceHardwareImpl", "pauseGeofence: Result is: " + bool);
      }
      return bool;
      if (this.mGpsService == null) {
        return false;
      }
      try
      {
        bool = this.mGpsService.pauseHardwareGeofence(paramInt1);
      }
      catch (RemoteException localRemoteException1)
      {
        Log.e("GeofenceHardwareImpl", "PauseGeofence: Remote Exception calling LocationManagerService");
        bool = false;
      }
      continue;
      if (this.mFusedService == null) {
        return false;
      }
      try
      {
        this.mFusedService.pauseMonitoringGeofence(paramInt1);
        bool = true;
      }
      catch (RemoteException localRemoteException2)
      {
        Log.e("GeofenceHardwareImpl", "PauseGeofence: RemoteException calling LocationManagerService");
        bool = false;
      }
    }
  }
  
  public boolean registerForMonitorStateChangeCallback(int paramInt, IGeofenceHardwareMonitorCallback paramIGeofenceHardwareMonitorCallback)
  {
    Message localMessage = this.mReaperHandler.obtainMessage(2, paramIGeofenceHardwareMonitorCallback);
    localMessage.arg1 = paramInt;
    this.mReaperHandler.sendMessage(localMessage);
    paramIGeofenceHardwareMonitorCallback = this.mCallbacksHandler.obtainMessage(2, paramIGeofenceHardwareMonitorCallback);
    paramIGeofenceHardwareMonitorCallback.arg1 = paramInt;
    this.mCallbacksHandler.sendMessage(paramIGeofenceHardwareMonitorCallback);
    return true;
  }
  
  public boolean removeGeofence(int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.d("GeofenceHardwareImpl", "Remove Geofence: GeofenceId: " + paramInt1);
    }
    synchronized (this.mGeofences)
    {
      if (this.mGeofences.get(paramInt1) == null) {
        throw new IllegalArgumentException("Geofence " + paramInt1 + " not registered.");
      }
    }
    boolean bool;
    switch (paramInt2)
    {
    default: 
      bool = false;
    }
    for (;;)
    {
      if (DEBUG) {
        Log.d("GeofenceHardwareImpl", "removeGeofence: Result is: " + bool);
      }
      return bool;
      if (this.mGpsService == null) {
        return false;
      }
      try
      {
        bool = this.mGpsService.removeHardwareGeofence(paramInt1);
      }
      catch (RemoteException localRemoteException1)
      {
        Log.e("GeofenceHardwareImpl", "RemoveGeofence: Remote Exception calling LocationManagerService");
        bool = false;
      }
      continue;
      if (this.mFusedService == null) {
        return false;
      }
      try
      {
        this.mFusedService.removeGeofences(new int[] { paramInt1 });
        bool = true;
      }
      catch (RemoteException localRemoteException2)
      {
        Log.e("GeofenceHardwareImpl", "RemoveGeofence: RemoteException calling LocationManagerService");
        bool = false;
      }
    }
  }
  
  public void reportGeofenceAddStatus(int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.d("GeofenceHardwareImpl", "AddCallback| id:" + paramInt1 + ", status:" + paramInt2);
    }
    reportGeofenceOperationStatus(2, paramInt1, paramInt2);
  }
  
  public void reportGeofenceMonitorStatus(int paramInt1, int paramInt2, Location paramLocation, int paramInt3)
  {
    setMonitorAvailability(paramInt1, paramInt2);
    acquireWakeLock();
    paramLocation = new GeofenceHardwareMonitorEvent(paramInt1, paramInt2, paramInt3, paramLocation);
    this.mCallbacksHandler.obtainMessage(1, paramLocation).sendToTarget();
  }
  
  public void reportGeofencePauseStatus(int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.d("GeofenceHardwareImpl", "PauseCallbac| id:" + paramInt1 + ", status" + paramInt2);
    }
    reportGeofenceOperationStatus(4, paramInt1, paramInt2);
  }
  
  public void reportGeofenceRemoveStatus(int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.d("GeofenceHardwareImpl", "RemoveCallback| id:" + paramInt1 + ", status:" + paramInt2);
    }
    reportGeofenceOperationStatus(3, paramInt1, paramInt2);
  }
  
  public void reportGeofenceResumeStatus(int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.d("GeofenceHardwareImpl", "ResumeCallback| id:" + paramInt1 + ", status:" + paramInt2);
    }
    reportGeofenceOperationStatus(5, paramInt1, paramInt2);
  }
  
  public void reportGeofenceTransition(int paramInt1, Location paramLocation, int paramInt2, long paramLong, int paramInt3, int paramInt4)
  {
    if (paramLocation == null)
    {
      Log.e("GeofenceHardwareImpl", String.format("Invalid Geofence Transition: location=null", new Object[0]));
      return;
    }
    if (DEBUG) {
      Log.d("GeofenceHardwareImpl", "GeofenceTransition| " + paramLocation + ", transition:" + paramInt2 + ", transitionTimestamp:" + paramLong + ", monitoringType:" + paramInt3 + ", sourcesUsed:" + paramInt4);
    }
    paramLocation = new GeofenceTransition(paramInt1, paramInt2, paramLong, paramLocation, paramInt3, paramInt4);
    acquireWakeLock();
    this.mGeofenceHandler.obtainMessage(1, paramLocation).sendToTarget();
  }
  
  public boolean resumeGeofence(int paramInt1, int paramInt2, int paramInt3)
  {
    if (DEBUG) {
      Log.d("GeofenceHardwareImpl", "Resume Geofence: GeofenceId: " + paramInt1);
    }
    synchronized (this.mGeofences)
    {
      if (this.mGeofences.get(paramInt1) == null) {
        throw new IllegalArgumentException("Geofence " + paramInt1 + " not registered.");
      }
    }
    boolean bool;
    switch (paramInt2)
    {
    default: 
      bool = false;
    }
    for (;;)
    {
      if (DEBUG) {
        Log.d("GeofenceHardwareImpl", "resumeGeofence: Result is: " + bool);
      }
      return bool;
      if (this.mGpsService == null) {
        return false;
      }
      try
      {
        bool = this.mGpsService.resumeHardwareGeofence(paramInt1, paramInt3);
      }
      catch (RemoteException localRemoteException1)
      {
        Log.e("GeofenceHardwareImpl", "ResumeGeofence: Remote Exception calling LocationManagerService");
        bool = false;
      }
      continue;
      if (this.mFusedService == null) {
        return false;
      }
      try
      {
        this.mFusedService.resumeMonitoringGeofence(paramInt1, paramInt3);
        bool = true;
      }
      catch (RemoteException localRemoteException2)
      {
        Log.e("GeofenceHardwareImpl", "ResumeGeofence: RemoteException calling LocationManagerService");
        bool = false;
      }
    }
  }
  
  public void setFusedGeofenceHardware(IFusedGeofenceHardware paramIFusedGeofenceHardware)
  {
    if (this.mFusedService == null)
    {
      this.mFusedService = paramIFusedGeofenceHardware;
      updateFusedHardwareAvailability();
      return;
    }
    if (paramIFusedGeofenceHardware == null)
    {
      this.mFusedService = null;
      Log.w("GeofenceHardwareImpl", "Fused Geofence Hardware service seems to have crashed");
      return;
    }
    Log.e("GeofenceHardwareImpl", "Error: FusedService being set again");
  }
  
  public void setGpsHardwareGeofence(IGpsGeofenceHardware paramIGpsGeofenceHardware)
  {
    if (this.mGpsService == null)
    {
      this.mGpsService = paramIGpsGeofenceHardware;
      updateGpsHardwareAvailability();
      return;
    }
    if (paramIGpsGeofenceHardware == null)
    {
      this.mGpsService = null;
      Log.w("GeofenceHardwareImpl", "GPS Geofence Hardware service seems to have crashed");
      return;
    }
    Log.e("GeofenceHardwareImpl", "Error: GpsService being set again.");
  }
  
  public void setVersion(int paramInt)
  {
    this.mVersion = paramInt;
    updateFusedHardwareAvailability();
  }
  
  public boolean unregisterForMonitorStateChangeCallback(int paramInt, IGeofenceHardwareMonitorCallback paramIGeofenceHardwareMonitorCallback)
  {
    paramIGeofenceHardwareMonitorCallback = this.mCallbacksHandler.obtainMessage(3, paramIGeofenceHardwareMonitorCallback);
    paramIGeofenceHardwareMonitorCallback.arg1 = paramInt;
    this.mCallbacksHandler.sendMessage(paramIGeofenceHardwareMonitorCallback);
    return true;
  }
  
  private class GeofenceTransition
  {
    private int mGeofenceId;
    private Location mLocation;
    private int mMonitoringType;
    private int mSourcesUsed;
    private long mTimestamp;
    private int mTransition;
    
    GeofenceTransition(int paramInt1, int paramInt2, long paramLong, Location paramLocation, int paramInt3, int paramInt4)
    {
      this.mGeofenceId = paramInt1;
      this.mTransition = paramInt2;
      this.mTimestamp = paramLong;
      this.mLocation = paramLocation;
      this.mMonitoringType = paramInt3;
      this.mSourcesUsed = paramInt4;
    }
  }
  
  class Reaper
    implements IBinder.DeathRecipient
  {
    private IGeofenceHardwareCallback mCallback;
    private IGeofenceHardwareMonitorCallback mMonitorCallback;
    private int mMonitoringType;
    
    Reaper(IGeofenceHardwareCallback paramIGeofenceHardwareCallback, int paramInt)
    {
      this.mCallback = paramIGeofenceHardwareCallback;
      this.mMonitoringType = paramInt;
    }
    
    Reaper(IGeofenceHardwareMonitorCallback paramIGeofenceHardwareMonitorCallback, int paramInt)
    {
      this.mMonitorCallback = paramIGeofenceHardwareMonitorCallback;
      this.mMonitoringType = paramInt;
    }
    
    private boolean binderEquals(IInterface paramIInterface1, IInterface paramIInterface2)
    {
      if (paramIInterface1 == null) {
        return paramIInterface2 == null;
      }
      if (paramIInterface2 == null) {}
      while (paramIInterface1.asBinder() != paramIInterface2.asBinder()) {
        return false;
      }
      return true;
    }
    
    private boolean callbackEquals(IGeofenceHardwareCallback paramIGeofenceHardwareCallback)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.mCallback != null)
      {
        bool1 = bool2;
        if (this.mCallback.asBinder() == paramIGeofenceHardwareCallback.asBinder()) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    private boolean unlinkToDeath()
    {
      if (this.mMonitorCallback != null) {
        return this.mMonitorCallback.asBinder().unlinkToDeath(this, 0);
      }
      if (this.mCallback != null) {
        return this.mCallback.asBinder().unlinkToDeath(this, 0);
      }
      return true;
    }
    
    public void binderDied()
    {
      Message localMessage;
      if (this.mCallback != null)
      {
        localMessage = GeofenceHardwareImpl.-get3(GeofenceHardwareImpl.this).obtainMessage(6, this.mCallback);
        localMessage.arg1 = this.mMonitoringType;
        GeofenceHardwareImpl.-get3(GeofenceHardwareImpl.this).sendMessage(localMessage);
      }
      for (;;)
      {
        localMessage = GeofenceHardwareImpl.-get5(GeofenceHardwareImpl.this).obtainMessage(3, this);
        GeofenceHardwareImpl.-get5(GeofenceHardwareImpl.this).sendMessage(localMessage);
        return;
        if (this.mMonitorCallback != null)
        {
          localMessage = GeofenceHardwareImpl.-get2(GeofenceHardwareImpl.this).obtainMessage(4, this.mMonitorCallback);
          localMessage.arg1 = this.mMonitoringType;
          GeofenceHardwareImpl.-get2(GeofenceHardwareImpl.this).sendMessage(localMessage);
        }
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      if (paramObject == this) {
        return true;
      }
      paramObject = (Reaper)paramObject;
      if ((binderEquals(((Reaper)paramObject).mCallback, this.mCallback)) && (binderEquals(((Reaper)paramObject).mMonitorCallback, this.mMonitorCallback))) {
        return ((Reaper)paramObject).mMonitoringType == this.mMonitoringType;
      }
      return false;
    }
    
    public int hashCode()
    {
      int j = 0;
      if (this.mCallback != null) {}
      for (int i = this.mCallback.asBinder().hashCode();; i = 0)
      {
        if (this.mMonitorCallback != null) {
          j = this.mMonitorCallback.asBinder().hashCode();
        }
        return ((i + 527) * 31 + j) * 31 + this.mMonitoringType;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/GeofenceHardwareImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */