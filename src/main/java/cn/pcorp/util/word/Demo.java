
package cn.pcorp.util.word;

import cn.pcorp.util.word.Document;
import cn.pcorp.util.word.DocumentBuilder;
import cn.pcorp.util.word.Font;
import cn.pcorp.util.word.Table;




public class Demo {
	public static void main(String[] arg){
		// System.out.println(System.getProperty("java.library.path"));
		for(int i=0;i<1;i++){
			System.out.println("---开始生成---");
			Document document = new Document();
			bulidWord(document);
			//document.saveAsHtml("d:\\660000000000000087_660000000000000008"+i+".html");
			document.saveAs("d:\\660000000000000087_660000000000000008"+i+".doc");
			document.close();
			document.releaseCom();
			System.out.println("---生成成功,很帅吧!---");
//			try {
//				wordToPdf("d:\\test.pdf","d:\\660000000000000087_660000000000000008"+i+".html");
//			} catch (Exception e) {
//
//				e.printStackTrace();
//			}
//			System.out.println("---转换成功!---");
		
		}
	}	
	
	public static void bulidWord(Document document){
		DocumentBuilder builder = new DocumentBuilder(document);
		
		builder.insert("这是标题",getFontTitle());
		builder.insertln();
		builder.insert("这个是正文",getFontText());
		builder.insertln("继续输出");
		builder.insertBookMark("B660000000000000087_660000000000000008_L");
		builder.insert("标签中的内容");
		builder.insertBookMark("B660000000000000087_660000000000000008_R");
		builder.insertln();
		//builder.insertImage("d:\\testword.jpg");
		Table table = new Table(document,5,5);
		table.setTableBorderWidth(0.5);
		table.setCellValue(2, 2, "2");
		table.setCellValue(3, 3, "3asdfasdasdasdasdksdafkjasflslkafkjl");
		//table.setColumnWidth(1,150);
		builder.goToEnd();
		builder.insertln("继续输出");
		builder.insertHyperlink("测试连接","B660000000000000087_660000000000000008_L","B660000000000000087_660000000000000008_L");
	}
	
	public static Font getFontTitle(){
		Font font = new Font();
		font.setBold(true);
		font.setSize(18);
		font.setName("黑体");
		font.setColor("16025346");
		return font;
	}
	
	public static Font getFontText(){
		Font font = new Font();
		font.setdefaultFont();
		return font;
	}
//	
//	private static void wordToPdf(String pdfFile,String htmlFile) throws Exception{
//		File file = new File(pdfFile);
//		FileInputStream input = new  FileInputStream(htmlFile);
//		com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4, 50, 50, 50, 50);
//		FileOutputStream out = new FileOutputStream(file);
//	    PdfWriter writer = PdfWriter.getInstance(document, out);
////	    
////	    SAXParser   parser   =   SAXParserFactory.newInstance().newSAXParser();   
//// 
////        parser.parse(htmlFile,   new   SAXmyHtmlHandler(document));
//        
//        HtmlParser.parse(document,input);
//	}
}
