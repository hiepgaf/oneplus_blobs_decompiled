package android.support.v4.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

public abstract class CursorAdapter
  extends BaseAdapter
  implements Filterable, CursorFilter.CursorFilterClient
{
  @Deprecated
  public static final int FLAG_AUTO_REQUERY = 1;
  public static final int FLAG_REGISTER_CONTENT_OBSERVER = 2;
  protected boolean mAutoRequery;
  protected ChangeObserver mChangeObserver;
  protected Context mContext;
  protected Cursor mCursor;
  protected CursorFilter mCursorFilter;
  protected DataSetObserver mDataSetObserver;
  protected boolean mDataValid;
  protected FilterQueryProvider mFilterQueryProvider;
  protected int mRowIDColumn;
  
  @Deprecated
  public CursorAdapter(Context paramContext, Cursor paramCursor)
  {
    init(paramContext, paramCursor, 1);
  }
  
  public CursorAdapter(Context paramContext, Cursor paramCursor, int paramInt)
  {
    init(paramContext, paramCursor, paramInt);
  }
  
  public CursorAdapter(Context paramContext, Cursor paramCursor, boolean paramBoolean)
  {
    if (!paramBoolean) {}
    for (int i = 2;; i = 1)
    {
      init(paramContext, paramCursor, i);
      return;
    }
  }
  
  public abstract void bindView(View paramView, Context paramContext, Cursor paramCursor);
  
  public void changeCursor(Cursor paramCursor)
  {
    paramCursor = swapCursor(paramCursor);
    if (paramCursor == null) {
      return;
    }
    paramCursor.close();
  }
  
  public CharSequence convertToString(Cursor paramCursor)
  {
    if (paramCursor != null) {
      return paramCursor.toString();
    }
    return "";
  }
  
  public int getCount()
  {
    if (!this.mDataValid) {}
    while (this.mCursor == null) {
      return 0;
    }
    return this.mCursor.getCount();
  }
  
  public Cursor getCursor()
  {
    return this.mCursor;
  }
  
  public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (!this.mDataValid) {
      return null;
    }
    this.mCursor.moveToPosition(paramInt);
    if (paramView != null) {}
    for (;;)
    {
      bindView(paramView, this.mContext, this.mCursor);
      return paramView;
      paramView = newDropDownView(this.mContext, this.mCursor, paramViewGroup);
    }
  }
  
  public Filter getFilter()
  {
    if (this.mCursorFilter != null) {}
    for (;;)
    {
      return this.mCursorFilter;
      this.mCursorFilter = new CursorFilter(this);
    }
  }
  
  public FilterQueryProvider getFilterQueryProvider()
  {
    return this.mFilterQueryProvider;
  }
  
  public Object getItem(int paramInt)
  {
    if (!this.mDataValid) {}
    while (this.mCursor == null) {
      return null;
    }
    this.mCursor.moveToPosition(paramInt);
    return this.mCursor;
  }
  
  public long getItemId(int paramInt)
  {
    if (!this.mDataValid) {}
    while (this.mCursor == null) {
      return 0L;
    }
    if (!this.mCursor.moveToPosition(paramInt)) {
      return 0L;
    }
    return this.mCursor.getLong(this.mRowIDColumn);
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (this.mDataValid)
    {
      if (!this.mCursor.moveToPosition(paramInt)) {
        break label49;
      }
      if (paramView == null) {
        break label76;
      }
    }
    for (;;)
    {
      bindView(paramView, this.mContext, this.mCursor);
      return paramView;
      throw new IllegalStateException("this should only be called when the cursor is valid");
      label49:
      throw new IllegalStateException("couldn't move cursor to position " + paramInt);
      label76:
      paramView = newView(this.mContext, this.mCursor, paramViewGroup);
    }
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  void init(Context paramContext, Cursor paramCursor, int paramInt)
  {
    boolean bool = false;
    label19:
    int i;
    if ((paramInt & 0x1) != 1)
    {
      this.mAutoRequery = false;
      if (paramCursor != null) {
        break label84;
      }
      this.mCursor = paramCursor;
      this.mDataValid = bool;
      this.mContext = paramContext;
      if (bool) {
        break label90;
      }
      i = -1;
      label43:
      this.mRowIDColumn = i;
      if ((paramInt & 0x2) == 2) {
        break label103;
      }
      this.mChangeObserver = null;
      this.mDataSetObserver = null;
      label66:
      if (bool) {
        break label131;
      }
    }
    for (;;)
    {
      return;
      paramInt |= 0x2;
      this.mAutoRequery = true;
      break;
      label84:
      bool = true;
      break label19;
      label90:
      i = paramCursor.getColumnIndexOrThrow("_id");
      break label43;
      label103:
      this.mChangeObserver = new ChangeObserver();
      this.mDataSetObserver = new MyDataSetObserver(null);
      break label66;
      label131:
      if (this.mChangeObserver == null) {}
      while (this.mDataSetObserver != null)
      {
        paramCursor.registerDataSetObserver(this.mDataSetObserver);
        return;
        paramCursor.registerContentObserver(this.mChangeObserver);
      }
    }
  }
  
  @Deprecated
  protected void init(Context paramContext, Cursor paramCursor, boolean paramBoolean)
  {
    if (!paramBoolean) {}
    for (int i = 2;; i = 1)
    {
      init(paramContext, paramCursor, i);
      return;
    }
  }
  
  public View newDropDownView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
  {
    return newView(paramContext, paramCursor, paramViewGroup);
  }
  
  public abstract View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup);
  
  protected void onContentChanged()
  {
    if (!this.mAutoRequery) {}
    while ((this.mCursor == null) || (this.mCursor.isClosed())) {
      return;
    }
    this.mDataValid = this.mCursor.requery();
  }
  
  public Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence)
  {
    if (this.mFilterQueryProvider == null) {
      return this.mCursor;
    }
    return this.mFilterQueryProvider.runQuery(paramCharSequence);
  }
  
  public void setFilterQueryProvider(FilterQueryProvider paramFilterQueryProvider)
  {
    this.mFilterQueryProvider = paramFilterQueryProvider;
  }
  
  public Cursor swapCursor(Cursor paramCursor)
  {
    Cursor localCursor;
    if (paramCursor != this.mCursor)
    {
      localCursor = this.mCursor;
      if (localCursor != null) {
        break label44;
      }
    }
    label44:
    label82:
    for (;;)
    {
      this.mCursor = paramCursor;
      if (paramCursor != null) {
        break;
      }
      this.mRowIDColumn = -1;
      this.mDataValid = false;
      notifyDataSetInvalidated();
      return localCursor;
      return null;
      if (this.mChangeObserver == null) {}
      for (;;)
      {
        if (this.mDataSetObserver == null) {
          break label82;
        }
        localCursor.unregisterDataSetObserver(this.mDataSetObserver);
        break;
        localCursor.unregisterContentObserver(this.mChangeObserver);
      }
    }
    if (this.mChangeObserver == null) {
      if (this.mDataSetObserver != null) {
        break label134;
      }
    }
    for (;;)
    {
      this.mRowIDColumn = paramCursor.getColumnIndexOrThrow("_id");
      this.mDataValid = true;
      notifyDataSetChanged();
      return localCursor;
      paramCursor.registerContentObserver(this.mChangeObserver);
      break;
      label134:
      paramCursor.registerDataSetObserver(this.mDataSetObserver);
    }
  }
  
  private class ChangeObserver
    extends ContentObserver
  {
    public ChangeObserver()
    {
      super();
    }
    
    public boolean deliverSelfNotifications()
    {
      return true;
    }
    
    public void onChange(boolean paramBoolean)
    {
      CursorAdapter.this.onContentChanged();
    }
  }
  
  private class MyDataSetObserver
    extends DataSetObserver
  {
    private MyDataSetObserver() {}
    
    public void onChanged()
    {
      CursorAdapter.this.mDataValid = true;
      CursorAdapter.this.notifyDataSetChanged();
    }
    
    public void onInvalidated()
    {
      CursorAdapter.this.mDataValid = false;
      CursorAdapter.this.notifyDataSetInvalidated();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/widget/CursorAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */