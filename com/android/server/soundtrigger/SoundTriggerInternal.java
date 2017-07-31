package com.android.server.soundtrigger;

import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel;
import android.hardware.soundtrigger.SoundTrigger.ModuleProperties;
import android.hardware.soundtrigger.SoundTrigger.RecognitionConfig;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class SoundTriggerInternal
{
  public static final int STATUS_ERROR = Integer.MIN_VALUE;
  public static final int STATUS_OK = 0;
  
  public abstract void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  public abstract SoundTrigger.ModuleProperties getModuleProperties();
  
  public abstract int startRecognition(int paramInt, SoundTrigger.KeyphraseSoundModel paramKeyphraseSoundModel, IRecognitionStatusCallback paramIRecognitionStatusCallback, SoundTrigger.RecognitionConfig paramRecognitionConfig);
  
  public abstract int stopRecognition(int paramInt, IRecognitionStatusCallback paramIRecognitionStatusCallback);
  
  public abstract int unloadKeyphraseModel(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/soundtrigger/SoundTriggerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */