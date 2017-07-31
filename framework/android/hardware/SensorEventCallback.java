package android.hardware;

public abstract class SensorEventCallback
  implements SensorEventListener2
{
  public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
  
  public void onFlushCompleted(Sensor paramSensor) {}
  
  public void onSensorAdditionalInfo(SensorAdditionalInfo paramSensorAdditionalInfo) {}
  
  public void onSensorChanged(SensorEvent paramSensorEvent) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/SensorEventCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */