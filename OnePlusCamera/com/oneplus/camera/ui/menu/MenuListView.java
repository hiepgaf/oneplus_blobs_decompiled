package com.oneplus.camera.ui.menu;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.widget.ViewUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuListView
  extends ListView
{
  private static final int ITEM_VIEW_TYPE_COUNT = 2;
  private static final int ITEM_VIEW_TYPE_DIVIDER = 0;
  private static final int ITEM_VIEW_TYPE_ITEM = 1;
  private final Adapter m_Adapter = new Adapter(null);
  private final PropertyChangedCallback<Boolean> m_IsCheckedChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      MenuListView.-wrap2(MenuListView.this, (MenuItem)paramAnonymousPropertySource);
    }
  };
  private final PropertyChangedCallback<Boolean> m_IsEnabledChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      MenuListView.-get0(MenuListView.this).notifyDataSetInvalidated();
    }
  };
  private int m_MenuItemDividerResId = 2130903050;
  private int m_MenuItemViewResId = 2130903051;
  private List<MenuItem> m_MenuItems = new ArrayList();
  private final AdapterView.OnItemClickListener m_OnInternalItemClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      MenuListView.-wrap1(MenuListView.this, paramAnonymousAdapterView, paramAnonymousView, paramAnonymousInt, paramAnonymousLong);
    }
  };
  private AdapterView.OnItemClickListener m_OnItemClickListener;
  private final PropertyChangedCallback<CharSequence> m_SummaryChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CharSequence> paramAnonymousPropertyKey, PropertyChangeEventArgs<CharSequence> paramAnonymousPropertyChangeEventArgs)
    {
      MenuListView.-get0(MenuListView.this).notifyDataSetInvalidated();
    }
  };
  private final PropertyChangedCallback<CharSequence> m_TitleChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CharSequence> paramAnonymousPropertyKey, PropertyChangeEventArgs<CharSequence> paramAnonymousPropertyChangeEventArgs)
    {
      MenuListView.-get0(MenuListView.this).notifyDataSetInvalidated();
    }
  };
  
  public MenuListView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    super.setAdapter(this.m_Adapter);
    super.setOnItemClickListener(this.m_OnInternalItemClickListener);
    super.setDivider(null);
  }
  
  private void attachToMenuItem(MenuItem paramMenuItem)
  {
    if (paramMenuItem != null)
    {
      paramMenuItem.addCallback(MenuItem.PROP_IS_CHECKED, this.m_IsCheckedChangedCallback);
      paramMenuItem.addCallback(MenuItem.PROP_IS_ENABLED, this.m_IsEnabledChangedCallback);
      paramMenuItem.addCallback(MenuItem.PROP_SUBTITLE, this.m_TitleChangedCallback);
      paramMenuItem.addCallback(MenuItem.PROP_SUMMARY, this.m_SummaryChangedCallback);
      paramMenuItem.addCallback(MenuItem.PROP_TITLE, this.m_TitleChangedCallback);
    }
  }
  
  private void detachFromMenuItem(MenuItem paramMenuItem)
  {
    if (paramMenuItem != null)
    {
      paramMenuItem.removeCallback(MenuItem.PROP_IS_CHECKED, this.m_IsCheckedChangedCallback);
      paramMenuItem.removeCallback(MenuItem.PROP_IS_ENABLED, this.m_IsEnabledChangedCallback);
      paramMenuItem.removeCallback(MenuItem.PROP_SUBTITLE, this.m_TitleChangedCallback);
      paramMenuItem.removeCallback(MenuItem.PROP_SUMMARY, this.m_SummaryChangedCallback);
      paramMenuItem.removeCallback(MenuItem.PROP_TITLE, this.m_TitleChangedCallback);
    }
  }
  
  private View getMenuItemView(int paramInt, final View paramView, ViewGroup paramViewGroup)
  {
    MenuItem localMenuItem = (MenuItem)this.m_MenuItems.get(paramInt);
    boolean bool = localMenuItem instanceof DividerMenuItem;
    if (paramView == null)
    {
      paramView = getContext();
      if (bool)
      {
        paramInt = this.m_MenuItemDividerResId;
        paramView = View.inflate(paramView, paramInt, null);
        paramViewGroup = new ViewInfo(null);
        paramViewGroup.menuContainer = ((LinearLayout)paramView.findViewById(2131361840));
        paramViewGroup.titleTextView = ((TextView)paramView.findViewById(2131361839));
        paramViewGroup.subTitleTextView = ((TextView)paramView.findViewById(2131361841));
        paramViewGroup.summaryTextView = ((TextView)paramView.findViewById(2131361842));
        paramViewGroup.radioButton = ((RadioButton)paramView.findViewById(2131361844));
        paramViewGroup.switchView = ((Switch)paramView.findViewById(2131361843));
        paramViewGroup.bottomLine = paramView.findViewById(2131361845);
        if (paramViewGroup.switchView != null) {
          paramViewGroup.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
          {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
            {
              paramAnonymousCompoundButton = (MenuListView.ViewInfo)paramView.getTag();
              if (!paramAnonymousCompoundButton.isUpdatingViews) {
                MenuListView.-wrap3(MenuListView.this, paramAnonymousCompoundButton, paramAnonymousBoolean);
              }
            }
          });
        }
        if (paramViewGroup.radioButton != null) {
          paramViewGroup.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
          {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
            {
              paramAnonymousCompoundButton = (MenuListView.ViewInfo)paramView.getTag();
              if (!paramAnonymousCompoundButton.isUpdatingViews) {
                MenuListView.-wrap3(MenuListView.this, paramAnonymousCompoundButton, paramAnonymousBoolean);
              }
            }
          });
        }
        paramView.setTag(paramViewGroup);
      }
    }
    for (;;)
    {
      detachFromMenuItem(paramViewGroup.menuItem);
      paramViewGroup.menuItem = localMenuItem;
      attachToMenuItem(localMenuItem);
      paramViewGroup.isUpdatingViews = true;
      setupMenuItemContainer(paramViewGroup);
      setupMenuItemSummary(paramViewGroup);
      setupMenuItemSwitch(paramViewGroup);
      setupMenuItemTitle(paramViewGroup);
      setupMenuItemSubTile(paramViewGroup);
      paramViewGroup.isUpdatingViews = false;
      return paramView;
      paramInt = this.m_MenuItemViewResId;
      break;
      paramViewGroup = (ViewInfo)paramView.getTag();
    }
  }
  
  private void onItemClicked(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    boolean bool = false;
    ViewInfo localViewInfo = (ViewInfo)paramView.getTag();
    Switch localSwitch = localViewInfo.switchView;
    if ((localSwitch != null) && (localViewInfo.menuItem != null) && (localSwitch.getVisibility() == 0))
    {
      if (localSwitch.isChecked()) {}
      for (;;)
      {
        localSwitch.setChecked(bool);
        return;
        bool = true;
      }
    }
    if (this.m_OnItemClickListener != null) {
      this.m_OnItemClickListener.onItemClick(paramAdapterView, paramView, paramInt, paramLong);
    }
  }
  
  private void onMenuItemCheckedChanged(MenuItem paramMenuItem)
  {
    if (this.m_MenuItems.indexOf(paramMenuItem) < 0) {
      return;
    }
    this.m_Adapter.notifyDataSetChanged();
  }
  
  private void onMenuItemSwitchChanged(ViewInfo paramViewInfo, boolean paramBoolean)
  {
    if ((paramViewInfo.menuItem != null) && ((Boolean)paramViewInfo.menuItem.get(MenuItem.PROP_IS_CHECKED) != null)) {
      paramViewInfo.menuItem.set(MenuItem.PROP_IS_CHECKED, Boolean.valueOf(paramBoolean));
    }
  }
  
  private void setupMenuItemContainer(ViewInfo paramViewInfo)
  {
    Resources localResources;
    if ((paramViewInfo.menuContainer != null) && (paramViewInfo.menuItem != null))
    {
      localResources = getContext().getResources();
      if (!(paramViewInfo.menuItem instanceof DividerMenuItem))
      {
        if (paramViewInfo.menuItem.get(MenuItem.PROP_SUBTITLE) == null) {
          break label103;
        }
        paramViewInfo.menuContainer.setMinimumHeight(localResources.getDimensionPixelSize(2131296269));
        if (paramViewInfo.menuItem.get(MenuItem.PROP_DIVIDER_STYLE) != MenuItem.DividerStyle.INDENTED) {
          break label120;
        }
        int i = localResources.getDimensionPixelSize(2131296409);
        ViewUtils.setMargins(paramViewInfo.bottomLine, i, 0, 0, 0);
      }
    }
    for (;;)
    {
      paramViewInfo.bottomLine.setVisibility(0);
      return;
      label103:
      paramViewInfo.menuContainer.setMinimumHeight(localResources.getDimensionPixelSize(2131296268));
      break;
      label120:
      if (paramViewInfo.menuItem.get(MenuItem.PROP_DIVIDER_STYLE) == MenuItem.DividerStyle.NORMAL) {
        ViewUtils.setMargins(paramViewInfo.bottomLine, 0, 0, 0, 0);
      }
    }
  }
  
  private void setupMenuItemSubTile(ViewInfo paramViewInfo)
  {
    CharSequence localCharSequence;
    if ((paramViewInfo.subTitleTextView != null) && (paramViewInfo.menuItem != null))
    {
      localCharSequence = (CharSequence)paramViewInfo.menuItem.get(MenuItem.PROP_SUBTITLE);
      if (localCharSequence != null) {
        break label60;
      }
      if (paramViewInfo.titleTextView != null) {
        ViewUtils.setMargins(paramViewInfo.titleTextView, 0, 0, 0, 0);
      }
      paramViewInfo.subTitleTextView.setVisibility(8);
    }
    label60:
    do
    {
      return;
      paramViewInfo.subTitleTextView.setVisibility(0);
      paramViewInfo.subTitleTextView.setText(localCharSequence);
    } while (paramViewInfo.titleTextView == null);
    int i = getContext().getResources().getDimensionPixelSize(2131296274);
    ViewUtils.setMargins(paramViewInfo.titleTextView, 0, i, 0, 0);
    ViewUtils.setMargins(paramViewInfo.subTitleTextView, 0, 0, 0, i);
  }
  
  private void setupMenuItemSummary(ViewInfo paramViewInfo)
  {
    CharSequence localCharSequence;
    if ((paramViewInfo.summaryTextView != null) && (paramViewInfo.menuItem != null))
    {
      localCharSequence = (CharSequence)paramViewInfo.menuItem.get(MenuItem.PROP_SUMMARY);
      if (localCharSequence == null) {
        paramViewInfo.summaryTextView.setVisibility(8);
      }
    }
    else
    {
      return;
    }
    paramViewInfo.summaryTextView.setVisibility(0);
    paramViewInfo.summaryTextView.setText(localCharSequence);
  }
  
  private void setupMenuItemSwitch(ViewInfo paramViewInfo)
  {
    Boolean localBoolean;
    if ((paramViewInfo.switchView != null) && (paramViewInfo.menuItem != null))
    {
      localBoolean = (Boolean)paramViewInfo.menuItem.get(MenuItem.PROP_IS_CHECKED);
      if (localBoolean != null) {
        break label65;
      }
      if (paramViewInfo.switchView != null) {
        paramViewInfo.switchView.setVisibility(8);
      }
      if (paramViewInfo.radioButton != null) {
        paramViewInfo.radioButton.setVisibility(8);
      }
    }
    label65:
    do
    {
      return;
      if (((paramViewInfo.menuItem instanceof RadioMenuItem)) && (paramViewInfo.radioButton != null))
      {
        if (paramViewInfo.switchView != null) {
          paramViewInfo.switchView.setVisibility(8);
        }
        paramViewInfo.radioButton.setVisibility(0);
        paramViewInfo.radioButton.setChecked(localBoolean.booleanValue());
        return;
      }
      if (paramViewInfo.switchView != null)
      {
        paramViewInfo.switchView.setVisibility(0);
        paramViewInfo.switchView.setChecked(localBoolean.booleanValue());
      }
    } while (paramViewInfo.radioButton == null);
    paramViewInfo.radioButton.setVisibility(8);
  }
  
  private void setupMenuItemTitle(ViewInfo paramViewInfo)
  {
    if ((paramViewInfo.titleTextView != null) && (paramViewInfo.menuItem != null))
    {
      paramViewInfo.titleTextView.setText((CharSequence)paramViewInfo.menuItem.get(MenuItem.PROP_TITLE));
      if (!(paramViewInfo.menuItem instanceof DividerMenuItem))
      {
        Object localObject = new TypedValue();
        getContext().getTheme().resolveAttribute(2130771972, (TypedValue)localObject, true);
        localObject = getContext().obtainStyledAttributes(((TypedValue)localObject).resourceId, new int[] { 16842904 });
        int i = ((TypedArray)localObject).getColor(0, -1);
        paramViewInfo.titleTextView.setTextColor(i);
        paramViewInfo.titleTextView.setTextSize(0, getResources().getDimensionPixelSize(2131296276));
        ((TypedArray)localObject).recycle();
      }
    }
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    throw new RuntimeException("Cannot change adapter.");
  }
  
  public void setMenuItems(List<MenuItem> paramList)
  {
    Iterator localIterator = this.m_MenuItems.iterator();
    while (localIterator.hasNext()) {
      detachFromMenuItem((MenuItem)localIterator.next());
    }
    this.m_MenuItems.clear();
    if (paramList != null) {
      this.m_MenuItems.addAll(paramList);
    }
    this.m_Adapter.notifyDataSetChanged();
  }
  
  public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    this.m_OnItemClickListener = paramOnItemClickListener;
  }
  
  private final class Adapter
    extends BaseAdapter
  {
    private Adapter() {}
    
    public int getCount()
    {
      return MenuListView.-get1(MenuListView.this).size();
    }
    
    public Object getItem(int paramInt)
    {
      return MenuListView.-get1(MenuListView.this).get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return ((MenuItem)MenuListView.-get1(MenuListView.this).get(paramInt)).hashCode();
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((MenuListView.-get1(MenuListView.this).get(paramInt) instanceof DividerMenuItem)) {
        return 0;
      }
      return 1;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      return MenuListView.-wrap0(MenuListView.this, paramInt, paramView, paramViewGroup);
    }
    
    public int getViewTypeCount()
    {
      return 2;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return !(MenuListView.-get1(MenuListView.this).get(paramInt) instanceof DividerMenuItem);
    }
  }
  
  private static final class ViewInfo
  {
    public View bottomLine;
    public boolean isUpdatingViews;
    public LinearLayout menuContainer;
    public MenuItem menuItem;
    public RadioButton radioButton;
    public TextView subTitleTextView;
    public TextView summaryTextView;
    public Switch switchView;
    public TextView titleTextView;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/menu/MenuListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */