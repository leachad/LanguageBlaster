package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import resources.ViewResource;
import file_system.LBFileController;
import file_system.LocalStorage;

public class LBMain {

	/** Static field to hold a reference to a Dimension Constant.*/
	private static final int FRAME_DIMENSION = 700;
	
	/** Static field to hold a header constant. */
	private static final int HEADER_CONSTANT = 3;

	/** Private field to hold a reference to a comma delimiter. */
	private static final String COMMA_DELIMITER = ",";

	/** Private field to hold a reference to a space delimiter. */
	private static final String SPACE_DELIMITER = " ";

	/** Private field to hold a JFrame for showing all of the GUI elements. */
	private JFrame myFrame;

	/** Private field to hold an option tool bar. */
	private OptionToolBar myToolBar;

	/** Private field to hold an ArrayList of schools. */
	private ArrayList<File> myFileList;

	/** Field to hold an instance of the file controller.*/
	private LBFileController myFileController;

	public LBMain() {

		myFileController = new LBFileController();

		start();
	}

	/**
	 * Private method to begin the creation of the methods that read and write
	 * all of the excel sheet data.
	 */
	private void start() {

		myFrame = new JFrame();

		addToolbar();

		addComponents();

		myFrame.setVisible(true);
		myFrame.setResizable(true);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setPreferredSize(new Dimension(FRAME_DIMENSION, FRAME_DIMENSION / HEADER_CONSTANT));
		myFrame.pack();
		myFrame.setLocationRelativeTo(null);

	}

	/**
	 * Private method to set the toolbar and add appropriate listeners.
	 */
	private void addToolbar() {

		myToolBar = new OptionToolBar(myFrame, myFileController.getDataStack(),
				myFileList);
		myFrame.add(myToolBar, BorderLayout.SOUTH);

	}

	/**
	 * Private method to add a DatePicker to the main panel.
	 * 
	 * @return theDatePickerWidget
	 */
	private JPanel getDatePicker() {
		return LBDate.addDatePickerWidgets();
	}

	/**
	 * Private method to return the widgets associated with a File Selector.
	 * 
	 * @return theFileSelector
	 */
	private JPanel getFileSelector() {
		final JPanel selectorPanel = new JPanel();
		final JLabel fileLabel = new JLabel(ViewResource.FILE_OUTPUT_LABEL.text);
		selectorPanel.add(fileLabel);
		final JTextField displayField = new JTextField();
		displayField.setColumns(20);
		selectorPanel.add(displayField);

		final JButton selectButton = new JButton(
				ViewResource.READ_BOOK_BUTTON.text);
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {
				JFileChooser chooser = new JFileChooser(
						ViewResource.READ_BOOK_BUTTON.text);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					myFileController.setMonthlyMasterFilePath(chooser
							.getSelectedFile());
					displayField.setText(myFileController
							.getMyParentFolderPath());

					myFileController.readWorkbook();

				}

			}
		});

		selectorPanel.add(selectButton);
		return selectorPanel;
	}

	/**
	 * Private method to add the components necessary to execute the main point
	 * of the application. Parsing the workbook and copying pertinent data to
	 * new excel files.
	 * 
	 * @return theExecutionSelector
	 */
	private JPanel getExecutionSelector() {
		final JPanel executePanel = new JPanel();
		final JButton runButton = new JButton(
				ViewResource.RUN_PARSER_BUTTON.text);
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {
				myFileController.executeBatchPublish();
				myToolBar.updateDataStack(myFileController.getDataStack());

				myFileController.closeSummaryWorkBook();
				

			}
		});
		executePanel.add(runButton);

		return executePanel;

	}

	/**
	 * Private method to add the top 'n' rows to the display so the user can
	 * visually select the appropriate row to sort on.
	 * 
	 * @return theHeaderPanel
	 */
	private JPanel getHeaderPanel() {
		final JPanel headerPanel = new JPanel();
		final JComboBox<String> startRowBox = new JComboBox<>(
				LocalStorage.SELECT_DATA_BEGINNING);
		startRowBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {
				String currentSelection = (String) theEvent.getItem();
				if (!currentSelection
						.equals(LocalStorage.SELECT_DATA_BEGINNING[0]))
					myFileController.setStartIndex(Integer
							.parseInt((String) theEvent.getItem()));
				else
					myFileController.setStartIndex(HEADER_CONSTANT);
			}

		});
		startRowBox.setSelectedIndex(0);
		headerPanel.add(startRowBox);

		if (startRowBox.getSelectedIndex() == 0)
			myFileController.setStartIndex(HEADER_CONSTANT);

		return headerPanel;
	}

	/**
	 * Private method to add the SLA Prompt to the display.
	 * 
	 * @return theSLAPanel
	 */
	private JPanel getSLAPanel() {
		final JPanel slaPanel = new JPanel();
		final JCheckBox writeToSLA = new JCheckBox(
				ViewResource.WRITE_SLA_PROMPT.text);
		writeToSLA.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {

				if (theEvent.getStateChange() == ItemEvent.SELECTED) {
					myFileController.createNewOverViewWorkbook(LBDate
							.getCurrentMonth().concat(SPACE_DELIMITER)
							.concat(LBDate.getCurrentDay())
							.concat(COMMA_DELIMITER + SPACE_DELIMITER)
							.concat(LBDate.getCurrentYear()));

				}
			}
		});

		slaPanel.add(writeToSLA);
		return slaPanel;
	}
	
	/**
	 * Private method used to get Header Widgets.
	 * 
	 * @return theHeaderWidgetPanel
	 */
	private JPanel getHeaderWidgets() {
		return null;
	}

	/**
	 * Private method to add the window components to the JFrame.
	 */
	private void addComponents() {

		final JPanel searchPanel = new JPanel();
		searchPanel.setPreferredSize(myFrame.getSize());
		searchPanel.setBackground(Color.gray);

		searchPanel.add(getDatePicker());
		searchPanel.add(getFileSelector());
		searchPanel.add(getExecutionSelector());
		searchPanel.add(getHeaderPanel());
		searchPanel.add(getSLAPanel());
		searchPanel.add(getHeaderWidgets());

		//TODO Add a MenuBar and more rigid framelayout
		myFrame.add(searchPanel, BorderLayout.CENTER);

	}

}
