package widgets;
import java.awt.*;

public class DoubleRectangleWidget extends Widget{

	private static final long serialVersionUID = 1L;
	private Color innerBackground;
	private Color outerBackground;
	private int	innerWidth;
	private int innerHeight;
	private int outerWidth; 
	private int outerHeight;
	public void setInnerBackground(Color value){this.innerBackground=value; repaint();}
	public void setOuterBackground(Color value){this.outerBackground=value; repaint();}
	public void setInnerWidth(int value)
	{this.innerWidth=Math.abs(value); repaint();}
	public void setInnerHeight(int value)
	{this.innerHeight=Math.abs(value); repaint();}
	public void setOuterWidth(int value)
	{this.outerWidth=Math.abs(value); repaint();}
	public void setOuterHeight(int value)
	{this.outerHeight=Math.abs(value);repaint();}
	
	public Color getInnerBackground(){return this.innerBackground;}
	public Color getOuterBackground(){return this.outerBackground;}
	public int getInnerWidth(){return this.innerWidth;}
	public int getInnerHeight(){return this.innerHeight;}
	public int getOuterWidth(){return this.outerWidth;}
	public int getOuterHeight(){return this.outerHeight;}
	
	public DoubleRectangleWidget(){
		innerBackground=Color.white;
		outerBackground=Color.black;
		innerWidth=50;
		innerHeight=50;
		outerWidth=100;
		outerHeight=100;
	}
	@Override
	public void parseCommand(String cmd) {
		String[] tokens = cmd.split("( )+", 6) ;
		if ( tokens.length != 6 ) return ;
		Color ic,oc= null ;
		int iw,ow,ih,oh ;
		try {
			ic = Color.decode(tokens[0]);
			oc = Color.decode(tokens[1]);
			iw = Integer.parseInt(tokens[2]) ;
			ih= Integer.parseInt(tokens[3]) ;
			ow = Integer.parseInt(tokens[4]) ;
			oh= Integer.parseInt(tokens[5]) ;
		} catch ( Exception e ) {
			return ;
		}
		setInnerBackground(ic);
		setInnerWidth(iw);
		setInnerHeight(ih);
		setOuterBackground(oc);
		setOuterWidth(ow);
		setOuterHeight(oh);
		setSize(ow, oh);
		setBackground(outerBackground);
	}

	@Override
	public String toCommand() {
		 return String.format("%s %s %d %d %d %d",
				getHexColor(innerBackground),getHexColor(outerBackground), innerWidth, innerHeight,
				 outerWidth, outerHeight);
	}
	private String getHexColor(Color cColor)
	{
        return String.format("#%02x%02x%02x", 
        		cColor.getRed(), cColor.getGreen(), cColor.getBlue() ) ;
	}
	@Override
	public void destroy() {
		
		
	}
	public void paint(Graphics g)
    {
		super.paint(g);
        setBackground(outerBackground);
        g.setColor(innerBackground);
        g.fillRect((outerWidth-innerWidth)/2,(outerHeight-innerHeight)/2 , innerWidth, innerHeight);
        setSize(outerWidth,outerHeight);
    }
	
	@Override 
	public void repaint() {
		super.repaint();
	}

}
