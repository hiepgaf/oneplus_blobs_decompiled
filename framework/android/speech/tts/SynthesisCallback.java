package android.speech.tts;

public abstract interface SynthesisCallback
{
  public abstract int audioAvailable(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract int done();
  
  public abstract void error();
  
  public abstract void error(int paramInt);
  
  public abstract int getMaxBufferSize();
  
  public abstract boolean hasFinished();
  
  public abstract boolean hasStarted();
  
  public abstract int start(int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/SynthesisCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */