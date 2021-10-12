package com.mb.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.mb.entities.GroupEntity;
import com.mb.entities.SubscriberEntity;


public interface SubscriberRepository extends CrudRepository<SubscriberEntity,String>{

	SubscriberEntity getByGroupAndPartitions(GroupEntity group,Integer i);
}
