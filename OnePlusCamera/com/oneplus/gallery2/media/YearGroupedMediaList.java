package com.oneplus.gallery2.media;

import android.text.format.DateFormat;
import com.oneplus.gallery2.location.AddressClassifier.LocationType;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class YearGroupedMediaList
  extends TimeGroupedMediaList
{
  protected YearGroupedMediaList(MediaList paramMediaList, boolean paramBoolean)
  {
    super(paramMediaList, paramBoolean);
  }
  
  protected YearGroupedMediaList(YearGroupedMediaList paramYearGroupedMediaList)
  {
    super(paramYearGroupedMediaList);
  }
  
  protected long getSeparatorTime(Media paramMedia, long paramLong)
  {
    paramMedia = getCalendar();
    paramMedia.setTimeInMillis(paramLong);
    paramMedia.set(6, paramMedia.getActualMaximum(6));
    paramMedia.set(11, 23);
    paramMedia.set(12, 59);
    paramMedia.set(13, 59);
    paramMedia.set(14, 999);
    return paramMedia.getTimeInMillis();
  }
  
  public static class YearSeparatorMedia
    extends TimeGroupedMediaList.SeparatorMedia
  {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyy"), Locale.getDefault());
    private final String m_Id;
    
    public YearSeparatorMedia(MediaSource paramMediaSource, long paramLong, boolean paramBoolean)
    {
      super(paramLong, paramBoolean);
      this.m_Id = ("YearGroupedMediaList.YearSeparatorMedia/" + Long.toString(paramLong));
      updateTitle();
    }
    
    private void updateTitle()
    {
      setReadOnly(PROP_TITLE, DATE_FORMAT.format(new Date(((Long)get(PROP_TIME)).longValue())));
    }
    
    public BaseSeparatorMedia clone()
    {
      MediaSource localMediaSource = getSource();
      long l = ((Long)get(PROP_TIME)).longValue();
      if (getAddressClassifier() == null) {}
      for (boolean bool = false;; bool = true) {
        return new YearSeparatorMedia(localMediaSource, l, bool);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/YearGroupedMediaList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */