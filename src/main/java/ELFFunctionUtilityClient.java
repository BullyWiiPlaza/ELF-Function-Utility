import com.bullywiihacks.elf.utility.graphical_user_interface.ELFFunctionUtilityGUI;

import javax.swing.*;

public class ELFFunctionUtilityClient
{
	public static void main(String[] arguments) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		SwingUtilities.invokeLater(() ->
		{
			ELFFunctionUtilityGUI functionUtilityGUI = new ELFFunctionUtilityGUI();
			functionUtilityGUI.setVisible(true);
		});
	}
}