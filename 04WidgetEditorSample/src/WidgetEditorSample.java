
import java.awt.Color;
import java.lang.reflect.Method;
import editor.BaseEditor;
import editor.WidgetEditor;

import widgets.*;


public class WidgetEditorSample {
	public static void main(String[] args) {
		new WidgetEditorSample();
	}
	
	public WidgetEditorSample(){
		Object newobj=null;
		//debug ColorEditor
		/*
		Class editorClass=null;
		try {
			editorClass=Class.forName("editor.ColorEditor");
			newobj= editorClass.newInstance();
			//setDefaultCloseOperation(DISPOSE_ON_CLOSE );
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		BaseEditor colorEditor=(BaseEditor)newobj;
		colorEditor.returnValue(Color.black);
		try {
			Method disposeMethod= editorClass.getMethod("dispose");
			disposeMethod.invoke(colorEditor, new Object[]{});
		} catch (Exception e){
			
		}
		 */		
		
		
		Class widgetClass=null;
		try {
			String type="DoubleRectangleWidget";
			//String type="RectangleWidget";
			//String type="CircleWidget";
			//String type="JugglerWidget";
			//String type="TimerWidget";
			//String type="StringWidget";
			widgetClass=Class.forName(String.format("widgets.%s",type));
			newobj= widgetClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		final Widget w = (Widget)newobj;		
		System.out.println(String.format("src=%s",w.toCommand()));
		WidgetEditor editor=new WidgetEditor();
		
		//sync
		/*
		Widget ret= editor.returnValue(w);
		if(ret!=null){
			System.out.println(String.format("ret=%s",ret.toCommand()));
			System.out.println(String.format("src=%s",w.toCommand()));
			w.parseCommand(ret.toCommand());
			System.out.println(String.format("src=%s",w.toCommand()));
			w.destroy();
			ret.destroy();
		}
		*/
		
		//async
		EditorCallBack editorCallBack=new EditorCallBack(editor);
		editorCallBack.asyncReturnValue(w, new CallBack() {
			@Override
			public void callback(Object obj) {
				if(obj!=null){
					Widget ret= (Widget)obj;
					System.out.println(String.format("ret=%s",ret.toCommand()));
					System.out.println(String.format("src=%s",w.toCommand()));
					w.parseCommand(ret.toCommand());
					System.out.println(String.format("src=%s",w.toCommand()));
					w.destroy();
					ret.destroy();
				}
			}
		});
		System.out.println("main is End.");
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
