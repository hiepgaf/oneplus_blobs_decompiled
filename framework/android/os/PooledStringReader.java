package android.os;

public class PooledStringReader
{
  private final Parcel mIn;
  private final String[] mPool;
  
  public PooledStringReader(Parcel paramParcel)
  {
    this.mIn = paramParcel;
    this.mPool = new String[paramParcel.readInt()];
  }
  
  public int getStringCount()
  {
    return this.mPool.length;
  }
  
  public String readString()
  {
    int i = this.mIn.readInt();
    if (i >= 0) {
      return this.mPool[i];
    }
    i = -i;
    String str = this.mIn.readString();
    this.mPool[(i - 1)] = str;
    return str;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/PooledStringReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */