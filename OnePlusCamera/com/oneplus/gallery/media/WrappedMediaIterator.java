package com.oneplus.gallery.media;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WrappedMediaIterator
  implements Iterator<Media>
{
  private List<Iterator<Media>> m_BaseIterators;
  private int m_IteratorIndex;
  private Media m_Next;
  
  public WrappedMediaIterator(Iterator<Media> paramIterator)
  {
    this.m_BaseIterators = new ArrayList();
    this.m_BaseIterators.add(paramIterator);
  }
  
  public WrappedMediaIterator(List<Iterator<Media>> paramList)
  {
    this.m_BaseIterators = new ArrayList(paramList);
  }
  
  public boolean hasNext()
  {
    while (this.m_IteratorIndex < this.m_BaseIterators.size())
    {
      Iterator localIterator = (Iterator)this.m_BaseIterators.get(this.m_IteratorIndex);
      while (localIterator.hasNext())
      {
        Media localMedia = (Media)localIterator.next();
        if (selectMedia(localMedia))
        {
          this.m_Next = localMedia;
          return true;
        }
      }
      this.m_IteratorIndex += 1;
    }
    return false;
  }
  
  public Media next()
  {
    if (this.m_Next == null) {
      throw new IllegalStateException();
    }
    Media localMedia = this.m_Next;
    this.m_Next = null;
    return localMedia;
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException("Cannot remove media");
  }
  
  protected boolean selectMedia(Media paramMedia)
  {
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/WrappedMediaIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */