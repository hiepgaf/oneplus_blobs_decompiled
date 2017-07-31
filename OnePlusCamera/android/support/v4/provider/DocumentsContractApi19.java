package android.support.v4.provider;

import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.text.TextUtils;

class DocumentsContractApi19
{
  private static final String TAG = "DocumentFile";
  
  public static boolean canRead(Context paramContext, Uri paramUri)
  {
    if (paramContext.checkCallingOrSelfUriPermission(paramUri, 1) == 0)
    {
      if (!TextUtils.isEmpty(getRawType(paramContext, paramUri))) {
        return true;
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public static boolean canWrite(Context paramContext, Uri paramUri)
  {
    int i;
    if (paramContext.checkCallingOrSelfUriPermission(paramUri, 2) == 0)
    {
      String str = getRawType(paramContext, paramUri);
      i = queryForInt(paramContext, paramUri, "flags", 0);
      if (TextUtils.isEmpty(str)) {
        break label57;
      }
      if ((i & 0x4) != 0) {
        break label59;
      }
      if ("vnd.android.document/directory".equals(str)) {
        break label61;
      }
      if (!TextUtils.isEmpty(str)) {
        break label70;
      }
    }
    label57:
    label59:
    label61:
    label70:
    while ((i & 0x2) == 0)
    {
      return false;
      return false;
      return false;
      return true;
      if ((i & 0x8) == 0) {
        break;
      }
      return true;
    }
    return true;
  }
  
  private static void closeQuietly(AutoCloseable paramAutoCloseable)
  {
    if (paramAutoCloseable == null) {
      return;
    }
    try
    {
      paramAutoCloseable.close();
      return;
    }
    catch (RuntimeException paramAutoCloseable)
    {
      throw paramAutoCloseable;
    }
    catch (Exception paramAutoCloseable) {}
  }
  
  public static boolean delete(Context paramContext, Uri paramUri)
  {
    return DocumentsContract.deleteDocument(paramContext.getContentResolver(), paramUri);
  }
  
  /* Error */
  public static boolean exists(Context paramContext, Uri paramUri)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 62	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: astore_0
    //   5: aload_0
    //   6: aload_1
    //   7: iconst_1
    //   8: anewarray 42	java/lang/String
    //   11: dup
    //   12: iconst_0
    //   13: ldc 71
    //   15: aastore
    //   16: aconst_null
    //   17: aconst_null
    //   18: aconst_null
    //   19: invokevirtual 77	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   22: astore_1
    //   23: aload_1
    //   24: astore_0
    //   25: aload_1
    //   26: invokeinterface 83 1 0
    //   31: istore_2
    //   32: iload_2
    //   33: ifgt +11 -> 44
    //   36: iconst_0
    //   37: istore_3
    //   38: aload_1
    //   39: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   42: iload_3
    //   43: ireturn
    //   44: iconst_1
    //   45: istore_3
    //   46: goto -8 -> 38
    //   49: astore 4
    //   51: aconst_null
    //   52: astore_1
    //   53: aload_1
    //   54: astore_0
    //   55: ldc 8
    //   57: new 87	java/lang/StringBuilder
    //   60: dup
    //   61: invokespecial 88	java/lang/StringBuilder:<init>	()V
    //   64: ldc 90
    //   66: invokevirtual 94	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload 4
    //   71: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   74: invokevirtual 101	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   77: invokestatic 107	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   80: pop
    //   81: aload_1
    //   82: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   85: iconst_0
    //   86: ireturn
    //   87: astore_1
    //   88: aconst_null
    //   89: astore_0
    //   90: aload_0
    //   91: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   94: aload_1
    //   95: athrow
    //   96: astore_1
    //   97: goto -7 -> 90
    //   100: astore 4
    //   102: goto -49 -> 53
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	paramContext	Context
    //   0	105	1	paramUri	Uri
    //   31	2	2	i	int
    //   37	9	3	bool	boolean
    //   49	21	4	localException1	Exception
    //   100	1	4	localException2	Exception
    // Exception table:
    //   from	to	target	type
    //   5	23	49	java/lang/Exception
    //   5	23	87	finally
    //   25	32	96	finally
    //   55	81	96	finally
    //   25	32	100	java/lang/Exception
  }
  
  public static String getName(Context paramContext, Uri paramUri)
  {
    return queryForString(paramContext, paramUri, "_display_name", null);
  }
  
  private static String getRawType(Context paramContext, Uri paramUri)
  {
    return queryForString(paramContext, paramUri, "mime_type", null);
  }
  
  public static String getType(Context paramContext, Uri paramUri)
  {
    paramContext = getRawType(paramContext, paramUri);
    if (!"vnd.android.document/directory".equals(paramContext)) {
      return paramContext;
    }
    return null;
  }
  
  public static boolean isDirectory(Context paramContext, Uri paramUri)
  {
    return "vnd.android.document/directory".equals(getRawType(paramContext, paramUri));
  }
  
  public static boolean isDocumentUri(Context paramContext, Uri paramUri)
  {
    return DocumentsContract.isDocumentUri(paramContext, paramUri);
  }
  
  public static boolean isFile(Context paramContext, Uri paramUri)
  {
    paramContext = getRawType(paramContext, paramUri);
    if ("vnd.android.document/directory".equals(paramContext)) {}
    while (TextUtils.isEmpty(paramContext)) {
      return false;
    }
    return true;
  }
  
  public static long lastModified(Context paramContext, Uri paramUri)
  {
    return queryForLong(paramContext, paramUri, "last_modified", 0L);
  }
  
  public static long length(Context paramContext, Uri paramUri)
  {
    return queryForLong(paramContext, paramUri, "_size", 0L);
  }
  
  private static int queryForInt(Context paramContext, Uri paramUri, String paramString, int paramInt)
  {
    return (int)queryForLong(paramContext, paramUri, paramString, paramInt);
  }
  
  /* Error */
  private static long queryForLong(Context paramContext, Uri paramUri, String paramString, long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 62	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: astore_0
    //   5: aload_0
    //   6: aload_1
    //   7: iconst_1
    //   8: anewarray 42	java/lang/String
    //   11: dup
    //   12: iconst_0
    //   13: aload_2
    //   14: aastore
    //   15: aconst_null
    //   16: aconst_null
    //   17: aconst_null
    //   18: invokevirtual 77	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   21: astore_1
    //   22: aload_1
    //   23: astore_0
    //   24: aload_1
    //   25: invokeinterface 137 1 0
    //   30: istore 7
    //   32: iload 7
    //   34: ifne +9 -> 43
    //   37: aload_1
    //   38: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   41: lload_3
    //   42: lreturn
    //   43: aload_1
    //   44: astore_0
    //   45: aload_1
    //   46: iconst_0
    //   47: invokeinterface 141 2 0
    //   52: ifne -15 -> 37
    //   55: aload_1
    //   56: astore_0
    //   57: aload_1
    //   58: iconst_0
    //   59: invokeinterface 145 2 0
    //   64: lstore 5
    //   66: aload_1
    //   67: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   70: lload 5
    //   72: lreturn
    //   73: astore_2
    //   74: aconst_null
    //   75: astore_1
    //   76: aload_1
    //   77: astore_0
    //   78: ldc 8
    //   80: new 87	java/lang/StringBuilder
    //   83: dup
    //   84: invokespecial 88	java/lang/StringBuilder:<init>	()V
    //   87: ldc 90
    //   89: invokevirtual 94	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   92: aload_2
    //   93: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   96: invokevirtual 101	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   99: invokestatic 107	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   102: pop
    //   103: aload_1
    //   104: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   107: lload_3
    //   108: lreturn
    //   109: astore_1
    //   110: aconst_null
    //   111: astore_0
    //   112: aload_0
    //   113: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   116: aload_1
    //   117: athrow
    //   118: astore_1
    //   119: goto -7 -> 112
    //   122: astore_2
    //   123: goto -47 -> 76
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	paramContext	Context
    //   0	126	1	paramUri	Uri
    //   0	126	2	paramString	String
    //   0	126	3	paramLong	long
    //   64	7	5	l	long
    //   30	3	7	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   5	22	73	java/lang/Exception
    //   5	22	109	finally
    //   24	32	118	finally
    //   45	55	118	finally
    //   57	66	118	finally
    //   78	103	118	finally
    //   24	32	122	java/lang/Exception
    //   45	55	122	java/lang/Exception
    //   57	66	122	java/lang/Exception
  }
  
  /* Error */
  private static String queryForString(Context paramContext, Uri paramUri, String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 62	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: astore_0
    //   5: aload_0
    //   6: aload_1
    //   7: iconst_1
    //   8: anewarray 42	java/lang/String
    //   11: dup
    //   12: iconst_0
    //   13: aload_2
    //   14: aastore
    //   15: aconst_null
    //   16: aconst_null
    //   17: aconst_null
    //   18: invokevirtual 77	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   21: astore_1
    //   22: aload_1
    //   23: astore_0
    //   24: aload_1
    //   25: invokeinterface 137 1 0
    //   30: istore 4
    //   32: iload 4
    //   34: ifne +9 -> 43
    //   37: aload_1
    //   38: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   41: aload_3
    //   42: areturn
    //   43: aload_1
    //   44: astore_0
    //   45: aload_1
    //   46: iconst_0
    //   47: invokeinterface 141 2 0
    //   52: ifne -15 -> 37
    //   55: aload_1
    //   56: astore_0
    //   57: aload_1
    //   58: iconst_0
    //   59: invokeinterface 149 2 0
    //   64: astore_2
    //   65: aload_1
    //   66: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   69: aload_2
    //   70: areturn
    //   71: astore_2
    //   72: aconst_null
    //   73: astore_1
    //   74: aload_1
    //   75: astore_0
    //   76: ldc 8
    //   78: new 87	java/lang/StringBuilder
    //   81: dup
    //   82: invokespecial 88	java/lang/StringBuilder:<init>	()V
    //   85: ldc 90
    //   87: invokevirtual 94	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: aload_2
    //   91: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   94: invokevirtual 101	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   97: invokestatic 107	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   100: pop
    //   101: aload_1
    //   102: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   105: aload_3
    //   106: areturn
    //   107: astore_1
    //   108: aconst_null
    //   109: astore_0
    //   110: aload_0
    //   111: invokestatic 85	android/support/v4/provider/DocumentsContractApi19:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   114: aload_1
    //   115: athrow
    //   116: astore_1
    //   117: goto -7 -> 110
    //   120: astore_2
    //   121: goto -47 -> 74
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	124	0	paramContext	Context
    //   0	124	1	paramUri	Uri
    //   0	124	2	paramString1	String
    //   0	124	3	paramString2	String
    //   30	3	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   5	22	71	java/lang/Exception
    //   5	22	107	finally
    //   24	32	116	finally
    //   45	55	116	finally
    //   57	65	116	finally
    //   76	101	116	finally
    //   24	32	120	java/lang/Exception
    //   45	55	120	java/lang/Exception
    //   57	65	120	java/lang/Exception
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/provider/DocumentsContractApi19.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */