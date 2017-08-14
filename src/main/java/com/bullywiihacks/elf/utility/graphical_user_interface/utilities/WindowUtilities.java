package com.bullywiihacks.elf.utility.graphical_user_interface.utilities;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Scanner;

public class WindowUtilities
{
	public static void setIconImage(Window window)
	{
		window.setIconImage(Toolkit.getDefaultToolkit().getImage(WindowUtilities.class.getResource("/Icon.png")));
	}

	public static String resourceToString(String filePath) throws IOException, URISyntaxException
	{
		InputStream inputStream = WindowUtilities.class.getClassLoader().getResourceAsStream(filePath);
		return toString(inputStream);
	}

	private static String toString(InputStream inputStream)
	{
		try (Scanner scanner = new Scanner(inputStream).useDelimiter("\\A"))
		{
			return scanner.hasNext() ? scanner.next() : "";
		}
	}
}