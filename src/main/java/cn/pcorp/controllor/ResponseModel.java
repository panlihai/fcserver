package cn.pcorp.controllor;

import cn.pcorp.controllor.util.MethodConstant;
import cn.pcorp.model.DynaBean;
import cn.pcorp.util.DateUtils;

public class ResponseModel {
	private DynaBean result;
	private String msg;
	private String code;
	private Long timestamp;
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
		result.set(MethodConstant.TIMESTAMP, this.getTimestamp());
	}
	private Long totalsize;
	private Long listsize;
	private Object data;
	public DynaBean getResult() {
		return result;
	}
	public void setResult(DynaBean result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
		result.set(MethodConstant.MSG, this.getMsg());
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
		result.set(MethodConstant.CODE, this.getCode());
	}
	public Long getTimestamp() {
		return DateUtils.getTimestamp();
	}
	public Long getTotalsize() {
		return totalsize;
	}
	public void setTotalsize(Long totalsize) {
		this.totalsize = totalsize;
		result.set(MethodConstant.TOTALSIZE, this.getTotalsize());
	}
	public Long getListsize() {
		return listsize;
	}
	public void setListsize(Long listsize) {
		this.listsize = listsize;
		result.set(MethodConstant.LISTSIZE, this.getListsize());
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;

		result.set(MethodConstant.DATA, this.getData());
	}
	public static ResponseModel getInstance() {		
		ResponseModel res = new ResponseModel();
		res.setResult(new DynaBean(""));
		res.setTimestamp(res.getTimestamp());
		res.setCode("0");
		res.setMsg("请求成功");
		res.getResult().set(MethodConstant.MSG, res.getMsg());
		res.getResult().set(MethodConstant.CODE, res.getCode());
		return res;
	}
}
