package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.UserHandle;
import android.util.Slog;

public class SensorNotificationService
  extends SystemService
  implements SensorEventListener
{
  private static final boolean DBG = true;
  private static final String TAG = "SensorNotificationService";
  private Context mContext;
  private Sensor mMetaSensor;
  private SensorManager mSensorManager;
  
  public SensorNotificationService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
  }
  
  private void broadcastDynamicSensorChanged()
  {
    Intent localIntent = new Intent("android.intent.action.DYNAMIC_SENSOR_CHANGED");
    localIntent.setFlags(1073741824);
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL);
    Slog.d("SensorNotificationService", "DYNS sent dynamic sensor broadcast");
  }
  
  public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 600)
    {
      this.mSensorManager = ((SensorManager)this.mContext.getSystemService("sensor"));
      this.mMetaSensor = this.mSensorManager.getDefaultSensor(32);
      if (this.mMetaSensor == null) {
        Slog.d("SensorNotificationService", "Cannot obtain dynamic meta sensor, not supported.");
      }
    }
    else
    {
      return;
    }
    this.mSensorManager.registerListener(this, this.mMetaSensor, 0);
  }
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    if (paramSensorEvent.sensor == this.mMetaSensor) {
      broadcastDynamicSensorChanged();
    }
  }
  
  public void onStart()
  {
    LocalServices.addService(SensorNotificationService.class, this);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/SensorNotificationService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */