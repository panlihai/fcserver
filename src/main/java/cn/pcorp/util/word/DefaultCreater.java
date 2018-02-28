
package cn.pcorp.util.word;

public abstract class DefaultCreater {
	public static final String BOOKMARK_COMPART="_";
	
	public void createFile(String svaeFilePath){
		Document document = null;
		try{
			document = new Document();
			bulidWord(document);
			document.saveAs(svaeFilePath);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(document!=null){
				document.close();
				document.releaseCom();
			}
		}
	}
	
	protected Font getTitleFont(){
		Font font = new Font();
		font.setSize(18);
		font.setBold(true);
		font.setName("黑体");
		return font;
	}
	
	protected Font getTitleHead(){
		Font font = new Font();
		font.setSize(14);
		font.setBold(true);
		return font;
	}
	
	protected Font getdefaultFont(){
		return new Font();
	}
	
	protected abstract void bulidWord(Document document);
	
	
}
