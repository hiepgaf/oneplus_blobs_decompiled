package android.content;

public class OperationApplicationException
  extends Exception
{
  private final int mNumSuccessfulYieldPoints;
  
  public OperationApplicationException()
  {
    this.mNumSuccessfulYieldPoints = 0;
  }
  
  public OperationApplicationException(int paramInt)
  {
    this.mNumSuccessfulYieldPoints = paramInt;
  }
  
  public OperationApplicationException(String paramString)
  {
    super(paramString);
    this.mNumSuccessfulYieldPoints = 0;
  }
  
  public OperationApplicationException(String paramString, int paramInt)
  {
    super(paramString);
    this.mNumSuccessfulYieldPoints = paramInt;
  }
  
  public OperationApplicationException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.mNumSuccessfulYieldPoints = 0;
  }
  
  public OperationApplicationException(Throwable paramThrowable)
  {
    super(paramThrowable);
    this.mNumSuccessfulYieldPoints = 0;
  }
  
  public int getNumSuccessfulYieldPoints()
  {
    return this.mNumSuccessfulYieldPoints;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/OperationApplicationException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */