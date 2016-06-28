package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import model.User;
import security.Authenticator;

public class MDIMenu extends JMenuBar {

	private MDIParent parent;
	
	public MDIMenu(MDIParent p) {
		super();
		
		this.parent = p;
		
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem("Quit");
		
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.doCommand(MenuCommands.APP_QUIT, null);
			}
		});
		
		menu.add(menuItem);
		this.add(menu);
		
		menu = new JMenu("Warehouses");
		menuItem = new JMenuItem("Warehouse & Inventory List");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.doCommand(MenuCommands.SHOW_LIST_WAREHOUSES, null);
			}
		});
		menu.add(menuItem);
		this.add(menu);
		
		menu = new JMenu("Parts");
		menuItem = new JMenuItem("Part List");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.doCommand(MenuCommands.SHOW_LIST_PARTS, null);
			}
		});
		
		menu.add(menuItem);
		this.add(menu);
		
		menu = new JMenu("Login");

		menuItem = new JMenuItem("Bob Roberts");
		JMenuItem menuItem2 = new JMenuItem("Sue Williams");
		JMenuItem menuItem3 = new JMenuItem("Ragnar Jones");
		JMenuItem badLogin = new JMenuItem("Bad Login");
		
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				User user = new User("Bob", "123", "Bob Roberts");
				try {
					parent.setCurrentSession(Authenticator.login(user.getLogin(), user.getPwHash()));
//					parent.setCurrentSession(Authenticator.login(user));
//					System.out.println(Authenticator.login(user));
				} catch (Exception ex ) {
					parent.displayChildMessage(ex.getMessage());
					return;
				}
				parent.displayChildMessage("Logged in as Bob");
			}
			
		});
		
		menuItem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				User user = new User("Sue", "123", "Sue Williams");
				try {
					parent.setCurrentSession(Authenticator.login(user.getLogin(), user.getPwHash()));
				} catch (Exception ex ) {
					parent.displayChildMessage(ex.getMessage());
					return;
				}
				parent.displayChildMessage("Logged in as Sue");
			}
		});
		
		menuItem3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				User user = new User("Ragnar", "123", "Ragnar Jones");
				try {
					parent.setCurrentSession(Authenticator.login(user.getLogin(), user.getPwHash()));
				} catch (Exception ex ) {
					parent.displayChildMessage(ex.getMessage());
					return;
				}
				parent.displayChildMessage("Logged in as Ragnar");
			}
		});
		
		badLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				User user = new User("Bad", "555", "Bad Login");
				try {
					parent.setCurrentSession(Authenticator.login(user.getLogin(), user.getPwHash()));
				} catch(Exception ex) {
					parent.displayChildMessage(ex.getMessage());
					return;
				}
			}
		});
		
		JMenuItem logout = new JMenuItem("Logout");
		logout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		});
		
		
		menu.add(menuItem);
		menu.add(menuItem2);
		menu.add(menuItem3);
		menu.add(badLogin);
		menu.add(logout);
		this.add(menu);
		
		menu = new JMenu("Export");
		
		JMenuItem export = new JMenuItem("Generate Report");
//		JMenuItem excel = new JMenuItem("Generate Excel");
		
		export.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.doCommand(MenuCommands.CREATE_FILE_VIEW, null);
			}
		});
		

//		excel.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				parent.doCommand(MenuCommands.GENERATE_EXCEL, null);
//			}
//		});
		
		
		menu.add(export);
//		menu.add(excel);
		this.add(menu);
		
		
	}
	
	public void logout() {
		Authenticator.logout(parent.getCurrentSession());
		parent.displayChildMessage("Logged out");
	}
}
