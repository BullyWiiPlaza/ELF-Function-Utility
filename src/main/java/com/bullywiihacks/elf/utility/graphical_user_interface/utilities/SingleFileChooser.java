package com.bullywiihacks.elf.utility.graphical_user_interface.utilities;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SingleFileChooser extends JFileChooser
{
	private JTextComponent pathTextComponent;
	private boolean isValidFilePath;

	public SingleFileChooser(JTextComponent pathTextComponent)
	{
		this.pathTextComponent = pathTextComponent;
		registerDeleteAction(this);
		startFilePathValidatorThread();

		String currentFilePath = pathTextComponent.getText();
		File currentFile = new File(currentFilePath);

		if (Files.isRegularFile(currentFile.toPath()))
		{
			setCurrentDirectory(currentFile);
		} else
		{
			String workingDirectory = System.getProperty("user.dir");
			File workingDirectoryFile = new File(workingDirectory);
			setCurrentDirectory(workingDirectoryFile);
		}

		setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

	private void startFilePathValidatorThread()
	{
		Thread thread = new Thread(() ->
		{
			while (!pathTextComponent.isShowing())
			{
				try
				{
					Thread.sleep(10);
				} catch (InterruptedException exception)
				{
					exception.printStackTrace();
				}
			}

			while (pathTextComponent.isShowing())
			{
				String filePath = pathTextComponent.getText();
				boolean isRegularFile = Files.isRegularFile(Paths.get(filePath));

				SwingUtilities.invokeLater(() -> pathTextComponent.setBackground(isRegularFile ? Color.GREEN : Color.RED));

				isValidFilePath = isRegularFile;

				try
				{
					Thread.sleep(10);
				} catch (InterruptedException exception)
				{
					exception.printStackTrace();
				}
			}
		});

		thread.setName("File Path Validator");
		thread.start();
	}

	private static void registerDeleteAction(JFileChooser fileChooser)
	{
		AbstractAction abstractAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				JFileChooser jFileChooser = (JFileChooser) actionEvent.getSource();

				try
				{
					File selectedFile = jFileChooser.getSelectedFile();

					if (selectedFile != null)
					{
						int selectedAnswer = JOptionPane.showConfirmDialog(null, "Are you sure want to permanently delete this file?", "Confirm", JOptionPane.YES_NO_OPTION);

						if (selectedAnswer == JOptionPane.YES_OPTION)
						{
							Files.delete(selectedFile.toPath());
							jFileChooser.rescanCurrentDirectory();
						}
					}
				} catch (Exception exception)
				{
					exception.printStackTrace();
				}
			}
		};

		fileChooser.getActionMap().put("delAction", abstractAction);
		fileChooser.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "delAction");
	}

	public void allowFileSelection(JRootPane rootPane)
	{
		int selectedAnswer = showOpenDialog(rootPane);

		if (selectedAnswer == JFileChooser.APPROVE_OPTION)
		{
			String selectedFile = getSelectedFile().getAbsolutePath();
			pathTextComponent.setText(selectedFile);
		}
	}

	public boolean isValidFilePath()
	{
		return isValidFilePath;
	}
}