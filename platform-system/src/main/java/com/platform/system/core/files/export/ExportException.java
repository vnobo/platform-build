package com.platform.system.core.files.export;

import com.platform.commons.annotation.RestServerException;

/**
 * com.peoples.storage.export.ExportException
 *
 * @author Alex bob(<a href="https://github.com/vnobo">https://github.com/vnobo</a>)
 * @date Created by 2021/3/19
 */
public class ExportException extends RestServerException {

    public ExportException(int status, Object msg) {
        super(status, msg);
    }
}