package com.mb.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mb.entities.MessageEntity;

public interface MessageRepository extends CrudRepository<MessageEntity,Long>{

	MessageEntity findFirstByIdGreaterThanAndTopicNameAndPartitionInOrderById(long id,String topicName,List<Integer> partitions);
	
	//MessageEntity findByIdGreaterThanAndTopicNameAndPartitionIn(long id,String topicName,List<Integer> partitions);
}
