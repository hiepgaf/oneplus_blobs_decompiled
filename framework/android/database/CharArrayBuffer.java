package android.database;

public final class CharArrayBuffer
{
  public char[] data;
  public int sizeCopied;
  
  public CharArrayBuffer(int paramInt)
  {
    this.data = new char[paramInt];
  }
  
  public CharArrayBuffer(char[] paramArrayOfChar)
  {
    this.data = paramArrayOfChar;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/CharArrayBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */