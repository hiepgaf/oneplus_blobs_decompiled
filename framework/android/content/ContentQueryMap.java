package android.content;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class ContentQueryMap
  extends Observable
{
  private String[] mColumnNames;
  private ContentObserver mContentObserver;
  private volatile Cursor mCursor;
  private boolean mDirty = false;
  private Handler mHandlerForUpdateNotifications = null;
  private boolean mKeepUpdated = false;
  private int mKeyColumn;
  private Map<String, ContentValues> mValues = null;
  
  public ContentQueryMap(Cursor paramCursor, String paramString, boolean paramBoolean, Handler paramHandler)
  {
    this.mCursor = paramCursor;
    this.mColumnNames = this.mCursor.getColumnNames();
    this.mKeyColumn = this.mCursor.getColumnIndexOrThrow(paramString);
    this.mHandlerForUpdateNotifications = paramHandler;
    setKeepUpdated(paramBoolean);
    if (!paramBoolean) {
      readCursorIntoCache(paramCursor);
    }
  }
  
  private void readCursorIntoCache(Cursor paramCursor)
  {
    for (;;)
    {
      try
      {
        if (this.mValues == null) {
          break label126;
        }
        i = this.mValues.size();
        this.mValues = new HashMap(i);
        if (paramCursor.moveToNext())
        {
          ContentValues localContentValues = new ContentValues();
          i = 0;
          if (i < this.mColumnNames.length)
          {
            if (i == this.mKeyColumn) {
              break label119;
            }
            localContentValues.put(this.mColumnNames[i], paramCursor.getString(i));
            break label119;
          }
          this.mValues.put(paramCursor.getString(this.mKeyColumn), localContentValues);
          continue;
        }
      }
      finally {}
      return;
      label119:
      i += 1;
      continue;
      label126:
      int i = 0;
    }
  }
  
  public void close()
  {
    try
    {
      if (this.mContentObserver != null)
      {
        this.mCursor.unregisterContentObserver(this.mContentObserver);
        this.mContentObserver = null;
      }
      this.mCursor.close();
      this.mCursor = null;
      return;
    }
    finally {}
  }
  
  protected void finalize()
    throws Throwable
  {
    if (this.mCursor != null) {
      close();
    }
    super.finalize();
  }
  
  public Map<String, ContentValues> getRows()
  {
    try
    {
      if (this.mDirty) {
        requery();
      }
      Map localMap = this.mValues;
      return localMap;
    }
    finally {}
  }
  
  public ContentValues getValues(String paramString)
  {
    try
    {
      if (this.mDirty) {
        requery();
      }
      paramString = (ContentValues)this.mValues.get(paramString);
      return paramString;
    }
    finally {}
  }
  
  public void requery()
  {
    Cursor localCursor = this.mCursor;
    if (localCursor == null) {
      return;
    }
    this.mDirty = false;
    if (!localCursor.requery()) {
      return;
    }
    readCursorIntoCache(localCursor);
    setChanged();
    notifyObservers();
  }
  
  public void setKeepUpdated(boolean paramBoolean)
  {
    if (paramBoolean == this.mKeepUpdated) {
      return;
    }
    this.mKeepUpdated = paramBoolean;
    if (!this.mKeepUpdated)
    {
      this.mCursor.unregisterContentObserver(this.mContentObserver);
      this.mContentObserver = null;
      return;
    }
    if (this.mHandlerForUpdateNotifications == null) {
      this.mHandlerForUpdateNotifications = new Handler();
    }
    if (this.mContentObserver == null) {
      this.mContentObserver = new ContentObserver(this.mHandlerForUpdateNotifications)
      {
        public void onChange(boolean paramAnonymousBoolean)
        {
          if (ContentQueryMap.this.countObservers() != 0)
          {
            ContentQueryMap.this.requery();
            return;
          }
          ContentQueryMap.-set0(ContentQueryMap.this, true);
        }
      };
    }
    this.mCursor.registerContentObserver(this.mContentObserver);
    this.mDirty = true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentQueryMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */