package android.hardware;

public abstract interface SensorEventListener
{
  public abstract void onAccuracyChanged(Sensor paramSensor, int paramInt);
  
  public abstract void onSensorChanged(SensorEvent paramSensorEvent);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/SensorEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */