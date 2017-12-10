#include <kr_co_timecapsule_SettingsActivity.h>

JNIEXPORT jstring JNICALL Java_kr_co_timecapsule_SettingsActivity_getLocate(JNIEnv *env, jobject obj){

    return env->NewStringUTF("Message from jniMain");
}