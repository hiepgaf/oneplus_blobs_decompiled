package android.content;

import java.util.Iterator;

public abstract interface EntityIterator
  extends Iterator<Entity>
{
  public abstract void close();
  
  public abstract void reset();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/EntityIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */