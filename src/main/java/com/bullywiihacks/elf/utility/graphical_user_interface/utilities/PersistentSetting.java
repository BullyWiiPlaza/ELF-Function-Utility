package com.bullywiihacks.elf.utility.graphical_user_interface.utilities;

public enum PersistentSetting
{
	EXECUTABLE_FILE_PATH,
	SELECTED_ROW_INDEX;

	@Override
	public String toString()
	{
		return name();
	}

	public static void validateUniqueness()
	{
		PersistentSetting[] persistentSettings = values();

		for (PersistentSetting persistentSetting : persistentSettings)
		{
			int occurrences = 0;

			for (PersistentSetting innerPersistentSetting : persistentSettings)
			{
				if (innerPersistentSetting.toString().equals(persistentSetting.toString()))
				{
					occurrences++;

					if (occurrences > 1)
					{
						throw new IllegalArgumentException(persistentSetting + " defined multiple times!");
					}
				}
			}
		}
	}
}