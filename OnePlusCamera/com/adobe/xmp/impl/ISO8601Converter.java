// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPDateTime;

public final class ISO8601Converter
{
    public static XMPDateTime parse(final String s) {
        return parse(s, new XMPDateTimeImpl());
    }
    
    public static XMPDateTime parse(final String s, final XMPDateTime xmpDateTime) {
        int n = 1;
        final char c = ':';
        final char c2 = '-';
        final int n2 = 5;
        int gatherInt = 0;
        ParameterAsserts.assertNotNull(s);
        final ParseState parseState = new ParseState(s);
        int n3 = 0;
        Label_0055: {
            if (parseState.ch(0) != 'T') {
                if (parseState.length() < 2 || parseState.ch(n) != c) {
                    if (parseState.length() < 3 || parseState.ch(2) != c) {
                        n3 = 0;
                        break Label_0055;
                    }
                }
            }
            n3 = n;
        }
        if (n3 != 0) {
            xmpDateTime.setMonth(n);
            xmpDateTime.setDay(n);
        }
        else {
            if (parseState.ch(0) == c2) {
                parseState.skip();
            }
            int gatherInt2 = parseState.gatherInt("Invalid year in date string", 9999);
            if (parseState.hasNext() && parseState.ch() != c2) {
                throw new XMPException("Invalid date string, after year", n2);
            }
            if (parseState.ch(0) == c2) {
                gatherInt2 = -gatherInt2;
            }
            xmpDateTime.setYear(gatherInt2);
            if (!parseState.hasNext()) {
                return xmpDateTime;
            }
            parseState.skip();
            final int gatherInt3 = parseState.gatherInt("Invalid month in date string", 12);
            if (parseState.hasNext() && parseState.ch() != c2) {
                throw new XMPException("Invalid date string, after month", n2);
            }
            xmpDateTime.setMonth(gatherInt3);
            if (!parseState.hasNext()) {
                return xmpDateTime;
            }
            parseState.skip();
            final int gatherInt4 = parseState.gatherInt("Invalid day in date string", 31);
            if (parseState.hasNext() && parseState.ch() != 'T') {
                throw new XMPException("Invalid date string, after day", n2);
            }
            xmpDateTime.setDay(gatherInt4);
            if (!parseState.hasNext()) {
                return xmpDateTime;
            }
        }
        if (parseState.ch() != 'T') {
            if (n3 == 0) {
                throw new XMPException("Invalid date string, missing 'T' after date", n2);
            }
        }
        else {
            parseState.skip();
        }
        final int gatherInt5 = parseState.gatherInt("Invalid hour in date string", 23);
        if (parseState.ch() != c) {
            throw new XMPException("Invalid date string, after hour", n2);
        }
        xmpDateTime.setHour(gatherInt5);
        parseState.skip();
        final int gatherInt6 = parseState.gatherInt("Invalid minute in date string", 59);
        if (parseState.hasNext() && parseState.ch() != c && parseState.ch() != 'Z' && parseState.ch() != '+' && parseState.ch() != c2) {
            throw new XMPException("Invalid date string, after minute", n2);
        }
        xmpDateTime.setMinute(gatherInt6);
        if (parseState.ch() == c) {
            parseState.skip();
            final int gatherInt7 = parseState.gatherInt("Invalid whole seconds in date string", 59);
            if (parseState.hasNext() && parseState.ch() != '.' && parseState.ch() != 'Z' && parseState.ch() != '+' && parseState.ch() != c2) {
                throw new XMPException("Invalid date string, after whole seconds", n2);
            }
            xmpDateTime.setSecond(gatherInt7);
            if (parseState.ch() == '.') {
                parseState.skip();
                final int pos = parseState.pos();
                int gatherInt8 = parseState.gatherInt("Invalid fractional seconds in date string", 999999999);
                if (parseState.ch() != 'Z' && parseState.ch() != '+' && parseState.ch() != c2) {
                    throw new XMPException("Invalid date string, after fractional second", n2);
                }
                int i;
                for (i = parseState.pos() - pos; i > 9; --i) {
                    gatherInt8 /= 10;
                }
                while (i < 9) {
                    gatherInt8 *= 10;
                    ++i;
                }
                xmpDateTime.setNanoSecond(gatherInt8);
            }
        }
        int gatherInt9;
        if (parseState.ch() != 'Z') {
            if (!parseState.hasNext()) {
                gatherInt9 = 0;
                n = 0;
            }
            else {
                if (parseState.ch() != '+') {
                    if (parseState.ch() != c2) {
                        throw new XMPException("Time zone must begin with 'Z', '+', or '-'", n2);
                    }
                    n = -1;
                }
                parseState.skip();
                gatherInt9 = parseState.gatherInt("Invalid time zone hour in date string", 23);
                if (parseState.ch() != c) {
                    throw new XMPException("Invalid date string, after time zone hour", n2);
                }
                parseState.skip();
                gatherInt = parseState.gatherInt("Invalid time zone minute in date string", 59);
            }
        }
        else {
            parseState.skip();
            gatherInt9 = 0;
            n = 0;
        }
        xmpDateTime.setTimeZone(new SimpleTimeZone((gatherInt9 * 3600 * 1000 + gatherInt * 60 * 1000) * n, ""));
        if (!parseState.hasNext()) {
            return xmpDateTime;
        }
        throw new XMPException("Invalid date string, extra chars at end", n2);
    }
    
    public static String render(final XMPDateTime xmpDateTime) {
        final int n = 3600000;
        final StringBuffer sb = new StringBuffer();
        final DecimalFormat decimalFormat = new DecimalFormat("0000", new DecimalFormatSymbols(Locale.ENGLISH));
        sb.append(decimalFormat.format(xmpDateTime.getYear()));
        if (xmpDateTime.getMonth() == 0) {
            return sb.toString();
        }
        decimalFormat.applyPattern("'-'00");
        sb.append(decimalFormat.format(xmpDateTime.getMonth()));
        if (xmpDateTime.getDay() != 0) {
            sb.append(decimalFormat.format(xmpDateTime.getDay()));
            if (xmpDateTime.getHour() == 0 && xmpDateTime.getMinute() == 0 && xmpDateTime.getSecond() == 0 && xmpDateTime.getNanoSecond() == 0) {
                if (xmpDateTime.getTimeZone() == null) {
                    return sb.toString();
                }
                if (xmpDateTime.getTimeZone().getRawOffset() == 0) {
                    return sb.toString();
                }
            }
            sb.append('T');
            decimalFormat.applyPattern("00");
            sb.append(decimalFormat.format(xmpDateTime.getHour()));
            sb.append(':');
            sb.append(decimalFormat.format(xmpDateTime.getMinute()));
            if (xmpDateTime.getSecond() != 0 || xmpDateTime.getNanoSecond() != 0) {
                final double n2 = xmpDateTime.getSecond() + xmpDateTime.getNanoSecond() / 1.0E9;
                decimalFormat.applyPattern(":00.#########");
                sb.append(decimalFormat.format(n2));
            }
            if (xmpDateTime.getTimeZone() != null) {
                final int offset = xmpDateTime.getTimeZone().getOffset(xmpDateTime.getCalendar().getTimeInMillis());
                if (offset != 0) {
                    final int n3 = offset / n;
                    final int abs = Math.abs(offset % n / 60000);
                    decimalFormat.applyPattern("+00;-00");
                    sb.append(decimalFormat.format(n3));
                    decimalFormat.applyPattern(":00");
                    sb.append(decimalFormat.format(abs));
                }
                else {
                    sb.append('Z');
                }
            }
            return sb.toString();
        }
        return sb.toString();
    }
}
