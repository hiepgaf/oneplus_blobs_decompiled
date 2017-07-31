package android.hardware;

public class SensorAdditionalInfo
{
  public static final int TYPE_FRAME_BEGIN = 0;
  public static final int TYPE_FRAME_END = 1;
  public static final int TYPE_INTERNAL_TEMPERATURE = 65537;
  public static final int TYPE_SAMPLING = 65540;
  public static final int TYPE_SENSOR_PLACEMENT = 65539;
  public static final int TYPE_UNTRACKED_DELAY = 65536;
  public static final int TYPE_VEC3_CALIBRATION = 65538;
  public final float[] floatValues;
  public final int[] intValues;
  public final Sensor sensor;
  public final int serial;
  public final int type;
  
  SensorAdditionalInfo(Sensor paramSensor, int paramInt1, int paramInt2, int[] paramArrayOfInt, float[] paramArrayOfFloat)
  {
    this.sensor = paramSensor;
    this.type = paramInt1;
    this.serial = paramInt2;
    this.intValues = paramArrayOfInt;
    this.floatValues = paramArrayOfFloat;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/SensorAdditionalInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */