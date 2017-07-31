package android.provider;

import android.content.Context;
import java.util.Locale;

public abstract class SearchIndexableData
{
  public String className;
  public Context context;
  public boolean enabled = true;
  public int iconResId;
  public String intentAction;
  public String intentTargetClass;
  public String intentTargetPackage;
  public String key;
  public Locale locale = Locale.getDefault();
  public String packageName;
  public int rank;
  public int userId = -1;
  
  public SearchIndexableData() {}
  
  public SearchIndexableData(Context paramContext)
  {
    this();
    this.context = paramContext;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SearchIndexableData[context: ");
    localStringBuilder.append(this.context);
    localStringBuilder.append(", ");
    localStringBuilder.append("locale: ");
    localStringBuilder.append(this.locale);
    localStringBuilder.append(", ");
    localStringBuilder.append("enabled: ");
    localStringBuilder.append(this.enabled);
    localStringBuilder.append(", ");
    localStringBuilder.append("rank: ");
    localStringBuilder.append(this.rank);
    localStringBuilder.append(", ");
    localStringBuilder.append("key: ");
    localStringBuilder.append(this.key);
    localStringBuilder.append(", ");
    localStringBuilder.append("userId: ");
    localStringBuilder.append(this.userId);
    localStringBuilder.append(", ");
    localStringBuilder.append("className: ");
    localStringBuilder.append(this.className);
    localStringBuilder.append(", ");
    localStringBuilder.append("packageName: ");
    localStringBuilder.append(this.packageName);
    localStringBuilder.append(", ");
    localStringBuilder.append("iconResId: ");
    localStringBuilder.append(this.iconResId);
    localStringBuilder.append(", ");
    localStringBuilder.append("intentAction: ");
    localStringBuilder.append(this.intentAction);
    localStringBuilder.append(", ");
    localStringBuilder.append("intentTargetPackage: ");
    localStringBuilder.append(this.intentTargetPackage);
    localStringBuilder.append(", ");
    localStringBuilder.append("intentTargetClass: ");
    localStringBuilder.append(this.intentTargetClass);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/SearchIndexableData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */