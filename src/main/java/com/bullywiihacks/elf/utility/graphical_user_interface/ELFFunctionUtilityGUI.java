package com.bullywiihacks.elf.utility.graphical_user_interface;

import com.bullywiihacks.elf.utility.assembly.AssemblyValidator;
import com.bullywiihacks.elf.utility.elf.ELFFunction;
import com.bullywiihacks.elf.utility.elf.ELFWrapper;
import com.bullywiihacks.elf.utility.graphical_user_interface.utilities.FileWatcher;
import com.bullywiihacks.elf.utility.graphical_user_interface.utilities.SimpleProperties;
import com.bullywiihacks.elf.utility.graphical_user_interface.utilities.SingleFileChooser;
import net.fornwall.jelf.ElfException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.bullywiihacks.elf.utility.assembly.AssemblyModification.forceCorrectSize;
import static com.bullywiihacks.elf.utility.graphical_user_interface.utilities.PersistentSetting.*;
import static com.bullywiihacks.elf.utility.graphical_user_interface.utilities.SystemClipboard.copy;
import static com.bullywiihacks.elf.utility.graphical_user_interface.utilities.WindowUtilities.setWindowIconImage;
import static com.bullywiihacks.elf.utility.utilities.Conversions.toHexadecimal;
import static java.awt.Toolkit.getDefaultToolkit;
import static java.lang.Thread.sleep;
import static java.nio.file.Files.isRegularFile;
import static javax.swing.JOptionPane.*;
import static javax.swing.SwingUtilities.invokeLater;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

public class ELFFunctionUtilityGUI extends JFrame
{
	private JPanel rootPanel;
	private JButton browseForELFButton;
	private JTextField executableFilePathField;
	private JTable functionsTable;
	private JButton copyMachineCodeButton;
	private JButton aboutButton;
	private JButton resetButton;

	private SingleFileChooser singleFileChooser;

	private int selectedFunctionIndex;
	private ELFFunctionsTableManager elfFunctionsTableManager;
	private SimpleProperties simpleProperties;

	private ELFWrapper elfWrapper;
	private List<Thread> fileWatchers;

	public ELFFunctionUtilityGUI()
	{
		fileWatchers = new ArrayList<>();
		setFrameProperties();
		addExecutableFilePathFieldListener();
		addBrowseELFFileButtonListener();
		addAboutButtonListener();
		addResetButtonListener();
		addCopyMachineCodeButtonListener();

		elfFunctionsTableManager = new ELFFunctionsTableManager(functionsTable);
		elfFunctionsTableManager.configure();

		startButtonAvailabilityMonitoring();
		handlePersistentSettings();
	}

	private void addExecutableFilePathFieldListener()
	{
		executableFilePathField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent documentEvent)
			{
				onExecutableFilePathFieldModified();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent)
			{
				onExecutableFilePathFieldModified();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent)
			{
				onExecutableFilePathFieldModified();
			}
		});
	}

	private void onExecutableFilePathFieldModified()
	{
		selectedFunctionIndex = elfFunctionsTableManager.getSelectedIndex();
		elfFunctionsTableManager.removeAllRows();

		String executableFilePath = executableFilePathField.getText();
		if (isRegularFile(Paths.get(executableFilePath)))
		{
			Thread thread = new Thread(() ->
			{
				try
				{
					String watchFile = executableFilePathField.getText();
					FileWatcher fileWatcher = new MyFileWatcher(watchFile);
					System.out.println("Watching " + executableFilePathField.getText() + "...");
					fileWatcher.watchFile();
				} catch (Exception exception)
				{
					exception.printStackTrace();
				}
			});

			thread.setName("Executable File Watcher");
			fileWatchers.add(thread);

			if (fileWatchers.size() == 1)
			{
				thread.start();
			}
		}
	}

	public class MyFileWatcher extends FileWatcher
	{
		MyFileWatcher(String watchFile)
		{
			super(watchFile);
		}

		@Override
		public void onModified()
		{
			onExecutableFilePathFieldModified();
		}
	}

	private void handlePersistentSettings()
	{
		simpleProperties = new SimpleProperties();
		validateUniqueness();
		restoreSettings();
		addSettingsBackupShutdownHook();
	}

	private void restoreSettings()
	{
		String executableFilePath = simpleProperties.get(EXECUTABLE_FILE_PATH.toString());
		if (executableFilePath != null)
		{
			executableFilePathField.setText(executableFilePath);
		}

		String selectedRowIndex = simpleProperties.get(SELECTED_ROW_INDEX.toString());
		if (selectedRowIndex != null)
		{
			Thread thread = new Thread(() ->
			{
				while (elfFunctionsTableManager.isTableEmpty())
				{
					try
					{
						sleep(10);
					} catch (InterruptedException exception)
					{
						exception.printStackTrace();
					}
				}

				int integerRowIndex = Integer.parseInt(selectedRowIndex);
				if (integerRowIndex < elfFunctionsTableManager.getRowCount() - 1)
				{
					invokeLater(() -> elfFunctionsTableManager.setSelectedRow(integerRowIndex));
				}
			});

			thread.setName("Selected Row Index Restorer");
			thread.start();
		}
	}

	private void addSettingsBackupShutdownHook()
	{
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread(() ->
		{
			String executableFilePath = executableFilePathField.getText();
			simpleProperties.put(EXECUTABLE_FILE_PATH.toString(), executableFilePath);
			int index = elfFunctionsTableManager.getSelectedIndex();
			simpleProperties.put(SELECTED_ROW_INDEX.toString(), index + "");
			simpleProperties.writeToFile();
		}));
	}

	private void addCopyMachineCodeButtonListener()
	{
		copyMachineCodeButton.addActionListener(actionEvent ->
		{
			try
			{
				ELFFunction elfFunction = elfFunctionsTableManager.getSelectedElement();
				byte[] assembly = elfFunction.getAssembly();

				if (elfWrapper.isPowerPC())
				{
					assembly = forceCorrectSize(assembly);
				}

				try
				{
					if (elfWrapper.isPowerPC())
					{
						AssemblyValidator.validate(assembly,
								elfFunction, elfFunctionsTableManager.getFunctions());
					}

					String hexadecimal = toHexadecimal(assembly);
					hexadecimal = hexadecimal.toUpperCase();
					copy(hexadecimal);
					getDefaultToolkit().beep();
					showMessageDialog(this,
							"The machine code of "
									+ elfFunction.getName()
									+ "() has been copied to the clipboard!",
							"Success",
							INFORMATION_MESSAGE);
				} catch (IllegalArgumentException exception)
				{
					showMessageDialog(this,
							exception.getMessage(),
							"Bad Function",
							WARNING_MESSAGE);
				}
			} catch (Exception exception)
			{
				exception.printStackTrace();
				showMessageDialog(this,
						getStackTrace(exception),
						"Error", ERROR_MESSAGE);
			}
		});
	}

	private void addResetButtonListener()
	{
		resetButton.addActionListener(actionEvent ->
		{
			executableFilePathField.setText("");
			onExecutableFilePathFieldModified();
		});
	}

	private void addAboutButtonListener()
	{
		aboutButton.addActionListener(actionEvent ->
				showMessageDialog(this,
						"This application allows you to list functions from an ELF\n" +
								"and copy their executable code to the clipboard.\n\n" +
								"Copyright 2017 \u00A9 BullyWiiPlaza Productions",
						aboutButton.getText(),
						INFORMATION_MESSAGE));
	}

	private void startButtonAvailabilityMonitoring()
	{
		Thread thread = new Thread(() ->
		{
			while (!isShowing())
			{
				try
				{
					sleep(10);
				} catch (InterruptedException exception)
				{
					exception.printStackTrace();
				}
			}

			while (isShowing())
			{
				populateFunctions();
			}
		});

		thread.setName("Button Availability Monitoring");
		thread.start();
	}

	private void populateFunctions()
	{
		boolean isValidFilePath = singleFileChooser.isValidFilePath();

		invokeLater(() ->
		{
			copyMachineCodeButton.setEnabled(isValidFilePath);

			new SwingWorker<String, String>()
			{
				List<ELFFunction> elfFunctions;

				@Override
				protected String doInBackground()
				{
					try
					{
						if (elfFunctionsTableManager.isTableEmpty())
						{
							String executableFilePath;

							try
							{
								executableFilePath = executableFilePathField.getText();
								elfWrapper = new ELFWrapper(executableFilePath);
								elfFunctions = elfWrapper.parseELFFunctions();
							} catch (ElfException ignored)
							{

							}
						}
					} catch (FileNotFoundException ignored)
					{

					} catch (Exception exception)
					{
						exception.printStackTrace();
					}

					return null;
				}

				@Override
				protected void done()
				{
					if (elfFunctions != null)
					{
						if (elfFunctionsTableManager.isTableEmpty())
						{
							elfFunctions.remove(elfFunctions.size() - 1);
							for (ELFFunction elfFunction : elfFunctions)
							{
								elfFunctionsTableManager.addRow(elfFunction);
							}

							if (selectedFunctionIndex != -1
									&& selectedFunctionIndex < elfFunctionsTableManager.getRowCount())
							{
								elfFunctionsTableManager.setSelectedRow(selectedFunctionIndex);
							} else
							{
								elfFunctionsTableManager.selectFirstRow();
							}
						}
					}
				}
			}.execute();
		});

		try
		{
			sleep(10);
		} catch (InterruptedException exception)
		{
			exception.printStackTrace();
		}
	}

	private void addBrowseELFFileButtonListener()
	{
		singleFileChooser = new SingleFileChooser(executableFilePathField);

		browseForELFButton.addActionListener(actionEvent ->
		{
			FileNameExtensionFilter filter = new FileNameExtensionFilter("ELF Files", "elf");
			singleFileChooser.setFileFilter(filter);
			singleFileChooser.allowFileSelection(rootPane);
		});
	}

	private void setFrameProperties()
	{
		add(rootPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("ELF Functions Utility");
		setLocationRelativeTo(null);
		setWindowIconImage(this);
		setSize(600, 400);
	}
}
