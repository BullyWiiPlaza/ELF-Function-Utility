package com.bullywiihacks.elf.utility.assembly;

public class PowerPCAssembly
{
	public static final int INSTRUCTION_BYTES_LENGTH = 4;
	static final byte BRANCH_AND_LINK = 0x4B;
	static final byte[] NO_OPERATION = {0x60, 0x00, 0x00, 0x00};
	static final byte[] BRANCH_TO_LINK_REGISTER = {0x4E, (byte) 0x80, 0x00, 0x20};
}