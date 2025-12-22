#include "RoleGraphManager.h"

RoleGraphManager::RoleGraphManager() {

    // Backend Role
    SkillGraph backend;
    backend.addDependency("Programming Basics", "OOP");
    backend.addDependency("OOP", "Data Structures");
    backend.addDependency("Data Structures", "Databases");
    backend.addDependency("Databases", "Backend Frameworks");

    // Frontend Role
    SkillGraph frontend;
    frontend.addDependency("HTML", "CSS");
    frontend.addDependency("CSS", "JavaScript");
    frontend.addDependency("JavaScript", "React");

    // ML Role
    SkillGraph ml;
    ml.addDependency("Programming Basics", "Python");
    ml.addDependency("Python", "NumPy");
    ml.addDependency("NumPy", "Machine Learning");
    ml.addDependency("Machine Learning", "Deep Learning");

    roleGraphs["Backend"] = backend;
    roleGraphs["Frontend"] = frontend;
    roleGraphs["ML"] = ml;
}

SkillGraph& RoleGraphManager::getGraphForRole(const std::string& role) {
    return roleGraphs[role];
}
