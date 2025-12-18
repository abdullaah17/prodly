#include "SkillTrie.h"

SkillTrie::SkillTrie() {
    root = new TrieNode();
}

void SkillTrie::insert(const std::string& skill) {
    TrieNode* curr = root;
    for (char c : skill) {
        if (!curr->children[c])
            curr->children[c] = new TrieNode();
        curr = curr->children[c];
    }
    curr->isEnd = true;
}

bool SkillTrie::search(const std::string& skill) {
    TrieNode* curr = root;
    for (char c : skill) {
        if (!curr->children[c])
            return false;
        curr = curr->children[c];
    }
    return curr->isEnd;
}
