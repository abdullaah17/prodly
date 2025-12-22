#include "CompletionTracker.h"
#include <fstream>

void CompletionTracker::loadCompletedSkills(const std::string& filename) {
    std::ifstream in(filename);
    std::string skill;

    while (getline(in, skill)) {
        if (!skill.empty())
            completedSkills.insert(skill);
    }
    in.close();
}

bool CompletionTracker::isCompleted(const std::string& skill) const {
    return completedSkills.count(skill) > 0;
}
