package com.codeject.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codeject.ppmtool.domain.Backlog;
import com.codeject.ppmtool.domain.Project;
import com.codeject.ppmtool.domain.ProjectTask;
import com.codeject.ppmtool.exceptions.ProjectNotFoundException;
import com.codeject.ppmtool.repositories.BacklogRepository;
import com.codeject.ppmtool.repositories.ProjectRepository;
import com.codeject.ppmtool.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	private BacklogRepository backlogRepository;

	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;

	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {

		try {
			Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

			projectTask.setBacklog(backlog);

			Integer BacklogSequence = backlog.getPTSequence();
			BacklogSequence++;

			backlog.setPTSequence(BacklogSequence);

			projectTask.setProjectIdentifier(projectIdentifier);
			projectTask.setProjectSequence(projectIdentifier + "-" + BacklogSequence);

			if (projectTask.getPriority() == null) {
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

	public Iterable<ProjectTask> findBacklogById(String backlog_id) {
		
		Project project = projectRepository.findByProjectIdentifier(backlog_id);
		
		if(project == null) throw new ProjectNotFoundException("Project with the given ID does not exists");
		
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlog_id);
	}
	
	public ProjectTask findProjectTaskBySequence(String backlog_id, String pt_id) {
		
		Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
		if(backlog == null) throw new ProjectNotFoundException("Project with the given ID does not exists");
		
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
		if(projectTask == null) throw new ProjectNotFoundException("Project task with the given ID does not exists");
		
		if(!projectTask.getProjectIdentifier().equals(backlog_id)) throw new ProjectNotFoundException("Project task with the given ID does not belong to the given backlog");
		
		return projectTask;
	}
	
	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id) {
		ProjectTask projectTask = findProjectTaskBySequence(backlog_id, pt_id);
		projectTask = updatedTask;
		
		return projectTaskRepository.save(projectTask);
	}
	
	public void deleteProjectTaskByProjectSequence(String backlog_id, String pt_id) {
		ProjectTask projectTask = findProjectTaskBySequence(backlog_id, pt_id);
		
		projectTaskRepository.delete(projectTask);
	}

}
