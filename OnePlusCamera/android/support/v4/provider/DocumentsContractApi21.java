package android.support.v4.provider;

import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;

class DocumentsContractApi21
{
  private static final String TAG = "DocumentFile";
  
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
  
  public static Uri createDirectory(Context paramContext, Uri paramUri, String paramString)
  {
    return createFile(paramContext, paramUri, "vnd.android.document/directory", paramString);
  }
  
  public static Uri createFile(Context paramContext, Uri paramUri, String paramString1, String paramString2)
  {
    return DocumentsContract.createDocument(paramContext.getContentResolver(), paramUri, paramString1, paramString2);
  }
  
  /* Error */
  public static Uri[] listFiles(Context paramContext, Uri paramUri)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 38	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: astore_0
    //   5: aload_1
    //   6: aload_1
    //   7: invokestatic 50	android/provider/DocumentsContract:getDocumentId	(Landroid/net/Uri;)Ljava/lang/String;
    //   10: invokestatic 54	android/provider/DocumentsContract:buildChildDocumentsUriUsingTree	(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   13: astore_3
    //   14: new 56	java/util/ArrayList
    //   17: dup
    //   18: invokespecial 57	java/util/ArrayList:<init>	()V
    //   21: astore 4
    //   23: aload_0
    //   24: aload_3
    //   25: iconst_1
    //   26: anewarray 59	java/lang/String
    //   29: dup
    //   30: iconst_0
    //   31: ldc 61
    //   33: aastore
    //   34: aconst_null
    //   35: aconst_null
    //   36: aconst_null
    //   37: invokevirtual 67	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   40: astore_3
    //   41: aload_3
    //   42: astore_0
    //   43: aload_3
    //   44: invokeinterface 73 1 0
    //   49: istore_2
    //   50: iload_2
    //   51: ifne +24 -> 75
    //   54: aload_3
    //   55: invokestatic 75	android/support/v4/provider/DocumentsContractApi21:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   58: aload 4
    //   60: aload 4
    //   62: invokevirtual 79	java/util/ArrayList:size	()I
    //   65: anewarray 81	android/net/Uri
    //   68: invokevirtual 85	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   71: checkcast 87	[Landroid/net/Uri;
    //   74: areturn
    //   75: aload_3
    //   76: astore_0
    //   77: aload 4
    //   79: aload_1
    //   80: aload_3
    //   81: iconst_0
    //   82: invokeinterface 91 2 0
    //   87: invokestatic 94	android/provider/DocumentsContract:buildDocumentUriUsingTree	(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   90: invokevirtual 98	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   93: pop
    //   94: goto -53 -> 41
    //   97: astore_1
    //   98: aload_3
    //   99: astore_0
    //   100: ldc 8
    //   102: new 100	java/lang/StringBuilder
    //   105: dup
    //   106: invokespecial 101	java/lang/StringBuilder:<init>	()V
    //   109: ldc 103
    //   111: invokevirtual 107	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: aload_1
    //   115: invokevirtual 110	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   118: invokevirtual 114	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   121: invokestatic 120	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   124: pop
    //   125: aload_3
    //   126: invokestatic 75	android/support/v4/provider/DocumentsContractApi21:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   129: goto -71 -> 58
    //   132: astore_1
    //   133: aconst_null
    //   134: astore_0
    //   135: aload_0
    //   136: invokestatic 75	android/support/v4/provider/DocumentsContractApi21:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   139: aload_1
    //   140: athrow
    //   141: astore_1
    //   142: goto -7 -> 135
    //   145: astore_1
    //   146: aconst_null
    //   147: astore_3
    //   148: goto -50 -> 98
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	151	0	paramContext	Context
    //   0	151	1	paramUri	Uri
    //   49	2	2	bool	boolean
    //   13	135	3	localObject	Object
    //   21	57	4	localArrayList	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   43	50	97	java/lang/Exception
    //   77	94	97	java/lang/Exception
    //   23	41	132	finally
    //   43	50	141	finally
    //   77	94	141	finally
    //   100	125	141	finally
    //   23	41	145	java/lang/Exception
  }
  
  public static Uri prepareTreeUri(Uri paramUri)
  {
    return DocumentsContract.buildDocumentUriUsingTree(paramUri, DocumentsContract.getTreeDocumentId(paramUri));
  }
  
  public static Uri renameTo(Context paramContext, Uri paramUri, String paramString)
  {
    return DocumentsContract.renameDocument(paramContext.getContentResolver(), paramUri, paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/provider/DocumentsContractApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */