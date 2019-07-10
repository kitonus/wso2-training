package com.jatis.demo.demoapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jatis.demo.demoapi.dto.StoreDTO;
import com.jatis.demo.demoapi.entity.StoreEntity;
import com.jatis.demo.demoapi.repository.StoreEntityRepostory;

@RestController
@RequestMapping("/store")
public class StoreController {

	@Autowired
	private StoreEntityRepostory repo;
	
	@Secured("ROLE_ADMIN")
	@PostMapping
	public StoreEntity save(@RequestBody StoreDTO store) {
		StoreEntity storeEnt = new StoreEntity();
		storeEnt.setCode(store.getCode());
		storeEnt.setName(store.getName());
		
		return repo.save(storeEnt);
	}
	
	@Secured("ROLE_USER")
	@GetMapping("/all")
	@PostMapping("/all")
	public Iterable<StoreEntity> findAll(){
		return repo.findAll();
	}
	
	@Secured("ROLE_USER")
	@GetMapping("/{code}")
	public StoreEntity findOne(@PathVariable("code") String code) {
		return repo.findById(code).get();
	}
}
