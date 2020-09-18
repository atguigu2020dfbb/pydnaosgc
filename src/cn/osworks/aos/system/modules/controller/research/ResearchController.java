package cn.osworks.aos.system.modules.controller.research;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.asset.WebCxt;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;

@Controller
@RequestMapping(value="research/research")
public class ResearchController {

	@RequestMapping(value="initResearch")
	public String initResearch(HttpServletRequest request,HttpServletResponse response){
		
		return "aos/research/research.jsp";
	}
	
	@RequestMapping(value="listAccounts")
	public void listAccounts(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		
		
		String outString="[{'r1':'关于如开xxx会议','r2':'编辑','r3':'其他','r4':'文件汇总','r5':'信息中心','r6':'孙洪强','r7':'2019-4-11','r8':'2019-4-16'}]";
		
		
		WebCxt.write(response, outString);
		
		
	}
}
