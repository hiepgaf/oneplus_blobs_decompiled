package com.amap.api.mapcore2d;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class dm
  extends SQLiteOpenHelper
{
  private dg a;
  
  public dm(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt, dg paramdg)
  {
    super(paramContext, paramString, paramCursorFactory, paramInt);
    this.a = paramdg;
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    this.a.a(paramSQLiteDatabase);
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    this.a.a(paramSQLiteDatabase, paramInt1, paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */