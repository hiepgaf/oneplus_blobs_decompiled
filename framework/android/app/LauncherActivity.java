package android.app;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ResolveInfo.DisplayNameComparator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class LauncherActivity
  extends ListActivity
{
  IconResizer mIconResizer;
  Intent mIntent;
  PackageManager mPackageManager;
  
  private void updateAlertTitle()
  {
    TextView localTextView = (TextView)findViewById(16909093);
    if (localTextView != null) {
      localTextView.setText(getTitle());
    }
  }
  
  private void updateButtonText()
  {
    Button localButton = (Button)findViewById(16908313);
    if (localButton != null) {
      localButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          LauncherActivity.this.finish();
        }
      });
    }
  }
  
  protected Intent getTargetIntent()
  {
    return new Intent();
  }
  
  protected Intent intentForPosition(int paramInt)
  {
    return ((ActivityAdapter)this.mAdapter).intentForPosition(paramInt);
  }
  
  protected ListItem itemForPosition(int paramInt)
  {
    return ((ActivityAdapter)this.mAdapter).itemForPosition(paramInt);
  }
  
  public List<ListItem> makeListItems()
  {
    List localList = onQueryPackageManager(this.mIntent);
    onSortResultList(localList);
    ArrayList localArrayList = new ArrayList(localList.size());
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localList.get(i);
      localArrayList.add(new ListItem(this.mPackageManager, localResolveInfo, null));
      i += 1;
    }
    return localArrayList;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPackageManager = getPackageManager();
    if (!this.mPackageManager.hasSystemFeature("android.hardware.type.watch"))
    {
      requestWindowFeature(5);
      setProgressBarIndeterminateVisibility(true);
    }
    onSetContentView();
    this.mIconResizer = new IconResizer();
    this.mIntent = new Intent(getTargetIntent());
    this.mIntent.setComponent(null);
    this.mAdapter = new ActivityAdapter(this.mIconResizer);
    setListAdapter(this.mAdapter);
    getListView().setTextFilterEnabled(true);
    updateAlertTitle();
    updateButtonText();
    if (!this.mPackageManager.hasSystemFeature("android.hardware.type.watch")) {
      setProgressBarIndeterminateVisibility(false);
    }
  }
  
  protected boolean onEvaluateShowIcons()
  {
    return true;
  }
  
  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    startActivity(intentForPosition(paramInt));
  }
  
  protected List<ResolveInfo> onQueryPackageManager(Intent paramIntent)
  {
    return this.mPackageManager.queryIntentActivities(paramIntent, 0);
  }
  
  protected void onSetContentView()
  {
    setContentView(17367075);
  }
  
  protected void onSortResultList(List<ResolveInfo> paramList)
  {
    Collections.sort(paramList, new ResolveInfo.DisplayNameComparator(this.mPackageManager));
  }
  
  public void setTitle(int paramInt)
  {
    super.setTitle(paramInt);
    updateAlertTitle();
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    super.setTitle(paramCharSequence);
    updateAlertTitle();
  }
  
  private class ActivityAdapter
    extends BaseAdapter
    implements Filterable
  {
    private final Object lock = new Object();
    protected List<LauncherActivity.ListItem> mActivitiesList;
    private Filter mFilter;
    protected final LauncherActivity.IconResizer mIconResizer;
    protected final LayoutInflater mInflater;
    private ArrayList<LauncherActivity.ListItem> mOriginalValues;
    private final boolean mShowIcons;
    
    public ActivityAdapter(LauncherActivity.IconResizer paramIconResizer)
    {
      this.mIconResizer = paramIconResizer;
      this.mInflater = ((LayoutInflater)LauncherActivity.this.getSystemService("layout_inflater"));
      this.mShowIcons = LauncherActivity.this.onEvaluateShowIcons();
      this.mActivitiesList = LauncherActivity.this.makeListItems();
    }
    
    private void bindView(View paramView, LauncherActivity.ListItem paramListItem)
    {
      paramView = (TextView)paramView;
      paramView.setText(paramListItem.label);
      if (this.mShowIcons)
      {
        if (paramListItem.icon == null) {
          paramListItem.icon = this.mIconResizer.createIconThumbnail(paramListItem.resolveInfo.loadIcon(LauncherActivity.this.getPackageManager()));
        }
        paramView.setCompoundDrawablesWithIntrinsicBounds(paramListItem.icon, null, null, null);
      }
    }
    
    public int getCount()
    {
      if (this.mActivitiesList != null) {
        return this.mActivitiesList.size();
      }
      return 0;
    }
    
    public Filter getFilter()
    {
      if (this.mFilter == null) {
        this.mFilter = new ArrayFilter(null);
      }
      return this.mFilter;
    }
    
    public Object getItem(int paramInt)
    {
      return Integer.valueOf(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null) {
        paramView = this.mInflater.inflate(17367076, paramViewGroup, false);
      }
      for (;;)
      {
        bindView(paramView, (LauncherActivity.ListItem)this.mActivitiesList.get(paramInt));
        return paramView;
      }
    }
    
    public Intent intentForPosition(int paramInt)
    {
      if (this.mActivitiesList == null) {
        return null;
      }
      Intent localIntent = new Intent(LauncherActivity.this.mIntent);
      LauncherActivity.ListItem localListItem = (LauncherActivity.ListItem)this.mActivitiesList.get(paramInt);
      localIntent.setClassName(localListItem.packageName, localListItem.className);
      if (localListItem.extras != null) {
        localIntent.putExtras(localListItem.extras);
      }
      return localIntent;
    }
    
    public LauncherActivity.ListItem itemForPosition(int paramInt)
    {
      if (this.mActivitiesList == null) {
        return null;
      }
      return (LauncherActivity.ListItem)this.mActivitiesList.get(paramInt);
    }
    
    private class ArrayFilter
      extends Filter
    {
      private ArrayFilter() {}
      
      protected Filter.FilterResults performFiltering(CharSequence arg1)
      {
        Filter.FilterResults localFilterResults1 = new Filter.FilterResults();
        if (LauncherActivity.ActivityAdapter.-get1(LauncherActivity.ActivityAdapter.this) == null) {}
        synchronized (LauncherActivity.ActivityAdapter.-get0(LauncherActivity.ActivityAdapter.this))
        {
          LauncherActivity.ActivityAdapter.-set0(LauncherActivity.ActivityAdapter.this, new ArrayList(LauncherActivity.ActivityAdapter.this.mActivitiesList));
          if (??? != null) {
            if (???.length() != 0) {
              break label130;
            }
          }
        }
        synchronized (LauncherActivity.ActivityAdapter.-get0(LauncherActivity.ActivityAdapter.this))
        {
          ??? = new ArrayList(LauncherActivity.ActivityAdapter.-get1(LauncherActivity.ActivityAdapter.this));
          localFilterResults1.values = ???;
          localFilterResults1.count = ((ArrayList)???).size();
          return localFilterResults1;
          ??? = finally;
          throw ???;
        }
        label130:
        ??? = ???.toString().toLowerCase();
        ??? = LauncherActivity.ActivityAdapter.-get1(LauncherActivity.ActivityAdapter.this);
        int k = ((ArrayList)???).size();
        ArrayList localArrayList = new ArrayList(k);
        int i = 0;
        if (i < k)
        {
          LauncherActivity.ListItem localListItem = (LauncherActivity.ListItem)((ArrayList)???).get(i);
          String[] arrayOfString = localListItem.label.toString().toLowerCase().split(" ");
          int m = arrayOfString.length;
          int j = 0;
          for (;;)
          {
            if (j < m)
            {
              if (arrayOfString[j].startsWith(???)) {
                localArrayList.add(localListItem);
              }
            }
            else
            {
              i += 1;
              break;
            }
            j += 1;
          }
        }
        localFilterResults2.values = localArrayList;
        localFilterResults2.count = localArrayList.size();
        return localFilterResults2;
      }
      
      protected void publishResults(CharSequence paramCharSequence, Filter.FilterResults paramFilterResults)
      {
        LauncherActivity.ActivityAdapter.this.mActivitiesList = ((List)paramFilterResults.values);
        if (paramFilterResults.count > 0)
        {
          LauncherActivity.ActivityAdapter.this.notifyDataSetChanged();
          return;
        }
        LauncherActivity.ActivityAdapter.this.notifyDataSetInvalidated();
      }
    }
  }
  
  public class IconResizer
  {
    private Canvas mCanvas = new Canvas();
    private int mIconHeight = -1;
    private int mIconWidth = -1;
    private final Rect mOldBounds = new Rect();
    
    public IconResizer()
    {
      this.mCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
      int i = (int)LauncherActivity.this.getResources().getDimension(17104896);
      this.mIconHeight = i;
      this.mIconWidth = i;
    }
    
    public Drawable createIconThumbnail(Drawable paramDrawable)
    {
      int j = this.mIconWidth;
      int k = this.mIconHeight;
      int m = paramDrawable.getIntrinsicWidth();
      int n = paramDrawable.getIntrinsicHeight();
      if ((paramDrawable instanceof PaintDrawable))
      {
        paramDrawable.setIntrinsicWidth(j);
        paramDrawable.setIntrinsicHeight(k);
      }
      Object localObject1 = paramDrawable;
      float f;
      if (j > 0)
      {
        localObject1 = paramDrawable;
        if (k > 0)
        {
          if ((j >= m) && (k >= n)) {
            break label258;
          }
          f = m / n;
          if (m <= n) {
            break label226;
          }
          i = (int)(j / f);
          if (paramDrawable.getOpacity() == -1) {
            break label250;
          }
          localObject1 = Bitmap.Config.ARGB_8888;
          label108:
          localObject1 = Bitmap.createBitmap(this.mIconWidth, this.mIconHeight, (Bitmap.Config)localObject1);
          localObject2 = this.mCanvas;
          ((Canvas)localObject2).setBitmap((Bitmap)localObject1);
          this.mOldBounds.set(paramDrawable.getBounds());
          k = (this.mIconWidth - j) / 2;
          m = (this.mIconHeight - i) / 2;
          paramDrawable.setBounds(k, m, k + j, m + i);
          paramDrawable.draw((Canvas)localObject2);
          paramDrawable.setBounds(this.mOldBounds);
          localObject1 = new BitmapDrawable(LauncherActivity.this.getResources(), (Bitmap)localObject1);
          ((Canvas)localObject2).setBitmap(null);
        }
      }
      label226:
      label250:
      label258:
      do
      {
        do
        {
          return (Drawable)localObject1;
          i = k;
          if (n <= m) {
            break;
          }
          j = (int)(k * f);
          i = k;
          break;
          localObject1 = Bitmap.Config.RGB_565;
          break label108;
          localObject1 = paramDrawable;
        } while (m >= j);
        localObject1 = paramDrawable;
      } while (n >= k);
      localObject1 = Bitmap.Config.ARGB_8888;
      Object localObject2 = Bitmap.createBitmap(this.mIconWidth, this.mIconHeight, (Bitmap.Config)localObject1);
      localObject1 = this.mCanvas;
      ((Canvas)localObject1).setBitmap((Bitmap)localObject2);
      this.mOldBounds.set(paramDrawable.getBounds());
      int i = (j - m) / 2;
      j = (k - n) / 2;
      paramDrawable.setBounds(i, j, i + m, j + n);
      paramDrawable.draw((Canvas)localObject1);
      paramDrawable.setBounds(this.mOldBounds);
      paramDrawable = new BitmapDrawable(LauncherActivity.this.getResources(), (Bitmap)localObject2);
      ((Canvas)localObject1).setBitmap(null);
      return paramDrawable;
    }
  }
  
  public static class ListItem
  {
    public String className;
    public Bundle extras;
    public Drawable icon;
    public CharSequence label;
    public String packageName;
    public ResolveInfo resolveInfo;
    
    public ListItem() {}
    
    ListItem(PackageManager paramPackageManager, ResolveInfo paramResolveInfo, LauncherActivity.IconResizer paramIconResizer)
    {
      this.resolveInfo = paramResolveInfo;
      this.label = paramResolveInfo.loadLabel(paramPackageManager);
      ActivityInfo localActivityInfo = paramResolveInfo.activityInfo;
      Object localObject = localActivityInfo;
      if (localActivityInfo == null) {
        localObject = paramResolveInfo.serviceInfo;
      }
      if ((this.label == null) && (localObject != null)) {
        this.label = paramResolveInfo.activityInfo.name;
      }
      if (paramIconResizer != null) {
        this.icon = paramIconResizer.createIconThumbnail(paramResolveInfo.loadIcon(paramPackageManager));
      }
      this.packageName = ((ComponentInfo)localObject).applicationInfo.packageName;
      this.className = ((PackageItemInfo)localObject).name;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/LauncherActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */