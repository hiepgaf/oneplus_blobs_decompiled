package android.net;

public class Credentials
{
  private final int gid;
  private final int pid;
  private final int uid;
  
  public Credentials(int paramInt1, int paramInt2, int paramInt3)
  {
    this.pid = paramInt1;
    this.uid = paramInt2;
    this.gid = paramInt3;
  }
  
  public int getGid()
  {
    return this.gid;
  }
  
  public int getPid()
  {
    return this.pid;
  }
  
  public int getUid()
  {
    return this.uid;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/Credentials.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */