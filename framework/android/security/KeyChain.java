package android.security;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class KeyChain
{
  public static final String ACCOUNT_TYPE = "com.android.keychain";
  private static final String ACTION_CHOOSER = "com.android.keychain.CHOOSER";
  private static final String ACTION_INSTALL = "android.credentials.INSTALL";
  public static final String ACTION_STORAGE_CHANGED = "android.security.STORAGE_CHANGED";
  private static final String CERT_INSTALLER_PACKAGE = "com.android.certinstaller";
  public static final String EXTRA_ALIAS = "alias";
  public static final String EXTRA_CERTIFICATE = "CERT";
  public static final String EXTRA_NAME = "name";
  public static final String EXTRA_PKCS12 = "PKCS12";
  public static final String EXTRA_RESPONSE = "response";
  public static final String EXTRA_SENDER = "sender";
  public static final String EXTRA_URI = "uri";
  private static final String KEYCHAIN_PACKAGE = "com.android.keychain";
  
  public static KeyChainConnection bind(Context paramContext)
    throws InterruptedException
  {
    return bindAsUser(paramContext, Process.myUserHandle());
  }
  
  public static KeyChainConnection bindAsUser(Context paramContext, UserHandle paramUserHandle)
    throws InterruptedException
  {
    if (paramContext == null) {
      throw new NullPointerException("context == null");
    }
    ensureNotOnMainThread(paramContext);
    LinkedBlockingQueue localLinkedBlockingQueue = new LinkedBlockingQueue(1);
    ServiceConnection local1 = new ServiceConnection()
    {
      volatile boolean mConnectedAtLeastOnce = false;
      
      public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
      {
        if (!this.mConnectedAtLeastOnce) {
          this.mConnectedAtLeastOnce = true;
        }
        try
        {
          this.val$q.put(IKeyChainService.Stub.asInterface(paramAnonymousIBinder));
          return;
        }
        catch (InterruptedException paramAnonymousComponentName) {}
      }
      
      public void onServiceDisconnected(ComponentName paramAnonymousComponentName) {}
    };
    Intent localIntent = new Intent(IKeyChainService.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(paramContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (paramContext.bindServiceAsUser(localIntent, local1, 1, paramUserHandle))) {
      return new KeyChainConnection(paramContext, local1, (IKeyChainService)localLinkedBlockingQueue.take(), null);
    }
    throw new AssertionError("could not bind to KeyChainService");
  }
  
  public static void choosePrivateKeyAlias(Activity paramActivity, KeyChainAliasCallback paramKeyChainAliasCallback, String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, Uri paramUri, String paramString)
  {
    if (paramActivity == null) {
      throw new NullPointerException("activity == null");
    }
    if (paramKeyChainAliasCallback == null) {
      throw new NullPointerException("response == null");
    }
    paramArrayOfString = new Intent("com.android.keychain.CHOOSER");
    paramArrayOfString.setPackage("com.android.keychain");
    paramArrayOfString.putExtra("response", new AliasResponse(paramKeyChainAliasCallback, null));
    paramArrayOfString.putExtra("uri", paramUri);
    paramArrayOfString.putExtra("alias", paramString);
    paramArrayOfString.putExtra("sender", PendingIntent.getActivity(paramActivity, 0, new Intent(), 0));
    paramActivity.startActivity(paramArrayOfString);
  }
  
  public static void choosePrivateKeyAlias(Activity paramActivity, KeyChainAliasCallback paramKeyChainAliasCallback, String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, String paramString1, int paramInt, String paramString2)
  {
    Object localObject = null;
    StringBuilder localStringBuilder;
    if (paramString1 != null)
    {
      localObject = new Uri.Builder();
      localStringBuilder = new StringBuilder().append(paramString1);
      if (paramInt == -1) {
        break label91;
      }
    }
    label91:
    for (paramString1 = ":" + paramInt;; paramString1 = "")
    {
      localObject = ((Uri.Builder)localObject).authority(paramString1).build();
      choosePrivateKeyAlias(paramActivity, paramKeyChainAliasCallback, paramArrayOfString, paramArrayOfPrincipal, (Uri)localObject, paramString2);
      return;
    }
  }
  
  public static Intent createInstallIntent()
  {
    Intent localIntent = new Intent("android.credentials.INSTALL");
    localIntent.setClassName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain");
    return localIntent;
  }
  
  private static void ensureNotOnMainThread(Context paramContext)
  {
    Looper localLooper = Looper.myLooper();
    if ((localLooper != null) && (localLooper == paramContext.getMainLooper())) {
      throw new IllegalStateException("calling this from your main thread can lead to deadlock");
    }
  }
  
  /* Error */
  public static X509Certificate[] getCertificateChain(Context paramContext, String paramString)
    throws KeyChainException, InterruptedException
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +13 -> 14
    //   4: new 72	java/lang/NullPointerException
    //   7: dup
    //   8: ldc -22
    //   10: invokespecial 77	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   13: athrow
    //   14: aload_0
    //   15: invokevirtual 238	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   18: invokestatic 240	android/security/KeyChain:bind	(Landroid/content/Context;)Landroid/security/KeyChain$KeyChainConnection;
    //   21: astore_0
    //   22: aload_0
    //   23: invokevirtual 244	android/security/KeyChain$KeyChainConnection:getService	()Landroid/security/IKeyChainService;
    //   26: astore_3
    //   27: aload_3
    //   28: aload_1
    //   29: invokeinterface 248 2 0
    //   34: astore_2
    //   35: aload_2
    //   36: ifnonnull +9 -> 45
    //   39: aload_0
    //   40: invokevirtual 251	android/security/KeyChain$KeyChainConnection:close	()V
    //   43: aconst_null
    //   44: areturn
    //   45: aload_2
    //   46: invokestatic 255	android/security/KeyChain:toCertificate	([B)Ljava/security/cert/X509Certificate;
    //   49: astore_2
    //   50: aload_3
    //   51: aload_1
    //   52: invokeinterface 258 2 0
    //   57: astore_1
    //   58: aload_1
    //   59: ifnull +62 -> 121
    //   62: aload_1
    //   63: arraylength
    //   64: ifeq +57 -> 121
    //   67: aload_1
    //   68: invokestatic 262	android/security/KeyChain:toCertificates	([B)Ljava/util/Collection;
    //   71: astore_1
    //   72: new 264	java/util/ArrayList
    //   75: dup
    //   76: aload_1
    //   77: invokeinterface 270 1 0
    //   82: iconst_1
    //   83: iadd
    //   84: invokespecial 271	java/util/ArrayList:<init>	(I)V
    //   87: astore_3
    //   88: aload_3
    //   89: aload_2
    //   90: invokevirtual 275	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   93: pop
    //   94: aload_3
    //   95: aload_1
    //   96: invokevirtual 279	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   99: pop
    //   100: aload_3
    //   101: aload_3
    //   102: invokevirtual 280	java/util/ArrayList:size	()I
    //   105: anewarray 282	java/security/cert/X509Certificate
    //   108: invokevirtual 286	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   111: checkcast 288	[Ljava/security/cert/X509Certificate;
    //   114: astore_1
    //   115: aload_0
    //   116: invokevirtual 251	android/security/KeyChain$KeyChainConnection:close	()V
    //   119: aload_1
    //   120: areturn
    //   121: new 290	com/android/org/conscrypt/TrustedCertificateStore
    //   124: dup
    //   125: invokespecial 291	com/android/org/conscrypt/TrustedCertificateStore:<init>	()V
    //   128: aload_2
    //   129: invokevirtual 294	com/android/org/conscrypt/TrustedCertificateStore:getCertificateChain	(Ljava/security/cert/X509Certificate;)Ljava/util/List;
    //   132: astore_1
    //   133: aload_1
    //   134: aload_1
    //   135: invokeinterface 297 1 0
    //   140: anewarray 282	java/security/cert/X509Certificate
    //   143: invokeinterface 298 2 0
    //   148: checkcast 288	[Ljava/security/cert/X509Certificate;
    //   151: astore_1
    //   152: aload_0
    //   153: invokevirtual 251	android/security/KeyChain$KeyChainConnection:close	()V
    //   156: aload_1
    //   157: areturn
    //   158: astore_1
    //   159: new 226	android/security/KeyChainException
    //   162: dup
    //   163: aload_1
    //   164: invokespecial 301	android/security/KeyChainException:<init>	(Ljava/lang/Throwable;)V
    //   167: athrow
    //   168: astore_1
    //   169: aload_0
    //   170: invokevirtual 251	android/security/KeyChain$KeyChainConnection:close	()V
    //   173: aload_1
    //   174: athrow
    //   175: astore_1
    //   176: new 226	android/security/KeyChainException
    //   179: dup
    //   180: aload_1
    //   181: invokespecial 301	android/security/KeyChainException:<init>	(Ljava/lang/Throwable;)V
    //   184: athrow
    //   185: astore_1
    //   186: new 226	android/security/KeyChainException
    //   189: dup
    //   190: aload_1
    //   191: invokespecial 301	android/security/KeyChainException:<init>	(Ljava/lang/Throwable;)V
    //   194: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	195	0	paramContext	Context
    //   0	195	1	paramString	String
    //   34	95	2	localObject1	Object
    //   26	76	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   22	35	158	java/lang/RuntimeException
    //   45	58	158	java/lang/RuntimeException
    //   62	115	158	java/lang/RuntimeException
    //   121	152	158	java/lang/RuntimeException
    //   22	35	168	finally
    //   45	58	168	finally
    //   62	115	168	finally
    //   121	152	168	finally
    //   159	168	168	finally
    //   176	185	168	finally
    //   186	195	168	finally
    //   22	35	175	android/os/RemoteException
    //   45	58	175	android/os/RemoteException
    //   62	115	175	android/os/RemoteException
    //   121	152	175	android/os/RemoteException
    //   22	35	185	java/security/cert/CertificateException
    //   45	58	185	java/security/cert/CertificateException
    //   62	115	185	java/security/cert/CertificateException
    //   121	152	185	java/security/cert/CertificateException
  }
  
  /* Error */
  public static java.security.PrivateKey getPrivateKey(Context paramContext, String paramString)
    throws KeyChainException, InterruptedException
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +13 -> 14
    //   4: new 72	java/lang/NullPointerException
    //   7: dup
    //   8: ldc -22
    //   10: invokespecial 77	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   13: athrow
    //   14: aload_0
    //   15: invokevirtual 238	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   18: invokestatic 240	android/security/KeyChain:bind	(Landroid/content/Context;)Landroid/security/KeyChain$KeyChainConnection;
    //   21: astore_0
    //   22: aload_0
    //   23: invokevirtual 244	android/security/KeyChain$KeyChainConnection:getService	()Landroid/security/IKeyChainService;
    //   26: aload_1
    //   27: invokeinterface 309 2 0
    //   32: astore_1
    //   33: aload_1
    //   34: ifnonnull +9 -> 43
    //   37: aload_0
    //   38: invokevirtual 251	android/security/KeyChain$KeyChainConnection:close	()V
    //   41: aconst_null
    //   42: areturn
    //   43: invokestatic 315	android/security/KeyStore:getInstance	()Landroid/security/KeyStore;
    //   46: aload_1
    //   47: iconst_m1
    //   48: invokestatic 321	android/security/keystore/AndroidKeyStoreProvider:loadAndroidKeyStorePrivateKeyFromKeystore	(Landroid/security/KeyStore;Ljava/lang/String;I)Landroid/security/keystore/AndroidKeyStorePrivateKey;
    //   51: astore_1
    //   52: aload_0
    //   53: invokevirtual 251	android/security/KeyChain$KeyChainConnection:close	()V
    //   56: aload_1
    //   57: areturn
    //   58: astore_1
    //   59: new 226	android/security/KeyChainException
    //   62: dup
    //   63: aload_1
    //   64: invokespecial 301	android/security/KeyChainException:<init>	(Ljava/lang/Throwable;)V
    //   67: athrow
    //   68: astore_1
    //   69: aload_0
    //   70: invokevirtual 251	android/security/KeyChain$KeyChainConnection:close	()V
    //   73: aload_1
    //   74: athrow
    //   75: astore_1
    //   76: new 226	android/security/KeyChainException
    //   79: dup
    //   80: aload_1
    //   81: invokespecial 301	android/security/KeyChainException:<init>	(Ljava/lang/Throwable;)V
    //   84: athrow
    //   85: astore_1
    //   86: new 226	android/security/KeyChainException
    //   89: dup
    //   90: aload_1
    //   91: invokespecial 301	android/security/KeyChainException:<init>	(Ljava/lang/Throwable;)V
    //   94: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	95	0	paramContext	Context
    //   0	95	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   22	33	58	java/security/UnrecoverableKeyException
    //   43	52	58	java/security/UnrecoverableKeyException
    //   22	33	68	finally
    //   43	52	68	finally
    //   59	68	68	finally
    //   76	85	68	finally
    //   86	95	68	finally
    //   22	33	75	java/lang/RuntimeException
    //   43	52	75	java/lang/RuntimeException
    //   22	33	85	android/os/RemoteException
    //   43	52	85	android/os/RemoteException
  }
  
  @Deprecated
  public static boolean isBoundKeyAlgorithm(String paramString)
  {
    if (!isKeyAlgorithmSupported(paramString)) {
      return false;
    }
    return KeyStore.getInstance().isHardwareBacked(paramString);
  }
  
  public static boolean isKeyAlgorithmSupported(String paramString)
  {
    paramString = paramString.toUpperCase(Locale.US);
    if (!"EC".equals(paramString)) {
      return "RSA".equals(paramString);
    }
    return true;
  }
  
  public static X509Certificate toCertificate(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("bytes == null");
    }
    try
    {
      paramArrayOfByte = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(paramArrayOfByte));
      return paramArrayOfByte;
    }
    catch (CertificateException paramArrayOfByte)
    {
      throw new AssertionError(paramArrayOfByte);
    }
  }
  
  public static Collection<X509Certificate> toCertificates(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("bytes == null");
    }
    try
    {
      paramArrayOfByte = CertificateFactory.getInstance("X.509").generateCertificates(new ByteArrayInputStream(paramArrayOfByte));
      return paramArrayOfByte;
    }
    catch (CertificateException paramArrayOfByte)
    {
      throw new AssertionError(paramArrayOfByte);
    }
  }
  
  private static class AliasResponse
    extends IKeyChainAliasCallback.Stub
  {
    private final KeyChainAliasCallback keyChainAliasResponse;
    
    private AliasResponse(KeyChainAliasCallback paramKeyChainAliasCallback)
    {
      this.keyChainAliasResponse = paramKeyChainAliasCallback;
    }
    
    public void alias(String paramString)
    {
      this.keyChainAliasResponse.alias(paramString);
    }
  }
  
  public static final class KeyChainConnection
    implements Closeable
  {
    private final Context context;
    private final IKeyChainService service;
    private final ServiceConnection serviceConnection;
    
    private KeyChainConnection(Context paramContext, ServiceConnection paramServiceConnection, IKeyChainService paramIKeyChainService)
    {
      this.context = paramContext;
      this.serviceConnection = paramServiceConnection;
      this.service = paramIKeyChainService;
    }
    
    public void close()
    {
      this.context.unbindService(this.serviceConnection);
    }
    
    public IKeyChainService getService()
    {
      return this.service;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/KeyChain.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */