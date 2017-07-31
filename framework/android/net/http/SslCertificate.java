package android.net.http;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.android.internal.util.HexDump;
import com.android.org.bouncycastle.asn1.x509.X509Name;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class SslCertificate
{
  private static String ISO_8601_DATE_FORMAT = "yyyy-MM-dd HH:mm:ssZ";
  private static final String ISSUED_BY = "issued-by";
  private static final String ISSUED_TO = "issued-to";
  private static final String VALID_NOT_AFTER = "valid-not-after";
  private static final String VALID_NOT_BEFORE = "valid-not-before";
  private static final String X509_CERTIFICATE = "x509-certificate";
  private final DName mIssuedBy;
  private final DName mIssuedTo;
  private final Date mValidNotAfter;
  private final Date mValidNotBefore;
  private final X509Certificate mX509Certificate;
  
  @Deprecated
  public SslCertificate(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this(paramString1, paramString2, parseDate(paramString3), parseDate(paramString4), null);
  }
  
  @Deprecated
  public SslCertificate(String paramString1, String paramString2, Date paramDate1, Date paramDate2)
  {
    this(paramString1, paramString2, paramDate1, paramDate2, null);
  }
  
  private SslCertificate(String paramString1, String paramString2, Date paramDate1, Date paramDate2, X509Certificate paramX509Certificate)
  {
    this.mIssuedTo = new DName(paramString1);
    this.mIssuedBy = new DName(paramString2);
    this.mValidNotBefore = cloneDate(paramDate1);
    this.mValidNotAfter = cloneDate(paramDate2);
    this.mX509Certificate = paramX509Certificate;
  }
  
  public SslCertificate(X509Certificate paramX509Certificate)
  {
    this(paramX509Certificate.getSubjectDN().getName(), paramX509Certificate.getIssuerDN().getName(), paramX509Certificate.getNotBefore(), paramX509Certificate.getNotAfter(), paramX509Certificate);
  }
  
  private static Date cloneDate(Date paramDate)
  {
    if (paramDate == null) {
      return null;
    }
    return (Date)paramDate.clone();
  }
  
  private static final String fingerprint(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      HexDump.appendByteAsHex(localStringBuilder, paramArrayOfByte[i], true);
      if (i + 1 != paramArrayOfByte.length) {
        localStringBuilder.append(':');
      }
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private String formatCertificateDate(Context paramContext, Date paramDate)
  {
    if (paramDate == null) {
      return "";
    }
    return android.text.format.DateFormat.getDateFormat(paramContext).format(paramDate);
  }
  
  private static String formatDate(Date paramDate)
  {
    if (paramDate == null) {
      return "";
    }
    return new SimpleDateFormat(ISO_8601_DATE_FORMAT).format(paramDate);
  }
  
  private static String getDigest(X509Certificate paramX509Certificate, String paramString)
  {
    if (paramX509Certificate == null) {
      return "";
    }
    try
    {
      paramX509Certificate = paramX509Certificate.getEncoded();
      paramX509Certificate = fingerprint(MessageDigest.getInstance(paramString).digest(paramX509Certificate));
      return paramX509Certificate;
    }
    catch (NoSuchAlgorithmException paramX509Certificate)
    {
      return "";
    }
    catch (CertificateEncodingException paramX509Certificate) {}
    return "";
  }
  
  private static String getSerialNumber(X509Certificate paramX509Certificate)
  {
    if (paramX509Certificate == null) {
      return "";
    }
    paramX509Certificate = paramX509Certificate.getSerialNumber();
    if (paramX509Certificate == null) {
      return "";
    }
    return fingerprint(paramX509Certificate.toByteArray());
  }
  
  private static Date parseDate(String paramString)
  {
    try
    {
      paramString = new SimpleDateFormat(ISO_8601_DATE_FORMAT).parse(paramString);
      return paramString;
    }
    catch (ParseException paramString) {}
    return null;
  }
  
  public static SslCertificate restoreState(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    Object localObject1 = paramBundle.getByteArray("x509-certificate");
    if (localObject1 == null) {
      localObject1 = null;
    }
    for (;;)
    {
      return new SslCertificate(paramBundle.getString("issued-to"), paramBundle.getString("issued-by"), parseDate(paramBundle.getString("valid-not-before")), parseDate(paramBundle.getString("valid-not-after")), (X509Certificate)localObject1);
      try
      {
        localObject1 = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream((byte[])localObject1));
      }
      catch (CertificateException localCertificateException)
      {
        Object localObject2 = null;
      }
    }
  }
  
  public static Bundle saveState(SslCertificate paramSslCertificate)
  {
    if (paramSslCertificate == null) {
      return null;
    }
    Bundle localBundle = new Bundle();
    localBundle.putString("issued-to", paramSslCertificate.getIssuedTo().getDName());
    localBundle.putString("issued-by", paramSslCertificate.getIssuedBy().getDName());
    localBundle.putString("valid-not-before", paramSslCertificate.getValidNotBefore());
    localBundle.putString("valid-not-after", paramSslCertificate.getValidNotAfter());
    paramSslCertificate = paramSslCertificate.mX509Certificate;
    if (paramSslCertificate != null) {}
    try
    {
      localBundle.putByteArray("x509-certificate", paramSslCertificate.getEncoded());
      return localBundle;
    }
    catch (CertificateEncodingException paramSslCertificate) {}
    return localBundle;
  }
  
  public DName getIssuedBy()
  {
    return this.mIssuedBy;
  }
  
  public DName getIssuedTo()
  {
    return this.mIssuedTo;
  }
  
  @Deprecated
  public String getValidNotAfter()
  {
    return formatDate(this.mValidNotAfter);
  }
  
  public Date getValidNotAfterDate()
  {
    return cloneDate(this.mValidNotAfter);
  }
  
  @Deprecated
  public String getValidNotBefore()
  {
    return formatDate(this.mValidNotBefore);
  }
  
  public Date getValidNotBeforeDate()
  {
    return cloneDate(this.mValidNotBefore);
  }
  
  public View inflateCertificateView(Context paramContext)
  {
    View localView = LayoutInflater.from(paramContext).inflate(17367273, null);
    Object localObject = getIssuedTo();
    if (localObject != null)
    {
      ((TextView)localView.findViewById(16909336)).setText(((DName)localObject).getCName());
      ((TextView)localView.findViewById(16909338)).setText(((DName)localObject).getOName());
      ((TextView)localView.findViewById(16909340)).setText(((DName)localObject).getUName());
    }
    ((TextView)localView.findViewById(16909342)).setText(getSerialNumber(this.mX509Certificate));
    localObject = getIssuedBy();
    if (localObject != null)
    {
      ((TextView)localView.findViewById(16909344)).setText(((DName)localObject).getCName());
      ((TextView)localView.findViewById(16909346)).setText(((DName)localObject).getOName());
      ((TextView)localView.findViewById(16909348)).setText(((DName)localObject).getUName());
    }
    localObject = formatCertificateDate(paramContext, getValidNotBeforeDate());
    ((TextView)localView.findViewById(16909351)).setText((CharSequence)localObject);
    paramContext = formatCertificateDate(paramContext, getValidNotAfterDate());
    ((TextView)localView.findViewById(16909353)).setText(paramContext);
    ((TextView)localView.findViewById(16909356)).setText(getDigest(this.mX509Certificate, "SHA256"));
    ((TextView)localView.findViewById(16909358)).setText(getDigest(this.mX509Certificate, "SHA1"));
    return localView;
  }
  
  public String toString()
  {
    return "Issued to: " + this.mIssuedTo.getDName() + ";\n" + "Issued by: " + this.mIssuedBy.getDName() + ";\n";
  }
  
  public class DName
  {
    private String mCName;
    private String mDName;
    private String mOName;
    private String mUName;
    
    public DName(String paramString)
    {
      if (paramString != null)
      {
        this.mDName = paramString;
        try
        {
          paramString = new X509Name(paramString);
          this$1 = paramString.getValues();
          paramString = paramString.getOIDs();
          int i = 0;
          while (i < paramString.size())
          {
            if (paramString.elementAt(i).equals(X509Name.CN))
            {
              if (this.mCName == null) {
                this.mCName = ((String)SslCertificate.this.elementAt(i));
              }
            }
            else if ((paramString.elementAt(i).equals(X509Name.O)) && (this.mOName == null)) {
              this.mOName = ((String)SslCertificate.this.elementAt(i));
            } else if ((paramString.elementAt(i).equals(X509Name.OU)) && (this.mUName == null)) {
              this.mUName = ((String)SslCertificate.this.elementAt(i));
            }
            i += 1;
          }
          return;
        }
        catch (IllegalArgumentException this$1) {}
      }
    }
    
    public String getCName()
    {
      if (this.mCName != null) {
        return this.mCName;
      }
      return "";
    }
    
    public String getDName()
    {
      if (this.mDName != null) {
        return this.mDName;
      }
      return "";
    }
    
    public String getOName()
    {
      if (this.mOName != null) {
        return this.mOName;
      }
      return "";
    }
    
    public String getUName()
    {
      if (this.mUName != null) {
        return this.mUName;
      }
      return "";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/http/SslCertificate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */