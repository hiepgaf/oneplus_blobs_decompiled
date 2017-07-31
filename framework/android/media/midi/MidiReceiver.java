package android.media.midi;

import java.io.IOException;

public abstract class MidiReceiver
{
  private final int mMaxMessageSize;
  
  public MidiReceiver()
  {
    this.mMaxMessageSize = Integer.MAX_VALUE;
  }
  
  public MidiReceiver(int paramInt)
  {
    this.mMaxMessageSize = paramInt;
  }
  
  public void flush()
    throws IOException
  {
    onFlush();
  }
  
  public final int getMaxMessageSize()
  {
    return this.mMaxMessageSize;
  }
  
  public void onFlush()
    throws IOException
  {}
  
  public abstract void onSend(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong)
    throws IOException;
  
  public void send(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    send(paramArrayOfByte, paramInt1, paramInt2, 0L);
  }
  
  public void send(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    int j = getMaxMessageSize();
    if (paramInt2 > 0)
    {
      if (paramInt2 > j) {}
      for (int i = j;; i = paramInt2)
      {
        onSend(paramArrayOfByte, paramInt1, i, paramLong);
        paramInt1 += i;
        paramInt2 -= i;
        break;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */