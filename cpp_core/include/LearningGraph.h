#ifndef LEARNING_GRAPH_H
#define LEARNING_GRAPH_H

#include <unordered_map>
#include <vector>
#include <string>

class LearningGraph {
private:
    std::unordered_map<std::string, std::vector<std::string>> adj;

public:
    void addDependency(const std::string& prereq, const std::string& skill);
    std::vector<std::string> generatePath();
};

#endif
