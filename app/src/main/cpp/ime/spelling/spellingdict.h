#ifndef FLORISBOARD_SPELLINGDICT_H
#define FLORISBOARD_SPELLINGDICT_H

#include "nuspell/dictionary.hxx"
#include <string>
#include <vector>

namespace ime::spellcheck {

class SpellingDict {
public:
    SpellingDict(const nuspell::Dictionary& dict);
    ~SpellingDict();

    static SpellingDict* load(const std::string& basePath);

    bool spell(const std::string& word);
    std::vector<std::string> suggest(const std::string& word);

private:
    std::unique_ptr<nuspell::Dictionary> dictionary;
};

} // namespace ime::spellcheck

#endif // FLORISBOARD_SPELLINGDICT_H
