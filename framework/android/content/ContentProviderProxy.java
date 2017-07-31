package android.content;

import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.ICancellationSignal.Stub;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

final class ContentProviderProxy
  implements IContentProvider
{
  private IBinder mRemote;
  
  public ContentProviderProxy(IBinder paramIBinder)
  {
    this.mRemote = paramIBinder;
  }
  
  public ContentProviderResult[] applyBatch(String paramString, ArrayList<ContentProviderOperation> paramArrayList)
    throws RemoteException, OperationApplicationException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      localParcel1.writeString(paramString);
      localParcel1.writeInt(paramArrayList.size());
      paramString = paramArrayList.iterator();
      while (paramString.hasNext()) {
        ((ContentProviderOperation)paramString.next()).writeToParcel(localParcel1, 0);
      }
      this.mRemote.transact(20, localParcel1, localParcel2, 0);
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
    DatabaseUtils.readExceptionWithOperationApplicationExceptionFromParcel(localParcel2);
    paramString = (ContentProviderResult[])localParcel2.createTypedArray(ContentProviderResult.CREATOR);
    localParcel1.recycle();
    localParcel2.recycle();
    return paramString;
  }
  
  public IBinder asBinder()
  {
    return this.mRemote;
  }
  
  public int bulkInsert(String paramString, Uri paramUri, ContentValues[] paramArrayOfContentValues)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      localParcel1.writeString(paramString);
      paramUri.writeToParcel(localParcel1, 0);
      localParcel1.writeTypedArray(paramArrayOfContentValues, 0);
      this.mRemote.transact(13, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      int i = localParcel2.readInt();
      return i;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public Bundle call(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      localParcel1.writeString(paramString1);
      localParcel1.writeString(paramString2);
      localParcel1.writeString(paramString3);
      localParcel1.writeBundle(paramBundle);
      this.mRemote.transact(21, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      paramString1 = localParcel2.readBundle();
      return paramString1;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public Uri canonicalize(String paramString, Uri paramUri)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      localParcel1.writeString(paramString);
      paramUri.writeToParcel(localParcel1, 0);
      this.mRemote.transact(25, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      paramString = (Uri)Uri.CREATOR.createFromParcel(localParcel2);
      return paramString;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public ICancellationSignal createCancellationSignal()
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      this.mRemote.transact(24, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      ICancellationSignal localICancellationSignal = ICancellationSignal.Stub.asInterface(localParcel2.readStrongBinder());
      return localICancellationSignal;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public int delete(String paramString1, Uri paramUri, String paramString2, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      localParcel1.writeString(paramString1);
      paramUri.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString2);
      localParcel1.writeStringArray(paramArrayOfString);
      this.mRemote.transact(4, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      int i = localParcel2.readInt();
      return i;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public String[] getStreamTypes(Uri paramUri, String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      paramUri.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString);
      this.mRemote.transact(22, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      paramUri = localParcel2.createStringArray();
      return paramUri;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public String getType(Uri paramUri)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      paramUri.writeToParcel(localParcel1, 0);
      this.mRemote.transact(2, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      paramUri = localParcel2.readString();
      return paramUri;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public Uri insert(String paramString, Uri paramUri, ContentValues paramContentValues)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      localParcel1.writeString(paramString);
      paramUri.writeToParcel(localParcel1, 0);
      paramContentValues.writeToParcel(localParcel1, 0);
      this.mRemote.transact(3, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      paramString = (Uri)Uri.CREATOR.createFromParcel(localParcel2);
      return paramString;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  /* Error */
  public android.content.res.AssetFileDescriptor openAssetFile(String paramString1, Uri paramUri, String paramString2, ICancellationSignal paramICancellationSignal)
    throws RemoteException, java.io.FileNotFoundException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: invokestatic 28	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   6: astore 6
    //   8: invokestatic 28	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   11: astore 7
    //   13: aload 6
    //   15: ldc 30
    //   17: invokevirtual 34	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   20: aload 6
    //   22: aload_1
    //   23: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   26: aload_2
    //   27: aload 6
    //   29: iconst_0
    //   30: invokevirtual 106	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
    //   33: aload 6
    //   35: aload_3
    //   36: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   39: aload 5
    //   41: astore_1
    //   42: aload 4
    //   44: ifnull +11 -> 55
    //   47: aload 4
    //   49: invokeinterface 177 1 0
    //   54: astore_1
    //   55: aload 6
    //   57: aload_1
    //   58: invokevirtual 180	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
    //   61: aload_0
    //   62: getfield 15	android/content/ContentProviderProxy:mRemote	Landroid/os/IBinder;
    //   65: bipush 15
    //   67: aload 6
    //   69: aload 7
    //   71: iconst_0
    //   72: invokeinterface 78 5 0
    //   77: pop
    //   78: aload 7
    //   80: invokestatic 183	android/database/DatabaseUtils:readExceptionWithFileNotFoundExceptionFromParcel	(Landroid/os/Parcel;)V
    //   83: aload 7
    //   85: invokevirtual 116	android/os/Parcel:readInt	()I
    //   88: ifeq +29 -> 117
    //   91: getstatic 186	android/content/res/AssetFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
    //   94: aload 7
    //   96: invokeinterface 135 2 0
    //   101: checkcast 185	android/content/res/AssetFileDescriptor
    //   104: astore_1
    //   105: aload 6
    //   107: invokevirtual 72	android/os/Parcel:recycle	()V
    //   110: aload 7
    //   112: invokevirtual 72	android/os/Parcel:recycle	()V
    //   115: aload_1
    //   116: areturn
    //   117: aconst_null
    //   118: astore_1
    //   119: goto -14 -> 105
    //   122: astore_1
    //   123: aload 6
    //   125: invokevirtual 72	android/os/Parcel:recycle	()V
    //   128: aload 7
    //   130: invokevirtual 72	android/os/Parcel:recycle	()V
    //   133: aload_1
    //   134: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	135	0	this	ContentProviderProxy
    //   0	135	1	paramString1	String
    //   0	135	2	paramUri	Uri
    //   0	135	3	paramString2	String
    //   0	135	4	paramICancellationSignal	ICancellationSignal
    //   1	39	5	localObject	Object
    //   6	118	6	localParcel1	Parcel
    //   11	118	7	localParcel2	Parcel
    // Exception table:
    //   from	to	target	type
    //   13	39	122	finally
    //   47	55	122	finally
    //   55	105	122	finally
  }
  
  /* Error */
  public android.os.ParcelFileDescriptor openFile(String paramString1, Uri paramUri, String paramString2, ICancellationSignal paramICancellationSignal, IBinder paramIBinder)
    throws RemoteException, java.io.FileNotFoundException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: invokestatic 28	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   6: astore 7
    //   8: invokestatic 28	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   11: astore 8
    //   13: aload 7
    //   15: ldc 30
    //   17: invokevirtual 34	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   20: aload 7
    //   22: aload_1
    //   23: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   26: aload_2
    //   27: aload 7
    //   29: iconst_0
    //   30: invokevirtual 106	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
    //   33: aload 7
    //   35: aload_3
    //   36: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   39: aload 6
    //   41: astore_1
    //   42: aload 4
    //   44: ifnull +11 -> 55
    //   47: aload 4
    //   49: invokeinterface 177 1 0
    //   54: astore_1
    //   55: aload 7
    //   57: aload_1
    //   58: invokevirtual 180	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
    //   61: aload 7
    //   63: aload 5
    //   65: invokevirtual 180	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
    //   68: aload_0
    //   69: getfield 15	android/content/ContentProviderProxy:mRemote	Landroid/os/IBinder;
    //   72: bipush 14
    //   74: aload 7
    //   76: aload 8
    //   78: iconst_0
    //   79: invokeinterface 78 5 0
    //   84: pop
    //   85: aload 8
    //   87: invokestatic 183	android/database/DatabaseUtils:readExceptionWithFileNotFoundExceptionFromParcel	(Landroid/os/Parcel;)V
    //   90: aload 8
    //   92: invokevirtual 116	android/os/Parcel:readInt	()I
    //   95: ifeq +29 -> 124
    //   98: getstatic 191	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
    //   101: aload 8
    //   103: invokeinterface 135 2 0
    //   108: checkcast 190	android/os/ParcelFileDescriptor
    //   111: astore_1
    //   112: aload 7
    //   114: invokevirtual 72	android/os/Parcel:recycle	()V
    //   117: aload 8
    //   119: invokevirtual 72	android/os/Parcel:recycle	()V
    //   122: aload_1
    //   123: areturn
    //   124: aconst_null
    //   125: astore_1
    //   126: goto -14 -> 112
    //   129: astore_1
    //   130: aload 7
    //   132: invokevirtual 72	android/os/Parcel:recycle	()V
    //   135: aload 8
    //   137: invokevirtual 72	android/os/Parcel:recycle	()V
    //   140: aload_1
    //   141: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	142	0	this	ContentProviderProxy
    //   0	142	1	paramString1	String
    //   0	142	2	paramUri	Uri
    //   0	142	3	paramString2	String
    //   0	142	4	paramICancellationSignal	ICancellationSignal
    //   0	142	5	paramIBinder	IBinder
    //   1	39	6	localObject	Object
    //   6	125	7	localParcel1	Parcel
    //   11	125	8	localParcel2	Parcel
    // Exception table:
    //   from	to	target	type
    //   13	39	129	finally
    //   47	55	129	finally
    //   55	112	129	finally
  }
  
  /* Error */
  public android.content.res.AssetFileDescriptor openTypedAssetFile(String paramString1, Uri paramUri, String paramString2, Bundle paramBundle, ICancellationSignal paramICancellationSignal)
    throws RemoteException, java.io.FileNotFoundException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: invokestatic 28	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   6: astore 7
    //   8: invokestatic 28	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   11: astore 8
    //   13: aload 7
    //   15: ldc 30
    //   17: invokevirtual 34	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   20: aload 7
    //   22: aload_1
    //   23: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   26: aload_2
    //   27: aload 7
    //   29: iconst_0
    //   30: invokevirtual 106	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
    //   33: aload 7
    //   35: aload_3
    //   36: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   39: aload 7
    //   41: aload 4
    //   43: invokevirtual 122	android/os/Parcel:writeBundle	(Landroid/os/Bundle;)V
    //   46: aload 6
    //   48: astore_1
    //   49: aload 5
    //   51: ifnull +11 -> 62
    //   54: aload 5
    //   56: invokeinterface 177 1 0
    //   61: astore_1
    //   62: aload 7
    //   64: aload_1
    //   65: invokevirtual 180	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
    //   68: aload_0
    //   69: getfield 15	android/content/ContentProviderProxy:mRemote	Landroid/os/IBinder;
    //   72: bipush 23
    //   74: aload 7
    //   76: aload 8
    //   78: iconst_0
    //   79: invokeinterface 78 5 0
    //   84: pop
    //   85: aload 8
    //   87: invokestatic 183	android/database/DatabaseUtils:readExceptionWithFileNotFoundExceptionFromParcel	(Landroid/os/Parcel;)V
    //   90: aload 8
    //   92: invokevirtual 116	android/os/Parcel:readInt	()I
    //   95: ifeq +29 -> 124
    //   98: getstatic 186	android/content/res/AssetFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
    //   101: aload 8
    //   103: invokeinterface 135 2 0
    //   108: checkcast 185	android/content/res/AssetFileDescriptor
    //   111: astore_1
    //   112: aload 7
    //   114: invokevirtual 72	android/os/Parcel:recycle	()V
    //   117: aload 8
    //   119: invokevirtual 72	android/os/Parcel:recycle	()V
    //   122: aload_1
    //   123: areturn
    //   124: aconst_null
    //   125: astore_1
    //   126: goto -14 -> 112
    //   129: astore_1
    //   130: aload 7
    //   132: invokevirtual 72	android/os/Parcel:recycle	()V
    //   135: aload 8
    //   137: invokevirtual 72	android/os/Parcel:recycle	()V
    //   140: aload_1
    //   141: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	142	0	this	ContentProviderProxy
    //   0	142	1	paramString1	String
    //   0	142	2	paramUri	Uri
    //   0	142	3	paramString2	String
    //   0	142	4	paramBundle	Bundle
    //   0	142	5	paramICancellationSignal	ICancellationSignal
    //   1	46	6	localObject	Object
    //   6	125	7	localParcel1	Parcel
    //   11	125	8	localParcel2	Parcel
    // Exception table:
    //   from	to	target	type
    //   13	46	129	finally
    //   54	62	129	finally
    //   62	112	129	finally
  }
  
  /* Error */
  public android.database.Cursor query(String paramString1, Uri paramUri, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, ICancellationSignal paramICancellationSignal)
    throws RemoteException
  {
    // Byte code:
    //   0: new 199	android/database/BulkCursorToCursorAdaptor
    //   3: dup
    //   4: invokespecial 200	android/database/BulkCursorToCursorAdaptor:<init>	()V
    //   7: astore 10
    //   9: invokestatic 28	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   12: astore 11
    //   14: invokestatic 28	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   17: astore 12
    //   19: aload 11
    //   21: ldc 30
    //   23: invokevirtual 34	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   26: aload 11
    //   28: aload_1
    //   29: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   32: aload_2
    //   33: aload 11
    //   35: iconst_0
    //   36: invokevirtual 106	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
    //   39: iconst_0
    //   40: istore 8
    //   42: aload_3
    //   43: ifnull +7 -> 50
    //   46: aload_3
    //   47: arraylength
    //   48: istore 8
    //   50: aload 11
    //   52: iload 8
    //   54: invokevirtual 47	android/os/Parcel:writeInt	(I)V
    //   57: iconst_0
    //   58: istore 9
    //   60: iload 9
    //   62: iload 8
    //   64: if_icmpge +21 -> 85
    //   67: aload 11
    //   69: aload_3
    //   70: iload 9
    //   72: aaload
    //   73: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   76: iload 9
    //   78: iconst_1
    //   79: iadd
    //   80: istore 9
    //   82: goto -22 -> 60
    //   85: aload 11
    //   87: aload 4
    //   89: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   92: aload 5
    //   94: ifnull +191 -> 285
    //   97: aload 5
    //   99: arraylength
    //   100: istore 8
    //   102: aload 11
    //   104: iload 8
    //   106: invokevirtual 47	android/os/Parcel:writeInt	(I)V
    //   109: iconst_0
    //   110: istore 9
    //   112: iload 9
    //   114: iload 8
    //   116: if_icmpge +22 -> 138
    //   119: aload 11
    //   121: aload 5
    //   123: iload 9
    //   125: aaload
    //   126: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   129: iload 9
    //   131: iconst_1
    //   132: iadd
    //   133: istore 9
    //   135: goto -23 -> 112
    //   138: aload 11
    //   140: aload 6
    //   142: invokevirtual 37	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   145: aload 11
    //   147: aload 10
    //   149: invokevirtual 204	android/database/BulkCursorToCursorAdaptor:getObserver	()Landroid/database/IContentObserver;
    //   152: invokeinterface 207 1 0
    //   157: invokevirtual 180	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
    //   160: aload 7
    //   162: ifnull +79 -> 241
    //   165: aload 7
    //   167: invokeinterface 177 1 0
    //   172: astore_1
    //   173: aload 11
    //   175: aload_1
    //   176: invokevirtual 180	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
    //   179: aload_0
    //   180: getfield 15	android/content/ContentProviderProxy:mRemote	Landroid/os/IBinder;
    //   183: iconst_1
    //   184: aload 11
    //   186: aload 12
    //   188: iconst_0
    //   189: invokeinterface 78 5 0
    //   194: pop
    //   195: aload 12
    //   197: invokestatic 113	android/database/DatabaseUtils:readExceptionFromParcel	(Landroid/os/Parcel;)V
    //   200: aload 12
    //   202: invokevirtual 116	android/os/Parcel:readInt	()I
    //   205: ifeq +41 -> 246
    //   208: aload 10
    //   210: getstatic 210	android/database/BulkCursorDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
    //   213: aload 12
    //   215: invokeinterface 135 2 0
    //   220: checkcast 209	android/database/BulkCursorDescriptor
    //   223: invokevirtual 214	android/database/BulkCursorToCursorAdaptor:initialize	(Landroid/database/BulkCursorDescriptor;)V
    //   226: aload 10
    //   228: astore_1
    //   229: aload 11
    //   231: invokevirtual 72	android/os/Parcel:recycle	()V
    //   234: aload 12
    //   236: invokevirtual 72	android/os/Parcel:recycle	()V
    //   239: aload_1
    //   240: areturn
    //   241: aconst_null
    //   242: astore_1
    //   243: goto -70 -> 173
    //   246: aload 10
    //   248: invokevirtual 217	android/database/BulkCursorToCursorAdaptor:close	()V
    //   251: aconst_null
    //   252: astore_1
    //   253: goto -24 -> 229
    //   256: astore_1
    //   257: aload 10
    //   259: invokevirtual 217	android/database/BulkCursorToCursorAdaptor:close	()V
    //   262: aload_1
    //   263: athrow
    //   264: astore_1
    //   265: aload 11
    //   267: invokevirtual 72	android/os/Parcel:recycle	()V
    //   270: aload 12
    //   272: invokevirtual 72	android/os/Parcel:recycle	()V
    //   275: aload_1
    //   276: athrow
    //   277: astore_1
    //   278: aload 10
    //   280: invokevirtual 217	android/database/BulkCursorToCursorAdaptor:close	()V
    //   283: aload_1
    //   284: athrow
    //   285: iconst_0
    //   286: istore 8
    //   288: goto -186 -> 102
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	291	0	this	ContentProviderProxy
    //   0	291	1	paramString1	String
    //   0	291	2	paramUri	Uri
    //   0	291	3	paramArrayOfString1	String[]
    //   0	291	4	paramString2	String
    //   0	291	5	paramArrayOfString2	String[]
    //   0	291	6	paramString3	String
    //   0	291	7	paramICancellationSignal	ICancellationSignal
    //   40	247	8	i	int
    //   58	76	9	j	int
    //   7	272	10	localBulkCursorToCursorAdaptor	android.database.BulkCursorToCursorAdaptor
    //   12	254	11	localParcel1	Parcel
    //   17	254	12	localParcel2	Parcel
    // Exception table:
    //   from	to	target	type
    //   19	39	256	java/lang/RuntimeException
    //   46	50	256	java/lang/RuntimeException
    //   50	57	256	java/lang/RuntimeException
    //   67	76	256	java/lang/RuntimeException
    //   85	92	256	java/lang/RuntimeException
    //   97	102	256	java/lang/RuntimeException
    //   102	109	256	java/lang/RuntimeException
    //   119	129	256	java/lang/RuntimeException
    //   138	160	256	java/lang/RuntimeException
    //   165	173	256	java/lang/RuntimeException
    //   173	226	256	java/lang/RuntimeException
    //   246	251	256	java/lang/RuntimeException
    //   19	39	264	finally
    //   46	50	264	finally
    //   50	57	264	finally
    //   67	76	264	finally
    //   85	92	264	finally
    //   97	102	264	finally
    //   102	109	264	finally
    //   119	129	264	finally
    //   138	160	264	finally
    //   165	173	264	finally
    //   173	226	264	finally
    //   246	251	264	finally
    //   257	264	264	finally
    //   278	285	264	finally
    //   19	39	277	android/os/RemoteException
    //   46	50	277	android/os/RemoteException
    //   50	57	277	android/os/RemoteException
    //   67	76	277	android/os/RemoteException
    //   85	92	277	android/os/RemoteException
    //   97	102	277	android/os/RemoteException
    //   102	109	277	android/os/RemoteException
    //   119	129	277	android/os/RemoteException
    //   138	160	277	android/os/RemoteException
    //   165	173	277	android/os/RemoteException
    //   173	226	277	android/os/RemoteException
    //   246	251	277	android/os/RemoteException
  }
  
  public Uri uncanonicalize(String paramString, Uri paramUri)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      localParcel1.writeString(paramString);
      paramUri.writeToParcel(localParcel1, 0);
      this.mRemote.transact(26, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      paramString = (Uri)Uri.CREATOR.createFromParcel(localParcel2);
      return paramString;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public int update(String paramString1, Uri paramUri, ContentValues paramContentValues, String paramString2, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken("android.content.IContentProvider");
      localParcel1.writeString(paramString1);
      paramUri.writeToParcel(localParcel1, 0);
      paramContentValues.writeToParcel(localParcel1, 0);
      localParcel1.writeString(paramString2);
      localParcel1.writeStringArray(paramArrayOfString);
      this.mRemote.transact(10, localParcel1, localParcel2, 0);
      DatabaseUtils.readExceptionFromParcel(localParcel2);
      int i = localParcel2.readInt();
      return i;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentProviderProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */