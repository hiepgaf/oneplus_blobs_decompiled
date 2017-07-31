package com.oneplus.gallery2.media;

import com.oneplus.base.ListHandlerBaseObject;
import com.oneplus.base.Log;
import com.oneplus.gallery2.ListChangeEventArgs;
import com.oneplus.gallery2.ListMoveEventArgs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class BaseMediaList
  extends ListHandlerBaseObject<Media>
  implements MediaList
{
  private final MediaComparator m_Comparator;
  private final List<Media> m_List = new ArrayList();
  private final int m_MaxMediaCount;
  
  protected BaseMediaList(MediaComparator paramMediaComparator, int paramInt)
  {
    this.m_Comparator = paramMediaComparator;
    this.m_MaxMediaCount = paramInt;
  }
  
  private boolean addMediaDirectly(List<Media> paramList, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int k = paramInt2 - paramInt1;
    int i;
    if (k > 0)
    {
      i = this.m_List.size();
      if (i == 0) {
        break label35;
      }
      if (paramBoolean) {
        break label187;
      }
    }
    label35:
    label154:
    label187:
    do
    {
      return false;
      return true;
      if (this.m_MaxMediaCount < 0)
      {
        addToInternalList(0, paramList, paramInt1, paramInt2);
        if (!paramBoolean) {
          break label154;
        }
      }
      for (;;)
      {
        paramList = ListChangeEventArgs.obtain(0, this.m_List.size() - 1);
        raise(EVENT_MEDIA_ADDED, paramList);
        paramList.recycle();
        return true;
        if (k <= this.m_MaxMediaCount) {
          break;
        }
        if (!paramBoolean)
        {
          addToInternalList(0, paramList, paramInt1, paramInt2);
          Collections.sort(this.m_List, this.m_Comparator);
          paramInt1 = this.m_List.size();
          for (;;)
          {
            paramInt1 -= 1;
            if (paramInt1 <= this.m_MaxMediaCount) {
              break;
            }
            this.m_List.remove(paramInt1);
          }
          Collections.sort(this.m_List, this.m_Comparator);
        }
        else
        {
          addToInternalList(0, paramList, paramInt1, paramInt2 - (k - this.m_MaxMediaCount));
        }
      }
      if (this.m_Comparator.compare((Media)this.m_List.get(i - 1), (Media)paramList.get(paramInt1)) < 0) {
        break;
      }
    } while (this.m_Comparator.compare((Media)this.m_List.get(0), (Media)paramList.get(paramInt2 - 1)) <= 0);
    int j = paramList.size() + i;
    if (this.m_MaxMediaCount < 0) {}
    label409:
    while (this.m_MaxMediaCount >= j)
    {
      addToInternalList(0, paramList, paramInt1, paramInt2);
      paramList = ListChangeEventArgs.obtain(0, this.m_List.size() - i - 1);
      raise(EVENT_MEDIA_ADDED, paramList);
      paramList.recycle();
      return true;
      j = i + k;
      if (this.m_MaxMediaCount < 0) {
        addToInternalList(i, paramList, paramInt1, paramInt2);
      }
      for (;;)
      {
        paramList = ListChangeEventArgs.obtain(i, this.m_List.size() - 1);
        raise(EVENT_MEDIA_ADDED, paramList);
        paramList.recycle();
        return true;
        if (this.m_MaxMediaCount >= j) {
          break;
        }
        if (this.m_MaxMediaCount == i) {
          break label409;
        }
        addToInternalList(i, paramList, paramInt1, paramInt2 - (j - this.m_MaxMediaCount));
      }
      return true;
    }
    ListChangeEventArgs localListChangeEventArgs;
    if (paramList.size() > this.m_MaxMediaCount)
    {
      localListChangeEventArgs = ListChangeEventArgs.obtain(0, this.m_List.size() - 1);
      raise(EVENT_MEDIA_REMOVING, localListChangeEventArgs);
      this.m_List.clear();
      raise(EVENT_MEDIA_REMOVED, localListChangeEventArgs);
      if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
      {
        addToInternalList(0, paramList, paramInt1, paramInt2 - (k - this.m_MaxMediaCount));
        paramList = ListChangeEventArgs.obtain(0, this.m_List.size() - 1);
        raise(EVENT_MEDIA_ADDED, paramList);
        paramList.recycle();
        return true;
      }
    }
    else
    {
      i = j - this.m_MaxMediaCount;
      localListChangeEventArgs = ListChangeEventArgs.obtain(this.m_List.size() - i - 1, this.m_List.size() - 1);
      raise(EVENT_MEDIA_REMOVING, localListChangeEventArgs);
      j = this.m_List.size() - 1;
      while (i > 0)
      {
        this.m_List.remove(j);
        j -= 1;
        i -= 1;
      }
      raise(EVENT_MEDIA_REMOVED, localListChangeEventArgs);
      if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
      {
        addToInternalList(0, paramList, paramInt1, paramInt2);
        paramList = ListChangeEventArgs.obtain(0, k - 1);
        raise(EVENT_MEDIA_ADDED, paramList);
        paramList.recycle();
        return true;
      }
      Log.d(this.TAG, "addMediaDirectly() - Media list has been released");
      return true;
    }
    Log.d(this.TAG, "addMediaDirectly() - Media list has been released");
    return true;
  }
  
  private void addToInternalList(int paramInt1, List<Media> paramList, int paramInt2, int paramInt3)
  {
    int i;
    if (paramInt3 > paramInt2)
    {
      if (paramInt2 == 0) {
        break label54;
      }
      i = paramInt1;
    }
    while (paramInt2 < paramInt3)
    {
      this.m_List.add(i, (Media)paramList.get(paramInt2));
      paramInt2 += 1;
      i += 1;
      continue;
      return;
      label54:
      i = paramInt1;
      if (paramInt3 == paramList.size() - 1) {
        this.m_List.addAll(paramInt1, paramList);
      }
    }
  }
  
  private boolean isCorrectPosition(Media paramMedia, int paramInt)
  {
    int i = this.m_List.size() - 1;
    if (paramMedia == null) {}
    while ((paramInt < 0) || (paramInt > i)) {
      return false;
    }
    if (i != 0)
    {
      if (paramInt == 0) {
        break label70;
      }
      if (paramInt == i) {
        break label98;
      }
      if (this.m_Comparator.compare(paramMedia, (Media)this.m_List.get(paramInt + 1)) <= 0) {
        break label128;
      }
    }
    label70:
    label98:
    label128:
    while (this.m_Comparator.compare(paramMedia, (Media)this.m_List.get(paramInt - 1)) < 0)
    {
      return false;
      return true;
      if (this.m_Comparator.compare(paramMedia, (Media)this.m_List.get(1)) > 0) {
        return false;
      }
      return true;
      return this.m_Comparator.compare(paramMedia, (Media)this.m_List.get(i - 1)) >= 0;
    }
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
    Log.d(this.TAG, "removeMediaInternal() -m_list is released");
    localListChangeEventArgs.recycle();
  }
  
  private void removeMediaInternal(int paramInt1, int paramInt2)
  {
    ListChangeEventArgs localListChangeEventArgs = ListChangeEventArgs.obtain(paramInt1, paramInt2);
    raise(EVENT_MEDIA_REMOVING, localListChangeEventArgs);
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
      while (paramInt2 >= paramInt1)
      {
        this.m_List.remove(paramInt2);
        paramInt2 -= 1;
      }
    }
    Log.d(this.TAG, "removeMediaInternal() -m_list is released");
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
  
  protected void addMedia(List<Media> paramList, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    verifyAccess();
    if (paramList == null) {}
    while (paramList.isEmpty()) {
      return;
    }
    int i;
    int j;
    Media localMedia;
    int k;
    int n;
    if (!addMediaDirectly(paramList, 0, paramList.size(), paramBoolean))
    {
      i = -1;
      j = -1;
      int m = paramInt1;
      for (;;)
      {
        if (m >= paramInt2) {
          break label437;
        }
        localMedia = (Media)paramList.get(m);
        k = i;
        n = j;
        if (verifyMediaToAdd(localMedia))
        {
          paramInt1 = Collections.binarySearch(this.m_List, localMedia, this.m_Comparator);
          if (paramInt1 < 0) {
            break;
          }
          n = j;
          k = i;
        }
        m += 1;
        i = k;
        j = n;
      }
    }
    return;
    paramInt1 ^= 0xFFFFFFFF;
    if (this.m_MaxMediaCount < 0)
    {
      k = j;
      n = i;
      label143:
      if (k >= 0) {
        break label350;
      }
      i = paramInt1;
      j = paramInt1;
    }
    for (;;)
    {
      this.m_List.add(paramInt1, localMedia);
      k = i;
      n = j;
      break;
      n = i;
      k = j;
      if (this.m_List.size() < this.m_MaxMediaCount) {
        break label143;
      }
      k = i;
      n = j;
      if (paramInt1 >= this.m_MaxMediaCount) {
        break;
      }
      if (j < 0)
      {
        k = i;
        i = j;
        j = k;
      }
      for (;;)
      {
        removeMediaInternal(this.m_MaxMediaCount - 1, this.m_List.size() - 1);
        if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
          break label340;
        }
        n = j;
        k = i;
        break;
        localListChangeEventArgs = ListChangeEventArgs.obtain(j, i);
        raise(EVENT_MEDIA_ADDED, localListChangeEventArgs);
        localListChangeEventArgs.recycle();
        if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
          break label330;
        }
        j = -1;
        i = -1;
      }
      label330:
      Log.d(this.TAG, "addMedia() - Media list has been released");
      return;
      label340:
      Log.d(this.TAG, "addMedia() - Media list has been released");
      return;
      label350:
      if (paramInt1 == n + 1) {}
      while (paramInt1 == k - 1)
      {
        i = n + 1;
        j = k;
        break;
      }
      ListChangeEventArgs localListChangeEventArgs = ListChangeEventArgs.obtain(k, n);
      raise(EVENT_MEDIA_ADDED, localListChangeEventArgs);
      localListChangeEventArgs.recycle();
      if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
        break label427;
      }
      i = paramInt1;
      j = paramInt1;
    }
    label427:
    Log.d(this.TAG, "addMedia() - Media list has been released");
    return;
    label437:
    if (j < 0) {
      return;
    }
    paramList = ListChangeEventArgs.obtain(j, i);
    raise(EVENT_MEDIA_ADDED, paramList);
    paramList.recycle();
  }
  
  protected void addMedia(List<Media> paramList, boolean paramBoolean)
  {
    addMedia(paramList, 0, paramList.size(), paramBoolean);
  }
  
  protected final boolean checkMediaIndex(Media paramMedia)
  {
    int j;
    if (paramMedia != null)
    {
      verifyAccess();
      j = indexOf(paramMedia);
      if (!isCorrectPosition(paramMedia, j)) {
        break label27;
      }
    }
    label27:
    while (j < 0)
    {
      return false;
      return false;
    }
    int i = Collections.binarySearch(this.m_List, paramMedia, this.m_Comparator) ^ 0xFFFFFFFF;
    if (!isCorrectPosition(paramMedia, i))
    {
      this.m_List.remove(j);
      i = Collections.binarySearch(this.m_List, paramMedia, this.m_Comparator) ^ 0xFFFFFFFF;
      if (!isCorrectPosition(paramMedia, i))
      {
        this.m_List.add(j, paramMedia);
        return false;
      }
    }
    else if (i > j) {}
    for (;;)
    {
      return moveMedia(j, i);
      i -= 1;
      continue;
      this.m_List.add(j, paramMedia);
    }
  }
  
  protected void clearMedia()
  {
    verifyAccess();
    int i = this.m_List.size();
    if (i <= 0) {
      return;
    }
    ListChangeEventArgs localListChangeEventArgs = ListChangeEventArgs.obtain(0, i - 1);
    raise(EVENT_MEDIA_REMOVING, localListChangeEventArgs);
    this.m_List.clear();
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
  
  public boolean equals(Object paramObject)
  {
    return this == paramObject;
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
    int j = Collections.binarySearch(this.m_List, (Media)paramObject, this.m_Comparator);
    int i = j;
    if (j < 0) {
      i = this.m_List.indexOf(paramObject);
    }
    return i;
  }
  
  protected boolean moveMedia(int paramInt1, int paramInt2)
  {
    ListMoveEventArgs localListMoveEventArgs = new ListMoveEventArgs(paramInt1, paramInt1, paramInt2, paramInt2);
    raise(EVENT_MEDIA_MOVING, localListMoveEventArgs);
    Media localMedia = (Media)this.m_List.remove(paramInt1);
    this.m_List.add(paramInt2, localMedia);
    raise(EVENT_MEDIA_MOVED, localListMoveEventArgs);
    return true;
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
      if (i < 0) {
        break label33;
      }
      if (i >= 0) {
        break label47;
      }
    }
    label33:
    label47:
    while (this.m_List.get(i) != paramMedia)
    {
      return false;
      return false;
      i = this.m_List.indexOf(paramMedia);
      break;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */