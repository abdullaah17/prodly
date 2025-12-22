#include "Engine.h"
#include "SkillHash.h"
#include "SkillTrie.h"
#include "SkillGraph.h"
#include "SkillProgress.h"
#include "RoleGraphManager.h"
#include "CompletionTracker.h"



#include <fstream>
#include <string>

/*
====================================================
 MODULE 1: Skill Gap Analyzer
 Level-1 DSA: Hash Table + Trie
====================================================
*/
void Engine::runSkillGapModule() {
    SkillHash userSkills;   // Hash Table
    SkillTrie roleSkills;   // Trie

    // Load role-required skills
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

    // Identify missing skills
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

/*
====================================================
 MODULE 2: Learning Path Generator
 Level-2 DSA: Graph + BFS
====================================================
*/
void runLearningPathModule() {
    RoleGraphManager manager;

    std::ifstream in("data/input/role.txt");
    std::string role;
    getline(in, role);
    in.close();

    SkillGraph& graph = manager.getGraphForRole(role);

    std::string startSkill;
    if (role == "Frontend") startSkill = "HTML";
    else startSkill = "Programming Basics";

    auto path = graph.getLearningPath(startSkill);

    std::ofstream out("data/output/learning_path.txt");
    for (const auto& skill : path) {
        out << skill << "\n";
    }
    out.close();
}

/*
====================================================
 MODULE 3: Progress Tracker + “What to Learn Next”
 Level-1 | Arrays (Heap storage)     
 Level-2 | Priority Queue / Max-Heap 

====================================================
*/
void runProgressTrackerModule() {
    SkillProgress tracker;
    CompletionTracker completed;

    completed.loadCompletedSkills("data/input/completed_skills.txt");

    // Skill + priority
    std::vector<std::pair<std::string, int>> skills = {
        {"Programming Basics", 1},
        {"OOP", 2},
        {"Data Structures", 5},
        {"Algorithms", 4},
        {"System Design", 3}
    };

    for (auto& s : skills) {
        if (!completed.isCompleted(s.first)) {
            tracker.addSkill(s.first, s.second);
        }
    }

    std::ofstream out("data/output/next_skills.txt");

    if (!tracker.isEmpty()) {
        auto next = tracker.getNextSkill();
        out << "NEXT SKILL: " << next.skill
            << " (priority " << next.priority << ")";
    } else {
        out << "All skills completed 🎉";
    }

    out.close();
}


/*
====================================================
 APPLICATION ENTRY POINT
====================================================
*/
int main() {
    Engine::runSkillGapModule();      // Module 1
    runLearningPathModule();          // Module 2
    runProgressTrackerModule();       // Module 3
    return 0;
}

