package ${packageName};

import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ${className} {

<#list columns as column>
	@RequestMapping(value = "/${column}.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String html_${column?replace('/','_')}(HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "${column}";
    }
    
</#list>

}