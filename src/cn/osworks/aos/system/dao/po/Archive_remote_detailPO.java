package cn.osworks.aos.system.dao.po;

import cn.osworks.aos.core.typewrap.PO;

/**
 * <b>archive_remote_detail[archive_remote_detail]数据持久化对象</b>
 * <p>
 * 注意:此文件由AOS平台自动生成-禁止手工修改。
 * </p>
 * 
 * @author AHei
 * @date 2019-02-21 16:43:00
 */
public class Archive_remote_detailPO extends PO {

	private static final long serialVersionUID = 1L;

	/**
	 * id_
	 */
	private String id_;
	
	/**
	 * tid
	 */
	private String tid;
	
	/**
	 * formid
	 */
	private String formid;
	

	/**
	 * id_
	 * 
	 * @return id_
	 */
	public String getId_() {
		return id_;
	}
	
	/**
	 * tid
	 * 
	 * @return tid
	 */
	public String getTid() {
		return tid;
	}
	
	/**
	 * formid
	 * 
	 * @return formid
	 */
	public String getFormid() {
		return formid;
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
	 * tid
	 * 
	 * @param tid
	 */
	public void setTid(String tid) {
		this.tid = tid;
	}
	
	/**
	 * formid
	 * 
	 * @param formid
	 */
	public void setFormid(String formid) {
		this.formid = formid;
	}
	

}