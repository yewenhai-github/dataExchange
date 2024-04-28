package com.easy.config;

import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MappingController {

	@RequestMapping(value = "/forms/auth/entedit.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_entedit(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/entedit";
    }
    
	@RequestMapping(value = "/forms/auth/entlist.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_entlist(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/entlist";
    }
    
	@RequestMapping(value = "/forms/auth/entuser.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_entuser(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/entuser";
    }
    
	@RequestMapping(value = "/forms/auth/passwordedit.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_passwordedit(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/passwordedit";
    }
    
	@RequestMapping(value = "/forms/auth/sauthmenuedit.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_sauthmenuedit(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/sauthmenuedit";
    }
    
	@RequestMapping(value = "/forms/auth/sauthmenulist.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_sauthmenulist(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/sauthmenulist";
    }
    
	@RequestMapping(value = "/forms/auth/sauthorgedit.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_sauthorgedit(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/sauthorgedit";
    }
    
	@RequestMapping(value = "/forms/auth/sauthorglist.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_sauthorglist(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/sauthorglist";
    }
    
	@RequestMapping(value = "/forms/auth/sauthpermrolelist.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_sauthpermrolelist(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/sauthpermrolelist";
    }
    
	@RequestMapping(value = "/forms/auth/sauthrolelist.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_sauthrolelist(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/sauthrolelist";
    }
    
	@RequestMapping(value = "/forms/auth/sauthuserlist.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_auth_sauthuserlist(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/auth/sauthuserlist";
    }
    
	@RequestMapping(value = "/forms/base/announcementedit.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_base_announcementedit(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/base/announcementedit";
    }
    
	@RequestMapping(value = "/forms/base/announcementlist.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_base_announcementlist(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/base/announcementlist";
    }
    
	@RequestMapping(value = "/forms/base/codetypelist.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_forms_base_codetypelist(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "forms/base/codetypelist";
    }
    
	@RequestMapping(value = "/index.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_index(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "index";
    }
    
	@RequestMapping(value = "/login.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_login(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "login";
    }
    
	@RequestMapping(value = "/main.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_main(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "main";
    }
    
	@RequestMapping(value = "/register.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_register(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "register";
    }
    

}