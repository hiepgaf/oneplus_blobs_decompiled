package com.android.server.voiceinteraction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.soundtrigger.SoundTrigger.Keyphrase;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel;
import android.text.TextUtils;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DatabaseHelper
  extends SQLiteOpenHelper
{
  private static final String CREATE_TABLE_SOUND_MODEL = "CREATE TABLE sound_model(model_uuid TEXT,vendor_uuid TEXT,keyphrase_id INTEGER,type INTEGER,data BLOB,recognition_modes INTEGER,locale TEXT,hint_text TEXT,users TEXT,PRIMARY KEY (keyphrase_id,locale,users))";
  static final boolean DBG = false;
  private static final String NAME = "sound_model.db";
  static final String TAG = "SoundModelDBHelper";
  private static final int VERSION = 6;
  
  public DatabaseHelper(Context paramContext)
  {
    super(paramContext, "sound_model.db", null, 6);
  }
  
  private static int[] getArrayForCommaSeparatedString(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    paramString = paramString.split(",");
    int[] arrayOfInt = new int[paramString.length];
    int i = 0;
    while (i < paramString.length)
    {
      arrayOfInt[i] = Integer.parseInt(paramString[i]);
      i += 1;
    }
    return arrayOfInt;
  }
  
  private static String getCommaSeparatedString(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append(paramArrayOfInt[i]);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  /* Error */
  public boolean deleteKeyphraseSoundModel(int paramInt1, int paramInt2, String paramString)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: aload_3
    //   4: invokestatic 82	java/util/Locale:forLanguageTag	(Ljava/lang/String;)Ljava/util/Locale;
    //   7: invokevirtual 85	java/util/Locale:toLanguageTag	()Ljava/lang/String;
    //   10: astore_3
    //   11: aload_0
    //   12: monitorenter
    //   13: aload_0
    //   14: iload_1
    //   15: iload_2
    //   16: aload_3
    //   17: invokevirtual 89	com/android/server/voiceinteraction/DatabaseHelper:getKeyphraseSoundModel	(IILjava/lang/String;)Landroid/hardware/soundtrigger/SoundTrigger$KeyphraseSoundModel;
    //   20: astore 5
    //   22: aload 5
    //   24: ifnonnull +7 -> 31
    //   27: aload_0
    //   28: monitorexit
    //   29: iconst_0
    //   30: ireturn
    //   31: aload_0
    //   32: invokevirtual 93	com/android/server/voiceinteraction/DatabaseHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   35: astore_3
    //   36: new 60	java/lang/StringBuilder
    //   39: dup
    //   40: invokespecial 63	java/lang/StringBuilder:<init>	()V
    //   43: ldc 95
    //   45: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: aload 5
    //   50: getfield 104	android/hardware/soundtrigger/SoundTrigger$KeyphraseSoundModel:uuid	Ljava/util/UUID;
    //   53: invokevirtual 107	java/util/UUID:toString	()Ljava/lang/String;
    //   56: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: ldc 109
    //   61: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   64: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   67: astore 5
    //   69: aload_3
    //   70: ldc 111
    //   72: aload 5
    //   74: aconst_null
    //   75: invokevirtual 117	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   78: istore_1
    //   79: iload_1
    //   80: ifeq +6 -> 86
    //   83: iconst_1
    //   84: istore 4
    //   86: aload_3
    //   87: invokevirtual 120	android/database/sqlite/SQLiteDatabase:close	()V
    //   90: aload_0
    //   91: monitorexit
    //   92: iload 4
    //   94: ireturn
    //   95: astore 5
    //   97: aload_3
    //   98: invokevirtual 120	android/database/sqlite/SQLiteDatabase:close	()V
    //   101: aload 5
    //   103: athrow
    //   104: astore_3
    //   105: aload_0
    //   106: monitorexit
    //   107: aload_3
    //   108: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	109	0	this	DatabaseHelper
    //   0	109	1	paramInt1	int
    //   0	109	2	paramInt2	int
    //   0	109	3	paramString	String
    //   1	92	4	bool	boolean
    //   20	53	5	localObject1	Object
    //   95	7	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   69	79	95	finally
    //   13	22	104	finally
    //   31	69	104	finally
    //   86	90	104	finally
    //   97	104	104	finally
  }
  
  public SoundTrigger.KeyphraseSoundModel getKeyphraseSoundModel(int paramInt1, int paramInt2, String paramString)
  {
    paramString = Locale.forLanguageTag(paramString).toLanguageTag();
    for (;;)
    {
      SQLiteDatabase localSQLiteDatabase;
      Cursor localCursor;
      String str1;
      try
      {
        paramString = "SELECT  * FROM sound_model WHERE keyphrase_id= '" + paramInt1 + "' AND " + "locale" + "='" + paramString + "'";
        localSQLiteDatabase = getReadableDatabase();
        localCursor = localSQLiteDatabase.rawQuery(paramString, null);
        try
        {
          if (localCursor.moveToFirst())
          {
            if (localCursor.getInt(localCursor.getColumnIndex("type")) != 0) {
              if (localCursor.moveToNext()) {
                continue;
              }
            }
          }
          else
          {
            Slog.w("SoundModelDBHelper", "No SoundModel available for the given keyphrase");
            localCursor.close();
            localSQLiteDatabase.close();
            return null;
          }
          str1 = localCursor.getString(localCursor.getColumnIndex("model_uuid"));
          if (str1 == null)
          {
            Slog.w("SoundModelDBHelper", "Ignoring SoundModel since it doesn't specify an ID");
            continue;
            continue;
            paramString = finally;
          }
        }
        finally
        {
          localCursor.close();
          localSQLiteDatabase.close();
        }
        paramString = null;
      }
      finally {}
      int i = localCursor.getColumnIndex("vendor_uuid");
      if (i != -1) {
        paramString = localCursor.getString(i);
      }
      byte[] arrayOfByte = localCursor.getBlob(localCursor.getColumnIndex("data"));
      int m = localCursor.getInt(localCursor.getColumnIndex("recognition_modes"));
      Object localObject1 = getArrayForCommaSeparatedString(localCursor.getString(localCursor.getColumnIndex("users")));
      Object localObject2 = localCursor.getString(localCursor.getColumnIndex("locale"));
      String str2 = localCursor.getString(localCursor.getColumnIndex("hint_text"));
      if (localObject1 == null)
      {
        Slog.w("SoundModelDBHelper", "Ignoring SoundModel since it doesn't specify users");
      }
      else
      {
        int k = 0;
        int j = 0;
        int n = localObject1.length;
        while (i != 0)
        {
          localObject2 = new SoundTrigger.Keyphrase(paramInt1, m, (String)localObject2, str2, (int[])localObject1);
          localObject1 = null;
          if (paramString != null) {
            localObject1 = UUID.fromString(paramString);
          }
          paramString = new SoundTrigger.KeyphraseSoundModel(UUID.fromString(str1), (UUID)localObject1, arrayOfByte, new SoundTrigger.Keyphrase[] { localObject2 });
          localCursor.close();
          localSQLiteDatabase.close();
          return paramString;
          do
          {
            j += 1;
            i = k;
            if (j >= n) {
              break;
            }
          } while (paramInt2 != localObject1[j]);
          i = 1;
        }
      }
    }
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE sound_model(model_uuid TEXT,vendor_uuid TEXT,keyphrase_id INTEGER,type INTEGER,data BLOB,recognition_modes INTEGER,locale TEXT,hint_text TEXT,users TEXT,PRIMARY KEY (keyphrase_id,locale,users))");
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    if (paramInt1 < 4)
    {
      paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS sound_model");
      onCreate(paramSQLiteDatabase);
      paramInt2 = paramInt1;
    }
    while (paramInt2 == 5)
    {
      Object localObject = paramSQLiteDatabase.rawQuery("SELECT * FROM sound_model", null);
      ArrayList localArrayList = new ArrayList();
      try
      {
        boolean bool = ((Cursor)localObject).moveToFirst();
        if (bool) {}
        try
        {
          do
          {
            localArrayList.add(new SoundModelRecord(5, (Cursor)localObject));
            bool = ((Cursor)localObject).moveToNext();
          } while (bool);
          ((Cursor)localObject).close();
          paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS sound_model");
          onCreate(paramSQLiteDatabase);
          localObject = localArrayList.iterator();
          for (;;)
          {
            if (((Iterator)localObject).hasNext())
            {
              SoundModelRecord localSoundModelRecord = (SoundModelRecord)((Iterator)localObject).next();
              if (localSoundModelRecord.ifViolatesV6PrimaryKeyIsFirstOfAnyDuplicates(localArrayList))
              {
                try
                {
                  long l = localSoundModelRecord.writeToDatabase(6, paramSQLiteDatabase);
                  if (l != -1L) {
                    continue;
                  }
                  Slog.e("SoundModelDBHelper", "Database write failed " + localSoundModelRecord.modelUuid + ": " + l);
                }
                catch (Exception localException2)
                {
                  Slog.e("SoundModelDBHelper", "Failed to update V6 record " + localSoundModelRecord.modelUuid, localException2);
                }
                continue;
                paramInt2 = paramInt1;
                if (paramInt1 != 4) {
                  break;
                }
                Slog.d("SoundModelDBHelper", "Adding vendor UUID column");
                paramSQLiteDatabase.execSQL("ALTER TABLE sound_model ADD COLUMN vendor_uuid TEXT");
                paramInt2 = paramInt1 + 1;
              }
            }
          }
        }
        catch (Exception localException1)
        {
          for (;;)
          {
            Slog.e("SoundModelDBHelper", "Failed to extract V5 record", localException1);
          }
        }
        return;
      }
      finally
      {
        ((Cursor)localObject).close();
      }
    }
  }
  
  public boolean updateKeyphraseSoundModel(SoundTrigger.KeyphraseSoundModel paramKeyphraseSoundModel)
  {
    boolean bool = true;
    try
    {
      localSQLiteDatabase = getWritableDatabase();
      localContentValues = new ContentValues();
      localContentValues.put("model_uuid", paramKeyphraseSoundModel.uuid.toString());
      if (paramKeyphraseSoundModel.vendorUuid != null) {
        localContentValues.put("vendor_uuid", paramKeyphraseSoundModel.vendorUuid.toString());
      }
      localContentValues.put("type", Integer.valueOf(0));
      localContentValues.put("data", paramKeyphraseSoundModel.data);
      if ((paramKeyphraseSoundModel.keyphrases != null) && (paramKeyphraseSoundModel.keyphrases.length == 1))
      {
        localContentValues.put("keyphrase_id", Integer.valueOf(paramKeyphraseSoundModel.keyphrases[0].id));
        localContentValues.put("recognition_modes", Integer.valueOf(paramKeyphraseSoundModel.keyphrases[0].recognitionModes));
        localContentValues.put("users", getCommaSeparatedString(paramKeyphraseSoundModel.keyphrases[0].users));
        localContentValues.put("locale", paramKeyphraseSoundModel.keyphrases[0].locale);
        localContentValues.put("hint_text", paramKeyphraseSoundModel.keyphrases[0].text);
      }
    }
    finally
    {
      try
      {
        ContentValues localContentValues;
        long l = localSQLiteDatabase.insertWithOnConflict("sound_model", null, localContentValues, 5);
        if (l != -1L) {}
        for (;;)
        {
          localSQLiteDatabase.close();
          return bool;
          bool = false;
        }
      }
      finally
      {
        SQLiteDatabase localSQLiteDatabase;
        localSQLiteDatabase.close();
      }
    }
    return false;
  }
  
  public static abstract interface SoundModelContract
  {
    public static final String KEY_DATA = "data";
    public static final String KEY_HINT_TEXT = "hint_text";
    public static final String KEY_KEYPHRASE_ID = "keyphrase_id";
    public static final String KEY_LOCALE = "locale";
    public static final String KEY_MODEL_UUID = "model_uuid";
    public static final String KEY_RECOGNITION_MODES = "recognition_modes";
    public static final String KEY_TYPE = "type";
    public static final String KEY_USERS = "users";
    public static final String KEY_VENDOR_UUID = "vendor_uuid";
    public static final String TABLE = "sound_model";
  }
  
  private static class SoundModelRecord
  {
    public final byte[] data;
    public final String hintText;
    public final int keyphraseId;
    public final String locale;
    public final String modelUuid;
    public final int recognitionModes;
    public final int type;
    public final String users;
    public final String vendorUuid;
    
    public SoundModelRecord(int paramInt, Cursor paramCursor)
    {
      this.modelUuid = paramCursor.getString(paramCursor.getColumnIndex("model_uuid"));
      if (paramInt >= 5) {}
      for (this.vendorUuid = paramCursor.getString(paramCursor.getColumnIndex("vendor_uuid"));; this.vendorUuid = null)
      {
        this.keyphraseId = paramCursor.getInt(paramCursor.getColumnIndex("keyphrase_id"));
        this.type = paramCursor.getInt(paramCursor.getColumnIndex("type"));
        this.data = paramCursor.getBlob(paramCursor.getColumnIndex("data"));
        this.recognitionModes = paramCursor.getInt(paramCursor.getColumnIndex("recognition_modes"));
        this.locale = paramCursor.getString(paramCursor.getColumnIndex("locale"));
        this.hintText = paramCursor.getString(paramCursor.getColumnIndex("hint_text"));
        this.users = paramCursor.getString(paramCursor.getColumnIndex("users"));
        return;
      }
    }
    
    private boolean V6PrimaryKeyMatches(SoundModelRecord paramSoundModelRecord)
    {
      if ((this.keyphraseId == paramSoundModelRecord.keyphraseId) && (stringComparisonHelper(this.locale, paramSoundModelRecord.locale))) {
        return stringComparisonHelper(this.users, paramSoundModelRecord.users);
      }
      return false;
    }
    
    private static boolean stringComparisonHelper(String paramString1, String paramString2)
    {
      if (paramString1 != null) {
        return paramString1.equals(paramString2);
      }
      return paramString1 == paramString2;
    }
    
    public boolean ifViolatesV6PrimaryKeyIsFirstOfAnyDuplicates(List<SoundModelRecord> paramList)
    {
      Object localObject = paramList.iterator();
      while (((Iterator)localObject).hasNext())
      {
        SoundModelRecord localSoundModelRecord = (SoundModelRecord)((Iterator)localObject).next();
        if ((this != localSoundModelRecord) && (V6PrimaryKeyMatches(localSoundModelRecord)) && (!Arrays.equals(this.data, localSoundModelRecord.data))) {
          return false;
        }
      }
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        localObject = (SoundModelRecord)paramList.next();
        if (V6PrimaryKeyMatches((SoundModelRecord)localObject)) {
          return this == localObject;
        }
      }
      return true;
    }
    
    public long writeToDatabase(int paramInt, SQLiteDatabase paramSQLiteDatabase)
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("model_uuid", this.modelUuid);
      if (paramInt >= 5) {
        localContentValues.put("vendor_uuid", this.vendorUuid);
      }
      localContentValues.put("keyphrase_id", Integer.valueOf(this.keyphraseId));
      localContentValues.put("type", Integer.valueOf(this.type));
      localContentValues.put("data", this.data);
      localContentValues.put("recognition_modes", Integer.valueOf(this.recognitionModes));
      localContentValues.put("locale", this.locale);
      localContentValues.put("hint_text", this.hintText);
      localContentValues.put("users", this.users);
      return paramSQLiteDatabase.insertWithOnConflict("sound_model", null, localContentValues, 5);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/voiceinteraction/DatabaseHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */