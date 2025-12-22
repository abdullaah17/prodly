#ifndef COMPLETION_TRACKER_H
#define COMPLETION_TRACKER_H

#include <string>
#include <unordered_set>

class CompletionTracker {
private:
    std::unordered_set<std::string> completedSkills;

public:
    void loadCompletedSkills(const std::string& filename);
    bool isCompleted(const std::string& skill) const;
};

#endif
