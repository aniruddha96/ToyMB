package com.toymb.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.toymb.entities.MessageEntity;

public interface MessageRepository extends CrudRepository<MessageEntity,Long>{

	MessageEntity findFirstByIdGreaterThanAndTopicNameAndPartitionNumberInOrderById(long id,String topicName,List<Integer> partitions);
	
	MessageEntity findByIdentifier(String identifier);
	//MessageEntity findByIdGreaterThanAndTopicNameAndPartitionIn(long id,String topicName,List<Integer> partitions);
}
