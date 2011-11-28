package ndsr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ndsr.Main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OutOfWorkFrame extends JFrame {
	private static final long serialVersionUID = -2110810928222164586L;
	
	private Main main;
	
	public OutOfWorkFrame(Main m) {
		main = m;
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Ndsr out of work");
		setMinimumSize(new Dimension(450, 180));
		
		JLabel lblYouAreOut = new JLabel("You are out of work now. Do you want to get back to work?");
		lblYouAreOut.setForeground(Color.RED);
		lblYouAreOut.setFont(new Font("Tahoma", Font.BOLD, 12));
		getContentPane().add(lblYouAreOut, BorderLayout.NORTH);

		JButton btnWork = new JButton("Work");
		btnWork.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
				workClicked();
			}
		});
		getContentPane().add(btnWork, BorderLayout.CENTER);
	}
	
	private void workClicked() {
		this.setVisible(false);
		main.setWork(true);
	}
	
	public void showWindow() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		 
		setLocation(x, y);
		setVisible(true);
	}
}
