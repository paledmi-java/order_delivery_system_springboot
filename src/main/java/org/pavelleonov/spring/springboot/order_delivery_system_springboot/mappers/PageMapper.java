package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

@Component
public class PageMapper {

    public <T> PagedResponseDto<T> toPagedResponse(Page<T> page){
        PagedResponseDto<T> responseDto = new PagedResponseDto<>();

        responseDto.setContent(page.getContent());
        responseDto.setPage(page.getNumber());
        responseDto.setSize(page.getSize());
        responseDto.setTotalPages(page.getTotalPages());
        responseDto.setTotalElements(page.getTotalElements());
        responseDto.setLast(page.isLast());

        return responseDto;
    }
}
