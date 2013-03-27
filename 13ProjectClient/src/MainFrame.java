import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.*;

import javax.swing.*;

import editor.BaseEditor;
import editor.WidgetEditor;

import widgets.*;
public class MainFrame extends JFrame{
	private JPanel contentPane = new JPanel();
	  
	public JButton selectBtn=null;
	public JTextField txtField;
	public JTextArea txtArea;
	public JScrollPane scrollPaneWhiteBorad;
	public JPanel paneWhiteBorad ; 
	public JPanel selectPane=null;
	JPanel paneMenu=null;
	JScrollPane scrollMenu=null;
	public int sX=0;
	public int sY=0;
	public Map<Widget,ChatPost> postObj=new LinkedHashMap<Widget,ChatPost>();
	public Map<String,JButton> btns=new LinkedHashMap<String,JButton>(); 
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
		scrollPaneWhiteBorad=new JScrollPane(paneWhiteBorad,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneWhiteBorad.setBounds(0, 0, 500, 300);
		
		
		contentPane.add(scrollPaneWhiteBorad);
		
		
		
		paneMenu=new JPanel();
		/*
		btn1 = new JButton("RectangleWidget");
		paneMenu.add(btn1);		
		btn2 = new JButton("CircleWidget");
		paneMenu.add(btn2);
		btn3 = new JButton("JugglerWidget");
		paneMenu.add(btn3);
		btn4 = new JButton("StringWidget");
		paneMenu.add(btn4);
		*/
		scrollMenu = new JScrollPane(paneMenu,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(scrollMenu);
		addBtn("RectangleWidget");
		addBtn("CircleWidget");
		addBtn("JugglerWidget");
		//addBtn("StringWidget");
		
		txtArea = new JTextArea(8,50);
		JScrollPane scrollPaneTxtArea=new JScrollPane(txtArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);		
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
		
		
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollMenu, 3, SpringLayout.EAST, this.scrollPaneWhiteBorad);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollMenu, -3, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollMenu, 3, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollMenu, 0, SpringLayout.SOUTH, this.scrollPaneWhiteBorad);
		
		
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
	public void addBtn(String widgetName){
		if(btns.containsKey(widgetName))
			return;
		JButton  btn =new JButton(widgetName);
		btns.put(widgetName, btn);
		btn.addActionListener(actionListener);
		paneMenu.add(btn);
		updateMenu();
	}
	void updateMenu(){
		paneMenu.setLayout(new GridLayout(paneMenu.getComponentCount(), 0));
		paneMenu.setPreferredSize(new Dimension(scrollMenu.getWidth()-20,paneMenu.getComponentCount()*50));
		scrollMenu.revalidate();
	}
	public MainFrame(){
		InitializeComponent();
		/*
		btn1.addActionListener(actionListener);
		btn2.addActionListener(actionListener);
		btn3.addActionListener(actionListener);
		btn4.addActionListener(actionListener);
		*/
		txtField.addKeyListener(keyAdapter);
		paneWhiteBorad.addMouseListener(mouseAdapter);
	}

	
	
	KeyAdapter keyAdapter=new KeyAdapter() {
        @Override
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
				if(selectBtn==null)
					return;
				String widgetType="";
				widgetType=selectBtn.getText();
				selectBtn=null;
				final String type=widgetType;
				
				Class widgetClass=null;
				Object newobj=null;
				try {
					widgetClass=Class.forName(String.format("widgets.%s",type));
					newobj= widgetClass.newInstance();
				} catch (Exception e1) {
					e1.printStackTrace();
					return;
				}
				final int wx=e.getX();
				final int wy=e.getY();
				final Widget w = (Widget)newobj;
				WidgetEditor editor=new WidgetEditor();
				EditorCallBack editorCallBack=new EditorCallBack(editor);
				editorCallBack.asyncReturnValue(w, new CallBack() {
					@Override
					public void callback(Object obj) {
						if(obj!=null){
							Widget ret= (Widget)obj;
							if(ChatClient.server==null)
								return;
							if(!ChatClient.server.isLogin)
								return;
							if(ret==null)
								return;
							ret.setLocation(wx,wy);
							ChatClient.server.invokePost(type,ret);
							w.destroy();
							ret.destroy();
						}
					}
				});
				
			}

			//super.mousePressed(e);
		}

		@Override 
		public void mouseClicked(MouseEvent e) {
			if(e.getComponent()!=paneWhiteBorad&&e.getClickCount()==2){
				final ChatPost post=postObj.get(e.getComponent());
				if(post==null)
					return;
				WidgetEditor editor=new WidgetEditor();
				EditorCallBack editorCallBack=new EditorCallBack(editor);
				editorCallBack.asyncReturnValue(post.value, new CallBack() {
					@Override
					public void callback(Object obj) {
						if(obj!=null){
							Widget ret= (Widget)obj;
							if(ChatClient.server==null)
								return;
							if(!ChatClient.server.isLogin)
								return;
							if(ret==null)
								return;
							ChatClient.server.invokeChange(post,ret);
						}
					}
				});
			}
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
		
		@Override
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
			addBtn(post.value.getClass().getSimpleName());
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
			((Widget)post.value).destroy();
		}
	}
	
	public interface CallBack{
		public void callback(Object obj);			
	}
	public class EditorCallBack extends Thread {
		BaseEditor editor=null;
		CallBack callback=null;
		Object value=null;
		public EditorCallBack(BaseEditor editor){
			this.editor=editor;
		}
		@Override
		public void run() {
			Object ret= editor.returnValue(value);
			if(callback!=null)
				callback.callback(ret);
			try {
				Method disposeMethod= editor.getClass().getMethod("dispose");
				disposeMethod.invoke(editor, new Object[]{});
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		public void asyncReturnValue(Object value,CallBack callback){
			this.callback=callback;
			this.value=value;
			this.start();
		}
	}
}

