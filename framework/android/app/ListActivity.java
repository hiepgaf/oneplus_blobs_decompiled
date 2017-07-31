package android.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListActivity
  extends Activity
{
  protected ListAdapter mAdapter;
  private boolean mFinishedStart = false;
  private Handler mHandler = new Handler();
  protected ListView mList;
  private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      ListActivity.this.onListItemClick((ListView)paramAnonymousAdapterView, paramAnonymousView, paramAnonymousInt, paramAnonymousLong);
    }
  };
  private Runnable mRequestFocus = new Runnable()
  {
    public void run()
    {
      ListActivity.this.mList.focusableViewAvailable(ListActivity.this.mList);
    }
  };
  
  private void ensureList()
  {
    if (this.mList != null) {
      return;
    }
    setContentView(17367161);
  }
  
  public ListAdapter getListAdapter()
  {
    return this.mAdapter;
  }
  
  public ListView getListView()
  {
    ensureList();
    return this.mList;
  }
  
  public long getSelectedItemId()
  {
    return this.mList.getSelectedItemId();
  }
  
  public int getSelectedItemPosition()
  {
    return this.mList.getSelectedItemPosition();
  }
  
  public void onContentChanged()
  {
    super.onContentChanged();
    View localView = findViewById(16908292);
    this.mList = ((ListView)findViewById(16908298));
    if (this.mList == null) {
      throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
    }
    if (localView != null) {
      this.mList.setEmptyView(localView);
    }
    this.mList.setOnItemClickListener(this.mOnClickListener);
    if (this.mFinishedStart) {
      setListAdapter(this.mAdapter);
    }
    this.mHandler.post(this.mRequestFocus);
    this.mFinishedStart = true;
  }
  
  protected void onDestroy()
  {
    this.mHandler.removeCallbacks(this.mRequestFocus);
    super.onDestroy();
  }
  
  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong) {}
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    ensureList();
    super.onRestoreInstanceState(paramBundle);
  }
  
  public void setListAdapter(ListAdapter paramListAdapter)
  {
    try
    {
      ensureList();
      this.mAdapter = paramListAdapter;
      this.mList.setAdapter(paramListAdapter);
      return;
    }
    finally
    {
      paramListAdapter = finally;
      throw paramListAdapter;
    }
  }
  
  public void setSelection(int paramInt)
  {
    this.mList.setSelection(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ListActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */