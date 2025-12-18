#include "SkillHash.h"

void SkillHash::addSkill(const std::string& name, int level) {
    skills[name] = level;
}

bool SkillHash::hasSkill(const std::string& name) {
    return skills.find(name) != skills.end();
}
