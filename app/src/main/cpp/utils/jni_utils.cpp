#include "jni_utils.h"
#include "log.h"

std::string utils::j2std_string(JNIEnv *env, jobject jStr) {
    auto cStr = reinterpret_cast<const char *>(env->GetDirectBufferAddress(jStr));
    auto size = env->GetDirectBufferCapacity(jStr);
    std::string stdStr(cStr, size);
    log_debug("spell j2s", stdStr);
    return stdStr;
}

jobject utils::std2j_string(JNIEnv *env, const std::string& stdStr) {
    log_debug("spell s2j", stdStr);
    size_t byteCount = stdStr.length();
    auto cStr = stdStr.c_str();
    auto buffer = env->NewDirectByteBuffer((void *) cStr, byteCount);
    return buffer;
}
