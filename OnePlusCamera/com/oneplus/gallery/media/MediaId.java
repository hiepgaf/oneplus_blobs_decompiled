package com.oneplus.gallery.media;

public class MediaId
{
  private static final long INVALID_ID = -1L;
  private final long m_LongId;
  private final String m_StringId;
  
  public MediaId(long paramLong)
  {
    this.m_LongId = paramLong;
    this.m_StringId = String.valueOf(paramLong);
  }
  
  public MediaId(String paramString)
  {
    if (paramString != null)
    {
      this.m_LongId = -1L;
      this.m_StringId = paramString;
      return;
    }
    throw new IllegalArgumentException("Null string id is not acceptable");
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof MediaId)) {}
    do
    {
      do
      {
        return false;
        paramObject = (MediaId)paramObject;
        if (((MediaId)paramObject).isNumber()) {
          break;
        }
      } while (!((MediaId)paramObject).getStringId().equals(this.m_StringId));
      return true;
    } while (((MediaId)paramObject).getLongId() != this.m_LongId);
    return true;
  }
  
  public long getLongId()
  {
    return this.m_LongId;
  }
  
  public String getStringId()
  {
    return this.m_StringId;
  }
  
  public int hashCode()
  {
    return this.m_StringId.hashCode();
  }
  
  public boolean isNumber()
  {
    return this.m_LongId != -1L;
  }
  
  public String toString()
  {
    return this.m_StringId;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */