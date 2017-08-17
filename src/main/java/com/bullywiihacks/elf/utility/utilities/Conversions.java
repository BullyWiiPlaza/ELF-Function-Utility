package com.bullywiihacks.elf.utility.utilities;

import java.math.BigInteger;

public class Conversions
{
	public static String toHexadecimal(byte[] data)
	{
		StringBuilder buf = new StringBuilder();

		for (byte aData : data)
		{
			int halfbyte = (aData >>> 4) & 0x0F;
			int two_halfs = 0;
			do
			{
				if ((0 <= halfbyte) && (halfbyte <= 9))
				{
					buf.append((char) ('0' + halfbyte));
				} else
				{
					buf.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = aData & 0x0F;
			} while (two_halfs++ < 1);
		}

		return buf.toString();
	}

	public static int toSigned24Bit(int value)
	{
		return toSigned(value, 24);
	}

	private static int toSigned(int value, int bits)
	{
		int maximumPositiveValue = (int) Math.pow(2, bits - 1);

		if (value <= maximumPositiveValue)
		{
			return value;
		}

		return value * (-1);
	}

	public static int toInteger(byte[] array)
	{
		return new BigInteger(array).intValue();
	}
}