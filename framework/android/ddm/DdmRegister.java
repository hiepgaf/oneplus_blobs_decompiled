package android.ddm;

import org.apache.harmony.dalvik.ddmc.DdmServer;

public class DdmRegister
{
  public static void registerHandlers()
  {
    DdmHandleHello.register();
    DdmHandleThread.register();
    DdmHandleHeap.register();
    DdmHandleNativeHeap.register();
    DdmHandleProfiling.register();
    DdmHandleExit.register();
    DdmHandleViewDebug.register();
    DdmServer.registrationComplete();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/ddm/DdmRegister.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */