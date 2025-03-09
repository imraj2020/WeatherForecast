#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_cse_nativelib_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "https://api.openweathermap.org/data/2.5/";
    return env->NewStringUTF(hello.c_str());
}