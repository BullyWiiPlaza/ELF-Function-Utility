package com.bullywiihacks.elf.utility.assembly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class AssemblyModification
{
	public static byte[] process(byte[] assembly) throws IOException
	{
		assembly = stripBlr(assembly);
		assembly = stripStackFrame(assembly);
		assembly = forceCorrectSize(assembly);

		return assembly;
	}

	private static byte[] stripBlr(byte[] assembly)
	{
		if (assembly.length >= PowerPCAssembly.INSTRUCTION_BYTES_LENGTH)
		{
			int blrIndex = 0;
			for (int assemblyIndex = assembly.length - PowerPCAssembly.INSTRUCTION_BYTES_LENGTH; assemblyIndex < assembly.length; assemblyIndex++)
			{
				if (assembly[assemblyIndex] != PowerPCAssembly.BRANCH_TO_LINK_REGISTER[blrIndex])
				{
					return assembly;
				}

				blrIndex++;
			}

			return Arrays.copyOfRange(assembly, 0, assembly.length - PowerPCAssembly.INSTRUCTION_BYTES_LENGTH);
		}

		return assembly;
	}

	public static byte[] forceCorrectSize(byte[] assembly) throws IOException
	{
		if (assembly.length % (PowerPCAssembly.INSTRUCTION_BYTES_LENGTH * 2) != 0)
		{
			assembly = AssemblyModification.concat(assembly);
		}

		return assembly;
	}

	private static byte[] concat(byte[] assembly) throws IOException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(assembly);
		outputStream.write(PowerPCAssembly.NO_OPERATION);

		return outputStream.toByteArray();
	}

	private static byte[] stripStackFrame(byte[] assembly)
	{
		return assembly;
	}
}