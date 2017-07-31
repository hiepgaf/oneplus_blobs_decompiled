package android.os;

import java.util.HashMap;

public class PooledStringWriter
{
  private int mNext;
  private final Parcel mOut;
  private final HashMap<String, Integer> mPool;
  private int mStart;
  
  public PooledStringWriter(Parcel paramParcel)
  {
    this.mOut = paramParcel;
    this.mPool = new HashMap();
    this.mStart = paramParcel.dataPosition();
    paramParcel.writeInt(0);
  }
  
  public void finish()
  {
    int i = this.mOut.dataPosition();
    this.mOut.setDataPosition(this.mStart);
    this.mOut.writeInt(this.mNext);
    this.mOut.setDataPosition(i);
  }
  
  public int getStringCount()
  {
    return this.mPool.size();
  }
  
  public void writeString(String paramString)
  {
    Integer localInteger = (Integer)this.mPool.get(paramString);
    if (localInteger != null)
    {
      this.mOut.writeInt(localInteger.intValue());
      return;
    }
    this.mPool.put(paramString, Integer.valueOf(this.mNext));
    this.mOut.writeInt(-(this.mNext + 1));
    this.mOut.writeString(paramString);
    this.mNext += 1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/PooledStringWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */