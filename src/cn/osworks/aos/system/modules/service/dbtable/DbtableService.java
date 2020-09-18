package cn.osworks.aos.system.modules.service.dbtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.osworks.aos.core.asset.AOSCons;
import cn.osworks.aos.core.asset.AOSUtils;
import cn.osworks.aos.core.id.AOSId;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.dao.mapper.Archive_tableInfoMapper;
import cn.osworks.aos.system.dao.mapper.Archive_tablefieldlistMapper;
import cn.osworks.aos.system.dao.mapper.Archive_tablenameMapper;
import cn.osworks.aos.system.dao.po.Aos_sys_dicPO;
import cn.osworks.aos.system.dao.po.Aos_sys_dic_indexPO;
import cn.osworks.aos.system.dao.po.Archive_tableInfoPO;
import cn.osworks.aos.system.dao.po.Archive_tablefieldlistPO;
import cn.osworks.aos.system.dao.po.Archive_tablenamePO;

/**
 * 
 * 数据表服务
 * 
 * @author shq
 * 
 * @date 2016-9-2
 */
@Service
public class DbtableService {

	@Autowired
	private Archive_tablenameMapper archive_tablenameMapper;
	@Autowired
	private Archive_tableInfoMapper archive_tableInfoMapper;
	@Autowired
	private Archive_tablefieldlistMapper archive_tablefieldlistMapper;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 
	 * 保存数据表信息
	 * 
	 * @param qDto
	 */
	@Transactional
	public Dto saveTable(Dto qDto) {
		Dto outDto = Dtos.newDto();
		Archive_tablenamePO archive_tablenamePO = new Archive_tablenamePO();
		AOSUtils.copyProperties(qDto, archive_tablenamePO);
		if (qDto.getString("tablenametemplate") != null
				&& qDto.getString("tablenametemplate") != "") {
			jdbcTemplate.execute("SELECT * INTO " + qDto.getString("tablename")
					+ " FROM " + qDto.getString("tablenametemplate")
					+ " WHERE 1=2");
			String fieldtid = AOSId.uuid();
			archive_tablenamePO.setId_(fieldtid);
			archive_tablenameMapper.insert(archive_tablenamePO);
			qDto.put("tid", qDto.getString("tableTemplate"));
			List<Archive_tablefieldlistPO> listfield = archive_tablefieldlistMapper
					.list(qDto);
			//Archive_tablefieldlistPO archive_tablefieldlistPO = new Archive_tablefieldlistPO();
			for (int i = 0; i < listfield.size(); i++) {
				// archive_tablefieldlistPO.setId_(AOSId.uuid());
				listfield.get(i).setId_(AOSId.uuid());
				listfield.get(i).setTid(fieldtid);
				archive_tablefieldlistMapper.insert(listfield.get(i));

			}
			if (qDto.getString("path").equals("on")) {
				createTablePath(qDto.getString("tablename"));
			}
			outDto.setAppCode(AOSCons.SUCCESS);
			outDto.setAppMsg("操作完成，数据表新增成功。");
			return outDto;
		}
		if (!checkTablename(archive_tablenamePO.getTablename())) {
			outDto.setAppCode(AOSCons.ERROR);
			String msg = AOSUtils.merge("数据表[{0}]已存在, 请重新输入。",
					archive_tablenamePO.getTablename());
			outDto.setAppMsg(msg);
			return outDto;
		}
		if (!createTable(archive_tablenamePO.getTablename())) {
			outDto.setAppCode(AOSCons.ERROR);
			String msg = AOSUtils.merge("数据表[{0}]已存在数据库中, 请重新输入。",
					archive_tablenamePO.getTablename());
			outDto.setAppMsg(msg);
			return outDto;
		}
		archive_tablenamePO.setId_(AOSId.uuid());
		archive_tablenameMapper.insert(archive_tablenamePO);

		String sql = "insert into archive_TableFieldList  (id_,tid,FieldEnName,FieldCnName,FieldClass,FieldSize,FieldView,indx) "
				+ "values('"
				+ AOSId.uuid()
				+ "','"
				+ archive_tablenamePO.getId_()
				+ "','_lrr','录入人','varchar','20','1','100')"
				+ "insert into archive_TableFieldList  (id_,tid,FieldEnName,FieldCnName,FieldClass,FieldSize,FieldView,indx) "
				+ "values('"
				+ AOSId.uuid()
				+ "','"
				+ archive_tablenamePO.getId_()
				+ "','_lrrq','录入日期','varchar','30','1','101')"
				+ "insert into archive_TableFieldList  (id_,tid,FieldEnName,FieldCnName,FieldClass,FieldSize,FieldView,indx) "
				+ "values('"
				+ AOSId.uuid()
				+ "','"
				+ archive_tablenamePO.getId_()
				+ "','_path','附件','int','4','1','102')";
		;
		jdbcExcel(sql);
		if (qDto.getString("path").equals("on")) {
			createTablePath(qDto.getString("tablename"));
		}if (qDto.getString("Ysmeta").equals("on")) {
			createTableInfo(qDto);
		}
		// 创建数据表
		// createTable(archive_tablenamePO.getTablename());
		outDto.setAppCode(AOSCons.SUCCESS);
		outDto.setAppMsg("操作完成，数据表新增成功。");
		return outDto;
	}

	/**
	 * 
	 * 修改数据表信息
	 * 
	 * @param qDto
	 * @return
	 */
	@Transactional
	public Dto updateTable(Dto qDto) {
		Dto outDto = Dtos.newDto();
		String tablename = qDto.getString("tablename");
		Archive_tablenamePO check_archive_tablenamePO = archive_tablenameMapper
				.selectByKey(qDto.getString("id_"));
		if (!tablename.equals(check_archive_tablenamePO.getTablename())) {
			if (!checkTablename(tablename)) {
				outDto.setAppCode(AOSCons.ERROR);
				outDto.setAppMsg(AOSUtils.merge("数据表[{0}]已经存在，请重新输入。",
						tablename));
				return outDto;
			}
		}
		String sql = "EXEC  sp_rename '"
				+ check_archive_tablenamePO.getTablename() + "' ,'" + tablename
				+ "'";
		if (!jdbcExcel(sql)) {
			outDto.setAppCode(AOSCons.ERROR);
			outDto.setAppMsg(AOSUtils.merge("数据库中已经存在，请重新输入。", tablename));
			return outDto;
		}
		Archive_tablenamePO archive_tablenamePO = new Archive_tablenamePO();
		AOSUtils.copyProperties(qDto, archive_tablenamePO);
		archive_tablenameMapper.updateByKey(archive_tablenamePO);
		outDto.setAppCode(AOSCons.SUCCESS);
		outDto.setAppMsg("操作完成，数据表修改成功。");
		return outDto;
	}

	/**
	 * 
	 * 检测数据表名的唯一性
	 * 
	 * @param tablename
	 * @return
	 */
	public boolean checkTablename(String tablename) {
		Dto calcDto = Dtos.newCalcDto("count(id_)");
		calcDto.put("tablename", tablename);
		Integer tablenameInteger = Integer.valueOf(archive_tablenameMapper
				.calc(calcDto));
		boolean out = true;
		if (tablenameInteger > 0) {
			out = false;
		}
		return out;
	}

	/**
	 * 
	 * 检测数据表是否创建成功
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean createTable(String tableName) {
		boolean out = true;
		try {

			jdbcTemplate
					.execute("create table "
							+ tableName
							+ " (id_ nvarchar(192) primary key,_lrr nvarchar(192),_lrrq nvarchar(192),_classtree varchar(100),_path int)");

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out = false;
		}

		return out;
	}

	/**
	 * 
	 * 创建电子附件表
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean createTablePath(String tableName) {
		boolean out = true;

		jdbcTemplate
				.execute("create table "
						+ tableName
						+ "_path"
						+ " (id_ nvarchar(192),tid nvarchar(192),_path varchar(256),dirname varchar(256),sdatetime datetime,_s_path varchar(256),_index int)");
		out = false;

		return out;
	}

	
	/**
	 * 
	 * 创建元数据表
	 * 
	 * @author Sun
	 * @param tablename
	 * @return
	 *
	 * 2018-8-10
	 */
	public boolean createTableInfo(Dto qDto){
		boolean out =true;
		try {
			jdbcTemplate.execute("select *  into "+qDto.getString("tablename")+"_info from archive_infoTemplate");
			Archive_tableInfoPO archive_tableInfoPO = new Archive_tableInfoPO();
			String fieldtid = AOSId.uuid();
			archive_tableInfoPO.setId_(fieldtid);
			archive_tableInfoPO.setTablename(qDto.getString("tablename")+"_info");
			archive_tableInfoPO.setTabledesc(qDto.getString("tabledesc")+"元数据");
			archive_tableInfoMapper.insert(archive_tableInfoPO);
			qDto.put("tid", "467927e3c456465fa1c8fc595f026522");
			List<Archive_tablefieldlistPO> listfield = archive_tablefieldlistMapper.list(qDto);
			for (int i = 0; i < listfield.size(); i++) {
				listfield.get(i).setId_(AOSId.uuid());
				listfield.get(i).setTid(fieldtid);
				//listfield.get(i).setFieldview(1);
				archive_tablefieldlistMapper.insert(listfield.get(i));

			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out=false;
		}
		
		return out;
	}
	
	/**
	 * 
	 * 删除数据表信息
	 * 
	 * @param qDto
	 * @return
	 */
	@SuppressWarnings("finally")
	@Transactional
	public int deleteTable(Dto qDto) {
		String[] selections = qDto.getRows();
		int rows = 0;
		for (String id_ : selections) {
			archive_tablenameMapper.deleteByKey(id_);
			rows++;
		}
		try {
			jdbcTemplate.execute("drop table " + qDto.getString("tablename"));
			jdbcTemplate.execute("drop table " + qDto.getString("tablename")
					+ "_path");
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			return rows;
		}

	}

	/**
	 * 
	 * 保存数据表字段信息
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto saveField(Dto qDto) {
		Dto outDto = Dtos.newDto();
		Archive_tablefieldlistPO archive_tablefieldlistPO = new Archive_tablefieldlistPO();
		AOSUtils.copyProperties(qDto, archive_tablefieldlistPO);
		StringBuffer sql = new StringBuffer();
		sql.append("ALTER TABLE " + qDto.getString("tablename") + " add "
				+ qDto.getString("fieldenname") + " "
				+ qDto.getString("fieldclass"));
		if (qDto.getString("fieldclass").equals("varchar")) {
			sql.append(" (" + qDto.getString("fieldsize") + ")");
		}
		if (!jdbcExcel(sql.toString())) {
			outDto.setAppCode(AOSCons.ERROR);
			String msg = AOSUtils.merge("数据库中没有此表或字段[{0}]己存在 ,请重新输入。",
					archive_tablefieldlistPO.getFieldenname());
			outDto.setAppMsg(msg);
			return outDto;
		}
		if (!checkFieldName(archive_tablefieldlistPO.getFieldenname(),
				archive_tablefieldlistPO.getTid())) {
			outDto.setAppCode(AOSCons.ERROR);
			String msg = AOSUtils.merge("字段[{0}]己存在,请重新输入。",
					archive_tablefieldlistPO.getFieldenname());
			outDto.setAppMsg(msg);
			return outDto;
		}
		archive_tablefieldlistPO.setId_(AOSId.uuid());
		archive_tablefieldlistMapper.insert(archive_tablefieldlistPO);
		String msg = AOSUtils.merge("字段[{0}]添加成功 。",
				archive_tablefieldlistPO.getFieldenname());
		outDto.setAppMsg(msg);
		return outDto;
	}

	/**
	 * 
	 * 修改数据表字段保存
	 * 
	 * @param qDto
	 * @return
	 */
	@Transactional
	public Dto updateField(Dto qDto) {
		Dto outDto = Dtos.newDto();
		String filedenname = qDto.getString("fieldenname");
		Archive_tablefieldlistPO check_archive_tablefieldlistPO = archive_tablefieldlistMapper
				.selectByKey(qDto.getString("id_"));
		if (!filedenname
				.equals(check_archive_tablefieldlistPO.getFieldenname())) {
			if (!checkFieldName(filedenname, qDto.getString("tid"))) {
				outDto.setAppCode(AOSCons.ERROR);
				outDto.setAppMsg(AOSUtils.merge("字段[{0}]己存在,请重新输入。",
						filedenname));
				return outDto;
			}
		}
		// 修改字段长度
		if(!updateFieldSize(qDto)){
			outDto.setAppCode(AOSCons.ERROR);
			outDto.setAppMsg(AOSUtils.merge("修改数据库字段[{0}]异常,请重新输入。",
					filedenname));
			return outDto;
		}
		String sql = "exec sp_rename '" + qDto.getString("tablename") + "."
				+ check_archive_tablefieldlistPO.getFieldenname() + "','"
				+ filedenname + "', 'COLUMN'";
		if (!jdbcExcel(sql)) {
			outDto.setAppCode(AOSCons.ERROR);
			outDto.setAppMsg(AOSUtils.merge("数据库字段[{0}]不存在或已存在,请重新输入。",
					filedenname));
			return outDto;
		}

		Archive_tablefieldlistPO archive_tablefieldlistPO = new Archive_tablefieldlistPO();
		AOSUtils.copyProperties(qDto, archive_tablefieldlistPO);
		archive_tablefieldlistMapper.updateByKey(archive_tablefieldlistPO);
		outDto.setAppCode(AOSCons.SUCCESS);
		outDto.setAppMsg("操作完成，字段修改成功。");
		return outDto;
	}

	/**
	 * 
	 * 删除数据表字段
	 * 
	 * @param qDto
	 * @return
	 */
	@Transactional
	public int deleteField(Dto qDto) {
		String[] selections = qDto.getRows();
		int rows = 0;
		String sql = " ALTER TABLE " + qDto.getString("tablename")
				+ " drop column " + qDto.getString("fieldenname");
		for (String id_ : selections) {
			archive_tablefieldlistMapper.deleteByKey(id_);
			jdbcExcel(sql);
			rows++;
		}
		return rows;
	}

	/**
	 * 
	 * 检测数据表字段的唯一性
	 * 
	 * @param fieldName
	 * @param tid
	 * @return
	 */
	public boolean checkFieldName(String fieldName, String tid) {
		Dto calcDto = Dtos.newCalcDto("count(id_)");
		calcDto.put("fieldenname", fieldName);
		calcDto.put("tid", tid);
		Integer fieldenanameInteger = Integer
				.valueOf(archive_tablefieldlistMapper.calc(calcDto));
		boolean out = true;
		if (fieldenanameInteger > 0) {
			out = false;
		}
		return out;
	}

	/**
	 * 
	 * 重置（创建数据表）
	 * 
	 * @param qDto
	 * @return
	 */
	public Dto resetTable(Dto qDto) {
		Dto outDto = Dtos.newDto();
		List<Archive_tablenamePO> list = archive_tablenameMapper.list(qDto);
		for (int i = 0; i < list.size(); i++) {
			deleteDbTableName(list.get(i).getTablename());
			String strfield = creatDbTable(list.get(i).getId_());
			String sql = " create table " + list.get(i).getTablename()
					+ " (id_ nvarchar(192) primary key," + strfield + ")";
			jdbcTemplate.execute(sql);
		}
		outDto.setAppCode(AOSCons.ERROR);
		String msg = AOSUtils.merge("数据库重置成功。");
		outDto.setAppMsg(msg);
		return outDto;
	}

	/**
	 * 
	 * 删除数据库己存在的表
	 * 
	 * @param tablename
	 * @return
	 */
	public boolean deleteDbTableName(String tablename) {
		int count = jdbcTemplate
				.queryForInt("SELECT COUNT(*) FROM dbo.sysobjects WHERE name= '"
						+ tablename + "'");
		boolean out = true;
		if (count > 0) {
			String sqlDrop = " drop table " + tablename;
			jdbcTemplate.execute(sqlDrop);
			out = false;
		}
		return out;
	}

	public String creatDbTable(String tid) {
		Dto pDto = Dtos.newDto();
		pDto.put("tid", tid);
		List<Archive_tablefieldlistPO> listfield = archive_tablefieldlistMapper
				.list(pDto);
		String querysql = "";
		String strsql = "";
		for (int j = 0; j < listfield.size(); j++) {
			String fieldenname = listfield.get(j).getFieldenname();
			String fieldclass = listfield.get(j).getFieldclass();
			int fieldsize = listfield.get(j).getFieldsize();

			strsql = fieldenname + " " + fieldclass + "(" + fieldsize + ")";
			if (fieldclass.equals("int")) {
				strsql = fieldenname + " " + fieldclass;
			}
			if (fieldclass.equals("datetime")) {
				strsql = fieldenname + " " + fieldclass;
			}
			if (querysql.equals("")) {
				querysql = strsql;
			} else
				querysql = querysql + "," + strsql;
		}
		return querysql;
	}

	/**
	 * 
	 * JDBC执行语句
	 * 
	 * @param sql
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean jdbcExcel(String sql) {
		boolean out = true;
		try {
			jdbcTemplate.execute(sql);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			out = false;
		} finally {
			return out;
		}

	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 
	 * 查询设置顺序信息
	 * 
	 * @param pDto
	 * @return
	 */
	public List<Archive_tablefieldlistPO> listOrderInfos(Dto pDto) {
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getOrderFields(pDto.getString("tid"));
		return list;
	}

	/**
	 * 修改字段顺序的索引值
	 * 
	 * @param dto
	 * @return
	 */
	public boolean updateField_index(Dto dto) {
		// TODO Auto-generated method stub
		String zdid = dto.getString("zdid_");
		String[] zds = zdid.split(",");
		int indx = 0;
		if (zds.length > 0 && zds != null) {
			for (int i = 0; i < zds.length; i++) {
				String sql = "update archive_TableFieldList set indx='" + indx
						+ "' where  id_='" + zds[i] + "'";
				jdbcTemplate.update(sql);
				indx = indx + 10;
			}
			return true;
		}
		return false;
	}

	// 修改字段长度
	public boolean updateFieldSize(Dto qDto) {
		String sql = "alter table " + qDto.getString("tablename")
				+ " alter column " + qDto.getString("fieldenname") + " varchar("
				+ qDto.getString("fieldsize") + ")";
		try {
			jdbcTemplate.execute(sql);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
