package com.platform.system.core.custom.column;

import com.platform.commons.security.ReactiveSecurityDetailsHolder;
import com.platform.commons.utils.SystemType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jjh
 * @date 2021/8/2
 */
@Tag(name = "用户自定义列")
@RestController
@RequestMapping("/custom/column/v1")
@RequiredArgsConstructor
public class CustomColumnController {

    private final CustomColumnService customColumnService;

    @GetMapping("search")
    public Flux<CustomColumn> search(CustomColumnRequest request,
                                     @PageableDefault(sort = "sortNo") Pageable pageable) {
        return this.customColumnService.search(request,pageable);
    }
    @GetMapping("page")
    public Mono<Page<CustomColumn>> page(CustomColumnRequest request,
                                         @PageableDefault(sort = "sortNo") Pageable pageable) {
        return this.customColumnService.page(request,pageable);
    }


    /**
     * 根据id修改信息，用户修改自定义列表时，若当前用户没有创建自己的自定义列信息时
     * 则对该用户进行添加，添加后，则使用用户自定义的自定义列
     * 若存在，则直接修改
     * 直接修改判断条件： 1. 当前登录人的租户id不为0 、2.当前数据的租户id不为0
     * 添加修改：1.当前登录人的租户id不为0 、2.当前数据的租户id为0
     * tips：数据的租户id为0时，代表该条数据为系统级别数据，用户不可以对其进行修改
     * 用户想要修改时，唯有对当前系统数据进行拷贝，而后保存为用户级别的数据信息，用户
     * 就可以修改当前数据信息，用户只可以对自定义列信息进行显隐，而不可以删除，系统管理
     * 员可以对系统级别的自定义列信息进行修改删除
     *
     * @param request 修改的内容参数
     * @return 返回修改后保存的值
     */
    @PostMapping("operation")
    public Mono<CustomColumn> operation( @Valid @RequestBody CustomColumnRequest request) {
        return this.customColumnService.operation(request);
    }

    @GetMapping("user")
    public Flux<CustomColumn> userSearch(@RequestHeader("login-system") SystemType system,
                                         CustomColumnRequest request) {
        return ReactiveSecurityDetailsHolder.getContext()
                .flatMapMany(securityDetails -> this.customColumnService.userSearch(request.system(system)
                           .tenantId(securityDetails.getTenantId()).userId(securityDetails.getUserId())));
    }

    @PostMapping("user")
    public Flux<CustomColumn> userOperation(@RequestHeader("login-system") SystemType system,
                                            @RequestBody @Valid List<CustomColumnRequest> requests) {
        return ReactiveSecurityDetailsHolder.getContext()
                .flatMapMany(securityDetails ->this.customColumnService
                        .userOperation(requests.parallelStream()
                                .peek(req->req.system(system)
                                        .tenantId(securityDetails.getTenantId())
                                        .userId(securityDetails.getUserId()))
                                .collect(Collectors.toList())));
    }

    /**
     * 根据id删除自定义列表
     *
     * @param id 删除的id
     * @return 无返回值
     */
    @DeleteMapping("/delete/{id}")
    public Mono<Void> delete(@PathVariable("id") Long id) {
        return this.customColumnService.delete(id);
    }

}