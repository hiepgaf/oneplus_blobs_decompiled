package com.oneplus.gallery2.media;

import android.text.format.DateUtils;
import com.oneplus.base.BaseApplication;
import com.oneplus.gallery2.location.AddressClassifier.LocationType;
import java.util.Calendar;

public abstract class MonthGroupedMediaList
  extends TimeGroupedMediaList
{
  protected MonthGroupedMediaList(MediaList paramMediaList, boolean paramBoolean)
  {
    super(paramMediaList, paramBoolean);
  }
  
  protected MonthGroupedMediaList(MonthGroupedMediaList paramMonthGroupedMediaList)
  {
    super(paramMonthGroupedMediaList);
  }
  
  protected long getSeparatorTime(Media paramMedia, long paramLong)
  {
    paramMedia = getCalendar();
    paramMedia.setTimeInMillis(paramLong);
    paramMedia.set(5, paramMedia.getActualMaximum(5));
    paramMedia.set(11, 23);
    paramMedia.set(12, 59);
    paramMedia.set(13, 59);
    paramMedia.set(14, 999);
    return paramMedia.getTimeInMillis();
  }
  
  public static class MonthSeparatorMedia
    extends TimeGroupedMediaList.SeparatorMedia
  {
    private final String m_Id;
    
    public MonthSeparatorMedia(MediaSource paramMediaSource, long paramLong, boolean paramBoolean)
    {
      super(paramLong, paramBoolean);
      this.m_Id = ("MonthGroupedMediaList.MonthSeparatorMedia/" + Long.toString(paramLong));
      updateTitle();
    }
    
    private void updateTitle()
    {
      setReadOnly(PROP_TITLE, DateUtils.formatDateTime(BaseApplication.current(), ((Long)get(PROP_TIME)).longValue(), 36));
    }
    
    public BaseSeparatorMedia clone()
    {
      MediaSource localMediaSource = getSource();
      long l = ((Long)get(PROP_TIME)).longValue();
      if (getAddressClassifier() == null) {}
      for (boolean bool = false;; bool = true) {
        return new MonthSeparatorMedia(localMediaSource, l, bool);
      }
    }
    
    protected AddressClassifier.LocationType getExpectedLocationType()
    {
      return AddressClassifier.LocationType.ADMIN_AREA;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MonthGroupedMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */