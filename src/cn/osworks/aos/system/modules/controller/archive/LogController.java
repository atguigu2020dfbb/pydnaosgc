package cn.osworks.aos.system.modules.controller.archive;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.asset.WebCxt;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.dao.mapper.LogMapper;
import cn.osworks.aos.system.dao.po.LogPO;
import cn.osworks.aos.system.modules.service.archive.LogDaoService;
import cn.osworks.aos.web.report.AOSReport;
import cn.osworks.aos.web.report.AOSReportModel;

/**
 * 日志页面控制器
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "archive/log")
public class LogController extends JdbcDaoSupport {
	@Autowired
	public JdbcTemplate jdbcTemplate;
	@Autowired
	public LogDaoService logDaoService;
	@Autowired
	public LogMapper logMapper;
	@Resource
	public void setJb(JdbcTemplate jb) {
		super.setJdbcTemplate(jb);
	}
	/**
	 * 日志页面初始化
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping("initData")
	public String initData(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		return "aos/archive/log.jsp";
	}
	/**
	 * 日志条件列表
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("listlogs")
	public void listlogs(HttpServletRequest request,
			HttpServletResponse response, HttpSession session){
			Dto dto = Dtos.newDto(request);
			List<LogPO> list = logMapper.listPage(dto);
			// 神奇的inDto又显神功了，它里面还包含了分页查询记录总条数喔。
			String outString = AOSJson.toGridJson(list, dto.getPageTotal());
			//如果有姓名，就传递到session中
			if(dto.getString("party")!=null&&dto.getString("party")!=""){
				session.setAttribute("party",dto.getString("party"));
			}
			// 就这样返回转换为Json后返回界面就可以了。
			WebCxt.write(response, outString);
	}
	/**
	 * 根据id查询日志信息
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("getlogInfo")
	public void getlogInfo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session){
				// inDto包装了全部的请求参数哦
				Dto inDto = Dtos.newDto(request);
				LogPO logPo = logDaoService.getlogInfo(inDto);
				String outString = AOSJson.toJson(logPo);
				WebCxt.write(response, outString);
	}
	/**
	 * 删除单条日志
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("dellogInfo")
	public void dellogInfo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session){
		// inDto包装了全部的请求参数哦
		Dto inDto = Dtos.newDto(request);
		Dto dellog = logDaoService.dellogInfo(inDto);
		WebCxt.write(response, AOSJson.toJson(dellog));
	}
	/**
	 * 清空日志操作
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("delAlllogInfo")
	public void delAlllogInfo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session){
		// inDto包装了全部的请求参数哦
		Dto inDto = Dtos.newDto(request);
		Dto outDto = logDaoService.delAlllogInfo(inDto);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	/**
	 * 导出日志操作
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("fillReport")
	public void fillReport(HttpServletRequest request,
			HttpServletResponse response, HttpSession session){
		// inDto包装了全部的请求参数哦
		Dto qDto = Dtos.newDto(request);
		//设置表头
		LinkedHashMap<String,Object> lhm=new LinkedHashMap<String, Object>();
		lhm.put("party", "用户名称");
		lhm.put("category", "类别");
		lhm.put("title", "标题");
		lhm.put("take", "利用");
		lhm.put("ip_address", "ip地址");
		lhm.put("create_time", "时间");
		List<Map<String, Object>> titleDtos = logDaoService
				.getDataFieldListTitle(qDto);
		// 组装报表数据模型
		AOSReportModel reportModel = new AOSReportModel();
		reportModel.setFileName("日志信息表");
		// 设置报表集合
		reportModel.setLogsList(titleDtos);
		reportModel.setLogHeader(lhm);
		// 填充报表
		// AOSPrint aosPrint = AOSReport.fillReport(reportModel);
		// aosPrint.setFileName("excel表");
		session.setAttribute(AOSReport.DEFAULT_AOSPRINT_KEY, reportModel);
		WebCxt.write(response, AOSJson.toJson(Dtos.newOutDto()));
	}
}
