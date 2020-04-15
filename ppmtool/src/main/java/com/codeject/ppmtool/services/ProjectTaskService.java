package com.codeject.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codeject.ppmtool.domain.Backlog;
import com.codeject.ppmtool.domain.ProjectTask;
import com.codeject.ppmtool.exceptions.ProjectNotFoundException;
import com.codeject.ppmtool.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectTaskRepository projectTaskRepository;

	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {

		try {
			Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();

			projectTask.setBacklog(backlog);

			Integer BacklogSequence = backlog.getPTSequence();
			BacklogSequence++;

			backlog.setPTSequence(BacklogSequence);

			projectTask.setProjectIdentifier(projectIdentifier);
			projectTask.setProjectSequence(projectIdentifier + "-" + BacklogSequence);

			if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
				projectTask.setPriority(3);
			}

			if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
				projectTask.setStatus("TO_DO");
			}

			return projectTaskRepository.save(projectTask);
		} catch (Exception e) {
			throw new ProjectNotFoundException("Project Not Found");
		}

	}

	public Iterable<ProjectTask>findBacklogById(String id, String username){

        projectService.findProjectByIdentifier(id, username);

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

	public ProjectTask findProjectTaskBySequence(String backlog_id, String pt_id, String username) {

		projectService.findProjectByIdentifier(backlog_id, username);

		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
		if (projectTask == null)
			throw new ProjectNotFoundException("Project task with the given ID does not exists");

		if (!projectTask.getProjectIdentifier().equals(backlog_id))
			throw new ProjectNotFoundException("Project task with the given ID does not belong to the given backlog");

		return projectTask;
	}

	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id, String username) {
		ProjectTask projectTask = findProjectTaskBySequence(backlog_id, pt_id, username);
		projectTask = updatedTask;

		return projectTaskRepository.save(projectTask);
	}

	public void deleteProjectTaskByProjectSequence(String backlog_id, String pt_id, String username) {
		ProjectTask projectTask = findProjectTaskBySequence(backlog_id, pt_id, username);

		projectTaskRepository.delete(projectTask);
	}

}
