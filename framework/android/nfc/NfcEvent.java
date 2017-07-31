package android.nfc;

public final class NfcEvent
{
  public final NfcAdapter nfcAdapter;
  public final int peerLlcpMajorVersion;
  public final int peerLlcpMinorVersion;
  
  NfcEvent(NfcAdapter paramNfcAdapter, byte paramByte)
  {
    this.nfcAdapter = paramNfcAdapter;
    this.peerLlcpMajorVersion = ((paramByte & 0xF0) >> 4);
    this.peerLlcpMinorVersion = (paramByte & 0xF);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/NfcEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */