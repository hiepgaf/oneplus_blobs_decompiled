package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParceledListSlice<T extends Parcelable>
  implements Parcelable
{
  public static final Parcelable.ClassLoaderCreator<ParceledListSlice> CREATOR = new Parcelable.ClassLoaderCreator()
  {
    public ParceledListSlice createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ParceledListSlice(paramAnonymousParcel, null, null);
    }
    
    public ParceledListSlice createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
    {
      return new ParceledListSlice(paramAnonymousParcel, paramAnonymousClassLoader, null);
    }
    
    public ParceledListSlice[] newArray(int paramAnonymousInt)
    {
      return new ParceledListSlice[paramAnonymousInt];
    }
  };
  private static boolean DEBUG = false;
  private static final int MAX_IPC_SIZE = 65536;
  private static String TAG = "ParceledListSlice";
  private final List<T> mList;
  
  static
  {
    DEBUG = false;
  }
  
  private ParceledListSlice(Parcel paramParcel, ClassLoader paramClassLoader)
  {
    int j = paramParcel.readInt();
    this.mList = new ArrayList(j);
    if (DEBUG) {
      Log.d(TAG, "Retrieving " + j + " items");
    }
    if (j <= 0) {
      return;
    }
    Parcelable.Creator localCreator = paramParcel.readParcelableCreator(paramClassLoader);
    Class localClass = null;
    int i = 0;
    Object localObject;
    if ((i >= j) || (paramParcel.readInt() == 0))
    {
      if (i < j) {}
    }
    else
    {
      localObject = paramParcel.readCreator(localCreator, paramClassLoader);
      if (localClass == null) {
        localClass = localObject.getClass();
      }
      for (;;)
      {
        this.mList.add(localObject);
        if (DEBUG) {
          Log.d(TAG, "Read inline #" + i + ": " + this.mList.get(this.mList.size() - 1));
        }
        i += 1;
        break;
        verifySameType(localClass, localObject.getClass());
      }
    }
    paramParcel = paramParcel.readStrongBinder();
    while (i < j)
    {
      if (DEBUG) {
        Log.d(TAG, "Reading more @" + i + " of " + j + ": retriever=" + paramParcel);
      }
      localObject = Parcel.obtain();
      Parcel localParcel = Parcel.obtain();
      ((Parcel)localObject).writeInt(i);
      try
      {
        paramParcel.transact(1, (Parcel)localObject, localParcel, 0);
        while ((i < j) && (localParcel.readInt() != 0))
        {
          Parcelable localParcelable = localParcel.readCreator(localCreator, paramClassLoader);
          verifySameType(localClass, localParcelable.getClass());
          this.mList.add(localParcelable);
          if (DEBUG) {
            Log.d(TAG, "Read extra #" + i + ": " + this.mList.get(this.mList.size() - 1));
          }
          i += 1;
        }
        localParcel.recycle();
      }
      catch (RemoteException paramParcel)
      {
        Log.w(TAG, "Failure retrieving array; only received " + i + " of " + j, paramParcel);
        return;
      }
      ((Parcel)localObject).recycle();
    }
  }
  
  public ParceledListSlice(List<T> paramList)
  {
    this.mList = paramList;
  }
  
  public static <T extends Parcelable> ParceledListSlice<T> emptyList()
  {
    return new ParceledListSlice(Collections.emptyList());
  }
  
  private static void verifySameType(Class<?> paramClass1, Class<?> paramClass2)
  {
    if (!paramClass2.equals(paramClass1)) {
      throw new IllegalArgumentException("Can't unparcel type " + paramClass2.getName() + " in list of type " + paramClass1.getName());
    }
  }
  
  public int describeContents()
  {
    int j = 0;
    int i = 0;
    while (i < this.mList.size())
    {
      j |= ((Parcelable)this.mList.get(i)).describeContents();
      i += 1;
    }
    return j;
  }
  
  public List<T> getList()
  {
    return this.mList;
  }
  
  public void writeToParcel(Parcel paramParcel, final int paramInt)
  {
    final int j = this.mList.size();
    paramParcel.writeInt(j);
    if (DEBUG) {
      Log.d(TAG, "Writing " + j + " items");
    }
    if (j > 0)
    {
      final Object localObject = ((Parcelable)this.mList.get(0)).getClass();
      paramParcel.writeParcelableCreator((Parcelable)this.mList.get(0));
      int i = 0;
      while ((i < j) && (paramParcel.dataSize() < 65536))
      {
        paramParcel.writeInt(1);
        Parcelable localParcelable = (Parcelable)this.mList.get(i);
        verifySameType((Class)localObject, localParcelable.getClass());
        localParcelable.writeToParcel(paramParcel, paramInt);
        if (DEBUG) {
          Log.d(TAG, "Wrote inline #" + i + ": " + this.mList.get(i));
        }
        i += 1;
      }
      if (i < j)
      {
        paramParcel.writeInt(0);
        localObject = new Binder()
        {
          protected boolean onTransact(int paramAnonymousInt1, Parcel paramAnonymousParcel1, Parcel paramAnonymousParcel2, int paramAnonymousInt2)
            throws RemoteException
          {
            if (paramAnonymousInt1 != 1) {
              return super.onTransact(paramAnonymousInt1, paramAnonymousParcel1, paramAnonymousParcel2, paramAnonymousInt2);
            }
            paramAnonymousInt2 = paramAnonymousParcel1.readInt();
            paramAnonymousInt1 = paramAnonymousInt2;
            if (ParceledListSlice.-get0())
            {
              Log.d(ParceledListSlice.-get1(), "Writing more @" + paramAnonymousInt2 + " of " + j);
              paramAnonymousInt1 = paramAnonymousInt2;
            }
            while ((paramAnonymousInt1 < j) && (paramAnonymousParcel2.dataSize() < 65536))
            {
              paramAnonymousParcel2.writeInt(1);
              paramAnonymousParcel1 = (Parcelable)ParceledListSlice.-get2(ParceledListSlice.this).get(paramAnonymousInt1);
              ParceledListSlice.-wrap0(localObject, paramAnonymousParcel1.getClass());
              paramAnonymousParcel1.writeToParcel(paramAnonymousParcel2, paramInt);
              if (ParceledListSlice.-get0()) {
                Log.d(ParceledListSlice.-get1(), "Wrote extra #" + paramAnonymousInt1 + ": " + ParceledListSlice.-get2(ParceledListSlice.this).get(paramAnonymousInt1));
              }
              paramAnonymousInt1 += 1;
            }
            if (paramAnonymousInt1 < j)
            {
              if (ParceledListSlice.-get0()) {
                Log.d(ParceledListSlice.-get1(), "Breaking @" + paramAnonymousInt1 + " of " + j);
              }
              paramAnonymousParcel2.writeInt(0);
            }
            return true;
          }
        };
        if (DEBUG) {
          Log.d(TAG, "Breaking @" + i + " of " + j + ": retriever=" + localObject);
        }
        paramParcel.writeStrongBinder((IBinder)localObject);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ParceledListSlice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */