package android.content.pm;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.crypto.Mac;

public class MacAuthenticatedInputStream
  extends FilterInputStream
{
  private final Mac mMac;
  
  public MacAuthenticatedInputStream(InputStream paramInputStream, Mac paramMac)
  {
    super(paramInputStream);
    this.mMac = paramMac;
  }
  
  public boolean isTagEqual(byte[] paramArrayOfByte)
  {
    boolean bool = false;
    byte[] arrayOfByte = this.mMac.doFinal();
    if ((paramArrayOfByte == null) || (arrayOfByte == null)) {}
    while (paramArrayOfByte.length != arrayOfByte.length) {
      return false;
    }
    int j = 0;
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      j |= paramArrayOfByte[i] ^ arrayOfByte[i];
      i += 1;
    }
    if (j == 0) {
      bool = true;
    }
    return bool;
  }
  
  public int read()
    throws IOException
  {
    int i = super.read();
    if (i >= 0) {
      this.mMac.update((byte)i);
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    paramInt2 = super.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt2 > 0) {
      this.mMac.update(paramArrayOfByte, paramInt1, paramInt2);
    }
    return paramInt2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/MacAuthenticatedInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */