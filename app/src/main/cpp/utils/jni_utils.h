#ifndef FLORISBOARD_JNI_UTILS_H
#define FLORISBOARD_JNI_UTILS_H

#include <jni.h>
#include <string>

namespace utils {

std::string j2std_string(JNIEnv *env, jobject jStr);
jobject std2j_string(JNIEnv *env, const std::string& in);

} // namespace utils

#endif // FLORISBOARD_JNI_UTILS_H
