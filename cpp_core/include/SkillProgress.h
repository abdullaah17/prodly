#ifndef SKILL_PROGRESS_H
#define SKILL_PROGRESS_H

#include <string>
#include <vector>

struct SkillNode {
    std::string skill;
    int priority;
};

class SkillProgress {
private:
    std::vector<SkillNode> heap;

    void heapifyUp(int index);
    void heapifyDown(int index);

public:
    void addSkill(const std::string& skill, int priority);
    SkillNode getNextSkill();
    bool isEmpty() const;
};

#endif
