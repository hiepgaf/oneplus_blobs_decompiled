package android.gesture;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public final class GestureLibraries
{
  public static GestureLibrary fromFile(File paramFile)
  {
    return new FileGestureLibrary(paramFile);
  }
  
  public static GestureLibrary fromFile(String paramString)
  {
    return fromFile(new File(paramString));
  }
  
  public static GestureLibrary fromPrivateFile(Context paramContext, String paramString)
  {
    return fromFile(paramContext.getFileStreamPath(paramString));
  }
  
  public static GestureLibrary fromRawResource(Context paramContext, int paramInt)
  {
    return new ResourceGestureLibrary(paramContext, paramInt);
  }
  
  private static class FileGestureLibrary
    extends GestureLibrary
  {
    private final File mPath;
    
    public FileGestureLibrary(File paramFile)
    {
      this.mPath = paramFile;
    }
    
    public boolean isReadOnly()
    {
      return !this.mPath.canWrite();
    }
    
    public boolean load()
    {
      boolean bool2 = false;
      File localFile = this.mPath;
      boolean bool1 = bool2;
      if (localFile.exists())
      {
        bool1 = bool2;
        if (!localFile.canRead()) {}
      }
      try
      {
        this.mStore.load(new FileInputStream(localFile), true);
        bool1 = true;
        return bool1;
      }
      catch (IOException localIOException)
      {
        Log.d("Gestures", "Could not load the gesture library from " + this.mPath, localIOException);
        return false;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        Log.d("Gestures", "Could not load the gesture library from " + this.mPath, localFileNotFoundException);
      }
      return false;
    }
    
    public boolean save()
    {
      if (!this.mStore.hasChanged()) {
        return true;
      }
      File localFile1 = this.mPath;
      File localFile2 = localFile1.getParentFile();
      if ((!localFile2.exists()) && (!localFile2.mkdirs())) {
        return false;
      }
      try
      {
        localFile1.createNewFile();
        this.mStore.save(new FileOutputStream(localFile1), true);
        return true;
      }
      catch (IOException localIOException)
      {
        Log.d("Gestures", "Could not save the gesture library in " + this.mPath, localIOException);
        return false;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        Log.d("Gestures", "Could not save the gesture library in " + this.mPath, localFileNotFoundException);
      }
      return false;
    }
  }
  
  private static class ResourceGestureLibrary
    extends GestureLibrary
  {
    private final WeakReference<Context> mContext;
    private final int mResourceId;
    
    public ResourceGestureLibrary(Context paramContext, int paramInt)
    {
      this.mContext = new WeakReference(paramContext);
      this.mResourceId = paramInt;
    }
    
    public boolean isReadOnly()
    {
      return true;
    }
    
    public boolean load()
    {
      boolean bool = false;
      Context localContext = (Context)this.mContext.get();
      InputStream localInputStream;
      if (localContext != null) {
        localInputStream = localContext.getResources().openRawResource(this.mResourceId);
      }
      try
      {
        this.mStore.load(localInputStream, true);
        bool = true;
        return bool;
      }
      catch (IOException localIOException)
      {
        Log.d("Gestures", "Could not load the gesture library from raw resource " + localContext.getResources().getResourceName(this.mResourceId), localIOException);
      }
      return false;
    }
    
    public boolean save()
    {
      return false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/GestureLibraries.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */