package com.oneplus.threekey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.Slog;

public class ThreeKeyBase
  implements ThreeKeyInterface
{
  private static final String ACTION_THREE_KEY = "com.oem.intent.action.THREE_KEY_MODE";
  private static final String ACTION_THREE_KEY_EXTRA = "switch_state";
  public static final int SWITCH_STATE_DOWN = 3;
  public static final int SWITCH_STATE_MIDDLE = 2;
  public static final int SWITCH_STATE_ON = 1;
  public static final int SWITCH_STATE_UNINIT = -1;
  private static final String TAG = "ThreeKeyBase";
  private Context mContext;
  private final BroadcastReceiver mReceiver = new ThreeKeyBroadcastReceiver(null);
  private int mThreeKeyMode = -1;
  
  public ThreeKeyBase(Context paramContext)
  {
    this.mContext = paramContext;
    register();
  }
  
  private void register()
  {
    IntentFilter localIntentFilter = new IntentFilter("com.oem.intent.action.THREE_KEY_MODE");
    this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, localIntentFilter, null, null);
  }
  
  public void init(int paramInt)
  {
    this.mThreeKeyMode = paramInt;
    setSwitchState(this.mThreeKeyMode);
  }
  
  public boolean isDown()
  {
    return this.mThreeKeyMode == 3;
  }
  
  public boolean isMiddle()
  {
    return this.mThreeKeyMode == 2;
  }
  
  public boolean isUp()
  {
    return this.mThreeKeyMode == 1;
  }
  
  public void reset()
  {
    Slog.d("ThreeKeyBase", "[reset]");
    init(this.mThreeKeyMode);
  }
  
  protected void setDown() {}
  
  protected void setMiddle() {}
  
  protected void setSwitchState(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Slog.e("ThreeKeyBase", "invalid switchState");
      return;
    case 1: 
      setUp();
    }
    for (;;)
    {
      this.mThreeKeyMode = paramInt;
      return;
      setMiddle();
      continue;
      setDown();
    }
  }
  
  protected void setUp() {}
  
  private class ThreeKeyBroadcastReceiver
    extends BroadcastReceiver
  {
    private ThreeKeyBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (paramIntent.getAction().equals("com.oem.intent.action.THREE_KEY_MODE"))
      {
        int i = paramIntent.getIntExtra("switch_state", -1);
        ThreeKeyBase.this.setSwitchState(i);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/threekey/ThreeKeyBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */