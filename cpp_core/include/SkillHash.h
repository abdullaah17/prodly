#ifndef SKILL_HASH_H
#define SKILL_HASH_H

#include <unordered_map>
#include <string>

class SkillHash {
private:
    std::unordered_map<std::string, int> skills;

public:
    void addSkill(const std::string& name, int level);
    bool hasSkill(const std::string& name);
};

#endif
