package cn.osworks.aos.system.modules.controller.receive;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.asset.WebCxt;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.dao.po.Archive_tablefieldlistPO;
import cn.osworks.aos.system.modules.service.archive.DataService;
import cn.osworks.aos.system.modules.service.batchUpload.BatchUploadService;

/**
 * 
 * 批量上传
 * 
 * @author Sun
 *
 * 2018-10-8
 */
@Controller
@RequestMapping(value="receive/batch")
public class BatchUploadController {
	
	@Autowired
	private DataService dataService;
	
	@Autowired
	private BatchUploadService batchUploadService;
	
	/**
	 * 
	 * 初始化
	 * 
	 * @author Sun
	 * @param request
	 * @param response
	 * @return
	 *
	 * 2018-10-20
	 */
	@RequestMapping(value="initBatch")
	public String initBatch(HttpServletRequest request,HttpServletResponse response){
		
		return "aos/receive/initBatch.jsp";
	}

	/**
	 * 
	 * 查询统计数
	 * 
	 * @author Sun
	 * @param request
	 * @param response
	 *
	 * 2018-10-20
	 */
	@RequestMapping(value="listAccounts")
	public void listAcounts(HttpServletRequest request,HttpServletResponse response){
		Dto inDto = Dtos.newDto(request);
		List<Dto> list=batchUploadService.getCount(inDto);
		String outString=AOSJson.toGridJson(list, list.size());
		WebCxt.write(response, outString);
	}
	
	/**
	 * 
	 * 
	 * 
	 * @author Sun
	 * @param request
	 * @param response
	 *
	 * 2018-10-20
	 */
	@RequestMapping(value="initReceive")
	public String initReceive(HttpServletRequest request,HttpServletResponse response){
		Dto inDto = Dtos.newDto(request);
		
		request.setAttribute("inDto", inDto);
		return "aos/receive/receive.jsp";
	}
	
	/**
	 * 
	 * 查询字段下拉框
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="queryFields")
	public void queryFields(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		List<Archive_tablefieldlistPO> titleDtos = dataService.getDataFieldListTitle(qDto.getString("tablename"));
		WebCxt.write(response, AOSJson.toGridJson(titleDtos));
	}
}
