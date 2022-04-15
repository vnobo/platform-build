package com.bootiful.system.core.files.export;

import com.bootiful.commons.annotation.RestServerException;
import com.fasterxml.jackson.core.JsonProcessingException;

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