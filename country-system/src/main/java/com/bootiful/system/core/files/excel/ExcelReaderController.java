package com.bootiful.system.core.files.excel;

import com.bootiful.commons.utils.ExcelReaderResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

/**
 * com.bootiful.system.core.files.excel.ExcelReaderController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/17
 */
@Tag(name = "Excel文件导入读取处理")
@RestController
@RequestMapping("files/excel/reader/v1")
@RequiredArgsConstructor
public class ExcelReaderController {

    private final ExcelReader excelReader;

    @Operation(summary = "Excel表格断续导入", description = "有更高的性能,处理大表格")
    @PostMapping(value = "sax")
    public Flux<ExcelReaderResult> readerSax(@Valid ReaderRequest request, @RequestPart("file") FilePart filePart) {
        return excelReader.reader(request, filePart);
    }
}