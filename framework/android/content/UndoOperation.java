package android.content;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class UndoOperation<DATA>
  implements Parcelable
{
  UndoOwner mOwner;
  
  public UndoOperation(UndoOwner paramUndoOwner)
  {
    this.mOwner = paramUndoOwner;
  }
  
  protected UndoOperation(Parcel paramParcel, ClassLoader paramClassLoader) {}
  
  public boolean allowMerge()
  {
    return true;
  }
  
  public abstract void commit();
  
  public int describeContents()
  {
    return 0;
  }
  
  public UndoOwner getOwner()
  {
    return this.mOwner;
  }
  
  public DATA getOwnerData()
  {
    return (DATA)this.mOwner.getData();
  }
  
  public boolean hasData()
  {
    return true;
  }
  
  public boolean matchOwner(UndoOwner paramUndoOwner)
  {
    return paramUndoOwner == getOwner();
  }
  
  public abstract void redo();
  
  public abstract void undo();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/UndoOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */