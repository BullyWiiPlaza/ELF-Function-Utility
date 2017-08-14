package com.bullywiihacks.elf.utility.elf;

import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfSection;
import net.fornwall.jelf.ElfSymbol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
}