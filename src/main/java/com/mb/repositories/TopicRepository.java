package com.mb.repositories;

import org.springframework.data.repository.CrudRepository;

import com.mb.entities.TopicEntity;

public interface TopicRepository extends CrudRepository<TopicEntity,String>{

	
}
