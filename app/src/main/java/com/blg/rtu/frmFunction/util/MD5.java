package com.blg.rtu.frmFunction.util;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
public class MD5 {
    public static String getMD5(byte[] paramArrayOfByte)
    {
        try
        {
            String str = HexDump.toHex(MessageDigest.getInstance("MD5").digest(paramArrayOfByte));
            return str;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /* Error */
    public static String getStreamMD5(String paramString)
    {
        // Byte code:
        //   0: sipush 4096
        //   3: newarray <illegal type>
        //   5: astore 4
        //   7: aconst_null
        //   8: astore_3
        //   9: ldc 15
        //   11: invokestatic 21	java/security/MessageDigest:getInstance	(Ljava/lang/String;)Ljava/security/MessageDigest;
        //   14: astore 5
        //   16: new 43	java/io/BufferedInputStream
        //   19: dup
        //   20: new 45	java/io/FileInputStream
        //   23: dup
        //   24: aload_0
        //   25: invokespecial 48	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
        //   28: invokespecial 51	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
        //   31: astore_2
        //   32: aload_2
        //   33: astore_0
        //   34: aload_2
        //   35: aload 4
        //   37: invokevirtual 55	java/io/BufferedInputStream:read	([B)I
        //   40: istore_1
        //   41: iload_1
        //   42: ifle +17 -> 59
        //   45: aload_2
        //   46: astore_0
        //   47: aload 5
        //   49: aload 4
        //   51: iconst_0
        //   52: iload_1
        //   53: invokevirtual 59	java/security/MessageDigest:update	([BII)V
        //   56: goto -24 -> 32
        //   59: aload_2
        //   60: astore_0
        //   61: aload_2
        //   62: invokevirtual 62	java/io/BufferedInputStream:close	()V
        //   65: aload_2
        //   66: astore_0
        //   67: aload 5
        //   69: invokevirtual 65	java/security/MessageDigest:digest	()[B
        //   72: invokestatic 30	com/fuchun/common/util/string/HexDump:toHex	([B)Ljava/lang/String;
        //   75: astore_3
        //   76: aload_3
        //   77: astore_0
        //   78: aload_2
        //   79: ifnull +53 -> 132
        //   82: aload_2
        //   83: invokevirtual 62	java/io/BufferedInputStream:close	()V
        //   86: aload_3
        //   87: areturn
        //   88: astore_0
        //   89: aload_0
        //   90: invokevirtual 68	java/lang/Exception:printStackTrace	()V
        //   93: aload_3
        //   94: areturn
        //   95: astore_3
        //   96: goto +12 -> 108
        //   99: astore_0
        //   100: aload_3
        //   101: astore_2
        //   102: goto +37 -> 139
        //   105: astore_3
        //   106: aconst_null
        //   107: astore_2
        //   108: aload_2
        //   109: astore_0
        //   110: aload_3
        //   111: invokevirtual 68	java/lang/Exception:printStackTrace	()V
        //   114: aload_2
        //   115: ifnull +15 -> 130
        //   118: aload_2
        //   119: invokevirtual 62	java/io/BufferedInputStream:close	()V
        //   122: goto +8 -> 130
        //   125: astore_0
        //   126: aload_0
        //   127: invokevirtual 68	java/lang/Exception:printStackTrace	()V
        //   130: aconst_null
        //   131: astore_0
        //   132: aload_0
        //   133: areturn
        //   134: astore_3
        //   135: aload_0
        //   136: astore_2
        //   137: aload_3
        //   138: astore_0
        //   139: aload_2
        //   140: ifnull +15 -> 155
        //   143: aload_2
        //   144: invokevirtual 62	java/io/BufferedInputStream:close	()V
        //   147: goto +8 -> 155
        //   150: astore_2
        //   151: aload_2
        //   152: invokevirtual 68	java/lang/Exception:printStackTrace	()V
        //   155: aload_0
        //   156: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	157	0	paramString	String
        //   40	13	1	i	int
        //   31	113	2	localObject1	Object
        //   150	2	2	localException1	Exception
        //   8	86	3	str	String
        //   95	6	3	localException2	Exception
        //   105	6	3	localException3	Exception
        //   134	4	3	localObject2	Object
        //   5	45	4	arrayOfByte	byte[]
        //   14	54	5	localMessageDigest	MessageDigest
        // Exception table:
        //   from	to	target	type
        //   82	86	88	java/lang/Exception
        //   34	41	95	java/lang/Exception
        //   47	56	95	java/lang/Exception
        //   61	65	95	java/lang/Exception
        //   67	76	95	java/lang/Exception
        //   9	32	99	finally
        //   9	32	105	java/lang/Exception
        //   118	122	125	java/lang/Exception
        //   34	41	134	finally
        //   47	56	134	finally
        //   61	65	134	finally
        //   67	76	134	finally
        //   110	114	134	finally
        //   143	147	150	java/lang/Exception
        return "" ;
    }

    public static String getStringMD5(String paramString)
    {
        if ((paramString != null) && (paramString.trim().length() >= 1)) {
            try
            {
                paramString = getMD5(paramString.getBytes("UTF-8"));
                return paramString;
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return null;
    }
}
