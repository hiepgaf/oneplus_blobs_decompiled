package android.media;

import java.util.Arrays;
import java.util.UUID;

public abstract class DrmInitData
{
  public abstract SchemeInitData get(UUID paramUUID);
  
  public static final class SchemeInitData
  {
    public final byte[] data;
    public final String mimeType;
    
    public SchemeInitData(String paramString, byte[] paramArrayOfByte)
    {
      this.mimeType = paramString;
      this.data = paramArrayOfByte;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = false;
      if (!(paramObject instanceof SchemeInitData)) {
        return false;
      }
      if (paramObject == this) {
        return true;
      }
      if (this.mimeType.equals(((SchemeInitData)paramObject).mimeType)) {
        bool = Arrays.equals(this.data, ((SchemeInitData)paramObject).data);
      }
      return bool;
    }
    
    public int hashCode()
    {
      return this.mimeType.hashCode() + Arrays.hashCode(this.data) * 31;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/DrmInitData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */