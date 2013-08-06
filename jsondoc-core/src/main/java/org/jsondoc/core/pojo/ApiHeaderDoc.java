package org.jsondoc.core.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jsondoc.core.annotation.ApiHeader;
import org.jsondoc.core.annotation.ApiHeaders;
import org.springframework.web.bind.annotation.RequestMapping;

public class ApiHeaderDoc {
	public String jsondocId = UUID.randomUUID().toString();
	private String name;
	private String description;

	public ApiHeaderDoc(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public static List<ApiHeaderDoc> buildFromAnnotation(ApiHeaders annotation) {
		List<ApiHeaderDoc> docs = new ArrayList<ApiHeaderDoc>();
		for (ApiHeader apiHeader : annotation.headers()) {
			docs.add(new ApiHeaderDoc(apiHeader.name(), apiHeader.description()));
		}
		return docs;
	}

    public static ApiMethodDoc augmentFromRequestMappingAnnotation(ApiMethodDoc apiMethodDoc, RequestMapping annotation) {
        // ToDo - set fields that can be extracted from the Spring annotation
        return apiMethodDoc;
    }

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

}
