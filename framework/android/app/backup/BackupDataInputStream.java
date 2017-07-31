package android.app.backup;

import java.io.IOException;
import java.io.InputStream;

public class BackupDataInputStream
  extends InputStream
{
  int dataSize;
  String key;
  BackupDataInput mData;
  byte[] mOneByte;
  
  BackupDataInputStream(BackupDataInput paramBackupDataInput)
  {
    this.mData = paramBackupDataInput;
  }
  
  public String getKey()
  {
    return this.key;
  }
  
  public int read()
    throws IOException
  {
    byte[] arrayOfByte = this.mOneByte;
    if (this.mOneByte == null)
    {
      arrayOfByte = new byte[1];
      this.mOneByte = arrayOfByte;
    }
    this.mData.readEntityData(arrayOfByte, 0, 1);
    return arrayOfByte[0];
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return this.mData.readEntityData(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return this.mData.readEntityData(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public int size()
  {
    return this.dataSize;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupDataInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */