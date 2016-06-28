//package view;
//
//import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
//
//import controller.MDIChild;
//import controller.MDIParent;
//import model.User;
//import security.Authenticator;
//
//public class LoginView extends MDIChild {
//
//	private MDIParent parent;
//	
//	public LoginView(String title, MDIParent parent) {
//		super(title, parent);
//		
//		this.parent = parent;
//		
//		LoginMenu lMenu = new LoginMenu(parent);
//		this.parent.setJMenuBar(lMenu);
//		
//		this.setPreferredSize(new Dimension(150, 150));
//	}
//}
//
//class LoginMenu extends JMenuBar {
//	private MDIParent parent;
//	
//	LoginMenu(MDIParent parent) {
//		super();
//		
//		this.parent = parent;
//		
//		JMenu menu = new JMenu("Login");
//		JMenuItem menuItem = new JMenuItem("Bob Roberts");
//		JMenuItem menuItem2 = new JMenuItem("Sue Williams");
//		JMenuItem menuItem3 = new JMenuItem("Ragnar Jones");
//		JMenuItem badLogin = new JMenuItem("Bad Login");
//		
//		menuItem.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				User user = new User("Bob", "123", "Bob Roberts");
//				try {
//					Authenticator.login(user);
//				} catch (Exception ex ) {
//					parent.displayChildMessage(ex.getMessage());
//					return;
//				}
//				parent.displayChildMessage("Success");
////				parent.doCommand(MenuCommands.USER_LOGIN, null);
//			}
//			
//		});
//		
//		menuItem2.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				User user = new User("Sue", "1235", "Sue Williams");
//				System.out.println(user.getPwHash());
//				try {
//					Authenticator.login(user);
//				} catch (Exception ex ) {
//					parent.displayChildMessage(ex.getMessage());
//					return;
//				}
//				parent.displayChildMessage("Success");
////				parent.doCommand(MenuCommands.USER_LOGIN, null);
//			}
//		});
//		
//		menuItem3.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				User user = new User("Ragnar", "123", "Ragnar Jones");
//				try {
//					Authenticator.login(user);
//				} catch (Exception ex ) {
//					parent.displayChildMessage(ex.getMessage());
//					return;
//				}
//				parent.displayChildMessage("Success");
////				parent.doCommand(MenuCommands.USER_LOGIN, null);
//			}
//		});
//		
//		menu.add(menuItem);
//		menu.add(menuItem2);
//		menu.add(menuItem3);
//		menu.add(badLogin);
//		
//		this.add(menu);
//		
//	}
//}
