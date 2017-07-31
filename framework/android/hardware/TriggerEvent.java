package android.hardware;

public final class TriggerEvent
{
  public Sensor sensor;
  public long timestamp;
  public final float[] values;
  
  TriggerEvent(int paramInt)
  {
    this.values = new float[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/TriggerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */