package android.app;

import android.os.BaseBundle;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

@Deprecated
public class TabActivity
  extends ActivityGroup
{
  private String mDefaultTab = null;
  private int mDefaultTabIndex = -1;
  private TabHost mTabHost;
  
  private void ensureTabHost()
  {
    if (this.mTabHost == null) {
      setContentView(17367276);
    }
  }
  
  public TabHost getTabHost()
  {
    ensureTabHost();
    return this.mTabHost;
  }
  
  public TabWidget getTabWidget()
  {
    return this.mTabHost.getTabWidget();
  }
  
  protected void onChildTitleChanged(Activity paramActivity, CharSequence paramCharSequence)
  {
    if (getLocalActivityManager().getCurrentActivity() == paramActivity)
    {
      paramActivity = this.mTabHost.getCurrentTabView();
      if ((paramActivity != null) && ((paramActivity instanceof TextView))) {
        paramActivity.setText(paramCharSequence);
      }
    }
  }
  
  public void onContentChanged()
  {
    super.onContentChanged();
    this.mTabHost = ((TabHost)findViewById(16908306));
    if (this.mTabHost == null) {
      throw new RuntimeException("Your content must have a TabHost whose id attribute is 'android.R.id.tabhost'");
    }
    this.mTabHost.setup(getLocalActivityManager());
  }
  
  protected void onPostCreate(Bundle paramBundle)
  {
    super.onPostCreate(paramBundle);
    ensureTabHost();
    if (this.mTabHost.getCurrentTab() == -1) {
      this.mTabHost.setCurrentTab(0);
    }
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    ensureTabHost();
    paramBundle = paramBundle.getString("currentTab");
    if (paramBundle != null) {
      this.mTabHost.setCurrentTabByTag(paramBundle);
    }
    if (this.mTabHost.getCurrentTab() < 0)
    {
      if (this.mDefaultTab == null) {
        break label57;
      }
      this.mTabHost.setCurrentTabByTag(this.mDefaultTab);
    }
    label57:
    while (this.mDefaultTabIndex < 0) {
      return;
    }
    this.mTabHost.setCurrentTab(this.mDefaultTabIndex);
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    String str = this.mTabHost.getCurrentTabTag();
    if (str != null) {
      paramBundle.putString("currentTab", str);
    }
  }
  
  public void setDefaultTab(int paramInt)
  {
    this.mDefaultTab = null;
    this.mDefaultTabIndex = paramInt;
  }
  
  public void setDefaultTab(String paramString)
  {
    this.mDefaultTab = paramString;
    this.mDefaultTabIndex = -1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/TabActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */