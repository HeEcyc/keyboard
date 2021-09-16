#include "spellingdict.h"
#include "utils/log.h"

using namespace ime::spellcheck;

SpellingDict::SpellingDict(const nuspell::Dictionary& dict) : dictionary(std::make_unique<nuspell::Dictionary>(dict))
{ }

SpellingDict::~SpellingDict() = default;

SpellingDict* SpellingDict::load(const std::string &basePath) {
    utils::start_stdout_stderr_logger("spell-floris");
    try {
        auto temp = nuspell::Dictionary::load_from_path(basePath);
        auto spellingDict = new SpellingDict(temp);
        return spellingDict;
    } catch (const nuspell::Dictionary_Loading_Error& e) {
        utils::log_error("SpellingDict.load()", e.what());
        return nullptr;
    } catch (...) {
        utils::log_error("SpellingDict.load()", "An unknown error occurred!");
        return nullptr;
    }
}

bool SpellingDict::spell(const std::string& word) {
    bool result = dictionary->spell(word);
    return result;
}

std::vector<std::string> SpellingDict::suggest(const std::string &word) {
    auto result = std::vector<std::string>();
    dictionary->suggest(word, result);
    return result;
}
