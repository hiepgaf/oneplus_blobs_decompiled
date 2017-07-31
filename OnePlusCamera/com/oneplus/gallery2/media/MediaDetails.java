package com.oneplus.gallery2.media;

public abstract interface MediaDetails
{
  public abstract <T> T get(Key<T> paramKey, T paramT);
  
  public static class Key<T>
  {
    public final String name;
    
    public Key(String paramString)
    {
      this.name = paramString;
    }
    
    public String toString()
    {
      return this.name;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */