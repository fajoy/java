package editor;
import java.awt.*;
import java.beans.*;
import java.lang.reflect.Method;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;

import widgets.Widget;
public class WidgetEditor extends JFrame implements BaseEditor{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane = new JPanel();
	SpringLayout contentPaneLayout = new SpringLayout();
	private Widget srcValue=null;
	private Widget retValue=null;
	private BeanHelper valueHelper=null;
	public JButton btnOk;
	public JButton btnCancel;
	public JScrollPane scrollProperty;
	public JPanel paneProperty ;
	public boolean isOK=false;
	void InitializeComponent(){
		setTitle("WidgetEditor");
		setSize(300,400);
		add(contentPane);
		contentPane.setLayout(null);
		paneProperty = new JPanel();


		scrollProperty=new JScrollPane(paneProperty,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		btnOk = new JButton("ok");
		btnCancel = new JButton("cancel");

		contentPane.add(scrollProperty);
		contentPane.add(btnOk);		
		contentPane.add(btnCancel);
		
		
		
		
		contentPaneLayout.putConstraint(SpringLayout.WEST, this.scrollProperty, 3, SpringLayout.WEST, contentPane);
		contentPaneLayout.putConstraint(SpringLayout.EAST, this.scrollProperty, -3, SpringLayout.EAST, contentPane);
		contentPaneLayout.putConstraint(SpringLayout.NORTH, this.scrollProperty, 3, SpringLayout.NORTH, contentPane);
		contentPaneLayout.putConstraint(SpringLayout.SOUTH, this.scrollProperty, -80, SpringLayout.SOUTH, contentPane);
		
		
		contentPaneLayout.putConstraint(SpringLayout.WEST, this.btnOk, -88, SpringLayout.WEST, btnCancel);
		contentPaneLayout.putConstraint(SpringLayout.EAST, this.btnOk, -8, SpringLayout.WEST, btnCancel);
		contentPaneLayout.putConstraint(SpringLayout.NORTH, this.btnOk, -33, SpringLayout.SOUTH, contentPane);
		contentPaneLayout.putConstraint(SpringLayout.SOUTH, this.btnOk, -3, SpringLayout.SOUTH, contentPane);
		
		contentPaneLayout.putConstraint(SpringLayout.WEST, this.btnCancel, -83, SpringLayout.EAST, contentPane);
		contentPaneLayout.putConstraint(SpringLayout.EAST, this.btnCancel, -3, SpringLayout.EAST, contentPane);
		contentPaneLayout.putConstraint(SpringLayout.NORTH, this.btnCancel, -33, SpringLayout.SOUTH, contentPane);
		contentPaneLayout.putConstraint(SpringLayout.SOUTH, this.btnCancel, -3, SpringLayout.SOUTH, contentPane);
		contentPane.setLayout(contentPaneLayout);
		
	}
	public WidgetEditor(){
		setDefaultCloseOperation(DISPOSE_ON_CLOSE );
		InitializeComponent();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				
				int rows=(paneProperty.getComponentCount()+1)/2;
				paneProperty.setLayout(new GridLayout(rows,1));
				paneProperty.setPreferredSize(new Dimension(scrollProperty.getWidth()-20,rows*20));
				WidgetEditor.this.scrollProperty.revalidate();
				//super.componentResized(e);
			}
		});
		btnOk.addActionListener(actionListener);
		btnCancel.addActionListener(actionListener);
		
		
	}

	
	
	ActionListener actionListener=new  ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {		
			if(e.getSource()==btnOk)
			{
				isOK=true;
			}
			if(e.getSource()==btnCancel)
			{
				isOK=false;
			}
			WidgetEditor.this.setVisible( false );
			WidgetEditor.this.dispose();
		}
	};
	
	public LinkedHashMap<String,JTextField> propertyFileds=new LinkedHashMap<String,JTextField>();
	void updatePropertyField(){
		for(String pname:propertyFileds.keySet()){
			JTextField tf=propertyFileds.get(pname);
			tf.setText(valueHelper.getProperty(pname).toString());
		}
	}
	void addPropertyField(String propertyName,Object value){
		if(value ==null)
			return ;
		final String pname=propertyName;
		final Object pvalue=value;
		if(value instanceof Integer
				||value instanceof Float
				||value instanceof Boolean
				||value instanceof String){
			JLabel l=new JLabel(propertyName);
			paneProperty.add(l);
			JTextField t=new JTextField(value.toString());
			paneProperty.add(t);
			t.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent arg0) {
					JTextField tf=(JTextField)arg0.getSource();
					String text=tf.getText();
					try{
					Object pvalue=valueHelper.getProperty(pname);
			
					if(pvalue instanceof Integer)
						valueHelper.setProperty(pname, Integer.valueOf(text));
					if(pvalue instanceof Float)
						valueHelper.setProperty(pname, Float.valueOf(text));
					if(pvalue instanceof Boolean)
						valueHelper.setProperty(pname, Boolean.valueOf(text));
					if(pvalue instanceof String)
						valueHelper.setProperty(pname, text);
					}catch (Exception e) {
					}
					updatePropertyField();
				}
				
				@Override
				public void focusGained(FocusEvent arg0) {					
				}
			});
			propertyFileds.put(propertyName,t);
			return ;
		}
		
		String typeName=value.getClass().getSimpleName();
		try{
			
		Class editorType=Class.forName(String.format("editor.%sEditor",typeName));
		JLabel l=new JLabel(propertyName);
		final BaseEditor editor=(BaseEditor)editorType.newInstance();
		JButton b=new JButton("...");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditorCallBack editorCallBack=new EditorCallBack(editor);
				editorCallBack.asyncReturnValue(pvalue, new CallBack() {
					@Override
					public void callback(Object obj) {
						if(obj!=null){
							valueHelper.setProperty(pname, obj);
						}
					}
				});
			}
		});
		paneProperty.add(l);
		paneProperty.add(b);

		
		}catch (Exception e) {
			return ;
		}
		
	}
	
	
	@Override
	public Object returnValue(Object curValue) {
		try{
			Widget w=(Widget)curValue;
			return returnValue(w);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public Widget returnValue(Widget curValue) {
		try {
			this.srcValue=curValue;
			Class type=this.srcValue.getClass();
			this.retValue=(Widget)type.newInstance();
			BeanHelper helper=new BeanHelper(srcValue);
			valueHelper=new BeanHelper(this.retValue);
			for(String pname:helper.mapProperty.keySet()){
				Object pvalue=helper.getProperty(pname);
				valueHelper.setProperty(pname, pvalue);
				addPropertyField(pname, pvalue);
			}
			
			
			//debug use review widget
			contentPane.add(this.retValue);
			contentPaneLayout.putConstraint(SpringLayout.WEST, this.retValue, 3, SpringLayout.WEST,contentPane );
			contentPaneLayout.putConstraint(SpringLayout.NORTH, this.retValue, 3, SpringLayout.SOUTH, scrollProperty);
			contentPane.setLayout(contentPaneLayout);
			//demo time please delete
			
			
			this.setVisible(true);
			while(this.isVisible()){
				Thread.sleep(100);
			}
			
			
			if(isOK){
				return this.retValue;
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
	

	public class BeanHelper {
			Object obj = null;
			public LinkedHashMap<String,PropertyDescriptor> mapProperty=new LinkedHashMap<String, PropertyDescriptor>();
			public BeanHelper(Object obj) {
				this.obj = obj;
				BeanInfo info = null;
				try {
					info = Introspector.getBeanInfo(obj.getClass());
				} catch (java.beans.IntrospectionException ex) {
					ex.printStackTrace();
				}
				//找出set或get開頭的方法 
				for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
					//取得的field_name第一個字元一定是小寫
					String field_name = pd.getName(); // get property
					
					if(pd.getPropertyType()!=null){
						if(pd.getReadMethod()!=null&&pd.getWriteMethod()!=null)
						mapProperty.put(field_name, pd);
					}
				}
			}
			
			//得到屬性類別
			public Class getPropertyType(String attributeName){
				PropertyDescriptor pd= mapProperty.get(attributeName);
				if(pd==null)
					return null;
				return pd.getPropertyType();
			}
			
			//取得屬性值
			public Object getProperty(String attributeName){
				PropertyDescriptor pd= mapProperty.get(attributeName);
				if(pd==null)
					return null;
				Method readMethod=pd.getReadMethod();
				if(readMethod==null)
					return null;
				try {
					return readMethod.invoke(this.obj,new Object[]{});
				} catch (Exception e) {
					return null;
				}
			}
			//設定屬性值
			public void setProperty(String attributeName,Object value){
				PropertyDescriptor pd= mapProperty.get(attributeName);
				if(pd==null)
					return;
				Method writeMethod=pd.getWriteMethod();
				if(writeMethod==null)
					return ;
				try {
					writeMethod.invoke(this.obj,value);	
				} catch (Exception e) {
					return ;
				}
			}
		}
}

