package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.SLACount;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;

import resources.ViewResource;
import exceptions.BlasterError;
import file_system.ExcelWriter;
import file_system.LocalStorage;

public class LBMain {

	/** Static field to hold a header constant. */
	private static final int HEADER_CONSTANT = 3;

	/** Private field to hold a JFrame for showing all of the GUI elements. */
	private JFrame myFrame;

	/** Private field to hold the selectedSheet from the Master Workbook. */
	private Sheet myMasterSheet = null;

	/** Private field to hold an option tool bar. */
	private OptionToolBar myToolBar;


	/** Private field to hold an ArrayList of schools. */
	private ArrayList<File> myFileList;


	private ExcelWriter myExcelWriter;

	/** Private field to hold an SLACount object for initiating counting. */
	private SLACount mySLACount;

	public LBMain() {

		myExcelWriter = new ExcelWriter();

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
		myFrame.setPreferredSize(new Dimension(500, 300));
		myFrame.pack();
		myFrame.setLocationRelativeTo(null);

	}

	/**
	 * Private method to set the toolbar and add appropriate listeners.
	 */
	private void addToolbar() {

		myToolBar = new OptionToolBar(myFrame, myExcelWriter.getDataStack(), myFileList);
		myFrame.add(myToolBar, BorderLayout.SOUTH);

	}

	/**
	 * Private method to add the window components to the JFrame.
	 */
	private void addComponents() {

		final JPanel searchPanel = new JPanel();
		searchPanel.setPreferredSize(myFrame.getSize());
		searchPanel.setBackground(Color.gray);

		searchPanel.add(LBDate.addDatePickerWidgets());

		final JTextField displayField = new JTextField();
		displayField.setColumns(10);
		searchPanel.add(displayField);

		final JButton selectButton = new JButton(
				ViewResource.READ_BOOK_BUTTON.text);
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {
				JFileChooser chooser = new JFileChooser(
						ViewResource.READ_BOOK_BUTTON.text);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					myExcelWriter.setMonthlyMasterFilePath(chooser.getSelectedFile());
					displayField.setText(myExcelWriter.getMyParentFolderPath());

					try {
						myExcelWriter.readWorkBookIntoBuffer();

					} catch (InvalidFormatException | IOException e) {

						e.printStackTrace();

					} 

				}

			}
		});
		searchPanel.add(selectButton);

		final JButton runButton = new JButton(
				ViewResource.RUN_PARSER_BUTTON.text);
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {
				if (myMasterSheet != null) {
					try {
						// TODO Get the returned Cell Map and update
						// the sort of rows
						myExcelWriter.parseWorkSheet(myMasterSheet);
						myToolBar.updateDataStack(myExcelWriter.getDataStack());
					} catch (IOException e) {
						JOptionPane.showMessageDialog(myFrame,
								BlasterError.PARSE_ERROR.text);
					}

					if (mySLACount != null) {
						myExcelWriter.closeSummaryWorkBook();
					}
				}

			}
		});
		searchPanel.add(runButton);

		final JComboBox<String> startRowBox = new JComboBox<>(
				LocalStorage.SELECT_DATA_BEGINNING);
		startRowBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {
				myExcelWriter.setStartIndex(Integer.parseInt((String) theEvent.getItem())); 
			}

		});
		startRowBox.setSelectedIndex(0);
		searchPanel.add(startRowBox);

		if (startRowBox.getSelectedIndex() == 0)
			myExcelWriter.setStartIndex(HEADER_CONSTANT);
		
		

		final JCheckBox writeToSLA = new JCheckBox(
				ViewResource.WRITE_SLA_PROMPT.text);
		writeToSLA.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {

				if (theEvent.getStateChange() == ItemEvent.SELECTED) {
					myExcelWriter.createNewOverViewWorkbook(LBDate.myCurrentMonth.concat(" ")
							.concat(LBDate.myCurrentDay).concat(", ")
							.concat(LBDate.myCurrentYear));

				}
			}
		});

		searchPanel.add(writeToSLA);
		myFrame.add(searchPanel, BorderLayout.CENTER);

	}

}
