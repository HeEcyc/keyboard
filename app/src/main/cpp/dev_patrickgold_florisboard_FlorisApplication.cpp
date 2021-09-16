#include <fstream>
#include <vector>
#include <jni.h>
#include <unicode/udata.h>
#include "utils/jni_utils.h"

#pragma ide diagnostic ignored "UnusedLocalVariable"

extern "C"
JNIEXPORT jint JNICALL
Java_com_live_keyboard_FlorisApplication_00024Companion_nativeInitICUData(
        JNIEnv *env,
        jobject thiz,
        jobject path) {
    auto path_str = utils::j2std_string(env, path);
    std::ifstream in_file(path_str, std::ios::in | std::ios::binary);
    if (!in_file) {
        return U_FILE_ACCESS_ERROR;
    }
    in_file.seekg(0, std::ios::end);
    size_t size = in_file.tellg();
    if (size <= 0) {
        return U_FILE_ACCESS_ERROR;
    }
    in_file.seekg(0, std::ios::beg);
    char *icu_data = new char[size + 1];
    in_file.read(icu_data, size);
    if (!in_file) {
        in_file.close();
        return U_FILE_ACCESS_ERROR;
    }
    icu_data[size] = 0;
    in_file.close();
    UErrorCode status = U_ZERO_ERROR;
    udata_setCommonData(reinterpret_cast<void *>(icu_data), &status);
    return status;
}
