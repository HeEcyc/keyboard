#include "token.h"

#include <utility>

namespace ime::nlp {

Token::Token() : data() {}
Token::Token(word_t &&_data) : data(std::move(_data)) {}

bool operator==(const Token &t1, const Token &t2) {
    return t1.data == t2.data;
}

bool operator!=(const Token &t1, const Token &t2) {
    return !(t1 == t2);
}

WeightedToken::WeightedToken() : Token(), freq(0) {}
WeightedToken::WeightedToken(word_t &&_data, freq_t _freq) : Token(std::move(_data)), freq(_freq) {}

bool operator==(const WeightedToken &t1, const WeightedToken &t2) {
    return t1.data == t2.data && t1.freq == t2.freq;
}

bool operator!=(const WeightedToken &t1, const WeightedToken &t2) {
    return !(t1 == t2);
}

bool operator<(const WeightedToken &t1, const WeightedToken &t2) {
    return t1.freq < t2.freq;
}

bool operator<=(const WeightedToken &t1, const WeightedToken &t2) {
    return t1.freq <= t2.freq;
}

bool operator>(const WeightedToken &t1, const WeightedToken &t2) {
    return t1.freq > t2.freq;
}

bool operator>=(const WeightedToken &t1, const WeightedToken &t2) {
    return t1.freq >= t2.freq;
}

} // namespace ime::nlp
