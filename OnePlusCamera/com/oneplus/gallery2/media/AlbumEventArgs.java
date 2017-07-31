package com.oneplus.gallery2.media;

import com.oneplus.base.EventArgs;
import java.util.Collections;
import java.util.List;

public class AlbumEventArgs
  extends EventArgs
{
  private final long m_AlbumId;
  private final List<AlbumMediaSet> m_MediaSets;
  
  public AlbumEventArgs(long paramLong, List<AlbumMediaSet> paramList)
  {
    this.m_AlbumId = paramLong;
    Object localObject = paramList;
    if (paramList == null) {
      localObject = Collections.EMPTY_LIST;
    }
    this.m_MediaSets = ((List)localObject);
  }
  
  public final long getAlbumId()
  {
    return this.m_AlbumId;
  }
  
  public final List<AlbumMediaSet> getMediaSets()
  {
    return this.m_MediaSets;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/AlbumEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */