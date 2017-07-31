package com.oneplus.database;

import android.database.Cursor;

public class CursorUtils
{
  public static double getDouble(Cursor paramCursor, String paramString, double paramDouble)
  {
    if (paramCursor != null) {
      try
      {
        int i = paramCursor.getColumnIndex(paramString);
        if (i >= 0)
        {
          if (paramCursor.isNull(i)) {
            return paramDouble;
          }
          double d = paramCursor.getDouble(i);
          return d;
        }
      }
      catch (Throwable paramCursor) {}
    }
    return paramDouble;
  }
  
  public static int getInt(Cursor paramCursor, String paramString, int paramInt)
  {
    if (paramCursor != null) {
      try
      {
        int i = paramCursor.getColumnIndex(paramString);
        if (i >= 0)
        {
          if (paramCursor.isNull(i)) {
            return paramInt;
          }
          i = paramCursor.getInt(i);
          return i;
        }
      }
      catch (Throwable paramCursor) {}
    }
    return paramInt;
  }
  
  public static long getLong(Cursor paramCursor, String paramString, long paramLong)
  {
    if (paramCursor != null) {
      try
      {
        int i = paramCursor.getColumnIndex(paramString);
        if (i >= 0)
        {
          if (paramCursor.isNull(i)) {
            return paramLong;
          }
          long l = paramCursor.getLong(i);
          return l;
        }
      }
      catch (Throwable paramCursor) {}
    }
    return paramLong;
  }
  
  public static String getString(Cursor paramCursor, String paramString)
  {
    if (paramCursor != null) {
      try
      {
        int i = paramCursor.getColumnIndex(paramString);
        if (i >= 0)
        {
          if (paramCursor.isNull(i)) {
            return null;
          }
          paramCursor = paramCursor.getString(i);
          return paramCursor;
        }
      }
      catch (Throwable paramCursor) {}
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/database/CursorUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */