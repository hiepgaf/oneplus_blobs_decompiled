package com.oneplus.gallery2.media;

import java.util.Collection;
import java.util.Iterator;

public class MediaIterable
  implements Iterable<Media>
{
  private final Iterable<Media>[] m_SubIterables;
  private final MediaType m_TargetMediaType;
  
  public MediaIterable(MediaType paramMediaType, Iterable<? extends Media> paramIterable)
  {
    if (paramIterable == null) {}
    for (this.m_SubIterables = new Iterable[0];; this.m_SubIterables = new Iterable[] { paramIterable })
    {
      this.m_TargetMediaType = paramMediaType;
      return;
    }
  }
  
  public MediaIterable(MediaType paramMediaType, Collection<Iterable<? extends Media>> paramCollection)
  {
    if (paramCollection == null) {}
    for (this.m_SubIterables = new Iterable[0];; this.m_SubIterables = ((Iterable[])paramCollection.toArray(new Iterable[paramCollection.size()])))
    {
      this.m_TargetMediaType = paramMediaType;
      return;
    }
  }
  
  protected boolean filterMedia(Media paramMedia)
  {
    if (this.m_TargetMediaType == null) {}
    while (paramMedia.getType() == this.m_TargetMediaType) {
      return true;
    }
    return false;
  }
  
  public Iterator<Media> iterator()
  {
    return new MediaIterator(null);
  }
  
  private final class MediaIterator
    implements Iterator<Media>
  {
    private Media m_Next;
    private int m_SubIterableIndex = -1;
    private Iterator<Media> m_SubIterator;
    
    private MediaIterator() {}
    
    public boolean hasNext()
    {
      for (;;)
      {
        if (this.m_SubIterableIndex < MediaIterable.this.m_SubIterables.length) {
          if (this.m_SubIterator == null) {}
        }
        while (this.m_SubIterator.hasNext())
        {
          this.m_Next = ((Media)this.m_SubIterator.next());
          if (!MediaIterable.this.filterMedia(this.m_Next))
          {
            this.m_Next = null;
            continue;
            this.m_SubIterableIndex += 1;
            if (this.m_SubIterableIndex >= MediaIterable.this.m_SubIterables.length) {
              return false;
            }
            this.m_SubIterator = MediaIterable.this.m_SubIterables[this.m_SubIterableIndex].iterator();
          }
          else
          {
            return true;
          }
        }
        this.m_SubIterator = null;
      }
    }
    
    public Media next()
    {
      if (this.m_Next != null)
      {
        Media localMedia = this.m_Next;
        this.m_Next = null;
        return localMedia;
      }
      throw new IllegalAccessError("No next media");
    }
    
    public void remove()
    {
      throw new IllegalAccessError("Cannot remove media");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaIterable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */