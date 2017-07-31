package com.amap.api.mapcore2d;

public class ck
  extends Exception
{
  private String a = "未知的错误";
  private int b = -1;
  
  public ck(String paramString)
  {
    super(paramString);
    this.a = paramString;
    a(paramString);
  }
  
  private void a(String paramString)
  {
    if (!"IO 操作异常 - IOException".equals(paramString))
    {
      if (!"socket 连接异常 - SocketException".equals(paramString))
      {
        if ("socket 连接超时 - SocketTimeoutException".equals(paramString)) {
          break label209;
        }
        if ("无效的参数 - IllegalArgumentException".equals(paramString)) {
          break label216;
        }
        if ("空指针异常 - NullPointException".equals(paramString)) {
          break label223;
        }
        if ("url异常 - MalformedURLException".equals(paramString)) {
          break label230;
        }
        if ("未知主机 - UnKnowHostException".equals(paramString)) {
          break label237;
        }
        if ("服务器连接失败 - UnknownServiceException".equals(paramString)) {
          break label244;
        }
        if ("协议解析错误 - ProtocolException".equals(paramString)) {
          break label251;
        }
        if ("http连接失败 - ConnectionException".equals(paramString)) {
          break label258;
        }
        if ("未知的错误".equals(paramString)) {
          break label265;
        }
        if ("key鉴权失败".equals(paramString)) {
          break label272;
        }
        if ("requeust is null".equals(paramString)) {
          break label279;
        }
        if ("request url is empty".equals(paramString)) {
          break label285;
        }
        if ("response is null".equals(paramString)) {
          break label291;
        }
        if ("thread pool has exception".equals(paramString)) {
          break label297;
        }
        if ("sdk name is invalid".equals(paramString)) {
          break label303;
        }
        if ("sdk info is null".equals(paramString)) {
          break label309;
        }
        if ("sdk packages is null".equals(paramString)) {
          break label316;
        }
        if ("线程池为空".equals(paramString)) {
          break label323;
        }
        if ("获取对象错误".equals(paramString)) {
          break label330;
        }
        this.b = -1;
      }
    }
    else
    {
      this.b = 21;
      return;
    }
    this.b = 22;
    return;
    label209:
    this.b = 23;
    return;
    label216:
    this.b = 24;
    return;
    label223:
    this.b = 25;
    return;
    label230:
    this.b = 26;
    return;
    label237:
    this.b = 27;
    return;
    label244:
    this.b = 28;
    return;
    label251:
    this.b = 29;
    return;
    label258:
    this.b = 30;
    return;
    label265:
    this.b = 31;
    return;
    label272:
    this.b = 32;
    return;
    label279:
    this.b = 1;
    return;
    label285:
    this.b = 2;
    return;
    label291:
    this.b = 3;
    return;
    label297:
    this.b = 4;
    return;
    label303:
    this.b = 5;
    return;
    label309:
    this.b = 6;
    return;
    label316:
    this.b = 7;
    return;
    label323:
    this.b = 8;
    return;
    label330:
    this.b = 101;
  }
  
  public String a()
  {
    return this.a;
  }
  
  public int b()
  {
    return this.b;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */