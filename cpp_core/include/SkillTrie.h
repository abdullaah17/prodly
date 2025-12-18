#ifndef SKILL_TRIE_H
#define SKILL_TRIE_H

#include <unordered_map>
#include <string>

struct TrieNode {
    bool isEnd;
    std::unordered_map<char, TrieNode*> children;
    TrieNode() : isEnd(false) {}
};

class SkillTrie {
private:
    TrieNode* root;

public:
    SkillTrie();
    void insert(const std::string& skill);
    bool search(const std::string& skill);
};

#endif
