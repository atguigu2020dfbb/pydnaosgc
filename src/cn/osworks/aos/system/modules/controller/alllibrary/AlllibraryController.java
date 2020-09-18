package cn.osworks.aos.system.modules.controller.alllibrary;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.asset.WebCxt;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.dao.po.Archive_tablefieldlistPO;
import cn.osworks.aos.system.modules.service.alllibrary.AlllibraryService;
/**
 * 全库检索
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value="alllibrary/alllibrary")
public class AlllibraryController {
	@Autowired
	private AlllibraryService alllibraryService;
	
	/**
	 * 
	 * 页面初始化
	 * 
	 * @return
	 */
	@RequestMapping(value="initData")
	public String initData(HttpServletRequest request,HttpSession session,HttpServletResponse response){
		//数据表所有字段检索，并且不要重复的,把不重复的列表展示出来
		List<Archive_tablefieldlistPO> titleDtos =alllibraryService.getDataFieldListTitle();
		request.setAttribute("fieldDtos", titleDtos);
		return "aos/alllibrary/alllibrary.jsp";
	}
	/**
	 * 
	 * 查询数据列表
	 * 
	 * @return
	 */
	@RequestMapping(value="getDataList")
	public void getDataList(HttpServletRequest request,HttpSession session,HttpServletResponse response){
		Dto dto = Dtos.newDto(request);
		List<Map<String,Object>> list = alllibraryService.getDataSjbList(dto);
		int pCount = list.size();
		String outString = AOSJson.toGridJson(list, pCount);
		// 就这样返回转换为Json后返回界面就可以了。
		WebCxt.write(response, outString);
	}
	/**
	 * 得到电子文件列表
	 * @param request
	 * @param session
	 * @param response
	 */
	@RequestMapping(value="getPath")
	public void getPath(HttpServletRequest request,HttpSession session,HttpServletResponse response){
		Dto dto = Dtos.newDto(request);
		List<Map<String,Object>> list = alllibraryService.getPath(dto);
		// 就这样返回转换为Json后返回界面就可以了。
		WebCxt.write(response, AOSJson.toGridJson(list));
	}
}
