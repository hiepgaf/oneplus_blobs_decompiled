package android.nfc;

import android.content.Context;

public final class NfcManager
{
  private final NfcAdapter mAdapter;
  
  public NfcManager(Context paramContext)
  {
    paramContext = paramContext.getApplicationContext();
    if (paramContext == null) {
      throw new IllegalArgumentException("context not associated with any application (using a mock context?)");
    }
    try
    {
      paramContext = NfcAdapter.getNfcAdapter(paramContext);
      this.mAdapter = paramContext;
      return;
    }
    catch (UnsupportedOperationException paramContext)
    {
      for (;;)
      {
        paramContext = null;
      }
    }
  }
  
  public NfcAdapter getDefaultAdapter()
  {
    return this.mAdapter;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/NfcManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */