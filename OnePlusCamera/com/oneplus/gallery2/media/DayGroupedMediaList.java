package com.oneplus.gallery2.media;

import android.text.format.DateUtils;
import com.oneplus.base.BaseApplication;
import com.oneplus.gallery2.location.AddressClassifier.LocationType;

public abstract class DayGroupedMediaList
  extends TimeGroupedMediaList
{
  private static final long DAY_IN_MILLIS = 86400000L;
  
  protected DayGroupedMediaList(DayGroupedMediaList paramDayGroupedMediaList)
  {
    super(paramDayGroupedMediaList);
  }
  
  protected DayGroupedMediaList(MediaList paramMediaList, boolean paramBoolean)
  {
    super(paramMediaList, paramBoolean);
  }
  
  protected long getSeparatorTime(Media paramMedia, long paramLong)
  {
    paramLong = getTimeZoneOffset() + paramLong + getTimeZoneDstOffset();
    return paramLong - paramLong % 86400000L + 86399999L - getTimeZoneOffset() - getTimeZoneDstOffset();
  }
  
  public static class DaySeparatorMedia
    extends TimeGroupedMediaList.SeparatorMedia
  {
    private final String m_Id;
    
    public DaySeparatorMedia(MediaSource paramMediaSource, long paramLong, boolean paramBoolean)
    {
      super(paramLong, paramBoolean);
      this.m_Id = ("DayGroupedMediaList.DaySeparatorMedia/" + Long.toString(paramLong));
      updateTitle();
    }
    
    private void updateTitle()
    {
      setReadOnly(PROP_TITLE, DateUtils.formatDateTime(BaseApplication.current(), ((Long)get(PROP_TIME)).longValue(), 4));
    }
    
    public BaseSeparatorMedia clone()
    {
      MediaSource localMediaSource = getSource();
      long l = ((Long)get(PROP_TIME)).longValue();
      if (getAddressClassifier() == null) {}
      for (boolean bool = false;; bool = true) {
        return new DaySeparatorMedia(localMediaSource, l, bool);
      }
    }
    
    protected AddressClassifier.LocationType getExpectedLocationType()
    {
      return AddressClassifier.LocationType.LOCALITY;
    }
    
    public String getId()
    {
      return this.m_Id;
    }
    
    protected void onLocaleChanged()
    {
      super.onLocaleChanged();
      updateTitle();
    }
    
    public String toString()
    {
      return "[" + get(PROP_TITLE) + "]";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/DayGroupedMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */