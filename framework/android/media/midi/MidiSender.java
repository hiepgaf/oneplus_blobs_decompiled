package android.media.midi;

public abstract class MidiSender
{
  public void connect(MidiReceiver paramMidiReceiver)
  {
    if (paramMidiReceiver == null) {
      throw new NullPointerException("receiver null in MidiSender.connect");
    }
    onConnect(paramMidiReceiver);
  }
  
  public void disconnect(MidiReceiver paramMidiReceiver)
  {
    if (paramMidiReceiver == null) {
      throw new NullPointerException("receiver null in MidiSender.disconnect");
    }
    onDisconnect(paramMidiReceiver);
  }
  
  public abstract void onConnect(MidiReceiver paramMidiReceiver);
  
  public abstract void onDisconnect(MidiReceiver paramMidiReceiver);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiSender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */