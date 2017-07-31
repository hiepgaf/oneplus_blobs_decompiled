package android.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class BulkCursorDescriptor
  implements Parcelable
{
  public static final Parcelable.Creator<BulkCursorDescriptor> CREATOR = new Parcelable.Creator()
  {
    public BulkCursorDescriptor createFromParcel(Parcel paramAnonymousParcel)
    {
      BulkCursorDescriptor localBulkCursorDescriptor = new BulkCursorDescriptor();
      localBulkCursorDescriptor.readFromParcel(paramAnonymousParcel);
      return localBulkCursorDescriptor;
    }
    
    public BulkCursorDescriptor[] newArray(int paramAnonymousInt)
    {
      return new BulkCursorDescriptor[paramAnonymousInt];
    }
  };
  public String[] columnNames;
  public int count;
  public IBulkCursor cursor;
  public boolean wantsAllOnMoveCalls;
  public CursorWindow window;
  
  public int describeContents()
  {
    return 0;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    boolean bool = false;
    this.cursor = BulkCursorNative.asInterface(paramParcel.readStrongBinder());
    this.columnNames = paramParcel.readStringArray();
    if (paramParcel.readInt() != 0) {
      bool = true;
    }
    this.wantsAllOnMoveCalls = bool;
    this.count = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      this.window = ((CursorWindow)CursorWindow.CREATOR.createFromParcel(paramParcel));
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.cursor.asBinder());
    paramParcel.writeStringArray(this.columnNames);
    if (this.wantsAllOnMoveCalls) {}
    for (int i = 1;; i = 0)
    {
      paramParcel.writeInt(i);
      paramParcel.writeInt(this.count);
      if (this.window == null) {
        break;
      }
      paramParcel.writeInt(1);
      this.window.writeToParcel(paramParcel, paramInt);
      return;
    }
    paramParcel.writeInt(0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/BulkCursorDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */