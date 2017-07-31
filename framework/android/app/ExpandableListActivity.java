package android.app;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class ExpandableListActivity
  extends Activity
  implements View.OnCreateContextMenuListener, ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener
{
  ExpandableListAdapter mAdapter;
  boolean mFinishedStart = false;
  ExpandableListView mList;
  
  private void ensureList()
  {
    if (this.mList != null) {
      return;
    }
    setContentView(17367041);
  }
  
  public ExpandableListAdapter getExpandableListAdapter()
  {
    return this.mAdapter;
  }
  
  public ExpandableListView getExpandableListView()
  {
    ensureList();
    return this.mList;
  }
  
  public long getSelectedId()
  {
    return this.mList.getSelectedId();
  }
  
  public long getSelectedPosition()
  {
    return this.mList.getSelectedPosition();
  }
  
  public boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int paramInt1, int paramInt2, long paramLong)
  {
    return false;
  }
  
  public void onContentChanged()
  {
    super.onContentChanged();
    View localView = findViewById(16908292);
    this.mList = ((ExpandableListView)findViewById(16908298));
    if (this.mList == null) {
      throw new RuntimeException("Your content must have a ExpandableListView whose id attribute is 'android.R.id.list'");
    }
    if (localView != null) {
      this.mList.setEmptyView(localView);
    }
    this.mList.setOnChildClickListener(this);
    this.mList.setOnGroupExpandListener(this);
    this.mList.setOnGroupCollapseListener(this);
    if (this.mFinishedStart) {
      setListAdapter(this.mAdapter);
    }
    this.mFinishedStart = true;
  }
  
  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo) {}
  
  public void onGroupCollapse(int paramInt) {}
  
  public void onGroupExpand(int paramInt) {}
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    ensureList();
    super.onRestoreInstanceState(paramBundle);
  }
  
  public void setListAdapter(ExpandableListAdapter paramExpandableListAdapter)
  {
    try
    {
      ensureList();
      this.mAdapter = paramExpandableListAdapter;
      this.mList.setAdapter(paramExpandableListAdapter);
      return;
    }
    finally
    {
      paramExpandableListAdapter = finally;
      throw paramExpandableListAdapter;
    }
  }
  
  public boolean setSelectedChild(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return this.mList.setSelectedChild(paramInt1, paramInt2, paramBoolean);
  }
  
  public void setSelectedGroup(int paramInt)
  {
    this.mList.setSelectedGroup(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ExpandableListActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */