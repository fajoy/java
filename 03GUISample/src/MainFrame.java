import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
public class MainFrame extends JFrame{
	private JPanel contentPane = new JPanel();
	public JButton btn1;
	public JButton btn2;
	public JButton btn3;	
	public JTextField txtField;
	public JTextArea txtArea;
	public JScrollPane scrollPaneWhiteBorad;
	public JPanel paneWhiteBorad ; 
	public JPanel selectPane=null;
	public int sX=0;
	public int sY=0;
	void InitializeComponent(){
		setTitle("mainFrame");
		setSize(750,600);
		add(contentPane);
		contentPane.setLayout(null);
		
		paneWhiteBorad = new JPanel();
		paneWhiteBorad.setBounds(0, 0, 200, 200);
		
		paneWhiteBorad.setLayout(null);
		paneWhiteBorad.setBackground(Color.white);
		scrollPaneWhiteBorad=new JScrollPane(paneWhiteBorad,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneWhiteBorad.setBounds(0, 0, 500, 300);
		
		
		contentPane.add(scrollPaneWhiteBorad);
		
		Panel pnl = new Panel();
		pnl.setBounds(500, 0, 180, 300);
		contentPane.add(pnl);
		pnl.setLayout(new GridLayout(3, 0, 0, 0));
		btn1 = new JButton("Rectangle");
		pnl.add(btn1);		
		btn2 = new JButton("Circle");
		pnl.add(btn2);
		btn3 = new JButton("Juggler");
		pnl.add(btn3);

		txtArea = new JTextArea(8,50);
		JScrollPane scrollPaneTxtArea=new JScrollPane(txtArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneTxtArea.setBounds(0, 306, 675, 175);
		
		contentPane.add(scrollPaneTxtArea);
		
		txtField = new JTextField();
		txtField.setBounds(95, 487, 590, 23);
		contentPane.add(txtField);
		
		Label label = new Label("Input");
		label.setBounds(20, 487, 69, 23);
		contentPane.add(label);
		
		SpringLayout sl_contentPane = new SpringLayout();
		sl_contentPane.putConstraint(SpringLayout.WEST, this.scrollPaneWhiteBorad, 3, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, this.scrollPaneWhiteBorad, 503, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, this.scrollPaneWhiteBorad, 3, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, this.scrollPaneWhiteBorad, 303, SpringLayout.NORTH, contentPane);
		
		
		sl_contentPane.putConstraint(SpringLayout.WEST, pnl, 3, SpringLayout.EAST, this.scrollPaneWhiteBorad);
		sl_contentPane.putConstraint(SpringLayout.EAST, pnl, -3, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, pnl, 3, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, pnl, 0, SpringLayout.SOUTH, this.scrollPaneWhiteBorad);
		
		
		sl_contentPane.putConstraint(SpringLayout.WEST, label, 3, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, label, 53, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, label,-33, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, label,-3, SpringLayout.SOUTH, contentPane);
		

		sl_contentPane.putConstraint(SpringLayout.WEST, txtField, 0, SpringLayout.EAST, label);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtField, -3, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtField, 0, SpringLayout.NORTH, label);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtField, 0, SpringLayout.SOUTH, label);
		
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPaneTxtArea, 3, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPaneTxtArea, -3, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPaneTxtArea, 3, SpringLayout.SOUTH, this.scrollPaneWhiteBorad);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPaneTxtArea, -3, SpringLayout.NORTH, label);
		
		contentPane.setLayout(sl_contentPane);
		
	}
	public MainFrame(){
		InitializeComponent();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		btn1.addActionListener(actionListener);
		btn2.addActionListener(actionListener);
		btn3.addActionListener(actionListener);
		txtField.addKeyListener(keyAdapter);
	}

	
	
	KeyAdapter keyAdapter=new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          int key = e.getKeyCode();
          if(txtArea.getText().length()>4096)
        	  txtArea.setText("");
          if (key == KeyEvent.VK_ENTER) {
       	   txtArea.append(txtField.getText());
       	   txtArea.append("\n");
				txtField.setText("");
				txtArea.setSelectionStart(txtArea.getText().length());
             }
          }
        };
	
	ActionListener actionListener=new  ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JPanel p=null;
			if(e.getSource()==btn1)
				p=new MovePanel(50,50,Color.red);
			if(e.getSource()==btn2)
				p=new MovePanel(100,100,Color.yellow);
			if(e.getSource()==btn3)
				p=new MovePanel(150,150,Color.green);
			paneWhiteBorad.add(p,0,0);
			paneWhiteBorad.scrollRectToVisible(p.getBounds());
			scrollPaneWhiteBorad.repaint();
		}
	};
	MouseAdapter mouseAdapter=new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getComponent()!=paneWhiteBorad){
			selectPane=(JPanel)e.getSource();
			sX=e.getX();
			sY=e.getY();
			System.out.println("Pressed obj" );
			}

			//super.mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(selectPane==null)
				return;

			paneWhiteBorad.scrollRectToVisible(selectPane.getBounds());
			selectPane=null;
			System.out.println("Released obj");
			scrollPaneWhiteBorad.revalidate();
			//super.mouseReleased(e);
		}

		public void mouseDragged(MouseEvent e) {
			if(selectPane==e.getSource()){
				int x= e.getXOnScreen()-selectPane.getParent().getLocationOnScreen().x-sX;
				int y= e.getYOnScreen()-selectPane.getParent().getLocationOnScreen().y-sY;
				if(x<0)x=0;
				if(y<0)y=0;
				
				System.out.println("x:"+x+" y:"+y );
				selectPane.setLocation(x, y);
				pnlWhiteBoradResize();
				
			}
			
			super.mouseDragged(e);
		};
	};
	
	void pnlWhiteBoradResize(){
		double maxX=1;
		double maxY=1;
		for (int i=0;i<paneWhiteBorad.getComponentCount();i++) {
			JPanel p=(JPanel)paneWhiteBorad.getComponent(i);
			if(maxX<p.getBounds().getMaxX())
				maxX=p.getBounds().getMaxX();
			if(maxY<p.getBounds().getMaxY())
				maxY=p.getBounds().getMaxY();
			
		}
		paneWhiteBorad.setPreferredSize(new Dimension((int)maxX+6,(int)maxY+6));
		scrollPaneWhiteBorad.revalidate();
	}
	
	
	class MovePanel extends JPanel{
		public  MovePanel(int width,int height,Color c){
			setBackground(c);
			setSize(width,height);
			addMouseListener(mouseAdapter);
			addMouseMotionListener(mouseAdapter);
		}
	}
}

