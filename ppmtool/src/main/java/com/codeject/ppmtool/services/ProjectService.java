package com.codeject.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codeject.ppmtool.domain.Backlog;
import com.codeject.ppmtool.domain.Project;
import com.codeject.ppmtool.domain.User;
import com.codeject.ppmtool.exceptions.ProjectIdException;
import com.codeject.ppmtool.exceptions.ProjectNotFoundException;
import com.codeject.ppmtool.repositories.BacklogRepository;
import com.codeject.ppmtool.repositories.ProjectRepository;
import com.codeject.ppmtool.repositories.UserRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private BacklogRepository backlogRepository;

	@Autowired
	private UserRepository userRepository;

	public Project saveOrUpdateProject(Project project, String username) {

		if (project.getId() != null) {
			Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
			if (existingProject != null && (!existingProject.getProjectLeader().equals(username))) {
				throw new ProjectNotFoundException("Project not found in your account");
			} else if (existingProject == null) {
				throw new ProjectNotFoundException("Project with ID: '" + project.getProjectIdentifier()
						+ "' cannot be updated because it doesn't exist");
			}
		}

		try {
			User user = userRepository.findByUsername(username);
			project.setUser(user);
			project.setProjectLeader(user.getUsername());

			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

			if (project.getId() == null) {
				Backlog backlog = new Backlog();
				project.setBacklog(backlog);
				backlog.setProject(project);
				backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			}

			if (project.getId() != null) {
				project.setBacklog(
						backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
			}

			return projectRepository.save(project);
		} catch (Exception e) {
			throw new ProjectIdException(
					"Project ID: " + project.getProjectIdentifier().toUpperCase() + " already exists");
		}
	}

	public Project findProjectByIdentifier(String projectId, String username) {

		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

		if (project == null)
			throw new ProjectIdException("Project ID: " + projectId + " dosen't exists");

		if (!project.getProjectLeader().equals(username)) {
			throw new ProjectNotFoundException("Project not found in your account");
		}

		return project;
	}

	public Iterable<Project> findAllProjects(String username) {
		return projectRepository.findAllByProjectLeader(username);
	}

	public void deleteProjectByIdentifier(String projectId, String username) {
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

		if (project == null)
			throw new ProjectIdException("Project ID: " + projectId + " dosen't exists");

		projectRepository.delete(findProjectByIdentifier(projectId, username));
	}

}
