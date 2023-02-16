package com.hitebaas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hitebaas.controller.handler.BaseController;
import com.hitebaas.util.ContractConstant;
import com.hitebaas.utils.KeysUtils;

@Controller
@RequestMapping("dev")
public class DevController extends BaseController{
	
	@GetMapping("/index")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView("dev");
		String token = KeysUtils.get32String();
		mav.addObject("token", token);
		getSession().setAttribute(ContractConstant.TOKEN, token);
		return mav;
	}
	
}
