package android.bluetooth.le;

import android.bluetooth.BluetoothUuid;
import android.os.ParcelUuid;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ScanRecord
{
  private static final int DATA_TYPE_FLAGS = 1;
  private static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 9;
  private static final int DATA_TYPE_LOCAL_NAME_SHORT = 8;
  private static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 255;
  private static final int DATA_TYPE_SERVICE_DATA = 22;
  private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 7;
  private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL = 6;
  private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 3;
  private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL = 2;
  private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE = 5;
  private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL = 4;
  private static final int DATA_TYPE_TX_POWER_LEVEL = 10;
  private static final String TAG = "ScanRecord";
  private final int mAdvertiseFlags;
  private final byte[] mBytes;
  private final String mDeviceName;
  private final SparseArray<byte[]> mManufacturerSpecificData;
  private final Map<ParcelUuid, byte[]> mServiceData;
  private final List<ParcelUuid> mServiceUuids;
  private final int mTxPowerLevel;
  
  private ScanRecord(List<ParcelUuid> paramList, SparseArray<byte[]> paramSparseArray, Map<ParcelUuid, byte[]> paramMap, int paramInt1, int paramInt2, String paramString, byte[] paramArrayOfByte)
  {
    this.mServiceUuids = paramList;
    this.mManufacturerSpecificData = paramSparseArray;
    this.mServiceData = paramMap;
    this.mDeviceName = paramString;
    this.mAdvertiseFlags = paramInt1;
    this.mTxPowerLevel = paramInt2;
    this.mBytes = paramArrayOfByte;
  }
  
  private static byte[] extractBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  public static ScanRecord parseFromBytes(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    int k = -1;
    ArrayList localArrayList2 = new ArrayList();
    Object localObject = null;
    int j = Integer.MIN_VALUE;
    SparseArray localSparseArray = new SparseArray();
    ArrayMap localArrayMap = new ArrayMap();
    int i = 0;
    int m = i;
    int n;
    label437:
    for (;;)
    {
      try
      {
        m = paramArrayOfByte.length;
        if (i >= m) {
          break label437;
        }
        m = i + 1;
        i = paramArrayOfByte[i] & 0xFF;
        if (i == 0) {
          localArrayList1 = localArrayList2;
        }
      }
      catch (Exception localException1)
      {
        ArrayList localArrayList1;
        Log.e("ScanRecord", "unable to parse scan record: " + Arrays.toString(paramArrayOfByte));
        return new ScanRecord(null, null, null, -1, Integer.MIN_VALUE, null, paramArrayOfByte);
      }
      try
      {
        if (localArrayList2.isEmpty()) {
          localArrayList1 = null;
        }
        localObject = new ScanRecord(localArrayList1, localSparseArray, localArrayMap, k, j, (String)localObject, paramArrayOfByte);
        return (ScanRecord)localObject;
      }
      catch (Exception localException2)
      {
        continue;
      }
      n = i - 1;
      i = m + 1;
      switch (paramArrayOfByte[m] & 0xFF)
      {
      case 2: 
      case 3: 
        m = i;
        parseServiceUuid(paramArrayOfByte, i, n, 2, localArrayList2);
        break;
      case 4: 
      case 5: 
        m = i;
        parseServiceUuid(paramArrayOfByte, i, n, 4, localArrayList2);
        break;
      case 6: 
      case 7: 
        m = i;
        parseServiceUuid(paramArrayOfByte, i, n, 16, localArrayList2);
        break;
      case 8: 
      case 9: 
        m = i;
        String str = new String(extractBytes(paramArrayOfByte, i, n));
        break;
      case 22: 
        m = i;
        localArrayMap.put(BluetoothUuid.parseUuidFrom(extractBytes(paramArrayOfByte, i, 2)), extractBytes(paramArrayOfByte, i + 2, n - 2));
        break;
      case 255: 
        m = i;
        localSparseArray.put(((paramArrayOfByte[(i + 1)] & 0xFF) << 8) + (paramArrayOfByte[i] & 0xFF), extractBytes(paramArrayOfByte, i + 2, n - 2));
        break;
      }
    }
    for (;;)
    {
      i += n;
      break;
      k = paramArrayOfByte[i] & 0xFF;
      continue;
      j = paramArrayOfByte[i];
    }
  }
  
  private static int parseServiceUuid(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, List<ParcelUuid> paramList)
  {
    while (paramInt2 > 0)
    {
      paramList.add(BluetoothUuid.parseUuidFrom(extractBytes(paramArrayOfByte, paramInt1, paramInt3)));
      paramInt2 -= paramInt3;
      paramInt1 += paramInt3;
    }
    return paramInt1;
  }
  
  public int getAdvertiseFlags()
  {
    return this.mAdvertiseFlags;
  }
  
  public byte[] getBytes()
  {
    return this.mBytes;
  }
  
  public String getDeviceName()
  {
    return this.mDeviceName;
  }
  
  public SparseArray<byte[]> getManufacturerSpecificData()
  {
    return this.mManufacturerSpecificData;
  }
  
  public byte[] getManufacturerSpecificData(int paramInt)
  {
    return (byte[])this.mManufacturerSpecificData.get(paramInt);
  }
  
  public Map<ParcelUuid, byte[]> getServiceData()
  {
    return this.mServiceData;
  }
  
  public byte[] getServiceData(ParcelUuid paramParcelUuid)
  {
    if (paramParcelUuid == null) {
      return null;
    }
    return (byte[])this.mServiceData.get(paramParcelUuid);
  }
  
  public List<ParcelUuid> getServiceUuids()
  {
    return this.mServiceUuids;
  }
  
  public int getTxPowerLevel()
  {
    return this.mTxPowerLevel;
  }
  
  public String toString()
  {
    return "ScanRecord [mAdvertiseFlags=" + this.mAdvertiseFlags + ", mServiceUuids=" + this.mServiceUuids + ", mManufacturerSpecificData=" + BluetoothLeUtils.toString(this.mManufacturerSpecificData) + ", mServiceData=" + BluetoothLeUtils.toString(this.mServiceData) + ", mTxPowerLevel=" + this.mTxPowerLevel + ", mDeviceName=" + this.mDeviceName + "]";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/ScanRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */