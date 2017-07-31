package android.os;

import android.util.AndroidException;

public class RemoteException
  extends AndroidException
{
  public RemoteException() {}
  
  public RemoteException(String paramString)
  {
    super(paramString);
  }
  
  public RuntimeException rethrowAsRuntimeException()
  {
    throw new RuntimeException(this);
  }
  
  public RuntimeException rethrowFromSystemServer()
  {
    if ((this instanceof DeadObjectException)) {
      throw new RuntimeException(new DeadSystemException());
    }
    throw new RuntimeException(this);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/RemoteException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */