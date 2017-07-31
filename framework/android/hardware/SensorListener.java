package android.hardware;

@Deprecated
public abstract interface SensorListener
{
  public abstract void onAccuracyChanged(int paramInt1, int paramInt2);
  
  public abstract void onSensorChanged(int paramInt, float[] paramArrayOfFloat);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/SensorListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */