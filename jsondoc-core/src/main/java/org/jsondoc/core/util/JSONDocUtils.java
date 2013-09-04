package org.jsondoc.core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiHeaders;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.jsondoc.core.pojo.ApiBodyObjectDoc;
import org.jsondoc.core.pojo.ApiDoc;
import org.jsondoc.core.pojo.ApiErrorDoc;
import org.jsondoc.core.pojo.ApiHeaderDoc;
import org.jsondoc.core.pojo.ApiMethodDoc;
import org.jsondoc.core.pojo.ApiObjectDoc;
import org.jsondoc.core.pojo.ApiParamDoc;
import org.jsondoc.core.pojo.ApiResponseObjectDoc;
import org.jsondoc.core.pojo.JSONDoc;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class JSONDocUtils {
	public static final String UNDEFINED = "undefined";
	private static Reflections reflections = null;
	
	/**
	 * Returns the main <code>ApiDoc</code>, containing <code>ApiMethodDoc</code> and <code>ApiObjectDoc</code> objects
	 * @return An <code>ApiDoc</code> object
	 */
	public static JSONDoc getApiDoc(String basePackage, String version, String basePath) {
		reflections = new Reflections(basePackage);
		JSONDoc apiDoc = new JSONDoc(version, basePath);
		apiDoc.setApis(getApiDocs(reflections.getTypesAnnotatedWith(Api.class)));
		apiDoc.setObjects(getApiObjectDocs(reflections.getTypesAnnotatedWith(ApiObject.class)));
		return apiDoc;
	}
	
	public static Set<ApiDoc> getApiDocs(Set<Class<?>> classes) {
		Set<ApiDoc> apiDocs = new TreeSet<ApiDoc>();
		for (Class<?> controller : classes) {
			ApiDoc apiDoc = ApiDoc.buildFromAnnotation(controller.getAnnotation(Api.class));
			apiDoc.setMethods(getApiMethodDocs(controller));
			apiDocs.add(apiDoc);
		}
		return apiDocs;
	}
	
	public static Set<ApiObjectDoc> getApiObjectDocs(Set<Class<?>> classes) {
		Set<ApiObjectDoc> pojoDocs = new TreeSet<ApiObjectDoc>();
		for (Class<?> pojo : classes) {
			ApiObject annotation = pojo.getAnnotation(ApiObject.class);
			ApiObjectDoc pojoDoc = ApiObjectDoc.buildFromAnnotation(annotation, pojo);
			if(annotation.show()) {
				pojoDocs.add(pojoDoc);
			}
		}
		return pojoDocs;
	}
	
	private static List<ApiMethodDoc> getApiMethodDocs(Class<?> controller) {
		List<ApiMethodDoc> apiMethodDocs = new ArrayList<ApiMethodDoc>();
		Method[] methods = controller.getMethods();

        // ToDo -- @ApiMethod can only have one 'verb' parameter, but
        //         spring mvc @RequestMapping can have an array, so
        //         we need to loop in those cases.

		for (Method method : methods) {
			if(method.isAnnotationPresent(ApiMethod.class)) {

                // Base Api Method
				ApiMethodDoc apiMethodDoc = ApiMethodDoc.buildFromAnnotation(method.getAnnotation(ApiMethod.class));
                boolean hasSpringRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                RequestMapping requestMappingAnnotation = null;
                if (hasSpringRequestMapping) {
                    requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
                }
                if (hasSpringRequestMapping) {
                    ApiMethodDoc.augmentFromRequestMappingAnnotation(apiMethodDoc, requestMappingAnnotation);
                }

                // Api Headers
                if(method.isAnnotationPresent(ApiHeaders.class)) {
					apiMethodDoc.setHeaders(ApiHeaderDoc.buildFromAnnotation(method.getAnnotation(ApiHeaders.class)));
				}
                if (hasSpringRequestMapping) {
                    ApiHeaderDoc.augmentFromRequestMappingAnnotation(apiMethodDoc, requestMappingAnnotation);
                }

                // Api request parameters -- (the Spring annotations are processed in ApiParamDoc.getApiParamDocs
				apiMethodDoc.setUrlparameters(ApiParamDoc.getApiParamDocs(method));

				apiMethodDoc.setBodyobject(ApiBodyObjectDoc.buildFromAnnotation(method));

                // Response object
				if(method.isAnnotationPresent(ApiResponseObject.class)) {
					apiMethodDoc.setResponse(ApiResponseObjectDoc.buildFromAnnotation(
                            method.getAnnotation(ApiResponseObject.class), method));
				}
                if (method.isAnnotationPresent(ResponseBody.class)) {
                    ApiResponseObjectDoc.augmentFromResponseBodyAnnotation(apiMethodDoc,
                            method.getAnnotation(ResponseBody.class));
                }

                if(method.isAnnotationPresent(ApiErrors.class)) {
					apiMethodDoc.setApierrors(ApiErrorDoc.buildFromAnnotation(method.getAnnotation(ApiErrors.class)));
				}
				
				apiMethodDocs.add(apiMethodDoc);
			}
			
		}
		return apiMethodDocs;
	}
	
	public static String getObjectNameFromAnnotatedClass(Class<?> clazz) {
        Class<?> annotatedClass = Reflections.forName(clazz.getName());
        if (annotatedClass.isAnnotationPresent(ApiObject.class)) {
            return annotatedClass.getAnnotation(ApiObject.class).name();
        }
        return clazz.getSimpleName().toLowerCase();
    }

    public static boolean isMultiple(Method method) {
		if(Collection.class.isAssignableFrom(method.getReturnType()) || method.getReturnType().isArray()) {
			return true;
		}
		return false;
	}
	
	public static boolean isMultiple(Class<?> clazz) {
		if(Collection.class.isAssignableFrom(clazz) || clazz.isArray()) {
			return true;
		}
		return false;
	}
	
}
