package com.android.server.soundtrigger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SoundTriggerDbHelper
  extends SQLiteOpenHelper
{
  private static final String CREATE_TABLE_ST_SOUND_MODEL = "CREATE TABLE st_sound_model(model_uuid TEXT PRIMARY KEY,vendor_uuid TEXT,data BLOB )";
  static final boolean DBG = false;
  private static final String NAME = "st_sound_model.db";
  static final String TAG = "SoundTriggerDbHelper";
  private static final int VERSION = 1;
  
  public SoundTriggerDbHelper(Context paramContext)
  {
    super(paramContext, "st_sound_model.db", null, 1);
  }
  
  /* Error */
  public boolean deleteGenericSoundModel(java.util.UUID paramUUID)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: aload_1
    //   6: invokevirtual 35	com/android/server/soundtrigger/SoundTriggerDbHelper:getGenericSoundModel	(Ljava/util/UUID;)Landroid/hardware/soundtrigger/SoundTrigger$GenericSoundModel;
    //   9: astore 4
    //   11: aload 4
    //   13: ifnonnull +7 -> 20
    //   16: aload_0
    //   17: monitorexit
    //   18: iconst_0
    //   19: ireturn
    //   20: aload_0
    //   21: invokevirtual 39	com/android/server/soundtrigger/SoundTriggerDbHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   24: astore_1
    //   25: new 41	java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial 44	java/lang/StringBuilder:<init>	()V
    //   32: ldc 46
    //   34: invokevirtual 50	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: aload 4
    //   39: getfield 56	android/hardware/soundtrigger/SoundTrigger$GenericSoundModel:uuid	Ljava/util/UUID;
    //   42: invokevirtual 62	java/util/UUID:toString	()Ljava/lang/String;
    //   45: invokevirtual 50	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: ldc 64
    //   50: invokevirtual 50	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: invokevirtual 65	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   56: astore 4
    //   58: aload_1
    //   59: ldc 67
    //   61: aload 4
    //   63: aconst_null
    //   64: invokevirtual 73	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   67: istore_2
    //   68: iload_2
    //   69: ifeq +5 -> 74
    //   72: iconst_1
    //   73: istore_3
    //   74: aload_1
    //   75: invokevirtual 76	android/database/sqlite/SQLiteDatabase:close	()V
    //   78: aload_0
    //   79: monitorexit
    //   80: iload_3
    //   81: ireturn
    //   82: astore 4
    //   84: aload_1
    //   85: invokevirtual 76	android/database/sqlite/SQLiteDatabase:close	()V
    //   88: aload 4
    //   90: athrow
    //   91: astore_1
    //   92: aload_0
    //   93: monitorexit
    //   94: aload_1
    //   95: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	96	0	this	SoundTriggerDbHelper
    //   0	96	1	paramUUID	java.util.UUID
    //   67	2	2	i	int
    //   1	80	3	bool	boolean
    //   9	53	4	localObject1	Object
    //   82	7	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   58	68	82	finally
    //   4	11	91	finally
    //   20	58	91	finally
    //   74	78	91	finally
    //   84	91	91	finally
  }
  
  /* Error */
  public android.hardware.soundtrigger.SoundTrigger.GenericSoundModel getGenericSoundModel(java.util.UUID paramUUID)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 41	java/lang/StringBuilder
    //   5: dup
    //   6: invokespecial 44	java/lang/StringBuilder:<init>	()V
    //   9: ldc 78
    //   11: invokevirtual 50	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   14: aload_1
    //   15: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   18: ldc 64
    //   20: invokevirtual 50	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: invokevirtual 65	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   26: astore_3
    //   27: aload_0
    //   28: invokevirtual 84	com/android/server/soundtrigger/SoundTriggerDbHelper:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore_2
    //   32: aload_2
    //   33: aload_3
    //   34: aconst_null
    //   35: invokevirtual 88	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   38: astore_3
    //   39: aload_3
    //   40: invokeinterface 94 1 0
    //   45: ifeq +61 -> 106
    //   48: aload_3
    //   49: aload_3
    //   50: ldc 96
    //   52: invokeinterface 100 2 0
    //   57: invokeinterface 104 2 0
    //   62: astore 4
    //   64: new 52	android/hardware/soundtrigger/SoundTrigger$GenericSoundModel
    //   67: dup
    //   68: aload_1
    //   69: aload_3
    //   70: aload_3
    //   71: ldc 106
    //   73: invokeinterface 100 2 0
    //   78: invokeinterface 110 2 0
    //   83: invokestatic 114	java/util/UUID:fromString	(Ljava/lang/String;)Ljava/util/UUID;
    //   86: aload 4
    //   88: invokespecial 117	android/hardware/soundtrigger/SoundTrigger$GenericSoundModel:<init>	(Ljava/util/UUID;Ljava/util/UUID;[B)V
    //   91: astore_1
    //   92: aload_3
    //   93: invokeinterface 118 1 0
    //   98: aload_2
    //   99: invokevirtual 76	android/database/sqlite/SQLiteDatabase:close	()V
    //   102: aload_0
    //   103: monitorexit
    //   104: aload_1
    //   105: areturn
    //   106: aload_3
    //   107: invokeinterface 118 1 0
    //   112: aload_2
    //   113: invokevirtual 76	android/database/sqlite/SQLiteDatabase:close	()V
    //   116: aload_0
    //   117: monitorexit
    //   118: aconst_null
    //   119: areturn
    //   120: astore_1
    //   121: aload_3
    //   122: invokeinterface 118 1 0
    //   127: aload_2
    //   128: invokevirtual 76	android/database/sqlite/SQLiteDatabase:close	()V
    //   131: aload_1
    //   132: athrow
    //   133: astore_1
    //   134: aload_0
    //   135: monitorexit
    //   136: aload_1
    //   137: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	138	0	this	SoundTriggerDbHelper
    //   0	138	1	paramUUID	java.util.UUID
    //   31	97	2	localSQLiteDatabase	SQLiteDatabase
    //   26	96	3	localObject	Object
    //   62	25	4	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   39	92	120	finally
    //   2	39	133	finally
    //   92	102	133	finally
    //   106	116	133	finally
    //   121	133	133	finally
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE st_sound_model(model_uuid TEXT PRIMARY KEY,vendor_uuid TEXT,data BLOB )");
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS st_sound_model");
    onCreate(paramSQLiteDatabase);
  }
  
  /* Error */
  public boolean updateGenericSoundModel(android.hardware.soundtrigger.SoundTrigger.GenericSoundModel paramGenericSoundModel)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 39	com/android/server/soundtrigger/SoundTriggerDbHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   6: astore 5
    //   8: new 134	android/content/ContentValues
    //   11: dup
    //   12: invokespecial 135	android/content/ContentValues:<init>	()V
    //   15: astore 6
    //   17: aload 6
    //   19: ldc -119
    //   21: aload_1
    //   22: getfield 56	android/hardware/soundtrigger/SoundTrigger$GenericSoundModel:uuid	Ljava/util/UUID;
    //   25: invokevirtual 62	java/util/UUID:toString	()Ljava/lang/String;
    //   28: invokevirtual 141	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   31: aload 6
    //   33: ldc 106
    //   35: aload_1
    //   36: getfield 144	android/hardware/soundtrigger/SoundTrigger$GenericSoundModel:vendorUuid	Ljava/util/UUID;
    //   39: invokevirtual 62	java/util/UUID:toString	()Ljava/lang/String;
    //   42: invokevirtual 141	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   45: aload 6
    //   47: ldc 96
    //   49: aload_1
    //   50: getfield 147	android/hardware/soundtrigger/SoundTrigger$GenericSoundModel:data	[B
    //   53: invokevirtual 150	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   56: aload 5
    //   58: ldc 67
    //   60: aconst_null
    //   61: aload 6
    //   63: iconst_5
    //   64: invokevirtual 154	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   67: lstore_2
    //   68: lload_2
    //   69: ldc2_w 155
    //   72: lcmp
    //   73: ifeq +16 -> 89
    //   76: iconst_1
    //   77: istore 4
    //   79: aload 5
    //   81: invokevirtual 76	android/database/sqlite/SQLiteDatabase:close	()V
    //   84: aload_0
    //   85: monitorexit
    //   86: iload 4
    //   88: ireturn
    //   89: iconst_0
    //   90: istore 4
    //   92: goto -13 -> 79
    //   95: astore_1
    //   96: aload 5
    //   98: invokevirtual 76	android/database/sqlite/SQLiteDatabase:close	()V
    //   101: aload_1
    //   102: athrow
    //   103: astore_1
    //   104: aload_0
    //   105: monitorexit
    //   106: aload_1
    //   107: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	this	SoundTriggerDbHelper
    //   0	108	1	paramGenericSoundModel	android.hardware.soundtrigger.SoundTrigger.GenericSoundModel
    //   67	2	2	l	long
    //   77	14	4	bool	boolean
    //   6	91	5	localSQLiteDatabase	SQLiteDatabase
    //   15	47	6	localContentValues	android.content.ContentValues
    // Exception table:
    //   from	to	target	type
    //   56	68	95	finally
    //   2	56	103	finally
    //   79	84	103	finally
    //   96	103	103	finally
  }
  
  public static abstract interface GenericSoundModelContract
  {
    public static final String KEY_DATA = "data";
    public static final String KEY_MODEL_UUID = "model_uuid";
    public static final String KEY_VENDOR_UUID = "vendor_uuid";
    public static final String TABLE = "st_sound_model";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/soundtrigger/SoundTriggerDbHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */