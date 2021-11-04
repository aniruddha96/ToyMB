package com.toymb.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.toymb.entities.GroupEntity;
import com.toymb.entities.TopicEntity;


public interface GroupRepository extends CrudRepository<GroupEntity,String>{

	List<GroupEntity> getByTopic(TopicEntity topic);
	
}
