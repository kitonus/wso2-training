package com.jatis.demo.demoapi.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.jatis.demo.demoapi.entity.StoreEntity;

public interface StoreEntityRepostory extends PagingAndSortingRepository<StoreEntity, String> {

}
