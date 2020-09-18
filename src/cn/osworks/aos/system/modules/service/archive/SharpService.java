package cn.osworks.aos.system.modules.service.archive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.id.AOSId;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.dao.mapper.Archive_tablefieldlistMapper;
import cn.osworks.aos.system.dao.po.Archive_tablefieldlistPO;
import cn.osworks.aos.system.dao.po.Archive_tablenamePO;
import cn.osworks.aos.system.modules.dao.vo.UserInfoVO;

@Service
public class SharpService extends JdbcDaoSupport {
	@Autowired
	public JdbcTemplate jdbcTemplate;
	@Autowired
	public DataService dataService;
	@Autowired
	private Archive_tablefieldlistMapper archive_tablefieldlistMapper;

	@Resource
	public void setJd(JdbcTemplate jd) {
		super.setJdbcTemplate(jd);
	}

	/**
	 * 得到数据表集合
	 * 
	 * @author PX
	 * @param listtablename
	 * 
	 *            2018-8-23
	 * @return
	 */
	public List<Map<String, Object>> getListData(String listtablename) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "select * from " + listtablename;
		list = jdbcTemplate.queryForList(sql);
		return list;
	}

	/**
	 * 
	 * 页面初始化
	 * 
	 * @param tablename
	 * @return
	 */
	public List<Archive_tablefieldlistPO> getDataFieldListTitle(String tablename) {
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getDataFieldDisplayAll(tablename);
		return list;
	}

	/*
	 * 查询表名
	 */
	public List<Archive_tablenamePO> findTablename() {
		// TODO Auto-generated method stub
		List<Archive_tablenamePO> alist = new ArrayList<Archive_tablenamePO>();
		String sql = "select * from archive_TableName";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				String tablename = (String) map.get("TableName");
				String tabledesc = (String) map.get("TableDesc");
				Archive_tablenamePO archive_tablenamePO = new Archive_tablenamePO();
				archive_tablenamePO.setTablename(tablename);
				archive_tablenamePO.setTabledesc(tabledesc);
				alist.add(archive_tablenamePO);
			}
		}
		return alist;
	}

	/**
	 * 
	 * 显示表头
	 * 
	 * @param qDto
	 * @return
	 */
	public List<Dto> getDataFieldListDisplayAll(Dto qDto) {
		String sql = "";
		String fieldName;
		String enfield = "id_";
		// 排列方式，页码，每页条目数
		Integer limit = Integer.valueOf(qDto.getString("limit"));
		Integer page = Integer.valueOf(qDto.getString("page"));
		// 条目开始索引
		Integer pageStart = (page - 1) * (limit) + 1;
		List<Archive_tablefieldlistPO> list = archive_tablefieldlistMapper
				.getDataFieldDisplayAll(qDto.getString("tablename"));
		String orderenfield = "";
		for (int i = 0; i < list.size(); i++) {
			fieldName = list.get(i).getFieldenname();
			enfield = enfield + "," + fieldName;
			if (i == 3) {
				orderenfield = enfield;
			}
		}
		orderenfield = orderenfield.substring(4);
		if (qDto.getString("page") != null && qDto.getString("page") != "") {
			sql = "WITH aos_query_ AS (SELECT TOP 100 PERCENT ROW_NUMBER () OVER (ORDER BY "
					+ orderenfield
					+ ") AS aos_rn_,"
					+ enfield
					+ " FROM "
					+ qDto.getString("tablename")
					+ ") SELECT * FROM aos_query_ WHERE aos_rn_ BETWEEN "
					+ pageStart
					+ " AND "
					+ limit
					* page
					* Integer.valueOf(qDto.getString("page"))
					+ " ORDER BY aos_rn_";
		} else {
			sql = "WITH aos_query_ AS (SELECT TOP 100 PERCENT ROW_NUMBER () OVER (ORDER BY "
					+ orderenfield
					+ ") AS aos_rn_,"
					+ enfield
					+ " FROM "
					+ qDto.getString("tablename")
					+ ") SELECT * FROM aos_query_  ORDER BY aos_rn_";
		}
		List listDto = jdbcTemplate.queryForList(sql);
		return listDto;
	}

	/**
	 * 存入信息
	 * 
	 * @author PX
	 * @param outdto
	 * 
	 *            2018-8-29
	 * @param session
	 * @return
	 * @throws ParseException
	 */
	public Dto jymessage(Dto outdto, HttpSession session) throws ParseException {
		// TODO Auto-generated method stub
		Dto out = Dtos.newDto();
		UserInfoVO userInfoVO = (UserInfoVO) session
				.getAttribute("_userInfoVO");
		String user = userInfoVO.getAccount_();
		String id_ = outdto.getString("id_");
		String listTablename = outdto.getString("listTablename");
		String _jy = outdto.getString("_jy");
		if (_jy.equals("1")) {
			// 查询借阅的时间
			String sql = "select * from archive_jy where tablename='"
					+ listTablename + "' and archive_id='" + id_ + "'";
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					String jytime = (String) list.get(0).get("jytime");
					String jyday = (String) list.get(0).get("jyday");
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date date = format.parse(jytime);
					//1532880000000
					long oldtime = date.getTime();
					//1535597961131
					long newtime =System.currentTimeMillis();
					long days = (newtime - oldtime) / (1000 * 60 * 60 * 24);
					/*
					 * if(jyday.equals("")){ _jy = "已借阅"; }
					 */
					if (days > Integer.valueOf(jyday)) {
						_jy = "已到期";
					} else {
						_jy = "已借阅";
					}
					out.put("borrowData", jytime);
					out.put("reletday","");
					out.put("user", user);
					out.put("archivenumber", id_);
					out.put("tablename", listTablename);
					out.put("archivestate", _jy);

				}
			}
		} else {
			_jy = "未借阅";
			out.put("user", user);
			out.put("borrowData", "");
			out.put("borrowday","");
			out.put("archivenumber", id_);
			out.put("tablename", listTablename);
			out.put("archivestate", _jy);
		}
		return out;
	}

	/**
	 * 借阅保存
	 * 
	 * @author PX
	 * @param outDto
	 * 
	 *            2018-8-29
	 * @return
	 */
	public boolean savejy(Dto outDto) {
		// TODO Auto-generated method stub
		// 添加之前判断是否存在
		String sql2 = "select * from archive_jy where tablename='"
				+ outDto.getString("tablename") + "' and archive_id='"
				+ outDto.getString("archivenumber")
				+ "' and archivestate='已借阅' and gh=''";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql2);
		if (list != null && list.size() > 0) {
			return false;
		} else {
			String sql = "insert into archive_jy(id_,jytime,users,archive_id,tablename,jyday,archivestate,gh) values('"
					+ AOSId.uuid()
					+ "','"
					+ outDto.getString("borrowData")
					+ "','"
					+ outDto.getString("user")
					+ "','"
					+ outDto.getString("archivenumber")
					+ "','"
					+ outDto.getString("tablename")
					+ "','"
					+ outDto.getString("borrowday")
					+ "','已借阅','')";
			jdbcTemplate.execute(sql);
			// 修改条目状态
			String sql3 = "update " + outDto.getString("tablename")
					+ " set _jy='1' where id_='"
					+ outDto.getString("archivenumber") + "'";
			jdbcTemplate.execute(sql3);
		}
		return true;
	}

	public void updatejy(Dto outDto) throws Exception {
		// TODO Auto-generated method stub
		// 修改借阅天数，完成续借
		// 查询原有天数
		String sql = "select * from archive_jy where tablename='"
				+ outDto.getString("tablename") + "' and archive_id='"
				+ outDto.getString("archivenumber") + "' and gh=''" ;
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if (list != null && list.size() > 0) {
			String jyday = (String) list.get(0).get("jyday");
			if (jyday != null || !jyday.equals("")) {
				String sql2 = "update archive_jy set jyday='"
						+ (Integer.valueOf(jyday) + Integer.valueOf(outDto
								.getString("reletday")))
						+ "' where tablename='" + outDto.getString("tablename")
						+ "' and archive_id='"
						+ outDto.getString("archivenumber") + "'";
				jdbcTemplate.execute(sql2);
			}
		}
	}

	/**
	 * 归还
	 * 
	 * @author PX
	 * @param outDto
	 * @param session
	 * @return
	 * 
	 *         2018-8-29
	 */
	public int returnjy(Dto outDto, HttpSession session) {
		// TODO Auto-generated method stub
		// 判断是否有权限
		String sql = "select * from archive_jy where tablename='"
				+ outDto.getString("listTablename") + "' and archive_id='"
				+ outDto.getString("id_") + "' and gh=''";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if (list != null && list.size() > 0) {
			String users = (String) list.get(0).get("users");
			// 判断当前用户和这个用户是不是一个人
			UserInfoVO userInfoVO = (UserInfoVO) session
					.getAttribute("_userInfoVO");
			String user = userInfoVO.getAccount_();
			if (!users.equals(user)) {
				return -2;
			}
			try {
				// 执行归还操作
				String sql2 = "update archive_jy set archivestate='已归还',gh='1' where tablename='"
						+ outDto.getString("listTablename")
						+ "' and archive_id='" + outDto.getString("id_") + "'";
				jdbcTemplate.execute(sql2);
				// 修改条目标记
				String sql3 = "update " + outDto.getString("listTablename")
						+ " set _jy=null where id_='" + outDto.getString("id_")
						+ "'";
				jdbcTemplate.execute(sql3);
				return 1;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return -1;
			}
		}
		return -1;
	}
	/**
	 * 
	 * 
	 * @author PX
	 *
	 * 2018-8-30
	 * @param dto 
	 * @return 
	 */
	public List<Map<String, Object>> listjy(Dto dto) {
		// TODO Auto-generated method stub
		String tablename = dto.getString("tablename");
		String sql="";
		if(tablename!=null&&!tablename.equals("")){
			sql="select * from archive_jy where 1=1 and tablename='"+tablename+"'";
		}else{
			sql="select * from archive_jy where 1=1";
		}
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
}
