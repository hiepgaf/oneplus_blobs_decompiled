package android.content;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Iterator;

public final class Entity
{
  private final ArrayList<NamedContentValues> mSubValues;
  private final ContentValues mValues;
  
  public Entity(ContentValues paramContentValues)
  {
    this.mValues = paramContentValues;
    this.mSubValues = new ArrayList();
  }
  
  public void addSubValue(Uri paramUri, ContentValues paramContentValues)
  {
    this.mSubValues.add(new NamedContentValues(paramUri, paramContentValues));
  }
  
  public ContentValues getEntityValues()
  {
    return this.mValues;
  }
  
  public ArrayList<NamedContentValues> getSubValues()
  {
    return this.mSubValues;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Entity: ").append(getEntityValues());
    Iterator localIterator = getSubValues().iterator();
    while (localIterator.hasNext())
    {
      NamedContentValues localNamedContentValues = (NamedContentValues)localIterator.next();
      localStringBuilder.append("\n  ").append(localNamedContentValues.uri);
      localStringBuilder.append("\n  -> ").append(localNamedContentValues.values);
    }
    return localStringBuilder.toString();
  }
  
  public static class NamedContentValues
  {
    public final Uri uri;
    public final ContentValues values;
    
    public NamedContentValues(Uri paramUri, ContentValues paramContentValues)
    {
      this.uri = paramUri;
      this.values = paramContentValues;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/Entity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */