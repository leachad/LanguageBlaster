package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Row;

import resources.FileResource;
import resources.ViewResource;
import file_system.LBFileController;

public class LBMain {

	/** Static field to hold a referencw to a Tookmit.*/
	private static final Toolkit TOOL_KIT = Toolkit.getDefaultToolkit();

	/** Static field to hold a reference to the Gap Constant. */
	private static final int COMPONENT_GAP_WIDTH = 0;
	
	private static final int FRAME_SCALAR = 5;

	/** Static field to hold a header constant. */
	private static final int HEADER_CONSTANT = 5;

	/** Private field to hold a reference to a comma delimiter. */
	private static final String COMMA_DELIMITER = ",";

	/** Private field to hold a reference to a space delimiter. */
	private static final String SPACE_DELIMITER = " ";
	
	private static final String FILTER_TITLE = "Spreadsheet File Types";

	/** Private field to hold a JFrame for showing all of the GUI elements. */
	private JFrame myFrame;

	/** Private field to hold an option tool bar. */
	private OptionToolBar myToolBar;

	/** Field to hold an instance of the file controller. */
	private LBFileController myFileController;

	/** Field to hold a reference to the base panel. */
	private JPanel myBasePanel;
	
	/** Field to hold a reference to the Execute Button.*/
	private JButton myExecuteButton;

	/**
	 * Field to hold a list of rows used for aiding the user in selecting the
	 * row to sort on.
	 */
	private List<Row> myTopRows;

	public LBMain() {

		myFileController = new LBFileController();
		myTopRows = new ArrayList<>(HEADER_CONSTANT);

		start();
	}

	/**
	 * Private method to begin the creation of the methods that read and write
	 * all of the excel sheet data.
	 */
	private void start() {

		myFrame = new JFrame();

		addToolbar();

		addMenu();
		addComponents();

		myFrame.setVisible(true);
		myFrame.setResizable(true);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setPreferredSize(new Dimension((int)TOOL_KIT.getScreenSize().getWidth(), (int)TOOL_KIT.getScreenSize().getHeight() / FRAME_SCALAR));
		myFrame.pack();
		myFrame.setLocationRelativeTo(null);

	}

	/**
	 * Private method to set the toolbar and add appropriate listeners.
	 */
	private void addToolbar() {

		myToolBar = new OptionToolBar(myFrame, myFileController.getDataStack(),
				myFileController.getFileList(), myFileController.getEmailList());
		myFrame.add(myToolBar, BorderLayout.SOUTH);

	}
	
	/**
	 * Private method to add a MenuBar to the GUI using a separately build class.
	 */
	private void addMenu() {
		LBMenu theMenuBar = new LBMenu();
		myFrame.setJMenuBar(theMenuBar);
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
		selectorPanel.setName(ViewResource.FILE_PANEL.text);

		final JButton selectButton = new JButton(
				ViewResource.READ_BOOK_BUTTON.text);
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {
				JFileChooser chooser = new JFileChooser(
						ViewResource.READ_BOOK_BUTTON.text);
				chooser.setFileFilter(getExcelFilter());

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					myFileController.setMonthlyMasterFilePath(chooser
							.getSelectedFile());

					myTopRows = myFileController
							.getPotentialSortingRows(HEADER_CONSTANT);

					updateHeaderPanel();

				}

			}
		});

		selectorPanel.add(selectButton);
		return selectorPanel;
	}

	/**
	 * Private method used to update the header panel used for selecting the
	 * appropriate row on which to sort the data.
	 */
	private void updateHeaderPanel() {
		myBasePanel.add(getHeaderPanel());
		myBasePanel.repaint();
		myBasePanel.revalidate();

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
		executePanel.setName(ViewResource.EXECUTION_PANEL.text);

		myExecuteButton = new JButton(
				ViewResource.RUN_PARSER_BUTTON.text);
		myExecuteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {

				myFileController.readWorkbook();
				myFileController.executeBatchPublish();
				myToolBar.updateToolBar(myFileController.getDataStack(), myFileController.getEmailList(), myFileController.getFileList());
				myFileController.closeSummaryWorkBook();

			}
		});
		executePanel.add(myExecuteButton);
		myExecuteButton.setEnabled(false);

		return executePanel;

	}

	/**
	 * Private method to add the SLA Prompt to the display.
	 * 
	 * @return theSLAPanel
	 */
	private JPanel getSLAPanel() {
		final JPanel slaPanel = new JPanel();
		slaPanel.setName(ViewResource.SLA_PANEL.text);

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
	private JPanel getHeaderPanel() {
		final JPanel headerPanel = new JPanel();
		headerPanel.setName(ViewResource.HEADER_PANEL.text);

		GroupLayout groupLayout = new GroupLayout(headerPanel);
		headerPanel.setLayout(groupLayout);
		ParallelGroup pGroup = groupLayout.createParallelGroup();
		groupLayout.setHorizontalGroup(pGroup);
		SequentialGroup sGroup = groupLayout.createSequentialGroup();
		groupLayout.setVerticalGroup(sGroup);

		ButtonGroup buttonGroup = new ButtonGroup();
		for (int i = 0; i < myTopRows.size(); i++) {
			HeaderRow current = new HeaderRow(i, myTopRows.get(i));
			buttonGroup.add(current.getSelector());
			pGroup.addComponent(current);
			sGroup.addComponent(current, GroupLayout.PREFERRED_SIZE,
					GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
			current.getSelector().addItemListener(new RadioListener(i));
			sGroup.addGap(COMPONENT_GAP_WIDTH);

		}
		
		return headerPanel;
	}
	
	/**
	 * Private method to return a FileNameExtensionFilter consisting of 
	 * common Spreadsheet File Types.
	 */
	private FileNameExtensionFilter getExcelFilter() {
		return new FileNameExtensionFilter(FILTER_TITLE, FileResource.XLS.text, FileResource.XLSX.text, FileResource.ODT.text);
	}

	/**
	 * Private method to add the window components to the JFrame.
	 */
	private void addComponents() {

		myBasePanel = new JPanel();
		myBasePanel.setName(ViewResource.BASE_PANEL.text);
		myBasePanel.setPreferredSize(myFrame.getSize());
		myBasePanel.setBackground(Color.gray);

		myBasePanel.add(getDatePicker());
		myBasePanel.add(getFileSelector());
		myBasePanel.add(getExecutionSelector());
		myBasePanel.add(getSLAPanel());

		myFrame.add(myBasePanel, BorderLayout.CENTER);

	}

	private class RadioListener implements ItemListener {

		private int myIndex;

		private RadioListener(final int theIndex) {
			myIndex = theIndex;
		}

		@Override
		public void itemStateChanged(ItemEvent theEvent) {
			if (theEvent.getStateChange() == ItemEvent.SELECTED) {
				myFileController.setStartIndex(myIndex + 1);
				myExecuteButton.setEnabled(true);
			}

		}

	}

}
