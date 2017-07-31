package android.provider;

import android.content.Context;

public class SearchIndexableResource
  extends SearchIndexableData
{
  public int xmlResId;
  
  public SearchIndexableResource(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    this.rank = paramInt1;
    this.xmlResId = paramInt2;
    this.className = paramString;
    this.iconResId = paramInt3;
  }
  
  public SearchIndexableResource(Context paramContext)
  {
    super(paramContext);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SearchIndexableResource[");
    localStringBuilder.append(super.toString());
    localStringBuilder.append(", ");
    localStringBuilder.append("xmlResId: ");
    localStringBuilder.append(this.xmlResId);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/SearchIndexableResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */