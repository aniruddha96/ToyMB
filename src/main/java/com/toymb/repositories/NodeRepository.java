package com.toymb.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.toymb.entities.NodeEntity;

public interface NodeRepository extends CrudRepository<NodeEntity,String>{

}
