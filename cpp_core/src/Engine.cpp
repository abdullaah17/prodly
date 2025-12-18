#include "Engine.h"
#include "SkillHash.h"
#include "SkillTrie.h"

#include <fstream>
#include <vector>
#include <string>

void Engine::runSkillGapModule() {
    SkillHash userSkills;
    SkillTrie roleSkills;

    // Load role requirements
    std::ifstream roleFile("data/input/role_config.txt");
    std::string roleSkill;
    while (roleFile >> roleSkill) {
        roleSkills.insert(roleSkill);
    }
    roleFile.close();

    // Load user skills
    std::ifstream userFile("data/input/user_profile.txt");
    std::string skill;
    int level;
    while (userFile >> skill >> level) {
        userSkills.addSkill(skill, level);
    }
    userFile.close();

    // Find gaps
    std::ofstream out("data/output/skill_gaps.txt");
    roleFile.open("data/input/role_config.txt");
    while (roleFile >> roleSkill) {
        if (!userSkills.hasSkill(roleSkill)) {
            out << roleSkill << "\n";
        }
    }
    roleFile.close();
    out.close();
}

int main() {
    Engine::runSkillGapModule();
    return 0;
}
