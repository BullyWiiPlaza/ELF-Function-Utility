package com.bullywiihacks.elf.utility.graphical_user_interface.utilities;

import java.io.File;
import java.net.URISyntaxException;

public class ProgramDirectoryUtilities
{
	private static final String JAR_EXTENSION = "jar";

	private static String getJarName()
	{
		return new File(ProgramDirectoryUtilities.class.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath())
				.getName();
	}

	private static boolean runningFromJAR()
	{
		String jarName = getJarName();
		return jarName.contains("." + JAR_EXTENSION);
	}

	public static String getProgramDirectory()
	{
		if (runningFromJAR())
		{
			return getCurrentJARDirectory();
		} else
		{
			return getCurrentProjectDirectory();
		}
	}

	private static String getCurrentProjectDirectory()
	{
		return new File("").getAbsolutePath();
	}

	private static String getCurrentJARDirectory()
	{
		try
		{
			return new File(ProgramDirectoryUtilities.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
		} catch (URISyntaxException exception)
		{
			exception.printStackTrace();
		}

		return null;
	}

	public static boolean isRunningFromIntelliJ()
	{
		String classPath = System.getProperty("java.class.path");
		return classPath.contains("idea_rt." + JAR_EXTENSION);
	}
}