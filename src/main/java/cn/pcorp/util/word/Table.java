
package cn.pcorp.util.word;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;


public class Table {
	private Dispatch table;
	private Document document;
	
	public Table(Document document,int numRows,int numCols){
		Dispatch tables = Dispatch.get(document.getDocument(), "Tables").toDispatch(); 
		
        Dispatch range = Dispatch.get(document.getSelection(), "Range").toDispatch(); 
        Dispatch newTable = Dispatch.call(tables, "Add", range, 
                new Variant(numRows), new Variant(numCols)).toDispatch(); 
        Dispatch.call(document.getSelection(), "MoveRight");
        table = newTable;
        this.document = document;
	}
	
	public Table(Document document,int tableIndex){
		 Dispatch tables = Dispatch.get(document.getDocument(), "Tables").toDispatch(); 
		 Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)) 
	                .toDispatch(); 
		 this.table = table;
		 this.document = document;
	}
	
	/** 
     * 增加一列 
     */ 
    public void addCol() {  
        // 表格的所有行 
        Dispatch cols = Dispatch.get(table, "Columns").toDispatch(); 
        Dispatch.call(cols, "Add").toDispatch(); 
        Dispatch.call(cols, "AutoFit"); 
    } 
    
    /** 
     * 增加一行 
     *  
     * @param tableIndex 
     *            word文档中的第N张表(从1开始) 
     */ 
    public void addRow() {  
        // 表格的所有行 
        Dispatch rows = Dispatch.get(table, "Rows").toDispatch(); 
        Dispatch.call(rows, "Add"); 
    } 

    /** 
     * 设置当前表格线的粗细
     * 
     * @param w 
     */  
    public void setTableBorderWidth(double w) { 
    	if (w > 1) { 
    		w = 0.5;
    	}
    	Dispatch borders = Dispatch.get(table, "Borders").toDispatch(); 
    	Dispatch border = null; 
    	for (int i = 1; i < 7; i++) { 
    		/** 设置表格线的粗细 1：代表最上边一条线 2：代表最左边一条线 3：最下边一条线 4：最右边一条线 5：除最上边最下边之外的所有横线 
    	     * 6：除最左边最右边之外的所有竖线 7：从左上角到右下角的斜线 8：从左下角到右上角的斜线 
    	     */ 
    		border = Dispatch.call(borders, "Item", new Variant(i)).toDispatch(); 
    		Dispatch.put(border, "LineWidth", new Variant(w)); 
    		Dispatch.put(border, "Visible", new Variant(true)); 
    	} 
    }
    
    /**
     * 在指定的单元格里填写数据
     *
     * @param rowIndex
     * @param colIndex
     * @param text
     */
    public void setCellValue(int rowIndex, int colIndex,
            String text) {
        Dispatch cell = Dispatch.call(table, "Cell", new Variant(rowIndex),
                new Variant(colIndex)).toDispatch();
        Dispatch.call(cell, "Select");
        //Dispatch selection = Dispatch.get(document.getObjWord(), "Selection").toDispatch();
        Dispatch.call(document.getSelection(), "TypeText", text);
    }
    
    /** 
     * 设置指定表格指定列的列宽 
     *  
     * @param tableIndex 
     * @param columnWidth 
     * @param columnIndex 
     */ 
    public void setColumnWidth(int columnIndex,float columnWidth) { 
    	Dispatch.put(getColumn(columnIndex), "Width", new Variant(columnWidth)); 
    } 

    /** 
     * 得到当前表格的某一列 
     *  
     * @param index
     * @return 
     */ 
    public Dispatch getColumn(int columnIndex) {
    	Dispatch columns = Dispatch.get(table, "Columns").toDispatch();
        return Dispatch.call(columns, "Item", 
                new Variant(columnIndex)).toDispatch(); 
    } 


	/*
	public int getTablesCount(Dispatch tables) throws Exception { 
        int count = 0; 
        try { 
            this.getTables(); 
        } catch (Exception e) { 
            throw new Exception("there is not any table!!"); 
        } 
        count = Dispatch.get(tables, "Count").toInt(); 

        return count; 
    }*/
    
	 /** 
     * 设置当前表格指定行的背景色 
     *  
     * @param rowIndex 
     * @param color 
     *            取值范围 0 < color < 17 默认：16 浅灰色 1：黑色 2：蓝色 3：浅蓝 ............... 
     */ 
//    public void setRowBgColor(int rowIndex, int color) { 
//        this.getRow(rowIndex); 
//
//        Dispatch shading = Dispatch.get(row, "Shading").toDispatch(); 
//
//        if (color > 16 || color < 1) 
//            color = 16; 
//        Dispatch 
//                .put(shading, "BackgroundPatternColorIndex", new Variant(color)); 
//    } 

}
