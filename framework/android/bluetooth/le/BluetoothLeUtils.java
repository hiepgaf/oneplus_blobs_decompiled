package android.bluetooth.le;

import android.bluetooth.BluetoothAdapter;
import android.util.SparseArray;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class BluetoothLeUtils
{
  static void checkAdapterStateOn(BluetoothAdapter paramBluetoothAdapter)
  {
    if ((paramBluetoothAdapter != null) && (paramBluetoothAdapter.isLeEnabled())) {
      return;
    }
    throw new IllegalStateException("BT Adapter is not turned ON");
  }
  
  static boolean equals(SparseArray<byte[]> paramSparseArray1, SparseArray<byte[]> paramSparseArray2)
  {
    if (paramSparseArray1 == paramSparseArray2) {
      return true;
    }
    if ((paramSparseArray1 == null) || (paramSparseArray2 == null)) {
      return false;
    }
    if (paramSparseArray1.size() != paramSparseArray2.size()) {
      return false;
    }
    int i = 0;
    while (i < paramSparseArray1.size()) {
      if ((paramSparseArray1.keyAt(i) == paramSparseArray2.keyAt(i)) && (Arrays.equals((byte[])paramSparseArray1.valueAt(i), (byte[])paramSparseArray2.valueAt(i)))) {
        i += 1;
      } else {
        return false;
      }
    }
    return true;
  }
  
  static <T> boolean equals(Map<T, byte[]> paramMap1, Map<T, byte[]> paramMap2)
  {
    if (paramMap1 == paramMap2) {
      return true;
    }
    if ((paramMap1 == null) || (paramMap2 == null)) {
      return false;
    }
    if (paramMap1.size() != paramMap2.size()) {
      return false;
    }
    Object localObject1 = paramMap1.keySet();
    if (!((Set)localObject1).equals(paramMap2.keySet())) {
      return false;
    }
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = ((Iterator)localObject1).next();
      if (!Objects.deepEquals(paramMap1.get(localObject2), paramMap2.get(localObject2))) {
        return false;
      }
    }
    return true;
  }
  
  static String toString(SparseArray<byte[]> paramSparseArray)
  {
    if (paramSparseArray == null) {
      return "null";
    }
    if (paramSparseArray.size() == 0) {
      return "{}";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('{');
    int i = 0;
    while (i < paramSparseArray.size())
    {
      localStringBuilder.append(paramSparseArray.keyAt(i)).append("=").append(Arrays.toString((byte[])paramSparseArray.valueAt(i)));
      i += 1;
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  static <T> String toString(Map<T, byte[]> paramMap)
  {
    if (paramMap == null) {
      return "null";
    }
    if (paramMap.isEmpty()) {
      return "{}";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('{');
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = ((Map.Entry)localIterator.next()).getKey();
      localStringBuilder.append(localObject).append("=").append(Arrays.toString((byte[])paramMap.get(localObject)));
      if (localIterator.hasNext()) {
        localStringBuilder.append(", ");
      }
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/BluetoothLeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */