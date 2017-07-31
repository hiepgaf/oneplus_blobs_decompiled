package com.oneplus.gallery2.media;

import com.oneplus.base.EventArgs;
import com.oneplus.base.ListHandlerBaseObject;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.gallery2.ListChangeEventArgs;
import com.oneplus.gallery2.ListMoveEventArgs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class BaseMediaSetList
  extends ListHandlerBaseObject<MediaSet>
  implements MediaSetList
{
  private final Set<MediaSet> m_AttachedMediaSet = new HashSet();
  private MediaSetComparator m_Comparator;
  private final Set<MediaSet> m_EmptySet = new HashSet();
  private final PropertyChangedCallback<Integer> m_HiddenMediaCountChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Integer> paramAnonymousPropertyKey, PropertyChangeEventArgs<Integer> paramAnonymousPropertyChangeEventArgs)
    {
      BaseMediaSetList.this.updateHiddenMediaSetCountProp();
    }
  };
  private final Set<MediaSet> m_HiddenSet = new HashSet();
  private final List<MediaSet> m_List = new ArrayList();
  private final PropertyChangedCallback<Integer> m_MediaCountChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Integer> paramAnonymousPropertyKey, PropertyChangeEventArgs<Integer> paramAnonymousPropertyChangeEventArgs)
    {
      BaseMediaSetList.this.onMediaCountChanged((MediaSet)paramAnonymousPropertySource, (Integer)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Integer)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final PropertyChangedCallback m_MediaSetPropChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
    {
      BaseMediaSetList.this.onMediaSetPropertyChanged((MediaSet)paramAnonymousPropertySource, paramAnonymousPropertyKey);
    }
  };
  private PropertyKey<?>[] m_MediaSetPropsForComparison;
  private final PropertyChangedCallback<Boolean> m_MediaSetVisibilityPropChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      BaseMediaSetList.this.onMediaSetVisibilityPropChanged((MediaSet)paramAnonymousPropertySource, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
    }
  };
  private final boolean m_ShowEmptyMediaSets;
  private boolean m_ShowHiddenMediaSets;
  
  protected BaseMediaSetList(MediaSetComparator paramMediaSetComparator)
  {
    this(paramMediaSetComparator, false);
  }
  
  protected BaseMediaSetList(MediaSetComparator paramMediaSetComparator, boolean paramBoolean)
  {
    if (paramMediaSetComparator != null)
    {
      this.m_Comparator = paramMediaSetComparator;
      this.m_MediaSetPropsForComparison = ((PropertyKey[])paramMediaSetComparator.getReferencedProperties().toArray(new PropertyKey[0]));
      this.m_ShowEmptyMediaSets = paramBoolean;
      return;
    }
    throw new IllegalArgumentException("No comparator");
  }
  
  private int addMediaSet(MediaSet paramMediaSet, boolean paramBoolean1, boolean paramBoolean2)
  {
    verifyAccess();
    int i;
    boolean bool;
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
    {
      if (paramMediaSet == null) {
        break label85;
      }
      if (!((Boolean)paramMediaSet.get(MediaSet.PROP_IS_VISIBLE)).booleanValue()) {
        break label87;
      }
      i = 1;
      if (MediaSets.isEmpty(paramMediaSet)) {
        break label144;
      }
      bool = paramBoolean2;
    }
    for (;;)
    {
      if (i != 0)
      {
        i = Collections.binarySearch(this.m_List, paramMediaSet, this.m_Comparator) ^ 0xFFFFFFFF;
        if (i >= 0) {
          break label190;
        }
        return -1;
        return -1;
        label85:
        return -1;
        label87:
        if (!this.m_HiddenSet.add(paramMediaSet))
        {
          i = 1;
          break;
        }
        updateHiddenMediaSetCountProp();
        if (this.m_ShowHiddenMediaSets)
        {
          i = 1;
          break;
        }
        if (!paramBoolean2) {}
        for (;;)
        {
          i = 0;
          break;
          attachToMediaSet(paramMediaSet);
          paramBoolean2 = false;
        }
        label144:
        bool = paramBoolean2;
        if (!this.m_ShowEmptyMediaSets)
        {
          if (!paramBoolean2) {}
          for (;;)
          {
            this.m_EmptySet.add(paramMediaSet);
            i = 0;
            bool = paramBoolean2;
            break;
            attachToMediaSet(paramMediaSet);
            paramBoolean2 = false;
          }
        }
      }
    }
    return -1;
    label190:
    if (!bool) {}
    for (;;)
    {
      this.m_List.add(i, paramMediaSet);
      if (!paramBoolean1) {
        break;
      }
      paramMediaSet = ListChangeEventArgs.obtain(i);
      raise(EVENT_MEDIA_SET_ADDED, paramMediaSet);
      paramMediaSet.recycle();
      return -1;
      attachToMediaSet(paramMediaSet);
    }
  }
  
  private void attachToMediaSet(MediaSet paramMediaSet)
  {
    if (this.m_AttachedMediaSet.add(paramMediaSet))
    {
      int i = this.m_MediaSetPropsForComparison.length;
      for (;;)
      {
        i -= 1;
        if (i < 0) {
          break;
        }
        paramMediaSet.addCallback(this.m_MediaSetPropsForComparison[i], this.m_MediaSetPropChangedCallback);
      }
    }
    return;
    paramMediaSet.addCallback(MediaSet.PROP_IS_VISIBLE, this.m_MediaSetVisibilityPropChangedCallback);
    paramMediaSet.addCallback(MediaSet.PROP_MEDIA_COUNT, this.m_MediaCountChangedCallback);
    paramMediaSet.addCallback(MediaSet.PROP_HIDDEN_MEDIA_COUNT, this.m_HiddenMediaCountChangedCallback);
    paramMediaSet.set(MediaSet.PROP_CONTAINS_HIDDEN_MEDIA, Boolean.valueOf(this.m_ShowHiddenMediaSets));
  }
  
  private void attachToMediaSets()
  {
    int i = this.m_List.size() - 1;
    while (i >= 0)
    {
      attachToMediaSet((MediaSet)this.m_List.get(i));
      i -= 1;
    }
  }
  
  private void detachFromMediaSet(MediaSet paramMediaSet)
  {
    if (this.m_AttachedMediaSet.remove(paramMediaSet))
    {
      int i = this.m_MediaSetPropsForComparison.length;
      for (;;)
      {
        i -= 1;
        if (i < 0) {
          break;
        }
        paramMediaSet.removeCallback(this.m_MediaSetPropsForComparison[i], this.m_MediaSetPropChangedCallback);
      }
    }
    return;
    paramMediaSet.removeCallback(MediaSet.PROP_IS_VISIBLE, this.m_MediaSetVisibilityPropChangedCallback);
    paramMediaSet.removeCallback(MediaSet.PROP_MEDIA_COUNT, this.m_MediaCountChangedCallback);
    paramMediaSet.removeCallback(MediaSet.PROP_HIDDEN_MEDIA_COUNT, this.m_HiddenMediaCountChangedCallback);
  }
  
  private void detachFromMediaSets()
  {
    int i = this.m_List.size() - 1;
    while (i >= 0)
    {
      detachFromMediaSet((MediaSet)this.m_List.get(i));
      i -= 1;
    }
  }
  
  private boolean isCorrectPosition(MediaSet paramMediaSet, int paramInt)
  {
    int i = this.m_List.size() - 1;
    if (paramMediaSet == null) {}
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
      if (this.m_Comparator.compare(paramMediaSet, (MediaSet)this.m_List.get(paramInt + 1)) <= 0) {
        break label128;
      }
    }
    label70:
    label98:
    label128:
    while (this.m_Comparator.compare(paramMediaSet, (MediaSet)this.m_List.get(paramInt - 1)) < 0)
    {
      return false;
      return true;
      if (this.m_Comparator.compare(paramMediaSet, (MediaSet)this.m_List.get(1)) > 0) {
        return false;
      }
      return true;
      return this.m_Comparator.compare(paramMediaSet, (MediaSet)this.m_List.get(i - 1)) >= 0;
    }
    return true;
  }
  
  private void onMediaCountChanged(MediaSet paramMediaSet, Integer paramInteger1, Integer paramInteger2)
  {
    int i;
    if (!MediaSets.isEmpty(paramInteger1))
    {
      i = 1;
      if (MediaSets.isEmpty(paramInteger2)) {
        break label70;
      }
    }
    label70:
    for (int j = 1;; j = 0)
    {
      if (i == j) {
        break label76;
      }
      if (this.m_ShowEmptyMediaSets) {
        break label77;
      }
      if (j != 0) {
        break label78;
      }
      this.m_EmptySet.add(paramMediaSet);
      removeMediaSet(paramMediaSet, true, true);
      updateHiddenMediaSetCountProp();
      return;
      i = 0;
      break;
    }
    label76:
    return;
    label77:
    return;
    label78:
    this.m_EmptySet.remove(paramMediaSet);
    addMediaSet(paramMediaSet, true, false);
    updateHiddenMediaSetCountProp();
  }
  
  private void onMediaSetPropertyChanged(MediaSet paramMediaSet, PropertyKey<?> paramPropertyKey)
  {
    checkMediaSetIndex(paramMediaSet);
  }
  
  private void onMediaSetVisibilityPropChanged(MediaSet paramMediaSet, boolean paramBoolean)
  {
    if (this.m_AttachedMediaSet.contains(paramMediaSet))
    {
      if (paramBoolean) {
        break label45;
      }
      paramBoolean = this.m_HiddenSet.add(paramMediaSet);
      if (!paramBoolean) {
        break label98;
      }
      if (!this.m_ShowHiddenMediaSets) {
        break label125;
      }
    }
    for (;;)
    {
      if (paramBoolean) {
        break label136;
      }
      return;
      return;
      label45:
      paramBoolean = this.m_HiddenSet.remove(paramMediaSet);
      if (paramBoolean) {}
      for (;;)
      {
        addMediaSet(paramMediaSet, true, false);
        break;
        Log.w(this.TAG, "onMediaSetVisibilityPropChanged() - remove fail, mediaSet:" + paramMediaSet);
      }
      label98:
      Log.w(this.TAG, "onMediaSetVisibilityPropChanged() - add fail, mediaSet:" + paramMediaSet);
      break;
      label125:
      removeMediaSet(paramMediaSet, true, true);
    }
    label136:
    updateHiddenMediaSetCountProp();
  }
  
  private boolean removeMediaSet(MediaSet paramMediaSet, boolean paramBoolean1, boolean paramBoolean2)
  {
    ListChangeEventArgs localListChangeEventArgs = null;
    verifyAccess();
    int i;
    if (paramMediaSet != null)
    {
      if (!paramBoolean2) {
        break label79;
      }
      i = Collections.binarySearch(this.m_List, paramMediaSet, this.m_Comparator);
      if (i >= 0) {
        break label114;
      }
      label34:
      i = this.m_List.indexOf(paramMediaSet);
      label46:
      if (i < 0) {
        break label132;
      }
      if (paramBoolean1) {
        break label134;
      }
      label55:
      if (!paramBoolean2) {
        break label181;
      }
      label59:
      this.m_List.remove(i);
      if (paramBoolean1) {
        break label189;
      }
    }
    for (;;)
    {
      return true;
      return false;
      label79:
      if (!this.m_HiddenSet.remove(paramMediaSet)) {}
      for (;;)
      {
        this.m_EmptySet.remove(paramMediaSet);
        break;
        updateHiddenMediaSetCountProp();
      }
      label114:
      if (this.m_List.get(i) != paramMediaSet) {
        break label34;
      }
      break label46;
      label132:
      return false;
      label134:
      localListChangeEventArgs = ListChangeEventArgs.obtain(i);
      raise(EVENT_MEDIA_SET_REMOVING, localListChangeEventArgs);
      if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
        break label55;
      }
      Log.d(this.TAG, "removeMediaSet() - List has been released");
      return false;
      label181:
      detachFromMediaSet(paramMediaSet);
      break label59;
      label189:
      raise(EVENT_MEDIA_SET_REMOVED, localListChangeEventArgs);
      localListChangeEventArgs.recycle();
    }
  }
  
  private boolean setComparatorProp(MediaSetComparator paramMediaSetComparator)
  {
    verifyAccess();
    if (paramMediaSetComparator != this.m_Comparator)
    {
      if (paramMediaSetComparator != null)
      {
        detachFromMediaSets();
        MediaSetComparator localMediaSetComparator = this.m_Comparator;
        this.m_Comparator = paramMediaSetComparator;
        this.m_MediaSetPropsForComparison = ((PropertyKey[])paramMediaSetComparator.getReferencedProperties().toArray(new PropertyKey[0]));
        attachToMediaSets();
        Collections.sort(this.m_List, paramMediaSetComparator);
        raise(EVENT_RESET, EventArgs.EMPTY);
        return notifyPropertyChanged(PROP_COMPARATOR, localMediaSetComparator, paramMediaSetComparator);
      }
    }
    else {
      return false;
    }
    throw new IllegalArgumentException();
  }
  
  private boolean setShowHiddenMediaSetsProp(boolean paramBoolean)
  {
    boolean bool = this.m_ShowHiddenMediaSets;
    if (bool != paramBoolean)
    {
      this.m_ShowHiddenMediaSets = paramBoolean;
      localObject = (MediaSet[])this.m_HiddenSet.toArray(new MediaSet[this.m_HiddenSet.size()]);
      j = localObject.length;
      i = 0;
      while (i < j)
      {
        localObject[i].set(MediaSet.PROP_CONTAINS_HIDDEN_MEDIA, Boolean.valueOf(paramBoolean));
        i += 1;
      }
    }
    return false;
    Object localObject = (MediaSet[])this.m_List.toArray(new MediaSet[this.m_List.size()]);
    int j = localObject.length;
    int i = 0;
    while (i < j)
    {
      localObject[i].set(MediaSet.PROP_CONTAINS_HIDDEN_MEDIA, Boolean.valueOf(paramBoolean));
      i += 1;
    }
    localObject = (MediaSet[])this.m_EmptySet.toArray(new MediaSet[this.m_EmptySet.size()]);
    j = localObject.length;
    i = 0;
    while (i < j)
    {
      localObject[i].set(MediaSet.PROP_CONTAINS_HIDDEN_MEDIA, Boolean.valueOf(paramBoolean));
      i += 1;
    }
    if (!paramBoolean)
    {
      localObject = this.m_HiddenSet.iterator();
      while (((Iterator)localObject).hasNext()) {
        removeMediaSet((MediaSet)((Iterator)localObject).next(), true, true);
      }
    }
    localObject = this.m_HiddenSet.iterator();
    while (((Iterator)localObject).hasNext()) {
      addMediaSet((MediaSet)((Iterator)localObject).next(), true, false);
    }
    return notifyPropertyChanged(PROP_SHOW_HIDDEN_MEDIA_SETS, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
  }
  
  private boolean updateHiddenMediaSetCountProp()
  {
    int i;
    MediaSet localMediaSet;
    if (!this.m_ShowEmptyMediaSets)
    {
      Iterator localIterator = this.m_HiddenSet.iterator();
      i = 0;
      if (!localIterator.hasNext()) {
        break label93;
      }
      localMediaSet = (MediaSet)localIterator.next();
      if (MediaSets.isEmpty(localMediaSet)) {
        break label72;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      return setReadOnly(PROP_HIDDEN_MEDIA_SET_COUNT, Integer.valueOf(this.m_HiddenSet.size()));
      label72:
      if (MediaSets.isEmpty((Integer)localMediaSet.get(MediaSet.PROP_HIDDEN_MEDIA_COUNT))) {
        break;
      }
    }
    label93:
    return setReadOnly(PROP_HIDDEN_MEDIA_SET_COUNT, Integer.valueOf(i));
  }
  
  protected int addMediaSet(MediaSet paramMediaSet, boolean paramBoolean)
  {
    return addMediaSet(paramMediaSet, paramBoolean, true);
  }
  
  protected void addMediaSets(Iterable<MediaSet> paramIterable, boolean paramBoolean)
  {
    int j = -1;
    verifyAccess();
    int k;
    MediaSet localMediaSet;
    int i;
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
    {
      if (paramIterable == null) {
        break label112;
      }
      paramIterable = paramIterable.iterator();
      k = -1;
      do
      {
        do
        {
          if (!paramIterable.hasNext()) {
            break;
          }
          localMediaSet = (MediaSet)paramIterable.next();
        } while (!verifyMediaSetToAdd(localMediaSet));
        i = Collections.binarySearch(this.m_List, localMediaSet, this.m_Comparator) ^ 0xFFFFFFFF;
      } while (i < 0);
      if (paramBoolean) {
        break label113;
      }
    }
    for (;;)
    {
      attachToMediaSet(localMediaSet);
      this.m_List.add(i, localMediaSet);
      break;
      return;
      label112:
      return;
      label113:
      if (k < 0)
      {
        j = i;
        k = i;
      }
      else
      {
        if (i == j + 1) {}
        while (i == k - 1)
        {
          j += 1;
          break;
        }
        ListChangeEventArgs localListChangeEventArgs = ListChangeEventArgs.obtain(k, j);
        raise(EVENT_MEDIA_SET_ADDED, localListChangeEventArgs);
        localListChangeEventArgs.recycle();
        if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
          break label200;
        }
        j = i;
        k = i;
      }
    }
    label200:
    Log.d(this.TAG, "addMediaSet() - List has been released");
    return;
    if (!paramBoolean) {}
    while (k < 0) {
      return;
    }
    paramIterable = ListChangeEventArgs.obtain(k, j);
    raise(EVENT_MEDIA_SET_ADDED, paramIterable);
    paramIterable.recycle();
  }
  
  protected void checkMediaSetIndex(MediaSet paramMediaSet)
  {
    verifyAccess();
    int j;
    if (paramMediaSet != null)
    {
      if (this.m_List.size() <= 1) {
        break label38;
      }
      j = indexOf(paramMediaSet);
      if (!isCorrectPosition(paramMediaSet, j)) {
        break label39;
      }
    }
    label38:
    label39:
    while (j < 0)
    {
      return;
      return;
      return;
    }
    int i = Collections.binarySearch(this.m_List, paramMediaSet, this.m_Comparator) ^ 0xFFFFFFFF;
    if (!isCorrectPosition(paramMediaSet, i))
    {
      this.m_List.remove(j);
      i = Collections.binarySearch(this.m_List, paramMediaSet, this.m_Comparator) ^ 0xFFFFFFFF;
      if (!isCorrectPosition(paramMediaSet, i)) {
        this.m_List.add(j, paramMediaSet);
      }
    }
    else if (i > j) {}
    for (;;)
    {
      ListMoveEventArgs localListMoveEventArgs = new ListMoveEventArgs(j, j, i, i);
      raise(EVENT_MEDIA_SET_MOVING, localListMoveEventArgs);
      if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
        break;
      }
      this.m_List.remove(j);
      this.m_List.add(i, paramMediaSet);
      raise(EVENT_MEDIA_SET_MOVED, localListMoveEventArgs);
      return;
      i -= 1;
      continue;
      this.m_List.add(j, paramMediaSet);
    }
    Log.d(this.TAG, "checkMediaSetIndex() - List has been released");
  }
  
  protected void clearMediaSetLists(boolean paramBoolean)
  {
    verifyAccess();
    if (this.m_List.isEmpty()) {}
    do
    {
      return;
      detachFromMediaSets();
      this.m_List.clear();
    } while (!paramBoolean);
    raise(EVENT_RESET, EventArgs.EMPTY);
  }
  
  public boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public MediaSet get(int paramInt)
  {
    return (MediaSet)this.m_List.get(paramInt);
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_COMPARATOR)
    {
      if (paramPropertyKey != PROP_SHOW_HIDDEN_MEDIA_SETS) {
        return (TValue)super.get(paramPropertyKey);
      }
    }
    else {
      return this.m_Comparator;
    }
    return Boolean.valueOf(this.m_ShowHiddenMediaSets);
  }
  
  public int indexOf(Object paramObject)
  {
    int i;
    if ((paramObject instanceof MediaSet))
    {
      paramObject = (MediaSet)paramObject;
      i = Collections.binarySearch(this.m_List, paramObject, this.m_Comparator);
      if (i >= 0) {
        break label42;
      }
    }
    label42:
    while (this.m_List.get(i) != paramObject)
    {
      return this.m_List.indexOf(paramObject);
      return -1;
    }
    return i;
  }
  
  public void ready()
  {
    setReadOnly(PROP_IS_READY, Boolean.valueOf(true));
  }
  
  public void release()
  {
    clearMediaSetLists(true);
    super.release();
  }
  
  protected boolean removeMediaSet(MediaSet paramMediaSet, boolean paramBoolean)
  {
    return removeMediaSet(paramMediaSet, paramBoolean, false);
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey != PROP_COMPARATOR)
    {
      if (paramPropertyKey != PROP_SHOW_HIDDEN_MEDIA_SETS) {
        return super.set(paramPropertyKey, paramTValue);
      }
    }
    else {
      return setComparatorProp((MediaSetComparator)paramTValue);
    }
    return setShowHiddenMediaSetsProp(((Boolean)paramTValue).booleanValue());
  }
  
  public int size()
  {
    return this.m_List.size();
  }
  
  protected boolean verifyMediaSetToAdd(MediaSet paramMediaSet)
  {
    return paramMediaSet != null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseMediaSetList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */