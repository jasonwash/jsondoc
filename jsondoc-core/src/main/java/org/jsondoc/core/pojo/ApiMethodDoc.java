package org.jsondoc.core.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jsondoc.core.annotation.ApiMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class ApiMethodDoc {
	public String jsondocId = UUID.randomUUID().toString();
	private String path;
	private String description;
	private ApiVerb verb;
	private List<String> produces;
	private List<String> consumes;
	private List<ApiHeaderDoc> headers;
	private List<ApiParamDoc> urlparameters;
	private ApiBodyObjectDoc bodyobject;
	private ApiResponseObjectDoc response;
	private List<ApiErrorDoc> apierrors;

	public static ApiMethodDoc buildFromAnnotation(ApiMethod annotation) {
		ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
		apiMethodDoc.setPath(annotation.path());
		apiMethodDoc.setDescription(annotation.description());
		apiMethodDoc.setVerb(annotation.verb());
		apiMethodDoc.setConsumes(Arrays.asList(annotation.consumes()));
		apiMethodDoc.setProduces(Arrays.asList(annotation.produces()));
		return apiMethodDoc;
	}

    public static ApiMethodDoc augmentFromRequestMappingAnnotation(ApiMethodDoc apiMethodDoc, RequestMapping annotation) {

        // consumes
        if (annotation.consumes() != null && annotation.consumes().length != 0) {
            apiMethodDoc.setConsumes(Arrays.asList(annotation.consumes()));
        }

        // path
        String pathFromSpringAnnotation = getPathFromSpringRequestMapping(annotation);
        if (pathFromSpringAnnotation != null) {
            // ToDo : combine path from class annotation (if exists) with method annotation
            apiMethodDoc.setPath(pathFromSpringAnnotation);
        } else {
            if (annotation.value() != null && annotation.value().length > 0) {
                apiMethodDoc.setPath(annotation.value()[0]);
            }
        }

        // produces
        if (annotation.produces() != null && annotation.produces().length != 0) {
            List<String> springProducesValues = getProducesStringFromSpringReqMapProduces(annotation);
            if (springProducesValues.isEmpty()) {
                apiMethodDoc.setProduces(Arrays.asList(annotation.produces()));
            } else {
                apiMethodDoc.setProduces(springProducesValues);
            }
        }

        // verb
        if (annotation.method() != null && annotation.method().length != 0) {
            ApiVerb verb = getVerbFromSpringType(annotation.method()[0]);
            apiMethodDoc.setVerb(verb);
        }

        return apiMethodDoc;
    }

    public static List<String> getProducesStringFromSpringReqMapProduces(RequestMapping requestMapping) {
        List<String> retval = new ArrayList<String>();
        if (requestMapping != null) {
            String[] produces = requestMapping.produces();
            if (produces.length > 0) {
                retval.addAll(Arrays.asList(produces));
            }
        }
        return retval;
    }

    public static String getPathFromSpringRequestMapping(RequestMapping requestMapping) {
        if (requestMapping == null || requestMapping.value().length == 0) {
            return null;
        }
        return requestMapping.value()[0];
    }

    public static ApiVerb getVerbFromSpringType(RequestMethod method) {
        if (method == RequestMethod.GET) {
            return ApiVerb.GET;
        } else if (method == RequestMethod.POST) {
            return ApiVerb.POST;
        } else if (method == RequestMethod.PUT) {
            return ApiVerb.PUT;
        } else if (method == RequestMethod.DELETE) {
            return ApiVerb.DELETE;
//        } else if (method == RequestMethod.HEAD) {
//            return ApiVerb.HEAD;
        } else {
            return null;
        }
    }

	public ApiMethodDoc() {
		super();
		this.headers = new ArrayList<ApiHeaderDoc>();
		this.urlparameters = new ArrayList<ApiParamDoc>();
		this.apierrors = new ArrayList<ApiErrorDoc>();
	}

	public List<ApiHeaderDoc> getHeaders() {
		return headers;
	}

	public void setHeaders(List<ApiHeaderDoc> headers) {
		this.headers = headers;
	}

	public List<String> getProduces() {
		return produces;
	}

	public void setProduces(List<String> produces) {
		this.produces = produces;
	}

	public List<String> getConsumes() {
		return consumes;
	}

	public void setConsumes(List<String> consumes) {
		this.consumes = consumes;
	}

	public ApiVerb getVerb() {
		return verb;
	}

	public void setVerb(ApiVerb verb) {
		this.verb = verb;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ApiParamDoc> getUrlparameters() {
		return urlparameters;
	}

	public void setUrlparameters(List<ApiParamDoc> urlparameters) {
		this.urlparameters = urlparameters;
	}

	public ApiResponseObjectDoc getResponse() {
		return response;
	}

	public void setResponse(ApiResponseObjectDoc response) {
		this.response = response;
	}

	public ApiBodyObjectDoc getBodyobject() {
		return bodyobject;
	}

	public void setBodyobject(ApiBodyObjectDoc bodyobject) {
		this.bodyobject = bodyobject;
	}

	public List<ApiErrorDoc> getApierrors() {
		return apierrors;
	}

	public void setApierrors(List<ApiErrorDoc> apierrors) {
		this.apierrors = apierrors;
	}

}
