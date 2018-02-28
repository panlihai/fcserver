
package cn.pcorp.util.word;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 *  字体、颜色等设置
 * @author zhangfengtao
 * @version 1.0
 */  
public class Font {
	public static final String FONT_COLOR_BLACK = "0,0,0";
	public static final String FONT_COLOR_BROWN = "1,1,1";
	public static final String FONT_COLOR_RED = "2,2,2";
	public static final String FONT_COLOR_GREEN = "0,255,255";
	public static final String FONT_COLOR_YELLOW = "11,90,12";
	public static final String FONT_COLOR_BLUE = "16025346";

	private int size;
	private String color;
	private String name;
	private boolean bold;
	private boolean italic;
	private boolean underline;
	
	public Font(){
		setdefaultFont();		
	}
	
	public void setdefaultFont(){
		size = 11;
		color = "0,0,0";
		name = "宋体";
		bold = false;
		italic = false;
		underline = false;
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isBold() {
		return bold;
	}
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	public boolean isItalic() {
		return italic;
	}
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	public boolean isUnderline() {
		return underline;
	}
	public void setUnderline(boolean underline) {
		this.underline = underline;
	}
	
	public void setFontStyle(Dispatch selection){
		Dispatch font = Dispatch.get(selection, "Font").toDispatch();
        Dispatch.put(font, "Name", new Variant(name)); 
        Dispatch.put(font, "Bold", new Variant(bold));
        Dispatch.put(font, "Italic", new Variant(italic)); 
        Dispatch.put(font, "Underline", new Variant(underline)); 
        Dispatch.put(font, "Color", color); 
        Dispatch.put(font, "Size", size); 
	}
	
	public void setdefaultStyle(Dispatch selection){
		setdefaultFont();
		setFontStyle(selection);		
	}
	
	//获取标题
	public static Font getFontTitle(int size){
		Font font = new Font();
		font.setBold(true);
		font.setSize(size);
		font.setName("黑体");
		//font.setColor("16025346");
		return font;
	}
	//获取文本内容
	public static Font getFontText(){
		Font font = new Font();
		font.setdefaultFont();
		return font;
	}
}
