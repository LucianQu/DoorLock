package com.blg.rtu.frmFunction.util;

import java.io.IOException;
import java.io.StringReader;
public class HexDump {
    private static final char[] m_hexCodes = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
    private static final int[] m_shifts = { 60, 56, 52, 48, 44, 40, 36, 32, 28, 24, 20, 16, 12, 8, 4, 0 };

    private static int charToNumber(char paramChar)
    {
        if ((paramChar >= '0') && (paramChar <= '9')) {
            return paramChar - '0';
        }
        if ((paramChar >= 'a') && (paramChar <= 'f')) {
            return paramChar - 'a' + 10;
        }
        if ((paramChar >= 'A') && (paramChar <= 'F')) {
            return paramChar - 'A' + 10;
        }
        return -1;
    }

    public static byte[] restoreBytes(String paramString)
    {
        byte[] arrayOfByte = new byte[paramString.length() / 2];
        int i = 0;
        while (i < arrayOfByte.length)
        {
            int k = i * 2;
            int j = charToNumber(paramString.charAt(k));
            k = charToNumber(paramString.charAt(k + 1));
            if ((j != -1) && (k != -1))
            {
                arrayOfByte[i] = ((byte)((j << 4) + k));
                i += 1;
            }
            else
            {
                return null;
            }
        }
        return arrayOfByte;
    }

    public static String tablify(String paramString1, int paramInt, String paramString2, String paramString3)
    {
        String str = "" ;
        HexDump localHexDump = new HexDump();
        localHexDump.getClass();
        return new HexTablifier(paramInt, paramString2, paramString3).format(paramString1);
    }

    public static String tablify(byte[] paramArrayOfByte)
    {
        HexDump localHexDump = new HexDump();
        localHexDump.getClass();
        return new HexTablifier().format(toHex(paramArrayOfByte));
    }

    public static String tablify(byte[] paramArrayOfByte, int paramInt)
    {
        HexDump localHexDump = new HexDump();
        localHexDump.getClass();
        return new HexTablifier(paramInt).format(toHex(paramArrayOfByte));
    }

    public static String tablify(byte[] paramArrayOfByte, int paramInt, String paramString)
    {
        HexDump localHexDump = new HexDump();
        localHexDump.getClass();
        return new HexTablifier(paramInt, paramString).format(toHex(paramArrayOfByte));
    }

    public static String toHex(byte paramByte)
    {
        return toHex(paramByte, 2);
    }

    public static String toHex(int paramInt)
    {
        return toHex(paramInt, 8);
    }

    public static String toHex(long paramLong)
    {
        return toHex(paramLong, 16);
    }

    private static String toHex(long paramLong, int paramInt)
    {
        StringBuilder localStringBuilder = new StringBuilder(paramInt);
        int i = 0;
        while (i < paramInt)
        {
            int j = (int)(paramLong >> m_shifts[(16 - paramInt + i)] & 0xF);
            localStringBuilder.append(m_hexCodes[j]);
            i += 1;
        }
        return localStringBuilder.toString();
    }

    public static String toHex(short paramShort)
    {
        return toHex(paramShort, 4);
    }

    public static String toHex(byte[] paramArrayOfByte)
    {
        return toHex(paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    public static String toHex(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = paramInt1;
        while (i < paramInt2 + paramInt1)
        {
            localStringBuilder.append(toHex(paramArrayOfByte[i]));
            i += 1;
        }
        return localStringBuilder.toString();
    }

    static class HexTablifier
    {
        private String m_post = "\n";
        private String m_pre = "";
        private int m_row = 8;

        public HexTablifier() {}

        public HexTablifier(int paramInt)
        {
            this(paramInt, "", "\n");
        }

        public HexTablifier(int paramInt, String paramString)
        {
            this(paramInt, paramString, "\n");
        }

        public HexTablifier(int paramInt, String paramString1, String paramString2)
        {
            this.m_row = paramInt;
            this.m_pre = paramString1;
            this.m_post = paramString2;
        }

        private boolean getHexByte(StringBuilder paramStringBuilder, StringReader paramStringReader)
                throws IOException
        {
            char[] arrayOfChar = new char[4];
            int i = paramStringReader.read(arrayOfChar);
            boolean bool = false;
            if (i == -1) {
                return false;
            }
            paramStringBuilder.append(arrayOfChar, 0, i);
            paramStringBuilder.append(" ");
            if (i == 4) {
                bool = true;
            }
            return bool;
        }

        private boolean getHexLine(StringBuilder paramStringBuilder, StringReader paramStringReader)
                throws IOException
        {
            StringBuilder localStringBuilder = new StringBuilder();
            boolean bool = true;
            int i = 0;
            while (i < this.m_row)
            {
                bool = getHexByte(localStringBuilder, paramStringReader);
                if (!bool) {
                    break;
                }
                i += 1;
            }
            if (localStringBuilder.length() > 0)
            {
                paramStringBuilder.append(this.m_pre);
                paramStringBuilder.append(localStringBuilder);
                paramStringBuilder.append(this.m_post);
            }
            return bool;
        }

        public String format(String paramString)
        {
            StringReader localStringReader = new StringReader(paramString);
            StringBuilder stringBuilder = new StringBuilder(paramString.length() * 2);
            try
            {
                for (;;)
                {
                    boolean bool = getHexLine(stringBuilder, localStringReader);
                    if (!bool) {
                        break;
                    }
                }
            }
            catch (IOException localIOException)
            {
            }
            return paramString.toString();
        }
    }
}
