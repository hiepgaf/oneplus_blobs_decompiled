package com.oneplus.camera.io;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThreadComponent;
import com.oneplus.camera.CameraThreadComponentBuilder;

public class FileManagerBuilder
  extends CameraThreadComponentBuilder
{
  public FileManagerBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, FileManagerImpl.class);
  }
  
  protected CameraThreadComponent create(CameraThread paramCameraThread)
  {
    return new FileManagerImpl(paramCameraThread);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/FileManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */