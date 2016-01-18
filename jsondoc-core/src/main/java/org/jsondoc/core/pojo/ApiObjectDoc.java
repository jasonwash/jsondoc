package org.jsondoc.core.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApiObjectDoc implements Comparable<ApiObjectDoc> {
	public String jsondocId = UUID.randomUUID().toString();
	private String name;
	private String description;
	private List<ApiObjectFieldDoc> fields;

	@SuppressWarnings("rawtypes")
	public static ApiObjectDoc buildFromAnnotation(ApiObject annotation, Class clazz) {
		List<ApiObjectFieldDoc> fieldDocs = new ArrayList<ApiObjectFieldDoc>();
		for (Field field : clazz.getDeclaredFields()) {

			JsonProperty jsonPropFasterXml = field.getAnnotation(JsonProperty.class);
			org.codehaus.jackson.annotate.JsonProperty jsonPropOldJackson = field.getAnnotation(
					org.codehaus.jackson.annotate.JsonProperty.class);

			if (field.getAnnotation(ApiObjectField.class) != null
					|| jsonPropFasterXml != null
					|| jsonPropOldJackson != null) {
				fieldDocs.add(ApiObjectFieldDoc.buildFromAnnotation(field.getAnnotation(ApiObjectField.class), field));
			}

		}

		Class<?> c = clazz.getSuperclass();
		if (c != null) {
			if (c.isAnnotationPresent(ApiObject.class)) {
				ApiObjectDoc objDoc = ApiObjectDoc.buildFromAnnotation(c.getAnnotation(ApiObject.class), c);
				fieldDocs.addAll(objDoc.getFields());
			}
		}

		String name;
		String description;
		if (annotation == null) {
			name = clazz.getSimpleName();
			description = "";
		} else {
		    name = annotation.name();
			description = annotation.description();
		}
		return new ApiObjectDoc(name, description, fieldDocs);
	}

	public ApiObjectDoc(String name, String description, List<ApiObjectFieldDoc> fields) {
		super();
		this.name = name;
		this.description = description;
		this.fields = fields;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<ApiObjectFieldDoc> getFields() {
		return fields;
	}

	@Override
	public int compareTo(ApiObjectDoc o) {
		return name.compareTo(o.getName());
	}

}
