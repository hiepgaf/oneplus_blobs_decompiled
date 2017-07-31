package android.os.health;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArrayMap;
import java.util.Arrays;
import java.util.Map;

public class HealthStats
{
  private String mDataType;
  private int[] mMeasurementKeys;
  private long[] mMeasurementValues;
  private int[] mMeasurementsKeys;
  private ArrayMap<String, Long>[] mMeasurementsValues;
  private int[] mStatsKeys;
  private ArrayMap<String, HealthStats>[] mStatsValues;
  private int[] mTimerCounts;
  private int[] mTimerKeys;
  private long[] mTimerTimes;
  private int[] mTimersKeys;
  private ArrayMap<String, TimerStat>[] mTimersValues;
  
  private HealthStats()
  {
    throw new RuntimeException("unsupported");
  }
  
  public HealthStats(Parcel paramParcel)
  {
    this.mDataType = paramParcel.readString();
    int j = paramParcel.readInt();
    this.mTimerKeys = new int[j];
    this.mTimerCounts = new int[j];
    this.mTimerTimes = new long[j];
    int i = 0;
    while (i < j)
    {
      this.mTimerKeys[i] = paramParcel.readInt();
      this.mTimerCounts[i] = paramParcel.readInt();
      this.mTimerTimes[i] = paramParcel.readLong();
      i += 1;
    }
    j = paramParcel.readInt();
    this.mMeasurementKeys = new int[j];
    this.mMeasurementValues = new long[j];
    i = 0;
    while (i < j)
    {
      this.mMeasurementKeys[i] = paramParcel.readInt();
      this.mMeasurementValues[i] = paramParcel.readLong();
      i += 1;
    }
    j = paramParcel.readInt();
    this.mStatsKeys = new int[j];
    this.mStatsValues = new ArrayMap[j];
    i = 0;
    while (i < j)
    {
      this.mStatsKeys[i] = paramParcel.readInt();
      this.mStatsValues[i] = createHealthStatsMap(paramParcel);
      i += 1;
    }
    j = paramParcel.readInt();
    this.mTimersKeys = new int[j];
    this.mTimersValues = new ArrayMap[j];
    i = 0;
    while (i < j)
    {
      this.mTimersKeys[i] = paramParcel.readInt();
      this.mTimersValues[i] = createParcelableMap(paramParcel, TimerStat.CREATOR);
      i += 1;
    }
    j = paramParcel.readInt();
    this.mMeasurementsKeys = new int[j];
    this.mMeasurementsValues = new ArrayMap[j];
    i = 0;
    while (i < j)
    {
      this.mMeasurementsKeys[i] = paramParcel.readInt();
      this.mMeasurementsValues[i] = createLongsMap(paramParcel);
      i += 1;
    }
  }
  
  private static ArrayMap<String, HealthStats> createHealthStatsMap(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    ArrayMap localArrayMap = new ArrayMap(j);
    int i = 0;
    while (i < j)
    {
      localArrayMap.put(paramParcel.readString(), new HealthStats(paramParcel));
      i += 1;
    }
    return localArrayMap;
  }
  
  private static ArrayMap<String, Long> createLongsMap(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    ArrayMap localArrayMap = new ArrayMap(j);
    int i = 0;
    while (i < j)
    {
      localArrayMap.put(paramParcel.readString(), Long.valueOf(paramParcel.readLong()));
      i += 1;
    }
    return localArrayMap;
  }
  
  private static <T extends Parcelable> ArrayMap<String, T> createParcelableMap(Parcel paramParcel, Parcelable.Creator<T> paramCreator)
  {
    int j = paramParcel.readInt();
    ArrayMap localArrayMap = new ArrayMap(j);
    int i = 0;
    while (i < j)
    {
      localArrayMap.put(paramParcel.readString(), (Parcelable)paramCreator.createFromParcel(paramParcel));
      i += 1;
    }
    return localArrayMap;
  }
  
  private static int getIndex(int[] paramArrayOfInt, int paramInt)
  {
    return Arrays.binarySearch(paramArrayOfInt, paramInt);
  }
  
  public String getDataType()
  {
    return this.mDataType;
  }
  
  public long getMeasurement(int paramInt)
  {
    int i = getIndex(this.mMeasurementKeys, paramInt);
    if (i < 0) {
      throw new IndexOutOfBoundsException("Bad measurement key dataType=" + this.mDataType + " key=" + paramInt);
    }
    return this.mMeasurementValues[i];
  }
  
  public int getMeasurementKeyAt(int paramInt)
  {
    return this.mMeasurementKeys[paramInt];
  }
  
  public int getMeasurementKeyCount()
  {
    return this.mMeasurementKeys.length;
  }
  
  public Map<String, Long> getMeasurements(int paramInt)
  {
    int i = getIndex(this.mMeasurementsKeys, paramInt);
    if (i < 0) {
      throw new IndexOutOfBoundsException("Bad measurements key dataType=" + this.mDataType + " key=" + paramInt);
    }
    return this.mMeasurementsValues[i];
  }
  
  public int getMeasurementsKeyAt(int paramInt)
  {
    return this.mMeasurementsKeys[paramInt];
  }
  
  public int getMeasurementsKeyCount()
  {
    return this.mMeasurementsKeys.length;
  }
  
  public Map<String, HealthStats> getStats(int paramInt)
  {
    int i = getIndex(this.mStatsKeys, paramInt);
    if (i < 0) {
      throw new IndexOutOfBoundsException("Bad stats key dataType=" + this.mDataType + " key=" + paramInt);
    }
    return this.mStatsValues[i];
  }
  
  public int getStatsKeyAt(int paramInt)
  {
    return this.mStatsKeys[paramInt];
  }
  
  public int getStatsKeyCount()
  {
    return this.mStatsKeys.length;
  }
  
  public TimerStat getTimer(int paramInt)
  {
    int i = getIndex(this.mTimerKeys, paramInt);
    if (i < 0) {
      throw new IndexOutOfBoundsException("Bad timer key dataType=" + this.mDataType + " key=" + paramInt);
    }
    return new TimerStat(this.mTimerCounts[i], this.mTimerTimes[i]);
  }
  
  public int getTimerCount(int paramInt)
  {
    int i = getIndex(this.mTimerKeys, paramInt);
    if (i < 0) {
      throw new IndexOutOfBoundsException("Bad timer key dataType=" + this.mDataType + " key=" + paramInt);
    }
    return this.mTimerCounts[i];
  }
  
  public int getTimerKeyAt(int paramInt)
  {
    return this.mTimerKeys[paramInt];
  }
  
  public int getTimerKeyCount()
  {
    return this.mTimerKeys.length;
  }
  
  public long getTimerTime(int paramInt)
  {
    int i = getIndex(this.mTimerKeys, paramInt);
    if (i < 0) {
      throw new IndexOutOfBoundsException("Bad timer key dataType=" + this.mDataType + " key=" + paramInt);
    }
    return this.mTimerTimes[i];
  }
  
  public Map<String, TimerStat> getTimers(int paramInt)
  {
    int i = getIndex(this.mTimersKeys, paramInt);
    if (i < 0) {
      throw new IndexOutOfBoundsException("Bad timers key dataType=" + this.mDataType + " key=" + paramInt);
    }
    return this.mTimersValues[i];
  }
  
  public int getTimersKeyAt(int paramInt)
  {
    return this.mTimersKeys[paramInt];
  }
  
  public int getTimersKeyCount()
  {
    return this.mTimersKeys.length;
  }
  
  public boolean hasMeasurement(int paramInt)
  {
    boolean bool = false;
    if (getIndex(this.mMeasurementKeys, paramInt) >= 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasMeasurements(int paramInt)
  {
    boolean bool = false;
    if (getIndex(this.mMeasurementsKeys, paramInt) >= 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasStats(int paramInt)
  {
    boolean bool = false;
    if (getIndex(this.mStatsKeys, paramInt) >= 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasTimer(int paramInt)
  {
    boolean bool = false;
    if (getIndex(this.mTimerKeys, paramInt) >= 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasTimers(int paramInt)
  {
    boolean bool = false;
    if (getIndex(this.mTimersKeys, paramInt) >= 0) {
      bool = true;
    }
    return bool;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/health/HealthStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */