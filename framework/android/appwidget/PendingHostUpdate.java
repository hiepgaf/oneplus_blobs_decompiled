package android.appwidget;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.widget.RemoteViews;

public class PendingHostUpdate
  implements Parcelable
{
  public static final Parcelable.Creator<PendingHostUpdate> CREATOR = new Parcelable.Creator()
  {
    public PendingHostUpdate createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PendingHostUpdate(paramAnonymousParcel, null);
    }
    
    public PendingHostUpdate[] newArray(int paramAnonymousInt)
    {
      return new PendingHostUpdate[paramAnonymousInt];
    }
  };
  static final int TYPE_PROVIDER_CHANGED = 1;
  static final int TYPE_VIEWS_UPDATE = 0;
  static final int TYPE_VIEW_DATA_CHANGED = 2;
  final int appWidgetId;
  final int type;
  int viewId;
  RemoteViews views;
  AppWidgetProviderInfo widgetInfo;
  
  private PendingHostUpdate(int paramInt1, int paramInt2)
  {
    this.appWidgetId = paramInt1;
    this.type = paramInt2;
  }
  
  private PendingHostUpdate(Parcel paramParcel)
  {
    this.appWidgetId = paramParcel.readInt();
    this.type = paramParcel.readInt();
    switch (this.type)
    {
    default: 
    case 0: 
    case 1: 
      do
      {
        do
        {
          return;
        } while (paramParcel.readInt() == 0);
        this.views = new RemoteViews(paramParcel);
        return;
      } while (paramParcel.readInt() == 0);
      this.widgetInfo = new AppWidgetProviderInfo(paramParcel);
      return;
    }
    this.viewId = paramParcel.readInt();
  }
  
  public static PendingHostUpdate providerChanged(int paramInt, AppWidgetProviderInfo paramAppWidgetProviderInfo)
  {
    PendingHostUpdate localPendingHostUpdate = new PendingHostUpdate(paramInt, 1);
    localPendingHostUpdate.widgetInfo = paramAppWidgetProviderInfo;
    return localPendingHostUpdate;
  }
  
  public static PendingHostUpdate updateAppWidget(int paramInt, RemoteViews paramRemoteViews)
  {
    PendingHostUpdate localPendingHostUpdate = new PendingHostUpdate(paramInt, 0);
    localPendingHostUpdate.views = paramRemoteViews;
    return localPendingHostUpdate;
  }
  
  public static PendingHostUpdate viewDataChanged(int paramInt1, int paramInt2)
  {
    PendingHostUpdate localPendingHostUpdate = new PendingHostUpdate(paramInt1, 2);
    localPendingHostUpdate.viewId = paramInt2;
    return localPendingHostUpdate;
  }
  
  private void writeNullParcelable(Parcelable paramParcelable, Parcel paramParcel, int paramInt)
  {
    if (paramParcelable != null)
    {
      paramParcel.writeInt(1);
      paramParcelable.writeToParcel(paramParcel, paramInt);
      return;
    }
    paramParcel.writeInt(0);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.appWidgetId);
    paramParcel.writeInt(this.type);
    switch (this.type)
    {
    default: 
      return;
    case 0: 
      writeNullParcelable(this.views, paramParcel, paramInt);
      return;
    case 1: 
      writeNullParcelable(this.widgetInfo, paramParcel, paramInt);
      return;
    }
    paramParcel.writeInt(this.viewId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/appwidget/PendingHostUpdate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */