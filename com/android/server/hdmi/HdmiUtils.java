package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.util.Slog;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class HdmiUtils
{
  private static final int[] ADDRESS_TO_TYPE = { 0, 1, 1, 3, 4, 5, 3, 3, 4, 1, 3, 4, 2, 2, 0 };
  private static final String[] DEFAULT_NAMES = { "TV", "Recorder_1", "Recorder_2", "Tuner_1", "Playback_1", "AudioSystem", "Tuner_2", "Tuner_3", "Playback_2", "Recorder_3", "Tuner_4", "Playback_3", "Reserved_1", "Reserved_2", "Secondary_TV" };
  
  static List<Integer> asImmutableList(int[] paramArrayOfInt)
  {
    ArrayList localArrayList = new ArrayList(paramArrayOfInt.length);
    int i = 0;
    int j = paramArrayOfInt.length;
    while (i < j)
    {
      localArrayList.add(Integer.valueOf(paramArrayOfInt[i]));
      i += 1;
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  static boolean checkCommandSource(HdmiCecMessage paramHdmiCecMessage, int paramInt, String paramString)
  {
    int i = paramHdmiCecMessage.getSource();
    if (i != paramInt)
    {
      Slog.w(paramString, "Invalid source [Expected:" + paramInt + ", Actual:" + i + "]");
      return false;
    }
    return true;
  }
  
  static HdmiDeviceInfo cloneHdmiDeviceInfo(HdmiDeviceInfo paramHdmiDeviceInfo, int paramInt)
  {
    return new HdmiDeviceInfo(paramHdmiDeviceInfo.getLogicalAddress(), paramHdmiDeviceInfo.getPhysicalAddress(), paramHdmiDeviceInfo.getPortId(), paramHdmiDeviceInfo.getDeviceType(), paramHdmiDeviceInfo.getVendorId(), paramHdmiDeviceInfo.getDisplayName(), paramInt);
  }
  
  static String getDefaultDeviceName(int paramInt)
  {
    if (isValidAddress(paramInt)) {
      return DEFAULT_NAMES[paramInt];
    }
    return "";
  }
  
  static int getTypeFromAddress(int paramInt)
  {
    if (isValidAddress(paramInt)) {
      return ADDRESS_TO_TYPE[paramInt];
    }
    return -1;
  }
  
  static boolean isAffectingActiveRoutingPath(int paramInt1, int paramInt2)
  {
    int i = 0;
    int j;
    for (;;)
    {
      j = paramInt2;
      if (i <= 12)
      {
        if ((paramInt2 >> i & 0xF) != 0) {
          j = paramInt2 & 65520 << i;
        }
      }
      else
      {
        if (j != 0) {
          break;
        }
        return true;
      }
      i += 4;
    }
    return isInActiveRoutingPath(paramInt1, j);
  }
  
  static boolean isInActiveRoutingPath(int paramInt1, int paramInt2)
  {
    int i = 12;
    for (;;)
    {
      int j;
      if (i >= 0)
      {
        j = paramInt1 >> i & 0xF;
        if (j != 0) {
          break label20;
        }
      }
      label20:
      int k;
      do
      {
        return true;
        k = paramInt2 >> i & 0xF;
      } while (k == 0);
      if (j != k) {
        return false;
      }
      i -= 4;
    }
  }
  
  static boolean isValidAddress(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt <= 14) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  static int languageToInt(String paramString)
  {
    paramString = paramString.toLowerCase();
    return (paramString.charAt(0) & 0xFF) << '\020' | (paramString.charAt(1) & 0xFF) << '\b' | paramString.charAt(2) & 0xFF;
  }
  
  static <T> List<T> mergeToUnmodifiableList(List<T> paramList1, List<T> paramList2)
  {
    if ((paramList1.isEmpty()) && (paramList2.isEmpty())) {
      return Collections.emptyList();
    }
    if (paramList1.isEmpty()) {
      return Collections.unmodifiableList(paramList2);
    }
    if (paramList2.isEmpty()) {
      return Collections.unmodifiableList(paramList1);
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(paramList1);
    localArrayList.addAll(paramList2);
    return Collections.unmodifiableList(localArrayList);
  }
  
  static boolean parseCommandParamSystemAudioStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return paramHdmiCecMessage.getParams()[0] == 1;
  }
  
  static <T> List<T> sparseArrayToList(SparseArray<T> paramSparseArray)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramSparseArray.size())
    {
      localArrayList.add(paramSparseArray.valueAt(i));
      i += 1;
    }
    return localArrayList;
  }
  
  static int threeBytesToInt(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] & 0xFF) << 16 | (paramArrayOfByte[1] & 0xFF) << 8 | paramArrayOfByte[2] & 0xFF;
  }
  
  static int twoBytesToInt(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] & 0xFF) << 8 | paramArrayOfByte[1] & 0xFF;
  }
  
  static int twoBytesToInt(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF;
  }
  
  static void verifyAddressType(int paramInt1, int paramInt2)
  {
    paramInt1 = getTypeFromAddress(paramInt1);
    if (paramInt1 != paramInt2) {
      throw new IllegalArgumentException("Device type missmatch:[Expected:" + paramInt2 + ", Actual:" + paramInt1);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */