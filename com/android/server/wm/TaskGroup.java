package com.android.server.wm;

import android.view.IApplicationToken;
import java.util.ArrayList;

public class TaskGroup
{
  public int taskId = -1;
  public ArrayList<IApplicationToken> tokens = new ArrayList();
  
  public String toString()
  {
    return "id=" + this.taskId + " tokens=" + this.tokens;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/TaskGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */