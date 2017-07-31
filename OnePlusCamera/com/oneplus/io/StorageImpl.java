package com.oneplus.io;

import android.os.Environment;
import java.io.File;

public class StorageImpl
  implements Storage
{
  private String m_DirectoryPath;
  private Storage.Type m_type;
  
  public StorageImpl(Storage.Type paramType, String paramString)
  {
    this.m_type = paramType;
    this.m_DirectoryPath = paramString;
  }
  
  public String getDirectoryPath()
  {
    return this.m_DirectoryPath;
  }
  
  public Storage.Type getType()
  {
    return this.m_type;
  }
  
  public boolean isReady()
  {
    return Environment.getExternalStorageState(new File(this.m_DirectoryPath)).equals("mounted");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/StorageImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */