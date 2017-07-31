package android.support.v4.widget;

import android.view.View;
import android.widget.ListView;

public class ListViewAutoScrollHelper
  extends AutoScrollHelper
{
  private final ListView mTarget;
  
  public ListViewAutoScrollHelper(ListView paramListView)
  {
    super(paramListView);
    this.mTarget = paramListView;
  }
  
  public boolean canTargetScrollHorizontally(int paramInt)
  {
    return false;
  }
  
  public boolean canTargetScrollVertically(int paramInt)
  {
    ListView localListView = this.mTarget;
    int i = localListView.getCount();
    int j;
    int k;
    if (i != 0)
    {
      j = localListView.getChildCount();
      k = localListView.getFirstVisiblePosition();
      if (paramInt <= 0)
      {
        if (paramInt < 0) {
          break label72;
        }
        return false;
      }
    }
    else
    {
      return false;
    }
    if (k + j < i) {}
    label72:
    while ((k > 0) || (localListView.getChildAt(0).getTop() < 0))
    {
      do
      {
        return true;
      } while (localListView.getChildAt(j - 1).getBottom() > localListView.getHeight());
      return false;
    }
    return false;
  }
  
  public void scrollTargetBy(int paramInt1, int paramInt2)
  {
    ListView localListView = this.mTarget;
    paramInt1 = localListView.getFirstVisiblePosition();
    if (paramInt1 != -1)
    {
      View localView = localListView.getChildAt(0);
      if (localView != null) {
        localListView.setSelectionFromTop(paramInt1, localView.getTop() - paramInt2);
      }
    }
    else {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/widget/ListViewAutoScrollHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */