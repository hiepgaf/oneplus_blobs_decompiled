package android.content;

import android.content.res.AssetFileDescriptor;
import android.database.BulkCursorDescriptor;
import android.database.Cursor;
import android.database.CursorToBulkCursorAdaptor;
import android.database.DatabaseUtils;
import android.database.IContentObserver;
import android.database.IContentObserver.Stub;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.ICancellationSignal.Stub;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;

public abstract class ContentProviderNative
  extends Binder
  implements IContentProvider
{
  public ContentProviderNative()
  {
    attachInterface(this, "android.content.IContentProvider");
  }
  
  public static IContentProvider asInterface(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IContentProvider localIContentProvider = (IContentProvider)paramIBinder.queryLocalInterface("android.content.IContentProvider");
    if (localIContentProvider != null) {
      return localIContentProvider;
    }
    return new ContentProviderProxy(paramIBinder);
  }
  
  public IBinder asBinder()
  {
    return this;
  }
  
  public abstract String getProviderName();
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    Object localObject1;
    Object localObject2;
    switch (paramInt1)
    {
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 11: 
    case 12: 
    case 16: 
    case 17: 
    case 18: 
    case 19: 
    default: 
      return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
    case 1: 
      for (;;)
      {
        try
        {
          paramParcel1.enforceInterface("android.content.IContentProvider");
          Object localObject5 = paramParcel1.readString();
          Uri localUri = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          paramInt2 = paramParcel1.readInt();
          localObject1 = null;
          if (paramInt2 > 0)
          {
            localObject2 = new String[paramInt2];
            paramInt1 = 0;
            localObject1 = localObject2;
            if (paramInt1 < paramInt2)
            {
              localObject2[paramInt1] = paramParcel1.readString();
              paramInt1 += 1;
              continue;
            }
          }
          String str1 = paramParcel1.readString();
          paramInt2 = paramParcel1.readInt();
          localObject2 = null;
          if (paramInt2 > 0)
          {
            localObject3 = new String[paramInt2];
            paramInt1 = 0;
            localObject2 = localObject3;
            if (paramInt1 < paramInt2)
            {
              localObject3[paramInt1] = paramParcel1.readString();
              paramInt1 += 1;
              continue;
            }
          }
          String str2 = paramParcel1.readString();
          Object localObject3 = IContentObserver.Stub.asInterface(paramParcel1.readStrongBinder());
          localObject1 = query((String)localObject5, localUri, (String[])localObject1, str1, (String[])localObject2, str2, ICancellationSignal.Stub.asInterface(paramParcel1.readStrongBinder()));
          if (localObject1 != null)
          {
            paramParcel1 = (Parcel)localObject1;
            try
            {
              localObject1 = new CursorToBulkCursorAdaptor((Cursor)localObject1, (IContentObserver)localObject3, getProviderName());
              localObject2 = null;
              localObject3 = null;
              if (localObject1 == null) {
                continue;
              }
            }
            finally
            {
              try
              {
                localObject5 = ((CursorToBulkCursorAdaptor)localObject1).getBulkCursorDescriptor();
                paramParcel1 = (Parcel)localObject3;
                paramParcel2.writeNoException();
                paramParcel1 = (Parcel)localObject3;
                paramParcel2.writeInt(1);
                paramParcel1 = (Parcel)localObject3;
                ((BulkCursorDescriptor)localObject5).writeToParcel(paramParcel2, 1);
                return true;
              }
              finally
              {
                continue;
              }
              localObject1 = null;
              localObject4 = finally;
              localObject2 = paramParcel1;
              paramParcel1 = (Parcel)localObject4;
            }
            ((CursorToBulkCursorAdaptor)localObject1).close();
            if (localObject2 != null) {
              ((Cursor)localObject2).close();
            }
            throw paramParcel1;
          }
        }
        catch (Exception paramParcel1)
        {
          DatabaseUtils.writeExceptionToParcel(paramParcel2, paramParcel1);
          return true;
        }
        paramParcel2.writeNoException();
        paramParcel2.writeInt(0);
      }
    case 2: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = getType((Uri)Uri.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    case 3: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = insert(paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), (ContentValues)ContentValues.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      Uri.writeToParcel(paramParcel2, paramParcel1);
      return true;
    case 13: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramInt1 = bulkInsert(paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), (ContentValues[])paramParcel1.createTypedArray(ContentValues.CREATOR));
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 20: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      localObject1 = paramParcel1.readString();
      paramInt2 = paramParcel1.readInt();
      localObject2 = new ArrayList(paramInt2);
      paramInt1 = 0;
      while (paramInt1 < paramInt2)
      {
        ((ArrayList)localObject2).add(paramInt1, (ContentProviderOperation)ContentProviderOperation.CREATOR.createFromParcel(paramParcel1));
        paramInt1 += 1;
      }
      paramParcel1 = applyBatch((String)localObject1, (ArrayList)localObject2);
      paramParcel2.writeNoException();
      paramParcel2.writeTypedArray(paramParcel1, 0);
      return true;
    case 4: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramInt1 = delete(paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString(), paramParcel1.readStringArray());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 10: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramInt1 = update(paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), (ContentValues)ContentValues.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString(), paramParcel1.readStringArray());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    case 14: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = openFile(paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString(), ICancellationSignal.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      else
      {
        paramParcel2.writeInt(0);
      }
      break;
    case 15: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = openAssetFile(paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString(), ICancellationSignal.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      else
      {
        paramParcel2.writeInt(0);
      }
      break;
    case 21: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = call(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readBundle());
      paramParcel2.writeNoException();
      paramParcel2.writeBundle(paramParcel1);
      return true;
    case 22: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = getStreamTypes((Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeStringArray(paramParcel1);
      return true;
    case 23: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = openTypedAssetFile(paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1), paramParcel1.readString(), paramParcel1.readBundle(), ICancellationSignal.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      else
      {
        paramParcel2.writeInt(0);
      }
      break;
    case 24: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = createCancellationSignal();
      paramParcel2.writeNoException();
      paramParcel2.writeStrongBinder(paramParcel1.asBinder());
      return true;
    case 25: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = canonicalize(paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      Uri.writeToParcel(paramParcel2, paramParcel1);
      return true;
    case 26: 
      paramParcel1.enforceInterface("android.content.IContentProvider");
      paramParcel1 = uncanonicalize(paramParcel1.readString(), (Uri)Uri.CREATOR.createFromParcel(paramParcel1));
      paramParcel2.writeNoException();
      Uri.writeToParcel(paramParcel2, paramParcel1);
      return true;
    }
    return true;
    return true;
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentProviderNative.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */