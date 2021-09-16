#ifndef FLORISBOARD_NLP_H
#define FLORISBOARD_NLP_H

#include <string>

namespace ime::nlp {

typedef std::string word_t;
typedef uint16_t freq_t;

static const freq_t FREQ_VALUE_MASK =          0xFF;
static const freq_t FREQ_POSSIBLY_OFFENSIVE =  0x01;

} // namespace ime::nlp

#endif // FLORISBOARD_NLP_H
