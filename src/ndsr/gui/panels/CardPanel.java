package ndsr.gui.panels;

import java.awt.CardLayout;
import java.util.HashMap;

import javax.swing.JPanel;

import ndsr.gui.WelcomeFrame;

public class CardPanel extends JPanel {

	private static final long serialVersionUID = 5628923836937519863L;

	private int length = 0;
	private int current = -1;

	private CardLayout cardLayout;
	
	private HashMap<String, CardChildPanel> map = new HashMap<String, CardChildPanel>();

	public CardPanel(WelcomeFrame welcomeFrame) {
		cardLayout = new CardLayout();
		setLayout(cardLayout);
	}

	public void addPanel(CardChildPanel panel) {
		String name = Integer.toString(length);
		map.put(name, panel);
		add(panel, name);
		if (length == 0) {
			cardLayout.show(this, name);
			current = length;
		}
		length += 1;
	}

	public void next() {
		int next = current + 1;
		if (next < length) {
			refreshPanel(next);
			cardLayout.next(this);
			current = next;
		}
	}

	public void previous() {
		int previous = current - 1;
		if (previous >= 0) {
			refreshPanel(previous);
			cardLayout.previous(this);
			current -= 1;
		}
	}
	
	private void refreshPanel(int index) {
		CardChildPanel cardChildPanel = map.get(Integer.toString(index));
		cardChildPanel.refresh();
	}

	public void first() {
		cardLayout.first(this);
		current = 0;
	}
	
	public void last() {
		cardLayout.last(this);
		current = length - 1;
	}
}
