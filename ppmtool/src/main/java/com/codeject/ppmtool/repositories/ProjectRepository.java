package com.codeject.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;

import com.codeject.ppmtool.domain.Project;

public interface ProjectRepository extends CrudRepository<Project, Long>{

	Project findByProjectIdentifier(String projectIdentifier);

	@Override
	Iterable<Project> findAll();
	
	Iterable<Project> findAllByProjectLeader(String username);
}
