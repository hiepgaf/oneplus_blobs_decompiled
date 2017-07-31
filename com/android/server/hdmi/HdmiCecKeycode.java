package com.android.server.hdmi;

import java.util.Arrays;
import libcore.util.EmptyArray;

final class HdmiCecKeycode
{
  public static final int CEC_KEYCODE_ANGLE = 80;
  public static final int CEC_KEYCODE_BACKWARD = 76;
  public static final int CEC_KEYCODE_CHANNEL_DOWN = 49;
  public static final int CEC_KEYCODE_CHANNEL_UP = 48;
  public static final int CEC_KEYCODE_CLEAR = 44;
  public static final int CEC_KEYCODE_CONTENTS_MENU = 11;
  public static final int CEC_KEYCODE_DATA = 118;
  public static final int CEC_KEYCODE_DISPLAY_INFORMATION = 53;
  public static final int CEC_KEYCODE_DOT = 42;
  public static final int CEC_KEYCODE_DOWN = 2;
  public static final int CEC_KEYCODE_EJECT = 74;
  public static final int CEC_KEYCODE_ELECTRONIC_PROGRAM_GUIDE = 83;
  public static final int CEC_KEYCODE_ENTER = 43;
  public static final int CEC_KEYCODE_EXIT = 13;
  public static final int CEC_KEYCODE_F1_BLUE = 113;
  public static final int CEC_KEYCODE_F2_RED = 114;
  public static final int CEC_KEYCODE_F3_GREEN = 115;
  public static final int CEC_KEYCODE_F4_YELLOW = 116;
  public static final int CEC_KEYCODE_F5 = 117;
  public static final int CEC_KEYCODE_FAST_FORWARD = 73;
  public static final int CEC_KEYCODE_FAVORITE_MENU = 12;
  public static final int CEC_KEYCODE_FORWARD = 75;
  public static final int CEC_KEYCODE_HELP = 54;
  public static final int CEC_KEYCODE_INITIAL_CONFIGURATION = 85;
  public static final int CEC_KEYCODE_INPUT_SELECT = 52;
  public static final int CEC_KEYCODE_LEFT = 3;
  public static final int CEC_KEYCODE_LEFT_DOWN = 8;
  public static final int CEC_KEYCODE_LEFT_UP = 7;
  public static final int CEC_KEYCODE_MEDIA_CONTEXT_SENSITIVE_MENU = 17;
  public static final int CEC_KEYCODE_MEDIA_TOP_MENU = 16;
  public static final int CEC_KEYCODE_MUTE = 67;
  public static final int CEC_KEYCODE_MUTE_FUNCTION = 101;
  public static final int CEC_KEYCODE_NEXT_FAVORITE = 47;
  public static final int CEC_KEYCODE_NUMBERS_1 = 33;
  public static final int CEC_KEYCODE_NUMBERS_2 = 34;
  public static final int CEC_KEYCODE_NUMBERS_3 = 35;
  public static final int CEC_KEYCODE_NUMBERS_4 = 36;
  public static final int CEC_KEYCODE_NUMBERS_5 = 37;
  public static final int CEC_KEYCODE_NUMBERS_6 = 38;
  public static final int CEC_KEYCODE_NUMBERS_7 = 39;
  public static final int CEC_KEYCODE_NUMBERS_8 = 40;
  public static final int CEC_KEYCODE_NUMBERS_9 = 41;
  public static final int CEC_KEYCODE_NUMBER_0_OR_NUMBER_10 = 32;
  public static final int CEC_KEYCODE_NUMBER_11 = 30;
  public static final int CEC_KEYCODE_NUMBER_12 = 31;
  public static final int CEC_KEYCODE_NUMBER_ENTRY_MODE = 29;
  public static final int CEC_KEYCODE_PAGE_DOWN = 56;
  public static final int CEC_KEYCODE_PAGE_UP = 55;
  public static final int CEC_KEYCODE_PAUSE = 70;
  public static final int CEC_KEYCODE_PAUSE_PLAY_FUNCTION = 97;
  public static final int CEC_KEYCODE_PAUSE_RECORD = 78;
  public static final int CEC_KEYCODE_PAUSE_RECORD_FUNCTION = 99;
  public static final int CEC_KEYCODE_PLAY = 68;
  public static final int CEC_KEYCODE_PLAY_FUNCTION = 96;
  public static final int CEC_KEYCODE_POWER = 64;
  public static final int CEC_KEYCODE_POWER_OFF_FUNCTION = 108;
  public static final int CEC_KEYCODE_POWER_ON_FUNCTION = 109;
  public static final int CEC_KEYCODE_POWER_TOGGLE_FUNCTION = 107;
  public static final int CEC_KEYCODE_PREVIOUS_CHANNEL = 50;
  public static final int CEC_KEYCODE_RECORD = 71;
  public static final int CEC_KEYCODE_RECORD_FUNCTION = 98;
  public static final int CEC_KEYCODE_RESERVED = 79;
  public static final int CEC_KEYCODE_RESTORE_VOLUME_FUNCTION = 102;
  public static final int CEC_KEYCODE_REWIND = 72;
  public static final int CEC_KEYCODE_RIGHT = 4;
  public static final int CEC_KEYCODE_RIGHT_DOWN = 6;
  public static final int CEC_KEYCODE_RIGHT_UP = 5;
  public static final int CEC_KEYCODE_ROOT_MENU = 9;
  public static final int CEC_KEYCODE_SELECT = 0;
  public static final int CEC_KEYCODE_SELECT_AUDIO_INPUT_FUNCTION = 106;
  public static final int CEC_KEYCODE_SELECT_AV_INPUT_FUNCTION = 105;
  public static final int CEC_KEYCODE_SELECT_BROADCAST_TYPE = 86;
  public static final int CEC_KEYCODE_SELECT_MEDIA_FUNCTION = 104;
  public static final int CEC_KEYCODE_SELECT_SOUND_PRESENTATION = 87;
  public static final int CEC_KEYCODE_SETUP_MENU = 10;
  public static final int CEC_KEYCODE_SOUND_SELECT = 51;
  public static final int CEC_KEYCODE_STOP = 69;
  public static final int CEC_KEYCODE_STOP_FUNCTION = 100;
  public static final int CEC_KEYCODE_STOP_RECORD = 77;
  public static final int CEC_KEYCODE_SUB_PICTURE = 81;
  public static final int CEC_KEYCODE_TIMER_PROGRAMMING = 84;
  public static final int CEC_KEYCODE_TUNE_FUNCTION = 103;
  public static final int CEC_KEYCODE_UP = 1;
  public static final int CEC_KEYCODE_VIDEO_ON_DEMAND = 82;
  public static final int CEC_KEYCODE_VOLUME_DOWN = 66;
  public static final int CEC_KEYCODE_VOLUME_UP = 65;
  private static final KeycodeEntry[] KEYCODE_ENTRIES = { new KeycodeEntry(23, 0, null), new KeycodeEntry(19, 1, null), new KeycodeEntry(20, 2, null), new KeycodeEntry(21, 3, null), new KeycodeEntry(22, 4, null), new KeycodeEntry(-1, 5, null), new KeycodeEntry(-1, 6, null), new KeycodeEntry(-1, 7, null), new KeycodeEntry(-1, 8, null), new KeycodeEntry(3, 9, null), new KeycodeEntry(176, 10, null), new KeycodeEntry(256, 11, false, null), new KeycodeEntry(-1, 12, null), new KeycodeEntry(4, 13, null), new KeycodeEntry(111, 13, null), new KeycodeEntry(226, 16, null), new KeycodeEntry(257, 17, null), new KeycodeEntry(234, 29, null), new KeycodeEntry(227, 30, null), new KeycodeEntry(228, 31, null), new KeycodeEntry(7, 32, null), new KeycodeEntry(8, 33, null), new KeycodeEntry(9, 34, null), new KeycodeEntry(10, 35, null), new KeycodeEntry(11, 36, null), new KeycodeEntry(12, 37, null), new KeycodeEntry(13, 38, null), new KeycodeEntry(14, 39, null), new KeycodeEntry(15, 40, null), new KeycodeEntry(16, 41, null), new KeycodeEntry(56, 42, null), new KeycodeEntry(160, 43, null), new KeycodeEntry(28, 44, null), new KeycodeEntry(-1, 47, null), new KeycodeEntry(166, 48, null), new KeycodeEntry(167, 49, null), new KeycodeEntry(229, 50, null), new KeycodeEntry(-1, 51, null), new KeycodeEntry(178, 52, null), new KeycodeEntry(165, 53, null), new KeycodeEntry(-1, 54, null), new KeycodeEntry(92, 55, null), new KeycodeEntry(93, 56, null), new KeycodeEntry(26, 64, false, null), new KeycodeEntry(24, 65, null), new KeycodeEntry(25, 66, null), new KeycodeEntry(164, 67, false, null), new KeycodeEntry(126, 68, null), new KeycodeEntry(86, 69, null), new KeycodeEntry(127, 70, null), new KeycodeEntry(85, 70, null), new KeycodeEntry(130, 71, null), new KeycodeEntry(89, 72, null), new KeycodeEntry(90, 73, null), new KeycodeEntry(129, 74, null), new KeycodeEntry(87, 75, null), new KeycodeEntry(88, 76, null), new KeycodeEntry(-1, 77, null), new KeycodeEntry(-1, 78, null), new KeycodeEntry(-1, 79, null), new KeycodeEntry(-1, 80, null), new KeycodeEntry(175, 81, null), new KeycodeEntry(-1, 82, null), new KeycodeEntry(172, 83, null), new KeycodeEntry(258, 84, null), new KeycodeEntry(-1, 85, null), new KeycodeEntry(-1, 86, null), new KeycodeEntry(235, 86, true, intToSingleByteArray(16), null), new KeycodeEntry(236, 86, true, intToSingleByteArray(96), null), new KeycodeEntry(238, 86, true, intToSingleByteArray(128), null), new KeycodeEntry(239, 86, true, intToSingleByteArray(144), null), new KeycodeEntry(241, 86, true, intToSingleByteArray(1), null), new KeycodeEntry(-1, 87, null), new KeycodeEntry(-1, 96, false, null), new KeycodeEntry(-1, 97, false, null), new KeycodeEntry(-1, 98, false, null), new KeycodeEntry(-1, 99, false, null), new KeycodeEntry(-1, 100, false, null), new KeycodeEntry(-1, 101, false, null), new KeycodeEntry(-1, 102, false, null), new KeycodeEntry(-1, 103, false, null), new KeycodeEntry(-1, 104, false, null), new KeycodeEntry(-1, 105, false, null), new KeycodeEntry(-1, 106, false, null), new KeycodeEntry(-1, 107, false, null), new KeycodeEntry(-1, 108, false, null), new KeycodeEntry(-1, 109, false, null), new KeycodeEntry(186, 113, null), new KeycodeEntry(183, 114, null), new KeycodeEntry(184, 115, null), new KeycodeEntry(185, 116, null), new KeycodeEntry(135, 117, null), new KeycodeEntry(230, 118, null) };
  public static final int NO_PARAM = -1;
  public static final int UI_BROADCAST_ANALOGUE = 16;
  public static final int UI_BROADCAST_ANALOGUE_CABLE = 48;
  public static final int UI_BROADCAST_ANALOGUE_SATELLITE = 64;
  public static final int UI_BROADCAST_ANALOGUE_TERRESTRIAL = 32;
  public static final int UI_BROADCAST_DIGITAL = 80;
  public static final int UI_BROADCAST_DIGITAL_CABLE = 112;
  public static final int UI_BROADCAST_DIGITAL_COMMNICATIONS_SATELLITE = 144;
  public static final int UI_BROADCAST_DIGITAL_COMMNICATIONS_SATELLITE_2 = 145;
  public static final int UI_BROADCAST_DIGITAL_SATELLITE = 128;
  public static final int UI_BROADCAST_DIGITAL_TERRESTRIAL = 96;
  public static final int UI_BROADCAST_IP = 160;
  public static final int UI_BROADCAST_TOGGLE_ALL = 0;
  public static final int UI_BROADCAST_TOGGLE_ANALOGUE_DIGITAL = 1;
  public static final int UI_SOUND_PRESENTATION_BASS_NEUTRAL = 178;
  public static final int UI_SOUND_PRESENTATION_BASS_STEP_MINUS = 179;
  public static final int UI_SOUND_PRESENTATION_BASS_STEP_PLUS = 177;
  public static final int UI_SOUND_PRESENTATION_SELECT_AUDIO_AUTO_EQUALIZER = 160;
  public static final int UI_SOUND_PRESENTATION_SELECT_AUDIO_AUTO_REVERBERATION = 144;
  public static final int UI_SOUND_PRESENTATION_SELECT_AUDIO_DOWN_MIX = 128;
  public static final int UI_SOUND_PRESENTATION_SOUND_MIX_DUAL_MONO = 32;
  public static final int UI_SOUND_PRESENTATION_SOUND_MIX_KARAOKE = 48;
  public static final int UI_SOUND_PRESENTATION_TREBLE_NEUTRAL = 194;
  public static final int UI_SOUND_PRESENTATION_TREBLE_STEP_MINUS = 195;
  public static final int UI_SOUND_PRESENTATION_TREBLE_STEP_PLUS = 193;
  public static final int UNSUPPORTED_KEYCODE = -1;
  
  static byte[] androidKeyToCecKey(int paramInt)
  {
    int i = 0;
    while (i < KEYCODE_ENTRIES.length)
    {
      byte[] arrayOfByte = KeycodeEntry.-wrap0(KEYCODE_ENTRIES[i], paramInt);
      if (arrayOfByte != null) {
        return arrayOfByte;
      }
      i += 1;
    }
    return null;
  }
  
  static int cecKeycodeAndParamsToAndroidKey(byte[] paramArrayOfByte)
  {
    int i = 0;
    while (i < KEYCODE_ENTRIES.length)
    {
      int j = KeycodeEntry.-wrap1(KEYCODE_ENTRIES[i], paramArrayOfByte);
      if (j != -1) {
        return j;
      }
      i += 1;
    }
    return -1;
  }
  
  public static int getMuteKey(boolean paramBoolean)
  {
    return 67;
  }
  
  private static byte[] intToSingleByteArray(int paramInt)
  {
    return new byte[] { (byte)(paramInt & 0xFF) };
  }
  
  static boolean isRepeatableKey(int paramInt)
  {
    int i = 0;
    while (i < KEYCODE_ENTRIES.length)
    {
      Boolean localBoolean = KeycodeEntry.-wrap2(KEYCODE_ENTRIES[i], paramInt);
      if (localBoolean != null) {
        return localBoolean.booleanValue();
      }
      i += 1;
    }
    return false;
  }
  
  static boolean isSupportedKeycode(int paramInt)
  {
    return androidKeyToCecKey(paramInt) != null;
  }
  
  private static class KeycodeEntry
  {
    private final int mAndroidKeycode;
    private final byte[] mCecKeycodeAndParams;
    private final boolean mIsRepeatable;
    
    private KeycodeEntry(int paramInt1, int paramInt2)
    {
      this(paramInt1, paramInt2, true, EmptyArray.BYTE);
    }
    
    private KeycodeEntry(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this(paramInt1, paramInt2, paramBoolean, EmptyArray.BYTE);
    }
    
    private KeycodeEntry(int paramInt1, int paramInt2, boolean paramBoolean, byte[] paramArrayOfByte)
    {
      this.mAndroidKeycode = paramInt1;
      this.mIsRepeatable = paramBoolean;
      this.mCecKeycodeAndParams = new byte[paramArrayOfByte.length + 1];
      System.arraycopy(paramArrayOfByte, 0, this.mCecKeycodeAndParams, 1, paramArrayOfByte.length);
      this.mCecKeycodeAndParams[0] = ((byte)(paramInt2 & 0xFF));
    }
    
    private KeycodeEntry(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      this(paramInt1, paramInt2, true, paramArrayOfByte);
    }
    
    private Boolean isRepeatableIfMatched(int paramInt)
    {
      if (this.mAndroidKeycode == paramInt) {
        return Boolean.valueOf(this.mIsRepeatable);
      }
      return null;
    }
    
    private int toAndroidKeycodeIfMatched(byte[] paramArrayOfByte)
    {
      if (Arrays.equals(this.mCecKeycodeAndParams, paramArrayOfByte)) {
        return this.mAndroidKeycode;
      }
      return -1;
    }
    
    private byte[] toCecKeycodeAndParamIfMatched(int paramInt)
    {
      if (this.mAndroidKeycode == paramInt) {
        return this.mCecKeycodeAndParams;
      }
      return null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecKeycode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */