
package cn.pcorp.util.word;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class DocumentBuilder {
	private Document document;
	
	public DocumentBuilder(){
		document = new Document();
	}
	
	public DocumentBuilder(Document documnet){
		this.document = documnet;
	}
	
	/**
	 * 插入文本
	 * @param text
	 */
	public void insert(String text){
		Dispatch.put(document.getSelection(), "Text", text);
		Dispatch.call(document.getSelection(), "MoveDown");
	}

	/**
	 * 插入文本
	 * @param 文本
	 * @param 字体样式
	 */
	public void insert(String text,Font font){
		setFontStyle(font);
		Dispatch.put(document.getSelection(), "Text", text);
		Dispatch.call(document.getSelection(), "MoveDown");
	}
	
	/** 
     * 插入图片
     * @param 图片路径 
     */ 
    public void insertImage(String imagePath) { 
       Dispatch.call(Dispatch.get(document.getSelection(), "InLineShapes").toDispatch(), 
               "AddPicture", imagePath); 
	}
	
	/**
	 * 插入文本并换行
	 * @param text
	 */
	public void insertln(String text){
		Dispatch.put(document.getSelection(), "Text", text);
		Dispatch.call(document.getSelection(), "MoveDown");
		insertln();
	}
	
	/**
	 * 插入文本并换行
	 * @param text
	 */
	public void insertln(String text,Font font){
		setFontStyle(font);
		Dispatch.put(document.getSelection(), "Text", text);
		Dispatch.call(document.getSelection(), "MoveDown");
		insertln();
	}
	
	/**
	 * 插入回车
	 */
	public void insertln(){
		Dispatch.call(document.getSelection(), "TypeParagraph"); 
		Dispatch.call(document.getSelection(), "MoveDown");
	}
	
	/**
     * 查找替换文本
     * @param searchText 需要查找的关键字
     * @param replaceText 要替换成的关键字
     */
	public void replace(final String searchText,final String replaceText) {
		Dispatch selection = document.getSelection();
		Dispatch find = ActiveXComponent.call(selection, "Find").toDispatch();
        Dispatch.put(find, "Text", searchText);
        Dispatch.call(find,"ClearFormatting");
        Dispatch.put(find, "Text", searchText);
        Dispatch.call(find, "Execute");
        Dispatch.put(selection, "Text", replaceText);
	}

	/**
	 * 插入书签
	 */
	public void insertBookMark(String bookMarkText){
		ActiveXComponent objWord = document.getObjWord();
		Dispatch activeDocument=objWord.getProperty("ActiveDocument").toDispatch();    
		Dispatch bookMarks = Dispatch.call(activeDocument, "Bookmarks").toDispatch();
		Dispatch.call(bookMarks, "Add", bookMarkText, document.getSelection());  
		Dispatch.call(document.getSelection(), "MoveDown");
	}
	
	/**
	 * 插入书签(不向后移动)
	 */
	public void insertBookMarkNoD(String bookMarkText){
		ActiveXComponent objWord = document.getObjWord();
		Dispatch activeDocument=objWord.getProperty("ActiveDocument").toDispatch();    
		Dispatch bookMarks = Dispatch.call(activeDocument, "Bookmarks").toDispatch();
		Dispatch.call(bookMarks, "Add", bookMarkText, document.getSelection()); 
	}
	
	public void insertHyperlink(String text,String subAddress,String addressValue){
		Dispatch Hyperlinks = Dispatch.get(document.getDocument(), "Hyperlinks").toDispatch(); 
		Dispatch range = Dispatch.get(document.getSelection(), "Range").toDispatch(); 
		Dispatch hyperlink=Dispatch.invoke(Hyperlinks, 
		"Add", Dispatch.Method, new Object[] 
		{ range, 
		new Variant(""), 
		new Variant(subAddress), 
		new Variant(addressValue),//建议的数据链接处 
		new Variant(text) }, // 在WORD中显示的内容 
		new int[1]).toDispatch(); 
		//Dispatch hRange=Dispatch.get(h, "Range").toDispatch(); 
		//Dispatch.call(hRange,"select"); 
	}
	
	
	/** 
     * 对当前段落进行格式化 
     *  
     * @param align 
     *            设置排列方式 默认：居左 0:居左 1:居中 2:居右 3:两端对齐 4:分散对齐 
     * @param lineSpace 
     *            设置行间距 默认：1.0 0：1.0 1：1.5 2：2.0 3：最小值 4：固定值 
     */ 
    public void setParaFormat(int align, int lineSpace) { 
        if (align < 0 || align > 4) { 
            align = 0; 
        } 
        if (lineSpace < 0 || lineSpace > 4) { 
            lineSpace = 0; 
        }
        Dispatch alignment = Dispatch.get(document.getSelection(), "ParagraphFormat") 
                .toDispatch(); 
        Dispatch.put(alignment, "Alignment", align); 
        Dispatch.put(alignment, "LineSpacingRule", new Variant(lineSpace)); 
    }
    
    /** 
     * 还原段落默认的格式 左对齐,行间距：1.0 
     */ 
    public void clearParaFormat() { 
        this.setParaFormat(0, 0); 
    } 
	
	public void setFontStyle(Font font){
		font.setFontStyle(document.getSelection());
	}
	
	/** 
     * 按下End键 （转到本段结尾）
     */ 
    public void end() { 
        Dispatch.call(document.getSelection(), "EndKey", "5"); 
    } 
    
    /** 
     * 按下Home键 （转到本段开头）
     */ 
    public void begin() { 
        Dispatch.call(document.getSelection(), "HomeKey", "5"); 
    } 
    
    /** 
     * 按下Ctrl + End键(转到文档结尾) 
     */ 
    public void goToEnd() { 
        Dispatch.call(document.getSelection(), "EndKey", "6"); 
    }
    
    /** 
     * 按下Ctrl + Home键 
     */ 
    public void goToBegin() { 
        Dispatch.call(document.getSelection(), "HomeKey", "6"); 
    } 


}
