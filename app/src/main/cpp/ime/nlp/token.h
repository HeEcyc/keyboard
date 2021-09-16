#ifndef FLORISBOARD_TOKEN_H
#define FLORISBOARD_TOKEN_H

#include "nlp.h"
#include <string>

namespace ime::nlp {

class Token {
public:
    word_t data;
    Token();
    Token(word_t &&_data);

    friend bool operator==(const Token &t1, const Token &t2);
    friend bool operator!=(const Token &t1, const Token &t2);
};

class WeightedToken : public Token {
public:
    freq_t freq;
    WeightedToken();
    WeightedToken(word_t &&_data, freq_t _freq);

    friend bool operator==(const WeightedToken &t1, const WeightedToken &t2);
    friend bool operator!=(const WeightedToken &t1, const WeightedToken &t2);
    friend bool operator<(const WeightedToken &t1, const WeightedToken &t2);
    friend bool operator<=(const WeightedToken &t1, const WeightedToken &t2);
    friend bool operator>(const WeightedToken &t1, const WeightedToken &t2);
    friend bool operator>=(const WeightedToken &t1, const WeightedToken &t2);
};

} // namespace ime::nlp

#endif // FLORISBOARD_TOKEN_H
