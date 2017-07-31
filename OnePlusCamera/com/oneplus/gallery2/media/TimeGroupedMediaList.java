package com.oneplus.gallery2.media;

import android.content.res.Resources;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.gallery2.ListChangeEventArgs;
import com.oneplus.gallery2.location.AddressClassifier;
import com.oneplus.gallery2.location.AddressClassifier.LocationType;
import com.oneplus.gallery2.location.LocationManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class TimeGroupedMediaList
  extends WrappedMediaList
{
  public static final EventKey<ListChangeEventArgs> EVENT_SEPARATOR_MEDIA_ADDED = new EventKey("SeparatorMediaAdded", ListChangeEventArgs.class, TimeGroupedMediaList.class);
  public static final EventKey<ListChangeEventArgs> EVENT_SEPARATOR_MEDIA_REMOVED = new EventKey("SeparatorMediaRemoved", ListChangeEventArgs.class, TimeGroupedMediaList.class);
  public static final EventKey<ListChangeEventArgs> EVENT_SEPARATOR_MEDIA_REMOVING = new EventKey("SeparatorMediaRemoving", ListChangeEventArgs.class, TimeGroupedMediaList.class);
  private final Calendar m_Calendar;
  private final PropertyChangedCallback<Locale> m_LocaleChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Locale> paramAnonymousPropertyKey, PropertyChangeEventArgs<Locale> paramAnonymousPropertyChangeEventArgs)
    {
      TimeGroupedMediaList.this.onLocaleChanged();
    }
  };
  private final MediaSource m_MediaSource = (MediaSource)BaseApplication.current().findComponent(TempMediaSource.class);
  private final List<SeparatorMedia> m_SeparatorMediaList = new ArrayList();
  private final Map<Media, SeparatorMedia> m_SeparatorMediaMap = new HashMap();
  private final int m_TimeZoneDstOffset;
  private final int m_TimeZoneOffset;
  
  protected TimeGroupedMediaList(MediaList paramMediaList, boolean paramBoolean)
  {
    super(paramMediaList, new MediaComparatorByTime(0), -1, paramBoolean);
    ((MediaComparatorByTime)getComparator()).owner = this;
    this.m_Calendar = Calendar.getInstance();
    this.m_TimeZoneOffset = this.m_Calendar.get(15);
    this.m_TimeZoneDstOffset = this.m_Calendar.get(16);
    int i = paramMediaList.size() - 1;
    while (i >= 0)
    {
      addMedia((Media)paramMediaList.get(i));
      i -= 1;
    }
    BaseApplication.current().addCallback(BaseApplication.PROP_LOCALE, this.m_LocaleChangedCallback);
  }
  
  protected TimeGroupedMediaList(TimeGroupedMediaList paramTimeGroupedMediaList)
  {
    super(paramTimeGroupedMediaList.getInternalMediaList(), new MediaComparatorByTime(0), -1, false);
    ((MediaComparatorByTime)getComparator()).owner = this;
    this.m_Calendar = Calendar.getInstance();
    this.m_TimeZoneOffset = this.m_Calendar.get(15);
    this.m_TimeZoneDstOffset = this.m_Calendar.get(16);
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(null);
    int k = 0;
    int m = 0;
    int i = 0;
    if (k < paramTimeGroupedMediaList.size())
    {
      Media localMedia = paramTimeGroupedMediaList.get(k);
      int j;
      if (!(localMedia instanceof DecorationMedia))
      {
        m += 1;
        j = i;
        i = m;
      }
      for (;;)
      {
        k += 1;
        m = i;
        i = j;
        break;
        if (m <= i) {}
        for (;;)
        {
          j = k + 1;
          localObject = ((DecorationMedia)localMedia).clone();
          localArrayList.set(0, localObject);
          super.addMedia(localArrayList, 0, 1, true);
          if ((localObject instanceof SeparatorMedia)) {
            break label255;
          }
          i = j;
          break;
          super.addMedia(paramTimeGroupedMediaList, i, m, true);
        }
        label255:
        Object localObject = (SeparatorMedia)localObject;
        ((SeparatorMedia)localObject).owner = this;
        ((SeparatorMedia)localObject).groupedMediaCount = ((SeparatorMedia)localMedia).groupedMediaCount;
        i = indexOfSeparatorMedia((SeparatorMedia)localObject);
        if (i >= 0)
        {
          i = j;
        }
        else
        {
          this.m_SeparatorMediaList.add(i ^ 0xFFFFFFFF, localObject);
          i = j;
        }
      }
    }
    if (m <= i) {}
    for (;;)
    {
      BaseApplication.current().addCallback(BaseApplication.PROP_LOCALE, this.m_LocaleChangedCallback);
      return;
      super.addMedia(paramTimeGroupedMediaList, i, m, true);
    }
  }
  
  private SeparatorMedia findSeparatorMedia(Media paramMedia, boolean paramBoolean)
  {
    long l = getSeparatorTime(paramMedia, getMediaTime(paramMedia));
    int i = indexOfSeparatorMedia(l);
    if (i >= 0) {
      if (i < 0) {
        break label74;
      }
    }
    for (;;)
    {
      return (SeparatorMedia)this.m_SeparatorMediaList.get(i);
      if (paramBoolean) {
        break;
      }
      paramMedia = (SeparatorMedia)this.m_SeparatorMediaMap.get(paramMedia);
      if (paramMedia != null)
      {
        i = indexOfSeparatorMedia(paramMedia);
        break;
      }
      return null;
      label74:
      if (!paramBoolean) {
        break label136;
      }
      paramMedia = createSeparatorMedia(this.m_MediaSource, l);
      if (paramMedia == null) {
        break label138;
      }
      i ^= 0xFFFFFFFF;
      paramMedia.owner = this;
      this.m_SeparatorMediaList.add(i, paramMedia);
      int j = super.addMedia(paramMedia);
      if (j < 0) {
        break label140;
      }
      onSeparatorMediaAdded(i, j, paramMedia);
    }
    label136:
    return null;
    label138:
    return null;
    label140:
    this.m_SeparatorMediaList.remove(i);
    return null;
  }
  
  private int indexOfSeparatorMedia(long paramLong)
  {
    int j = this.m_SeparatorMediaList.size() - 1;
    int i = 0;
    while (j >= i)
    {
      int m = (i + j) / 2;
      long l = ((Long)((SeparatorMedia)this.m_SeparatorMediaList.get(m)).get(SeparatorMedia.PROP_TIME)).longValue();
      if (l == paramLong) {
        return m;
      }
      if (paramLong <= l) {}
      for (int k = 1;; k = 0)
      {
        if (k != 0) {
          break label97;
        }
        j = m - 1;
        break;
      }
      label97:
      i = m + 1;
    }
    return i ^ 0xFFFFFFFF;
  }
  
  private boolean removeSeparatorMedia(SeparatorMedia paramSeparatorMedia)
  {
    int i = indexOfSeparatorMedia(((Long)paramSeparatorMedia.get(SeparatorMedia.PROP_TIME)).longValue());
    int j = super.indexOf(paramSeparatorMedia);
    if (i < 0) {}
    while (j < 0) {
      return false;
    }
    onSeparatorMediaRemoving(i, j, paramSeparatorMedia);
    super.removeMedia(paramSeparatorMedia);
    this.m_SeparatorMediaList.remove(i);
    onSeparatorMediaRemoved(i, j, paramSeparatorMedia);
    paramSeparatorMedia.release();
    return true;
  }
  
  protected int addMedia(Media paramMedia)
  {
    SeparatorMedia localSeparatorMedia;
    int i;
    if (paramMedia != null)
    {
      if (!(paramMedia instanceof SeparatorMedia))
      {
        localSeparatorMedia = findSeparatorMedia(paramMedia, true);
        if (localSeparatorMedia == null) {
          break label65;
        }
        localSeparatorMedia.groupedMediaCount += 1;
        i = super.addMedia(paramMedia);
        if (i >= 0) {
          break label67;
        }
        localSeparatorMedia.groupedMediaCount -= 1;
        if (localSeparatorMedia.groupedMediaCount <= 0) {
          break label86;
        }
        return i;
      }
    }
    else {
      return -1;
    }
    return -1;
    label65:
    return -1;
    label67:
    this.m_SeparatorMediaMap.put(paramMedia, localSeparatorMedia);
    localSeparatorMedia.onMediaAddedToGroup(paramMedia);
    return i;
    label86:
    removeSeparatorMedia(localSeparatorMedia);
    return i;
  }
  
  protected void clearMedia()
  {
    super.clearMedia();
    int i = this.m_SeparatorMediaList.size() - 1;
    while (i >= 0)
    {
      ((SeparatorMedia)this.m_SeparatorMediaList.get(i)).release();
      i -= 1;
    }
    this.m_SeparatorMediaList.clear();
  }
  
  protected int compareMedia(Media paramMedia1, Media paramMedia2)
  {
    boolean bool1 = paramMedia1 instanceof SeparatorMedia;
    boolean bool2 = paramMedia2 instanceof SeparatorMedia;
    long l1;
    long l2;
    if (!bool1)
    {
      l1 = getMediaTime(paramMedia1);
      if (bool2) {
        break label79;
      }
      l2 = getMediaTime(paramMedia2);
      label36:
      l1 = l2 - l1;
      if (l1 < 0L) {
        break label100;
      }
    }
    label79:
    label100:
    for (int i = 1;; i = 0)
    {
      if (i != 0) {
        break label105;
      }
      return -1;
      l1 = ((Long)((SeparatorMedia)paramMedia1).get(SeparatorMedia.PROP_TIME)).longValue();
      break;
      l2 = ((Long)((SeparatorMedia)paramMedia2).get(SeparatorMedia.PROP_TIME)).longValue();
      break label36;
    }
    label105:
    if (l1 <= 0L) {}
    for (i = 1; i == 0; i = 0) {
      return 1;
    }
    if (!bool1)
    {
      if (bool2) {}
    }
    else
    {
      while (bool2) {
        return MediaComparator.FILE_PATH_DESC.compare(paramMedia1, paramMedia2);
      }
      return -1;
    }
    return 1;
  }
  
  protected abstract SeparatorMedia createSeparatorMedia(MediaSource paramMediaSource, long paramLong);
  
  public SeparatorMedia findSeparatorMedia(Media paramMedia)
  {
    if (paramMedia == null) {
      return null;
    }
    return findSeparatorMedia(paramMedia, false);
  }
  
  public Calendar getCalendar()
  {
    return this.m_Calendar;
  }
  
  protected abstract long getMediaTime(Media paramMedia);
  
  public SeparatorMedia getSeparatorMedia(int paramInt)
  {
    return (SeparatorMedia)this.m_SeparatorMediaList.get(paramInt);
  }
  
  public int getSeparatorMediaCount()
  {
    return this.m_SeparatorMediaList.size();
  }
  
  protected abstract long getSeparatorTime(Media paramMedia, long paramLong);
  
  public final int getTimeZoneDstOffset()
  {
    return this.m_TimeZoneDstOffset;
  }
  
  public final int getTimeZoneOffset()
  {
    return this.m_TimeZoneOffset;
  }
  
  public int indexOfSeparatorMedia(Media paramMedia)
  {
    int i;
    if (paramMedia != null)
    {
      if (!(paramMedia instanceof SeparatorMedia))
      {
        i = indexOfSeparatorMedia(getSeparatorTime(paramMedia, getMediaTime(paramMedia)));
        if (i >= 0) {
          return i;
        }
        return -1;
      }
    }
    else {
      return -1;
    }
    return indexOfSeparatorMedia((SeparatorMedia)paramMedia);
    return i;
  }
  
  public int indexOfSeparatorMedia(SeparatorMedia paramSeparatorMedia)
  {
    int i;
    if (paramSeparatorMedia != null)
    {
      i = indexOfSeparatorMedia(((Long)paramSeparatorMedia.get(SeparatorMedia.PROP_TIME)).longValue());
      if (i >= 0) {
        break label39;
      }
    }
    label39:
    while (this.m_SeparatorMediaList.get(i) != paramSeparatorMedia)
    {
      return this.m_SeparatorMediaList.indexOf(paramSeparatorMedia);
      return -1;
    }
    return i;
  }
  
  protected boolean moveMedia(int paramInt1, int paramInt2)
  {
    Media localMedia = super.get(paramInt1);
    int i;
    if (!(localMedia instanceof SeparatorMedia))
    {
      i = paramInt1 - 1;
      for (;;)
      {
        if (i < 0) {
          break label243;
        }
        localObject = super.get(i);
        if ((localObject instanceof SeparatorMedia)) {
          break;
        }
        i -= 1;
      }
    }
    return false;
    label171:
    label178:
    label183:
    label211:
    label218:
    label233:
    label243:
    for (Object localObject = (SeparatorMedia)localObject;; localObject = null)
    {
      SeparatorMedia localSeparatorMedia;
      if (localObject != null)
      {
        localSeparatorMedia = findSeparatorMedia(localMedia, false);
        if (localObject == localSeparatorMedia) {
          break label171;
        }
        if (localSeparatorMedia == null) {
          break label178;
        }
        i = 0;
        if (i != 0) {
          break label183;
        }
        if (!super.moveMedia(paramInt1, paramInt2)) {
          break label218;
        }
        ((SeparatorMedia)localObject).groupedMediaCount -= 1;
        ((SeparatorMedia)localObject).onMediaRemovedFromGroup(localMedia);
        if (((SeparatorMedia)localObject).groupedMediaCount <= 0) {
          break label233;
        }
      }
      for (;;)
      {
        localSeparatorMedia.groupedMediaCount += 1;
        this.m_SeparatorMediaMap.remove(localMedia);
        this.m_SeparatorMediaMap.put(localMedia, localSeparatorMedia);
        localSeparatorMedia.onMediaAddedToGroup(localMedia);
        return true;
        return false;
        return super.moveMedia(paramInt1, paramInt2);
        i = 1;
        break;
        localSeparatorMedia = findSeparatorMedia(localMedia, true);
        if (localSeparatorMedia != null) {
          if (paramInt2 < paramInt1) {
            break label211;
          }
        }
        for (;;)
        {
          paramInt2 += 1;
          break;
          return false;
          paramInt1 += 1;
        }
        if (i == 0) {
          return false;
        }
        removeSeparatorMedia(localSeparatorMedia);
        return false;
        removeSeparatorMedia((SeparatorMedia)localObject);
      }
    }
  }
  
  protected void onLocaleChanged()
  {
    int i = this.m_SeparatorMediaList.size() - 1;
    while (i >= 0)
    {
      ((SeparatorMedia)this.m_SeparatorMediaList.get(i)).onLocaleChanged();
      i -= 1;
    }
  }
  
  protected void onMediaAddedToInternalMediaList(ListChangeEventArgs paramListChangeEventArgs)
  {
    MediaList localMediaList = getInternalMediaList();
    int i = paramListChangeEventArgs.getStartIndex();
    int j = paramListChangeEventArgs.getEndIndex();
    while (i <= j)
    {
      addMedia((Media)localMediaList.get(i));
      i += 1;
    }
  }
  
  protected void onSeparatorMediaAdded(int paramInt1, int paramInt2, SeparatorMedia paramSeparatorMedia)
  {
    paramSeparatorMedia = ListChangeEventArgs.obtain(paramInt1);
    raise(EVENT_SEPARATOR_MEDIA_ADDED, paramSeparatorMedia);
    paramSeparatorMedia.recycle();
  }
  
  protected void onSeparatorMediaRemoved(int paramInt1, int paramInt2, SeparatorMedia paramSeparatorMedia)
  {
    paramSeparatorMedia = ListChangeEventArgs.obtain(paramInt1);
    raise(EVENT_SEPARATOR_MEDIA_REMOVED, paramSeparatorMedia);
    paramSeparatorMedia.recycle();
  }
  
  protected void onSeparatorMediaRemoving(int paramInt1, int paramInt2, SeparatorMedia paramSeparatorMedia)
  {
    paramSeparatorMedia = ListChangeEventArgs.obtain(paramInt1);
    raise(EVENT_SEPARATOR_MEDIA_REMOVING, paramSeparatorMedia);
    paramSeparatorMedia.recycle();
  }
  
  public void release()
  {
    super.release();
    int i = this.m_SeparatorMediaList.size() - 1;
    while (i >= 0)
    {
      ((SeparatorMedia)this.m_SeparatorMediaList.get(i)).release();
      i -= 1;
    }
    this.m_SeparatorMediaList.clear();
    BaseApplication.current().removeCallback(BaseApplication.PROP_LOCALE, this.m_LocaleChangedCallback);
  }
  
  protected boolean removeMedia(Media paramMedia)
  {
    SeparatorMedia localSeparatorMedia;
    if (paramMedia != null)
    {
      localSeparatorMedia = findSeparatorMedia(paramMedia, false);
      if (localSeparatorMedia == null) {
        break label60;
      }
      localSeparatorMedia.groupedMediaCount -= 1;
      localSeparatorMedia.onMediaRemovedFromGroup(paramMedia);
      if (localSeparatorMedia.groupedMediaCount <= 0) {
        break label62;
      }
    }
    while (super.removeMedia(paramMedia))
    {
      this.m_SeparatorMediaMap.remove(paramMedia);
      return true;
      return false;
      label60:
      return false;
      label62:
      removeSeparatorMedia(localSeparatorMedia);
    }
    return false;
  }
  
  private static final class MediaComparatorByTime
    extends MediaComparator
  {
    public TimeGroupedMediaList owner;
    
    public MediaComparatorByTime(int paramInt)
    {
      super();
    }
    
    public int compare(Media paramMedia1, Media paramMedia2)
    {
      return this.owner.compareMedia(paramMedia1, paramMedia2);
    }
  }
  
  public static abstract class SeparatorMedia
    extends BaseSeparatorMedia
    implements TimeSeparatorMedia
  {
    private static final int DELAY_UPDATE_SUMMARY = 500;
    int groupedMediaCount;
    private final AddressClassifier m_AddressClassifier;
    private boolean m_IsSummaryUpdateScheduled;
    private final Long m_Time;
    private final Runnable m_UpdateSummaryRunnable;
    TimeGroupedMediaList owner;
    
    protected SeparatorMedia(MediaSource paramMediaSource, long paramLong, boolean paramBoolean)
    {
      super();
      this.m_Time = Long.valueOf(paramLong);
      if (!paramBoolean)
      {
        this.m_AddressClassifier = null;
        if (this.m_AddressClassifier == null) {
          this.m_UpdateSummaryRunnable = null;
        }
      }
      else
      {
        paramMediaSource = (LocationManager)BaseApplication.current().findComponent(LocationManager.class);
        if (paramMediaSource == null) {}
        for (paramMediaSource = null;; paramMediaSource = paramMediaSource.createAddressClassifier(0))
        {
          this.m_AddressClassifier = paramMediaSource;
          break;
        }
      }
      this.m_UpdateSummaryRunnable = new Runnable()
      {
        public void run()
        {
          TimeGroupedMediaList.SeparatorMedia.this.m_IsSummaryUpdateScheduled = false;
          TimeGroupedMediaList.SeparatorMedia.this.updateSummary();
        }
      };
      this.m_AddressClassifier.addHandler(AddressClassifier.EVENT_UPDATED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
        {
          if (TimeGroupedMediaList.SeparatorMedia.this.m_IsSummaryUpdateScheduled) {
            return;
          }
          TimeGroupedMediaList.SeparatorMedia.this.m_IsSummaryUpdateScheduled = true;
          HandlerUtils.post(TimeGroupedMediaList.SeparatorMedia.this.getHandler(), TimeGroupedMediaList.SeparatorMedia.this.m_UpdateSummaryRunnable, 500L);
        }
      });
    }
    
    private void updateSummary()
    {
      List localList = this.m_AddressClassifier.getLocationNameList(getExpectedLocationType(), 0);
      if (localList == null) {}
      while (localList.isEmpty())
      {
        setReadOnly(PROP_SUMMARY, null);
        return;
      }
      Object localObject = BaseApplication.current().getResources();
      int i = ((Resources)localObject).getIdentifier("address_comma", "string", "com.oneplus.gallery");
      if (i <= 0) {}
      StringBuilder localStringBuilder;
      for (localObject = ", ";; localObject = ((Resources)localObject).getString(i))
      {
        localStringBuilder = new StringBuilder((String)localList.get(0));
        int j = localList.size();
        i = 1;
        while (i < j)
        {
          localStringBuilder.append((String)localObject);
          localStringBuilder.append((String)localList.get(i));
          if (localStringBuilder.length() > 256) {
            break;
          }
          i += 1;
        }
      }
      setReadOnly(PROP_SUMMARY, localStringBuilder);
    }
    
    public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
    {
      if (paramPropertyKey != PROP_TIME) {
        return (TValue)super.get(paramPropertyKey);
      }
      return this.m_Time;
    }
    
    public AddressClassifier getAddressClassifier()
    {
      return this.m_AddressClassifier;
    }
    
    protected AddressClassifier.LocationType getExpectedLocationType()
    {
      return AddressClassifier.LocationType.LOCALITY;
    }
    
    public int getGroupedMediaCount()
    {
      return this.groupedMediaCount;
    }
    
    protected TimeGroupedMediaList getMediaList()
    {
      return this.owner;
    }
    
    protected void onLocaleChanged() {}
    
    protected void onMediaAddedToGroup(Media paramMedia)
    {
      if (this.m_IsSummaryUpdateScheduled) {}
      while (this.m_AddressClassifier == null)
      {
        return;
        this.m_IsSummaryUpdateScheduled = true;
        HandlerUtils.post(getHandler(), this.m_UpdateSummaryRunnable, 0L);
      }
      this.m_AddressClassifier.addMedia(paramMedia, 0);
    }
    
    protected void onMediaRemovedFromGroup(Media paramMedia)
    {
      if (this.m_AddressClassifier == null) {
        return;
      }
      this.m_AddressClassifier.removeMedia(paramMedia, 0);
    }
    
    public void release()
    {
      if (this.m_AddressClassifier == null) {
        if (this.m_IsSummaryUpdateScheduled) {
          break label32;
        }
      }
      for (;;)
      {
        this.owner = null;
        return;
        this.m_AddressClassifier.release();
        break;
        label32:
        this.m_IsSummaryUpdateScheduled = false;
        HandlerUtils.removeCallbacks(this, this.m_UpdateSummaryRunnable);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/TimeGroupedMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */