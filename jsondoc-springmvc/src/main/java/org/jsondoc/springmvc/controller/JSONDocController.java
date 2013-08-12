package org.jsondoc.springmvc.controller;

import javax.servlet.ServletContext;

import org.jsondoc.core.pojo.JSONDoc;
import org.jsondoc.core.util.JSONDocUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/jsondoc")
public class JSONDocController {

    private String basePackage;
	private String version;
	private String basePath;

	public void setVersion(String version) {
		this.version = version;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    @RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	JSONDoc getApi() {
		return JSONDocUtils.getApiDoc(basePackage, version, basePath);
	}

}
