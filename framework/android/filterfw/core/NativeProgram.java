package android.filterfw.core;

public class NativeProgram
  extends Program
{
  private boolean mHasGetValueFunction = false;
  private boolean mHasInitFunction = false;
  private boolean mHasResetFunction = false;
  private boolean mHasSetValueFunction = false;
  private boolean mHasTeardownFunction = false;
  private boolean mTornDown = false;
  private int nativeProgramId;
  
  static
  {
    System.loadLibrary("filterfw");
  }
  
  public NativeProgram(String paramString1, String paramString2)
  {
    allocate();
    paramString1 = "lib" + paramString1 + ".so";
    if (!openNativeLibrary(paramString1)) {
      throw new RuntimeException("Could not find native library named '" + paramString1 + "' " + "required for native program!");
    }
    String str = paramString2 + "_process";
    if (!bindProcessFunction(str)) {
      throw new RuntimeException("Could not find native program function name " + str + " in library " + paramString1 + "! " + "This function is required!");
    }
    this.mHasInitFunction = bindInitFunction(paramString2 + "_init");
    this.mHasTeardownFunction = bindTeardownFunction(paramString2 + "_teardown");
    this.mHasSetValueFunction = bindSetValueFunction(paramString2 + "_setvalue");
    this.mHasGetValueFunction = bindGetValueFunction(paramString2 + "_getvalue");
    this.mHasResetFunction = bindResetFunction(paramString2 + "_reset");
    if ((!this.mHasInitFunction) || (callNativeInit())) {
      return;
    }
    throw new RuntimeException("Could not initialize NativeProgram!");
  }
  
  private native boolean allocate();
  
  private native boolean bindGetValueFunction(String paramString);
  
  private native boolean bindInitFunction(String paramString);
  
  private native boolean bindProcessFunction(String paramString);
  
  private native boolean bindResetFunction(String paramString);
  
  private native boolean bindSetValueFunction(String paramString);
  
  private native boolean bindTeardownFunction(String paramString);
  
  private native String callNativeGetValue(String paramString);
  
  private native boolean callNativeInit();
  
  private native boolean callNativeProcess(NativeFrame[] paramArrayOfNativeFrame, NativeFrame paramNativeFrame);
  
  private native boolean callNativeReset();
  
  private native boolean callNativeSetValue(String paramString1, String paramString2);
  
  private native boolean callNativeTeardown();
  
  private native boolean deallocate();
  
  private native boolean nativeInit();
  
  private native boolean openNativeLibrary(String paramString);
  
  protected void finalize()
    throws Throwable
  {
    tearDown();
  }
  
  public Object getHostValue(String paramString)
  {
    if (this.mTornDown) {
      throw new RuntimeException("NativeProgram already torn down!");
    }
    if (!this.mHasGetValueFunction) {
      throw new RuntimeException("Attempting to get native variable, but native code does not define native getvalue function!");
    }
    return callNativeGetValue(paramString);
  }
  
  public void process(Frame[] paramArrayOfFrame, Frame paramFrame)
  {
    if (this.mTornDown) {
      throw new RuntimeException("NativeProgram already torn down!");
    }
    NativeFrame[] arrayOfNativeFrame = new NativeFrame[paramArrayOfFrame.length];
    int i = 0;
    while (i < paramArrayOfFrame.length) {
      if ((paramArrayOfFrame[i] == null) || ((paramArrayOfFrame[i] instanceof NativeFrame)))
      {
        arrayOfNativeFrame[i] = ((NativeFrame)paramArrayOfFrame[i]);
        i += 1;
      }
      else
      {
        throw new RuntimeException("NativeProgram got non-native frame as input " + i + "!");
      }
    }
    if ((paramFrame == null) || ((paramFrame instanceof NativeFrame)))
    {
      if (!callNativeProcess(arrayOfNativeFrame, (NativeFrame)paramFrame)) {
        throw new RuntimeException("Calling native process() caused error!");
      }
    }
    else {
      throw new RuntimeException("NativeProgram got non-native output frame!");
    }
  }
  
  public void reset()
  {
    if ((!this.mHasResetFunction) || (callNativeReset())) {
      return;
    }
    throw new RuntimeException("Could not reset NativeProgram!");
  }
  
  public void setHostValue(String paramString, Object paramObject)
  {
    if (this.mTornDown) {
      throw new RuntimeException("NativeProgram already torn down!");
    }
    if (!this.mHasSetValueFunction) {
      throw new RuntimeException("Attempting to set native variable, but native code does not define native setvalue function!");
    }
    if (!callNativeSetValue(paramString, paramObject.toString())) {
      throw new RuntimeException("Error setting native value for variable '" + paramString + "'!");
    }
  }
  
  public void tearDown()
  {
    if (this.mTornDown) {
      return;
    }
    if ((!this.mHasTeardownFunction) || (callNativeTeardown()))
    {
      deallocate();
      this.mTornDown = true;
      return;
    }
    throw new RuntimeException("Could not tear down NativeProgram!");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/NativeProgram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */