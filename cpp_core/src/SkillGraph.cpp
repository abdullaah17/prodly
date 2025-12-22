#include "SkillGraph.h"

void SkillGraph::addSkill(const std::string& skill) {
    if (skillIndex.count(skill)) return;

    int index = skills.size();
    skillIndex[skill] = index;
    skills.push_back(skill);
    adjList.push_back({});
}

void SkillGraph::addDependency(const std::string& from, const std::string& to) {
    addSkill(from);
    addSkill(to);

    int u = skillIndex[from];
    int v = skillIndex[to];

    adjList[u].push_back(v);
}

std::vector<std::string> SkillGraph::getLearningPath(const std::string& startSkill) {
    std::vector<std::string> path;
    if (!skillIndex.count(startSkill)) return path;

    std::vector<bool> visited(skills.size(), false);
    std::queue<int> q;

    q.push(skillIndex[startSkill]);
    visited[skillIndex[startSkill]] = true;

    while (!q.empty()) {
        int node = q.front(); q.pop();
        path.push_back(skills[node]);

        for (int neighbor : adjList[node]) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                q.push(neighbor);
            }
        }
    }
    return path;
}
