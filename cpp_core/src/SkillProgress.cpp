#include "SkillProgress.h"

void SkillProgress::heapifyUp(int index) {
    while (index > 0) {
        int parent = (index - 1) / 2;
        if (heap[parent].priority >= heap[index].priority)
            break;

        std::swap(heap[parent], heap[index]);
        index = parent;
    }
}

void SkillProgress::heapifyDown(int index) {
    int size = heap.size();

    while (true) {
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        int largest = index;

        if (left < size && heap[left].priority > heap[largest].priority)
            largest = left;
        if (right < size && heap[right].priority > heap[largest].priority)
            largest = right;

        if (largest == index)
            break;

        std::swap(heap[index], heap[largest]);
        index = largest;
    }
}

void SkillProgress::addSkill(const std::string& skill, int priority) {
    heap.push_back({skill, priority});
    heapifyUp(heap.size() - 1);
}

SkillNode SkillProgress::getNextSkill() {
    SkillNode top = heap.front();
    heap[0] = heap.back();
    heap.pop_back();
    heapifyDown(0);
    return top;
}

bool SkillProgress::isEmpty() const {
    return heap.empty();
}
