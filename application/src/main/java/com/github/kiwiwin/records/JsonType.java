package com.github.kiwiwin.records;

import java.util.Map;

/**
 * VndMediaType: can only be one of FULL, BACKOFFICE, CUSTOMER. The value will be determined by
 * PermissionCheckRequestFilter, default value to FULL
 */
public interface JsonType {

    Map<String, Object> toJson();

    default Map<String, Object> toReferenceJson() {
        return toJson();
    }
}
