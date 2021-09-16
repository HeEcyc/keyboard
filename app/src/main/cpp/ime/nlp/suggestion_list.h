#ifndef FLORISBOARD_SUGGESTION_LIST_H
#define FLORISBOARD_SUGGESTION_LIST_H

#include <optional>
#include <vector>
#include "token.h"

namespace ime::nlp {

class SuggestionList {
public:
    SuggestionList(size_t _maxSize);
    ~SuggestionList();

    bool add(word_t &&word, freq_t &&freq);
    void clear();
    bool contains(const WeightedToken &element) const;
    bool containsWord(const word_t &word) const;
    const WeightedToken *get(size_t index) const;
    std::optional<size_t> indexOf(const WeightedToken &element) const;
    std::optional<size_t> indexOfWord(const word_t &word) const;
    bool isEmpty() const;
    size_t size() const;

    bool isPrimaryTokenAutoInsert;

private:
    std::vector<WeightedToken> tokens;
    size_t internalSize;
    size_t maxSize;
};

} // namespace ime::nlp

#endif // FLORISBOARD_SUGGESTION_LIST_H
