package com.bullywiihacks.elf.utility.elf;

import java.io.IOException;
import java.util.List;

public class AssemblyValidator
{
	public static void validate(byte[] assembly, ELFFunction elfFunction, List<ELFFunction> elfFunctions) throws IOException
	{
		for (int assemblyIndex = 0; assemblyIndex < assembly.length; assemblyIndex += PowerPCAssembly.INSTRUCTION_BYTES_LENGTH)
		{
			byte assemblyByte = assembly[assemblyIndex];
			if (assemblyByte == PowerPCAssembly.BRANCH_AND_LINK)
			{
				try
				{
					ELFFunction calledFunction = ELFWrapper.getCalledFunction(elfFunctions, assembly, assemblyIndex);

					throw new IllegalArgumentException("Branch and link (bl instruction) found!\n\n" +
							"Please make sure you do NOT call any sub functions such as " + calledFunction.getName() + "()\n" +
							"from the base function " + elfFunction.getName() + "() when you're trying to convert to a cheat code.\n\n" +
							"This for instance can be achieved easily by marking all sub functions\n" +
							"with \"static inline\" in the source code.");
				} catch (IllegalStateException ignored)
				{
					return;
				}
			}
		}
	}
}