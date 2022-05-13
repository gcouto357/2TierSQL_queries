import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class FrontPage {

	protected Shell shell;
	private Text text_Username;
	private Text text_Password;
	private Text text_SQLcommand;
	private Label lblStatus;
	private static MysqlDataSource dataSource = new MysqlDataSource();
	private static MysqlDataSource dataSource2 = new MysqlDataSource();
	String[] columns;
	private Table table_Res;
	private int count_queries = 0;
	private int count_updates = 0;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		java.util.Date date = new java.util.Date();
	    Properties properties = new Properties();
	    FileInputStream filein = null;
	    
	    try (InputStream input = new FileInputStream("root.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("db.url"));
            System.out.println(prop.getProperty("db.user"));
            System.out.println(prop.getProperty("db.password"));
            
            dataSource.setUrl(prop.getProperty("db.url"));
    	    dataSource2.setUrl("jdbc:mysql://localhost:3306/operationslog?useTimezone=true&serverTimezone=UTC");
            
            dataSource2.setUser(prop.getProperty("db.user"));
			dataSource2.setPassword(prop.getProperty("db.password"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
	    
		try {
			FrontPage window = new FrontPage();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(674, 494);
		shell.setText("Project 3 - SQL Client App");
		shell.setLayout(new FormLayout());
		
		Label lblConnectionDetails = new Label(shell, SWT.NONE);
		FormData fd_lblConnectionDetails = new FormData();
		fd_lblConnectionDetails.right = new FormAttachment(0, 117);
		fd_lblConnectionDetails.top = new FormAttachment(0, 10);
		fd_lblConnectionDetails.left = new FormAttachment(0, 10);
		lblConnectionDetails.setLayoutData(fd_lblConnectionDetails);
		lblConnectionDetails.setText("Connection Details");
		
		Label lblUsername = new Label(shell, SWT.NONE);
		FormData fd_lblUsername = new FormData();
		fd_lblUsername.right = new FormAttachment(0, 65);
		fd_lblUsername.top = new FormAttachment(0, 71);
		fd_lblUsername.left = new FormAttachment(0, 10);
		lblUsername.setLayoutData(fd_lblUsername);
		lblUsername.setText("Username");
		
		Label lblPassword = new Label(shell, SWT.NONE);
		FormData fd_lblPassword = new FormData();
		fd_lblPassword.right = new FormAttachment(0, 65);
		fd_lblPassword.top = new FormAttachment(0, 92);
		fd_lblPassword.left = new FormAttachment(0, 10);
		lblPassword.setLayoutData(fd_lblPassword);
		lblPassword.setText("Password");
		
		Label lblPropertiesFile = new Label(shell, SWT.NONE);
		FormData fd_lblPropertiesFile = new FormData();
		fd_lblPropertiesFile.right = new FormAttachment(0, 95);
		fd_lblPropertiesFile.top = new FormAttachment(0, 31);
		fd_lblPropertiesFile.left = new FormAttachment(0, 10);
		lblPropertiesFile.setLayoutData(fd_lblPropertiesFile);
		lblPropertiesFile.setText("Properties  File");
		
		Combo combo_File = new Combo(shell, SWT.NONE);
		combo_File.setItems(new String[] {"root.properties", "client.properties"});
		FormData fd_combo_File = new FormData();
		fd_combo_File.right = new FormAttachment(0, 297);
		fd_combo_File.top = new FormAttachment(0, 31);
		fd_combo_File.left = new FormAttachment(0, 101);
		combo_File.setLayoutData(fd_combo_File);
		combo_File.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		text_Username = new Text(shell, SWT.BORDER);
		FormData fd_text_Username = new FormData();
		fd_text_Username.right = new FormAttachment(0, 228);
		fd_text_Username.top = new FormAttachment(0, 68);
		fd_text_Username.left = new FormAttachment(0, 71);
		text_Username.setLayoutData(fd_text_Username);
		
		text_Password = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		FormData fd_text_Password = new FormData();
		fd_text_Password.right = new FormAttachment(0, 228);
		fd_text_Password.top = new FormAttachment(0, 92);
		fd_text_Password.left = new FormAttachment(0, 71);
		text_Password.setLayoutData(fd_text_Password);
		
		Label lblEnterAnSql = new Label(shell, SWT.NONE);
		FormData fd_lblEnterAnSql = new FormData();
		fd_lblEnterAnSql.right = new FormAttachment(0, 499);
		fd_lblEnterAnSql.top = new FormAttachment(0, 10);
		fd_lblEnterAnSql.left = new FormAttachment(0, 362);
		lblEnterAnSql.setLayoutData(fd_lblEnterAnSql);
		lblEnterAnSql.setText("Enter An SQL Command");
		
		text_SQLcommand = new Text(shell, SWT.BORDER | SWT.WRAP);
		FormData fd_text_SQLcommand = new FormData();
		fd_text_SQLcommand.bottom = new FormAttachment(0, 195);
		fd_text_SQLcommand.right = new FormAttachment(0, 648);
		fd_text_SQLcommand.top = new FormAttachment(0, 31);
		fd_text_SQLcommand.left = new FormAttachment(0, 362);
		text_SQLcommand.setLayoutData(fd_text_SQLcommand);
		
		Button btnClearSqlCommand = new Button(shell, SWT.NONE);
		btnClearSqlCommand.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_SQLcommand.setText("");
			}
		});
		FormData fd_btnClearSqlCommand = new FormData();
		fd_btnClearSqlCommand.right = new FormAttachment(0, 499);
		fd_btnClearSqlCommand.top = new FormAttachment(0, 201);
		fd_btnClearSqlCommand.left = new FormAttachment(0, 372);
		btnClearSqlCommand.setLayoutData(fd_btnClearSqlCommand);
		btnClearSqlCommand.setText("Clear SQL Command");
		
		Button btnExecuteSqlCommand = new Button(shell, SWT.NONE);
		FormData fd_btnExecuteSqlCommand = new FormData();
		fd_btnExecuteSqlCommand.right = new FormAttachment(0, 636);
		fd_btnExecuteSqlCommand.top = new FormAttachment(0, 201);
		fd_btnExecuteSqlCommand.left = new FormAttachment(0, 505);
		btnExecuteSqlCommand.setLayoutData(fd_btnExecuteSqlCommand);
		btnExecuteSqlCommand.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String sql = "update operationscount set num_updates = " + count_updates;
					String sql2 = "update operationscount set num_queries = " + count_queries;
					Connection connection = dataSource.getConnection();
					Statement statement = connection.createStatement();
					
					Connection connection2 = dataSource2.getConnection();
					PreparedStatement statement2 = connection2.prepareStatement(sql);
					
					if(statement.execute(text_SQLcommand.getText()) == false)
					{
						System.out.println("Yur");
						statement.executeUpdate(text_SQLcommand.getText());
						count_updates++;
						statement2.executeUpdate(sql);
						System.out.println(count_updates);
					}
					else
					{
						ResultSet resultSet = statement.executeQuery(text_SQLcommand.getText());
						count_queries++;
						statement2.executeUpdate(sql2);
						System.out.println(count_queries);
						
						JTable table = new JTable(buildTableModel(resultSet));
						table.setBounds(10, 253, 638, 164);
						statement2.execute(sql2);
						
						JOptionPane.showMessageDialog(null, new JScrollPane(table));
					}
					
					connection.close();
					connection2.close();
			      }  // end try
			      catch ( SQLException sqlException ) 
			      {
			    	  JOptionPane.showMessageDialog(null, sqlException);
			      } // end catch
			}
		});
		btnExecuteSqlCommand.setText("Execute SQL Command");
		
		Button btnConnect = new Button(shell, SWT.NONE);
		FormData fd_btnConnect = new FormData();
		fd_btnConnect.right = new FormAttachment(0, 85);
		fd_btnConnect.top = new FormAttachment(0, 130);
		fd_btnConnect.left = new FormAttachment(0, 10);
		btnConnect.setLayoutData(fd_btnConnect);
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataSource.setUser(text_Username.getText());
				dataSource.setPassword(text_Password.getText());
				
				try {
					Connection connection = dataSource.getConnection();
					System.out.println("Yur good");
					
					lblStatus.setText("jdbc:mysql://localhost:3306/project3?useTimezone=true&serverTimezone=UTC");
					
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					text_Username.setText("");
					text_Password.setText("");
				}
			}
		});
		btnConnect.setText("Connect");
		
		lblStatus = new Label(shell, SWT.WRAP);
		FormData fd_lblStatus = new FormData();
		fd_lblStatus.bottom = new FormAttachment(text_SQLcommand, 0, SWT.BOTTOM);
		fd_lblStatus.right = new FormAttachment(0, 333);
		fd_lblStatus.top = new FormAttachment(0, 130);
		fd_lblStatus.left = new FormAttachment(0, 101);
		lblStatus.setLayoutData(fd_lblStatus);
		lblStatus.setText("No Connection");
		
		Label lblSqlExecutionResult = new Label(shell, SWT.NONE);
		FormData fd_lblSqlExecutionResult = new FormData();
		fd_lblSqlExecutionResult.right = new FormAttachment(0, 176);
		fd_lblSqlExecutionResult.top = new FormAttachment(0, 232);
		fd_lblSqlExecutionResult.left = new FormAttachment(0, 10);
		lblSqlExecutionResult.setLayoutData(fd_lblSqlExecutionResult);
		lblSqlExecutionResult.setText("SQL Execution Result Window");
		
		Button btnClearResultWindow = new Button(shell, SWT.NONE);
		btnClearResultWindow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		FormData fd_btnClearResultWindow = new FormData();
		fd_btnClearResultWindow.top = new FormAttachment(0, 423);
		fd_btnClearResultWindow.left = new FormAttachment(0, 10);
		btnClearResultWindow.setLayoutData(fd_btnClearResultWindow);
		btnClearResultWindow.setText("Clear Result Window");
		
		TableViewer tableViewer = new TableViewer(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table_Res = tableViewer.getTable();
		FormData fd_table_Res = new FormData();
		fd_table_Res.right = new FormAttachment(text_SQLcommand, 0, SWT.RIGHT);
		fd_table_Res.bottom = new FormAttachment(lblSqlExecutionResult, 170, SWT.BOTTOM);
		fd_table_Res.top = new FormAttachment(lblSqlExecutionResult, 6);
		fd_table_Res.left = new FormAttachment(lblConnectionDetails, 0, SWT.LEFT);
		table_Res.setLayoutData(fd_table_Res);

	}
	
	
	public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {

	    ResultSetMetaData metaData = rs.getMetaData();
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    
	    for (int column = 1; column <= columnCount; column++) {
	        columnNames.add(metaData.getColumnName(column));
	    }

	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex));
	        }
	        
	        data.add(vector);
	    }
	    return new DefaultTableModel(data, columnNames);
	}
}
