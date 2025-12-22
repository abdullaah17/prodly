#ifndef ROLE_GRAPH_MANAGER_H
#define ROLE_GRAPH_MANAGER_H

#include "SkillGraph.h"
#include <unordered_map>
#include <string>

class RoleGraphManager {
private:
    std::unordered_map<std::string, SkillGraph> roleGraphs;

public:
    RoleGraphManager();
    SkillGraph& getGraphForRole(const std::string& role);
};

#endif
