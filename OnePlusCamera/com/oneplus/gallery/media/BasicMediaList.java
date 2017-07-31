package com.oneplus.gallery.media;

import com.oneplus.base.ListHandlerBaseObject;
import com.oneplus.base.Log;
import com.oneplus.gallery.ListChangeEventArgs;
import com.oneplus.gallery.ListMoveEventArgs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class BasicMediaList
  extends ListHandlerBaseObject<Media>
  implements MediaList
{
  private final MediaComparator m_Comparator;
  private final List<Media> m_List = new ArrayList();
  private final int m_MaxMediaCount;
  
  protected BasicMediaList(MediaComparator paramMediaComparator, int paramInt)
  {
    this.m_Comparator = paramMediaComparator;
    this.m_MaxMediaCount = paramInt;
  }
  
  private boolean addMediaDirectly(List<Media> paramList, boolean paramBoolean)
  {
    int i = 0;
    int j = this.m_List.size();
    if (j != 0) {
      if (paramBoolean) {
        break label202;
      }
    }
    label151:
    label202:
    do
    {
      return false;
      if (this.m_MaxMediaCount < 0)
      {
        this.m_List.addAll(paramList);
        if (!paramBoolean) {
          break label151;
        }
      }
      for (;;)
      {
        paramList = ListChangeEventArgs.obtain(0, this.m_List.size() - 1);
        raise(EVENT_MEDIA_ADDED, paramList);
        paramList.recycle();
        return true;
        if (paramList.size() <= this.m_MaxMediaCount) {
          break;
        }
        if (!paramBoolean)
        {
          this.m_List.addAll(paramList);
          Collections.sort(this.m_List, this.m_Comparator);
          i = this.m_List.size();
          for (;;)
          {
            i -= 1;
            if (i <= this.m_MaxMediaCount) {
              break;
            }
            this.m_List.remove(i);
          }
          Collections.sort(this.m_List, this.m_Comparator);
        }
        else
        {
          i = 0;
          while (i < this.m_MaxMediaCount)
          {
            this.m_List.add((Media)paramList.get(i));
            i += 1;
          }
        }
      }
      if (this.m_Comparator.compare((Media)this.m_List.get(j - 1), (Media)paramList.get(0)) < 0) {
        break;
      }
    } while (this.m_Comparator.compare((Media)this.m_List.get(0), (Media)paramList.get(paramList.size() - 1)) <= 0);
    if (this.m_MaxMediaCount < 0) {
      this.m_List.addAll(0, paramList);
    }
    for (;;)
    {
      paramList = ListChangeEventArgs.obtain(0, j - 1);
      raise(EVENT_MEDIA_ADDED, paramList);
      paramList.recycle();
      return true;
      if (this.m_MaxMediaCount < 0) {
        this.m_List.addAll(paramList);
      }
      for (;;)
      {
        paramList = ListChangeEventArgs.obtain(j, this.m_List.size() - 1);
        raise(EVENT_MEDIA_ADDED, paramList);
        paramList.recycle();
        return true;
        if (this.m_MaxMediaCount >= paramList.size() + j) {
          break;
        }
        if (this.m_MaxMediaCount == j) {
          break label436;
        }
        int k = this.m_MaxMediaCount;
        while (i < k - j)
        {
          this.m_List.add((Media)paramList.get(i));
          i += 1;
        }
      }
      label436:
      return true;
      if (this.m_MaxMediaCount >= paramList.size() + j) {
        break;
      }
      if (this.m_MaxMediaCount == j) {
        break label504;
      }
      i = this.m_MaxMediaCount - j - 1;
      while (i >= 0)
      {
        this.m_List.add(0, (Media)paramList.get(i));
        i -= 1;
      }
    }
    label504:
    return true;
  }
  
  private void removeMediaInternal(int paramInt)
  {
    ListChangeEventArgs localListChangeEventArgs = ListChangeEventArgs.obtain(paramInt);
    raise(EVENT_MEDIA_REMOVING, localListChangeEventArgs);
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
    {
      this.m_List.remove(paramInt);
      raise(EVENT_MEDIA_REMOVED, localListChangeEventArgs);
      localListChangeEventArgs.recycle();
      return;
    }
    Log.d(this.TAG, "removeMediaInternal() - Media list is released");
    localListChangeEventArgs.recycle();
  }
  
  private void removeMediaInternal(int paramInt1, int paramInt2)
  {
    ListChangeEventArgs localListChangeEventArgs = ListChangeEventArgs.obtain(paramInt1, paramInt2);
    raise(EVENT_MEDIA_REMOVING, localListChangeEventArgs);
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
    {
      int i = paramInt1;
      while (i <= paramInt2)
      {
        this.m_List.remove(paramInt1);
        i += 1;
      }
    }
    Log.d(this.TAG, "removeMediaInternal() - Media list is released");
    localListChangeEventArgs.recycle();
    return;
    raise(EVENT_MEDIA_REMOVED, localListChangeEventArgs);
    localListChangeEventArgs.recycle();
  }
  
  protected int addMedia(Media paramMedia)
  {
    verifyAccess();
    int i;
    if (verifyMediaToAdd(paramMedia))
    {
      i = Collections.binarySearch(this.m_List, paramMedia, this.m_Comparator);
      if (i >= 0) {
        return -1;
      }
    }
    else
    {
      return -1;
    }
    i ^= 0xFFFFFFFF;
    if (this.m_MaxMediaCount < 0) {}
    for (;;)
    {
      this.m_List.add(i, paramMedia);
      paramMedia = ListChangeEventArgs.obtain(i);
      raise(EVENT_MEDIA_ADDED, paramMedia);
      paramMedia.recycle();
      return i;
      if (this.m_List.size() >= this.m_MaxMediaCount)
      {
        if (i >= this.m_MaxMediaCount) {
          break;
        }
        removeMediaInternal(this.m_MaxMediaCount - 1, this.m_List.size() - 1);
      }
    }
    return i ^ 0xFFFFFFFF;
  }
  
  protected void addMedia(Collection<Media> paramCollection)
  {
    if (!(paramCollection instanceof List))
    {
      addMedia(new ArrayList(paramCollection), false);
      return;
    }
    addMedia((List)paramCollection, false);
  }
  
  protected void addMedia(List<Media> paramList, boolean paramBoolean)
  {
    verifyAccess();
    if (paramList == null) {}
    while (paramList.isEmpty()) {
      return;
    }
    int j;
    int k;
    Media localMedia;
    int m;
    int i1;
    int i;
    if (!addMediaDirectly(paramList, paramBoolean))
    {
      int i2 = paramList.size();
      int n = 0;
      j = -1;
      for (k = -1;; k = i1)
      {
        if (n >= i2) {
          break label438;
        }
        localMedia = (Media)paramList.get(n);
        m = j;
        i1 = k;
        if (verifyMediaToAdd(localMedia))
        {
          i = Collections.binarySearch(this.m_List, localMedia, this.m_Comparator);
          if (i < 0) {
            break;
          }
          i1 = k;
          m = j;
        }
        n += 1;
        j = m;
      }
    }
    return;
    i ^= 0xFFFFFFFF;
    if (this.m_MaxMediaCount < 0)
    {
      m = k;
      i1 = j;
      label144:
      if (m >= 0) {
        break label351;
      }
      j = i;
      k = i;
    }
    for (;;)
    {
      this.m_List.add(i, localMedia);
      m = j;
      i1 = k;
      break;
      i1 = j;
      m = k;
      if (this.m_List.size() < this.m_MaxMediaCount) {
        break label144;
      }
      m = j;
      i1 = k;
      if (i >= this.m_MaxMediaCount) {
        break;
      }
      if (k < 0)
      {
        m = j;
        j = k;
        k = m;
      }
      for (;;)
      {
        removeMediaInternal(this.m_MaxMediaCount - 1, this.m_List.size() - 1);
        if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
          break label341;
        }
        i1 = k;
        m = j;
        break;
        localListChangeEventArgs = ListChangeEventArgs.obtain(k, j);
        raise(EVENT_MEDIA_ADDED, localListChangeEventArgs);
        localListChangeEventArgs.recycle();
        if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
          break label331;
        }
        k = -1;
        j = -1;
      }
      label331:
      Log.d(this.TAG, "addMedia() - Media list is released");
      return;
      label341:
      Log.d(this.TAG, "addMedia() - Media list is released");
      return;
      label351:
      if (i == i1 + 1) {}
      while (i == m - 1)
      {
        j = i1 + 1;
        k = m;
        break;
      }
      ListChangeEventArgs localListChangeEventArgs = ListChangeEventArgs.obtain(m, i1);
      raise(EVENT_MEDIA_ADDED, localListChangeEventArgs);
      localListChangeEventArgs.recycle();
      if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
        break label428;
      }
      j = i;
      k = i;
    }
    label428:
    Log.d(this.TAG, "addMedia() -m_list is released");
    return;
    label438:
    if (k < 0) {
      return;
    }
    paramList = ListChangeEventArgs.obtain(k, j);
    raise(EVENT_MEDIA_ADDED, paramList);
    paramList.recycle();
  }
  
  protected final boolean checkMediaIndex(Media paramMedia)
  {
    int i;
    int j;
    if (paramMedia != null)
    {
      verifyAccess();
      i = Collections.binarySearch(this.m_List, paramMedia, this.m_Comparator);
      if (i >= 0) {
        break label106;
      }
      j = this.m_List.indexOf(paramMedia);
      if (j < 0) {
        break label122;
      }
      if (i < 0) {
        break label124;
      }
      label44:
      if (i > j) {
        break label131;
      }
    }
    for (;;)
    {
      ListMoveEventArgs localListMoveEventArgs = new ListMoveEventArgs(j, j, i, j);
      raise(EVENT_MEDIA_MOVING, localListMoveEventArgs);
      this.m_List.remove(j);
      this.m_List.add(i, paramMedia);
      raise(EVENT_MEDIA_MOVED, localListMoveEventArgs);
      return true;
      return false;
      label106:
      if (this.m_List.get(i) != paramMedia) {
        break;
      }
      return false;
      label122:
      return false;
      label124:
      i ^= 0xFFFFFFFF;
      break label44;
      label131:
      i -= 1;
    }
  }
  
  protected void clearMedia()
  {
    verifyAccess();
    int i = this.m_List.size();
    if (i <= 0) {
      return;
    }
    this.m_List.clear();
    ListChangeEventArgs localListChangeEventArgs = ListChangeEventArgs.obtain(0, i - 1);
    raise(EVENT_MEDIA_REMOVED, localListChangeEventArgs);
    localListChangeEventArgs.recycle();
  }
  
  public boolean contains(Object paramObject)
  {
    if (!(paramObject instanceof Media)) {
      return false;
    }
    return Collections.binarySearch(this.m_List, (Media)paramObject, this.m_Comparator) >= 0;
  }
  
  public Media get(int paramInt)
  {
    return (Media)this.m_List.get(paramInt);
  }
  
  public final MediaComparator getComparator()
  {
    return this.m_Comparator;
  }
  
  public final int getMaxMediaCount()
  {
    return this.m_MaxMediaCount;
  }
  
  public int indexOf(Object paramObject)
  {
    if (!(paramObject instanceof Media)) {
      return -1;
    }
    int i = Collections.binarySearch(this.m_List, (Media)paramObject, this.m_Comparator);
    if (i < 0) {
      return -1;
    }
    return i;
  }
  
  protected void removeMedia(int paramInt)
  {
    verifyAccess();
    removeMediaInternal(paramInt);
  }
  
  protected boolean removeMedia(Media paramMedia)
  {
    verifyAccess();
    int i;
    if (paramMedia != null)
    {
      i = Collections.binarySearch(this.m_List, paramMedia, this.m_Comparator);
      if (i >= 0) {
        break label29;
      }
    }
    label29:
    while (this.m_List.get(i) != paramMedia)
    {
      return false;
      return false;
    }
    removeMediaInternal(i);
    return true;
  }
  
  public int size()
  {
    return this.m_List.size();
  }
  
  protected boolean verifyMediaToAdd(Media paramMedia)
  {
    return paramMedia != null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/BasicMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */