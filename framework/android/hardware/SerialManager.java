package android.hardware;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import java.io.IOException;

public class SerialManager
{
  private static final String TAG = "SerialManager";
  private final Context mContext;
  private final ISerialManager mService;
  
  public SerialManager(Context paramContext, ISerialManager paramISerialManager)
  {
    this.mContext = paramContext;
    this.mService = paramISerialManager;
  }
  
  public String[] getSerialPorts()
  {
    try
    {
      String[] arrayOfString = this.mService.getSerialPorts();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public SerialPort openSerialPort(String paramString, int paramInt)
    throws IOException
  {
    try
    {
      ParcelFileDescriptor localParcelFileDescriptor = this.mService.openSerialPort(paramString);
      if (localParcelFileDescriptor != null)
      {
        paramString = new SerialPort(paramString);
        paramString.open(localParcelFileDescriptor, paramInt);
        return paramString;
      }
      throw new IOException("Could not open serial port " + paramString);
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/SerialManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */