package br.com.binpacking.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import br.com.binpacking.domain.Container;
import br.com.binpacking.domain.Item;

public class DrawContainer extends JComponent {

	private Container container = null;
	private final int SCALE = 5;
	private static final long serialVersionUID = 1L;

	public DrawContainer() {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
		this.setPreferredSize(new Dimension(600, 600));
		frame.getContentPane().add(this, BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public void drawing(Container container){
		//this.clear();
		this.container= container;
		repaint();
	}
	
	public void clear(){
		if(container != null && container.getItems() != null){
			this.container.getItems().clear();
			repaint();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	
		if(container != null && container.getItems() != null){
			List<Item> items = new ArrayList<>();
			items.addAll(container.getItems());
			
			g.drawString("Container items: " +  container.getItems().size(), 5 * SCALE, (container.getMeasures().length().intValue() + 10) * SCALE);
			g.drawString("Container sizes: [ Width: " + container.getMeasures().width() + ", Length: " + container.getMeasures().length() + ", Height: " + container.getMeasures().height() + " ]", 5 * SCALE, (container.getMeasures().length().intValue() + 14) * SCALE);
			g.drawString("--------------------------------------------------------------------------------------------------------------", 5 * SCALE, (container.getMeasures().length().intValue() + 16) * SCALE);
			//int count = 20;
			for (Item item : items) {
//				Color randomColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
				g.drawRect(item.getPoint().width().begin().intValue() * SCALE, item.getPoint().length().begin().intValue() * SCALE, item.getWidth().intValue() * SCALE, item.getLength().intValue() * SCALE);  
				g.setColor(Color.BLACK); 
//				g.setColor(randomColor); 
			    //g.fillRect(item.getPoint().getWidth().getBegin().intValue() * SCALE, item.getPoint().getLength().getBegin().intValue() * SCALE, item.getWidth().intValue() * SCALE, item.getLength().intValue() * SCALE);
				
				//g.drawString(item.toString(), 5 * SCALE, (container.getLength().intValue() + count) * SCALE);
				//count += 4;
			}
			
		}
	}
	
	
}
