package android.media.midi;

class MidiPortImpl
{
  private static final int DATA_PACKET_OVERHEAD = 9;
  public static final int MAX_PACKET_DATA_SIZE = 1015;
  public static final int MAX_PACKET_SIZE = 1024;
  public static final int PACKET_TYPE_DATA = 1;
  public static final int PACKET_TYPE_FLUSH = 2;
  private static final String TAG = "MidiPort";
  private static final int TIMESTAMP_SIZE = 8;
  
  public static int getDataOffset(byte[] paramArrayOfByte, int paramInt)
  {
    return 1;
  }
  
  public static int getDataSize(byte[] paramArrayOfByte, int paramInt)
  {
    return paramInt - 9;
  }
  
  public static long getPacketTimestamp(byte[] paramArrayOfByte, int paramInt)
  {
    long l = 0L;
    int j = 0;
    int i = paramInt;
    paramInt = j;
    while (paramInt < 8)
    {
      i -= 1;
      l = l << 8 | paramArrayOfByte[i] & 0xFF;
      paramInt += 1;
    }
    return l;
  }
  
  public static int getPacketType(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[0];
  }
  
  public static int packData(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, long paramLong, byte[] paramArrayOfByte2)
  {
    int i = paramInt2;
    if (paramInt2 > 1015) {
      i = 1015;
    }
    paramArrayOfByte2[0] = 1;
    System.arraycopy(paramArrayOfByte1, paramInt1, paramArrayOfByte2, 1, i);
    paramInt2 = 0;
    paramInt1 = i + 1;
    while (paramInt2 < 8)
    {
      paramArrayOfByte2[paramInt1] = ((byte)(int)paramLong);
      paramLong >>= 8;
      paramInt2 += 1;
      paramInt1 += 1;
    }
    return paramInt1;
  }
  
  public static int packFlush(byte[] paramArrayOfByte)
  {
    paramArrayOfByte[0] = 2;
    return 1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiPortImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */