package android.gesture;

import java.io.DataInputStream;
import java.io.IOException;

public class GesturePoint
{
  public final long timestamp;
  public final float x;
  public final float y;
  
  public GesturePoint(float paramFloat1, float paramFloat2, long paramLong)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.timestamp = paramLong;
  }
  
  static GesturePoint deserialize(DataInputStream paramDataInputStream)
    throws IOException
  {
    return new GesturePoint(paramDataInputStream.readFloat(), paramDataInputStream.readFloat(), paramDataInputStream.readLong());
  }
  
  public Object clone()
  {
    return new GesturePoint(this.x, this.y, this.timestamp);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/GesturePoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */