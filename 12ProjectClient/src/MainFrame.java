import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import widgets.*;
public class MainFrame extends JFrame{
	private JPanel contentPane = new JPanel();
	public JButton btn1;
	public JButton btn2;
	public JButton btn3;
	public JButton btn4;
	public JButton selectBtn=null;
	public JTextField txtField;
	public JTextArea txtArea;
	public JScrollPane scrollPaneWhiteBorad;
	public JPanel paneWhiteBorad ; 
	public JPanel selectPane=null;
	public int sX=0;
	public int sY=0;
	public Map<Widget,ChatPost> postObj=new LinkedHashMap<Widget,ChatPost>(); 
	void InitializeComponent(){
		setTitle("mainFrame");
		setSize(750,600);
		addWindowListener(windowAdapter);
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
		pnl.setLayout(new GridLayout(4, 0, 0, 0));
		btn1 = new JButton("Rectangle");
		pnl.add(btn1);		
		btn2 = new JButton("Circle");
		pnl.add(btn2);
		btn3 = new JButton("Juggler");
		pnl.add(btn3);
		btn4 = new JButton("???");
		pnl.add(btn4);
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
		btn1.addActionListener(actionListener);
		btn2.addActionListener(actionListener);
		btn3.addActionListener(actionListener);
		btn4.addActionListener(actionListener);
		txtField.addKeyListener(keyAdapter);
		paneWhiteBorad.addMouseListener(mouseAdapter);
	}

	
	
	KeyAdapter keyAdapter=new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          int key = e.getKeyCode();
          if(txtArea.getText().length()>4096)
        	  txtArea.setText("");
          if (key == KeyEvent.VK_ENTER) {
        	  String line= txtField.getText();
        	  txtField.setText("");
        		  try{
        		 	  ChatClient.server.invokeReadline(line);
        		  }catch (Exception ex) {
        			if(ChatClient.server==null){
        				String cmd = "/connect ";
        				if (cmd.length() < line.length()) {
        					if (line.substring(0, cmd.length()).equals(cmd)) {
        							String msgline = line.substring(cmd.length());
        							int msgi = msgline.indexOf(" ");
        							String strHost = msgline.substring(0, msgi);
        							String strPort = msgline.substring(msgi + 1);
        							int port = Integer.parseInt(strPort);
        							ChatClient.connect(strHost, port);
        					}
        				}
        			}else{    			  
        				MainFrame.this.writeLine("Connect error.");
        			}
				}

             }
          }
        };
	WindowAdapter windowAdapter=new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		};
	};
	ActionListener actionListener=new  ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() instanceof JButton ){
				selectBtn=(JButton)e.getSource();
				return ;
			}
			
			/*
			if(e.getSource()==btn1){
				RectangleWidget r=new RectangleWidget();
				p=r;
				r.setwbHeight(100);
				r.setwbWidth(100);
				r.parseCommand( r.toCommand());
				
			}
			if(e.getSource()==btn2){
				p=new CircleWidget();
			}
			if(e.getSource()==btn3){
				JugglerWidget j=new JugglerWidget();
				j.start();
				p=j;
			}
			p.addMouseListener(mouseAdapter);
			p.addMouseMotionListener(mouseAdapter);
			pnlWhiteBorad.add(p,0,0);
			pnlWhiteBorad.scrollRectToVisible(p.getBounds());
			scrollPane.repaint();
			*/
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
			if(e.getComponent()==paneWhiteBorad){
				Widget w=null;
				if(selectBtn==btn1){
					w=new RectangleWidget();
				}
				if(selectBtn==btn2){
					w=new CircleWidget();
				}
				if(selectBtn==btn3){
					w=new JugglerWidget();
				}
				if(selectBtn==btn4){
					w=new TimerWidget();
				}
				if(w==null)
					return ;
				w.setLocation(e.getX(), e.getY());
				selectBtn=null;
				if(ChatClient.server==null)
					return;
				if(!ChatClient.server.isLogin)
					return;
				ChatClient.server.invokePost(w);
			}

			//super.mousePressed(e);
		}

		@Override 
		public void mouseClicked(MouseEvent e) {
			
		};
		@Override
		public void mouseReleased(MouseEvent e) {
			if(selectPane==null)
				return;
			int x= e.getXOnScreen()-selectPane.getParent().getLocationOnScreen().x-sX;
			int y= e.getYOnScreen()-selectPane.getParent().getLocationOnScreen().y-sY;
			if(x<0)x=0;
			if(y<0)y=0;
			ChatPost post=postObj.get(selectPane);
			if(post==null){
				//System.out.println("x:"+x+" y:"+y );
				selectPane.setLocation(x, y);
				pnlWhiteBoradResize();	
				return;
			}
			if(ChatClient.server==null)
				return;
			if(ChatClient.server.isLeave)
				return;
			ChatClient.server.invokeMove(post,x,y);
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
				selectPane.setLocation(x,y);
				pnlWhiteBoradResize();
				/*
				ChatPost post=postObj.get(selectPnl);
				if(post==null){
					//System.out.println("x:"+x+" y:"+y );
					selectPnl.setLocation(x, y);
					pnlWhiteBoradResize();	
					return;
				}
				if(ChatClient.server==null)
					return;
				if(ChatClient.server.isLeave)
					return;
				ChatClient.server.invokeMove(post,x,y);
				*/
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
		paneWhiteBorad.repaint();
		scrollPaneWhiteBorad.revalidate();
	}
	
	public void writeLine(String msg){
		txtArea.append(msg);
	   	txtArea.append("\n");
		txtArea.setSelectionStart(txtArea.getText().length());
	}
	
	public void flush(){
		
	}
	public void resetWhiteBorad(){
		for (Component c : paneWhiteBorad.getComponents()) {
			paneWhiteBorad.remove(c);
			if(c instanceof Widget)
				((Widget)c).destroy();
		}		
		postObj.clear();
		pnlWhiteBoradResize();
	}
	public void addWidget(ChatPost post){
		if(post.value instanceof Widget){
			Widget w=(Widget)post.value;
			postObj.put(w,post);
			paneWhiteBorad.add(w,0,0);
			pnlWhiteBoradResize();
			if(!ChatClient.server.isLogin)
				return;
			if(!ChatClient.server.userName.equals(post.userName))
				return;
			w.addMouseListener(mouseAdapter);
			w.addMouseMotionListener(mouseAdapter);
		}
		if(post.value instanceof JugglerWidget){
			((JugglerWidget)post.value).repaint();
		}
	}
	public void moveWidget(ChatPost post,int x,int y){
		if(post.value instanceof Widget){
			((Widget)post.value).setLocation(x,y);
			pnlWhiteBoradResize();
		}
		
	}
	public void removeWidget(ChatPost post){
		if(post.value instanceof Widget){
			postObj.remove(post.value);
			paneWhiteBorad.remove((Widget)post.value);
			pnlWhiteBoradResize();
		}
	}
}

