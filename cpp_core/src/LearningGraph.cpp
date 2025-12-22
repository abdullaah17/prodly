#include "LearningGraph.h"
#include <queue>
#include <unordered_map>

void LearningGraph::addDependency(const std::string& prereq, const std::string& skill) {
    adj[prereq].push_back(skill);
}

std::vector<std::string> LearningGraph::generatePath() {
    std::unordered_map<std::string, int> indegree;

    for (auto& pair : adj) {
        if (!indegree.count(pair.first))
            indegree[pair.first] = 0;

        for (auto& next : pair.second) {
            indegree[next]++;
        }
    }

    std::queue<std::string> q;
    for (auto& pair : indegree) {
        if (pair.second == 0)
            q.push(pair.first);
    }

    std::vector<std::string> path;
    while (!q.empty()) {
        std::string curr = q.front();
        q.pop();
        path.push_back(curr);

        for (auto& next : adj[curr]) {
            indegree[next]--;
            if (indegree[next] == 0)
                q.push(next);
        }
    }

    return path;
}
