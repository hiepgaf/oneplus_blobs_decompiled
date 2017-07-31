package android.drm;

import java.util.ArrayList;
import java.util.Iterator;

public class DrmSupportInfo
{
  private String mDescription = "";
  private final ArrayList<String> mFileSuffixList = new ArrayList();
  private final ArrayList<String> mMimeTypeList = new ArrayList();
  
  public void addFileSuffix(String paramString)
  {
    if (paramString == "") {
      throw new IllegalArgumentException("fileSuffix is an empty string");
    }
    this.mFileSuffixList.add(paramString);
  }
  
  public void addMimeType(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("mimeType is null");
    }
    if (paramString == "") {
      throw new IllegalArgumentException("mimeType is an empty string");
    }
    this.mMimeTypeList.add(paramString);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof DrmSupportInfo))
    {
      paramObject = (DrmSupportInfo)paramObject;
      boolean bool1 = bool2;
      if (this.mFileSuffixList.equals(((DrmSupportInfo)paramObject).mFileSuffixList))
      {
        bool1 = bool2;
        if (this.mMimeTypeList.equals(((DrmSupportInfo)paramObject).mMimeTypeList)) {
          bool1 = this.mDescription.equals(((DrmSupportInfo)paramObject).mDescription);
        }
      }
      return bool1;
    }
    return false;
  }
  
  public String getDescriprition()
  {
    return this.mDescription;
  }
  
  public String getDescription()
  {
    return this.mDescription;
  }
  
  public Iterator<String> getFileSuffixIterator()
  {
    return this.mFileSuffixList.iterator();
  }
  
  public Iterator<String> getMimeTypeIterator()
  {
    return this.mMimeTypeList.iterator();
  }
  
  public int hashCode()
  {
    return this.mFileSuffixList.hashCode() + this.mMimeTypeList.hashCode() + this.mDescription.hashCode();
  }
  
  boolean isSupportedFileSuffix(String paramString)
  {
    return this.mFileSuffixList.contains(paramString);
  }
  
  boolean isSupportedMimeType(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {}
    for (;;)
    {
      return false;
      int i = 0;
      while (i < this.mMimeTypeList.size())
      {
        if (((String)this.mMimeTypeList.get(i)).startsWith(paramString)) {
          return true;
        }
        i += 1;
      }
    }
  }
  
  public void setDescription(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("description is null");
    }
    if (paramString == "") {
      throw new IllegalArgumentException("description is an empty string");
    }
    this.mDescription = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmSupportInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */