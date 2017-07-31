package android.hardware;

public class SensorEvent
{
  public int accuracy;
  public Sensor sensor;
  public long timestamp;
  public final float[] values;
  
  SensorEvent(int paramInt)
  {
    this.values = new float[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/SensorEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */