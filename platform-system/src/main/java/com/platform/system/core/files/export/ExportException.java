package com.platform.system.core.files.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.platform.commons.annotation.RestServerException;

/**
 * com.peoples.storage.export.ExportException
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/3/19
 */
public class ExportException extends RestServerException {

    public ExportException(int status, Object msg) throws JsonProcessingException {
        super(status, msg);
    }
}