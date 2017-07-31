package android.media;

import java.io.Closeable;
import java.io.IOException;

public abstract class MediaDataSource
  implements Closeable
{
  public abstract long getSize()
    throws IOException;
  
  public abstract int readAt(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */