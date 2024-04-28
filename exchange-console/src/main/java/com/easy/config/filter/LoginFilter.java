package com.easy.config.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import com.easy.session.SessionManager;
import com.easy.utility.SysUtility;

@Configuration
@WebFilter(filterName="LoginFilter",urlPatterns="/*")
public class LoginFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException {
        String uri = request.getRequestURI();
        String[] urls = SysUtility.GetSetting("system", "FilterUrl").split(",");
        boolean doFilter = UrlFilter(urls, uri);
        
        SessionManager.setSession(request.getSession());
      //门户类的页面不判断登录
        if(uri.indexOf("cloud/") > 0
        		|| uri.indexOf("header.html") > 0
        		|| uri.indexOf("footer.html") > 0
        		|| uri.indexOf("portal.html") > 0) {
        	filterChain.doFilter(request, response);
        }else if ((uri.lastIndexOf(".html") >= 0 ) && doFilter && SysUtility.isEmpty(SysUtility.getCurrentUserIndx())) {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            
            PrintWriter out = response.getWriter();
            StringBuilder builder = new StringBuilder();
            builder.append("<script type=\"text/javascript\">");
            //builder.append("alert('Invalid session, please login again!');");
            builder.append(getLoginUrl(uri));
            builder.append("</script>");
            out.print(builder.toString());
        }else{
            filterChain.doFilter(request, response);
        }
	}

	private boolean UrlFilter(String[] notFilter,String uri){
         for (String s : notFilter) {
             if (uri.indexOf(s) >= 0) {
                 return false;
             }
         }
         return true;
	}
	
	private String getLoginUrl(String uri){
		String loginPage = SysUtility.GetSetting("system", "LoginPage");
        String indexPage = SysUtility.GetSetting("system", "IndexPage");
        if(SysUtility.isEmpty(loginPage)) {
         	loginPage = "login.html";
        }
        if(SysUtility.isEmpty(indexPage)) {
         	indexPage = "index.html";
        }
		StringBuffer str = new StringBuffer();
		if(uri.lastIndexOf(indexPage) >= 0){
			str.append("window.location.href='");
        } else {
        	str.append("window.top.location.href='");
        }
		
		String[] uris = uri.split("\\/");
		for (int i = 2; i < uris.length - 1; i++) {
			loginPage = "../"+loginPage;
		}
		str.append(loginPage);
		
		
		str.append("';");
		
		return str.toString();
	}
}
