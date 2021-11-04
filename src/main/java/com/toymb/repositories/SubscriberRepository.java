package com.toymb.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.toymb.entities.GroupEntity;
import com.toymb.entities.SubscriberEntity;


public interface SubscriberRepository extends CrudRepository<SubscriberEntity,String>{

	SubscriberEntity getByGroupAndPartitions(GroupEntity group,Integer i);
}
