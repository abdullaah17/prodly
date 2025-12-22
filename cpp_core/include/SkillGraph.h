#ifndef SKILL_GRAPH_H
#define SKILL_GRAPH_H

#include <vector>
#include <unordered_map>
#include <queue>
#include <string>

class SkillGraph {
private:
    std::unordered_map<std::string, int> skillIndex;
    std::vector<std::vector<int>> adjList;
    std::vector<std::string> skills;

public:
    void addSkill(const std::string& skill);
    void addDependency(const std::string& from, const std::string& to);
    std::vector<std::string> getLearningPath(const std::string& startSkill);
};

#endif
