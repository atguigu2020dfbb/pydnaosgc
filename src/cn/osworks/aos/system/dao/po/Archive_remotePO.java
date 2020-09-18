package cn.osworks.aos.system.dao.po;

import cn.osworks.aos.core.typewrap.PO;

/**
 * <b>archive_remote[archive_remote]数据持久化对象</b>
 * <p>
 * 注意:此文件由AOS平台自动生成-禁止手工修改。
 * </p>
 * 
 * @author AHei
 * @date 2019-04-12 13:55:28
 */
public class Archive_remotePO extends PO {

	private static final long serialVersionUID = 1L;

	/**
	 * id_
	 */
	private String id_;
	
	/**
	 * proc_ins_id
	 */
	private String proc_ins_id;
	
	/**
	 * xm
	 */
	private String xm;
	
	/**
	 * sqrq
	 */
	private String sqrq;
	
	/**
	 * comment
	 */
	private String comment;
	
	/**
	 * state
	 */
	private Integer state;
	

	/**
	 * id_
	 * 
	 * @return id_
	 */
	public String getId_() {
		return id_;
	}
	
	/**
	 * proc_ins_id
	 * 
	 * @return proc_ins_id
	 */
	public String getProc_ins_id() {
		return proc_ins_id;
	}
	
	/**
	 * xm
	 * 
	 * @return xm
	 */
	public String getXm() {
		return xm;
	}
	
	/**
	 * sqrq
	 * 
	 * @return sqrq
	 */
	public String getSqrq() {
		return sqrq;
	}
	
	/**
	 * comment
	 * 
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * state
	 * 
	 * @return state
	 */
	public Integer getState() {
		return state;
	}
	

	/**
	 * id_
	 * 
	 * @param id_
	 */
	public void setId_(String id_) {
		this.id_ = id_;
	}
	
	/**
	 * proc_ins_id
	 * 
	 * @param proc_ins_id
	 */
	public void setProc_ins_id(String proc_ins_id) {
		this.proc_ins_id = proc_ins_id;
	}
	
	/**
	 * xm
	 * 
	 * @param xm
	 */
	public void setXm(String xm) {
		this.xm = xm;
	}
	
	/**
	 * sqrq
	 * 
	 * @param sqrq
	 */
	public void setSqrq(String sqrq) {
		this.sqrq = sqrq;
	}
	
	/**
	 * comment
	 * 
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * state
	 * 
	 * @param state
	 */
	public void setState(Integer state) {
		this.state = state;
	}
	

}