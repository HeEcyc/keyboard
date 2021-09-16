#include <jni.h>
#include "ime/nlp/suggestion_list.h"

#pragma ide diagnostic ignored "UnusedLocalVariable"

using namespace ime::nlp;

extern "C"
JNIEXPORT jlong JNICALL
Java_com_live_keyboard_ime_nlp_SuggestionList_00024Companion_nativeInitialize(
        JNIEnv *env,
        jobject thiz,
        jint max_size) {
    auto *suggestionList = new SuggestionList(max_size);
    return reinterpret_cast<jlong>(suggestionList);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_live_keyboard_ime_nlp_SuggestionList_00024Companion_nativeDispose(
        JNIEnv *env,
        jobject thiz,
        jlong native_ptr) {
    auto *suggestionList = reinterpret_cast<SuggestionList *>(native_ptr);
    suggestionList->clear();
    delete suggestionList;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_live_keyboard_ime_nlp_SuggestionList_00024Companion_nativeAdd(
        JNIEnv *env,
        jobject thiz,
        jlong native_ptr,
        jstring word,
        jint freq) {
    const char *cWord = env->GetStringUTFChars(word, nullptr);
    word_t stdWord = word_t(cWord);
    env->ReleaseStringUTFChars(word, cWord);
    auto *suggestionList = reinterpret_cast<SuggestionList *>(native_ptr);
    return suggestionList->add(std::move(stdWord), freq);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_live_keyboard_ime_nlp_SuggestionList_00024Companion_nativeClear(
        JNIEnv *env,
        jobject thiz,
        jlong native_ptr) {
    auto *suggestionList = reinterpret_cast<SuggestionList *>(native_ptr);
    suggestionList->clear();
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_live_keyboard_ime_nlp_SuggestionList_00024Companion_nativeContains(
        JNIEnv *env,
        jobject thiz,
        jlong native_ptr,
        jstring element) {
    const char *cWord = env->GetStringUTFChars(element, nullptr);
    const word_t stdWord = word_t(cWord);
    env->ReleaseStringUTFChars(element, cWord);
    auto *suggestionList = reinterpret_cast<SuggestionList *>(native_ptr);
    return suggestionList->containsWord(stdWord);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_live_keyboard_ime_nlp_SuggestionList_00024Companion_nativeGetOrNull(
        JNIEnv *env,
        jobject thiz,
        jlong native_ptr,
        jint index) {
    auto *suggestionList = reinterpret_cast<SuggestionList *>(native_ptr);
    auto weightedToken = suggestionList->get(index);
    if (weightedToken == nullptr) {
        return nullptr;
    }
    return env->NewStringUTF(weightedToken->data.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_live_keyboard_ime_nlp_SuggestionList_00024Companion_nativeSize(
        JNIEnv *env,
        jobject thiz,
        jlong native_ptr) {
    auto *suggestionList = reinterpret_cast<SuggestionList *>(native_ptr);
    return suggestionList->size();
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_live_keyboard_ime_nlp_SuggestionList_00024Companion_nativeGetIsPrimaryTokenAutoInsert(
        JNIEnv *env, jobject thiz, jlong native_ptr) {
    auto *suggestionList = reinterpret_cast<SuggestionList *>(native_ptr);
    return suggestionList->isPrimaryTokenAutoInsert;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_live_keyboard_ime_nlp_SuggestionList_00024Companion_nativeSetIsPrimaryTokenAutoInsert(
        JNIEnv *env, jobject thiz, jlong native_ptr, jboolean v) {
    auto *suggestionList = reinterpret_cast<SuggestionList *>(native_ptr);
    suggestionList->isPrimaryTokenAutoInsert = v;
}
