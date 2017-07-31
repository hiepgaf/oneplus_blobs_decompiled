package android.os.health;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

public class HealthStatsWriter
{
  private final HealthKeys.Constants mConstants;
  private final boolean[] mMeasurementFields;
  private final long[] mMeasurementValues;
  private final ArrayMap<String, Long>[] mMeasurementsValues;
  private final ArrayMap<String, HealthStatsWriter>[] mStatsValues;
  private final int[] mTimerCounts;
  private final boolean[] mTimerFields;
  private final long[] mTimerTimes;
  private final ArrayMap<String, TimerStat>[] mTimersValues;
  
  public HealthStatsWriter(HealthKeys.Constants paramConstants)
  {
    this.mConstants = paramConstants;
    int i = paramConstants.getSize(0);
    this.mTimerFields = new boolean[i];
    this.mTimerCounts = new int[i];
    this.mTimerTimes = new long[i];
    i = paramConstants.getSize(1);
    this.mMeasurementFields = new boolean[i];
    this.mMeasurementValues = new long[i];
    this.mStatsValues = new ArrayMap[paramConstants.getSize(2)];
    this.mTimersValues = new ArrayMap[paramConstants.getSize(3)];
    this.mMeasurementsValues = new ArrayMap[paramConstants.getSize(4)];
  }
  
  private static int countBooleanArray(boolean[] paramArrayOfBoolean)
  {
    int j = 0;
    int m = paramArrayOfBoolean.length;
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (paramArrayOfBoolean[i] != 0) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private static <T> int countObjectArray(T[] paramArrayOfT)
  {
    int j = 0;
    int m = paramArrayOfT.length;
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (paramArrayOfT[i] != null) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private static void writeHealthStatsWriterMap(Parcel paramParcel, ArrayMap<String, HealthStatsWriter> paramArrayMap)
  {
    int j = paramArrayMap.size();
    paramParcel.writeInt(j);
    int i = 0;
    while (i < j)
    {
      paramParcel.writeString((String)paramArrayMap.keyAt(i));
      ((HealthStatsWriter)paramArrayMap.valueAt(i)).flattenToParcel(paramParcel);
      i += 1;
    }
  }
  
  private static void writeLongsMap(Parcel paramParcel, ArrayMap<String, Long> paramArrayMap)
  {
    int j = paramArrayMap.size();
    paramParcel.writeInt(j);
    int i = 0;
    while (i < j)
    {
      paramParcel.writeString((String)paramArrayMap.keyAt(i));
      paramParcel.writeLong(((Long)paramArrayMap.valueAt(i)).longValue());
      i += 1;
    }
  }
  
  private static <T extends Parcelable> void writeParcelableMap(Parcel paramParcel, ArrayMap<String, T> paramArrayMap)
  {
    int j = paramArrayMap.size();
    paramParcel.writeInt(j);
    int i = 0;
    while (i < j)
    {
      paramParcel.writeString((String)paramArrayMap.keyAt(i));
      ((Parcelable)paramArrayMap.valueAt(i)).writeToParcel(paramParcel, 0);
      i += 1;
    }
  }
  
  public void addMeasurement(int paramInt, long paramLong)
  {
    paramInt = this.mConstants.getIndex(1, paramInt);
    this.mMeasurementFields[paramInt] = true;
    this.mMeasurementValues[paramInt] = paramLong;
  }
  
  public void addMeasurements(int paramInt, String paramString, long paramLong)
  {
    paramInt = this.mConstants.getIndex(4, paramInt);
    ArrayMap localArrayMap2 = this.mMeasurementsValues[paramInt];
    ArrayMap localArrayMap1 = localArrayMap2;
    if (localArrayMap2 == null)
    {
      localArrayMap1 = new ArrayMap(1);
      this.mMeasurementsValues[paramInt] = localArrayMap1;
    }
    localArrayMap1.put(paramString, Long.valueOf(paramLong));
  }
  
  public void addStats(int paramInt, String paramString, HealthStatsWriter paramHealthStatsWriter)
  {
    paramInt = this.mConstants.getIndex(2, paramInt);
    ArrayMap localArrayMap2 = this.mStatsValues[paramInt];
    ArrayMap localArrayMap1 = localArrayMap2;
    if (localArrayMap2 == null)
    {
      localArrayMap1 = new ArrayMap(1);
      this.mStatsValues[paramInt] = localArrayMap1;
    }
    localArrayMap1.put(paramString, paramHealthStatsWriter);
  }
  
  public void addTimer(int paramInt1, int paramInt2, long paramLong)
  {
    paramInt1 = this.mConstants.getIndex(0, paramInt1);
    this.mTimerFields[paramInt1] = true;
    this.mTimerCounts[paramInt1] = paramInt2;
    this.mTimerTimes[paramInt1] = paramLong;
  }
  
  public void addTimers(int paramInt, String paramString, TimerStat paramTimerStat)
  {
    paramInt = this.mConstants.getIndex(3, paramInt);
    ArrayMap localArrayMap2 = this.mTimersValues[paramInt];
    ArrayMap localArrayMap1 = localArrayMap2;
    if (localArrayMap2 == null)
    {
      localArrayMap1 = new ArrayMap(1);
      this.mTimersValues[paramInt] = localArrayMap1;
    }
    localArrayMap1.put(paramString, paramTimerStat);
  }
  
  public void flattenToParcel(Parcel paramParcel)
  {
    paramParcel.writeString(this.mConstants.getDataType());
    paramParcel.writeInt(countBooleanArray(this.mTimerFields));
    int[] arrayOfInt = this.mConstants.getKeys(0);
    int i = 0;
    while (i < arrayOfInt.length)
    {
      if (this.mTimerFields[i] != 0)
      {
        paramParcel.writeInt(arrayOfInt[i]);
        paramParcel.writeInt(this.mTimerCounts[i]);
        paramParcel.writeLong(this.mTimerTimes[i]);
      }
      i += 1;
    }
    paramParcel.writeInt(countBooleanArray(this.mMeasurementFields));
    arrayOfInt = this.mConstants.getKeys(1);
    i = 0;
    while (i < arrayOfInt.length)
    {
      if (this.mMeasurementFields[i] != 0)
      {
        paramParcel.writeInt(arrayOfInt[i]);
        paramParcel.writeLong(this.mMeasurementValues[i]);
      }
      i += 1;
    }
    paramParcel.writeInt(countObjectArray(this.mStatsValues));
    arrayOfInt = this.mConstants.getKeys(2);
    i = 0;
    while (i < arrayOfInt.length)
    {
      if (this.mStatsValues[i] != null)
      {
        paramParcel.writeInt(arrayOfInt[i]);
        writeHealthStatsWriterMap(paramParcel, this.mStatsValues[i]);
      }
      i += 1;
    }
    paramParcel.writeInt(countObjectArray(this.mTimersValues));
    arrayOfInt = this.mConstants.getKeys(3);
    i = 0;
    while (i < arrayOfInt.length)
    {
      if (this.mTimersValues[i] != null)
      {
        paramParcel.writeInt(arrayOfInt[i]);
        writeParcelableMap(paramParcel, this.mTimersValues[i]);
      }
      i += 1;
    }
    paramParcel.writeInt(countObjectArray(this.mMeasurementsValues));
    arrayOfInt = this.mConstants.getKeys(4);
    i = 0;
    while (i < arrayOfInt.length)
    {
      if (this.mMeasurementsValues[i] != null)
      {
        paramParcel.writeInt(arrayOfInt[i]);
        writeLongsMap(paramParcel, this.mMeasurementsValues[i]);
      }
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/health/HealthStatsWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */