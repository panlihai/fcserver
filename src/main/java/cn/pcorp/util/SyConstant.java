package cn.pcorp.util;

import java.util.UUID;

/**
 * 定义系统模块中用到的常量
 *
 * @author panlihai
 */
public class SyConstant {
    
	/** 数据库插入:general.insert*/
	public static final String INSERT = "general.insert";
	/** 数据库删除:general.delete*/
	public static final String DELETE = "general.delete";
	/** 数据库修改:general.update*/
	public static final String UPDATE = "general.update";
	/** 数据库查询:general.select*/
	public static final String SELECT = "general.select";
	/** 数据库查询:general.selectcount*/
	public static final String SELECTCOUNT = "general.selectCount";
	/** 字符串通用是否：是 */
	public static final int INT_YES = 1;
	/** 字符串通用是否：否 */
	public static final int INT_NO = 0;
	/** 字符串通用是否：是 */
	public static final String STR_YES = "Y";
	/** 字符串通用是否：否 */
	public static final String STR_NO = "N";
	/** 常用的多项数据的分隔符 */
	public static final String SEPARATOR = ",";
	/** 高级分隔符 */
	public static final String SEPARATOR_HIGH = ";";
	/** 编码字段分隔符号 */
	public static final String CODE_SEPARATOR = "`";
	/** 字段参数分隔符号 */
	public static final String FIELD_SEPARATOR = "#";
	/** 公式参数分隔符号 */
	public static final String FORMULA_SEPARATOR = ":";
	/** 系统变量参数分隔符号 */
	public static final String VARIABLE_SEPARATOR = "@";
	
	/** 在response中放置的自定义header名称，用于确认是否已经跳转到JSP页面 */
	public static final String HEADER_HAS_GOTO_JSP = "HEADER_HAS_GOTO_JSP";
	/** 选择类型:单选 */
	public static final String SELECT_TYPE_SINGLE = "single";
	/** 选择类型:多选 */
	public static final String SELECT_TYPE_MULTI = "multi";
	/** $ACTION$：显示-列表接送数据 */
	public static final String ACT_DATA_JSON = "listJson";
	/** $ACTION$：显示-列表接送数据 */
	public static final String ACT_DATA_JSON_VALUE = "listJsonValue";
	 /** $ACTION$：显示-所有列表查看页面 */
    public static final String ACT_ALLLIST_VIEW = "listAll";
    /** $ACTION$：显示-所有列表查看页面 */
    public static final String ACT_VIEW_ONE = "viewOne";
    /** $ACTION$：显示-列表查看页面 */
    public static final String ACT_LIST_VIEW = "listView";
    /** $ACTION$：显示-列表编辑页面 */
    public static final String ACT_LIST_EDIT = "listEdit";
    /** $ACTION$：操作-列表批量保存 */
    public static final String ACT_LIST_UPDATE = "listUpdate";
    /** $ACTION$：显示-快速查询列表页面 */
    public static final String ACT_LIST_QUICK = "listQuick";
    /** $ACTION$：显示-快速查询列表页面 */
    public static final String ACT_QUICK = "quick";
    /** $ACTION$：显示-卡片查看页面 */
    public static final String ACT_CARD_READ = "cardRead";
    /** $ACTION$：显示-卡片编辑页面 */
    public static final String ACT_CARD_EDIT = "cardEdit";
    /** $ACTION$：显示-添加页面 */
    public static final String ACT_CARD_ADD = "cardAdd";
    /** $ACTION$：显示-添加页面 */
    public static final String ACT_LIST_ADD = "listAdd";
    /** $ACTION$：显示-导出页面 */
    public static final String ACT_DATA_EXPORT_FIRST = "showExport";
    /** $ACTION$：显示-导出第二步选择页面 */
    public static final String ACT_DATA_EXPORT_NEXT = "showNextExport";
    /** $ACTION$：显示-导入页面 */
    public static final String ACT_DATA_IMPORT_FIRST = "showImport";
    /** $ACTION$：显示-导入第二步选择页面 */
    public static final String ACT_DATA_IMPORT_NEXT = "showNextImport";
    /** $ACTION$：显示-导出总控 */
    public static final String ACT_DATA_EXPORT = "export";
    /** $ACTION$：显示-导入总控 */
    public static final String ACT_DATA_IMPORT = "import";
    /** $ACTION$：显示-导出对应关系 */
    public static final String ACT_DATA_RELATION = "relation";
    /** $ACTION$：操作-获取菜单 */
    public static final String ACT_VIEW_MENUS = "showMenus";
    /** $ACTION$：操作-表单保存 */
    public static final String ACT_CARD_SAVE = "cardSave";
    /** $ACTION$：操作-卡片更新 */
    public static final String ACT_CARD_UPDATE = "cardUpdate";
    /** $ACTION$：操作-审核 */
    public static final String ACT_AUDIT = "audit";
    /** $ACTION$：操作-弃审 */
    public static final String ACT_UNAUDIT = "unaudit";
    /** $ACTION$：操作-流程审核：挂接流程 */
    public static final String ACT_WF_AUDIT = "wfAudit";
    /** $ACTION$：操作-卡片插入 */
    public static final String ACT_CARD_INSERT = "cardInsert";
    /** $ACTION$：操作-卡片项排序 */
    public static final String ACT_CARD_SORT = "cardSort";
    /** $ACTION$：操作-删除 */
    public static final String ACT_DELETE = "listDelete";
    /** $ACTION$：操作-查询 */
    public static final String ACT_QUERY = "query";
    /** $ACTION$：操作-导出文件 */
    public static final String ACT_EXP_FILE = "exportFile";
    /** $ACTION$：操作-导入文件 */
    public static final String ACT_IMP_FILE = "importFile";
    /** $ACTION$：操作-导出模版 */
    public static final String ACT_EXP_TEMPLATE = "exportTemplate";
    /** $ACTION$：操作-模版导入 */
    public static final String ACT_IMP_TEMPLATE = "importTemplate";
    /** $ACTION$：操作-卡片中的翻页 */
    public static final String ACT_CARD_FLIP = "cardFlip";
    /** $ACTION$：显示-分析图 */
    public static final String ACT_CHART = "chart";
    /** $ACTION$：生成web service描述wsdl */
    public static final String ACT_WSDL = "wsdl";
    /** $ACTION$：jsp报表打印的action */
    public static final String ACT_PRINT_JSPRPT = "printJspRpt";
    /** $ACTION$: 显示指标报表参数页面的action */
    public static final String ACT_SHOW_PARAM_PAGE = "showZBReportParamPage";
    
    /** $ACTION$：不显示图形 */
    public static final String CHART_BLANK = "0";
    /** $ACTION$：显示饼图 */
    public static final String CHART_PIE = "1";
    /** $ACTION$：显示柱状图 */
    public static final String CHART_BAR = "2";
    /** $ACTION$：显示曲线图 */
    public static final String CHART_XY = "3";
    /** $ACTION$：显示以时间（年、月、日）为单位坐标的曲线图 */
    public static final String CHART_XY_TIME = "4";
    /** $ACTION$：时间线图为年线图 */
    public static final String CHART_XY_TIME_YEAR = "5";
    /** $ACTION$：时间线图为月线图 */
    public static final String CHART_XY_TIME_MONTH = "6";
    /** $ACTION$：时间线图为日线图 */
    public static final String CHART_XY_TIME_DAY = "7";
    /** $ACTION$：单饼图 */
    public static final String CHART_PIE_SINGLE = "8";
    /** $ACTION$：横轴 */
    public static final String CHART_XAXIS = "1";
    /** $ACTION$：纵轴 */
    public static final String CHART_YAXIS = "2";
    /** $ACTION$：日期格式：年 */
    public static final String FORMAT_DATE_YEAR = "1";
    /** $ACTION$：日期格式：月 */
    public static final String FORMAT_DATE_MONTH = "2";
    /** $ACTION$：日期格式：天 */
    public static final String FORMAT_DATE_DAY = "3";

    
    /**-----------------------用户子模块---------------------------------------**/
    /** 用户状态：系统 */
    public static final int USER_FLAG_SY = 1;
    /** 用户状态：非系统 */
    public static final int USER_FLAG_NOT_SY = 2;
    /** 用户状态：被锁定 */
    public static final int USER_FLAG_LOCKED = 3;
    /** 动作：显示修改 */
    public static final String USER_INFO_SHOWFRONT = "showFront";
    /** 动作：显示修改 */
    public static final String USER_INFO_SHOWNAME = "showName";
    /** 动作：修改 */
    public static final String USER_INFO_CHANGENAME = "changeName";
    /** 修改状态：未修改 */
    public static final String USER_INFO_CHANGED_NO = "1";
    /** 修改状态：已修改 */
    public static final String USER_INFO_CHANGED_YES = "2";
   
    /** 用户初试密码：123456 */
    public static final String USER_INIT_PASS = "123456";
    
    /** 登录会计期间是否是当前会计期间*/
    public static final String USER_ACCOUNT_NOW_YESNO = "RIGHTACCOUNT";
    
    
    /**----------------------部门子模块---------------------------------------**/
    /** 部门状态：内部 */
    public static final int ORG_FLAG_IN = 1;
    /** 部门状态：外部 */
    public static final int ORG_FLAG_OUT = 2;
    /** 部门查询包含方式：全部(包含自身) */
    public static final int ORG_INCLUDE_ALL = 1;
    /** 部门查询包含方式：所有子孙 */
    public static final int ORG_INCLUDE_ALL_CHILD = 2;
    /** 部门查询包含方式：所有子 */
    public static final int ORG_INCLUDE_SON = 3;
    
    /**----------------------功能子模块---------------------------------------**/
    /** 功能类型：表单 FUNC_TYPE=1 */
    public static final int FUNC_TYPE_FORM = 1;
    /** 功能类型：模板 FUNC_TYPE=2 */
    public static final int FUNC_TYPE_PAGE = 2;
    /** 功能类型：自定义类 FUNC_TYPE=3 */
    public static final int FUNC_TYPE_CLASS = 3;
    /** 功能类型：URL FUNC_TYPE=4 */
    public static final int FUNC_TYPE_URL = 4;
    /** 功能类型：WEB SERVICE FUNC_TYPE=5 */
    public static final int FUNC_TYPE_WS = 5;
    /** 功能启动标志：正常 FUNC_FLAG=1 */
    public static final String FUNC_FLAG_NORMAL = "1";
    /** 功能启动标志：禁用 FUNC_FLAG=2 */
    public static final String FUNC_FLAG_DISABLE = "2";
    /** 菜单的最高层数 */
    public static final int MENU_TOP_LAYER = 1;
    /**----------------------表单定义子模块---------------------------------------**/
    /** 表单类型：普通查询表 */
    public static final String FORM_TYPE_COMMON = "COM";
    /** 表单类型：自定义表单 */
    public static final String FORM_TYPE_SELF = "SELF";
    /** 表单类型：内置图表 */
    public static final String FORM_TYPE_INCH = "INCH";
    /** 表单类型：内置报表 */
    public static final String FORM_TYPE_INRP = "INRP";
    /** 表单类型: 外置报表*/
    public static final String FORM_TYPE_OUTRP = "OUTRP";
    /** 表单类型：指标报表 */
    public static final String FORM_TYPE_ZBRP = "ZBRP";
    
    /** 输入元素: 输入框*/
    public static final String INPUT_ELEMENT_TEXT = "INPUT";
    /** 输入元素: 下拉框*/
    public static final String INPUT_ELEMENT_SELECT = "SELECT";
    /** 输入元素: 隐藏字段*/
    public static final String INPUT_ELEMENT_HIDDEN = "HIDDEN";
    /** 输入元素: 大文本*/
    public static final String INPUT_ELEMENT_TEXTAREA = "TEXTAREA";
    /** 输入元素: 单选按钮*/
    public static final String INPUT_ELEMENT_RADIO = "RADIO";    
    /** 输入元素: 多选按钮*/
    public static final String INPUT_ELEMENT_CHECKBOX = "CHECKBOX";    
    /** 输入元素: 图片链接*/
    public static final String INPUT_ELEMENT_IMAGE = "IMAGE";    
    /** 输入元素: 水平线*/
    public static final String INPUT_ELEMENT_HR = "HR";    
    
    /** 输入类型: 自动*/
    public static final String INPUT_TYPE_AUTO = "AUTO";
    /** 输入类型: 查询选择*/
    public static final String INPUT_TYPE_QUERY = "QUERY";
    /** 输入类型: 日期时间*/
    public static final String INPUT_TYPE_DATE = "DATE";
    /** 输入类型: 字典(无名称)*/
    public static final String INPUT_TYPE_DICTNN = "DICTNN";
    /** 输入类型: 字典(有名称)*/
    public static final String INPUT_TYPE_DICTWN = "DICTWN";
    /** 输入类型: 手工输入*/
    public static final String INPUT_TYPE_HAND = "HAND";
    /** 输入类型: 字典编码*/
    public static final String INPUT_TYPE_DICTCODE = "DICTCODE";    
    /** 输入类型: 编号*/
    public static final String INPUT_TYPE_CODE = "CODE";    
    /** 输入类型: 自处理*/
    public static final String INPUT_TYPE_SELFTRAN = "SELFTRAN";
    
    /** 输入是否可为空: 不能为空*/
    public static final String DATA_INPUT_NO = "1";
    /** 输入是否可为空: 可为空*/
    public static final String DATA_INPUT_YES = "2";
    
    /** 数据类型: 字符串*/
    public static final String DATA_TYPE_STR = "STR";
    /** 数据类型: 数字*/
    public static final String DATA_TYPE_NUM = "NUM";
    /** 数据类型: 数字*/
    public static final String DATA_TYPE_DATE = "DATE";
    /** 数据类型: 数字*/
    public static final String DATA_TYPE_DATETIME = "DATETIME";
    /** 数据类型: 数字*/
    public static final String DATA_TYPE_BLOB = "BLOB";
    /** 数据类型: 图片*/
    public static final String DATA_TYPE_PIC = "PIC";
    
    /** 数据项所属类型: 表字段*/
    public static final String FITEM_TYPE_TAB = "TAB";
    /** 数据项所属类型: 自定义字段*/
    public static final String FITEM_TYPE_SELF = "SELF";
    /** 数据项所属类型: 视图字段*/
    public static final String FITEM_TYPE_VIEW = "VIEW";
    /** 数据项所属类型: 参数字段*/
    public static final String FITEM_TYPE_PARAM = "PARAM";
    
    /** 查询关系: 等于*/
    public static final String QUERY_SIGN_EQUAL = "=";
    /** 查询关系: 大于*/
    public static final String QUERY_SIGN_BIG = ">";
    /** 查询关系: 小于*/
    public static final String QUERY_SIGN_SMALL = "<";
    /** 查询关系: 大于等于*/
    public static final String QUERY_SIGN_BIGEQ = ">=";
    /** 查询关系: 小于等于*/
    public static final String QUERY_SIGN_SMALLEQ = "<=";
    /** 查询关系: 不等于*/
    public static final String QUERY_SIGN_NOTEQ = "<>";
    /** 查询关系: in*/
    public static final String QUERY_SIGN_IN = "in";
    /** 查询关系: like*/
    public static final String QUERY_SIGN_LIKE = "like";
    /** 查询关系: 全文检索*/
    public static final String QUERY_SIGN_CONTAINS = "contains";
    
    /** LIKE方式: 左like*/
    public static final String LIKE_TYPE_LEFT = "1";
    /** LIKE方式: 右like*/
    public static final String LIKE_TYPE_RIGHT = "2";
    /** LIKE方式: 全like*/
    public static final String LIKE_TYPE_ALL = "3";
    
    
    /** 数据最大导出条数 */
    public static final int DATA_EXPORT_LIMINT_LINE = 1000;
    
    /** 页面类型：卡片 */
    public static final String BUTTON_PAGE_CARD = "CARD";
    /** 页面类型：列表 */
    public static final String BUTTON_PAGE_LIST = "LIST";
    
    /** 按钮所属操作组：查看 */
    public static final String BUTTON_GROUP_READ = "READ";
    
    /** 系统管理员用户代码 */
    public static final String ADMIN_USER_CODE = "admin";
    
    /** 缺省按钮导入模板 */
    public static final String DEFAULT_TEMPLETE_CODE = "164085";
    
    /**----------------------页面自定义子模块---------------------------------------**/
    /** 页面显示样式：横向 */
    public static final int PAGE_STYLE_ACROSS = 1;
    /** 页面显示样式：竖向 */
    public static final int PAGE_STYLE_VERTICAL = 2;
    
    /** 模块类型：标准SQL */
    public static final int PAGE_MODU_SQL = 1;
    /** 模块类型：JSP文件 */
    public static final int PAGE_MODU_JSP = 2;
    /** 模块类型：URL地址 */
    public static final int PAGE_MODU_URL = 3;
    /** 模块类型：IFRAME */
    public static final int PAGE_MODU_IFRAME = 4;
    
    /** 权限类型：数据权限 */
    public static final String DS_TYPE_DATA = "DATA";
    /** 权限类型：操作权限 */
    public static final String DS_TYPE_ACT = "ACT";
    
    /**----------------------菜单子模块---------------------------------------**/
    
    /** 菜单类型：父菜单 */
    public static final int MENU_TYPE_PARENT = 1;
    /** 菜单类型：功能 */
    public static final int MENU_TYPE_FUNC = 2;
    /** 菜单类型：URL地址 */
    public static final int MENU_TYPE_URL = 3;
    /** 菜单类型：模块菜单 */
    public static final int MENU_TYPE_MODU = 4;
    
    /** 默认指标组数据父菜单*/
    public static final String DEFAULT_GP_PARENT_MENU = "00130005";
    
    /**-----------------------信息发布子模块---------------------------------------**/
    
    /** 模板类型：普通文字模板 */
    public static final int INFO_TMPL_COMMON = 1;
    /** 模板类型：文件链接模板 */
    public static final int INFO_TMPL_FILE = 2;
    /** 模板类型：URL地址链接模板 */
    public static final int INFO_TMPL_URL = 3;

    /**-----------------------编号---------------------------------------**/
    /** 自动编号标志字符串 */
    public static final String AUTO_CODE_FLAG = "<系统自动生成>";
    /** 编号生成方式:本地 */
    public static final String CODE_GEN_TYPE_LOCAL = "1";
    /** 编号生成方式:远程 */
    public static final String CODE_GEN_TYPE_REMOTE = "2";
    
    /** 功能流程挂接方式: 自动挂接 */
    public static final int FUNC_WF_BIND_AUTO = 1;
    /** 功能流程挂接方式: 手动挂接 */
    public static final int FUNC_WF_BIND_MANUAL = 2;
    /** 功能流程挂接方式: 自动挂接 */
    public static final int FUNC_WF_BIND_NO = 3;
    
/**-----------------------投票子模块---------------------------------------**/
    
    /** 投票状态：尚未开始 */
    public static final int VOTE_STATUS_NOT = 1;
    /** 投票状态：进行中 */
    public static final int VOTE_STATUS_NOW = 2;
    /** 投票状态：已结束 */
    public static final int VOTE_STATUS_ALR = 3;
    
    /** 审核控制：不控制 */
    public static final int AUD_CONTROL_NO = 1;
    /** 审核控制：删除 */
    public static final int AUD_CONTROL_DEL = 2;
    /** 审核控制：修改 */
    public static final int AUD_CONTROL_EDIT = 3;
    /** 审核控制：删除和修改 */
    public static final int AUD_CONTROL_DELEDIT = 4;
    
    /**－－－－－－－－－－－－－－查询策略类型－－－－－－－－－－－－－－－－－**/
    /** 查询策略：公共 */
    public static final int FORM_QUERY_PUBLIC = 1; 
    /** 查询策略：私有 */
    public static final int FORM_QUERY_PRIVATE = 2; 
    /**－－－－－－－－－－－－－－SQL类型－－－－－－－－－－－－－－－－－**/
    /** SQL类型：数据操作 */
    public static final int SQL_TYPE_DML = 1; 
    /** SQL类型：数据定义 */
    public static final int SQL_TYPE_DDL = 2; 
    /**－－－－－－－－－－－－－－报表引擎－－－－－－－－－－－－－－－－－**/
    /** 报表查询条件：查询、全息 */
    public static final String RPT_SQL_CONDITION = "$SQL_CONDITION"; 
    /** 报表标准条件：功能、表单、数据权限 */
    public static final String RPT_STD_CONDITION = "$STD_CONDITION"; 
    
    
    
    /** 功能删除类型：删除系统数据 */
    public static final String FUNC_DEL_TYPE_SY = "SY";
    /** 功能删除类型：删除设置信息及历史数据 */
    public static final String FUNC_DEL_TYPE_QT = "QT";
    /** 功能删除类型：删除全部 */
    public static final String FUNC_DEL_TYPE_ALL = "ALL";
    
    /** 关联数据类型：基于功能关联 */
    public static final String LINK_DATA_TYPE_FUNC = "FUNC";
    /** 关联数据类型：基于表关联 */
    public static final String LINK_DATA_TYPE_TABLE = "TABLE";
    
    /** 关联删除类型：关联删除 */
    public static final int LINK_DELETE_TYPE_YES = 1;
    /** 关联删除类型：禁止删除 */
    public static final int LINK_DELETE_TYPE_NO = 2;
    /** 关联删除类型：不控制 */
    public static final int LINK_DELETE_TYPE_IGNORE = 3;
    
    /****************报表服务器名称，地址*****************************************/
    public static final String RPT_SERVER = "leiliangof";
/****************报表服务器名端口****************************/
    public static final String RPT_SERVER_PORT = "80";
/****************报表服务器虚拟主机路径************************************************/
    public static final String RPT_SERVER_VIRPATH = "/rq";
    
    /** 用户权限数据更新方式：自动更新 */
    public static final String USER_ACL_UPDATE_AUTO = "1";
    /** 用户权限数据更新方式：手动更新 */
    public static final String USER_ACL_UPDATE_HAND = "2";
    
    /** 简单工作流节点类型：起始 */
    public static final int SIMP_FLOW_START = 1;
    /** 简单工作流节点类型：中间 */
    public static final int SIMP_FLOW_MID = 2;
    /** 简单工作流节点类型：终点 */
    public static final int SIMP_FLOW_END = 3;
    
    /** 简单工作流处理方式：上报 */
    public static final String SIMP_FLOW_METHOD_S = "S";
    /** 简单工作流处理方式：退回 */
    public static final String SIMP_FLOW_METHOD_T = "T";
    /** 简单工作流处理方式：不批准 */
    public static final String SIMP_FLOW_METHOD_F = "F";
    /** 简单工作流处理方式：批准 */
    public static final String SIMP_FLOW_METHOD_P = "P";
    
    /** 系统成功登陆标志 */
    public static final String SUCCESS_LOGIN = "1";
    /** HTTP前缀,在DoServlet中初始化 */
    private static String httpPrefix;
    
    /** 文件导入重复数据的类型：主键重复 */
    public static final String FORM_EXPIMP_DUPTYPE_PK = "PK";
    /** 文件导入重复数据的类型：唯一组重复 */
    public static final String FORM_EXPIMP_DUPTYPE_UNIQUE = "UNIQUE";
    
	
    /** 获得UUID*/
    public static String getUUID(){
    	return UUID.randomUUID().toString().replaceAll("-", "");
    }
    /**
     * 取得HTTP前缀
     * @return HTTP前缀
     */
    public static String getHttpPrefix() {
        return httpPrefix;
    }
    /**
     * 设置HTTP前缀
     * @param pHttpPrefix HTTP前缀
     */
    public static void setHttpPrefix(String pHttpPrefix) {
        httpPrefix = pHttpPrefix;
    }
}
