package com.bullywiihacks.elf.utility.elf;

import net.fornwall.jelf.ElfSymbol;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

public class ELFFunction implements Comparable<ELFFunction>
{
	private long fileOffset;
	private ElfSymbol elfSymbol;
	private String executableFilePath;

	public ELFFunction(long fileOffset, ElfSymbol elfSymbol, String executableFilePath)
	{
		this.executableFilePath = executableFilePath;
		this.fileOffset = fileOffset;
		this.elfSymbol = elfSymbol;
	}

	public byte[] getAssembly() throws IOException
	{
		return readByteRange(executableFilePath, fileOffset, (int) elfSymbol.size);
	}

	private static byte[] readByteRange(String sourceFilePath, long startingOffset, int length) throws IOException
	{
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(sourceFilePath, "r"))
		{
			byte[] buffer = new byte[length];
			randomAccessFile.seek(startingOffset);
			randomAccessFile.readFully(buffer);

			return buffer;
		}
	}

	public String getName()
	{
		try
		{
			return elfSymbol.getName();
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}

		return null;
	}

	public long getOffsetLong()
	{
		return fileOffset;
	}

	public String getOffset()
	{
		return "0x" + Long.toHexString(fileOffset).toUpperCase();
	}

	public String getSize()
	{
		return elfSymbol.size + "";
	}

	@Override
	public int compareTo(ELFFunction o)
	{
		return Long.compare(fileOffset, o.fileOffset);
	}

	public static ELFFunction findByAssembly(List<ELFFunction> elfFunctions,
	                                         byte[] assembly) throws IOException
	{
		for (ELFFunction elfFunction : elfFunctions)
		{
			byte[] currentAssembly = elfFunction.getAssembly();

			if (Arrays.equals(currentAssembly, assembly))
			{
				return elfFunction;
			}
		}

		throw new IllegalStateException("ELF function not found!");
	}

	public static ELFFunction findByCalledFunctionOffset(List<ELFFunction> elfFunctions, long calledFunctionOffset)
	{
		for (ELFFunction elfFunction : elfFunctions)
		{
			long currentFunctionOffset = elfFunction.getOffsetLong();
			if (currentFunctionOffset == calledFunctionOffset)
			{
				return elfFunction;
			}
		}

		throw new IllegalStateException("ELF function not found!");
	}
}