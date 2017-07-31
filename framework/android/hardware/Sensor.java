package android.hardware;

import java.util.UUID;

public final class Sensor
{
  private static final int ADDITIONAL_INFO_MASK = 64;
  private static final int ADDITIONAL_INFO_SHIFT = 6;
  private static final int DATA_INJECTION_MASK = 16;
  private static final int DATA_INJECTION_SHIFT = 4;
  private static final int DYNAMIC_SENSOR_MASK = 32;
  private static final int DYNAMIC_SENSOR_SHIFT = 5;
  public static final int ONEPLUS_TYPE_PICK_UP = 33171026;
  public static final int REPORTING_MODE_CONTINUOUS = 0;
  private static final int REPORTING_MODE_MASK = 14;
  public static final int REPORTING_MODE_ONE_SHOT = 2;
  public static final int REPORTING_MODE_ON_CHANGE = 1;
  private static final int REPORTING_MODE_SHIFT = 1;
  public static final int REPORTING_MODE_SPECIAL_TRIGGER = 3;
  private static final int SENSOR_FLAG_WAKE_UP_SENSOR = 1;
  public static final String SENSOR_STRING_TYPE_TILT_DETECTOR = "android.sensor.tilt_detector";
  public static final String STRING_TYPE_ACCELEROMETER = "android.sensor.accelerometer";
  public static final String STRING_TYPE_AMBIENT_TEMPERATURE = "android.sensor.ambient_temperature";
  public static final String STRING_TYPE_DEVICE_ORIENTATION = "android.sensor.device_orientation";
  public static final String STRING_TYPE_DYNAMIC_SENSOR_META = "android.sensor.dynamic_sensor_meta";
  public static final String STRING_TYPE_GAME_ROTATION_VECTOR = "android.sensor.game_rotation_vector";
  public static final String STRING_TYPE_GEOMAGNETIC_ROTATION_VECTOR = "android.sensor.geomagnetic_rotation_vector";
  public static final String STRING_TYPE_GLANCE_GESTURE = "android.sensor.glance_gesture";
  public static final String STRING_TYPE_GRAVITY = "android.sensor.gravity";
  public static final String STRING_TYPE_GYROSCOPE = "android.sensor.gyroscope";
  public static final String STRING_TYPE_GYROSCOPE_UNCALIBRATED = "android.sensor.gyroscope_uncalibrated";
  public static final String STRING_TYPE_HEART_BEAT = "android.sensor.heart_beat";
  public static final String STRING_TYPE_HEART_RATE = "android.sensor.heart_rate";
  public static final String STRING_TYPE_LIGHT = "android.sensor.light";
  public static final String STRING_TYPE_LINEAR_ACCELERATION = "android.sensor.linear_acceleration";
  public static final String STRING_TYPE_MAGNETIC_FIELD = "android.sensor.magnetic_field";
  public static final String STRING_TYPE_MAGNETIC_FIELD_UNCALIBRATED = "android.sensor.magnetic_field_uncalibrated";
  public static final String STRING_TYPE_MOTION_DETECT = "android.sensor.motion_detect";
  @Deprecated
  public static final String STRING_TYPE_ORIENTATION = "android.sensor.orientation";
  public static final String STRING_TYPE_PICK_UP_GESTURE = "android.sensor.pick_up_gesture";
  public static final String STRING_TYPE_POSE_6DOF = "android.sensor.pose_6dof";
  public static final String STRING_TYPE_PRESSURE = "android.sensor.pressure";
  public static final String STRING_TYPE_PROXIMITY = "android.sensor.proximity";
  public static final String STRING_TYPE_RELATIVE_HUMIDITY = "android.sensor.relative_humidity";
  public static final String STRING_TYPE_ROTATION_VECTOR = "android.sensor.rotation_vector";
  public static final String STRING_TYPE_SIGNIFICANT_MOTION = "android.sensor.significant_motion";
  public static final String STRING_TYPE_STATIONARY_DETECT = "android.sensor.stationary_detect";
  public static final String STRING_TYPE_STEP_COUNTER = "android.sensor.step_counter";
  public static final String STRING_TYPE_STEP_DETECTOR = "android.sensor.step_detector";
  @Deprecated
  public static final String STRING_TYPE_TEMPERATURE = "android.sensor.temperature";
  public static final String STRING_TYPE_WAKE_GESTURE = "android.sensor.wake_gesture";
  public static final String STRING_TYPE_WRIST_TILT_GESTURE = "android.sensor.wrist_tilt_gesture";
  public static final int TYPE_ACCELEROMETER = 1;
  public static final int TYPE_ALL = -1;
  public static final int TYPE_AMBIENT_TEMPERATURE = 13;
  public static final int TYPE_DEVICE_ORIENTATION = 27;
  public static final int TYPE_DEVICE_PRIVATE_BASE = 65536;
  public static final int TYPE_DYNAMIC_SENSOR_META = 32;
  public static final int TYPE_GAME_ROTATION_VECTOR = 15;
  public static final int TYPE_GEOMAGNETIC_ROTATION_VECTOR = 20;
  public static final int TYPE_GLANCE_GESTURE = 24;
  public static final int TYPE_GRAVITY = 9;
  public static final int TYPE_GYROSCOPE = 4;
  public static final int TYPE_GYROSCOPE_UNCALIBRATED = 16;
  public static final int TYPE_HEART_BEAT = 31;
  public static final int TYPE_HEART_RATE = 21;
  public static final int TYPE_LIGHT = 5;
  public static final int TYPE_LINEAR_ACCELERATION = 10;
  public static final int TYPE_MAGNETIC_FIELD = 2;
  public static final int TYPE_MAGNETIC_FIELD_UNCALIBRATED = 14;
  public static final int TYPE_MOTION_DETECT = 30;
  @Deprecated
  public static final int TYPE_ORIENTATION = 3;
  public static final int TYPE_PICK_UP_GESTURE = 25;
  public static final int TYPE_POCKET = 33171025;
  public static final int TYPE_POSE_6DOF = 28;
  public static final int TYPE_PRESSURE = 6;
  public static final int TYPE_PROXIMITY = 8;
  public static final int TYPE_RELATIVE_HUMIDITY = 12;
  public static final int TYPE_ROTATION_VECTOR = 11;
  public static final int TYPE_SIGNIFICANT_MOTION = 17;
  public static final int TYPE_STATIONARY_DETECT = 29;
  public static final int TYPE_STEP_COUNTER = 19;
  public static final int TYPE_STEP_DETECTOR = 18;
  @Deprecated
  public static final int TYPE_TEMPERATURE = 7;
  public static final int TYPE_TILT_DETECTOR = 22;
  public static final int TYPE_WAKE_GESTURE = 23;
  public static final int TYPE_WRIST_TILT_GESTURE = 26;
  private static final int[] sSensorReportingModes = { 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 6, 4, 6, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 1, 16, 1, 1, 1, 2 };
  private int mFifoMaxEventCount;
  private int mFifoReservedEventCount;
  private int mFlags;
  private int mHandle;
  private int mId;
  private int mMaxDelay;
  private float mMaxRange;
  private int mMinDelay;
  private String mName;
  private float mPower;
  private String mRequiredPermission;
  private float mResolution;
  private String mStringType;
  private int mType;
  private String mVendor;
  private int mVersion;
  
  static int getMaxLengthValuesArray(Sensor paramSensor, int paramInt)
  {
    if ((paramSensor.mType == 11) && (paramInt <= 17)) {
      return 3;
    }
    paramInt = paramSensor.mType;
    if (paramInt >= sSensorReportingModes.length) {
      return 16;
    }
    return sSensorReportingModes[paramInt];
  }
  
  private boolean setType(int paramInt)
  {
    this.mType = paramInt;
    switch (this.mType)
    {
    case 26: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
    default: 
      return false;
    case 1: 
      this.mStringType = "android.sensor.accelerometer";
      return true;
    case 13: 
      this.mStringType = "android.sensor.ambient_temperature";
      return true;
    case 15: 
      this.mStringType = "android.sensor.game_rotation_vector";
      return true;
    case 20: 
      this.mStringType = "android.sensor.geomagnetic_rotation_vector";
      return true;
    case 24: 
      this.mStringType = "android.sensor.glance_gesture";
      return true;
    case 9: 
      this.mStringType = "android.sensor.gravity";
      return true;
    case 4: 
      this.mStringType = "android.sensor.gyroscope";
      return true;
    case 16: 
      this.mStringType = "android.sensor.gyroscope_uncalibrated";
      return true;
    case 21: 
      this.mStringType = "android.sensor.heart_rate";
      return true;
    case 5: 
      this.mStringType = "android.sensor.light";
      return true;
    case 10: 
      this.mStringType = "android.sensor.linear_acceleration";
      return true;
    case 2: 
      this.mStringType = "android.sensor.magnetic_field";
      return true;
    case 14: 
      this.mStringType = "android.sensor.magnetic_field_uncalibrated";
      return true;
    case 25: 
      this.mStringType = "android.sensor.pick_up_gesture";
      return true;
    case 6: 
      this.mStringType = "android.sensor.pressure";
      return true;
    case 8: 
      this.mStringType = "android.sensor.proximity";
      return true;
    case 12: 
      this.mStringType = "android.sensor.relative_humidity";
      return true;
    case 11: 
      this.mStringType = "android.sensor.rotation_vector";
      return true;
    case 17: 
      this.mStringType = "android.sensor.significant_motion";
      return true;
    case 19: 
      this.mStringType = "android.sensor.step_counter";
      return true;
    case 18: 
      this.mStringType = "android.sensor.step_detector";
      return true;
    case 22: 
      this.mStringType = "android.sensor.tilt_detector";
      return true;
    case 23: 
      this.mStringType = "android.sensor.wake_gesture";
      return true;
    case 3: 
      this.mStringType = "android.sensor.orientation";
      return true;
    case 7: 
      this.mStringType = "android.sensor.temperature";
      return true;
    case 27: 
      this.mStringType = "android.sensor.device_orientation";
      return true;
    }
    this.mStringType = "android.sensor.dynamic_sensor_meta";
    return true;
  }
  
  private void setUuid(long paramLong1, long paramLong2)
  {
    this.mId = ((int)paramLong1);
  }
  
  public int getFifoMaxEventCount()
  {
    return this.mFifoMaxEventCount;
  }
  
  public int getFifoReservedEventCount()
  {
    return this.mFifoReservedEventCount;
  }
  
  public int getHandle()
  {
    return this.mHandle;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getMaxDelay()
  {
    return this.mMaxDelay;
  }
  
  public float getMaximumRange()
  {
    return this.mMaxRange;
  }
  
  public int getMinDelay()
  {
    return this.mMinDelay;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public float getPower()
  {
    return this.mPower;
  }
  
  public int getReportingMode()
  {
    return (this.mFlags & 0xE) >> 1;
  }
  
  public String getRequiredPermission()
  {
    return this.mRequiredPermission;
  }
  
  public float getResolution()
  {
    return this.mResolution;
  }
  
  public String getStringType()
  {
    return this.mStringType;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public UUID getUuid()
  {
    throw new UnsupportedOperationException();
  }
  
  public String getVendor()
  {
    return this.mVendor;
  }
  
  public int getVersion()
  {
    return this.mVersion;
  }
  
  public boolean isAdditionalInfoSupported()
  {
    boolean bool = false;
    if ((this.mFlags & 0x40) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDataInjectionSupported()
  {
    boolean bool = false;
    if ((this.mFlags & 0x10) >> 4 != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDynamicSensor()
  {
    boolean bool = false;
    if ((this.mFlags & 0x20) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isWakeUpSensor()
  {
    boolean bool = false;
    if ((this.mFlags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  void setRange(float paramFloat1, float paramFloat2)
  {
    this.mMaxRange = paramFloat1;
    this.mResolution = paramFloat2;
  }
  
  public String toString()
  {
    return "{Sensor name=\"" + this.mName + "\", vendor=\"" + this.mVendor + "\", version=" + this.mVersion + ", type=" + this.mType + ", maxRange=" + this.mMaxRange + ", resolution=" + this.mResolution + ", power=" + this.mPower + ", minDelay=" + this.mMinDelay + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/Sensor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */