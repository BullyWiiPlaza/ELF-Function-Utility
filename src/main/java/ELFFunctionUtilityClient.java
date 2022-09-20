import com.bullywiihacks.elf.utility.graphical_user_interface.ELFFunctionUtilityGUI;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

// TODO: Add message:
// Note: Please prefer the "IDA Pro ASM File Parser" in JGecko U to get the (compiled) assembly source.
// This is because symbol references (mainly floats and doubles) are not handled by "ELF Utility"
// so it will result in a crash if ran. If you know that there are no such issues, you may keep using this without any problems though.
// TODO: Thread Violation Checker
// TODO Update 2017 - 2018 Copyright
public class ELFFunctionUtilityClient
{
	public static void main(String[] arguments) throws Exception
	{
		setLookAndFeel(getSystemLookAndFeelClassName());

		invokeLater(() ->
		{
			ELFFunctionUtilityGUI functionUtilityGUI = new ELFFunctionUtilityGUI();
			functionUtilityGUI.setVisible(true);
		});
	}
}
