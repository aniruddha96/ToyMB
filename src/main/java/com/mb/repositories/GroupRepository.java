package com.mb.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mb.entities.GroupEntity;
import com.mb.entities.TopicEntity;


public interface GroupRepository extends CrudRepository<GroupEntity,String>{

	List<GroupEntity> getByTopic(TopicEntity topic);
}
