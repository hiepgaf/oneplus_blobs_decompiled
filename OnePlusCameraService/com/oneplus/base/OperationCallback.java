package com.oneplus.base;

public abstract interface OperationCallback<TCanceled, TCompleted, TStarted>
{
  public abstract void onCanceled(TCanceled paramTCanceled);
  
  public abstract void onCompleted(TCompleted paramTCompleted);
  
  public abstract void onStarted(TStarted paramTStarted);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/OperationCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */