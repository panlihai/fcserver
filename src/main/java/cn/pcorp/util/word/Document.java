
package cn.pcorp.util.word;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;


public class Document {
	private  ActiveXComponent objWord;
	private Dispatch wordObject;
	private Dispatch document;
	private Dispatch selection;
	private boolean visible = false;
	
	public Document(){
		initCom();
		objWord = new ActiveXComponent("Word.Application");
		wordObject = objWord.getObject();
	    Dispatch documents = Dispatch.get(objWord,"Documents").toDispatch();
	    document = Dispatch.call(documents,"Add").toDispatch();
	    selection = Dispatch.get(objWord, "Selection").toDispatch();
	}
	
	public Document(String fileName){
		initCom();
		visible = false;
		objWord = new ActiveXComponent("Word.Application");
		wordObject = objWord.getObject();
		Dispatch.put(wordObject, "Visible", new Variant(visible));
		Dispatch documents = objWord.getProperty("Documents").toDispatch();
		document = Dispatch.call(documents, "Open", fileName).toDispatch();
		selection = Dispatch.get(objWord, "Selection").toDispatch();
	}
	
	public Document(String fileName,boolean visible){
		initCom();
		setVisible(visible);
		objWord = new ActiveXComponent("Word.Application");
		wordObject = objWord.getObject();
		Dispatch.put(wordObject, "Visible", new Variant(visible));
		Dispatch documents = objWord.getProperty("Documents").toDispatch();
		document = Dispatch.call(documents, "Open", fileName).toDispatch();
		selection = Dispatch.get(objWord, "Selection").toDispatch();
	}
	
	/**
	 * 打开
	 * @param 文件名(包括绝对路径)
	 */
	public void open(String fileName){
		if(objWord==null){
			objWord = new ActiveXComponent("Word.Application");
		}
		wordObject = objWord.getObject();
		Dispatch.put(wordObject, "Visible", new Variant(visible));
		Dispatch documents = objWord.getProperty("Documents").toDispatch();
		document = Dispatch.call(documents, "Open", fileName).toDispatch();
		selection = Dispatch.get(objWord, "Selection").toDispatch();
	}
	
	/** 
     * 从选定内容或插入点开始查找文本 
     *  
     * @param toFindText 
     *            要查找的文本 
     * @return boolean true-查找到并选中该文本，false-未查找到文本 
     */ 
    public boolean find(String toFindText) { 
        if (toFindText == null || toFindText.equals("")) 
            return false; 
        // 从selection所在位置开始查询 
        Dispatch find = objWord.call(selection, "Find").toDispatch(); 
        // 设置要查找的内容 
        Dispatch.put(find, "Text", toFindText); 
        // 向前查找 
        Dispatch.put(find, "Forward", "True"); 
        // 设置格式 
        Dispatch.put(find, "Format", "True"); 
        // 大小写匹配 
        Dispatch.put(find, "MatchCase", "True"); 
        // 全字匹配 
        Dispatch.put(find, "MatchWholeWord", "True"); 
        // 查找并选中 
        return Dispatch.call(find, "Execute").getBoolean(); 
    }
	
	/**
	 * 保存
	 */
	public void save() {
		Dispatch.call(document, "Save");
		close();
	}
	
	/**
	 * 另存为。。
	 * @param 文件名(包括绝对路径)
	 */
	public void saveAs(String fileName) {
		Dispatch.invoke(document, "SaveAs", Dispatch.Method, new Object[] {fileName, new Variant(0)} , new int[1]);
		close();
	}
	
	/**
	 * 另存为HTML文件
	 * @param 文件名(包括绝对路径)
	 */
	public void saveAsHtml(String fileName) {
		Dispatch.invoke(document, "SaveAs", Dispatch.Method, new Object[] {fileName, new Variant(10)} , new int[1]);  
		close();
	}
	
	/**
	 * 关闭
	 */
	public void close(){
		closeDocument();
		closeWord();
	}
	
	private void closeWord() {
		if(objWord != null){
		    Dispatch.call(objWord,"Quit");
		    objWord = null;
		}
	}
	
	private void closeDocument() {
		if(document != null){
		    Dispatch.call(document, "Close", new Variant(0));
			document = null;
		}
	}
	
	/**
	 * 打印
	 */
	public void print() {
		if(document != null)
			Dispatch.call(document,"PrintOut");
	}
	/** 
     * 初始化com线程
     */ 
	public void initCom() {
		ComThread.InitSTA();
	}
	
	/** 
     * 释放 com 线程资源 com 的线程回收不由 java 垃圾回收机制回收 
     */ 
    public void releaseCom() { 
        ComThread.Release(); 
    }
    
	public Dispatch getSelection() {
		return selection;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public ActiveXComponent getObjWord() {
		return objWord;
	}

	public Dispatch getDocument() {
		return document;
	}
	
	 
}
