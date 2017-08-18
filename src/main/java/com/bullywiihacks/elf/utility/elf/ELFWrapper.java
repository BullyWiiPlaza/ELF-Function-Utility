package com.bullywiihacks.elf.utility.elf;

import com.bullywiihacks.elf.utility.assembly.PowerPCAssembly;
import com.bullywiihacks.elf.utility.utilities.Conversions;
import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfSection;
import net.fornwall.jelf.ElfSymbol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ELFWrapper
{
	private ElfFile elfFile;
	private String executableFilePath;

	public ELFWrapper(String executableFilePath) throws IOException
	{
		this.executableFilePath = executableFilePath;
		File file = new File(executableFilePath);
		this.elfFile = ElfFile.fromFile(file);
	}

	public boolean isPowerPC()
	{
		short architecture = elfFile.arch;

		// http://www.sco.com/developers/gabi/latest/ch4.eheader.html
		return architecture == 0x20 || architecture == 0x21;
	}

	public List<ELFFunction> parseELFFunctions() throws IOException
	{
		List<ELFFunction> elfFunctions = new ArrayList<>();

		long executableCodeOffset = elfFile.getSection(1).section_offset;
		ElfSection elfSection = elfFile.getSymbolTableSection();
		long symbolsCount = elfSection.getNumberOfSymbols();

		for (int symbolTableIndex = 0; symbolTableIndex < symbolsCount; symbolTableIndex++)
		{
			ElfSymbol elfSymbol = elfSection.getELFSymbol(symbolTableIndex);

			int symbolType = elfSymbol.getType();
			if (symbolType == ElfSymbol.STT_FUNC)
			{
				long functionOffset = executableCodeOffset + elfSymbol.value;
				ELFFunction elfFunction = new ELFFunction(functionOffset, elfSymbol, executableFilePath);
				elfFunctions.add(elfFunction);
			}
		}

		Collections.sort(elfFunctions);

		return elfFunctions;
	}

	public static ELFFunction getCalledFunction(List<ELFFunction> elfFunctions,
	                                            byte[] assembly,
	                                            int assemblyIndex) throws IOException
	{
		ELFFunction callerFunction = ELFFunction.findByAssembly(elfFunctions, assembly);
		long calledFunctionOffset = getCalledFunctionOffset(assembly, assemblyIndex, callerFunction);

		return ELFFunction.findByCalledFunctionOffset(elfFunctions, calledFunctionOffset);
	}

	private static long getCalledFunctionOffset(byte[] assembly, int assemblyIndex, ELFFunction callerFunction)
	{
		int branchOffset = getBranchOffset(assembly, assemblyIndex);
		long callerFunctionOffset = callerFunction.getOffsetLong();

		return callerFunctionOffset + branchOffset + assemblyIndex - 1;
	}

	private static int getBranchOffset(byte[] assembly, int assemblyIndex)
	{
		int startingIndex = assemblyIndex + 1;
		byte[] branchAndLinkOffset = Arrays.copyOfRange(assembly, startingIndex, startingIndex + PowerPCAssembly.INSTRUCTION_BYTES_LENGTH - 1);

		return Conversions.toInteger(branchAndLinkOffset);
	}
}