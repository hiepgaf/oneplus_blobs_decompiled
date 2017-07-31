package com.android.server;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.ISerialManager.Stub;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.util.ArrayList;

public class SerialService
  extends ISerialManager.Stub
{
  private final Context mContext;
  private final String[] mSerialPorts;
  
  public SerialService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mSerialPorts = paramContext.getResources().getStringArray(17236000);
  }
  
  private native ParcelFileDescriptor native_open(String paramString);
  
  public String[] getSerialPorts()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.SERIAL_PORT", null);
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < this.mSerialPorts.length)
    {
      localObject = this.mSerialPorts[i];
      if (new File((String)localObject).exists()) {
        localArrayList.add(localObject);
      }
      i += 1;
    }
    Object localObject = new String[localArrayList.size()];
    localArrayList.toArray((Object[])localObject);
    return (String[])localObject;
  }
  
  public ParcelFileDescriptor openSerialPort(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.SERIAL_PORT", null);
    int i = 0;
    while (i < this.mSerialPorts.length)
    {
      if (this.mSerialPorts[i].equals(paramString)) {
        return native_open(paramString);
      }
      i += 1;
    }
    throw new IllegalArgumentException("Invalid serial port " + paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/SerialService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */