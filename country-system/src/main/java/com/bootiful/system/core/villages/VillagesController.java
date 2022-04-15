package com.bootiful.system.core.villages;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * com.bootiful.system.core.configurations.ConfigurationController
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/7
 */
@Tag(name = "村信息管理")
@RestController
@RequestMapping("villages/v1")
@RequiredArgsConstructor
public class VillagesController {

    private final VillagesRepository villagesRepository;

    @GetMapping
    public Flux<Villages> search(Villages villages) {
        return this.villagesRepository.findAll(Example.of(villages))
                .limitRate(10000);
    }

}