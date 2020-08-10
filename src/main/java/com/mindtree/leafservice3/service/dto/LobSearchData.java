package com.mindtree.leafservice3.service.dto;

import java.util.List;

import com.mindtree.leafservice3.domain.Activity;
import com.mindtree.leafservice3.domain.BusinessFunction;
import com.mindtree.leafservice3.domain.BusinessProcess;
import com.mindtree.leafservice3.domain.Capabilities;
import com.mindtree.leafservice3.domain.Task;

public class LobSearchData {
    public Long id;
    public String name;
    public List<BusinessFunction> bFunction;
    public List<Capabilities> capability;
    public List<BusinessProcess> bProcesses;
    public List<Activity> activities;
    public List<Task> tasks;
}