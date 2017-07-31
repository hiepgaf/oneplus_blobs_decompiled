package android.app.usage;

import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class UsageEvents
  implements Parcelable
{
  public static final Parcelable.Creator<UsageEvents> CREATOR = new Parcelable.Creator()
  {
    public UsageEvents createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UsageEvents(paramAnonymousParcel);
    }
    
    public UsageEvents[] newArray(int paramAnonymousInt)
    {
      return new UsageEvents[paramAnonymousInt];
    }
  };
  private final int mEventCount;
  private List<Event> mEventsToWrite = null;
  private int mIndex = 0;
  private Parcel mParcel = null;
  private String[] mStringPool;
  
  UsageEvents()
  {
    this.mEventCount = 0;
  }
  
  public UsageEvents(Parcel paramParcel)
  {
    this.mEventCount = paramParcel.readInt();
    this.mIndex = paramParcel.readInt();
    if (this.mEventCount > 0)
    {
      this.mStringPool = paramParcel.createStringArray();
      int i = paramParcel.readInt();
      int j = paramParcel.readInt();
      this.mParcel = Parcel.obtain();
      this.mParcel.setDataPosition(0);
      this.mParcel.appendFrom(paramParcel, paramParcel.dataPosition(), i);
      this.mParcel.setDataSize(this.mParcel.dataPosition());
      this.mParcel.setDataPosition(j);
    }
  }
  
  public UsageEvents(List<Event> paramList, String[] paramArrayOfString)
  {
    this.mStringPool = paramArrayOfString;
    this.mEventCount = paramList.size();
    this.mEventsToWrite = paramList;
  }
  
  private int findStringIndex(String paramString)
  {
    int i = Arrays.binarySearch(this.mStringPool, paramString);
    if (i < 0) {
      throw new IllegalStateException("String '" + paramString + "' is not in the string pool");
    }
    return i;
  }
  
  private void readEventFromParcel(Parcel paramParcel, Event paramEvent)
  {
    int i = paramParcel.readInt();
    if (i >= 0)
    {
      paramEvent.mPackage = this.mStringPool[i];
      label19:
      i = paramParcel.readInt();
      if (i < 0) {
        break label109;
      }
    }
    label109:
    for (paramEvent.mClass = this.mStringPool[i];; paramEvent.mClass = null)
    {
      paramEvent.mEventType = paramParcel.readInt();
      paramEvent.mTimeStamp = paramParcel.readLong();
      paramEvent.mConfiguration = null;
      paramEvent.mShortcutId = null;
      switch (paramEvent.mEventType)
      {
      case 6: 
      case 7: 
      default: 
        return;
        paramEvent.mPackage = null;
        break label19;
      }
    }
    paramEvent.mConfiguration = ((Configuration)Configuration.CREATOR.createFromParcel(paramParcel));
    return;
    paramEvent.mShortcutId = paramParcel.readString();
  }
  
  private void writeEventToParcel(Event paramEvent, Parcel paramParcel, int paramInt)
  {
    int i;
    if (paramEvent.mPackage != null)
    {
      i = findStringIndex(paramEvent.mPackage);
      label17:
      if (paramEvent.mClass == null) {
        break label103;
      }
    }
    label103:
    for (int j = findStringIndex(paramEvent.mClass);; j = -1)
    {
      paramParcel.writeInt(i);
      paramParcel.writeInt(j);
      paramParcel.writeInt(paramEvent.mEventType);
      paramParcel.writeLong(paramEvent.mTimeStamp);
      switch (paramEvent.mEventType)
      {
      case 6: 
      case 7: 
      default: 
        return;
        i = -1;
        break label17;
      }
    }
    paramEvent.mConfiguration.writeToParcel(paramParcel, paramInt);
    return;
    paramParcel.writeString(paramEvent.mShortcutId);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean getNextEvent(Event paramEvent)
  {
    if (this.mIndex >= this.mEventCount) {
      return false;
    }
    readEventFromParcel(this.mParcel, paramEvent);
    this.mIndex += 1;
    if (this.mIndex >= this.mEventCount)
    {
      this.mParcel.recycle();
      this.mParcel = null;
    }
    return true;
  }
  
  public boolean hasNextEvent()
  {
    return this.mIndex < this.mEventCount;
  }
  
  public void resetToStart()
  {
    this.mIndex = 0;
    if (this.mParcel != null) {
      this.mParcel.setDataPosition(0);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mEventCount);
    paramParcel.writeInt(this.mIndex);
    Parcel localParcel;
    if (this.mEventCount > 0)
    {
      paramParcel.writeStringArray(this.mStringPool);
      if (this.mEventsToWrite != null) {
        localParcel = Parcel.obtain();
      }
    }
    else
    {
      try
      {
        localParcel.setDataPosition(0);
        int i = 0;
        while (i < this.mEventCount)
        {
          writeEventToParcel((Event)this.mEventsToWrite.get(i), localParcel, paramInt);
          i += 1;
        }
        paramInt = localParcel.dataPosition();
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(0);
        paramParcel.appendFrom(localParcel, 0, paramInt);
        return;
      }
      finally
      {
        localParcel.recycle();
      }
    }
    if (this.mParcel != null)
    {
      paramParcel.writeInt(this.mParcel.dataSize());
      paramParcel.writeInt(this.mParcel.dataPosition());
      paramParcel.appendFrom(this.mParcel, 0, this.mParcel.dataSize());
      return;
    }
    throw new IllegalStateException("Either mParcel or mEventsToWrite must not be null");
  }
  
  public static final class Event
  {
    public static final int CONFIGURATION_CHANGE = 5;
    public static final int CONTINUE_PREVIOUS_DAY = 4;
    public static final int END_OF_DAY = 3;
    public static final int MOVE_TO_BACKGROUND = 2;
    public static final int MOVE_TO_FOREGROUND = 1;
    public static final int NONE = 0;
    public static final int SHORTCUT_INVOCATION = 8;
    public static final int SYSTEM_INTERACTION = 6;
    public static final int USER_INTERACTION = 7;
    public String mClass;
    public Configuration mConfiguration;
    public int mEventType;
    public String mPackage;
    public String mShortcutId;
    public long mTimeStamp;
    
    public String getClassName()
    {
      return this.mClass;
    }
    
    public Configuration getConfiguration()
    {
      return this.mConfiguration;
    }
    
    public int getEventType()
    {
      return this.mEventType;
    }
    
    public String getPackageName()
    {
      return this.mPackage;
    }
    
    public String getShortcutId()
    {
      return this.mShortcutId;
    }
    
    public long getTimeStamp()
    {
      return this.mTimeStamp;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/usage/UsageEvents.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */