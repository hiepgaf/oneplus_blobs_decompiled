package com.amap.api.mapcore2d;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class dh
{
  private static Map<Class<? extends dg>, dg> d = new HashMap();
  private dm a;
  private SQLiteDatabase b;
  private dg c;
  
  public dh(Context paramContext, dg paramdg)
  {
    try
    {
      this.a = new dm(paramContext.getApplicationContext(), paramdg.a(), null, paramdg.b(), paramdg);
      this.c = paramdg;
      return;
    }
    catch (Throwable paramContext)
    {
      for (;;)
      {
        paramContext.printStackTrace();
      }
    }
  }
  
  private ContentValues a(Object paramObject, di paramdi)
  {
    ContentValues localContentValues = new ContentValues();
    paramdi = a(paramObject.getClass(), paramdi.b());
    int j = paramdi.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return localContentValues;
      }
      Field localField = paramdi[i];
      localField.setAccessible(true);
      a(paramObject, localField, localContentValues);
      i += 1;
    }
  }
  
  private SQLiteDatabase a(boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        SQLiteDatabase localSQLiteDatabase = this.b;
        if (localSQLiteDatabase == null) {
          continue;
        }
      }
      catch (Throwable localThrowable)
      {
        if (!paramBoolean) {
          continue;
        }
        localThrowable.printStackTrace();
        continue;
        cy.a(localThrowable, "DBOperation", "getReadAbleDataBase");
        continue;
      }
      return this.b;
      this.b = this.a.getReadableDatabase();
    }
  }
  
  /* Error */
  public static dg a(Class<? extends dg> paramClass)
    throws IllegalAccessException, InstantiationException
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 22	com/amap/api/mapcore2d/dh:d	Ljava/util/Map;
    //   6: aload_0
    //   7: invokeinterface 105 2 0
    //   12: ifnull +21 -> 33
    //   15: getstatic 22	com/amap/api/mapcore2d/dh:d	Ljava/util/Map;
    //   18: aload_0
    //   19: invokeinterface 105 2 0
    //   24: checkcast 37	com/amap/api/mapcore2d/dg
    //   27: astore_0
    //   28: ldc 2
    //   30: monitorexit
    //   31: aload_0
    //   32: areturn
    //   33: getstatic 22	com/amap/api/mapcore2d/dh:d	Ljava/util/Map;
    //   36: aload_0
    //   37: aload_0
    //   38: invokevirtual 111	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   41: invokeinterface 115 3 0
    //   46: pop
    //   47: goto -32 -> 15
    //   50: astore_0
    //   51: ldc 2
    //   53: monitorexit
    //   54: aload_0
    //   55: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	56	0	paramClass	Class<? extends dg>
    // Exception table:
    //   from	to	target	type
    //   3	15	50	finally
    //   15	28	50	finally
    //   33	47	50	finally
  }
  
  private <T> T a(Cursor paramCursor, Class<T> paramClass, di paramdi)
    throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
  {
    paramdi = a(paramClass, paramdi.b());
    paramClass = paramClass.getDeclaredConstructor(new Class[0]);
    paramClass.setAccessible(true);
    paramClass = paramClass.newInstance(new Object[0]);
    int j = paramdi.length;
    int i = 0;
    if (i >= j) {
      return paramClass;
    }
    Object localObject1 = paramdi[i];
    ((Field)localObject1).setAccessible(true);
    Object localObject2 = ((Field)localObject1).getAnnotation(dj.class);
    int m;
    if (localObject2 != null)
    {
      localObject2 = (dj)localObject2;
      int k = ((dj)localObject2).b();
      m = paramCursor.getColumnIndex(((dj)localObject2).a());
      switch (k)
      {
      }
    }
    for (;;)
    {
      i += 1;
      break;
      ((Field)localObject1).set(paramClass, Integer.valueOf(paramCursor.getInt(m)));
      continue;
      ((Field)localObject1).set(paramClass, Short.valueOf(paramCursor.getShort(m)));
      continue;
      ((Field)localObject1).set(paramClass, Long.valueOf(paramCursor.getLong(m)));
      continue;
      ((Field)localObject1).set(paramClass, Double.valueOf(paramCursor.getDouble(m)));
      continue;
      ((Field)localObject1).set(paramClass, paramCursor.getString(m));
      continue;
      ((Field)localObject1).set(paramClass, Float.valueOf(paramCursor.getFloat(m)));
      continue;
      ((Field)localObject1).set(paramClass, paramCursor.getBlob(m));
    }
  }
  
  private <T> String a(di paramdi)
  {
    if (paramdi != null) {
      return paramdi.a();
    }
    return null;
  }
  
  public static String a(Map<String, String> paramMap)
  {
    StringBuilder localStringBuilder;
    Iterator localIterator;
    int i;
    if (paramMap != null)
    {
      localStringBuilder = new StringBuilder();
      localIterator = paramMap.keySet().iterator();
      i = 1;
      if (!localIterator.hasNext()) {
        return localStringBuilder.toString();
      }
    }
    else
    {
      return "";
    }
    String str = (String)localIterator.next();
    if (i == 0) {
      localStringBuilder.append(" and ").append(str).append(" = '").append((String)paramMap.get(str)).append("'");
    }
    for (;;)
    {
      break;
      localStringBuilder.append(str).append(" = '").append((String)paramMap.get(str)).append("'");
      i = 0;
    }
  }
  
  private <T> void a(SQLiteDatabase paramSQLiteDatabase, T paramT)
  {
    di localdi = b(paramT.getClass());
    String str = a(localdi);
    if (!TextUtils.isEmpty(str)) {
      if (paramT != null) {
        break label30;
      }
    }
    label30:
    while (paramSQLiteDatabase == null)
    {
      return;
      return;
    }
    paramT = a(paramT, localdi);
    if (paramT != null)
    {
      paramSQLiteDatabase.insert(str, null, paramT);
      return;
    }
  }
  
  private void a(Object paramObject, Field paramField, ContentValues paramContentValues)
  {
    Object localObject = paramField.getAnnotation(dj.class);
    if (localObject != null) {
      localObject = (dj)localObject;
    }
    switch (((dj)localObject).b())
    {
    default: 
      return;
      return;
    case 2: 
      try
      {
        int i = paramField.getInt(paramObject);
        paramContentValues.put(((dj)localObject).a(), Integer.valueOf(i));
        return;
      }
      catch (IllegalAccessException paramObject)
      {
        ((IllegalAccessException)paramObject).printStackTrace();
        return;
      }
    case 1: 
      short s = paramField.getShort(paramObject);
      paramContentValues.put(((dj)localObject).a(), Short.valueOf(s));
      return;
    case 5: 
      long l = paramField.getLong(paramObject);
      paramContentValues.put(((dj)localObject).a(), Long.valueOf(l));
      return;
    case 4: 
      double d1 = paramField.getDouble(paramObject);
      paramContentValues.put(((dj)localObject).a(), Double.valueOf(d1));
      return;
    case 6: 
      paramObject = (String)paramField.get(paramObject);
      paramContentValues.put(((dj)localObject).a(), (String)paramObject);
      return;
    case 3: 
      float f = paramField.getFloat(paramObject);
      paramContentValues.put(((dj)localObject).a(), Float.valueOf(f));
      return;
    }
    paramObject = (byte[])paramField.get(paramObject);
    paramContentValues.put(((dj)localObject).a(), (byte[])paramObject);
  }
  
  private boolean a(Annotation paramAnnotation)
  {
    return paramAnnotation != null;
  }
  
  private Field[] a(Class<?> paramClass, boolean paramBoolean)
  {
    if (paramClass != null)
    {
      if (!paramBoolean) {
        return paramClass.getDeclaredFields();
      }
    }
    else {
      return null;
    }
    return paramClass.getSuperclass().getDeclaredFields();
  }
  
  private SQLiteDatabase b(boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        if (this.b != null) {
          continue;
        }
        if (this.b != null) {
          continue;
        }
        this.b = this.a.getWritableDatabase();
      }
      catch (Throwable localThrowable)
      {
        cy.a(localThrowable, "DBOperation", "getWriteDatabase");
        continue;
      }
      return this.b;
      if (!this.b.isReadOnly())
      {
        continue;
        this.b.close();
      }
    }
  }
  
  private <T> di b(Class<T> paramClass)
  {
    paramClass = paramClass.getAnnotation(di.class);
    if (a(paramClass)) {
      return (di)paramClass;
    }
    return null;
  }
  
  /* Error */
  public <T> List<T> a(String paramString, Class<T> paramClass, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 50	com/amap/api/mapcore2d/dh:c	Lcom/amap/api/mapcore2d/dg;
    //   4: astore 6
    //   6: aload 6
    //   8: monitorenter
    //   9: new 337	java/util/ArrayList
    //   12: dup
    //   13: invokespecial 338	java/util/ArrayList:<init>	()V
    //   16: astore 7
    //   18: aload_0
    //   19: aload_2
    //   20: invokespecial 253	com/amap/api/mapcore2d/dh:b	(Ljava/lang/Class;)Lcom/amap/api/mapcore2d/di;
    //   23: astore 8
    //   25: aload_0
    //   26: aload 8
    //   28: invokespecial 255	com/amap/api/mapcore2d/dh:a	(Lcom/amap/api/mapcore2d/di;)Ljava/lang/String;
    //   31: astore 5
    //   33: aload_0
    //   34: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   37: ifnull +16 -> 53
    //   40: aload_0
    //   41: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   44: ifnonnull +27 -> 71
    //   47: aload 6
    //   49: monitorexit
    //   50: aload 7
    //   52: areturn
    //   53: aload_0
    //   54: aload_0
    //   55: iload_3
    //   56: invokespecial 340	com/amap/api/mapcore2d/dh:a	(Z)Landroid/database/sqlite/SQLiteDatabase;
    //   59: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   62: goto -22 -> 40
    //   65: astore_1
    //   66: aload 6
    //   68: monitorexit
    //   69: aload_1
    //   70: athrow
    //   71: aload 5
    //   73: invokestatic 261	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   76: istore 4
    //   78: iload 4
    //   80: ifne -33 -> 47
    //   83: aload_1
    //   84: ifnull -37 -> 47
    //   87: aload_0
    //   88: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   91: aload 5
    //   93: aconst_null
    //   94: aload_1
    //   95: aconst_null
    //   96: aconst_null
    //   97: aconst_null
    //   98: aconst_null
    //   99: invokevirtual 344	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   102: astore 5
    //   104: aload 5
    //   106: ifnull +38 -> 144
    //   109: aload 5
    //   111: astore_1
    //   112: aload 5
    //   114: invokeinterface 347 1 0
    //   119: istore 4
    //   121: iload 4
    //   123: ifne +57 -> 180
    //   126: aload 5
    //   128: ifnonnull +168 -> 296
    //   131: aload_0
    //   132: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   135: ifnonnull +171 -> 306
    //   138: aload 6
    //   140: monitorexit
    //   141: aload 7
    //   143: areturn
    //   144: aload 5
    //   146: astore_1
    //   147: aload_0
    //   148: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   151: invokevirtual 328	android/database/sqlite/SQLiteDatabase:close	()V
    //   154: aload 5
    //   156: astore_1
    //   157: aload_0
    //   158: aconst_null
    //   159: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   162: aload 5
    //   164: ifnonnull +107 -> 271
    //   167: aload_0
    //   168: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   171: ifnonnull +110 -> 281
    //   174: aload 6
    //   176: monitorexit
    //   177: aload 7
    //   179: areturn
    //   180: aload 5
    //   182: astore_1
    //   183: aload 7
    //   185: aload_0
    //   186: aload 5
    //   188: aload_2
    //   189: aload 8
    //   191: invokespecial 349	com/amap/api/mapcore2d/dh:a	(Landroid/database/Cursor;Ljava/lang/Class;Lcom/amap/api/mapcore2d/di;)Ljava/lang/Object;
    //   194: invokeinterface 355 2 0
    //   199: pop
    //   200: goto -91 -> 109
    //   203: astore_2
    //   204: iload_3
    //   205: ifeq +30 -> 235
    //   208: aload 5
    //   210: ifnonnull +111 -> 321
    //   213: aload_0
    //   214: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   217: ifnull -79 -> 138
    //   220: aload_0
    //   221: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   224: invokevirtual 328	android/database/sqlite/SQLiteDatabase:close	()V
    //   227: aload_0
    //   228: aconst_null
    //   229: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   232: goto -94 -> 138
    //   235: aload 5
    //   237: astore_1
    //   238: aload_2
    //   239: ldc_w 357
    //   242: ldc_w 359
    //   245: invokestatic 94	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   248: goto -40 -> 208
    //   251: astore 5
    //   253: aload_1
    //   254: astore_2
    //   255: aload 5
    //   257: astore_1
    //   258: aload_2
    //   259: ifnonnull +72 -> 331
    //   262: aload_0
    //   263: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   266: ifnonnull +74 -> 340
    //   269: aload_1
    //   270: athrow
    //   271: aload 5
    //   273: invokeinterface 360 1 0
    //   278: goto -111 -> 167
    //   281: aload_0
    //   282: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   285: invokevirtual 328	android/database/sqlite/SQLiteDatabase:close	()V
    //   288: aload_0
    //   289: aconst_null
    //   290: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   293: goto -119 -> 174
    //   296: aload 5
    //   298: invokeinterface 360 1 0
    //   303: goto -172 -> 131
    //   306: aload_0
    //   307: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   310: invokevirtual 328	android/database/sqlite/SQLiteDatabase:close	()V
    //   313: aload_0
    //   314: aconst_null
    //   315: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   318: goto -180 -> 138
    //   321: aload 5
    //   323: invokeinterface 360 1 0
    //   328: goto -115 -> 213
    //   331: aload_2
    //   332: invokeinterface 360 1 0
    //   337: goto -75 -> 262
    //   340: aload_0
    //   341: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   344: invokevirtual 328	android/database/sqlite/SQLiteDatabase:close	()V
    //   347: aload_0
    //   348: aconst_null
    //   349: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   352: goto -83 -> 269
    //   355: astore_1
    //   356: aconst_null
    //   357: astore_2
    //   358: goto -100 -> 258
    //   361: astore_2
    //   362: aconst_null
    //   363: astore 5
    //   365: goto -161 -> 204
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	368	0	this	dh
    //   0	368	1	paramString	String
    //   0	368	2	paramClass	Class<T>
    //   0	368	3	paramBoolean	boolean
    //   76	46	4	bool	boolean
    //   31	205	5	localObject1	Object
    //   251	71	5	localObject2	Object
    //   363	1	5	localObject3	Object
    //   4	171	6	localdg	dg
    //   16	168	7	localArrayList	java.util.ArrayList
    //   23	167	8	localdi	di
    // Exception table:
    //   from	to	target	type
    //   9	40	65	finally
    //   40	47	65	finally
    //   47	50	65	finally
    //   53	62	65	finally
    //   66	69	65	finally
    //   71	78	65	finally
    //   131	138	65	finally
    //   138	141	65	finally
    //   167	174	65	finally
    //   174	177	65	finally
    //   213	232	65	finally
    //   262	269	65	finally
    //   269	271	65	finally
    //   271	278	65	finally
    //   281	293	65	finally
    //   296	303	65	finally
    //   306	318	65	finally
    //   321	328	65	finally
    //   331	337	65	finally
    //   340	352	65	finally
    //   112	121	203	java/lang/Throwable
    //   147	154	203	java/lang/Throwable
    //   157	162	203	java/lang/Throwable
    //   183	200	203	java/lang/Throwable
    //   112	121	251	finally
    //   147	154	251	finally
    //   157	162	251	finally
    //   183	200	251	finally
    //   238	248	251	finally
    //   87	104	355	finally
    //   87	104	361	java/lang/Throwable
  }
  
  public <T> void a(T paramT)
  {
    a(paramT, false);
  }
  
  public <T> void a(T paramT, boolean paramBoolean)
  {
    synchronized (this.c)
    {
      this.b = b(paramBoolean);
      SQLiteDatabase localSQLiteDatabase = this.b;
      if (localSQLiteDatabase != null) {}
      try
      {
        a(this.b, paramT);
      }
      catch (Throwable paramT)
      {
        for (;;)
        {
          cy.a(paramT, "DataBase", "insertData");
          if (this.b != null)
          {
            this.b.close();
            this.b = null;
          }
        }
        paramT = finally;
        throw paramT;
      }
      finally
      {
        if (this.b != null) {
          break label112;
        }
      }
      return;
      return;
    }
    for (;;)
    {
      throw paramT;
      this.b.close();
      this.b = null;
      break;
      label112:
      this.b.close();
      this.b = null;
    }
  }
  
  public <T> void a(String paramString, Class<T> paramClass)
  {
    synchronized (this.c)
    {
      paramClass = a(b(paramClass));
      if (!TextUtils.isEmpty(paramClass))
      {
        this.b = b(false);
        SQLiteDatabase localSQLiteDatabase = this.b;
        if (localSQLiteDatabase == null) {
          break label68;
        }
      }
      try
      {
        this.b.delete(paramClass, paramString, null);
      }
      catch (Throwable paramString)
      {
        for (;;)
        {
          label68:
          cy.a(paramString, "DataBase", "deleteData");
          if (this.b != null)
          {
            this.b.close();
            this.b = null;
          }
        }
        paramString = finally;
        throw paramString;
      }
      finally
      {
        if (this.b != null) {
          break label134;
        }
      }
      return;
      return;
      return;
    }
    for (;;)
    {
      throw paramString;
      this.b.close();
      this.b = null;
      break;
      label134:
      this.b.close();
      this.b = null;
    }
  }
  
  public <T> void a(String paramString, Object paramObject)
  {
    a(paramString, paramObject, false);
  }
  
  /* Error */
  public <T> void a(String paramString, Object paramObject, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 50	com/amap/api/mapcore2d/dh:c	Lcom/amap/api/mapcore2d/dg;
    //   4: astore 4
    //   6: aload 4
    //   8: monitorenter
    //   9: aload_2
    //   10: ifnull +85 -> 95
    //   13: aload_0
    //   14: aload_2
    //   15: invokevirtual 61	java/lang/Object:getClass	()Ljava/lang/Class;
    //   18: invokespecial 253	com/amap/api/mapcore2d/dh:b	(Ljava/lang/Class;)Lcom/amap/api/mapcore2d/di;
    //   21: astore 6
    //   23: aload_0
    //   24: aload 6
    //   26: invokespecial 255	com/amap/api/mapcore2d/dh:a	(Lcom/amap/api/mapcore2d/di;)Ljava/lang/String;
    //   29: astore 5
    //   31: aload 5
    //   33: invokestatic 261	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   36: ifne +63 -> 99
    //   39: aload_0
    //   40: aload_2
    //   41: aload 6
    //   43: invokespecial 263	com/amap/api/mapcore2d/dh:a	(Ljava/lang/Object;Lcom/amap/api/mapcore2d/di;)Landroid/content/ContentValues;
    //   46: astore_2
    //   47: aload_2
    //   48: ifnull +55 -> 103
    //   51: aload_0
    //   52: aload_0
    //   53: iload_3
    //   54: invokespecial 368	com/amap/api/mapcore2d/dh:b	(Z)Landroid/database/sqlite/SQLiteDatabase;
    //   57: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   60: aload_0
    //   61: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   64: astore 6
    //   66: aload 6
    //   68: ifnull +39 -> 107
    //   71: aload_0
    //   72: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   75: aload 5
    //   77: aload_2
    //   78: aload_1
    //   79: aconst_null
    //   80: invokevirtual 390	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   83: pop
    //   84: aload_0
    //   85: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   88: ifnonnull +83 -> 171
    //   91: aload 4
    //   93: monitorexit
    //   94: return
    //   95: aload 4
    //   97: monitorexit
    //   98: return
    //   99: aload 4
    //   101: monitorexit
    //   102: return
    //   103: aload 4
    //   105: monitorexit
    //   106: return
    //   107: aload 4
    //   109: monitorexit
    //   110: return
    //   111: astore_1
    //   112: iload_3
    //   113: ifeq +35 -> 148
    //   116: aload_1
    //   117: invokevirtual 53	java/lang/Throwable:printStackTrace	()V
    //   120: aload_0
    //   121: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   124: ifnull -33 -> 91
    //   127: aload_0
    //   128: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   131: invokevirtual 328	android/database/sqlite/SQLiteDatabase:close	()V
    //   134: aload_0
    //   135: aconst_null
    //   136: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   139: goto -48 -> 91
    //   142: astore_1
    //   143: aload 4
    //   145: monitorexit
    //   146: aload_1
    //   147: athrow
    //   148: aload_1
    //   149: ldc_w 357
    //   152: ldc_w 392
    //   155: invokestatic 94	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   158: goto -38 -> 120
    //   161: astore_1
    //   162: aload_0
    //   163: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   166: ifnonnull +20 -> 186
    //   169: aload_1
    //   170: athrow
    //   171: aload_0
    //   172: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   175: invokevirtual 328	android/database/sqlite/SQLiteDatabase:close	()V
    //   178: aload_0
    //   179: aconst_null
    //   180: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   183: goto -92 -> 91
    //   186: aload_0
    //   187: getfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   190: invokevirtual 328	android/database/sqlite/SQLiteDatabase:close	()V
    //   193: aload_0
    //   194: aconst_null
    //   195: putfield 81	com/amap/api/mapcore2d/dh:b	Landroid/database/sqlite/SQLiteDatabase;
    //   198: goto -29 -> 169
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	201	0	this	dh
    //   0	201	1	paramString	String
    //   0	201	2	paramObject	Object
    //   0	201	3	paramBoolean	boolean
    //   4	140	4	localdg	dg
    //   29	47	5	str	String
    //   21	46	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   71	84	111	java/lang/Throwable
    //   13	47	142	finally
    //   51	66	142	finally
    //   84	91	142	finally
    //   91	94	142	finally
    //   95	98	142	finally
    //   99	102	142	finally
    //   103	106	142	finally
    //   107	110	142	finally
    //   120	139	142	finally
    //   143	146	142	finally
    //   162	169	142	finally
    //   169	171	142	finally
    //   171	183	142	finally
    //   186	198	142	finally
    //   71	84	161	finally
    //   116	120	161	finally
    //   148	158	161	finally
  }
  
  public <T> List<T> b(String paramString, Class<T> paramClass)
  {
    return a(paramString, paramClass, false);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */